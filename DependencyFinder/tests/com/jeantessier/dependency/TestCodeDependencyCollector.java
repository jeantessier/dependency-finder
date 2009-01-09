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

import com.jeantessier.classreader.*;

public class TestCodeDependencyCollector extends TestCase {
    public static final String TEST_CLASS    = "test";
    public static final String TEST_FILENAME = "classes" + File.separator + "test.class";
    
    private NodeFactory factory;

    protected void setUp() throws Exception {
        factory = new NodeFactory();

        ClassfileLoader loader = new AggregatingClassfileLoader();
        loader.addLoadListener(new LoadListenerVisitorAdapter(new CodeDependencyCollector(factory)));
        loader.load(Collections.singleton(TEST_FILENAME));
    }
    
    public void testPackages() {
        assertEquals("nb packages", 4, factory.getPackages().size());

        Node node;

        node = factory.getPackages().get("");
        assertNotNull("default package missing", node);
        assertTrue("default package not concrete", node.isConfirmed());

        node = factory.getPackages().get("java.io");
        assertNotNull("package java.io missing", node);
        assertFalse("package java.io is concrete", node.isConfirmed());

        node = factory.getPackages().get("java.lang");
        assertNotNull("package java.lang missing", node);
        assertFalse("package java.lang is concrete", node.isConfirmed());

        node = factory.getPackages().get("java.util");
        assertNotNull("package java.util missing", node);
        assertFalse("package java.util is concrete", node.isConfirmed());
    }
    
    public void testClasses() {
        assertEquals("nb classes", 8, factory.getClasses().size());

        Node node;

        node = factory.getClasses().get("test");
        assertNotNull("class test missing", node);
        assertTrue("class test not concrete", node.isConfirmed());

        node = factory.getClasses().get("java.io.PrintStream");
        assertNotNull("class java.io.PrintStream missing", node);
        assertFalse("class java.io.PrintStream is concrete", node.isConfirmed());

        node = factory.getClasses().get("java.lang.NullPointerException");
        assertNotNull("class java.lang.NullPointerException missing", node);
        assertFalse("class java.lang.NullPointerException is concrete", node.isConfirmed());

        node = factory.getClasses().get("java.lang.Object");
        assertNotNull("class java.lang.Object missing", node);
        assertFalse("class java.lang.Object is concrete", node.isConfirmed());

        node = factory.getClasses().get("java.lang.String");
        assertNotNull("class java.lang.String missing", node);
        assertFalse("class java.lang.String is concrete", node.isConfirmed());

        node = factory.getClasses().get("java.lang.System");
        assertNotNull("class java.lang.System missing", node);
        assertFalse("class java.lang.System is concrete", node.isConfirmed());

        node = factory.getClasses().get("java.util.Collections");
        assertNotNull("class java.util.Collections missing", node);
        assertFalse("class java.util.Collections is concrete", node.isConfirmed());

        node = factory.getClasses().get("java.util.Set");
        assertNotNull("class java.util.Set missing", node);
        assertFalse("class java.util.Set is concrete", node.isConfirmed());
    }
    
    public void testFeatures() {
        assertEquals("nb features", 6, factory.getFeatures().size());

        Node node;

        node = factory.getFeatures().get("test.main(java.lang.String[])");
        assertNotNull("feature test.main(java.lang.String[]) missing", node);
        assertTrue("feature test.main(java.lang.String[]) not concrete", node.isConfirmed());

        node = factory.getFeatures().get("test.test()");
        assertNotNull("feature test.test() missing", node);
        assertTrue("feature test.test() not concrete", node.isConfirmed());

        node = factory.getFeatures().get("java.io.PrintStream.println(java.lang.Object)");
        assertNotNull("feature java.io.PrintStream.println(java.lang.Object) missing", node);
        assertFalse("feature java.io.PrintStream.println(java.lang.Object) is concrete", node.isConfirmed());

        node = factory.getFeatures().get("java.lang.Object.Object()");
        assertNotNull("feature java.lang.Object.Object() missing", node);
        assertFalse("feature java.lang.Object.Object() is concrete", node.isConfirmed());

        node = factory.getFeatures().get("java.lang.System.out");
        assertNotNull("feature java.lang.System.out missing", node);
        assertFalse("feature java.lang.System.out is concrete", node.isConfirmed());

        node = factory.getFeatures().get("java.util.Collections.singleton(java.lang.Object)");
        assertNotNull("feature java.util.Collections.singleton(java.lang.Object) missing", node);
        assertFalse("feature java.util.Collections.singleton(java.lang.Object) is concrete", node.isConfirmed());
    }
}
