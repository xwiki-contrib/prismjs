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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang.StringUtils;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.prismjs.PrismMacroLayout;
import org.xwiki.contrib.prismjs.PrismMacroParameters;
import org.xwiki.contrib.prismjs.source.CodeMacroSource;
import org.xwiki.contrib.prismjs.source.CodeMacroSourceFactory;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.RawBlock;
import org.xwiki.rendering.macro.AbstractMacro;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.macro.descriptor.DefaultContentDescriptor;
import org.xwiki.rendering.syntax.Syntax;
import org.xwiki.rendering.transformation.MacroTransformationContext;
import org.xwiki.script.service.ScriptService;
import org.xwiki.skinx.SkinExtension;
import org.xwiki.webjars.script.WebJarsScriptService;
import org.xwiki.xml.XMLUtils;

/**
 * Perform syntax highlighting on the content of the Macro.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named("prism")
@Singleton
public class PrismMacro extends AbstractMacro<PrismMacroParameters>
{
    /**
     * The description of the macro.
     */
    private static final String DESCRIPTION = "Highlights code snippets of various programming languages, "
        + "using Prism.js";

    /**
     * The description of the macro content.
     */
    private static final String CONTENT_DESCRIPTION = "the content to highlight";

    private static final String[] SEARCH_STRINGS = new String[] { "<", "&" };

    private static final String[] REPLACE_STRINGS = new String[] { "&lt;", "&amp;" };

    private static final Map<String, Object> CSS_PARAMETERS =  Collections.singletonMap("rel", "stylesheet");

    @Inject
    @Named("linkx")
    private SkinExtension linkxExtension;

    @Inject
    @Named("jsrx")
    private SkinExtension jsrxExtension;

    // TODO: Replace with "private WebJarsUrlFactory webJarsUrlFactory;" when the parent POM is moved to XWiki 17.3.0+
    @Inject
    @Named("webjars")
    private ScriptService webjarsScriptService;

    @Inject
    private CodeMacroSourceFactory sourceFactory;

    /**
     * Create and initialize the descriptor of the macro.
     */
    public PrismMacro()
    {
        super("Prism", DESCRIPTION, new DefaultContentDescriptor(CONTENT_DESCRIPTION, false),
            PrismMacroParameters.class);
        setDefaultCategories(Set.of(DEFAULT_CATEGORY_FORMATTING));
    }

    @Override
    public boolean supportsInlineMode()
    {
        return true;
    }

    @Override
    public List<Block> execute(PrismMacroParameters parameters, String content, MacroTransformationContext context)
        throws MacroExecutionException
    {
        // TODO: Verify that when this macro is used several times on the page we don't generate several link and
        // load several times the JS. We need to find a way to only do this once. This could be achieved by navigating
        // the XDOM. However this would not support an include macro, including some doc using this macro.
        // Maybe the use() APIs perform a check to only issue the same content once?

        // Inject a LINK HTML tag in the HEAD to load the Prism CSS:
        //   <link href="themes/prism.css" rel="stylesheet" />
        addCSSForPrism();

        // Add the styling for the line-numbers plugin
        addCSSForLineNumberPluginIfNeeded(parameters);

        // TODO: Once https://jira.xwiki.org/browse/XWIKI-12788 is implemented, remove RegisterPrismUIExtension and
        // the custom JS and instead use the js*x here.
        // For now, execute the JS to load prism.js through requirejs
        this.jsrxExtension.use("load-prism.js", Map.of("wysiwyg", true));

        // Use a RawBlock since we need to generate the following structure for Prism.js to work:
        //   <pre><code class="language-css">p { color: red }</code></pre>
        // Since the content inside the <code> tag is going to be parsed as HTML content by the browser, we need to
        // escape the & and < characters.
        CodeMacroSource computedContent = getContent(parameters, content, context);
        String normalizedContent;
        if (computedContent != null) {
            normalizedContent = XMLUtils.escapeElementText(computedContent.getContent());
        } else {
            normalizedContent = "";
        }
        String html = computeHTML(normalizedContent, parameters, context.isInline());

        return Collections.singletonList(new RawBlock(html, Syntax.HTML_5_0));
    }

    private void addCSSForPrism()
    {
        this.linkxExtension.use(getURL("themes/prism.css"), CSS_PARAMETERS);
    }

    private void addCSSForLineNumberPluginIfNeeded(PrismMacroParameters parameters)
    {
        if (parameters.getLayout().equals(PrismMacroLayout.LINENUMBERS)) {
            this.linkxExtension.use(getURL("plugins/line-numbers/prism-line-numbers.css"), CSS_PARAMETERS);
        }
    }

    private String getURL(String cssPath)
    {
        return  ((WebJarsScriptService) this.webjarsScriptService).url("org.webjars.npm:prismjs", cssPath);
    }

    private String computeHTML(String content, PrismMacroParameters parameters, boolean isInline)
    {
        return String.format("%s<code class=\"%s\">%s</code>%s",
            isInline ? "" : computeOpeningPRE(parameters.getLayout()),
            computeLanguageCSS(parameters.getLanguage()),
            content,
            isInline ? "" : "</pre>");
    }

    private String computeOpeningPRE(PrismMacroLayout layout)
    {
        return String.format("<pre%s>", layout.equals(PrismMacroLayout.LINENUMBERS) ? " class=\"line-numbers\"" : "");
    }

    private String computeLanguageCSS(String language)
    {
        return String.format("language-%s", StringUtils.isEmpty(language) ? "none" : language);
    }

    private CodeMacroSource getContent(PrismMacroParameters parameters, String content,
        MacroTransformationContext context) throws MacroExecutionException
    {
        if (parameters.getSource() != null) {
            return this.sourceFactory.getContent(parameters.getSource(), context);
        }

        return content != null ? new CodeMacroSource(null, content, null) : null;
    }
}
