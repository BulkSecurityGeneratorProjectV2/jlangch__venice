/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2018 Venice
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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

import com.github.jlangch.venice.impl.util.ThreadLocalMap;
import com.github.jlangch.venice.util.CallFrame;
import com.github.jlangch.venice.util.CallStack;


public class VncException extends RuntimeException {

	public VncException() {
		callstack = ThreadLocalMap.getCallStack().copy();
	}
	
	public VncException(final String message) {
		super(message);
		callstack = ThreadLocalMap.getCallStack().copy();
	}

	public VncException(final String message, final Throwable cause) {
		super(message, cause);
		callstack = ThreadLocalMap.getCallStack().copy();
	}

	public VncException(final Throwable cause) {
		super(cause);
		callstack = ThreadLocalMap.getCallStack().copy();
	}

	public boolean hasCallStack() {
		return !callstack.isEmpty();
	}

	public List<CallFrame> getCallStack() {
		return callstack.callstack();
	}

	public List<String> getCallStackAsStringList() {
		return getCallStack()
					.stream()
					.map(v -> callFrameToString(v))
					.collect(Collectors.toList());
	}

	public String getCallStackAsString(final String indent) {
		return getCallStack()
					.stream()
					.map(v -> (indent == null ? "" : indent) + callFrameToString(v))
					.collect(Collectors.joining("\n"));
	}
	
	public void printVeniceStackTrace() {
		printVeniceStackTrace(System.out);
	}

	public void printVeniceStackTrace(final PrintStream ps) {
		printVeniceStackTrace(new PrintWriter(ps));
	}
	
	public void printVeniceStackTrace(final PrintWriter pw) {
		pw.println(String.format(
				"Exception in thread \"%s\" %s: %s",
				Thread.currentThread().getName(),
				getClass().getSimpleName(),
				getMessage()));
		
		if (hasCallStack()) {
			pw.println(getCallStackAsString("    at: "));
		}
		
		if (getCause() != null) {
			printVeniceCauseStackTrace(pw, getCause());
		}
	}

	public String printVeniceStackTraceToString() {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		printVeniceStackTrace(pw);
		pw.flush();
		return sw.toString();
	}

	private void printVeniceCauseStackTrace(final PrintWriter pw, final Throwable ex) {
		pw.println(String.format(
				"Caused by: %s: %s",
				ex instanceof VncException
					? ex.getClass().getSimpleName()
					: ex.getClass().getName(),
				ex.getMessage()));

		if (ex instanceof VncException) {
			final VncException vncEx = (VncException)ex;
			if (vncEx.hasCallStack()) {
				pw.println(vncEx.getCallStackAsString("    at: "));
			}
		}
		
		if (ex.getCause() != null) {
			printVeniceCauseStackTrace(pw, ex.getCause());
		}
	}

	private String callFrameToString(final CallFrame callFrame) {
		return callFrame.getFnName() == null
				? String.format(
						"%s: line %d, col %d", 
						callFrame.getFile(), 
						callFrame.getLine(), 
						callFrame.getCol())
				: String.format(
						"%s (%s: line %d, col %d)", 
						callFrame.getFnName(), 
						callFrame.getFile(), 
						callFrame.getLine(), 
						callFrame.getCol());
	}

	
	private static final long serialVersionUID = 5439694361809280080L;
	
	private final CallStack callstack;
}