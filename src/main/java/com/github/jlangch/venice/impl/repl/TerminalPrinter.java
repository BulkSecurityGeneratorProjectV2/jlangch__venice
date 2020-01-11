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
package com.github.jlangch.venice.impl.repl;

import java.util.function.Consumer;

import org.jline.terminal.Terminal;

import com.github.jlangch.venice.ValueException;
import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.Printer;


public class TerminalPrinter {
	public TerminalPrinter(
			final ReplConfig config,
			final Terminal terminal,
			final boolean printJavaEx
	) {
		this.config = config;
		this.terminal = terminal;
		this.printJavaEx = printJavaEx;
	}
	
	public void setPrintJavaEx(final boolean printJavaEx) {
		this.printJavaEx = printJavaEx;
	}
	
	public void print(
			final String colorID,
			final Consumer<Terminal> fn
	) {
		final String color = config.getColor("colors." + colorID);
		if (color != null) {
			terminal.writer().print(color);
		}
		
		fn.accept(terminal);
		
		if (color != null) {
			terminal.writer().print(ReplConfig.ANSI_RESET);
		}
		
		terminal.flush();
	}
	
	public void println() {
		terminal.writer().println();
		terminal.flush();
	}
	
	public void println(
			final String colorID,
			final String text
	) {
		print(colorID, t -> t.writer().print(text));
		terminal.writer().println();
		terminal.flush();
	}
	
	public void printex(
			final String colorID,
			final Throwable ex
	) {
		try {
			if (ex instanceof ValueException) {
				print(colorID, t -> ((ValueException)ex).printVeniceStackTrace(t.writer()));		
				println(colorID, "Thrown value: " + Printer.pr_str(((ValueException)ex).getValue(), false));			
			}
			else if (ex instanceof VncException) {
				if (printJavaEx) {
					print(colorID, t -> ex.printStackTrace(t.writer()));			
				}
				else {
					print(colorID, t -> ((VncException)ex).printVeniceStackTrace(t.writer()));		
				}
			}
			else {
				print(colorID, t -> ex.printStackTrace(t.writer()));			
			}
		}
		catch(Throwable e) {
			System.out.println("Internal REPL error while printing exception.");
			e.printStackTrace();
		}
	}
	
	private final Terminal terminal;
	private ReplConfig config;
	private boolean printJavaEx;
}
