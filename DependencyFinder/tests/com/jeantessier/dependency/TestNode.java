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

import junit.framework.*;

public class TestNode extends TestCase {
	Node a;
	Node a_A;
	Node a_A_a;
	Node a_A_b;
	Node a_B;
	Node a_B_a;
	Node a_B_b;

	Node b;
	Node b_B;
	Node b_B_b;

	protected void setUp() throws Exception {
		NodeFactory factory = new NodeFactory();

		a     = factory.CreatePackage("a");
		a_A   = factory.CreateClass("a.A");
		a_A_a = factory.CreateFeature("a.A.a");
		a_A_b = factory.CreateFeature("a.A.b");
		a_B   = factory.CreateClass("a.B");
		a_B_a = factory.CreateFeature("a.B.a");
		a_B_b = factory.CreateFeature("a.B.b");

		b     = factory.CreatePackage("b");
		b_B   = factory.CreateClass("b.B");
		b_B_b = factory.CreateFeature("b.B.b");
	}

	public void testPackageCanAddDependency() {
		assertFalse(a.CanAddDependency(a));
		assertTrue(a.CanAddDependency(a_A));
		assertTrue(a.CanAddDependency(a_A_a));
		assertTrue(a.CanAddDependency(a_A_b));
		assertTrue(a.CanAddDependency(a_B));
		assertTrue(a.CanAddDependency(a_B_a));
		assertTrue(a.CanAddDependency(a_B_b));

		assertTrue(a.CanAddDependency(b));
		assertTrue(a.CanAddDependency(b_B));
		assertTrue(a.CanAddDependency(b_B_b));
	}

	public void testClassCanAddDependency() {
		assertFalse(a_A.CanAddDependency(a));
		assertFalse(a_A.CanAddDependency(a_A));
		assertTrue(a_A.CanAddDependency(a_A_a));
		assertTrue(a_A.CanAddDependency(a_A_b));
		assertTrue(a_A.CanAddDependency(a_B));
		assertTrue(a_A.CanAddDependency(a_B_a));
		assertTrue(a_A.CanAddDependency(a_B_b));

		assertTrue(a_A.CanAddDependency(b));
		assertTrue(a_A.CanAddDependency(b_B));
		assertTrue(a_A.CanAddDependency(b_B_b));
	}

	public void testFeatureCanAddDependency() {
		assertFalse(a_A_a.CanAddDependency(a));
		assertFalse(a_A_a.CanAddDependency(a_A));
		assertFalse(a_A_a.CanAddDependency(a_A_a));
		assertTrue(a_A_a.CanAddDependency(a_A_b));
		assertTrue(a_A_a.CanAddDependency(a_B));
		assertTrue(a_A_a.CanAddDependency(a_B_a));
		assertTrue(a_A_a.CanAddDependency(a_B_b));

		assertTrue(a_A_a.CanAddDependency(b));
		assertTrue(a_A_a.CanAddDependency(b_B));
		assertTrue(a_A_a.CanAddDependency(b_B_b));
	}
}
