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

import org.apache.oro.text.perl.*;

public class TestTransitiveClosure extends TestCase {
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

	List scope_includes;
	
    TransitiveClosure copier;

	public TestTransitiveClosure(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		strategy = new SelectiveTraversalStrategy();
		factory = new NodeFactory();

		_package = factory.CreatePackage("");
		test_class = factory.CreateClass("test");
		test_main_method = factory.CreateFeature("test.main(String[])");
		test_Test_method = factory.CreateFeature("test.test()");
		
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

		scope_includes = new ArrayList(1);
		scope_includes.add("/test/");
		
		copier = new TransitiveClosure(strategy);
	}

	public void testCompleteClosure() {
		strategy.ScopeIncludes(scope_includes) ;
		
		copier.TraverseNodes(factory.Packages().values());

		assertEquals("Different number of packages",
					 factory.Packages().size(),
					 copier.Factory().Packages().size());
		assertEquals("Different number of classes",
					 factory.Classes().size(),
					 copier.Factory().Classes().size());
		assertEquals("Different number of features",
					 factory.Features().size(),
					 copier.Factory().Features().size());

		Iterator i;

		i = copier.Factory().Packages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Packages().get(key), copier.Factory().Packages().get(key));
			assertTrue(factory.Packages().get(key) != copier.Factory().Packages().get(key));
			assertEquals("Package " + key + " has different inbound count",
						 ((Node) factory.Packages().get(key)).Inbound().size(),
						 ((Node) copier.Factory().Packages().get(key)).Inbound().size());
			assertEquals("Package " + key + " has different outbound count",
						 ((Node) factory.Packages().get(key)).Outbound().size(),
						 ((Node) copier.Factory().Packages().get(key)).Outbound().size());
		}
		
		i = copier.Factory().Classes().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Classes().get(key), copier.Factory().Classes().get(key));
			assertTrue(factory.Classes().get(key) != copier.Factory().Classes().get(key));
			assertEquals("Class " + key + " has different inbound count",
						 ((Node) factory.Classes().get(key)).Inbound().size(),
						 ((Node) copier.Factory().Classes().get(key)).Inbound().size());
			assertEquals("Class " + key + " has different outbound count",
						 ((Node) factory.Classes().get(key)).Outbound().size(),
						 ((Node) copier.Factory().Classes().get(key)).Outbound().size());
		}
		
		i = copier.Factory().Features().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Features().get(key), copier.Factory().Features().get(key));
			assertTrue(factory.Features().get(key) != copier.Factory().Features().get(key));
			assertEquals("Feature " + key + " has different inbound count",
						 ((Node) factory.Features().get(key)).Inbound().size(),
						 ((Node) copier.Factory().Features().get(key)).Inbound().size());
			assertEquals("Feature " + key + " has different outbound count",
						 ((Node) factory.Features().get(key)).Outbound().size(),
						 ((Node) copier.Factory().Features().get(key)).Outbound().size());
		}
	}

	public void testCopyAllNodesOnly() {
		strategy.ScopeIncludes(scope_includes) ;
		strategy.PackageFilter(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);
		
		copier.TraverseNodes(factory.Packages().values());

		assertEquals("Different number of packages",
					 1,
					 copier.Factory().Packages().size());
		assertEquals("Different number of classes",
					 1,
					 copier.Factory().Classes().size());
		assertEquals("Different number of features",
					 2,
					 copier.Factory().Features().size());

		Iterator i;

		i = copier.Factory().Packages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Packages().get(key), copier.Factory().Packages().get(key));
			assertTrue(factory.Packages().get(key) != copier.Factory().Packages().get(key));
			assertTrue(((Node) copier.Factory().Packages().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.Factory().Packages().get(key)).Outbound().isEmpty());
		}
		
		i = copier.Factory().Classes().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Classes().get(key), copier.Factory().Classes().get(key));
			assertTrue(factory.Classes().get(key) != copier.Factory().Classes().get(key));
			assertTrue(((Node) copier.Factory().Classes().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.Factory().Classes().get(key)).Outbound().isEmpty());
		}
		
		i = copier.Factory().Features().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Features().get(key), copier.Factory().Features().get(key));
			assertTrue(factory.Features().get(key) != copier.Factory().Features().get(key));
			assertTrue(((Node) copier.Factory().Features().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.Factory().Features().get(key)).Outbound().isEmpty());
		}
	}

	public void testCopyPackageNodesOnly() {
		strategy.ScopeIncludes(scope_includes) ;
		strategy.ClassScope(false);
		strategy.FeatureScope(false);
		strategy.PackageFilter(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);
		
		copier.TraverseNodes(factory.Packages().values());

		assertTrue(copier.Factory().Packages().isEmpty());
		assertTrue(copier.Factory().Classes().isEmpty());
		assertTrue(copier.Factory().Features().isEmpty());
	}

	public void testCopyClassNodesOnly() {
		strategy.ScopeIncludes(scope_includes) ;
		strategy.PackageScope(false);
		strategy.FeatureScope(false);
		strategy.PackageFilter(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);
		
		copier.TraverseNodes(factory.Packages().values());

		assertEquals("Different number of packages",
					 1,
					 copier.Factory().Packages().size());
		assertEquals("Different number of classes",
					 1,
					 copier.Factory().Classes().size());
		assertTrue(copier.Factory().Features().isEmpty());

		Iterator i;

		i = copier.Factory().Packages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Packages().get(key), copier.Factory().Packages().get(key));
			assertTrue(factory.Packages().get(key) != copier.Factory().Packages().get(key));
			assertTrue(((Node) copier.Factory().Packages().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.Factory().Packages().get(key)).Outbound().isEmpty());
		}
		
		i = copier.Factory().Classes().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Classes().get(key), copier.Factory().Classes().get(key));
			assertTrue(factory.Classes().get(key) != copier.Factory().Classes().get(key));
			assertTrue(((Node) copier.Factory().Classes().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.Factory().Classes().get(key)).Outbound().isEmpty());
		}
	}

	public void testCopyFeatureNodesOnly() {
		strategy.ScopeIncludes(scope_includes) ;
		strategy.PackageScope(false);
		strategy.ClassScope(false);
		strategy.PackageFilter(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);
		
		copier.TraverseNodes(factory.Packages().values());

		assertEquals("Different number of packages",
					 1,
					 copier.Factory().Packages().size());
		assertEquals("Different number of classes",
					 1,
					 copier.Factory().Classes().size());
		assertEquals("Different number of features",
					 2,
					 copier.Factory().Features().size());

		Iterator i;

		i = copier.Factory().Packages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Packages().get(key), copier.Factory().Packages().get(key));
			assertTrue(factory.Packages().get(key) != copier.Factory().Packages().get(key));
			assertTrue(((Node) copier.Factory().Packages().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.Factory().Packages().get(key)).Outbound().isEmpty());
		}
		
		i = copier.Factory().Classes().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Classes().get(key), copier.Factory().Classes().get(key));
			assertTrue(factory.Classes().get(key) != copier.Factory().Classes().get(key));
			assertTrue(((Node) copier.Factory().Classes().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.Factory().Classes().get(key)).Outbound().isEmpty());
		}
		
		i = copier.Factory().Features().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Features().get(key), copier.Factory().Features().get(key));
			assertTrue(factory.Features().get(key) != copier.Factory().Features().get(key));
			assertTrue(((Node) copier.Factory().Features().get(key)).Inbound().isEmpty());
			assertTrue(((Node) copier.Factory().Features().get(key)).Outbound().isEmpty());
		}
	}

	public void testCopyNothing() {
		strategy.PackageScope(false);
		strategy.ClassScope(false);
		strategy.FeatureScope(false);
		
		copier.TraverseNodes(factory.Packages().values());

		assertTrue(copier.Factory().Packages().isEmpty());
		assertTrue(copier.Factory().Classes().isEmpty());
		assertTrue(copier.Factory().Features().isEmpty());
	}

//  	public void testSinglePath() {
//  		factory = new NodeFactory();
		
//  		ClassNode A = factory.CreateClass("A");
//  		ClassNode B = factory.CreateClass("B");
//  		ClassNode C = factory.CreateClass("C");
//  		ClassNode D = factory.CreateClass("D");

//  		A.AddDependency(B);
//  		A.AddDependency(C);
//  		A.AddDependency(D);

//  		B.AddDependency(A);
//  		B.AddDependency(C);
//  		B.AddDependency(D);

//  		C.AddDependency(A);
//  		C.AddDependency(B);
//  		C.AddDependency(D);

//  		D.AddDependency(A);
//  		D.AddDependency(B);
//  		D.AddDependency(C);

//  		strategy.ScopeIncludes(Collections.singletonList("//"));
//  		strategy.PackageScope(false);
//  		strategy.FeatureScope(false);
//  		strategy.FilterIncludes(Collections.singletonList("//"));
//  		strategy.PackageFilter(false);
//  		strategy.FeatureFilter(false);

//  		copier = new TransitiveClosure(new SortedTraversalStrategy(strategy));

//  		A.Accept(copier);

//  		assertEquals(4, copier.Factory().Classes().size());
//  		assertTrue(copier.Factory().Classes().values().contains(A));
//  		assertTrue(copier.Factory().Classes().values().contains(B));
//  		assertTrue(copier.Factory().Classes().values().contains(C));
//  		assertTrue(copier.Factory().Classes().values().contains(D));

//  		assertEquals(0, copier.Factory().CreateClass("A").Inbound().size());
//  		assertEquals(1, copier.Factory().CreateClass("A").Outbound().size());
//  		assertEquals(1, copier.Factory().CreateClass("B").Inbound().size());
//  		assertEquals(1, copier.Factory().CreateClass("B").Outbound().size());
//  		assertEquals(1, copier.Factory().CreateClass("C").Inbound().size());
//  		assertEquals(1, copier.Factory().CreateClass("C").Outbound().size());
//  		assertEquals(1, copier.Factory().CreateClass("D").Inbound().size());
//  		assertEquals(0, copier.Factory().CreateClass("D").Outbound().size());

//  		assertTrue(copier.Factory().CreateClass("A").Inbound().isEmpty());
//  		assertTrue(copier.Factory().CreateClass("A").Outbound().contains(B));
//  		assertTrue(copier.Factory().CreateClass("B").Inbound().contains(A));
//  		assertTrue(copier.Factory().CreateClass("B").Outbound().contains(C));
//  		assertTrue(copier.Factory().CreateClass("C").Inbound().contains(B));
//  		assertTrue(copier.Factory().CreateClass("C").Outbound().contains(D));
//  		assertTrue(copier.Factory().CreateClass("D").Inbound().contains(C));
//  		assertTrue(copier.Factory().CreateClass("D").Outbound().isEmpty());
//  	}
}
