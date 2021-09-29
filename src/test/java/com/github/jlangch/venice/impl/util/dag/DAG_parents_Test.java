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
package com.github.jlangch.venice.impl.util.dag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class DAG_parents_Test {

	@Test
	public void test_parents_1() {
		final DAG<String> dag = new DAG<>();
		
		dag.addEdge("A", "B");      //	     A
		dag.addEdge("A", "C");      //	    / \ 
		dag.addEdge("B", "D");      //     B   C
		dag.addEdge("C", "D");      //      \ /
		dag.addEdge("D", "E");      //       D 
		dag.addEdge("D", "F");      //      / \
		dag.update();               //     E   F

		assertEquals("", String.join(" ", dag.parents("A")));

		assertEquals("A", String.join(" ", dag.parents("B")));

		assertEquals("A", String.join(" ", dag.parents("C")));

		assertEquals("B C A", String.join(" ", dag.parents("D")));

		assertEquals("D B C A", String.join(" ", dag.parents("E")));

		assertEquals("D B C A", String.join(" ", dag.parents("F")));
	}

	@Test
	public void test_parents_2() {
		final DAG<String> dag = new DAG<>();
		
		dag.addEdge("A", "B");      //	     A
		dag.addEdge("A", "C");      //	    / \ 
		dag.addEdge("B", "D");      //     B   C
		dag.addEdge("C", "D");      //      \ / \
		dag.addEdge("D", "E");      //       D   |
		dag.addEdge("D", "F");      //      / \ /
		dag.addEdge("C", "F");      //     E   F
		dag.update();

		assertEquals("", String.join(" ", dag.parents("A")));

		assertEquals("A", String.join(" ", dag.parents("B")));

		assertEquals("A", String.join(" ", dag.parents("C")));

		assertEquals("B C A", String.join(" ", dag.parents("D")));

		assertEquals("D B C A", String.join(" ", dag.parents("E")));

		assertEquals("D C B A", String.join(" ", dag.parents("F")));
	}

	@Test
	public void test_immediate_parents() {
		final DAG<String> dag = new DAG<>();
		
		dag.addEdge("A", "B");      //	     A
		dag.addEdge("A", "C");      //	    / \ 
		dag.addEdge("B", "D");      //     B   C
		dag.addEdge("C", "D");      //      \ / \
		dag.addEdge("D", "E");      //       D   |
		dag.addEdge("D", "F");      //      / \ /
		dag.addEdge("C", "F");      //     E   F
		dag.update();
		
		assertEquals("", String.join(" ", dag.immediateParents("A")));
		
		assertEquals("A", String.join(" ", dag.immediateParents("B")));
		
		assertEquals("A", String.join(" ", dag.immediateParents("C")));
		
		assertEquals("B C", String.join(" ", dag.immediateParents("D")));
		
		assertEquals("D", String.join(" ", dag.immediateParents("E")));
		
		assertEquals("D C", String.join(" ", dag.immediateParents("F")));
	}

}
