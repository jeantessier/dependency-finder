/*
 *  Copyright (c) 2001-2004, Jean Tessier
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

	private RegularExpressionSelectionCriteria start_criteria;
	private RegularExpressionSelectionCriteria stop_criteria;

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

		a_A_a.addDependency(b_B_b);
		b_B_b.addDependency(c_C_c);

		start_criteria = new RegularExpressionSelectionCriteria();
		stop_criteria  = new RegularExpressionSelectionCriteria();
		stop_criteria.GlobalIncludes("");
	}

	public void testSelectScope() {
		start_criteria.GlobalIncludes("/a.A.a/");

		GraphCopier copier = new GraphCopier(new SelectiveTraversalStrategy(start_criteria, new RegularExpressionSelectionCriteria()));

		copier.traverseNodes(factory.Packages().values());
		
		assertEquals("packages in scope: " , 1, copier.ScopeFactory().Packages().values().size());
		assertEquals("classes in scope"    , 1, copier.ScopeFactory().Classes().values().size());
		assertEquals("features in scope"   , 1, copier.ScopeFactory().Features().values().size());

		assertEquals("package b in scope"    , a,     copier.ScopeFactory().Packages().get("a"));
		assertEquals("class a.A in scope"    , a_A,   copier.ScopeFactory().Classes().get("a.A"));
		assertEquals("feature a.A.a in scope", a_A_a, copier.ScopeFactory().Features().get("a.A.a"));
	}

	public void testOutboundStartingPoint() {
		start_criteria.GlobalIncludes("/a.A.a/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), start_criteria, stop_criteria, new ClosureOutboundSelector());

		assertEquals("Nb layers", 1, engine.NbLayers());

		assertEquals("layer 0", 1, engine.Layer(0).size());
		assertEquals("a.A.a in layer 0", a_A_a, engine.Layer(0).iterator().next());
		assertNotSame("a.A.a in layer 0", a_A_a, engine.Layer(0).iterator().next());

		assertEquals("Nb outbounds from a.A.a", 0, ((Node) engine.Layer(0).iterator().next()).getOutboundDependencies().size());
		
		assertEquals("packages in scope: ", 1, engine.Factory().Packages().values().size());
		assertEquals("classes in scope" ,   1, engine.Factory().Classes().values().size());
		assertEquals("features in scope",   1, engine.Factory().Features().values().size());

		assertEquals("package a in scope",     a,     engine.Factory().Packages().get("a"));
		assertEquals("class a.A in scope",     a_A,   engine.Factory().Classes().get("a.A"));
		assertEquals("feature a.A.a in scope", a_A_a, engine.Factory().Features().get("a.A.a"));
	}

	public void testOneOutboundLayer() {
		start_criteria.GlobalIncludes("/a.A.a/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), start_criteria, stop_criteria, new ClosureOutboundSelector());
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 2, engine.NbLayers());

		assertEquals("layer 1", 1, engine.Layer(1).size());
		assertEquals("b.B.b in layer 1", b_B_b, engine.Layer(1).iterator().next());
		assertNotSame("b.B.b in layer 1", b_B_b, engine.Layer(1).iterator().next());

		assertEquals("Nb outbounds from a.A.a", a_A_a.getOutboundDependencies().size(), ((Node) engine.Layer(0).iterator().next()).getOutboundDependencies().size());
		assertEquals("Nb outbounds from b.B.b", 0,                       ((Node) engine.Layer(1).iterator().next()).getOutboundDependencies().size());
		
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
		start_criteria.GlobalIncludes("/a.A.a/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), start_criteria, stop_criteria, new ClosureOutboundSelector());
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 3, engine.NbLayers());

		assertEquals("layer 2", 1, engine.Layer(1).size());
		assertEquals("c.C.c in layer 2", c_C_c, engine.Layer(2).iterator().next());
		assertNotSame("c.C.c in layer 2", c_C_c, engine.Layer(2).iterator().next());

		assertEquals("Nb outbounds from a.A.a", a_A_a.getOutboundDependencies().size(), ((Node) engine.Layer(0).iterator().next()).getOutboundDependencies().size());
		assertEquals("Nb outbounds from b.B.b", b_B_b.getOutboundDependencies().size(), ((Node) engine.Layer(1).iterator().next()).getOutboundDependencies().size());
		assertEquals("Nb outbounds from c.C.c", 0,                       ((Node) engine.Layer(2).iterator().next()).getOutboundDependencies().size());
		
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
		start_criteria.GlobalIncludes("/a.A.a/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), start_criteria, stop_criteria, new ClosureOutboundSelector());
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 3, engine.NbLayers());

		assertEquals("Nb outbounds from a.A.a", a_A_a.getOutboundDependencies().size(), ((Node) engine.Layer(0).iterator().next()).getOutboundDependencies().size());
		assertEquals("Nb outbounds from b.B.b", b_B_b.getOutboundDependencies().size(), ((Node) engine.Layer(1).iterator().next()).getOutboundDependencies().size());
		assertEquals("Nb outbounds from c.C.c", c_C_c.getOutboundDependencies().size(), ((Node) engine.Layer(2).iterator().next()).getOutboundDependencies().size());
		
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
		start_criteria.GlobalIncludes("/a.A.a/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), start_criteria, stop_criteria, new ClosureOutboundSelector());
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 3, engine.NbLayers());

		assertEquals("Nb outbounds from a.A.a", a_A_a.getOutboundDependencies().size(), ((Node) engine.Layer(0).iterator().next()).getOutboundDependencies().size());
		assertEquals("Nb outbounds from b.B.b", b_B_b.getOutboundDependencies().size(), ((Node) engine.Layer(1).iterator().next()).getOutboundDependencies().size());
		assertEquals("Nb outbounds from c.C.c", c_C_c.getOutboundDependencies().size(), ((Node) engine.Layer(2).iterator().next()).getOutboundDependencies().size());
		
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
		start_criteria.GlobalIncludes("/c.C.c/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), start_criteria, stop_criteria, new ClosureInboundSelector());

		assertEquals("Nb layers", 1, engine.NbLayers());

		assertEquals("layer 0", 1, engine.Layer(0).size());
		assertEquals("c.C.c in layer 0", c_C_c, engine.Layer(0).iterator().next());
		assertNotSame("c.C.c in layer 0", c_C_c, engine.Layer(0).iterator().next());

		assertEquals("Nb inbounds from c.C.c", 0, ((Node) engine.Layer(0).iterator().next()).getInboundDependencies().size());
		
		assertEquals("packages in scope: ", 1, engine.Factory().Packages().values().size());
		assertEquals("classes in scope" ,   1, engine.Factory().Classes().values().size());
		assertEquals("features in scope",   1, engine.Factory().Features().values().size());

		assertEquals("package c in scope",     c,     engine.Factory().Packages().get("c"));
		assertEquals("class c.C in scope",     c_C,   engine.Factory().Classes().get("c.C"));
		assertEquals("feature c.C.c in scope", c_C_c, engine.Factory().Features().get("c.C.c"));
	}

	public void testOneInboundLayer() {
		start_criteria.GlobalIncludes("/c.C.c/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), start_criteria, stop_criteria, new ClosureInboundSelector());
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 2, engine.NbLayers());

		assertEquals("layer 1", 1, engine.Layer(1).size());
		assertEquals("b.B.b in layer 1", b_B_b, engine.Layer(1).iterator().next());
		assertNotSame("b.B.b in layer 1", b_B_b, engine.Layer(1).iterator().next());

		assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.Layer(0).iterator().next()).getInboundDependencies().size());
		assertEquals("Nb inbounds from b.B.b", 0,                      ((Node) engine.Layer(1).iterator().next()).getInboundDependencies().size());
		
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
		start_criteria.GlobalIncludes("/c.C.c/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), start_criteria, stop_criteria, new ClosureInboundSelector());
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 3, engine.NbLayers());

		assertEquals("layer 2", 1, engine.Layer(1).size());
		assertEquals("a.A.a in layer 2", a_A_a, engine.Layer(2).iterator().next());
		assertNotSame("a.A.a in layer 2", a_A_a, engine.Layer(2).iterator().next());

		assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.Layer(0).iterator().next()).getInboundDependencies().size());
		assertEquals("Nb inbounds from b.B.b", b_B_b.getInboundDependencies().size(), ((Node) engine.Layer(1).iterator().next()).getInboundDependencies().size());
		assertEquals("Nb inbounds from a.A.a", 0,                      ((Node) engine.Layer(2).iterator().next()).getInboundDependencies().size());
		
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
		start_criteria.GlobalIncludes("/c.C.c/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), start_criteria, stop_criteria, new ClosureInboundSelector());
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 3, engine.NbLayers());

		assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.Layer(0).iterator().next()).getInboundDependencies().size());
		assertEquals("Nb inbounds from b.B.b", b_B_b.getInboundDependencies().size(), ((Node) engine.Layer(1).iterator().next()).getInboundDependencies().size());
		assertEquals("Nb inbounds from a.A.a", a_A_a.getInboundDependencies().size(), ((Node) engine.Layer(2).iterator().next()).getInboundDependencies().size());
		
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
		start_criteria.GlobalIncludes("/c.C.c/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), start_criteria, stop_criteria, new ClosureInboundSelector());
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 3, engine.NbLayers());

		assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.Layer(0).iterator().next()).getInboundDependencies().size());
		assertEquals("Nb inbounds from b.B.b", b_B_b.getInboundDependencies().size(), ((Node) engine.Layer(1).iterator().next()).getInboundDependencies().size());
		assertEquals("Nb inbounds from a.A.a", a_A_a.getInboundDependencies().size(), ((Node) engine.Layer(2).iterator().next()).getInboundDependencies().size());
		
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

	public void testStopCriteria() {
		start_criteria.GlobalIncludes("/c.C.c/");
		stop_criteria.GlobalIncludes("/b.B.b/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), start_criteria, stop_criteria, new ClosureInboundSelector());
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();
		engine.ComputeNextLayer();

		assertEquals("Nb layers", 2, engine.NbLayers());

		assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.Layer(0).iterator().next()).getInboundDependencies().size());
		assertEquals("Nb inbounds from b.B.b", 0,                      ((Node) engine.Layer(1).iterator().next()).getInboundDependencies().size());
		
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

	public void testComputeAllLayers() {
		start_criteria.GlobalIncludes("/c.C.c/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), start_criteria, stop_criteria, new ClosureInboundSelector());
		engine.ComputeAllLayers();

		assertEquals("Nb layers", 3, engine.NbLayers());

		assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.Layer(0).iterator().next()).getInboundDependencies().size());
		assertEquals("Nb inbounds from b.B.b", b_B_b.getInboundDependencies().size(), ((Node) engine.Layer(1).iterator().next()).getInboundDependencies().size());
		assertEquals("Nb inbounds from a.A.a", a_A_a.getInboundDependencies().size(), ((Node) engine.Layer(2).iterator().next()).getInboundDependencies().size());
		
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

	public void testComputeAllLayersWithStopCriteria() {
		start_criteria.GlobalIncludes("/c.C.c/");
		stop_criteria.GlobalIncludes("/b.B.b/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), start_criteria, stop_criteria, new ClosureInboundSelector());
		engine.ComputeAllLayers();

		assertEquals("Nb layers", 2, engine.NbLayers());

		assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.Layer(0).iterator().next()).getInboundDependencies().size());
		assertEquals("Nb inbounds from b.B.b", 0,                      ((Node) engine.Layer(1).iterator().next()).getInboundDependencies().size());
		
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

	public void testComputeAllLayersUntilStartCriteria() {
		start_criteria.GlobalIncludes("/c.C.c/");
		stop_criteria.GlobalIncludes("//");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), start_criteria, stop_criteria, new ClosureInboundSelector());
		engine.ComputeAllLayers();

		assertEquals("Nb layers", 1, engine.NbLayers());

		assertEquals("Nb inbounds from c.C.c", 0, ((Node) engine.Layer(0).iterator().next()).getInboundDependencies().size());
		
		assertEquals("packages in scope: ", 1, engine.Factory().Packages().values().size());
		assertEquals("classes in scope" ,   1, engine.Factory().Classes().values().size());
		assertEquals("features in scope",   1, engine.Factory().Features().values().size());

		assertEquals("package c in scope",     c,     engine.Factory().Packages().get("c"));
		assertEquals("class c.C in scope",     c_C,   engine.Factory().Classes().get("c.C"));
		assertEquals("feature c.C.c in scope", c_C_c, engine.Factory().Features().get("c.C.c"));
	}

	public void testCompute1LayerOnly() {
		start_criteria.GlobalIncludes("/c.C.c/");
		stop_criteria.GlobalIncludes("");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), start_criteria, stop_criteria, new ClosureInboundSelector());
		engine.ComputeLayers(1);

		assertEquals("Nb layers", 2, engine.NbLayers());

		assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.Layer(0).iterator().next()).getInboundDependencies().size());
		assertEquals("Nb inbounds from b.B.b", 0,                      ((Node) engine.Layer(1).iterator().next()).getInboundDependencies().size());
		
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

	public void testCompute4LayersWithStopCriteria() {
		start_criteria.GlobalIncludes("/c.C.c/");
		stop_criteria.GlobalIncludes("/b.B.b/");

		TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.Packages().values(), start_criteria, stop_criteria, new ClosureInboundSelector());
		engine.ComputeLayers(4);

		assertEquals("Nb layers", 2, engine.NbLayers());

		assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.Layer(0).iterator().next()).getInboundDependencies().size());
		assertEquals("Nb inbounds from b.B.b", 0,                      ((Node) engine.Layer(1).iterator().next()).getInboundDependencies().size());
		
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
}
