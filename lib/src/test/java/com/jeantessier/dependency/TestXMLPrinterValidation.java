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

import org.apache.oro.text.perl.*;
import org.junit.jupiter.api.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestXMLPrinterValidation extends TestPrinterBase {
    private static final String SPECIFIC_ENCODING = "iso-latin-1";
    private static final String SPECIFIC_DTD_PREFIX = "./etc";

    private XMLReader reader;
    private final Perl5Util perl = new Perl5Util();

    @BeforeEach
    void setUp() throws Exception {
	    var validate = Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE");

        reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        reader.setFeature("http://xml.org/sax/features/validation", validate);
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", validate);
    }

    @Test
    void testDefaultDTDPrefix() {
        new XMLPrinter(new PrintWriter(out));

        String xmlDocument = out.toString();
        assertTrue(perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument), xmlDocument + "Missing DTD");
        assertTrue(perl.group(1).startsWith(XMLPrinter.DEFAULT_DTD_PREFIX), "DTD \"" + perl.group(1) + "\" does not have prefix \"" + XMLPrinter.DEFAULT_DTD_PREFIX + "\"");
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXException ex) {
            // Ignore
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    @Test
    void testSpecificDTDPrefix() {
        new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

        String xmlDocument = out.toString();
        assertTrue(perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument), xmlDocument + "Missing DTD");
        assertTrue(perl.group(1).startsWith(SPECIFIC_DTD_PREFIX), "DTD \"" + perl.group(1) + "\" does not have prefix \"./etc\"");
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXException ex) {
            // Ignore
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    @Test
    void testDefaultEncoding() {
        new XMLPrinter(new PrintWriter(out));

        String xmlDocument = out.toString();
        assertTrue(perl.match("/encoding=\"([^\"]*)\"/", xmlDocument), xmlDocument + "Missing encoding");
        assertEquals(XMLPrinter.DEFAULT_ENCODING, perl.group(1), "Encoding");
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXException ex) {
            // Ignore
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    @Test
    void testSpecificEncoding() {
        new XMLPrinter(new PrintWriter(out), SPECIFIC_ENCODING, XMLPrinter.DEFAULT_DTD_PREFIX);

        String xmlDocument = out.toString();
        assertTrue(perl.match("/encoding=\"([^\"]*)\"/", xmlDocument), xmlDocument + "Missing encoding");
        assertEquals(SPECIFIC_ENCODING, perl.group(1), "Encoding");
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXException ex) {
            // Ignore
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }
}
