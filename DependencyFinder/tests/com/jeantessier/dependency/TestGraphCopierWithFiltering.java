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

public class TestGraphCopierWithFiltering extends TestCase {
	private RegularExpressionSelectionCriteria scope_criteria;
	private RegularExpressionSelectionCriteria filter_criteria;
	private NodeFactory                        factory;
	
	private Node a;
	private Node a_A;
	private Node a_A_a;
	
	private Node b;
	private Node b_B;
	private Node b_B_b;
	
	private Node c;
	private Node c_C;
	private Node c_C_c;

	private List include_filter;
	private List exclude_filter;

	private GraphCopier copier;

	protected void setUp() throws Exception {
		scope_criteria  = new RegularExpressionSelectionCriteria();
		filter_criteria = new RegularExpressionSelectionCriteria();
		factory         = new NodeFactory();

		a     = factory.CreatePackage("a");
		a_A   = factory.CreateClass("a.A");
		a_A_a = factory.CreateFeature("a.A.a");
		
		b     = factory.CreatePackage("b");
		b_B   = factory.CreateClass("b.B");
		b_B_b = factory.CreateFeature("b.B.b");
		
		c     = factory.CreatePackage("c");
		c_C   = factory.CreateClass("c.C");
		c_C_c = factory.CreateFeature("c.C.c");
		
		include_filter = new LinkedList();
		include_filter.add("/^b/");
		
		exclude_filter = new LinkedList();
		exclude_filter.add("/^c/");

		copier = new GraphCopier(new SelectiveTraversalStrategy(scope_criteria, filter_criteria));
	}

	public void testIncludeFilterF2FtoP2P() {
		a_A_a.AddDependency(b_B_b);
		a_A_a.AddDependency(c_C_c);
		
		scope_criteria.MatchClass(false);
		scope_criteria.MatchFeature(false);
		filter_criteria.MatchClass(false);
		filter_criteria.MatchFeature(false);
		filter_criteria.GlobalIncludes(include_filter);
		
		copier.TraverseNodes(factory.Packages().values());

		assertTrue(copier.ScopeFactory().CreatePackage("a").Inbound().isEmpty());
		assertTrue(copier.ScopeFactory().CreatePackage("a").Outbound().isEmpty());
	}

	public void testExcludeFilterF2FtoP2P() {
		a_A_a.AddDependency(b_B_b);
		a_A_a.AddDependency(c_C_c);
		
		scope_criteria.MatchClass(false);
		scope_criteria.MatchFeature(false);
		filter_criteria.MatchClass(false);
		filter_criteria.MatchFeature(false);
		filter_criteria.GlobalExcludes(exclude_filter);
		
		copier.TraverseNodes(factory.Packages().values());

		assertTrue(copier.ScopeFactory().CreatePackage("a").Inbound().isEmpty());
		assertTrue(copier.ScopeFactory().CreatePackage("a").Outbound().isEmpty());
	}
}
