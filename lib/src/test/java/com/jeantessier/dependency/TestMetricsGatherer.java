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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestMetricsGatherer {
    private final NodeFactory factory = new NodeFactory();

    private final Node _package = factory.createPackage("", true);
    private final Node test_class = factory.createClass("test", true);
    private final Node test_main_method = factory.createFeature("test.main(java.lang.String[])", true);
    private final Node test_test_method = factory.createFeature("test.test()", true);

    private final Node java_lang_package = factory.createPackage("java.lang");
    private final Node java_lang_Object_class = factory.createClass("java.lang.Object");
    private final Node java_lang_Object_Object_method = factory.createFeature("java.lang.Object.Object()");
    private final Node java_lang_String_class = factory.createClass("java.lang.String");

    private final Node java_io_package = factory.createPackage("java.io");
    private final Node java_io_Writer_class = factory.createClass("java.io.Writer");
    private final Node java_io_Writer_write_method = factory.createFeature("java.io.Writer.write(int)");

    private final Node java_util_package = factory.createPackage("java.util");
    private final Node java_util_Collections_class = factory.createClass("java.util.Collections");
    private final Node java_util_Collections_singleton_method = factory.createFeature("java.util.Collections.singleton(java.lang.Object)");
    private final Node java_util_Set_class = factory.createClass("java.util.Set");

    private final MetricsGatherer metrics = new MetricsGatherer();

    @BeforeEach
    void setUp() {
        test_class.addDependency(java_lang_Object_class);
        test_main_method.addDependency(java_lang_Object_class);
        test_main_method.addDependency(java_lang_Object_Object_method);
        test_main_method.addDependency(java_lang_String_class);
        test_main_method.addDependency(java_util_Collections_singleton_method);
        test_main_method.addDependency(java_util_Set_class);
        test_test_method.addDependency(java_lang_Object_Object_method);
    }

    @Test
    void testEverything() {
        metrics.traverseNodes(factory.getPackages().values());

        assertEquals(4, metrics.getPackages().size(), "Number of packages");
        assertEquals(6, metrics.getClasses().size(), "Number of classes");
        assertEquals(5, metrics.getFeatures().size(), "Number of features");

        assertEquals(7, metrics.getNbInbound(), "Number of inbounds");
        assertEquals(0, metrics.getNbInboundPackages(), "Number of inbounds to packages");
        assertEquals(4, metrics.getNbInboundClasses(), "Number of inbounds to classes");
        assertEquals(3, metrics.getNbInboundFeatures(), "Number of inbounds to features");

        assertEquals(7, metrics.getNbOutbound(), "Number of outbounds");
        assertEquals(0, metrics.getNbOutboundPackages(), "Number of outbounds from packages");
        assertEquals(1, metrics.getNbOutboundClasses(), "Number of outbounds from classes");
        assertEquals(6, metrics.getNbOutboundFeatures(), "Number of outbounds from features");
    }
}
