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

package com.jeantessier.metrics;

import junit.framework.*;

import java.io.*;
import java.util.*;

import org.xml.sax.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

public class TestMetricsGathererDependenciesScope extends TestCase {
	public static final String TEST_DIRNAME  = "classes" + File.separator + "testpackage";
	public static final String OTHER_DIRNAME = "classes" + File.separator + "otherpackage";

	private MetricsFactory  factory;
	private ClassfileLoader loader;
	private MetricsGatherer gatherer;
	
	protected void setUp() throws Exception {
		Logger.getLogger(getClass()).debug("Starting " + getName() + " ...");
		
		factory = new MetricsFactory("test", new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).Load("etc" + File.separator + "MetricsConfig.xml"));

		Collection dirs = new ArrayList(2);
		dirs.add(TEST_DIRNAME);
		dirs.add(OTHER_DIRNAME);
		loader = new AggregatingClassfileLoader();
		loader.Load(dirs);

		gatherer = new MetricsGatherer("test", factory);
	}

	protected void tearDown() throws Exception {
		Logger.getLogger(getClass()).debug("Done with " + getName() + " ...");
	}
	
	public void testpackage_TestClass_TestMethod_IntraClass() {
		Collection scope_includes = new HashSet();
		scope_includes.add("testpackage.TestClass.SourceMethod()");

		gatherer.ScopeIncludes(scope_includes);
		gatherer.VisitClassfiles(loader.Classfiles());

		Collection dependencies;

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES)).Values();
		assertTrue(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES + " " + dependencies + " missing testpackage.TestClass.SourceMethod()", dependencies.contains("testpackage.TestClass.SourceMethod()"));
		assertEquals(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES + " " + dependencies, 1, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
	}
	
	public void testpackage_TestClass_TestMethod_IntraPackage() {
		Collection scope_includes = new HashSet();
		scope_includes.add("testpackage.SourceClass.SourceMethod()");

		gatherer.ScopeIncludes(scope_includes);
		gatherer.VisitClassfiles(loader.Classfiles());

		Collection dependencies;

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertTrue(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies + " missing testpackage.SourceClass.SourceMethod()", dependencies.contains("testpackage.SourceClass.SourceMethod()"));
		assertEquals(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 1, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
	}
	
	public void testpackage_TestClass_TestMethod_ExtraPackage() {
		Collection filter_includes = new HashSet();
		filter_includes.add("otherpackage.SourceClass.SourceMethod()");

		gatherer.ScopeIncludes(filter_includes);
		gatherer.VisitClassfiles(loader.Classfiles());

		Collection dependencies;

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertTrue(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies + " missing otherpackage.SourceClass.SourceMethod()", dependencies.contains("otherpackage.SourceClass.SourceMethod()"));
		assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 1, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("testpackage.TestClass.TestMethod(java.lang.String)").Measurement(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
	}
	
	public void testpackage_TestClass_IntraPackageClass() {
		Collection scope_includes = new HashSet();
		scope_includes.add("testpackage.SourceClass");

		gatherer.ScopeIncludes(scope_includes);
		gatherer.VisitClassfiles(loader.Classfiles());

		Collection dependencies;

		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.INBOUND_INTRA_PACKAGE_DEPENDENCIES)).Values();
		assertTrue(Metrics.INBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies + " missing testpackage.SourceClass", dependencies.contains("testpackage.SourceClass"));
		assertEquals(Metrics.INBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 1, dependencies.size());
		
		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.INBOUND_EXTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
		
		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
		
		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
	}
	
	public void testpackage_TestClass_ExtraPackageClass() {
		Collection scope_includes = new HashSet();
		scope_includes.add("otherpackage.SourceClass");

		gatherer.ScopeIncludes(scope_includes);
		gatherer.VisitClassfiles(loader.Classfiles());

		Collection dependencies;

		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.INBOUND_INTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
		
		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.INBOUND_EXTRA_PACKAGE_DEPENDENCIES)).Values();
		assertTrue(Metrics.INBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies + " missing otherpackage.SourceClass", dependencies.contains("otherpackage.SourceClass"));
		assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 1, dependencies.size());
		
		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
		
		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
	}
	
	public void testpackage_TestClass_IntraPackageMethod() {
		Collection scope_includes = new HashSet();
		scope_includes.add("testpackage.SourceClass.SourceMethod()");

		gatherer.ScopeIncludes(scope_includes);
		gatherer.VisitClassfiles(loader.Classfiles());

		Collection dependencies;

		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.INBOUND_INTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
		
		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.INBOUND_EXTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
		
		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
		
		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertTrue(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies + " missing testpackage.SourceClass.SourceMethod()", dependencies.contains("testpackage.SourceClass.SourceMethod()"));
		assertEquals(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 1, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
	}
	
	public void testpackage_TestClass_ExtraPackageMethod() {
		Collection scope_includes = new HashSet();
		scope_includes.add("otherpackage.SourceClass.SourceMethod()");

		gatherer.ScopeIncludes(scope_includes);
		gatherer.VisitClassfiles(loader.Classfiles());

		Collection dependencies;

		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.INBOUND_INTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
		
		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.INBOUND_EXTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
		
		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
		
		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("testpackage.TestClass").Measurement(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertTrue(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies + " missing otherpackage.SourceClass.SourceMethod()", dependencies.contains("otherpackage.SourceClass.SourceMethod()"));
		assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 1, dependencies.size());
	}
}
