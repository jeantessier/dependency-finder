/*
 *  Copyright (c) 2001-2008, Jean Tessier
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

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.apache.oro.text.perl.*;
import org.jmock.integration.junit3.*;
import org.jmock.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class TestXMLPrinter extends MockObjectTestCase {
    private static final String TEST_CLASS = "test";
    private static final String TEST_FILENAME = "classes" + File.separator + "test.class";
    private static final String TEST_DIRECTORY = "tests" + File.separator + "JarJarDiff" + File.separator + "new";

    private static final String SPECIFIC_ENCODING = "iso-latin-1";
    private static final String SPECIFIC_DTD_PREFIX = "./etc";

    private ClassfileLoader loader;
    private StringWriter buffer;
    private Visitor printer;
    private XMLReader reader;
    private ErrorHandler errorHandler;

    private Perl5Util perl;

    protected void setUp() throws Exception {
        super.setUp();

        loader = new AggregatingClassfileLoader();

        buffer = new StringWriter();
        printer = new XMLPrinter(new PrintWriter(buffer), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();

        boolean validate = Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE");
        reader.setFeature("http://xml.org/sax/features/validation", validate);
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", validate);

        errorHandler = mock(ErrorHandler.class);
        reader.setErrorHandler(errorHandler);

        perl = new Perl5Util();
    }
    
    public void testDefaultDTDPrefix() throws Exception {
        checking(new Expectations() {{
            one (errorHandler).fatalError(with(any(SAXParseException.class)));
        }});

        buffer = new StringWriter();
        printer = new XMLPrinter(new PrintWriter(buffer));

        String xmlDocument = buffer.toString();
        assertTrue(xmlDocument + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument));
        assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"" + XMLPrinter.DEFAULT_DTD_PREFIX + "\"", perl.group(1).startsWith(XMLPrinter.DEFAULT_DTD_PREFIX));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXParseException ex) {
            // Expected
        }
    }
    
    public void testSpecificDTDPrefix() throws Exception {
        checking(new Expectations() {{
            one (errorHandler).fatalError(with(any(SAXParseException.class)));
        }});

        buffer = new StringWriter();
        printer = new XMLPrinter(new PrintWriter(buffer), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

        String xmlDocument = buffer.toString();
        assertTrue(xmlDocument + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument));
        assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"./etc\"", perl.group(1).startsWith(SPECIFIC_DTD_PREFIX));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXParseException ex) {
            // Expected
        }
    }

    public void testDefaultEncoding() throws Exception {
        checking(new Expectations() {{
            one (errorHandler).fatalError(with(any(SAXParseException.class)));
        }});

        buffer = new StringWriter();
        printer = new XMLPrinter(new PrintWriter(buffer));

        String xmlDocument = buffer.toString();
        assertTrue(xmlDocument + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xmlDocument));
        assertEquals("Encoding", XMLPrinter.DEFAULT_ENCODING, perl.group(1));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXParseException ex) {
            // Expected
        }
    }

    public void testSpecificEncoding() throws Exception {
        checking(new Expectations() {{
            one (errorHandler).fatalError(with(any(SAXParseException.class)));
        }});

        buffer = new StringWriter();
        printer = new XMLPrinter(new PrintWriter(buffer), SPECIFIC_ENCODING, XMLPrinter.DEFAULT_DTD_PREFIX);

        String xmlDocument = buffer.toString();
        assertTrue(xmlDocument + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xmlDocument));
        assertEquals("Encoding", SPECIFIC_ENCODING, perl.group(1));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXParseException ex) {
            // Expected
        }
    }

    public void testSingleClassfile() throws Exception {
        loader.load(Collections.singleton(TEST_FILENAME));

        loader.getClassfile(TEST_CLASS).accept(printer);

        String xmlDocument = buffer.toString();
        assertXPath(xmlDocument, "classfile[this-class='" + TEST_CLASS + "']", 1);
    }

    public void testZeroClassfile() throws Exception {
        printer.visitClassfiles(Collections.EMPTY_LIST);

        String xmlDocument = buffer.toString();
        assertXPath(xmlDocument, "*/classfile[this-class='" + TEST_CLASS + "']", 0);
    }

    public void testOneClassfile() throws Exception {
        loader.load(Collections.singleton(TEST_FILENAME));

        printer.visitClassfiles(loader.getAllClassfiles());

        String xmlDocument = buffer.toString();
        assertXPath(xmlDocument, "*/classfile", loader.getAllClassfiles().size());
    }

    public void testMultipleClassfiles() throws Exception {
        loader.load(Collections.singleton(TEST_DIRECTORY));

        printer.visitClassfiles(loader.getAllClassfiles());

        String xmlDocument = buffer.toString();
        assertXPath(xmlDocument, "*/classfile", loader.getAllClassfiles().size());
    }

    public void testVisitLocalVariable() throws Exception {
        final LocalVariable localVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            one (localVariable).getStartPC();
            one (localVariable).getLength();
            one (localVariable).getRawName();
            one (localVariable).getDescriptor();
            will(returnValue("I"));
            one (localVariable).getIndex();
        }});

        printer.visitLocalVariable(localVariable);
    }

    public void testVisitLocalVariableType() throws Exception {
        final LocalVariableType localVariableType = mock(LocalVariableType.class);

        checking(new Expectations() {{
            one (localVariableType).getStartPC();
            one (localVariableType).getLength();
            one (localVariableType).getRawName();
            one (localVariableType).getRawSignature();
            one (localVariableType).getIndex();
        }});

        printer.visitLocalVariableType(localVariableType);
    }

    private void assertXPath(String xmlDocument, String xPathExpression, int i) throws Exception {
        XPath xPath = XPathFactory.newInstance().newXPath();
        InputSource in = new InputSource(new StringReader(xmlDocument));

        NodeList nodeList = (NodeList) xPath.evaluate(xPathExpression, in, XPathConstants.NODESET);
        assertEquals("XPath \"" + xPathExpression + "\" in \n" + xmlDocument, i, nodeList.getLength());
    }
}
