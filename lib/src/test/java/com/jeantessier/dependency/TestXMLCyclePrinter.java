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

import org.xml.sax.*;
import org.apache.oro.text.perl.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import javax.xml.parsers.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestXMLCyclePrinter {
    private static final String SPECIFIC_ENCODING = "iso-latin-1";
    private static final String SPECIFIC_DTD_PREFIX = "./etc";

    private XMLReader reader;
    private final Perl5Util perl = new Perl5Util();
    private final StringWriter writer = new StringWriter();

    private final NodeFactory factory = new NodeFactory();

    private final Node a_package = factory.createPackage("a");
    private final Node b_package = factory.createPackage("b");
    private final Node c_package = factory.createPackage("c");

    @BeforeEach
    void setUp() throws Exception {
	    var validate = Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE");

        reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        reader.setFeature("http://xml.org/sax/features/validation", validate);
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", validate);
    }

    @Test
    void testDefaultDTDPrefix() {
        new XMLCyclePrinter(new PrintWriter(writer));

        String xmlDocument = writer.toString();
        assertTrue(perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument), xmlDocument + "Missing DTD");
        assertTrue(perl.group(1).startsWith(XMLPrinter.DEFAULT_DTD_PREFIX), "DTD \"" + perl.group(1) + "\" does not have prefix \"" + XMLPrinter.DEFAULT_DTD_PREFIX + "\"");

        assertThrows(SAXException.class, () -> reader.parse(new InputSource(new StringReader(xmlDocument))));
    }

    @Test
    void testSpecificDTDPrefix() {
        new XMLCyclePrinter(new PrintWriter(writer), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

        String xmlDocument = writer.toString();
        assertTrue(perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument), xmlDocument + "Missing DTD");
        assertTrue(perl.group(1).startsWith(SPECIFIC_DTD_PREFIX), "DTD \"" + perl.group(1) + "\" does not have prefix \"./etc\"");

        assertThrows(SAXException.class, () -> reader.parse(new InputSource(new StringReader(xmlDocument))));
    }

    @Test
    void testDefaultEncoding() {
        new XMLCyclePrinter(new PrintWriter(writer));

        String xmlDocument = writer.toString();
        assertTrue(perl.match("/encoding=\"([^\"]*)\"/", xmlDocument), xmlDocument + "Missing encoding");
        assertEquals(XMLPrinter.DEFAULT_ENCODING, perl.group(1), "Encoding");

        assertThrows(SAXException.class, () -> reader.parse(new InputSource(new StringReader(xmlDocument))));
    }

    @Test
    void testSpecificEncoding() {
        new XMLCyclePrinter(new PrintWriter(writer), SPECIFIC_ENCODING, XMLPrinter.DEFAULT_DTD_PREFIX);

        String xmlDocument = writer.toString();
        assertTrue(perl.match("/encoding=\"([^\"]*)\"/", xmlDocument), xmlDocument + "Missing encoding");
        assertEquals(SPECIFIC_ENCODING, perl.group(1), "Encoding");

        assertThrows(SAXException.class, () -> reader.parse(new InputSource(new StringReader(xmlDocument))));
    }

    @Test
    void testVisitCyclesWith2NodeCycle() {
        var cycle = new Cycle(List.of(a_package, b_package));

        var printer = new XMLCyclePrinter(new PrintWriter(writer), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.visitCycles(Collections.singletonList(cycle));

        var expectedLines = Stream.of(
                "<?xml version=\"1.0\" encoding=\"utf-8\" ?>",
                "",
                "<!DOCTYPE dependencies SYSTEM \"./etc/cycles.dtd\">",
                "",
                "<cycles>",
                "    <cycle>",
                "        <node type=\"package\">a</node>",
                "        <node type=\"package\">b</node>",
                "    </cycle>",
                "</cycles>"
        );

        assertLinesMatch(expectedLines, writer.toString().lines());
    }

    @Test
    void testVisitCyclesWith2NodeCycleWithIndentText() {
        var cycle = new Cycle(List.of(a_package, b_package));

        var printer = new XMLCyclePrinter(new PrintWriter(writer), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setIndentText("*");
        printer.visitCycles(Collections.singletonList(cycle));

        var expectedLines = Stream.of(
                "<?xml version=\"1.0\" encoding=\"utf-8\" ?>",
                "",
                "<!DOCTYPE dependencies SYSTEM \"./etc/cycles.dtd\">",
                "",
                "<cycles>",
                "*<cycle>",
                "**<node type=\"package\">a</node>",
                "**<node type=\"package\">b</node>",
                "*</cycle>",
                "</cycles>"
        );

        assertLinesMatch(expectedLines, writer.toString().lines());
    }

    @Test
    void testVisitCycleWith3NodeCycle() {
        var cycle = new Cycle(List.of(a_package, b_package, c_package));

        var printer = new XMLCyclePrinter(new PrintWriter(writer), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.visitCycles(Collections.singletonList(cycle));

        var expectedLines = Stream.of(
                "<?xml version=\"1.0\" encoding=\"utf-8\" ?>",
                "",
                "<!DOCTYPE dependencies SYSTEM \"./etc/cycles.dtd\">",
                "",
                "<cycles>",
                "    <cycle>",
                "        <node type=\"package\">a</node>",
                "        <node type=\"package\">b</node>",
                "        <node type=\"package\">c</node>",
                "    </cycle>",
                "</cycles>"
        );

        assertLinesMatch(expectedLines, writer.toString().lines());
    }

    @Test
    void testVisitCycleWith2Cycles() {
        var cycles = List.of(
                new Cycle(List.of(a_package, b_package)),
                new Cycle(List.of(a_package, b_package, c_package))
        );

        var printer = new XMLCyclePrinter(new PrintWriter(writer), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.visitCycles(cycles);

        var expectedLines = Stream.of(
                "<?xml version=\"1.0\" encoding=\"utf-8\" ?>",
                "",
                "<!DOCTYPE dependencies SYSTEM \"./etc/cycles.dtd\">",
                "",
                "<cycles>",
                "    <cycle>",
                "        <node type=\"package\">a</node>",
                "        <node type=\"package\">b</node>",
                "    </cycle>",
                "    <cycle>",
                "        <node type=\"package\">a</node>",
                "        <node type=\"package\">b</node>",
                "        <node type=\"package\">c</node>",
                "    </cycle>",
                "</cycles>"
        );

        assertLinesMatch(expectedLines, writer.toString().lines());
    }
}
