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

public class TestCollectionSelectionCriteria extends TestCase {
	private Collection                  include;
	private Collection                  exclude;
	private CollectionSelectionCriteria criteria;

	private PackageNode a;
	private ClassNode   a_A;
	private FeatureNode a_A_a;
	
	private PackageNode b;
	private ClassNode   b_B;
	private FeatureNode b_B_b;

	protected void setUp() throws Exception {
		include  = new HashSet();
		exclude  = new HashSet();
		criteria = new CollectionSelectionCriteria(include, exclude);

		NodeFactory factory = new NodeFactory();

		a     = factory.CreatePackage("a");
		a_A   = factory.CreateClass("a.A");
		a_A_a = factory.CreateFeature("a.A.a");
		
		b     = factory.CreatePackage("b");
		b_B   = factory.CreateClass("b.B");
		b_B_b = factory.CreateFeature("b.B.b");
	}

	public void testEmptyInclude() {
		assertFalse("a",     criteria.Match(a));
		assertFalse("a.A",   criteria.Match(a_A));
		assertFalse("a.A.a", criteria.Match(a_A_a));

		assertFalse("b",     criteria.Match(b));
		assertFalse("b.B",   criteria.Match(b_B));
		assertFalse("b.B.b", criteria.Match(b_B_b));
	}

	public void testNullInclude() {
		criteria = new CollectionSelectionCriteria(null, exclude);

		assertTrue("a",     criteria.Match(a));
		assertTrue("a.A",   criteria.Match(a_A));
		assertTrue("a.A.a", criteria.Match(a_A_a));

		assertTrue("b",     criteria.Match(b));
		assertTrue("b.B",   criteria.Match(b_B));
		assertTrue("b.B.b", criteria.Match(b_B_b));
	}

	public void testMatchPackageNode() {
		include.add("a");

		assertTrue("a",      criteria.Match(a));
		assertFalse("a.A",   criteria.Match(a_A));
		assertFalse("a.A.a", criteria.Match(a_A_a));

		assertFalse("b",     criteria.Match(b));
		assertFalse("b.B",   criteria.Match(b_B));
		assertFalse("b.B.b", criteria.Match(b_B_b));
	}

	public void testMatchClassNode() {
		include.add("a.A");

		assertFalse("a",     criteria.Match(a));
		assertTrue("a.A",    criteria.Match(a_A));
		assertFalse("a.A.a", criteria.Match(a_A_a));

		assertFalse("b",     criteria.Match(b));
		assertFalse("b.B",   criteria.Match(b_B));
		assertFalse("b.B.b", criteria.Match(b_B_b));
	}

	public void testMatchFeatureNode() {
		include.add("a.A.a");

		assertFalse("a",     criteria.Match(a));
		assertFalse("a.A",   criteria.Match(a_A));
		assertTrue("a.A.a",  criteria.Match(a_A_a));

		assertFalse("b",     criteria.Match(b));
		assertFalse("b.B",   criteria.Match(b_B));
		assertFalse("b.B.b", criteria.Match(b_B_b));
	}

	public void testMatchPackageName() {
		include.add("a");

		assertTrue("a",      criteria.PackageMatch("a"));
		assertFalse("a.A",   criteria.ClassMatch("a.A"));
		assertFalse("a.A.a", criteria.FeatureMatch("a.A.a"));

		assertFalse("b",     criteria.PackageMatch("b"));
		assertFalse("b.B",   criteria.ClassMatch("b.B"));
		assertFalse("b.B.b", criteria.FeatureMatch("b.B.b"));
	}

	public void testMatchClassName() {
		include.add("a.A");

		assertFalse("a",     criteria.PackageMatch("a"));
		assertTrue("a.A",    criteria.ClassMatch("a.A"));
		assertFalse("a.A.a", criteria.FeatureMatch("a.A.a"));

		assertFalse("b",     criteria.PackageMatch("b"));
		assertFalse("b.B",   criteria.ClassMatch("b.B"));
		assertFalse("b.B.b", criteria.FeatureMatch("b.B.b"));
	}

	public void testMatchFeatureName() {
		include.add("a.A.a");

		assertFalse("a",     criteria.PackageMatch("a"));
		assertFalse("a.A",   criteria.ClassMatch("a.A"));
		assertTrue("a.A.a",  criteria.FeatureMatch("a.A.a"));

		assertFalse("b",     criteria.PackageMatch("b"));
		assertFalse("b.B",   criteria.ClassMatch("b.B"));
		assertFalse("b.B.b", criteria.FeatureMatch("b.B.b"));
	}

	public void testExclude() {
		include.add("a");
		include.add("a.A");
		include.add("a.A.a");
		exclude.add("a.A.a");

		assertTrue("a",      criteria.Match(a));
		assertTrue("a.A",    criteria.Match(a_A));
		assertFalse("a.A.a", criteria.Match(a_A_a));
	}

	public void testExcludeOnly() {
		exclude.add("a.A.a");

		criteria = new CollectionSelectionCriteria(null, exclude);

		assertTrue("a",      criteria.Match(a));
		assertTrue("a.A",    criteria.Match(a_A));
		assertFalse("a.A.a", criteria.Match(a_A_a));
	}
}
