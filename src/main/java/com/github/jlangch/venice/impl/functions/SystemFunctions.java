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
package com.github.jlangch.venice.impl.functions;

import static com.github.jlangch.venice.impl.types.Constants.Nil;
import static com.github.jlangch.venice.impl.types.VncBoolean.False;
import static com.github.jlangch.venice.impl.util.reflect.ReflectionAccessor.classForName;
import static com.github.jlangch.venice.impl.util.reflect.ReflectionAccessor.invokeInstanceMethod;
import static com.github.jlangch.venice.impl.util.reflect.ReflectionAccessor.invokeStaticMethod;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.github.jlangch.venice.Version;
import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.javainterop.DynamicClassLoader2;
import com.github.jlangch.venice.impl.javainterop.JavaInterop;
import com.github.jlangch.venice.impl.types.Constants;
import com.github.jlangch.venice.impl.types.VncBoolean;
import com.github.jlangch.venice.impl.types.VncFunction;
import com.github.jlangch.venice.impl.types.VncKeyword;
import com.github.jlangch.venice.impl.types.VncLong;
import com.github.jlangch.venice.impl.types.VncString;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncHashMap;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.collections.VncOrderedMap;
import com.github.jlangch.venice.impl.types.collections.VncVector;
import com.github.jlangch.venice.impl.types.concurrent.ThreadLocalMap;
import com.github.jlangch.venice.impl.types.util.Coerce;
import com.github.jlangch.venice.impl.types.util.Types;
import com.github.jlangch.venice.impl.util.ArityExceptions;
import com.github.jlangch.venice.impl.util.CallStack;
import com.github.jlangch.venice.javainterop.IInterceptor;
import com.github.jlangch.venice.javainterop.ReturnValue;


public class SystemFunctions {

	///////////////////////////////////////////////////////////////////////////
	// System
	///////////////////////////////////////////////////////////////////////////

	public static VncFunction version =
		new VncFunction(
				"version",
				VncFunction
					.meta()
					.arglists("(version)")
					.doc("Returns the version.")
					.examples("(version )")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				return new VncString(Version.VERSION);
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};


	public static VncFunction uuid =
		new VncFunction(
				"uuid",
				VncFunction
					.meta()
					.arglists("(uuid)")
					.doc("Generates a UUID.")
					.examples("(uuid )")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);
				return new VncString(UUID.randomUUID().toString());
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};


	public static VncFunction objid =
		new VncFunction(
				"objid",
				VncFunction
					.meta()
					.arglists("(objid)")
					.doc("Returns the original unique hash code for the given object.")
					.examples("(objid x)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 1);
				return new VncLong(System.identityHashCode(args.first()));
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};


	public static VncFunction current_time_millis =
		new VncFunction(
				"current-time-millis",
				VncFunction
					.meta()
					.arglists("(current-time-millis)")
					.doc("Returns the current time in milliseconds.")
					.examples("(current-time-millis)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				return new VncLong(System.currentTimeMillis());
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction nano_time =
		new VncFunction(
				"nano-time",
				VncFunction
					.meta()
					.arglists("(nano-time)")
					.doc(
						"Returns the current value of the running Java Virtual Machine's " +
						"high-resolution time source, in nanoseconds.")
					.examples("(nano-time)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				return new VncLong(System.nanoTime());
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction format_nano_time =
		new VncFunction(
				"format-nano-time",
				VncFunction
					.meta()
					.arglists(
						"(format-nano-time time)",
						"(format-nano-time time & options)")
					.doc(
						"Formats a time given in nanoseconds as long or double. \n\n" +
						"Options: \n" +
						"  :precision p - e.g :precision 4 (defaults to 3)")
					.examples(
						"(format-nano-time 203)",
						"(format-nano-time 20389.0 :precision 2)",
						"(format-nano-time 20389 :precision 2)",
						"(format-nano-time 20389 :precision 0)",
						"(format-nano-time 203867669)",
						"(format-nano-time 20386766988 :precision 2)",
						"(format-nano-time 20386766988 :precision 6)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertMinArity(this, args, 1);

				final VncVal val = args.first();

				if (Types.isVncLong(val) || Types.isVncInteger(val)) {
					final long time = Numeric.toLong(val).getValue();

					if (time < 1_000) {
						return new VncString(String.format("%d ns", time));
					}
				}

				final VncHashMap options = VncHashMap.ofAll(args.rest());
				final int precision = Coerce.toVncLong(options.get(new VncKeyword("precision"), new VncLong(3)))
											.getIntValue();

				final double time = Numeric.toDouble(val).getValue();

				String unit = "s";
				double scale = 1_000_000_000.0D;

				if (time < 1_000.0D) {
					unit = "ns";
					scale = 1.0D;
				}
				else if (time < 1_000_000.0D) {
					unit = "µs";
					scale = 1_000.0D;
				}
				else if (time < 1_000_000_000.0D) {
					unit = "ms";
					scale = 1_000_000.0D;
				}

				return new VncString(String.format("%." + precision + "f " + unit, time / scale));
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction sleep =
		new VncFunction(
				"sleep",
				VncFunction
					.meta()
					.arglists(
						"(sleep n)",
						"(sleep n time-unit)")
					.doc(
						"Sleep for the time n. The default time unit is milliseconds \n" +
						"Time unit is one of :milliseconds, :seconds, :minutes, :hours, or :days. ")
					.examples(
						"(sleep 30)",
						"(sleep 30 :milliseconds)",
						"(sleep 5 :seconds)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 1, 2);

				try {
					final long sleep = Coerce.toVncLong(args.first()).getValue();
					final TimeUnit unit = args.size() == 1
											? TimeUnit.MILLISECONDS
											: toTimeUnit(Coerce.toVncKeyword(args.second()));

					Thread.sleep(unit.toMillis(Math.max(0,sleep)));
				}
				catch(InterruptedException ex) {
					throw new com.github.jlangch.venice.InterruptedException("interrupted while calling (sleep n)", ex);
				}

				return Nil;
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction pid =
		new VncFunction(
				"pid",
				VncFunction
					.meta()
					.arglists("(pid)")
					.doc("Returns the PID of this process.")
					.examples("(pid)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				if (javaMajorVersion() <= 8) {
					return new VncString(
								ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
				}
				else {
					// Java 9+  -> ProcessHandle.current().pid()					
					final ReturnValue procHandle = invokeStaticMethod(
														classForName("java.lang.ProcessHandle"),
														"current",
														new Object[]{});
					
					final ReturnValue ret = invokeInstanceMethod(
												procHandle.getValue(), 
												procHandle.getFormalType(),
												"pid", 
												new Object[]{});
					
					return new VncLong((Long)ret.getValue());
				}
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction host_name =
		new VncFunction(
				"host-name",
				VncFunction
					.meta()
					.arglists("(host-name)")
					.doc("Returns this host's name.")
					.examples("(host-name)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				try {
					return new VncString(
							InetAddress.getLocalHost().getHostName());
				}
				catch(Exception ex) {
					throw new VncException("(host-name) failed", ex);
				}
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction host_address =
		new VncFunction(
				"host-address",
				VncFunction
					.meta()
					.arglists("(host-address)")
					.doc("Returns this host's ip address.")
					.examples("(host-address)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				try {
					return new VncString(
							InetAddress.getLocalHost().getHostAddress());
				}
				catch(Exception ex) {
					throw new VncException("(host-address) failed", ex);
				}
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction gc =
		new VncFunction(
				"gc",
				VncFunction
					.meta()
					.arglists("(gc)")
					.doc(
						"Run the Java garbage collector. Runs the finalization methods " +
						"of any objects pending finalization prior to the GC.")
					.examples("(gc)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				sandboxFunctionCallValidation();
				
				Runtime.getRuntime().runFinalization();
				Runtime.getRuntime().gc();

				return Nil;
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction cpus =
		new VncFunction(
				"cpus",
				VncFunction
					.meta()
					.arglists("(cpus)")
					.doc(
						"Returns the number of available processors or number of " +
						"hyperthreads if the CPU supportd hyperthreads.")
					.examples("(cpus)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				return new VncLong(Runtime.getRuntime().availableProcessors());
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction shutdown_hook =
		new VncFunction(
				"shutdown-hook",
				VncFunction
					.meta()
					.arglists("(shutdown-hook f)")
					.doc("Registers the function f as JVM shutdown hook.")
					.examples("(shutdown-hook (fn [] (println \"shutdown\")))")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 1);

				sandboxFunctionCallValidation();

				final VncFunction fn = Coerce.toVncFunction(args.first());

				final IInterceptor parentInterceptor = JavaInterop.getInterceptor();

				// thread local values from the parent thread
				final AtomicReference<Map<VncKeyword,VncVal>> parentThreadLocals =
						new AtomicReference<>(ThreadLocalMap.getValues());

				Runtime.getRuntime().addShutdownHook(new Thread() {
				    public void run() {
						try {
							// inherit thread local values to the child thread
							ThreadLocalMap.setValues(parentThreadLocals.get());
							ThreadLocalMap.clearCallStack();
							JavaInterop.register(parentInterceptor);

							fn.apply(VncList.empty());
						}
						finally {
							// clean up
							JavaInterop.unregister();
							ThreadLocalMap.remove();
						}
				    }
				});

				return Nil;
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};


	public static VncFunction callstack =
		new VncFunction(
				"callstack",
				VncFunction
					.meta()
					.arglists("(callstack )")
					.doc("Returns the current callstack.")
					.examples(
						"(do                             \n" +
						"   (defn f1 [x] (f2 x))         \n" +
						"   (defn f2 [x] (f3 x))         \n" +
						"   (defn f3 [x] (f4 x))         \n" +
						"   (defn f4 [x] (callstack))    \n" +
						"   (f1 100))                      ")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				final CallStack stack = ThreadLocalMap.getCallStack();

				return VncVector.ofList(
						stack
							.callstack()
							.stream()
							.map(f -> VncOrderedMap.of(
											CALLSTACK_KEY_FN_NAME, f.getFnName() == null
																		? Constants.Nil
																		: new VncString(f.getFnName()),
											CALLSTACK_KEY_FILE, new VncString(f.getFile()),
											CALLSTACK_KEY_LINE, new VncLong(f.getLine()),
											CALLSTACK_KEY_COL, new VncLong(f.getCol())))
							.collect(Collectors.toList()));
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};
		
		
	public static VncFunction charset_default_encoding =
		new VncFunction(
				"charset-default-encoding",
				VncFunction
					.meta()
					.arglists("(charset-default-encoding)")
					.doc("Returns the default charset of this Java virtual machine.")
					.examples("(charset-default-encoding)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				return new VncKeyword(Charset.defaultCharset().name());
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction os_type =
		new VncFunction(
				"os-type",
				VncFunction
					.meta()
					.arglists("(os-type)")
					.doc("Returns the OS type")
					.examples("(os-type)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				final String osName = System.getProperty("os.name");
				if (osName.startsWith("Windows")) {
					return new VncKeyword("windows");
				}
				else if (osName.startsWith("Mac OS X")) {
					return new VncKeyword("mac-osx");
				}
				else if (osName.startsWith("LINUX")) {
					return new VncKeyword("linux");
				}
				else {
					return new VncKeyword("unknown");
				}
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction os_type_Q =
		new VncFunction(
				"os-type?",
				VncFunction
					.meta()
					.arglists("(os-type? type)")
					.doc(
						"Returns true if the OS id of the type otherwise false. Type is one " +
						"of :windows, :mac-osx, or :linux")
					.examples("(os-type? :mac-osx)", "(os-type? :windows)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 1);

				final String type = Coerce.toVncKeyword(args.first()).getValue();
				final String osName = System.getProperty("os.name");
				switch(type) {
					case "windows": return VncBoolean.of(osName.startsWith("Windows"));
					case "mac-osx": return VncBoolean.of(osName.startsWith("Mac OS X"));
					case "linux":   return VncBoolean.of(osName.startsWith("LINUX"));
					default:        return False;
				}
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction os_arch =
		new VncFunction(
				"os-arch",
				VncFunction
					.meta()
					.arglists("(os-arch)")
					.doc("Returns the OS architecture")
					.examples("(os-arch)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				return new VncString(System.getProperty("os.arch"));
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction os_name =
		new VncFunction(
				"os-name",
				VncFunction
					.meta()
					.arglists("(os-name)")
					.doc("Returns the OS name")
					.examples("(os-name)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				return new VncString(System.getProperty("os.name"));
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction os_version =
			new VncFunction(
					"os-version",
					VncFunction
						.meta()
						.arglists("(os-version)")
						.doc("Returns the OS version")
						.examples("(os-version)")
						.build()
			) {
				public VncVal apply(final VncList args) {
					ArityExceptions.assertArity(this, args, 0);

					return new VncString(System.getProperty("os.version"));
				}

				private static final long serialVersionUID = -1848883965231344442L;
			};

	public static VncFunction sandboxed_Q =
		new VncFunction(
				"sandboxed?",
				VncFunction
					.meta()
					.arglists("(sandboxed? )")
					.doc("Returns true if there is a sandbox otherwise false")
					.examples("(sandboxed? )")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				return VncBoolean.of(JavaInterop.isSandboxed());
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction sandbox_type =
		new VncFunction(
				"sandbox-type",
				VncFunction
					.meta()
					.arglists("(sandbox-type )")
					.doc("Returns the sandbox type")
					.examples("(sandbox-type )")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				final IInterceptor interceptor = JavaInterop.getInterceptor();
				 
				return interceptor == null
						? Constants.Nil
						: new VncKeyword(interceptor.getClass().getSimpleName());
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction system_prop =
		new VncFunction(
				"system-prop",
				VncFunction
					.meta()
					.arglists("(system-prop name default-val)")
					.doc(
						"Returns the system property with the given name. Returns " +
						"the default-val if the property does not exist or it's value is nil")
					.examples(
						"(system-prop :os.name)",
						"(system-prop :foo.org \"abc\")",
						"(system-prop \"os.name\")")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 1, 2);

				final VncString key = Coerce.toVncString(
										CoreFunctions.name.apply(
											VncList.of(args.first())));
				final VncVal defaultVal = args.size() == 2 ? args.second() : Nil;

				final String val = JavaInterop.getInterceptor().onReadSystemProperty(key.getValue());

				return val == null ? defaultVal : new VncString(val);
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction system_env =
		new VncFunction(
				"system-env",
				VncFunction
					.meta()
					.arglists("(system-env name default-val)")
					.doc(
						"Returns the system env variable with the given name. Returns " +
						"the default-val if the variable does not exist or it's value is nil")
					.examples(
						"(system-env :SHELL)",
						"(system-env :FOO \"test\")")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 1, 2);

				final VncString key = Coerce.toVncString(
										CoreFunctions.name.apply(
											VncList.of(args.first())));
				final VncVal defaultVal = args.size() == 2 ? args.second() : Nil;

				final String val = JavaInterop.getInterceptor().onReadSystemEnv(key.getValue());

				return val == null ? defaultVal : new VncString(val);
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction system_exit_code =
		new VncFunction(
				"system-exit-code",
				VncFunction
					.meta()
					.arglists("(system-exit-code code)")
					.doc(
						"Defines the exit code that is used if the Java VM exits. " +
						"Defaults to 0. \n\n" +
						"Note: \n" +
						"The exit code is only used when the Venice launcher has been" +
						"used to run a script file, a command line script, a Venice app " +
						"archive, or the REPL.")
					.examples(
						"(system-exit-code 0)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 1);

				final VncLong code = Coerce.toVncLong(args.first());
				
				SYSTEM_EXIT_CODE.set(code.getIntValue());
				
				return Constants.Nil;
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction java_version =
		new VncFunction(
				"java-version",
				VncFunction
					.meta()
					.arglists("(java-version)")
					.doc("Returns the Java VM version (1.8.0_252, 11.0.7, ...")
					.examples("(java-version)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				final String version = System.getProperty("java.version");
				
				return new VncString(version);
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction java_major_version =
		new VncFunction(
				"java-major-version",
				VncFunction
					.meta()
					.arglists("(java-major-version)")
					.doc("Returns the Java major version (8, 9, 11, ...).")
					.examples("(java-major-version)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				return new VncLong(javaMajorVersion());
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};
		
	public static VncFunction java_version_info =
		new VncFunction(
				"java-version-info",
				VncFunction
					.meta()
					.arglists("(java-version-info)")
					.doc("Returns the Java VM version info.")
					.examples("(java-version-info)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

				return VncOrderedMap.of(
						new VncKeyword("version"),
						new VncString(System.getProperty("java.version")),
						new VncKeyword("vendor"),
						new VncString(System.getProperty("java.vendor")),
						new VncKeyword("vm-version"),
						new VncString(System.getProperty("java.vm.version")),
						new VncKeyword("vm-name"),
						new VncString(System.getProperty("java.vm.name")),
						new VncKeyword("vm-vendor"),
						new VncString(System.getProperty("java.vm.vendor")));
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};
		
		
	public static VncFunction java_source_location =
		new VncFunction(
				"java-source-location",
				VncFunction
					.meta()
					.arglists("(java-source-location class)")
					.doc("Returns the path of the source location of a class (fully qualified class name).")
					.examples("(java-source-location :com.github.jlangch.venice.Venice)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 1);

				try {
					final String className = Coerce.toVncString(
												CoreFunctions.name.apply(VncList.of(args.first()))
											).getValue();
					
					final Class<?> clazz = Class.forName(className);
					
					final URI uri = clazz.getProtectionDomain()
										 .getCodeSource()
										 .getLocation()
										 .toURI();
					
					return new VncString(new File(uri).getPath());
				}
				catch(Exception ex) {
					return Nil;
				}
			}

			private static final long serialVersionUID = -1848883965231344442L;
		};
		

	public static VncFunction used_memory =
		new VncFunction(
				"used-memory",
				VncFunction
					.meta()
					.arglists("(used-memory)")
					.doc("Returns the memory used by the Java VM memory.")
					.examples("(used-memory)")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 0);

			    System.gc();		    
			    final Runtime rt = Runtime.getRuntime();
			    double usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024.0D / 1024.0D;
			    
			    return new VncString(String.format("%.1fMB", usedMB));
			}
			
			private static final long serialVersionUID = -1848883965231344442L;
		};
		

	public static VncFunction load_jar =
		new VncFunction(
				"load-jar",
				VncFunction
					.meta()
					.arglists("(load-jar url)")
					.doc(
						"Dynamically load a JAR into the classpath. \n\n" +
						"Dynamically loading JARs is experimental and for security reasons " +
						"available in the REPL only!")
					.examples(
						"(do                                                              \n" +
						"  (load-module :maven)                                           \n" +
						"  (let [uri (maven/uri \"org.knowm.xchart:xchart:3.6.5\" :jar)]  \n" +
						"    (load-jar uri)))                                               ")
					.build()
		) {
			public VncVal apply(final VncList args) {
				ArityExceptions.assertArity(this, args, 1);

				final VncString url = Coerce.toVncString(args.first());
				final ClassLoader cl = Thread.currentThread().getContextClassLoader();
				if (cl != null && cl instanceof DynamicClassLoader2) {
					try {
						((DynamicClassLoader2)cl).addURL(new URL(url.getValue()));
						return Nil;
					}
					catch (MalformedURLException ex) {
						throw new VncException(
								String.format("Malformed URL '%s'", url.getValue()),
								ex);
					}
				}
				else {
					throw new VncException(
							"There is no thread context ClassLoader available to dynamically " +
							"load a JAR. For security reasons dynamically loading JARs is only " +
							"available in the REPL!");
				}
			}
			
			private static final long serialVersionUID = -1848883965231344442L;
		};
	

		
	private static TimeUnit toTimeUnit(final VncKeyword unit) {
		switch(unit.getValue()) {
			case "milliseconds": return TimeUnit.MILLISECONDS;
			case "seconds": return TimeUnit.SECONDS;
			case "minutes":  return TimeUnit.MINUTES;
			case "hours": return TimeUnit.HOURS;
			case "days": return TimeUnit.DAYS;
			default: throw new VncException(
						"Invalid scheduler time-unit " + unit.getValue() + ". " 
							+ "Use one of {:milliseconds, :seconds, :minutes, :hours, :days}");
		}
	}

	private static long javaMajorVersion() {
		String version = System.getProperty("java.version");
		
		if (version.startsWith("1.")) {
			version = version.substring(2);
		}
		
		return Long.parseLong(version.substring(0, version.indexOf(".")));
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////
	// types_ns is namespace of type functions
	///////////////////////////////////////////////////////////////////////////

	public static Map<VncVal, VncVal> ns =
			new VncHashMap
					.Builder()
					.add(uuid)
					.add(objid)
					.add(current_time_millis)
					.add(nano_time)
					.add(format_nano_time)
					.add(pid)
					.add(host_name)
					.add(host_address)
					.add(gc)
					.add(cpus)
					.add(shutdown_hook)
					.add(sandboxed_Q)
					.add(sandbox_type)
					.add(sleep)
					.add(callstack)
					.add(os_type)
					.add(os_type_Q)
					.add(os_arch)
					.add(os_name)
					.add(os_version)
					.add(version)
					.add(system_prop)
					.add(system_env)
					.add(system_exit_code)
					.add(java_version)
					.add(java_version_info)
					.add(java_source_location)
					.add(java_major_version)
					.add(used_memory)
					.add(charset_default_encoding)
					.add(load_jar)
					.toMap();



	public static final VncKeyword CALLSTACK_KEY_FN_NAME = new VncKeyword(":fn-name");
	public static final VncKeyword CALLSTACK_KEY_FILE = new VncKeyword(":file");
	public static final VncKeyword CALLSTACK_KEY_LINE = new VncKeyword(":line");
	public static final VncKeyword CALLSTACK_KEY_COL = new VncKeyword(":col");
	
	public static final AtomicInteger SYSTEM_EXIT_CODE = new AtomicInteger(0);
}
