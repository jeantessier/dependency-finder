/*
 *  Copyright (c) 2001-2004, Jean Tessier
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

public class TestMetricsGatherer extends TestCase {
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

	private MetricsGatherer metrics;

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

		metrics = new MetricsGatherer();
	}

	public void testEverything() throws IOException {
		metrics.TraverseNodes(factory.Packages().values());

		assertEquals("Number of packages", 4, metrics.Packages().size());
		assertEquals("Number of classes",  6, metrics.Classes().size());
		assertEquals("Number of features", 5, metrics.Features().size());

		assertEquals("Number of inbounds",             7, metrics.NbInbound());
		assertEquals("Number of inbounds to packages", 0, metrics.NbInboundPackages());
		assertEquals("Number of inbounds to classes",  4, metrics.NbInboundClasses());
		assertEquals("Number of inbounds to features", 3, metrics.NbInboundFeatures());

		assertEquals("Number of outbounds",               7, metrics.NbOutbound());
		assertEquals("Number of outbounds from packages", 0, metrics.NbOutboundPackages());
		assertEquals("Number of outbounds from classes",  1, metrics.NbOutboundClasses());
		assertEquals("Number of outbounds from features", 6, metrics.NbOutboundFeatures());
	}
}
