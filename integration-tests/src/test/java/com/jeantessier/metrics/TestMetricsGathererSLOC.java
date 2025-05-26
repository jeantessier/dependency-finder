/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import com.jeantessier.classreader.*;

public class TestMetricsGathererSLOC {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    public static final String TEST_DIRNAME = CLASSES_DIR.resolve("sloc").toString();

    private MetricsFactory factory;
    
    @BeforeEach
    void setUp() throws Exception {
        factory = new MetricsFactory("test", new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).load(Paths.get("../etc/MetricsConfig.xml").toString()));

        ClassfileLoader loader = new AggregatingClassfileLoader();
        loader.load(Collections.singleton(TEST_DIRNAME));

        MetricsGatherer gatherer = new MetricsGatherer(factory);

        for (Classfile classfile : loader.getAllClassfiles()) {
            classfile.accept(gatherer);
        }
    }

    @Test
    void test_sloc_TestInterface() {
        assertEquals(1, factory.createClassMetrics("sloc.TestInterface"), BasicMeasurements.CLASS_SLOC);
        assertEquals(3, factory.createClassMetrics("sloc.TestInterface"), BasicMeasurements.SLOC);
        assertEquals(2, factory.createClassMetrics("sloc.TestInterface"), BasicMeasurements.METHODS);
        assertEquals(2, factory.createClassMetrics("sloc.TestInterface"), BasicMeasurements.ABSTRACT_METHODS);
        assertEquals(0, factory.createClassMetrics("sloc.TestInterface"), BasicMeasurements.SYNTHETIC_METHODS);
    }
    
    @Test
    void test_sloc_TestInterface_method1() {
        assertEquals(1, factory.createMethodMetrics("sloc.TestInterface.method1(): void"), BasicMeasurements.SLOC);
    }
    
    @Test
    void test_sloc_TestInterface_method2() {
        assertEquals(1, factory.createMethodMetrics("sloc.TestInterface.method2(): void"), BasicMeasurements.SLOC);
    }
    
    @Test
    void test_sloc_TestAbstractClass() {
        assertEquals(1, factory.createClassMetrics("sloc.TestAbstractClass"), BasicMeasurements.CLASS_SLOC);
        assertEquals(17, factory.createClassMetrics("sloc.TestAbstractClass"), BasicMeasurements.SLOC);
        assertEquals(2, factory.createClassMetrics("sloc.TestAbstractClass"), BasicMeasurements.METHODS);
        assertEquals(0, factory.createClassMetrics("sloc.TestAbstractClass"), BasicMeasurements.ABSTRACT_METHODS);
        assertEquals(0, factory.createClassMetrics("sloc.TestAbstractClass"), BasicMeasurements.SYNTHETIC_METHODS);
    }
    
    @Test
    void test_sloc_TestAbstractClass_TestAbstractClass() {
        assertEquals(2, factory.createMethodMetrics("sloc.TestAbstractClass.TestAbstractClass()"), BasicMeasurements.SLOC);
        assertEquals(1, factory.createMethodMetrics("sloc.TestAbstractClass.TestAbstractClass()"), BasicMeasurements.RAW_METHOD_LENGTH);
        assertEquals(1, factory.createMethodMetrics("sloc.TestAbstractClass.TestAbstractClass()"), BasicMeasurements.EFFECTIVE_METHOD_LENGTH);
    }

    @Test
    void test_sloc_TestAbstractClass_method1() {
        assertEquals(14, factory.createMethodMetrics("sloc.TestAbstractClass.method1(): void"), BasicMeasurements.SLOC);
        assertEquals(21, factory.createMethodMetrics("sloc.TestAbstractClass.method1(): void"), BasicMeasurements.RAW_METHOD_LENGTH);
        assertEquals(13, factory.createMethodMetrics("sloc.TestAbstractClass.method1(): void"), BasicMeasurements.EFFECTIVE_METHOD_LENGTH);
    }

    @Test
    void test_sloc_TestAbstractClass_method2() {
        assertEquals(0, factory.createMethodMetrics("sloc.TestAbstractClass.method2(): void"), BasicMeasurements.SLOC);
    }
    
    @Test
    void test_sloc_TestSuperClass() {
        assertEquals(1, factory.createClassMetrics("sloc.TestSuperClass"), BasicMeasurements.CLASS_SLOC);
        assertEquals(3, factory.createClassMetrics("sloc.TestSuperClass"), BasicMeasurements.SLOC);
        assertEquals(1, factory.createClassMetrics("sloc.TestSuperClass"), BasicMeasurements.METHODS);
        assertEquals(0, factory.createClassMetrics("sloc.TestSuperClass"), BasicMeasurements.ABSTRACT_METHODS);
        assertEquals(0, factory.createClassMetrics("sloc.TestSuperClass"), BasicMeasurements.SYNTHETIC_METHODS);
    }

    @Test
    void test_sloc_TestSuperClass_TestSuperClass() {
        assertEquals(2, factory.createMethodMetrics("sloc.TestSuperClass.TestSuperClass()"), BasicMeasurements.SLOC);
        assertEquals(1, factory.createMethodMetrics("sloc.TestSuperClass.TestSuperClass()"), BasicMeasurements.RAW_METHOD_LENGTH);
        assertEquals(1, factory.createMethodMetrics("sloc.TestSuperClass.TestSuperClass()"), BasicMeasurements.EFFECTIVE_METHOD_LENGTH);
    }

    @Test
    void test_sloc_TestClass() {
        assertEquals(1, factory.createClassMetrics("sloc.TestClass"), BasicMeasurements.CLASS_SLOC);
        assertEquals(11, factory.createClassMetrics("sloc.TestClass"), BasicMeasurements.SLOC);
        assertEquals(3, factory.createClassMetrics("sloc.TestClass"), BasicMeasurements.METHODS);
        assertEquals(0, factory.createClassMetrics("sloc.TestClass"), BasicMeasurements.ABSTRACT_METHODS);
        assertEquals(0, factory.createClassMetrics("sloc.TestClass"), BasicMeasurements.SYNTHETIC_METHODS);
    }

    @Test
    void test_sloc_TestClass_TestClass() {
        assertEquals(2, factory.createMethodMetrics("sloc.TestClass.TestClass()"), BasicMeasurements.SLOC);
        assertEquals(1, factory.createMethodMetrics("sloc.TestClass.TestClass()"), BasicMeasurements.RAW_METHOD_LENGTH);
        assertEquals(1, factory.createMethodMetrics("sloc.TestClass.TestClass()"), BasicMeasurements.EFFECTIVE_METHOD_LENGTH);
    }

    @Test
    void test_sloc_TestClass_method1() {
        assertEquals(0, factory.createMethodMetrics("sloc.TestClass.method1(): void"), BasicMeasurements.SLOC);
        assertEquals(0, factory.createMethodMetrics("sloc.TestClass.method1(): void"), BasicMeasurements.RAW_METHOD_LENGTH);
        assertEquals(0, factory.createMethodMetrics("sloc.TestClass.method1(): void"), BasicMeasurements.EFFECTIVE_METHOD_LENGTH);
    }
    
    @Test
    void test_sloc_TestClass_method2() {
        assertEquals(3, factory.createMethodMetrics("sloc.TestClass.method2(): void"), BasicMeasurements.SLOC);
        assertEquals(2, factory.createMethodMetrics("sloc.TestClass.method2(): void"), BasicMeasurements.RAW_METHOD_LENGTH);
        assertEquals(2, factory.createMethodMetrics("sloc.TestClass.method2(): void"), BasicMeasurements.EFFECTIVE_METHOD_LENGTH);
    }

    @Test
    void test_sloc_TestClass_method3() {
        assertEquals(5, factory.createMethodMetrics("sloc.TestClass.method3(): int"), BasicMeasurements.SLOC);
        assertEquals(7, factory.createMethodMetrics("sloc.TestClass.method3(): int"), BasicMeasurements.RAW_METHOD_LENGTH);
        assertEquals(3, factory.createMethodMetrics("sloc.TestClass.method3(): int"), BasicMeasurements.EFFECTIVE_METHOD_LENGTH);
    }

    @Test
    void test_sloc() {
        assertEquals(34, factory.createGroupMetrics("sloc"), BasicMeasurements.SLOC);
    }

    @Test
    void testProject() {
        assertEquals(34, factory.createProjectMetrics("test"), BasicMeasurements.SLOC);
    }

    private void assertEquals(int expectedValue, Metrics metrics, BasicMeasurements measurement) {
        assertEquals(expectedValue, metrics.getMeasurement(measurement), measurement.getAbbreviation());
    }

    private void assertEquals(int expectedValue, Measurement actualMeasurement, String message) {
        Assertions.assertEquals(expectedValue, actualMeasurement.getValue().intValue(), message);
    }
}
