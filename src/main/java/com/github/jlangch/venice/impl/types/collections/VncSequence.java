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
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.github.jlangch.venice.impl.types.Constants;
import com.github.jlangch.venice.impl.types.VncVal;


public abstract class VncSequence extends VncCollection {

	public VncSequence(VncVal meta) {
		super(meta);
	}

	@Override
	public abstract VncSequence copy();
	
	@Override
	public abstract VncSequence withMeta(VncVal meta);
	
	public abstract VncSequence empty();
	
	public abstract VncSequence withValues(final Collection<? extends VncVal> replaceVals);

	public abstract List<VncVal> getList();
	
	public abstract VncVal nth(int idx);

	public abstract VncVal nthOrDefault(int idx, VncVal defaultVal);

	public VncVal first() {
		return nthOrDefault(0, Constants.Nil);
	}

	public VncVal second() {
		return nthOrDefault(1, Constants.Nil);
	}

	public VncVal third() {
		return nthOrDefault(2, Constants.Nil);
	}

	public VncVal last() {
		return nthOrDefault(size()-1, Constants.Nil);
	}

	public abstract VncSequence rest();

	public abstract VncSequence slice(int start, int end);
	
	public abstract VncSequence slice(int start);

	public abstract VncSequence setAt(int idx, VncVal val);

	public abstract VncSequence addAtStart(VncVal val) ;
	
	public abstract VncSequence addAllAtStart(VncSequence list);
	
	public abstract VncSequence addAtEnd(VncVal val);
	
	public abstract VncSequence addAllAtEnd(VncSequence list);
	
	public abstract VncSequence removeAt(int idx);

	public abstract void forEach(Consumer<? super VncVal> action);

	public  Stream<VncVal> stream() {
		return getList().stream();
	}


	private static final long serialVersionUID = -1848883965231344442L;
}
