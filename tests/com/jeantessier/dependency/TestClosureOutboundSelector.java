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

public class TestClosureOutboundSelector extends TestCase {
    private NodeFactory localFactory;

    private PackageNode a;
    private ClassNode a_A;
    private FeatureNode a_A_a;
    
    private PackageNode b;
    private ClassNode b_B;
    private FeatureNode b_B_b;
    
    private PackageNode c;
    private ClassNode c_C;
    private FeatureNode c_C_c;

    protected void setUp() throws Exception {
        super.setUp();
        
        localFactory = new NodeFactory();

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

    public void testFactory() {
        ClosureOutboundSelector selector = new ClosureOutboundSelector();

        selector.setFactory(localFactory);

        assertEquals("factory", localFactory, selector.getFactory());
    }

    public void testCoverage() {
        Collection<Node> coverage = new ArrayList<Node>();

        ClosureOutboundSelector selector = new ClosureOutboundSelector();

        selector.setCoverage(coverage);

        assertEquals("coverage", coverage, selector.getCoverage());
    }

    public void testOneSelectedNode() {
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("nodes in selection", 1, selector.getSelectedNodes().size());
        assertEquals("c.C.c in selection", c_C_c, selector.getSelectedNodes().iterator().next());
        assertSame("c.C.c in selection", c_C_c, selector.getSelectedNodes().iterator().next());
    }

    public void testOneCopiedNode() {
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("packages in scope", 2, localFactory.getPackages().size());
        assertEquals("classes in scope" , 2, localFactory.getClasses().size());
        assertEquals("features in scope", 2, localFactory.getFeatures().size());

        assertEquals("package b in scope"    , b,     localFactory.getPackages().get("b"));
        assertEquals("class b.B in scope"    , b_B,   localFactory.getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, localFactory.getFeatures().get("b.B.b"));
        assertEquals("package c in scope"    , c,     localFactory.getPackages().get("c"));
        assertEquals("class c.C in scope"    , c_C,   localFactory.getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, localFactory.getFeatures().get("c.C.c"));

        assertNotSame("package b in scope"    , b,     localFactory.getPackages().get("b"));
        assertNotSame("class b.B in scope"    , b_B,   localFactory.getClasses().get("b.B"));
        assertNotSame("feature b.B.b in scope", b_B_b, localFactory.getFeatures().get("b.B.b"));
        assertNotSame("package c in scope"    , c,     localFactory.getPackages().get("c"));
        assertNotSame("class c.C in scope"    , c_C,   localFactory.getClasses().get("c.C"));
        assertNotSame("feature c.C.c in scope", c_C_c, localFactory.getFeatures().get("c.C.c"));

        assertEquals("nodes in selection", 1, selector.getCopiedNodes().size());
        assertEquals("c.C.c in selection", c_C_c, selector.getCopiedNodes().iterator().next());
        assertNotSame("c.C.c in selection", c_C_c, selector.getCopiedNodes().iterator().next());
        assertSame("c.C.c in selection", localFactory.getFeatures().get("c.C.c"), selector.getCopiedNodes().iterator().next());
        assertEquals("c.C.c's inbounds",  1, selector.getCopiedNodes().iterator().next().getInboundDependencies().size());
        assertEquals("c.C.c's outbounds", 0, selector.getCopiedNodes().iterator().next().getOutboundDependencies().size());
    }

    public void testThreeSelectedNodesFromPackage() {
        b.addDependency(c);
        b.addDependency(c_C);
        b.addDependency(c_C_c);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals("nodes in selection", 3, selector.getSelectedNodes().size());
        assertTrue("c in selection",     selector.getSelectedNodes().contains(c));
        assertTrue("c.C in selection",   selector.getSelectedNodes().contains(c_C));
        assertTrue("c.C.c in selection", selector.getSelectedNodes().contains(c_C_c));
    }

    public void testThreeSelectedNodesFromClass() {
        b_B.addDependency(c);
        b_B.addDependency(c_C);
        b_B.addDependency(c_C_c);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals("nodes in selection", 3, selector.getSelectedNodes().size());
        assertTrue("c in selection",     selector.getSelectedNodes().contains(c));
        assertTrue("c.C in selection",   selector.getSelectedNodes().contains(c_C));
        assertTrue("c.C.c in selection", selector.getSelectedNodes().contains(c_C_c));
    }

    public void testThreeSelectedNodesFromFeature() {
        b_B_b.addDependency(c);
        b_B_b.addDependency(c_C);
        b_B_b.addDependency(c_C_c);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("nodes in selection", 3, selector.getSelectedNodes().size());
        assertTrue("c in selection",     selector.getSelectedNodes().contains(c));
        assertTrue("c.C in selection",   selector.getSelectedNodes().contains(c_C));
        assertTrue("c.C.c in selection", selector.getSelectedNodes().contains(c_C_c));
    }

    public void testThreeCopiedNodesFromPackage() {
        b.addDependency(c);
        b.addDependency(c_C);
        b.addDependency(c_C_c);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals("nodes in selection", 3, selector.getCopiedNodes().size());
        assertTrue("c in selection",     selector.getCopiedNodes().contains(c));
        assertTrue("c.C in selection",   selector.getCopiedNodes().contains(c_C));
        assertTrue("c.C.c in selection", selector.getCopiedNodes().contains(c_C_c));

        assertEquals("b's outbounds", 3, localFactory.createPackage("b").getOutboundDependencies().size());
    }

    public void testThreeCopiedNodesFromClass() {
        b_B.addDependency(c);
        b_B.addDependency(c_C);
        b_B.addDependency(c_C_c);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals("nodes in selection", 3, selector.getCopiedNodes().size());
        assertTrue("c in selection",     selector.getCopiedNodes().contains(c));
        assertTrue("c.C in selection",   selector.getCopiedNodes().contains(c_C));
        assertTrue("c.C.c in selection", selector.getCopiedNodes().contains(c_C_c));

        assertEquals("b.B's outbounds", 3, localFactory.createClass("b.B").getOutboundDependencies().size());
    }

    public void testThreeCopiedNodesFromFeature() {
        b_B_b.addDependency(c);
        b_B_b.addDependency(c_C);
        b_B_b.addDependency(c_C_c);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("nodes in selection", 3, selector.getCopiedNodes().size());
        assertTrue("c in selection",     selector.getCopiedNodes().contains(c));
        assertTrue("c.C in selection",   selector.getCopiedNodes().contains(c_C));
        assertTrue("c.C.c in selection", selector.getCopiedNodes().contains(c_C_c));

        assertEquals("b.B.b's outbounds", 3, localFactory.createFeature("b.B.b").getOutboundDependencies().size());
    }

    public void testTwoSelectedNodeWithPackageInCoverage() {
        b_B_b.addDependency(c);
        b_B_b.addDependency(c_C);
        b_B_b.addDependency(c_C_c);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.singleton(c));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("nodes in selection", 2, selector.getSelectedNodes().size());
        assertTrue("c.C in selection",   selector.getSelectedNodes().contains(c_C));
        assertTrue("c.C.c in selection", selector.getSelectedNodes().contains(c_C_c));
    }

    public void testTwoSelectedNodeWithClassInCoverage() {
        b_B_b.addDependency(c);
        b_B_b.addDependency(c_C);
        b_B_b.addDependency(c_C_c);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.singleton(c_C));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("nodes in selection", 2, selector.getSelectedNodes().size());
        assertTrue("c in selection",     selector.getSelectedNodes().contains(c));
        assertTrue("c.C.c in selection", selector.getSelectedNodes().contains(c_C_c));
    }

    public void testTwoSelectedNodeWithFeatureInCoverage() {
        b_B_b.addDependency(c);
        b_B_b.addDependency(c_C);
        b_B_b.addDependency(c_C_c);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.singleton(c_C_c));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("nodes in selection", 2, selector.getSelectedNodes().size());
        assertTrue("c in selection",   selector.getSelectedNodes().contains(c));
        assertTrue("c.C in selection", selector.getSelectedNodes().contains(c_C));
    }

    public void testTwoCopiedNodeWithPackageInCoverage() {
        b_B_b.addDependency(c);
        b_B_b.addDependency(c_C);
        b_B_b.addDependency(c_C_c);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.singleton(c));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("nodes in selection", 2, selector.getCopiedNodes().size());
        assertTrue("c.C in selection",   selector.getCopiedNodes().contains(c_C));
        assertTrue("c.C.c in selection", selector.getCopiedNodes().contains(c_C_c));

        assertEquals("b.B.b's outbounds", 2, localFactory.createFeature("b.B.b").getOutboundDependencies().size());
    }

    public void testTwoCopiedNodeWithClassInCoverage() {
        b_B_b.addDependency(c);
        b_B_b.addDependency(c_C);
        b_B_b.addDependency(c_C_c);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.singleton(c_C));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("nodes in selection", 2, selector.getCopiedNodes().size());
        assertTrue("c in selection",     selector.getCopiedNodes().contains(c));
        assertTrue("c.C.c in selection", selector.getCopiedNodes().contains(c_C_c));

        assertEquals("b.B.b's outbounds", 2, localFactory.createFeature("b.B.b").getOutboundDependencies().size());
    }

    public void testTwoCopiedNodeWithFeatureInCoverage() {
        b_B_b.addDependency(c);
        b_B_b.addDependency(c_C);
        b_B_b.addDependency(c_C_c);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.singleton(c_C_c));
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("nodes in selection", 2, selector.getCopiedNodes().size());
        assertTrue("c in selection",   selector.getCopiedNodes().contains(c));
        assertTrue("c.C in selection", selector.getCopiedNodes().contains(c_C));

        assertEquals("b.B.b's outbounds", 2, localFactory.createFeature("b.B.b").getOutboundDependencies().size());
    }

    public void testReset() {
        NodeFactory localFactory  = new NodeFactory();

        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("nodes in selection", 1, selector.getSelectedNodes().size());
        assertEquals("copied nodes",       1, selector.getCopiedNodes().size());

        selector.reset();
        
        assertEquals("nodes in selection", 0, selector.getSelectedNodes().size());
        assertEquals("copied nodes",       0, selector.getCopiedNodes().size());
    }

    public void testVisitInferredPackage() {
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals("package.isConfirmed()", b.isConfirmed(), localFactory.getPackages().get(b.getName()).isConfirmed());
    }

    public void testVisitConfirmedPackage() {
        b.setConfirmed(true);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals("package.isConfirmed()", b.isConfirmed(), localFactory.getPackages().get(b.getName()).isConfirmed());
    }

    public void testFollowToInferredPackage() {
        b.addDependency(c);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals("package.isConfirmed()", c.isConfirmed(), localFactory.getPackages().get(c.getName()).isConfirmed());
    }

    public void testFollowToConfirmedPackage() {
        b.addDependency(c);
        c.setConfirmed(true);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals("package.isConfirmed()", c.isConfirmed(), localFactory.getPackages().get(c.getName()).isConfirmed());
    }

    public void testVisitInferredClass() {
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals("class.isConfirmed()", b_B.isConfirmed(), localFactory.getClasses().get(b_B.getName()).isConfirmed());
    }

    public void testVisitConfirmedClass() {
        b_B.setConfirmed(true);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals("class.isConfirmed()", b_B.isConfirmed(), localFactory.getClasses().get(b_B.getName()).isConfirmed());
    }

    public void testFollowToInferredClass() {
        b_B.addDependency(c_C);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals("class.isConfirmed()", c_C.isConfirmed(), localFactory.getClasses().get(c_C.getName()).isConfirmed());
    }

    public void testFollowToConfirmedClass() {
        b_B.addDependency(c_C);
        c_C.setConfirmed(true);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals("class.isConfirmed()", c_C.isConfirmed(), localFactory.getClasses().get(c_C.getName()).isConfirmed());
    }

    public void testVisitInferredFeature() {
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("feature.isConfirmed()", b_B_b.isConfirmed(), localFactory.getFeatures().get(b_B_b.getName()).isConfirmed());
    }

    public void testVisitConfirmedFeature() {
        b_B_b.setConfirmed(true);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("feature.isConfirmed()", b_B_b.isConfirmed(), localFactory.getFeatures().get(b_B_b.getName()).isConfirmed());
    }

    public void testFollowToInferredFeature() {
        b_B_b.addDependency(c_C_c);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("feature.isConfirmed()", c_C_c.isConfirmed(), localFactory.getFeatures().get(c_C_c.getName()).isConfirmed());
    }

    public void testFollowToConfirmedFeature() {
        b_B_b.addDependency(c_C_c);
        c_C_c.setConfirmed(true);
        
        ClosureOutboundSelector selector = new ClosureOutboundSelector(localFactory, Collections.<Node>emptySet());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("feature.isConfirmed()", c_C_c.isConfirmed(), localFactory.getFeatures().get(c_C_c.getName()).isConfirmed());
    }
}
