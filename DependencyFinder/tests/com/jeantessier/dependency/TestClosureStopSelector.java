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

public class TestClosureStopSelector extends TestCase {
	private NodeFactory factory;

	private PackageNode a;
	private ClassNode   a_A;
	private FeatureNode a_A_a;
	
	private PackageNode b;
	private ClassNode   b_B;
	private FeatureNode b_B_b;
	
	private PackageNode c;
	private ClassNode   c_C;
	private FeatureNode c_C_c;

	protected void setUp() throws Exception {
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

		a_A_a.addDependency(b_B_b);
		b_B_b.addDependency(c_C_c);
	}

	public void testEmpty() {
		RegularExpressionSelectionCriteria local_criteria = new RegularExpressionSelectionCriteria();
		local_criteria.GlobalIncludes("/b.B.b/");

		ClosureStopSelector selector = new ClosureStopSelector(local_criteria);
		selector.traverseNodes(Collections.EMPTY_SET);

		assertTrue("Failed to recognize empty collection", selector.Done());
	}

	public void testPositive() {
		RegularExpressionSelectionCriteria local_criteria = new RegularExpressionSelectionCriteria();
		local_criteria.GlobalIncludes("/b.B.b/");

		ClosureStopSelector selector = new ClosureStopSelector(local_criteria);
		selector.traverseNodes(Collections.singleton(b_B_b));

		assertTrue("Failed to recognize target", selector.Done());
	}

	public void testNegative() {
		RegularExpressionSelectionCriteria local_criteria = new RegularExpressionSelectionCriteria();
		local_criteria.GlobalIncludes("/b.B.b/");

		ClosureStopSelector selector = new ClosureStopSelector(local_criteria);
		selector.traverseNodes(Collections.singleton(a_A_a));

		assertFalse("Failed to ignore non-target", selector.Done());
	}

	public void testMultiple() {
		RegularExpressionSelectionCriteria local_criteria = new RegularExpressionSelectionCriteria();
		local_criteria.GlobalIncludes("/b.B.b/");

		Collection targets = new ArrayList();
		targets.add(a_A_a);
		targets.add(b_B_b);
		
		ClosureStopSelector selector = new ClosureStopSelector(local_criteria);
		selector.traverseNodes(targets);

		assertTrue("Failed to recognize target", selector.Done());
	}
}
