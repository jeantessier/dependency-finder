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

public class TestClosureOutboundSelector {
    private final NodeFactory localFactory = new NodeFactory();

    private PackageNode a;
    private ClassNode a_A;
    private FeatureNode a_A_a;
    
    private PackageNode b;
    private ClassNode b_B;
    private FeatureNode b_B_b;
    
    private PackageNode c;
    private ClassNode c_C;
    private FeatureNode c_C_c;

    @BeforeEach
    void setUp() throws Exception {
        NodeFactory factory = new NodeFactory();

        a = factory.createPackage("a");
        a_A = factory.createClass("a.A");
        a_A_a = factory.createFeature("a.A.a");
        
        b = factory.createPackage("b");
        b_B = factory.createClass("b.B");
        b_B_b = factory.createFeature("b.B.b");
        
        c = factory.createPackage("c");
        c_C = factory.createClass("c.C");
        c_C_c = factory.createFeature("c.C.c");

        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);
    }

    @Test
    void testFactory() {
        var selector = new ClosureOutboundSelector();

        selector.setFactory(localFactory);

        assertEquals(localFactory, selector.getFactory(), "factory");
    }

    @Test
    void testCoverage() {
        Collection<Node> coverage = new ArrayList<>();

        var selector = new ClosureOutboundSelector();

        selector.setCoverage(coverage);

        assertEquals(coverage, selector.getCoverage(), "coverage");
    }

    @Test
    void testOneSelectedNode() {
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEquals(List.of(c_C_c), selector.getSelectedNodes());
    }

    @Test
    void testOneCopiedNode() {
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals(2, localFactory.getPackages().size(), "packages in scope");
        assertEquals(2, localFactory.getClasses().size(), "classes in scope" );
        assertEquals(2, localFactory.getFeatures().size(), "features in scope");

        assertEquals(b, localFactory.getPackages().get("b"), "package b in scope"    );
        assertEquals(b_B, localFactory.getClasses().get("b.B"), "class b.B in scope"    );
        assertEquals(b_B_b, localFactory.getFeatures().get("b.B.b"), "feature b.B.b in scope");
        assertEquals(c, localFactory.getPackages().get("c"), "package c in scope"    );
        assertEquals(c_C, localFactory.getClasses().get("c.C"), "class c.C in scope"    );
        assertEquals(c_C_c, localFactory.getFeatures().get("c.C.c"), "feature c.C.c in scope");

        assertNotSame(b, localFactory.getPackages().get("b"), "package b in scope");
        assertNotSame(b_B, localFactory.getClasses().get("b.B"), "class b.B in scope");
        assertNotSame(b_B_b, localFactory.getFeatures().get("b.B.b"), "feature b.B.b in scope");
        assertNotSame(c, localFactory.getPackages().get("c"), "package c in scope");
        assertNotSame(c_C, localFactory.getClasses().get("c.C"), "class c.C in scope");
        assertNotSame(c_C_c, localFactory.getFeatures().get("c.C.c"), "feature c.C.c in scope");

        assertIterableEquals(List.of(c_C_c), selector.getCopiedNodes());

        assertEquals(1, selector.getCopiedNodes().iterator().next().getInboundDependencies().size(), "c.C.c's inbounds");
        assertEquals(0, selector.getCopiedNodes().iterator().next().getOutboundDependencies().size(), "c.C.c's outbounds");
    }

    @Test
    void testThreeSelectedNodesFromPackage() {
        b.addDependency(c);
        b.addDependency(c_C);
        b.addDependency(c_C_c);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertIterableEqualsAnyOrder(List.of(c, c_C, c_C_c), selector.getSelectedNodes());
    }

    @Test
    void testThreeSelectedNodesFromClass() {
        b_B.addDependency(c);
        b_B.addDependency(c_C);
        b_B.addDependency(c_C_c);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertIterableEqualsAnyOrder(List.of(c, c_C, c_C_c), selector.getSelectedNodes());
    }

    @Test
    void testThreeSelectedNodesFromFeature() {
        b_B_b.addDependency(c);
        b_B_b.addDependency(c_C);
        b_B_b.addDependency(c_C_c);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(c, c_C, c_C_c), selector.getSelectedNodes());
    }

    @Test
    void testThreeCopiedNodesFromPackage() {
        b.addDependency(c);
        b.addDependency(c_C);
        b.addDependency(c_C_c);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertIterableEqualsAnyOrder(List.of(c, c_C, c_C_c), selector.getCopiedNodes());

        assertEquals(3, localFactory.createPackage("b").getOutboundDependencies().size(), "b's outbounds");
    }

    @Test
    void testThreeCopiedNodesFromClass() {
        b_B.addDependency(c);
        b_B.addDependency(c_C);
        b_B.addDependency(c_C_c);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertIterableEqualsAnyOrder(List.of(c, c_C, c_C_c), selector.getCopiedNodes());

        assertEquals(3, localFactory.createClass("b.B").getOutboundDependencies().size(), "b.B's outbounds");
    }

    @Test
    void testThreeCopiedNodesFromFeature() {
        b_B_b.addDependency(c);
        b_B_b.addDependency(c_C);
        b_B_b.addDependency(c_C_c);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(c, c_C, c_C_c), selector.getCopiedNodes());

        assertEquals(3, localFactory.createFeature("b.B.b").getOutboundDependencies().size(), "b.B.b's outbounds");
    }

    @Test
    void testTwoSelectedNodeWithPackageInCoverage() {
        b_B_b.addDependency(c);
        b_B_b.addDependency(c_C);
        b_B_b.addDependency(c_C_c);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.singleton(c));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(c_C, c_C_c), selector.getSelectedNodes());
    }

    @Test
    void testTwoSelectedNodeWithClassInCoverage() {
        b_B_b.addDependency(c);
        b_B_b.addDependency(c_C);
        b_B_b.addDependency(c_C_c);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.singleton(c_C));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(c, c_C_c), selector.getSelectedNodes());
    }

    @Test
    void testTwoSelectedNodeWithFeatureInCoverage() {
        b_B_b.addDependency(c);
        b_B_b.addDependency(c_C);
        b_B_b.addDependency(c_C_c);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.singleton(c_C_c));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(c, c_C), selector.getSelectedNodes());
    }

    @Test
    void testTwoCopiedNodeWithPackageInCoverage() {
        b_B_b.addDependency(c);
        b_B_b.addDependency(c_C);
        b_B_b.addDependency(c_C_c);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.singleton(c));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(c_C, c_C_c), selector.getCopiedNodes());

        assertEquals(2, localFactory.createFeature("b.B.b").getOutboundDependencies().size(), "b.B.b's outbounds");
    }

    @Test
    void testTwoCopiedNodeWithClassInCoverage() {
        b_B_b.addDependency(c);
        b_B_b.addDependency(c_C);
        b_B_b.addDependency(c_C_c);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.singleton(c_C));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(c, c_C_c), selector.getCopiedNodes());

        assertEquals(2, localFactory.createFeature("b.B.b").getOutboundDependencies().size(), "b.B.b's outbounds");
    }

    @Test
    void testTwoCopiedNodeWithFeatureInCoverage() {
        b_B_b.addDependency(c);
        b_B_b.addDependency(c_C);
        b_B_b.addDependency(c_C_c);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.singleton(c_C_c));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertIterableEqualsAnyOrder(List.of(c, c_C), selector.getCopiedNodes());

        assertEquals(2, localFactory.createFeature("b.B.b").getOutboundDependencies().size(), "b.B.b's outbounds");
    }

    @Test
    void testReset() {
        NodeFactory localFactory  = new NodeFactory();

        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals(1, selector.getSelectedNodes().size(), "nodes in selection");
        assertEquals(1, selector.getCopiedNodes().size(), "copied nodes");

        selector.reset();
        
        assertEquals(0, selector.getSelectedNodes().size(), "nodes in selection");
        assertEquals(0, selector.getCopiedNodes().size(), "copied nodes");
    }

    @Test
    void testVisitInferredPackage() {
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals(b.isConfirmed(), localFactory.getPackages().get(b.getName()).isConfirmed(), "package.isConfirmed()");
    }

    @Test
    void testVisitConfirmedPackage() {
        b.setConfirmed(true);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals(b.isConfirmed(), localFactory.getPackages().get(b.getName()).isConfirmed(), "package.isConfirmed()");
    }

    @Test
    void testFollowToInferredPackage() {
        b.addDependency(c);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals(c.isConfirmed(), localFactory.getPackages().get(c.getName()).isConfirmed(), "package.isConfirmed()");
    }

    @Test
    void testFollowToConfirmedPackage() {
        b.addDependency(c);
        c.setConfirmed(true);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals(c.isConfirmed(), localFactory.getPackages().get(c.getName()).isConfirmed(), "package.isConfirmed()");
    }

    @Test
    void testVisitInferredClass() {
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals(b_B.isConfirmed(), localFactory.getClasses().get(b_B.getName()).isConfirmed(), "class.isConfirmed()");
    }

    @Test
    void testVisitConfirmedClass() {
        b_B.setConfirmed(true);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals(b_B.isConfirmed(), localFactory.getClasses().get(b_B.getName()).isConfirmed(), "class.isConfirmed()");
    }

    @Test
    void testFollowToInferredClass() {
        b_B.addDependency(c_C);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals(c_C.isConfirmed(), localFactory.getClasses().get(c_C.getName()).isConfirmed(), "class.isConfirmed()");
    }

    @Test
    void testFollowToConfirmedClass() {
        b_B.addDependency(c_C);
        c_C.setConfirmed(true);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals(c_C.isConfirmed(), localFactory.getClasses().get(c_C.getName()).isConfirmed(), "class.isConfirmed()");
    }

    @Test
    void testVisitInferredFeature() {
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals(b_B_b.isConfirmed(), localFactory.getFeatures().get(b_B_b.getName()).isConfirmed(), "feature.isConfirmed()");
    }

    @Test
    void testVisitConfirmedFeature() {
        b_B_b.setConfirmed(true);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals(b_B_b.isConfirmed(), localFactory.getFeatures().get(b_B_b.getName()).isConfirmed(), "feature.isConfirmed()");
    }

    @Test
    void testFollowToInferredFeature() {
        b_B_b.addDependency(c_C_c);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals(c_C_c.isConfirmed(), localFactory.getFeatures().get(c_C_c.getName()).isConfirmed(), "feature.isConfirmed()");
    }

    @Test
    void testFollowToConfirmedFeature() {
        b_B_b.addDependency(c_C_c);
        c_C_c.setConfirmed(true);
        
        var selector = new ClosureOutboundSelector(localFactory, Collections.emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals(c_C_c.isConfirmed(), localFactory.getFeatures().get(c_C_c.getName()).isConfirmed(), "feature.isConfirmed()");
    }

    private static void assertIterableEqualsAnyOrder(Collection<Node> expectedNodes, Collection<Node> actualNodes) {
        assertIterableEquals(expectedNodes, actualNodes.stream().sorted().toList());
    }
}
