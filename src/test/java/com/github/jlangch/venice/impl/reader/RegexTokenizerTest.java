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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.jlangch.venice.ParseError;
import com.github.jlangch.venice.impl.ModuleLoader;
import com.github.jlangch.venice.impl.reader.RegexTokenizer;
import com.github.jlangch.venice.impl.reader.Token;
import com.github.jlangch.venice.impl.reader.Tokenizer;
import com.github.jlangch.venice.impl.util.StopWatch;


public class RegexTokenizerTest {

	@Test
	public void test_empty() {	
		final List<Token> tokens = RegexTokenizer.tokenize("", "test");
		assertEquals(0, tokens.size());
	}

	@Test
	public void test_whitespaces() {	
		List<Token> tokens = RegexTokenizer.tokenize(" \t \r ", "test");
		assertEquals(0, tokens.size());
		
		tokens = RegexTokenizer.tokenize(" , \t , \r , ", "test");
		assertEquals(0, tokens.size());
	}

	@Test
	public void test_comment() {	
		List<Token> tokens = RegexTokenizer.tokenize(" ; comment ", "test");
		assertEquals(0, tokens.size());

		tokens = RegexTokenizer.tokenize(" abc ; comment ", "test");
		assertEquals(1, tokens.size());
		assertEquals("abc", tokens.get(0).getToken());
	}

	@Test
	public void test_any() {	
		final List<Token> tokens = RegexTokenizer.tokenize("abc", "test");
		assertEquals(1, tokens.size());
		assertEquals("abc", tokens.get(0).getToken());
	}

	@Test
	public void test_any_with_whitespaces() {	
		final List<Token> tokens = RegexTokenizer.tokenize(" abc ", "test");
		assertEquals(1, tokens.size());
		assertEquals("abc", tokens.get(0).getToken());
	}

	@Test
	public void test_special() {	
		List<Token> tokens = RegexTokenizer.tokenize("^'`~#@", "test");
		assertEquals(6, tokens.size());
		assertEquals("^",   tokens.get(0).getToken());
		assertEquals("'",   tokens.get(1).getToken());
		assertEquals("`",   tokens.get(2).getToken());
		assertEquals("~",   tokens.get(3).getToken());
		assertEquals("#",   tokens.get(4).getToken());
		assertEquals("@",   tokens.get(5).getToken());
		
		tokens = RegexTokenizer.tokenize(" ^ ' ` ~ # @ ", "test");
		assertEquals(6, tokens.size());
		assertEquals("^",   tokens.get(0).getToken());
		assertEquals("'",   tokens.get(1).getToken());
		assertEquals("`",   tokens.get(2).getToken());
		assertEquals("~",   tokens.get(3).getToken());
		assertEquals("#",   tokens.get(4).getToken());
		assertEquals("@",   tokens.get(5).getToken());
		
		tokens = RegexTokenizer.tokenize(" ^ , ' , ` , ~ , # , @ ", "test");
		assertEquals(6, tokens.size());
		assertEquals("^",   tokens.get(0).getToken());
		assertEquals("'",   tokens.get(1).getToken());
		assertEquals("`",   tokens.get(2).getToken());
		assertEquals("~",   tokens.get(3).getToken());
		assertEquals("#",   tokens.get(4).getToken());
		assertEquals("@",   tokens.get(5).getToken());
	}

	@Test
	public void test_any_with_special() {	
		List<Token> tokens = RegexTokenizer.tokenize("(abc)", "test");
		assertEquals(3, tokens.size());
		assertEquals("(",   tokens.get(0).getToken());
		assertEquals("abc", tokens.get(1).getToken());
		assertEquals(")",   tokens.get(2).getToken());

		tokens = RegexTokenizer.tokenize("[abc]", "test");
		assertEquals(3, tokens.size());
		assertEquals("[",   tokens.get(0).getToken());
		assertEquals("abc", tokens.get(1).getToken());
		assertEquals("]",   tokens.get(2).getToken());

		tokens = RegexTokenizer.tokenize("{abc}", "test");
		assertEquals(3, tokens.size());
		assertEquals("{",   tokens.get(0).getToken());
		assertEquals("abc", tokens.get(1).getToken());
		assertEquals("}",   tokens.get(2).getToken());

		tokens = RegexTokenizer.tokenize("^\\`~#@abc^", "test");
		assertEquals(8, tokens.size());
		assertEquals("^",   tokens.get(0).getToken());
		assertEquals("\\",  tokens.get(1).getToken());
		assertEquals("`",   tokens.get(2).getToken());
		assertEquals("~",   tokens.get(3).getToken());
		assertEquals("#",   tokens.get(4).getToken());
		assertEquals("@",   tokens.get(5).getToken());
		assertEquals("abc", tokens.get(6).getToken());
		assertEquals("^",   tokens.get(7).getToken());
	}

	@Test
	public void test_single_quoted_string() {	
		List<Token> tokens = RegexTokenizer.tokenize("\"\"", "test");
		assertEquals(1, tokens.size());
		assertEquals("\"\"", tokens.get(0).getToken());

		tokens = RegexTokenizer.tokenize(" \"\" ", "test");
		assertEquals(1, tokens.size());
		assertEquals("\"\"", tokens.get(0).getToken());

		tokens = RegexTokenizer.tokenize(" \" \" ", "test");
		assertEquals(1, tokens.size());
		assertEquals("\" \"", tokens.get(0).getToken());

		tokens = RegexTokenizer.tokenize(" \"a b c d\" ", "test");
		assertEquals(1, tokens.size());
		assertEquals("\"a b c d\"", tokens.get(0).getToken());

		tokens = RegexTokenizer.tokenize(" \"a b \\\" c d\" ", "test");
		assertEquals(1, tokens.size());
		assertEquals("\"a b \\\" c d\"", tokens.get(0).getToken());

		tokens = RegexTokenizer.tokenize(" \"\\\"\\\"\" ", "test");
		assertEquals(1, tokens.size());
		assertEquals("\"\\\"\\\"\"", tokens.get(0).getToken());

		tokens = RegexTokenizer.tokenize(" \"a b \\t c d\" ", "test");
		assertEquals(1, tokens.size());
		assertEquals("\"a b \\t c d\"", tokens.get(0).getToken());
	}

	@Test
	public void test_single_quoted_string_errors() {
		// premature EOL
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\n", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"a\n", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"ab\n", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"abc\n", "test"));
		
		// premature EOF
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"a", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"ab", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"abc", "test"));
		
		// invalid escape 
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\\\"", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"a\\\"", "test"));
		
		// invalid escape - premature EOL
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\\\n", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"a\\\n", "test"));
		
		// invalid escape - premature EOF
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\\", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"a\\", "test"));
	}

	@Test
	public void test_triple_quoted_string() {
		List<Token> tokens = RegexTokenizer.tokenize("\"\"\"\"\"\"", "test");
		assertEquals(1, tokens.size());
		assertEquals("\"\"\"\"\"\"", tokens.get(0).getToken());

		tokens = RegexTokenizer.tokenize(" \"\"\"\"\"\" ", "test");
		assertEquals(1, tokens.size());
		assertEquals("\"\"\"\"\"\"", tokens.get(0).getToken());

		tokens = RegexTokenizer.tokenize("\"\"\" \"\"\"", "test");
		assertEquals(1, tokens.size());
		assertEquals("\"\"\" \"\"\"", tokens.get(0).getToken());

		tokens = RegexTokenizer.tokenize(" \"\"\"a b c d\"\"\" ", "test");
		assertEquals(1, tokens.size());
		assertEquals("\"\"\"a b c d\"\"\"", tokens.get(0).getToken());

		tokens = RegexTokenizer.tokenize(" \"\"\"a b \\S c d\"\"\" ", "test");
		assertEquals(1, tokens.size());
		assertEquals("\"\"\"a b \\S c d\"\"\"", tokens.get(0).getToken());

		tokens = RegexTokenizer.tokenize(" \"\"\"a b \n 1 \n c d\"\"\" ", "test");
		assertEquals(1, tokens.size());
		assertEquals("\"\"\"a b \n 1 \n c d\"\"\"", tokens.get(0).getToken());

		tokens = RegexTokenizer.tokenize(" \"\"\"a b \\\"\\\"\\\" c d\"\"\" ", "test");
		assertEquals(1, tokens.size());
		assertEquals("\"\"\"a b \\\"\\\"\\\" c d\"\"\"", tokens.get(0).getToken());

	    // with quotes
		tokens = RegexTokenizer.tokenize(" \"\"\"a b \"xy\" c d\"\"\" ", "test");
		assertEquals(1, tokens.size());
		assertEquals("\"\"\"a b \"xy\" c d\"\"\"", tokens.get(0).getToken());
	
		// not working
//		tokens = RegexTokenizer.tokenize(" \"\"\"\"a b c d\\\"\"\"\" ", "test");
//		assertEquals(1, tokens.size());
//		assertEquals("\"\"\"\"a b c d\\\"\"\"\"", tokens.get(0).getToken());
	}
	
	@Test
	public void test_triple_quoted_string_errors() {
		// premature EOL
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\"\"\n", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\"\"a\n", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\"\"ab\n", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\"\"abc\n", "test"));
		
		// premature EOF
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\"\"\"", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\"\"\"a", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\"\"\"ab", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\"\"\"abc", "test"));
		
		// invalid escape 
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\"\"\"\\\"", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\"\"\"a\\\"", "test"));
		
		// invalid escape - premature EOL
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\"\"\"\\\n", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\"\"\"a\\\n", "test"));
		
		// invalid escape - premature EOF
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\"\"\"\\", "test"));
		assertThrows(ParseError.class, () ->  Tokenizer.tokenize("\"\"\"\"a\\", "test"));
	}
	
		
	@Test
	public void testTokenize_LF() {	
		final String s = 
				"(do                                  \n" +
				"   100                               \n" +
				"   100.4                             \n" +
				"   100.4M                            \n" +
				"   ;comment                          \n" +
				"   \"abcdef\"                        \n" +
				"   \"abc\\\"def\"                    \n" +
				"   \"abc\ndef\"                      \n" +
				"   \"\"\"uvwxyz\"\"\"                \n" +
				"   \"\"\"uvw\"xyz\"\"\"              \n" +
				"   \"\"\"uvw\nxyz\"\"\"              \n" +
				"   \"\"\"uvw\"\"\" \"\"\"xyz\"\"\"   \n" +
				"   (+ 2 3)                           \n" +
				")                                      ";
		
		int pos = 0;
		
		final List<Token> tokens = RegexTokenizer.tokenize(s, "test");
		assertEquals("(", tokens.get(pos++).getToken());
		assertEquals("do", tokens.get(pos++).getToken());
		assertEquals("100", tokens.get(pos++).getToken());
		assertEquals("100.4", tokens.get(pos++).getToken());
		assertEquals("100.4M", tokens.get(pos++).getToken());
		assertEquals("\"abcdef\"", tokens.get(pos++).getToken());
		assertEquals("\"abc\\\"def\"", tokens.get(pos++).getToken());
		assertEquals("\"abc\ndef\"", tokens.get(pos++).getToken());
		assertEquals("\"\"\"uvwxyz\"\"\"", tokens.get(pos++).getToken());
		assertEquals("\"\"\"uvw\"xyz\"\"\"", tokens.get(pos++).getToken());
		assertEquals("\"\"\"uvw\nxyz\"\"\"", tokens.get(pos++).getToken());
		assertEquals("\"\"\"uvw\"\"\"", tokens.get(pos++).getToken());
		assertEquals("\"\"\"xyz\"\"\"", tokens.get(pos++).getToken());
		assertEquals("(", tokens.get(pos++).getToken());
		assertEquals("+", tokens.get(pos++).getToken());
		assertEquals("2", tokens.get(pos++).getToken());
		assertEquals("3", tokens.get(pos++).getToken());
		assertEquals(")", tokens.get(pos++).getToken());
		assertEquals(")", tokens.get(pos++).getToken());
	}

	@Test
	public void testTokenize_CR_LF() {	
		final String s = 
				"(do                                  \r\n" +
				"   100                               \r\n" +
				"   100.4                             \r\n" +
				"   100.4M                            \r\n" +
				"   ;comment                          \r\n" +
				"   \"abcdef\"                        \r\n" +
				"   \"abc\\\"def\"                    \r\n" +
				"   \"abc\ndef\"                      \r\n" +
				"   \"\"\"uvwxyz\"\"\"                \r\n" +
				"   \"\"\"uvw\"xyz\"\"\"              \r\n" +
				"   \"\"\"uvw\nxyz\"\"\"              \r\n" +
				"   \"\"\"uvw\"\"\" \"\"\"xyz\"\"\"   \r\n" +
				"   (+ 2 3)                           \r\n" +
				")                                      ";
		
		int pos = 0;
		
		final List<Token> tokens = RegexTokenizer.tokenize(s, "test");
		assertEquals("(", tokens.get(pos++).getToken());
		assertEquals("do", tokens.get(pos++).getToken());
		assertEquals("100", tokens.get(pos++).getToken());
		assertEquals("100.4", tokens.get(pos++).getToken());
		assertEquals("100.4M", tokens.get(pos++).getToken());
		assertEquals("\"abcdef\"", tokens.get(pos++).getToken());
		assertEquals("\"abc\\\"def\"", tokens.get(pos++).getToken());
		assertEquals("\"abc\ndef\"", tokens.get(pos++).getToken());
		assertEquals("\"\"\"uvwxyz\"\"\"", tokens.get(pos++).getToken());
		assertEquals("\"\"\"uvw\"xyz\"\"\"", tokens.get(pos++).getToken());
		assertEquals("\"\"\"uvw\nxyz\"\"\"", tokens.get(pos++).getToken());
		assertEquals("\"\"\"uvw\"\"\"", tokens.get(pos++).getToken());
		assertEquals("\"\"\"xyz\"\"\"", tokens.get(pos++).getToken());
		assertEquals("(", tokens.get(pos++).getToken());
		assertEquals("+", tokens.get(pos++).getToken());
		assertEquals("2", tokens.get(pos++).getToken());
		assertEquals("3", tokens.get(pos++).getToken());
		assertEquals(")", tokens.get(pos++).getToken());
		assertEquals(")", tokens.get(pos++).getToken());
	}

	@Test
	public void test_position() {	
		List<Token> tokens = RegexTokenizer.tokenize("   100.0  ", "test");
		assertEquals(1, tokens.size());
		assertEquals("100.0", tokens.get(0).getToken());
		assertEquals("test", tokens.get(0).getFile());
		assertEquals(1, tokens.get(0).getLine());
		assertEquals(4, tokens.get(0).getColumn());
		assertEquals(3, tokens.get(0).getFileStartPos());
		assertEquals(7, tokens.get(0).getFileEndPos());
	}

	@Test
	public void test_sexpr() {	
		List<Token> tokens = RegexTokenizer.tokenize("(do 100.2)", "test");
		assertEquals(4, tokens.size());
		assertEquals("(",     tokens.get(0).getToken());
		assertEquals("do",    tokens.get(1).getToken());
		assertEquals("100.2", tokens.get(2).getToken());
		assertEquals(")",     tokens.get(3).getToken());
	}

	@Test
	public void test_unquote_splicing() {	
		List<Token> tokens = RegexTokenizer.tokenize(" ~@ ", "test");
		assertEquals(1, tokens.size());
		assertEquals("~@", tokens.get(0).getToken());

		tokens = RegexTokenizer.tokenize(" ~@body ", "test");
		assertEquals(2, tokens.size());
		assertEquals("~@", tokens.get(0).getToken());
		assertEquals("body", tokens.get(1).getToken());
	}

	@Test
	public void test_syntax_quote() {	
		List<Token> tokens = RegexTokenizer.tokenize("`(if true 1)", "test");
		assertEquals(6,      tokens.size());
		assertEquals("`",    tokens.get(0).getToken());
		assertEquals("(",    tokens.get(1).getToken());
		assertEquals("if",   tokens.get(2).getToken());
		assertEquals("true", tokens.get(3).getToken());
		assertEquals("1",    tokens.get(4).getToken());
		assertEquals(")",    tokens.get(5).getToken());
	}
	
	@Test
	public void test_unquote() {	
		List<Token> tokens = RegexTokenizer.tokenize("~expr", "test");
		assertEquals(2,       tokens.size());
		assertEquals("~",     tokens.get(0).getToken());
		assertEquals("expr",   tokens.get(1).getToken());
		
		tokens = RegexTokenizer.tokenize("'~expr", "test");
		assertEquals(3,       tokens.size());
		assertEquals("'",     tokens.get(0).getToken());
		assertEquals("~",     tokens.get(1).getToken());
		assertEquals("expr",  tokens.get(2).getToken());
	}

	@Test
	public void test_list() {	
		List<Token> tokens = tokenize("'(1 2 3)", "test");
		assertEquals(6,      tokens.size());
		assertEquals("'",    tokens.get(0).getToken());
		assertEquals("(",    tokens.get(1).getToken());
		assertEquals("1",    tokens.get(2).getToken());
		assertEquals("2",    tokens.get(3).getToken());
		assertEquals("3",    tokens.get(4).getToken());
		assertEquals(")",    tokens.get(5).getToken());
	}

	@Test
	public void test_set() {	
		List<Token> tokens = tokenize("#{1 2}", "test");
		assertEquals(5, tokens.size());
		assertEquals("#", tokens.get(0).getToken());
		assertEquals("{", tokens.get(1).getToken());
		assertEquals("1", tokens.get(2).getToken());
		assertEquals("2", tokens.get(3).getToken());
		assertEquals("}", tokens.get(4).getToken());
	}

	@Test
	public void test_keyword() {	
		List<Token> tokens = tokenize(":alpha", "test");
		assertEquals(1, tokens.size());
		assertEquals(":alpha", tokens.get(0).getToken());
	}

	@Test
	public void test_autogen_sym() {	
		List<Token> tokens = tokenize("c#", "test");
		assertEquals(1, tokens.size());
		assertEquals("c#", tokens.get(0).getToken());
	}

	@Test
	public void test_metadata() {	
		List<Token> tokens = tokenize(
								"^{ :arglists '(             \n" +
								"       \"(if then else)\")  \n" + 
								"   :doc                     \n" + 
								"       \"\"\"\n" + 
								"       doc\n" + 
								"       \"\"\"\n" + 
								"   :examples '(             \n" +
								"       \"(if true 1)\") }    ", 
								"test");
		assertEquals(15, tokens.size());
		assertEquals("^",                   tokens.get(0).getToken());
		assertEquals("{",                   tokens.get(1).getToken());
		assertEquals(":arglists",           tokens.get(2).getToken());
		assertEquals("'",                   tokens.get(3).getToken());
		assertEquals("(",                   tokens.get(4).getToken());
		assertEquals("\"(if then else)\"",  tokens.get(5).getToken());
		assertEquals(")",                   tokens.get(6).getToken());
		assertEquals(":doc",                tokens.get(7).getToken());
		assertEquals("\"\"\"\n       doc\n       \"\"\"",   tokens.get(8).getToken());
		assertEquals(":examples",           tokens.get(9).getToken());
		assertEquals("'",                   tokens.get(10).getToken());
		assertEquals("(",                   tokens.get(11).getToken());
		assertEquals("\"(if true 1)\"",     tokens.get(12).getToken());
		assertEquals(")",                   tokens.get(13).getToken());
		assertEquals("}",                   tokens.get(14).getToken());
	}

	@Test
	public void test_tokenize_core() {
		final String core = ModuleLoader.loadModule("core");
		final StopWatch sw = new StopWatch();
		final List<Token> tokens = tokenize(core, "core");
		System.out.println("Tokenizing :core module with RegexTokenizer: " + sw.stop().toString());
		assertTrue(!tokens.isEmpty());
	}

	
	private static List<Token> tokenize(final String text, final String fileName) {
		return RegexTokenizer.tokenize(text, fileName);
	}

}
