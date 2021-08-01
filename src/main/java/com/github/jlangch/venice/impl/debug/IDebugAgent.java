/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2021 Venice
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
package com.github.jlangch.venice.impl.debug;

import java.util.Map;


public interface IDebugAgent {

	/**
	 * Detach the debugger from Venice
	 */
	void detach();

	
	// -------------------------------------------------------------------------
	// Breakpoint management
	// -------------------------------------------------------------------------

	/**
	 * @return all the registered breakpoints
	 */
	Map<String, BreakpointFn> getBreakpoints();

	/**
	 * Add a new breakpoint
	 * 
	 * @param breakpoint A breakpoint
	 */
	void addBreakpoint(BreakpointFn breakpoint);

	/**
	 * Removes a breakpoint
	 * 
	 * @param qualifiedName The qualified name of the function or special form
	 */
	void removeBreakpoint(String qualifiedName);

	/**
	 * Remove all breakpoints
	 */
	void removeAllBreakpoints();

	
	void storeBreakpoints();
	
	void restoreBreakpoints();


	// -------------------------------------------------------------------------
	// Breaks
	// -------------------------------------------------------------------------

	/**
	 * Checks if there is a breakpoint matching the qualified name
	 *  
	 * @param qualifiedName The qualified name of the function or special form
	 * @return Returns <code>true</code> if there is a breakpoint matching the
	 *         qualified name, otherwise <code>false</code>.
	 */
	boolean hasBreak(String qualifiedName);

	void addBreakListener(IBreakListener listener);

	boolean hasBreak();
	
	Break getBreak();

	void resume();

	void stepToNextFn();

	void stepToNextNonSystemFn();

	void stepToReturn();

}
