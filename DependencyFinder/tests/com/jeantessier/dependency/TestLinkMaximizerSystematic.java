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

import org.apache.oro.text.perl.*;

public class TestLinkMaximizerSystematic extends TestCase {
	NodeFactory factory;
	
	PackageNode a;
	ClassNode   a_A;
	FeatureNode a_A_a;
	
	PackageNode b;
	ClassNode   b_B;
	FeatureNode b_B_b;
	
	public TestLinkMaximizerSystematic(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		factory = new NodeFactory();

		a     = factory.CreatePackage("a");
		a_A   = factory.CreateClass("a.A");
		a_A_a = factory.CreateFeature("a.A.a()");
	
		b     = factory.CreatePackage("b");
		b_B   = factory.CreateClass("b.B");
		b_B_b = factory.CreateFeature("b.B.b()");
	}

	public void testPackagePackage() {
		a.AddDependency(b);

		Visitor visitor = new LinkMaximizer();
		visitor.TraverseNodes(factory.Packages().values());

		assertEquals("a outbound",     1, a.Outbound().size());
		assertTrue("Missing a --> b", a.Outbound().contains(b));
		assertEquals("a inbound",      0, a.Inbound().size());
		assertEquals("a_A outbound",   0, a_A.Outbound().size());
		assertEquals("a_A inbound",    0, a_A.Inbound().size());
		assertEquals("a_A_a outbound", 0, a_A_a.Outbound().size());
		assertEquals("a_A_a inbound",  0, a_A_a.Inbound().size()); 
		assertEquals("b outbound",     0, b.Outbound().size());
		assertEquals("b inbound",      1, b.Inbound().size());
		assertTrue("Missing b <-- a", b.Inbound().contains(a));
		assertEquals("b_B outbound",   0, b_B.Outbound().size());
		assertEquals("b_B inbound",    0, b_B.Inbound().size());
		assertEquals("b_B_b outbound", 0, b_B_b.Outbound().size());
		assertEquals("b_B_b inbound",  0, b_B_b.Inbound().size());
	}

	public void testPackageClass() {
		a.AddDependency(b_B);

		Visitor visitor = new LinkMaximizer();
		visitor.TraverseNodes(factory.Packages().values());

		assertEquals("a outbound",     2, a.Outbound().size());
		assertTrue("Missing a --> b",   a.Outbound().contains(b));
		assertTrue("Missing a --> b.B", a.Outbound().contains(b_B));
		assertEquals("a inbound",      0, a.Inbound().size());
		assertEquals("a_A outbound",   0, a_A.Outbound().size());
		assertEquals("a_A inbound",    0, a_A.Inbound().size());
		assertEquals("a_A_a outbound", 0, a_A_a.Outbound().size());
		assertEquals("a_A_a inbound",  0, a_A_a.Inbound().size());
		assertEquals("b outbound",     0, b.Outbound().size());
		assertEquals("b inbound",      1, b.Inbound().size());
		assertTrue("Missing b <-- a", b.Inbound().contains(a));
		assertEquals("b_B outbound",   0, b_B.Outbound().size());
		assertEquals("b_B inbound",    1, b_B.Inbound().size());
		assertTrue("Missing b.B <-- a", b_B.Inbound().contains(a));
		assertEquals("b_B_b outbound", 0, b_B_b.Outbound().size());
		assertEquals("b_B_b inbound",  0, b_B_b.Inbound().size());
	}

	public void testPackageFeature() {
		a.AddDependency(b_B_b);

		Visitor visitor = new LinkMaximizer();
		visitor.TraverseNodes(factory.Packages().values());

		assertEquals("a outbound",     3, a.Outbound().size());
		assertTrue("Missing a --> b",     a.Outbound().contains(b));
		assertTrue("Missing a --> b.B",   a.Outbound().contains(b_B));
		assertTrue("Missing a --> b.B.b", a.Outbound().contains(b_B_b));
		assertEquals("a inbound",      0, a.Inbound().size());
		assertEquals("a_A outbound",   0, a_A.Outbound().size());
		assertEquals("a_A inbound",    0, a_A.Inbound().size());
		assertEquals("a_A_a outbound", 0, a_A_a.Outbound().size());
		assertEquals("a_A_a inbound",  0, a_A_a.Inbound().size());
		assertEquals("b outbound",     0, b.Outbound().size());
		assertEquals("b inbound",      1, b.Inbound().size());
		assertTrue("Missing b <-- a", b.Inbound().contains(a));
		assertEquals("b_B outbound",   0, b_B.Outbound().size());
		assertEquals("b_B inbound",    1, b_B.Inbound().size());
		assertTrue("Missing b.B <-- a", b_B.Inbound().contains(a));
		assertEquals("b_B_b outbound", 0, b_B_b.Outbound().size());
		assertEquals("b_B_b inbound",  1, b_B_b.Inbound().size());
		assertTrue("Missing b.B.b <-- a", b_B_b.Inbound().contains(a));
	}

	public void testClassPackage() {
		a_A.AddDependency(b);

		Visitor visitor = new LinkMaximizer();
		visitor.TraverseNodes(factory.Packages().values());

		assertEquals("a outbound",     1, a.Outbound().size());
		assertTrue("Missing a --> b", a.Outbound().contains(b));
		assertEquals("a inbound",      0, a.Inbound().size());
		assertEquals("a_A outbound",   1, a_A.Outbound().size());
		assertTrue("Missing a.A --> b", a_A.Outbound().contains(b));
		assertEquals("a_A inbound",    0, a_A.Inbound().size());
		assertEquals("a_A_a outbound", 0, a_A_a.Outbound().size());
		assertEquals("a_A_a inbound",  0, a_A_a.Inbound().size()); 
		assertEquals("b outbound",     0, b.Outbound().size());
		assertEquals("b inbound",      2, b.Inbound().size());
		assertTrue("Missing b <-- a", b.Inbound().contains(a));
		assertTrue("Missing b <-- a.A", b.Inbound().contains(a_A));
		assertEquals("b_B outbound",   0, b_B.Outbound().size());
		assertEquals("b_B inbound",    0, b_B.Inbound().size());
		assertEquals("b_B_b outbound", 0, b_B_b.Outbound().size());
		assertEquals("b_B_b inbound",  0, b_B_b.Inbound().size());
	}

	public void testClassClass() {
		a_A.AddDependency(b_B);

		Visitor visitor = new LinkMaximizer();
		visitor.TraverseNodes(factory.Packages().values());

		assertEquals("a outbound",     2, a.Outbound().size());
		assertTrue("Missing a --> b",   a.Outbound().contains(b));
		assertTrue("Missing a --> b.B", a.Outbound().contains(b_B));
		assertEquals("a inbound",      0, a.Inbound().size());
		assertEquals("a_A outbound",   2, a_A.Outbound().size());
		assertTrue("Missing a.A --> b",   a_A.Outbound().contains(b));
		assertTrue("Missing a.A --> b.B", a_A.Outbound().contains(b_B));
		assertEquals("a_A inbound",    0, a_A.Inbound().size());
		assertEquals("a_A_a outbound", 0, a_A_a.Outbound().size());
		assertEquals("a_A_a inbound",  0, a_A_a.Inbound().size());
		assertEquals("b outbound",     0, b.Outbound().size());
		assertEquals("b inbound",      2, b.Inbound().size());
		assertTrue("Missing b <-- a",   b.Inbound().contains(a));
		assertTrue("Missing b <-- a.A", b.Inbound().contains(a_A));
		assertEquals("b_B outbound",   0, b_B.Outbound().size());
		assertEquals("b_B inbound",    2, b_B.Inbound().size());
		assertTrue("Missing b.B <-- a",   b_B.Inbound().contains(a));
		assertTrue("Missing b.B <-- a.A", b_B.Inbound().contains(a_A));
		assertEquals("b_B_b outbound", 0, b_B_b.Outbound().size());
		assertEquals("b_B_b inbound",  0, b_B_b.Inbound().size());
	}

	public void testClassClassSparse() {
		a.AddDependency(b);
		a_A.AddDependency(b_B);

		Visitor visitor = new LinkMaximizer();
		visitor.TraverseNodes(factory.Packages().values());

		assertEquals("a outbound",     2, a.Outbound().size());
		assertTrue("Missing a --> b",   a.Outbound().contains(b));
		assertTrue("Missing a --> b.B", a.Outbound().contains(b_B));
		assertEquals("a inbound",      0, a.Inbound().size());
		assertEquals("a_A outbound",   2, a_A.Outbound().size());
		assertTrue("Missing a.A --> b",   a_A.Outbound().contains(b));
		assertTrue("Missing a.A --> b.B", a_A.Outbound().contains(b_B));
		assertEquals("a_A inbound",    0, a_A.Inbound().size());
		assertEquals("a_A_a outbound", 0, a_A_a.Outbound().size());
		assertEquals("a_A_a inbound",  0, a_A_a.Inbound().size());
		assertEquals("b outbound",     0, b.Outbound().size());
		assertEquals("b inbound",      2, b.Inbound().size());
		assertTrue("Missing b <-- a",   b.Inbound().contains(a));
		assertTrue("Missing b <-- a.A", b.Inbound().contains(a_A));
		assertEquals("b_B outbound",   0, b_B.Outbound().size());
		assertEquals("b_B inbound",    2, b_B.Inbound().size());
		assertTrue("Missing b.B <-- a",   b_B.Inbound().contains(a));
		assertTrue("Missing b.B <-- a.A", b_B.Inbound().contains(a_A));
		assertEquals("b_B_b outbound", 0, b_B_b.Outbound().size());
		assertEquals("b_B_b inbound",  0, b_B_b.Inbound().size());
	}

	public void testClassFeature() {
		a_A.AddDependency(b_B_b);

		Visitor visitor = new LinkMaximizer();
		visitor.TraverseNodes(factory.Packages().values());

		assertEquals("a outbound",     3, a.Outbound().size());
		assertTrue("Missing a --> b",     a.Outbound().contains(b));
		assertTrue("Missing a --> b.B",   a.Outbound().contains(b_B));
		assertTrue("Missing a --> b.B.b", a.Outbound().contains(b_B_b));
		assertEquals("a inbound",      0, a.Inbound().size());
		assertEquals("a_A outbound",   3, a_A.Outbound().size());
		assertTrue("Missing a.A --> b",     a_A.Outbound().contains(b));
		assertTrue("Missing a.A --> b.B",   a_A.Outbound().contains(b_B));
		assertTrue("Missing a.A --> b.B.b", a_A.Outbound().contains(b_B_b));
		assertEquals("a_A inbound",    0, a_A.Inbound().size());
		assertEquals("a_A_a outbound", 0, a_A_a.Outbound().size());
		assertEquals("a_A_a inbound",  0, a_A_a.Inbound().size());
		assertEquals("b outbound",     0, b.Outbound().size());
		assertEquals("b inbound",      2, b.Inbound().size());
		assertTrue("Missing b <-- a",   b.Inbound().contains(a));
		assertTrue("Missing b <-- a_A", b.Inbound().contains(a_A));
		assertEquals("b_B outbound",   0, b_B.Outbound().size());
		assertEquals("b_B inbound",    2, b_B.Inbound().size());
		assertTrue("Missing b.B <-- a",   b_B.Inbound().contains(a));
		assertTrue("Missing b.B <-- a_A", b_B.Inbound().contains(a_A));
		assertEquals("b_B_b outbound", 0, b_B_b.Outbound().size());
		assertEquals("b_B_b inbound",  2, b_B_b.Inbound().size());
		assertTrue("Missing b.B.b <-- a",   b_B_b.Inbound().contains(a));
		assertTrue("Missing b.B.b <-- a_A", b_B_b.Inbound().contains(a_A));
	}

	public void testFeaturePackage() {
		a_A_a.AddDependency(b);

		Visitor visitor = new LinkMaximizer();
		visitor.TraverseNodes(factory.Packages().values());

		assertEquals("a outbound",     1, a.Outbound().size());
		assertTrue("Missing a --> b", a.Outbound().contains(b));
		assertEquals("a inbound",      0, a.Inbound().size());
		assertEquals("a_A outbound",   1, a_A.Outbound().size());
		assertTrue("Missing a.A --> b", a_A.Outbound().contains(b));
		assertEquals("a_A inbound",    0, a_A.Inbound().size());
		assertEquals("a_A_a outbound", 1, a_A_a.Outbound().size());
		assertTrue("Missing a.A.a --> b", a_A_a.Outbound().contains(b));
		assertEquals("a_A_a inbound",  0, a_A_a.Inbound().size()); 
		assertEquals("b outbound",     0, b.Outbound().size());
		assertEquals("b inbound",      3, b.Inbound().size());
		assertTrue("Missing b <-- a",     b.Inbound().contains(a));
		assertTrue("Missing b <-- a.A",   b.Inbound().contains(a_A));
		assertTrue("Missing b <-- a.A.a", b.Inbound().contains(a_A_a));
		assertEquals("b_B outbound",   0, b_B.Outbound().size());
		assertEquals("b_B inbound",    0, b_B.Inbound().size());
		assertEquals("b_B_b outbound", 0, b_B_b.Outbound().size());
		assertEquals("b_B_b inbound",  0, b_B_b.Inbound().size());
	}
	
	public void testFeatureClass() {
		a_A_a.AddDependency(b_B);

		Visitor visitor = new LinkMaximizer();
		visitor.TraverseNodes(factory.Packages().values());

		assertEquals("a outbound",     2, a.Outbound().size());
		assertTrue("Missing a --> b",   a.Outbound().contains(b));
		assertTrue("Missing a --> b.B", a.Outbound().contains(b_B));
		assertEquals("a inbound",      0, a.Inbound().size());
		assertEquals("a_A outbound",   2, a_A.Outbound().size());
		assertTrue("Missing a.A --> b",   a_A.Outbound().contains(b));
		assertTrue("Missing a.A --> b.B", a_A.Outbound().contains(b_B));
		assertEquals("a_A inbound",    0, a_A.Inbound().size());
		assertEquals("a_A_a outbound", 2, a_A_a.Outbound().size());
		assertTrue("Missing a.A.a --> b",   a_A_a.Outbound().contains(b));
		assertTrue("Missing a.A.a --> b.B", a_A_a.Outbound().contains(b_B));
		assertEquals("a_A_a inbound",  0, a_A_a.Inbound().size());
		assertEquals("b outbound",     0, b.Outbound().size());
		assertEquals("b inbound",      3, b.Inbound().size());
		assertTrue("Missing b <-- a",     b.Inbound().contains(a));
		assertTrue("Missing b <-- a.A",   b.Inbound().contains(a_A));
		assertTrue("Missing b <-- a.A.a", b.Inbound().contains(a_A_a));
		assertEquals("b_B outbound",   0, b_B.Outbound().size());
		assertEquals("b_B inbound",    3, b_B.Inbound().size());
		assertTrue("Missing b.B <-- a",     b_B.Inbound().contains(a));
		assertTrue("Missing b.B <-- a.A",   b_B.Inbound().contains(a_A));
		assertTrue("Missing b.B <-- a.A.a", b_B.Inbound().contains(a_A_a));
		assertEquals("b_B_b outbound", 0, b_B_b.Outbound().size());
		assertEquals("b_B_b inbound",  0, b_B_b.Inbound().size());
	}
	
	public void testFeatureFeature() {
		a_A_a.AddDependency(b_B_b);

		Visitor visitor = new LinkMaximizer();
		visitor.TraverseNodes(factory.Packages().values());

		assertEquals("a outbound",     3, a.Outbound().size());
		assertTrue("Missing a --> b",     a.Outbound().contains(b));
		assertTrue("Missing a --> b.B",   a.Outbound().contains(b_B));
		assertTrue("Missing a --> b.B.b", a.Outbound().contains(b_B_b));
		assertEquals("a inbound",      0, a.Inbound().size());
		assertEquals("a_A outbound",   3, a_A.Outbound().size());
		assertTrue("Missing a.A --> b",     a_A.Outbound().contains(b));
		assertTrue("Missing a.A --> b.B",   a_A.Outbound().contains(b_B));
		assertTrue("Missing a.A --> b.B.b", a_A.Outbound().contains(b_B_b));
		assertEquals("a_A inbound",    0, a_A.Inbound().size());
		assertEquals("a_A_a outbound", 3, a_A_a.Outbound().size());
		assertTrue("Missing a.A.a --> b",     a_A_a.Outbound().contains(b));
		assertTrue("Missing a.A.a --> b.B",   a_A_a.Outbound().contains(b_B));
		assertTrue("Missing a.A.a --> b.B.b", a_A_a.Outbound().contains(b_B_b));
		assertEquals("a_A_a inbound",  0, a_A_a.Inbound().size());
		assertEquals("b outbound",     0, b.Outbound().size());
		assertEquals("b inbound",      3, b.Inbound().size());
		assertTrue("Missing b <-- a",     b.Inbound().contains(a));
		assertTrue("Missing b <-- a.A",   b.Inbound().contains(a_A));
		assertTrue("Missing b <-- a.A.a", b.Inbound().contains(a_A_a));
		assertEquals("b_B outbound",   0, b_B.Outbound().size());
		assertEquals("b_B inbound",    3, b_B.Inbound().size());
		assertTrue("Missing b.B <-- a",     b_B.Inbound().contains(a));
		assertTrue("Missing b.B <-- a.A",   b_B.Inbound().contains(a_A));
		assertTrue("Missing b.B <-- a.A.a", b_B.Inbound().contains(a_A_a));
		assertEquals("b_B_b outbound", 0, b_B_b.Outbound().size());
		assertEquals("b_B_b inbound",  3, b_B_b.Inbound().size());
		assertTrue("Missing b.B.b <-- a",     b_B_b.Inbound().contains(a));
		assertTrue("Missing b.B.b <-- a.A",   b_B_b.Inbound().contains(a_A));
		assertTrue("Missing b.B.b <-- a.A.a", b_B_b.Inbound().contains(a_A_a));
	}
	
	public void testFeatureFeatureSparse() {
		a.AddDependency(b);
		a_A.AddDependency(b_B);
		a_A_a.AddDependency(b_B_b);

		Visitor visitor = new LinkMaximizer();
		visitor.TraverseNodes(factory.Packages().values());

		assertEquals("a outbound",     3, a.Outbound().size());
		assertTrue("Missing a --> b",     a.Outbound().contains(b));
		assertTrue("Missing a --> b.B",   a.Outbound().contains(b_B));
		assertTrue("Missing a --> b.B.b", a.Outbound().contains(b_B_b));
		assertEquals("a inbound",      0, a.Inbound().size());
		assertEquals("a_A outbound",   3, a_A.Outbound().size());
		assertTrue("Missing a.A --> b",     a_A.Outbound().contains(b));
		assertTrue("Missing a.A --> b.B",   a_A.Outbound().contains(b_B));
		assertTrue("Missing a.A --> b.B.b", a_A.Outbound().contains(b_B_b));
		assertEquals("a_A inbound",    0, a_A.Inbound().size());
		assertEquals("a_A_a outbound", 3, a_A_a.Outbound().size());
		assertTrue("Missing a.A.a --> b",     a_A_a.Outbound().contains(b));
		assertTrue("Missing a.A.a --> b.B",   a_A_a.Outbound().contains(b_B));
		assertTrue("Missing a.A.a --> b.B.b", a_A_a.Outbound().contains(b_B_b));
		assertEquals("a_A_a inbound",  0, a_A_a.Inbound().size());
		assertEquals("b outbound",     0, b.Outbound().size());
		assertEquals("b inbound",      3, b.Inbound().size());
		assertTrue("Missing b <-- a",     b.Inbound().contains(a));
		assertTrue("Missing b <-- a.A",   b.Inbound().contains(a_A));
		assertTrue("Missing b <-- a.A.a", b.Inbound().contains(a_A_a));
		assertEquals("b_B outbound",   0, b_B.Outbound().size());
		assertEquals("b_B inbound",    3, b_B.Inbound().size());
		assertTrue("Missing b.B <-- a",     b_B.Inbound().contains(a));
		assertTrue("Missing b.B <-- a.A",   b_B.Inbound().contains(a_A));
		assertTrue("Missing b.B <-- a.A.a", b_B.Inbound().contains(a_A_a));
		assertEquals("b_B_b outbound", 0, b_B_b.Outbound().size());
		assertEquals("b_B_b inbound",  3, b_B_b.Inbound().size());
		assertTrue("Missing b.B.b <-- a",     b_B_b.Inbound().contains(a));
		assertTrue("Missing b.B.b <-- a.A",   b_B_b.Inbound().contains(a_A));
		assertTrue("Missing b.B.b <-- a.A.a", b_B_b.Inbound().contains(a_A_a));
	}
}
