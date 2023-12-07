/*
 *  Copyright (c) 2001-2023, Jean Tessier
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

import com.jeantessier.classreader.AggregatingClassfileLoader;
import com.jeantessier.classreader.ClassfileLoader;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

import java.nio.file.*;
import java.util.*;

public class TestMetricsGathererDependenciesFilter extends TestCase {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    public static final String TEST_DIRNAME  = CLASSES_DIR.resolve("testpackage").toString();
    public static final String OTHER_DIRNAME = CLASSES_DIR.resolve("otherpackage").toString();

    private MetricsFactory  factory;
    private ClassfileLoader loader;
    private MetricsGatherer gatherer;
    
    protected void setUp() throws Exception {
        super.setUp();

        Logger.getLogger(getClass()).debug("Starting " + getName() + " ...");
        
        factory = new MetricsFactory("test", new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).load(Paths.get("../etc/MetricsConfig.xml").toString()));

        Collection<String> dirs = new ArrayList<>();
        dirs.add(TEST_DIRNAME);
        dirs.add(OTHER_DIRNAME);
        loader = new AggregatingClassfileLoader();
        loader.load(dirs);

        gatherer = new MetricsGatherer(factory);
    }

    protected void tearDown() throws Exception {
        try {
            Logger.getLogger(getClass()).debug("Done with " + getName() + " ...");
        } finally {
            super.tearDown();
        }
    }
    
    public void testpackage_TestClass_testMethod_withFilterForMethod() {
        Collection<String> filterIncludes = new HashSet<>();
        filterIncludes.add("testpackage.TestClass.targetMethod()");

        gatherer.setFilterIncludes(filterIncludes);
        gatherer.visitClassfiles(loader.getAllClassfiles());

        Collection<String> dependencies;

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES)).getValues();
        assertTrue(BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES + " " + dependencies + " missing testpackage.TestClass.targetMethod()", dependencies.contains("testpackage.TestClass.targetMethod()"));
        assertEquals(BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES + " " + dependencies, 1, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
    }
    
    public void testpackage_TestClass_testMethod_withFilterForClass() {
        Collection<String> filterIncludes = new HashSet<>();
        filterIncludes.add("testpackage.TargetClass");

        gatherer.setFilterIncludes(filterIncludes);
        gatherer.visitClassfiles(loader.getAllClassfiles());

        Collection<String> dependencies;

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES)).getValues();
        assertTrue(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies + " missing testpackage.TargetClass", dependencies.contains("testpackage.TargetClass"));
        assertEquals(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 1, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
    }
    
    public void testpackage_TestClass_testMethod_withFilterForPackage() {
        Collection<String> filterIncludes = new HashSet<>();
        filterIncludes.add("java.lang");

        gatherer.setFilterIncludes(filterIncludes);
        gatherer.visitClassfiles(loader.getAllClassfiles());

        Collection<String> dependencies;

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String)").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
    }
    
    public void testpackage_TestClass_withFilterForClass() {
        Collection<String> filterIncludes = new HashSet<>();
        filterIncludes.add("testpackage.TargetInterface");

        gatherer.setFilterIncludes(filterIncludes);
        gatherer.visitClassfiles(loader.getAllClassfiles());

        Collection<String> dependencies;

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertTrue(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies + " missing testpackage.TargetInterface", dependencies.contains("testpackage.TargetInterface"));
        assertEquals(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 1, dependencies.size());
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
    }
    
    public void testpackage_TestClass_withFilterForPackage() {
        Collection<String> filterIncludes = new HashSet<>();
        filterIncludes.add("java.lang");

        gatherer.setFilterIncludes(filterIncludes);
        gatherer.visitClassfiles(loader.getAllClassfiles());

        Collection<String> dependencies;

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies, 0, dependencies.size());
    }
}