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

public class TestClosureOutboundSelector extends TestCase {
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

		ClosureOutboundSelector selector = new ClosureOutboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 1, selector.SelectedNodes().size());
		assertEquals("c.C.c in selection", c_C_c, selector.SelectedNodes().iterator().next());
		assertSame("c.C.c in selection", c_C_c, selector.SelectedNodes().iterator().next());
	}

	public void testOneCopiedNode() {
		NodeFactory local_factory  = new NodeFactory();

		ClosureOutboundSelector selector = new ClosureOutboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("packages in scope", 2, local_factory.Packages().size());
		assertEquals("classes in scope" , 2, local_factory.Classes().size());
		assertEquals("features in scope", 2, local_factory.Features().size());

		assertEquals("package b in scope"    , b,     local_factory.Packages().get("b"));
		assertEquals("class b.B in scope"    , b_B,   local_factory.Classes().get("b.B"));
		assertEquals("feature b.B.b in scope", b_B_b, local_factory.Features().get("b.B.b"));
		assertEquals("package c in scope"    , c,     local_factory.Packages().get("c"));
		assertEquals("class c.C in scope"    , c_C,   local_factory.Classes().get("c.C"));
		assertEquals("feature c.C.c in scope", c_C_c, local_factory.Features().get("c.C.c"));

		assertNotSame("package b in scope"    , b,     local_factory.Packages().get("b"));
		assertNotSame("class b.B in scope"    , b_B,   local_factory.Classes().get("b.B"));
		assertNotSame("feature b.B.b in scope", b_B_b, local_factory.Features().get("b.B.b"));
		assertNotSame("package c in scope"    , c,     local_factory.Packages().get("c"));
		assertNotSame("class c.C in scope"    , c_C,   local_factory.Classes().get("c.C"));
		assertNotSame("feature c.C.c in scope", c_C_c, local_factory.Features().get("c.C.c"));

		assertEquals("nodes in selection", 1, selector.CopiedNodes().size());
		assertEquals("c.C.c in selection", c_C_c, selector.CopiedNodes().iterator().next());
		assertNotSame("c.C.c in selection", c_C_c, selector.CopiedNodes().iterator().next());
		assertSame("c.C.c in selection", local_factory.Features().get("c.C.c"), selector.CopiedNodes().iterator().next());
		assertEquals("c.C.c's inbounds",  1, ((Node) selector.CopiedNodes().iterator().next()).Inbound().size());
		assertEquals("c.C.c's outbounds", 0, ((Node) selector.CopiedNodes().iterator().next()).Outbound().size());
	}

	public void testThreeSelectedNodesFromPackage() {
		b.AddDependency(c);
		b.AddDependency(c_C);
		b.AddDependency(c_C_c);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureOutboundSelector selector = new ClosureOutboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b));

		assertEquals("nodes in selection", 3, selector.SelectedNodes().size());
		assertTrue("c in selection",     selector.SelectedNodes().contains(c));
		assertTrue("c.C in selection",   selector.SelectedNodes().contains(c_C));
		assertTrue("c.C.c in selection", selector.SelectedNodes().contains(c_C_c));
	}

	public void testThreeSelectedNodesFromClass() {
		b_B.AddDependency(c);
		b_B.AddDependency(c_C);
		b_B.AddDependency(c_C_c);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureOutboundSelector selector = new ClosureOutboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b_B));

		assertEquals("nodes in selection", 3, selector.SelectedNodes().size());
		assertTrue("c in selection",     selector.SelectedNodes().contains(c));
		assertTrue("c.C in selection",   selector.SelectedNodes().contains(c_C));
		assertTrue("c.C.c in selection", selector.SelectedNodes().contains(c_C_c));
	}

	public void testThreeSelectedNodesFromFeature() {
		b_B_b.AddDependency(c);
		b_B_b.AddDependency(c_C);
		b_B_b.AddDependency(c_C_c);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureOutboundSelector selector = new ClosureOutboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 3, selector.SelectedNodes().size());
		assertTrue("c in selection",     selector.SelectedNodes().contains(c));
		assertTrue("c.C in selection",   selector.SelectedNodes().contains(c_C));
		assertTrue("c.C.c in selection", selector.SelectedNodes().contains(c_C_c));
	}

	public void testThreeCopiedNodesFromPackage() {
		b.AddDependency(c);
		b.AddDependency(c_C);
		b.AddDependency(c_C_c);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureOutboundSelector selector = new ClosureOutboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b));

		assertEquals("nodes in selection", 3, selector.CopiedNodes().size());
		assertTrue("c in selection",     selector.CopiedNodes().contains(c));
		assertTrue("c.C in selection",   selector.CopiedNodes().contains(c_C));
		assertTrue("c.C.c in selection", selector.CopiedNodes().contains(c_C_c));

		assertEquals("b's outbounds", 3, local_factory.CreatePackage("b").Outbound().size());
	}

	public void testThreeCopiedNodesFromClass() {
		b_B.AddDependency(c);
		b_B.AddDependency(c_C);
		b_B.AddDependency(c_C_c);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureOutboundSelector selector = new ClosureOutboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b_B));

		assertEquals("nodes in selection", 3, selector.CopiedNodes().size());
		assertTrue("c in selection",     selector.CopiedNodes().contains(c));
		assertTrue("c.C in selection",   selector.CopiedNodes().contains(c_C));
		assertTrue("c.C.c in selection", selector.CopiedNodes().contains(c_C_c));

		assertEquals("b.B's outbounds", 3, local_factory.CreateClass("b.B").Outbound().size());
	}

	public void testThreeCopiedNodesFromFeature() {
		b_B_b.AddDependency(c);
		b_B_b.AddDependency(c_C);
		b_B_b.AddDependency(c_C_c);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureOutboundSelector selector = new ClosureOutboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 3, selector.CopiedNodes().size());
		assertTrue("c in selection",     selector.CopiedNodes().contains(c));
		assertTrue("c.C in selection",   selector.CopiedNodes().contains(c_C));
		assertTrue("c.C.c in selection", selector.CopiedNodes().contains(c_C_c));

		assertEquals("b.B.b's outbounds", 3, local_factory.CreateFeature("b.B.b").Outbound().size());
	}

	public void testTwoSelectedNodeWithPackageInCoverage() {
		b_B_b.AddDependency(c);
		b_B_b.AddDependency(c_C);
		b_B_b.AddDependency(c_C_c);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureOutboundSelector selector = new ClosureOutboundSelector(local_factory, Collections.singleton(c));
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 2, selector.SelectedNodes().size());
		assertTrue("c.C in selection",   selector.SelectedNodes().contains(c_C));
		assertTrue("c.C.c in selection", selector.SelectedNodes().contains(c_C_c));
	}

	public void testTwoSelectedNodeWithClassInCoverage() {
		b_B_b.AddDependency(c);
		b_B_b.AddDependency(c_C);
		b_B_b.AddDependency(c_C_c);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureOutboundSelector selector = new ClosureOutboundSelector(local_factory, Collections.singleton(c_C));
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 2, selector.SelectedNodes().size());
		assertTrue("c in selection",     selector.SelectedNodes().contains(c));
		assertTrue("c.C.c in selection", selector.SelectedNodes().contains(c_C_c));
	}

	public void testTwoSelectedNodeWithFeatureInCoverage() {
		b_B_b.AddDependency(c);
		b_B_b.AddDependency(c_C);
		b_B_b.AddDependency(c_C_c);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureOutboundSelector selector = new ClosureOutboundSelector(local_factory, Collections.singleton(c_C_c));
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 2, selector.SelectedNodes().size());
		assertTrue("c in selection",   selector.SelectedNodes().contains(c));
		assertTrue("c.C in selection", selector.SelectedNodes().contains(c_C));
	}

	public void testTwoCopiedNodeWithPackageInCoverage() {
		b_B_b.AddDependency(c);
		b_B_b.AddDependency(c_C);
		b_B_b.AddDependency(c_C_c);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureOutboundSelector selector = new ClosureOutboundSelector(local_factory, Collections.singleton(c));
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 2, selector.CopiedNodes().size());
		assertTrue("c.C in selection",   selector.CopiedNodes().contains(c_C));
		assertTrue("c.C.c in selection", selector.CopiedNodes().contains(c_C_c));

		assertEquals("b.B.b's outbounds", 2, local_factory.CreateFeature("b.B.b").Outbound().size());
	}

	public void testTwoCopiedNodeWithClassInCoverage() {
		b_B_b.AddDependency(c);
		b_B_b.AddDependency(c_C);
		b_B_b.AddDependency(c_C_c);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureOutboundSelector selector = new ClosureOutboundSelector(local_factory, Collections.singleton(c_C));
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 2, selector.CopiedNodes().size());
		assertTrue("c in selection",     selector.CopiedNodes().contains(c));
		assertTrue("c.C.c in selection", selector.CopiedNodes().contains(c_C_c));

		assertEquals("b.B.b's outbounds", 2, local_factory.CreateFeature("b.B.b").Outbound().size());
	}

	public void testTwoCopiedNodeWithFeatureInCoverage() {
		b_B_b.AddDependency(c);
		b_B_b.AddDependency(c_C);
		b_B_b.AddDependency(c_C_c);
		
		NodeFactory local_factory  = new NodeFactory();

		ClosureOutboundSelector selector = new ClosureOutboundSelector(local_factory, Collections.singleton(c_C_c));
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 2, selector.CopiedNodes().size());
		assertTrue("c in selection",   selector.CopiedNodes().contains(c));
		assertTrue("c.C in selection", selector.CopiedNodes().contains(c_C));

		assertEquals("b.B.b's outbounds", 2, local_factory.CreateFeature("b.B.b").Outbound().size());
	}

	public void testReset() {
		NodeFactory local_factory  = new NodeFactory();

		ClosureOutboundSelector selector = new ClosureOutboundSelector(local_factory, Collections.EMPTY_SET);
		selector.TraverseNodes(Collections.singleton(b_B_b));

		assertEquals("nodes in selection", 1, selector.SelectedNodes().size());
		assertEquals("copied nodes",       1, selector.CopiedNodes().size());

		selector.Reset();
		
		assertEquals("nodes in selection", 0, selector.SelectedNodes().size());
		assertEquals("copied nodes",       0, selector.CopiedNodes().size());
	}
}
