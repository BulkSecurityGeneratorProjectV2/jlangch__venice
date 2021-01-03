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
package com.github.jlangch.venice;

import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import com.github.jlangch.venice.impl.RunMode;
import com.github.jlangch.venice.impl.VeniceInterpreter;
import com.github.jlangch.venice.impl.env.Env;
import com.github.jlangch.venice.impl.env.Var;
import com.github.jlangch.venice.impl.functions.JsonFunctions;
import com.github.jlangch.venice.impl.functions.SystemFunctions;
import com.github.jlangch.venice.impl.javainterop.JavaInterop;
import com.github.jlangch.venice.impl.repl.CustomREPL;
import com.github.jlangch.venice.impl.repl.REPL;
import com.github.jlangch.venice.impl.types.VncJavaObject;
import com.github.jlangch.venice.impl.types.VncString;
import com.github.jlangch.venice.impl.types.VncSymbol;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.collections.VncMap;
import com.github.jlangch.venice.impl.types.util.Coerce;
import com.github.jlangch.venice.impl.util.CommandLineArgs;
import com.github.jlangch.venice.impl.util.StringUtil;
import com.github.jlangch.venice.impl.util.io.ClassPathResource;
import com.github.jlangch.venice.impl.util.io.FileUtil;
import com.github.jlangch.venice.impl.util.io.zip.ZipFileSystemUtil;
import com.github.jlangch.venice.javainterop.AcceptAllInterceptor;
import com.github.jlangch.venice.javainterop.IInterceptor;
import com.github.jlangch.venice.javainterop.ILoadPaths;
import com.github.jlangch.venice.javainterop.LoadPathsFactory;


/**
 * The {@code Launcher} runs Venice scripts or apps and starts the REPL.
 * 
 * <p>The launcher is configured as the Venice JAR's main-class.
 *  
 * <p>Running scripts:
 * <pre>java -jar venice-1.9.7.jar -script "(+ 1 1)"</pre>
 *  
 * <p>Running a REPL:
 * <pre>
 *    java \
 *       -server \
 *       -Xmx6G \
 *       -XX:-OmitStackTraceInFastThrow \
 *       -cp "libs/*" \
 *       com.github.jlangch.venice.Launcher \
 *       -repl \
 *       -colors
 *  </pre>
 *  
 *  <p>Launcher command line options:
 *  <pre>
 *  -loadpath path    defines a load path
 *                    E.g.: -loadpath "/users/foo/scripts;/users/foo/res"
 *  
 *  -macroexpand      turns up-front macro expansion on, resulting in a
 *                    much better performance
 *                    
 *  -file script      loads the script to run from a file
 *                    E.g.:  -file ./test.venice
 *                    
 *  -cp-file res      loads the script to run from the classpath
 *                    E.g.:  -cp-file com/github/jlangch/venice/test.venice
 *                    
 *  -script script    run a script
 *                    E.g.:  -script "(+ 1 10)"
 *  
 *  -app app          run a Venice app 
 *                    E.g.:  -app test-app.zip
 *    
 *  -repl             start a REPL
 *  
 *  -help             prints a help
 *  </pre>
 *  
 *  <p>Note:
 *  The options '-file', '-cp-file', '-script', '-app', and '-repl' exclude each 
 *  other
 */
public class Launcher {
	
	public static void main(final String[] args) {
		final CommandLineArgs cli = new CommandLineArgs(args);
		
		final ILoadPaths loadPaths = LoadPathsFactory.parseDelimitedLoadPath(
											cli.switchValue("-loadpath"),
											true);
		
		final boolean macroexpand = cli.switchPresent("-macroexpand");

		try {
			if (cli.switchPresent("-help")) {
				printHelp();
			}
			else if (cli.switchPresent("-file")) {
				final IInterceptor interceptor = new AcceptAllInterceptor(loadPaths);
				JavaInterop.register(interceptor);
				
				// run the file from the filesystem
				final String file = suffixWithVeniceFileExt(cli.switchValue("-file"));
				final String script = new String(FileUtil.load(new File(file)));
				
				System.out.println(
						runScript(cli, macroexpand, interceptor, script, new File(file).getName()));
			}
			else if (cli.switchPresent("-cp-file")) {
				final IInterceptor interceptor = new AcceptAllInterceptor(loadPaths);
				JavaInterop.register(interceptor);
				
				// run the file from the classpath
				final String file = suffixWithVeniceFileExt(cli.switchValue("-cp-file"));
				final String script = new ClassPathResource(file).getResourceAsString();
				
				System.out.println(
						runScript(cli, macroexpand, interceptor, script, new File(file).getName()));
			}
			else if (cli.switchPresent("-script")) {
				final IInterceptor interceptor = new AcceptAllInterceptor(loadPaths);
				JavaInterop.register(interceptor);
				
				// run the script passed as command line argument
				final String script = cli.switchValue("-script");
				
				System.out.println(
						runScript(cli, macroexpand, interceptor, script, "script"));
			}
			else if (cli.switchPresent("-app")) {
				// run the Venice application archive
				final File appFile = new File(suffixWithZipFileExt(cli.switchValue("-app")));
				
				// Merge the load paths from the command line with the application archive
				final List<File> mergedLoadPaths = Arrays.asList(appFile.getAbsoluteFile());
				mergedLoadPaths.addAll(loadPaths.getPaths());
				final ILoadPaths appLoadPaths = LoadPathsFactory.of(
													mergedLoadPaths, 
													loadPaths.isUnlimitedAccess());

				final IInterceptor interceptor = new AcceptAllInterceptor(appLoadPaths);
				JavaInterop.register(interceptor);

				final VncMap manifest = getManifest(appFile);
				
				final String appName = Coerce.toVncString(manifest.get(new VncString("app-name"))).getValue();
				final String mainFile = Coerce.toVncString(manifest.get(new VncString("main-file"))).getValue();
						
				System.out.println(String.format("Launching Venice application '%s' ...", appName));

				final String appBootstrap = String.format("(do (load-file \"%s\") nil)", stripVeniceFileExt(mainFile));

				runApp(cli, macroexpand, interceptor, appBootstrap, appName, appFile);
			}
			else if (cli.switchPresent("-app-repl")) {
				final IInterceptor interceptor = new AcceptAllInterceptor(loadPaths);
				JavaInterop.register(interceptor);
				
				// run a custom application repl
				final String file = cli.switchValue("-app-repl");
					
				new CustomREPL(interceptor, new File(file)).run(args);
			}
			else if (cli.switchPresent("-repl")) {
				final IInterceptor interceptor = new AcceptAllInterceptor(loadPaths);
				JavaInterop.register(interceptor);
				
				// run the Venice repl
				new REPL(interceptor).run(args);
			}
			else {
				final IInterceptor interceptor = new AcceptAllInterceptor(loadPaths);
				JavaInterop.register(interceptor);
				
				// run the Venice repl
				new REPL(interceptor).run(args);
			}
			
			System.exit(SystemFunctions.SYSTEM_EXIT_CODE.get());
		}
		catch (VncException ex) {
			ex.printVeniceStackTrace();
			System.exit(99);
		}	
		catch (Exception ex) {
			ex.printStackTrace();
			System.exit(99);
		}	
	}

	private static void printHelp() {
		System.out.println(
			 "The Launcher runs Venice scripts or apps and starts the REPL. \n" +
			 "\n" +
			 "The launcher is configured as the Venice JAR's main-class. \n" +
			 "\n" +
			 "Running scripts: \n" +
			 "    java -jar venice-1.9.7.jar -script \"(+ 1 1)\" \n" +
			 "\n" +
			 "Running a REPL:  \n" +
			 "    java \\ \n" +
			 "       -server \\ \n" +
			 "       -Xmx6G \\ \n" +
			 "       -XX:-OmitStackTraceInFastThrow \\ \n" +
			 "       -cp \"libs/*\" \\ \n" +
			 "       com.github.jlangch.venice.Launcher \\ \n" +
			 "       -repl \\ \n" +
			 "       -colors \n" +
			 "\n\n" +
			 "Launcher command line options: \n" +
			 "  -loadpath path    defines a load path \n" +
			 "                    E.g.: -loadpath \"/users/foo/scripts;/users/foo/res\" \n" +
			 "\n" +
			 "  -macroexpand      turns up-front macro expansion on, resulting in a \n" +
			 "                    much better performance \n" +
			 "\n" +
			 "  -file script      loads the script to run from a file \n" +
			 "                    E.g.:  -file ./test.venice \n" +
			 "\n" +
			 "  -cp-file res      loads the script to run from the classpath \n" +
			 "                    E.g.:  -cp-file com/github/jlangch/venice/test.venice \n" +
			 "\n" +
			 "  -script script    run a script \n" +
			 "                    E.g.:  -script \"(+ 1 10)\" \n" +
			 "\n" +
			 "  -app app          run a Venice app  \n" +
			 "                    E.g.:  -app test-app.zip \n" +
			 "\n" +
			 "  -repl             start a REPL \n" +
			 "\n" +
			 "  -help             prints a help \n" +
			 "\n" +
			 "Note: \n" +
			 "  The options '-file', '-cp-file', '-script', '-app', and '-repl' exclude \n" +
			 "  each other \n");
	}

	private static String runApp(
			final CommandLineArgs cli,
			final boolean macroexpand,
			final IInterceptor interceptor,
			final String script,
			final String name,
			final File appArchive
	) {
		final VeniceInterpreter venice = new VeniceInterpreter(interceptor);
			
		final Env env = createEnv(
							venice,
							macroexpand,
							RunMode.APP,
							Arrays.asList(
								convertCliArgsToVar(cli),
								convertAppNameToVar(name),
								convertAppArchiveToVar(appArchive)));

		return venice.PRINT(venice.RE(script, name, env));
	}
	
	private static String runScript(
			final CommandLineArgs cli,
			final boolean macroexpand,
			final IInterceptor interceptor,
			final String script,
			final String name
	) {
		final VeniceInterpreter venice = new VeniceInterpreter(interceptor);
		
		final Env env = createEnv(
							venice, 
							macroexpand,
							RunMode.SCRIPT,
							Arrays.asList(
								convertCliArgsToVar(cli)));

		return venice.PRINT(venice.RE(script, name, env));
	}
	
	private static Env createEnv(
			final VeniceInterpreter venice,
			final boolean macroexpand, 
			final RunMode runMode,
			final List<Var> vars
	) {
		return venice.createEnv(macroexpand, false, runMode)
					 .addGlobalVars(vars)
					 .setStdoutPrintStream(new PrintStream(System.out, true))
					 .setStderrPrintStream(new PrintStream(System.err, true))
					 .setStdinReader(new InputStreamReader(System.in));
	}

	private static Var convertAppNameToVar(final String appName) {
		return new Var(new VncSymbol("*app-name*"), new VncString(appName), false);
	}

	private static Var convertAppArchiveToVar(final File appArchive) {
		return new Var(new VncSymbol("*app-archive*"), new VncJavaObject(appArchive), false);
	}

	private static Var convertCliArgsToVar(final CommandLineArgs cli) {
		return new Var(new VncSymbol("*ARGV*"), cli.argsAsList(), false);
	}

	private static String stripVeniceFileExt(final String s) {
		return StringUtil.removeEnd(s, ".venice");
	}

	private static String suffixWithVeniceFileExt(final String s) {
		return s == null ? null : (s.endsWith(".venice") ? s : s + ".venice");
	}

	private static String suffixWithZipFileExt(final String s) {
		return s == null ? null : (s.endsWith(".zip") ? s : s + ".zip");
	}

	private static VncMap getManifest(final File app) {
		if (app.exists()) {
			try {
				final VncVal manifest = ZipFileSystemUtil.loadTextFileFromZip(app, new File("MANIFEST.MF"), "utf-8");			
				return Coerce.toVncMap(JsonFunctions.read_str.apply(VncList.of(manifest)));
			}
			catch (Exception ex) {
				throw new VncException(String.format(
						"Failed to load manifest from Venice application archive '%s'.",
						app.getPath()));
			}
		}
		else {
			throw new VncException(String.format(
					"The Venice application archive '%s' does not exist",
					app.getPath()));
		}
	}
}
