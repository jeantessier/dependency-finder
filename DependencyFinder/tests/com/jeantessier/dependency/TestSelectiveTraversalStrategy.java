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

public class TestSelectiveTraversalStrategy extends TestCase {
	private RegularExpressionSelectionCriteria scope_criteria;
	private RegularExpressionSelectionCriteria filter_criteria;
	private SelectiveTraversalStrategy         strategy;
	private NodeFactory                        factory;

	private PackageNode a;
	private ClassNode a_A;
	private FeatureNode a_A_a;
	
	private PackageNode b;
	private ClassNode b_B;
	private FeatureNode b_B_b;
	
	private PackageNode c;
	private ClassNode c_C;
	private FeatureNode c_C_c;

	private List include;
	private List exclude;

	protected void setUp() throws Exception {
		scope_criteria  = new RegularExpressionSelectionCriteria();
		filter_criteria = new RegularExpressionSelectionCriteria();
		strategy        = new SelectiveTraversalStrategy(scope_criteria, filter_criteria);
		factory         = new NodeFactory();

		a     = factory.createPackage("a");
		a_A   = factory.createClass("a.A");
		a_A_a = factory.createFeature("a.A.a");
		
		b     = factory.createPackage("b");
		b_B   = factory.createClass("b.B");
		b_B_b = factory.createFeature("b.B.b");
		
		c     = factory.createPackage("c");
		c_C   = factory.createClass("c.C");
		c_C_c = factory.createFeature("c.C.c");
		
		include = new LinkedList();
		include.add("/^b/");
		
		exclude = new LinkedList();
		exclude.add("/^c/");
	}
	
	public void testScope() {
		scope_criteria.setMatchingPackages(true);
		scope_criteria.setMatchingClasses(false);
		scope_criteria.setMatchingFeatures(false);

		assertTrue("a not in package scope",  strategy.isInScope(a));
		assertTrue("a.A in package scope",   !strategy.isInScope(a_A));
		assertTrue("a.A.a in package scope", !strategy.isInScope(a_A_a));
		assertTrue("b not in package scope",  strategy.isInScope(b));
		assertTrue("b.B in package scope",   !strategy.isInScope(b_B));
		assertTrue("b.B.b in package scope", !strategy.isInScope(b_B_b));
		assertTrue("c not in package scope",  strategy.isInScope(c));
		assertTrue("c.C in package scope",   !strategy.isInScope(c_C));
		assertTrue("c.C.c in package scope", !strategy.isInScope(c_C_c));

		scope_criteria.setMatchingPackages(false);
		scope_criteria.setMatchingClasses(true);
		scope_criteria.setMatchingFeatures(false);

		assertTrue("a in package scope",       !strategy.isInScope(a));
		assertTrue("a.A not in package scope",  strategy.isInScope(a_A));
		assertTrue("a.A.a in package scope",   !strategy.isInScope(a_A_a));
		assertTrue("b not in package scope",   !strategy.isInScope(b));
		assertTrue("b.B in package scope",      strategy.isInScope(b_B));
		assertTrue("b.B.b in package scope",   !strategy.isInScope(b_B_b));
		assertTrue("c not in package scope",   !strategy.isInScope(c));
		assertTrue("c.C in package scope",      strategy.isInScope(c_C));
		assertTrue("c.C.c in package scope",   !strategy.isInScope(c_C_c));

		scope_criteria.setMatchingPackages(false);
		scope_criteria.setMatchingClasses(false);
		scope_criteria.setMatchingFeatures(true);

		assertTrue("a in package scope",         !strategy.isInScope(a));
		assertTrue("a.A in package scope",       !strategy.isInScope(a_A));
		assertTrue("a.A.a not in package scope",  strategy.isInScope(a_A_a));
		assertTrue("b not in package scope",     !strategy.isInScope(b));
		assertTrue("b.B in package scope",       !strategy.isInScope(b_B));
		assertTrue("b.B.b in package scope",      strategy.isInScope(b_B_b));
		assertTrue("c not in package scope",     !strategy.isInScope(c));
		assertTrue("c.C in package scope",       !strategy.isInScope(c_C));
		assertTrue("c.C.c in package scope",      strategy.isInScope(c_C_c));
	}

	public void testScopeIncludes() {
		scope_criteria.setGlobalIncludes(include);

		scope_criteria.setMatchingPackages(true);
		scope_criteria.setMatchingClasses(false);
		scope_criteria.setMatchingFeatures(false);

		assertTrue("a in package scope",     !strategy.isInScope(a));
		assertTrue("a.A in package scope",   !strategy.isInScope(a_A));
		assertTrue("a.A.a in package scope", !strategy.isInScope(a_A_a));
		assertTrue("b not in package scope",  strategy.isInScope(b));
		assertTrue("b.B in package scope",   !strategy.isInScope(b_B));
		assertTrue("b.B.b in package scope", !strategy.isInScope(b_B_b));
		assertTrue("c in package scope",     !strategy.isInScope(c));
		assertTrue("c.C in package scope",   !strategy.isInScope(c_C));
		assertTrue("c.C.c in package scope", !strategy.isInScope(c_C_c));

		scope_criteria.setMatchingPackages(false);
		scope_criteria.setMatchingClasses(true);
		scope_criteria.setMatchingFeatures(false);

		assertTrue("a in package scope",       !strategy.isInScope(a));
		assertTrue("a.A in package scope",     !strategy.isInScope(a_A));
		assertTrue("a.A.a in package scope",   !strategy.isInScope(a_A_a));
		assertTrue("b in package scope",       !strategy.isInScope(b));
		assertTrue("b.B not in package scope",  strategy.isInScope(b_B));
		assertTrue("b.B.b in package scope",   !strategy.isInScope(b_B_b));
		assertTrue("c in package scope",       !strategy.isInScope(c));
		assertTrue("c.C in package scope",     !strategy.isInScope(c_C));
		assertTrue("c.C.c in package scope",   !strategy.isInScope(c_C_c));

		scope_criteria.setMatchingPackages(false);
		scope_criteria.setMatchingClasses(false);
		scope_criteria.setMatchingFeatures(true);

		assertTrue("a in package scope",         !strategy.isInScope(a));
		assertTrue("a.A in package scope",       !strategy.isInScope(a_A));
		assertTrue("a.A.a in package scope",     !strategy.isInScope(a_A_a));
		assertTrue("b in package scope",         !strategy.isInScope(b));
		assertTrue("b.B in package scope",       !strategy.isInScope(b_B));
		assertTrue("b.B.b not in package scope",  strategy.isInScope(b_B_b));
		assertTrue("c in package scope",         !strategy.isInScope(c));
		assertTrue("c.C in package scope",       !strategy.isInScope(c_C));
		assertTrue("c.C.c in package scope",     !strategy.isInScope(c_C_c));
	}

	public void testScopeExcludes() {
		scope_criteria.setGlobalExcludes(exclude);

		scope_criteria.setMatchingPackages(true);
		scope_criteria.setMatchingClasses(false);
		scope_criteria.setMatchingFeatures(false);

		assertTrue("a not in package scope",  strategy.isInScope(a));
		assertTrue("a.A in package scope",   !strategy.isInScope(a_A));
		assertTrue("a.A.a in package scope", !strategy.isInScope(a_A_a));
		assertTrue("b not in package scope",  strategy.isInScope(b));
		assertTrue("b.B in package scope",   !strategy.isInScope(b_B));
		assertTrue("b.B.b in package scope", !strategy.isInScope(b_B_b));
		assertTrue("c in package scope",     !strategy.isInScope(c));
		assertTrue("c.C in package scope",   !strategy.isInScope(c_C));
		assertTrue("c.C.c in package scope", !strategy.isInScope(c_C_c));

		scope_criteria.setMatchingPackages(false);
		scope_criteria.setMatchingClasses(true);
		scope_criteria.setMatchingFeatures(false);

		assertTrue("a in package scope",       !strategy.isInScope(a));
		assertTrue("a.A not in package scope",  strategy.isInScope(a_A));
		assertTrue("a.A.a in package scope",   !strategy.isInScope(a_A_a));
		assertTrue("b in package scope",       !strategy.isInScope(b));
		assertTrue("b.B not in package scope",  strategy.isInScope(b_B));
		assertTrue("b.B.b in package scope",   !strategy.isInScope(b_B_b));
		assertTrue("c not in package scope",   !strategy.isInScope(c));
		assertTrue("c.C in package scope",     !strategy.isInScope(c_C));
		assertTrue("c.C.c in package scope",   !strategy.isInScope(c_C_c));

		scope_criteria.setMatchingPackages(false);
		scope_criteria.setMatchingClasses(false);
		scope_criteria.setMatchingFeatures(true);

		assertTrue("a in package scope",         !strategy.isInScope(a));
		assertTrue("a.A in package scope",       !strategy.isInScope(a_A));
		assertTrue("a.A.a not in package scope",  strategy.isInScope(a_A_a));
		assertTrue("b in package scope",         !strategy.isInScope(b));
		assertTrue("b.B in package scope",       !strategy.isInScope(b_B));
		assertTrue("b.B.b not in package scope",  strategy.isInScope(b_B_b));
		assertTrue("c not in package scope",     !strategy.isInScope(c));
		assertTrue("c.C in package scope",       !strategy.isInScope(c_C));
		assertTrue("c.C.c in package scope",     !strategy.isInScope(c_C_c));
	}

	public void testFilter() {
		filter_criteria.setMatchingPackages(true);
		filter_criteria.setMatchingClasses(false);
		filter_criteria.setMatchingFeatures(false);

		assertTrue("a not in package filter",  strategy.isInFilter(a));
		assertTrue("a.A in package filter",   !strategy.isInFilter(a_A));
		assertTrue("a.A.a in package filter", !strategy.isInFilter(a_A_a));
		assertTrue("b not in package filter",  strategy.isInFilter(b));
		assertTrue("b.B in package filter",   !strategy.isInFilter(b_B));
		assertTrue("b.B.b in package filter", !strategy.isInFilter(b_B_b));
		assertTrue("c not in package filter",  strategy.isInFilter(c));
		assertTrue("c.C in package filter",   !strategy.isInFilter(c_C));
		assertTrue("c.C.c in package filter", !strategy.isInFilter(c_C_c));

		filter_criteria.setMatchingPackages(false);
		filter_criteria.setMatchingClasses(true);
		filter_criteria.setMatchingFeatures(false);

		assertTrue("a in package filter",       !strategy.isInFilter(a));
		assertTrue("a.A not in package filter",  strategy.isInFilter(a_A));
		assertTrue("a.A.a in package filter",   !strategy.isInFilter(a_A_a));
		assertTrue("b not in package filter",   !strategy.isInFilter(b));
		assertTrue("b.B in package filter",      strategy.isInFilter(b_B));
		assertTrue("b.B.b in package filter",   !strategy.isInFilter(b_B_b));
		assertTrue("c not in package filter",   !strategy.isInFilter(c));
		assertTrue("c.C in package filter",      strategy.isInFilter(c_C));
		assertTrue("c.C.c in package filter",   !strategy.isInFilter(c_C_c));

		filter_criteria.setMatchingPackages(false);
		filter_criteria.setMatchingClasses(false);
		filter_criteria.setMatchingFeatures(true);

		assertTrue("a in package filter",         !strategy.isInFilter(a));
		assertTrue("a.A in package filter",       !strategy.isInFilter(a_A));
		assertTrue("a.A.a not in package filter",  strategy.isInFilter(a_A_a));
		assertTrue("b not in package filter",     !strategy.isInFilter(b));
		assertTrue("b.B in package filter",       !strategy.isInFilter(b_B));
		assertTrue("b.B.b in package filter",      strategy.isInFilter(b_B_b));
		assertTrue("c not in package filter",     !strategy.isInFilter(c));
		assertTrue("c.C in package filter",       !strategy.isInFilter(c_C));
		assertTrue("c.C.c in package filter",      strategy.isInFilter(c_C_c));
	}

	public void testFilterIncludes() {
		filter_criteria.setGlobalIncludes(include);

		filter_criteria.setMatchingPackages(true);
		filter_criteria.setMatchingClasses(false);
		filter_criteria.setMatchingFeatures(false);

		assertTrue("a in package filter",     !strategy.isInFilter(a));
		assertTrue("a.A in package filter",   !strategy.isInFilter(a_A));
		assertTrue("a.A.a in package filter", !strategy.isInFilter(a_A_a));
		assertTrue("b not in package filter",  strategy.isInFilter(b));
		assertTrue("b.B in package filter",   !strategy.isInFilter(b_B));
		assertTrue("b.B.b in package filter", !strategy.isInFilter(b_B_b));
		assertTrue("c in package filter",     !strategy.isInFilter(c));
		assertTrue("c.C in package filter",   !strategy.isInFilter(c_C));
		assertTrue("c.C.c in package filter", !strategy.isInFilter(c_C_c));

		filter_criteria.setMatchingPackages(false);
		filter_criteria.setMatchingClasses(true);
		filter_criteria.setMatchingFeatures(false);

		assertTrue("a in package filter",       !strategy.isInFilter(a));
		assertTrue("a.A in package filter",     !strategy.isInFilter(a_A));
		assertTrue("a.A.a in package filter",   !strategy.isInFilter(a_A_a));
		assertTrue("b in package filter",       !strategy.isInFilter(b));
		assertTrue("b.B not in package filter",  strategy.isInFilter(b_B));
		assertTrue("b.B.b in package filter",   !strategy.isInFilter(b_B_b));
		assertTrue("c in package filter",       !strategy.isInFilter(c));
		assertTrue("c.C in package filter",     !strategy.isInFilter(c_C));
		assertTrue("c.C.c in package filter",   !strategy.isInFilter(c_C_c));

		filter_criteria.setMatchingPackages(false);
		filter_criteria.setMatchingClasses(false);
		filter_criteria.setMatchingFeatures(true);

		assertTrue("a in package filter",         !strategy.isInFilter(a));
		assertTrue("a.A in package filter",       !strategy.isInFilter(a_A));
		assertTrue("a.A.a in package filter",     !strategy.isInFilter(a_A_a));
		assertTrue("b in package filter",         !strategy.isInFilter(b));
		assertTrue("b.B in package filter",       !strategy.isInFilter(b_B));
		assertTrue("b.B.b not in package filter",  strategy.isInFilter(b_B_b));
		assertTrue("c in package filter",         !strategy.isInFilter(c));
		assertTrue("c.C in package filter",       !strategy.isInFilter(c_C));
		assertTrue("c.C.c in package filter",     !strategy.isInFilter(c_C_c));
	}

	public void testFilterExcludes() {
		filter_criteria.setGlobalExcludes(exclude);

		filter_criteria.setMatchingPackages(true);
		filter_criteria.setMatchingClasses(false);
		filter_criteria.setMatchingFeatures(false);

		assertTrue("a not in package filter",  strategy.isInFilter(a));
		assertTrue("a.A in package filter",   !strategy.isInFilter(a_A));
		assertTrue("a.A.a in package filter", !strategy.isInFilter(a_A_a));
		assertTrue("b not in package filter",  strategy.isInFilter(b));
		assertTrue("b.B in package filter",   !strategy.isInFilter(b_B));
		assertTrue("b.B.b in package filter", !strategy.isInFilter(b_B_b));
		assertTrue("c in package filter",     !strategy.isInFilter(c));
		assertTrue("c.C in package filter",   !strategy.isInFilter(c_C));
		assertTrue("c.C.c in package filter", !strategy.isInFilter(c_C_c));

		filter_criteria.setMatchingPackages(false);
		filter_criteria.setMatchingClasses(true);
		filter_criteria.setMatchingFeatures(false);

		assertTrue("a in package filter",       !strategy.isInFilter(a));
		assertTrue("a.A not in package filter",  strategy.isInFilter(a_A));
		assertTrue("a.A.a in package filter",   !strategy.isInFilter(a_A_a));
		assertTrue("b in package filter",       !strategy.isInFilter(b));
		assertTrue("b.B not in package filter",  strategy.isInFilter(b_B));
		assertTrue("b.B.b in package filter",   !strategy.isInFilter(b_B_b));
		assertTrue("c in package filter",       !strategy.isInFilter(c));
		assertTrue("c.C in package filter",     !strategy.isInFilter(c_C));
		assertTrue("c.C.c in package filter",   !strategy.isInFilter(c_C_c));

		filter_criteria.setMatchingPackages(false);
		filter_criteria.setMatchingClasses(false);
		filter_criteria.setMatchingFeatures(true);

		assertTrue("a in package filter",         !strategy.isInFilter(a));
		assertTrue("a.A in package filter",       !strategy.isInFilter(a_A));
		assertTrue("a.A.a not in package filter",  strategy.isInFilter(a_A_a));
		assertTrue("b in package filter",         !strategy.isInFilter(b));
		assertTrue("b.B in package filter",       !strategy.isInFilter(b_B));
		assertTrue("b.B.b not in package filter",  strategy.isInFilter(b_B_b));
		assertTrue("c in package filter",         !strategy.isInFilter(c));
		assertTrue("c.C in package filter",       !strategy.isInFilter(c_C));
		assertTrue("c.C.c in package filter",     !strategy.isInFilter(c_C_c));
	}
}
