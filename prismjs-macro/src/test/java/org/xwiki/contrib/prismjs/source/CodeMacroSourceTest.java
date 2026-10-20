/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.prismjs.source;

import org.junit.jupiter.api.Test;
import org.xwiki.rendering.macro.source.MacroContentSourceReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Unit tests for {@link CodeMacroSource}.
 *
 * @version $Id$
 */
class CodeMacroSourceTest
{
    private static final MacroContentSourceReference REFERENCE =
        new MacroContentSourceReference("string", "value");

    @Test
    void getters()
    {
        CodeMacroSource source = new CodeMacroSource(REFERENCE, "content", "java");

        assertEquals(REFERENCE, source.getReference());
        assertEquals("content", source.getContent());
        assertEquals("java", source.getLanguage());
    }

    @Test
    void equalsAndHashCode()
    {
        CodeMacroSource source = new CodeMacroSource(REFERENCE, "content", "java");
        CodeMacroSource sameSource = new CodeMacroSource(new MacroContentSourceReference("string", "value"),
            "content", "java");

        assertEquals(source, source);
        assertEquals(source, sameSource);
        assertEquals(source.hashCode(), sameSource.hashCode());

        assertNotEquals(source, null);
        assertNotEquals(source, "content");
        assertNotEquals(source, new CodeMacroSource(new MacroContentSourceReference("string", "other"),
            "content", "java"));
        assertNotEquals(source, new CodeMacroSource(REFERENCE, "other", "java"));
        assertNotEquals(source, new CodeMacroSource(REFERENCE, "content", "python"));
    }

    @Test
    void toStringContainsAllFields()
    {
        CodeMacroSource source = new CodeMacroSource(REFERENCE, "content", "java");

        assertEquals("reference = [" + REFERENCE + "], language = [java], content = [content]", source.toString());
    }
}
