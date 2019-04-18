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
import java.util.*;

import javax.xml.parsers.*;

import junit.framework.*;
import org.xml.sax.*;
import org.apache.oro.text.perl.*;

public class TestXMLCyclePrinter extends TestCase implements ErrorHandler {
    private static final String SPECIFIC_ENCODING   = "iso-latin-1";
    private static final String SPECIFIC_DTD_PREFIX = "./etc";

    private XMLReader    reader;
    private Perl5Util    perl;
    private NodeFactory  factory;
    private StringWriter out;

    private Node a_package;
    private Node b_package;
    private Node c_package;

    protected void setUp() throws Exception {
	boolean validate = Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE");

        reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        reader.setFeature("http://xml.org/sax/features/validation", validate);
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", validate);
        reader.setErrorHandler(this);

        perl = new Perl5Util();

        factory = new NodeFactory();
        out     = new StringWriter();

        a_package = factory.createPackage("a");
        b_package = factory.createPackage("b");
        c_package = factory.createPackage("c");
    }

    public void testDefaultDTDPrefix() {
        XMLCyclePrinter printer = new XMLCyclePrinter(new PrintWriter(out));

        String xmlDocument = out.toString();
        assertTrue(xmlDocument + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument));
        assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"" + XMLPrinter.DEFAULT_DTD_PREFIX + "\"", perl.group(1).startsWith(XMLPrinter.DEFAULT_DTD_PREFIX));

        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXException ex) {
            // Ignore
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    public void testSpecificDTDPrefix() {
        XMLCyclePrinter printer = new XMLCyclePrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

        String xmlDocument = out.toString();
        assertTrue(xmlDocument + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument));
        assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"./etc\"", perl.group(1).startsWith(SPECIFIC_DTD_PREFIX));

        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXException ex) {
            // Ignore
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    public void testDefaultEncoding() {
        XMLCyclePrinter printer = new XMLCyclePrinter(new PrintWriter(out));

        String xmlDocument = out.toString();
        assertTrue(xmlDocument + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xmlDocument));
        assertEquals("Encoding", XMLPrinter.DEFAULT_ENCODING, perl.group(1));

        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXException ex) {
            // Ignore
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    public void testSpecificEncoding() {
        XMLCyclePrinter printer = new XMLCyclePrinter(new PrintWriter(out), SPECIFIC_ENCODING, XMLPrinter.DEFAULT_DTD_PREFIX);

        String xmlDocument = out.toString();
        assertTrue(xmlDocument + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xmlDocument));
        assertEquals("Encoding", SPECIFIC_ENCODING, perl.group(1));

        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXException ex) {
            // Ignore
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    public void testVisitCyclesWith2NodeCycle() throws IOException {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(a_package);
        nodes.add(b_package);
        Cycle cycle = new Cycle(nodes);

        XMLCyclePrinter printer = new XMLCyclePrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.visitCycles(Collections.singletonList(cycle));
        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/cycles.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<cycles>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <cycle>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <node type=\"package\">a</node>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <node type=\"package\">b</node>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </cycle>", in.readLine());
        assertEquals("line " + ++lineNumber, "</cycles>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testVisitCyclesWith2NodeCycleWithIndentText() throws IOException {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(a_package);
        nodes.add(b_package);
        Cycle cycle = new Cycle(nodes);

        CyclePrinter printer = new XMLCyclePrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setIndentText("*");
        printer.visitCycles(Collections.singletonList(cycle));
        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/cycles.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<cycles>", in.readLine());
        assertEquals("line " + ++lineNumber, "*<cycle>", in.readLine());
        assertEquals("line " + ++lineNumber, "**<node type=\"package\">a</node>", in.readLine());
        assertEquals("line " + ++lineNumber, "**<node type=\"package\">b</node>", in.readLine());
        assertEquals("line " + ++lineNumber, "*</cycle>", in.readLine());
        assertEquals("line " + ++lineNumber, "</cycles>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testVisitCycleWith3NodeCycle() throws IOException {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(a_package);
        nodes.add(b_package);
        nodes.add(c_package);
        Cycle cycle = new Cycle(nodes);

        XMLCyclePrinter printer = new XMLCyclePrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.visitCycles(Collections.singletonList(cycle));
        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/cycles.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<cycles>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <cycle>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <node type=\"package\">a</node>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <node type=\"package\">b</node>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <node type=\"package\">c</node>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </cycle>", in.readLine());
        assertEquals("line " + ++lineNumber, "</cycles>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testVisitCycleWith2Cycles() throws IOException {
        List<Cycle> cycles = new ArrayList<Cycle>();

        List<Node> nodes1 = new ArrayList<Node>();
        nodes1.add(a_package);
        nodes1.add(b_package);
        cycles.add(new Cycle(nodes1));

        List<Node> nodes2 = new ArrayList<Node>();
        nodes2.add(a_package);
        nodes2.add(b_package);
        nodes2.add(c_package);
        cycles.add(new Cycle(nodes2));

        XMLCyclePrinter printer = new XMLCyclePrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.visitCycles(cycles);
        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/cycles.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<cycles>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <cycle>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <node type=\"package\">a</node>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <node type=\"package\">b</node>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </cycle>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <cycle>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <node type=\"package\">a</node>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <node type=\"package\">b</node>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <node type=\"package\">c</node>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </cycle>", in.readLine());
        assertEquals("line " + ++lineNumber, "</cycles>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void error(SAXParseException ex) {
        // Ignore
    }

    public void fatalError(SAXParseException ex) {
        // Ignore
    }

    public void warning(SAXParseException ex) {
        // Ignore
    }
}
