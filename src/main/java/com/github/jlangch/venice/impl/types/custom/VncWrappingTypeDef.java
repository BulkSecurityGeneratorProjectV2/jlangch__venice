/*   __	__		 _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *	\ \/ / _ \ '_ \| |/ __/ _ \
 *	 \  /  __/ | | | | (_|  __/
 *	  \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2020 Venice
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jlangch.venice.impl.types.custom;

import com.github.jlangch.venice.impl.types.VncFunction;
import com.github.jlangch.venice.impl.types.VncKeyword;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncList;


public class VncWrappingTypeDef {

	public VncWrappingTypeDef(
			final VncKeyword type,
			final VncKeyword baseType
	) {
		this(type, baseType, null);
	}

	public VncWrappingTypeDef(
			final VncKeyword type,
			final VncKeyword baseType,
			final VncFunction validationFn
	) {
		this.type = type;
		this.baseType = baseType;
		this.validationFn = validationFn;
	}


	public VncKeyword getType() {
		return type;
	}
 
	public VncKeyword getBaseType() {
		return baseType;
	}
 
	public VncFunction getValidationFn() {
		return validationFn;
	}

	public void validate(final VncVal val) {
		if (validationFn != null) {
			validationFn.apply(VncList.of(val));
		}
	}

	
	private final VncKeyword type;
	private final VncKeyword baseType;
	private final VncFunction validationFn;
}