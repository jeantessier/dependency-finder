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

import org.apache.log4j.*;
import org.apache.oro.text.perl.*;

import com.jeantessier.classreader.*;

public class TestDependencyExtractor extends TestCase {
	public static final String TEST_CLASS    = "test";
	public static final String TEST_FILENAME = "classes" + File.separator + "test.class";
	
	NodeFactory factory;
	
	Node _package;
	Node test_class;
	Node test_main_feature;
	Node test_test_feature;
		
	Node java_io_package;
	Node java_io_PrintStream_class;
	Node java_io_PrintStream_println_feature;
	
	Node java_lang_package;
	Node java_lang_NullPointerException_class;
	Node java_lang_Object_class;
	Node java_lang_Object_Object_feature;
	Node java_lang_String_class;
	Node java_lang_System_class;
	Node java_lang_System_out_feature;
		
	Node java_util_package;
	Node java_util_Collections_class;
	Node java_util_Collections_singleton_feature;
	Node java_util_Set_class;

	ClassfileLoader loader;
	NodeFactory     test_factory;

	public TestDependencyExtractor(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		Category.getInstance(getClass().getName()).info("Starting test: " + getName());

		factory = new NodeFactory();

		_package = factory.CreatePackage("");
		test_class = factory.CreateClass("test");
		test_main_feature = factory.CreateFeature("test.main(java.lang.String[])");
		test_test_feature = factory.CreateFeature("test.test()");
		
		java_io_package = factory.CreatePackage("java.io");
		java_io_PrintStream_class = factory.CreateClass("java.io.PrintStream");
		java_io_PrintStream_println_feature = factory.CreateFeature("java.io.PrintStream.println(java.lang.Object)");

		java_lang_package = factory.CreatePackage("java.lang");
		java_lang_NullPointerException_class = factory.CreateClass("java.lang.NullPointerException");
		java_lang_Object_class = factory.CreateClass("java.lang.Object");
		java_lang_Object_Object_feature = factory.CreateFeature("java.lang.Object.Object()");
		java_lang_String_class = factory.CreateClass("java.lang.String");
		java_lang_System_class = factory.CreateClass("java.lang.System");
		java_lang_System_out_feature = factory.CreateFeature("java.lang.System.out");
		
		java_util_package = factory.CreatePackage("java.util");
		java_util_Collections_class = factory.CreateClass("java.util.Collections");
		java_util_Collections_singleton_feature = factory.CreateFeature("java.util.Collections.singleton(java.lang.Object)");
		java_util_Set_class = factory.CreateClass("java.util.Set");
		
		test_class.AddDependency(java_lang_Object_class);
		test_main_feature.AddDependency(java_io_PrintStream_class);
		test_main_feature.AddDependency(java_io_PrintStream_println_feature);
		test_main_feature.AddDependency(java_lang_NullPointerException_class);
		test_main_feature.AddDependency(java_lang_Object_class);
		test_main_feature.AddDependency(java_lang_Object_Object_feature);
		test_main_feature.AddDependency(java_lang_String_class);
		test_main_feature.AddDependency(java_lang_System_out_feature);
		test_main_feature.AddDependency(java_util_Collections_singleton_feature);
		test_main_feature.AddDependency(java_util_Set_class);
		test_test_feature.AddDependency(java_lang_Object_Object_feature);

		loader = new DirectoryClassfileLoader(new String[] {TEST_FILENAME});
		loader.Start();

		test_factory = new NodeFactory();
		loader.Classfile(TEST_CLASS).Accept(new CodeDependencyCollector(test_factory));
	}

	protected void tearDown() throws Exception {
		Category.getInstance(getClass().getName()).info("End of " + getName());
	}
	
	public void testPackageList() {
		assertEquals("Different list of packages",
					 factory.Packages().keySet(),
					 test_factory.Packages().keySet());
	}
	
	public void testClassList() {
		assertEquals("Different list of classes",
					 factory.Classes().keySet(),
					 test_factory.Classes().keySet());
	}
	
	public void testFeatureList() {
		assertEquals("Different list of features",
					 factory.Features().keySet(),
					 test_factory.Features().keySet());
	}
	
	public void testPackages() {
		Iterator i = factory.Packages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Packages().get(key), test_factory.Packages().get(key));
			assertTrue(key + " is same", factory.Packages().get(key) != test_factory.Packages().get(key));
			assertEquals(key + " inbounds",
						 ((Node) factory.Packages().get(key)).Inbound().size(),
						 ((Node) test_factory.Packages().get(key)).Inbound().size());
			assertEquals(key + " outbounds",
						 ((Node) factory.Packages().get(key)).Outbound().size(),
						 ((Node) test_factory.Packages().get(key)).Outbound().size());
		}
	}
	
	public void testClasses() {
		Iterator i = factory.Classes().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Classes().get(key), test_factory.Classes().get(key));
			assertTrue(key + " is same", factory.Classes().get(key) != test_factory.Classes().get(key));
			assertEquals(key + " inbounds",
						 ((Node) factory.Classes().get(key)).Inbound().size(),
						 ((Node) test_factory.Classes().get(key)).Inbound().size());
			assertEquals(key + " outbounds",
						 ((Node) factory.Classes().get(key)).Outbound().size(),
						 ((Node) test_factory.Classes().get(key)).Outbound().size());
		}
	}
	
	public void testFeatures() {
		Iterator i = factory.Features().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Features().get(key), test_factory.Features().get(key));
			assertTrue(key + " is same", factory.Features().get(key) != test_factory.Features().get(key));
			assertEquals(key + " inbounds",
						 ((Node) factory.Features().get(key)).Inbound().size(),
						 ((Node) test_factory.Features().get(key)).Inbound().size());
			assertEquals(key + " outbounds",
						 ((Node) factory.Features().get(key)).Outbound().size(),
						 ((Node) test_factory.Features().get(key)).Outbound().size());
		}
	}
}
