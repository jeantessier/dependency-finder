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

public class TestTransitiveClosureEngine extends TestCase {
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

	public void testSelectScope() {
		RegularExpressionSelectionCriteria criteria = new RegularExpressionSelectionCriteria();
		criteria.GlobalIncludes("/a.A.a/");

		GraphCopier copier = new GraphCopier(new SelectiveTraversalStrategy(criteria, new RegularExpressionSelectionCriteria()));

		copier.TraverseNodes(factory.Packages().values());
		
		assertEquals("packages in scope: " , 1, copier.ScopeFactory().Packages().values().size());
		assertEquals("classes in scope"    , 1, copier.ScopeFactory().Classes().values().size());
		assertEquals("features in scope"   , 1, copier.ScopeFactory().Features().values().size());

		assertEquals("package b in scope"    , a,     copier.ScopeFactory().Packages().get("a"));
		assertEquals("class a.A in scope"    , a_A,   copier.ScopeFactory().Classes().get("a.A"));
		assertEquals("feature a.A.a in scope", a_A_a, copier.ScopeFactory().Features().get("a.A.a"));
	}

	public void testOutboundStartingPoint() {
		RegularExpressionSelectionCriteria criteria = new RegularExpressionSelectionCriteria();
		criteria.GlobalIncludes("/a.A.a/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), criteria, new RegularExpressionSelectionCriteria(), new ClosureOutboundSelector());

		assertEquals("Nb layers", 1, engine.NbLayers());

		assertEquals("layer 0", 1, engine.Layer(0).size());
		assertEquals("a.A.a in layer 0", a_A_a, engine.Layer(0).iterator().next());
		assertNotSame("a.A.a in layer 0", a_A_a, engine.Layer(0).iterator().next());

		assertEquals("Nb outbounds from a.A.a", 0, ((Node) engine.Layer(0).iterator().next()).Outbound().size());
		
		assertEquals("packages in scope: ", 1, engine.Factory().Packages().values().size());
		assertEquals("classes in scope" ,   1, engine.Factory().Classes().values().size());
		assertEquals("features in scope",   1, engine.Factory().Features().values().size());

		assertEquals("package a in scope",     a,     engine.Factory().Packages().get("a"));
		assertEquals("class a.A in scope",     a_A,   engine.Factory().Classes().get("a.A"));
		assertEquals("feature a.A.a in scope", a_A_a, engine.Factory().Features().get("a.A.a"));
	}

	public void testOneOutboundLayer() {
		RegularExpressionSelectionCriteria criteria = new RegularExpressionSelectionCriteria();
		criteria.GlobalIncludes("/a.A.a/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), criteria, new RegularExpressionSelectionCriteria(), new ClosureOutboundSelector());
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 2, engine.NbLayers());

		assertEquals("layer 1", 1, engine.Layer(1).size());
		assertEquals("b.B.b in layer 1", b_B_b, engine.Layer(1).iterator().next());
		assertNotSame("b.B.b in layer 1", b_B_b, engine.Layer(1).iterator().next());

		assertEquals("Nb outbounds from a.A.a", a_A_a.Outbound().size(), ((Node) engine.Layer(0).iterator().next()).Outbound().size());
		assertEquals("Nb outbounds from b.B.b", 0,                       ((Node) engine.Layer(1).iterator().next()).Outbound().size());
		
		assertEquals("packages in scope: ", 2, engine.Factory().Packages().values().size());
		assertEquals("classes in scope" ,   2, engine.Factory().Classes().values().size());
		assertEquals("features in scope",   2, engine.Factory().Features().values().size());

		assertEquals("package a in scope",     a,     engine.Factory().Packages().get("a"));
		assertEquals("class a.A in scope",     a_A,   engine.Factory().Classes().get("a.A"));
		assertEquals("feature a.A.a in scope", a_A_a, engine.Factory().Features().get("a.A.a"));
		assertEquals("package b in scope",     b,     engine.Factory().Packages().get("b"));
		assertEquals("class b.B in scope",     b_B,   engine.Factory().Classes().get("b.B"));
		assertEquals("feature b.B.b in scope", b_B_b, engine.Factory().Features().get("b.B.b"));
	}

	public void testTwoOutboundLayers() {
		RegularExpressionSelectionCriteria criteria = new RegularExpressionSelectionCriteria();
		criteria.GlobalIncludes("/a.A.a/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), criteria, new RegularExpressionSelectionCriteria(), new ClosureOutboundSelector());
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 3, engine.NbLayers());

		assertEquals("layer 2", 1, engine.Layer(1).size());
		assertEquals("c.C.c in layer 2", c_C_c, engine.Layer(2).iterator().next());
		assertNotSame("c.C.c in layer 2", c_C_c, engine.Layer(2).iterator().next());

		assertEquals("Nb outbounds from a.A.a", a_A_a.Outbound().size(), ((Node) engine.Layer(0).iterator().next()).Outbound().size());
		assertEquals("Nb outbounds from b.B.b", b_B_b.Outbound().size(), ((Node) engine.Layer(1).iterator().next()).Outbound().size());
		assertEquals("Nb outbounds from c.C.c", 0,                       ((Node) engine.Layer(2).iterator().next()).Outbound().size());
		
		assertEquals("packages in scope: ", 3, engine.Factory().Packages().values().size());
		assertEquals("classes in scope" ,   3, engine.Factory().Classes().values().size());
		assertEquals("features in scope",   3, engine.Factory().Features().values().size());

		assertEquals("package a in scope",     a,     engine.Factory().Packages().get("a"));
		assertEquals("class a.A in scope",     a_A,   engine.Factory().Classes().get("a.A"));
		assertEquals("feature a.A.a in scope", a_A_a, engine.Factory().Features().get("a.A.a"));
		assertEquals("package b in scope",     b,     engine.Factory().Packages().get("b"));
		assertEquals("class b.B in scope",     b_B,   engine.Factory().Classes().get("b.B"));
		assertEquals("feature b.B.b in scope", b_B_b, engine.Factory().Features().get("b.B.b"));
		assertEquals("package c in scope",     c,     engine.Factory().Packages().get("c"));
		assertEquals("class c.C in scope",     c_C,   engine.Factory().Classes().get("c.C"));
		assertEquals("feature c.C.c in scope", c_C_c, engine.Factory().Features().get("c.C.c"));
	}

	public void testThreeOutboundLayers() {
		RegularExpressionSelectionCriteria criteria = new RegularExpressionSelectionCriteria();
		criteria.GlobalIncludes("/a.A.a/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), criteria, new RegularExpressionSelectionCriteria(), new ClosureOutboundSelector());
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 3, engine.NbLayers());

		assertEquals("Nb outbounds from a.A.a", a_A_a.Outbound().size(), ((Node) engine.Layer(0).iterator().next()).Outbound().size());
		assertEquals("Nb outbounds from b.B.b", b_B_b.Outbound().size(), ((Node) engine.Layer(1).iterator().next()).Outbound().size());
		assertEquals("Nb outbounds from c.C.c", c_C_c.Outbound().size(), ((Node) engine.Layer(2).iterator().next()).Outbound().size());
		
		assertEquals("packages in scope: ", 3, engine.Factory().Packages().values().size());
		assertEquals("classes in scope" ,   3, engine.Factory().Classes().values().size());
		assertEquals("features in scope",   3, engine.Factory().Features().values().size());

		assertEquals("package a in scope",     a,     engine.Factory().Packages().get("a"));
		assertEquals("class a.A in scope",     a_A,   engine.Factory().Classes().get("a.A"));
		assertEquals("feature a.A.a in scope", a_A_a, engine.Factory().Features().get("a.A.a"));
		assertEquals("package b in scope",     b,     engine.Factory().Packages().get("b"));
		assertEquals("class b.B in scope",     b_B,   engine.Factory().Classes().get("b.B"));
		assertEquals("feature b.B.b in scope", b_B_b, engine.Factory().Features().get("b.B.b"));
		assertEquals("package c in scope",     c,     engine.Factory().Packages().get("c"));
		assertEquals("class c.C in scope",     c_C,   engine.Factory().Classes().get("c.C"));
		assertEquals("feature c.C.c in scope", c_C_c, engine.Factory().Features().get("c.C.c"));
	}

	public void testFourOutboundLayers() {
		RegularExpressionSelectionCriteria criteria = new RegularExpressionSelectionCriteria();
		criteria.GlobalIncludes("/a.A.a/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), criteria, new RegularExpressionSelectionCriteria(), new ClosureOutboundSelector());
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 3, engine.NbLayers());

		assertEquals("Nb outbounds from a.A.a", a_A_a.Outbound().size(), ((Node) engine.Layer(0).iterator().next()).Outbound().size());
		assertEquals("Nb outbounds from b.B.b", b_B_b.Outbound().size(), ((Node) engine.Layer(1).iterator().next()).Outbound().size());
		assertEquals("Nb outbounds from c.C.c", c_C_c.Outbound().size(), ((Node) engine.Layer(2).iterator().next()).Outbound().size());
		
		assertEquals("packages in scope: ", 3, engine.Factory().Packages().values().size());
		assertEquals("classes in scope" ,   3, engine.Factory().Classes().values().size());
		assertEquals("features in scope",   3, engine.Factory().Features().values().size());

		assertEquals("package a in scope",     a,     engine.Factory().Packages().get("a"));
		assertEquals("class a.A in scope",     a_A,   engine.Factory().Classes().get("a.A"));
		assertEquals("feature a.A.a in scope", a_A_a, engine.Factory().Features().get("a.A.a"));
		assertEquals("package b in scope",     b,     engine.Factory().Packages().get("b"));
		assertEquals("class b.B in scope",     b_B,   engine.Factory().Classes().get("b.B"));
		assertEquals("feature b.B.b in scope", b_B_b, engine.Factory().Features().get("b.B.b"));
		assertEquals("package c in scope",     c,     engine.Factory().Packages().get("c"));
		assertEquals("class c.C in scope",     c_C,   engine.Factory().Classes().get("c.C"));
		assertEquals("feature c.C.c in scope", c_C_c, engine.Factory().Features().get("c.C.c"));
	}

	public void testInboundStartingPoint() {
		RegularExpressionSelectionCriteria criteria = new RegularExpressionSelectionCriteria();
		criteria.GlobalIncludes("/c.C.c/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), criteria, new RegularExpressionSelectionCriteria(), new ClosureInboundSelector());

		assertEquals("Nb layers", 1, engine.NbLayers());

		assertEquals("layer 0", 1, engine.Layer(0).size());
		assertEquals("c.C.c in layer 0", c_C_c, engine.Layer(0).iterator().next());
		assertNotSame("c.C.c in layer 0", c_C_c, engine.Layer(0).iterator().next());

		assertEquals("Nb inbounds from c.C.c", 0, ((Node) engine.Layer(0).iterator().next()).Inbound().size());
		
		assertEquals("packages in scope: ", 1, engine.Factory().Packages().values().size());
		assertEquals("classes in scope" ,   1, engine.Factory().Classes().values().size());
		assertEquals("features in scope",   1, engine.Factory().Features().values().size());

		assertEquals("package c in scope",     c,     engine.Factory().Packages().get("c"));
		assertEquals("class c.C in scope",     c_C,   engine.Factory().Classes().get("c.C"));
		assertEquals("feature c.C.c in scope", c_C_c, engine.Factory().Features().get("c.C.c"));
	}

	public void testOneInboundLayer() {
		RegularExpressionSelectionCriteria criteria = new RegularExpressionSelectionCriteria();
		criteria.GlobalIncludes("/c.C.c/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), criteria, new RegularExpressionSelectionCriteria(), new ClosureInboundSelector());
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 2, engine.NbLayers());

		assertEquals("layer 1", 1, engine.Layer(1).size());
		assertEquals("b.B.b in layer 1", b_B_b, engine.Layer(1).iterator().next());
		assertNotSame("b.B.b in layer 1", b_B_b, engine.Layer(1).iterator().next());

		assertEquals("Nb inbounds from c.C.c", c_C_c.Inbound().size(), ((Node) engine.Layer(0).iterator().next()).Inbound().size());
		assertEquals("Nb inbounds from b.B.b", 0,                       ((Node) engine.Layer(1).iterator().next()).Inbound().size());
		
		assertEquals("packages in scope: ", 2, engine.Factory().Packages().values().size());
		assertEquals("classes in scope" ,   2, engine.Factory().Classes().values().size());
		assertEquals("features in scope",   2, engine.Factory().Features().values().size());

		assertEquals("package b in scope",     b,     engine.Factory().Packages().get("b"));
		assertEquals("class b.B in scope",     b_B,   engine.Factory().Classes().get("b.B"));
		assertEquals("feature b.B.b in scope", b_B_b, engine.Factory().Features().get("b.B.b"));
		assertEquals("package c in scope",     c,     engine.Factory().Packages().get("c"));
		assertEquals("class c.C in scope",     c_C,   engine.Factory().Classes().get("c.C"));
		assertEquals("feature c.C.c in scope", c_C_c, engine.Factory().Features().get("c.C.c"));
	}

	public void testTwoInboundLayers() {
		RegularExpressionSelectionCriteria criteria = new RegularExpressionSelectionCriteria();
		criteria.GlobalIncludes("/c.C.c/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), criteria, new RegularExpressionSelectionCriteria(), new ClosureInboundSelector());
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 3, engine.NbLayers());

		assertEquals("layer 2", 1, engine.Layer(1).size());
		assertEquals("a.A.a in layer 2", a_A_a, engine.Layer(2).iterator().next());
		assertNotSame("a.A.a in layer 2", a_A_a, engine.Layer(2).iterator().next());

		assertEquals("Nb inbounds from c.C.c", c_C_c.Inbound().size(), ((Node) engine.Layer(0).iterator().next()).Inbound().size());
		assertEquals("Nb inbounds from b.B.b", b_B_b.Inbound().size(), ((Node) engine.Layer(1).iterator().next()).Inbound().size());
		assertEquals("Nb inbounds from a.A.a", 0,                       ((Node) engine.Layer(2).iterator().next()).Inbound().size());
		
		assertEquals("packages in scope: ", 3, engine.Factory().Packages().values().size());
		assertEquals("classes in scope" ,   3, engine.Factory().Classes().values().size());
		assertEquals("features in scope",   3, engine.Factory().Features().values().size());

		assertEquals("package a in scope",     a,     engine.Factory().Packages().get("a"));
		assertEquals("class a.A in scope",     a_A,   engine.Factory().Classes().get("a.A"));
		assertEquals("feature a.A.a in scope", a_A_a, engine.Factory().Features().get("a.A.a"));
		assertEquals("package b in scope",     b,     engine.Factory().Packages().get("b"));
		assertEquals("class b.B in scope",     b_B,   engine.Factory().Classes().get("b.B"));
		assertEquals("feature b.B.b in scope", b_B_b, engine.Factory().Features().get("b.B.b"));
		assertEquals("package c in scope",     c,     engine.Factory().Packages().get("c"));
		assertEquals("class c.C in scope",     c_C,   engine.Factory().Classes().get("c.C"));
		assertEquals("feature c.C.c in scope", c_C_c, engine.Factory().Features().get("c.C.c"));
	}

	public void testThreeInboundLayers() {
		RegularExpressionSelectionCriteria criteria = new RegularExpressionSelectionCriteria();
		criteria.GlobalIncludes("/c.C.c/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), criteria, new RegularExpressionSelectionCriteria(), new ClosureInboundSelector());
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 3, engine.NbLayers());

		assertEquals("Nb inbounds from c.C.c", c_C_c.Inbound().size(), ((Node) engine.Layer(0).iterator().next()).Inbound().size());
		assertEquals("Nb inbounds from b.B.b", b_B_b.Inbound().size(), ((Node) engine.Layer(1).iterator().next()).Inbound().size());
		assertEquals("Nb inbounds from a.A.a", a_A_a.Inbound().size(), ((Node) engine.Layer(2).iterator().next()).Inbound().size());
		
		assertEquals("packages in scope: ", 3, engine.Factory().Packages().values().size());
		assertEquals("classes in scope" ,   3, engine.Factory().Classes().values().size());
		assertEquals("features in scope",   3, engine.Factory().Features().values().size());

		assertEquals("package a in scope",     a,     engine.Factory().Packages().get("a"));
		assertEquals("class a.A in scope",     a_A,   engine.Factory().Classes().get("a.A"));
		assertEquals("feature a.A.a in scope", a_A_a, engine.Factory().Features().get("a.A.a"));
		assertEquals("package b in scope",     b,     engine.Factory().Packages().get("b"));
		assertEquals("class b.B in scope",     b_B,   engine.Factory().Classes().get("b.B"));
		assertEquals("feature b.B.b in scope", b_B_b, engine.Factory().Features().get("b.B.b"));
		assertEquals("package c in scope",     c,     engine.Factory().Packages().get("c"));
		assertEquals("class c.C in scope",     c_C,   engine.Factory().Classes().get("c.C"));
		assertEquals("feature c.C.c in scope", c_C_c, engine.Factory().Features().get("c.C.c"));
	}

	public void testFourInboundLayers() {
		RegularExpressionSelectionCriteria criteria = new RegularExpressionSelectionCriteria();
		criteria.GlobalIncludes("/c.C.c/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), criteria, new RegularExpressionSelectionCriteria(), new ClosureInboundSelector());
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 3, engine.NbLayers());

		assertEquals("Nb inbounds from c.C.c", c_C_c.Inbound().size(), ((Node) engine.Layer(0).iterator().next()).Inbound().size());
		assertEquals("Nb inbounds from b.B.b", b_B_b.Inbound().size(), ((Node) engine.Layer(1).iterator().next()).Inbound().size());
		assertEquals("Nb inbounds from a.A.a", a_A_a.Inbound().size(), ((Node) engine.Layer(2).iterator().next()).Inbound().size());
		
		assertEquals("packages in scope: ", 3, engine.Factory().Packages().values().size());
		assertEquals("classes in scope" ,   3, engine.Factory().Classes().values().size());
		assertEquals("features in scope",   3, engine.Factory().Features().values().size());

		assertEquals("package a in scope",     a,     engine.Factory().Packages().get("a"));
		assertEquals("class a.A in scope",     a_A,   engine.Factory().Classes().get("a.A"));
		assertEquals("feature a.A.a in scope", a_A_a, engine.Factory().Features().get("a.A.a"));
		assertEquals("package b in scope",     b,     engine.Factory().Packages().get("b"));
		assertEquals("class b.B in scope",     b_B,   engine.Factory().Classes().get("b.B"));
		assertEquals("feature b.B.b in scope", b_B_b, engine.Factory().Features().get("b.B.b"));
		assertEquals("package c in scope",     c,     engine.Factory().Packages().get("c"));
		assertEquals("class c.C in scope",     c_C,   engine.Factory().Classes().get("c.C"));
		assertEquals("feature c.C.c in scope", c_C_c, engine.Factory().Features().get("c.C.c"));
	}
}
