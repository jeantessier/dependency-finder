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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestLinkMinimizer {
    private final NodeFactory factory = new NodeFactory();

    private final Node _package = factory.createPackage("");
    private final Node test_class = factory.createClass("test");
    private final Node test_main_method = factory.createFeature("test.main(String[])");
    private final Node test_test_method = factory.createFeature("test.test()");

    private final Node java_lang_package = factory.createPackage("java.lang");
    private final Node java_lang_Object_class = factory.createClass("java.lang.Object");
    private final Node java_lang_Object_Object_method = factory.createFeature("java.lang.Object.Object()");
    private final Node java_lang_String_class = factory.createClass("java.lang.String");

    private final Node java_util_package = factory.createPackage("java.util");
    private final Node java_util_Collections_class = factory.createClass("java.util.Collections");
    private final Node java_util_Collections_singleton_method = factory.createFeature("java.util.Collections.singleton(java.lang.Object)");
    private final Node java_util_Set_class = factory.createClass("java.util.Set");

    private final LinkMinimizer sut = new LinkMinimizer();

    @BeforeEach
    void setUp() {
        test_class.addDependency(java_lang_Object_class);
        test_main_method.addDependency(java_lang_Object_class);
        test_main_method.addDependency(java_lang_Object_Object_method);
        test_main_method.addDependency(java_lang_String_class);
        test_main_method.addDependency(java_util_Collections_singleton_method);
        test_main_method.addDependency(java_util_Set_class);
        test_test_method.addDependency(java_lang_Object_Object_method);

        sut.traverseNodes(factory.getPackages().values());
    }

    @Test
    void test_package() {
        var expectedOutbounds = List.of();
        assertEquals(expectedOutbounds, _package.getOutboundDependencies().stream().sorted().toList());

        var expectedInbounds = List.of();
        assertEquals(expectedInbounds, _package.getInboundDependencies().stream().sorted().toList());
    }

    @Test
    void testtest_class() {
        var expectedOutbounds = List.of();
        assertEquals(expectedOutbounds, test_class.getOutboundDependencies().stream().sorted().toList());

        var expectedInbounds = List.of();
        assertEquals(expectedInbounds, test_class.getInboundDependencies().stream().sorted().toList());
    }

    @Test
    void testtest_main_method() {
        var expectedOutbounds = List.of(
                java_lang_Object_Object_method,
                java_lang_String_class,
                java_util_Collections_singleton_method,
                java_util_Set_class
        );
        assertEquals(expectedOutbounds, test_main_method.getOutboundDependencies().stream().sorted().toList());

        var expectedInbounds = List.of();
        assertEquals(expectedInbounds, test_main_method.getInboundDependencies().stream().sorted().toList());
    }

    @Test
    void testtest_test_method() {
        var expectedOutbounds = List.of(
                java_lang_Object_Object_method
        );
        assertEquals(expectedOutbounds, test_test_method.getOutboundDependencies().stream().sorted().toList());

        var expectedInbounds = List.of();
        assertEquals(expectedInbounds, test_test_method.getInboundDependencies().stream().sorted().toList());
    }

    @Test
    void testjava_lang_package() {
        var expectedOutbounds = List.of();
        assertEquals(expectedOutbounds, java_lang_package.getOutboundDependencies().stream().sorted().toList());

        var expectedInbounds = List.of();
        assertEquals(expectedInbounds, java_lang_package.getInboundDependencies().stream().sorted().toList());
    }

    @Test
    void testjava_lang_Object_class() {
        var expectedOutbounds = List.of();
        assertEquals(expectedOutbounds, java_lang_Object_class.getOutboundDependencies().stream().sorted().toList());

        var expectedInbounds = List.of();
        assertEquals(expectedInbounds, java_lang_Object_class.getInboundDependencies().stream().sorted().toList());
    }

    @Test
    void testjava_lang_Object_Object_method() {
        var expectedOutbounds = List.of();
        assertEquals(expectedOutbounds, java_lang_Object_Object_method.getOutboundDependencies().stream().sorted().toList());

        var expectedInbounds = List.of(
                test_main_method,
                test_test_method
        );
        assertEquals(expectedInbounds, java_lang_Object_Object_method.getInboundDependencies().stream().sorted().toList());
    }

    @Test
    void testjava_lang_String_class() {
        var expectedOutbounds = List.of();
        assertEquals(expectedOutbounds, java_lang_String_class.getOutboundDependencies().stream().sorted().toList());

        var expectedInbounds = List.of(
                test_main_method
        );
        assertEquals(expectedInbounds, java_lang_String_class.getInboundDependencies().stream().sorted().toList());
    }

    @Test
    void testjava_util_package() {
        var expectedOutbounds = List.of();
        assertEquals(expectedOutbounds, java_util_package.getOutboundDependencies().stream().sorted().toList());

        var expectedInbounds = List.of();
        assertEquals(expectedInbounds, java_util_package.getInboundDependencies().stream().sorted().toList());
    }

    @Test
    void testjava_util_Collections_class() {
        var expectedOutbounds = List.of();
        assertEquals(expectedOutbounds, java_util_Collections_class.getOutboundDependencies().stream().sorted().toList());

        var expectedInbounds = List.of();
        assertEquals(expectedInbounds, java_util_Collections_class.getInboundDependencies().stream().sorted().toList());
    }

    @Test
    void testjava_util_Collections_singleton_method() {
        var expectedOutbounds = List.of();
        assertEquals(expectedOutbounds, java_util_Collections_singleton_method.getOutboundDependencies().stream().sorted().toList());

        var expectedInbounds = List.of(
                test_main_method
        );
        assertEquals(expectedInbounds, java_util_Collections_singleton_method.getInboundDependencies().stream().sorted().toList());
    }

    @Test
    void testjava_util_Set_class() {
        var expectedOutbounds = List.of();
        assertEquals(expectedOutbounds, java_util_Set_class.getOutboundDependencies().stream().sorted().toList());

        var expectedInbounds = List.of(
                test_main_method
        );
        assertEquals(expectedInbounds, java_util_Set_class.getInboundDependencies().stream().sorted().toList());
    }
}
