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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.github.jlangch.venice.Venice;
import com.github.jlangch.venice.VncException;


public class MathFunctionsTest {

	@Test
	public void test_abs() {
		final Venice venice = new Venice();

		// Integer
		assertEquals(Integer.valueOf(3), venice.eval("(abs (int 3))"));
		assertEquals(Integer.valueOf(3), venice.eval("(abs (int -3))"));

		// Long
		assertEquals(Long.valueOf(3), venice.eval("(abs 3)"));
		assertEquals(Long.valueOf(3), venice.eval("(abs -3)"));

		// Double
		assertEquals(Double.valueOf(3.0D), venice.eval("(abs 3.0)"));
		assertEquals(Double.valueOf(3.0D), venice.eval("(abs -3.0)"));

		// Decimal
		assertEquals(new BigDecimal("3.333"), venice.eval("(abs 3.333M))"));
		assertEquals(new BigDecimal("3.333"), venice.eval("(abs -3.333M))"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(abs 3.0M)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(abs -3.0M)"));
	}

	@Test
	public void test_add() {
		final Venice venice = new Venice();

		assertEquals(Long.valueOf(0), venice.eval("(+)"));

		// Integer
		assertEquals(Integer.valueOf(3), venice.eval("(+ (int 3))"));
		assertEquals(Integer.valueOf(3), venice.eval("(+ (int 1) (int 2))"));
		assertEquals(Integer.valueOf(6), venice.eval("(+ (int 1) (int 2) (int 3))"));
		assertEquals(Long.valueOf(3), venice.eval("(+ (int 1) 2)"));
		assertEquals(Double.valueOf(3.0D), venice.eval("(+ (int 1) 2.0)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(+ (int 1) 2.0M)"));

		// Long
		assertEquals(Long.valueOf(3), venice.eval("(+ 3)"));
		assertEquals(Long.valueOf(3), venice.eval("(+ 1 2)"));
		assertEquals(Long.valueOf(6), venice.eval("(+ 1 2 3)"));
		assertEquals(Double.valueOf(3.0D), venice.eval("(+ 1 2.0)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(+ 1 2.0M)"));

		// Double
		assertEquals(Double.valueOf(3.0D), venice.eval("(+ 3.0)"));
		assertEquals(Double.valueOf(3.0D), venice.eval("(+ 1.0 2.0)"));
		assertEquals(Double.valueOf(3.0D), venice.eval("(+ 1.0 2)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(+ 1.0 2.0M)"));

		// Decimal
		assertEquals(new BigDecimal("3.0"), venice.eval("(+ 3.0M)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(+ 1.0M 2.0M)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(+ 1.0M 2)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(+ 1.0M 2.0)"));
	}

	@Test
	public void test_dec() {
		final Venice venice = new Venice();

		assertEquals(Integer.valueOf(1), venice.eval("(dec (int 2))"));

		assertEquals(Long.valueOf(1L), venice.eval("(dec 2)"));

		assertEquals(Double.valueOf(3.2D), (Double)venice.eval("(dec 4.2)"), 0.00001);

		assertEquals(new BigDecimal("4.01234"), venice.eval("(dec 5.01234M)"));
	}

	@Test
	public void test_dec_add() {
		final Venice venice = new Venice();

		assertEquals(new BigDecimal("6.323"), venice.eval("(dec/add 3.12345M 3.2M 3 :HALF_UP)"));
	}
	
	@Test
	public void test_dec_sub() {
		final Venice venice = new Venice();

		assertEquals(new BigDecimal("6.420"), venice.eval("(dec/sub 8.54321M 2.123M 3 :HALF_UP)"));
	}
	
	@Test
	public void test_dec_mul() {
		final Venice venice = new Venice();

		assertEquals(new BigDecimal("8.664"), venice.eval("(dec/mul 3.2345M 2.6787M 3 :HALF_UP)"));
	}
	
	@Test
	public void test_dec_div() {
		final Venice venice = new Venice();

		assertEquals(new BigDecimal("2.675"), venice.eval("(dec/div 6.567876M 2.45556M 3 :HALF_UP)"));
	}
	
	@Test
	public void test_dec_scale() {
		final Venice venice = new Venice();

		assertEquals(new BigDecimal("3"), venice.eval("(dec/scale 3.0M 0 :HALF_UP)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(dec/scale 3.0M 1 :HALF_UP)"));
		assertEquals(new BigDecimal("3.000"), venice.eval("(dec/scale 3.0M 3 :HALF_UP)"));
		assertEquals(new BigDecimal("3.457"), venice.eval("(dec/scale 3.456789M 3 :HALF_UP)"));
	}

	@Test
	public void test_decimal() {
		final Venice venice = new Venice();

		assertEquals(new BigDecimal("1.00"), venice.eval("(decimal (int 1) 2 :HALF_UP)"));
		assertEquals(new BigDecimal("1.00"), venice.eval("(decimal 1 2 :HALF_UP)"));
		assertEquals(new BigDecimal("1.23"), venice.eval("(decimal 1.23 2 :HALF_UP)"));
		assertEquals(new BigDecimal("1.230"), venice.eval("(decimal 1.23M 3 :HALF_UP)"));
		assertEquals(new BigDecimal("1.23"), venice.eval("(decimal \"1.23\" 2 :HALF_UP)"));
	}

	@Test
	public void test_double() {
		final Venice venice = new Venice();

		assertEquals(1D, venice.eval("(double (int 1))"));
		assertEquals(1D, venice.eval("(double 1)"));
		assertEquals(1.23D, venice.eval("(double 1.23)"));
		assertEquals(1.23D, venice.eval("(double 1.23M)"));
		assertEquals(1.23D, venice.eval("(double \"1.23\")"));
	}

	@Test
	public void test_div() {
		final Venice venice = new Venice();
		
		// Integer
		assertEquals(Integer.valueOf(0), venice.eval("(/ (int 12))"));
		assertEquals(Integer.valueOf(6), venice.eval("(/ (int 12) (int 2))"));
		assertEquals(Integer.valueOf(2), venice.eval("(/ (int 12) (int 2) (int 3))"));
		assertEquals(Long.valueOf(2L), venice.eval("(/ (int 12) 2 3)"));
		assertEquals(Double.valueOf(6.0D), venice.eval("(/ (int 12) 2.0)"));
		assertEquals(new BigDecimal("6.0000000000000000"), venice.eval("(/ (int 12) 2.0M)"));
		
		// Long
		assertEquals(Long.valueOf(0), venice.eval("(/ 12)"));
		assertEquals(Long.valueOf(6), venice.eval("(/ 12 2)"));
		assertEquals(Long.valueOf(2), venice.eval("(/ 12 2 3)"));
		assertEquals(Double.valueOf(6.0D), venice.eval("(/ 12 2.0)"));
		assertEquals(new BigDecimal("6.0000000000000000"), venice.eval("(/ 12 2.0M)"));

		// Double
		assertEquals(Double.valueOf(0.16666D), (Double)venice.eval("(/ 6.0)"), 0.001D);
		assertEquals(Double.valueOf(6.0D), venice.eval("(/ 12.0 2.0)"));
		assertEquals(Double.valueOf(2.0D), venice.eval("(/ 12.0 2.0 3.0)"));
		assertEquals(Double.valueOf(6.0D), venice.eval("(/ 12.0 2)"));
		assertEquals(new BigDecimal("6.0000000000000000"), venice.eval("(/ 12.0 2.0M)"));

		// Decimal
		assertEquals(new BigDecimal("0.1666666666666667"), venice.eval("(/ 6.0M)"));
		assertEquals(new BigDecimal("6.0000000000000000"), venice.eval("(/ 12.0M 2.0M)"));
		assertEquals(new BigDecimal("6.0000000000000000"), venice.eval("(/ 12.0M 2)"));
		assertEquals(new BigDecimal("6.0000000000000000"), venice.eval("(/ 12.0M 2.0)"));
	}

	@Test
	public void test_div_by_zero() {
		final Venice venice = new Venice();

		final String s = 
				"(do                        \n" +
				"   (defn f1 [x] (f2 x))    \n" +
				"   (defn f2 [x] (f3 x))    \n" +
				"   (defn f3 [x] (f4 x))    \n" +
				"   (defn f4 [x] (f5 x))    \n" +
				"   (defn f5 [x] (/ 1 x))   \n" +
				"   (f1 0))                    ";
	
		assertThrows(VncException.class, () -> {
			venice.eval("test", s);
		});
	}

	@Test
	public void test_even_Q() {
		final Venice venice = new Venice();

		assertTrue((Boolean)venice.eval("(even? (int 2))"));	
		assertTrue((Boolean)venice.eval("(even? (int 0))"));	
		assertFalse((Boolean)venice.eval("(even? (int 1))"));	

		assertTrue((Boolean)venice.eval("(even? 2)"));	
		assertTrue((Boolean)venice.eval("(even? 0)"));	
		assertFalse((Boolean)venice.eval("(even? 1)"));	
		
		try {
			venice.eval("(even? 1.0)");
			fail("Expected exception");
		}
		catch(RuntimeException ex) {
			assertTrue(true);
		}
	}

	@Test
	public void test_inc() {
		final Venice venice = new Venice();

		assertEquals(Integer.valueOf(1), venice.eval("(inc (int 0))"));

		assertEquals(Long.valueOf(1L), venice.eval("(inc 0)"));

		assertEquals(Double.valueOf(3.2D), (Double)venice.eval("(inc 2.2)"), 0.00001);

		assertEquals(new BigDecimal("4.01234"), venice.eval("(inc 3.01234M)"));
	}

	@Test
	public void test_long() {
		final Venice venice = new Venice();

		assertEquals(1L, venice.eval("(long 1)"));
		assertEquals(1L, venice.eval("(long (int 1))"));
		assertEquals(1L, venice.eval("(long 1.23)"));
		assertEquals(1L, venice.eval("(long 1.23M)"));
		assertEquals(1L, venice.eval("(long \"1\")"));
	}
	
	@Test
	public void test_max() {
		final Venice venice = new Venice();

		// Integer
		assertEquals(Integer.valueOf(3), venice.eval("(max (int 3))"));
		assertEquals(Integer.valueOf(3), venice.eval("(max (int 1) (int 3))"));
		assertEquals(Integer.valueOf(3), venice.eval("(max (int 1) (int 2) (int 3))"));
		assertEquals(Integer.valueOf(3), venice.eval("(max 1 2 (int 3))"));
		assertEquals(Integer.valueOf(3), venice.eval("(max 1 2 (int 3) 1.0)"));
		assertEquals(Integer.valueOf(3), venice.eval("(max 1 2 (int 3) 1.0M)"));
		assertEquals(Double.valueOf(3.0D), venice.eval("(max (int 1) 2 3.0)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(max (int 1) 2 2.0 3.0M)"));

		// Long
		assertEquals(Long.valueOf(3), venice.eval("(max 3)"));
		assertEquals(Long.valueOf(3), venice.eval("(max 1 3)"));
		assertEquals(Long.valueOf(3), venice.eval("(max 1 2 3)"));
		assertEquals(Long.valueOf(3), venice.eval("(max 1 2 3 1.0)"));
		assertEquals(Long.valueOf(3), venice.eval("(max 1 2 3 1.0M)"));
		assertEquals(Double.valueOf(3.0D), venice.eval("(max 1 3.0)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(max 1 2.0 3.0M)"));

		// Double
		assertEquals(Double.valueOf(3.0D), venice.eval("(max 3.0)"));
		assertEquals(Double.valueOf(3.0D), venice.eval("(max 1.0 3.0)"));
		assertEquals(Double.valueOf(3.0D), venice.eval("(max 1.0 2.0 3.0)"));
		assertEquals(Double.valueOf(3.0D), venice.eval("(max 1.0 2.0 3.0 2)"));
		assertEquals(Double.valueOf(3.0D), venice.eval("(max 1.0 2.0 3.0 1.0M)"));
		assertEquals(Long.valueOf(3), venice.eval("(max 1.0 3)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(max 1.0 3.0M)"));

		// Decimal
		assertEquals(new BigDecimal("3.0"), venice.eval("(max 3.0M)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(max 1.0M 3.0M)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(max 1.0M 2.0M 3.0M)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(max 1.0M 2.0M 3.0M 2)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(max 1.0M 2.0M 3.0M 2.0)"));
		assertEquals(Long.valueOf(3), venice.eval("(max 1.0M 3)"));
		assertEquals(Double.valueOf(3.0D), venice.eval("(max 1.0M 3.0)"));
	}

	@Test
	public void test_min() {
		final Venice venice = new Venice();

		// Integer
		assertEquals(Integer.valueOf(3), venice.eval("(min (int 3))"));
		assertEquals(Integer.valueOf(1), venice.eval("(min (int 1) (int 3))"));
		assertEquals(Integer.valueOf(1), venice.eval("(min (int 1) (int 2) (int 3))"));
		assertEquals(Integer.valueOf(1), venice.eval("(min (int 1) 2 (int 3) 4.0)"));
		assertEquals(Integer.valueOf(1), venice.eval("(min (int 1) 2 (int 3) 4.0M)"));
		assertEquals(Long.valueOf(1), venice.eval("(min 1 2 (int 3))"));
		assertEquals(Double.valueOf(3.0D), venice.eval("(min (int 4) 5 3.0)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(min (int 4) 5 6.0 3.0M)"));

		// Long
		assertEquals(Long.valueOf(3), venice.eval("(min 3)"));
		assertEquals(Long.valueOf(1), venice.eval("(min 1 3)"));
		assertEquals(Long.valueOf(1), venice.eval("(min 1 2 3)"));
		assertEquals(Long.valueOf(1), venice.eval("(min 1 2 3 2.0)"));
		assertEquals(Long.valueOf(1), venice.eval("(min 1 2 3 2.0M)"));
		assertEquals(Double.valueOf(3.0D), venice.eval("(min 4 3.0)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(min 4 5.0 3.0M)"));

		// Double
		assertEquals(Double.valueOf(3.0D), venice.eval("(min 3.0)"));
		assertEquals(Double.valueOf(1.0D), venice.eval("(min 1.0 3.0)"));
		assertEquals(Double.valueOf(1.0D), venice.eval("(min 1.0 2.0 3.0)"));
		assertEquals(Double.valueOf(1.0D), venice.eval("(min 1.0 2.0 3.0 2)"));
		assertEquals(Double.valueOf(1.0D), venice.eval("(min 1.0 2.0 3.0 2.0M)"));
		assertEquals(Long.valueOf(3), venice.eval("(min 4.0 3)"));
		assertEquals(new BigDecimal("3.0"), venice.eval("(min 4.0 3.0M)"));

		// Decimal
		assertEquals(new BigDecimal("3.0"), venice.eval("(min 3.0M)"));
		assertEquals(new BigDecimal("1.0"), venice.eval("(min 1.0M 3.0M)"));
		assertEquals(new BigDecimal("1.0"), venice.eval("(min 1.0M 2.0M 3.0M)"));
		assertEquals(new BigDecimal("1.0"), venice.eval("(min 1.0M 2.0M 3.0M 2)"));
		assertEquals(new BigDecimal("1.0"), venice.eval("(min 1.0M 2.0M 3.0M 2.0)"));
		assertEquals(Long.valueOf(3), venice.eval("(min 4.0M 3)"));
		assertEquals(Double.valueOf(3.0D), venice.eval("(min 4.0M 3.0)"));
	}

	@Test
	public void test_mean() {
		final Venice venice = new Venice();

		// Integer
		assertEquals(3.0, venice.eval("(mean (int 3))"));
		assertEquals(2.0, venice.eval("(mean (int 1) (int 3))"));
		assertEquals(2.0, venice.eval("(mean (int 1) (int 2) (int 3))"));
		assertEquals(2.5, venice.eval("(mean (int 1) 2 (int 3) 4.0)"));
		assertEquals(2.5, venice.eval("(double (mean (int 1) 2 (int 3) 4.0M))"));
		assertEquals(2.0, venice.eval("(mean 1 2 (int 3))"));
		assertEquals(4.0, venice.eval("(mean (int 4) 5 3.0)"));
		assertEquals(4.5, venice.eval("(double (mean (int 4) 5 6.0 3.0M))"));

		// Long
		assertEquals(3.0, venice.eval("(mean 3)"));
		assertEquals(2.0, venice.eval("(mean 1 3)"));
		assertEquals(2.0, venice.eval("(mean 1 2 3)"));
		assertEquals(2.0, venice.eval("(mean 1 2 3 2.0)"));
		assertEquals(2.0, venice.eval("(double (mean 1 2 3 2.0M))"));
		assertEquals(3.5, venice.eval("(mean 4 3.0)"));
		assertEquals(4.0, venice.eval("(double (mean 4 5.0 3.0M))"));

		// Double
		assertEquals(3.0, venice.eval("(mean 3.0)"));
		assertEquals(2.0, venice.eval("(mean 1.0 3.0)"));
		assertEquals(2.0, venice.eval("(mean 1.0 2.0 3.0)"));
		assertEquals(2.0, venice.eval("(mean 1.0 2.0 3.0 2)"));
		assertEquals(2.0, venice.eval("(double (mean 1.0 2.0 3.0 2.0M))"));
		assertEquals(3.5, venice.eval("(mean 4.0 3)"));
		assertEquals(3.5, venice.eval("(double (mean 4.0 3.0M))"));

		// Decimal
		assertEquals(new BigDecimal("3.000"), venice.eval("(dec/scale(mean 3.0M) 3 :HALF_UP)"));
		assertEquals(new BigDecimal("2.000"), venice.eval("(dec/scale(mean 1.0M 3.0M) 3 :HALF_UP)"));
		assertEquals(new BigDecimal("2.000"), venice.eval("(dec/scale(mean 1.0M 2.0M 3.0M) 3 :HALF_UP)"));
		assertEquals(new BigDecimal("2.000"), venice.eval("(dec/scale(mean 1.0M 2.0M 3.0M 2) 3 :HALF_UP)"));
		assertEquals(new BigDecimal("2.000"), venice.eval("(dec/scale(mean 1.0M 2.0M 3.0M 2.0) 3 :HALF_UP)"));
		assertEquals(new BigDecimal("3.500"), venice.eval("(dec/scale(mean 4.0M 3) 3 :HALF_UP)"));
		assertEquals(new BigDecimal("3.500"), venice.eval("(dec/scale(mean 4.0M 3.0) 3 :HALF_UP)"));
	}

	@Test
	public void test_median() {
		final Venice venice = new Venice();

		// Integer
		assertEquals(3.0, venice.eval("(median 3I)"));
		assertEquals(3.0, venice.eval("(median 1I 3I 4I)"));
		assertEquals(2.5, venice.eval("(median 1I 4I)"));
		assertEquals(2.5, venice.eval("(median 1I 2I 3I 4I)"));

		// Long
		assertEquals(3.0, venice.eval("(median 3)"));
		assertEquals(3.0, venice.eval("(median 1 3 4)"));
		assertEquals(2.5, venice.eval("(median 1 4)"));
		assertEquals(2.5, venice.eval("(median 1 2 3 4)"));

		// Double
		assertEquals(3.0, venice.eval("(median 3.0)"));
		assertEquals(3.0, venice.eval("(median 1.0 3.0 4.0)"));
		assertEquals(2.5, venice.eval("(median 1.0 4.0)"));
		assertEquals(2.5, venice.eval("(median 1.0 2.0 3.0 4.0)"));

		// Decimal
		assertEquals(new BigDecimal("3.000"), venice.eval("(dec/scale(median 3.0M) 3 :HALF_UP)"));
		assertEquals(new BigDecimal("3.000"), venice.eval("(dec/scale(median 1.0M 3.0M 4.0M) 3 :HALF_UP)"));
		assertEquals(new BigDecimal("2.500"), venice.eval("(dec/scale(median 1.0M 4.0M) 3 :HALF_UP)"));
		assertEquals(new BigDecimal("2.500"), venice.eval("(dec/scale(median 1.0M 2.0M 3.0M 4.0M) 3 :HALF_UP)"));
	}

	@Test
	public void test_first_quartile() {
		final Venice venice = new Venice();

		// Integer
		assertEquals(6.0, venice.eval("(first-quartile 3I 7I 8I 5I 12I 14I 21I 13I 18I)"));
		assertEquals(7.0, venice.eval("(first-quartile 3I 7I 8I 5I 12I 14I 21I 15I 18I 14I)"));

		// Long
		assertEquals(6.0, venice.eval("(first-quartile 3 7 8 5 12 14 21 13 18)"));
		assertEquals(7.0, venice.eval("(first-quartile 3 7 8 5 12 14 21 15 18 14)"));

		// Double
		assertEquals(6.0, venice.eval("(first-quartile 3.0 7.0 8.0 5.0 12.0 14.0 21.0 13.0 18.0)"));
		assertEquals(7.0, venice.eval("(first-quartile 3.0 7.0 8.0 5.0 12.0 14.0 21.0 15.0 18.0 14.0)"));

		// Decimal
		assertEquals(new BigDecimal("6.000"), venice.eval("(dec/scale(first-quartile 3.0M 7.0M 8.0M 5.0M 12.0M 14.0M 21.0M 13.0M 18.0M) 3 :HALF_UP)"));
		assertEquals(new BigDecimal("7.000"), venice.eval("(dec/scale(first-quartile 3.0M 7.0M 8.0M 5.0M 12.0M 14.0M 21.0M 15.0M 18.0M 14.0M) 3 :HALF_UP)"));
	}

	@Test
	public void test_third_quartile() {
		final Venice venice = new Venice();

		// Integer
		assertEquals(16.0, venice.eval("(third-quartile 3I 7I 8I 5I 12I 14I 21I 13I 18I)"));
		assertEquals(15.0, venice.eval("(third-quartile 3I 7I 8I 5I 12I 14I 21I 15I 18I 14I)"));

		// Long
		assertEquals(16.0, venice.eval("(third-quartile 3 7 8 5 12 14 21 13 18)"));
		assertEquals(15.0, venice.eval("(third-quartile 3 7 8 5 12 14 21 15 18 14)"));

		// Double
		assertEquals(16.0, venice.eval("(third-quartile 3.0 7.0 8.0 5.0 12.0 14.0 21.0 13.0 18.0)"));
		assertEquals(15.0, venice.eval("(third-quartile 3.0 7.0 8.0 5.0 12.0 14.0 21.0 15.0 18.0 14.0)"));

		// Decimal
		assertEquals(new BigDecimal("16.000"), venice.eval("(dec/scale(third-quartile 3.0M 7.0M 8.0M 5.0M 12.0M 14.0M 21.0M 13.0M 18.0M) 3 :HALF_UP)"));
		assertEquals(new BigDecimal("15.000"), venice.eval("(dec/scale(third-quartile 3.0M 7.0M 8.0M 5.0M 12.0M 14.0M 21.0M 15.0M 18.0M 14.0M) 3 :HALF_UP)"));
	}

	@Test
	public void test_standard_deviation() {
		final Venice venice = new Venice();

		assertEquals(
				9.0553851381374, 
				(Double)venice.eval("(standard-deviation :sample 10 8 30 22 15)"), 
				0.0000000000001);
		
		assertEquals(
				8.0993826925266, 
				(Double)venice.eval("(standard-deviation :population 10 8 30 22 15)"), 
				0.0000000000001);

		
		assertEquals(
				9.0553851381374, 
				(Double)venice.eval("(standard-deviation :sample 10.0 8.0 30.0 22.0 15.0)"), 
				0.0000000000001);
		
		assertEquals(
				8.0993826925266, 
				(Double)venice.eval("(standard-deviation :population 10.0 8.0 30.0 22.0 15.0)"), 
				0.0000000000001);

		
		assertEquals(
				9.0553851381374, 
				(Double)venice.eval("(standard-deviation :sample  10.0M 8.0M 30.0M 22.0M 15.0M)"), 
				0.0000000000001);
		
		assertEquals(
				8.0993826925266, 
				(Double)venice.eval("(standard-deviation :population 10.0M 8.0M 30.0M 22.0M 15.0M)"), 
				0.0000000000001);
	}

	@Test
	public void test_mod() {
		final Venice venice = new Venice();

		assertEquals(Integer.valueOf(0), venice.eval("(mod (int 0) (int 6))"));
		assertEquals(Integer.valueOf(4), venice.eval("(mod (int 4) (int 6))"));
		assertEquals(Integer.valueOf(1), venice.eval("(mod (int 13) (int 6))"));

		assertEquals(Long.valueOf(0), venice.eval("(mod 0 6)"));
		assertEquals(Long.valueOf(4), venice.eval("(mod 4 6)"));
		assertEquals(Long.valueOf(1), venice.eval("(mod 13 6)"));
	}

	@Test
	public void test_mul() {
		final Venice venice = new Venice();

		assertEquals(Long.valueOf(1), venice.eval("(*)"));

		// Integer
		assertEquals(Integer.valueOf(3), venice.eval("(* (int 3))"));
		assertEquals(Integer.valueOf(2), venice.eval("(* (int 1) (int 2))"));
		assertEquals(Integer.valueOf(6), venice.eval("(* (int 1) (int 2) (int 3))"));
		assertEquals(Long.valueOf(6), venice.eval("(* (int 1) 2 (int 3))"));
		assertEquals(Double.valueOf(2.0D), venice.eval("(* (int 1) 2.0)"));
		assertEquals(new BigDecimal("2.0"), venice.eval("(* 1 2.0M)"));

		// Long
		assertEquals(Long.valueOf(3), venice.eval("(* 3)"));
		assertEquals(Long.valueOf(2), venice.eval("(* 1 2)"));
		assertEquals(Long.valueOf(6), venice.eval("(* 1 2 3)"));
		assertEquals(Double.valueOf(2.0D), venice.eval("(* 1 2.0)"));
		assertEquals(new BigDecimal("2.0"), venice.eval("(* 1 2.0M)"));

		// Double
		assertEquals(Double.valueOf(3.0D), venice.eval("(* 3.0)"));
		assertEquals(Double.valueOf(2.0D), venice.eval("(* 1.0 2.0)"));
		assertEquals(Double.valueOf(2.0D), venice.eval("(* 1.0 2)"));
		assertEquals(new BigDecimal("2.0"), venice.eval("(* 1.0 2.0M)"));

		// Decimal
		assertEquals(new BigDecimal("3.0"), venice.eval("(* 3.0M)"));
		assertEquals(new BigDecimal("2.00"), venice.eval("(* 1.0M 2.0M)"));
		assertEquals(new BigDecimal("2.0"), venice.eval("(* 1.0M 2)"));
		assertEquals(new BigDecimal("2.0"), venice.eval("(* 1.0M 2.0)"));
	}

	@Test
	public void test_neg() {
		final Venice venice = new Venice();

		// Integer
		assertTrue((Boolean)venice.eval("(neg? (int -3))"));
		assertFalse((Boolean)venice.eval("(neg? (int 0))"));
		assertFalse((Boolean)venice.eval("(neg? (int 3))"));

		// Long
		assertTrue((Boolean)venice.eval("(neg? -3)"));
		assertFalse((Boolean)venice.eval("(neg? 0)"));
		assertFalse((Boolean)venice.eval("(neg? 3)"));
	
		// Double
		assertTrue((Boolean)venice.eval("(neg? -3.0)"));
		assertFalse((Boolean)venice.eval("(neg? 0.0)"));
		assertFalse((Boolean)venice.eval("(neg? 3.0)"));

		// Decimal
		assertTrue((Boolean)venice.eval("(neg? -3.0M)"));
		assertFalse((Boolean)venice.eval("(neg? 0.0M)"));
		assertFalse((Boolean)venice.eval("(neg? 3.0M)"));
	}
	
	@Test
	public void test_odd_Q() {
		final Venice venice = new Venice();

		assertFalse((Boolean)venice.eval("(odd? (int 2))"));	
		assertFalse((Boolean)venice.eval("(odd? (int 0))"));	
		assertTrue((Boolean)venice.eval("(odd? (int 1))"));	

		assertFalse((Boolean)venice.eval("(odd? 2)"));	
		assertFalse((Boolean)venice.eval("(odd? 0)"));	
		assertTrue((Boolean)venice.eval("(odd? 1)"));	
		
		try {
			venice.eval("(odd? 1.0)");
			fail("Expected exception");
		}
		catch(RuntimeException ex) {
			assertTrue(true);
		}
		
		try {
			venice.eval("(odd? 1.0M)");
			fail("Expected exception");
		}
		catch(RuntimeException ex) {
			assertTrue(true);
		}
	}
	
	@Test
	public void test_rand_long() {
		final Venice venice = new Venice();

		for(int ii=0; ii<1000; ii++) {
			final long x = ((Long)venice.eval("(rand-long )")).longValue();			
			assertTrue(x >= 0);
		}

		for(int ii=0; ii<1000; ii++) {
			final long x = ((Long)venice.eval("(rand-long 10)")).longValue();			
			assertTrue(x >= 0 && x < 10);
		}
	}

	@Test
	public void test_rand_double() {
		final Venice venice = new Venice();

		for(int ii=0; ii<1000; ii++) {
			final double x = ((Double)venice.eval("(rand-double )")).doubleValue();			
			assertTrue(x >= 0.0 && x <= 1.0);
		}

		for(int ii=0; ii<1000; ii++) {
			final double x = ((Double)venice.eval("(rand-double 2.0)")).doubleValue();			
			assertTrue(x >= 0.0 && x <= 2.0);
		}
	}
	
	@Test
	public void test_range() {
		final Venice venice = new Venice();

		// Integer
		assertEquals("()", venice.eval("(str (range (int 10) (int 10)))"));
		assertEquals("(1I 2I 3I 4I 5I 6I 7I 8I 9I)", venice.eval("(str (range (int 1) (int 10)))"));
		assertEquals("(1I 2I 3I 4I 5I 6I 7I 8I 9I)", venice.eval("(str (range (int 1) (int 10) (int 1)))"));
		assertEquals("(-5I -4I -3I -2I -1I 0I 1I 2I 3I 4I)", venice.eval("(str (range (int -5) (int 5)))"));
		assertEquals("(-100I -90I -80I -70I -60I -50I -40I -30I -20I -10I 0I 10I 20I 30I 40I 50I 60I 70I 80I 90I)", venice.eval("(str (range (int -100) (int 100) (int 10)))"));
		assertEquals("(0I 2I)", venice.eval("(str (range (int 0) (int 4) (int 2)))"));
		assertEquals("(0I 2I 4I)", venice.eval("(str (range (int 0) (int 5) (int 2)))"));
		assertEquals("(0I 2I 4I)", venice.eval("(str (range (int 0) (int 6) (int 2)))"));
		assertEquals("(0I 2I 4I 6I)", venice.eval("(str (range (int 0) (int 7) (int 2)))"));
		assertEquals("(100I 90I 80I 70I 60I 50I 40I 30I 20I 10I)", venice.eval("(str (range (int 100) (int 0) (int -10)))"));
		assertEquals("(10I 9I 8I 7I 6I 5I 4I 3I 2I 1I 0I -1I -2I -3I -4I -5I -6I -7I -8I -9I)", venice.eval("(str (range (int 10) (int -10) (int -1)))"));

		// Long
		assertEquals("()", venice.eval("(str (range 10 10))"));
		assertEquals("(1 2 3 4 5 6 7 8 9)", venice.eval("(str (range 1 10))"));
		assertEquals("(1 2 3 4 5 6 7 8 9)", venice.eval("(str (range 1 10 1))"));
		assertEquals("(-5 -4 -3 -2 -1 0 1 2 3 4)", venice.eval("(str (range -5 5))"));
		assertEquals("(-100 -90 -80 -70 -60 -50 -40 -30 -20 -10 0 10 20 30 40 50 60 70 80 90)", venice.eval("(str (range -100 100 10))"));
		assertEquals("(0 2)", venice.eval("(str (range 0 4 2))"));
		assertEquals("(0 2 4)", venice.eval("(str (range 0 5 2))"));
		assertEquals("(0 2 4)", venice.eval("(str (range 0 6 2))"));
		assertEquals("(0 2 4 6)", venice.eval("(str (range 0 7 2))"));
		assertEquals("(100 90 80 70 60 50 40 30 20 10)", venice.eval("(str (range 100 0 -10))"));
		assertEquals("(10 9 8 7 6 5 4 3 2 1 0 -1 -2 -3 -4 -5 -6 -7 -8 -9)", venice.eval("(str (range 10 -10 -1))"));
		
		// Double
		assertEquals("()", venice.eval("(str (range 6.0 6.0))"));
		assertEquals("(1.0 2.0 3.0 4.0 5.0)", venice.eval("(str (range 1.0 6.0))"));
		assertEquals("(1.0 2.0 3.0 4.0 5.0)", venice.eval("(str (range 1.0 6.0 1.0))"));
		assertEquals("(1.0 1.5 2.0 2.5 3.0)", venice.eval("(str (range 1.0 3.1 0.5))"));
		
		// Decimal
		assertEquals("()", venice.eval("(str (range 6.0M 6.0M))"));
		assertEquals("(1.0M 2.0M 3.0M 4.0M 5.0M)", venice.eval("(str (range 1.0M 6.0M))"));
		assertEquals("(1.0M 2.0M 3.0M 4.0M 5.0M)", venice.eval("(str (range 1.0M 6.0M 1.0M))"));
		assertEquals("(1.0M 1.5M 2.0M 2.5M 3.0M)", venice.eval("(str (range 1.0M 3.1M 0.5M))"));
		
		// Mixed
		assertEquals("(1 2 3 4 5)", venice.eval("(str (range 1 6.0))"));
		assertEquals("(1.0 2.0 3.0 4.0 5.0)", venice.eval("(str (range 1.0 6))"));
		assertEquals("(1 1.5 2.0 2.5 3.0)", venice.eval("(str (range 1 3.1 0.5))"));
		assertEquals("(1.0 1.5 2.0 2.5 3.0 3.5)", venice.eval("(str (range 1.0 4 0.5))"));
	}

	@Test
	public void test_zero_Q() {
		final Venice venice = new Venice();

		// Integer
		assertFalse((Boolean)venice.eval("(zero? (int -3))"));
		assertTrue((Boolean)venice.eval("(zero? (int 0))"));
		assertFalse((Boolean)venice.eval("(zero? (int 3))"));

		// Long
		assertFalse((Boolean)venice.eval("(zero? -3)"));
		assertTrue((Boolean)venice.eval("(zero? 0)"));
		assertFalse((Boolean)venice.eval("(zero? 3)"));
	
		// Double
		assertFalse((Boolean)venice.eval("(zero? -3.0)"));
		assertTrue((Boolean)venice.eval("(zero? 0.0)"));
		assertFalse((Boolean)venice.eval("(zero? 3.0)"));

		// Decimal
		assertFalse((Boolean)venice.eval("(zero? -3.0M)"));
		assertTrue((Boolean)venice.eval("(zero? 0.0M)"));
		assertFalse((Boolean)venice.eval("(zero? 3.0M)"));
	}	

}
