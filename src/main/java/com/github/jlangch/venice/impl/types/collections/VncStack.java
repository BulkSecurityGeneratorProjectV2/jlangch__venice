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
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

import com.github.jlangch.venice.impl.Printer;
import com.github.jlangch.venice.impl.types.Constants;
import com.github.jlangch.venice.impl.types.VncVal;


public class VncStack extends VncCollection {

	public VncStack() {
		super(Constants.Nil);
		this.stack = new ConcurrentLinkedDeque<>();
	}

	private VncStack(final VncStack stack, final VncVal meta) {
		super(meta);
		this.stack = stack.stack;
	}

	
	@Override
	public VncCollection empty() {
		return new VncStack();
	}

	@Override
	public VncStack withMeta(final VncVal meta) {
		return new VncStack(this, meta);
	}

	@Override
	public VncList toVncList() {
		return VncList.of(stack.toArray(new VncVal[0]));
	}

	@Override
	public VncVector toVncVector() {
		return VncVector.of(stack.toArray(new VncVal[0]));
	}

	@Override
	public int size() {
		return stack.size();
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}
	
	public VncStack push(final VncVal val) {
		stack.push(val);
		return this;
	}

	public VncVal pop() {
		return toNil(stack.poll());
	}

	public VncVal peek() {
		return toNil(stack.peek());
	}

	public void clear() {
		stack.clear();
	}
	
	@Override 
	public int typeRank() {
		return 203;
	}

	@Override
	public Object convertToJavaObject() {
		return Arrays
				.stream(stack.toArray(new VncVal[0]))
				.map(v -> v.convertToJavaObject())
				.collect(Collectors.toList());
	}

	@Override 
	public String toString() {
		return "(" + Printer.join(toVncList().getList(), " ", true) + ")";
	}
	
	public String toString(final boolean print_readably) {
		return "(" + Printer.join(toVncList().getList(), " ", print_readably) + ")";
	}

	
	private VncVal toNil(final VncVal val) {
		return val == null ? Constants.Nil : val;
	}


	private static final long serialVersionUID = -564531670922145260L;

	private final ConcurrentLinkedDeque<VncVal> stack;
}
