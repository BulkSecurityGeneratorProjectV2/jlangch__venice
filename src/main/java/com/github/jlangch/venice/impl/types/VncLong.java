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

import com.github.jlangch.venice.impl.functions.Numeric;
import com.github.jlangch.venice.impl.types.util.Types;


public class VncLong extends VncVal {

	public VncLong(final Long v) { 
		this(v, Constants.Nil); 
	}
	
	public VncLong(final Integer v) { 
		this(v.longValue(), Constants.Nil); 
	}

	public VncLong(final Long v, final VncVal meta) { 
		super(meta);
		value = v; 
	}
	
	
	@Override
	public VncLong withMeta(final VncVal meta) {
		return new VncLong(value, meta);
	}
	
	@Override
	public VncKeyword getType() {
		return TYPE;
	}
	
	@Override
	public VncKeyword getSupertype() {
		return new VncKeyword(":core/val");
	}
	
	public VncLong negate() { 
		return new VncLong(value * -1L); 
	}

	public Long getValue() { 
		return value; 
	}
	
	public Integer getIntValue() { 
		return value.intValue(); 
	}
	
	@Override 
	public TypeRank typeRank() {
		return TypeRank.LONG;
	}
	
	@Override
	public Object convertToJavaObject() {
		return value;
	}

	@Override 
	public int compareTo(final VncVal o) {
		if (Types.isVncLong(o)) {
			return value.compareTo(((VncLong)o).getValue());
		}
		else if (Types.isVncInteger(o)) {
			return value.compareTo(((VncInteger)o).getLongValue());
		}
		else if (Types.isVncDouble(o)) {
			return value.compareTo(Numeric.doubleToLong((VncDouble)o).getValue());
		}
		else if (Types.isVncBigDecimal(o)) {
			return value.compareTo(Numeric.decimalToLong((VncBigDecimal)o).getValue());
		}
		else if (o == Constants.Nil) {
			return 1;
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
		VncLong other = (VncLong) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override 
	public String toString() {
		return value.toString();
	}

    
    public final static VncKeyword TYPE = new VncKeyword(":core/long");

    private static final long serialVersionUID = -1848883965231344442L;

	private final Long value;
}