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
 *  	* Neither the name of the Jean Tessier nor the names of his contributors
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
import java.util.*;

import junit.framework.*;

import org.apache.oro.text.perl.*;

public class TestTextPrinter extends TestCase {
	NodeFactory factory;
	
	Node _package;
	Node test_class;
	Node test_main_method;
	Node test_test_method;
		
	Node java_lang_package;
	Node java_lang_Object_class;
	Node java_lang_Object_Object_method;
	Node java_lang_String_class;
		
	Node java_util_package;
	Node java_util_Collections_class;
	Node java_util_Collections_singleton_method;
	Node java_util_Set_class;

	Visitor visitor;

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

		visitor = new TextPrinter();
	}

	public void testPrinter() {
		visitor.TraverseNodes(factory.Packages().values());

		int pos = -1;

		pos = visitor.toString().indexOf("--> java.lang.Object", pos + 1);
		assertTrue("test --> java.lang.Object\n" + visitor, pos != -1);

		pos = visitor.toString().indexOf("--> java.lang.Object", pos + 1);
		assertTrue("test.main(java.lang.String[]) --> java.lang.Object\n" + visitor, pos != -1);

		pos = visitor.toString().indexOf("--> java.lang.Object.Object()", pos + 1);
		assertTrue("test.main(java.lang.String[]) --> java.lang.Object.Object()\n" + visitor, pos != -1);
		
		pos = visitor.toString().indexOf("--> java.lang.String", pos + 1);
		assertTrue("test.main(java.lang.String[]) --> java.lang.String\n" + visitor, pos != -1);

		pos = visitor.toString().indexOf("--> java.util.Collections.singleton(java.lang.Object)", pos + 1);
		assertTrue("test.main(java.lang.String[]) --> java.util.Collections.singleton(java.lang.Object)\n" + visitor, pos != -1);

		pos = visitor.toString().indexOf("--> java.util.Set", pos + 1);
		assertTrue("test.main(java.lang.String[]) --> java.util.Set\n" + visitor, pos != -1);

		pos = visitor.toString().indexOf("--> java.lang.Object.Object()", pos + 1);
		assertTrue("test.test() --> java.lang.Object.Object()\n" + visitor, pos != -1);
		
		pos = visitor.toString().indexOf("<-- test", pos + 1);
		assertTrue("java.lang.Object <-- test\n" + visitor, pos != -1);

		pos = visitor.toString().indexOf("<-- test.main(java.lang.String[])", pos + 1);
		assertTrue("java.lang.Object <-- test.main(java.lang.String[])\n" + visitor, pos != -1);

		pos = visitor.toString().indexOf("<-- test.main(java.lang.String[])", pos + 1);
		assertTrue("java.lang.Object.Object() <-- test.main(java.lang.String[])\n" + visitor, pos != -1);

		pos = visitor.toString().indexOf("<-- test.test()", pos + 1);
		assertTrue("java.lang.Object.Object() <-- test.test()\n" + visitor, pos != -1);

		pos = visitor.toString().indexOf("<-- test.main(java.lang.String[])", pos + 1);
		assertTrue("java.lang.String <-- test.main(java.lang.String[])\n" + visitor, pos != -1);

		pos = visitor.toString().indexOf("<-- test.main(java.lang.String[])", pos + 1);
		assertTrue("java.util.Collections(java.lang.Object) <-- test.main(java.lang.String[])\n" + visitor, pos != -1);

		pos = visitor.toString().indexOf("<-- test.main(java.lang.String[])", pos + 1);
		assertTrue("java.util.Set <-- test.main(java.lang.String[])\n" + visitor, pos != -1);
	}
}
