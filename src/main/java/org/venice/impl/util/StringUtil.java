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
package org.venice.impl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
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
	 * @param text
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
	
	/**
	 * Unescapes a text
	 *
	 * @param text
	 * @return the unescaped text
	 */
	public static String unescape(final String text) {
		if (text == null) {
			return text;
		}
		
		
		final StringBuilder sb = new StringBuilder();
		
		final char[] chars = text.toCharArray();
		final int len = chars.length;
		int ii = 0;
		while(ii<len) {
			final char c = chars[ii++];
			if (c == '\\') {
				switch(chars[ii++]) {
					case 'n': sb.append('\n'); break;
					case 'r': sb.append('\r'); break;
					case 't': sb.append('\t'); break;
					case '"':  sb.append('\"'); break;
					case '\\': sb.append('\\'); break;
					default:  break;
				}
			}
			else {
				sb.append(c);
			}
		}
		
		return sb.toString();
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

	public static boolean isEmpty(final CharSequence cs){
		return cs == null || cs.length() == 0;
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
	        if(index > (length-6))break;
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
}
