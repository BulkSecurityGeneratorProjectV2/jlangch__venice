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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.Printer;
import com.github.jlangch.venice.impl.types.Constants;
import com.github.jlangch.venice.impl.types.TypeRank;
import com.github.jlangch.venice.impl.types.VncKeyword;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.util.Types;
import com.github.jlangch.venice.impl.util.EmptyIterator;
import com.github.jlangch.venice.impl.util.ErrorMessage;


public class VncMutableVector extends VncSequence {

	public VncMutableVector() {
		this(null, null);
	}

	public VncMutableVector(final VncVal meta) {
		this(null, meta);
	}

	public VncMutableVector(final Collection<? extends VncVal> vals) {
		this(vals, null);
	}

	@SuppressWarnings("unchecked")
	public VncMutableVector(final Collection<? extends VncVal> vals, final VncVal meta) {
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
	
	
	public static VncMutableVector of(final VncVal... mvs) {
		return new VncMutableVector(Arrays.asList(mvs), Constants.Nil);
	}
	
	
	@Override
	public VncMutableVector emptyWithMeta() {
		return new VncMutableVector(getMeta());
	}
	
	@Override
	public VncMutableVector withVariadicValues(final VncVal... replaceVals) {
		return VncMutableVector.of(replaceVals).withMeta(getMeta());
	}
	
	@Override
	public VncMutableVector withValues(final List<? extends VncVal> replaceVals) {
		return new VncMutableVector(replaceVals, getMeta());
	}

	@Override
	public VncMutableVector withValues(final List<? extends VncVal> replaceVals, final VncVal meta) {
		return new VncMutableVector(replaceVals, meta);
	}

	@Override
	public VncMutableVector withMeta(final VncVal meta) {
		return new VncMutableVector(value, meta);
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
    public Iterator<VncVal> iterator() {
        return isEmpty() ? EmptyIterator.empty() : value.iterator();
    }

	@Override
	public void forEach(Consumer<? super VncVal> action) {
		value.forEach(v -> action.accept(v));
	}
	
	@Override
	public VncList filter(final Predicate<? super VncVal> predicate) {
		return new VncList(
					value.stream()
						 .filter(predicate)
						 .collect(Collectors.toList()), 
					getMeta());
	}

	@Override
	public VncList map(final Function<? super VncVal, ? extends VncVal> mapper) {
		return new VncList(
					value.stream()
						 .map(mapper)
						 .collect(Collectors.toList()), 
					getMeta());
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
						"nth: index %d out of range for a mutable vector of size %d. %s", 
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
	public VncMutableVector rest() {
		return value.size() <= 1 ? new VncMutableVector(getMeta()) : slice(1);
	}
	
	@Override
	public VncMutableVector butlast() {
		return value.size() <= 1 ? new VncMutableVector(getMeta()) : slice(0, value.size()-1);
	}
	
	@Override
	public VncMutableVector drop(final int n) {
		return slice(n);
	}

	@Override
	public VncMutableVector dropWhile(final Predicate<? super VncVal> predicate) {
		for(int i=0; i<value.size(); i++) {
			final boolean drop = predicate.test(VncList.of(value.get(i)));
			if (!drop) {
				return slice(i);
			}
		}

		return new VncMutableVector(getMeta());
	}

	@Override
	public VncMutableVector take(final int n) {
		return slice(0, n);
	}

	@Override
	public VncMutableVector takeWhile(final Predicate<? super VncVal> predicate) {
		for(int i=0; i<value.size(); i++) {
			final boolean take = predicate.test(VncList.of(value.get(i)));
			if (!take) {
				return slice(0, i);
			}
		}

		return this;
	}

	@Override
	public VncMutableVector slice(final int start, final int end) {
		if (start >= value.size()) {
			return new VncMutableVector(getMeta());
		}
		else {
			return new VncMutableVector(value.subList(start, Math.min(end, value.size())), getMeta());
		}
	}
	
	@Override
	public VncMutableVector slice(final int start) {
		return slice(start, value.size());
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
	public VncMutableVector addAtStart(final VncVal val) {
		value.add(0, val);
		return this;
	}
	
	@Override
	public VncMutableVector addAllAtStart(final VncSequence list) {
		value.addAll(0, list.getList());
		return this;
	}
	
	@Override
	public VncMutableVector addAtEnd(final VncVal val) {
		value.add(val);
		return this;
	}
	
	@Override
	public VncMutableVector addAllAtEnd(final VncSequence list) {
		value.addAll(list.getList());
		return this;
	}
	
	@Override
	public VncMutableVector setAt(final int idx, final VncVal val) {
		value.set(idx, val);
		return this;
	}
	
	@Override
	public VncMutableVector removeAt(final int idx) {
		value.remove(idx);
		return this;
	}
	
	@Override 
	public TypeRank typeRank() {
		return TypeRank.MUTABLEVECTOR;
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
			final Integer sizeOther = ((VncMutableVector)o).size();
			int c = sizeThis.compareTo(sizeOther);
			if (c != 0) {
				return c;
			}
			else {
				for(int ii=0; ii<sizeThis; ii++) {
					c = nth(ii).compareTo(((VncMutableVector)o).nth(ii));
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
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		VncMutableVector other = (VncMutableVector) obj;
		return value.equals(other.value);
	}

	@Override 
	public String toString() {
		return "[" + Printer.join(value, " ", true) + "]";
	}
	
	public String toString(final boolean print_readably) {
		return "[" + Printer.join(value, " ", print_readably) + "]";
	}

   
	public static final VncKeyword TYPE = new VncKeyword(":core/mutable-vector");

	private static final long serialVersionUID = -1848883965231344442L;
 
	private final CopyOnWriteArrayList<VncVal> value;
}