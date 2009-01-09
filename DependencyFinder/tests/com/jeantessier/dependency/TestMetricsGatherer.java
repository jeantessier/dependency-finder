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

import junit.framework.*;

public class TestMetricsGatherer extends TestCase {
    private NodeFactory factory;
    
    private Node _package;
    private Node test_class;
    private Node test_main_method;
    private Node test_test_method;
        
    private Node java_lang_package;
    private Node java_lang_Object_class;
    private Node java_lang_Object_Object_method;
    private Node java_lang_String_class;
        
    private Node java_io_package;
    private Node java_io_Writer_class;
    private Node java_io_Writer_write_method;
        
    private Node java_util_package;
    private Node java_util_Collections_class;
    private Node java_util_Collections_singleton_method;
    private Node java_util_Set_class;

    private MetricsGatherer metrics;

    protected void setUp() throws Exception {
        factory = new NodeFactory();

        _package = factory.createPackage("", true);
        test_class = factory.createClass("test", true);
        test_main_method = factory.createFeature("test.main(java.lang.String[])", true);
        test_test_method = factory.createFeature("test.test()", true);
        
        java_lang_package = factory.createPackage("java.lang");
        java_lang_Object_class = factory.createClass("java.lang.Object");
        java_lang_Object_Object_method = factory.createFeature("java.lang.Object.Object()");
        java_lang_String_class = factory.createClass("java.lang.String");

        java_io_package = factory.createPackage("java.io");
        java_io_Writer_class = factory.createClass("java.io.Writer");
        java_io_Writer_write_method = factory.createFeature("java.io.Writer.write(int)");
        
        java_util_package = factory.createPackage("java.util");
        java_util_Collections_class = factory.createClass("java.util.Collections");
        java_util_Collections_singleton_method = factory.createFeature("java.util.Collections.singleton(java.lang.Object)");
        java_util_Set_class = factory.createClass("java.util.Set");

        test_class.addDependency(java_lang_Object_class);
        test_main_method.addDependency(java_lang_Object_class);
        test_main_method.addDependency(java_lang_Object_Object_method);
        test_main_method.addDependency(java_lang_String_class);
        test_main_method.addDependency(java_util_Collections_singleton_method);
        test_main_method.addDependency(java_util_Set_class);
        test_test_method.addDependency(java_lang_Object_Object_method);

        metrics = new MetricsGatherer();
    }

    public void testEverything() {
        metrics.traverseNodes(factory.getPackages().values());

        assertEquals("Number of packages", 4, metrics.getPackages().size());
        assertEquals("Number of classes",  6, metrics.getClasses().size());
        assertEquals("Number of features", 5, metrics.getFeatures().size());

        assertEquals("Number of inbounds",             7, metrics.getNbInbound());
        assertEquals("Number of inbounds to packages", 0, metrics.getNbInboundPackages());
        assertEquals("Number of inbounds to classes",  4, metrics.getNbInboundClasses());
        assertEquals("Number of inbounds to features", 3, metrics.getNbInboundFeatures());

        assertEquals("Number of outbounds",               7, metrics.getNbOutbound());
        assertEquals("Number of outbounds from packages", 0, metrics.getNbOutboundPackages());
        assertEquals("Number of outbounds from classes",  1, metrics.getNbOutboundClasses());
        assertEquals("Number of outbounds from features", 6, metrics.getNbOutboundFeatures());
    }
}
