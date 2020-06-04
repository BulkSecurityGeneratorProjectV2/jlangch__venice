/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2020 Venice
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
package com.github.jlangch.venice.impl.reader;

public enum AtomType {
	NIL,			// nil
	TRUE,			// true
	FALSE,			// false
	INTEGER,		// 10I, -10I
	LONG,			// 10, -10
	DOUBLE,			// 10.2, -10.2
	DECIMAL,		// 10.2M, -10.2M
	STRING,			// "abcde"
	STRING_BLOCK,	// """abcde"""
	KEYWORD,		// :abc
	SYMBOL,			// abcd, .:, $ab
	UNKNOWN;
}