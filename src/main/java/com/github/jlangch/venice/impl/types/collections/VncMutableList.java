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
package com.github.jlangch.venice.impl.types.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.Printer;
import com.github.jlangch.venice.impl.types.Constants;
import com.github.jlangch.venice.impl.types.TypeRank;
import com.github.jlangch.venice.impl.types.VncKeyword;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.util.Types;
import com.github.jlangch.venice.impl.util.ErrorMessage;


public class VncMutableList extends VncSequence {

	public VncMutableList() {
		this(null, null);
	}

	public VncMutableList(final VncVal meta) {
		this(null, meta);
	}

	public VncMutableList(final Collection<? extends VncVal> vals) {
		this(vals, null);
	}

	@SuppressWarnings("unchecked")
	public VncMutableList(final Collection<? extends VncVal> vals, final VncVal meta) {
		super(meta == null ? Constants.Nil : meta);
		if (vals == null) {
			value = new CopyOnWriteArrayList<>();
		}
		else if (vals instanceof CopyOnWriteArrayList){
			value = (CopyOnWriteArrayList<VncVal>)vals;
		}
		else {
			value = new CopyOnWriteArrayList<>(vals);
		}
	}
	
	
	public static VncMutableList of(final VncVal... mvs) {
		return new VncMutableList(Arrays.asList(mvs), Constants.Nil);
	}
	
	
	@Override
	public VncMutableList emptyWithMeta() {
		return new VncMutableList(getMeta());
	}
	
	@Override
	public VncMutableList withVariadicValues(final VncVal... replaceVals) {
		return VncMutableList.of(replaceVals).withMeta(getMeta());
	}
	
	@Override
	public VncMutableList withValues(final List<? extends VncVal> replaceVals) {
		return new VncMutableList(replaceVals, getMeta());
	}

	@Override
	public VncMutableList withValues(final List<? extends VncVal> replaceVals, final VncVal meta) {
		return new VncMutableList(replaceVals, meta);
	}

	@Override
	public VncMutableList withMeta(final VncVal meta) {
		return new VncMutableList(value, meta);
	}
	
	@Override
	public VncKeyword getType() {
		return TYPE;
	}
	
	@Override
	public VncKeyword getSupertype() {
		return VncSequence.TYPE;
	}

	@Override
	public List<VncKeyword> getAllSupertypes() {
		return Arrays.asList(VncSequence.TYPE, VncVal.TYPE);
	}

	@Override
	public void forEach(Consumer<? super VncVal> action) {
		value.forEach(v -> action.accept(v));
	}

	@Override
	public List<VncVal> getList() { 
		return value; 
	}

	@Override
	public int size() {
		return value.size();
	}
	
	@Override
	public boolean isEmpty() {
		return value.isEmpty();
	}

	@Override
	public VncVal nth(final int idx) {
		if (idx < 0 || idx >= value.size()) {
			throw new VncException(String.format(
						"nth: index %d out of range for a mutable list of size %d. %s", 
						idx, 
						size(),
						isEmpty() ? "" : ErrorMessage.buildErrLocation(value.get(0))));
		}

		return value.get(idx);
	}

	@Override
	public VncVal nthOrDefault(final int idx, final VncVal defaultVal) {
		return idx >= 0 && idx < value.size() ? value.get(idx) : defaultVal;
	}

	@Override
	public VncVal first() {
		return isEmpty() ? Constants.Nil : value.get(0);
	}

	@Override
	public VncVal last() {
		return isEmpty() ? Constants.Nil : value.get(value.size()-1);
	}
	
	@Override
	public VncMutableList rest() {
		return isEmpty() 
				? new VncMutableList(getMeta()) 
				: new VncMutableList(value.subList(1, value.size()), getMeta());
	}
	
	@Override
	public VncMutableList butlast() {
		return isEmpty() 
				? new VncMutableList(getMeta()) 
				: new VncMutableList(value.subList(0, value.size()-1), getMeta());
	}

	@Override
	public VncMutableList slice(final int start, final int end) {
		return new VncMutableList(value.subList(start, end), getMeta());
	}
	
	@Override
	public VncMutableList slice(final int start) {
		return new VncMutableList(value.subList(start, value.size()), getMeta());
	}
	
	@Override
	public VncList toVncList() {
		return new VncList(value, getMeta());
	}

	@Override
	public VncVector toVncVector() {
		return new VncVector(value, getMeta());
	}

	
	@Override
	public VncMutableList addAtStart(final VncVal val) {
		value.add(0, val);
		return this;
	}
	
	@Override
	public VncMutableList addAllAtStart(final VncSequence list) {
		value.addAll(0, list.getList());
		return this;
	}
	
	@Override
	public VncMutableList addAtEnd(final VncVal val) {
		value.add(val);
		return this;
	}
	
	@Override
	public VncMutableList addAllAtEnd(final VncSequence list) {
		value.addAll(list.getList());
		return this;
	}
	
	@Override
	public VncMutableList setAt(final int idx, final VncVal val) {
		value.set(idx, val);
		return this;
	}
	
	@Override
	public VncMutableList removeAt(final int idx) {
		value.remove(idx);
		return this;
	}
	
	@Override 
	public TypeRank typeRank() {
		return TypeRank.MUTABLELIST;
	}

	@Override 
	public boolean isVncList() {
		return true;
	}

	@Override
	public Object convertToJavaObject() {
		return getList()
				.stream()
				.map(v -> v.convertToJavaObject())
				.collect(Collectors.toList());
	}
	
	@Override
	public int compareTo(final VncVal o) {
		if (o == Constants.Nil) {
			return 1;
		}
		else if (Types.isVncList(o)) {
			final Integer sizeThis = size();
			final Integer sizeOther = ((VncMutableList)o).size();
			int c = sizeThis.compareTo(sizeOther);
			if (c != 0) {
				return c;
			}
			else {
				for(int ii=0; ii<sizeThis; ii++) {
					c = nth(ii).compareTo(((VncMutableList)o).nth(ii));
					if (c != 0) {
						return c;
					}
				}
				return 0;
			}
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
		VncMutableList other = (VncMutableList) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override 
	public String toString() {
		return "(" + Printer.join(value, " ", true) + ")";
	}
	
	public String toString(final boolean print_readably) {
		return "(" + Printer.join(value, " ", print_readably) + ")";
	}

   
	public static final VncKeyword TYPE = new VncKeyword(":core/mutable-list");

	private static final long serialVersionUID = -1848883965231344442L;
 
	private final CopyOnWriteArrayList<VncVal> value;
}