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

import java.util.*;

import junit.framework.*;

public class TestTransitiveClosure extends TestCase {
	private SelectiveTraversalStrategy strategy = new SelectiveTraversalStrategy();
	private NodeFactory                factory  = new NodeFactory();

	public void testFullConnectivity() {
		ClassNode A = factory.CreateClass("A");
		ClassNode B = factory.CreateClass("B");
		ClassNode C = factory.CreateClass("C");
		ClassNode D = factory.CreateClass("D");

		A.AddDependency(B);
		A.AddDependency(C);
		A.AddDependency(D);

		B.AddDependency(A);
		B.AddDependency(C);
		B.AddDependency(D);

		C.AddDependency(A);
		C.AddDependency(B);
		C.AddDependency(D);

		D.AddDependency(A);
		D.AddDependency(B);
		D.AddDependency(C);

		List includes = new ArrayList(1);
		includes.add("//");
		
		strategy.ScopeIncludes(includes);
		strategy.PackageScope(false);
		strategy.FeatureScope(false);
		strategy.FilterIncludes(includes);
		strategy.PackageFilter(false);
		strategy.FeatureFilter(false);

		TransitiveClosure selector = new TransitiveClosure(new SortedTraversalStrategy(strategy));
		selector.SinglePath(false);

		A.Accept(selector);
		B.Accept(selector);
		C.Accept(selector);
		D.Accept(selector);

		assertEquals(4, selector.Factory().Classes().size());
		assertTrue(selector.Factory().Classes().values().contains(A));
		assertTrue(selector.Factory().Classes().values().contains(B));
		assertTrue(selector.Factory().Classes().values().contains(C));
		assertTrue(selector.Factory().Classes().values().contains(D));

		assertEquals(3, selector.Factory().CreateClass("A").Inbound().size());
		assertEquals(3, selector.Factory().CreateClass("A").Outbound().size());
		assertEquals(3, selector.Factory().CreateClass("B").Inbound().size());
		assertEquals(3, selector.Factory().CreateClass("B").Outbound().size());
		assertEquals(3, selector.Factory().CreateClass("C").Inbound().size());
		assertEquals(3, selector.Factory().CreateClass("C").Outbound().size());
		assertEquals(3, selector.Factory().CreateClass("D").Inbound().size());
		assertEquals(3, selector.Factory().CreateClass("D").Outbound().size());

		assertTrue(selector.Factory().CreateClass("A").Inbound().contains(B));
		assertTrue(selector.Factory().CreateClass("A").Inbound().contains(C));
		assertTrue(selector.Factory().CreateClass("A").Inbound().contains(D));
		assertTrue(selector.Factory().CreateClass("A").Outbound().contains(B));
		assertTrue(selector.Factory().CreateClass("A").Outbound().contains(C));
		assertTrue(selector.Factory().CreateClass("A").Outbound().contains(D));
		assertTrue(selector.Factory().CreateClass("B").Inbound().contains(A));
		assertTrue(selector.Factory().CreateClass("B").Inbound().contains(C));
		assertTrue(selector.Factory().CreateClass("B").Inbound().contains(D));
		assertTrue(selector.Factory().CreateClass("B").Outbound().contains(A));
		assertTrue(selector.Factory().CreateClass("B").Outbound().contains(C));
		assertTrue(selector.Factory().CreateClass("B").Outbound().contains(D));
		assertTrue(selector.Factory().CreateClass("C").Inbound().contains(A));
		assertTrue(selector.Factory().CreateClass("C").Inbound().contains(B));
		assertTrue(selector.Factory().CreateClass("C").Inbound().contains(D));
		assertTrue(selector.Factory().CreateClass("C").Outbound().contains(A));
		assertTrue(selector.Factory().CreateClass("C").Outbound().contains(B));
		assertTrue(selector.Factory().CreateClass("C").Outbound().contains(D));
		assertTrue(selector.Factory().CreateClass("D").Inbound().contains(A));
		assertTrue(selector.Factory().CreateClass("D").Inbound().contains(B));
		assertTrue(selector.Factory().CreateClass("D").Inbound().contains(C));
		assertTrue(selector.Factory().CreateClass("D").Outbound().contains(A));
		assertTrue(selector.Factory().CreateClass("D").Outbound().contains(B));
		assertTrue(selector.Factory().CreateClass("D").Outbound().contains(C));
	}

	public void testSinglePathThroughFullConnectivity() {
		ClassNode A = factory.CreateClass("A");
		ClassNode B = factory.CreateClass("B");
		ClassNode C = factory.CreateClass("C");
		ClassNode D = factory.CreateClass("D");

		A.AddDependency(B);
		A.AddDependency(C);
		A.AddDependency(D);

		B.AddDependency(A);
		B.AddDependency(C);
		B.AddDependency(D);

		C.AddDependency(A);
		C.AddDependency(B);
		C.AddDependency(D);

		D.AddDependency(A);
		D.AddDependency(B);
		D.AddDependency(C);

		List includes = new ArrayList(1);
		includes.add("//");
		
		strategy.ScopeIncludes(includes);
		strategy.PackageScope(false);
		strategy.FeatureScope(false);
		strategy.FilterIncludes(includes);
		strategy.PackageFilter(false);
		strategy.FeatureFilter(false);

		TransitiveClosure selector = new TransitiveClosure(new SortedTraversalStrategy(strategy));
		selector.SinglePath(true);

		A.Accept(selector);
		B.Accept(selector);
		C.Accept(selector);
		D.Accept(selector);

		assertEquals(4, selector.Factory().Classes().size());
		assertTrue(selector.Factory().Classes().values().contains(A));
		assertTrue(selector.Factory().Classes().values().contains(B));
		assertTrue(selector.Factory().Classes().values().contains(C));
		assertTrue(selector.Factory().Classes().values().contains(D));

		assertEquals(0, selector.Factory().CreateClass("A").Inbound().size());
		assertEquals(1, selector.Factory().CreateClass("A").Outbound().size());
		assertEquals(1, selector.Factory().CreateClass("B").Inbound().size());
		assertEquals(1, selector.Factory().CreateClass("B").Outbound().size());
		assertEquals(1, selector.Factory().CreateClass("C").Inbound().size());
		assertEquals(1, selector.Factory().CreateClass("C").Outbound().size());
		assertEquals(1, selector.Factory().CreateClass("D").Inbound().size());
		assertEquals(0, selector.Factory().CreateClass("D").Outbound().size());

		assertTrue(selector.Factory().CreateClass("A").Inbound().isEmpty());
		assertTrue(selector.Factory().CreateClass("A").Outbound().contains(B));
		assertTrue(selector.Factory().CreateClass("B").Inbound().contains(A));
		assertTrue(selector.Factory().CreateClass("B").Outbound().contains(C));
		assertTrue(selector.Factory().CreateClass("C").Inbound().contains(B));
		assertTrue(selector.Factory().CreateClass("C").Outbound().contains(D));
		assertTrue(selector.Factory().CreateClass("D").Inbound().contains(C));
		assertTrue(selector.Factory().CreateClass("D").Outbound().isEmpty());
	}
}
