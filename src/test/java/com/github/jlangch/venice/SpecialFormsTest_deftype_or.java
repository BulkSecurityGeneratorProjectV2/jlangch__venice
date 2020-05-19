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
package com.github.jlangch.venice;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


public class SpecialFormsTest_deftype_or {
		
	@Test
	public void test_deftype_or_1() {
		final Venice venice = new Venice();

		final String script =
				"(do                                                      \n" +
				"  (deftype-or :user/color :red :green :blue)             \n" +
				"  (doc :user/color))                                       ";

		assertNotNull(venice.eval(script));					
	}
	
	@Test
	public void test_deftype_or_2() {
		final Venice venice = new Venice();
	
		final String script =
				"(do                                                      \n" +
				"  (deftype-or :user/numbers 1 2 3)            			  \n" +
				"  (doc :user/color))                                       ";
	
		//assertEquals("\"foo@foo.org\"", venice.eval(script));					
	}

	
	@Test
	public void test_deftype_or_no_values() {
		final String script =
				"(do                                                      \n" +
				"  (deftype-or :user/numbers )               			  \n" +
				"  (doc :user/color))                                       ";
	
		assertThrows(VncException.class, () -> new Venice().eval(script));
	}

}
