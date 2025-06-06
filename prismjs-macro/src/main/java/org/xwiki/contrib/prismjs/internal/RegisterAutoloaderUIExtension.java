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

import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;

/**
 * Register the Autoloader plugin for prism.js file as {@code prismjs-autoloader} in RequireJS, so that it can then
 * be loaded in a custom JS file added through a JSRX in the Prism macro.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named(RegisterAutoloaderUIExtension.ID)
@Singleton
public class RegisterAutoloaderUIExtension extends AbstractPrismUIExtension
{
    /**
     * The id of the UI extension.
     */
    public static final String ID = "org.xwiki.contrib.prismjs.requirejs.module.autoloader";

    @Override
    public void initialize()
    {
        initialize("prismjs-autoloader", "plugins/autoloader/prism-autoloader.js");
    }

    @Override
    public String getId()
    {
        return ID;
    }
}
