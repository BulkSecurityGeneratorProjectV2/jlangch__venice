/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2018 Venice
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.util.ClassPathResource;


public class ModuleLoader {
	
	public static String load(final String module) {
		if (!validModules.contains(module)) {
			throw new VncException(String.format(
					"The Venice core module '%s' does not exist",
					module));
		}
		
		final String name = module + ".venice";

		try {
			return modules.computeIfAbsent(
					name, 
					k -> new ClassPathResource("com/github/jlangch/venice/" + k)
								.getResourceAsString("UTF-8"));
		}
		catch(Exception ex) {
			throw new VncException(String.format("Failed to load Venice core module '%s'", name), ex);
		}
	}

		
		
	private static final Map<String,String> modules = new HashMap<>();
	
	private static final Set<String> validModules = 
			new HashSet<>(Arrays.asList("core", "json", "protocol"));
}
