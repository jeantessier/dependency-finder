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

public class TestTransitiveClosureWithTestClass extends TestCase {
	private RegularExpressionSelectionCriteria scope_criteria;
	private RegularExpressionSelectionCriteria filter_criteria;
	private NodeFactory                        factory;
	
	private Node _package;
	private Node test_class;
	private Node test_main_method;
	private Node test_Test_method;
		
	private Node java_lang_package;
	private Node java_lang_Object_class;
	private Node java_lang_Object_Object_method;
	private Node java_lang_String_class;
		
	private Node java_util_package;
	private Node java_util_Collections_class;
	private Node java_util_Collections_singleton_method;

	private List scope_includes;
	
	private TransitiveClosure selector;

	protected void setUp() throws Exception {
		scope_criteria  = new RegularExpressionSelectionCriteria();
		filter_criteria = new RegularExpressionSelectionCriteria();
		factory         = new NodeFactory();

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
		
		test_class.addDependency(java_lang_Object_class);
		test_main_method.addDependency(java_lang_Object_class);
		test_main_method.addDependency(java_lang_Object_Object_method);
		test_main_method.addDependency(java_lang_String_class);
		test_main_method.addDependency(java_util_Collections_singleton_method);
		test_Test_method.addDependency(java_lang_Object_Object_method);

		scope_includes = new ArrayList(1);
		scope_includes.add("/test/");
		
		selector = new TransitiveClosure(new SelectiveTraversalStrategy(scope_criteria, filter_criteria));
	}

	public void testCompleteClosure() {
		scope_criteria.GlobalIncludes(scope_includes) ;
		
		selector.traverseNodes(factory.Packages().values());

		assertEquals("Different number of packages",
					 factory.Packages().size(),
					 selector.Factory().Packages().size());
		assertEquals("Different number of classes",
					 factory.Classes().size(),
					 selector.Factory().Classes().size());
		assertEquals("Different number of features",
					 factory.Features().size(),
					 selector.Factory().Features().size());

		Iterator i;

		i = selector.Factory().Packages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Packages().get(key), selector.Factory().Packages().get(key));
			assertTrue(factory.Packages().get(key) != selector.Factory().Packages().get(key));
			assertEquals("Package " + key + " has different inbound count",
						 ((Node) factory.Packages().get(key)).getInboundDependencies().size(),
						 ((Node) selector.Factory().Packages().get(key)).getInboundDependencies().size());
			assertEquals("Package " + key + " has different outbound count",
						 ((Node) factory.Packages().get(key)).getOutboundDependencies().size(),
						 ((Node) selector.Factory().Packages().get(key)).getOutboundDependencies().size());
		}
		
		i = selector.Factory().Classes().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Classes().get(key), selector.Factory().Classes().get(key));
			assertTrue(factory.Classes().get(key) != selector.Factory().Classes().get(key));
			assertEquals("Class " + key + " has different inbound count",
						 ((Node) factory.Classes().get(key)).getInboundDependencies().size(),
						 ((Node) selector.Factory().Classes().get(key)).getInboundDependencies().size());
			assertEquals("Class " + key + " has different outbound count",
						 ((Node) factory.Classes().get(key)).getOutboundDependencies().size(),
						 ((Node) selector.Factory().Classes().get(key)).getOutboundDependencies().size());
		}
		
		i = selector.Factory().Features().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Features().get(key), selector.Factory().Features().get(key));
			assertTrue(factory.Features().get(key) != selector.Factory().Features().get(key));
			assertEquals("Feature " + key + " has different inbound count",
						 ((Node) factory.Features().get(key)).getInboundDependencies().size(),
						 ((Node) selector.Factory().Features().get(key)).getInboundDependencies().size());
			assertEquals("Feature " + key + " has different outbound count",
						 ((Node) factory.Features().get(key)).getOutboundDependencies().size(),
						 ((Node) selector.Factory().Features().get(key)).getOutboundDependencies().size());
		}
	}

	public void testCopyAllNodesOnly() {
		scope_criteria.GlobalIncludes(scope_includes) ;
		filter_criteria.MatchPackage(false);
		filter_criteria.MatchClass(false);
		filter_criteria.MatchFeature(false);
		
		selector.traverseNodes(factory.Packages().values());

		assertEquals("Different number of packages",
					 1,
					 selector.Factory().Packages().size());
		assertEquals("Different number of classes",
					 1,
					 selector.Factory().Classes().size());
		assertEquals("Different number of features",
					 2,
					 selector.Factory().Features().size());

		Iterator i;

		i = selector.Factory().Packages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Packages().get(key), selector.Factory().Packages().get(key));
			assertTrue(factory.Packages().get(key) != selector.Factory().Packages().get(key));
			assertTrue(((Node) selector.Factory().Packages().get(key)).getInboundDependencies().isEmpty());
			assertTrue(((Node) selector.Factory().Packages().get(key)).getOutboundDependencies().isEmpty());
		}
		
		i = selector.Factory().Classes().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Classes().get(key), selector.Factory().Classes().get(key));
			assertTrue(factory.Classes().get(key) != selector.Factory().Classes().get(key));
			assertTrue(((Node) selector.Factory().Classes().get(key)).getInboundDependencies().isEmpty());
			assertTrue(((Node) selector.Factory().Classes().get(key)).getOutboundDependencies().isEmpty());
		}
		
		i = selector.Factory().Features().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Features().get(key), selector.Factory().Features().get(key));
			assertTrue(factory.Features().get(key) != selector.Factory().Features().get(key));
			assertTrue(((Node) selector.Factory().Features().get(key)).getInboundDependencies().isEmpty());
			assertTrue(((Node) selector.Factory().Features().get(key)).getOutboundDependencies().isEmpty());
		}
	}

	public void testCopyPackageNodesOnly() {
		scope_criteria.MatchClass(false);
		scope_criteria.MatchFeature(false);
		scope_criteria.GlobalIncludes(scope_includes) ;
		filter_criteria.MatchPackage(false);
		filter_criteria.MatchClass(false);
		filter_criteria.MatchFeature(false);
		
		selector.traverseNodes(factory.Packages().values());

		assertTrue(selector.Factory().Packages().isEmpty());
		assertTrue(selector.Factory().Classes().isEmpty());
		assertTrue(selector.Factory().Features().isEmpty());
	}

	public void testCopyClassNodesOnly() {
		scope_criteria.MatchPackage(false);
		scope_criteria.MatchFeature(false);
		scope_criteria.GlobalIncludes(scope_includes) ;
		filter_criteria.MatchPackage(false);
		filter_criteria.MatchClass(false);
		filter_criteria.MatchFeature(false);
		
		selector.traverseNodes(factory.Packages().values());

		assertEquals("Different number of packages",
					 1,
					 selector.Factory().Packages().size());
		assertEquals("Different number of classes",
					 1,
					 selector.Factory().Classes().size());
		assertTrue(selector.Factory().Features().isEmpty());

		Iterator i;

		i = selector.Factory().Packages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Packages().get(key), selector.Factory().Packages().get(key));
			assertTrue(factory.Packages().get(key) != selector.Factory().Packages().get(key));
			assertTrue(((Node) selector.Factory().Packages().get(key)).getInboundDependencies().isEmpty());
			assertTrue(((Node) selector.Factory().Packages().get(key)).getOutboundDependencies().isEmpty());
		}
		
		i = selector.Factory().Classes().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Classes().get(key), selector.Factory().Classes().get(key));
			assertTrue(factory.Classes().get(key) != selector.Factory().Classes().get(key));
			assertTrue(((Node) selector.Factory().Classes().get(key)).getInboundDependencies().isEmpty());
			assertTrue(((Node) selector.Factory().Classes().get(key)).getOutboundDependencies().isEmpty());
		}
	}

	public void testCopyFeatureNodesOnly() {
		scope_criteria.MatchPackage(false);
		scope_criteria.MatchClass(false);
		scope_criteria.GlobalIncludes(scope_includes) ;
		filter_criteria.MatchPackage(false);
		filter_criteria.MatchClass(false);
		filter_criteria.MatchFeature(false);
		
		selector.traverseNodes(factory.Packages().values());

		assertEquals("Different number of packages",
					 1,
					 selector.Factory().Packages().size());
		assertEquals("Different number of classes",
					 1,
					 selector.Factory().Classes().size());
		assertEquals("Different number of features",
					 2,
					 selector.Factory().Features().size());

		Iterator i;

		i = selector.Factory().Packages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Packages().get(key), selector.Factory().Packages().get(key));
			assertTrue(factory.Packages().get(key) != selector.Factory().Packages().get(key));
			assertTrue(((Node) selector.Factory().Packages().get(key)).getInboundDependencies().isEmpty());
			assertTrue(((Node) selector.Factory().Packages().get(key)).getOutboundDependencies().isEmpty());
		}
		
		i = selector.Factory().Classes().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Classes().get(key), selector.Factory().Classes().get(key));
			assertTrue(factory.Classes().get(key) != selector.Factory().Classes().get(key));
			assertTrue(((Node) selector.Factory().Classes().get(key)).getInboundDependencies().isEmpty());
			assertTrue(((Node) selector.Factory().Classes().get(key)).getOutboundDependencies().isEmpty());
		}
		
		i = selector.Factory().Features().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.Features().get(key), selector.Factory().Features().get(key));
			assertTrue(factory.Features().get(key) != selector.Factory().Features().get(key));
			assertTrue(((Node) selector.Factory().Features().get(key)).getInboundDependencies().isEmpty());
			assertTrue(((Node) selector.Factory().Features().get(key)).getOutboundDependencies().isEmpty());
		}
	}

	public void testCopyNothing() {
		scope_criteria.MatchPackage(false);
		scope_criteria.MatchClass(false);
		scope_criteria.MatchFeature(false);
		
		selector.traverseNodes(factory.Packages().values());

		assertTrue(selector.Factory().Packages().isEmpty());
		assertTrue(selector.Factory().Classes().isEmpty());
		assertTrue(selector.Factory().Features().isEmpty());
	}
}
