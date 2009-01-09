/*
 *  Copyright (c) 2001-2009, Jean Tessier
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

import org.apache.oro.text.perl.*;

public class TestHTMLPrinterEscapeMetaCharacters extends TestHTMLPrinterBase {
    private HTMLPrinter visitor;
    private Perl5Util perl;

    private PackageNode fooPackage;
    private ClassNode fooClass;
    private FeatureNode fooFeature;
    private PackageNode barPackage;
    private ClassNode barClass;
    private FeatureNode barFeature;

    protected void setUp() throws Exception {
        super.setUp();

        SelectionCriteria scopeCriteria = new RegularExpressionSelectionCriteria("/foo/");
        SelectionCriteria filterCriteria = new RegularExpressionSelectionCriteria("/bar/");
        SelectiveTraversalStrategy strategy = new SelectiveTraversalStrategy(scopeCriteria, filterCriteria);

        visitor = new HTMLPrinter(strategy, new PrintWriter(out), FORMAT);
        perl = new Perl5Util();
    }

    public void testEscapeOpeningParenthesisInScope() throws Exception {
        setupGraph("(");

        visitor.traverseNodes(factory.getPackages().values());

        assertScopeLine("", "foo\\(", fooPackage.getName(), "foo(");
        assertScopeLine("    ", "foo\\(.Foo\\(", fooClass.getName(), "Foo(");
        assertScopeLine("        ", "foo\\(.Foo\\(.foo\\(", fooFeature.getName(), "foo(");
    }

    public void testEscapeOpeningParenthesisInInboundFilter() throws Exception {
        setupGraph("(");
        setupInboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertInboundLine("    ", "bar\\(", fooPackage.getName(), barPackage.getName());
        assertInboundLine("        ", "bar\\(.Bar\\(", fooClass.getName(), barClass.getName());
        assertInboundLine("            ", "bar\\(.Bar\\(.bar\\(", fooFeature.getName(), barFeature.getName());
    }

    public void testEscapeOpeningParenthesisInOutboundFilter() throws Exception {
        setupGraph("(");
        setupOutboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertOutboundLine("    ", "bar\\(", fooPackage.getName(), barPackage.getName());
        assertOutboundLine("        ", "bar\\(.Bar\\(", fooClass.getName(), barClass.getName());
        assertOutboundLine("            ", "bar\\(.Bar\\(.bar\\(", fooFeature.getName(), barFeature.getName());
    }

    public void testEscapeClosingParenthesisInScope() throws Exception {
        setupGraph(")");

        visitor.traverseNodes(factory.getPackages().values());

        assertScopeLine("", "foo\\)", fooPackage.getName(), "foo)");
        assertScopeLine("    ", "foo\\).Foo\\)", fooClass.getName(), "Foo)");
        assertScopeLine("        ", "foo\\).Foo\\).foo\\)", fooFeature.getName(), "foo)");
    }

    public void testEscapeClosingParenthesisInInboundFilter() throws Exception {
        setupGraph(")");
        setupInboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertInboundLine("    ", "bar\\)", fooPackage.getName(), barPackage.getName());
        assertInboundLine("        ", "bar\\).Bar\\)", fooClass.getName(), barClass.getName());
        assertInboundLine("            ", "bar\\).Bar\\).bar\\)", fooFeature.getName(), barFeature.getName());
    }

    public void testEscapeClosingParenthesisInOutboundFilter() throws Exception {
        setupGraph(")");
        setupOutboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertOutboundLine("    ", "bar\\)", fooPackage.getName(), barPackage.getName());
        assertOutboundLine("        ", "bar\\).Bar\\)", fooClass.getName(), barClass.getName());
        assertOutboundLine("            ", "bar\\).Bar\\).bar\\)", fooFeature.getName(), barFeature.getName());
    }

    public void testEscapeDollarSignInScope() throws Exception {
        setupGraph("$");

        visitor.traverseNodes(factory.getPackages().values());

        assertScopeLine("", "foo\\$", fooPackage.getName(), "foo$");
        assertScopeLine("    ", "foo\\$.Foo\\$", fooClass.getName(), "Foo$");
        assertScopeLine("        ", "foo\\$.Foo\\$.foo\\$", fooFeature.getName(), "foo$");
    }

    public void testEscapeDollarSignInInboundFilter() throws Exception {
        setupGraph("$");
        setupInboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertInboundLine("    ", "bar\\$", fooPackage.getName(), barPackage.getName());
        assertInboundLine("        ", "bar\\$.Bar\\$", fooClass.getName(), barClass.getName());
        assertInboundLine("            ", "bar\\$.Bar\\$.bar\\$", fooFeature.getName(), barFeature.getName());
    }

    public void testEscapeDollarSignInOutboundFilter() throws Exception {
        setupGraph("$");
        setupOutboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertOutboundLine("    ", "bar\\$", fooPackage.getName(), barPackage.getName());
        assertOutboundLine("        ", "bar\\$.Bar\\$", fooClass.getName(), barClass.getName());
        assertOutboundLine("            ", "bar\\$.Bar\\$.bar\\$", fooFeature.getName(), barFeature.getName());
    }

    public void testEscapeOpeningSquareBracketInScope() throws Exception {
        setupGraph("[");

        visitor.traverseNodes(factory.getPackages().values());

        assertScopeLine("", "foo\\[", fooPackage.getName(), "foo[");
        assertScopeLine("    ", "foo\\[.Foo\\[", fooClass.getName(), "Foo[");
        assertScopeLine("        ", "foo\\[.Foo\\[.foo\\[", fooFeature.getName(), "foo[");
    }

    public void testEscapeOpeningSquareBracketInInboundFilter() throws Exception {
        setupGraph("[");
        setupInboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertInboundLine("    ", "bar\\[", fooPackage.getName(), barPackage.getName());
        assertInboundLine("        ", "bar\\[.Bar\\[", fooClass.getName(), barClass.getName());
        assertInboundLine("            ", "bar\\[.Bar\\[.bar\\[", fooFeature.getName(), barFeature.getName());
    }

    public void testEscapeOpeningSquareBracketInOutboundFilter() throws Exception {
        setupGraph("[");
        setupOutboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertOutboundLine("    ", "bar\\[", fooPackage.getName(), barPackage.getName());
        assertOutboundLine("        ", "bar\\[.Bar\\[", fooClass.getName(), barClass.getName());
        assertOutboundLine("            ", "bar\\[.Bar\\[.bar\\[", fooFeature.getName(), barFeature.getName());
    }

    public void testEscapeClosingSquareBracketInScope() throws Exception {
        setupGraph("]");

        visitor.traverseNodes(factory.getPackages().values());

        assertScopeLine("", "foo\\]", fooPackage.getName(), "foo]");
        assertScopeLine("    ", "foo\\].Foo\\]", fooClass.getName(), "Foo]");
        assertScopeLine("        ", "foo\\].Foo\\].foo\\]", fooFeature.getName(), "foo]");
    }

    public void testEscapeClosingSquareBracketInInboundFilter() throws Exception {
        setupGraph("]");
        setupInboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertInboundLine("    ", "bar\\]", fooPackage.getName(), barPackage.getName());
        assertInboundLine("        ", "bar\\].Bar\\]", fooClass.getName(), barClass.getName());
        assertInboundLine("            ", "bar\\].Bar\\].bar\\]", fooFeature.getName(), barFeature.getName());
    }

    public void testEscapeClosingSquareBracketInOutboundFilter() throws Exception {
        setupGraph("]");
        setupOutboundDependencies();

        visitor.traverseNodes(factory.getPackages().values());

        assertOutboundLine("    ", "bar\\]", fooPackage.getName(), barPackage.getName());
        assertOutboundLine("        ", "bar\\].Bar\\]", fooClass.getName(), barClass.getName());
        assertOutboundLine("            ", "bar\\].Bar\\].bar\\]", fooFeature.getName(), barFeature.getName());
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

    private void assertScopeLine(String indent, String escapedName, String fullName, String tagContents) throws IOException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new StringReader(out.toString()));
            String line;
            boolean found = false;
            while ((line = in.readLine()) != null) {
                if (perl.match("/^" + indent + "<a class=\"scope\" href=\"(.*)\" id=\"(.*)\">(.*)<\\/a>/", line)) {
                    assertEquals(line, PREFIX + escapedName + SUFFIX, perl.group(1));
                    assertEquals(line, fullName, perl.group(2));
                    assertEquals(line, tagContents, perl.group(3));
                    found = true;
                }
            }
            assertTrue("Missing " + fullName, found);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private void assertInboundLine(String indent, String escapedName, String fullScopeName, String fullDependencyName) throws IOException {
        assertDependencyLine(indent, escapedName, fullScopeName + "_from_" + fullDependencyName, fullDependencyName, "&lt;--");
    }

    private void assertOutboundLine(String indent, String escapedName, String fullScopeName, String fullDependencyName) throws IOException {
        assertDependencyLine(indent, escapedName, fullScopeName + "_to_" + fullDependencyName, fullDependencyName, "--&gt;");
    }

    private void assertDependencyLine(String indent, String escapedName, String fullName, String tagContents, String marker) throws IOException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new StringReader(out.toString()));
            String line;
            boolean found = false;
            while ((line = in.readLine()) != null) {
                if (perl.match("/^" + indent + marker + " <a href=\"(.*)\" id=\"(.*)\">(.*)<\\/a>/", line)) {
                    assertEquals(line, PREFIX + escapedName + SUFFIX, perl.group(1));
                    assertEquals(line, fullName, perl.group(2));
                    assertEquals(line, tagContents, perl.group(3));
                    found = true;
                }
            }
            assertTrue("Missing " + fullName, found);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}