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
	private RegularExpressionSelectionCriteria scopeCriteria;
	private RegularExpressionSelectionCriteria filterCriteria;
	private NodeFactory                        factory;

	private ClassNode A;
	private ClassNode B;
	private ClassNode C;
	private ClassNode D;

	private List includes;

	private TransitiveClosure selector;

	protected void setUp() throws Exception {
		scopeCriteria  = new RegularExpressionSelectionCriteria();
		filterCriteria = new RegularExpressionSelectionCriteria();
		factory        = new NodeFactory();

		A = factory.createClass("A");
		B = factory.createClass("B");
		C = factory.createClass("C");
		D = factory.createClass("D");

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

		scopeCriteria.setMatchingPackages(false);
		scopeCriteria.setMatchingFeatures(false);
		scopeCriteria.setGlobalIncludes(includes);
		filterCriteria.setMatchingPackages(false);
		filterCriteria.setMatchingFeatures(false);
		filterCriteria.setGlobalIncludes(includes);

		selector = new TransitiveClosure(new SortedTraversalStrategy(new SelectiveTraversalStrategy(scopeCriteria, filterCriteria)));
	}
	
	public void testFullConnectivity() {
		selector.setSinglePath(false);

		A.accept(selector);
		B.accept(selector);
		C.accept(selector);
		D.accept(selector);

		assertEquals(4, selector.getFactory().getClasses().size());
		assertTrue(selector.getFactory().getClasses().values().contains(A));
		assertTrue(selector.getFactory().getClasses().values().contains(B));
		assertTrue(selector.getFactory().getClasses().values().contains(C));
		assertTrue(selector.getFactory().getClasses().values().contains(D));

		assertEquals(3, selector.getFactory().createClass("A").getInboundDependencies().size());
		assertEquals(3, selector.getFactory().createClass("A").getOutboundDependencies().size());
		assertEquals(3, selector.getFactory().createClass("B").getInboundDependencies().size());
		assertEquals(3, selector.getFactory().createClass("B").getOutboundDependencies().size());
		assertEquals(3, selector.getFactory().createClass("C").getInboundDependencies().size());
		assertEquals(3, selector.getFactory().createClass("C").getOutboundDependencies().size());
		assertEquals(3, selector.getFactory().createClass("D").getInboundDependencies().size());
		assertEquals(3, selector.getFactory().createClass("D").getOutboundDependencies().size());

		assertTrue(selector.getFactory().createClass("A").getInboundDependencies().contains(B));
		assertTrue(selector.getFactory().createClass("A").getInboundDependencies().contains(C));
		assertTrue(selector.getFactory().createClass("A").getInboundDependencies().contains(D));
		assertTrue(selector.getFactory().createClass("A").getOutboundDependencies().contains(B));
		assertTrue(selector.getFactory().createClass("A").getOutboundDependencies().contains(C));
		assertTrue(selector.getFactory().createClass("A").getOutboundDependencies().contains(D));
		assertTrue(selector.getFactory().createClass("B").getInboundDependencies().contains(A));
		assertTrue(selector.getFactory().createClass("B").getInboundDependencies().contains(C));
		assertTrue(selector.getFactory().createClass("B").getInboundDependencies().contains(D));
		assertTrue(selector.getFactory().createClass("B").getOutboundDependencies().contains(A));
		assertTrue(selector.getFactory().createClass("B").getOutboundDependencies().contains(C));
		assertTrue(selector.getFactory().createClass("B").getOutboundDependencies().contains(D));
		assertTrue(selector.getFactory().createClass("C").getInboundDependencies().contains(A));
		assertTrue(selector.getFactory().createClass("C").getInboundDependencies().contains(B));
		assertTrue(selector.getFactory().createClass("C").getInboundDependencies().contains(D));
		assertTrue(selector.getFactory().createClass("C").getOutboundDependencies().contains(A));
		assertTrue(selector.getFactory().createClass("C").getOutboundDependencies().contains(B));
		assertTrue(selector.getFactory().createClass("C").getOutboundDependencies().contains(D));
		assertTrue(selector.getFactory().createClass("D").getInboundDependencies().contains(A));
		assertTrue(selector.getFactory().createClass("D").getInboundDependencies().contains(B));
		assertTrue(selector.getFactory().createClass("D").getInboundDependencies().contains(C));
		assertTrue(selector.getFactory().createClass("D").getOutboundDependencies().contains(A));
		assertTrue(selector.getFactory().createClass("D").getOutboundDependencies().contains(B));
		assertTrue(selector.getFactory().createClass("D").getOutboundDependencies().contains(C));
	}

	public void testSinglePathThroughFullConnectivity() {
		selector.setSinglePath(true);

		A.accept(selector);
		B.accept(selector);
		C.accept(selector);
		D.accept(selector);

		assertEquals(4, selector.getFactory().getClasses().size());
		assertTrue(selector.getFactory().getClasses().values().contains(A));
		assertTrue(selector.getFactory().getClasses().values().contains(B));
		assertTrue(selector.getFactory().getClasses().values().contains(C));
		assertTrue(selector.getFactory().getClasses().values().contains(D));

		assertEquals(0, selector.getFactory().createClass("A").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("A").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("B").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("B").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("C").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("C").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("D").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("D").getOutboundDependencies().size());

		assertTrue(selector.getFactory().createClass("A").getInboundDependencies().isEmpty());
		assertTrue(selector.getFactory().createClass("A").getOutboundDependencies().contains(B));
		assertTrue(selector.getFactory().createClass("B").getInboundDependencies().contains(A));
		assertTrue(selector.getFactory().createClass("B").getOutboundDependencies().contains(C));
		assertTrue(selector.getFactory().createClass("C").getInboundDependencies().contains(B));
		assertTrue(selector.getFactory().createClass("C").getOutboundDependencies().contains(D));
		assertTrue(selector.getFactory().createClass("D").getInboundDependencies().contains(C));
		assertTrue(selector.getFactory().createClass("D").getOutboundDependencies().isEmpty());
	}
}
