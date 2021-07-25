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
import java.util.Set;


public interface IDebugAgent {

	// -------------------------------------------------------------------------
	// Debugger turn on/off
	// -------------------------------------------------------------------------
	
	void start();
	
	void stop();

	boolean active();



	// -------------------------------------------------------------------------
	// Breakpoint management
	// -------------------------------------------------------------------------

	boolean hasBreakpoint(String qualifiedName);

	Map<String, Set<BreakpointType>> getBreakpoints();

	void addBreakpoint(String qualifiedName, Set<BreakpointType> types);

	void removeBreakpoint(String qualifiedName);

	void removeAllBreakpoints();



	// -------------------------------------------------------------------------
	// Breaks
	// -------------------------------------------------------------------------

	void addBreakListener(IBreakListener listener);

	boolean hasBreak();
	
	Break getBreak();

	void leaveBreak(StopNextType type);

}