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

public class TestLinkMaximizer extends TestCase {
    private Node _package;
    private Node test_class;
    private Node test_main_method;
    private Node test_test_method;

    private Node java_lang_package;
    private Node java_lang_Object_class;
    private Node java_lang_Object_Object_method;
    private Node java_lang_String_class;

    private Node java_util_package;
    private Node java_util_Collections_class;
    private Node java_util_Collections_singleton_method;
    private Node java_util_Set_class;

    protected void setUp() throws Exception {
        NodeFactory factory = new NodeFactory();

        _package = factory.createPackage("");
        test_class = factory.createClass("test");
        test_main_method = factory.createFeature("test.main(String[])");
        test_test_method = factory.createFeature("test.test()");

        java_lang_package = factory.createPackage("java.lang");
        java_lang_Object_class = factory.createClass("java.lang.Object");
        java_lang_Object_Object_method = factory.createFeature("java.lang.Object.Object()");
        java_lang_String_class = factory.createClass("java.lang.String");

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

        LinkMaximizer sut = new LinkMaximizer();
        sut.traverseNodes(factory.getPackages().values());
    }

    public void test_package() {
        assertEquals("_package.Outbound()",
                     8,
                     _package.getOutboundDependencies().size());
        assertTrue("default missing " + java_lang_package,
                   _package.getOutboundDependencies().contains(java_lang_package));
        assertTrue("default missing " + java_lang_Object_class,
                   _package.getOutboundDependencies().contains(java_lang_Object_class));
        assertTrue("default missing " + java_lang_Object_Object_method,
                   _package.getOutboundDependencies().contains(java_lang_Object_Object_method));
        assertTrue("default missing " + java_lang_String_class,
                   _package.getOutboundDependencies().contains(java_lang_String_class));
        assertTrue("default missing " + java_util_package,
                   _package.getOutboundDependencies().contains(java_util_package));
        assertTrue("default missing " + java_util_Collections_class,
                   _package.getOutboundDependencies().contains(java_util_Collections_class));
        assertTrue("default missing " + java_util_Collections_singleton_method,
                   _package.getOutboundDependencies().contains(java_util_Collections_singleton_method));
        assertTrue("default missing " + java_util_Set_class,
                   _package.getOutboundDependencies().contains(java_util_Set_class));
        assertEquals("_package.Inbound()",
                     0,
                     _package.getInboundDependencies().size());
    }

    public void testtest_class() {
        assertEquals("test_class.Outbound()",
                     8,
                     test_class.getOutboundDependencies().size());
        assertTrue("test missing " + java_lang_package,
                   test_class.getOutboundDependencies().contains(java_lang_package));
        assertTrue("test missing " + java_lang_Object_class,
                   test_class.getOutboundDependencies().contains(java_lang_Object_class));
        assertTrue("test missing " + java_lang_Object_Object_method,
                   test_class.getOutboundDependencies().contains(java_lang_Object_Object_method));
        assertTrue("test missing " + java_lang_String_class,
                   test_class.getOutboundDependencies().contains(java_lang_String_class));
        assertTrue("test missing " + java_util_package,
                   test_class.getOutboundDependencies().contains(java_util_package));
        assertTrue("test missing " + java_util_Collections_class,
                   test_class.getOutboundDependencies().contains(java_util_Collections_class));
        assertTrue("test missing " + java_util_Collections_singleton_method,
                   test_class.getOutboundDependencies().contains(java_util_Collections_singleton_method));
        assertTrue("test missing " + java_util_Set_class,
                   test_class.getOutboundDependencies().contains(java_util_Set_class));
        assertEquals("test_class.Inbound()",
                     0,
                     test_class.getInboundDependencies().size());
    }

    public void testtest_main_method() {
        assertEquals("test_main_method.Outbound()",
                     8,
                     test_main_method.getOutboundDependencies().size());
        assertTrue("test.main(java.lang.String[]) missing " + java_lang_package,
                   test_main_method.getOutboundDependencies().contains(java_lang_package));
        assertTrue("test.main(java.lang.String[]) missing " + java_lang_Object_class,
                   test_main_method.getOutboundDependencies().contains(java_lang_Object_class));
        assertTrue("test.main(java.lang.String[]) missing " + java_lang_Object_Object_method,
                   test_main_method.getOutboundDependencies().contains(java_lang_Object_Object_method));
        assertTrue("test.main(java.lang.String[]) missing " + java_lang_String_class,
                   test_main_method.getOutboundDependencies().contains(java_lang_String_class));
        assertTrue("test.main(java.lang.String[]) missing " + java_util_package,
                   test_main_method.getOutboundDependencies().contains(java_util_package));
        assertTrue("test.main(java.lang.String[]) missing " + java_util_Collections_class,
                   test_main_method.getOutboundDependencies().contains(java_util_Collections_class));
        assertTrue("test.main(java.lang.String[]) missing " + java_util_Collections_singleton_method,
                   test_main_method.getOutboundDependencies().contains(java_util_Collections_singleton_method));
        assertTrue("test.main(java.lang.String[]) missing " + java_util_Set_class,
                   test_main_method.getOutboundDependencies().contains(java_util_Set_class));
        assertEquals("test_main_method.Inbound()",
                     0,
                     test_main_method.getInboundDependencies().size());
    }

    public void testtest_test_method() {
        assertEquals("test_test_method.Outbound()",
                     3,
                     test_test_method.getOutboundDependencies().size());
        assertTrue("test.test() missing " + java_lang_package,
                   test_test_method.getOutboundDependencies().contains(java_lang_package));
        assertTrue("test.test() missing " + java_lang_Object_class,
                   test_test_method.getOutboundDependencies().contains(java_lang_Object_class));
        assertTrue("test.test() missing " + java_lang_Object_Object_method,
                   test_test_method.getOutboundDependencies().contains(java_lang_Object_Object_method));
        assertEquals("_package.Inbound()",
                     0,
                     test_test_method.getInboundDependencies().size());
    }

    public void testjava_lang_package() {
        assertEquals("java_lang_package.Outbound()",
                     0,
                     java_lang_package.getOutboundDependencies().size());
        assertEquals("java_lang_package.Inbound()",
                     4,
                     java_lang_package.getInboundDependencies().size());
        assertTrue("java.lang missing " + _package,
                   java_lang_package.getInboundDependencies().contains(_package));
        assertTrue("java.lang missing " + test_class,
                   java_lang_package.getInboundDependencies().contains(test_class));
        assertTrue("java.lang missing " + test_main_method,
                   java_lang_package.getInboundDependencies().contains(test_main_method));
        assertTrue("java.lang missing " + test_test_method,
                   java_lang_package.getInboundDependencies().contains(test_test_method));
    }

    public void testjava_lang_Object_class() {
        assertEquals("java_lang_Object_class.Outbound()",
                     0,
                     java_lang_Object_class.getOutboundDependencies().size());
        assertEquals("java_lang_Object_class.Inbound()",
                     4,
                     java_lang_Object_class.getInboundDependencies().size());
        assertTrue("java.lang.Object missing " + _package,
                   java_lang_Object_class.getInboundDependencies().contains(_package));
        assertTrue("java.lang.Object missing " + test_class,
                   java_lang_Object_class.getInboundDependencies().contains(test_class));
        assertTrue("java.lang.Object missing " + test_main_method,
                   java_lang_Object_class.getInboundDependencies().contains(test_main_method));
        assertTrue("java.lang.Object missing " + test_test_method,
                   java_lang_Object_class.getInboundDependencies().contains(test_test_method));
    }

    public void testjava_lang_Object_Object_method() {
        assertEquals("java_lang_Object_Object_method.Outbound()",
                     0,
                     java_lang_Object_Object_method.getOutboundDependencies().size());
        assertEquals("java_lang_Object_Object_method.Inbound()",
                     4,
                     java_lang_Object_Object_method.getInboundDependencies().size());
        assertTrue("java.lang.Object.Object() missing " + _package,
                   java_lang_Object_Object_method.getInboundDependencies().contains(_package));
        assertTrue("java.lang.Object.Object() missing " + test_class,
                   java_lang_Object_Object_method.getInboundDependencies().contains(test_class));
        assertTrue("java.lang.Object.Object() missing " + test_main_method,
                   java_lang_Object_Object_method.getInboundDependencies().contains(test_main_method));
        assertTrue("java.lang.Object.Object() missing " + test_test_method,
                   java_lang_Object_Object_method.getInboundDependencies().contains(test_test_method));
    }

    public void testjava_lang_String_class() {
        assertEquals("java_lang_String_class.Outbound()",
                     0,
                     java_lang_String_class.getOutboundDependencies().size());
        assertEquals("java_lang_String_class.Inbound()",
                     3,
                     java_lang_String_class.getInboundDependencies().size());
        assertTrue("java.lang.String missing " + _package,
                   java_lang_String_class.getInboundDependencies().contains(_package));
        assertTrue("java.lang.String missing " + test_class,
                   java_lang_String_class.getInboundDependencies().contains(test_class));
        assertTrue("java.lang.String missing " + test_main_method,
                   java_lang_String_class.getInboundDependencies().contains(test_main_method));
    }

    public void testjava_util_package() {
        assertEquals("java_util_package.Outbound()",
                     0,
                     java_util_package.getOutboundDependencies().size());
        assertEquals("java_util_package.Inbound()",
                     3,
                     java_util_package.getInboundDependencies().size());
        assertTrue("java.util missing " + _package,
                   java_util_package.getInboundDependencies().contains(_package));
        assertTrue("java.util missing " + test_class,
                   java_util_package.getInboundDependencies().contains(test_class));
        assertTrue("java.util missing " + test_main_method,
                   java_util_package.getInboundDependencies().contains(test_main_method));
    }

    public void testjava_util_Collections_class() {
        assertEquals("java_util_Collections_class.Outbound()",
                     0,
                     java_util_Collections_class.getOutboundDependencies().size());
        assertEquals("java_util_Collections_class.Inbound()",
                     3,
                     java_util_Collections_class.getInboundDependencies().size());
        assertTrue("java.util.Collections missing " + _package,
                   java_util_Collections_class.getInboundDependencies().contains(_package));
        assertTrue("java.util.Collections missing " + test_class,
                   java_util_Collections_class.getInboundDependencies().contains(test_class));
        assertTrue("java.util.Collections missing " + test_main_method,
                   java_util_Collections_class.getInboundDependencies().contains(test_main_method));
    }

    public void testjava_util_Collections_singleton_method() {
        assertEquals("java_util_Collections_singleton_method.Outbound()",
                     0,
                     java_util_Collections_singleton_method.getOutboundDependencies().size());
        assertEquals("java_util_Collections_singleton_method.Inbound()",
                     3,
                     java_util_Collections_singleton_method.getInboundDependencies().size());
        assertTrue("java.util.Collections.singleton(java.lang.Object) missing " + _package,
                   java_util_Collections_singleton_method.getInboundDependencies().contains(_package));
        assertTrue("java.util.Collections.singleton(java.lang.Object) missing " + test_class,
                   java_util_Collections_singleton_method.getInboundDependencies().contains(test_class));
        assertTrue("java.util.Collections.singleton(java.lang.Object) missing " + test_main_method,
                   java_util_Collections_singleton_method.getInboundDependencies().contains(test_main_method));
    }

    public void testjava_util_Set_class() {
        assertEquals("java_util_Set_class.Outbound()",
                     0,
                     java_util_Set_class.getOutboundDependencies().size());
        assertEquals("java_util_Set_class.Inbound()",
                     3,
                     java_util_Set_class.getInboundDependencies().size());
        assertTrue("java.util.Set missing " + _package,
                   java_util_Set_class.getInboundDependencies().contains(_package));
        assertTrue("java.util.Set missing " + test_class,
                   java_util_Set_class.getInboundDependencies().contains(test_class));
        assertTrue("java.util.Set missing " + test_main_method,
                   java_util_Set_class.getInboundDependencies().contains(test_main_method));
    }
}
