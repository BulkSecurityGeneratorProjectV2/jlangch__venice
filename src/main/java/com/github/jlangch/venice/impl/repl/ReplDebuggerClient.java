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
package com.github.jlangch.venice.impl.repl;

import static com.github.jlangch.venice.impl.debug.BreakpointType.FunctionEntry;
import static com.github.jlangch.venice.impl.debug.BreakpointType.FunctionException;
import static com.github.jlangch.venice.impl.debug.BreakpointType.FunctionExit;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.jlangch.venice.impl.debug.Break;
import com.github.jlangch.venice.impl.debug.BreakpointType;
import com.github.jlangch.venice.impl.debug.IDebugAgent;
import com.github.jlangch.venice.impl.debug.StopNextType;
import com.github.jlangch.venice.impl.env.Env;
import com.github.jlangch.venice.impl.env.Var;
import com.github.jlangch.venice.impl.types.VncFunction;
import com.github.jlangch.venice.impl.types.VncSymbol;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.collections.VncVector;
import com.github.jlangch.venice.impl.util.CallStack;
import com.github.jlangch.venice.impl.util.CollectionUtil;
import com.github.jlangch.venice.impl.util.StringUtil;


/**
 * REPL debugger client
 * 
 * <p>A typical debug session looks like:
 * <pre>
 *   venice> (defn sum [x y] (+ x y))
 *   venice> !dbg attach
 *   venice> !dbg activate
 *   venice> !dbg breakpoint add (!) user/sum
 *   venice> (sum 6 7)
 *   Stopped in function user/sum at FunctionEntry
 *   venice> !dbg params
 *   Parameters:
 *   [x y]
 *
 *   Arguments:
 *   (6 7)
 *   venice> !dbg next
 *   Stopped in function user/sum at FunctionExit
 *   venice> !dbg retval
 *   return: 13
 *   venice> !dbg next
 *   Resuming from function user/sum
 * </pre>
 */
public class ReplDebuggerClient {

	public ReplDebuggerClient(
			final IDebugAgent agent,
			final TerminalPrinter printer
	) {
		this.agent = agent;
		this.printer = printer;
	}

	public void handleDebuggerCommand(final List<String> params) {
		switch(StringUtil.trimToEmpty(params.get(0))) {
			case "start":
				start();
				break;
			case "stop":
				stop();
				break;
			case "breakpoint":
			case "bp":
				handleBreakpointCmd(CollectionUtil.drop(params, 1));
				break;
			case "next":
			case "n":
				run();
				break;
			case "next+":
			case "n+":
				runToNextFunction();
				break;			
			case "next-":
			case "n-":
				runToNextNonSystemFunction();
				break;			
			case "callstack":
			case "cs":
				callstack();
				break;
			case "params":
			case "p":
				fn_args(CollectionUtil.drop(params, 1));
				break;
			case "locals":
				locals(CollectionUtil.drop(params, 1));
				break;
			case "local":
				local(params.get(1));
				break;
			case "global":
				global(params.get(1));
				break;
			case "retval":
			case "ret":
				retval();
				break;
			case "ex":
				ex();
				break;
			default:
				printer.println("error", "Invalid dbg command.");
				break;
		}
	}

	private void start() {
		agent.start();
		agent.addBreakListener(this::breakpointListener);
		printer.println("stdout", "Debugger: started");
	}
	
	private void stop() {
		agent.stop();
		printer.println("stdout", "Debugger: stopped");
	}
	
	private void run() {
		// final String fnName = agent.getBreak().getFn().getQualifiedName();
		// printer.println("debug", "Returning from function " + fnName);
		agent.leaveBreak(StopNextType.MatchingFnName);
	}
	
	private void runToNextFunction() {
		// final String fnName = agent.getBreak().getFn().getQualifiedName();
		// printer.println("debug", "Returning from function " + fnName + ". Stop on next function...");
		agent.leaveBreak(StopNextType.AnyFunction);
	}
	
	private void runToNextNonSystemFunction() {
		agent.leaveBreak(StopNextType.AnyNonSystemFunction);
	}
	
	private void callstack() {
		if (!agent.hasBreak()) {
			printer.println("debug", "Not in a break!");
			return;
		}
		
		final CallStack cs = agent.getBreak().getCallStack();
		printer.println("debug", cs.toString());
	}
	
	private void fn_args(final List<String> params) {
		if (!agent.hasBreak()) {
			printer.println("debug", "Not in a break!");
			return;
		}
		
		final VncFunction fn = agent.getBreak().getFn();
		final VncVector spec = fn.getParams();	
		final VncList args = agent.getBreak().getArgs();
		
		printer.println("debug", 
						"Parameters:\n" + spec.toString(true) +
						"\n\nArguments:\n" + args.toString(true));
	}
	
	private void locals(final List<String> params) {
		if (!agent.hasBreak()) {
			printer.println("debug", "Not in a break!");
			return;
		}

		Env env = agent.getBreak().getEnv();
		int maxLevel = env.level() + 1;
		int level = params.isEmpty() ? 1 : Integer.parseInt(params.get(0));
		level = Math.max(Math.min(maxLevel, level), 1);
		
		printer.println(
			"debug",
			String.format(
				"[%d/%d] Local vars:\n%s",
				level,
				maxLevel,
				env.getLocalVars(level-1)
				   .stream()
				   .map(v -> v.getName().getSimpleName())
				   .map(s -> "   " + s)
				   .collect(Collectors.joining("\n"))));
	}
	
	private void local(final String name) {
		if (!agent.hasBreak()) {
			printer.println("debug", "Not in a break!");
			return;
		}

		final VncSymbol sym = new VncSymbol(name);
		final Var v = agent.getBreak().getEnv().findLocalVar(sym);
		if (v == null) {
			printer.println(
					"debug", 
					String.format("%s: <not found>", name));
		}
		else {
			final String sval = StringUtil.truncate(v.toString(true), 100, "...");
			printer.println("debug", String.format("%s: %s", name, sval));
		}
	}
	
	private void global(final String name) {
		if (!agent.hasBreak()) {
			printer.println("debug", "Not in a break!");
			return;
		}

		final VncSymbol sym = new VncSymbol(name);
		final Var v = agent.getBreak().getEnv().getGlobalVarOrNull(sym);
		if (v == null) {
			printer.println(
					"debug", 
					String.format("%s: <not found>", name));
		}
		else {
			final String sval = StringUtil.truncate(v.toString(true), 100, "...");
			printer.println("debug", String.format("%s: %s", name, sval));
		}
	}
	
	private void retval() {
		if (!agent.hasBreak()) {
			printer.println("debug", "Not in a break!");
			return;
		}

		final VncVal v = agent.getBreak().getRetVal();
		if (v == null) {
			printer.println("debug", "return: <not available>");
		}
		else {
			final String sval = StringUtil.truncate(v.toString(true), 100, "...");
			printer.println("debug", String.format("return: %s", sval));
		}
	}
	
	private void ex() {
		if (!agent.hasBreak()) {
			printer.println("debug", "Not in a break!");
			return;
		}

		final Exception e = agent.getBreak().getException();
		if (e == null) {
			printer.println("debug", "exception: <not available>");
		}
		else {
			printer.printex("debug", e);
		}
	}
	
	private void handleBreakpointCmd(final List<String> params) {
		if (params.size() < 1)  {
			printer.println("error", "Invalid 'dbg breakpoint {cmd}' command");
		}
		else {
			switch(StringUtil.trimToEmpty(params.get(0))) {
				case "add":
					String types = StringUtil.trimToEmpty(params.get(1));
					if (types.matches("^[(!)]+$")) {
						CollectionUtil.drop(params, 2)
									  .stream()
									  .filter(s -> !s.matches("^[(!)]+$"))
						  			  .forEach(s -> agent.addBreakpoint(
						  					 			s, 
						  					 			parseBreakpointTypes(types)));
					}
					else {
						CollectionUtil.drop(params, 1)
									  .stream()
									  .filter(s -> !s.matches("^[(!)]+$"))
									  .forEach(s -> agent.addBreakpoint(
											  			s,
											  			parseBreakpointTypes("(")));
					}
					break;
					
				case "remove":
					CollectionUtil.drop(params, 1)
								  .forEach(s -> agent.removeBreakpoint(s));
					break;
					
				case "clear":
					agent.removeAllBreakpoints();
					break;
					
				case "list":
					agent.getBreakpoints()
						 .entrySet()
						 .stream()
						 .sorted(Comparator.comparing(e -> e.getKey()))
						 .forEach(e -> printer.println(
								 		"stdout", 
								 		String.format(
								 			"  %s %s", 
								 			e.getKey(),
								 			format(e.getValue()))));
					break;
			}
		}
	}
	
	private String format(final BreakpointType type) {
		switch(type) {
			case FunctionEntry:     return "(";
			case FunctionException: return "!";
			case FunctionExit:      return ")";
			default:                return "";
		}
	}

	private String format(final Set<BreakpointType> types) {
		// predefined order of breakpoint types
		return Arrays.asList(FunctionEntry, FunctionException, FunctionExit)
					 .stream()
					 .filter(t -> types.contains(t))
					 .map(t -> format(t))
					 .collect(Collectors.joining());
	}

	private Set<BreakpointType> parseBreakpointTypes(final String types) {
		return Arrays.asList(BreakpointType.values())
					 .stream()
					 .filter(t -> types.contains(format(t)))
					 .collect(Collectors.toSet());
	}
	
	private void breakpointListener(final Break b) {
		printer.println(
				"debug", 
				String.format(
						"Stopped in function %s at %s",
						b.getFn().getQualifiedName(),
						b.getBreakpointType()));		
	}
   
    
	private final TerminalPrinter printer;
	private final IDebugAgent agent;
}