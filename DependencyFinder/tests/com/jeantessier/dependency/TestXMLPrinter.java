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

public class TestXMLPrinter extends TestCase {
	private static final String READER_CLASSNAME = "org.apache.xerces.parsers.SAXParser";

	private static final String SPECIFIC_ENCODING   = "iso-latin-1";
	private static final String SPECIFIC_DTD_PREFIX = "./etc";

	private StringWriter    buffer;
	private Visitor         printer;
	private XMLReader       reader;

	private Perl5Util perl;

	private NodeFactory factory;
	
	private Node _package;
	private Node test_class;
	private Node test_main_method;
	private Node test_test_method;
		
	private Node java_lang_package;
	private Node java_lang_Object_class;
	private Node java_lang_Object_Object_method;
	private Node java_lang_String_class;
		
	private Node java_io_package;
	private Node java_io_Writer_class;
	private Node java_io_Writer_write_method;
		
	private Node java_util_package;
	private Node java_util_Collections_class;
	private Node java_util_Collections_singleton_method;
	private Node java_util_Set_class;

	private StringWriter out;
	private Visitor      visitor;

	protected void setUp() throws Exception {
		buffer  = new StringWriter();
		printer = new XMLPrinter(new PrintWriter(buffer), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

		reader = XMLReaderFactory.createXMLReader(READER_CLASSNAME);
		reader.setFeature("http://xml.org/sax/features/validation", true);
		reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);

		perl = new Perl5Util();

		factory = new NodeFactory();

		_package = factory.CreatePackage("");
		test_class = factory.CreateClass("test");
		test_main_method = factory.CreateFeature("test.main(java.lang.String[])");
		test_test_method = factory.CreateFeature("test.test()");
		
		java_lang_package = factory.CreatePackage("java.lang");
		java_lang_Object_class = factory.CreateClass("java.lang.Object");
		java_lang_Object_Object_method = factory.CreateFeature("java.lang.Object.Object()");
		java_lang_String_class = factory.CreateClass("java.lang.String");

		java_io_package = factory.CreatePackage("java.io");
		java_io_Writer_class = factory.CreateClass("java.io.Writer");
		java_io_Writer_write_method = factory.CreateFeature("java.io.Writer.write(int)");
		
		java_util_package = factory.CreatePackage("java.util");
		java_util_Collections_class = factory.CreateClass("java.util.Collections");
		java_util_Collections_singleton_method = factory.CreateFeature("java.util.Collections.singleton(java.lang.Object)");
		java_util_Set_class = factory.CreateClass("java.util.Set");

		test_class.AddDependency(java_lang_Object_class);
		test_main_method.AddDependency(java_lang_Object_class);
		test_main_method.AddDependency(java_lang_Object_Object_method);
		test_main_method.AddDependency(java_lang_String_class);
		test_main_method.AddDependency(java_util_Collections_singleton_method);
		test_main_method.AddDependency(java_util_Set_class);
		test_test_method.AddDependency(java_lang_Object_Object_method);

		out     = new StringWriter();
		visitor = new XMLPrinter(new PrintWriter(out));
	}

	public void testDefaultDTDPrefix() {
		buffer  = new StringWriter();
		printer = new XMLPrinter(new PrintWriter(buffer));

		String xml_document = buffer.toString();
		assertTrue(xml_document + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xml_document));
		assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"" + XMLPrinter.DEFAULT_DTD_PREFIX + "\"", perl.group(1).startsWith(XMLPrinter.DEFAULT_DTD_PREFIX));
	}
	
	public void testSpecificDTDPrefix() {
		buffer  = new StringWriter();
		printer = new XMLPrinter(new PrintWriter(buffer), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

		String xml_document = buffer.toString();
		assertTrue(xml_document + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xml_document));
		assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"./etc\"", perl.group(1).startsWith(SPECIFIC_DTD_PREFIX));
	}

	public void testDefaultEncoding() {
		buffer  = new StringWriter();
		printer = new XMLPrinter(new PrintWriter(buffer));

		String xml_document = buffer.toString();
		assertTrue(xml_document + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xml_document));
		assertEquals("Encoding", XMLPrinter.DEFAULT_ENCODING, perl.group(1));
	}

	public void testSpecificEncoding() {
		buffer  = new StringWriter();
		printer = new XMLPrinter(new PrintWriter(buffer), SPECIFIC_ENCODING, XMLPrinter.DEFAULT_DTD_PREFIX);

		String xml_document = buffer.toString();
		assertTrue(xml_document + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xml_document));
		assertEquals("Encoding", SPECIFIC_ENCODING, perl.group(1));
	}

	public void testEverything() throws IOException {
		visitor.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\" ?>", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<!DOCTYPE dependencies SYSTEM \"http://depfind.sourceforge.net/dtd/dependencies.dtd\">", in.readLine());
		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "<dependencies>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name></name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>test</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <outbound type=\"class\">java.lang.Object</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>test.main(java.lang.String[])</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <outbound type=\"class\">java.lang.Object</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "                <outbound type=\"feature\">java.lang.Object.Object()</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "                <outbound type=\"class\">java.lang.String</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "                <outbound type=\"feature\">java.util.Collections.singleton(java.lang.Object)</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "                <outbound type=\"class\">java.util.Set</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>test.test()</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <outbound type=\"feature\">java.lang.Object.Object()</outbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>java.io</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>java.io.Writer</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>java.io.Writer.write(int)</name>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>java.lang</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>java.lang.Object</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <inbound type=\"class\">test</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "            <inbound type=\"feature\">test.main(java.lang.String[])</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>java.lang.Object.Object()</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <inbound type=\"feature\">test.main(java.lang.String[])</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "                <inbound type=\"feature\">test.test()</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>java.lang.String</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <inbound type=\"feature\">test.main(java.lang.String[])</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "    <package>", in.readLine());
		assertEquals("line " + ++line_number, "        <name>java.util</name>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>java.util.Collections</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <feature>", in.readLine());
		assertEquals("line " + ++line_number, "                <name>java.util.Collections.singleton(java.lang.Object)</name>", in.readLine());
		assertEquals("line " + ++line_number, "                <inbound type=\"feature\">test.main(java.lang.String[])</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "            </feature>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "        <class>", in.readLine());
		assertEquals("line " + ++line_number, "            <name>java.util.Set</name>", in.readLine());
		assertEquals("line " + ++line_number, "            <inbound type=\"feature\">test.main(java.lang.String[])</inbound>", in.readLine());
		assertEquals("line " + ++line_number, "        </class>", in.readLine());
		assertEquals("line " + ++line_number, "    </package>", in.readLine());
		assertEquals("line " + ++line_number, "</dependencies>", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}
}
