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

package com.jeantessier.metrics;

import junit.framework.*;

import java.io.*;

import org.xml.sax.*;

import com.jeantessier.classreader.*;

public class TestMetricsGatherer extends TestCase {
	public static final String TEST_CLASS    = "test";
	public static final String TEST_FILENAME = "classes" + File.separator + "test.class";

	private MetricsFactory factory;
	
	public TestMetricsGatherer(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		factory = new MetricsFactory("test", new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).Load("etc" + File.separator + "MetricsConfig.xml"));

		DirectoryClassfileLoader loader = new DirectoryClassfileLoader(new AggregatingClassfileLoader());
		loader.Load(new DirectoryExplorer(TEST_FILENAME));

		loader.Classfile(TEST_CLASS).Accept(new MetricsGatherer("test", factory));
	}
	
	public void testNbElements() {
		assertEquals("factory.ProjectNames().size()", 1, factory.ProjectNames().size());
		assertTrue(factory.ProjectNames().toString() + " does not contain project \"test\"", factory.ProjectNames().contains("test"));
		assertEquals("factory.GroupNames().size()",   1, factory.GroupNames().size());
		assertTrue(factory.GroupNames().toString() + " does not contain package \"\"", factory.GroupNames().contains(""));
		assertEquals("factory.ClassNames().size()",   1, factory.ClassNames().size());
		assertTrue(factory.ClassNames().toString() + " does not contain class \"test\"", factory.ClassNames().contains("test"));
		assertEquals("factory.MethodNames().size()",  2, factory.MethodNames().size());
		assertTrue(factory.MethodNames().toString() + " does not contain method \"test.test()\"", factory.MethodNames().contains("test.test()"));
		assertTrue(factory.MethodNames().toString() + " does not contain method \"test.main(java.lang.String[])\"", factory.MethodNames().contains("test.main(java.lang.String[])"));
	}
	
	public void test_test_test() {
		assertEquals(Metrics.SLOC, 1, factory.CreateMethodMetrics("test.test()").Measurement(Metrics.SLOC).intValue());
		assertEquals(Metrics.PARAMETERS, 0, factory.CreateMethodMetrics("test.test()").Measurement(Metrics.PARAMETERS).intValue());
		assertEquals(Metrics.LOCAL_VARIABLES, 1, factory.CreateMethodMetrics("test.test()").Measurement(Metrics.LOCAL_VARIABLES).intValue());
	}
	
	public void test_test_main() {
		assertEquals(Metrics.SLOC, 4, factory.CreateMethodMetrics("test.main(java.lang.String[])").Measurement(Metrics.SLOC).intValue());
		assertEquals(Metrics.PARAMETERS, 1, factory.CreateMethodMetrics("test.main(java.lang.String[])").Measurement(Metrics.PARAMETERS).intValue());
		assertEquals(Metrics.LOCAL_VARIABLES, 3, factory.CreateMethodMetrics("test.main(java.lang.String[])").Measurement(Metrics.LOCAL_VARIABLES).intValue());
	}
	
	public void test_test() {
		assertEquals(Metrics.DEPTH_OF_INHERITANCE, 1, factory.CreateClassMetrics("test").Measurement(Metrics.DEPTH_OF_INHERITANCE).intValue());
		assertEquals("M", 2, factory.CreateClassMetrics("test").Measurement("M").intValue());
		assertEquals(Metrics.PUBLIC_METHODS, 2, factory.CreateClassMetrics("test").Measurement(Metrics.PUBLIC_METHODS).intValue());
		assertEquals(Metrics.PROTECTED_METHODS, 0, factory.CreateClassMetrics("test").Measurement(Metrics.PROTECTED_METHODS).intValue());
		assertEquals(Metrics.PRIVATE_METHODS, 0, factory.CreateClassMetrics("test").Measurement(Metrics.PRIVATE_METHODS).intValue());
		assertEquals(Metrics.PACKAGE_METHODS, 0, factory.CreateClassMetrics("test").Measurement(Metrics.PACKAGE_METHODS).intValue());
		assertEquals(Metrics.SYNTHETIC_METHODS, 0, factory.CreateClassMetrics("test").Measurement(Metrics.SYNTHETIC_METHODS).intValue());
		assertEquals(Metrics.STATIC_METHODS, 1, factory.CreateClassMetrics("test").Measurement(Metrics.STATIC_METHODS).intValue());
		assertEquals("PUBMR", 1, factory.CreateClassMetrics("test").Measurement("PUBMR").doubleValue(), 0.01);
		assertEquals("STAMR", 0.5, factory.CreateClassMetrics("test").Measurement("STAMR").doubleValue(), 0.01);
		assertEquals(Metrics.SLOC, 5, factory.CreateClassMetrics("test").Measurement(Metrics.SLOC).intValue());
	}

	public void test_() {
		assertEquals(Metrics.SLOC, 5, factory.CreateGroupMetrics("").Measurement(Metrics.SLOC).intValue());
	}

	public void testProject() {
		// assertEquals(Metrics.SLOC, 5, factory.CreateProjectMetrics("Project").Measurement(Metrics.SLOC).intValue());
		assertEquals(Metrics.SLOC, 5, factory.CreateProjectMetrics("test").Measurement(Metrics.SLOC).intValue());
	}
}
