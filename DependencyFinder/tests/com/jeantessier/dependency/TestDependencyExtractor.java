/*
 *  Copyright (c) 2001-2002, Jean Tessier
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

	protected void setUp() throws Exception {
		Logger.getLogger(getClass()).info("Starting test: " + getName());

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

		loader = new AggregatingClassfileLoader();
		loader.Load(Collections.singleton(TEST_FILENAME));

		test_factory = new NodeFactory();
		loader.Classfile(TEST_CLASS).Accept(new CodeDependencyCollector(test_factory));
	}

	protected void tearDown() throws Exception {
		Logger.getLogger(getClass()).info("End of " + getName());
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
		while (i.hasNext()) {
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
		while (i.hasNext()) {
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
		while (i.hasNext()) {
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

	public void testStaticInitializer() throws IOException {
		ClassfileLoader loader  = new AggregatingClassfileLoader();
		NodeFactory     factory = new NodeFactory();
		
		loader.Load(Collections.singleton("classes" + File.separator + "StaticInitializerTest.class"));

		Classfile classfile = loader.Classfile("StaticInitializerTest");
		classfile.Accept(new CodeDependencyCollector(factory));

		Collection feature_names = factory.Features().keySet();
		
		Iterator i = classfile.Methods().iterator();
		while (i.hasNext()) {
			Method_info method = (Method_info) i.next();
			assertTrue("Missing method " + method.FullSignature(), feature_names.contains(method.FullSignature()));
		}
	}
}
