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

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.jlangch.venice.JavaMethodInvocationException;
import com.github.jlangch.venice.impl.types.Constants;
import com.github.jlangch.venice.impl.types.IVncJavaObject;
import com.github.jlangch.venice.impl.types.Types;
import com.github.jlangch.venice.impl.types.VncBigDecimal;
import com.github.jlangch.venice.impl.types.VncByteBuffer;
import com.github.jlangch.venice.impl.types.VncConstant;
import com.github.jlangch.venice.impl.types.VncDouble;
import com.github.jlangch.venice.impl.types.VncJavaObject;
import com.github.jlangch.venice.impl.types.VncKeyword;
import com.github.jlangch.venice.impl.types.VncLong;
import com.github.jlangch.venice.impl.types.VncString;
import com.github.jlangch.venice.impl.types.VncSymbol;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncJavaList;
import com.github.jlangch.venice.impl.types.collections.VncJavaMap;
import com.github.jlangch.venice.impl.types.collections.VncJavaSet;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.collections.VncMap;
import com.github.jlangch.venice.impl.types.collections.VncHashSet;
import com.github.jlangch.venice.impl.types.collections.VncVector;
import com.github.jlangch.venice.impl.util.Agent;
import com.github.jlangch.venice.impl.util.Delay;
import com.github.jlangch.venice.impl.util.ErrorMessage;
import com.github.jlangch.venice.impl.util.reflect.ReflectionAccessor;
import com.github.jlangch.venice.impl.util.reflect.ReflectionTypes;


public class JavaInteropUtil {

	public static VncVal applyJavaAccess(final VncList args, final JavaImports javaImports) {
		try {
			final VncVal arg0 = args.first();		
			final VncString method = (VncString)args.second();
			final VncList params = args.slice(2);
			
			final String methodName = method.getValue();
			
			
			if ("new".equals(methodName)) {			
				// call constructor (. :java.util.String :new \"abc\")
				final String className = javaImports.resolveClassName(((VncString)arg0).getValue());
				final Class<?> targetClass = ReflectionAccessor.classForName(className);
				
				// Delay & Agents exceptionally get the original Venice data types passed!
				final Object[] methodArgs = isDelayOrAgentClass(className) 
												? copyToJavaMethodArgs(params)
												: convertToJavaMethodArgs(params);
												
				return JavaInteropUtil.convertToVncVal(
						JavaInterop
							.getInterceptor()
							.onInvokeConstructor(new Invoker(), targetClass, methodArgs));
			}
			else if ("class".equals(methodName)) {			
				// get class (. :java.util.String :class)
				if (Types.isVncString(arg0)) {
					final String className = javaImports.resolveClassName(((VncString)arg0).getValue());
					try {
						return new VncJavaObject(ReflectionAccessor.classForName(className));
					}
					catch(Exception ex) {
						return Constants.Nil;
					}
				}
				else if (Types.isVncJavaObject(arg0)) {
					return new VncJavaObject(((VncJavaObject)arg0).getDelegate().getClass());
				}
				else {
					return new VncJavaObject(Types.getClassName(arg0));
				}
			}
			else {
				if (Types.isVncString(arg0)) {
					// static method / field:   (. :org.foo.Foo :getLastName)
					final String className = javaImports.resolveClassName(((VncString)arg0).getValue());
					final Class<?> targetClass = ReflectionAccessor.classForName(className);

					
					// Delay & Agents exceptionally get the original Venice data types passed!
					final Object[] methodArgs = isDelayOrAgentClass(className) 
													? copyToJavaMethodArgs(params)
													: convertToJavaMethodArgs(params);

					if (methodArgs.length > 0 || ReflectionAccessor.isStaticMethod(targetClass, methodName, methodArgs)) {
						// static method
						return JavaInteropUtil.convertToVncVal(
								JavaInterop
									.getInterceptor()
									.onInvokeStaticMethod(new Invoker(), targetClass, methodName, methodArgs));
					}
					else if (ReflectionAccessor.isStaticField(targetClass, methodName)) {
						// static field
						return JavaInteropUtil.convertToVncVal(
								JavaInterop
									.getInterceptor()
									.onGetStaticField(new Invoker(), targetClass, methodName));
					}
					else {
						throw new JavaMethodInvocationException(String.format(
								"No matching public static method or field found: '%s' for target '%s'",
								methodName,
								targetClass));
					}
				}
				else {
					// instance method/field:   (. person :getLastName)
					//	                        (. person :setLastName \"john\")
					final Object target = Types.isVncJavaObject(arg0)
											? ((VncJavaObject)arg0).getDelegate()
											: JavaInteropUtil.convertToJavaObject(arg0);
											
					// Delay & Agents exceptionally get the original Venice data types passed!
					final Object[] methodArgs = isDelayOrAgentClass(target) 
													? copyToJavaMethodArgs(params)
													: convertToJavaMethodArgs(params);
	
					if (methodArgs.length > 0 || ReflectionAccessor.isInstanceMethod(target, methodName, methodArgs)) {
						// instance method
						return JavaInteropUtil.convertToVncVal(
								JavaInterop
									.getInterceptor()
									.onInvokeInstanceMethod(new Invoker(), target, methodName, methodArgs));
					}
					else if (ReflectionAccessor.isInstanceField(target, methodName)) {
						// instance field
						return JavaInteropUtil.convertToVncVal(
								JavaInterop
									.getInterceptor()
									.onGetInstanceField(new Invoker(), target, methodName));
					}
					else {
						throw new JavaMethodInvocationException(String.format(
								"No matching public instance method or field found: '%s' for target '%s'",
								methodName,
								target.getClass()));
					}
				}
			}
		}
		catch(JavaMethodInvocationException ex) {
			if (ex.getCause() != null && ex.getCause() instanceof SecurityException) {
				throw new SecurityException(String.format(
						"%s. %s", 
						ex.getMessage(),
						ErrorMessage.buildErrLocation(args)));
			}
			else {
				throw new JavaMethodInvocationException(
						String.format(
							"%s. %s", 
							ex.getMessage(),
							ErrorMessage.buildErrLocation(args)),
						ex);
			}
		}
		catch(SecurityException ex) {
			throw new SecurityException(String.format(
					"%s. %s", 
					ex.getMessage(),
					ErrorMessage.buildErrLocation(args)));
		}
		catch(RuntimeException ex) {
			throw new JavaMethodInvocationException(String.format(
						"JavaInterop failure. %s", 
						ErrorMessage.buildErrLocation(args)));
		}
	}
	
	private static Object[] convertToJavaMethodArgs(final VncList params) {
		final Object[] methodArgs = new Object[params.size()];
		for(int ii=0; ii<params.size(); ii++) {
			methodArgs[ii] = JavaInteropUtil.convertToJavaObject(params.nth(ii));
		}
		return methodArgs;
	}

	private static Object[] copyToJavaMethodArgs(final VncList params) {
		final Object[] methodArgs = new Object[params.size()];
		for(int ii=0; ii<params.size(); ii++) {
			methodArgs[ii] = params.nth(ii);
		}
		return methodArgs;
	}

	public static Object convertToJavaObject(final VncVal value) {
		if (Types.isVncConstant(value)) {
			if (((VncConstant)value) == Constants.Nil) {
				return null;
			}
			else if (((VncConstant)value) == Constants.True) {
				return Boolean.TRUE;
			}
			else if (((VncConstant)value) == Constants.False) {
				return Boolean.FALSE;
			}
		}
		else if (value instanceof IVncJavaObject) {
			return ((IVncJavaObject)value).getDelegate();
		}
		else if (Types.isVncKeyword(value)) {
			return ((VncKeyword)value).getValue();
		}
		else if (Types.isVncSymbol(value)) {
			return ((VncSymbol)value).getName();
		}
		else if (Types.isVncString(value)) {
			return ((VncString)value).getValue();
		}
		else if (Types.isVncLong(value)) {
			return ((VncLong)value).getValue();
		}
		else if (Types.isVncDouble(value)) {
			return ((VncDouble)value).getValue();
		}
		else if (Types.isVncBigDecimal(value)) {
			return ((VncBigDecimal)value).getValue();
		}
		else if (Types.isVncByteBuffer(value)) {
			return ((VncByteBuffer)value).getValue();
		}
		else if (Types.isVncVector(value)) {
			return ((VncVector)value)
						.getList()
						.stream()
						.map(v -> convertToJavaObject(v))
						.filter(v -> v != null)
						.collect(Collectors.toList());
		}
		else if (Types.isVncList(value)) {
			return ((VncList)value)
						.getList()
						.stream()
						.map(v -> convertToJavaObject(v))
						.filter(v -> v != null)
						.collect(Collectors.toList());
		}
		else if (Types.isVncHashSet(value)) {
			return ((VncHashSet)value)
						.getList()
						.stream()
						.map(v -> convertToJavaObject(v))
						.filter(v -> v != null)
						.collect(Collectors.toSet());
		}
		else if (Types.isVncMap(value)) {
			return ((VncMap)value)
						.entries()
						.stream()
						.collect(Collectors.toMap(
								e -> convertToJavaObject(e.getKey()),
								e -> convertToJavaObject(e.getValue())));
		}

		// other types not supported yet
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static VncVal convertToVncVal(final Object value) {
		if (value == null) {
			return Constants.Nil;
		}
		else if (value instanceof VncVal) {
			return (VncVal)value;
		}
		else if (value instanceof String) {
			return new VncString((String)value);
		}
		else if (value instanceof Long) {
			return new VncLong((Long)value);
		}
		else if (value instanceof Double) {
			return new VncDouble((Double)value);
		}
		else if (value instanceof Byte) {
			return null;
		}
		else if (value instanceof Short) {
			return new VncLong(((Short)value).longValue());
		}
		else if (value instanceof Integer) {
			return new VncLong(((Integer)value).longValue());
		}
		else if (value instanceof Float) {
			return new VncDouble((Float)value);
		}
		else if (value instanceof BigDecimal) {
			return new VncBigDecimal((BigDecimal)value);
		}
		else if (value instanceof Boolean) {
			return ((Boolean)value).booleanValue() ? Constants.True : Constants.False;
		}
		else if (ReflectionTypes.isEnumType(value.getClass())) {
			return new VncString(value.toString());
		}
		else if (value instanceof ByteBuffer) {
			return new VncByteBuffer((ByteBuffer)value);
		}
		else if (value instanceof List) {
			return new VncJavaList((List<Object>)value);
		}
		else if (value instanceof Set) {
			return new VncJavaSet((Set<Object>)value);
		}
		else if (value instanceof Map) {
			return new VncJavaMap((Map<Object,Object>)value);
		}
		else if (ReflectionTypes.isArrayType(value.getClass())) {
			final Class<?> componentType = value.getClass().getComponentType();					
			if (componentType == byte.class) {
				return new VncByteBuffer(ByteBuffer.wrap((byte[])value));
			}
			else {
				final List<VncVal> vec = new ArrayList<>();
				for(int ii=0; ii<Array.getLength(value); ii++) {
					vec.add(convertToVncVal(Array.get(value, ii)));
				}
				return new VncVector(vec);
			}
		}
		else if (value instanceof Class) {
			return new VncString(((Class<?>)value).getName());
		}
		else { 
			return new VncJavaObject(value);
		}
	}

	private static boolean isDelayOrAgentClass(final String className) {
		return (className.equals(Delay.class.getName()) || className.equals(Agent.class.getName()));
	}
	
	private static boolean isDelayOrAgentClass(final Object target) {
		return target instanceof Delay || target instanceof Agent;
	}
}
