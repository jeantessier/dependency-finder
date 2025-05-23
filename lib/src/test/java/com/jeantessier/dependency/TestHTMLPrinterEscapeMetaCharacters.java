/*
 *  Copyright (c) 2001-2025, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeantessier.dependency;

import java.io.*;
import java.util.regex.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestHTMLPrinterEscapeMetaCharacters extends TestHTMLPrinterBase {
    private PackageNode fooPackage;
    private ClassNode fooClass;
    private FeatureNode fooFeature;
    private PackageNode barPackage;
    private ClassNode barClass;
    private FeatureNode barFeature;

    private final SelectionCriteria scopeCriteria = new RegularExpressionSelectionCriteria("/foo/");
    private final SelectionCriteria filterCriteria = new RegularExpressionSelectionCriteria("/bar/");
    private final TraversalStrategy strategy = new SelectiveTraversalStrategy(scopeCriteria, filterCriteria);

    private final HTMLPrinter visitor = new HTMLPrinter(strategy, new PrintWriter(writer), FORMAT);

    @Test
    void testEscapeOpeningParenthesisInScope() {
        setupGraph("(");

        visitor.traverseNodes(factory.getPackages().values());

        assertScopeLine("", "foo%5C%28", fooPackage.getName(), "foo(");
        assertScopeLine("    ", "foo%5C%28.Foo%5C%28", fooClass.getName(), "Foo(");
        assertScopeLine("        ", "foo%5C%28.Foo%5C%28.foo%5C%28", fooFeature.getName(), "foo(");
    }

    @Test
    void testEscapeOpeningParenthesisInInboundFilter() {
        setupGraph("(");
        setupInboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertInboundLine("    ", "bar%5C%28", fooPackage.getName(), barPackage.getName());
        assertInboundLine("        ", "bar%5C%28.Bar%5C%28", fooClass.getName(), barClass.getName());
        assertInboundLine("            ", "bar%5C%28.Bar%5C%28.bar%5C%28", fooFeature.getName(), barFeature.getName());
    }

    @Test
    void testEscapeOpeningParenthesisInOutboundFilter() {
        setupGraph("(");
        setupOutboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertOutboundLine("    ", "bar%5C%28", fooPackage.getName(), barPackage.getName());
        assertOutboundLine("        ", "bar%5C%28.Bar%5C%28", fooClass.getName(), barClass.getName());
        assertOutboundLine("            ", "bar%5C%28.Bar%5C%28.bar%5C%28", fooFeature.getName(), barFeature.getName());
    }

    @Test
    void testEscapeClosingParenthesisInScope() {
        setupGraph(")");

        visitor.traverseNodes(factory.getPackages().values());

        assertScopeLine("", "foo%5C%29", fooPackage.getName(), "foo)");
        assertScopeLine("    ", "foo%5C%29.Foo%5C%29", fooClass.getName(), "Foo)");
        assertScopeLine("        ", "foo%5C%29.Foo%5C%29.foo%5C%29", fooFeature.getName(), "foo)");
    }

    @Test
    void testEscapeClosingParenthesisInInboundFilter() {
        setupGraph(")");
        setupInboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertInboundLine("    ", "bar%5C%29", fooPackage.getName(), barPackage.getName());
        assertInboundLine("        ", "bar%5C%29.Bar%5C%29", fooClass.getName(), barClass.getName());
        assertInboundLine("            ", "bar%5C%29.Bar%5C%29.bar%5C%29", fooFeature.getName(), barFeature.getName());
    }

    @Test
    void testEscapeClosingParenthesisInOutboundFilter() {
        setupGraph(")");
        setupOutboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertOutboundLine("    ", "bar%5C%29", fooPackage.getName(), barPackage.getName());
        assertOutboundLine("        ", "bar%5C%29.Bar%5C%29", fooClass.getName(), barClass.getName());
        assertOutboundLine("            ", "bar%5C%29.Bar%5C%29.bar%5C%29", fooFeature.getName(), barFeature.getName());
    }

    @Test
    void testEscapeDollarSignInScope() {
        setupGraph("$");

        visitor.traverseNodes(factory.getPackages().values());

        assertScopeLine("", "foo%5C%24", fooPackage.getName(), "foo$");
        assertScopeLine("    ", "foo%5C%24.Foo%5C%24", fooClass.getName(), "Foo$");
        assertScopeLine("        ", "foo%5C%24.Foo%5C%24.foo%5C%24", fooFeature.getName(), "foo$");
    }

    @Test
    void testEscapeDollarSignInInboundFilter() {
        setupGraph("$");
        setupInboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertInboundLine("    ", "bar%5C%24", fooPackage.getName(), barPackage.getName());
        assertInboundLine("        ", "bar%5C%24.Bar%5C%24", fooClass.getName(), barClass.getName());
        assertInboundLine("            ", "bar%5C%24.Bar%5C%24.bar%5C%24", fooFeature.getName(), barFeature.getName());
    }

    @Test
    void testEscapeDollarSignInOutboundFilter() {
        setupGraph("$");
        setupOutboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertOutboundLine("    ", "bar%5C%24", fooPackage.getName(), barPackage.getName());
        assertOutboundLine("        ", "bar%5C%24.Bar%5C%24", fooClass.getName(), barClass.getName());
        assertOutboundLine("            ", "bar%5C%24.Bar%5C%24.bar%5C%24", fooFeature.getName(), barFeature.getName());
    }

    @Test
    void testEscapeOpeningSquareBracketInScope() {
        setupGraph("[");

        visitor.traverseNodes(factory.getPackages().values());

        assertScopeLine("", "foo%5C%5B", fooPackage.getName(), "foo[");
        assertScopeLine("    ", "foo%5C%5B.Foo%5C%5B", fooClass.getName(), "Foo[");
        assertScopeLine("        ", "foo%5C%5B.Foo%5C%5B.foo%5C%5B", fooFeature.getName(), "foo[");
    }

    @Test
    void testEscapeOpeningSquareBracketInInboundFilter() {
        setupGraph("[");
        setupInboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertInboundLine("    ", "bar%5C%5B", fooPackage.getName(), barPackage.getName());
        assertInboundLine("        ", "bar%5C%5B.Bar%5C%5B", fooClass.getName(), barClass.getName());
        assertInboundLine("            ", "bar%5C%5B.Bar%5C%5B.bar%5C%5B", fooFeature.getName(), barFeature.getName());
    }

    @Test
    void testEscapeOpeningSquareBracketInOutboundFilter() {
        setupGraph("[");
        setupOutboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertOutboundLine("    ", "bar%5C%5B", fooPackage.getName(), barPackage.getName());
        assertOutboundLine("        ", "bar%5C%5B.Bar%5C%5B", fooClass.getName(), barClass.getName());
        assertOutboundLine("            ", "bar%5C%5B.Bar%5C%5B.bar%5C%5B", fooFeature.getName(), barFeature.getName());
    }

    @Test
    void testEscapeClosingSquareBracketInScope() {
        setupGraph("]");

        visitor.traverseNodes(factory.getPackages().values());

        assertScopeLine("", "foo%5C%5D", fooPackage.getName(), "foo]");
        assertScopeLine("    ", "foo%5C%5D.Foo%5C%5D", fooClass.getName(), "Foo]");
        assertScopeLine("        ", "foo%5C%5D.Foo%5C%5D.foo%5C%5D", fooFeature.getName(), "foo]");
    }

    @Test
    void testEscapeClosingSquareBracketInInboundFilter() {
        setupGraph("]");
        setupInboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertInboundLine("    ", "bar%5C%5D", fooPackage.getName(), barPackage.getName());
        assertInboundLine("        ", "bar%5C%5D.Bar%5C%5D", fooClass.getName(), barClass.getName());
        assertInboundLine("            ", "bar%5C%5D.Bar%5C%5D.bar%5C%5D", fooFeature.getName(), barFeature.getName());
    }

    @Test
    void testEscapeClosingSquareBracketInOutboundFilter() {
        setupGraph("]");
        setupOutboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertOutboundLine("    ", "bar%5C%5D", fooPackage.getName(), barPackage.getName());
        assertOutboundLine("        ", "bar%5C%5D.Bar%5C%5D", fooClass.getName(), barClass.getName());
        assertOutboundLine("            ", "bar%5C%5D.Bar%5C%5D.bar%5C%5D", fooFeature.getName(), barFeature.getName());
    }

    private void setupGraph(String specialCharacter) {
        fooPackage = factory.createPackage("foo" + specialCharacter, true);
        fooClass = factory.createClass("foo" + specialCharacter + "." + "Foo" + specialCharacter, true);
        fooFeature = factory.createFeature("foo" + specialCharacter + "." + "Foo" + specialCharacter + "." + "foo" + specialCharacter, true);
        barPackage = factory.createPackage("bar" + specialCharacter, true);
        barClass = factory.createClass("bar" + specialCharacter + "." + "Bar" + specialCharacter, true);
        barFeature = factory.createFeature("bar" + specialCharacter + "." + "Bar" + specialCharacter + "." + "bar" + specialCharacter, true);
    }

    private void setupInboundDependencies() {
        barPackage.addDependency(fooPackage);
        barClass.addDependency(fooClass);
        barFeature.addDependency(fooFeature);
    }

    private void setupOutboundDependencies() {
        fooPackage.addDependency(barPackage);
        fooClass.addDependency(barClass);
        fooFeature.addDependency(barFeature);
    }

    private void assertScopeLine(String indent, String escapedName, String fullName, String tagContents) {
        var regex = Pattern.compile("^" + indent + "<a class=\"scope\" href=\"(.*)\" id=\"(.*)\">(.*)</a>");

        assertTrue(
                writer.toString().lines()
                        .map(regex::matcher)
                        .filter(Matcher::find)
                        .anyMatch(matcher -> {
                            assertEquals(PREFIX + escapedName + SUFFIX, matcher.group(1));
                            assertEquals(fullName, matcher.group(2));
                            assertEquals(tagContents, matcher.group(3));
                            return true;
                        })
        );
    }

    private void assertInboundLine(String indent, String escapedName, String fullScopeName, String fullDependencyName) {
        assertDependencyLine(indent, escapedName, fullScopeName + "_from_" + fullDependencyName, fullDependencyName, "&lt;--");
    }

    private void assertOutboundLine(String indent, String escapedName, String fullScopeName, String fullDependencyName) {
        assertDependencyLine(indent, escapedName, fullScopeName + "_to_" + fullDependencyName, fullDependencyName, "--&gt;");
    }

    private void assertDependencyLine(String indent, String escapedName, String fullName, String tagContents, String marker) {
        var regex = Pattern.compile("^" + indent + marker + " <a href=\"(.*)\" id=\"(.*)\">(.*)</a>");

        assertTrue(
                writer.toString().lines()
                        .map(regex::matcher)
                        .filter(Matcher::find)
                        .anyMatch(matcher -> {
                            assertEquals(PREFIX + escapedName + SUFFIX, matcher.group(1));
                            assertEquals(fullName, matcher.group(2));
                            assertEquals(tagContents, matcher.group(3));
                            return true;
                        })
        );
    }
}
