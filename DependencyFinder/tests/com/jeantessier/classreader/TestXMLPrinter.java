/*
 *  Copyright (c) 2001-2003, Jean Tessier
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

import junit.framework.*;
import org.apache.oro.text.perl.*;

public class TestXMLPrinter extends TestCase {
	private static final String READER_CLASSNAME = "org.apache.xerces.parsers.SAXParser";
	private static final String TEST_CLASS       = "test";
	private static final String TEST_FILENAME    = "classes" + File.separator + "test.class";
	private static final String TEST_DIRECTORY   = "tests" + File.separator + "JarJarDiff" + File.separator + "new";

	private ClassfileLoader loader;
	private StringWriter    buffer;
	private Visitor         printer;
	private XMLReader       reader;

	private Perl5Util perl;

	protected void setUp() throws Exception {
		loader = new AggregatingClassfileLoader();

		buffer  = new StringWriter();
		printer = new XMLPrinter(new PrintWriter(buffer), "./etc");

		reader = XMLReaderFactory.createXMLReader(READER_CLASSNAME);
		reader.setFeature("http://xml.org/sax/features/validation", true);
		reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);

		perl = new Perl5Util();
	}
	
	public void testSingleClassfile() {
		loader.Load(Collections.singleton(TEST_FILENAME));

		loader.Classfile(TEST_CLASS).Accept(printer);

		String xml_document = buffer.toString();
		assertTrue(xml_document + "Missing DOCTYPE", perl.match("/DOCTYPE (\\w+) SYSTEM/", xml_document));
		assertEquals("DOCTYPE", "classfiles", perl.group(1));
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xml_document);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}
	}
	
	public void testZeroClassfile() {
		printer.VisitClassfiles(Collections.EMPTY_LIST);

		String xml_document = buffer.toString();
		assertTrue(xml_document + "Missing DOCTYPE", perl.match("/DOCTYPE (\\w+) SYSTEM/", xml_document));
		assertEquals("DOCTYPE", "classfiles", perl.group(1));
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xml_document);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}
	}
	
	public void testOneClassfile() {
		loader.Load(Collections.singleton(TEST_FILENAME));

		printer.VisitClassfiles(loader.Classfiles());

		String xml_document = buffer.toString();
		assertTrue(xml_document + "Missing DOCTYPE", perl.match("/DOCTYPE (\\w+) SYSTEM/", xml_document));
		assertEquals("DOCTYPE", "classfiles", perl.group(1));
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xml_document);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}
	}

	public void testMultipleClassfiles() throws SAXException, IOException {
		loader.Load(Collections.singleton(TEST_DIRECTORY));

		printer.VisitClassfiles(loader.Classfiles());

		String xml_document = buffer.toString();
		assertTrue(xml_document + "Missing DOCTYPE", perl.match("/DOCTYPE (\\w+) SYSTEM/", xml_document));
		assertEquals("DOCTYPE", "classfiles", perl.group(1));
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xml_document);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}
	}
}
