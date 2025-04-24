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

package com.jeantessier.classreader;

import org.junit.jupiter.api.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.xpath.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestXMLPrinter {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    private static final String TEST_CLASS = "test";
    public static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();
    private static final String TEST_DIRECTORY = Paths.get("jarjardiff/new/build/libs/new.jar").toString();

    private static final String SPECIFIC_DTD_PREFIX = "../etc";

    private final ClassfileLoader loader = new AggregatingClassfileLoader();
    private final StringWriter buffer = new StringWriter();
    private final Visitor printer = new XMLPrinter(new PrintWriter(buffer), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

    @Test
    void testSingleClassfile() throws Exception {
        loader.load(Collections.singleton(TEST_FILENAME));

        loader.getClassfile(TEST_CLASS).accept(printer);

        var xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile[this-class='" + TEST_CLASS + "']", 1);
    }

    @Test
    void testZeroClassfile() throws Exception {
        printer.visitClassfiles(Collections.emptyList());

        var xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "*/classfile[this-class='" + TEST_CLASS + "']", 0);
    }

    @Test
    void testOneClassfile() throws Exception {
        loader.load(Collections.singleton(TEST_FILENAME));

        printer.visitClassfiles(loader.getAllClassfiles());

        var xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "*/classfile", loader.getAllClassfiles().size());
    }

    @Test
    void testMultipleClassfiles() throws Exception {
        loader.load(Collections.singleton(TEST_DIRECTORY));

        printer.visitClassfiles(loader.getAllClassfiles());

        var xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "*/classfile", loader.getAllClassfiles().size());
    }

    private void assertXPathCount(String xmlDocument, String xPathExpression, int expectedCount) throws Exception {
        var xPath = XPathFactory.newInstance().newXPath();
        var in = new InputSource(new StringReader(xmlDocument));

        var nodeList = (NodeList) xPath.evaluate(xPathExpression, in, XPathConstants.NODESET);
        var actualCount = nodeList.getLength();
        assertEquals(expectedCount, actualCount, "XPath \"" + xPathExpression + "\" in \n" + xmlDocument);
    }
}
