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
package com.github.jlangch.venice.impl.types.collections;

import static com.github.jlangch.venice.impl.types.Constants.False;
import static com.github.jlangch.venice.impl.types.Constants.True;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.jlangch.venice.impl.Printer;
import com.github.jlangch.venice.impl.javainterop.JavaInteropUtil;
import com.github.jlangch.venice.impl.types.Constants;
import com.github.jlangch.venice.impl.types.IVncJavaObject;
import com.github.jlangch.venice.impl.types.VncVal;


public class VncJavaMap extends VncMap implements IVncJavaObject {

	public VncJavaMap() {
		super(Constants.Nil);
	}

	public VncJavaMap(final Map<Object,Object> map) {
		super(Constants.Nil);
		map.entrySet().forEach(e -> {
			value.put(
				e.getKey() instanceof VncVal ? JavaInteropUtil.convertToVncVal(e.getKey()) : e.getKey(), 
				e.getValue() instanceof VncVal ? JavaInteropUtil.convertToVncVal(e.getValue()) : e.getValue()); 
		});
	}
	
	
	@Override
	public Object getDelegate() {
		return value;
	}

	@Override
	public VncJavaMap empty() {
		return copyMetaTo(new VncJavaMap());
	}

	@Override
	public VncHashMap copy() {
		return copyMetaTo(new VncHashMap(getMap()));
	}

	@Override
	public VncHashMap withMeta(final VncVal meta) {
		return copyMetaTo(new VncHashMap(getMap()));
	}

	@Override
	public Map<VncVal,VncVal> getMap() {
		return value
				.entrySet()
				.stream()
				.collect(Collectors.toMap(
						e -> JavaInteropUtil.convertToVncVal(e.getKey()),
						e -> JavaInteropUtil.convertToVncVal(e.getValue())));
	}

	@Override
	public VncVal get(final VncVal key) {
		return JavaInteropUtil.convertToVncVal(
					value.get(JavaInteropUtil.convertToJavaObject(key)));
	}

	@Override
	public VncVal containsKey(final VncVal key) {
		return value.containsKey(value.get(JavaInteropUtil.convertToJavaObject(key))) ? True : False;
	}

	@Override
	public VncList keys() {
		return new VncList(
					value
						.keySet()
						.stream()
						.map(k -> JavaInteropUtil.convertToVncVal(k))
						.collect(Collectors.toList()));
	}

	@Override
	public List<VncMapEntry> entries() {
		return Collections.unmodifiableList(
					getMap()
						.entrySet()
						.stream().map(e -> new VncMapEntry(e.getKey(), e.getValue()))
						.collect(Collectors.toList()));
	}

	@Override
	public VncJavaMap putAll(final VncMap map) {
		getMap().entrySet().forEach(
				e -> value.put(
					JavaInteropUtil.convertToJavaObject(e.getKey()), 
					JavaInteropUtil.convertToJavaObject(e.getValue())));
		return this;
	}
	
	@Override
	public VncJavaMap assoc(final VncVal... mvs) {
		for (int i=0; i<mvs.length; i+=2) {
			value.put(
				JavaInteropUtil.convertToJavaObject(mvs[i]), 
				JavaInteropUtil.convertToJavaObject(mvs[i+1]));
		}
		return this;
	}

	@Override
	public VncJavaMap assoc(final VncList mvs) {
		for (int i=0; i<mvs.getList().size(); i+=2) {
			value.put(
				JavaInteropUtil.convertToJavaObject(mvs.nth(i)), 
				JavaInteropUtil.convertToJavaObject(mvs.nth(i)));
		}
		return this;
	}

	@Override
	public VncJavaMap dissoc(final VncVal... keys) {
		for (VncVal key : keys) {
			value.remove(JavaInteropUtil.convertToJavaObject(key));
		}
		return this;
	}

	@Override
	public VncJavaMap dissoc(final VncList keys) {
		for (int i=0; i<keys.getList().size(); i++) {
			value.remove(JavaInteropUtil.convertToJavaObject(keys.nth(i)));
		}
		return this;
	}
	
	@Override
	public VncList toVncList() {
		return new VncList(value
							.entrySet()
							.stream()
							.map(e -> VncVector.ofAll(
										JavaInteropUtil.convertToVncVal(e.getKey()), 
										JavaInteropUtil.convertToVncVal(e.getValue())))
							.collect(Collectors.toList()));
	}
	
	@Override
	public VncVector toVncVector() {
		return new VncVector(value
							.entrySet()
							.stream()
							.map(e -> VncVector.ofAll(
										JavaInteropUtil.convertToVncVal(e.getKey()), 
										JavaInteropUtil.convertToVncVal(e.getValue())))
							.collect(Collectors.toList()));
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
		VncJavaMap other = (VncJavaMap) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override 
	public String toString() {
		return toString(true);
	}
	
	@Override
	public String toString(final boolean print_readably) {
		final List<VncVal> list = new ArrayList<>();
		value.entrySet().forEach(e -> {
			list.add(JavaInteropUtil.convertToVncVal(e.getKey()));
			list.add(JavaInteropUtil.convertToVncVal(e.getValue()));
		});
	
		return "{" + Printer.join(list, " ", print_readably) + "}";
	}
	

    private static final long serialVersionUID = -1848883965231344442L;

	private final Map<Object,Object> value = new HashMap<Object,Object>();	
}