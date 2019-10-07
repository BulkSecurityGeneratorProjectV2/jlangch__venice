/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2019 Venice
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
package com.github.jlangch.venice.impl.javainterop;

import static com.github.jlangch.venice.impl.functions.FunctionsUtil.assertArity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.jlangch.venice.ArityException;
import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.Namespaces;
import com.github.jlangch.venice.impl.types.Constants;
import com.github.jlangch.venice.impl.types.VncFunction;
import com.github.jlangch.venice.impl.types.VncJavaObject;
import com.github.jlangch.venice.impl.types.VncKeyword;
import com.github.jlangch.venice.impl.types.VncTunnelAsJavaObject;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncHashMap;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.util.Coerce;
import com.github.jlangch.venice.impl.types.util.Types;
import com.github.jlangch.venice.impl.util.CallFrame;
import com.github.jlangch.venice.impl.util.StreamUtil;
import com.github.jlangch.venice.impl.util.reflect.ReflectionUtil;


public class JavaInteropFunctions {

	private static abstract class AbstractJavaFn extends VncFunction {
		public AbstractJavaFn(final String name, final VncVal meta) {
			super(name, meta);
		}

		@Override
		public boolean isRedefinable() { 
			return false;  // don't allow redefinition for security reasons
		}
		
	    private static final long serialVersionUID = -1848883965231344442L;
	}

	
	public static class JavaFn extends AbstractJavaFn {
		public JavaFn() {
			super(
				".", 
				VncFunction
					.meta()
					.arglists(
						"(. classname :new args)", 
						"(. classname method-name args)",
						"(. classname field-name)",
						"(. classname :class)",
						"(. object method-name args)", 
						"(. object field-name)",
						"(. object :class)")		
					.doc(
						"Java interop. Calls a constructor or an class/object method or accesses a " +
						"class/instance field. The function is sandboxed.")
					.examples(
						";; invoke constructor \n(. :java.lang.Long :new 10)", 
						";; invoke static method \n(. :java.time.ZonedDateTime :now)",
						";; invoke static method \n(. :java.lang.Math :min 10 20)", 
						";; access static field \n(. :java.lang.Math :PI)",
						";; invoke method \n(. (. :java.lang.Long :new 10) :toString)", 
						";; get class name \n(. :java.lang.Math :class)", 
						";; get class name \n(. (. :java.io.File :new \"/temp\") :class)")
					.build());
		}
	
		@Override
		public VncVal apply(final VncList args) {
			return JavaInteropUtil.applyJavaAccess(
					args, 
					Namespaces.getCurrentNamespace().getJavaImports());
		}
		
	    private static final long serialVersionUID = -1848883965231344442L;
	}
	
	public static class ProxifyFn extends AbstractJavaFn {
		public ProxifyFn() {
			super(
				"proxify", 
				VncFunction
					.meta()
					.arglists("(proxify classname method-map)")		
					.doc(
						"Proxifies a Java interface to be passed as a Callback object to " +
						"Java functions. The interface's methods are implemented by Venice " +
						"functions.")
					.examples(
						"(do \n" +
						"   (import :java.io.File :java.io.FilenameFilter) \n" +
						"\n" +
						"   (def file-filter \n" +
						"        (fn [dir name] (str/ends-with? name \".xxx\"))) \n" +
						"\n" +
						"   (let [dir (io/tmp-dir )] \n" +
						"        ;; create a dynamic proxy for the interface FilenameFilter\n" +
						"        ;; and implement its function 'accept' by 'file-filter'\n" +
						"        (. dir :list (proxify :FilenameFilter {:accept file-filter}))) \n" +
						")")
					.build());
		}

		@Override
		public VncVal apply(final VncList args) {
			if (args.size() != 2) {
				throw new ArityException(2, "proxify");
			}

			final Class<?> clazz = JavaInteropUtil.toClass(
										args.first(), 
										Namespaces.getCurrentNamespace().getJavaImports());

			return new VncJavaObject(
						DynamicInvocationHandler.proxify(
								CallFrame.fromVal("proxify(:" + clazz.getName() +")", args),
								clazz, 
								Coerce.toVncMap(args.second())));
		}

	    private static final long serialVersionUID = -1848883965231344442L;
	}

	public static class JavaClassFn extends AbstractJavaFn {
		public JavaClassFn() {
			super(
				"class", 
				VncFunction
					.meta()
					.arglists("(class name)")
					.doc("Returns the Java class for the given name.")
					.examples("(class :java.util.ArrayList)")
					.build());
		}
	
		@Override
		public VncVal apply(final VncList args) {
			assertArity("class", args, 1);
					
			return new VncJavaObject(
						JavaInteropUtil.toClass(
							args.first(), 
							Namespaces.getCurrentNamespace().getJavaImports()));
		}

	    private static final long serialVersionUID = -1848883965231344442L;
	}

	public static class SupersFn extends AbstractJavaFn {
		public SupersFn() {
			super(
				"supers", 
				VncFunction
					.meta()
					.arglists("(supers class)")
					.doc("Returns the immediate and indirect superclasses and interfaces of class, if any.")
					.examples("(supers :java.util.ArrayList)")
					.build());
		}
	
		@Override
		public VncVal apply(final VncList args) {
			assertArity("supers", args, 1);
					
			final Class<?> clazz = JavaInteropUtil.toClass(
										args.first(), 
										Namespaces.getCurrentNamespace().getJavaImports());

			final List<Class<?>> classes = new ArrayList<>();

			final List<Class<?>> superclasses = ReflectionUtil.getAllSuperclasses(clazz);
			final List<Class<?>> interfaces = ReflectionUtil.getAllInterfaces(superclasses);
			
			classes.addAll(superclasses);
			classes.addAll(interfaces);
	
			return new VncList(JavaInteropUtil.toVncKeywords(ReflectionUtil.distinct(classes)));
		}

	    private static final long serialVersionUID = -1848883965231344442L;
	}

	public static class BasesFn extends AbstractJavaFn {
		public BasesFn() {
			super(
				"bases", 
				VncFunction
					.meta()
					.arglists("(bases class)")
					.doc("Returns the immediate superclass and interfaces of class, if any.")
					.examples("(bases :java.util.ArrayList)")
					.build());
		}
	
		@Override
		public VncVal apply(final VncList args) {
			assertArity("bases", args, 1);
			
			final Class<?> clazz = JavaInteropUtil.toClass(
										args.first(), 
										Namespaces.getCurrentNamespace().getJavaImports());
						
			final List<Class<?>> classes = new ArrayList<>();
			final Class<?> superclass = ReflectionUtil.getSuperclass(clazz);
			if (superclass != null) {
				classes.add(superclass);
			}
			classes.addAll(ReflectionUtil.getAllDirectInterfaces(clazz));
	
			return new VncList(JavaInteropUtil.toVncKeywords(ReflectionUtil.distinct(classes)));
		}
	
	    private static final long serialVersionUID = -1848883965231344442L;
	}

	public static class DescribeJavaClassFn extends AbstractJavaFn {
		public DescribeJavaClassFn() {
			super(
				"describe-class", 
				VncFunction
					.meta()
					.arglists("(describe-class class)")
					.doc("Describes a Java class.")
					.examples("(describe :java.util.ArrayList)")
					.build());
		}
	
		@Override
		public VncVal apply(final VncList args) {
			assertArity("describe-class", args, 1);
				
			final Class<?> clazz = JavaInteropUtil.toClass(
										args.first(), 
										Namespaces.getCurrentNamespace().getJavaImports());
			
			VncHashMap map = new VncHashMap();
			
			map = map.assoc(
						new VncKeyword("constructors"),
						new VncList(
								ReflectionUtil
									.getPublicConstructors(clazz)
									.stream()
									.map(c -> mapConstructor(c))
									.collect(Collectors.toList())));

			map = map.assoc(
					new VncKeyword("methods"),
					new VncList()
							.addAllAtEnd(
								new VncList(
									ReflectionUtil
										.getAllPublicInstanceMethods(clazz, true)
										.stream()
										.filter(m -> !skippedFn.contains(m.getName()))
										.map(m -> mapMethod(m))
										.collect(Collectors.toList())))
							.addAllAtEnd(
									new VncList(
										ReflectionUtil
											.getAllPublicStaticMethods(clazz, true)
											.stream()
											.filter(m -> !skippedFn.contains(m.getName()))
											.map(m -> mapMethod(m))
											.collect(Collectors.toList()))));

			map = map.assoc(
					new VncKeyword("fields"),
					new VncList()
						.addAllAtEnd(
							new VncList(
								ReflectionUtil
									.getPublicInstanceFields(clazz)
									.stream()
									.map(m -> mapField(m))
									.collect(Collectors.toList())))
						.addAllAtEnd(
							new VncList(
								ReflectionUtil
									.getPublicStaticFields(clazz)
									.stream()
									.map(m -> mapField(m))
									.collect(Collectors.toList()))));
			
			return map;
		}

	    private static final long serialVersionUID = -1848883965231344442L;
	}

	public static class JavaObjQFn extends AbstractJavaFn {
		public JavaObjQFn() {
			super(
				"java-obj?", 
				VncFunction
					.meta()
					.arglists("(java-obj? obj)")		
					.doc("Returns true if obj is a Java object")
					.examples("(java-obj? (. :java.math.BigInteger :new \"0\"))")
					.build());
		}
		
		@Override
		public VncVal apply(final VncList args) {
			assertArity("java-obj?", args, 1);
			
			return Types.isVncJavaObject(args.first()) ? Constants.True : Constants.False;
		}
		
		private static final long serialVersionUID = -1848883965231344442L;
	}
			
	public static class JavaEnumToListFn extends AbstractJavaFn {
		public JavaEnumToListFn() {
			super(
				"java-enumeration-to-list", 
				VncFunction
					.meta()
					.arglists("(java-enumeration-to-list e)")		
					.doc("Converts a Java enumeration to a list")
					.build());
		}
		
		@Override
		public VncVal apply(final VncList args) {
			assertArity("java-enumeration-to-list", args, 1);
			
			if (Types.isVncJavaObject(args.first(), Enumeration.class)) {
				final Enumeration<?> e = (Enumeration<?>)Coerce.toVncJavaObject(args.first()).getDelegate();
				final List<VncVal> list = StreamUtil
											 .stream(e)
											 .map(v -> JavaInteropUtil.convertToVncVal(v))
											 .collect(Collectors.toList());
				
				return new VncList(list); 
			}
			else {
				throw new VncException(String.format(
						"Function 'java-enumeration-to-list' does not allow %s as parameter", 
						Types.getType(args.first())));
			}
		}
		
		private static final long serialVersionUID = -1848883965231344442L;
	};

	public static class JavaIterToListFn extends AbstractJavaFn {
		public JavaIterToListFn() {
			super(
				"java-iterator-to-list", 
				VncFunction
					.meta()
					.arglists("(java-iterator-to-list e)")		
					.doc("Converts a Java iterator to a list")
					.build());
		}
			
		@Override
		public VncVal apply(final VncList args) {
			assertArity("java-iterator-to-list", args, 1);
			
			if (Types.isVncJavaObject(args.first(), Iterator.class)) {
				final Iterator<?> i = (Iterator<?>)Coerce.toVncJavaObject(args.first()).getDelegate();
				final List<VncVal> list = StreamUtil
												.stream(i)
												.map(v -> JavaInteropUtil.convertToVncVal(v))
												.collect(Collectors.toList());
				return new VncList(list); 
			}
			else {
				throw new VncException(String.format(
						"Function 'java-iterator-to-list' does not allow %s as parameter", 
						Types.getType(args.first())));
			}
		}
		
		private static final long serialVersionUID = -1848883965231344442L;
	};

	public static class JavaObjWrapFn extends AbstractJavaFn {
		public JavaObjWrapFn() {
			super(
				"java-wrap", 
				VncFunction
					.meta()
					.arglists("(java-wrap val)")		
					.doc("Wraps a venice value")
					.build());
		}
		
		@Override
		public VncVal apply(final VncList args) {
			assertArity("java-wrap", args, 1);
			
			final VncVal arg = args.first();
			
			return arg instanceof VncTunnelAsJavaObject 
					? arg 
					: new VncTunnelAsJavaObject(arg);
		}
		
		private static final long serialVersionUID = -1848883965231344442L;
	}

	public static class JavaObjUnwrapFn extends AbstractJavaFn {
		public JavaObjUnwrapFn() {
			super(
				"java-unwrap", 
				VncFunction
					.meta()
					.arglists("(java-unwrap val)")		
					.doc("Unwraps a venice value")
					.build());
		}
		
		@Override
		public VncVal apply(final VncList args) {
			assertArity("java-unwrap", args, 1);
			
			final VncVal arg = args.first();
			
			return arg instanceof VncTunnelAsJavaObject
					? ((VncTunnelAsJavaObject)arg).getDelegate()
					: arg;
		}
	
		private static final long serialVersionUID = -1848883965231344442L;
	}
	
	private static VncHashMap mapField(final Field f) {
		return new VncHashMap()
				.assoc(new VncKeyword(":name"), new VncKeyword(f.getName()))
				.assoc(new VncKeyword(":type"), new VncKeyword(f.getType().getName()))
				.assoc(new VncKeyword(":static"), ReflectionUtil.isStatic(f) ? Constants.True : Constants.False);
	}
	
	private static VncHashMap mapMethod(final Method m) {
		final Parameter[] params = m.getParameters();
		final Type[] types = m.getGenericParameterTypes();
		final Type ret = m.getGenericReturnType();

		return new VncHashMap()
			.assoc(new VncKeyword(":name"), new VncKeyword(m.getName()))
			.assoc(new VncKeyword(":params"), mapParams(params, types))
			.assoc(new VncKeyword(":return"), new VncKeyword(ret.getTypeName()))
			.assoc(new VncKeyword(":static"), ReflectionUtil.isStatic(m) ? Constants.True : Constants.False);
	}
	
	private static VncHashMap mapConstructor(final Constructor<?> c) {
		final Parameter[] params = c.getParameters();
		final Type[] types = c.getGenericParameterTypes();

		return new VncHashMap()
				.assoc(new VncKeyword(":default"), params.length == 0 ? Constants.True : Constants.False)
				.assoc(new VncKeyword(":params"), mapParams(params, types));
	}
	
	private static VncHashMap mapParams(final Parameter[] params, final Type[] types) {
		VncHashMap map = new VncHashMap();
		for(int ii=0; ii<params.length; ii++) {
			map = map.assoc(
						new VncKeyword(params[ii].getName()),
					 	new VncKeyword(types[ii].getTypeName()));
		}
		return map;
	}

	private static Set<String> skippedFn = new HashSet<>(Arrays.asList(
													"clone",
													"equals",
													"hashCode",
													"notify",
													"notifyAll",
													"getClass",
													"toString",
													"wait"));
	
	///////////////////////////////////////////////////////////////////////////
	// types_ns is namespace of type functions
	///////////////////////////////////////////////////////////////////////////

	public static Map<VncVal, VncVal> ns = 
			new VncHashMap
					.Builder()
					.add(new JavaFn())
					.add(new ProxifyFn())
					.add(new SupersFn())
					.add(new BasesFn())
					.add(new DescribeJavaClassFn())
					.add(new JavaClassFn())
					.add(new JavaObjQFn())
					.add(new JavaEnumToListFn())
					.add(new JavaIterToListFn())
					.add(new JavaObjWrapFn())
					.add(new JavaObjUnwrapFn())
					.toMap();	
}
