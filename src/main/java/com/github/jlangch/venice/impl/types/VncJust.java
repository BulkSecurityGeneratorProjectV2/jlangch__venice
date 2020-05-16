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

import com.github.jlangch.venice.impl.types.util.Types;


public class VncJust extends VncVal implements IDeref {
	
	public VncJust(final VncVal v) { 
		this(v, Constants.Nil); 
	}

	public VncJust(final VncVal v, final VncVal meta) { 
		super(meta);
		value = (v == null) ? Constants.Nil : v; 
	}

	public VncVal getValue() { 
		return value; 
	}

	@Override
	public VncVal deref() {
		return value;
	}
	
	@Override
	public VncJust withMeta(final VncVal meta) {
		return new VncJust(value, meta); 
	}
	
	@Override
	public VncKeyword getType() {
		return new VncKeyword(":core/just");
	}
	
	@Override
	public VncKeyword getSupertype() {
		return new VncKeyword(":core/val");
	}

	@Override 
	public TypeRank typeRank() {
		return TypeRank.JUST;
	}
	
	@Override
	public Object convertToJavaObject() {
		return value.convertToJavaObject();
	}

	@Override 
	public int compareTo(final VncVal o) {
		if (o == Constants.Nil) {
			return 1;
		}
		else if (Types.isVncJust(o)) {
			return getValue().compareTo(((VncJust)o).getValue());
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
		VncJust other = (VncJust) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override 
	public String toString() {
		return toString(false);
	}
	
	public String toString(final boolean print_readably) {
		return String.format("(just %s)", value.toString(print_readably));
	}
	

    private static final long serialVersionUID = -1848883965231344442L;
 
	private final VncVal value;
}