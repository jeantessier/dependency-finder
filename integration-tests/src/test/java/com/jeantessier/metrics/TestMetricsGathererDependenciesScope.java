/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

import java.nio.file.*;
import java.util.*;

import org.junit.jupiter.api.*;

import com.jeantessier.classreader.AggregatingClassfileLoader;
import com.jeantessier.classreader.ClassfileLoader;

import static org.junit.jupiter.api.Assertions.*;

public class TestMetricsGathererDependenciesScope {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    public static final String TEST_DIRNAME = CLASSES_DIR.resolve("testpackage").toString();
    public static final String OTHER_DIRNAME = CLASSES_DIR.resolve("otherpackage").toString();

    private MetricsFactory factory;
    private ClassfileLoader loader;
    private MetricsGatherer gatherer;
    
    @BeforeEach
    void setUp() throws Exception {
        factory = new MetricsFactory("test", new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).load(Paths.get("../etc/MetricsConfig.xml").toString()));

        Collection<String> dirs = new ArrayList<>();
        dirs.add(TEST_DIRNAME);
        dirs.add(OTHER_DIRNAME);
        loader = new AggregatingClassfileLoader();
        loader.load(dirs);

        gatherer = new MetricsGatherer(factory);
    }

    @Test
    void testpackage_TestClass_testMethod_IntraClass() {
        Collection<String> scopeIncludes = new HashSet<>();
        scopeIncludes.add("testpackage.TestClass.sourceMethod(): testpackage.TestClass");

        gatherer.setScopeIncludes(scopeIncludes);
        gatherer.visitClassfiles(loader.getAllClassfiles());

        Collection<String> dependencies;

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES)).getValues();
        assertTrue(dependencies.contains("testpackage.TestClass.sourceMethod(): testpackage.TestClass"), BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES + " " + dependencies + " missing testpackage.TestClass.sourceMethod()");
        assertEquals(1, dependencies.size(), BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies);
    }
    
    @Test
    void testpackage_TestClass_testMethod_IntraPackage() {
        Collection<String> scopeIncludes = new HashSet<>();
        scopeIncludes.add("testpackage.SourceClass.sourceMethod(): testpackage.TestClass");

        gatherer.setScopeIncludes(scopeIncludes);
        gatherer.visitClassfiles(loader.getAllClassfiles());

        Collection<String> dependencies;

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertTrue(dependencies.contains("testpackage.SourceClass.sourceMethod(): testpackage.TestClass"), BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies + " missing testpackage.SourceClass.sourceMethod()");
        assertEquals(1, dependencies.size(), BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies);
    }
    
    @Test
    void testpackage_TestClass_testMethod_ExtraPackage() {
        Collection<String> scopeIncludes = new HashSet<>();
        scopeIncludes.add("otherpackage.SourceClass.sourceMethod(): testpackage.TestClass");

        gatherer.setScopeIncludes(scopeIncludes);
        gatherer.visitClassfiles(loader.getAllClassfiles());

        Collection<String> dependencies;

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertTrue(dependencies.contains("otherpackage.SourceClass.sourceMethod(): testpackage.TestClass"), BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies + " missing otherpackage.SourceClass.sourceMethod()");
        assertEquals(1, dependencies.size(), BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createMethodMetrics("testpackage.TestClass.testMethod(java.lang.String): void").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES + " " + dependencies);
    }
    
    @Test
    void testpackage_TestClass_IntraPackageClass() {
        Collection<String> scopeIncludes = new HashSet<>();
        scopeIncludes.add("testpackage.SourceClass");

        gatherer.setScopeIncludes(scopeIncludes);
        gatherer.visitClassfiles(loader.getAllClassfiles());

        Collection<String> dependencies;

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertTrue(dependencies.contains("testpackage.SourceClass"), BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies + " missing testpackage.SourceClass");
        assertEquals(1, dependencies.size(), BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies);
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies);
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies);
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies);
    }
    
    @Test
    void testpackage_TestClass_ExtraPackageClass() {
        Collection<String> scopeIncludes = new HashSet<>();
        scopeIncludes.add("otherpackage.SourceClass");

        gatherer.setScopeIncludes(scopeIncludes);
        gatherer.visitClassfiles(loader.getAllClassfiles());

        Collection<String> dependencies;

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies);
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertTrue(dependencies.contains("otherpackage.SourceClass"), BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies + " missing otherpackage.SourceClass");
        assertEquals(1, dependencies.size(), BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies);
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies);
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies);
    }
    
    @Test
    void testpackage_TestClass_IntraPackageMethod() {
        Collection<String> scopeIncludes = new HashSet<>();
        scopeIncludes.add("testpackage.SourceClass.sourceMethod(): testpackage.TestClass");

        gatherer.setScopeIncludes(scopeIncludes);
        gatherer.visitClassfiles(loader.getAllClassfiles());

        Collection<String> dependencies;

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies);
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies);
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies);
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertTrue(dependencies.contains("testpackage.SourceClass.sourceMethod(): testpackage.TestClass"), BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies + " missing testpackage.SourceClass.sourceMethod()");
        assertEquals(1, dependencies.size(), BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies);
    }

    @Test
    void testpackage_TestClass_ExtraPackageMethod() {
        Collection<String> scopeIncludes = new HashSet<>();
        scopeIncludes.add("otherpackage.SourceClass.sourceMethod(): testpackage.TestClass");

        gatherer.setScopeIncludes(scopeIncludes);
        gatherer.visitClassfiles(loader.getAllClassfiles());

        Collection<String> dependencies;

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies);
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies);
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES + " " + dependencies);
        
        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertEquals(0, dependencies.size(), BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies);

        dependencies = ((CollectionMeasurement) factory.createClassMetrics("testpackage.TestClass").getMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES)).getValues();
        assertTrue(dependencies.contains("otherpackage.SourceClass.sourceMethod(): testpackage.TestClass"), BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies + " missing otherpackage.SourceClass.sourceMethod()");
        assertEquals(1, dependencies.size(), BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES + " " + dependencies);
    }
}
