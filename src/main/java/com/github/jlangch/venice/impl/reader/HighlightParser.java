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

import static com.github.jlangch.venice.impl.reader.HighlightClass.AT;
import static com.github.jlangch.venice.impl.reader.HighlightClass.BRACE_BEGIN;
import static com.github.jlangch.venice.impl.reader.HighlightClass.BRACE_END;
import static com.github.jlangch.venice.impl.reader.HighlightClass.BRACKET_BEGIN;
import static com.github.jlangch.venice.impl.reader.HighlightClass.BRACKET_END;
import static com.github.jlangch.venice.impl.reader.HighlightClass.CONSTANT;
import static com.github.jlangch.venice.impl.reader.HighlightClass.HASH;
import static com.github.jlangch.venice.impl.reader.HighlightClass.KEYWORD;
import static com.github.jlangch.venice.impl.reader.HighlightClass.META;
import static com.github.jlangch.venice.impl.reader.HighlightClass.NUMBER;
import static com.github.jlangch.venice.impl.reader.HighlightClass.PARENTHESIS_BEGIN;
import static com.github.jlangch.venice.impl.reader.HighlightClass.PARENTHESIS_END;
import static com.github.jlangch.venice.impl.reader.HighlightClass.QUASI_QUOTE;
import static com.github.jlangch.venice.impl.reader.HighlightClass.QUOTE;
import static com.github.jlangch.venice.impl.reader.HighlightClass.STRING;
import static com.github.jlangch.venice.impl.reader.HighlightClass.SYMBOL;
import static com.github.jlangch.venice.impl.reader.HighlightClass.SYMBOL_FUNCTION_NAME;
import static com.github.jlangch.venice.impl.reader.HighlightClass.SYMBOL_SPECIAL_FORM;
import static com.github.jlangch.venice.impl.reader.HighlightClass.UNKNOWN;
import static com.github.jlangch.venice.impl.reader.HighlightClass.UNQUOTE;
import static com.github.jlangch.venice.impl.reader.HighlightClass.UNQUOTE_SPLICING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.jlangch.venice.EofException;


public class HighlightParser {
	
	private HighlightParser(
			final String form, 
			final List<Token> formTokens
	) {
		this.form = form;
		this.tokens = formTokens;
		this.position = 0;
	}

	
	public static List<HighlightItem> parse(final String str) {
		final List<Token> tokens = Tokenizer.tokenize(
										str, "highlighter", false, false);
		
		final HighlightParser hl = new HighlightParser(str, tokens);

		try {
			hl.process_form();
			hl.finish();
			return hl.items();
		}
		catch(EofException ex) {
			// return what we've got so far
			return hl.items();
		}
	}
	
	private List<HighlightItem> items() {
		return items;
	}
	
	private void addItem(final String token, final HighlightClass clazz) {
		items.add(new HighlightItem(token, pinnedClass == null ? clazz : pinnedClass));
	}
	
	private void addItem(final char token, final HighlightClass clazz) {
		addItem(String.valueOf(token), clazz);
	}
	
	private Token peek() {	
		while(true) {
			if (position >= tokens.size()) {
				throw new EofException("Unexpected EOF");
			}

			final Token token = tokens.get(position);

			if (token.isWhitespaces()) {
				addItem(token.getToken(), HighlightClass.WHITESPACES);
				position++;
			}
			else if (token.isComment()) {
				addItem(token.getToken(), HighlightClass.COMMENT);
				position++;
			}
			else {
				return token;
			}
		}
	}
   
	private Token next() {
		final Token t = peek();
		position++;
		return t;
	}
	
	private void process_atom() {
		final Token token = next();
		final String sToken = token.getToken();
				
		switch(Reader.getAtomType(token)) {
			case NIL:				
			case TRUE:
			case FALSE:
				addItem(sToken, CONSTANT);
				break;
				
			case INTEGER: 
			case LONG:
			case DOUBLE:
			case DECIMAL:
				addItem(sToken, NUMBER);
				break;
				
			case STRING: 
				addItem(sToken, STRING);
				break;
			
			case STRING_BLOCK:
				addItem(sToken, STRING);
				break;
			
			case KEYWORD:
				addItem(sToken, KEYWORD);
				break;
				
			case SYMBOL: {
					final HighlightItem last = items.size() < 1 ? null : items.get(items.size()-1);
					final HighlightItem secondLast = items.size() < 2 ? null : items.get(items.size()-2);
					
					if (last != null && last.getClazz() == PARENTHESIS_BEGIN) {
						if (secondLast != null && secondLast.getClazz() == QUOTE) {
							// '(a 2 3)
							addItem(sToken, SYMBOL);
						}
						else if (SPECIAL_FORMS.contains(sToken)) {
							// (def a 10)
							addItem(sToken, SYMBOL_SPECIAL_FORM);
						}
						else {
							// (+ 1 2)
							addItem(sToken, SYMBOL_FUNCTION_NAME);
						}
					}
					else {
						addItem(sToken, SYMBOL);
					}
				}
				break;
			
			case UNKNOWN:
			default:
				addItem(sToken, UNKNOWN);
				break;
		}
	}

	private void process_list(
			final char start, 
			final char end
	) {
		final Token startTok = next();
		addItem(startTok.getToken(), parenToClass(start));
		
		Token token = peek();
		while (token.charAt(0) != end) {
			process_form();
			token = peek();
		}
		
		final Token endTok = next();
		addItem(endTok.getToken(), parenToClass(end));
	}

	private void process_form() {
		final Token token = peek();
		
		switch (token.charAt(0)) {
			case '\'': 
				next();
				addItem('\'', QUOTE);
				process_form();
				break;
			
			case '`': 
				next();
				addItem('`', QUASI_QUOTE);
				process_form();
				break;
			
			case '~':
				if (token.equals("~")) {
					next();
					addItem('~', UNQUOTE);
					process_form();
				} 
				else {
					next();
					addItem("~@", UNQUOTE_SPLICING);
					process_form();
				}
				break;
			
			case '^':
				next();
				addItem('^', META);
				pinnedClass = META;
				process_form();
				pinnedClass = null;
				break;
			
			case '@': 
				next();
				addItem("@", AT);
				process_form();
				break;
				
			case '#': 
				next();
				addItem("#", HASH);

				Token t = peek();
				if (t.charAt(0) == '{') { // set literal #{1 2}
					process_list('{' , '}'); 
				}
				else if (t.charAt(0) == '(') { // anonymous function literal #(> % 2)
					process_list('(' , ')');
				}
				break;
			
			case '(': 
				process_list('(' , ')'); 
				break;
			
			case ')': 
				next();
				addItem(')', PARENTHESIS_END);
				break;
			
			case '[': 
				process_list('[' , ']'); 
				break;
			
			case ']': 
				next();
				addItem(']', BRACKET_END);
				break;
				
			case '{': 
				process_list('{', '}');
				break;
				
			case '}': 
				next();
				addItem('}', BRACE_END);
				break;
				
			default:  
				process_atom();
				break;
		}
	}
	
	private HighlightClass parenToClass(final char ch) {
		switch(ch) {
			case '(': return PARENTHESIS_BEGIN;
			case ')': return PARENTHESIS_END;
			case '[': return BRACKET_BEGIN;
			case ']': return BRACKET_END;
			case '{': return BRACE_BEGIN;
			case '}': return BRACE_END;
			default: throw new RuntimeException("Invalid parenthesis '" + ch + "'");
		}
	}

	private void finish() {
		final int numTokens = tokens.size();
		if (position >= numTokens) {
			// all tokens processed
		}
		else if (position == numTokens-1) {
			final Token tok = tokens.get(position);
			if (tok.getType() != TokenType.WHITESPACES) {
				addItem(form.substring(tok.getFileStartPos()), HighlightClass.UNPROCESSED);
			}
		}
		else {
			final Token tok = tokens.get(position);
			addItem(form.substring(tok.getFileStartPos()), HighlightClass.UNPROCESSED);
		}
	}
	
	private static Set<String> SPECIAL_FORMS = new HashSet<>(
			Arrays.asList(
					"def",
					"defonce",
					"def-dynamic",
					"defmacro",
					"defn",
					"defmulti",
					"defmethod",
					"deftype",
					"deftype?",		
					"deftype-of",
					"deftype-or",
					"import",
					"if",
					"do",
					"eval",
					"resolve",
					"loop",
					"recur",
					"try",
					"try-with",
					"catch",
					"finally",
					"macroexpand"));
	
	
	private final String form;
	private final List<HighlightItem> items= new ArrayList<>();
	private final List<Token> tokens;
	private int position;
	private HighlightClass pinnedClass = null;
}
