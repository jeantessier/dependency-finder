/*
 *  Copyright (c) 2001-2002, Jean Tessier
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

public class TestGraphCopierWithFiltering extends TestCase {
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

	List include_filter;
	List exclude_filter;

    GraphCopier copier;

	public TestGraphCopierWithFiltering(String name) {
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
		
		include_filter = new LinkedList();
		include_filter.add("/^b/");
		
		exclude_filter = new LinkedList();
		exclude_filter.add("/^c/");

		copier = new GraphCopier(strategy);
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
		assertTrue(copier.Factory().CreatePackage("a").Outbound().isEmpty());
	}

	public void testExcludeFilterF2FtoP2P() {
		a_A_a.AddDependency(b_B_b);
		a_A_a.AddDependency(c_C_c);
		
		strategy.ClassScope(false);
		strategy.FeatureScope(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);
		strategy.FilterExcludes(exclude_filter);
		
		copier.TraverseNodes(factory.Packages().values());

		assertTrue(copier.Factory().CreatePackage("a").Inbound().isEmpty());
		assertTrue(copier.Factory().CreatePackage("a").Outbound().isEmpty());
	}
}
