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

import org.apache.log4j.*;

public class TestGraphSummarizer extends TestCase {
	private RegularExpressionSelectionCriteria scope_criteria;
	private RegularExpressionSelectionCriteria filter_criteria;
	private NodeFactory                        factory;
	
	private Node a_package;
	private Node a_A_class;
	private Node a_A_a_method;
	private Node a_B_class;

	private Node b_package;
	private Node b_B_class;
	private Node b_B_b_method;

	private GraphSummarizer summarizer;

	protected void setUp() throws Exception {
		Logger.getLogger(getClass()).info("Starting test: " + getName());

		scope_criteria  = new RegularExpressionSelectionCriteria();
		filter_criteria = new RegularExpressionSelectionCriteria();
		factory         = new NodeFactory();

		a_package = factory.CreatePackage("a");
		a_A_class = factory.CreateClass("a.A");
		a_A_a_method = factory.CreateFeature("a.A.a");
		a_B_class = factory.CreateClass("a.B");
		
		b_package = factory.CreatePackage("b");
		b_B_class = factory.CreateClass("b.B");
		b_B_b_method = factory.CreateFeature("b.B.b");
		
		summarizer = new GraphSummarizer(scope_criteria, filter_criteria);
	}

	protected void tearDown() throws Exception {
		Logger.getLogger(getClass()).info("End of " + getName());
	}

	public void testP2PasP2P() {
		a_package.AddDependency(b_package);
		
		scope_criteria.MatchClass(false);
		scope_criteria.MatchFeature(false);
		filter_criteria.MatchClass(false);
		filter_criteria.MatchFeature(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("a"));
		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("b"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().isEmpty());
		assertTrue(summarizer.ScopeFactory().Features().keySet().toString(), summarizer.ScopeFactory().Features().keySet().isEmpty());

		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Inbound().size());
		assertEquals(1, summarizer.ScopeFactory().CreatePackage("a").Outbound().size());
		assertTrue(summarizer.ScopeFactory().CreatePackage("a").Outbound().contains(summarizer.ScopeFactory().CreatePackage("b")));
		assertEquals(1, summarizer.ScopeFactory().CreatePackage("b").Inbound().size());
		assertTrue(summarizer.ScopeFactory().CreatePackage("b").Inbound().contains(summarizer.ScopeFactory().CreatePackage("a")));
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Outbound().size());
	}

	public void testP2PasC2C() {
		a_package.AddDependency(b_package);
		
		scope_criteria.MatchPackage(false);
		scope_criteria.MatchFeature(false);
		filter_criteria.MatchPackage(false);
		filter_criteria.MatchFeature(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("a"));
		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("b"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().contains("a.A"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().contains("b.B"));
		assertTrue(summarizer.ScopeFactory().Features().keySet().toString(), summarizer.ScopeFactory().Features().keySet().isEmpty());

		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Outbound().size());

		assertEquals(0, summarizer.ScopeFactory().CreateClass("a.A").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateClass("a.A").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateClass("b.B").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateClass("b.B").Outbound().size());
	}

	public void testP2PasF2F() {
		a_package.AddDependency(b_package);
		
		scope_criteria.MatchPackage(false);
		scope_criteria.MatchClass(false);
		filter_criteria.MatchPackage(false);
		filter_criteria.MatchClass(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("a"));
		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("b"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().contains("a.A"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().contains("b.B"));
		assertTrue(summarizer.ScopeFactory().Features().keySet().toString(), summarizer.ScopeFactory().Features().keySet().contains("a.A.a"));
		assertTrue(summarizer.ScopeFactory().Features().keySet().toString(), summarizer.ScopeFactory().Features().keySet().contains("b.B.b"));

		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Outbound().size());

		assertEquals(0, summarizer.ScopeFactory().CreateClass("a.A").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateClass("a.A").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateClass("b.B").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateClass("b.B").Outbound().size());

		assertEquals(0, summarizer.ScopeFactory().CreateFeature("a.A.a").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateFeature("a.A.a").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateFeature("b.B.b").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateFeature("b.B.b").Outbound().size());
	}

	public void testC2CasP2P() {
		a_A_class.AddDependency(b_B_class);
		
		scope_criteria.MatchClass(false);
		scope_criteria.MatchFeature(false);
		filter_criteria.MatchClass(false);
		filter_criteria.MatchFeature(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("a"));
		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("b"));
		assertTrue(summarizer.ScopeFactory().Classes().isEmpty());
		assertTrue(summarizer.ScopeFactory().Features().isEmpty());

		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Inbound().size());
		assertEquals(1, summarizer.ScopeFactory().CreatePackage("a").Outbound().size());
		assertTrue(summarizer.ScopeFactory().CreatePackage("a").Outbound().contains(summarizer.ScopeFactory().CreatePackage("b")));
		assertEquals(1, summarizer.ScopeFactory().CreatePackage("b").Inbound().size());
		assertTrue(summarizer.ScopeFactory().CreatePackage("b").Inbound().contains(summarizer.ScopeFactory().CreatePackage("a")));
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Outbound().size());
	}

	public void testC2CasP2CSamePackage() {
		a_A_class.AddDependency(a_B_class);
		
		scope_criteria.MatchClass(false);
		scope_criteria.MatchFeature(false);
		filter_criteria.MatchPackage(false);
		filter_criteria.MatchFeature(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("a"));
		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("b"));
		assertTrue(summarizer.ScopeFactory().Classes().isEmpty());
		assertTrue(summarizer.ScopeFactory().Features().isEmpty());

		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Outbound().size());
	}

	public void testC2CasC2C() {
		a_A_class.AddDependency(b_B_class);
		
		scope_criteria.MatchPackage(false);
		scope_criteria.MatchFeature(false);
		filter_criteria.MatchPackage(false);
		filter_criteria.MatchFeature(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("a"));
		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("b"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().contains("a.A"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().contains("b.B"));
		assertTrue(summarizer.ScopeFactory().Features().isEmpty());

		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Outbound().size());

		assertEquals(0, summarizer.ScopeFactory().CreateClass("a.A").Inbound().size());
		assertEquals(1, summarizer.ScopeFactory().CreateClass("a.A").Outbound().size());
		assertTrue(summarizer.ScopeFactory().CreateClass("a.A").Outbound().contains(summarizer.ScopeFactory().CreateClass("b.B")));
		assertEquals(1, summarizer.ScopeFactory().CreateClass("b.B").Inbound().size());
		assertTrue(summarizer.ScopeFactory().CreateClass("b.B").Inbound().contains(summarizer.ScopeFactory().CreateClass("a.A")));
		assertEquals(0, summarizer.ScopeFactory().CreateClass("b.B").Outbound().size());
	}

	public void testC2CasF2F() {
		a_A_class.AddDependency(b_B_class);
		
		scope_criteria.MatchPackage(false);
		scope_criteria.MatchClass(false);
		filter_criteria.MatchPackage(false);
		filter_criteria.MatchClass(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("a"));
		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("b"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().contains("a.A"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().contains("b.B"));
		assertTrue(summarizer.ScopeFactory().Features().keySet().toString(), summarizer.ScopeFactory().Features().keySet().contains("a.A.a"));
		assertTrue(summarizer.ScopeFactory().Features().keySet().toString(), summarizer.ScopeFactory().Features().keySet().contains("b.B.b"));

		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Outbound().size());

		assertEquals(0, summarizer.ScopeFactory().CreateClass("a.A").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateClass("a.A").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateClass("b.B").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateClass("b.B").Outbound().size());

		assertEquals(0, summarizer.ScopeFactory().CreateFeature("a.A.a").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateFeature("a.A.a").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateFeature("b.B.b").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateFeature("b.B.b").Outbound().size());
	}

	public void testF2FasP2P() {
		a_A_a_method.AddDependency(b_B_b_method);
		
		scope_criteria.MatchClass(false);
		scope_criteria.MatchFeature(false);
		filter_criteria.MatchClass(false);
		filter_criteria.MatchFeature(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("a"));
		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("b"));
		assertTrue(summarizer.ScopeFactory().Classes().isEmpty());
		assertTrue(summarizer.ScopeFactory().Features().isEmpty());

		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Inbound().size());
		assertEquals(1, summarizer.ScopeFactory().CreatePackage("a").Outbound().size());
		assertTrue(summarizer.ScopeFactory().CreatePackage("a").Outbound().contains(summarizer.ScopeFactory().CreatePackage("b")));
		assertEquals(1, summarizer.ScopeFactory().CreatePackage("b").Inbound().size());
		assertTrue(summarizer.ScopeFactory().CreatePackage("b").Inbound().contains(summarizer.ScopeFactory().CreatePackage("a")));
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Outbound().size());
	}

	public void testF2FasC2C() {
		a_A_a_method.AddDependency(b_B_b_method);
		
		scope_criteria.MatchPackage(false);
		scope_criteria.MatchFeature(false);
		filter_criteria.MatchPackage(false);
		filter_criteria.MatchFeature(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("a"));
		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("b"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().contains("a.A"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().contains("b.B"));
		assertTrue(summarizer.ScopeFactory().Features().isEmpty());

		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Outbound().size());

		assertEquals(0, summarizer.ScopeFactory().CreateClass("a.A").Inbound().size());
		assertEquals(1, summarizer.ScopeFactory().CreateClass("a.A").Outbound().size());
		assertTrue(summarizer.ScopeFactory().CreateClass("a.A").Outbound().contains(summarizer.ScopeFactory().CreateClass("b.B")));
		assertEquals(1, summarizer.ScopeFactory().CreateClass("b.B").Inbound().size());
		assertTrue(summarizer.ScopeFactory().CreateClass("b.B").Inbound().contains(summarizer.ScopeFactory().CreateClass("a.A")));
		assertEquals(0, summarizer.ScopeFactory().CreateClass("b.B").Outbound().size());
	}

	public void testF2FasF2F() {
		a_A_a_method.AddDependency(b_B_b_method);
		
		scope_criteria.MatchPackage(false);
		scope_criteria.MatchClass(false);
		filter_criteria.MatchPackage(false);
		filter_criteria.MatchClass(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("a"));
		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("b"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().contains("a.A"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().contains("b.B"));
		assertTrue(summarizer.ScopeFactory().Features().keySet().toString(), summarizer.ScopeFactory().Features().keySet().contains("a.A.a"));
		assertTrue(summarizer.ScopeFactory().Features().keySet().toString(), summarizer.ScopeFactory().Features().keySet().contains("b.B.b"));

		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Outbound().size());

		assertEquals(0, summarizer.ScopeFactory().CreateClass("a.A").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateClass("a.A").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateClass("b.B").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateClass("b.B").Outbound().size());

		assertEquals(0, summarizer.ScopeFactory().CreateFeature("a.A.a").Inbound().size());
		assertEquals(1, summarizer.ScopeFactory().CreateFeature("a.A.a").Outbound().size());
		assertTrue(summarizer.ScopeFactory().CreateFeature("a.A.a").Outbound().contains(summarizer.ScopeFactory().CreateFeature("b.B.b")));
		assertEquals(1, summarizer.ScopeFactory().CreateFeature("b.B.b").Inbound().size());
		assertTrue(summarizer.ScopeFactory().CreateFeature("b.B.b").Inbound().contains(summarizer.ScopeFactory().CreateFeature("a.A.a")));
		assertEquals(0, summarizer.ScopeFactory().CreateFeature("b.B.b").Outbound().size());
	}

	public void testF2CasP2P() {
		a_A_a_method.AddDependency(b_B_class);
		
		scope_criteria.MatchClass(false);
		scope_criteria.MatchFeature(false);
		filter_criteria.MatchClass(false);
		filter_criteria.MatchFeature(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("a"));
		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("b"));
		assertTrue(summarizer.ScopeFactory().Classes().isEmpty());
		assertTrue(summarizer.ScopeFactory().Features().isEmpty());

		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Inbound().size());
		assertEquals(1, summarizer.ScopeFactory().CreatePackage("a").Outbound().size());
		assertTrue(summarizer.ScopeFactory().CreatePackage("a").Outbound().contains(summarizer.ScopeFactory().CreatePackage("b")));
		assertEquals(1, summarizer.ScopeFactory().CreatePackage("b").Inbound().size());
		assertTrue(summarizer.ScopeFactory().CreatePackage("b").Inbound().contains(summarizer.ScopeFactory().CreatePackage("a")));
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Outbound().size());
	}

	public void testF2CasC2C() {
		a_A_a_method.AddDependency(b_B_class);
		
		scope_criteria.MatchPackage(false);
		scope_criteria.MatchFeature(false);
		filter_criteria.MatchPackage(false);
		filter_criteria.MatchFeature(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("a"));
		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("b"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().contains("a.A"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().contains("b.B"));
		assertTrue(summarizer.ScopeFactory().Features().isEmpty());

		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Outbound().size());

		assertEquals(0, summarizer.ScopeFactory().CreateClass("a.A").Inbound().size());
		assertEquals(1, summarizer.ScopeFactory().CreateClass("a.A").Outbound().size());
		assertTrue(summarizer.ScopeFactory().CreateClass("a.A").Outbound().contains(summarizer.ScopeFactory().CreateClass("b.B")));
		assertEquals(1, summarizer.ScopeFactory().CreateClass("b.B").Inbound().size());
		assertTrue(summarizer.ScopeFactory().CreateClass("b.B").Inbound().contains(summarizer.ScopeFactory().CreateClass("a.A")));
		assertEquals(0, summarizer.ScopeFactory().CreateClass("b.B").Outbound().size());
	}

	public void testF2CasF2F() {
		a_A_a_method.AddDependency(b_B_class);
		
		scope_criteria.MatchPackage(false);
		scope_criteria.MatchClass(false);
		filter_criteria.MatchPackage(false);
		filter_criteria.MatchClass(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("a"));
		assertTrue(summarizer.ScopeFactory().Packages().keySet().toString(), summarizer.ScopeFactory().Packages().keySet().contains("b"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().contains("a.A"));
		assertTrue(summarizer.ScopeFactory().Classes().keySet().toString(), summarizer.ScopeFactory().Classes().keySet().contains("b.B"));
		assertTrue(summarizer.ScopeFactory().Features().keySet().toString(), summarizer.ScopeFactory().Features().keySet().contains("a.A.a"));
		assertTrue(summarizer.ScopeFactory().Features().keySet().toString(), summarizer.ScopeFactory().Features().keySet().contains("b.B.b"));

		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreatePackage("b").Outbound().size());

		assertEquals(0, summarizer.ScopeFactory().CreateClass("a").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateClass("a").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateClass("b").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateClass("b").Outbound().size());

		assertEquals(0, summarizer.ScopeFactory().CreateFeature("a").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateFeature("a").Outbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateFeature("b").Inbound().size());
		assertEquals(0, summarizer.ScopeFactory().CreateFeature("b").Outbound().size());
	}
}
