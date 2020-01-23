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
package com.github.jlangch.venice.impl.util.reflect;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.github.jlangch.venice.JavaMethodInvocationException;


public class LambdaMetafactoryUtil {

	// ------------------------------------------------------------------------
	// Instance method with return value
	// ------------------------------------------------------------------------
	
	public static Function1<Object,Object> instanceMethod_0_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function1<Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function1.class),
						MethodType.methodType(Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 0 arg instance method " + method.getName(), 
						ex);
		}
	}
	
	public static Function2<Object,Object,Object> instanceMethod_1_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function2<Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function2.class),
						MethodType.methodType(Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 1 arg instance method " + method.getName(), 
						ex);
		}
	}
	
	public static Function3<Object,Object,Object,Object> instanceMethod_2_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function3<Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function3.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 2 arg instance method " + method.getName(), 
						ex);
		}
	}
	
	public static Function4<Object,Object,Object,Object,Object> instanceMethod_3_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function4<Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function4.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 3 arg instance method " + method.getName(), 
						ex);
		}
	}
	
	public static Function5<Object,Object,Object,Object,Object,Object> instanceMethod_4_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function5<Object,Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function5.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 4 arg instance method " + method.getName(), 
						ex);
		}
	}
	
	public static Function6<Object,Object,Object,Object,Object,Object,Object> instanceMethod_5_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function6<Object,Object,Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function6.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 5 arg instance method " + method.getName(), 
						ex);
		}
	}
	
	public static Function7<Object,Object,Object,Object,Object,Object,Object,Object> instanceMethod_6_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function7<Object,Object,Object,Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function7.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 6 arg instance method " + method.getName(), 
						ex);
		}
	}
	
	public static Function8<Object,Object,Object,Object,Object,Object,Object,Object,Object> instanceMethod_7_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function8<Object,Object,Object,Object,Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function8.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 7 arg instance method " + method.getName(), 
						ex);
		}
	}

	
	// ------------------------------------------------------------------------
	// Instance method void
	// ------------------------------------------------------------------------

	public static Consumer1<Object> instanceMethodVoid_0_args(Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Consumer1<Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer1.class),
						MethodType.methodType(Void.TYPE, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
					"Could not generate the function to access the 0 arg void instance method " + method.getName(), 
					ex);
		}
	}

	public static Consumer2<Object,Object> instanceMethodVoid_1_args(Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Consumer2<Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer2.class),
						MethodType.methodType(Void.TYPE, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
					"Could not generate the function to access the 1 arg void instance method " + method.getName(), 
					ex);
		}
	}

	public static Consumer3<Object,Object,Object> instanceMethodVoid_2_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Consumer3<Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer3.class),
						MethodType.methodType(Void.TYPE, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 2 arg void instance method " + method.getName(), 
						ex);
		}
	}

	public static Consumer4<Object,Object,Object,Object> instanceMethodVoid_3_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Consumer4<Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer4.class),
						MethodType.methodType(Void.TYPE, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 3 arg void instance method " + method.getName(), 
						ex);
		}
	}

	public static Consumer5<Object,Object,Object,Object,Object> instanceMethodVoid_4_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Consumer5<Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer5.class),
						MethodType.methodType(Void.TYPE, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 4 arg void instance method " + method.getName(), 
						ex);
		}
	}

	public static Consumer6<Object,Object,Object,Object,Object,Object> instanceMethodVoid_5_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Consumer6<Object,Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer6.class),
						MethodType.methodType(Void.TYPE, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 5 arg void instance method " + method.getName(), 
						ex);
		}
	}

	public static Consumer7<Object,Object,Object,Object,Object,Object,Object> instanceMethodVoid_6_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Consumer7<Object,Object,Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer7.class),
						MethodType.methodType(Void.TYPE, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 6 arg void instance method " + method.getName(), 
						ex);
		}
	}

	public static Consumer8<Object,Object,Object,Object,Object,Object,Object,Object> instanceMethodVoid_7_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Consumer8<Object,Object,Object,Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer8.class),
						MethodType.methodType(Void.TYPE, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 7 arg void instance method " + method.getName(), 
						ex);
		}
	}



	// ------------------------------------------------------------------------
	// Static method with return value
	// ------------------------------------------------------------------------
	
	public static Function0<Object> staticMethod_0_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function0<Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function0.class),
						MethodType.methodType(Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 0 arg static method " + method.getName(), 
						ex);
		}
	}
	
	public static Function1<Object,Object> staticMethod_1_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function1<Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function1.class),
						MethodType.methodType(Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 1 arg static method " + method.getName(), 
						ex);
		}
	}
	
	public static Function2<Object,Object,Object> staticMethod_2_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function2<Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function2.class),
						MethodType.methodType(Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 2 arg static method " + method.getName(), 
						ex);
		}
	}
	
	public static Function3<Object,Object,Object,Object> staticMethod_3_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function3<Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function3.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 3 arg static method " + method.getName(), 
						ex);
		}
	}
	
	public static Function4<Object,Object,Object,Object,Object> staticMethod_4_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function4<Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function4.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 4 arg static method " + method.getName(), 
						ex);
		}
	}
	
	public static Function5<Object,Object,Object,Object,Object,Object> staticMethod_5_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function5<Object,Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function5.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 5 arg static method " + method.getName(), 
						ex);
		}
	}
	
	public static Function6<Object,Object,Object,Object,Object,Object,Object> staticMethod_6_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function6<Object,Object,Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function6.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 6 arg static method " + method.getName(), 
						ex);
		}
	}
	
	public static Function7<Object,Object,Object,Object,Object,Object,Object,Object> staticMethod_7_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function7<Object,Object,Object,Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function7.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 7 arg static method " + method.getName(), 
						ex);
		}
	}

	
	// ------------------------------------------------------------------------
	// Static method void
	// ------------------------------------------------------------------------

	public static Consumer0 staticMethodVoid_0_args(Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Consumer0)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer0.class),
						MethodType.methodType(Void.TYPE),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
					"Could not generate the function to access the 0 arg void static method " + method.getName(), 
					ex);
		}
	}

	public static Consumer1<Object> staticMethodVoid_1_args(Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Consumer1<Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer1.class),
						MethodType.methodType(Void.TYPE, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
					"Could not generate the function to access the 1 arg void static method " + method.getName(), 
					ex);
		}
	}

	public static Consumer2<Object,Object> staticMethodVoid_2_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Consumer2<Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer2.class),
						MethodType.methodType(Void.TYPE, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 2 arg void static method " + method.getName(), 
						ex);
		}
	}

	public static Consumer3<Object,Object,Object> staticMethodVoid_3_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Consumer3<Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer3.class),
						MethodType.methodType(Void.TYPE, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 3 arg void static method " + method.getName(), 
						ex);
		}
	}

	public static Consumer4<Object,Object,Object,Object> staticMethodVoid_4_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Consumer4<Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer4.class),
						MethodType.methodType(Void.TYPE, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 4 arg void static method " + method.getName(), 
						ex);
		}
	}

	public static Consumer5<Object,Object,Object,Object,Object> staticMethodVoid_5_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Consumer5<Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer5.class),
						MethodType.methodType(Void.TYPE, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 5 arg void static method " + method.getName(), 
						ex);
		}
	}

	public static Consumer6<Object,Object,Object,Object,Object,Object> staticMethodVoid_6_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Consumer6<Object,Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer6.class),
						MethodType.methodType(Void.TYPE, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 6 arg void static method " + method.getName(), 
						ex);
		}
	}

	public static Consumer7<Object,Object,Object,Object,Object,Object,Object> staticMethodVoid_7_args(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Consumer7<Object,Object,Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer7.class),
						MethodType.methodType(Void.TYPE, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 7 arg void static method " + method.getName(), 
						ex);
		}
	}


	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public static Function0<Object> constructor_0_args(final Constructor<?> constructor) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflectConstructor(constructor);
			
			return (Function0<Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function0.class),
						MethodType.methodType(Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 0 arg constructor " + constructor.getName(), 
						ex);
		}
	}
	
	public static Function1<Object,Object> constructor_1_args(final Constructor<?> constructor) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflectConstructor(constructor);
			
			return (Function1<Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function1.class),
						MethodType.methodType(Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 1 arg constructor " + constructor.getName(), 
						ex);
		}
	}
	
	public static Function2<Object,Object,Object> constructor_2_args(final Constructor<?> constructor) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflectConstructor(constructor);
			
			return (Function2<Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function2.class),
						MethodType.methodType(Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 2 arg constructor " + constructor.getName(), 
						ex);
		}
	}
	
	public static Function3<Object,Object,Object,Object> constructor_3_args(final Constructor<?> constructor) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflectConstructor(constructor);
			
			return (Function3<Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function3.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 3 arg constructor " + constructor.getName(), 
						ex);
		}
	}
	
	public static Function4<Object,Object,Object,Object,Object> constructor_4_args(final Constructor<?> constructor) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflectConstructor(constructor);
			
			return (Function4<Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function4.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 4 arg constructor " + constructor.getName(), 
						ex);
		}
	}
	
	public static Function5<Object,Object,Object,Object,Object,Object> constructor_5_args(final Constructor<?> constructor) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflectConstructor(constructor);
			
			return (Function5<Object,Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function5.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 5 arg constructor " + constructor.getName(), 
						ex);
		}
	}
	
	public static Function6<Object,Object,Object,Object,Object,Object,Object> constructor_6_args(final Constructor<?> constructor) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflectConstructor(constructor);
			
			return (Function6<Object,Object,Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function6.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 6 arg constructor " + constructor.getName(), 
						ex);
		}
	}
	
	public static Function7<Object,Object,Object,Object,Object,Object,Object,Object> constructor_7_args(final Constructor<?> constructor) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflectConstructor(constructor);
			
			return (Function7<Object,Object,Object,Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function7.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 7 arg constructor " + constructor.getName(), 
						ex);
		}
	}
	
	public static Function8<Object,Object,Object,Object,Object,Object,Object,Object,Object> constructor_8_args(final Constructor<?> constructor) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflectConstructor(constructor);
			
			return (Function8<Object,Object,Object,Object,Object,Object,Object,Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function8.class),
						MethodType.methodType(Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the 8 arg constructor " + constructor.getName(), 
						ex);
		}
	}
	
	

	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------

	public static Function1<Object,Object> instanceField_get(final Field field) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflectGetter(field);
			
			return (Function1<Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function1.class),
						MethodType.methodType(Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the field " + field.getName(), 
						ex);
		}
	}
	
	public static Consumer2<Object,Object> instanceField_set(final Field field) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflectSetter(field);
			
			return (Consumer2<Object,Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer2.class),
						MethodType.methodType(Void.TYPE, Object.class, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the field " + field.getName(), 
						ex);
		}
	}

	public static Function0<Object> staticField_get(final Field field) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflectGetter(field);
			
			return (Function0<Object>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function0.class),
						MethodType.methodType(Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the field " + field.getName(), 
						ex);
		}
	}
	
	public static Consumer1<Object> staticField_set(final Field field) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflectSetter(field);
			
			return (Consumer1<Object>)LambdaMetafactory
					.metafactory(
						caller,
						"accept",
						MethodType.methodType(Consumer1.class),
						MethodType.methodType(Void.TYPE, Object.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new JavaMethodInvocationException(
						"Could not generate the function to access the field " + field.getName(), 
						ex);
		}
	}

	
	// ------------------------------------------------------------------------
	// SAM
	// ------------------------------------------------------------------------
	
	@FunctionalInterface
	public static interface Function0<R> {
		R apply();
	}

	@FunctionalInterface
	public static interface Function1<T1, R> {
		R apply(T1 t1);
	}

	@FunctionalInterface
	public static interface Function2<T1, T2, R> {
		R apply(T1 t1, T2 t2);
	}
	
	@FunctionalInterface
	public static interface Function3<T1, T2, T3, R> {
		R apply(T1 t1, T2 t2, T3 t3);
	}
	
	@FunctionalInterface
	public static interface Function4<T1, T2, T3, T4, R> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4);
	}
	
	@FunctionalInterface
	public static interface Function5<T1, T2, T3, T4, T5, R> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
	}
	
	@FunctionalInterface
	public static interface Function6<T1, T2, T3, T4, T5, T6, R> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);
	}
	
	@FunctionalInterface
	public static interface Function7<T1, T2, T3, T4, T5, T6, T7, R> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);
	}
	
	@FunctionalInterface
	public static interface Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> {
		R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);
	}

	
	
	@FunctionalInterface
	public static interface Consumer0 {
		void accept();
	}

	@FunctionalInterface
	public static interface Consumer1<T1> {
		void accept(T1 t1);
	}

	@FunctionalInterface
	public static interface Consumer2<T1, T2> {
		void accept(T1 t1, T2 t2);
	}
	
	@FunctionalInterface
	public static interface Consumer3<T1, T2, T3> {
		void accept(T1 t1, T2 t2, T3 t3);
	}
	
	@FunctionalInterface
	public static interface Consumer4<T1, T2, T3, T4> {
		void accept(T1 t1, T2 t2, T3 t3, T4 t4);
	}
	
	@FunctionalInterface
	public static interface Consumer5<T1, T2, T3, T4, T5> {
		void accept(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
	}
	
	@FunctionalInterface
	public static interface Consumer6<T1, T2, T3, T4, T5, T6> {
		void accept(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);
	}
	
	@FunctionalInterface
	public static interface Consumer7<T1, T2, T3, T4, T5, T6, T7> {
		void accept(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);
	}
	
	@FunctionalInterface
	public static interface Consumer8<T1, T2, T3, T4, T5, T6, T7, T8> {
		void accept(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);
	}

}
