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
package com.github.jlangch.venice.impl.functions;

import static com.github.jlangch.venice.impl.functions.FunctionsUtil.assertArity;
import static com.github.jlangch.venice.impl.types.Constants.Nil;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.javainterop.JavaInteropUtil;
import com.github.jlangch.venice.impl.types.VncFunction;
import com.github.jlangch.venice.impl.types.VncInteger;
import com.github.jlangch.venice.impl.types.VncJavaObject;
import com.github.jlangch.venice.impl.types.VncLong;
import com.github.jlangch.venice.impl.types.VncString;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncHashMap;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.util.Coerce;
import com.github.jlangch.venice.impl.types.util.Types;
import com.github.jlangch.venice.impl.util.reflect.ReflectionTypes;
import com.github.jlangch.venice.impl.util.reflect.ReflectionUtil;


public class ArrayFunctions {
	
	public static VncFunction aset = 
		new VncFunction(
				"aset", 
				VncFunction
					.meta()
					.arglists("(aset array idx val)")		
					.doc("Sets the value at the index of an array")
					.examples("(aset (long-array '(1 2 3 4 5)) 1 20)")
					.build()
		) {		
			public VncVal apply(final VncList args) {			
				assertArity("aset", args, 3);

				final VncJavaObject jo = Coerce.toVncJavaObject(args.first());
				final VncInteger idx = Numeric.toInteger(args.second());
				final VncVal val = args.third();

				final Object delegate = jo.getDelegate();
				final Class<?> delegateClass = delegate.getClass();
				
				if (!ReflectionTypes.isArrayType(delegateClass)) {
					throw new VncException(String.format(
							"The array argument (%s) is not an array",
							Types.getType(jo)));
				}

				final Class<?> componentType = delegateClass.getComponentType();

				if (componentType == String.class) {
					Array.set(delegate, idx.getValue(), Coerce.toVncString(val).getValue());
				}
				else if (componentType == int.class) {
					Array.setInt(delegate, idx.getValue(), Numeric.toInteger(val).getValue());
				}
				else if (componentType == long.class) {
					Array.setLong(delegate, idx.getValue(), Numeric.toLong(val).getValue());
				}
				else if (componentType == float.class) {
					Array.setFloat(delegate, idx.getValue(), Numeric.toDouble(val).getValue().floatValue());
				}
				else if (componentType == double.class) {
					Array.setDouble(delegate, idx.getValue(), Numeric.toDouble(val).getValue());
				}
				else {
					Array.set(delegate, idx.getValue(), val.convertToJavaObject());
				}
				
				return jo;
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction aget = 
			new VncFunction(
					"aget", 
					VncFunction
						.meta()
						.arglists("(aget array idx)")		
						.doc("Returns the value at the index of an array of Java Objects")
						.examples("(aget (long-array '(1 2 3 4 5)) 1)")
						.build()
			) {		
				public VncVal apply(final VncList args) {			
					assertArity("aget", args, 2);

					final VncJavaObject jo = Coerce.toVncJavaObject(args.first());
					final VncInteger idx = Numeric.toInteger(args.second());

					final Object delegate = jo.getDelegate();
					
					if (!ReflectionTypes.isArrayType(delegate.getClass())) {
						throw new VncException(String.format(
								"The array argument (%s) is not an array",
								Types.getType(jo)));
					}

					return JavaInteropUtil.convertToVncVal(Array.get(delegate, idx.getValue()));
				}
		
			    private static final long serialVersionUID = -1848883965231344442L;
			};
			
	public static VncFunction alength = 
			new VncFunction(
					"alength", 
					VncFunction
						.meta()
						.arglists("(alength array)")		
						.doc("Returns the length of an array")
						.examples("(alength (long-array '(1 2 3 4 5)))")
						.build()
			) {		
				public VncVal apply(final VncList args) {			
					assertArity("alength", args, 1);

					final VncJavaObject jo = Coerce.toVncJavaObject(args.first());
	
					final Object delegate = jo.getDelegate();
					
					if (!ReflectionTypes.isArrayType(delegate.getClass())) {
						throw new VncException(String.format(
								"The array argument (%s) is not an array",
								Types.getType(jo)));
					}

					return new VncLong(Array.getLength(delegate));
				}
		
			    private static final long serialVersionUID = -1848883965231344442L;
			};
			
	public static VncFunction asub = 
			new VncFunction(
					"asub", 
					VncFunction
						.meta()
						.arglists("(asub array start len)")		
						.doc("Returns a sub array")
						.examples("(asub (long-array '(1 2 3 4 5)) 2 3)")
						.build()
			) {		
				public VncVal apply(final VncList args) {			
					assertArity("asub", args, 3);
	
					final VncJavaObject jo = Coerce.toVncJavaObject(args.first());
					final int start = Coerce.toVncLong(args.second()).getIntValue();
					final int len = Coerce.toVncLong(args.third()).getIntValue();
	
					final Object delegate = jo.getDelegate();
					final Class<?> delegateClass = delegate.getClass();
					
					if (!ReflectionTypes.isArrayType(delegateClass)) {
						throw new VncException(String.format(
								"The array argument (%s) is not an array",
								Types.getType(jo)));
					}
	
					final Class<?> componentType = delegateClass.getComponentType();
	
					
					final Object arr = Array.newInstance(componentType, len);
					System.arraycopy(delegate, start, arr, 0, len);
					return new VncJavaObject(arr);
				}
		
			    private static final long serialVersionUID = -1848883965231344442L;
			};

	public static VncFunction acopy = 
		new VncFunction(
				"acopy", 
				VncFunction
					.meta()
					.arglists(
						"(acopy src src-pos dest dest-pos dest-len)")		
					.doc(
						"Copies an array from the src array, beginning at the" + 
						"specified position, to the specified position of the dst array. " +
						"Returns the modified desitination array")
					.examples(
						"(acopy (long-array '(1 2 3 4 5)) 2 (long-array 20) 10 3)")
					.build()
		) {		
			public VncVal apply(final VncList args) {			
				assertArity("acopy", args, 5);
	
				final VncJavaObject joSrc = Coerce.toVncJavaObject(args.nth(0));
				final int srcPos = Coerce.toVncLong(args.nth(1)).getIntValue();
				final VncJavaObject joDst = Coerce.toVncJavaObject(args.nth(2));
				final int dstPos = Coerce.toVncLong(args.nth(3)).getIntValue();
				final int dstLen = Coerce.toVncLong(args.nth(4)).getIntValue();
	
				final Object delegateSrc = joSrc.getDelegate();
				final Class<?> delegateSrcClass = delegateSrc.getClass();
				final Object delegateDst = joDst.getDelegate();
				final Class<?> delegateDstClass = delegateDst.getClass();
				
				if (!ReflectionTypes.isArrayType(delegateSrcClass)) {
					throw new VncException(String.format(
							"The source array argument (%s) is not an array",
							Types.getType(joSrc)));
				}
				if (!ReflectionTypes.isArrayType(delegateDstClass)) {
					throw new VncException(String.format(
							"The destination array argument (%s) is not an array",
							Types.getType(joDst)));
				}
	
				final Class<?> srcCcomponentType = delegateSrcClass.getComponentType();
				final Class<?> dstCcomponentType = delegateDstClass.getComponentType();

				if (srcCcomponentType != dstCcomponentType) {
					throw new VncException("Source and destination array are not from the sane type");
				}

				System.arraycopy(delegateSrc, srcPos, delegateDst, dstPos, dstLen);
				
				return args.nth(2);
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction make_array = 
		new VncFunction(
				"make-array", 
				VncFunction
					.meta()
					.arglists(
						"(make-array type len)")		
					.doc(
						"Returns an array of the given type and length")
					.examples(
						"(make-array :java.lang.Long 5)",
						"(aset (make-array :java.lang.Integer 5) 3 9999)")
					.build()
		) {		
			public VncVal apply(final VncList args) {			
				assertArity("make-array", args, 2);

				final String className = Coerce.toVncKeyword(args.first()).getValue();
				final int len = Numeric.toInteger(args.second()).getValue();

				if (className.startsWith("venice.")) {
					throw new VncException("make-array does not support Venice data types");
				}

				return new VncJavaObject(
								Array.newInstance(
										ReflectionUtil.classForName(className), 
										len));
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction object_array = 
		new VncFunction(
				"object-array", 
				VncFunction
					.meta()
					.arglists(
						"(object-array coll)", 
						"(object-array len)", 
						"(object-array len init-val)")		
					.doc(
						"Returns an array of Java Objects containing the contents of coll "
							+ "or returns an array with the given length")
					.examples(
						"(object-array '(1 2 3 4 5))",
						"(object-array '(1 2.0 3.45M \"4\" true))",
						"(object-array 10)",
						"(object-array 10 42)")
					.build()
		) {		
			public VncVal apply(final VncList args) {			
				assertArity("object-array", args, 1, 2);

				final VncVal arg = args.first();
				
				if (Types.isVncLong(arg)) {
					final Object[] arr = new Object[((VncLong)arg).getIntValue()];
					if (args.size() == 2) {
						Arrays.fill(arr, args.second().convertToJavaObject());
					}
					return new VncJavaObject(arr);
				}
				else {
					final List<VncVal> list = Coerce.toVncSequence(args.first()).getList();
					
					final Object[] arr = new Object[list.size()];
				
					int ii=0;
					for(VncVal v : list) {
						arr[ii++] = v.convertToJavaObject();
					}

					return new VncJavaObject(arr);
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction string_array = 
		new VncFunction(
				"string-array", 
				VncFunction
					.meta()
					.arglists(
						"(string-array coll)", 
						"(string-array len)", 
						"(string-array len init-val)")		
					.doc(
						"Returns an array of Java strings containing the contents of coll"
							+ "or returns an array with the given length")
					.examples(
						"(string-array '(\"1\" \"2\" \"3\"))",
						"(string-array 10)",
						"(string-array 10 \"42\")") 
					.build()
		) {		
			public VncVal apply(final VncList args) {			
				assertArity("string-array", args, 1, 2);

				final VncVal arg = args.first();
				
				if (Types.isVncLong(arg)) {
					final String[] arr = new String[((VncLong)arg).getIntValue()];
					if (args.size() == 2) {
						Arrays.fill(arr, Coerce.toVncString(args.second()).getValue());
					}
					return new VncJavaObject(arr);
				}
				else {
					final List<VncVal> list = Coerce.toVncSequence(args.first()).getList();
					
					final String[] arr = new String[list.size()];
					
					int ii=0;
					for(VncVal v : list) {
						if (!Types.isVncString(v)) {
							throw new VncException(String.format(
									"The value at pos %d in the collection is not a string",
									ii));
						}
						arr[ii++] = ((VncString)v).getValue();
					}
					
					return new VncJavaObject(arr);
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
		
	public static VncFunction int_array = 
		new VncFunction(
				"int-array", 
				VncFunction
					.meta()
					.arglists(
						"(int-array coll)", 
						"(int-array len)", 
						"(int-array len init-val)")		
					.doc(
						"Returns an array of Java primitive ints containing the contents of coll"
							+ "or returns an array with the given length")
					.examples(
						"(int-array '(1I 2I 3I))",
						"(int-array '(1I 2 3.2 3.56M))",
						"(int-array 10)",
						"(int-array 10 42I)") 
					.build()
		) {		
			public VncVal apply(final VncList args) {			
				assertArity("int-array", args, 1, 2);

				final VncVal arg = args.first();
				
				if (Types.isVncLong(arg)) {
					final int[] arr = new int[((VncLong)arg).getIntValue()];
					if (args.size() == 2) {
						Arrays.fill(arr, Numeric.toInteger(args.second()).getValue());
					}
					return new VncJavaObject(arr);
				}
				else {
					final List<VncVal> list = Coerce.toVncSequence(args.first()).getList();
					
					final int[] arr = new int[list.size()];
					
					int ii=0;
					for(VncVal v : list) {
						if (v == Nil || !Types.isVncNumber(v)) {
							throw new VncException(String.format(
									"The value at pos %d in the collection is not a number",
									ii));
						}
						arr[ii++] = Numeric.toInteger(v).getValue().intValue();
					}
					
					return new VncJavaObject(arr);
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
			
	public static VncFunction long_array = 
		new VncFunction(
				"long-array", 
				VncFunction
					.meta()
					.arglists(
						"(long-array coll)", 
						"(long-array len)", 
						"(long-array len init-val)")		
					.doc(
						"Returns an array of Java primitive longs containing the contents of coll"
							+ "or returns an array with the given length")
					.examples(
						"(long-array '(1 2 3))",
						"(long-array '(1I 2 3.2 3.56M))",
						"(long-array 10)",
						"(long-array 10 42)") 
					.build()
		) {		
			public VncVal apply(final VncList args) {			
				assertArity("long-array", args, 1, 2);

				final VncVal arg = args.first();
				
				if (Types.isVncLong(arg)) {
					final long[] arr = new long[((VncLong)arg).getIntValue()];
					if (args.size() == 2) {
						Arrays.fill(arr, Numeric.toLong(args.second()).getValue());
					}
					return new VncJavaObject(arr);
				}
				else {
					final List<VncVal> list = Coerce.toVncSequence(args.first()).getList();
					
					final long[] arr = new long[list.size()];
					
					int ii=0;
					for(VncVal v : list) {
						if (v == Nil || !Types.isVncNumber(v)) {
							throw new VncException(String.format(
									"The value at pos %d in the collection is not a number",
									ii));
						}
						arr[ii++] = Numeric.toLong(v).getValue().longValue();
					}
					
					return new VncJavaObject(arr);
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
		
	public static VncFunction float_array = 
		new VncFunction(
				"float-array", 
				VncFunction
					.meta()
					.arglists(
						"(float-array coll)", 
						"(float-array len)", 
						"(float-array len init-val)")		
					.doc(
						"Returns an array of Java primitive floats containing the contents of coll"
							+ "or returns an array with the given length")
					.examples(
						"(float-array '(1.0 2.0 3.0))",
						"(float-array '(1I 2 3.2 3.56M))",
						"(float-array 10)",
						"(float-array 10 42.0)") 
					.build()
		) {		
			public VncVal apply(final VncList args) {			
				assertArity("float-array", args, 1, 2);

				final VncVal arg = args.first();
				
				if (Types.isVncLong(arg)) {
					final float[] arr = new float[((VncLong)arg).getIntValue()];
					if (args.size() == 2) {
						Arrays.fill(arr, Numeric.toDouble(args.second()).getValue().floatValue());
					}
					return new VncJavaObject(arr);
				}
				else {
					final List<VncVal> list = Coerce.toVncSequence(args.first()).getList();
					
					final float[] arr = new float[list.size()];
					
					int ii=0;
					for(VncVal v : list) {
						if (v == Nil || !Types.isVncNumber(v)) {
							throw new VncException(String.format(
									"The value at pos %d in the collection is not a number",
									ii));
						}
						arr[ii++] = Numeric.toDouble(v).getValue().floatValue();
					}
					
					return new VncJavaObject(arr);
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
			
	public static VncFunction double_array = 
		new VncFunction(
				"double-array", 
				VncFunction
					.meta()
					.arglists(
						"(double-array coll)", 
						"(double-array len)", 
						"(double-array len init-val)")		
					.doc(
						"Returns an array of Java primitive doubles containing the contents of coll"
							+ "or returns an array with the given length")
					.examples(
						"(double-array '(1.0 2.0 3.0))",
						"(double-array '(1I 2 3.2 3.56M))",
						"(double-array 10)",
						"(double-array 10 42.0)") 
					.build()
		) {		
			public VncVal apply(final VncList args) {			
				assertArity("double-array", args, 1, 2);

				final VncVal arg = args.first();
				
				if (Types.isVncLong(arg)) {
					final double[] arr = new double[((VncLong)arg).getIntValue()];
					if (args.size() == 2) {
						Arrays.fill(arr, Numeric.toDouble(args.second()).getValue());
					}
					return new VncJavaObject(arr);
				}
				else {
					final List<VncVal> list = Coerce.toVncSequence(args.first()).getList();
					
					final double[] arr = new double[list.size()];
					
					int ii=0;
					for(VncVal v : list) {
						if (v == Nil || !Types.isVncNumber(v)) {
							throw new VncException(String.format(
									"The value at pos %d in the collection is not a number",
									ii));
						}
						arr[ii++] = Numeric.toDouble(v).getValue().doubleValue();
					}
					
					return new VncJavaObject(arr);
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};

	public static String arrayToString(final VncJavaObject val) {
		if (val.isArray()) {
			final StringBuilder sb = new StringBuilder("[");
			
			final Object delegate = val.getDelegate();

			final int length = Array.getLength(delegate);
			for(int ii=0; ii<length; ii++) {
				if (ii>=MAX_TO_STRING_ITEMS) {
					sb.append(String.format(", ... (%d more)", length - MAX_TO_STRING_ITEMS));
					break;
				}
				if (ii>0) sb.append(", ");
				sb.append(JavaInteropUtil.convertToVncVal(Array.get(delegate,ii)));
			}
			
			sb.append("]");
			return sb.toString();
		}
		else {
			throw new VncException(String.format("Not an array. Got %s", Types.getType(val)));
		}
	}
	
	
	private static int MAX_TO_STRING_ITEMS = 20;
	
			
	///////////////////////////////////////////////////////////////////////////
	// types_ns is namespace of type functions
	///////////////////////////////////////////////////////////////////////////

	public static Map<VncVal, VncVal> ns = 
			new VncHashMap.Builder()
					.put("aget",			aget)
					.put("aset",			aset)
					.put("alength",			alength)
					.put("asub",			asub)
					.put("acopy",			acopy)
					.put("make-array",		make_array)
					.put("object-array",	object_array)
					.put("string-array",	string_array)
					.put("int-array",		int_array)
					.put("long-array",		long_array)
					.put("float-array",		float_array)
					.put("double-array",	double_array)
					.toMap();	
}
