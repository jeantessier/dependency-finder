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

public class TestClosureInboundSelector {
    private final NodeFactory factory = new NodeFactory();

    private final Node a = factory.createPackage("a");
    private final Node a_A = factory.createClass("a.A");
    private final Node a_A_a = factory.createFeature("a.A.a");

    private final Node b = factory.createPackage("b");
    private final Node b_B = factory.createClass("b.B");
    private final Node b_B_b = factory.createFeature("b.B.b");

    private final Node c = factory.createPackage("c");
    private final Node c_C = factory.createClass("c.C");
    private final Node c_C_c = factory.createFeature("c.C.c");

    private final NodeFactory localFactory = new NodeFactory();

    @BeforeEach
    void setUp() {
        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);
    }

    @Test
    void testFactory() {
        var selector = new ClosureInboundSelector();

        selector.setFactory(localFactory);

        assertEquals(localFactory, selector.getFactory(), "factory");
    }

    @Test
    void testCoverage() {
        Collection<Node> coverage = new ArrayList<>();

        var selector = new ClosureInboundSelector();

        selector.setCoverage(coverage);

        assertEquals(coverage, selector.getCoverage(), "coverage");
    }
    
    @Test
    void testOneSelectedNode() {
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(a_A_a), selector.getSelectedNodes());
    }

    @Test
    void testOneCopiedNode() {
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

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

        assertIterableEqualsAnyOrder(List.of(a_A_a), selector.getCopiedNodes());

        assertEquals(0, selector.getCopiedNodes().iterator().next().getInboundDependencies().size(), "a.A.a's inbounds");
        assertEquals(1, selector.getCopiedNodes().iterator().next().getOutboundDependencies().size(), "a.A.a's outbounds");
    }

    @Test
    void testThreeSelectedNodesFromPackage() {
        a.addDependency(b);
        a_A.addDependency(b);
        a_A_a.addDependency(b);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertIterableEqualsAnyOrder(List.of(a, a_A, a_A_a), selector.getSelectedNodes());
    }

    @Test
    void testThreeSelectedNodesFromClass() {
        a.addDependency(b_B);
        a_A.addDependency(b_B);
        a_A_a.addDependency(b_B);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertIterableEqualsAnyOrder(List.of(a, a_A, a_A_a), selector.getSelectedNodes());
    }

    @Test
    void testThreeSelectedNodesFromFeature() {
        a.addDependency(b_B_b);
        a_A.addDependency(b_B_b);
        a_A_a.addDependency(b_B_b);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(a, a_A, a_A_a), selector.getSelectedNodes());
    }

    @Test
    void testThreeCopiedNodesFromPackage() {
        a.addDependency(b);
        a_A.addDependency(b);
        a_A_a.addDependency(b);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertIterableEqualsAnyOrder(List.of(a, a_A, a_A_a), selector.getCopiedNodes());

        assertEquals(3, localFactory.createPackage("b").getInboundDependencies().size(), "b's inbounds");
    }

    @Test
    void testThreeCopiedNodesFromClass() {
        a.addDependency(b_B);
        a_A.addDependency(b_B);
        a_A_a.addDependency(b_B);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertIterableEqualsAnyOrder(List.of(a, a_A, a_A_a), selector.getCopiedNodes());

        assertEquals(3, localFactory.createClass("b.B").getInboundDependencies().size(), "b.B's inbounds");
    }

    @Test
    void testThreeCopiedNodesFromFeature() {
        a.addDependency(b_B_b);
        a_A.addDependency(b_B_b);
        a_A_a.addDependency(b_B_b);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(a, a_A, a_A_a), selector.getCopiedNodes());

        assertEquals(3, localFactory.createFeature("b.B.b").getInboundDependencies().size(), "b.B.b's inbounds");
    }

    @Test
    void testTwoSelectedNodeWithPackageInCoverage() {
        a.addDependency(b_B_b);
        a_A.addDependency(b_B_b);
        a_A_a.addDependency(b_B_b);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.singleton(a));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(a_A, a_A_a), selector.getSelectedNodes());
    }

    @Test
    void testTwoSelectedNodeWithClassInCoverage() {
        a.addDependency(b_B_b);
        a_A.addDependency(b_B_b);
        a_A_a.addDependency(b_B_b);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.singleton(a_A));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(a, a_A_a), selector.getSelectedNodes());
    }

    @Test
    void testTwoSelectedNodeWithFeatureInCoverage() {
        a.addDependency(b_B_b);
        a_A.addDependency(b_B_b);
        a_A_a.addDependency(b_B_b);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.singleton(a_A_a));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(a, a_A), selector.getSelectedNodes());
    }

    @Test
    void testTwoCopiedNodeWithPackageInCoverage() {
        a.addDependency(b_B_b);
        a_A.addDependency(b_B_b);
        a_A_a.addDependency(b_B_b);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.singleton(a));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(a_A, a_A_a), selector.getCopiedNodes());

        assertEquals(2, localFactory.createFeature("b.B.b").getInboundDependencies().size(), "b.B.b's inbounds");
    }

    @Test
    void testTwoCopiedNodeWithClassInCoverage() {
        a.addDependency(b_B_b);
        a_A.addDependency(b_B_b);
        a_A_a.addDependency(b_B_b);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.singleton(a_A));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(a, a_A_a), selector.getCopiedNodes());

        assertEquals(2, localFactory.createFeature("b.B.b").getInboundDependencies().size(), "b.B.b's inbounds");
    }

    @Test
    void testTwoCopiedNodeWithFeatureInCoverage() {
        a.addDependency(b_B_b);
        a_A.addDependency(b_B_b);
        a_A_a.addDependency(b_B_b);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.singleton(a_A_a));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(a, a_A), selector.getCopiedNodes());

        assertEquals(2, localFactory.createFeature("b.B.b").getInboundDependencies().size(), "b.B.b's inbounds");
    }

    @Test
    void testReset() {
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals(1, selector.getSelectedNodes().size(), "nodes in selection");
        assertEquals(1, selector.getCopiedNodes().size(), "copied nodes");

        selector.reset();
        
        assertEquals(0, selector.getSelectedNodes().size(), "nodes in selection");
        assertEquals(0, selector.getCopiedNodes().size(), "copied nodes");
    }

    @Test
    void testVisitInferredPackage() {
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals(b.isConfirmed(), localFactory.getPackages().get(b.getName()).isConfirmed(), "package.isConfirmed()");
    }

    @Test
    void testVisitConfirmedPackage() {
        b.setConfirmed(true);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals(b.isConfirmed(), localFactory.getPackages().get(b.getName()).isConfirmed(), "package.isConfirmed()");
    }

    @Test
    void testFollowToInferredPackage() {
        a.addDependency(b);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals(a.isConfirmed(), localFactory.getPackages().get(a.getName()).isConfirmed(), "package.isConfirmed()");
    }

    @Test
    void testFollowToConfirmedPackage() {
        a.addDependency(b);
        a.setConfirmed(true);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals(a.isConfirmed(), localFactory.getPackages().get(a.getName()).isConfirmed(), "package.isConfirmed()");
    }

    @Test
    void testVisitInferredClass() {
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals(b_B.isConfirmed(), localFactory.getClasses().get(b_B.getName()).isConfirmed(), "class.isConfirmed()");
    }

    @Test
    void testVisitConfirmedClass() {
        b_B.setConfirmed(true);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals(b_B.isConfirmed(), localFactory.getClasses().get(b_B.getName()).isConfirmed(), "class.isConfirmed()");
    }

    @Test
    void testFollowToInferredClass() {
        a_A.addDependency(b_B);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals(a_A.isConfirmed(), localFactory.getClasses().get(a_A.getName()).isConfirmed(), "class.isConfirmed()");
    }

    @Test
    void testFollowToConfirmedClass() {
        a_A.addDependency(b_B);
        a_A.setConfirmed(true);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals(a_A.isConfirmed(), localFactory.getClasses().get(a_A.getName()).isConfirmed(), "class.isConfirmed()");
    }

    @Test
    void testVisitInferredFeature() {
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals(b_B_b.isConfirmed(), localFactory.getFeatures().get(b_B_b.getName()).isConfirmed(), "feature.isConfirmed()");
    }

    @Test
    void testVisitConfirmedFeature() {
        b_B_b.setConfirmed(true);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals(b_B_b.isConfirmed(), localFactory.getFeatures().get(b_B_b.getName()).isConfirmed(), "feature.isConfirmed()");
    }

    @Test
    void testFollowToInferredFeature() {
        a_A_a.addDependency(b_B_b);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals(a_A_a.isConfirmed(), localFactory.getFeatures().get(a_A_a.getName()).isConfirmed(), "feature.isConfirmed()");
    }

    @Test
    void testFollowToConfirmedFeature() {
        a_A_a.addDependency(b_B_b);
        a_A_a.setConfirmed(true);
        
        var selector = new ClosureInboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals(a_A_a.isConfirmed(), localFactory.getFeatures().get(a_A_a.getName()).isConfirmed(), "feature.isConfirmed()");
    }

    private static void assertIterableEqualsAnyOrder(Collection<Node> expectedNodes, Collection<Node> actualNodes) {
        assertIterableEquals(expectedNodes, actualNodes.stream().sorted().toList());
    }
}
