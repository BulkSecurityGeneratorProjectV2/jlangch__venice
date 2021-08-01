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

import static com.github.jlangch.venice.impl.debug.BreakpointScope.FunctionEntry;
import static com.github.jlangch.venice.impl.debug.BreakpointScope.FunctionException;
import static com.github.jlangch.venice.impl.debug.BreakpointScope.FunctionExit;
import static com.github.jlangch.venice.impl.util.CollectionUtil.drop;
import static com.github.jlangch.venice.impl.util.CollectionUtil.first;
import static com.github.jlangch.venice.impl.util.CollectionUtil.*;
import static com.github.jlangch.venice.impl.util.CollectionUtil.toSet;
import static com.github.jlangch.venice.impl.util.StringUtil.trimToEmpty;
import static com.github.jlangch.venice.impl.util.StringUtil.trimToNull;
import static com.github.jlangch.venice.impl.util.StringUtil.truncate;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.jlangch.venice.impl.Destructuring;
import com.github.jlangch.venice.impl.debug.Break;
import com.github.jlangch.venice.impl.debug.BreakpointScope;
import com.github.jlangch.venice.impl.debug.IDebugAgent;
import com.github.jlangch.venice.impl.env.Env;
import com.github.jlangch.venice.impl.env.Var;
import com.github.jlangch.venice.impl.types.VncFunction;
import com.github.jlangch.venice.impl.types.VncSymbol;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.collections.VncVector;
import com.github.jlangch.venice.impl.util.CallFrame;


/**
 * REPL debugger client
 * 
 * <p>A typical debug session looks like:
 * <pre>
 *   venice> (defn sum [x y] (+ x y))
 *   venice> !attach
 *   debug> !breakpoint add (!) user/sum
 *   debug> (sum 6 7)
 *   Stopped in function user/sum (user: line 1, col 7) at entry
 *   debug> !params
 *   Break in function user/sum at entry.
 *   Arguments passed to function user/sum:
 *   x -> 6
 *   y -> 7
 *   debug> !step-return
 *   Stopped in function user/sum (user: line 1, col 7) at exit
 *   debug> !params
 *   Break in function user/sum at exit.
 *   Arguments passed to function user/sum:
 *   x -> 6
 *   y -> 7
 *   [return] -> 13
 *   debug> !resume
 * </pre>
 */
public class ReplDebugClient {

	public ReplDebugClient(
			final IDebugAgent agent,
			final TerminalPrinter printer,
			final Thread replThread
	) {
		this.agent = agent;
		this.printer = printer;
		this.replThread = replThread;
		
		agent.addBreakListener(this::breakpointListener);
	}

	public void handleCommand(final String cmdLine) {
		final List<String> params = Arrays.asList(cmdLine.split(" +"));

		switch(trimToEmpty(first(params))) {
			case "breakpoint":
			case "b":
				handleBreakpointCmd(drop(params, 1));
				break;

			case "resume":
			case "r":
				agent.resume();
				break;
				
			case "step-into":
			case "si":
				agent.stepToNextFn();
				break;
				
			case "step-into-":
			case "si-":
				agent.stepToNextNonSystemFn();
				break;
				
			case "step-return":
			case "sr":
				stepReturn();
				break;
				
			case "break?": 
			case "b?":
				isBreak();
				break;
				
			case "callstack": 
			case "cs":
				callstack();
				break;
				
			case "params": 
			case "p":
				params(drop(params, 1));
				break;
				
			case "locals":
			case "l":
				locals(second(params));
				break;
				
			case "local": 
				local(second(params));
				break;
				
			case "global":
				global(second(params));
				break;
				
			case "retval":
			case "ret":
				retval();
				break;
				
			case "ex":
				ex();
				break;

			default:
				println(HELP);
				break;
		}
	}

	public static void pringHelp(final TerminalPrinter printer) {
		printer.println("stdout", HELP);
	}
	
	private void isBreak() {
		if (!agent.hasBreak()) {
			println("Not in a debug break!");
		}
		else {
			println(formatStop(agent.getBreak()));
		}
	}
	
	private void stepReturn() {
		if (!agent.hasBreak()) {
			printer.println("error", "Not in a break!");
		}
		else if (agent.getBreak().isSpecialForm()) {
			printer.println("error", "Cannot not step into the return of a special form!");
		}
		else {
			printer.println(
					"debug", 
					String.format(
						"Stepping into return of function %s ...",
						agent.getBreak().getFn().getQualifiedName()));
			agent.stepToReturn();
		}
	}
	
	private void callstack() {
		if (!agent.hasBreak()) {
			println("Not in a debug break!");
			return;
		}
		
		final Break br = agent.getBreak();

		println(formatBreak(br));
		println(br.getCallStack().toString());
	}
	
	private void params(final List<String> params) {
		if (!agent.hasBreak()) {
			println("Not in a debug break!");
			return;
		}

		final Break br = agent.getBreak();

		println(formatBreak(br));

		if (br.isNativeFn()) {
			println(renderNativeFnParams(br));
		}
		else {
			final VncVector spec = br.getFn().getParams();	
			final boolean plainSymbolParams = Destructuring.isFnParamsWithoutDestructuring(spec);
			if (plainSymbolParams) {
				println(renderFnNoDestructuring(br));
			}
			else {
				println(renderFnDestructuring(br));
			}
		}
	}
	
	private void locals(final String sLevel) {
		if (!agent.hasBreak()) {
			println("Not in a debug break!");
			return;
		}
		
		final Break br = agent.getBreak();

		println(formatBreak(br));

		Env env = agent.getBreak().getEnv();
		if (env == null) {
			println("No information on local vars available");
		}
		else {
			int maxLevel = env.level() + 1;
			int level = sLevel == null ? 1 : Integer.parseInt(sLevel);
			level = Math.max(Math.min(maxLevel, level), 1);
			
			final List<Var> vars = env.getLocalVars(level-1);
			final String info = vars.isEmpty()
									? String.format(
										"   <no local vars at level %d>",
										level)	
									: vars.stream()
										  .map(v -> formatVar(v))
										  .collect(Collectors.joining("\n"));
	
			println(String.format(
						"Local vars at level %d/%d:\n%s",
						level,
						maxLevel,
						info));
		}
	}
	
	private void local(final String name) {
		if (!agent.hasBreak()) {
			println("Not in a debug break!");
			return;
		}

		final Env env = agent.getBreak().getEnv();
		if (env == null) {
			println("No information on local vars available");
		}
		else {
			final VncSymbol sym = new VncSymbol(name);
			final Var v = env.findLocalVar(sym);
			if (v == null) {
				println(String.format("%s -> <not found>", name));
			}
			else {
				println(formatVar(sym, v.getVal()));
			}
		}
	}
	
	private void global(final String name) {
		if (!agent.hasBreak()) {
			println("Not in a debug break!");
			return;
		}

		final Env env = agent.getBreak().getEnv();
		if (env == null) {
			println("No information on global vars available");
		}
		else {
			final VncSymbol sym = new VncSymbol(name);
			final Var v = env.getGlobalVarOrNull(sym);
			if (v == null) {
				println(String.format("%s: <not found>", name));
			}
			else {
				final String sval = truncate(v.getVal().toString(true), 100, "...");
				println(String.format("%s: %s", name, sval));
			}
		}
	}
	
	private void retval() {
		if (!agent.hasBreak()) {
			println("Not in a debug break!");
			return;
		}
		
		final Break br = agent.getBreak();

		println(formatBreak(br));

		final VncVal v = br.getRetVal();
		if (v == null) {
			println("Return value: <not available>");
		}
		else {
			final String sval = truncate(v.toString(true), 100, "...");
			println(String.format("Return value: %s", sval));
		}
	}
	
	private void ex() {
		if (!agent.hasBreak()) {
			println("Not in a debug break!");
			return;
		}
	
		final Break br = agent.getBreak();

		println(formatBreak(br));

		final Exception e = br.getException();
		if (e == null) {
			println("exception: <not available>");
		}
		else {
			printer.printex("debug", e);
		}
	}

	private void handleBreakpointCmd(final List<String> params) {
		if (params.size() < 1)  {
			printlnErr("Invalid 'dbg breakpoint {cmd}' command");
		}
		else {
			// build regex: "^[(!)]+$"
			final String regex = "^[" + getBreakpointScopeSymbolList() + "]+$";
			
			switch(trimToEmpty(params.get(0))) {
				case "add":
					String scopes = trimToEmpty(params.get(1));
					if (scopes.matches(regex)) {
						drop(params, 2)
							.stream()
							.filter(s -> !s.matches(regex))
							.forEach(s -> agent.addBreakpoint(
			  					 			s, 
			  					 			parseBreakpointScopes(scopes)));
					}
					else {
						drop(params, 1)
							.stream()
							.filter(s -> !s.matches(regex))
							.forEach(s -> agent.addBreakpoint(
								  			s,
								  			toSet(FunctionEntry)));
					}
					break;
					
				case "remove":
					drop(params, 1).forEach(s -> agent.removeBreakpoint(s));
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
					
				default:
					printlnErr("Invalid breakpoint command.");
					break;
			}
		}
	}
	
	private String format(final Set<BreakpointScope> scopes) {
		// predefined order of breakpoint scopes
		if (scopes.contains(FunctionException) || scopes.contains(FunctionExit)) {
			return Arrays.asList(FunctionEntry, FunctionException, FunctionExit)
						 .stream()
						 .filter(t -> scopes.contains(t))
						 .map(t -> t.symbol())
						 .collect(Collectors.joining());
		}
		else {
			return "";
		}
	}

	private Set<BreakpointScope> parseBreakpointScopes(final String scopes) {
		return parseBreakpointScopes(scopes, new HashSet<>());
	}

	private Set<BreakpointScope> parseBreakpointScopes(
			final String scopes,
			final Set<BreakpointScope> defaultScopes
	) {
		if (trimToNull(scopes) == null) {
			return defaultScopes;
		}
		else {
			Set<BreakpointScope> tset = Arrays.asList(BreakpointScope.values())
											 .stream()
											 .filter(t -> scopes.contains(t.symbol()))
											 .collect(Collectors.toSet());
			
			return tset.isEmpty() ? defaultScopes : tset;
		}
	}
	
	private String getBreakpointScopeSymbolList() {
		// return "(!)"
		return BreakpointScope
					.all()
					.stream()
					.map(t -> t.symbol())
					.collect(Collectors.joining());
	}
	
	private void breakpointListener(final Break b) {
		printer.println("debug", formatStop(b));

		// Interrupt the LineReader to display a new prompt
		replThread.interrupt();
	}

	private String renderNativeFnParams(final Break br) {
		final VncFunction fn = br.getFn();
		final VncList args = br.getArgs();

		final StringBuilder sb = new StringBuilder();
		
		sb.append(String.format(
					"Arguments passed to native function %s:",
					fn.getQualifiedName()));
		
		VncList args_ = args;
		
		for(int ii=0; ii<10; ii++) {
			sb.append("\n");
			sb.append(formatVar(ii, args_.first()));
			
			args_ = args_.rest();
			if (args_.isEmpty()) break;
		}
		
		if (args_.size() > 0) {
			sb.append(String.format(
						"\n... %d more arguments not displayed", 
						args_.size()));
		}
		
		if (br.getRetVal() != null) {
			sb.append('\n');
			sb.append(formatReturnVal(br.getRetVal()));
		}
		
		return sb.toString();
	}
	   
	private String renderFnNoDestructuring(final Break br) {
		final VncFunction fn = br.getFn();
		final VncVector spec = fn.getParams();	
		final VncList args = br.getArgs();

		final StringBuilder sb = new StringBuilder();
	
		VncVector spec_ = spec;
		VncList args_ = args;

		sb.append(String.format(
				"Arguments passed to %s %s:",
				br.isSpecialForm() 
					? "special form"
					: "function",
				fn.getQualifiedName()));

		while(true) {
			sb.append("\n");
			sb.append(formatVar(spec_.first(), args_.first()));
			
			spec_ = spec_.rest();
			args_ = args_.rest();
			if (spec_.isEmpty()) {
				if (!args_.isEmpty()) {
					sb.append(String.format(
								"\n... %d more arguments not matching a parameter", 
								args_.size()));
				}
				break;
			}
		}
		
		if (br.getRetVal() != null) {
			sb.append('\n');
			sb.append(formatReturnVal(br.getRetVal()));
		}
		
		return sb.toString();
	}
	   
	private String renderFnDestructuring(final Break br) {
		final VncFunction fn = br.getFn();
		final VncVector spec = fn.getParams();	
		final VncList args = br.getArgs();

		final StringBuilder sb = new StringBuilder();

		sb.append(String.format(
				"Arguments passed to %s %s (destructured):",
				br.isSpecialForm() 
					? "special form"
					: "function",
				fn.getQualifiedName()));

		final List<Var> vars = Destructuring.destructure(spec, args);
		vars.forEach(v -> {
			sb.append('\n');
			sb.append(formatVar(v)); 
		});

		
		if (br.getRetVal() != null) {
			sb.append('\n');
			sb.append(formatReturnVal(br.getRetVal()));
		}

		return sb.toString();
	}
	
	private String formatVar(final Var v) {
		return formatVar(v.getName(), v.getVal());
	}
	
	private String formatVar(final VncVal name, final VncVal value) {
		final String sval = truncate(value.toString(true), 100, "...");
		final String sname =  name.toString(true);
		return String.format("%s -> %s", sname, sval);
	}
	
	private String formatVar(final int index, final VncVal value) {
		final String sval = truncate(value.toString(true), 100, "...");
		return String.format("[%d] -> %s", index, sval);
	}

	private String formatReturnVal(final VncVal retval) {
		final String sval = truncate(retval.toString(true), 100, "...");
		return String.format("[return] -> %s", sval);
	}

	private String formatBreak(final Break br) {
		return String.format(
				"Break in %s %s at %s.",
				br.isSpecialForm()
					? "special form"
					: "function",
				br.getFn().getQualifiedName(),
				br.getBreakpointScope().description());
	}
	
	private String formatStop(final Break br) {
		return String.format(
				"Stopped in %s %s%s at %s.",
				br.isSpecialForm()
					? "special form"
					: "function",
				br.getFn().getQualifiedName(),
				br.getFn().isNative() 
					? "" 
					: " (" + new CallFrame(br.getFn()).getSourcePosInfo() +")",
				br.getBreakpointScope().description());
	}
	
	private void println(final String s) {
		printer.println("debug", s);
	}
	
	private void printlnErr(final String s) {
		printer.println("error", s);
	}
	
	
	private final static String HELP =
		  //+------------------------------------------------------------------------------+
			"Venice debugger\n\n" +
			"Commands: \n" +
			"  !attach      Attach the debugger to the REPL\n" +
			"  !detach      Detach the debugger from the REPL\n" +
			"  !terminate   Terminate a running debug session\n" +
			"               Sends an interrupt to the script under debugging.\n" +
			"  !breakpoint  Manage breakpoints\n" +
			"               o Add one or multiple breakpoints\n" +
			"                  !breakpoint add n, n*\n" +
			"                  E.g.: !breakpoint add user/gauss\n" +
			"                        !breakpoint add user/gauss +\n" +
			"               o Remove one or multiple breakpoints\n" +
			"                  !breakpoint remove n, n*\n" +
			"                  E.g.: !breakpoint remove user/gauss + \n" +
			"               o List breakpoints\n" +
			"                  !breakpoint list\n" +
			"                  E.g.: !breakpoint list\n" +
			"               Short form: !b ...\n" +
			"  !resume      Resume from breakpoint\n" +
			"               Short form: !r\n" +
			"  !step-into   Step into next function\n" +
			"               Short form: !si\n" +
			"  !step-return Step to the return of the function\n" +
			"               Short form: !sr\n" +
			"  !break?      Prints info on whether the debugger is in a break or not\n" +
			"               Short form: !b?\n" +
			"  !params      Print the functions parameters\n" +
			"               Short form: !p\n" +
			"  !locals x    Print the local vars from the level x. The level is optional\n" +
			"               and defaults to the top level.\n" +
			"               Short form: !l\n" +
			"  !local v     Print a local var with the name v\n" +
			"  !global v    Print a global var with the name v\n" +
			"  !callstack   Print the current callstack\n" +
			"               Short form: !cs\n" +
			"  !retval      Print the functions return value\n" +
			"               Short form: !ret\n" +
			"  !ex          Print the function's exception\n";

   
	private final TerminalPrinter printer;
	private final IDebugAgent agent;
	private final Thread replThread;
}