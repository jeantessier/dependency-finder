/*
 *  Copyright (c) 2001-2003, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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

import org.apache.oro.text.perl.*;

public class TestClosureInboundSelector extends TestCase {
	private NodeFactory factory;

	private PackageNode a;
	private ClassNode   a_A;
	private FeatureNode a_A_a;
	
	private PackageNode b;
	private ClassNode   b_B;
	private FeatureNode b_B_b;
	
	private PackageNode c;
	private ClassNode   c_C;
	private FeatureNode c_C_c;

	protected void setUp() throws Exception {
		factory = new NodeFactory();

		a     = factory.CreatePackage("a");
		a_A   = factory.CreateClass("a.A");
		a_A_a = factory.CreateFeature("a.A.a");
		
		b     = factory.CreatePackage("b");
		b_B   = factory.CreateClass("b.B");
		b_B_b = factory.CreateFeature("b.B.b");
		
		c     = factory.CreatePackage("c");
		c_C   = factory.CreateClass("c.C");
		c_C_c = factory.CreateFeature("c.C.c");

		a_A_a.AddDependency(b_B_b);
		b_B_b.AddDependency(c_C_c);
	}

	public void testFactory() {
		NodeFactory local_factory  = new NodeFactory();

		ClosureInboundSelector selector = new ClosureInboundSelector();

		selector.Factory(local_factory);

		assertEquals("factory", local_factory, selector.Factory());
	}

	public void testCoverage() {
		Collection coverage = new ArrayList();

		ClosureInboundSelector selector = new ClosureInboundSelector();

		selector.Coverage(coverage);

		assertEquals("coverage", coverage, selector.Coverage());
	}
	
	public void testOneSelectedNode() {
		NodeFactory local_factory  = new NodeFactory();

		ClosureInboundSelector selector = new ClosureInboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 1, selector.SelectedNodes().size());
		assertEquals("a.A.a in selection", a_A_a, selector.SelectedNodes().iterator().next());
		assertSame("a.A.a in selection", a_A_a, selector.SelectedNodes().iterator().next());
	}

	public void testOneCopiedNode() {
		NodeFactory local_factory  = new NodeFactory();

		ClosureInboundSelector selector = new ClosureInboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("packages in scope", 2, local_factory.Packages().size());
		assertEquals("classes in scope" , 2, local_factory.Classes().size());
		assertEquals("features in scope", 2, local_factory.Features().size());

		assertEquals("package a in scope"    , a,     local_factory.Packages().get("a"));
		assertEquals("class a.A in scope"    , a_A,   local_factory.Classes().get("a.A"));
		assertEquals("feature a.A.a in scope", a_A_a, local_factory.Features().get("a.A.a"));
		assertEquals("package b in scope"    , b,     local_factory.Packages().get("b"));
		assertEquals("class b.B in scope"    , b_B,   local_factory.Classes().get("b.B"));
		assertEquals("feature b.B.b in scope", b_B_b, local_factory.Features().get("b.B.b"));

		assertNotSame("package a in scope"    , a,     local_factory.Packages().get("a"));
		assertNotSame("class a.A in scope"    , a_A,   local_factory.Classes().get("a.A"));
		assertNotSame("feature a.A.a in scope", a_A_a, local_factory.Features().get("a.A.a"));
		assertNotSame("package b in scope"    , b,     local_factory.Packages().get("b"));
		assertNotSame("class b.B in scope"    , b_B,   local_factory.Classes().get("b.B"));
		assertNotSame("feature b.B.b in scope", b_B_b, local_factory.Features().get("b.B.b"));

		assertEquals("nodes in selection", 1, selector.CopiedNodes().size());
		assertEquals("a.A.a in selection", a_A_a, selector.CopiedNodes().iterator().next());
		assertNotSame("a.A.a in selection", a_A_a, selector.CopiedNodes().iterator().next());
		assertSame("a.A.a in selection", local_factory.Features().get("a.A.a"), selector.CopiedNodes().iterator().next());
		assertEquals("a.A.a's inbounds",  0, ((Node) selector.CopiedNodes().iterator().next()).Inbound().size());
		assertEquals("a.A.a's outbounds", 1, ((Node) selector.CopiedNodes().iterator().next()).Outbound().size());
	}

	public void testThreeSelectedNodesFromPackage() {
		a.AddDependency(b);
		a_A.AddDependency(b);
		a_A_a.AddDependency(b);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureInboundSelector selector = new ClosureInboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b));

		assertEquals("nodes in selection", 3, selector.SelectedNodes().size());
		assertTrue("a in selection",     selector.SelectedNodes().contains(a));
		assertTrue("a.A in selection",   selector.SelectedNodes().contains(a_A));
		assertTrue("a.A.a in selection", selector.SelectedNodes().contains(a_A_a));
	}

	public void testThreeSelectedNodesFromClass() {
		a.AddDependency(b_B);
		a_A.AddDependency(b_B);
		a_A_a.AddDependency(b_B);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureInboundSelector selector = new ClosureInboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b_B));

		assertEquals("nodes in selection", 3, selector.SelectedNodes().size());
		assertTrue("a in selection",     selector.SelectedNodes().contains(a));
		assertTrue("a.A in selection",   selector.SelectedNodes().contains(a_A));
		assertTrue("a.A.a in selection", selector.SelectedNodes().contains(a_A_a));
	}

	public void testThreeSelectedNodesFromFeature() {
		a.AddDependency(b_B_b);
		a_A.AddDependency(b_B_b);
		a_A_a.AddDependency(b_B_b);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureInboundSelector selector = new ClosureInboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 3, selector.SelectedNodes().size());
		assertTrue("a in selection",     selector.SelectedNodes().contains(a));
		assertTrue("a.A in selection",   selector.SelectedNodes().contains(a_A));
		assertTrue("a.A.a in selection", selector.SelectedNodes().contains(a_A_a));
	}

	public void testThreeCopiedNodesFromPackage() {
		a.AddDependency(b);
		a_A.AddDependency(b);
		a_A_a.AddDependency(b);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureInboundSelector selector = new ClosureInboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b));

		assertEquals("nodes in selection", 3, selector.CopiedNodes().size());
		assertTrue("a in selection",     selector.CopiedNodes().contains(a));
		assertTrue("a.A in selection",   selector.CopiedNodes().contains(a_A));
		assertTrue("a.A.a in selection", selector.CopiedNodes().contains(a_A_a));

		assertEquals("b's inbounds", 3, local_factory.CreatePackage("b").Inbound().size());
	}

	public void testThreeCopiedNodesFromClass() {
		a.AddDependency(b_B);
		a_A.AddDependency(b_B);
		a_A_a.AddDependency(b_B);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureInboundSelector selector = new ClosureInboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b_B));

		assertEquals("nodes in selection", 3, selector.CopiedNodes().size());
		assertTrue("a in selection",     selector.CopiedNodes().contains(a));
		assertTrue("a.A in selection",   selector.CopiedNodes().contains(a_A));
		assertTrue("a.A.a in selection", selector.CopiedNodes().contains(a_A_a));

		assertEquals("b.B's inbounds", 3, local_factory.CreateClass("b.B").Inbound().size());
	}

	public void testThreeCopiedNodesFromFeature() {
		a.AddDependency(b_B_b);
		a_A.AddDependency(b_B_b);
		a_A_a.AddDependency(b_B_b);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureInboundSelector selector = new ClosureInboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 3, selector.CopiedNodes().size());
		assertTrue("a in selection",     selector.CopiedNodes().contains(a));
		assertTrue("a.A in selection",   selector.CopiedNodes().contains(a_A));
		assertTrue("a.A.a in selection", selector.CopiedNodes().contains(a_A_a));

		assertEquals("b.B.b's inbounds", 3, local_factory.CreateFeature("b.B.b").Inbound().size());
	}

	public void testTwoSelectedNodeWithPackageInCoverage() {
		a.AddDependency(b_B_b);
		a_A.AddDependency(b_B_b);
		a_A_a.AddDependency(b_B_b);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureInboundSelector selector = new ClosureInboundSelector(local_factory, Collections.singleton(a));
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 2, selector.SelectedNodes().size());
		assertTrue("a.A in selection",   selector.SelectedNodes().contains(a_A));
		assertTrue("a.A.a in selection", selector.SelectedNodes().contains(a_A_a));
	}

	public void testTwoSelectedNodeWithClassInCoverage() {
		a.AddDependency(b_B_b);
		a_A.AddDependency(b_B_b);
		a_A_a.AddDependency(b_B_b);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureInboundSelector selector = new ClosureInboundSelector(local_factory, Collections.singleton(a_A));
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 2, selector.SelectedNodes().size());
		assertTrue("a in selection",     selector.SelectedNodes().contains(a));
		assertTrue("a.A.a in selection", selector.SelectedNodes().contains(a_A_a));
	}

	public void testTwoSelectedNodeWithFeatureInCoverage() {
		a.AddDependency(b_B_b);
		a_A.AddDependency(b_B_b);
		a_A_a.AddDependency(b_B_b);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureInboundSelector selector = new ClosureInboundSelector(local_factory, Collections.singleton(a_A_a));
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 2, selector.SelectedNodes().size());
		assertTrue("a in selection",   selector.SelectedNodes().contains(a));
		assertTrue("a.A in selection", selector.SelectedNodes().contains(a_A));
	}

	public void testTwoCopiedNodeWithPackageInCoverage() {
		a.AddDependency(b_B_b);
		a_A.AddDependency(b_B_b);
		a_A_a.AddDependency(b_B_b);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureInboundSelector selector = new ClosureInboundSelector(local_factory, Collections.singleton(a));
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 2, selector.CopiedNodes().size());
		assertTrue("a.A in selection",   selector.CopiedNodes().contains(a_A));
		assertTrue("a.A.a in selection", selector.CopiedNodes().contains(a_A_a));

		assertEquals("b.B.b's inbounds", 2, local_factory.CreateFeature("b.B.b").Inbound().size());
	}

	public void testTwoCopiedNodeWithClassInCoverage() {
		a.AddDependency(b_B_b);
		a_A.AddDependency(b_B_b);
		a_A_a.AddDependency(b_B_b);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureInboundSelector selector = new ClosureInboundSelector(local_factory, Collections.singleton(a_A));
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 2, selector.CopiedNodes().size());
		assertTrue("a in selection",     selector.CopiedNodes().contains(a));
		assertTrue("a.A.a in selection", selector.CopiedNodes().contains(a_A_a));

		assertEquals("b.B.b's inbounds", 2, local_factory.CreateFeature("b.B.b").Inbound().size());
	}

	public void testTwoCopiedNodeWithFeatureInCoverage() {
		a.AddDependency(b_B_b);
		a_A.AddDependency(b_B_b);
		a_A_a.AddDependency(b_B_b);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureInboundSelector selector = new ClosureInboundSelector(local_factory, Collections.singleton(a_A_a));
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 2, selector.CopiedNodes().size());
		assertTrue("a in selection",   selector.CopiedNodes().contains(a));
		assertTrue("a.A in selection", selector.CopiedNodes().contains(a_A));

		assertEquals("b.B.b's inbounds", 2, local_factory.CreateFeature("b.B.b").Inbound().size());
	}

	public void testReset() {
		NodeFactory local_factory  = new NodeFactory();

		ClosureInboundSelector selector = new ClosureInboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 1, selector.SelectedNodes().size());
		assertEquals("copied nodes",       1, selector.CopiedNodes().size());

		selector.Reset();
		
		assertEquals("nodes in selection", 0, selector.SelectedNodes().size());
		assertEquals("copied nodes",       0, selector.CopiedNodes().size());
	}
}
