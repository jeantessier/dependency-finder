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
import java.util.*;

import junit.framework.*;

import org.apache.oro.text.perl.*;

public class TestLinkMinimizer extends TestCase {
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
	
	LinkMinimizer visitor;

	protected void setUp() throws Exception {
		factory = new NodeFactory();

		_package = factory.CreatePackage("");
		test_class = factory.CreateClass("test");
		test_main_method = factory.CreateFeature("test.main(String[])");
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

		visitor = new LinkMinimizer();
		visitor.TraverseNodes(factory.Packages().values());
	}

	public void test_package() {
		assertEquals("_package.Outbound()",
					 0,
					 _package.Outbound().size());
		assertEquals("_package.Inbound()",
					 0,
					 _package.Inbound().size());
	}

	public void testtest_class() {
		assertEquals("test_class.Outbound()",
					 0,
					 test_class.Outbound().size());
		assertEquals("test_class.Inbound()",
					 0,
					 test_class.Inbound().size());
	}

	public void testtest_main_method() {
		assertEquals("test_main_method.Outbound()",
					 4,
					 test_main_method.Outbound().size());
		assertTrue("test.main(java.lang.String[]) missing " + java_lang_Object_Object_method,
				   test_main_method.Outbound().contains(java_lang_Object_Object_method));
		assertTrue("test.main(java.lang.String[]) missing " + java_lang_String_class,
				   test_main_method.Outbound().contains(java_lang_String_class));
		assertTrue("test.main(java.lang.String[]) missing " + java_util_Collections_singleton_method,
				   test_main_method.Outbound().contains(java_util_Collections_singleton_method));
		assertTrue("test.main(java.lang.String[]) missing " + java_util_Set_class,
				   test_main_method.Outbound().contains(java_util_Set_class));
		assertEquals("test_main_method.Inbound()",
					 0,
					 test_main_method.Inbound().size());
	}

	public void testtest_test_method() {
		assertEquals("test_test_method.Outbound()",
					 1,
					 test_test_method.Outbound().size());
		assertTrue("test.test() missing " + java_lang_Object_Object_method,
				   test_test_method.Outbound().contains(java_lang_Object_Object_method));
		assertEquals("_package.Inbound()",
					 0,
					 test_test_method.Inbound().size());
	}

	public void testjava_lang_package() {
		assertEquals("java_lang_package.Outbound()",
					 0,
					 java_lang_package.Outbound().size());
		assertEquals("java_lang_package.Inbound()",
					 0,
					 java_lang_package.Inbound().size());
	}

	public void testjava_lang_Object_class() {
		assertEquals("java_lang_Object_class.Outbound()",
					 0,
					 java_lang_Object_class.Outbound().size());
		assertEquals("java_lang_Object_class.Inbound()",
					 0,
					 java_lang_Object_class.Inbound().size());
	}

	public void testjava_lang_Object_Object_method() {
		assertEquals("java_lang_Object_Object_method.Outbound()",
					 0,
					 java_lang_Object_Object_method.Outbound().size());
		assertEquals("java_lang_Object_Object_method.Inbound()",
					 2,
					 java_lang_Object_Object_method.Inbound().size());
		assertTrue("java.lang.Object.Object() missing " + test_main_method,
				   java_lang_Object_Object_method.Inbound().contains(test_main_method));
		assertTrue("java.lang.Object.Object() missing " + test_test_method,
				   java_lang_Object_Object_method.Inbound().contains(test_test_method));
	}

	public void testjava_lang_String_class() {
		assertEquals("java_lang_String_class.Outbound()",
					 0,
					 java_lang_String_class.Outbound().size());
		assertEquals("java_lang_String_class.Inbound()",
					 1,
					 java_lang_String_class.Inbound().size());
		assertTrue("java.lang.String missing " + test_main_method,
				   java_lang_String_class.Inbound().contains(test_main_method));
	}

	public void testjava_util_package() {
		assertEquals("java_util_package.Outbound()",
					 0,
					 java_util_package.Outbound().size());
		assertEquals("java_util_package.Inbound()",
					 0,
					 java_util_package.Inbound().size());
	}

	public void testjava_util_Collections_class() {
		assertEquals("java_util_Collections_class.Outbound()",
					 0,
					 java_util_Collections_class.Outbound().size());
		assertEquals("java_util_Collections_class.Inbound()",
					 0,
					 java_util_Collections_class.Inbound().size());
	}

	public void testjava_util_Collections_singleton_method() {
		assertEquals("java_util_Collections_singleton_method.Outbound()",
					 0,
					 java_util_Collections_singleton_method.Outbound().size());
		assertEquals("java_util_Collections_singleton_method.Inbound()",
					 1,
					 java_util_Collections_singleton_method.Inbound().size());
		assertTrue("java.util.Collections.singleton(java.lang.Object) missing " + test_main_method,
				   java_util_Collections_singleton_method.Inbound().contains(test_main_method));
	}

	public void testjava_util_Set_class() {
		assertEquals("java_util_Set_class.Outbound()",
					 0,
					 java_util_Set_class.Outbound().size());
		assertEquals("java_util_Set_class.Inbound()",
					 1,
					 java_util_Set_class.Inbound().size());
		assertTrue("java.util.Set missing " + test_main_method,
				   java_util_Set_class.Inbound().contains(test_main_method));
	}
}
