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
package com.github.jlangch.venice.impl.reader;

public class CharacterReader {

	public CharacterReader(final String str) {
		this.str = str == null ? "" : str;
		this.length = str.length();
		this.pos = str.isEmpty() ? 1 : 0;
		this.chNext = str.isEmpty() ? EOF : str.charAt(0);
	}

	public int peek() {
		return chNext;
	}
	
	public void consume() {
		if (chNext != EOF) {
			pos++;
			
			if (chNext == LF) {
				lineNr++;
				columnNr = 1;
			}
			else {
				columnNr++;
			}
			
			chNext = pos >= length ? EOF : str.charAt(pos);
		}
	}

	public boolean eof() {
		return pos >= length;
	}

	public int getPos() {
		return pos;
	}

	public int getLineNumber() {
		return lineNr;
	}
	
	public int getColumnNumber() {
		return columnNr;
	}
	
	@Override
	public String toString() {
		return String.format(
					"(%d,%d) %s at pos %d", 
					lineNr, 
					columnNr, 
					toDisplay(peek()), 
					pos);
	}

	private String toDisplay(final int ch) {
		switch(ch) {
			case EOF:  return "EOF";
			case TAB:  return "TAB";
			case CR:   return "CR";
			case LF:   return "LF";
			default:   return ch < 32 
							? String.format("%03x", ch) 
							: "'" + String.valueOf((char)ch) + "'";
		}
	}
	
	
	private static final int LF  = (int)'\n';
	private static final int CR  = (int)'\r';
	private static final int TAB = (int)'\t';
	private static final int EOF = -1;
	
	private final String str;
	private final int length;
	
	private int chNext;
	private int pos;
	private int lineNr = 1;
	private int columnNr = 1;
}
