/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

public class TestGraphCopier {
    private final RegularExpressionSelectionCriteria scopeCriteria = new RegularExpressionSelectionCriteria("//");
    private final RegularExpressionSelectionCriteria filterCriteria = new RegularExpressionSelectionCriteria("//");
    private final NodeFactory factory = new NodeFactory();

    private final GraphCopier copier = new GraphCopier(new SelectiveTraversalStrategy(scopeCriteria, filterCriteria));

    @BeforeEach
    void setUp() {
        Node test_class = factory.createClass("test");
        Node test_main_method = factory.createFeature("test.main(String[])");
        Node test_Test_method = factory.createFeature("test.Test()");

        Node java_lang_Object_class = factory.createClass("java.lang.Object");
        Node java_lang_Object_Object_method = factory.createFeature("java.lang.Object.Object()");
        Node java_lang_String_class = factory.createClass("java.lang.String");

        Node java_util_Collections_singleton_method = factory.createFeature("java.util.Collections.singleton(java.lang.Object)");

        test_class.addDependency(java_lang_Object_class);
        test_main_method.addDependency(java_lang_Object_class);
        test_main_method.addDependency(java_lang_Object_Object_method);
        test_main_method.addDependency(java_lang_String_class);
        test_main_method.addDependency(java_util_Collections_singleton_method);
        test_Test_method.addDependency(java_lang_Object_Object_method);
    }

    @Test
    void testCopyFullGraph() {
        copier.traverseNodes(factory.getPackages().values());

        assertEquals(factory.getPackages().size(), copier.getScopeFactory().getPackages().size(), "Different number of packages");
        assertEquals(factory.getClasses().size(), copier.getScopeFactory().getClasses().size(), "Different number of classes");
        assertEquals(factory.getFeatures().size(), copier.getScopeFactory().getFeatures().size(), "Different number of features");

        factory.getPackages().keySet().forEach(key -> {
            assertEquals(factory.getPackages().get(key), copier.getScopeFactory().getPackages().get(key));
            assertNotSame(factory.getPackages().get(key), copier.getScopeFactory().getPackages().get(key));
            assertEquals(factory.getPackages().get(key).getInboundDependencies().size(),
                         copier.getScopeFactory().getPackages().get(key).getInboundDependencies().size());
            assertEquals(factory.getPackages().get(key).getOutboundDependencies().size(),
                         copier.getScopeFactory().getPackages().get(key).getOutboundDependencies().size());
        });

        factory.getClasses().keySet().forEach(key -> {
            assertEquals(factory.getClasses().get(key), copier.getScopeFactory().getClasses().get(key));
            assertNotSame(factory.getClasses().get(key), copier.getScopeFactory().getClasses().get(key));
            assertEquals(factory.getClasses().get(key).getInboundDependencies().size(),
                         copier.getScopeFactory().getClasses().get(key).getInboundDependencies().size());
            assertEquals(factory.getClasses().get(key).getOutboundDependencies().size(),
                         copier.getScopeFactory().getClasses().get(key).getOutboundDependencies().size());
        });

        factory.getFeatures().keySet().forEach(key -> {
            assertEquals(factory.getFeatures().get(key), copier.getScopeFactory().getFeatures().get(key));
            assertNotSame(factory.getFeatures().get(key), copier.getScopeFactory().getFeatures().get(key));
            assertEquals(factory.getFeatures().get(key).getInboundDependencies().size(),
                         copier.getScopeFactory().getFeatures().get(key).getInboundDependencies().size());
            assertEquals(factory.getFeatures().get(key).getOutboundDependencies().size(),
                         copier.getScopeFactory().getFeatures().get(key).getOutboundDependencies().size());
        });
    }

    @Test
    void testCopyAllNodesOnly() {
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);
        
        copier.traverseNodes(factory.getPackages().values());

        assertEquals(factory.getPackages().size(), copier.getScopeFactory().getPackages().size(), "Different number of packages");
        assertEquals(factory.getClasses().size(), copier.getScopeFactory().getClasses().size(), "Different number of classes");
        assertEquals(factory.getFeatures().size(), copier.getScopeFactory().getFeatures().size(), "Different number of features");

        factory.getPackages().keySet().forEach(key -> {
            assertEquals(factory.getPackages().get(key), copier.getScopeFactory().getPackages().get(key));
            assertNotSame(factory.getPackages().get(key), copier.getScopeFactory().getPackages().get(key));
            assertTrue(copier.getScopeFactory().getPackages().get(key).getInboundDependencies().isEmpty());
            assertTrue(copier.getScopeFactory().getPackages().get(key).getOutboundDependencies().isEmpty());
        });

        factory.getClasses().keySet().forEach(key -> {
            assertEquals(factory.getClasses().get(key), copier.getScopeFactory().getClasses().get(key));
            assertNotSame(factory.getClasses().get(key), copier.getScopeFactory().getClasses().get(key));
            assertTrue(copier.getScopeFactory().getClasses().get(key).getInboundDependencies().isEmpty());
            assertTrue(copier.getScopeFactory().getClasses().get(key).getOutboundDependencies().isEmpty());
        });

        factory.getFeatures().keySet().forEach(key -> {
            assertEquals(factory.getFeatures().get(key), copier.getScopeFactory().getFeatures().get(key));
            assertNotSame(factory.getFeatures().get(key), copier.getScopeFactory().getFeatures().get(key));
            assertTrue(copier.getScopeFactory().getFeatures().get(key).getInboundDependencies().isEmpty());
            assertTrue(copier.getScopeFactory().getFeatures().get(key).getOutboundDependencies().isEmpty());
        });
    }

    @Test
    void testCopyPackageNodesOnly() {
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);
        
        copier.traverseNodes(factory.getPackages().values());

        assertEquals(factory.getPackages().size(), copier.getScopeFactory().getPackages().size(), "Different number of packages");
        assertTrue(copier.getScopeFactory().getClasses().isEmpty());
        assertTrue(copier.getScopeFactory().getFeatures().isEmpty());

        factory.getPackages().keySet().forEach(key -> {
            assertEquals(factory.getPackages().get(key), copier.getScopeFactory().getPackages().get(key));
            assertNotSame(factory.getPackages().get(key), copier.getScopeFactory().getPackages().get(key));
            assertTrue(copier.getScopeFactory().getPackages().get(key).getInboundDependencies().isEmpty());
            assertTrue(copier.getScopeFactory().getPackages().get(key).getOutboundDependencies().isEmpty());
        });
    }

    @Test
    void testCopyClassNodesOnly() {
        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingFeatures(false);
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);
        
        copier.traverseNodes(factory.getPackages().values());

        assertEquals(factory.getPackages().size(), copier.getScopeFactory().getPackages().size(), "Different number of packages");
        assertEquals(factory.getClasses().size(), copier.getScopeFactory().getClasses().size(), "Different number of classes");
        assertTrue(copier.getScopeFactory().getFeatures().isEmpty());

        factory.getPackages().keySet().forEach(key -> {
            assertEquals(factory.getPackages().get(key), copier.getScopeFactory().getPackages().get(key));
            assertNotSame(factory.getPackages().get(key), copier.getScopeFactory().getPackages().get(key));
            assertTrue(copier.getScopeFactory().getPackages().get(key).getInboundDependencies().isEmpty());
            assertTrue(copier.getScopeFactory().getPackages().get(key).getOutboundDependencies().isEmpty());
        });
        
        factory.getClasses().keySet().forEach(key -> {
            assertEquals(factory.getClasses().get(key), copier.getScopeFactory().getClasses().get(key));
            assertNotSame(factory.getClasses().get(key), copier.getScopeFactory().getClasses().get(key));
            assertTrue(copier.getScopeFactory().getClasses().get(key).getInboundDependencies().isEmpty());
            assertTrue(copier.getScopeFactory().getClasses().get(key).getOutboundDependencies().isEmpty());
        });
    }

    @Test
    void testCopyFeatureNodesOnly() {
        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);
        
        copier.traverseNodes(factory.getPackages().values());

        assertEquals(factory.getPackages().size(), copier.getScopeFactory().getPackages().size(), "Different number of packages");
        assertEquals(3, copier.getScopeFactory().getClasses().size(), "Different number of classes");
        assertEquals(factory.getFeatures().size(), copier.getScopeFactory().getFeatures().size(), "Different number of features");

        copier.getScopeFactory().getPackages().keySet().forEach(key -> {
            assertEquals(factory.getPackages().get(key), copier.getScopeFactory().getPackages().get(key));
            assertNotSame(factory.getPackages().get(key), copier.getScopeFactory().getPackages().get(key));
            assertTrue(copier.getScopeFactory().getPackages().get(key).getInboundDependencies().isEmpty());
            assertTrue(copier.getScopeFactory().getPackages().get(key).getOutboundDependencies().isEmpty());
        });

        copier.getScopeFactory().getClasses().keySet().forEach(key -> {
            assertEquals(factory.getClasses().get(key), copier.getScopeFactory().getClasses().get(key));
            assertNotSame(factory.getClasses().get(key), copier.getScopeFactory().getClasses().get(key));
            assertTrue(copier.getScopeFactory().getClasses().get(key).getInboundDependencies().isEmpty());
            assertTrue(copier.getScopeFactory().getClasses().get(key).getOutboundDependencies().isEmpty());
        });

        copier.getScopeFactory().getFeatures().keySet().forEach(key -> {
            assertEquals(factory.getFeatures().get(key), copier.getScopeFactory().getFeatures().get(key));
            assertNotSame(factory.getFeatures().get(key), copier.getScopeFactory().getFeatures().get(key));
            assertTrue(copier.getScopeFactory().getFeatures().get(key).getInboundDependencies().isEmpty());
            assertTrue(copier.getScopeFactory().getFeatures().get(key).getOutboundDependencies().isEmpty());
        });
    }

    @Test
    void testCopyNothing() {
        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);
        
        copier.traverseNodes(factory.getPackages().values());

        assertTrue(copier.getScopeFactory().getPackages().isEmpty());
        assertTrue(copier.getScopeFactory().getClasses().isEmpty());
        assertTrue(copier.getScopeFactory().getFeatures().isEmpty());
    }

    @Test
    void testC2CasP2CSamePackage() {
        NodeFactory factory = new NodeFactory();

        Node a_A = factory.createClass("a.A");
        Node a_B = factory.createClass("a.B");
    
        a_A.addDependency(a_B);

        RegularExpressionSelectionCriteria scopeCriteria = new RegularExpressionSelectionCriteria();
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);
        scopeCriteria.setGlobalIncludes("//");

        RegularExpressionSelectionCriteria filterCriteria = new RegularExpressionSelectionCriteria();
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingFeatures(false);
        filterCriteria.setGlobalIncludes("//");
        
        GraphCopier copier = new GraphCopier(new SelectiveTraversalStrategy(scopeCriteria, filterCriteria));

        copier.traverseNodes(factory.getPackages().values());

        assertTrue(copier.getScopeFactory().getPackages().containsKey("a"), copier.getScopeFactory().getPackages().keySet().toString());
        assertTrue(copier.getScopeFactory().getClasses().isEmpty());
        assertTrue(copier.getScopeFactory().getFeatures().isEmpty());

        assertEquals(0, copier.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(0, copier.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertEquals(0, copier.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertEquals(0, copier.getScopeFactory().createPackage("b").getOutboundDependencies().size());
    }
}
