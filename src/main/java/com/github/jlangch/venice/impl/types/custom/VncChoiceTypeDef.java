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
package com.github.jlangch.venice.impl.types.custom;

import com.github.jlangch.venice.impl.types.VncKeyword;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.collections.VncSet;


public class VncChoiceTypeDef {

	public VncChoiceTypeDef(
			final VncKeyword type,
			final VncSet choiceTypes,
			final VncSet choiceValues
	) {
		this.type = type;
		this.choiceTypes = choiceTypes;
		this.choiceValues = choiceValues;
	}


	public VncKeyword getType() {
		return type;
	}

	public boolean isChoice(final VncVal val) {
		return isChoiceType(val) || isChoiceValue(val);
	}

	public boolean isChoiceType(final VncVal val) {
		return choiceTypes.contains(val);
	}

	public boolean isChoiceValue(final VncVal val) {
		return choiceValues.contains(val);
	}

	public VncList values() {
		final VncList list = new VncList();
		list.addAllAtEnd(choiceTypes.toVncList());
		list.addAllAtEnd(choiceValues.toVncList());
		return list;
	}


	private final VncKeyword type;
	private final VncSet choiceTypes;
	private final VncSet choiceValues;
}
