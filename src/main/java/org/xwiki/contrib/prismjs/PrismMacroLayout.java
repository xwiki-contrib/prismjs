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
package org.xwiki.contrib.prismjs;

/**
 * Values allowed in the <code>layout</code> parameter of the macro.
 * 
 * @version $Id$
 * @since 1.0
 */
public enum PrismMacroLayout
{
    /**
     * Don't display line numbers.
     */
    PLAIN(Constants.PLAIN_HINT),
    /**
     * Display line numbers.
     */
    LINENUMBERS(Constants.LINENUMBERS_HINT);

    private String hint;

    PrismMacroLayout(String hint)
    {
        this.hint = hint;
    }

    /**
     * Convenience class holding hint values. Links the outer enum class to the component classes as it is not
     * possible to reference enums in annotations (and avoids using plain strings).
     * 
     * @version $Id$
     */
    public static class Constants
    {
        /**
         * @see PrismMacroLayout#PLAIN
         */
        public static final String PLAIN_HINT = "plain";

        /**
         * @see PrismMacroLayout#LINENUMBERS
         */
        public static final String LINENUMBERS_HINT = "linenumbers";
    }
}
