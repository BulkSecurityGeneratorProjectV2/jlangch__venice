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
package com.github.jlangch.venice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.jlangch.venice.support.AuditEvent;
import com.github.jlangch.venice.support.AuditEventType;
import com.github.jlangch.venice.util.CapturingPrintStream;


public class VeniceTest {

	@Test
	public void evalWithIntegerAndLong() {
		final Venice venice = new Venice();
		
		assertEquals(7L, venice.eval("(+ 1 x)", Parameters.of("x", (short)6)));
		assertEquals(7L, venice.eval("(+ 1 x)", Parameters.of("x", 6)));
		assertEquals(7L, venice.eval("(+ 1 x)", Parameters.of("x", 6L)));
		assertEquals(7.2D, (Double)venice.eval("(+ 1 x)", Parameters.of("x", 6.2F)), 0.0001D);
		assertEquals(7.2D, (Double)venice.eval("(+ 1 x)", Parameters.of("x", 6.2D)), 0.0001D);
		assertEquals(247L, venice.eval("(+ 1 x)", Parameters.of("x", (byte)-10)));
	}

	@Test
	public void evalEnv() {
		final AuditEvent event = new AuditEvent(
										"su",
										2000L,
										AuditEventType.ALERT,
										"superuser",
										"webapp.started",
										"text");

		final Venice venice = new Venice();
		
		final Map<String,Object> symbols = Parameters.of("event", event);

		assertEquals("webapp.started", venice.eval("(get event :eventName)", symbols));
		assertEquals("superuser", venice.eval("(get event :eventKey)", symbols));
		assertEquals("ALERT", venice.eval("(get event :eventType)", symbols));
		assertEquals(2000L, venice.eval("(get event :elapsedTimeMillis)", symbols));
	}

	@Test
	public void evalWithObject() {
		final AuditEvent event1 = new AuditEvent(
										"su",
										2000L,
										AuditEventType.ALERT,
										"superuser",
										"webapp.started",
										"text");
		
		final AuditEvent event2 = new AuditEvent(
										"jd",
										2000L,
										AuditEventType.INFO,
										"john.doe",
										"login",
										"text");

		final Venice venice = new Venice();
		

		final String script = 
				"(or (match? (get event :eventName) \"webapp[.](started|stopped)\") " +
				"    (== (get event :eventKey) \"superuser\") " +
				"    (== (get event :eventType) \"ALERT\") " +
				")";       

		assertEquals(Boolean.TRUE, (Boolean)venice.eval(script, Parameters.of("event", event1)));

		assertEquals(Boolean.FALSE, (Boolean)venice.eval(script, Parameters.of("event", event2)));
	}

	@Test
	public void test_CapturingPrintStream() {
		final CapturingPrintStream ps = CapturingPrintStream.create();
		
		final Venice venice = new Venice();
		
		venice.eval("(print 10)", Parameters.of("*out*", ps));
		assertEquals("10", ps.getOutput());
		
		ps.reset();
		
		venice.eval("(print 10)", Parameters.of("*out*", null));
		assertEquals("", ps.getOutput());
	}

	@Test
	public void test_CapturingPrintStream_BelowLimit() {
		final CapturingPrintStream ps = CapturingPrintStream.create();
		
		final Venice venice = new Venice();
		
		venice.eval("(range 1 10000)", Parameters.of("*out*", ps));
		assertNotNull(ps.getOutput());
	}

	@Test
	public void test_CapturingPrintStream_Limit() {
		final CapturingPrintStream ps = CapturingPrintStream.create(10000);
		
		final Venice venice = new Venice();

		assertThrows(SecurityException.class, () -> {
			venice.eval("(map print (range 1 10000))", Parameters.of("*out*", ps));
		});
	}
}
