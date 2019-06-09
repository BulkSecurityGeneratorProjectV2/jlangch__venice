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
package com.github.jlangch.venice.impl;

import static com.github.jlangch.venice.impl.VeniceClasspath.getVeniceBasePath;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.util.ClassPathResource;


public class ModuleLoader {
	
	public static String load(final String module) {
		if (!VALID_MODULES.contains(module)) {
			throw new VncException(String.format(
					"The Venice core module '%s' does not exist",
					module));
		}
		
		final String name = module + ".venice";

		try {
			return modules.computeIfAbsent(
					name, 
					k -> new ClassPathResource(getVeniceBasePath() + k)
								.getResourceAsString("UTF-8"));
		}
		catch(Exception ex) {
			throw new VncException(String.format(
					"Failed to load Venice core module '%s'", name), 
					ex);
		}
	}

	public static boolean loaded(final String module) {
		return modules.containsKey(module);
	}

	public static String loadVeniceResource(final String resource) {
		// For security reasons just allow to load venice scripts!
		if (!resource.endsWith(".venice")) {
			throw new VncException(String.format(
					"Must not load other than Venice (*.venice) resources from "
						+ "classpath. Resource: '%s'"));
		}
		
		try {
			return modules.computeIfAbsent(
					resource, 
					k -> new ClassPathResource(resource).getResourceAsString("UTF-8"));
		}
		catch(Exception ex) {
			throw new VncException(String.format(
					"Failed to load Venice resource '%s'", resource), 
					ex);
		}
	}
		
		
	private static final Map<String,String> modules = new HashMap<>();
	
	public static final Set<String> VALID_MODULES = 
			Collections.unmodifiableSet(
				new HashSet<>(
					Arrays.asList(
							"core", "http", "jackson", "logger", 
							"math", "webdav", "xchart", "test",
							"tomcat", "ring", "maven" , "kira",
							"xml", "crypt")));
}
