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
	
	public TestMetricsGatherer(String name) {
		super(name);
	}

	public void testCreateProjectMetrics() throws IOException, SAXException {
		MetricsFactory  factory = new MetricsFactory("test", new MetricsConfigurationLoader().Load("info/MetricsConfig.xml"));

		DirectoryClassfileLoader loader = new DirectoryClassfileLoader(new AggregatingClassfileLoader());
		loader.Load(new DirectoryExplorer(TEST_FILENAME));

		loader.Classfile(TEST_CLASS).Accept(new MetricsGatherer("test", factory));

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
}
