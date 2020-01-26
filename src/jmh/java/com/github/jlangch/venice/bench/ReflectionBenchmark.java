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
package com.github.jlangch.venice.bench;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import com.github.jlangch.venice.impl.util.reflect.LambdaMetafactoryUtil;
import com.github.jlangch.venice.impl.util.reflect.LambdaMetafactoryUtil.Function1;
import com.github.jlangch.venice.impl.util.reflect.LambdaMetafactoryUtil.Function2;

// Benchmark                                      Mode  Cnt   Score   Error  Units
// -------------------------------------------------------------------------------
// ReflectionBenchmark.bench_native               avgt    3  23.701 ± 1.406  ns/op
// ReflectionBenchmark.bench_reflective           avgt    3  57.975 ± 3.189  ns/op
// ReflectionBenchmark.bench_LambdaMetafactory_1  avgt    3  24.539 ± 2.248  ns/op
// ReflectionBenchmark.bench_LambdaMetafactory_2  avgt    3  24.560 ± 1.015  ns/op
// ReflectionBenchmark.bench_LambdaMetafactory_3  avgt    3  24.691 ± 1.335  ns/op
// ReflectionBenchmark.bench_LambdaMetafactory_4  avgt    3  30.845 ± 4.883  ns/op

@Warmup(iterations=3, time=3, timeUnit=TimeUnit.SECONDS)
@Measurement(iterations=3, time=10, timeUnit=TimeUnit.SECONDS)
@Fork(1)
@BenchmarkMode (Mode.AverageTime)
@OutputTimeUnit (TimeUnit.NANOSECONDS)
@State (Scope.Benchmark)
@Threads (1)
public class ReflectionBenchmark {
	
	public ReflectionBenchmark() {
		init();
	}
	
	
	@Benchmark
	public BigInteger bench_native() {
		final BigInteger i1 = BigInteger.valueOf(10L);
		final BigInteger i2 = BigInteger.valueOf(100L);
		return i1.add(i2);
	}

	@Benchmark
	public BigInteger bench_reflective() {
		try {
			final BigInteger i1 = (BigInteger)mValueOf.invoke(BigInteger.class, new Object[] {10L});
			final BigInteger i2 = (BigInteger)mValueOf.invoke(BigInteger.class, new Object[] {100L});
			return (BigInteger)mAdd.invoke(i1, new Object[] {i2});	       		
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Benchmark
	public BigInteger bench_LambdaMetafactory_1() {
		// Typed SAM
		try {
			final BigInteger i1 = (BigInteger)fnTypedValueOf.apply(10L);
			final BigInteger i2 = (BigInteger)fnTypedValueOf.apply(100L);
			return fnTypedAdd.apply(i1, i2);      		
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Benchmark
	public BigInteger bench_LambdaMetafactory_2() {
		// Generic SAM
		try {
			final BigInteger i1 = (BigInteger)fnGenericValueOf.apply(10L);
			final BigInteger i2 = (BigInteger)fnGenericValueOf.apply(100L);
			return fnGenericAdd.apply(i1, i2);      		
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Benchmark
	public BigInteger bench_LambdaMetafactory_3() {
		// Generic SAM (Object)
		try {
			final BigInteger i1 = (BigInteger)fnValueOf.apply(10L);
			final BigInteger i2 = (BigInteger)fnValueOf.apply(100L);
			return (BigInteger)fnAdd.apply(i1, i2);      		
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Benchmark
	public BigInteger bench_LambdaMetafactory_4() {
		// Generic SAM (Object) / generic dispatch
		try {
			final BigInteger i1 = (BigInteger)LambdaMetafactoryUtil.invoke_staticMethod(new Object[] {10L}, fnValueOf);
			final BigInteger i2 = (BigInteger)LambdaMetafactoryUtil.invoke_staticMethod(new Object[] {100L}, fnValueOf);
			return (BigInteger)LambdaMetafactoryUtil.invoke_instanceMethod(i1, new Object[] {i2}, fnAdd);      		
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private void init() {
		try {
			mValueOf = BigInteger.class.getDeclaredMethod("valueOf", long.class);
			mAdd = BigInteger.class.getDeclaredMethod("add", BigInteger.class);
			
			fnValueOf = LambdaMetafactoryUtil.staticMethod_1_args(mValueOf);
			fnAdd = LambdaMetafactoryUtil.instanceMethod_1_args(mAdd);
			
			fnGenericValueOf = compileValueOfGeneric(mValueOf);
			fnGenericAdd = compileAddGeneric(mAdd);
			
			fnTypedValueOf = compileValueOfTyped(mValueOf);
			fnTypedAdd = compileAddTyped(mAdd);
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private static Function1<Long,BigInteger> compileValueOfGeneric(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function1<Long,BigInteger>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function1.class),
						MethodType.methodType(Object.class, Object.class), // type erasure on SAM!
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private static Function2<BigInteger,BigInteger,BigInteger> compileAddGeneric(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (Function2<BigInteger,BigInteger,BigInteger>)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(Function2.class),
						MethodType.methodType(Object.class, Object.class, Object.class), // type erasure on SAM!
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private static BigInteger_ValueOf compileValueOfTyped(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (BigInteger_ValueOf)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(BigInteger_ValueOf.class),
						MethodType.methodType(BigInteger.class, Long.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private static BigInteger_Add compileAddTyped(final Method method) {
		try {
			final MethodHandles.Lookup caller = MethodHandles.lookup();
			final MethodHandle handle = caller.unreflect(method);
			
			return (BigInteger_Add)LambdaMetafactory
					.metafactory(
						caller,
						"apply",
						MethodType.methodType(BigInteger_Add.class),
						MethodType.methodType(BigInteger.class, BigInteger.class, BigInteger.class),
						handle,
						handle.type())
					.getTarget()
					.invoke();
		} 
		catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
	}

	
	@FunctionalInterface
	public static interface BigInteger_ValueOf {
		BigInteger apply(Long u);
	}
	
	@FunctionalInterface
	public static interface BigInteger_Add {
		BigInteger apply(BigInteger t, BigInteger u);
	}

	
	private Method mValueOf;
	private Method mAdd;
	private Function1<Object,Object> fnValueOf;
	private Function2<Object,Object,Object> fnAdd;
	private Function1<Long,BigInteger> fnGenericValueOf;
	private Function2<BigInteger,BigInteger,BigInteger> fnGenericAdd;
	private BigInteger_ValueOf fnTypedValueOf;
	private BigInteger_Add fnTypedAdd;
}
