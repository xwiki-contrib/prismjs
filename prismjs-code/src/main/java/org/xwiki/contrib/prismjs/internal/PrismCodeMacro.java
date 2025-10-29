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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.prismjs.PrismMacroLayout;
import org.xwiki.contrib.prismjs.PrismMacroParameters;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.internal.macro.code.CodeMacro;
import org.xwiki.rendering.macro.Macro;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.macro.MacroPreparationException;
import org.xwiki.rendering.macro.code.CodeMacroLayout;
import org.xwiki.rendering.macro.code.CodeMacroParameters;
import org.xwiki.rendering.macro.source.MacroContentSourceReference;
import org.xwiki.rendering.transformation.MacroTransformationContext;

/**
 * Perform syntax highlighting on the content of the Macro.
 *
 * @version $Id$
 * @since 1.1
 */
@Component
@Named("code")
@Singleton
public class PrismCodeMacro extends CodeMacro
{
    @Inject
    @Named("prism")
    private Macro prismMacro;

    @Override
    public void prepare(MacroBlock macroBlock) throws MacroPreparationException
    {
        // Overridden so that CodeMacro#prepare is not called, as it's expensive.
    }

    @Override
    public List<Block> execute(CodeMacroParameters parameters, String content, MacroTransformationContext context)
        throws MacroExecutionException
    {
        PrismMacroParameters prismParameters = new PrismMacroParameters();
        prismParameters.setLanguage(parameters.getLanguage());
        if (CodeMacroLayout.LINENUMBERS.equals(parameters.getLayout())) {
            prismParameters.setLayout(PrismMacroLayout.LINENUMBERS);
        }
        MacroContentSourceReference sourceReference = parameters.getSource();
        prismParameters.setSource(sourceReference);

        return this.prismMacro.execute(prismParameters, content, context);
    }
}
