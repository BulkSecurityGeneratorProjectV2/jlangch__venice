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

import static com.github.jlangch.venice.impl.reader.TokenType.ANY;
import static com.github.jlangch.venice.impl.reader.TokenType.COMMENT;
import static com.github.jlangch.venice.impl.reader.TokenType.SPECIAL_CHAR;
import static com.github.jlangch.venice.impl.reader.TokenType.STRING;
import static com.github.jlangch.venice.impl.reader.TokenType.STRING_BLOCK;
import static com.github.jlangch.venice.impl.reader.TokenType.UNQUOTE_SPLICE;
import static com.github.jlangch.venice.impl.reader.TokenType.WHITESPACES;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.jlangch.venice.EofException;
import com.github.jlangch.venice.ParseError;
import com.github.jlangch.venice.impl.util.ErrorMessage;


public class Tokenizer {

	private Tokenizer(
			final String text, 
			final String fileName
	) {
		this(text, fileName, true, true);
	}

	private Tokenizer(
			final String text, 
			final String fileName,
			final boolean skipWhitespaces,
			final boolean errorOnUnbalancedStringQuotes
	) {
		this.reader = new CharacterReader(text);
		this.fileName = fileName;
		this.skipWhitespaces = skipWhitespaces;
		this.errorOnUnbalancedStringQuotes = errorOnUnbalancedStringQuotes;
	}

	
	public static List<Token> tokenize(final String text, final String fileName) {
		return new Tokenizer(text, fileName, true, true).tokenize();
	}

	public static List<Token> tokenize(
			final String text, 
			final String fileName,
			final boolean skipWhitespaces,
			final boolean errorOnUnbalancedStringQuotes
	) {
		return new Tokenizer(text, fileName, skipWhitespaces, errorOnUnbalancedStringQuotes).tokenize();
	}
	
	
	private List<Token> tokenize() {
		tokens.clear();

		try {
			while(true) {
				int filePos = reader.getPos();
				int line = reader.getLineNumber();
				int col = reader.getColumnNumber();
				
				int ch = reader.peek();
				
				if (ch == EOF) {
					break;
				}

				else if (ch == LF) {
					addLinefeedToken(filePos, line, col);
					reader.consume();
				}

				// - comma: , (treated like a whitespace) ---------------------
				else if (ch == (int)',') {  
					addToken(WHITESPACES, ",", filePos, line, col);	
					reader.consume();
				}
				
				// - whitespaces ----------------------------------------------
				else if (Character.isWhitespace((char)ch)) {
					final StringBuilder sb = new StringBuilder();
					sb.append((char)ch);
					reader.consume();

					while(Character.isWhitespace((char)reader.peek())) {		
						sb.append((char)reader.peek());
						reader.consume();
					}
					
					addToken(WHITESPACES, sb.toString(), filePos, line, col);	
				}
				
				// - unquote splicing: ~@ -------------------------------------
				else if (ch == (int)'~') { 
					reader.consume();
					
					final int chNext = reader.peek();
					if (chNext == (int)'@') {
						addToken(UNQUOTE_SPLICE, "~@", filePos, line, col);	
						reader.consume();
					}
					else {
						addToken(SPECIAL_CHAR, "~", filePos, line, col);
					}
				}
				
				// - comment:  ; ....  read to EOL ----------------------------
				else if (ch == (int)';') {
					reader.consume();
					final StringBuilder sb = new StringBuilder();
					sb.append(';');

					while(!isLForEOF(reader.peek())) {		
						sb.append((char)reader.peek());
						reader.consume();
					}

					addToken(COMMENT, sb.toString(), filePos, line, col);				
				}
				
				// - special chars:  ()[]{}^'`~@ ------------------------------
				else if (isSpecialChar((char)ch)) {
					reader.consume();
					addToken(SPECIAL_CHAR, String.valueOf((char)ch), filePos, line, col);
				}
				
				// - string:  "xx" or """xx""" --------------------------------
				else if (ch == (int)'"') {  
					reader.consume();
					
					final int chNext = reader.peek();
					if (chNext != (int)'"'){
						final String s = readSingleQuotedString(filePos, line, col);
						addToken(STRING, s, filePos, line, col);
					}
					else {
						reader.consume();
						
						final int chNextNext = reader.peek();
						if (chNextNext != (int)'"') {
							addToken(STRING, "\"\"", filePos, line, col);	
						}
						else {
							reader.consume();
							addToken(STRING_BLOCK, readTripleQuotedString(filePos, line, col), filePos, line, col);
						}
					}
				}
				
				// - anything else --------------------------------------------
				else {
					reader.consume();
					final StringBuilder sb = new StringBuilder();
					sb.append((char)ch);
					
					ch = reader.peek();
					while(ch != EOF 
							&& ch != (int)',' 
							&& ch != (int)';'  
							&& ch != (int)'"' 
							&& !Character.isWhitespace(ch) 
							&& !isSpecialChar((char)ch)
					) { 		
						sb.append((char)ch);
						reader.consume();
						
						ch = reader.peek();
					}

					addToken(ANY, sb.toString(), filePos, line, col);	
				}
			}
		}
		catch(Exception ex) {
			throw new ParseError("Parse error (tokenizer phase) while reading from input", ex);
		}
		
		return tokens;
	}
	
	private String readSingleQuotedString(
			final int filePosStart, 
			final int lineStart, 
			final int colStart
	) throws IOException {
		final StringBuilder sb = new StringBuilder("\"");

		while(true) {
			final int ch = reader.peek();
			
			if (ch == EOF) {
				if (errorOnUnbalancedStringQuotes) {
					throwSingleQuotedStringEofError(sb.toString(), filePosStart, lineStart, colStart);
				}
				break;
			}
			else if (ch == (int)'"') {
				reader.consume();
				sb.append((char)ch);
				break;
			}
			else if (ch == (int)'\\') {
				final int filePos = reader.getPos();
				final int line = reader.getLineNumber();
				final int col = reader.getColumnNumber();
				reader.consume();
				sb.append((char)ch);
				sb.append(readStringEscapeChar(STRING, filePos, line, col));
			}
			else {
				reader.consume();
				sb.append((char)ch);
			}		
		}
		
		return sb.toString();
	}

	
	private String readTripleQuotedString(
			final int filePosStart, 
			final int lineStart, 
			final int colStart
	) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("\"\"\"");

		while(true) {
			final int ch = reader.peek();
			
			if (ch == EOF) {
				if (errorOnUnbalancedStringQuotes) {
					throwTripleQuotedStringEofError(sb.toString(), filePosStart, lineStart, colStart);
				}
				break;
			}
			else if (ch == (int)'"') {
				reader.consume();
				sb.append('"');

				final int chNext = reader.peek();
				if (chNext == (int)'"') {
					reader.consume();
					sb.append('"');
									
					final int chNextNext = reader.peek();
					if (chNextNext == (int)'"') {
						reader.consume();
						sb.append('"');
						break;
					}
				}
			}
			else if (ch == (int)'\\') {
				final int filePos = reader.getPos();
				final int line = reader.getLineNumber();
				final int col = reader.getColumnNumber();
				reader.consume();
				sb.append((char)ch);
				sb.append(readStringEscapeChar(STRING, filePos, line, col));
			}
			else {
				reader.consume();
				sb.append((char)ch);
			}
		}
		
		return sb.toString();
	}
		
	private boolean isSpecialChar(final char ch) {
		switch(ch) {
			case '(':
			case ')': 
			case '[': 
			case ']':
			case '{': 
			case '}':
			case '^':
			case '\'': 
			case '`':
			case '~':
			case '@':
				return true;
				
			default:
				return false;
		}
	}

	private void addLinefeedToken(final int filePos, final int line, final int col) { 
		addToken(WHITESPACES, "\n", filePos, line, col);
	}

	private void addToken(
			final TokenType type,
			final String token, 
			final int filePos, 
			final int line, 
			final int col
	) { 
		if (skipWhitespaces) {
			if (type != WHITESPACES && type != COMMENT) {
				tokens.add(new Token(type, token, fileName, filePos, line, col));	
			}
		}
		else {
			tokens.add(new Token(type, token, fileName, filePos, line, col));	
		}
	}
	
	private char readStringEscapeChar(
			final TokenType type,
			final int filePos, 
			final int line, 
			final int col
	) throws IOException {
		final int ch = reader.peek();
		
		if (ch == LF) {
			throw new ParseError(formatParseError(
					new Token(type, "\\", fileName, filePos, line, col), 
					"Expected escaped char in a string but got EOL"));
		}
		else if (ch == EOF) {
			throw new EofException(formatParseError(
					new Token(type, "\\", fileName, filePos, line, col), 
					"Expected escaped char in a string but got EOF"));
		}
		else {
			reader.consume();			
			return (char)ch;
		}
	}

	private String formatParseError(
			final Token token, 
			final String format, 
			final Object... args
	) {
		return String.format(format, args) 
				+ ". " 
				+ ErrorMessage.buildErrLocation(token);
	}
	
	private void throwSingleQuotedStringEofError(
			final String s, 
			final int filePos, 
			final int line, 
			final int col
	) {
		throw new ParseError(formatParseError(
				new Token(STRING, s, fileName, filePos, line, col), 
				"Expected closing \" for single quoted string but got EOF"));
	}
	
	private void throwTripleQuotedStringEofError(
			final String s, 
			final int filePos, 
			final int line, 
			final int col
	) {
		throw new ParseError(formatParseError(
				new Token(STRING_BLOCK, s, fileName, filePos, line, col), 
				"Expected closing \" for triple quoted string but got EOF"));
	}
	
	private boolean isLForEOF(final int ch) {
		return ch == LF || ch == EOF;
	}
	
	
	private static final int LF = (int)'\n';
	private static final int EOF = -1;

	//private final LineNumberingPushbackReader reader;
	private final CharacterReader reader;
	private final String fileName;
	private final boolean skipWhitespaces;
	private final boolean errorOnUnbalancedStringQuotes;
	private final List<Token> tokens = new ArrayList<>();
}
