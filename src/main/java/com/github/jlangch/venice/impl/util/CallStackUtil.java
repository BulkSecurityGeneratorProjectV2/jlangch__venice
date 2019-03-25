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
package com.github.jlangch.venice.impl.util;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.github.jlangch.venice.impl.Env;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.concurrent.ThreadLocalMap;


public class CallStackUtil {

	public static void runWithCallStack(
			final CallFrame callFrame, 
			final Runnable fn
	) {
		try {
			ThreadLocalMap.getCallStack().push(callFrame);
			fn.run();
		}
		finally {
			ThreadLocalMap.getCallStack().pop();
		}
	}

	public static VncVal runWithCallStack(
			final CallFrame callFrame, 
			final Supplier<VncVal> fn
	) {
		try {
			ThreadLocalMap.getCallStack().push(callFrame);
			return fn.get();
		}
		finally {
			ThreadLocalMap.getCallStack().pop();
		}
	}

	public static VncVal runWithCallStack(
			final CallFrame callFrame,
			final VncList ast, 
			final Env env, 
			final BiFunction<VncList,Env,VncVal> fn
	) {
		try {
			ThreadLocalMap.getCallStack().push(callFrame);
			return fn.apply(ast, env);
		}
		finally {
			ThreadLocalMap.getCallStack().pop();
		}
	}

}
