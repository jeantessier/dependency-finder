/*
 *  Copyright (c) 2001-2009, Jean Tessier
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

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

public class TestDependencyExtractor extends TestCase {
    public static final String TEST_CLASS    = "test";
    public static final String TEST_FILENAME = "classes" + File.separator + "test.class";
    
    private NodeFactory factory;
    
    private Node _package;
    private Node test_class;
    private Node test_main_feature;
    private Node test_test_feature;
        
    private Node java_io_package;
    private Node java_io_PrintStream_class;
    private Node java_io_PrintStream_println_feature;
    
    private Node java_lang_package;
    private Node java_lang_NullPointerException_class;
    private Node java_lang_Object_class;
    private Node java_lang_Object_Object_feature;
    private Node java_lang_String_class;
    private Node java_lang_System_class;
    private Node java_lang_System_out_feature;
        
    private Node java_util_package;
    private Node java_util_Collections_class;
    private Node java_util_Collections_singleton_feature;
    private Node java_util_Set_class;

    private ClassfileLoader loader;
    private NodeFactory     testFactory;

    protected void setUp() throws Exception {
        Logger.getLogger(getClass()).info("Starting test: " + getName());

        factory = new NodeFactory();

        _package = factory.createPackage("");
        test_class = factory.createClass("test");
        test_main_feature = factory.createFeature("test.main(java.lang.String[])");
        test_test_feature = factory.createFeature("test.test()");
        
        java_io_package = factory.createPackage("java.io");
        java_io_PrintStream_class = factory.createClass("java.io.PrintStream");
        java_io_PrintStream_println_feature = factory.createFeature("java.io.PrintStream.println(java.lang.Object)");

        java_lang_package = factory.createPackage("java.lang");
        java_lang_NullPointerException_class = factory.createClass("java.lang.NullPointerException");
        java_lang_Object_class = factory.createClass("java.lang.Object");
        java_lang_Object_Object_feature = factory.createFeature("java.lang.Object.Object()");
        java_lang_String_class = factory.createClass("java.lang.String");
        java_lang_System_class = factory.createClass("java.lang.System");
        java_lang_System_out_feature = factory.createFeature("java.lang.System.out");
        
        java_util_package = factory.createPackage("java.util");
        java_util_Collections_class = factory.createClass("java.util.Collections");
        java_util_Collections_singleton_feature = factory.createFeature("java.util.Collections.singleton(java.lang.Object)");
        java_util_Set_class = factory.createClass("java.util.Set");
        
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

        loader = new AggregatingClassfileLoader();
        loader.load(Collections.singleton(TEST_FILENAME));

        testFactory = new NodeFactory();
        loader.getClassfile(TEST_CLASS).accept(new CodeDependencyCollector(testFactory));
    }

    protected void tearDown() throws Exception {
        Logger.getLogger(getClass()).info("End of " + getName());
    }
    
    public void testPackageList() {
        assertEquals("Different list of packages",
                     factory.getPackages().keySet(),
                     testFactory.getPackages().keySet());
    }
    
    public void testClassList() {
        assertEquals("Different list of classes",
                     factory.getClasses().keySet(),
                     testFactory.getClasses().keySet());
    }
    
    public void testFeatureList() {
        assertEquals("Different list of features",
                     factory.getFeatures().keySet(),
                     testFactory.getFeatures().keySet());
    }
    
    public void testPackages() {
        for (String key : factory.getPackages().keySet()) {
            assertEquals(factory.getPackages().get(key), testFactory.getPackages().get(key));
            assertTrue(key + " is same", factory.getPackages().get(key) != testFactory.getPackages().get(key));
            assertEquals(key + " inbounds",
                         factory.getPackages().get(key).getInboundDependencies().size(),
                         testFactory.getPackages().get(key).getInboundDependencies().size());
            assertEquals(key + " outbounds",
                         factory.getPackages().get(key).getOutboundDependencies().size(),
                         testFactory.getPackages().get(key).getOutboundDependencies().size());
        }
    }
    
    public void testClasses() {
        for (String key : factory.getClasses().keySet()) {
            assertEquals(factory.getClasses().get(key), testFactory.getClasses().get(key));
            assertTrue(key + " is same", factory.getClasses().get(key) != testFactory.getClasses().get(key));
            assertEquals(key + " inbounds",
                         factory.getClasses().get(key).getInboundDependencies().size(),
                         testFactory.getClasses().get(key).getInboundDependencies().size());
            assertEquals(key + " outbounds",
                         factory.getClasses().get(key).getOutboundDependencies().size(),
                         testFactory.getClasses().get(key).getOutboundDependencies().size());
        }
    }
    
    public void testFeatures() {
        for (String key : factory.getFeatures().keySet()) {
            assertEquals(factory.getFeatures().get(key), testFactory.getFeatures().get(key));
            assertTrue(key + " is same", factory.getFeatures().get(key) != testFactory.getFeatures().get(key));
            assertEquals(key + " inbounds",
                         factory.getFeatures().get(key).getInboundDependencies().size(),
                         testFactory.getFeatures().get(key).getInboundDependencies().size());
            assertEquals(key + " outbounds",
                         factory.getFeatures().get(key).getOutboundDependencies().size(),
                         testFactory.getFeatures().get(key).getOutboundDependencies().size());
        }
    }

    public void testStaticInitializer() {
        ClassfileLoader loader  = new AggregatingClassfileLoader();
        NodeFactory     factory = new NodeFactory();
        
        loader.load(Collections.singleton("classes" + File.separator + "StaticInitializerTest.class"));

        Classfile classfile = loader.getClassfile("StaticInitializerTest");
        classfile.accept(new CodeDependencyCollector(factory));

        Collection featureNames = factory.getFeatures().keySet();

        for (Method_info method : classfile.getAllMethods()) {
            assertTrue("Missing method " + method.getFullSignature(), featureNames.contains(method.getFullSignature()));
        }
    }
}
