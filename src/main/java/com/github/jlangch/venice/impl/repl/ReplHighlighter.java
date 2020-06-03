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
package com.github.jlangch.venice.impl.repl;

import java.util.regex.Pattern;

import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;

import com.github.jlangch.venice.impl.ansi.AnsiColorTheme;
import com.github.jlangch.venice.impl.ansi.AnsiColorThemes;
import com.github.jlangch.venice.impl.reader.HighlightItem;
import com.github.jlangch.venice.impl.reader.HighlightParser;
import com.github.jlangch.venice.impl.repl.ReplConfig.ColorMode;


public class ReplHighlighter implements Highlighter {

	public ReplHighlighter(final ReplConfig config) {
		this.theme = getAnsiColorTheme(config.getColorMode());
	}
	
	public void enable(final boolean val) {
		enabled = val;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public AttributedString highlight(
			final LineReader reader, 
			final String buffer
	) {
		final AttributedStringBuilder sb = new AttributedStringBuilder();
		
		if (enabled && !isReplCommand(buffer)) {
			HighlightParser
				.parse(buffer)
				.forEach(it -> sb.ansiAppend(highlight(it)));		
		}
		else {
			sb.append(buffer);
		}
		
		return sb.toAttributedString();
	}

	@Override
	public void setErrorPattern(final Pattern errorPattern) {
	}

	@Override
	public void setErrorIndex(final int errorIndex) {
	}

	
	private String highlight(final HighlightItem item) {
		return theme == null 
				? item.getForm() 
				: theme.style(item.getForm(), item.getClazz());
	}
	
	private AnsiColorTheme getAnsiColorTheme(final ColorMode mode) {
		switch(mode) {
			case Light: return AnsiColorThemes.getLightTheme();
			case Dark:  return AnsiColorThemes.getDarkTheme();
			case None:  return null;
			default:    return null;
		}
	}

	private boolean isReplCommand(final String buffer) {
		return buffer.startsWith("!");
	}

	
	
	private final AnsiColorTheme theme;
	private boolean enabled = true;
}
