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

import java.util.*;

import junit.framework.*;

public class TestTransitiveClosure extends TestCase {
	private RegularExpressionSelectionCriteria scope_criteria;
	private RegularExpressionSelectionCriteria filter_criteria;
	private NodeFactory                        factory;

	private ClassNode A;
	private ClassNode B;
	private ClassNode C;
	private ClassNode D;

	private List includes;

	private TransitiveClosure selector;

	protected void setUp() throws Exception {
		scope_criteria  = new RegularExpressionSelectionCriteria();
		filter_criteria = new RegularExpressionSelectionCriteria();
		factory         = new NodeFactory();

		A = factory.CreateClass("A");
		B = factory.CreateClass("B");
		C = factory.CreateClass("C");
		D = factory.CreateClass("D");

		A.addDependency(B);
		A.addDependency(C);
		A.addDependency(D);

		B.addDependency(A);
		B.addDependency(C);
		B.addDependency(D);

		C.addDependency(A);
		C.addDependency(B);
		C.addDependency(D);

		D.addDependency(A);
		D.addDependency(B);
		D.addDependency(C);

		includes = new ArrayList(1);
		includes.add("//");

		scope_criteria.MatchPackage(false);
		scope_criteria.MatchFeature(false);
		scope_criteria.GlobalIncludes(includes);
		filter_criteria.MatchPackage(false);
		filter_criteria.MatchFeature(false);
		filter_criteria.GlobalIncludes(includes);

		selector = new TransitiveClosure(new SortedTraversalStrategy(new SelectiveTraversalStrategy(scope_criteria, filter_criteria)));
	}
	
	public void testFullConnectivity() {
		selector.SinglePath(false);

		A.accept(selector);
		B.accept(selector);
		C.accept(selector);
		D.accept(selector);

		assertEquals(4, selector.Factory().Classes().size());
		assertTrue(selector.Factory().Classes().values().contains(A));
		assertTrue(selector.Factory().Classes().values().contains(B));
		assertTrue(selector.Factory().Classes().values().contains(C));
		assertTrue(selector.Factory().Classes().values().contains(D));

		assertEquals(3, selector.Factory().CreateClass("A").getInboundDependencies().size());
		assertEquals(3, selector.Factory().CreateClass("A").getOutboundDependencies().size());
		assertEquals(3, selector.Factory().CreateClass("B").getInboundDependencies().size());
		assertEquals(3, selector.Factory().CreateClass("B").getOutboundDependencies().size());
		assertEquals(3, selector.Factory().CreateClass("C").getInboundDependencies().size());
		assertEquals(3, selector.Factory().CreateClass("C").getOutboundDependencies().size());
		assertEquals(3, selector.Factory().CreateClass("D").getInboundDependencies().size());
		assertEquals(3, selector.Factory().CreateClass("D").getOutboundDependencies().size());

		assertTrue(selector.Factory().CreateClass("A").getInboundDependencies().contains(B));
		assertTrue(selector.Factory().CreateClass("A").getInboundDependencies().contains(C));
		assertTrue(selector.Factory().CreateClass("A").getInboundDependencies().contains(D));
		assertTrue(selector.Factory().CreateClass("A").getOutboundDependencies().contains(B));
		assertTrue(selector.Factory().CreateClass("A").getOutboundDependencies().contains(C));
		assertTrue(selector.Factory().CreateClass("A").getOutboundDependencies().contains(D));
		assertTrue(selector.Factory().CreateClass("B").getInboundDependencies().contains(A));
		assertTrue(selector.Factory().CreateClass("B").getInboundDependencies().contains(C));
		assertTrue(selector.Factory().CreateClass("B").getInboundDependencies().contains(D));
		assertTrue(selector.Factory().CreateClass("B").getOutboundDependencies().contains(A));
		assertTrue(selector.Factory().CreateClass("B").getOutboundDependencies().contains(C));
		assertTrue(selector.Factory().CreateClass("B").getOutboundDependencies().contains(D));
		assertTrue(selector.Factory().CreateClass("C").getInboundDependencies().contains(A));
		assertTrue(selector.Factory().CreateClass("C").getInboundDependencies().contains(B));
		assertTrue(selector.Factory().CreateClass("C").getInboundDependencies().contains(D));
		assertTrue(selector.Factory().CreateClass("C").getOutboundDependencies().contains(A));
		assertTrue(selector.Factory().CreateClass("C").getOutboundDependencies().contains(B));
		assertTrue(selector.Factory().CreateClass("C").getOutboundDependencies().contains(D));
		assertTrue(selector.Factory().CreateClass("D").getInboundDependencies().contains(A));
		assertTrue(selector.Factory().CreateClass("D").getInboundDependencies().contains(B));
		assertTrue(selector.Factory().CreateClass("D").getInboundDependencies().contains(C));
		assertTrue(selector.Factory().CreateClass("D").getOutboundDependencies().contains(A));
		assertTrue(selector.Factory().CreateClass("D").getOutboundDependencies().contains(B));
		assertTrue(selector.Factory().CreateClass("D").getOutboundDependencies().contains(C));
	}

	public void testSinglePathThroughFullConnectivity() {
		selector.SinglePath(true);

		A.accept(selector);
		B.accept(selector);
		C.accept(selector);
		D.accept(selector);

		assertEquals(4, selector.Factory().Classes().size());
		assertTrue(selector.Factory().Classes().values().contains(A));
		assertTrue(selector.Factory().Classes().values().contains(B));
		assertTrue(selector.Factory().Classes().values().contains(C));
		assertTrue(selector.Factory().Classes().values().contains(D));

		assertEquals(0, selector.Factory().CreateClass("A").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("A").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("B").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("B").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("C").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("C").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("D").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("D").getOutboundDependencies().size());

		assertTrue(selector.Factory().CreateClass("A").getInboundDependencies().isEmpty());
		assertTrue(selector.Factory().CreateClass("A").getOutboundDependencies().contains(B));
		assertTrue(selector.Factory().CreateClass("B").getInboundDependencies().contains(A));
		assertTrue(selector.Factory().CreateClass("B").getOutboundDependencies().contains(C));
		assertTrue(selector.Factory().CreateClass("C").getInboundDependencies().contains(B));
		assertTrue(selector.Factory().CreateClass("C").getOutboundDependencies().contains(D));
		assertTrue(selector.Factory().CreateClass("D").getInboundDependencies().contains(C));
		assertTrue(selector.Factory().CreateClass("D").getOutboundDependencies().isEmpty());
	}
}
