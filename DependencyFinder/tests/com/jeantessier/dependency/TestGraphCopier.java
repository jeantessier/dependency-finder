/*
 *  Copyright (c) 2001-2005, Jean Tessier
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

public class TestGraphCopier extends TestCase {
    private RegularExpressionSelectionCriteria scopeCriteria;
    private RegularExpressionSelectionCriteria filterCriteria;
    private NodeFactory                        factory;
    
    private Node _package;
    private Node test_class;
    private Node test_main_method;
    private Node test_Test_method;
        
    private Node java_lang_package;
    private Node java_lang_Object_class;
    private Node java_lang_Object_Object_method;
    private Node java_lang_String_class;
        
    private Node java_util_package;
    private Node java_util_Collections_class;
    private Node java_util_Collections_singleton_method;
    
    private GraphCopier copier;

    protected void setUp() throws Exception {
        scopeCriteria  = new RegularExpressionSelectionCriteria();
        filterCriteria = new RegularExpressionSelectionCriteria();
        factory        = new NodeFactory();

        _package = factory.createPackage("");
        test_class = factory.createClass("test");
        test_main_method = factory.createFeature("test.main(String[])");
        test_Test_method = factory.createFeature("test.Test()");
        
        java_lang_package = factory.createPackage("java.lang");
        java_lang_Object_class = factory.createClass("java.lang.Object");
        java_lang_Object_Object_method = factory.createFeature("java.lang.Object.Object()");
        java_lang_String_class = factory.createClass("java.lang.String");
        
        java_util_package = factory.createPackage("java.util");
        java_util_Collections_class = factory.createClass("java.util.Collections");
        java_util_Collections_singleton_method = factory.createFeature("java.util.Collections.singleton(java.lang.Object)");
        
        test_class.addDependency(java_lang_Object_class);
        test_main_method.addDependency(java_lang_Object_class);
        test_main_method.addDependency(java_lang_Object_Object_method);
        test_main_method.addDependency(java_lang_String_class);
        test_main_method.addDependency(java_util_Collections_singleton_method);
        test_Test_method.addDependency(java_lang_Object_Object_method);

        copier = new GraphCopier(new SelectiveTraversalStrategy(scopeCriteria, filterCriteria));
    }

    public void testCopyFullGraph() {
        copier.traverseNodes(factory.getPackages().values());

        assertEquals("Different number of packages",
                     factory.getPackages().size(),
                     copier.getScopeFactory().getPackages().size());
        assertEquals("Different number of classes",
                     factory.getClasses().size(),
                     copier.getScopeFactory().getClasses().size());
        assertEquals("Different number of features",
                     factory.getFeatures().size(),
                     copier.getScopeFactory().getFeatures().size());

        Iterator i;

        i = factory.getPackages().keySet().iterator();
        while(i.hasNext()) {
            Object key = i.next();
            assertEquals(factory.getPackages().get(key), copier.getScopeFactory().getPackages().get(key));
            assertTrue(factory.getPackages().get(key) != copier.getScopeFactory().getPackages().get(key));
            assertEquals(((Node) factory.getPackages().get(key)).getInboundDependencies().size(),
                         ((Node) copier.getScopeFactory().getPackages().get(key)).getInboundDependencies().size());
            assertEquals(((Node) factory.getPackages().get(key)).getOutboundDependencies().size(),
                         ((Node) copier.getScopeFactory().getPackages().get(key)).getOutboundDependencies().size());
        }
        
        i = factory.getClasses().keySet().iterator();
        while(i.hasNext()) {
            Object key = i.next();
            assertEquals(factory.getClasses().get(key), copier.getScopeFactory().getClasses().get(key));
            assertTrue(factory.getClasses().get(key) != copier.getScopeFactory().getClasses().get(key));
            assertEquals(((Node) factory.getClasses().get(key)).getInboundDependencies().size(),
                         ((Node) copier.getScopeFactory().getClasses().get(key)).getInboundDependencies().size());
            assertEquals(((Node) factory.getClasses().get(key)).getOutboundDependencies().size(),
                         ((Node) copier.getScopeFactory().getClasses().get(key)).getOutboundDependencies().size());
        }
        
        i = factory.getFeatures().keySet().iterator();
        while(i.hasNext()) {
            Object key = i.next();
            assertEquals(factory.getFeatures().get(key), copier.getScopeFactory().getFeatures().get(key));
            assertTrue(factory.getFeatures().get(key) != copier.getScopeFactory().getFeatures().get(key));
            assertEquals(((Node) factory.getFeatures().get(key)).getInboundDependencies().size(),
                         ((Node) copier.getScopeFactory().getFeatures().get(key)).getInboundDependencies().size());
            assertEquals(((Node) factory.getFeatures().get(key)).getOutboundDependencies().size(),
                         ((Node) copier.getScopeFactory().getFeatures().get(key)).getOutboundDependencies().size());
        }
    }

    public void testCopyAllNodesOnly() {
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);
        
        copier.traverseNodes(factory.getPackages().values());

        assertEquals("Different number of packages",
                     factory.getPackages().size(),
                     copier.getScopeFactory().getPackages().size());
        assertEquals("Different number of classes",
                     factory.getClasses().size(),
                     copier.getScopeFactory().getClasses().size());
        assertEquals("Different number of features",
                     factory.getFeatures().size(),
                     copier.getScopeFactory().getFeatures().size());

        Iterator i;

        i = factory.getPackages().keySet().iterator();
        while(i.hasNext()) {
            Object key = i.next();
            assertEquals(factory.getPackages().get(key), copier.getScopeFactory().getPackages().get(key));
            assertTrue(factory.getPackages().get(key) != copier.getScopeFactory().getPackages().get(key));
            assertTrue(((Node) copier.getScopeFactory().getPackages().get(key)).getInboundDependencies().isEmpty());
            assertTrue(((Node) copier.getScopeFactory().getPackages().get(key)).getOutboundDependencies().isEmpty());
        }
        
        i = factory.getClasses().keySet().iterator();
        while(i.hasNext()) {
            Object key = i.next();
            assertEquals(factory.getClasses().get(key), copier.getScopeFactory().getClasses().get(key));
            assertTrue(factory.getClasses().get(key) != copier.getScopeFactory().getClasses().get(key));
            assertTrue(((Node) copier.getScopeFactory().getClasses().get(key)).getInboundDependencies().isEmpty());
            assertTrue(((Node) copier.getScopeFactory().getClasses().get(key)).getOutboundDependencies().isEmpty());
        }
        
        i = factory.getFeatures().keySet().iterator();
        while(i.hasNext()) {
            Object key = i.next();
            assertEquals(factory.getFeatures().get(key), copier.getScopeFactory().getFeatures().get(key));
            assertTrue(factory.getFeatures().get(key) != copier.getScopeFactory().getFeatures().get(key));
            assertTrue(((Node) copier.getScopeFactory().getFeatures().get(key)).getInboundDependencies().isEmpty());
            assertTrue(((Node) copier.getScopeFactory().getFeatures().get(key)).getOutboundDependencies().isEmpty());
        }
    }

    public void testCopyPackageNodesOnly() {
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);
        
        copier.traverseNodes(factory.getPackages().values());

        assertEquals("Different number of packages",
                     factory.getPackages().size(),
                     copier.getScopeFactory().getPackages().size());
        assertTrue(copier.getScopeFactory().getClasses().isEmpty());
        assertTrue(copier.getScopeFactory().getFeatures().isEmpty());

        Iterator i;

        i = factory.getPackages().keySet().iterator();
        while(i.hasNext()) {
            Object key = i.next();
            assertEquals(factory.getPackages().get(key), copier.getScopeFactory().getPackages().get(key));
            assertTrue(factory.getPackages().get(key) != copier.getScopeFactory().getPackages().get(key));
            assertTrue(((Node) copier.getScopeFactory().getPackages().get(key)).getInboundDependencies().isEmpty());
            assertTrue(((Node) copier.getScopeFactory().getPackages().get(key)).getOutboundDependencies().isEmpty());
        }
    }

    public void testCopyClassNodesOnly() {
        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingFeatures(false);
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);
        
        copier.traverseNodes(factory.getPackages().values());

        assertEquals("Different number of packages",
                     factory.getPackages().size(),
                     copier.getScopeFactory().getPackages().size());
        assertEquals("Different number of classes",
                     factory.getClasses().size(),
                     copier.getScopeFactory().getClasses().size());
        assertTrue(copier.getScopeFactory().getFeatures().isEmpty());

        Iterator i;

        i = factory.getPackages().keySet().iterator();
        while(i.hasNext()) {
            Object key = i.next();
            assertEquals(factory.getPackages().get(key), copier.getScopeFactory().getPackages().get(key));
            assertTrue(factory.getPackages().get(key) != copier.getScopeFactory().getPackages().get(key));
            assertTrue(((Node) copier.getScopeFactory().getPackages().get(key)).getInboundDependencies().isEmpty());
            assertTrue(((Node) copier.getScopeFactory().getPackages().get(key)).getOutboundDependencies().isEmpty());
        }
        
        i = factory.getClasses().keySet().iterator();
        while(i.hasNext()) {
            Object key = i.next();
            assertEquals(factory.getClasses().get(key), copier.getScopeFactory().getClasses().get(key));
            assertTrue(factory.getClasses().get(key) != copier.getScopeFactory().getClasses().get(key));
            assertTrue(((Node) copier.getScopeFactory().getClasses().get(key)).getInboundDependencies().isEmpty());
            assertTrue(((Node) copier.getScopeFactory().getClasses().get(key)).getOutboundDependencies().isEmpty());
        }
    }

    public void testCopyFeatureNodesOnly() {
        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);
        
        copier.traverseNodes(factory.getPackages().values());

        assertEquals("Different number of packages",
                     factory.getPackages().size(),
                     copier.getScopeFactory().getPackages().size());
        assertEquals("Different number of classes",
                     3,
                     copier.getScopeFactory().getClasses().size());
        assertEquals("Different number of features",
                     factory.getFeatures().size(),
                     copier.getScopeFactory().getFeatures().size());

        Iterator i;

        i = copier.getScopeFactory().getPackages().keySet().iterator();
        while(i.hasNext()) {
            Object key = i.next();
            assertEquals(factory.getPackages().get(key), copier.getScopeFactory().getPackages().get(key));
            assertTrue(factory.getPackages().get(key) != copier.getScopeFactory().getPackages().get(key));
            assertTrue(((Node) copier.getScopeFactory().getPackages().get(key)).getInboundDependencies().isEmpty());
            assertTrue(((Node) copier.getScopeFactory().getPackages().get(key)).getOutboundDependencies().isEmpty());
        }
        
        i = copier.getScopeFactory().getClasses().keySet().iterator();
        while(i.hasNext()) {
            Object key = i.next();
            assertEquals(factory.getClasses().get(key), copier.getScopeFactory().getClasses().get(key));
            assertTrue(factory.getClasses().get(key) != copier.getScopeFactory().getClasses().get(key));
            assertTrue(((Node) copier.getScopeFactory().getClasses().get(key)).getInboundDependencies().isEmpty());
            assertTrue(((Node) copier.getScopeFactory().getClasses().get(key)).getOutboundDependencies().isEmpty());
        }
        
        i = copier.getScopeFactory().getFeatures().keySet().iterator();
        while(i.hasNext()) {
            Object key = i.next();
            assertEquals(factory.getFeatures().get(key), copier.getScopeFactory().getFeatures().get(key));
            assertTrue(factory.getFeatures().get(key) != copier.getScopeFactory().getFeatures().get(key));
            assertTrue(((Node) copier.getScopeFactory().getFeatures().get(key)).getInboundDependencies().isEmpty());
            assertTrue(((Node) copier.getScopeFactory().getFeatures().get(key)).getOutboundDependencies().isEmpty());
        }
    }

    public void testCopyNothing() {
        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);
        
        copier.traverseNodes(factory.getPackages().values());

        assertTrue(copier.getScopeFactory().getPackages().isEmpty());
        assertTrue(copier.getScopeFactory().getClasses().isEmpty());
        assertTrue(copier.getScopeFactory().getFeatures().isEmpty());
    }

    public void testC2CasP2CSamePackage() {
        NodeFactory factory = new NodeFactory();

        Node a   = factory.createPackage("a");
        Node a_A = factory.createClass("a.A");
        Node a_B = factory.createClass("a.B");
    
        a_A.addDependency(a_B);

        RegularExpressionSelectionCriteria scopeCriteria = new RegularExpressionSelectionCriteria();
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);

        RegularExpressionSelectionCriteria filterCriteria = new RegularExpressionSelectionCriteria();
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingFeatures(false);
        
        GraphCopier copier = new GraphCopier(new SelectiveTraversalStrategy(scopeCriteria, filterCriteria));

        copier.traverseNodes(factory.getPackages().values());

        assertTrue(copier.getScopeFactory().getPackages().keySet().toString(), copier.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(copier.getScopeFactory().getClasses().isEmpty());
        assertTrue(copier.getScopeFactory().getFeatures().isEmpty());

        assertEquals(0, copier.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(0, copier.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertEquals(0, copier.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertEquals(0, copier.getScopeFactory().createPackage("b").getOutboundDependencies().size());
    }
}
