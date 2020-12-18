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
package com.github.jlangch.venice.impl.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;


public class ThreadPoolUtil {

	public static ThreadFactory createThreadFactory(
			final String poolNameFormat, 
			final AtomicLong threadPoolCounter,
			final boolean deamon
	) {
		return new ThreadFactory() {
			public Thread newThread(final Runnable runnable) {
				final Thread thread = new Thread(runnable);
				thread.setDaemon(deamon);
				thread.setName(String.format(
								poolNameFormat, 
								threadPoolCounter.getAndIncrement()));
				return thread;
			}
		};
	}

}
