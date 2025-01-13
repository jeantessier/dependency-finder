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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestTransitiveClosureWithTestClass {
    private final NodeFactory factory = new NodeFactory();

    private final Node _package = factory.createPackage("");
    private final Node test_class = factory.createClass("test");
    private final Node test_main_method = factory.createFeature("test.main(String[])");
    private final Node test_Test_method = factory.createFeature("test.test()");

    private final Node java_lang_package = factory.createPackage("java.lang");
    private final Node java_lang_Object_class = factory.createClass("java.lang.Object");
    private final Node java_lang_Object_Object_method = factory.createFeature("java.lang.Object.Object()");
    private final Node java_lang_String_class = factory.createClass("java.lang.String");

    private final Node java_util_package = factory.createPackage("java.util");
    private final Node java_util_Collections_class = factory.createClass("java.util.Collections");
    private final Node java_util_Collections_singleton_method = factory.createFeature("java.util.Collections.singleton(java.lang.Object)");

    private final List<String> scopeIncludes = List.of("/test/");
    
    private final RegularExpressionSelectionCriteria startCriteria = new RegularExpressionSelectionCriteria();
    private final RegularExpressionSelectionCriteria stopCriteria = new RegularExpressionSelectionCriteria();

    @BeforeEach
    void setUp() {
        test_class.addDependency(java_lang_Object_class);
        test_main_method.addDependency(java_lang_Object_class);
        test_main_method.addDependency(java_lang_Object_Object_method);
        test_main_method.addDependency(java_lang_String_class);
        test_main_method.addDependency(java_util_Collections_singleton_method);
        test_Test_method.addDependency(java_lang_Object_Object_method);
    }

    @Test
    void testCompleteClosure() {
        startCriteria.setGlobalIncludes(scopeIncludes);
        stopCriteria.setGlobalIncludes(Collections.emptyList());

        var resultFactory = compute(factory.getPackages().values());

        assertEquals(factory.getPackages().size(), resultFactory.getPackages().size(), "Different number of packages");
        assertEquals(factory.getClasses().size(), resultFactory.getClasses().size(), "Different number of classes");
        assertEquals(factory.getFeatures().size(), resultFactory.getFeatures().size(), "Different number of features");

        resultFactory.getPackages().keySet().forEach(key -> {
            assertEquals(factory.getPackages().get(key), resultFactory.getPackages().get(key));
            assertNotSame(factory.getPackages().get(key), resultFactory.getPackages().get(key));
            assertEquals(factory.getPackages().get(key).getInboundDependencies().size(), resultFactory.getPackages().get(key).getInboundDependencies().size(), "Package " + key + " has different inbound count");
            assertEquals(factory.getPackages().get(key).getOutboundDependencies().size(), resultFactory.getPackages().get(key).getOutboundDependencies().size(), "Package " + key + " has different outbound count");
        });
        
        resultFactory.getClasses().keySet().forEach(key -> {
            assertEquals(factory.getClasses().get(key), resultFactory.getClasses().get(key));
            assertNotSame(factory.getClasses().get(key), resultFactory.getClasses().get(key));
            assertEquals(factory.getClasses().get(key).getInboundDependencies().size(), resultFactory.getClasses().get(key).getInboundDependencies().size(), "Class " + key + " has different inbound count");
            assertEquals(factory.getClasses().get(key).getOutboundDependencies().size(), resultFactory.getClasses().get(key).getOutboundDependencies().size(), "Class " + key + " has different outbound count");
        });
        
        resultFactory.getFeatures().keySet().forEach(key -> {
            assertEquals(factory.getFeatures().get(key), resultFactory.getFeatures().get(key));
            assertNotSame(factory.getFeatures().get(key), resultFactory.getFeatures().get(key));
            assertEquals(factory.getFeatures().get(key).getInboundDependencies().size(), resultFactory.getFeatures().get(key).getInboundDependencies().size(), "Feature " + key + " has different inbound count");
            assertEquals(factory.getFeatures().get(key).getOutboundDependencies().size(), resultFactory.getFeatures().get(key).getOutboundDependencies().size(), "Feature " + key + " has different outbound count");
        });
    }

    @Test
    void testCopyAllNodesOnly() {
        startCriteria.setGlobalIncludes(scopeIncludes);
        stopCriteria.setMatchingPackages(false);
        stopCriteria.setMatchingClasses(false);
        stopCriteria.setMatchingFeatures(false);
        stopCriteria.setGlobalIncludes("//");

        var resultFactory = compute(factory.getPackages().values());

        assertEquals(1, resultFactory.getPackages().size(), "Different number of packages");
        assertEquals(1, resultFactory.getClasses().size(), "Different number of classes");
        assertEquals(2, resultFactory.getFeatures().size(), "Different number of features");

        resultFactory.getPackages().keySet().forEach(key -> {
            assertEquals(factory.getPackages().get(key), resultFactory.getPackages().get(key));
            assertNotSame(factory.getPackages().get(key), resultFactory.getPackages().get(key));
            assertTrue(resultFactory.getPackages().get(key).getInboundDependencies().isEmpty());
            assertTrue(resultFactory.getPackages().get(key).getOutboundDependencies().isEmpty());
        });
        
        resultFactory.getClasses().keySet().forEach(key -> {
            assertEquals(factory.getClasses().get(key), resultFactory.getClasses().get(key));
            assertNotSame(factory.getClasses().get(key), resultFactory.getClasses().get(key));
            assertTrue(resultFactory.getClasses().get(key).getInboundDependencies().isEmpty());
            assertTrue(resultFactory.getClasses().get(key).getOutboundDependencies().isEmpty());
        });
        
        resultFactory.getFeatures().keySet().forEach(key -> {
            assertEquals(factory.getFeatures().get(key), resultFactory.getFeatures().get(key));
            assertNotSame(factory.getFeatures().get(key), resultFactory.getFeatures().get(key));
            assertTrue(resultFactory.getFeatures().get(key).getInboundDependencies().isEmpty());
            assertTrue(resultFactory.getFeatures().get(key).getOutboundDependencies().isEmpty());
        });
    }

    @Test
    void testCopyPackageNodesOnly() {
        startCriteria.setMatchingClasses(false);
        startCriteria.setMatchingFeatures(false);
        startCriteria.setGlobalIncludes(scopeIncludes);
        stopCriteria.setMatchingPackages(false);
        stopCriteria.setMatchingClasses(false);
        stopCriteria.setMatchingFeatures(false);
        stopCriteria.setGlobalIncludes("//");

        var resultFactory = compute(factory.getPackages().values());

        assertEquals(1, resultFactory.getPackages().size(), "Different number of packages");
        assertTrue(resultFactory.getClasses().isEmpty());
        assertTrue(resultFactory.getFeatures().isEmpty());
    }

    @Test
    void testCopyClassNodesOnly() {
        startCriteria.setMatchingPackages(false);
        startCriteria.setMatchingFeatures(false);
        startCriteria.setGlobalIncludes(scopeIncludes);
        stopCriteria.setMatchingPackages(false);
        stopCriteria.setMatchingClasses(false);
        stopCriteria.setMatchingFeatures(false);
        stopCriteria.setGlobalIncludes("//");

        var resultFactory = compute(factory.getPackages().values());

        assertEquals(1, resultFactory.getPackages().size(), "Different number of packages");
        assertEquals(1, resultFactory.getClasses().size(), "Different number of classes");
        assertTrue(resultFactory.getFeatures().isEmpty());

        resultFactory.getPackages().keySet().forEach(key -> {
            assertEquals(factory.getPackages().get(key), resultFactory.getPackages().get(key));
            assertNotSame(factory.getPackages().get(key), resultFactory.getPackages().get(key));
            assertTrue(resultFactory.getPackages().get(key).getInboundDependencies().isEmpty());
            assertTrue(resultFactory.getPackages().get(key).getOutboundDependencies().isEmpty());
        });
        
        resultFactory.getClasses().keySet().forEach(key -> {
            assertEquals(factory.getClasses().get(key), resultFactory.getClasses().get(key));
            assertNotSame(factory.getClasses().get(key), resultFactory.getClasses().get(key));
            assertTrue(resultFactory.getClasses().get(key).getInboundDependencies().isEmpty());
            assertTrue(resultFactory.getClasses().get(key).getOutboundDependencies().isEmpty());
        });
    }

    @Test
    void testCopyFeatureNodesOnly() {
        startCriteria.setMatchingPackages(false);
        startCriteria.setMatchingClasses(false);
        startCriteria.setGlobalIncludes(scopeIncludes);
        stopCriteria.setMatchingPackages(false);
        stopCriteria.setMatchingClasses(false);
        stopCriteria.setMatchingFeatures(false);
        stopCriteria.setGlobalIncludes("//");

        var resultFactory = compute(factory.getPackages().values());

        assertEquals(1, resultFactory.getPackages().size(), "Different number of packages");
        assertEquals(1, resultFactory.getClasses().size(), "Different number of classes");
        assertEquals(2, resultFactory.getFeatures().size(), "Different number of features");

        resultFactory.getPackages().keySet().forEach(key -> {
            assertEquals(factory.getPackages().get(key), resultFactory.getPackages().get(key));
            assertNotSame(factory.getPackages().get(key), resultFactory.getPackages().get(key));
            assertTrue(resultFactory.getPackages().get(key).getInboundDependencies().isEmpty());
            assertTrue(resultFactory.getPackages().get(key).getOutboundDependencies().isEmpty());
        });
        
        resultFactory.getClasses().keySet().forEach(key -> {
            assertEquals(factory.getClasses().get(key), resultFactory.getClasses().get(key));
            assertNotSame(factory.getClasses().get(key), resultFactory.getClasses().get(key));
            assertTrue(resultFactory.getClasses().get(key).getInboundDependencies().isEmpty());
            assertTrue(resultFactory.getClasses().get(key).getOutboundDependencies().isEmpty());
        });
        
        resultFactory.getFeatures().keySet().forEach(key -> {
            assertEquals(factory.getFeatures().get(key), resultFactory.getFeatures().get(key));
            assertNotSame(factory.getFeatures().get(key), resultFactory.getFeatures().get(key));
            assertTrue(resultFactory.getFeatures().get(key).getInboundDependencies().isEmpty());
            assertTrue(resultFactory.getFeatures().get(key).getOutboundDependencies().isEmpty());
        });
    }

    @Test
    void testCopyNothing() {
        startCriteria.setMatchingPackages(false);
        startCriteria.setMatchingClasses(false);
        startCriteria.setMatchingFeatures(false);
        
        var resultFactory = compute(factory.getPackages().values());

        assertTrue(resultFactory.getPackages().isEmpty());
        assertTrue(resultFactory.getClasses().isEmpty());
        assertTrue(resultFactory.getFeatures().isEmpty());
    }

    private NodeFactory compute(Collection<? extends Node> nodes) {
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

        return summarizer.getScopeFactory();
    }
}
