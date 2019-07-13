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

import com.github.jlangch.venice.impl.Printer;
import com.github.jlangch.venice.impl.types.IDeref;
import com.github.jlangch.venice.impl.types.VncVal;


public class Reduced implements IDeref {

	public Reduced(final VncVal val) {
		this.val = val;
	}
	
	@Override 
	public VncVal deref() {
		return val;
	}

	@Override 
	public String toString() {
		return toString(true);
	}

	public String toString(final boolean print_readably) {
		return "(reduced :value " + Printer.pr_str(val, print_readably) + ")";
	}

	
	private final VncVal val;
}