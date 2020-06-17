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
package com.github.jlangch.venice.impl.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.jlangch.venice.impl.types.VncLong;


public class CallstackTest {

	@Test
	public void testPushPop() {
		final CallStack stack = new CallStack();
		
		stack.push(CallFrame.fromVal("fn-1", new VncLong(1)));
		stack.push(CallFrame.fromVal("fn-2", new VncLong(2)));
		stack.push(CallFrame.fromVal("fn-3", new VncLong(3)));
				
		assertEquals("fn-3", stack.pop().getFnName());
		assertEquals("fn-2", stack.pop().getFnName());
		assertEquals("fn-1", stack.pop().getFnName());
		assertTrue(stack.isEmpty());
	}

	@Test
	public void testClear() {
		final CallStack stack = new CallStack();
		
		stack.push(CallFrame.fromVal("fn-1", new VncLong(1)));
		stack.push(CallFrame.fromVal("fn-2", new VncLong(2)));
		stack.push(CallFrame.fromVal("fn-3", new VncLong(3)));
			
		stack.clear();
		
		assertTrue(stack.isEmpty());
	}

	@Test
	public void testCopy() {
		final CallStack stack = new CallStack();
		
		stack.push(CallFrame.fromVal("fn-1", new VncLong(1)));
		stack.push(CallFrame.fromVal("fn-2", new VncLong(2)));
		stack.push(CallFrame.fromVal("fn-3", new VncLong(3)));
		
		final CallStack copy = stack.copy();
		
		assertEquals("fn-3", copy.pop().getFnName());
		assertEquals("fn-2", copy.pop().getFnName());
		assertEquals("fn-1", copy.pop().getFnName());
		assertTrue(copy.isEmpty());
	}

	@Test
	public void testList() {
		final CallStack stack = new CallStack();
		
		stack.push(CallFrame.fromVal("fn-1", new VncLong(1)));
		stack.push(CallFrame.fromVal("fn-2", new VncLong(2)));
		stack.push(CallFrame.fromVal("fn-3", new VncLong(3)));
		
		final List<String> items = stack.toList();
		
		assertEquals(3, items.size());
		assertTrue(items.get(0).startsWith("fn-3"));
		assertTrue(items.get(1).startsWith("fn-2"));
		assertTrue(items.get(2).startsWith("fn-1"));
	}

	@Test
	public void testCallFrames() {
		final CallStack stack = new CallStack();
		
		stack.push(CallFrame.fromVal("fn-1", new VncLong(1)));
		stack.push(CallFrame.fromVal("fn-2", new VncLong(2)));
		stack.push(CallFrame.fromVal("fn-3", new VncLong(3)));
		
		final List<CallFrame> items = stack.callstack();
		
		assertEquals(3, items.size());
		assertEquals("fn-3", items.get(0).getFnName());
		assertEquals("fn-2", items.get(1).getFnName());
		assertEquals("fn-1", items.get(2).getFnName());
	}
	
}
