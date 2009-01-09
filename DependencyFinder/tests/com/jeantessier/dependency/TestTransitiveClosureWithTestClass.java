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

import java.util.*;

import junit.framework.*;

public class TestTransitiveClosureWithTestClass extends TestCase {
    private NodeFactory factory;

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

    private List<String> scopeIncludes;
    
    private RegularExpressionSelectionCriteria startCriteria;
    private RegularExpressionSelectionCriteria stopCriteria;

    private NodeFactory resultFactory;

    protected void setUp() throws Exception {
        super.setUp();
        
        factory = new NodeFactory();

        _package = factory.createPackage("");
        test_class = factory.createClass("test");
        test_main_method = factory.createFeature("test.main(String[])");
        test_Test_method = factory.createFeature("test.test()");

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

        scopeIncludes = new ArrayList<String>(1);
        scopeIncludes.add("/test/");
        
        startCriteria = new RegularExpressionSelectionCriteria();
        stopCriteria  = new RegularExpressionSelectionCriteria();
    }

    public void testCompleteClosure() {
        startCriteria.setGlobalIncludes(scopeIncludes);
        stopCriteria.setGlobalIncludes(Collections.<String>emptyList());
        
        compute(factory.getPackages().values());

        assertEquals("Different number of packages",
                     factory.getPackages().size(),
                     resultFactory.getPackages().size());
        assertEquals("Different number of classes",
                     factory.getClasses().size(),
                     resultFactory.getClasses().size());
        assertEquals("Different number of features",
                     factory.getFeatures().size(),
                     resultFactory.getFeatures().size());

        for (String key : resultFactory.getPackages().keySet()) {
            assertEquals(factory.getPackages().get(key), resultFactory.getPackages().get(key));
            assertTrue(factory.getPackages().get(key) != resultFactory.getPackages().get(key));
            assertEquals("Package " + key + " has different inbound count",
                         factory.getPackages().get(key).getInboundDependencies().size(),
                         resultFactory.getPackages().get(key).getInboundDependencies().size());
            assertEquals("Package " + key + " has different outbound count",
                         factory.getPackages().get(key).getOutboundDependencies().size(),
                         resultFactory.getPackages().get(key).getOutboundDependencies().size());
        }
        
        for (String key : resultFactory.getClasses().keySet()) {
            assertEquals(factory.getClasses().get(key), resultFactory.getClasses().get(key));
            assertTrue(factory.getClasses().get(key) != resultFactory.getClasses().get(key));
            assertEquals("Class " + key + " has different inbound count",
                         factory.getClasses().get(key).getInboundDependencies().size(),
                         resultFactory.getClasses().get(key).getInboundDependencies().size());
            assertEquals("Class " + key + " has different outbound count",
                         factory.getClasses().get(key).getOutboundDependencies().size(),
                         resultFactory.getClasses().get(key).getOutboundDependencies().size());
        }
        
        for (String key : resultFactory.getFeatures().keySet()) {
            assertEquals(factory.getFeatures().get(key), resultFactory.getFeatures().get(key));
            assertTrue(factory.getFeatures().get(key) != resultFactory.getFeatures().get(key));
            assertEquals("Feature " + key + " has different inbound count",
                         factory.getFeatures().get(key).getInboundDependencies().size(),
                         resultFactory.getFeatures().get(key).getInboundDependencies().size());
            assertEquals("Feature " + key + " has different outbound count",
                         factory.getFeatures().get(key).getOutboundDependencies().size(),
                         resultFactory.getFeatures().get(key).getOutboundDependencies().size());
        }
    }

    public void testCopyAllNodesOnly() {
        startCriteria.setGlobalIncludes(scopeIncludes);
        stopCriteria.setMatchingPackages(false);
        stopCriteria.setMatchingClasses(false);
        stopCriteria.setMatchingFeatures(false);
        stopCriteria.setGlobalIncludes("//");

        compute(factory.getPackages().values());

        assertEquals("Different number of packages",
                     1,
                     resultFactory.getPackages().size());
        assertEquals("Different number of classes",
                     1,
                     resultFactory.getClasses().size());
        assertEquals("Different number of features",
                     2,
                     resultFactory.getFeatures().size());

        for (String key : resultFactory.getPackages().keySet()) {
            assertEquals(factory.getPackages().get(key), resultFactory.getPackages().get(key));
            assertTrue(factory.getPackages().get(key) != resultFactory.getPackages().get(key));
            assertTrue(resultFactory.getPackages().get(key).getInboundDependencies().isEmpty());
            assertTrue(resultFactory.getPackages().get(key).getOutboundDependencies().isEmpty());
        }
        
        for (String key : resultFactory.getClasses().keySet()) {
            assertEquals(factory.getClasses().get(key), resultFactory.getClasses().get(key));
            assertTrue(factory.getClasses().get(key) != resultFactory.getClasses().get(key));
            assertTrue(resultFactory.getClasses().get(key).getInboundDependencies().isEmpty());
            assertTrue(resultFactory.getClasses().get(key).getOutboundDependencies().isEmpty());
        }
        
        for (String key : resultFactory.getFeatures().keySet()) {
            assertEquals(factory.getFeatures().get(key), resultFactory.getFeatures().get(key));
            assertTrue(factory.getFeatures().get(key) != resultFactory.getFeatures().get(key));
            assertTrue(resultFactory.getFeatures().get(key).getInboundDependencies().isEmpty());
            assertTrue(resultFactory.getFeatures().get(key).getOutboundDependencies().isEmpty());
        }
    }

    public void testCopyPackageNodesOnly() {
        startCriteria.setMatchingClasses(false);
        startCriteria.setMatchingFeatures(false);
        startCriteria.setGlobalIncludes(scopeIncludes);
        stopCriteria.setMatchingPackages(false);
        stopCriteria.setMatchingClasses(false);
        stopCriteria.setMatchingFeatures(false);
        stopCriteria.setGlobalIncludes("//");

        compute(factory.getPackages().values());

        assertEquals("Different number of packages",
                     1,
                     resultFactory.getPackages().size());
        assertTrue(resultFactory.getClasses().isEmpty());
        assertTrue(resultFactory.getFeatures().isEmpty());
    }

    public void testCopyClassNodesOnly() {
        startCriteria.setMatchingPackages(false);
        startCriteria.setMatchingFeatures(false);
        startCriteria.setGlobalIncludes(scopeIncludes);
        stopCriteria.setMatchingPackages(false);
        stopCriteria.setMatchingClasses(false);
        stopCriteria.setMatchingFeatures(false);
        stopCriteria.setGlobalIncludes("//");

        compute(factory.getPackages().values());

        assertEquals("Different number of packages",
                     1,
                     resultFactory.getPackages().size());
        assertEquals("Different number of classes",
                     1,
                     resultFactory.getClasses().size());
        assertTrue(resultFactory.getFeatures().isEmpty());

        for (String key : resultFactory.getPackages().keySet()) {
            assertEquals(factory.getPackages().get(key), resultFactory.getPackages().get(key));
            assertTrue(factory.getPackages().get(key) != resultFactory.getPackages().get(key));
            assertTrue(resultFactory.getPackages().get(key).getInboundDependencies().isEmpty());
            assertTrue(resultFactory.getPackages().get(key).getOutboundDependencies().isEmpty());
        }
        
        for (String key : resultFactory.getClasses().keySet()) {
            assertEquals(factory.getClasses().get(key), resultFactory.getClasses().get(key));
            assertTrue(factory.getClasses().get(key) != resultFactory.getClasses().get(key));
            assertTrue(resultFactory.getClasses().get(key).getInboundDependencies().isEmpty());
            assertTrue(resultFactory.getClasses().get(key).getOutboundDependencies().isEmpty());
        }
    }

    public void testCopyFeatureNodesOnly() {
        startCriteria.setMatchingPackages(false);
        startCriteria.setMatchingClasses(false);
        startCriteria.setGlobalIncludes(scopeIncludes);
        stopCriteria.setMatchingPackages(false);
        stopCriteria.setMatchingClasses(false);
        stopCriteria.setMatchingFeatures(false);
        stopCriteria.setGlobalIncludes("//");

        compute(factory.getPackages().values());

        assertEquals("Different number of packages",
                     1,
                     resultFactory.getPackages().size());
        assertEquals("Different number of classes",
                     1,
                     resultFactory.getClasses().size());
        assertEquals("Different number of features",
                     2,
                     resultFactory.getFeatures().size());

        for (String key : resultFactory.getPackages().keySet()) {
            assertEquals(factory.getPackages().get(key), resultFactory.getPackages().get(key));
            assertTrue(factory.getPackages().get(key) != resultFactory.getPackages().get(key));
            assertTrue(resultFactory.getPackages().get(key).getInboundDependencies().isEmpty());
            assertTrue(resultFactory.getPackages().get(key).getOutboundDependencies().isEmpty());
        }
        
        for (String key : resultFactory.getClasses().keySet()) {
            assertEquals(factory.getClasses().get(key), resultFactory.getClasses().get(key));
            assertTrue(factory.getClasses().get(key) != resultFactory.getClasses().get(key));
            assertTrue(resultFactory.getClasses().get(key).getInboundDependencies().isEmpty());
            assertTrue(resultFactory.getClasses().get(key).getOutboundDependencies().isEmpty());
        }
        
        for (String key : resultFactory.getFeatures().keySet()) {
            assertEquals(factory.getFeatures().get(key), resultFactory.getFeatures().get(key));
            assertTrue(factory.getFeatures().get(key) != resultFactory.getFeatures().get(key));
            assertTrue(resultFactory.getFeatures().get(key).getInboundDependencies().isEmpty());
            assertTrue(resultFactory.getFeatures().get(key).getOutboundDependencies().isEmpty());
        }
    }

    public void testCopyNothing() {
        startCriteria.setMatchingPackages(false);
        startCriteria.setMatchingClasses(false);
        startCriteria.setMatchingFeatures(false);
        
        compute(factory.getPackages().values());

        assertTrue(resultFactory.getPackages().isEmpty());
        assertTrue(resultFactory.getClasses().isEmpty());
        assertTrue(resultFactory.getFeatures().isEmpty());
    }

    private void compute(Collection<? extends Node> nodes) {
        RegularExpressionSelectionCriteria localStartCriteria = new RegularExpressionSelectionCriteria();
        localStartCriteria.setGlobalIncludes(startCriteria.getGlobalIncludes());
        RegularExpressionSelectionCriteria localStopCriteria  = new RegularExpressionSelectionCriteria();
        localStopCriteria.setGlobalIncludes(stopCriteria.getGlobalIncludes());

        TransitiveClosure closure = new TransitiveClosure(localStartCriteria, localStopCriteria);
        closure.traverseNodes(nodes);

        RegularExpressionSelectionCriteria localScopeCriteria  = new RegularExpressionSelectionCriteria();
        localScopeCriteria.setMatchingPackages(startCriteria.isMatchingPackages());
        localScopeCriteria.setMatchingClasses(startCriteria.isMatchingClasses());
        localScopeCriteria.setMatchingFeatures(startCriteria.isMatchingFeatures());
        localScopeCriteria.setGlobalIncludes("//");
        RegularExpressionSelectionCriteria localFilterCriteria = new RegularExpressionSelectionCriteria();
        localFilterCriteria.setMatchingPackages(stopCriteria.isMatchingPackages());
        localFilterCriteria.setMatchingClasses(stopCriteria.isMatchingClasses());
        localFilterCriteria.setMatchingFeatures(stopCriteria.isMatchingFeatures());
        localFilterCriteria.setGlobalIncludes("//");

        GraphSummarizer summarizer = new GraphSummarizer(localScopeCriteria, localFilterCriteria);
        summarizer.traverseNodes(closure.getFactory().getPackages().values());

        resultFactory = summarizer.getScopeFactory();
    }
}
