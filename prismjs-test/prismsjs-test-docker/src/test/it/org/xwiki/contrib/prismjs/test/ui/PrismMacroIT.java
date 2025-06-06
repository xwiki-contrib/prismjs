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
package org.xwiki.contrib.prismjs.test.ui;

import org.junit.jupiter.api.Test;
import org.xwiki.test.docker.junit5.TestReference;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.ui.TestUtils;
import org.xwiki.test.ui.po.ViewPage;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the Prism.js Macro features.
 *
 * @version $Id$
 * @since 1.0
 */
@UITest
class PrismMacroIT
{
    @Test
    void verifyMacro(TestUtils setup, TestReference reference)
    {
        setup.loginAsSuperAdmin();

        // Create a page and add some prism.js macro examples
        ViewPage vp = setup.createPage(reference,
            "Hello {{prism language=\"html\"}}<p class=prism></p>{{/prism}} world.\n"
            + "\n"
            + "{{prism language=\"html\"}}\n"
            + "<p class=prism></p>\n"
            + "{{/prism}}\n"
            + "\n"
            + "{{prism language=\"java\" layout=\"linenumbers\"}}\n"
            + "@Component\n"
            + "@Named(\"prism\")\n"
            + "@Singleton\n"
            + "public class PrismMacro extends AbstractMacro<PrismMacroParameters>\n"
            + "{\n"
            + "}\n"
            + "{{/prism}}");

        String expected = "<p>Hello <code class=\"language-html\"><span class=\"token tag\"><span class=\"token tag\">"
            + "<span class=\"token punctuation\">&lt;</span>p</span> <span class=\"token attr-name\">class</span>"
            + "<span class=\"token attr-value\"><span class=\"token punctuation attr-equals\">=</span>prism</span>"
            + "<span class=\"token punctuation\">&gt;</span></span><span class=\"token tag\"><span class=\"token tag\">"
            + "<span class=\"token punctuation\">&lt;/</span>p</span><span class=\"token punctuation\">&gt;"
            + "</span></span></code> world.</p>"
            + "<pre class=\"language-html\" tabindex=\"0\"><code class=\"language-html\"><span class=\"token tag\">"
            + "<span class=\"token tag\"><span class=\"token punctuation\">&lt;</span>p</span> "
            + "<span class=\"token attr-name\">class</span><span class=\"token attr-value\">"
            + "<span class=\"token punctuation attr-equals\">=</span>prism</span>"
            + "<span class=\"token punctuation\">&gt;</span></span><span class=\"token tag\"><span class=\"token tag\">"
            + "<span class=\"token punctuation\">&lt;/</span>p</span><span class=\"token punctuation\">&gt;"
            + "</span></span></code></pre>"
            + "<pre class=\"line-numbers language-java\" tabindex=\"0\"><code class=\"language-java\">@Component\n"
            + "@Named(\"prism\")\n"
            + "@Singleton\n"
            + "public class PrismMacro extends AbstractMacro&lt;PrismMacroParameters&gt;\n"
            + "{\n"
            + "}</code></pre>";

        String source = setup.getDriver().getPageSource();
        assertThat(source, containsString(expected));

    }
}
