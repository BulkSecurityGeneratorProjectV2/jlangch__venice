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
package com.github.jlangch.venice.impl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class StringUtil {
	
	/**
	 * Splits a text into lines
	 * 
	 * @param text	a string
	 * 
	 * @return the lines (maybe empty if the text was <tt>null</tt> or empty
	 */
	public static List<String> splitIntoLines(final String text) {
		if (text == null || text.isEmpty()) {
			return new ArrayList<>();
		}
		else {
			try(final BufferedReader br = new BufferedReader(new StringReader(text))) {
				return br.lines().collect(Collectors.toList());
			}
			catch(IOException | RuntimeException ex) {
				throw new RuntimeException("Failed to split text into lines", ex);
			}
		}
	}


	/**
	 * Escapes a text
	 * 
	 * Backspace is replaced with \b
	 * Form feed is replaced with \f
	 * Newline is replaced with \n
	 * Carriage return is replaced with \r
	 * Tab is replaced with \t
	 * Double quote is replaced with \"
	 * Backslash is replaced with \\
	 *
	 * @param text text to escape
	 * @return the escaped text
	 */
	public static String escape(final String text) {
		if (text == null) {
			return text;
		}
		
		final StringBuilder sb = new StringBuilder();
		
		for(char c : text.toCharArray()) {
			switch(c) {
				case '\n': sb.append('\\').append('n'); break;
				case '\r': sb.append('\\').append('r'); break;
				case '\t': sb.append('\\').append('t'); break;
				case '"':  sb.append('\\').append('"'); break;
				case '\\': sb.append('\\').append('\\'); break;
				default:   sb.append(c); break;
			}
		}
		
		return sb.toString();
	}
		
	public static int indexNotOf(final String text, final String searchChars, final int startPos) {
		if (text == null) {
			throw new IllegalArgumentException("A text must not be null");
		}
		if (isEmpty(searchChars)) {
			throw new IllegalArgumentException("A searchChars must not be empty");
		}
		if (startPos < 0) {
			throw new IllegalArgumentException("A startPos must not be negativ");
		}
		
		
		if (startPos >= text.length()) {
			return -1;
		}
	
		final Set<Character> chars = searchChars.chars().mapToObj(c -> (char)c).collect(Collectors.toSet());

		int pos = startPos;
		while(pos < text.length()) {
			if (chars.contains(text.charAt(pos))) {
				pos++;
			}
			else {
				return pos;
			}
		}
		
		return -1;
	}
	
	public static String stripIndent(final String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}
		
		final List<String> lines = StringUtil.splitIntoLines(text);
		final String first = lines.get(0);
		
		final int pos = StringUtil.indexNotOf(first, " \t", 0);
		if (pos < 0) {
			return text;
		}
		else {
			final String indent = first.substring(0, pos);				
			return lines
					.stream()
					.map(s -> s.startsWith(indent) ? s.substring(pos) : s)
					.collect(Collectors.joining("\n"));
		}
	}

	
	public static String stripMargin(final String text, final char margin) {
		if (text == null || text.isEmpty()) {
			return text;
		}
		
		final List<String> lines = StringUtil.splitIntoLines(text);
		return stripIndent(
				lines
					.stream()
					.map(s -> { int pos = s.indexOf(margin); return pos < 0 ? s : s.substring(pos+1); })
					.collect(Collectors.joining("\n")));
	}
	
	/**
	 * Truncates a string.
	 * 
	 * @param text a string
	 * @param maxLen the max length of the truncated string (truncation marker included)
	 * @param truncationMarker a truncation marker
	 * @return the truncated string
	 */
	public static String truncate(
			final String text, 
			final int maxLen, 
			final String truncationMarker
	) {
		if (truncationMarker == null) {
			throw new IllegalArgumentException("A truncationMarker must not be null");
		}
		
		int lenTruncationMarker = truncationMarker.length();
		
		if (maxLen <= lenTruncationMarker){
			throw new IllegalArgumentException(
					"A maxLen must greater than the length of the truncation marker");
		}
		
		if (text == null || text.length() <= maxLen) {
			return text;
		}
		
		return text.substring(0, maxLen - lenTruncationMarker) + truncationMarker;
	}
	
	public static String repeat(final String s, final int times) {
		if (s == null) {
			throw new IllegalArgumentException("s must not be null");
		}
		if (times < 0) {
			throw new IllegalArgumentException("A times must not be negative");
		}

		final StringBuilder sb = new StringBuilder();
		for(int ii=0; ii<times; ii++) sb.append(s);
		return sb.toString();
	}
	
	public static String repeat(final char c, final int times) {
		if (times < 0) {
			throw new IllegalArgumentException("A times must not be negative");
		}

		return new String(new char[times]).replace('\0', c);
	}

	public static boolean isEmpty(final String s){
		return s == null || s.length() == 0;
	}
	
	public static boolean isNotEmpty(final String s){
		return !isEmpty(s);
	}

	public static boolean isBlank(final String s){
		return s == null || s.length() == 0 || s.trim().length() == 0;
	}

	public static boolean isNotBlank(final String s){
		return !isBlank(s);
	}
	
	public static boolean isAsciiAlphaUpper(final char ch){
		return ch >= 'A' && ch <= 'Z';
	}
	
	public static String removeStart(final String str, final String remove) {
		if (isEmpty(str) || isEmpty(remove)) {
			return str;
		}
		else if (str.startsWith(remove)) {
			return str.substring(remove.length());
		}
		else {
			return str;
		}
	}
	
	public static String removeEnd(final String str, final String remove) {
		if (isEmpty(str) || isEmpty(remove)) {
			return str;
		}
		else if (str.endsWith(remove)) {
			return str.substring(0, str.length()-remove.length());
		}
		else {
			return str;
		}
	}
	
	public static String emptyToNull(final String s) {
		return isEmpty(s) ? null : s;
	}
	
	public static String nullToEmpty(final String s) {
		return s == null ? "" : s;
	}
	
	public static String trimToEmpty(final String str) {
		return str == null ? "" : str.trim();
	}
	
	public static String trimToNull(final String str) {
		final String s = str == null ? null : str.trim();
		return isEmpty(s) ? null : s;
	}
	
	public static String decodeUnicode(final String s) {
		String working = s;
		int index;
		index = working.indexOf("\\u");
		while(index > -1) {
			int length = working.length();
			if(index > (length-6)) break;
			int numStart = index + 2;
			int numFinish = numStart + 4;
			String substring = working.substring(numStart, numFinish);
			int number = Integer.parseInt(substring,16);
			String stringStart = working.substring(0, index);
			String stringEnd   = working.substring(numFinish);
			working = stringStart + ((char)number) + stringEnd;
			index = working.indexOf("\\u");
		}
		return working;
	}	
	
	public static String quote(final String str, final char quote) {
		return new StringBuilder().append(quote).append(str).append(quote).toString();
	}
}
