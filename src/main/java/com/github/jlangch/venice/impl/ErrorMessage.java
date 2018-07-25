/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2018 Venice
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

import com.github.jlangch.venice.impl.types.Constants;
import com.github.jlangch.venice.impl.types.VncLong;
import com.github.jlangch.venice.impl.types.VncString;
import com.github.jlangch.venice.impl.types.VncVal;


public class ErrorMessage {
	
	public static String buildErrLocation(final Token token) {
		return String.format(
				"File <%s> (%d,%d)",
				token.getFile(),
				token.getLine(),
				token.getColumn());
	}
	
	public static String buildErrLocation(final VncVal val) {
		final VncVal file = val.getMetaVal(MetaUtil.FILE);
		final VncVal line = val.getMetaVal(MetaUtil.LINE);
		final VncVal column = val.getMetaVal(MetaUtil.COLUMN);
		return String.format(
				"File <%s> (%d,%d)",
				file == Constants.Nil ? "unknown" : ((VncString)file).getValue(),
				line == Constants.Nil ? 1 : ((VncLong)line).getValue(),
				column == Constants.Nil ? 1 : ((VncLong)column).getValue());
	}
}
