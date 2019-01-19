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
package com.github.jlangch.venice.impl.types.collections;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.github.jlangch.venice.impl.types.VncVal;


public abstract class VncSet extends VncCollection {

	public VncSet(VncVal meta) {
		super(meta);
	}
	
	@Override
	public abstract VncSet copy();
	
	@Override
	public abstract VncSet empty();

	public abstract VncSet withValues(Collection<? extends VncVal> replaceVals);
	
	public abstract VncSet withValues(Collection<? extends VncVal> replaceVals, VncVal meta);

	@Override
	public abstract VncSet withMeta(VncVal meta);
	

	public abstract VncSet add(VncVal val);
	
	public abstract VncSet addAll(VncSet val);
	
	public abstract VncSet addAll(VncSequence val);

	public abstract VncSet remove(VncVal val);

	public abstract VncSet removeAll(VncSet val);

	public abstract VncSet removeAll(VncSequence val);
	
	public abstract boolean contains(VncVal val);
	
	public abstract Set<VncVal> getSet();
	
	public abstract List<VncVal> getList();


    private static final long serialVersionUID = -1848883965231344442L;
}