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

package com.jeantessier.dependency;

import java.nio.file.*;
import java.util.*;

import org.junit.jupiter.api.*;

import com.jeantessier.classreader.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestDependencyExtractor {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    public static final String TEST_CLASS    = "test";
    public static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();
    
    private final NodeFactory factory = new NodeFactory();
    
    private final ClassfileLoader loader = new AggregatingClassfileLoader();
    private final NodeFactory testFactory = new NodeFactory();

    @BeforeEach
    void setUp() {
        var _package = factory.createPackage("");
        var test_class = factory.createClass("test");
        var test_main_feature = factory.createFeature("test.main(java.lang.String[]): void");
        var test_test_feature = factory.createFeature("test.test()");
        
        var java_io_package = factory.createPackage("java.io");
        var java_io_PrintStream_class = factory.createClass("java.io.PrintStream");
        var java_io_PrintStream_println_feature = factory.createFeature("java.io.PrintStream.println(java.lang.Object): void");

        var java_lang_package = factory.createPackage("java.lang");
        var java_lang_NullPointerException_class = factory.createClass("java.lang.NullPointerException");
        var java_lang_Object_class = factory.createClass("java.lang.Object");
        var java_lang_Object_Object_feature = factory.createFeature("java.lang.Object.Object()");
        var java_lang_String_class = factory.createClass("java.lang.String");
        var java_lang_System_class = factory.createClass("java.lang.System");
        var java_lang_System_out_feature = factory.createFeature("java.lang.System.out");
        
        var java_util_package = factory.createPackage("java.util");
        var java_util_Collections_class = factory.createClass("java.util.Collections");
        var java_util_Collections_singleton_feature = factory.createFeature("java.util.Collections.singleton(java.lang.Object): java.util.Set");
        var java_util_Set_class = factory.createClass("java.util.Set");
        
        test_class.addDependency(java_lang_Object_class);
        test_main_feature.addDependency(java_io_PrintStream_class);
        test_main_feature.addDependency(java_io_PrintStream_println_feature);
        test_main_feature.addDependency(java_lang_NullPointerException_class);
        test_main_feature.addDependency(java_lang_Object_class);
        test_main_feature.addDependency(java_lang_Object_Object_feature);
        test_main_feature.addDependency(java_lang_String_class);
        test_main_feature.addDependency(java_lang_System_out_feature);
        test_main_feature.addDependency(java_util_Collections_singleton_feature);
        test_main_feature.addDependency(java_util_Set_class);
        test_test_feature.addDependency(java_lang_Object_Object_feature);

        loader.load(Collections.singleton(TEST_FILENAME));
        loader.getClassfile(TEST_CLASS).accept(new CodeDependencyCollector(testFactory));
    }

    @Test
    void testPackageList() {
        assertEquals(
                factory.getPackages().keySet(),
                testFactory.getPackages().keySet(),
                "Different list of packages");
    }

    @Test
    void testClassList() {
        assertEquals(
                factory.getClasses().keySet(),
                testFactory.getClasses().keySet(),
                "Different list of classes");
    }

    @Test
    void testFeatureList() {
        assertEquals(
                factory.getFeatures().keySet(),
                testFactory.getFeatures().keySet(),
                "Different list of features");
    }

    @Test
    void testPackages() {
        factory.getPackages().keySet().forEach(key -> {
            assertEquals(factory.getPackages().get(key), testFactory.getPackages().get(key), key);
            assertNotSame(factory.getPackages().get(key), testFactory.getPackages().get(key), key + " is same");
            assertEquals(
                    factory.getPackages().get(key).getInboundDependencies().size(),
                    testFactory.getPackages().get(key).getInboundDependencies().size(),
                    key + " inbounds");
            assertEquals(
                    factory.getPackages().get(key).getOutboundDependencies().size(),
                    testFactory.getPackages().get(key).getOutboundDependencies().size(),
                    key + " outbounds");
        });
    }

    @Test
    void testClasses() {
        factory.getClasses().keySet().forEach(key -> {
            assertEquals(factory.getClasses().get(key), testFactory.getClasses().get(key), key);
            assertNotSame(factory.getClasses().get(key), testFactory.getClasses().get(key), key + " is same");
            assertEquals(
                    factory.getClasses().get(key).getInboundDependencies().size(),
                    testFactory.getClasses().get(key).getInboundDependencies().size(),
                    key + " inbounds");
            assertEquals(
                    factory.getClasses().get(key).getOutboundDependencies().size(),
                    testFactory.getClasses().get(key).getOutboundDependencies().size(),
                    key + " outbounds");
        });
    }

    @Test
    void testFeatures() {
        factory.getFeatures().keySet().forEach(key -> {
            assertEquals(factory.getFeatures().get(key), testFactory.getFeatures().get(key), key);
            assertNotSame(factory.getFeatures().get(key), testFactory.getFeatures().get(key), key + " is same");
            assertEquals(
                    factory.getFeatures().get(key).getInboundDependencies().size(),
                    testFactory.getFeatures().get(key).getInboundDependencies().size(),
                    key + " inbounds");
            assertEquals(
                    factory.getFeatures().get(key).getOutboundDependencies().size(),
                    testFactory.getFeatures().get(key).getOutboundDependencies().size(),
                    key + " outbounds");
        });
    }

    @Test
    void testStaticInitializer() {
        var loader = new AggregatingClassfileLoader();
        var factory = new NodeFactory();
        
        loader.load(Collections.singleton(CLASSES_DIR.resolve("StaticInitializerTest.class").toString()));

        var classfile = loader.getClassfile("StaticInitializerTest");
        classfile.accept(new CodeDependencyCollector(factory));

        var featureNames = factory.getFeatures().keySet();

        classfile.getAllMethods().forEach(method -> assertTrue(featureNames.contains(method.getFullSignature()), "Missing method " + method.getFullSignature()));
    }
}
