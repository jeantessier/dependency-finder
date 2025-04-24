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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestClosureStartSelector {
    private final NodeFactory factory = new NodeFactory();
    private final NodeFactory localFactory = new NodeFactory();
    private final RegularExpressionSelectionCriteria localCriteria = new RegularExpressionSelectionCriteria();

    private final Node a = factory.createPackage("a");
    private final Node a_A = factory.createClass("a.A");
    private final Node a_A_a = factory.createFeature("a.A.a");

    private final Node b = factory.createPackage("b");
    private final Node b_B = factory.createClass("b.B");
    private final Node b_B_b = factory.createFeature("b.B.b");

    private final Node c = factory.createPackage("c");
    private final Node c_C = factory.createClass("c.C");
    private final Node c_C_c = factory.createFeature("c.C.c");

    @BeforeEach
    void setUp() {
        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);
    }

    @Test
    void testOneSelectedNode() {
        localCriteria.setGlobalIncludes("/b.B.b/");

        ClosureStartSelector selector = new ClosureStartSelector(localFactory, localCriteria);
        selector.traverseNodes(factory.getPackages().values());

        assertEquals(1, selector.getSelectedNodes().size(), "nodes in selection");
        assertEquals(b_B_b, selector.getSelectedNodes().iterator().next(), "b.B.b in selection");
        assertSame(b_B_b, selector.getSelectedNodes().iterator().next(), "b.B.b in selection");
    }

    @Test
    void testOneCopiedNode() {
        localCriteria.setGlobalIncludes("/b.B.b/");

        ClosureStartSelector selector = new ClosureStartSelector(localFactory, localCriteria);
        selector.traverseNodes(factory.getPackages().values());

        assertEquals(1, localFactory.getPackages().size(), "packages in scope");
        assertEquals(1, localFactory.getClasses().size(), "classes in scope");
        assertEquals(1, localFactory.getFeatures().size(), "features in scope");

        assertEquals(b, localFactory.getPackages().get("b"), "package b in scope");
        assertEquals(b_B, localFactory.getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, localFactory.getFeatures().get("b.B.b"), "feature b.B.b in scope");

        assertNotSame(b, localFactory.getPackages().get("b"), "package b in scope");
        assertNotSame(b_B, localFactory.getClasses().get("b.B"), "class b.B in scope");
        assertNotSame(b_B_b, localFactory.getFeatures().get("b.B.b"), "feature b.B.b in scope");

        assertEquals(1, selector.getCopiedNodes().size(), "nodes in selection");
        assertEquals(b_B_b, selector.getCopiedNodes().iterator().next(), "b.B.b in selection");
        assertNotSame(b_B_b, selector.getCopiedNodes().iterator().next(), "b.B.b in selection");
        assertSame(localFactory.getFeatures().get("b.B.b"), selector.getCopiedNodes().iterator().next(), "b.B.b in selection");
    }

    @Test
    void testMultipleSelectedNodes() {
        localCriteria.setGlobalIncludes("/a.A.a/, /^b/");

        ClosureStartSelector selector = new ClosureStartSelector(localFactory, localCriteria);
        selector.traverseNodes(factory.getPackages().values());

        assertEquals(4, selector.getSelectedNodes().size(), "nodes in selection");
        assertTrue(selector.getSelectedNodes().contains(a_A_a), "a.A.a in selection");
        assertTrue(selector.getSelectedNodes().contains(b), "b in selection");
        assertTrue(selector.getSelectedNodes().contains(b_B), "b.B in selection");
        assertTrue(selector.getSelectedNodes().contains(b_B_b), "b.B.b in selection");
    }

    @Test
    void testMultipleCopiedNodes() {
        localCriteria.setGlobalIncludes("/a.A.a/, /^b/");

        ClosureStartSelector selector = new ClosureStartSelector(localFactory, localCriteria);
        selector.traverseNodes(factory.getPackages().values());

        assertEquals(2, localFactory.getPackages().size(), "packages in scope");
        assertEquals(2, localFactory.getClasses().size(), "classes in scope");
        assertEquals(2, localFactory.getFeatures().size(), "features in scope");

        assertEquals(a, localFactory.getPackages().get("a"), "package a in scope");
        assertEquals(a_A, localFactory.getClasses().get("a.A"), "class a.A in scope");
        assertEquals(a_A_a, localFactory.getFeatures().get("a.A.a"), "feature a.A.a in scope");
        assertEquals(b, localFactory.getPackages().get("b"), "package b in scope");
        assertEquals(b_B, localFactory.getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, localFactory.getFeatures().get("b.B.b"), "feature b.B.b in scope");

        assertNotSame(a, localFactory.getPackages().get("a"), "package a in scope");
        assertNotSame(a_A, localFactory.getClasses().get("a.A"), "class a.A in scope");
        assertNotSame(a_A_a, localFactory.getFeatures().get("a.A.a"), "feature a.A.a in scope");
        assertNotSame(b, localFactory.getPackages().get("b"), "package b in scope");
        assertNotSame(b_B, localFactory.getClasses().get("b.B"), "class b.B in scope");
        assertNotSame(b_B_b, localFactory.getFeatures().get("b.B.b"), "feature b.B.b in scope");

        assertEquals(4, selector.getCopiedNodes().size(), "nodes in selection");
        assertTrue(selector.getCopiedNodes().contains(a_A_a), "a.A.a in selection");
        assertTrue(selector.getCopiedNodes().contains(b), "b in selection");
        assertTrue(selector.getCopiedNodes().contains(b_B), "b.B in selection");
        assertTrue(selector.getCopiedNodes().contains(b_B_b), "b.B.b in selection");
    }

    @Test
    void testVisitInferredPackage() {
        ClosureStartSelector selector = new ClosureStartSelector(localFactory, new ComprehensiveSelectionCriteria());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals(b.isConfirmed(), localFactory.getPackages().get(b.getName()).isConfirmed(), "package.isConfirmed()");
    }

    @Test
    void testVisitConfirmedPackage() {
        b.setConfirmed(true);
        
        ClosureStartSelector selector = new ClosureStartSelector(localFactory, new ComprehensiveSelectionCriteria());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals(b.isConfirmed(), localFactory.getPackages().get(b.getName()).isConfirmed(), "package.isConfirmed()");
    }

    @Test
    void testVisitInferredClass() {
        ClosureStartSelector selector = new ClosureStartSelector(localFactory, new ComprehensiveSelectionCriteria());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals(b_B.isConfirmed(), localFactory.getClasses().get(b_B.getName()).isConfirmed(), "class.isConfirmed()");
    }

    @Test
    void testVisitConfirmedClass() {
        b_B.setConfirmed(true);
        
        ClosureStartSelector selector = new ClosureStartSelector(localFactory, new ComprehensiveSelectionCriteria());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals(b_B.isConfirmed(), localFactory.getClasses().get(b_B.getName()).isConfirmed(), "class.isConfirmed()");
    }

    @Test
    void testVisitInferredFeature() {
        ClosureStartSelector selector = new ClosureStartSelector(localFactory, new ComprehensiveSelectionCriteria());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals(b_B_b.isConfirmed(), localFactory.getFeatures().get(b_B_b.getName()).isConfirmed(), "feature.isConfirmed()");
    }

    @Test
    void testVisitConfirmedFeature() {
        b_B_b.setConfirmed(true);
        
        ClosureStartSelector selector = new ClosureStartSelector(localFactory, new ComprehensiveSelectionCriteria());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals(b_B_b.isConfirmed(), localFactory.getFeatures().get(b_B_b.getName()).isConfirmed(), "feature.isConfirmed()");
    }
}
