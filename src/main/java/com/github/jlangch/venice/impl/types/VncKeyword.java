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
package com.github.jlangch.venice.impl.types;

import com.github.jlangch.venice.impl.functions.FunctionsUtil;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.collections.VncMap;
import com.github.jlangch.venice.impl.types.util.Coerce;
import com.github.jlangch.venice.impl.types.util.Types;


public class VncKeyword extends VncString implements IVncFunction {
	
	public VncKeyword(final String v) { 
		this(v, Constants.Nil); 
	}

	public VncKeyword(final String v, final VncVal meta) {
		super(v.startsWith(":") ? v.substring(1): v, meta); 
	}

	
	public VncVal apply(final VncList args) {
		FunctionsUtil.assertArity("keyword", args, 1, 2);
		
		if (args.first() == Constants.Nil) {
			return Constants.Nil;
		}
		else {
			final VncMap map = Coerce.toVncMap(args.first());
			if (args.size() == 1) {
				return map.get(this);
			}
			else if (map.containsKey(this) == Constants.True) {
				return map.get(this);
			}
			else {
				return args.second();
			}
		}
	}
	
	
	@Override
	public VncKeyword withMeta(final VncVal meta) {
		return new VncKeyword(getValue(), meta);
	}

	public VncSymbol toSymbol() {
		return new VncSymbol(getValue());
	}
	
	@Override 
	public int typeRank() {
		return 6;
	}

	@Override 
	public int compareTo(final VncVal o) {
		if (o == Constants.Nil) {
			return 1;
		}
		else if (Types.isVncKeyword(o)) {
			return getValue().compareTo(((VncKeyword)o).getValue());
		}

		return super.compareTo(o);
	}

	@Override 
	public String toString() {
		return ":" + getValue();
	}
	
	public String toString(final boolean print_readably) {
		return toString();
	}
	
	
    private static final long serialVersionUID = -1848883965231344442L;
}