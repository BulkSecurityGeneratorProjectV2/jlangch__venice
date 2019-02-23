/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2019 Venice
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jlangch.venice.impl;

import static com.github.jlangch.venice.impl.types.Constants.Nil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.types.Types;
import com.github.jlangch.venice.impl.types.VncSymbol;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.util.ThreadLocalMap;
import com.github.jlangch.venice.util.CallFrame;


public class Env implements Serializable {
	
	public Env() {
		this(null);
	}

	public Env(final Env outer) {
		this.outer = outer;
		this.level = outer == null ? 0 : outer.level() + 1;
		this.globalSymbols = outer == null ? new ConcurrentHashMap<>() : outer.globalSymbols;
		this.symbols = new ConcurrentHashMap<>();
	}

	public VncVal get(final VncSymbol key) {
		final VncVal val = getOrNull(key);
		if (val != null) {
			return val;
		}
		
		try {
			ThreadLocalMap.getCallStack().push(CallFrame.fromVal(key));
			throw new VncException(String.format("Symbol '%s' not found.",  key.getName()));
		}
		finally {
			ThreadLocalMap.getCallStack().pop();
		}
	}

	public VncVal getOrNil(final VncSymbol key) {
		final VncVal val = getOrNull(key);
		return val == null ? Nil : val;
	}

	public VncVal getGlobalOrNil(final VncSymbol key) {
		final Var glob = globalSymbols.get(key);
		return glob == null ? Nil : glob.getVal();
	}

	public VncVal getGlobalOrNull(final VncSymbol key) {
		final Var glob = globalSymbols.get(key);
		return glob == null ? null : glob.getVal();
	}

	public int level() {
		return level;
	}

	public Env set(final VncSymbol name, final VncVal val) {
		final Var v = globalSymbols.get(name);
		if (v != null && !v.isOverwritable() && Types.isVncFunction(v.getVal())) {
			// allow shadowing of a global non function var by a local var
			// e.g.:   (do (defonce x 1) (defonce y 3) (let [x 10 y 20] (+ x y)))
			try {
				ThreadLocalMap.getCallStack().push(CallFrame.fromVal(name));
				throw new VncException(String.format(
						"The global function '%s' must not be shadowed by a local var!",
						name));
			}
			finally {
				ThreadLocalMap.getCallStack().pop();
			}
		}

		symbols.put(name, new Var(name, val));
		return this;
	}
	
	public Env addAll(final List<Binding> bindings) {
		for(Binding b : bindings) {
			set(b.sym, b.val);
		}
		return this;
	}

	public Env setGlobal(final Var val) {
		final Var v = globalSymbols.get(val.getName());
		if (v != null && !v.isOverwritable()) {
			try {
				ThreadLocalMap.getCallStack().push(CallFrame.fromVal(val.getName()));
				throw new VncException(String.format(
						"The existing global var '%s' must not be overwritten!",
						val.getName()));
			}
			finally {
				ThreadLocalMap.getCallStack().pop();
			}
		}
		
		globalSymbols.put(val.getName(), val);
		return this;
	}

	public Env pushGlobalDynamic(final Var val) {
		final Var dv = globalSymbols.get(val.getName());
		if (dv != null) {
			if (dv instanceof DynamicVar) {
				((DynamicVar)dv).pushVal(val.getVal());
			}
			else {
				try {
					ThreadLocalMap.getCallStack().push(CallFrame.fromVal(val.getName()));
					throw new VncException(String.format(
							"The var '%s' is not defined as dynamic",
							val.getName()));
				}
				finally {
					ThreadLocalMap.getCallStack().pop();
				}
			}
		}
		else {
			final DynamicVar nv = new DynamicVar(val.getName(), Nil);
			globalSymbols.put(val.getName(), nv);
			nv.pushVal(val.getVal());
		}
		return this;
	}

	public VncVal popGlobalDynamic(final VncSymbol sym) {
		final Var dv = globalSymbols.get(sym);
		if (dv != null) {
			if (dv instanceof DynamicVar) {
				return ((DynamicVar)dv).popVal();
			}
			else {
				try {
					ThreadLocalMap.getCallStack().push(CallFrame.fromVal(sym));
					throw new VncException(String.format(
							"The var '%s' is not defined as dynamic",
							sym.getName()));
				}
				finally {
					ThreadLocalMap.getCallStack().pop();
				}
			}
		}
		else {
			return Nil;
		}
	}

	public VncVal peekGlobalDynamic(final VncSymbol sym) {
		final Var dv = globalSymbols.get(sym);
		if (dv != null) {
			if (dv instanceof DynamicVar) {
				return ((DynamicVar)dv).peekVal();
			}
			else {
				try {
					ThreadLocalMap.getCallStack().push(CallFrame.fromVal(sym));
					throw new VncException(String.format(
							"The var '%s' is not defined as dynamic",
							sym.getName()));
				}
				finally {
					ThreadLocalMap.getCallStack().pop();
				}
			}
		}
		else {
			return Nil;
		}
	}

	public boolean hasGlobalSymbol(final VncSymbol key) {
		return globalSymbols.containsKey(key);
	}

	public Env getRootEnv() {
		Env env = this;
		while(env.outer != null) env = env.outer;
		return env;
	}

	public Env getLevelEnv(final int level) {
		Env env = this;
		if (env.level == level) {
			return env;
		}
		else {
			while(env.outer != null) {
				env = env.outer;
				if (env.level == level) {
					return env;
				}
			}
		}
		
		throw new VncException(String.format("No env level %d", level));
	}
	
	@Override
	public String toString() {
		return String.format(
				"level %d:\n   [local]\n%s\n   [global]\n%s", 
				level, 
				toString(symbols, "      "), 
				toString(globalSymbols, "      "));
	}
	
	private String toString(final Map<VncSymbol,Var> vars, final String indent) {
		return vars.values()
				   .stream()
				   .sorted((a,b) -> a.getName().getName().compareTo(b.getName().getName()))
				   .map(v -> String.format(
							"%s%s: %s", 
							indent,
							v.getName().getName(), 
							Printer._pr_str(v.getVal(), true)))
				   .collect(Collectors.joining("\n"));
	}
	
	private VncVal getOrNull(final VncSymbol key) {
		final Env e = findEnv(key);
		if (e == null) {
			final Var glob = globalSymbols.get(key);
			if (glob != null) {
				return glob.getVal();
			}
		}
		else {
			final Var loc = e.symbols.get(key);
			if (loc != null) {
				return loc.getVal();
			}
		}
		
		return null;
	}
	
	private Env findEnv(final VncSymbol key) {		
		if (symbols.containsKey(key)) {
			return this;
		} 
		else if (outer != null) {
			return outer.findEnv(key);
		} 
		else {
			return null;
		}
	}
	
	
	private static final long serialVersionUID = 9002640180394221858L;

	private final Env outer;
	private final int level;
	private final Map<VncSymbol,Var> globalSymbols;
	private final Map<VncSymbol,Var> symbols;
}

