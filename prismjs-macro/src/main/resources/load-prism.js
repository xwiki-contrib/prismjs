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
// Load prism.js first and before the plugins so that when the plugins execute, they can register against Prism.
// If we use "require['prismjs', 'prismjs-line-numbers', 'prismjs-autoloader']" then there's no guarantee of the
// loading order.
require(['jquery', 'prismjs'], ($) => {
    // Avoid Prism from highlighting automatically since we're forcing the highlighting, as otherwise we get
    // highlighting flickers because requirejs uses async <script> elements, preventing prism.js from working well.
    Prism.manual = true;
    require(['prismjs-line-numbers', 'prismjs-autoloader'], () => {
        Prism.highlightAll()
        // To allow the WYSIWYG in-place editor to display highlighted content.
        $(document).on('xwiki:dom:updated', (event, data) => {
            for (const element of data.elements) {
                Prism.highlightAllUnder(element)
            }
        })
    })
})
