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
package org.xwiki.contrib.prismjs.internal;

import java.util.List;

import javax.inject.Named;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xwiki.contrib.prismjs.PrismMacroLayout;
import org.xwiki.contrib.prismjs.PrismMacroParameters;
import org.xwiki.contrib.prismjs.source.CodeMacroSource;
import org.xwiki.contrib.prismjs.source.CodeMacroSourceFactory;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.RawBlock;
import org.xwiki.rendering.macro.source.MacroContentSourceReference;
import org.xwiki.rendering.syntax.Syntax;
import org.xwiki.rendering.transformation.MacroTransformationContext;
import org.xwiki.script.service.ScriptService;
import org.xwiki.skinx.SkinExtension;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;
import org.xwiki.webjars.script.WebJarsScriptService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link PrismMacro}.
 *
 * @version $Id$
 */
@ComponentTest
@ComponentList
class PrismMacroTest
{
    private static final String WEBJAR = "org.webjars.npm:prismjs";

    @InjectMockComponents
    private PrismMacro macro;

    @MockComponent
    @Named("linkx")
    private SkinExtension linkx;

    @MockComponent
    @Named("jsrx")
    private SkinExtension jsrx;

    @MockComponent
    @Named("ssrx")
    private SkinExtension ssrx;

    @MockComponent(classToMock = WebJarsScriptService.class)
    @Named("webjars")
    private ScriptService webjars;

    @MockComponent
    private CodeMacroSourceFactory sourceFactory;

    private MacroTransformationContext context;

    @BeforeEach
    void setUp()
    {
        when(((WebJarsScriptService) this.webjars).url(eq(WEBJAR), anyString()))
            .thenAnswer(invocation -> "/webjars/" + invocation.getArgument(1));
        this.context = new MacroTransformationContext();
    }

    @Test
    void executeWithLanguage() throws Exception
    {
        PrismMacroParameters parameters = new PrismMacroParameters();
        parameters.setLanguage("java");

        assertEquals("<pre><code class=\"language-java\">a &#60; b &#38;&#38; c</code></pre>",
            execute(parameters, "a < b && c"));
        verify(this.ssrx).use(eq("prism-accessible.css"), any());
        verify(this.linkx, never()).use(eq("/webjars/themes/prism.css"), any());
        verify(this.linkx, never()).use(eq("/webjars/plugins/line-numbers/prism-line-numbers.css"), any());
        verify(this.jsrx).use(eq("load-prism.js"), any());
    }

    @Test
    void executeWithoutLanguage() throws Exception
    {
        assertEquals("<pre><code class=\"language-none\">code</code></pre>",
            execute(new PrismMacroParameters(), "code"));
    }

    @Test
    void executeWithSpecialCharactersInLanguage() throws Exception
    {
        PrismMacroParameters parameters = new PrismMacroParameters();
        parameters.setLanguage("a\"b<c>&d");

        String html = execute(parameters, "code");

        assertTrue(html.startsWith("<pre><code class=\"language-a&#34;b&#60;c&#62;&#38;d\">"), html);
    }

    @Test
    void executeWithLineNumbers() throws Exception
    {
        PrismMacroParameters parameters = new PrismMacroParameters();
        parameters.setLanguage("java");
        parameters.setLayout(PrismMacroLayout.LINENUMBERS);

        assertEquals("<pre class=\"line-numbers\"><code class=\"language-java\">code</code></pre>",
            execute(parameters, "code"));
        verify(this.linkx).use(eq("/webjars/plugins/line-numbers/prism-line-numbers.css"), any());
    }

    @Test
    void executeInline() throws Exception
    {
        this.context.setInline(true);
        PrismMacroParameters parameters = new PrismMacroParameters();
        parameters.setLanguage("java");

        assertEquals("<code class=\"language-java\">code</code>", execute(parameters, "code"));
    }

    @Test
    void executeWithSource() throws Exception
    {
        MacroContentSourceReference reference = new MacroContentSourceReference("string", "<source>");
        when(this.sourceFactory.getContent(reference, this.context))
            .thenReturn(new CodeMacroSource(reference, "<source>", null));
        PrismMacroParameters parameters = new PrismMacroParameters();
        parameters.setSource(reference);

        assertEquals("<pre><code class=\"language-none\">&#60;source></code></pre>",
            execute(parameters, "ignored"));
    }

    @Test
    void executeWithoutContent() throws Exception
    {
        assertEquals("<pre><code class=\"language-none\"></code></pre>",
            execute(new PrismMacroParameters(), null));
    }

    private String execute(PrismMacroParameters parameters, String content) throws Exception
    {
        List<Block> blocks = this.macro.execute(parameters, content, this.context);

        assertEquals(1, blocks.size());
        RawBlock rawBlock = (RawBlock) blocks.get(0);
        assertEquals(Syntax.HTML_5_0, rawBlock.getSyntax());
        return rawBlock.getRawContent();
    }
}
