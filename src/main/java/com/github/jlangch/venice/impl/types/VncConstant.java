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
package com.github.jlangch.venice.impl.types;

import static com.github.jlangch.venice.impl.types.Constants.Nil;

import com.github.jlangch.venice.impl.types.util.Types;


public class VncConstant extends VncVal {

	public VncConstant(final String name) { 
		super(Nil);
		value = name; 
	}

	
	@Override
	public VncConstant withMeta(final VncVal meta) {
		return this;
	}
	
	@Override
	public VncKeyword getType() {
		if (this == Constants.Nil) {
			return new VncKeyword(":core/nil");
		}
		else if (Constants.isTrue(this)) {
			return new VncKeyword(":core/boolean");
		}
		else if (Constants.isFalse(this)) {
			return new VncKeyword(":core/boolean");
		}
		else {
			return null;
		}
	}
	
	@Override
	public VncKeyword getSupertype() {
		return new VncKeyword(":core/val");
	}

	public String getValue() { 
		return value; 
	}
	
	@Override 
	public TypeRank typeRank() {
		return TypeRank.CONSTANT;
	}

	@Override
	public Object convertToJavaObject() {
		if (this == Constants.Nil) {
			return null;
		}
		else if (Constants.isTrue(this)) {
			return Boolean.TRUE;
		}
		else if (Constants.isFalse(this)) {
			return Boolean.FALSE;
		}
		else {
			return null;
		}
	}

	@Override 
	public int compareTo(final VncVal o) {
		if (this == Nil) {
			return o == Nil ? 0 : -1;
		}
		else if (o == Nil) {
			return 1;
		}
		else if (Types.isVncBoolean(this) && Types.isVncBoolean(o)) {
			return Long.valueOf(Constants.isFalse(this) ? 0L : 1L).compareTo(Constants.isFalse(o) ? 0L : 1L);				
		}

		return super.compareTo(o);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		VncConstant other = (VncConstant) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override 
	public String toString() { 
		return value; 
	}
   

    private static final long serialVersionUID = -1848883965231344442L;

	private final String value;
}