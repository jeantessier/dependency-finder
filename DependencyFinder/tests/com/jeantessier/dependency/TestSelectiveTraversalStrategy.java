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
 *  	* Neither the name of the Jean Tessier nor the names of his contributors
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

	public void testParseRE() {
		List expected = new ArrayList();
		expected.add("/test/");

		List test = SelectiveTraversalStrategy.ParseRE("/test/");
		
		assertEquals("size", expected.size(), test.size());
		assertEquals("/test/", expected.get(0), test.get(0));
	}

	public void testParseBrokenRE() {
		List expected = new ArrayList();
		expected.add("/test");

		List test = SelectiveTraversalStrategy.ParseRE("/test");
		
		assertEquals("size", expected.size(), test.size());
		assertEquals("/test", expected.get(0), test.get(0));
	}

	public void testParseMultipleREs() {
		List expected = new ArrayList();
		expected.add("/test1/");
		expected.add("/test2/");

		List test = SelectiveTraversalStrategy.ParseRE("/test1/,/test2/");
		
		assertEquals("size", expected.size(), test.size());
		assertEquals("/test1/", expected.get(0), test.get(0));
		assertEquals("/test2/", expected.get(1), test.get(1));
	}

	public void testParseComplexREs() {
		List expected = new ArrayList();
		expected.add("/test1\\/test2/");

		List test = SelectiveTraversalStrategy.ParseRE("/test1\\/test2/");
		
		assertEquals("size", expected.size(), test.size());
		assertEquals("/test1\\/test2/", expected.get(0), test.get(0));
	}

	public void testParseReallyComplexREs() {
		List expected = new ArrayList();
		expected.add("m=test1\\=test2=i");

		List test = SelectiveTraversalStrategy.ParseRE("m=test1\\=test2=i");
		
		assertEquals("size", expected.size(), test.size());
		assertEquals("m=test1\\=test2=i", expected.get(0), test.get(0));
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
