/*
 *  Dependency Finder - Computes quality factors from compiled Java code
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeantessier.dependency;

import java.io.*;
import java.util.*;

import junit.framework.*;

public class TestGraphSummarizerWithFiltering extends TestCase {
	SelectiveTraversalStrategy strategy;
	NodeFactory                factory;
	
	Node a;
	Node a_A;
	Node a_A_a;
	
	Node b;
	Node b_B;
	Node b_B_b;
	
	Node c;
	Node c_C;
	Node c_C_c;

	List include_scope;
	List include_filter;
	List exclude_filter;

    GraphCopier copier;

	public TestGraphSummarizerWithFiltering(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		strategy = new SelectiveTraversalStrategy();
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
		
		include_scope = new LinkedList();
		include_scope.add("/^a/");
		
		include_filter = new LinkedList();
		include_filter.add("/^b/");
		
		exclude_filter = new LinkedList();
		exclude_filter.add("/^c/");

		copier = new GraphSummarizer(strategy);
	}

	public void testIncludeFilterF2FtoP2P() {
		a_A_a.AddDependency(b_B_b);
		a_A_a.AddDependency(c_C_c);
		
		strategy.ClassScope(false);
		strategy.FeatureScope(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);
		strategy.FilterIncludes(include_filter);
		
		copier.TraverseNodes(factory.Packages().values());

		assertTrue(copier.Factory().CreatePackage("a").Inbound().isEmpty());
		assertEquals(copier.Factory().CreatePackage("a").Outbound().toString(),
					 1, 
					 copier.Factory().CreatePackage("a").Outbound().size());
		assertTrue(copier.Factory().CreatePackage("a").Outbound().contains(b));
	}

	public void testExcludeFilterF2FtoP2P() {
		a_A_a.AddDependency(b_B_b);
		a_A_a.AddDependency(c_C_c);
		
		strategy.ClassScope(false);
		strategy.FeatureScope(false);
		strategy.ScopeIncludes(include_scope);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);
		strategy.FilterExcludes(exclude_filter);

		assertTrue(!strategy.FeatureFilterMatch(c_C_c.Name()));
		
		copier.TraverseNodes(factory.Packages().values());

		assertTrue(copier.Factory().CreatePackage("a").Inbound().isEmpty());
		assertEquals(copier.Factory().CreatePackage("a").Outbound().toString(),
					 1, 
					 copier.Factory().CreatePackage("a").Outbound().size());
		assertTrue(copier.Factory().CreatePackage("a").Outbound().contains(b));
	}
}
