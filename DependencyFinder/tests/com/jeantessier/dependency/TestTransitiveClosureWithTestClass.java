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
	private RegularExpressionSelectionCriteria scopeCriteria;
	private RegularExpressionSelectionCriteria filterCriteria;
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

	private List scopeIncludes;
	
	private TransitiveClosure selector;

	protected void setUp() throws Exception {
		scopeCriteria  = new RegularExpressionSelectionCriteria();
		filterCriteria = new RegularExpressionSelectionCriteria();
		factory        = new NodeFactory();

		_package = factory.createPackage("");
		test_class = factory.createClass("test");
		test_main_method = factory.createFeature("test.main(String[])");
		test_Test_method = factory.createFeature("test.test()");
		
		java_lang_package = factory.createPackage("java.lang");
		java_lang_Object_class = factory.createClass("java.lang.Object");
		java_lang_Object_Object_method = factory.createFeature("java.lang.Object.Object()");
		java_lang_String_class = factory.createClass("java.lang.String");
		
		java_util_package = factory.createPackage("java.util");
		java_util_Collections_class = factory.createClass("java.util.Collections");
		java_util_Collections_singleton_method = factory.createFeature("java.util.Collections.singleton(java.lang.Object)");
		
		test_class.addDependency(java_lang_Object_class);
		test_main_method.addDependency(java_lang_Object_class);
		test_main_method.addDependency(java_lang_Object_Object_method);
		test_main_method.addDependency(java_lang_String_class);
		test_main_method.addDependency(java_util_Collections_singleton_method);
		test_Test_method.addDependency(java_lang_Object_Object_method);

		scopeIncludes = new ArrayList(1);
		scopeIncludes.add("/test/");
		
		selector = new TransitiveClosure(new SelectiveTraversalStrategy(scopeCriteria, filterCriteria));
	}

	public void testCompleteClosure() {
		scopeCriteria.setGlobalIncludes(scopeIncludes) ;
		
		selector.traverseNodes(factory.getPackages().values());

		assertEquals("Different number of packages",
					 factory.getPackages().size(),
					 selector.getFactory().getPackages().size());
		assertEquals("Different number of classes",
					 factory.getClasses().size(),
					 selector.getFactory().getClasses().size());
		assertEquals("Different number of features",
					 factory.getFeatures().size(),
					 selector.getFactory().getFeatures().size());

		Iterator i;

		i = selector.getFactory().getPackages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.getPackages().get(key), selector.getFactory().getPackages().get(key));
			assertTrue(factory.getPackages().get(key) != selector.getFactory().getPackages().get(key));
			assertEquals("Package " + key + " has different inbound count",
						 ((Node) factory.getPackages().get(key)).getInboundDependencies().size(),
						 ((Node) selector.getFactory().getPackages().get(key)).getInboundDependencies().size());
			assertEquals("Package " + key + " has different outbound count",
						 ((Node) factory.getPackages().get(key)).getOutboundDependencies().size(),
						 ((Node) selector.getFactory().getPackages().get(key)).getOutboundDependencies().size());
		}
		
		i = selector.getFactory().getClasses().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.getClasses().get(key), selector.getFactory().getClasses().get(key));
			assertTrue(factory.getClasses().get(key) != selector.getFactory().getClasses().get(key));
			assertEquals("Class " + key + " has different inbound count",
						 ((Node) factory.getClasses().get(key)).getInboundDependencies().size(),
						 ((Node) selector.getFactory().getClasses().get(key)).getInboundDependencies().size());
			assertEquals("Class " + key + " has different outbound count",
						 ((Node) factory.getClasses().get(key)).getOutboundDependencies().size(),
						 ((Node) selector.getFactory().getClasses().get(key)).getOutboundDependencies().size());
		}
		
		i = selector.getFactory().getFeatures().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.getFeatures().get(key), selector.getFactory().getFeatures().get(key));
			assertTrue(factory.getFeatures().get(key) != selector.getFactory().getFeatures().get(key));
			assertEquals("Feature " + key + " has different inbound count",
						 ((Node) factory.getFeatures().get(key)).getInboundDependencies().size(),
						 ((Node) selector.getFactory().getFeatures().get(key)).getInboundDependencies().size());
			assertEquals("Feature " + key + " has different outbound count",
						 ((Node) factory.getFeatures().get(key)).getOutboundDependencies().size(),
						 ((Node) selector.getFactory().getFeatures().get(key)).getOutboundDependencies().size());
		}
	}

	public void testCopyAllNodesOnly() {
		scopeCriteria.setGlobalIncludes(scopeIncludes) ;
		filterCriteria.setMatchingPackages(false);
		filterCriteria.setMatchingClasses(false);
		filterCriteria.setMatchingFeatures(false);
		
		selector.traverseNodes(factory.getPackages().values());

		assertEquals("Different number of packages",
					 1,
					 selector.getFactory().getPackages().size());
		assertEquals("Different number of classes",
					 1,
					 selector.getFactory().getClasses().size());
		assertEquals("Different number of features",
					 2,
					 selector.getFactory().getFeatures().size());

		Iterator i;

		i = selector.getFactory().getPackages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.getPackages().get(key), selector.getFactory().getPackages().get(key));
			assertTrue(factory.getPackages().get(key) != selector.getFactory().getPackages().get(key));
			assertTrue(((Node) selector.getFactory().getPackages().get(key)).getInboundDependencies().isEmpty());
			assertTrue(((Node) selector.getFactory().getPackages().get(key)).getOutboundDependencies().isEmpty());
		}
		
		i = selector.getFactory().getClasses().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.getClasses().get(key), selector.getFactory().getClasses().get(key));
			assertTrue(factory.getClasses().get(key) != selector.getFactory().getClasses().get(key));
			assertTrue(((Node) selector.getFactory().getClasses().get(key)).getInboundDependencies().isEmpty());
			assertTrue(((Node) selector.getFactory().getClasses().get(key)).getOutboundDependencies().isEmpty());
		}
		
		i = selector.getFactory().getFeatures().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.getFeatures().get(key), selector.getFactory().getFeatures().get(key));
			assertTrue(factory.getFeatures().get(key) != selector.getFactory().getFeatures().get(key));
			assertTrue(((Node) selector.getFactory().getFeatures().get(key)).getInboundDependencies().isEmpty());
			assertTrue(((Node) selector.getFactory().getFeatures().get(key)).getOutboundDependencies().isEmpty());
		}
	}

	public void testCopyPackageNodesOnly() {
		scopeCriteria.setMatchingClasses(false);
		scopeCriteria.setMatchingFeatures(false);
		scopeCriteria.setGlobalIncludes(scopeIncludes) ;
		filterCriteria.setMatchingPackages(false);
		filterCriteria.setMatchingClasses(false);
		filterCriteria.setMatchingFeatures(false);
		
		selector.traverseNodes(factory.getPackages().values());

		assertTrue(selector.getFactory().getPackages().isEmpty());
		assertTrue(selector.getFactory().getClasses().isEmpty());
		assertTrue(selector.getFactory().getFeatures().isEmpty());
	}

	public void testCopyClassNodesOnly() {
		scopeCriteria.setMatchingPackages(false);
		scopeCriteria.setMatchingFeatures(false);
		scopeCriteria.setGlobalIncludes(scopeIncludes) ;
		filterCriteria.setMatchingPackages(false);
		filterCriteria.setMatchingClasses(false);
		filterCriteria.setMatchingFeatures(false);
		
		selector.traverseNodes(factory.getPackages().values());

		assertEquals("Different number of packages",
					 1,
					 selector.getFactory().getPackages().size());
		assertEquals("Different number of classes",
					 1,
					 selector.getFactory().getClasses().size());
		assertTrue(selector.getFactory().getFeatures().isEmpty());

		Iterator i;

		i = selector.getFactory().getPackages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.getPackages().get(key), selector.getFactory().getPackages().get(key));
			assertTrue(factory.getPackages().get(key) != selector.getFactory().getPackages().get(key));
			assertTrue(((Node) selector.getFactory().getPackages().get(key)).getInboundDependencies().isEmpty());
			assertTrue(((Node) selector.getFactory().getPackages().get(key)).getOutboundDependencies().isEmpty());
		}
		
		i = selector.getFactory().getClasses().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.getClasses().get(key), selector.getFactory().getClasses().get(key));
			assertTrue(factory.getClasses().get(key) != selector.getFactory().getClasses().get(key));
			assertTrue(((Node) selector.getFactory().getClasses().get(key)).getInboundDependencies().isEmpty());
			assertTrue(((Node) selector.getFactory().getClasses().get(key)).getOutboundDependencies().isEmpty());
		}
	}

	public void testCopyFeatureNodesOnly() {
		scopeCriteria.setMatchingPackages(false);
		scopeCriteria.setMatchingClasses(false);
		scopeCriteria.setGlobalIncludes(scopeIncludes) ;
		filterCriteria.setMatchingPackages(false);
		filterCriteria.setMatchingClasses(false);
		filterCriteria.setMatchingFeatures(false);
		
		selector.traverseNodes(factory.getPackages().values());

		assertEquals("Different number of packages",
					 1,
					 selector.getFactory().getPackages().size());
		assertEquals("Different number of classes",
					 1,
					 selector.getFactory().getClasses().size());
		assertEquals("Different number of features",
					 2,
					 selector.getFactory().getFeatures().size());

		Iterator i;

		i = selector.getFactory().getPackages().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.getPackages().get(key), selector.getFactory().getPackages().get(key));
			assertTrue(factory.getPackages().get(key) != selector.getFactory().getPackages().get(key));
			assertTrue(((Node) selector.getFactory().getPackages().get(key)).getInboundDependencies().isEmpty());
			assertTrue(((Node) selector.getFactory().getPackages().get(key)).getOutboundDependencies().isEmpty());
		}
		
		i = selector.getFactory().getClasses().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.getClasses().get(key), selector.getFactory().getClasses().get(key));
			assertTrue(factory.getClasses().get(key) != selector.getFactory().getClasses().get(key));
			assertTrue(((Node) selector.getFactory().getClasses().get(key)).getInboundDependencies().isEmpty());
			assertTrue(((Node) selector.getFactory().getClasses().get(key)).getOutboundDependencies().isEmpty());
		}
		
		i = selector.getFactory().getFeatures().keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			assertEquals(factory.getFeatures().get(key), selector.getFactory().getFeatures().get(key));
			assertTrue(factory.getFeatures().get(key) != selector.getFactory().getFeatures().get(key));
			assertTrue(((Node) selector.getFactory().getFeatures().get(key)).getInboundDependencies().isEmpty());
			assertTrue(((Node) selector.getFactory().getFeatures().get(key)).getOutboundDependencies().isEmpty());
		}
	}

	public void testCopyNothing() {
		scopeCriteria.setMatchingPackages(false);
		scopeCriteria.setMatchingClasses(false);
		scopeCriteria.setMatchingFeatures(false);
		
		selector.traverseNodes(factory.getPackages().values());

		assertTrue(selector.getFactory().getPackages().isEmpty());
		assertTrue(selector.getFactory().getClasses().isEmpty());
		assertTrue(selector.getFactory().getFeatures().isEmpty());
	}
}
