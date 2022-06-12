/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2022 Venice
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
package com.github.jlangch.venice.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.jlangch.venice.impl.util.StringUtil;


public class StringUtilTest {

    @Test
    public void testRemoveStart() {
        assertEquals(null, StringUtil.removeStart(null, "-"));
        assertEquals("", StringUtil.removeStart("", "-"));
        assertEquals("", StringUtil.removeStart("-", "-"));
        assertEquals("x", StringUtil.removeStart("x", "-"));
        assertEquals("x", StringUtil.removeStart("-x", "-"));
        assertEquals("-x", StringUtil.removeStart("--x", "-"));
    }

    @Test
    public void testRemoveEnd() {
        assertEquals(null, StringUtil.removeEnd(null, "-"));
        assertEquals("", StringUtil.removeEnd("", "-"));
        assertEquals("", StringUtil.removeEnd("-", "-"));
        assertEquals("x", StringUtil.removeEnd("x", "-"));
        assertEquals("x", StringUtil.removeEnd("x-", "-"));
        assertEquals("x-", StringUtil.removeEnd("x--", "-"));
    }

    @Test
    public void testEscape() {
        assertEquals("", StringUtil.escape(""));
        assertEquals(" ", StringUtil.escape(" "));
        assertEquals("a", StringUtil.escape("a"));
        assertEquals("•", StringUtil.escape("•"));

        assertEquals("abc-123", StringUtil.escape("abc-123"));

        assertEquals(" \\n \\r \\t \\\" \\\\ ", StringUtil.escape(" \n \r \t \" \\ "));

        assertEquals("--•--", StringUtil.escape("--•--"));
    }

    @Test
    public void testSplitIntoLines() {
        List<String> lines = StringUtil.splitIntoLines(null);
        assertEquals(0, lines.size());

        lines = StringUtil.splitIntoLines("");
        assertEquals(0, lines.size());

        lines = StringUtil.splitIntoLines("123");
        assertEquals(1, lines.size());
        assertEquals("123", lines.get(0));

        lines = StringUtil.splitIntoLines("\n");
        assertEquals(1, lines.size());
        assertEquals("", lines.get(0));

        lines = StringUtil.splitIntoLines("\r\n");
        assertEquals(1, lines.size());
        assertEquals("", lines.get(0));

        lines = StringUtil.splitIntoLines("123\n456");
        assertEquals(2, lines.size());
        assertEquals("123", lines.get(0));
        assertEquals("456", lines.get(1));

        lines = StringUtil.splitIntoLines("123\r\n456");
        assertEquals(2, lines.size());
        assertEquals("123", lines.get(0));
        assertEquals("456", lines.get(1));

        lines = StringUtil.splitIntoLines("123\n456\n");
        assertEquals(2, lines.size());
        assertEquals("123", lines.get(0));
        assertEquals("456", lines.get(1));

        lines = StringUtil.splitIntoLines("123\r\n456\r\n");
        assertEquals(2, lines.size());
        assertEquals("123", lines.get(0));
        assertEquals("456", lines.get(1));
    }

    @Test
    public void testStripMargin() {
        assertEquals("123", StringUtil.stripMargin("123", '|'));
        assertEquals("123", StringUtil.stripMargin("  |123", '|'));

        assertEquals("1\n2\n3", StringUtil.stripMargin("1\n  |2\n  |3", '|'));
        assertEquals("1\n 2\n 3", StringUtil.stripMargin("1\n  | 2\n  | 3", '|'));
    }

    @Test
    public void testStripIndent() {
        assertEquals(null, StringUtil.stripIndent(null));
        assertEquals("", StringUtil.stripIndent(""));
        assertEquals("123", StringUtil.stripIndent("123"));
        assertEquals("123", StringUtil.stripIndent("  123"));
        assertEquals("", StringUtil.stripIndent("\n"));
        assertEquals("\n  ", StringUtil.stripIndent("\n  "));
        assertEquals("\n123", StringUtil.stripIndent("\n123"));

        assertEquals("1\n2\n3", StringUtil.stripIndent("1\n2\n3"));
        assertEquals("1\n  2\n  3", StringUtil.stripIndent("1\n  2\n  3"));

        assertEquals("1\n2\n3", StringUtil.stripIndent("  1\n  2\n  3"));
    }

    @Test
    public void testStripIndent_CR() {
        assertEquals(null, StringUtil.stripIndent(null));
        assertEquals("", StringUtil.stripIndent(""));
        assertEquals("123", StringUtil.stripIndent("123"));
        assertEquals("123", StringUtil.stripIndent("  123"));
        assertEquals("", StringUtil.stripIndent("\r\n"));
        assertEquals("\n  ", StringUtil.stripIndent("\r\n  "));
        assertEquals("\n123", StringUtil.stripIndent("\r\n123"));

        assertEquals("1\n2\n3", StringUtil.stripIndent("1\r\n2\r\n3"));
        assertEquals("1\n  2\n  3", StringUtil.stripIndent("1\r\n  2\r\n  3"));

        assertEquals("1\n2\n3", StringUtil.stripIndent("  1\r\n  2\r\n  3"));
    }

    @Test
    public void testStripIndentIfFirstLineEmpty() {
        assertEquals("123", StringUtil.stripIndentIfFirstLineEmpty("123"));
        assertEquals("  123", StringUtil.stripIndentIfFirstLineEmpty("  123"));
        assertEquals("", StringUtil.stripIndentIfFirstLineEmpty("\n"));
        assertEquals("123", StringUtil.stripIndentIfFirstLineEmpty("\n123"));
        assertEquals("123", StringUtil.stripIndentIfFirstLineEmpty("\n123\n"));
        assertEquals("123\n456", StringUtil.stripIndentIfFirstLineEmpty("\n123\n456"));

        assertEquals("1\n2\n3", StringUtil.stripIndentIfFirstLineEmpty("1\n2\n3"));
        assertEquals("1\n  2\n  3", StringUtil.stripIndentIfFirstLineEmpty("1\n  2\n  3"));

        assertEquals("", StringUtil.stripIndentIfFirstLineEmpty("\n  "));
        assertEquals("", StringUtil.stripIndentIfFirstLineEmpty("\n  \n"));

        assertEquals("1\n2\n3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\n  2\n  3"));
        assertEquals("1\n2\n3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\n  2\n  3\n"));

        assertEquals("1\n 2\n  3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\n   2\n    3"));
        assertEquals("1\n 2\n  3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\n   2\n    3\n"));
    }

    @Test
    public void testStripIndentIfFirstLineEmpty_CR() {
        assertEquals("123", StringUtil.stripIndentIfFirstLineEmpty("123"));
        assertEquals("  123", StringUtil.stripIndentIfFirstLineEmpty("  123"));
        assertEquals("", StringUtil.stripIndentIfFirstLineEmpty("\r\n"));
        assertEquals("123", StringUtil.stripIndentIfFirstLineEmpty("\r\n123"));
        assertEquals("123", StringUtil.stripIndentIfFirstLineEmpty("\r\n123\r\n"));
        assertEquals("123\n456", StringUtil.stripIndentIfFirstLineEmpty("\r\n123\r\n456"));

        assertEquals("1\n2\n3", StringUtil.stripIndentIfFirstLineEmpty("1\r\n2\r\n3"));
        assertEquals("1\n  2\n  3", StringUtil.stripIndentIfFirstLineEmpty("1\r\n  2\r\n  3"));

        assertEquals("", StringUtil.stripIndentIfFirstLineEmpty("\r\n  "));
        assertEquals("", StringUtil.stripIndentIfFirstLineEmpty("\r\n  \r\n"));

        assertEquals("1\n2\n3", StringUtil.stripIndentIfFirstLineEmpty("\r\n  1\r\n  2\r\n  3"));
        assertEquals("1\n2\n3", StringUtil.stripIndentIfFirstLineEmpty("\r\n  1\r\n  2\r\n  3\r\n"));

        assertEquals("1\n 2\n  3", StringUtil.stripIndentIfFirstLineEmpty("\r\n  1\r\n   2\r\n    3"));
        assertEquals("1\n 2\n  3", StringUtil.stripIndentIfFirstLineEmpty("\r\n  1\r\n   2\r\n    3\r\n"));
    }

    @Test
    public void testStripIndentIfFirstLineEmpty_Join() {
        assertEquals("123456", StringUtil.stripIndentIfFirstLineEmpty("\n123\\\n456"));

        assertEquals("1\\\n2\\\n3", StringUtil.stripIndentIfFirstLineEmpty("1\\\n2\\\n3"));
        assertEquals("1\\\n2\n3", StringUtil.stripIndentIfFirstLineEmpty("1\\\n2\n3"));
        assertEquals("1\n2\\\n3", StringUtil.stripIndentIfFirstLineEmpty("1\n2\\\n3"));
        assertEquals("1\\\n  2\\\n  3", StringUtil.stripIndentIfFirstLineEmpty("1\\\n  2\\\n  3"));

        assertEquals("", StringUtil.stripIndentIfFirstLineEmpty("\n  "));
        assertEquals("", StringUtil.stripIndentIfFirstLineEmpty("\n  \\\n"));

        assertEquals("1\n2\n3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\n  2\n  3"));
        assertEquals("12\n3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\\\n  2\n  3"));
        assertEquals("1\n23", StringUtil.stripIndentIfFirstLineEmpty("\n  1\n  2\\\n  3"));
        assertEquals("123", StringUtil.stripIndentIfFirstLineEmpty("\n  1\\\n  2\\\n  3"));

        assertEquals("1\n2\n3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\n  2\n  3\n"));
        assertEquals("12\n3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\\\n  2\n  3\n"));
        assertEquals("1\n23", StringUtil.stripIndentIfFirstLineEmpty("\n  1\n  2\\\n  3\n"));
        assertEquals("1\n2\n3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\n  2\n  3\\\n"));

        assertEquals("1\n 2\n  3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\n   2\n    3"));
        assertEquals("1 2\n  3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\\\n   2\n    3"));
        assertEquals("1\n 2  3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\n   2\\\n    3"));
        assertEquals("1 2  3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\\\n   2\\\n    3"));

        assertEquals("1\n 2\n  3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\n   2\n    3\n"));
        assertEquals("1 2\n  3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\\\n   2\n    3\n"));
        assertEquals("1\n 2  3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\n   2\\\n    3\n"));
        assertEquals("1\n 2\n  3", StringUtil.stripIndentIfFirstLineEmpty("\n  1\n   2\n    3\\\n"));
    }
}
