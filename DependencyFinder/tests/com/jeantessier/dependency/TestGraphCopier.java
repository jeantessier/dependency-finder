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

import java.io.*;
import java.util.*;

import junit.framework.*;

public class TestGraphCopier extends TestCase {
	SelectiveTraversalStrategy strategy;
	NodeFactory                factory;
	
	Node _package;
	Node test_class;
	Node test_main_method;
	Node test_Test_method;
		
	Node java_lang_package;
	Node java_lang_Object_class;
	Node java_lang_Object_Object_method;
	Node java_lang_String_class;
		
	Node java_util_package;
	Node java_util_Collections_class;
	Node java_util_Collections_singleton_method;
	
    GraphCopier copier;

	protected void setUp() throws Exception {
		strategy = new SelectiveTraversalStrategy();
		factory = new NodeFactory();

		_package = factory.CreatePackage("");
		test_class = factory.CreateClass("test");
		test_main_method = factory.CreateFeature("test.main(String[])");
		test_Test_method = factory.CreateFeature("test.Test()");
		
		java_lang_package = factory.CreatePackage("java.lang");
		java_lang_Object_class = factory.CreateClass("java.lang.Object");
		java_lang_Object_Object_method = factory.CreateFeature("java.lang.Object.Object()");
		java_lang_String_class = factory.CreateClass("java.lang.String");
		
		java_util_package = factory.CreatePackage("java.util");
		java_util_Collections_class = factory.CreateClass("java.util.Collections");
		java_util_Collections_singleton_method = factory.CreateFeature("java.util.Collections.singleton(java.lang.Object)");
		
		test_class.AddDependency(java_lang_Object_class);
		test_main_method.AddDependency(java_lang_Object_class);
		test_main_method.AddDependency(java_lang_Object_Object_method);
		test_main_method.AddDependency(java_lang_String_class);
		test_main_method.AddDependency(java_util_Collections_singleton_method);
		test_Test_method.AddDependency(java_lang_Object_Object_method);

		copier = new GraphCopier(strategy);
	}

	public void testCopyFullGraph() {
		copier.TraverseNodes(factory.Packages().values());

		assertEquals("Different number of packages",
					 factory.Packages().size(),
					 copier.ScopeFactory().Packages().size());
		assertEquals("Different number of classes",
					 factory.Classes().size(),
					 copier.ScopeFactory().Classes().size());
		assertEquals("Different number of features",
					 factory.Features().size(),
					 copier.ScopeFactory().Features().size());

		Iterator i;

		i = factory.Packages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Packages().get(key), copier.ScopeFactory().Packages().get(key));
			assertTrue(factory.Packages().get(key) != copier.ScopeFactory().Packages().get(key));
			assertEquals(((Node) factory.Packages().get(key)).Inbound().size(),
						 ((Node) copier.ScopeFactory().Packages().get(key)).Inbound().size());
			assertEquals(((Node) factory.Packages().get(key)).Outbound().size(),
						 ((Node) copier.ScopeFactory().Packages().get(key)).Outbound().size());
		}
		
		i = factory.Classes().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Classes().get(key), copier.ScopeFactory().Classes().get(key));
			assertTrue(factory.Classes().get(key) != copier.ScopeFactory().Classes().get(key));
			assertEquals(((Node) factory.Classes().get(key)).Inbound().size(),
						 ((Node) copier.ScopeFactory().Classes().get(key)).Inbound().size());
			assertEquals(((Node) factory.Classes().get(key)).Outbound().size(),
						 ((Node) copier.ScopeFactory().Classes().get(key)).Outbound().size());
		}
		
		i = factory.Features().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Features().get(key), copier.ScopeFactory().Features().get(key));
			assertTrue(factory.Features().get(key) != copier.ScopeFactory().Features().get(key));
			assertEquals(((Node) factory.Features().get(key)).Inbound().size(),
						 ((Node) copier.ScopeFactory().Features().get(key)).Inbound().size());
			assertEquals(((Node) factory.Features().get(key)).Outbound().size(),
						 ((Node) copier.ScopeFactory().Features().get(key)).Outbound().size());
		}
	}

	public void testCopyAllNodesOnly() {
		strategy.PackageFilter(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);
		
		copier.TraverseNodes(factory.Packages().values());

		assertEquals("Different number of packages",
					 factory.Packages().size(),
					 copier.ScopeFactory().Packages().size());
		assertEquals("Different number of classes",
					 factory.Classes().size(),
					 copier.ScopeFactory().Classes().size());
		assertEquals("Different number of features",
					 factory.Features().size(),
					 copier.ScopeFactory().Features().size());

		Iterator i;

		i = factory.Packages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Packages().get(key), copier.ScopeFactory().Packages().get(key));
			assertTrue(factory.Packages().get(key) != copier.ScopeFactory().Packages().get(key));
			assertTrue(((Node) copier.ScopeFactory().Packages().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.ScopeFactory().Packages().get(key)).Outbound().isEmpty());
		}
		
		i = factory.Classes().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Classes().get(key), copier.ScopeFactory().Classes().get(key));
			assertTrue(factory.Classes().get(key) != copier.ScopeFactory().Classes().get(key));
			assertTrue(((Node) copier.ScopeFactory().Classes().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.ScopeFactory().Classes().get(key)).Outbound().isEmpty());
		}
		
		i = factory.Features().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Features().get(key), copier.ScopeFactory().Features().get(key));
			assertTrue(factory.Features().get(key) != copier.ScopeFactory().Features().get(key));
			assertTrue(((Node) copier.ScopeFactory().Features().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.ScopeFactory().Features().get(key)).Outbound().isEmpty());
		}
	}

	public void testCopyPackageNodesOnly() {
		strategy.ClassScope(false);
		strategy.FeatureScope(false);
		strategy.PackageFilter(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);
		
		copier.TraverseNodes(factory.Packages().values());

		assertEquals("Different number of packages",
					 factory.Packages().size(),
					 copier.ScopeFactory().Packages().size());
		assertTrue(copier.ScopeFactory().Classes().isEmpty());
		assertTrue(copier.ScopeFactory().Features().isEmpty());

		Iterator i;

		i = factory.Packages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Packages().get(key), copier.ScopeFactory().Packages().get(key));
			assertTrue(factory.Packages().get(key) != copier.ScopeFactory().Packages().get(key));
			assertTrue(((Node) copier.ScopeFactory().Packages().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.ScopeFactory().Packages().get(key)).Outbound().isEmpty());
		}
	}

	public void testCopyClassNodesOnly() {
		strategy.PackageScope(false);
		strategy.FeatureScope(false);
		strategy.PackageFilter(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);
		
		copier.TraverseNodes(factory.Packages().values());

		assertEquals("Different number of packages",
					 factory.Packages().size(),
					 copier.ScopeFactory().Packages().size());
		assertEquals("Different number of classes",
					 factory.Classes().size(),
					 copier.ScopeFactory().Classes().size());
		assertTrue(copier.ScopeFactory().Features().isEmpty());

		Iterator i;

		i = factory.Packages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Packages().get(key), copier.ScopeFactory().Packages().get(key));
			assertTrue(factory.Packages().get(key) != copier.ScopeFactory().Packages().get(key));
			assertTrue(((Node) copier.ScopeFactory().Packages().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.ScopeFactory().Packages().get(key)).Outbound().isEmpty());
		}
		
		i = factory.Classes().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Classes().get(key), copier.ScopeFactory().Classes().get(key));
			assertTrue(factory.Classes().get(key) != copier.ScopeFactory().Classes().get(key));
			assertTrue(((Node) copier.ScopeFactory().Classes().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.ScopeFactory().Classes().get(key)).Outbound().isEmpty());
		}
	}

	public void testCopyFeatureNodesOnly() {
		strategy.PackageScope(false);
		strategy.ClassScope(false);
		strategy.PackageFilter(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);
		
		copier.TraverseNodes(factory.Packages().values());

		assertEquals("Different number of packages",
					 factory.Packages().size(),
					 copier.ScopeFactory().Packages().size());
		assertEquals("Different number of classes",
					 3,
					 copier.ScopeFactory().Classes().size());
		assertEquals("Different number of features",
					 factory.Features().size(),
					 copier.ScopeFactory().Features().size());

		Iterator i;

		i = copier.ScopeFactory().Packages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Packages().get(key), copier.ScopeFactory().Packages().get(key));
			assertTrue(factory.Packages().get(key) != copier.ScopeFactory().Packages().get(key));
			assertTrue(((Node) copier.ScopeFactory().Packages().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.ScopeFactory().Packages().get(key)).Outbound().isEmpty());
		}
		
		i = copier.ScopeFactory().Classes().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Classes().get(key), copier.ScopeFactory().Classes().get(key));
			assertTrue(factory.Classes().get(key) != copier.ScopeFactory().Classes().get(key));
			assertTrue(((Node) copier.ScopeFactory().Classes().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.ScopeFactory().Classes().get(key)).Outbound().isEmpty());
		}
		
		i = copier.ScopeFactory().Features().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Features().get(key), copier.ScopeFactory().Features().get(key));
			assertTrue(factory.Features().get(key) != copier.ScopeFactory().Features().get(key));
			assertTrue(((Node) copier.ScopeFactory().Features().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.ScopeFactory().Features().get(key)).Outbound().isEmpty());
		}
	}

	public void testCopyNothing() {
		strategy.PackageScope(false);
		strategy.ClassScope(false);
		strategy.FeatureScope(false);
		
		copier.TraverseNodes(factory.Packages().values());

		assertTrue(copier.ScopeFactory().Packages().isEmpty());
		assertTrue(copier.ScopeFactory().Classes().isEmpty());
		assertTrue(copier.ScopeFactory().Features().isEmpty());
	}

	public void testC2CasP2CSamePackage() {
		NodeFactory factory   = new NodeFactory();
		Node        a_package = factory.CreatePackage("a");
		Node        a_A_class = factory.CreateClass("a.A");
		Node        a_B_class = factory.CreateClass("a.B");
	
		a_A_class.AddDependency(a_B_class);

		SelectiveTraversalStrategy strategy = new SelectiveTraversalStrategy();
		strategy.ClassScope(false);
		strategy.FeatureScope(false);
		strategy.PackageFilter(false);
		strategy.FeatureFilter(false);
		
		GraphCopier copier = new GraphCopier(strategy);

		copier.TraverseNodes(factory.Packages().values());

		assertTrue(copier.ScopeFactory().Packages().keySet().toString(), copier.ScopeFactory().Packages().keySet().contains("a"));
		assertTrue(copier.ScopeFactory().Classes().isEmpty());
		assertTrue(copier.ScopeFactory().Features().isEmpty());

		assertEquals(0, copier.ScopeFactory().CreatePackage("a").Inbound().size());
		assertEquals(0, copier.ScopeFactory().CreatePackage("a").Outbound().size());
		assertEquals(0, copier.ScopeFactory().CreatePackage("b").Inbound().size());
		assertEquals(0, copier.ScopeFactory().CreatePackage("b").Outbound().size());
	}
}
