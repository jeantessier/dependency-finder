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

import junit.framework.*;

public class TestTextPrinter extends TestCase {
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
	private TextPrinter  visitor;

	protected void setUp() throws Exception {
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
		visitor = new TextPrinter(new PrintWriter(out));
	}

	public void testEverything() throws IOException {
		visitor.ShowInbounds(true);
		visitor.ShowOutbounds(true);
		visitor.ShowEmptyNodes(true);
		visitor.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "    test", in.readLine());
		assertEquals("line " + ++line_number, "        --> java.lang.Object", in.readLine());
		assertEquals("line " + ++line_number, "        main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.lang.Object", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.lang.Object.Object()", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.lang.String", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.util.Collections.singleton(java.lang.Object)", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.util.Set", in.readLine());
		assertEquals("line " + ++line_number, "        test()", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.lang.Object.Object()", in.readLine());
		assertEquals("line " + ++line_number, "java.io", in.readLine());
		assertEquals("line " + ++line_number, "    Writer", in.readLine());
		assertEquals("line " + ++line_number, "        write(int)", in.readLine());
		assertEquals("line " + ++line_number, "java.lang", in.readLine());
		assertEquals("line " + ++line_number, "    Object", in.readLine());
		assertEquals("line " + ++line_number, "        <-- test", in.readLine());
		assertEquals("line " + ++line_number, "        <-- test.main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "        Object()", in.readLine());
		assertEquals("line " + ++line_number, "            <-- test.main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "            <-- test.test()", in.readLine());
		assertEquals("line " + ++line_number, "    String", in.readLine());
		assertEquals("line " + ++line_number, "        <-- test.main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "java.util", in.readLine());
		assertEquals("line " + ++line_number, "    Collections", in.readLine());
		assertEquals("line " + ++line_number, "        singleton(java.lang.Object)", in.readLine());
		assertEquals("line " + ++line_number, "            <-- test.main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "    Set", in.readLine());
		assertEquals("line " + ++line_number, "        <-- test.main(java.lang.String[])", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testNoEmpty() throws IOException {
		visitor.ShowInbounds(true);
		visitor.ShowOutbounds(true);
		visitor.ShowEmptyNodes(false);
		visitor.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "    test", in.readLine());
		assertEquals("line " + ++line_number, "        --> java.lang.Object", in.readLine());
		assertEquals("line " + ++line_number, "        main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.lang.Object", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.lang.Object.Object()", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.lang.String", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.util.Collections.singleton(java.lang.Object)", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.util.Set", in.readLine());
		assertEquals("line " + ++line_number, "        test()", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.lang.Object.Object()", in.readLine());
		assertEquals("line " + ++line_number, "java.lang", in.readLine());
		assertEquals("line " + ++line_number, "    Object", in.readLine());
		assertEquals("line " + ++line_number, "        <-- test", in.readLine());
		assertEquals("line " + ++line_number, "        <-- test.main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "        Object()", in.readLine());
		assertEquals("line " + ++line_number, "            <-- test.main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "            <-- test.test()", in.readLine());
		assertEquals("line " + ++line_number, "    String", in.readLine());
		assertEquals("line " + ++line_number, "        <-- test.main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "java.util", in.readLine());
		assertEquals("line " + ++line_number, "    Collections", in.readLine());
		assertEquals("line " + ++line_number, "        singleton(java.lang.Object)", in.readLine());
		assertEquals("line " + ++line_number, "            <-- test.main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "    Set", in.readLine());
		assertEquals("line " + ++line_number, "        <-- test.main(java.lang.String[])", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowInboundsWithEmpty() throws IOException {
		visitor.ShowInbounds(true);
		visitor.ShowOutbounds(false);
		visitor.ShowEmptyNodes(true);
		visitor.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "    test", in.readLine());
		assertEquals("line " + ++line_number, "        main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "        test()", in.readLine());
		assertEquals("line " + ++line_number, "java.io", in.readLine());
		assertEquals("line " + ++line_number, "    Writer", in.readLine());
		assertEquals("line " + ++line_number, "        write(int)", in.readLine());
		assertEquals("line " + ++line_number, "java.lang", in.readLine());
		assertEquals("line " + ++line_number, "    Object", in.readLine());
		assertEquals("line " + ++line_number, "        <-- test", in.readLine());
		assertEquals("line " + ++line_number, "        <-- test.main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "        Object()", in.readLine());
		assertEquals("line " + ++line_number, "            <-- test.main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "            <-- test.test()", in.readLine());
		assertEquals("line " + ++line_number, "    String", in.readLine());
		assertEquals("line " + ++line_number, "        <-- test.main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "java.util", in.readLine());
		assertEquals("line " + ++line_number, "    Collections", in.readLine());
		assertEquals("line " + ++line_number, "        singleton(java.lang.Object)", in.readLine());
		assertEquals("line " + ++line_number, "            <-- test.main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "    Set", in.readLine());
		assertEquals("line " + ++line_number, "        <-- test.main(java.lang.String[])", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowInboundsWithoutEmpty() throws IOException {
		visitor.ShowInbounds(true);
		visitor.ShowOutbounds(false);
		visitor.ShowEmptyNodes(false);
		visitor.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "java.lang", in.readLine());
		assertEquals("line " + ++line_number, "    Object", in.readLine());
		assertEquals("line " + ++line_number, "        <-- test", in.readLine());
		assertEquals("line " + ++line_number, "        <-- test.main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "        Object()", in.readLine());
		assertEquals("line " + ++line_number, "            <-- test.main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "            <-- test.test()", in.readLine());
		assertEquals("line " + ++line_number, "    String", in.readLine());
		assertEquals("line " + ++line_number, "        <-- test.main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "java.util", in.readLine());
		assertEquals("line " + ++line_number, "    Collections", in.readLine());
		assertEquals("line " + ++line_number, "        singleton(java.lang.Object)", in.readLine());
		assertEquals("line " + ++line_number, "            <-- test.main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "    Set", in.readLine());
		assertEquals("line " + ++line_number, "        <-- test.main(java.lang.String[])", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowOutboundsWithEmpty() throws IOException {
		visitor.ShowInbounds(false);
		visitor.ShowOutbounds(true);
		visitor.ShowEmptyNodes(true);
		visitor.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "    test", in.readLine());
		assertEquals("line " + ++line_number, "        --> java.lang.Object", in.readLine());
		assertEquals("line " + ++line_number, "        main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.lang.Object", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.lang.Object.Object()", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.lang.String", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.util.Collections.singleton(java.lang.Object)", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.util.Set", in.readLine());
		assertEquals("line " + ++line_number, "        test()", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.lang.Object.Object()", in.readLine());
		assertEquals("line " + ++line_number, "java.io", in.readLine());
		assertEquals("line " + ++line_number, "    Writer", in.readLine());
		assertEquals("line " + ++line_number, "        write(int)", in.readLine());
		assertEquals("line " + ++line_number, "java.lang", in.readLine());
		assertEquals("line " + ++line_number, "    Object", in.readLine());
		assertEquals("line " + ++line_number, "        Object()", in.readLine());
		assertEquals("line " + ++line_number, "    String", in.readLine());
		assertEquals("line " + ++line_number, "java.util", in.readLine());
		assertEquals("line " + ++line_number, "    Collections", in.readLine());
		assertEquals("line " + ++line_number, "        singleton(java.lang.Object)", in.readLine());
		assertEquals("line " + ++line_number, "    Set", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowOutboundsWithoutEmpty() throws IOException {
		visitor.ShowInbounds(false);
		visitor.ShowOutbounds(true);
		visitor.ShowEmptyNodes(false);
		visitor.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "    test", in.readLine());
		assertEquals("line " + ++line_number, "        --> java.lang.Object", in.readLine());
		assertEquals("line " + ++line_number, "        main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.lang.Object", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.lang.Object.Object()", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.lang.String", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.util.Collections.singleton(java.lang.Object)", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.util.Set", in.readLine());
		assertEquals("line " + ++line_number, "        test()", in.readLine());
		assertEquals("line " + ++line_number, "            --> java.lang.Object.Object()", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testEmpty() throws IOException {
		visitor.ShowInbounds(false);
		visitor.ShowOutbounds(false);
		visitor.ShowEmptyNodes(true);
		visitor.TraverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "", in.readLine());
		assertEquals("line " + ++line_number, "    test", in.readLine());
		assertEquals("line " + ++line_number, "        main(java.lang.String[])", in.readLine());
		assertEquals("line " + ++line_number, "        test()", in.readLine());
		assertEquals("line " + ++line_number, "java.io", in.readLine());
		assertEquals("line " + ++line_number, "    Writer", in.readLine());
		assertEquals("line " + ++line_number, "        write(int)", in.readLine());
		assertEquals("line " + ++line_number, "java.lang", in.readLine());
		assertEquals("line " + ++line_number, "    Object", in.readLine());
		assertEquals("line " + ++line_number, "        Object()", in.readLine());
		assertEquals("line " + ++line_number, "    String", in.readLine());
		assertEquals("line " + ++line_number, "java.util", in.readLine());
		assertEquals("line " + ++line_number, "    Collections", in.readLine());
		assertEquals("line " + ++line_number, "        singleton(java.lang.Object)", in.readLine());
		assertEquals("line " + ++line_number, "    Set", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testNothing() {
		visitor.ShowInbounds(false);
		visitor.ShowOutbounds(false);
		visitor.ShowEmptyNodes(false);
		visitor.TraverseNodes(factory.Packages().values());

		assertEquals("out.toString()", "", out.toString());
	}
}
