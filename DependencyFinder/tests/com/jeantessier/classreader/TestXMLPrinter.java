/*
 *  Copyright (c) 2001-2004, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import org.apache.oro.text.perl.*;

import junit.framework.*;

public class TestXMLPrinter extends TestCase implements ErrorHandler {
	private static final String READER_CLASSNAME = "org.apache.xerces.parsers.SAXParser";
	private static final String TEST_CLASS       = "test";
	private static final String TEST_FILENAME    = "classes" + File.separator + "test.class";
	private static final String TEST_DIRECTORY   = "tests" + File.separator + "JarJarDiff" + File.separator + "new";

	private static final String SPECIFIC_ENCODING   = "iso-latin-1";
	private static final String SPECIFIC_DTD_PREFIX = "./etc";

	private ClassfileLoader loader;
	private StringWriter    buffer;
	private Visitor         printer;
	private XMLReader       reader;

	private Perl5Util perl;

	protected void setUp() throws Exception {
		loader = new AggregatingClassfileLoader();

		buffer  = new StringWriter();
		printer = new XMLPrinter(new PrintWriter(buffer), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

		reader = XMLReaderFactory.createXMLReader(READER_CLASSNAME);
		reader.setFeature("http://xml.org/sax/features/validation", true);
		reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
		reader.setErrorHandler(this);

		perl = new Perl5Util();
	}
	
	public void testDefaultDTDPrefix() {
		buffer  = new StringWriter();
		printer = new XMLPrinter(new PrintWriter(buffer));

		String xmlDocument = buffer.toString();
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
		buffer  = new StringWriter();
		printer = new XMLPrinter(new PrintWriter(buffer), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

		String xmlDocument = buffer.toString();
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
		buffer  = new StringWriter();
		printer = new XMLPrinter(new PrintWriter(buffer));

		String xmlDocument = buffer.toString();
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
		buffer  = new StringWriter();
		printer = new XMLPrinter(new PrintWriter(buffer), SPECIFIC_ENCODING, XMLPrinter.DEFAULT_DTD_PREFIX);

		String xmlDocument = buffer.toString();
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

	public void testSingleClassfile() {
		loader.load(Collections.singleton(TEST_FILENAME));

		loader.getClassfile(TEST_CLASS).accept(printer);

		String xmlDocument = buffer.toString();
		assertTrue(xmlDocument + "Missing DOCTYPE", perl.match("/DOCTYPE (\\w+) SYSTEM/", xmlDocument));
		assertEquals("DOCTYPE", "classfiles", perl.group(1));

		try {
			reader.parse(new InputSource(new StringReader(xmlDocument)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		}
	}
	
	public void testZeroClassfile() {
		printer.visitClassfiles(Collections.EMPTY_LIST);

		String xmlDocument = buffer.toString();
		assertTrue(xmlDocument + "Missing DOCTYPE", perl.match("/DOCTYPE (\\w+) SYSTEM/", xmlDocument));
		assertEquals("DOCTYPE", "classfiles", perl.group(1));

		try {
			reader.parse(new InputSource(new StringReader(xmlDocument)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		}
	}
	
	public void testOneClassfile() {
		loader.load(Collections.singleton(TEST_FILENAME));

		printer.visitClassfiles(loader.getAllClassfiles());

		String xmlDocument = buffer.toString();
		assertTrue(xmlDocument + "Missing DOCTYPE", perl.match("/DOCTYPE (\\w+) SYSTEM/", xmlDocument));
		assertEquals("DOCTYPE", "classfiles", perl.group(1));

		try {
			reader.parse(new InputSource(new StringReader(xmlDocument)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		}
	}

	public void testMultipleClassfiles() throws SAXException, IOException {
		loader.load(Collections.singleton(TEST_DIRECTORY));

		printer.visitClassfiles(loader.getAllClassfiles());

		String xmlDocument = buffer.toString();
		assertTrue(xmlDocument + "Missing DOCTYPE", perl.match("/DOCTYPE (\\w+) SYSTEM/", xmlDocument));
		assertEquals("DOCTYPE", "classfiles", perl.group(1));
		
		try {
			reader.parse(new InputSource(new StringReader(xmlDocument)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		}
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
