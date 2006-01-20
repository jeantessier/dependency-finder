/*
 *  Copyright (c) 2001-2006, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
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
        factory = new MetricsFactory("test", new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).load("etc" + File.separator + "MetricsConfig.xml"));

        loader = new AggregatingClassfileLoader();
        loader.load(Collections.singleton(TEST_FILENAME));

        loader.getClassfile(TEST_CLASS).accept(new MetricsGatherer("test", factory));
    }
    
    public void testNbElements() {
        assertEquals("factory.ProjectNames().size()", 1, factory.getProjectNames().size());
        assertTrue(factory.getProjectNames().toString() + " does not contain project \"test\"", factory.getProjectNames().contains("test"));

        assertTrue(factory.getGroupNames().toString() + " does not contain package \"\"", factory.getGroupNames().contains(""));
        assertEquals(factory.getGroupNames().toString(), 1, factory.getGroupNames().size());

        assertTrue(factory.getClassNames().toString() + " does not contain class \"test\"", factory.getClassNames().contains("test"));
        assertEquals(factory.getClassNames().toString(), 1, factory.getClassNames().size());

        assertTrue(factory.getMethodNames().toString() + " does not contain method \"test.main(java.lang.String[])\"", factory.getMethodNames().contains("test.main(java.lang.String[])"));
        assertTrue(factory.getMethodNames().toString() + " does not contain method \"test.test()\"", factory.getMethodNames().contains("test.test()"));
        assertEquals(factory.getMethodNames().toString(), 2, factory.getMethodNames().size());
    }
    
    public void testNbAllElements() {
        assertEquals("factory.AllProjectNames().size()", 1, factory.getAllProjectNames().size());
        assertTrue(factory.getAllProjectNames().toString() + " does not contain project \"test\"", factory.getAllProjectNames().contains("test"));

        assertTrue(factory.getAllGroupNames().toString() + " does not contain package \"\"", factory.getAllGroupNames().contains(""));
        assertTrue(factory.getAllGroupNames().toString() + " does not contain package \"java.lang\"", factory.getAllGroupNames().contains("java.lang"));
        assertTrue(factory.getAllGroupNames().toString() + " does not contain package \"java.util\"", factory.getAllGroupNames().contains("java.util"));
        assertTrue(factory.getAllGroupNames().toString() + " does not contain package \"java.io\"", factory.getAllGroupNames().contains("java.util"));
        assertEquals(factory.getAllGroupNames().toString(), 4, factory.getAllGroupNames().size());

        assertTrue(factory.getAllClassNames().toString() + " does not contain class \"java.io.PrintStream\"", factory.getAllClassNames().contains("java.io.PrintStream"));
        assertTrue(factory.getAllClassNames().toString() + " does not contain class \"java.lang.NullPointerException\"", factory.getAllClassNames().contains("java.lang.NullPointerException"));
        assertTrue(factory.getAllClassNames().toString() + " does not contain class \"java.lang.Object\"", factory.getAllClassNames().contains("java.lang.Object"));
        assertTrue(factory.getAllClassNames().toString() + " does not contain class \"java.lang.String\"", factory.getAllClassNames().contains("java.lang.String"));
        assertTrue(factory.getAllClassNames().toString() + " does not contain class \"java.lang.System\"", factory.getAllClassNames().contains("java.lang.System"));
        assertTrue(factory.getAllClassNames().toString() + " does not contain class \"java.util.Collections\"", factory.getAllClassNames().contains("java.util.Collections"));
        assertTrue(factory.getAllClassNames().toString() + " does not contain class \"java.util.Collection\"", factory.getAllClassNames().contains("java.util.Collection"));
        assertTrue(factory.getAllClassNames().toString() + " does not contain class \"java.util.Set\"", factory.getAllClassNames().contains("java.util.Set"));
        assertTrue(factory.getAllClassNames().toString() + " does not contain class \"test\"", factory.getAllClassNames().contains("test"));
        assertEquals(factory.getAllClassNames().toString(), 9, factory.getAllClassNames().size());

        assertTrue(factory.getAllMethodNames().toString() + " does not contain method \"java.io.PrintStream.println(java.lang.Object)\"", factory.getAllMethodNames().contains("java.io.PrintStream.println(java.lang.Object)"));
        assertTrue(factory.getAllMethodNames().toString() + " does not contain method \"java.lang.Object.Object()\"", factory.getAllMethodNames().contains("java.lang.Object.Object()"));
        assertTrue(factory.getAllMethodNames().toString() + " does not contain method \"java.util.Collections.singleton(java.lang.Object)\"", factory.getAllMethodNames().contains("java.util.Collections.singleton(java.lang.Object)"));
        assertTrue(factory.getAllMethodNames().toString() + " does not contain method \"test.main(java.lang.String[])\"", factory.getAllMethodNames().contains("test.main(java.lang.String[])"));
        assertTrue(factory.getAllMethodNames().toString() + " does not contain method \"test.test()\"", factory.getAllMethodNames().contains("test.test()"));
        assertEquals(factory.getAllMethodNames().toString(), 5, factory.getAllMethodNames().size());
    }
    
    public void test_test_test() {
        assertEquals(Metrics.SLOC, 1, factory.createMethodMetrics("test.test()").getMeasurement(Metrics.SLOC).intValue());
        assertEquals(Metrics.PARAMETERS, 0, factory.createMethodMetrics("test.test()").getMeasurement(Metrics.PARAMETERS).intValue());
        assertEquals(Metrics.LOCAL_VARIABLES, 1, factory.createMethodMetrics("test.test()").getMeasurement(Metrics.LOCAL_VARIABLES).intValue());

        //
        // Dependencies
        //
        
        Collection dependencies;

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("test.test()").getMeasurement(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES)).getValues();
        assertEquals(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("test.test()").getMeasurement(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("test.test()").getMeasurement(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("test.test()").getMeasurement(Metrics.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(Metrics.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("test.test()").getMeasurement(Metrics.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("test.test()").getMeasurement(Metrics.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES)).getValues();
        assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("test.test()").getMeasurement(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES)).getValues();
        assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies + "missing java.lang.Object.Object()", dependencies.contains("java.lang.Object.Object()"));
        assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 1, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("test.test()").getMeasurement(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES)).getValues();
        assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
    }
    
    public void test_test_main() {
        assertEquals(Metrics.SLOC, 5, factory.createMethodMetrics("test.main(java.lang.String[])").getMeasurement(Metrics.SLOC).intValue());
        assertEquals(Metrics.PARAMETERS, 1, factory.createMethodMetrics("test.main(java.lang.String[])").getMeasurement(Metrics.PARAMETERS).intValue());
        assertEquals(Metrics.LOCAL_VARIABLES, 3, factory.createMethodMetrics("test.main(java.lang.String[])").getMeasurement(Metrics.LOCAL_VARIABLES).intValue());

        //
        // Dependencies
        //
        
        Collection dependencies;

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("test.main(java.lang.String[])").getMeasurement(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES)).getValues();
        assertEquals(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("test.main(java.lang.String[])").getMeasurement(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("test.main(java.lang.String[])").getMeasurement(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("test.main(java.lang.String[])").getMeasurement(Metrics.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(Metrics.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("test.main(java.lang.String[])").getMeasurement(Metrics.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("test.main(java.lang.String[])").getMeasurement(Metrics.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES)).getValues();
        assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("test.main(java.lang.String[])").getMeasurement(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES)).getValues();
        assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies + "missing java.util.Collections.singleton(java.lang.Object)", dependencies.contains("java.util.Collections.singleton(java.lang.Object)"));
        assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies + "missing java.lang.Object.Object()", dependencies.contains("java.lang.Object.Object()"));
        assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies + "missing java.io.PrintStream.println(java.lang.Object)", dependencies.contains("java.io.PrintStream.println(java.lang.Object)"));
        assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 3, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("test.main(java.lang.String[])").getMeasurement(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES)).getValues();
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
        assertEquals(Metrics.SLOC, 7, factory.createClassMetrics("test").getMeasurement(Metrics.SLOC).intValue());
        assertEquals("M", 2, factory.createClassMetrics("test").getMeasurement("M").intValue());
        assertEquals(Metrics.PUBLIC_METHODS, 2, factory.createClassMetrics("test").getMeasurement(Metrics.PUBLIC_METHODS).intValue());
        assertEquals(Metrics.PROTECTED_METHODS, 0, factory.createClassMetrics("test").getMeasurement(Metrics.PROTECTED_METHODS).intValue());
        assertEquals(Metrics.PRIVATE_METHODS, 0, factory.createClassMetrics("test").getMeasurement(Metrics.PRIVATE_METHODS).intValue());
        assertEquals(Metrics.PACKAGE_METHODS, 0, factory.createClassMetrics("test").getMeasurement(Metrics.PACKAGE_METHODS).intValue());
        assertEquals(Metrics.FINAL_METHODS, 0, factory.createClassMetrics("test").getMeasurement(Metrics.FINAL_METHODS).intValue());
        assertEquals(Metrics.ABSTRACT_METHODS, 0, factory.createClassMetrics("test").getMeasurement(Metrics.ABSTRACT_METHODS).intValue());
        assertEquals(Metrics.DEPRECATED_METHODS, 0, factory.createClassMetrics("test").getMeasurement(Metrics.DEPRECATED_METHODS).intValue());
        assertEquals(Metrics.SYNTHETIC_METHODS, 0, factory.createClassMetrics("test").getMeasurement(Metrics.SYNTHETIC_METHODS).intValue());
        assertEquals(Metrics.STATIC_METHODS, 1, factory.createClassMetrics("test").getMeasurement(Metrics.STATIC_METHODS).intValue());
        assertEquals(Metrics.SYNCHRONIZED_METHODS, 0, factory.createClassMetrics("test").getMeasurement(Metrics.SYNCHRONIZED_METHODS).intValue());
        assertEquals(Metrics.NATIVE_METHODS, 0, factory.createClassMetrics("test").getMeasurement(Metrics.NATIVE_METHODS).intValue());
        assertEquals(Metrics.TRIVIAL_METHODS, 0, factory.createClassMetrics("test").getMeasurement(Metrics.TRIVIAL_METHODS).intValue());
        assertEquals("PuMR", 1, factory.createClassMetrics("test").getMeasurement("PuMR").doubleValue(), 0.01);
        assertEquals("ProMR", 0, factory.createClassMetrics("test").getMeasurement("ProMR").doubleValue(), 0.01);
        assertEquals("PriMR", 0, factory.createClassMetrics("test").getMeasurement("PriMR").doubleValue(), 0.01);
        assertEquals("PaMR", 0, factory.createClassMetrics("test").getMeasurement("PaMR").doubleValue(), 0.01);
        assertEquals("FMR", 0, factory.createClassMetrics("test").getMeasurement("FMR").doubleValue(), 0.01);
        assertEquals("AMR", 0, factory.createClassMetrics("test").getMeasurement("AMR").doubleValue(), 0.01);
        assertEquals("DMR", 0, factory.createClassMetrics("test").getMeasurement("DMR").doubleValue(), 0.01);
        assertEquals("SynthMR", 0, factory.createClassMetrics("test").getMeasurement("SynthMR").doubleValue(), 0.01);
        assertEquals("SMR", 0.5, factory.createClassMetrics("test").getMeasurement("SMR").doubleValue(), 0.01);
        assertEquals("SynchMR", 0, factory.createClassMetrics("test").getMeasurement("SynchMR").doubleValue(), 0.01);
        assertEquals("NMR", 0, factory.createClassMetrics("test").getMeasurement("NMR").doubleValue(), 0.01);
        assertEquals("TMR", 0, factory.createClassMetrics("test").getMeasurement("TMR").doubleValue(), 0.01);
        assertEquals("A", 0, factory.createClassMetrics("test").getMeasurement("A").intValue());
        assertEquals(Metrics.PUBLIC_ATTRIBUTES, 0, factory.createClassMetrics("test").getMeasurement(Metrics.PUBLIC_ATTRIBUTES).intValue());
        assertEquals(Metrics.PROTECTED_ATTRIBUTES, 0, factory.createClassMetrics("test").getMeasurement(Metrics.PROTECTED_ATTRIBUTES).intValue());
        assertEquals(Metrics.PRIVATE_ATTRIBUTES, 0, factory.createClassMetrics("test").getMeasurement(Metrics.PRIVATE_ATTRIBUTES).intValue());
        assertEquals(Metrics.PACKAGE_ATTRIBUTES, 0, factory.createClassMetrics("test").getMeasurement(Metrics.PACKAGE_ATTRIBUTES).intValue());
        assertEquals(Metrics.FINAL_ATTRIBUTES, 0, factory.createClassMetrics("test").getMeasurement(Metrics.FINAL_ATTRIBUTES).intValue());
        assertEquals(Metrics.DEPRECATED_ATTRIBUTES, 0, factory.createClassMetrics("test").getMeasurement(Metrics.DEPRECATED_ATTRIBUTES).intValue());
        assertEquals(Metrics.SYNTHETIC_ATTRIBUTES, 0, factory.createClassMetrics("test").getMeasurement(Metrics.SYNTHETIC_ATTRIBUTES).intValue());
        assertEquals(Metrics.STATIC_ATTRIBUTES, 0, factory.createClassMetrics("test").getMeasurement(Metrics.STATIC_ATTRIBUTES).intValue());
        assertEquals(Metrics.TRANSIENT_ATTRIBUTES, 0, factory.createClassMetrics("test").getMeasurement(Metrics.TRANSIENT_ATTRIBUTES).intValue());
        assertEquals(Metrics.VOLATILE_ATTRIBUTES, 0, factory.createClassMetrics("test").getMeasurement(Metrics.VOLATILE_ATTRIBUTES).intValue());
        assertTrue("PuAR", Double.isNaN(factory.createClassMetrics("test").getMeasurement("PuAR").doubleValue()));
        assertTrue("ProAR", Double.isNaN(factory.createClassMetrics("test").getMeasurement("ProAR").doubleValue()));
        assertTrue("PriAR", Double.isNaN(factory.createClassMetrics("test").getMeasurement("PriAR").doubleValue()));
        assertTrue("PaAR", Double.isNaN(factory.createClassMetrics("test").getMeasurement("PaAR").doubleValue()));
        assertTrue("FAR", Double.isNaN(factory.createClassMetrics("test").getMeasurement("FAR").doubleValue()));
        assertTrue("DAR", Double.isNaN(factory.createClassMetrics("test").getMeasurement("DAR").doubleValue()));
        assertTrue("SynthAR", Double.isNaN(factory.createClassMetrics("test").getMeasurement("SynthAR").doubleValue()));
        assertTrue("SAR", Double.isNaN(factory.createClassMetrics("test").getMeasurement("SAR").doubleValue()));
        assertTrue("TAR", Double.isNaN(factory.createClassMetrics("test").getMeasurement("TAR").doubleValue()));
        assertTrue("VAR", Double.isNaN(factory.createClassMetrics("test").getMeasurement("VAR").doubleValue()));
        assertEquals(Metrics.SUBCLASSES, 0, factory.createClassMetrics("test").getMeasurement(Metrics.SUBCLASSES).intValue());
        assertEquals(Metrics.DEPTH_OF_INHERITANCE, 1, factory.createClassMetrics("test").getMeasurement(Metrics.DEPTH_OF_INHERITANCE).intValue());

        //
        // Dependencies
        //

        Collection dependencies;

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("test").getMeasurement(Metrics.INBOUND_INTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(Metrics.INBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("test").getMeasurement(Metrics.INBOUND_EXTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(Metrics.INBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("test").getMeasurement(Metrics.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(Metrics.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("test").getMeasurement(Metrics.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(Metrics.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES, 1, dependencies.size());
        assertTrue(Metrics.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies + "missing java.lang.Object", dependencies.contains("java.lang.Object"));
    }

    public void test_() {
        assertEquals(Metrics.SLOC, 7, factory.createGroupMetrics("").getMeasurement(Metrics.SLOC).intValue());
    }

    public void testProject() {
        assertEquals(Metrics.SLOC, 7, factory.createProjectMetrics("test").getMeasurement(Metrics.SLOC).intValue());
    }
}
