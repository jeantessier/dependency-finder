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

import org.xml.sax.*;

import com.jeantessier.classreader.*;

public class TestMetricsGatherer extends TestCase {
	public static final String TEST_CLASS    = "test";
	public static final String TEST_FILENAME = "classes" + File.separator + "test.class";

	private MetricsFactory  factory;
	private ClassfileLoader loader;
	
	protected void setUp() throws Exception {
		factory = new MetricsFactory("test", new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).Load("etc" + File.separator + "MetricsConfig.xml"));

		loader = new AggregatingClassfileLoader();
		loader.load(Collections.singleton(TEST_FILENAME));

		loader.getClassfile(TEST_CLASS).accept(new MetricsGatherer("test", factory));
	}
	
	public void testNbElements() {
		assertEquals("factory.ProjectNames().size()", 1, factory.ProjectNames().size());
		assertTrue(factory.ProjectNames().toString() + " does not contain project \"test\"", factory.ProjectNames().contains("test"));

		assertTrue(factory.GroupNames().toString() + " does not contain package \"\"", factory.GroupNames().contains(""));
		assertEquals(factory.GroupNames().toString(), 1, factory.GroupNames().size());

		assertTrue(factory.ClassNames().toString() + " does not contain class \"test\"", factory.ClassNames().contains("test"));
		assertEquals(factory.ClassNames().toString(), 1, factory.ClassNames().size());

		assertTrue(factory.MethodNames().toString() + " does not contain method \"test.main(java.lang.String[])\"", factory.MethodNames().contains("test.main(java.lang.String[])"));
		assertTrue(factory.MethodNames().toString() + " does not contain method \"test.test()\"", factory.MethodNames().contains("test.test()"));
		assertEquals(factory.MethodNames().toString(), 2, factory.MethodNames().size());
	}
	
	public void testNbAllElements() throws IOException, SAXException {
		assertEquals("factory.AllProjectNames().size()", 1, factory.AllProjectNames().size());
		assertTrue(factory.AllProjectNames().toString() + " does not contain project \"test\"", factory.AllProjectNames().contains("test"));

		assertTrue(factory.AllGroupNames().toString() + " does not contain package \"\"", factory.AllGroupNames().contains(""));
		assertTrue(factory.AllGroupNames().toString() + " does not contain package \"java.lang\"", factory.AllGroupNames().contains("java.lang"));
		assertTrue(factory.AllGroupNames().toString() + " does not contain package \"java.util\"", factory.AllGroupNames().contains("java.util"));
		assertTrue(factory.AllGroupNames().toString() + " does not contain package \"java.io\"", factory.AllGroupNames().contains("java.util"));
		assertEquals(factory.AllGroupNames().toString(), 4, factory.AllGroupNames().size());

		assertTrue(factory.AllClassNames().toString() + " does not contain class \"java.io.PrintStream\"", factory.AllClassNames().contains("java.io.PrintStream"));
		assertTrue(factory.AllClassNames().toString() + " does not contain class \"java.lang.NullPointerException\"", factory.AllClassNames().contains("java.lang.NullPointerException"));
		assertTrue(factory.AllClassNames().toString() + " does not contain class \"java.lang.Object\"", factory.AllClassNames().contains("java.lang.Object"));
		assertTrue(factory.AllClassNames().toString() + " does not contain class \"java.lang.String\"", factory.AllClassNames().contains("java.lang.String"));
		assertTrue(factory.AllClassNames().toString() + " does not contain class \"java.lang.System\"", factory.AllClassNames().contains("java.lang.System"));
		assertTrue(factory.AllClassNames().toString() + " does not contain class \"java.util.Collections\"", factory.AllClassNames().contains("java.util.Collections"));
		assertTrue(factory.AllClassNames().toString() + " does not contain class \"java.util.Collection\"", factory.AllClassNames().contains("java.util.Collection"));
		assertTrue(factory.AllClassNames().toString() + " does not contain class \"java.util.Set\"", factory.AllClassNames().contains("java.util.Set"));
		assertTrue(factory.AllClassNames().toString() + " does not contain class \"test\"", factory.AllClassNames().contains("test"));
		assertEquals(factory.AllClassNames().toString(), 9, factory.AllClassNames().size());

		assertTrue(factory.AllMethodNames().toString() + " does not contain method \"java.io.PrintStream.println(java.lang.Object)\"", factory.AllMethodNames().contains("java.io.PrintStream.println(java.lang.Object)"));
		assertTrue(factory.AllMethodNames().toString() + " does not contain method \"java.lang.Object.Object()\"", factory.AllMethodNames().contains("java.lang.Object.Object()"));
		assertTrue(factory.AllMethodNames().toString() + " does not contain method \"java.util.Collections.singleton(java.lang.Object)\"", factory.AllMethodNames().contains("java.util.Collections.singleton(java.lang.Object)"));
		assertTrue(factory.AllMethodNames().toString() + " does not contain method \"test.main(java.lang.String[])\"", factory.AllMethodNames().contains("test.main(java.lang.String[])"));
		assertTrue(factory.AllMethodNames().toString() + " does not contain method \"test.test()\"", factory.AllMethodNames().contains("test.test()"));
		assertEquals(factory.AllMethodNames().toString(), 5, factory.AllMethodNames().size());
	}
	
	public void test_test_test() {
		assertEquals(Metrics.SLOC, 1, factory.CreateMethodMetrics("test.test()").Measurement(Metrics.SLOC).intValue());
		assertEquals(Metrics.PARAMETERS, 0, factory.CreateMethodMetrics("test.test()").Measurement(Metrics.PARAMETERS).intValue());
		assertEquals(Metrics.LOCAL_VARIABLES, 1, factory.CreateMethodMetrics("test.test()").Measurement(Metrics.LOCAL_VARIABLES).intValue());

		//
		// Dependencies
		//
		
		Collection dependencies;

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("test.test()").Measurement(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("test.test()").Measurement(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("test.test()").Measurement(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("test.test()").Measurement(Metrics.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("test.test()").Measurement(Metrics.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("test.test()").Measurement(Metrics.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("test.test()").Measurement(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES)).Values();
		assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies + "missing java.lang.Object.Object()", dependencies.contains("java.lang.Object.Object()"));
		assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 1, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("test.test()").Measurement(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
	}
	
	public void test_test_main() {
		assertEquals(Metrics.SLOC, 5, factory.CreateMethodMetrics("test.main(java.lang.String[])").Measurement(Metrics.SLOC).intValue());
		assertEquals(Metrics.PARAMETERS, 1, factory.CreateMethodMetrics("test.main(java.lang.String[])").Measurement(Metrics.PARAMETERS).intValue());
		assertEquals(Metrics.LOCAL_VARIABLES, 3, factory.CreateMethodMetrics("test.main(java.lang.String[])").Measurement(Metrics.LOCAL_VARIABLES).intValue());

		//
		// Dependencies
		//
		
		Collection dependencies;

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("test.main(java.lang.String[])").Measurement(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("test.main(java.lang.String[])").Measurement(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("test.main(java.lang.String[])").Measurement(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("test.main(java.lang.String[])").Measurement(Metrics.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("test.main(java.lang.String[])").Measurement(Metrics.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("test.main(java.lang.String[])").Measurement(Metrics.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("test.main(java.lang.String[])").Measurement(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES)).Values();
		assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies + "missing java.util.Collections.singleton(java.lang.Object)", dependencies.contains("java.util.Collections.singleton(java.lang.Object)"));
		assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies + "missing java.lang.Object.Object()", dependencies.contains("java.lang.Object.Object()"));
		assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies + "missing java.io.PrintStream.println(java.lang.Object)", dependencies.contains("java.io.PrintStream.println(java.lang.Object)"));
		assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 3, dependencies.size());

		dependencies = ((CollectionMeasurement) factory.CreateMethodMetrics("test.main(java.lang.String[])").Measurement(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES)).Values();
		assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies + "missing java.io.PrintStream", dependencies.contains("java.io.PrintStream"));
		assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies + "missing java.lang.NullPointerException", dependencies.contains("java.lang.NullPointerException"));
		assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies + "missing java.lang.Object", dependencies.contains("java.lang.Object"));
		assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies + "missing java.lang.String", dependencies.contains("java.lang.String"));
		assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies + "missing java.lang.System", dependencies.contains("java.lang.System"));
		assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies + "missing java.util.Collection", dependencies.contains("java.util.Collection"));
		assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies + "missing java.util.Set", dependencies.contains("java.util.Set"));
		assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 7, dependencies.size());
	}
	
	public void test_test() {
		assertEquals(Metrics.SLOC, 7, factory.CreateClassMetrics("test").Measurement(Metrics.SLOC).intValue());
		assertEquals("M", 2, factory.CreateClassMetrics("test").Measurement("M").intValue());
		assertEquals(Metrics.PUBLIC_METHODS, 2, factory.CreateClassMetrics("test").Measurement(Metrics.PUBLIC_METHODS).intValue());
		assertEquals(Metrics.PROTECTED_METHODS, 0, factory.CreateClassMetrics("test").Measurement(Metrics.PROTECTED_METHODS).intValue());
		assertEquals(Metrics.PRIVATE_METHODS, 0, factory.CreateClassMetrics("test").Measurement(Metrics.PRIVATE_METHODS).intValue());
		assertEquals(Metrics.PACKAGE_METHODS, 0, factory.CreateClassMetrics("test").Measurement(Metrics.PACKAGE_METHODS).intValue());
		assertEquals(Metrics.FINAL_METHODS, 0, factory.CreateClassMetrics("test").Measurement(Metrics.FINAL_METHODS).intValue());
		assertEquals(Metrics.ABSTRACT_METHODS, 0, factory.CreateClassMetrics("test").Measurement(Metrics.ABSTRACT_METHODS).intValue());
		assertEquals(Metrics.DEPRECATED_METHODS, 0, factory.CreateClassMetrics("test").Measurement(Metrics.DEPRECATED_METHODS).intValue());
		assertEquals(Metrics.SYNTHETIC_METHODS, 0, factory.CreateClassMetrics("test").Measurement(Metrics.SYNTHETIC_METHODS).intValue());
		assertEquals(Metrics.STATIC_METHODS, 1, factory.CreateClassMetrics("test").Measurement(Metrics.STATIC_METHODS).intValue());
		assertEquals(Metrics.SYNCHRONIZED_METHODS, 0, factory.CreateClassMetrics("test").Measurement(Metrics.SYNCHRONIZED_METHODS).intValue());
		assertEquals(Metrics.NATIVE_METHODS, 0, factory.CreateClassMetrics("test").Measurement(Metrics.NATIVE_METHODS).intValue());
		assertEquals(Metrics.TRIVIAL_METHODS, 0, factory.CreateClassMetrics("test").Measurement(Metrics.TRIVIAL_METHODS).intValue());
		assertEquals("PuMR", 1, factory.CreateClassMetrics("test").Measurement("PuMR").doubleValue(), 0.01);
		assertEquals("ProMR", 0, factory.CreateClassMetrics("test").Measurement("ProMR").doubleValue(), 0.01);
		assertEquals("PriMR", 0, factory.CreateClassMetrics("test").Measurement("PriMR").doubleValue(), 0.01);
		assertEquals("PaMR", 0, factory.CreateClassMetrics("test").Measurement("PaMR").doubleValue(), 0.01);
		assertEquals("FMR", 0, factory.CreateClassMetrics("test").Measurement("FMR").doubleValue(), 0.01);
		assertEquals("AMR", 0, factory.CreateClassMetrics("test").Measurement("AMR").doubleValue(), 0.01);
		assertEquals("DMR", 0, factory.CreateClassMetrics("test").Measurement("DMR").doubleValue(), 0.01);
		assertEquals("SynthMR", 0, factory.CreateClassMetrics("test").Measurement("SynthMR").doubleValue(), 0.01);
		assertEquals("SMR", 0.5, factory.CreateClassMetrics("test").Measurement("SMR").doubleValue(), 0.01);
		assertEquals("SynchMR", 0, factory.CreateClassMetrics("test").Measurement("SynchMR").doubleValue(), 0.01);
		assertEquals("NMR", 0, factory.CreateClassMetrics("test").Measurement("NMR").doubleValue(), 0.01);
		assertEquals("TMR", 0, factory.CreateClassMetrics("test").Measurement("TMR").doubleValue(), 0.01);
		assertEquals("A", 0, factory.CreateClassMetrics("test").Measurement("A").intValue());
		assertEquals(Metrics.PUBLIC_ATTRIBUTES, 0, factory.CreateClassMetrics("test").Measurement(Metrics.PUBLIC_ATTRIBUTES).intValue());
		assertEquals(Metrics.PROTECTED_ATTRIBUTES, 0, factory.CreateClassMetrics("test").Measurement(Metrics.PROTECTED_ATTRIBUTES).intValue());
		assertEquals(Metrics.PRIVATE_ATTRIBUTES, 0, factory.CreateClassMetrics("test").Measurement(Metrics.PRIVATE_ATTRIBUTES).intValue());
		assertEquals(Metrics.PACKAGE_ATTRIBUTES, 0, factory.CreateClassMetrics("test").Measurement(Metrics.PACKAGE_ATTRIBUTES).intValue());
		assertEquals(Metrics.FINAL_ATTRIBUTES, 0, factory.CreateClassMetrics("test").Measurement(Metrics.FINAL_ATTRIBUTES).intValue());
		assertEquals(Metrics.DEPRECATED_ATTRIBUTES, 0, factory.CreateClassMetrics("test").Measurement(Metrics.DEPRECATED_ATTRIBUTES).intValue());
		assertEquals(Metrics.SYNTHETIC_ATTRIBUTES, 0, factory.CreateClassMetrics("test").Measurement(Metrics.SYNTHETIC_ATTRIBUTES).intValue());
		assertEquals(Metrics.STATIC_ATTRIBUTES, 0, factory.CreateClassMetrics("test").Measurement(Metrics.STATIC_ATTRIBUTES).intValue());
		assertEquals(Metrics.TRANSIENT_ATTRIBUTES, 0, factory.CreateClassMetrics("test").Measurement(Metrics.TRANSIENT_ATTRIBUTES).intValue());
		assertEquals(Metrics.VOLATILE_ATTRIBUTES, 0, factory.CreateClassMetrics("test").Measurement(Metrics.VOLATILE_ATTRIBUTES).intValue());
		assertTrue("PuAR", Double.isNaN(factory.CreateClassMetrics("test").Measurement("PuAR").doubleValue()));
		assertTrue("ProAR", Double.isNaN(factory.CreateClassMetrics("test").Measurement("ProAR").doubleValue()));
		assertTrue("PriAR", Double.isNaN(factory.CreateClassMetrics("test").Measurement("PriAR").doubleValue()));
		assertTrue("PaAR", Double.isNaN(factory.CreateClassMetrics("test").Measurement("PaAR").doubleValue()));
		assertTrue("FAR", Double.isNaN(factory.CreateClassMetrics("test").Measurement("FAR").doubleValue()));
		assertTrue("DAR", Double.isNaN(factory.CreateClassMetrics("test").Measurement("DAR").doubleValue()));
		assertTrue("SynthAR", Double.isNaN(factory.CreateClassMetrics("test").Measurement("SynthAR").doubleValue()));
		assertTrue("SAR", Double.isNaN(factory.CreateClassMetrics("test").Measurement("SAR").doubleValue()));
		assertTrue("TAR", Double.isNaN(factory.CreateClassMetrics("test").Measurement("TAR").doubleValue()));
		assertTrue("VAR", Double.isNaN(factory.CreateClassMetrics("test").Measurement("VAR").doubleValue()));
		assertEquals(Metrics.SUBCLASSES, 0, factory.CreateClassMetrics("test").Measurement(Metrics.SUBCLASSES).intValue());
		assertEquals(Metrics.DEPTH_OF_INHERITANCE, 1, factory.CreateClassMetrics("test").Measurement(Metrics.DEPTH_OF_INHERITANCE).intValue());

		//
		// Dependencies
		//

		Collection dependencies;

		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("test").Measurement(Metrics.INBOUND_INTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
		
		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("test").Measurement(Metrics.INBOUND_EXTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
		
		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("test").Measurement(Metrics.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
		
		dependencies = ((CollectionMeasurement) factory.CreateClassMetrics("test").Measurement(Metrics.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES)).Values();
		assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES, 1, dependencies.size());
		assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies + "missing java.lang.Object", dependencies.contains("java.lang.Object"));
	}

	public void test_() {
		assertEquals(Metrics.SLOC, 7, factory.CreateGroupMetrics("").Measurement(Metrics.SLOC).intValue());
	}

	public void testProject() {
		assertEquals(Metrics.SLOC, 7, factory.CreateProjectMetrics("test").Measurement(Metrics.SLOC).intValue());
	}
}
