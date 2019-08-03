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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.jlangch.venice.JavaValueException;
import com.github.jlangch.venice.Parameters;
import com.github.jlangch.venice.Venice;
import com.github.jlangch.venice.util.CapturingPrintStream;


public class ConcurrencyFunctionsTest {

	@Test
	public void test_atom() {
		final Venice venice = new Venice();

		final String script = 
				"(do                          \n" +
				"   (def x (atom 100))        \n" +
				"   @x)                        ";

		final Object result = venice.eval(script);
		
		assertEquals(100L, result);
	}

	@Test
	public void test_atom_reset() {
		final Venice venice = new Venice();

		final String script = 
				"(do                          \n" +
				"   (def x (atom 100))        \n" +
				"   (reset! x 200)            \n" +
				"   @x)                        ";

		final Object result = venice.eval(script);
		
		assertEquals(200L, result);
	}

	@Test
	public void test_atom_swap() {
		final Venice venice = new Venice();

		final String script = 
				"(do                          \n" +
				"   (def x (atom 100))        \n" +
				"   (swap! x inc)             \n" +
				"   @x)                        ";

		final Object result = venice.eval(script);
		
		assertEquals(101L, result);
	}

	@Test
	public void test_atom_compareAndSet() {
		final Venice venice = new Venice();

		final String script = 
				"(do                               \n" +
				"   (def x (atom 2))               \n" +
				"   (compare-and-set! x 3 4)       \n" +
				"   (compare-and-set! x 2 4)       \n" +
				"   @x)                              ";

		final Object result = venice.eval(script);
		
		assertEquals(4L, result);
	}


	@Test
	public void test_volatile() {
		final Venice venice = new Venice();

		final String script = 
				"(do                          \n" +
				"   (def x (volatile 100))    \n" +
				"   @x)                        ";

		final Object result = venice.eval(script);
		
		assertEquals(100L, result);
	}

	@Test
	public void test_volatile_reset() {
		final Venice venice = new Venice();

		final String script = 
				"(do                          \n" +
				"   (def x (volatile 100))    \n" +
				"   (reset! x 200)            \n" +
				"   @x)                        ";

		final Object result = venice.eval(script);
		
		assertEquals(200L, result);
	}

	@Test
	public void test_volatile_swap() {
		final Venice venice = new Venice();

		final String script = 
				"(do                          \n" +
				"   (def x (volatile 100))    \n" +
				"   (swap! x inc)             \n" +
				"   @x)                        ";

		final Object result = venice.eval(script);
		
		assertEquals(101L, result);
	}

	@Test
	public void test_agent() {
		final Venice venice = new Venice();

		final String script = 
				"(do                          \n" +
				"   (def x (agent 100))       \n" +
				"   (deref x))                  ";

		final Object result = venice.eval(script);
		
		assertEquals(Long.valueOf(100), result);
	}

	@Test
	public void test_agent_restart() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                 \n" +
				"   (def x (agent 100))              \n" +
				"   (restart-agent x 200)            \n" +
				"   (deref x))                         ";

		final Object result = venice.eval(script);
		
		assertEquals(Long.valueOf(200), result);
	}

	@Test
	public void test_agent_send() {
		final Venice venice = new Venice();

		final String script = 
				"(do                         \n" +
				"   (def x (agent 100))      \n" +
				"   (send x + 5)             \n" +
				"   (sleep 200)              \n" +
				"   (deref x))                 ";

		final Object result = venice.eval(script);
		
		assertEquals(Long.valueOf(105), result);
	}

	@Test
	public void test_agent_send_order_1() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                               \n" +
				"   (def a1 (agent 2))                             \n" +
				"   (def a2 (agent 3))                             \n" +
				"   (send a1 (fn [x] (do (sleep 500) (+ x 10))))   \n" +
				"   (send a2 (fn [x] (do (sleep 400) (+ x 10))))   \n" +
				"   (send a1 (fn [x] (do (sleep 100) (* x 2))))    \n" +
				"   (send a2 (fn [x] (do (sleep 100) (* x 2))))    \n" +
				"   (sleep 800)                                    \n" +
				"   (str [@a1 @a2]))                                 ";

		assertEquals("[24 26]", venice.eval(script));
	}

	@Test
	public void test_agent_send_order_2() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                               \n" +
				"   (def a1 (agent 2))                             \n" +
				"   (def a2 (agent 3))                             \n" +
				"   (send a1 (fn [x] (do (sleep 500) (+ x 10))))   \n" +
				"   (send a2 (fn [x] (do (sleep 400) (+ x 10))))   \n" +
				"   (send a1 (fn [x] (do (sleep 100) (* x 2))))    \n" +
				"   (send a2 (fn [x] (do (sleep 100) (* x 2))))    \n" +
				"   (await-for 1000 a1 a2)                         \n" +
				"   (str [@a1 @a2]))                                 ";

		assertEquals("[24 26]", venice.eval(script));
	}

	@Test
	public void test_agent_send_off() {
		final Venice venice = new Venice();

		final String script = 
				"(do                         \n" +
				"   (def x (agent 100))      \n" +
				"   (send-off x + 5)         \n" +
				"   (sleep 100)              \n" +
				"   (deref x))                 ";

		final Object result = venice.eval(script);
		
		assertEquals(Long.valueOf(105), result);
	}

	@Test
	public void test_agent_watch() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                                                     \n" +
				"   (defn watcher [key ref old new]                                      \n" +
				"         (println \"watcher: \" key \", old:\" old \", new:\" new ))    \n" +
				"   (def x (agent 100))                                                  \n" +
				"   (add-watch x :test watcher)                                          \n" +
				"   (send x + 5)                                                         \n" +
				"   (sleep 100)                                                          \n" +
				"   (deref x))                                                             ";

		final Object result = venice.eval(script);
		
		assertEquals(Long.valueOf(105), result);
	}

	@Test
	public void test_agent_error_mode_1() {
		final Venice venice = new Venice();

		final String script = 
				"(do                              \n" +
				"   (def x (agent 100))           \n" +
				"   (str (agent-error-mode x)))     ";

		assertEquals(":continue", venice.eval(script));
	}

	@Test
	public void test_agent_error_mode_2() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                              \n" +
				"   (def x (agent 100 :error-mode :continue))     \n" +
				"   (str (agent-error-mode x)))                     ";

		assertEquals(":continue", venice.eval(script));
	}

	@Test
	public void test_agent_error_mode_3() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                              \n" +
				"   (def x (agent 100 :error-mode :fail))         \n" +
				"   (str (agent-error-mode x)))                     ";

		assertEquals(":fail", venice.eval(script));
	}

	@Test
	public void test_agent_error_1() {
		final Venice venice = new Venice();

		// Agents as message relay
		
		final String script = 
				"(do                                              \n" +
				"   (def logger (agent (list)))                   \n" +
				"                                                 \n" +
				"   (defn log* [msg]                              \n" +
				"      (send logger #(cons %2 %1) msg))           \n" +
				"                                                 \n" +
				"   (def x (agent 100))                           \n" +
				"                                                 \n" +
				"   (defn err-handler-fn [ag ex]                  \n" +
				"      (log* (str \"error occured: \"             \n" +
				"                (:message ex)                    \n" +
				"                \" and we still have value \"    \n" +
				"                @ag)))                           \n" +
				"                                                 \n" +
				"   (set-error-handler! x err-handler-fn)         \n" +
				"   (send x (fn [x] (/ x 0)))                     \n" +
				"   (sleep 500)                                   \n" +
				"   (with-out-str (print @logger)))                 ";

		assertEquals(
				"(error occured: / by zero and we still have value 100)", 
				venice.eval(script));
	}

	@Test
	public void test_agent_error_2() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                              \n" +
				"   (def x (agent 100 :error-mode :fail))         \n" +
				"                                                 \n" +
				"   (send x (fn [x] (/ x 0)))                     \n" +
				"   (sleep 500)                                   \n" +
				"   (:message (agent-error x)))                     ";

		assertEquals("/ by zero", venice.eval(script));
	}

	@Test
	public void test_agent_await_for() {
		final Venice venice = new Venice();
		
		final String script = 
				"(do                            \n" +
				"   (def x1 (agent 100))        \n" +
				"   (def x2 (agent 100))        \n" +
				"   (await-for 500 x1 x2))        ";

		assertTrue((Boolean)venice.eval(script));
	}

	@Test
	public void test_agent_relay() {
		final Venice venice = new Venice();

		// Agents as message relay
		
		final String script = 
				"(do                                                                         \n" +
				"   (def logger (agent (list)))                                              \n" +
				"                                                                            \n" +
				"   (defn log* [msg]                                                         \n" +
				"      (send logger #(cons %2 %1) msg))                                      \n" +
				"                                                                            \n" +
				"   (defn create-relay [n]                                                   \n" +
				"      (reduce (fn [prev _] (agent prev)) nil (range 0 n)))                  \n" +
				"                                                                            \n" +
				"   (defn process [relay msg]                                                \n" +
				"      (let [relay-fn (fn [next-actor hop msg]                               \n" +
				"                         (if next-actor                                     \n" +
				"                            (do                                             \n" +
				"                               (log* (list hop msg))                        \n" +
				"                               (send next-actor relay-fn (inc hop) msg)     \n" +
				"                               @next-actor)                                 \n" +
				"                            (log* \"finished relay\") ))]                   \n" +
				"         (send relay relay-fn 0 msg)))                                      \n" +
				"                                                                            \n" +
				"   (process (create-relay 5) \"hello\")                                     \n" +
				"   (sleep 500)                                                              \n" +
				"   (with-out-str (print @logger)))                                            ";

		assertEquals(
				"(finished relay (3 hello) (2 hello) (1 hello) (0 hello))", 
				venice.eval(script));
	}

	@Test
	public void test_agent_logger() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                                                         \n" +
				"   (import :java.io.PrintWriter)                                            \n" +
				"   (import :java.io.BufferedWriter)                                         \n" +
				"                                                                            \n" +
				"   (let [pwtr (. :PrintWriter :new *out* true)                              \n" +
				"         wtr (agent (. :BufferedWriter :new pwtr))]                         \n" +
				"      (defn log* [msg]                                                      \n" +
				"	      (let [write (fn [out msg] (do (. out :write msg) out))]            \n" +
				"	         (send wtr write msg)))                                          \n" +
				"	   (defn log-close []                                                    \n" +
				"	         (do                                                             \n" +
				"	            (send wtr (fn [out] (do (. out :flush) (. out :close) out))) \n" +
				"	            (await-for 2000 wtr))))                                      \n" +
				"                                                                            \n" +
				"	(log* \"test\n\")                                                        \n" +
				"	(log* \"another line\n\")                                                \n" +
				"	(log-close)                                                              \n" +
				"	(println \"DONE.\"))                                                 ";

		venice.eval(script);
	}
	
	@Test
	public void test_agent_thread_local() {
		final Venice venice = new Venice();

		final String script = 
				"(do                            \n" +
				"   (defn add [a b] (+ a b z))  \n" +
				"   (def x (agent 100))         \n" +
				"   (binding [z 10]             \n" +
				"     (send x add 5)            \n" +
				"     (sleep 200)               \n" +
				"     (deref x)))                 ";

		final Object result = venice.eval(script);
		
		assertEquals(Long.valueOf(115), result);
	}

	@Test
	public void test_delay() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                                \n" +
				"   (def x (delay (println \"working...\") 100))    \n" +
				"   (println \"start\")                             \n" +
				"   (deref x)                                       \n" +
				"   (deref x)                                       \n" +
				"   (deref x)                                       \n" +
				"   (println \"end\")                               \n" +
				"   (deref x))                                     ";

		final CapturingPrintStream ps = CapturingPrintStream.create();

		final Object result = venice.eval(script, Parameters.of("*out*", ps));
		
		assertEquals(Long.valueOf(100), result);
		assertEquals("start\nworking...\nend\n", ps.getOutput());
	}

	@Test
	public void test_delay_realized_1() {
		final Venice venice = new Venice();

		final String script = 
				"(do                       \n" +
				"   (def x (delay 100))    \n" +
				"   (realized? x))           ";
		
		assertFalse((Boolean)venice.eval(script));
	}

	@Test
	public void test_delay_realized_2() {
		final Venice venice = new Venice();

		final String script = 
				"(do                       \n" +
				"   (def x (delay 100))    \n" +
				"   @x                     \n" +
				"   (realized? x))           ";
		
		assertTrue((Boolean)venice.eval(script));
	}

	@Test
	public void test_promise() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                        " +
				"   (def p (promise))                       " +
				"   (def task (fn []                        " +
				"                 (do                       " +
				"                    (sleep 500)            " +
				"                    (deliver p 123))))     " +
				"                                           " +
				"   (future task)                           " +
				"   (deref p))                              " +
				") ";

		assertEquals(Long.valueOf(123), venice.eval(script));
	}

	@Test
	public void test_future_deref_1() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                             " +
				"   (let [f (future (fn [] {:a 100}))]           " +
				"        @f)                                     " +
				") ";

		assertEquals("{:a 100}", venice.eval("(str " + script + ")"));
	}

	@Test
	public void test_future_deref_2() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                             " +
				"   (let [f (future (fn [] {:a 100}))]           " +
				"        (conj @f {:c 3}))                       " +
				") ";

		assertEquals("{:a 100 :c 3}", venice.eval("(str " + script + ")"));
	}

	@Test
	public void test_future_1() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                             " +
				"   (def wait (fn [] (do (sleep 500) {:a 100}))) " +
				"                                                " +
				"   (let [f (future wait)]                       " +
				"        (deref f))                              " +
				") ";

		assertEquals("{:a 100}", venice.eval("(str " + script + ")"));
	}

	@Test
	public void test_future_2() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                        " +
				"   (def wait (fn [] (do (sleep 500) 100))) " +
				"                                           " +
				"   (let [f (future wait)]                  " +
				"        (deref f 700 :timeout))            " +
				") ";

		assertEquals(Long.valueOf(100), venice.eval(script));
	}

	@Test
	public void test_future_exception() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                              \n" +
				"   (def wait (fn [] (do (sleep 500) (throw 1)))) \n" +
				"                                                 \n" +
				"   (let [f (future wait)]                        \n" +
				"        (deref f))                               \n" +
				") ";

		assertThrows(JavaValueException.class, () -> venice.eval(script));
	}

	@Test
	public void test_future_timeout() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                        " +
				"   (def wait (fn [] (do (sleep 500) 100))) " +
				"                                           " +
				"   (let [f (future wait)]                  " +
				"        (deref f 300 :timeout))            " +
				") ";

		assertEquals("timeout", venice.eval(script));
	}

	@Test
	public void test_future_thread_local_inherited() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                                      \n" +
				"   (assoc (thread-local) :a 10 :b 20)                    \n" +
				"   (assoc (thread-local) :a 11)                          \n" +
				"   (let [f (future (fn [] (get (thread-local) :a)))]     \n" +
				"        @f)                                              \n" +
				") ";

		assertEquals(11L, venice.eval(script));
	}

	@Test
	public void test_future_thread_local_parent_untouched() {
		final Venice venice = new Venice();

		final String script = 
				"(do                                                        \n" +
				"   (assoc (thread-local) :a 10 :b 20)                      \n" +
				"   (assoc (thread-local) :a 11)                            \n" +
				"   [ (let [f (future (fn []                                \n" +
				"                         (assoc (thread-local) :a 90)      \n" +
				"                         (get (thread-local) :a)))]        \n" +
				"          @f)                                              \n" +
				"     (get (thread-local) :a) ]                             \n" +
				") ";

		assertEquals("[90 11]", venice.eval("(str " + script + ")"));
	}
	
	@Test
	public void test_thread_id() {
		final Venice venice = new Venice();

		assertNotNull((Long)venice.eval("(thread-id)"));
	}
	
	@Test
	public void test_thread_name() {
		final Venice venice = new Venice();

		assertNotNull((String)venice.eval("(thread-name)"));
	}

}
