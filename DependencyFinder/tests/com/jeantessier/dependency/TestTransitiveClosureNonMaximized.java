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

import org.apache.log4j.*;

public class TestTransitiveClosureNonMaximized extends TestCase {
	private RegularExpressionSelectionCriteria scopeCriteria;
	private RegularExpressionSelectionCriteria filterCriteria;
	private NodeFactory                        factory;

	private FeatureNode in2;
	private FeatureNode in1;
	private FeatureNode base;
	private FeatureNode out1;
	private FeatureNode out2;

	private TransitiveClosure          selector;
	
	protected void setUp() {
		scopeCriteria  = new RegularExpressionSelectionCriteria();
		filterCriteria = new RegularExpressionSelectionCriteria();
		factory        = new NodeFactory();

		in2  = factory.createFeature("in2.In2.In2()");
		in1  = factory.createFeature("in1.In1.In1()");
		base = factory.createFeature("base.Base.Base()");
		out1 = factory.createFeature("out1.Out1.Out1()");
		out2 = factory.createFeature("out2.Out2.Out2()");

		in2.addDependency(in1);
		in1.addDependency(base);
		base.addDependency(out1);
		out1.addDependency(out2);
		
		List scopeIncludes = new ArrayList(1);
		scopeIncludes.add("/^base/");
		List filderIncludes = new ArrayList(1);
		filderIncludes.add("//");
		
		scopeCriteria.setMatchingPackages(false);
		scopeCriteria.setMatchingClasses(false);
		scopeCriteria.setMatchingFeatures(false);
		scopeCriteria.setGlobalIncludes(scopeIncludes);
		filterCriteria.setMatchingPackages(false);
		filterCriteria.setMatchingClasses(false);
		filterCriteria.setMatchingFeatures(false);
		filterCriteria.setGlobalIncludes(filderIncludes);
		
		selector = new TransitiveClosure(new SortedTraversalStrategy(new SelectiveTraversalStrategy(scopeCriteria, filterCriteria)));
		selector.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
		selector.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
	}

	public void testFeatureToFeatureFromFeature() {
		scopeCriteria.setMatchingFeatures(true);
		filterCriteria.setMatchingFeatures(true);

		Logger.getLogger(getClass()).info("Start f2f test from feature ...");
		base.accept(selector);
		Logger.getLogger(getClass()).info("Stop f2f test from feature ...");

		assertEquals(5, selector.getFactory().getFeatures().size());
		assertTrue(selector.getFactory().getFeatures().values().contains(in2));
		assertTrue(selector.getFactory().getFeatures().values().contains(in1));
		assertTrue(selector.getFactory().getFeatures().values().contains(base));
		assertTrue(selector.getFactory().getFeatures().values().contains(out1));
		assertTrue(selector.getFactory().getFeatures().values().contains(out2));

		assertEquals(0, selector.getFactory().createFeature("in2.In2.In2()").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createFeature("in2.In2.In2()").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createFeature("in1.In1.In1()").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createFeature("in1.In1.In1()").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createFeature("base.Base.Base()").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createFeature("base.Base.Base()").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createFeature("out1.Out1.Out1()").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createFeature("out1.Out1.Out1()").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createFeature("out2.Out2.Out2()").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createFeature("out2.Out2.Out2()").getOutboundDependencies().size());

		assertEquals(0, selector.getFactory().createClass("in2.In2").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("in2.In2").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("in1.In1").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("in1.In1").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("base.Base").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("base.Base").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("out1.Out1").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("out1.Out1").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("out2.Out2").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("out2.Out2").getOutboundDependencies().size());

		assertEquals(0, selector.getFactory().createPackage("in2").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("in2").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("in1").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("in1").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("base").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("base").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out1").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out1").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out2").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out2").getOutboundDependencies().size());
	}

	public void testFeatureToFeatureFromPackages() {
		scopeCriteria.setMatchingFeatures(true);
		filterCriteria.setMatchingFeatures(true);

		Logger.getLogger(getClass()).info("Start f2f test from package list ...");
		selector.traverseNodes(factory.getPackages().values());
		Logger.getLogger(getClass()).info("Stop f2f test from package list ...");

		assertEquals(5, selector.getFactory().getFeatures().size());
		assertTrue(selector.getFactory().getFeatures().values().contains(in2));
		assertTrue(selector.getFactory().getFeatures().values().contains(in1));
		assertTrue(selector.getFactory().getFeatures().values().contains(base));
		assertTrue(selector.getFactory().getFeatures().values().contains(out1));
		assertTrue(selector.getFactory().getFeatures().values().contains(out2));

		assertEquals(0, selector.getFactory().createFeature("in2.In2.In2()").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createFeature("in2.In2.In2()").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createFeature("in1.In1.In1()").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createFeature("in1.In1.In1()").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createFeature("base.Base.Base()").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createFeature("base.Base.Base()").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createFeature("out1.Out1.Out1()").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createFeature("out1.Out1.Out1()").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createFeature("out2.Out2.Out2()").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createFeature("out2.Out2.Out2()").getOutboundDependencies().size());

		assertEquals(0, selector.getFactory().createClass("in2.In2").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("in2.In2").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("in1.In1").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("in1.In1").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("base.Base").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("base.Base").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("out1.Out1").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("out1.Out1").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("out2.Out2").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("out2.Out2").getOutboundDependencies().size());

		assertEquals(0, selector.getFactory().createPackage("in2").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("in2").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("in1").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("in1").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("base").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("base").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out1").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out1").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out2").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out2").getOutboundDependencies().size());
	}

	public void testClassToClassFromClass() {
		scopeCriteria.setMatchingClasses(true);
		filterCriteria.setMatchingClasses(true);

		Logger.getLogger(getClass()).info("Start c2c test from class ...");
		base.getClassNode().accept(selector);
		Logger.getLogger(getClass()).info("Stop c2c test from class ...");

		assertEquals(0, selector.getFactory().getFeatures().size());

		assertEquals(5, selector.getFactory().getClasses().size());
		assertTrue(selector.getFactory().getClasses().values().contains(in2.getClassNode()));
		assertTrue(selector.getFactory().getClasses().values().contains(in1.getClassNode()));
		assertTrue(selector.getFactory().getClasses().values().contains(base.getClassNode()));
		assertTrue(selector.getFactory().getClasses().values().contains(out1.getClassNode()));
		assertTrue(selector.getFactory().getClasses().values().contains(out2.getClassNode()));

		assertEquals(0, selector.getFactory().createClass("in2.In2").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("in2.In2").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("in1.In1").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("in1.In1").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("base.Base").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("base.Base").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("out1.Out1").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("out1.Out1").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("out2.Out2").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("out2.Out2").getOutboundDependencies().size());

		assertEquals(0, selector.getFactory().createPackage("in2").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("in2").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("in1").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("in1").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("base").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("base").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out1").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out1").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out2").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out2").getOutboundDependencies().size());
	}

	public void testClassToClassFromPackageList() {
		scopeCriteria.setMatchingClasses(true);
		filterCriteria.setMatchingClasses(true);

		Logger.getLogger(getClass()).info("Start c2c test from package list ...");
		selector.traverseNodes(factory.getPackages().values());
		Logger.getLogger(getClass()).info("Stop c2c test from package list ...");

		assertEquals(0, selector.getFactory().getFeatures().size());

		assertEquals(5, selector.getFactory().getClasses().size());
		assertTrue(selector.getFactory().getClasses().values().contains(in2.getClassNode()));
		assertTrue(selector.getFactory().getClasses().values().contains(in1.getClassNode()));
		assertTrue(selector.getFactory().getClasses().values().contains(base.getClassNode()));
		assertTrue(selector.getFactory().getClasses().values().contains(out1.getClassNode()));
		assertTrue(selector.getFactory().getClasses().values().contains(out2.getClassNode()));

		assertEquals(0, selector.getFactory().createClass("in2.In2").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("in2.In2").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("in1.In1").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("in1.In1").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("base.Base").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("base.Base").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("out1.Out1").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("out1.Out1").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createClass("out2.Out2").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createClass("out2.Out2").getOutboundDependencies().size());

		assertEquals(0, selector.getFactory().createPackage("in2").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("in2").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("in1").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("in1").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("base").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("base").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out1").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out1").getOutboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out2").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out2").getOutboundDependencies().size());
	}

	public void testPackageToPackageFromPackage() {
		scopeCriteria.setMatchingPackages(true);
		filterCriteria.setMatchingPackages(true);

		Logger.getLogger(getClass()).info("Start p2p test from package ...");
		base.getClassNode().getPackageNode().accept(selector);
		Logger.getLogger(getClass()).info("Stop p2p test from package ...");

		assertEquals(0, selector.getFactory().getFeatures().size());

		assertEquals(0, selector.getFactory().getClasses().size());

		assertEquals(5, selector.getFactory().getPackages().size());
		assertTrue(selector.getFactory().getPackages().values().contains(in2.getClassNode().getPackageNode()));
		assertTrue(selector.getFactory().getPackages().values().contains(in1.getClassNode().getPackageNode()));
		assertTrue(selector.getFactory().getPackages().values().contains(base.getClassNode().getPackageNode()));
		assertTrue(selector.getFactory().getPackages().values().contains(out1.getClassNode().getPackageNode()));
		assertTrue(selector.getFactory().getPackages().values().contains(out2.getClassNode().getPackageNode()));

		assertEquals(0, selector.getFactory().createPackage("in2").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createPackage("in2").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createPackage("in1").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createPackage("in1").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createPackage("base").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createPackage("base").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createPackage("out1").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createPackage("out1").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createPackage("out2").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out2").getOutboundDependencies().size());
	}

	public void testPackageToPackageFromPackageList() {
		scopeCriteria.setMatchingPackages(true);
		filterCriteria.setMatchingPackages(true);

		Logger.getLogger(getClass()).info("Start p2p test from package list ...");
		selector.traverseNodes(factory.getPackages().values());
		Logger.getLogger(getClass()).info("Stop p2p test from package list ...");

		assertEquals(0, selector.getFactory().getFeatures().size());

		assertEquals(0, selector.getFactory().getClasses().size());

		assertEquals(5, selector.getFactory().getPackages().size());
		assertTrue(selector.getFactory().getPackages().values().contains(in2.getClassNode().getPackageNode()));
		assertTrue(selector.getFactory().getPackages().values().contains(in1.getClassNode().getPackageNode()));
		assertTrue(selector.getFactory().getPackages().values().contains(base.getClassNode().getPackageNode()));
		assertTrue(selector.getFactory().getPackages().values().contains(out1.getClassNode().getPackageNode()));
		assertTrue(selector.getFactory().getPackages().values().contains(out2.getClassNode().getPackageNode()));

		assertEquals(0, selector.getFactory().createPackage("in2").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createPackage("in2").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createPackage("in1").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createPackage("in1").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createPackage("base").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createPackage("base").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createPackage("out1").getInboundDependencies().size());
		assertEquals(1, selector.getFactory().createPackage("out1").getOutboundDependencies().size());
		assertEquals(1, selector.getFactory().createPackage("out2").getInboundDependencies().size());
		assertEquals(0, selector.getFactory().createPackage("out2").getOutboundDependencies().size());
	}
}
