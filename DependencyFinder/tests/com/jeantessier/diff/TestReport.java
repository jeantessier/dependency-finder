/*
 *  Copyright (c) 2001-2005, Jean Tessier
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

package com.jeantessier.diff;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;

import org.apache.xpath.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import org.apache.oro.text.perl.*;

import junit.framework.*;

import com.jeantessier.classreader.*;

public class TestReport extends TestCase implements ErrorHandler {
	private static final String READER_CLASSNAME = "org.apache.xerces.parsers.SAXParser";

	private static final String SPECIFIC_ENCODING   = "iso-latin-1";
	private static final String SPECIFIC_DTD_PREFIX = "./etc";

	private static final String OLD_CLASSPATH = "tests" + File.separator + "JarJarDiff" + File.separator + "old";
	private static final String NEW_CLASSPATH = "tests" + File.separator + "JarJarDiff" + File.separator + "new";

	private Visitor   printer;
	private XMLReader reader;

	private Perl5Util perl;

	protected void setUp() throws Exception {
		reader = XMLReaderFactory.createXMLReader(READER_CLASSNAME);
		reader.setFeature("http://xml.org/sax/features/validation", true);
		reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
		reader.setErrorHandler(this);

		perl = new Perl5Util();
	}

	public void testDefaultDTDPrefix() {
		printer = new Report();

		String xmlDocument = printer.toString();
		assertTrue(xmlDocument + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument));
		assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"" + Report.DEFAULT_DTD_PREFIX + "\"", perl.group(1).startsWith(Report.DEFAULT_DTD_PREFIX));
		
		try {
			reader.parse(new InputSource(new StringReader(xmlDocument)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		}
	}
	
	public void testSpecificDTDPrefix() {
		printer = new Report(Report.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

		String xmlDocument = printer.toString();
		assertTrue(xmlDocument + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument));
		assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"./etc\"", perl.group(1).startsWith(SPECIFIC_DTD_PREFIX));
		
		try {
			reader.parse(new InputSource(new StringReader(xmlDocument)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		}
	}

	public void testDefaultEncoding() {
		printer = new Report();

		String xmlDocument = printer.toString();
		assertTrue(xmlDocument + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xmlDocument));
		assertEquals("Encoding", Report.DEFAULT_ENCODING, perl.group(1));
		
		try {
			reader.parse(new InputSource(new StringReader(xmlDocument)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		}
	}

	public void testSpecificEncoding() {
		printer = new Report(SPECIFIC_ENCODING, Report.DEFAULT_DTD_PREFIX);

		String xmlDocument = printer.toString();
		assertTrue(xmlDocument + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xmlDocument));
		assertEquals("Encoding", SPECIFIC_ENCODING, perl.group(1));
		
		try {
			reader.parse(new InputSource(new StringReader(xmlDocument)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		}
	}

	public void testContent() throws IOException, ParserConfigurationException, SAXException, TransformerException {
		ClassfileLoader oldJar = new AggregatingClassfileLoader();
		oldJar.load(Collections.singleton(OLD_CLASSPATH));

		ClassfileLoader newJar = new AggregatingClassfileLoader();
		newJar.load(Collections.singleton(NEW_CLASSPATH));

		Validator oldValidator = new ListBasedValidator(new BufferedReader(new FileReader(OLD_CLASSPATH + ".txt")));
		Validator newValidator = new ListBasedValidator(new BufferedReader(new FileReader(NEW_CLASSPATH + ".txt")));

		DifferencesFactory factory = new DifferencesFactory(oldValidator, newValidator);
		JarDifferences jarDifferences = (JarDifferences) factory.createJarDifferences("test", "old", oldJar, "new", newJar);

		printer = new Report(Report.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		jarDifferences.accept(printer);

		String xmlDocument = printer.toString();

		try {
			reader.parse(new InputSource(new StringReader(xmlDocument)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
		}

		InputSource in  = new InputSource(new StringReader(xmlDocument));
		Document    doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

		assertNotNull("//differences", XPathAPI.selectSingleNode(doc, "//differences"));
		assertNotNull("*/old[text()='old']", XPathAPI.selectSingleNode(doc, "*/old[text()='old']"));
		assertEquals("*/modified-classes/class", 1, XPathAPI.selectNodeList(doc, "*/modified-classes/class").getLength());
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
