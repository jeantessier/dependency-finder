/*
 *  Dependency Finder - Computes quality factors from compiled Java code
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeantessier.dependency;

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.apache.oro.text.perl.*;

public class TestPrettyPrinter extends TestCase {
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

	public TestPrettyPrinter(String name) {
		super(name);
	}

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

		visitor = new PrettyPrinter();
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
