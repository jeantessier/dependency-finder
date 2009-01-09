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

public class TestHTMLPrinter extends TestHTMLPrinterBase {
    private HTMLPrinter visitor;

    protected void setUp() throws Exception {
        super.setUp();

        visitor = new HTMLPrinter(new PrintWriter(out), FORMAT);
    }

    public void testShowInboundsPackageTrueWithInferred() throws IOException {
        factory.createPackage("outbound").addDependency(factory.createPackage("inbound"));
        factory.createPackage("empty");

        visitor.setShowInbounds(true);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\">&lt;-- <a class=\"inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"inbound_from_outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsPackageTrueWithConfirmed() throws IOException {
        factory.createPackage("outbound", true).addDependency(factory.createPackage("inbound", true));
        factory.createPackage("empty", true);

        visitor.setShowInbounds(true);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    &lt;-- <a href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"inbound_from_outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsPackageFalseWithInferred() throws IOException {
        factory.createPackage("outbound").addDependency(factory.createPackage("inbound"));
        factory.createPackage("empty");

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsPackageFalseWithConfirmed() throws IOException {
        factory.createPackage("outbound", true).addDependency(factory.createPackage("inbound", true));
        factory.createPackage("empty", true);

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsClassTrueWithInferred() throws IOException {
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createClass("empty.Empty");

        visitor.setShowInbounds(true);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\">&lt;-- <a class=\"inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"inbound.Inbound_from_outbound.Outbound\">outbound.Outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsClassTrueWithConfirmed() throws IOException {
        factory.createClass("outbound.Outbound", true).addDependency(factory.createClass("inbound.Inbound", true));
        factory.createClass("empty.Empty", true);

        visitor.setShowInbounds(true);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        &lt;-- <a href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"inbound.Inbound_from_outbound.Outbound\">outbound.Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsClassFalseWithInferred() throws IOException {
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createClass("empty.Empty");

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsClassFalseWithConfirmed() throws IOException {
        factory.createClass("outbound.Outbound", true).addDependency(factory.createClass("inbound.Inbound", true));
        factory.createClass("empty.Empty", true);

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsInnerClass() throws IOException {
        factory.createClass("outbound.Outbound$Outbound", true).addDependency(factory.createClass("inbound.Inbound$Inbound", true));
        factory.createClass("empty.Empty$Empty", true);

        visitor.setShowInbounds(true);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "empty.Empty\\$Empty" + SUFFIX + "\" id=\"empty.Empty$Empty\">Empty$Empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound\\$Inbound" + SUFFIX + "\" id=\"inbound.Inbound$Inbound\">Inbound$Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        &lt;-- <a href=\"" + PREFIX + "outbound.Outbound\\$Outbound" + SUFFIX + "\" id=\"inbound.Inbound$Inbound_from_outbound.Outbound$Outbound\">outbound.Outbound$Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound\\$Outbound" + SUFFIX + "\" id=\"outbound.Outbound$Outbound\">Outbound$Outbound</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsFeatureTrueWithInferred() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createFeature("empty.Empty.empty()");

        visitor.setShowInbounds(true);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty.Empty.empty\\(\\)" + SUFFIX + "\" id=\"empty.Empty.empty()\">empty()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <span class=\"inferred\">&lt;-- <a class=\"inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()_from_outbound.Outbound.outbound()\">outbound.Outbound.outbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsFeatureTrueWithConfirmed() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()", true).addDependency(factory.createFeature("inbound.Inbound.inbound()", true));
        factory.createFeature("empty.Empty.empty()", true);

        visitor.setShowInbounds(true);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "empty.Empty.empty\\(\\)" + SUFFIX + "\" id=\"empty.Empty.empty()\">empty()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            &lt;-- <a href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()_from_outbound.Outbound.outbound()\">outbound.Outbound.outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsFeatureFalseWithInferred() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createFeature("empty.Empty.empty()");

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty.Empty.empty\\(\\)" + SUFFIX + "\" id=\"empty.Empty.empty()\">empty()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsFeatureFalseWithConfirmed() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()", true).addDependency(factory.createFeature("inbound.Inbound.inbound()", true));
        factory.createFeature("empty.Empty.empty()", true);

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "empty.Empty.empty\\(\\)" + SUFFIX + "\" id=\"empty.Empty.empty()\">empty()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsInnerClassFeature() throws IOException {
        factory.createFeature("outbound.Outbound$Outbound.outbound()", true).addDependency(factory.createFeature("inbound.Inbound$Inbound.inbound()", true));
        factory.createFeature("empty.Empty$Empty.empty()", true);

        visitor.setShowInbounds(true);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "empty.Empty\\$Empty" + SUFFIX + "\" id=\"empty.Empty$Empty\">Empty$Empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "empty.Empty\\$Empty.empty\\(\\)" + SUFFIX + "\" id=\"empty.Empty$Empty.empty()\">empty()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound\\$Inbound" + SUFFIX + "\" id=\"inbound.Inbound$Inbound\">Inbound$Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound\\$Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound$Inbound.inbound()\">inbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            &lt;-- <a href=\"" + PREFIX + "outbound.Outbound\\$Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound$Inbound.inbound()_from_outbound.Outbound$Outbound.outbound()\">outbound.Outbound$Outbound.outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound\\$Outbound" + SUFFIX + "\" id=\"outbound.Outbound$Outbound\">Outbound$Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound\\$Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound$Outbound.outbound()\">outbound()</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsPackageTrueWithInferred() throws IOException {
        factory.createPackage("outbound").addDependency(factory.createPackage("inbound"));
        factory.createPackage("empty");

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(true);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\">--&gt; <a class=\"inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"outbound_to_inbound\">inbound</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsPackageTrueWithConfirmed() throws IOException {
        factory.createPackage("outbound", true).addDependency(factory.createPackage("inbound", true));
        factory.createPackage("empty", true);

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(true);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    --&gt; <a href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"outbound_to_inbound\">inbound</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsPackageFalseWithInferred() throws IOException {
        factory.createPackage("outbound").addDependency(factory.createPackage("inbound"));
        factory.createPackage("empty");

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsPackageFalseWithConfirmed() throws IOException {
        factory.createPackage("outbound", true).addDependency(factory.createPackage("inbound", true));
        factory.createPackage("empty", true);

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsClassTrueWithInferred() throws IOException {
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createClass("empty.Empty");

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(true);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\">--&gt; <a class=\"inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"outbound.Outbound_to_inbound.Inbound\">inbound.Inbound</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsClassTrueWithConfirmed() throws IOException {
        factory.createClass("outbound.Outbound", true).addDependency(factory.createClass("inbound.Inbound", true));
        factory.createClass("empty.Empty", true);

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(true);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        --&gt; <a href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"outbound.Outbound_to_inbound.Inbound\">inbound.Inbound</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsClassFalseWithInferred() throws IOException {
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createClass("empty.Empty");

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsClassFalseWithConfirmed() throws IOException {
        factory.createClass("outbound.Outbound", true).addDependency(factory.createClass("inbound.Inbound", true));
        factory.createClass("empty.Empty", true);

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsInnerClass() throws IOException {
        factory.createClass("outbound.Outbound$Outbound", true).addDependency(factory.createClass("inbound.Inbound$Inbound", true));
        factory.createClass("empty.Empty$Empty", true);

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(true);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "empty.Empty\\$Empty" + SUFFIX + "\" id=\"empty.Empty$Empty\">Empty$Empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound\\$Inbound" + SUFFIX + "\" id=\"inbound.Inbound$Inbound\">Inbound$Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound\\$Outbound" + SUFFIX + "\" id=\"outbound.Outbound$Outbound\">Outbound$Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        --&gt; <a href=\"" + PREFIX + "inbound.Inbound\\$Inbound" + SUFFIX + "\" id=\"outbound.Outbound$Outbound_to_inbound.Inbound$Inbound\">inbound.Inbound$Inbound</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsFeatureTrueWithInferred() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createFeature("empty.Empty.empty()");

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(true);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty.Empty.empty\\(\\)" + SUFFIX + "\" id=\"empty.Empty.empty()\">empty()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <span class=\"inferred\">--&gt; <a class=\"inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()_to_inbound.Inbound.inbound()\">inbound.Inbound.inbound()</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsFeatureTrueWithConfirmed() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()", true).addDependency(factory.createFeature("inbound.Inbound.inbound()", true));
        factory.createFeature("empty.Empty.empty()", true);

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(true);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "empty.Empty.empty\\(\\)" + SUFFIX + "\" id=\"empty.Empty.empty()\">empty()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            --&gt; <a href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()_to_inbound.Inbound.inbound()\">inbound.Inbound.inbound()</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsFeatureFalseWithInferred() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createFeature("empty.Empty.empty()");

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty.Empty.empty\\(\\)" + SUFFIX + "\" id=\"empty.Empty.empty()\">empty()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsFeatureFalseWithConfirmed() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()", true).addDependency(factory.createFeature("inbound.Inbound.inbound()", true));
        factory.createFeature("empty.Empty.empty()", true);

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "empty.Empty.empty\\(\\)" + SUFFIX + "\" id=\"empty.Empty.empty()\">empty()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsInnerClassFeature() throws IOException {
        factory.createFeature("outbound.Outbound$Outbound.outbound()", true).addDependency(factory.createFeature("inbound.Inbound$Inbound.inbound()", true));
        factory.createFeature("empty.Empty$Empty.empty()", true);

        visitor.setShowInbounds(false);
        visitor.setShowOutbounds(true);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "empty.Empty\\$Empty" + SUFFIX + "\" id=\"empty.Empty$Empty\">Empty$Empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "empty.Empty\\$Empty.empty\\(\\)" + SUFFIX + "\" id=\"empty.Empty$Empty.empty()\">empty()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound\\$Inbound" + SUFFIX + "\" id=\"inbound.Inbound$Inbound\">Inbound$Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound\\$Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound$Inbound.inbound()\">inbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound\\$Outbound" + SUFFIX + "\" id=\"outbound.Outbound$Outbound\">Outbound$Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound\\$Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound$Outbound.outbound()\">outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            --&gt; <a href=\"" + PREFIX + "inbound.Inbound\\$Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound$Outbound.outbound()_to_inbound.Inbound$Inbound.inbound()\">inbound.Inbound$Inbound.inbound()</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyPackageTrueWithInferred() throws IOException {
        factory.createPackage("outbound").addDependency(factory.createPackage("inbound"));
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createPackage("empty");

        visitor.setShowEmptyNodes(true);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\">&lt;-- <a class=\"inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"inbound_from_outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\">&lt;-- <a class=\"inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"inbound.Inbound_from_outbound.Outbound\">outbound.Outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <span class=\"inferred\">&lt;-- <a class=\"inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()_from_outbound.Outbound.outbound()\">outbound.Outbound.outbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\">--&gt; <a class=\"inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"outbound_to_inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\">--&gt; <a class=\"inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"outbound.Outbound_to_inbound.Inbound\">inbound.Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <span class=\"inferred\">--&gt; <a class=\"inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()_to_inbound.Inbound.inbound()\">inbound.Inbound.inbound()</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testSetFlagInferredToFalse() throws IOException {
        factory.createPackage("outbound").addDependency(factory.createPackage("inbound"));
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createPackage("empty");

        visitor.setShowInferred(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    &lt;-- <a href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"inbound_from_outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        &lt;-- <a href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"inbound.Inbound_from_outbound.Outbound\">outbound.Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            &lt;-- <a href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()_from_outbound.Outbound.outbound()\">outbound.Outbound.outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    --&gt; <a href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"outbound_to_inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        --&gt; <a href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"outbound.Outbound_to_inbound.Inbound\">inbound.Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            --&gt; <a href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()_to_inbound.Inbound.inbound()\">inbound.Inbound.inbound()</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyPackageTrueWithConfirmed() throws IOException {
        factory.createPackage("outbound", true).addDependency(factory.createPackage("inbound", true));
        factory.createClass("outbound.Outbound", true).addDependency(factory.createClass("inbound.Inbound", true));
        factory.createFeature("outbound.Outbound.outbound()", true).addDependency(factory.createFeature("inbound.Inbound.inbound()", true));
        factory.createPackage("empty", true);

        visitor.setShowEmptyNodes(true);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    &lt;-- <a href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"inbound_from_outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        &lt;-- <a href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"inbound.Inbound_from_outbound.Outbound\">outbound.Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            &lt;-- <a href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()_from_outbound.Outbound.outbound()\">outbound.Outbound.outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    --&gt; <a href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"outbound_to_inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        --&gt; <a href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"outbound.Outbound_to_inbound.Inbound\">inbound.Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            --&gt; <a href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()_to_inbound.Inbound.inbound()\">inbound.Inbound.inbound()</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyPackageFalseWithInferred() throws IOException {
        factory.createPackage("outbound").addDependency(factory.createPackage("inbound"));
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createPackage("empty");

        visitor.setShowEmptyNodes(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\">&lt;-- <a class=\"inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"inbound_from_outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\">&lt;-- <a class=\"inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"inbound.Inbound_from_outbound.Outbound\">outbound.Outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <span class=\"inferred\">&lt;-- <a class=\"inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()_from_outbound.Outbound.outbound()\">outbound.Outbound.outbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\">--&gt; <a class=\"inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"outbound_to_inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\">--&gt; <a class=\"inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"outbound.Outbound_to_inbound.Inbound\">inbound.Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <span class=\"inferred\">--&gt; <a class=\"inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()_to_inbound.Inbound.inbound()\">inbound.Inbound.inbound()</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyPackageFalseWithConfirmed() throws IOException {
        factory.createPackage("outbound", true).addDependency(factory.createPackage("inbound", true));
        factory.createClass("outbound.Outbound", true).addDependency(factory.createClass("inbound.Inbound", true));
        factory.createFeature("outbound.Outbound.outbound()", true).addDependency(factory.createFeature("inbound.Inbound.inbound()", true));
        factory.createPackage("empty", true);

        visitor.setShowEmptyNodes(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    &lt;-- <a href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"inbound_from_outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        &lt;-- <a href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"inbound.Inbound_from_outbound.Outbound\">outbound.Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            &lt;-- <a href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()_from_outbound.Outbound.outbound()\">outbound.Outbound.outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    --&gt; <a href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"outbound_to_inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        --&gt; <a href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"outbound.Outbound_to_inbound.Inbound\">inbound.Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            --&gt; <a href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()_to_inbound.Inbound.inbound()\">inbound.Inbound.inbound()</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyClassTrueWithInferred() throws IOException {
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createClass("empty.Empty");

        visitor.setShowEmptyNodes(true);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\">&lt;-- <a class=\"inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"inbound.Inbound_from_outbound.Outbound\">outbound.Outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <span class=\"inferred\">&lt;-- <a class=\"inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()_from_outbound.Outbound.outbound()\">outbound.Outbound.outbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\">--&gt; <a class=\"inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"outbound.Outbound_to_inbound.Inbound\">inbound.Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <span class=\"inferred\">--&gt; <a class=\"inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()_to_inbound.Inbound.inbound()\">inbound.Inbound.inbound()</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyClassTrueWithConfirmed() throws IOException {
        factory.createClass("outbound.Outbound", true).addDependency(factory.createClass("inbound.Inbound", true));
        factory.createFeature("outbound.Outbound.outbound()", true).addDependency(factory.createFeature("inbound.Inbound.inbound()", true));
        factory.createClass("empty.Empty", true);

        visitor.setShowEmptyNodes(true);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        &lt;-- <a href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"inbound.Inbound_from_outbound.Outbound\">outbound.Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            &lt;-- <a href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()_from_outbound.Outbound.outbound()\">outbound.Outbound.outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        --&gt; <a href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"outbound.Outbound_to_inbound.Inbound\">inbound.Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            --&gt; <a href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()_to_inbound.Inbound.inbound()\">inbound.Inbound.inbound()</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyClassFalseWithInferred() throws IOException {
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createClass("empty.Empty");

        visitor.setShowEmptyNodes(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\">&lt;-- <a class=\"inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"inbound.Inbound_from_outbound.Outbound\">outbound.Outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <span class=\"inferred\">&lt;-- <a class=\"inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()_from_outbound.Outbound.outbound()\">outbound.Outbound.outbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\">--&gt; <a class=\"inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"outbound.Outbound_to_inbound.Inbound\">inbound.Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <span class=\"inferred\">--&gt; <a class=\"inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()_to_inbound.Inbound.inbound()\">inbound.Inbound.inbound()</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyClassFalseWithConfirmed() throws IOException {
        factory.createClass("outbound.Outbound", true).addDependency(factory.createClass("inbound.Inbound", true));
        factory.createFeature("outbound.Outbound.outbound()", true).addDependency(factory.createFeature("inbound.Inbound.inbound()", true));
        factory.createClass("empty.Empty", true);

        visitor.setShowEmptyNodes(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        &lt;-- <a href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"inbound.Inbound_from_outbound.Outbound\">outbound.Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            &lt;-- <a href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()_from_outbound.Outbound.outbound()\">outbound.Outbound.outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        --&gt; <a href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"outbound.Outbound_to_inbound.Inbound\">inbound.Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            --&gt; <a href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()_to_inbound.Inbound.inbound()\">inbound.Inbound.inbound()</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyFeatureTrueWithInferred() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createFeature("empty.Empty.empty()");

        visitor.setShowEmptyNodes(true);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "empty.Empty.empty\\(\\)" + SUFFIX + "\" id=\"empty.Empty.empty()\">empty()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <span class=\"inferred\">&lt;-- <a class=\"inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()_from_outbound.Outbound.outbound()\">outbound.Outbound.outbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <span class=\"inferred\">--&gt; <a class=\"inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()_to_inbound.Inbound.inbound()\">inbound.Inbound.inbound()</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyFeatureTrueWithConfirmed() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()", true).addDependency(factory.createFeature("inbound.Inbound.inbound()", true));
        factory.createFeature("empty.Empty.empty()", true);

        visitor.setShowEmptyNodes(true);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "empty.Empty" + SUFFIX + "\" id=\"empty.Empty\">Empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "empty.Empty.empty\\(\\)" + SUFFIX + "\" id=\"empty.Empty.empty()\">empty()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            &lt;-- <a href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()_from_outbound.Outbound.outbound()\">outbound.Outbound.outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            --&gt; <a href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()_to_inbound.Inbound.inbound()\">inbound.Inbound.inbound()</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyFeatureFalseWithInferred() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createFeature("empty.Empty.empty()");

        visitor.setShowEmptyNodes(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <span class=\"inferred\">&lt;-- <a class=\"inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()_from_outbound.Outbound.outbound()\">outbound.Outbound.outbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "<span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <span class=\"inferred\"><a class=\"scope inferred\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a> *</span>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <span class=\"inferred\">--&gt; <a class=\"inferred\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()_to_inbound.Inbound.inbound()\">inbound.Inbound.inbound()</a> *</span>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyFeatureFalseWithConfirmed() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()", true).addDependency(factory.createFeature("inbound.Inbound.inbound()", true));
        factory.createFeature("empty.Empty.empty()", true);

        visitor.setShowEmptyNodes(false);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            &lt;-- <a href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()_from_outbound.Outbound.outbound()\">outbound.Outbound.outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            --&gt; <a href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()_to_inbound.Inbound.inbound()\">inbound.Inbound.inbound()</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInnerClass() throws IOException {
        factory.createPackage("outbound", true).addDependency(factory.createPackage("inbound", true));
        factory.createClass("outbound.Outbound$Outbound", true).addDependency(factory.createClass("inbound.Inbound$Inbound", true));
        factory.createFeature("outbound.Outbound$Outbound.outbound()", true).addDependency(factory.createFeature("inbound.Inbound$Inbound.inbound()", true));
        factory.createFeature("empty.Empty$Empty.empty()", true);

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "empty" + SUFFIX + "\" id=\"empty\">empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "empty.Empty\\$Empty" + SUFFIX + "\" id=\"empty.Empty$Empty\">Empty$Empty</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "empty.Empty\\$Empty.empty\\(\\)" + SUFFIX + "\" id=\"empty.Empty$Empty.empty()\">empty()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    &lt;-- <a href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"inbound_from_outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound\\$Inbound" + SUFFIX + "\" id=\"inbound.Inbound$Inbound\">Inbound$Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        &lt;-- <a href=\"" + PREFIX + "outbound.Outbound\\$Outbound" + SUFFIX + "\" id=\"inbound.Inbound$Inbound_from_outbound.Outbound$Outbound\">outbound.Outbound$Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound\\$Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound$Inbound.inbound()\">inbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            &lt;-- <a href=\"" + PREFIX + "outbound.Outbound\\$Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound$Inbound.inbound()_from_outbound.Outbound$Outbound.outbound()\">outbound.Outbound$Outbound.outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    --&gt; <a href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"outbound_to_inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound\\$Outbound" + SUFFIX + "\" id=\"outbound.Outbound$Outbound\">Outbound$Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        --&gt; <a href=\"" + PREFIX + "inbound.Inbound\\$Inbound" + SUFFIX + "\" id=\"outbound.Outbound$Outbound_to_inbound.Inbound$Inbound\">inbound.Inbound$Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound\\$Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound$Outbound.outbound()\">outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            --&gt; <a href=\"" + PREFIX + "inbound.Inbound\\$Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound$Outbound.outbound()_to_inbound.Inbound$Inbound.inbound()\">inbound.Inbound$Inbound.inbound()</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowBidirectionalDependencies() throws IOException {
        factory.createPackage("outbound", true).addDependency(factory.createPackage("inbound", true));
        factory.createClass("outbound.Outbound", true).addDependency(factory.createClass("inbound.Inbound", true));
        factory.createFeature("outbound.Outbound.outbound()", true).addDependency(factory.createFeature("inbound.Inbound.inbound()", true));
        factory.createPackage("inbound", true).addDependency(factory.createPackage("outbound", true));
        factory.createClass("inbound.Inbound", true).addDependency(factory.createClass("outbound.Outbound", true));
        factory.createFeature("inbound.Inbound.inbound()", true).addDependency(factory.createFeature("outbound.Outbound.outbound()", true));

        visitor.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    &lt;-&gt; <a href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"inbound_bidirectional_outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"inbound.Inbound\">Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        &lt;-&gt; <a href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"inbound.Inbound_bidirectional_outbound.Outbound\">outbound.Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()\">inbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            &lt;-&gt; <a href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"inbound.Inbound.inbound()_bidirectional_outbound.Outbound.outbound()\">outbound.Outbound.outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "outbound" + SUFFIX + "\" id=\"outbound\">outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    &lt;-&gt; <a href=\"" + PREFIX + "inbound" + SUFFIX + "\" id=\"outbound_bidirectional_inbound\">inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound" + SUFFIX + "\" id=\"outbound.Outbound\">Outbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        &lt;-&gt; <a href=\"" + PREFIX + "inbound.Inbound" + SUFFIX + "\" id=\"outbound.Outbound_bidirectional_inbound.Inbound\">inbound.Inbound</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <a class=\"scope\" href=\"" + PREFIX + "outbound.Outbound.outbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()\">outbound()</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            &lt;-&gt; <a href=\"" + PREFIX + "inbound.Inbound.inbound\\(\\)" + SUFFIX + "\" id=\"outbound.Outbound.outbound()_bidirectional_inbound.Inbound.inbound()\">inbound.Inbound.inbound()</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }
}
