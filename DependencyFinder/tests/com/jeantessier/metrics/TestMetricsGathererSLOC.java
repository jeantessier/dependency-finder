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

package com.jeantessier.metrics;

import junit.framework.*;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import org.xml.sax.*;

import com.jeantessier.classreader.*;

public class TestMetricsGathererSLOC extends TestCase {
	public static final String TEST_DIRNAME = "classes" + File.separator + "sloc";

	private MetricsFactory factory;
	
	protected void setUp() throws Exception {
		Logger.getLogger(getClass()).debug("Starting " + getName() + " ...");
		
		factory = new MetricsFactory("test", new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).load("etc" + File.separator + "MetricsConfig.xml"));

		ClassfileLoader loader =new AggregatingClassfileLoader();
		loader.load(Collections.singleton(TEST_DIRNAME));

		MetricsGatherer gatherer = new MetricsGatherer("test", factory);
		
		Iterator i = loader.getAllClassfiles().iterator();
		while (i.hasNext()) {
			((Classfile) i.next()).accept(gatherer);
		}
	}

	protected void tearDown() throws Exception {
		Logger.getLogger(getClass()).debug("Done with " + getName() + " ...");
	}
	
	public void test_sloc_TestInterface() {
		assertEquals(Metrics.SLOC, 3, factory.createClassMetrics("sloc.TestInterface").getMeasurement(Metrics.SLOC).intValue());
		assertEquals("M", 2, factory.createClassMetrics("sloc.TestInterface").getMeasurement("M").intValue());
		assertEquals("AM", 2, factory.createClassMetrics("sloc.TestInterface").getMeasurement("AM").intValue());
		assertEquals("SynthM", 0, factory.createClassMetrics("sloc.TestInterface").getMeasurement("SynthM").intValue());
	}
	
	public void test_sloc_TestInterface_Method1() {
		assertEquals(Metrics.SLOC, 1, factory.createMethodMetrics("sloc.TestInterface.Method1()").getMeasurement(Metrics.SLOC).intValue());
	}
	
	public void test_sloc_TestInterface_Method2() {
		assertEquals(Metrics.SLOC, 1, factory.createMethodMetrics("sloc.TestInterface.Method2()").getMeasurement(Metrics.SLOC).intValue());
	}
	
	public void test_sloc_TestAbstractClass() {
		assertEquals(Metrics.SLOC, 15, factory.createClassMetrics("sloc.TestAbstractClass").getMeasurement(Metrics.SLOC).intValue());
		assertEquals("M", 2, factory.createClassMetrics("sloc.TestAbstractClass").getMeasurement("M").intValue());
		assertEquals("AM", 0, factory.createClassMetrics("sloc.TestAbstractClass").getMeasurement("AM").intValue());
		assertEquals("SynthM", 0, factory.createClassMetrics("sloc.TestAbstractClass").getMeasurement("SynthM").intValue());
	}
	
	public void test_sloc_TestAbstractClass_Method1() {
		assertEquals(Metrics.SLOC, 13, factory.createMethodMetrics("sloc.TestAbstractClass.Method1()").getMeasurement(Metrics.SLOC).intValue());
	}
	
	public void test_sloc_TestAbstractClass_Method2() {
		assertEquals(Metrics.SLOC, 0, factory.createMethodMetrics("sloc.TestAbstractClass.Method2()").getMeasurement(Metrics.SLOC).intValue());
	}
	
	public void test_sloc_TestSuperClass() {
		assertEquals(Metrics.SLOC, 2, factory.createClassMetrics("sloc.TestSuperClass").getMeasurement(Metrics.SLOC).intValue());
		assertEquals("M", 1, factory.createClassMetrics("sloc.TestSuperClass").getMeasurement("M").intValue());
		assertEquals("ABSM", 0, factory.createClassMetrics("sloc.TestSuperClass").getMeasurement("ABSM").intValue());
		assertEquals("SYNTHM", 0, factory.createClassMetrics("sloc.TestSuperClass").getMeasurement("SYNTHM").intValue());
	}
	
	public void test_sloc_TestClass() {
		assertEquals(Metrics.SLOC, 4, factory.createClassMetrics("sloc.TestClass").getMeasurement(Metrics.SLOC).intValue());
		assertEquals("M", 2, factory.createClassMetrics("sloc.TestClass").getMeasurement("M").intValue());
		assertEquals("ABSM", 0, factory.createClassMetrics("sloc.TestClass").getMeasurement("ABSM").intValue());
		assertEquals("SYNTHM", 0, factory.createClassMetrics("sloc.TestClass").getMeasurement("SYNTHM").intValue());
	}
	
	public void test_sloc_TestClass_Method1() {
		assertEquals(Metrics.SLOC, 0, factory.createMethodMetrics("sloc.TestClass.Method1()").getMeasurement(Metrics.SLOC).intValue());
	}
	
	public void test_sloc_TestClass_Method2() {
		assertEquals(Metrics.SLOC, 2, factory.createMethodMetrics("sloc.TestClass.Method2()").getMeasurement(Metrics.SLOC).intValue());
	}

	public void test_sloc() {
		assertEquals(Metrics.SLOC, 24, factory.createGroupMetrics("sloc").getMeasurement(Metrics.SLOC).intValue());
	}

	public void testProject() {
		assertEquals(Metrics.SLOC, 24, factory.createProjectMetrics("test").getMeasurement(Metrics.SLOC).intValue());
	}
}
