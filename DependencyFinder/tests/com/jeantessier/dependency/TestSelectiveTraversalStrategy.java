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

public class TestSelectiveTraversalStrategy extends TestCase {
	SelectiveTraversalStrategy strategy;
	NodeFactory                factory;

	PackageNode a;
	ClassNode a_A;
	FeatureNode a_A_a;
	
	PackageNode b;
	ClassNode b_B;
	FeatureNode b_B_b;
	
	PackageNode c;
	ClassNode c_C;
	FeatureNode c_C_c;

	List include;
	List exclude;

	public TestSelectiveTraversalStrategy(String name) {
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
		
		include = new LinkedList();
		include.add("/^b/");
		
		exclude = new LinkedList();
		exclude.add("/^c/");
	}

	public void testScope() {
		strategy.PackageScope(true);
		strategy.ClassScope(false);
		strategy.FeatureScope(false);

		assertTrue("a not in package scope",  strategy.InScope(a));
		assertTrue("a.A in package scope",   !strategy.InScope(a_A));
		assertTrue("a.A.a in package scope", !strategy.InScope(a_A_a));
		assertTrue("b not in package scope",  strategy.InScope(b));
		assertTrue("b.B in package scope",   !strategy.InScope(b_B));
		assertTrue("b.B.b in package scope", !strategy.InScope(b_B_b));
		assertTrue("c not in package scope",  strategy.InScope(c));
		assertTrue("c.C in package scope",   !strategy.InScope(c_C));
		assertTrue("c.C.c in package scope", !strategy.InScope(c_C_c));

		strategy.PackageScope(false);
		strategy.ClassScope(true);
		strategy.FeatureScope(false);

		assertTrue("a in package scope",       !strategy.InScope(a));
		assertTrue("a.A not in package scope",  strategy.InScope(a_A));
		assertTrue("a.A.a in package scope",   !strategy.InScope(a_A_a));
		assertTrue("b not in package scope",   !strategy.InScope(b));
		assertTrue("b.B in package scope",      strategy.InScope(b_B));
		assertTrue("b.B.b in package scope",   !strategy.InScope(b_B_b));
		assertTrue("c not in package scope",   !strategy.InScope(c));
		assertTrue("c.C in package scope",      strategy.InScope(c_C));
		assertTrue("c.C.c in package scope",   !strategy.InScope(c_C_c));

		strategy.PackageScope(false);
		strategy.ClassScope(false);
		strategy.FeatureScope(true);

		assertTrue("a in package scope",         !strategy.InScope(a));
		assertTrue("a.A in package scope",       !strategy.InScope(a_A));
		assertTrue("a.A.a not in package scope",  strategy.InScope(a_A_a));
		assertTrue("b not in package scope",     !strategy.InScope(b));
		assertTrue("b.B in package scope",       !strategy.InScope(b_B));
		assertTrue("b.B.b in package scope",      strategy.InScope(b_B_b));
		assertTrue("c not in package scope",     !strategy.InScope(c));
		assertTrue("c.C in package scope",       !strategy.InScope(c_C));
		assertTrue("c.C.c in package scope",      strategy.InScope(c_C_c));
	}

	public void testScopeIncludes() {
		strategy.ScopeIncludes(include);

		strategy.PackageScope(true);
		strategy.ClassScope(false);
		strategy.FeatureScope(false);

		assertTrue("a in package scope",     !strategy.InScope(a));
		assertTrue("a.A in package scope",   !strategy.InScope(a_A));
		assertTrue("a.A.a in package scope", !strategy.InScope(a_A_a));
		assertTrue("b not in package scope",  strategy.InScope(b));
		assertTrue("b.B in package scope",   !strategy.InScope(b_B));
		assertTrue("b.B.b in package scope", !strategy.InScope(b_B_b));
		assertTrue("c in package scope",     !strategy.InScope(c));
		assertTrue("c.C in package scope",   !strategy.InScope(c_C));
		assertTrue("c.C.c in package scope", !strategy.InScope(c_C_c));

		strategy.PackageScope(false);
		strategy.ClassScope(true);
		strategy.FeatureScope(false);

		assertTrue("a in package scope",       !strategy.InScope(a));
		assertTrue("a.A in package scope",     !strategy.InScope(a_A));
		assertTrue("a.A.a in package scope",   !strategy.InScope(a_A_a));
		assertTrue("b in package scope",       !strategy.InScope(b));
		assertTrue("b.B not in package scope",  strategy.InScope(b_B));
		assertTrue("b.B.b in package scope",   !strategy.InScope(b_B_b));
		assertTrue("c in package scope",       !strategy.InScope(c));
		assertTrue("c.C in package scope",     !strategy.InScope(c_C));
		assertTrue("c.C.c in package scope",   !strategy.InScope(c_C_c));

		strategy.PackageScope(false);
		strategy.ClassScope(false);
		strategy.FeatureScope(true);

		assertTrue("a in package scope",         !strategy.InScope(a));
		assertTrue("a.A in package scope",       !strategy.InScope(a_A));
		assertTrue("a.A.a in package scope",     !strategy.InScope(a_A_a));
		assertTrue("b in package scope",         !strategy.InScope(b));
		assertTrue("b.B in package scope",       !strategy.InScope(b_B));
		assertTrue("b.B.b not in package scope",  strategy.InScope(b_B_b));
		assertTrue("c in package scope",         !strategy.InScope(c));
		assertTrue("c.C in package scope",       !strategy.InScope(c_C));
		assertTrue("c.C.c in package scope",     !strategy.InScope(c_C_c));
	}

	public void testScopeExcludes() {
		strategy.ScopeExcludes(exclude);

		strategy.PackageScope(true);
		strategy.ClassScope(false);
		strategy.FeatureScope(false);

		assertTrue("a not in package scope",  strategy.InScope(a));
		assertTrue("a.A in package scope",   !strategy.InScope(a_A));
		assertTrue("a.A.a in package scope", !strategy.InScope(a_A_a));
		assertTrue("b not in package scope",  strategy.InScope(b));
		assertTrue("b.B in package scope",   !strategy.InScope(b_B));
		assertTrue("b.B.b in package scope", !strategy.InScope(b_B_b));
		assertTrue("c in package scope",     !strategy.InScope(c));
		assertTrue("c.C in package scope",   !strategy.InScope(c_C));
		assertTrue("c.C.c in package scope", !strategy.InScope(c_C_c));

		strategy.PackageScope(false);
		strategy.ClassScope(true);
		strategy.FeatureScope(false);

		assertTrue("a in package scope",       !strategy.InScope(a));
		assertTrue("a.A not in package scope",  strategy.InScope(a_A));
		assertTrue("a.A.a in package scope",   !strategy.InScope(a_A_a));
		assertTrue("b in package scope",       !strategy.InScope(b));
		assertTrue("b.B not in package scope",  strategy.InScope(b_B));
		assertTrue("b.B.b in package scope",   !strategy.InScope(b_B_b));
		assertTrue("c not in package scope",   !strategy.InScope(c));
		assertTrue("c.C in package scope",     !strategy.InScope(c_C));
		assertTrue("c.C.c in package scope",   !strategy.InScope(c_C_c));

		strategy.PackageScope(false);
		strategy.ClassScope(false);
		strategy.FeatureScope(true);

		assertTrue("a in package scope",         !strategy.InScope(a));
		assertTrue("a.A in package scope",       !strategy.InScope(a_A));
		assertTrue("a.A.a not in package scope",  strategy.InScope(a_A_a));
		assertTrue("b in package scope",         !strategy.InScope(b));
		assertTrue("b.B in package scope",       !strategy.InScope(b_B));
		assertTrue("b.B.b not in package scope",  strategy.InScope(b_B_b));
		assertTrue("c not in package scope",     !strategy.InScope(c));
		assertTrue("c.C in package scope",       !strategy.InScope(c_C));
		assertTrue("c.C.c in package scope",     !strategy.InScope(c_C_c));
	}

	public void testFilter() {
		strategy.PackageFilter(true);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);

		assertTrue("a not in package filter",  strategy.InFilter(a));
		assertTrue("a.A in package filter",   !strategy.InFilter(a_A));
		assertTrue("a.A.a in package filter", !strategy.InFilter(a_A_a));
		assertTrue("b not in package filter",  strategy.InFilter(b));
		assertTrue("b.B in package filter",   !strategy.InFilter(b_B));
		assertTrue("b.B.b in package filter", !strategy.InFilter(b_B_b));
		assertTrue("c not in package filter",  strategy.InFilter(c));
		assertTrue("c.C in package filter",   !strategy.InFilter(c_C));
		assertTrue("c.C.c in package filter", !strategy.InFilter(c_C_c));

		strategy.PackageFilter(false);
		strategy.ClassFilter(true);
		strategy.FeatureFilter(false);

		assertTrue("a in package filter",       !strategy.InFilter(a));
		assertTrue("a.A not in package filter",  strategy.InFilter(a_A));
		assertTrue("a.A.a in package filter",   !strategy.InFilter(a_A_a));
		assertTrue("b not in package filter",   !strategy.InFilter(b));
		assertTrue("b.B in package filter",      strategy.InFilter(b_B));
		assertTrue("b.B.b in package filter",   !strategy.InFilter(b_B_b));
		assertTrue("c not in package filter",   !strategy.InFilter(c));
		assertTrue("c.C in package filter",      strategy.InFilter(c_C));
		assertTrue("c.C.c in package filter",   !strategy.InFilter(c_C_c));

		strategy.PackageFilter(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(true);

		assertTrue("a in package filter",         !strategy.InFilter(a));
		assertTrue("a.A in package filter",       !strategy.InFilter(a_A));
		assertTrue("a.A.a not in package filter",  strategy.InFilter(a_A_a));
		assertTrue("b not in package filter",     !strategy.InFilter(b));
		assertTrue("b.B in package filter",       !strategy.InFilter(b_B));
		assertTrue("b.B.b in package filter",      strategy.InFilter(b_B_b));
		assertTrue("c not in package filter",     !strategy.InFilter(c));
		assertTrue("c.C in package filter",       !strategy.InFilter(c_C));
		assertTrue("c.C.c in package filter",      strategy.InFilter(c_C_c));
	}

	public void testFilterIncludes() {
		strategy.FilterIncludes(include);

		strategy.PackageFilter(true);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);

		assertTrue("a in package filter",     !strategy.InFilter(a));
		assertTrue("a.A in package filter",   !strategy.InFilter(a_A));
		assertTrue("a.A.a in package filter", !strategy.InFilter(a_A_a));
		assertTrue("b not in package filter",  strategy.InFilter(b));
		assertTrue("b.B in package filter",   !strategy.InFilter(b_B));
		assertTrue("b.B.b in package filter", !strategy.InFilter(b_B_b));
		assertTrue("c in package filter",     !strategy.InFilter(c));
		assertTrue("c.C in package filter",   !strategy.InFilter(c_C));
		assertTrue("c.C.c in package filter", !strategy.InFilter(c_C_c));

		strategy.PackageFilter(false);
		strategy.ClassFilter(true);
		strategy.FeatureFilter(false);

		assertTrue("a in package filter",       !strategy.InFilter(a));
		assertTrue("a.A in package filter",     !strategy.InFilter(a_A));
		assertTrue("a.A.a in package filter",   !strategy.InFilter(a_A_a));
		assertTrue("b in package filter",       !strategy.InFilter(b));
		assertTrue("b.B not in package filter",  strategy.InFilter(b_B));
		assertTrue("b.B.b in package filter",   !strategy.InFilter(b_B_b));
		assertTrue("c in package filter",       !strategy.InFilter(c));
		assertTrue("c.C in package filter",     !strategy.InFilter(c_C));
		assertTrue("c.C.c in package filter",   !strategy.InFilter(c_C_c));

		strategy.PackageFilter(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(true);

		assertTrue("a in package filter",         !strategy.InFilter(a));
		assertTrue("a.A in package filter",       !strategy.InFilter(a_A));
		assertTrue("a.A.a in package filter",     !strategy.InFilter(a_A_a));
		assertTrue("b in package filter",         !strategy.InFilter(b));
		assertTrue("b.B in package filter",       !strategy.InFilter(b_B));
		assertTrue("b.B.b not in package filter",  strategy.InFilter(b_B_b));
		assertTrue("c in package filter",         !strategy.InFilter(c));
		assertTrue("c.C in package filter",       !strategy.InFilter(c_C));
		assertTrue("c.C.c in package filter",     !strategy.InFilter(c_C_c));
	}

	public void testFilterExcludes() {
		strategy.FilterExcludes(exclude);

		strategy.PackageFilter(true);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);

		assertTrue("a not in package filter",  strategy.InFilter(a));
		assertTrue("a.A in package filter",   !strategy.InFilter(a_A));
		assertTrue("a.A.a in package filter", !strategy.InFilter(a_A_a));
		assertTrue("b not in package filter",  strategy.InFilter(b));
		assertTrue("b.B in package filter",   !strategy.InFilter(b_B));
		assertTrue("b.B.b in package filter", !strategy.InFilter(b_B_b));
		assertTrue("c in package filter",     !strategy.InFilter(c));
		assertTrue("c.C in package filter",   !strategy.InFilter(c_C));
		assertTrue("c.C.c in package filter", !strategy.InFilter(c_C_c));

		strategy.PackageFilter(false);
		strategy.ClassFilter(true);
		strategy.FeatureFilter(false);

		assertTrue("a in package filter",       !strategy.InFilter(a));
		assertTrue("a.A not in package filter",  strategy.InFilter(a_A));
		assertTrue("a.A.a in package filter",   !strategy.InFilter(a_A_a));
		assertTrue("b in package filter",       !strategy.InFilter(b));
		assertTrue("b.B not in package filter",  strategy.InFilter(b_B));
		assertTrue("b.B.b in package filter",   !strategy.InFilter(b_B_b));
		assertTrue("c in package filter",       !strategy.InFilter(c));
		assertTrue("c.C in package filter",     !strategy.InFilter(c_C));
		assertTrue("c.C.c in package filter",   !strategy.InFilter(c_C_c));

		strategy.PackageFilter(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(true);

		assertTrue("a in package filter",         !strategy.InFilter(a));
		assertTrue("a.A in package filter",       !strategy.InFilter(a_A));
		assertTrue("a.A.a not in package filter",  strategy.InFilter(a_A_a));
		assertTrue("b in package filter",         !strategy.InFilter(b));
		assertTrue("b.B in package filter",       !strategy.InFilter(b_B));
		assertTrue("b.B.b not in package filter",  strategy.InFilter(b_B_b));
		assertTrue("c in package filter",         !strategy.InFilter(c));
		assertTrue("c.C in package filter",       !strategy.InFilter(c_C));
		assertTrue("c.C.c in package filter",     !strategy.InFilter(c_C_c));
	}
}
