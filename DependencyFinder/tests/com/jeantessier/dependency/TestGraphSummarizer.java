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

import org.apache.log4j.*;

public class TestGraphSummarizer extends TestCase {
	SelectiveTraversalStrategy strategy;
	NodeFactory                factory;
	
	Node a_package;
	Node a_A_class;
	Node a_A_a_method;

	Node b_package;
	Node b_B_class;
	Node b_B_b_method;

    GraphSummarizer summarizer;

	public TestGraphSummarizer(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		Category.getInstance(getClass().getName()).info("Starting test: " + getName());

		strategy = new SelectiveTraversalStrategy();
		factory = new NodeFactory();

		a_package = factory.CreatePackage("a");
		a_A_class = factory.CreateClass("a.A");
		a_A_a_method = factory.CreateFeature("a.A.a");
		
		b_package = factory.CreatePackage("b");
		b_B_class = factory.CreateClass("b.B");
		b_B_b_method = factory.CreateFeature("b.B.b");
		
		summarizer = new GraphSummarizer(strategy);
	}

	protected void tearDown() throws Exception {
		Category.getInstance(getClass().getName()).info("End of " + getName());
	}

	public void testP2PasP2P() {
		a_package.AddDependency(b_package);
		
		strategy.ClassScope(false);
		strategy.FeatureScope(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("a"));
		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("b"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().isEmpty());
		assertTrue(summarizer.Factory().Features().keySet().toString(), summarizer.Factory().Features().keySet().isEmpty());

		assertEquals(0, summarizer.Factory().CreatePackage("a").Inbound().size());
		assertEquals(1, summarizer.Factory().CreatePackage("a").Outbound().size());
		assertTrue(summarizer.Factory().CreatePackage("a").Outbound().contains(summarizer.Factory().CreatePackage("b")));
		assertEquals(1, summarizer.Factory().CreatePackage("b").Inbound().size());
		assertTrue(summarizer.Factory().CreatePackage("b").Inbound().contains(summarizer.Factory().CreatePackage("a")));
		assertEquals(0, summarizer.Factory().CreatePackage("b").Outbound().size());
	}

	public void testP2PasC2C() {
		a_package.AddDependency(b_package);
		
		strategy.PackageScope(false);
		strategy.FeatureScope(false);
		strategy.PackageFilter(false);
		strategy.FeatureFilter(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("a"));
		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("b"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().contains("a.A"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().contains("b.B"));
		assertTrue(summarizer.Factory().Features().keySet().toString(), summarizer.Factory().Features().keySet().isEmpty());

		assertEquals(0, summarizer.Factory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("b").Outbound().size());

		assertEquals(0, summarizer.Factory().CreateClass("a.A").Inbound().size());
		assertEquals(0, summarizer.Factory().CreateClass("a.A").Outbound().size());
		assertEquals(0, summarizer.Factory().CreateClass("b.B").Inbound().size());
		assertEquals(0, summarizer.Factory().CreateClass("b.B").Outbound().size());
	}

	public void testP2PasF2F() {
		a_package.AddDependency(b_package);
		
		strategy.PackageScope(false);
		strategy.ClassScope(false);
		strategy.PackageFilter(false);
		strategy.ClassFilter(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("a"));
		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("b"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().contains("a.A"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().contains("b.B"));
		assertTrue(summarizer.Factory().Features().keySet().toString(), summarizer.Factory().Features().keySet().contains("a.A.a"));
		assertTrue(summarizer.Factory().Features().keySet().toString(), summarizer.Factory().Features().keySet().contains("b.B.b"));

		assertEquals(0, summarizer.Factory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("b").Outbound().size());

		assertEquals(0, summarizer.Factory().CreateClass("a.A").Inbound().size());
		assertEquals(0, summarizer.Factory().CreateClass("a.A").Outbound().size());
		assertEquals(0, summarizer.Factory().CreateClass("b.B").Inbound().size());
		assertEquals(0, summarizer.Factory().CreateClass("b.B").Outbound().size());

		assertEquals(0, summarizer.Factory().CreateFeature("a.A.a").Inbound().size());
		assertEquals(0, summarizer.Factory().CreateFeature("a.A.a").Outbound().size());
		assertEquals(0, summarizer.Factory().CreateFeature("b.B.b").Inbound().size());
		assertEquals(0, summarizer.Factory().CreateFeature("b.B.b").Outbound().size());
	}

	public void testC2CasP2P() {
		a_A_class.AddDependency(b_B_class);
		
		strategy.ClassScope(false);
		strategy.FeatureScope(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("a"));
		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("b"));
		assertTrue(summarizer.Factory().Classes().isEmpty());
		assertTrue(summarizer.Factory().Features().isEmpty());

		assertEquals(0, summarizer.Factory().CreatePackage("a").Inbound().size());
		assertEquals(1, summarizer.Factory().CreatePackage("a").Outbound().size());
		assertTrue(summarizer.Factory().CreatePackage("a").Outbound().contains(summarizer.Factory().CreatePackage("b")));
		assertEquals(1, summarizer.Factory().CreatePackage("b").Inbound().size());
		assertTrue(summarizer.Factory().CreatePackage("b").Inbound().contains(summarizer.Factory().CreatePackage("a")));
		assertEquals(0, summarizer.Factory().CreatePackage("b").Outbound().size());
	}

	public void testC2CasC2C() {
		a_A_class.AddDependency(b_B_class);
		
		strategy.PackageScope(false);
		strategy.FeatureScope(false);
		strategy.PackageFilter(false);
		strategy.FeatureFilter(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("a"));
		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("b"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().contains("a.A"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().contains("b.B"));
		assertTrue(summarizer.Factory().Features().isEmpty());

		assertEquals(0, summarizer.Factory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("b").Outbound().size());

		assertEquals(0, summarizer.Factory().CreateClass("a.A").Inbound().size());
		assertEquals(1, summarizer.Factory().CreateClass("a.A").Outbound().size());
		assertTrue(summarizer.Factory().CreateClass("a.A").Outbound().contains(summarizer.Factory().CreateClass("b.B")));
		assertEquals(1, summarizer.Factory().CreateClass("b.B").Inbound().size());
		assertTrue(summarizer.Factory().CreateClass("b.B").Inbound().contains(summarizer.Factory().CreateClass("a.A")));
		assertEquals(0, summarizer.Factory().CreateClass("b.B").Outbound().size());
	}

	public void testC2CasF2F() {
		a_A_class.AddDependency(b_B_class);
		
		strategy.PackageScope(false);
		strategy.ClassScope(false);
		strategy.PackageFilter(false);
		strategy.ClassFilter(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("a"));
		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("b"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().contains("a.A"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().contains("b.B"));
		assertTrue(summarizer.Factory().Features().keySet().toString(), summarizer.Factory().Features().keySet().contains("a.A.a"));
		assertTrue(summarizer.Factory().Features().keySet().toString(), summarizer.Factory().Features().keySet().contains("b.B.b"));

		assertEquals(0, summarizer.Factory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("b").Outbound().size());

		assertEquals(0, summarizer.Factory().CreateClass("a.A").Inbound().size());
		assertEquals(0, summarizer.Factory().CreateClass("a.A").Outbound().size());
		assertEquals(0, summarizer.Factory().CreateClass("b.B").Inbound().size());
		assertEquals(0, summarizer.Factory().CreateClass("b.B").Outbound().size());

		assertEquals(0, summarizer.Factory().CreateFeature("a.A.a").Inbound().size());
		assertEquals(0, summarizer.Factory().CreateFeature("a.A.a").Outbound().size());
		assertEquals(0, summarizer.Factory().CreateFeature("b.B.b").Inbound().size());
		assertEquals(0, summarizer.Factory().CreateFeature("b.B.b").Outbound().size());
	}

	public void testF2FasP2P() {
		a_A_a_method.AddDependency(b_B_b_method);
		
		strategy.ClassScope(false);
		strategy.FeatureScope(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("a"));
		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("b"));
		assertTrue(summarizer.Factory().Classes().isEmpty());
		assertTrue(summarizer.Factory().Features().isEmpty());

		assertEquals(0, summarizer.Factory().CreatePackage("a").Inbound().size());
		assertEquals(1, summarizer.Factory().CreatePackage("a").Outbound().size());
		assertTrue(summarizer.Factory().CreatePackage("a").Outbound().contains(summarizer.Factory().CreatePackage("b")));
		assertEquals(1, summarizer.Factory().CreatePackage("b").Inbound().size());
		assertTrue(summarizer.Factory().CreatePackage("b").Inbound().contains(summarizer.Factory().CreatePackage("a")));
		assertEquals(0, summarizer.Factory().CreatePackage("b").Outbound().size());
	}

	public void testF2FasC2C() {
		a_A_a_method.AddDependency(b_B_b_method);
		
		strategy.PackageScope(false);
		strategy.FeatureScope(false);
		strategy.PackageFilter(false);
		strategy.FeatureFilter(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("a"));
		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("b"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().contains("a.A"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().contains("b.B"));
		assertTrue(summarizer.Factory().Features().isEmpty());

		assertEquals(0, summarizer.Factory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("b").Outbound().size());

		assertEquals(0, summarizer.Factory().CreateClass("a.A").Inbound().size());
		assertEquals(1, summarizer.Factory().CreateClass("a.A").Outbound().size());
		assertTrue(summarizer.Factory().CreateClass("a.A").Outbound().contains(summarizer.Factory().CreateClass("b.B")));
		assertEquals(1, summarizer.Factory().CreateClass("b.B").Inbound().size());
		assertTrue(summarizer.Factory().CreateClass("b.B").Inbound().contains(summarizer.Factory().CreateClass("a.A")));
		assertEquals(0, summarizer.Factory().CreateClass("b.B").Outbound().size());
	}

	public void testF2FasF2F() {
		a_A_a_method.AddDependency(b_B_b_method);
		
		strategy.PackageScope(false);
		strategy.ClassScope(false);
		strategy.PackageFilter(false);
		strategy.ClassFilter(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("a"));
		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("b"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().contains("a.A"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().contains("b.B"));
		assertTrue(summarizer.Factory().Features().keySet().toString(), summarizer.Factory().Features().keySet().contains("a.A.a"));
		assertTrue(summarizer.Factory().Features().keySet().toString(), summarizer.Factory().Features().keySet().contains("b.B.b"));

		assertEquals(0, summarizer.Factory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("b").Outbound().size());

		assertEquals(0, summarizer.Factory().CreateClass("a.A").Inbound().size());
		assertEquals(0, summarizer.Factory().CreateClass("a.A").Outbound().size());
		assertEquals(0, summarizer.Factory().CreateClass("b.B").Inbound().size());
		assertEquals(0, summarizer.Factory().CreateClass("b.B").Outbound().size());

		assertEquals(0, summarizer.Factory().CreateFeature("a.A.a").Inbound().size());
		assertEquals(1, summarizer.Factory().CreateFeature("a.A.a").Outbound().size());
		assertTrue(summarizer.Factory().CreateFeature("a.A.a").Outbound().contains(summarizer.Factory().CreateFeature("b.B.b")));
		assertEquals(1, summarizer.Factory().CreateFeature("b.B.b").Inbound().size());
		assertTrue(summarizer.Factory().CreateFeature("b.B.b").Inbound().contains(summarizer.Factory().CreateFeature("a.A.a")));
		assertEquals(0, summarizer.Factory().CreateFeature("b.B.b").Outbound().size());
	}

	public void testF2CasP2P() {
		a_A_a_method.AddDependency(b_B_class);
		
		strategy.ClassScope(false);
		strategy.FeatureScope(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("a"));
		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("b"));
		assertTrue(summarizer.Factory().Classes().isEmpty());
		assertTrue(summarizer.Factory().Features().isEmpty());

		assertEquals(0, summarizer.Factory().CreatePackage("a").Inbound().size());
		assertEquals(1, summarizer.Factory().CreatePackage("a").Outbound().size());
		assertTrue(summarizer.Factory().CreatePackage("a").Outbound().contains(summarizer.Factory().CreatePackage("b")));
		assertEquals(1, summarizer.Factory().CreatePackage("b").Inbound().size());
		assertTrue(summarizer.Factory().CreatePackage("b").Inbound().contains(summarizer.Factory().CreatePackage("a")));
		assertEquals(0, summarizer.Factory().CreatePackage("b").Outbound().size());
	}

	public void testF2CasC2C() {
		a_A_a_method.AddDependency(b_B_class);
		
		strategy.PackageScope(false);
		strategy.FeatureScope(false);
		strategy.PackageFilter(false);
		strategy.FeatureFilter(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("a"));
		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("b"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().contains("a.A"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().contains("b.B"));
		assertTrue(summarizer.Factory().Features().isEmpty());

		assertEquals(0, summarizer.Factory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("b").Outbound().size());

		assertEquals(0, summarizer.Factory().CreateClass("a.A").Inbound().size());
		assertEquals(1, summarizer.Factory().CreateClass("a.A").Outbound().size());
		assertTrue(summarizer.Factory().CreateClass("a.A").Outbound().contains(summarizer.Factory().CreateClass("b.B")));
		assertEquals(1, summarizer.Factory().CreateClass("b.B").Inbound().size());
		assertTrue(summarizer.Factory().CreateClass("b.B").Inbound().contains(summarizer.Factory().CreateClass("a.A")));
		assertEquals(0, summarizer.Factory().CreateClass("b.B").Outbound().size());
	}

	public void testF2CasF2F() {
		a_A_a_method.AddDependency(b_B_class);
		
		strategy.PackageScope(false);
		strategy.ClassScope(false);
		strategy.PackageFilter(false);
		strategy.ClassFilter(false);

		summarizer.TraverseNodes(factory.Packages().values());

		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("a"));
		assertTrue(summarizer.Factory().Packages().keySet().toString(), summarizer.Factory().Packages().keySet().contains("b"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().contains("a.A"));
		assertTrue(summarizer.Factory().Classes().keySet().toString(), summarizer.Factory().Classes().keySet().contains("b.B"));
		assertTrue(summarizer.Factory().Features().keySet().toString(), summarizer.Factory().Features().keySet().contains("a.A.a"));
		assertTrue(summarizer.Factory().Features().keySet().toString(), summarizer.Factory().Features().keySet().contains("b.B.b"));

		assertEquals(0, summarizer.Factory().CreatePackage("a").Inbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("a").Outbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("b").Inbound().size());
		assertEquals(0, summarizer.Factory().CreatePackage("b").Outbound().size());

		assertEquals(0, summarizer.Factory().CreateClass("a").Inbound().size());
		assertEquals(0, summarizer.Factory().CreateClass("a").Outbound().size());
		assertEquals(0, summarizer.Factory().CreateClass("b").Inbound().size());
		assertEquals(0, summarizer.Factory().CreateClass("b").Outbound().size());

		assertEquals(0, summarizer.Factory().CreateFeature("a").Inbound().size());
		assertEquals(0, summarizer.Factory().CreateFeature("a").Outbound().size());
		assertEquals(0, summarizer.Factory().CreateFeature("b").Inbound().size());
		assertEquals(0, summarizer.Factory().CreateFeature("b").Outbound().size());
	}
}
