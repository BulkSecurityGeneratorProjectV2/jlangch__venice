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
package com.github.jlangch.venice;


public class JavaValueException extends VncException {
		
	public JavaValueException(final Object value) {
		super("Venice value exception");
		this.value = value;
	}
  
	public JavaValueException(final Object value, final Throwable cause) {
		super("Venice value exception", cause);
		this.value = value;
	}
	
	public Object getValue() { 
		return value; 
	}

	
	private static final long serialVersionUID = -7070216020647646364L;

	private final Object value;
}