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

package com.jeantessier.dependency;

import java.io.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import org.apache.oro.text.perl.*;

import junit.framework.*;

public class TestXMLPrinter extends TestCase implements ErrorHandler {
	private static final String READER_CLASSNAME = "org.apache.xerces.parsers.SAXParser";

	private static final String SPECIFIC_ENCODING   = "iso-latin-1";
	private static final String SPECIFIC_DTD_PREFIX = "./etc";

	private XMLReader reader;
	private Perl5Util perl;

	private NodeFactory  factory;
	private StringWriter out;
	private XMLPrinter   printer;

	protected void setUp() throws Exception {
		reader = XMLReaderFactory.createXMLReader(READER_CLASSNAME);
		reader.setFeature("http://xml.org/sax/features/validation", true);
		reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
		reader.setErrorHandler(this);

		perl = new Perl5Util();

		factory = new NodeFactory();
		out     = new StringWriter();
	}

	public void testDefaultDTDPrefix() {
		printer = new XMLPrinter(new PrintWriter(out));

		String xml_document = out.toString();
		assertTrue(xml_document + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xml_document));
		assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"" + XMLPrinter.DEFAULT_DTD_PREFIX + "\"", perl.group(1).startsWith(XMLPrinter.DEFAULT_DTD_PREFIX));
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
			fail("Parsed non-existant document\n" + xml_document);
		} catch (SAXException ex) {
			// Ignore
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}
	}
	
	public void testSpecificDTDPrefix() {
		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

		String xml_document = out.toString();
		assertTrue(xml_document + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xml_document));
		assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"./etc\"", perl.group(1).startsWith(SPECIFIC_DTD_PREFIX));
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
			fail("Parsed non-existant document\n" + xml_document);
		} catch (SAXException ex) {
			// Ignore
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}
	}

	public void testDefaultEncoding() {
		printer = new XMLPrinter(new PrintWriter(out));

		String xml_document = out.toString();
		assertTrue(xml_document + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xml_document));
		assertEquals("Encoding", XMLPrinter.DEFAULT_ENCODING, perl.group(1));
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
			fail("Parsed non-existant document\n" + xml_document);
		} catch (SAXException ex) {
			// Ignore
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}
	}

	public void testSpecificEncoding() {
		printer = new XMLPrinter(new PrintWriter(out), SPECIFIC_ENCODING, XMLPrinter.DEFAULT_DTD_PREFIX);

		String xml_document = out.toString();
		assertTrue(xml_document + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xml_document));
		assertEquals("Encoding", SPECIFIC_ENCODING, perl.group(1));
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
			fail("Parsed non-existant document\n" + xml_document);
		} catch (SAXException ex) {
			// Ignore
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}
	}

	public void testShowInboundsPackageTrue() throws IOException {
		factory.CreatePackage("outbound").AddDependency(factory.CreatePackage("inbound"));
		factory.CreatePackage("empty");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowInbounds(true);
		printer.ShowOutbounds(false);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <inbound type=\"package\">outbound</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowInboundsPackageFalse() throws IOException {
		factory.CreatePackage("outbound").AddDependency(factory.CreatePackage("inbound"));
		factory.CreatePackage("empty");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowInbounds(false);
		printer.ShowOutbounds(false);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowInboundsClassTrue() throws IOException {
		factory.CreateClass("outbound.Outbound").AddDependency(factory.CreateClass("inbound.Inbound"));
		factory.CreateClass("empty.Empty");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowInbounds(true);
		printer.ShowOutbounds(false);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>empty.Empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>inbound.Inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <inbound type=\"class\">outbound.Outbound</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>outbound.Outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowInboundsClassFalse() throws IOException {
		factory.CreateClass("outbound.Outbound").AddDependency(factory.CreateClass("inbound.Inbound"));
		factory.CreateClass("empty.Empty");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowInbounds(false);
		printer.ShowOutbounds(false);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>empty.Empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>inbound.Inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>outbound.Outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowInboundsFeatureTrue() throws IOException {
		factory.CreateFeature("outbound.Outbound.outbound()").AddDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreateFeature("empty.Empty.empty()");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowInbounds(true);
		printer.ShowOutbounds(false);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>empty.Empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>empty.Empty.empty()</name>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>inbound.Inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <inbound type=\"feature\">outbound.Outbound.outbound()</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>outbound.Outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowInboundsFeatureFalse() throws IOException {
		factory.CreateFeature("outbound.Outbound.outbound()").AddDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreateFeature("empty.Empty.empty()");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowInbounds(false);
		printer.ShowOutbounds(false);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>empty.Empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>empty.Empty.empty()</name>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>inbound.Inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>outbound.Outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowOutboundsPackageTrue() throws IOException {
		factory.CreatePackage("outbound").AddDependency(factory.CreatePackage("inbound"));
		factory.CreatePackage("empty");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowInbounds(false);
		printer.ShowOutbounds(true);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <outbound type=\"package\">inbound</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowOutboundsPackageFalse() throws IOException {
		factory.CreatePackage("outbound").AddDependency(factory.CreatePackage("inbound"));
		factory.CreatePackage("empty");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowInbounds(false);
		printer.ShowOutbounds(false);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowOutboundsClassTrue() throws IOException {
		factory.CreateClass("outbound.Outbound").AddDependency(factory.CreateClass("inbound.Inbound"));
		factory.CreateClass("empty.Empty");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowInbounds(false);
		printer.ShowOutbounds(true);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>empty.Empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>inbound.Inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>outbound.Outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <outbound type=\"class\">inbound.Inbound</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowOutboundsClassFalse() throws IOException {
		factory.CreateClass("outbound.Outbound").AddDependency(factory.CreateClass("inbound.Inbound"));
		factory.CreateClass("empty.Empty");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowInbounds(false);
		printer.ShowOutbounds(false);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>empty.Empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>inbound.Inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>outbound.Outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowOutboundsFeatureTrue() throws IOException {
		factory.CreateFeature("outbound.Outbound.outbound()").AddDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreateFeature("empty.Empty.empty()");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowInbounds(false);
		printer.ShowOutbounds(true);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>empty.Empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>empty.Empty.empty()</name>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>inbound.Inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>outbound.Outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <outbound type=\"feature\">inbound.Inbound.inbound()</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowOutboundsFeatureFalse() throws IOException {
		factory.CreateFeature("outbound.Outbound.outbound()").AddDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreateFeature("empty.Empty.empty()");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowInbounds(false);
		printer.ShowOutbounds(false);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>empty.Empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>empty.Empty.empty()</name>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>inbound.Inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>outbound.Outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}
	
	public void testShowEmptyPackageTrue() throws IOException {
		factory.CreatePackage("outbound").AddDependency(factory.CreatePackage("inbound"));
		factory.CreateClass("outbound.Outbound").AddDependency(factory.CreateClass("inbound.Inbound"));
		factory.CreateFeature("outbound.Outbound.outbound()").AddDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreatePackage("empty");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowEmptyNodes(true);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <inbound type=\"package\">outbound</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>inbound.Inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <inbound type=\"class\">outbound.Outbound</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <inbound type=\"feature\">outbound.Outbound.outbound()</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <outbound type=\"package\">inbound</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>outbound.Outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <outbound type=\"class\">inbound.Inbound</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <outbound type=\"feature\">inbound.Inbound.inbound()</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowEmptyPackageFalse() throws IOException {
		factory.CreatePackage("outbound").AddDependency(factory.CreatePackage("inbound"));
		factory.CreateClass("outbound.Outbound").AddDependency(factory.CreateClass("inbound.Inbound"));
		factory.CreateFeature("outbound.Outbound.outbound()").AddDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreatePackage("empty");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowEmptyNodes(false);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <inbound type=\"package\">outbound</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>inbound.Inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <inbound type=\"class\">outbound.Outbound</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <inbound type=\"feature\">outbound.Outbound.outbound()</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <outbound type=\"package\">inbound</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>outbound.Outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <outbound type=\"class\">inbound.Inbound</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <outbound type=\"feature\">inbound.Inbound.inbound()</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowEmptyClassTrue() throws IOException {
		factory.CreateClass("outbound.Outbound").AddDependency(factory.CreateClass("inbound.Inbound"));
		factory.CreateFeature("outbound.Outbound.outbound()").AddDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreateClass("empty.Empty");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowEmptyNodes(true);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>empty.Empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>inbound.Inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <inbound type=\"class\">outbound.Outbound</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <inbound type=\"feature\">outbound.Outbound.outbound()</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>outbound.Outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <outbound type=\"class\">inbound.Inbound</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <outbound type=\"feature\">inbound.Inbound.inbound()</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowEmptyClassFalse() throws IOException {
		factory.CreateClass("outbound.Outbound").AddDependency(factory.CreateClass("inbound.Inbound"));
		factory.CreateFeature("outbound.Outbound.outbound()").AddDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreateClass("empty.Empty");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowEmptyNodes(false);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>inbound.Inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <inbound type=\"class\">outbound.Outbound</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <inbound type=\"feature\">outbound.Outbound.outbound()</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>outbound.Outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <outbound type=\"class\">inbound.Inbound</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <outbound type=\"feature\">inbound.Inbound.inbound()</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowEmptyFeatureTrue() throws IOException {
		factory.CreateFeature("outbound.Outbound.outbound()").AddDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreateFeature("empty.Empty.empty()");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowEmptyNodes(true);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>empty.Empty</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>empty.Empty.empty()</name>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>inbound.Inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <inbound type=\"feature\">outbound.Outbound.outbound()</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>outbound.Outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <outbound type=\"feature\">inbound.Inbound.inbound()</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowEmptyFeatureFalse() throws IOException {
		factory.CreateFeature("outbound.Outbound.outbound()").AddDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreateFeature("empty.Empty.empty()");

		printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
		printer.ShowEmptyNodes(false);

		printer.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>inbound.Inbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <inbound type=\"feature\">outbound.Outbound.outbound()</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>outbound.Outbound</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <outbound type=\"feature\">inbound.Inbound.inbound()</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

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
