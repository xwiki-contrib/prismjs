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
package org.xwiki.contrib.prismjs.test.po;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xwiki.test.ui.po.BaseElement;

import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;

/**
 * Represents the code highlighted by the Prism macros of the current page.
 *
 * @version $Id$
 * @since 1.3
 */
public class PrismCodeElement extends BaseElement
{
    private static final String CODE_SELECTOR = "code[class*=\"language-\"]";

    // The highlighting is done asynchronously by Prism, once all its scripts are loaded.
    private static final String HIGHLIGHTED_SCRIPT = "return [...document.querySelectorAll('" + CODE_SELECTOR
        + ":not(.language-none)')].every(code => code.querySelector('.token'))";

    private static final String TOKEN_TYPES_SCRIPT = "return [...new Set([...document.querySelectorAll('"
        + CODE_SELECTOR + " .token')].map(token => token.classList[1]))]";

    // The line numbers are generated content of the line-numbers plugin, which axe-core doesn't check.
    private static final String LINE_NUMBER_COLORS_SCRIPT = """
        const number = document.querySelector('.line-numbers-rows > span');
        return [getComputedStyle(number, '::before').color, getComputedStyle(number.closest('pre')).backgroundColor];
        """;

    private static final Pattern RGB_PATTERN = Pattern.compile("rgba?\\((\\d+), (\\d+), (\\d+)");

    /**
     * @return the color contrast issues reported by axe-core on the highlighted code, i.e. both the violations and the
     *     checks that axe-core couldn't complete, empty if all the highlighted text has enough contrast for WCAG 2.x AA
     */
    public List<String> getContrastIssues()
    {
        waitUntilHighlighted();
        Results results = new AxeBuilder()
            .withRules(List.of("color-contrast"))
            .include(CODE_SELECTOR)
            .analyze(getDriver());
        List<String> issues = new ArrayList<>();
        if (results.isErrored()) {
            issues.add(results.getErrorMessage());
        }
        List<Rule> rules = new ArrayList<>(results.getViolations());
        rules.addAll(results.getIncomplete());
        rules.forEach(rule -> rule.getNodes().forEach(
            node -> issues.add(node.getHtml() + ": " + node.getFailureSummary())));
        return issues;
    }

    /**
     * @return the types of the tokens found in the highlighted code (e.g. "keyword", "comment")
     */
    public Set<String> getTokenTypes()
    {
        waitUntilHighlighted();
        return new HashSet<>((List<String>) getDriver().executeScript(TOKEN_TYPES_SCRIPT));
    }

    /**
     * @return the WCAG contrast ratio between the line numbers and their background
     */
    public double getLineNumberContrastRatio()
    {
        waitUntilHighlighted();
        List<String> colors = (List<String>) getDriver().executeScript(LINE_NUMBER_COLORS_SCRIPT);
        double numberLuminance = getRelativeLuminance(colors.get(0));
        double backgroundLuminance = getRelativeLuminance(colors.get(1));
        return (Math.max(numberLuminance, backgroundLuminance) + 0.05)
            / (Math.min(numberLuminance, backgroundLuminance) + 0.05);
    }

    private void waitUntilHighlighted()
    {
        getDriver().waitUntilCondition(driver -> (Boolean) getDriver().executeScript(HIGHLIGHTED_SCRIPT));
    }

    private double getRelativeLuminance(String color)
    {
        Matcher matcher = RGB_PATTERN.matcher(color);
        if (!matcher.find()) {
            throw new IllegalArgumentException(String.format("Unsupported color [%s]", color));
        }
        double[] coefficients = { 0.2126, 0.7152, 0.0722 };
        double luminance = 0;
        for (int i = 0; i < coefficients.length; i++) {
            double channel = Integer.parseInt(matcher.group(i + 1)) / 255.0;
            channel = channel <= 0.03928 ? channel / 12.92 : Math.pow((channel + 0.055) / 1.055, 2.4);
            luminance += coefficients[i] * channel;
        }
        return luminance;
    }
}
