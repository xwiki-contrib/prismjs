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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.CompositeBlock;
import org.xwiki.script.service.ScriptService;
import org.xwiki.uiextension.UIExtension;
import org.xwiki.webjars.script.WebJarsScriptService;

/**
 * Register the Prism.js file as "prismjs" in RequireJS, so that it can then be loaded in a custom JS file added through
 * a JSRX in the Prism macro.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named(RegisterPrismUIExtension.ID)
@Singleton
public class RegisterPrismUIExtension implements UIExtension, Initializable
{
    /**
     * The id of the UI extension.
     */
    public static final String ID = "org.xwiki.contrib.prismjs.requirejs.module";

    // TODO: Replace with "private WebJarsUrlFactory webJarsUrlFactory;" when the parent POM is moved to XWiki 17.3.0+
    @Inject
    @Named("webjars")
    private ScriptService webjarsScriptService;

    private Map<String, String> parameters;

    @Override
    public void initialize()
    {
        this.parameters = new HashMap<>();
        this.parameters.put("id", "prismjs");
        String prismJSURL = ((WebJarsScriptService) this.webjarsScriptService).url("org.webjars.npm:prismjs",
            "prism.js");
        this.parameters.put("path", prismJSURL);
    }

    @Override
    public String getId()
    {
        return ID;
    }

    @Override
    public String getExtensionPointId()
    {
        return "org.xwiki.platform.requirejs.module";
    }

    @Override
    public Map<String, String> getParameters()
    {
        return this.parameters;
    }

    @Override
    public Block execute()
    {
        return new CompositeBlock();
    }
}
