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

package com.jeantessier.metrics;

import com.jeantessier.classreader.AggregatingClassfileLoader;
import com.jeantessier.classreader.ClassfileLoader;
import org.apache.oro.text.perl.Perl5Util;
import org.jmock.Expectations;
import org.jmock.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestXMLPrinter {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    public static final String TEST_CLASS = "test";
    public static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();
    private static final String CONFIGURATION_FILENAME = Paths.get("../etc/MetricsConfig.xml").toString();

    private static final String SPECIFIC_ENCODING = "iso-latin-1";
    private static final String SPECIFIC_DTD_PREFIX = "../etc";
    
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();
    
    private StringWriter buffer;
    private MetricsConfiguration configuration;
    private XMLReader reader;
    private ErrorHandler errorHandler;

    private Perl5Util perl;

    @BeforeEach
    void setUp() throws Exception {
        buffer = new StringWriter();
        configuration = new MetricsConfigurationLoader().load(CONFIGURATION_FILENAME);

	    boolean validate = Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE");

        reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        reader.setFeature("http://xml.org/sax/features/validation", validate);
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", validate);

        errorHandler = context.mock(ErrorHandler.class);
        reader.setErrorHandler(errorHandler);

        perl = new Perl5Util();
    }

    @Test
    void testDefaultDTDPrefix() throws Exception {
        context.checking(new Expectations() {{
            oneOf (errorHandler).fatalError(with(any(SAXParseException.class)));
        }});

        var printer = new XMLPrinter(new PrintWriter(buffer), configuration);

        var xmlDocument = buffer.toString();
        assertTrue(perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument), xmlDocument + "Missing DTD");
        assertTrue(perl.group(1).startsWith(XMLPrinter.DEFAULT_DTD_PREFIX), "DTD \"" + perl.group(1) + "\" does not have prefix \"" + XMLPrinter.DEFAULT_DTD_PREFIX + "\"");
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existent document\n" + xmlDocument);
        } catch (SAXException ex) {
            // Ignore
        }
    }
    
    @Test
    void testSpecificDTDPrefix() throws Exception {
        context.checking(new Expectations() {{
            oneOf (errorHandler).fatalError(with(any(SAXParseException.class)));
        }});

        var printer = new XMLPrinter(new PrintWriter(buffer), configuration, XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

        var xmlDocument = buffer.toString();
        assertTrue(perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument), xmlDocument + "Missing DTD");
        assertTrue(perl.group(1).startsWith(SPECIFIC_DTD_PREFIX), "DTD \"" + perl.group(1) + "\" does not have prefix \"./etc\"");
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existent document\n" + xmlDocument);
        } catch (SAXException ex) {
            // Ignore
        }
    }

    @Test
    void testDefaultEncoding() throws Exception {
        context.checking(new Expectations() {{
            oneOf (errorHandler).fatalError(with(any(SAXParseException.class)));
        }});

        var printer = new XMLPrinter(new PrintWriter(buffer), configuration);

        var xmlDocument = buffer.toString();
        assertTrue(perl.match("/encoding=\"([^\"]*)\"/", xmlDocument), xmlDocument + "Missing encoding");
        assertEquals(XMLPrinter.DEFAULT_ENCODING, perl.group(1), "Encoding");
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existent document\n" + xmlDocument);
        } catch (SAXException ex) {
            // Ignore
        }
    }

    @Test
    void testSpecificEncoding() throws Exception {
        context.checking(new Expectations() {{
            oneOf (errorHandler).fatalError(with(any(SAXParseException.class)));
        }});

        var printer = new XMLPrinter(new PrintWriter(buffer), configuration, SPECIFIC_ENCODING, XMLPrinter.DEFAULT_DTD_PREFIX);

        var xmlDocument = buffer.toString();
        assertTrue(perl.match("/encoding=\"([^\"]*)\"/", xmlDocument), xmlDocument + "Missing encoding");
        assertEquals(SPECIFIC_ENCODING, perl.group(1), "Encoding");
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existent document\n" + xmlDocument);
        } catch (SAXException ex) {
            // Ignore
        }
    }

    @Test
    void testOneClass() throws Exception {
        MetricsFactory factory = new MetricsFactory("test", configuration);

        ClassfileLoader loader = new AggregatingClassfileLoader();
        loader.load(Collections.singleton(TEST_FILENAME));
        loader.getClassfile(TEST_CLASS).accept(new MetricsGatherer(factory));

        var printer = new XMLPrinter(new PrintWriter(buffer), configuration, XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.visitMetrics(factory.getProjectMetrics());

        var xmlDocument = buffer.toString();
        reader.parse(new InputSource(new StringReader(xmlDocument)));
        assertXPath(xmlDocument, "metrics/project/group/class/measurement[short-name='PARAM' and value=0.5]/median", 1);
    }

    private void assertXPath(String xmlDocument, String xPathExpression, int i) throws Exception {
        XPath xPath = XPathFactory.newInstance().newXPath();
        InputSource in = new InputSource(new StringReader(xmlDocument));

        NodeList nodeList = (NodeList) xPath.evaluate(xPathExpression, in, XPathConstants.NODESET);
        assertEquals(i, nodeList.getLength(), "XPath \"" + xPathExpression + "\" in \n" + xmlDocument);
    }
}
