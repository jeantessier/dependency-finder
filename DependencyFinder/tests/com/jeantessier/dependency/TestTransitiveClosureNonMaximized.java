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
	private RegularExpressionSelectionCriteria scope_criteria;
	private RegularExpressionSelectionCriteria filter_criteria;
	private NodeFactory                        factory;

	private FeatureNode in2;
	private FeatureNode in1;
	private FeatureNode base;
	private FeatureNode out1;
	private FeatureNode out2;

	private TransitiveClosure          selector;
	
	protected void setUp() {
		scope_criteria  = new RegularExpressionSelectionCriteria();
		filter_criteria = new RegularExpressionSelectionCriteria();
		factory         = new NodeFactory();

		in2  = factory.CreateFeature("in2.In2.In2()");
		in1  = factory.CreateFeature("in1.In1.In1()");
		base = factory.CreateFeature("base.Base.Base()");
		out1 = factory.CreateFeature("out1.Out1.Out1()");
		out2 = factory.CreateFeature("out2.Out2.Out2()");

		in2.addDependency(in1);
		in1.addDependency(base);
		base.addDependency(out1);
		out1.addDependency(out2);
		
		List scope_includes = new ArrayList(1);
		scope_includes.add("/^base/");
		List filder_includes = new ArrayList(1);
		filder_includes.add("//");
		
		scope_criteria.MatchPackage(false);
		scope_criteria.MatchClass(false);
		scope_criteria.MatchFeature(false);
		scope_criteria.GlobalIncludes(scope_includes);
		filter_criteria.MatchPackage(false);
		filter_criteria.MatchClass(false);
		filter_criteria.MatchFeature(false);
		filter_criteria.GlobalIncludes(filder_includes);
		
		selector = new TransitiveClosure(new SortedTraversalStrategy(new SelectiveTraversalStrategy(scope_criteria, filter_criteria)));
		selector.MaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
		selector.MaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
	}

	public void testFeatureToFeatureFromFeature() {
		scope_criteria.MatchFeature(true);
		filter_criteria.MatchFeature(true);

		Logger.getLogger(getClass()).info("Start f2f test from feature ...");
		base.accept(selector);
		Logger.getLogger(getClass()).info("Stop f2f test from feature ...");

		assertEquals(5, selector.Factory().Features().size());
		assertTrue(selector.Factory().Features().values().contains(in2));
		assertTrue(selector.Factory().Features().values().contains(in1));
		assertTrue(selector.Factory().Features().values().contains(base));
		assertTrue(selector.Factory().Features().values().contains(out1));
		assertTrue(selector.Factory().Features().values().contains(out2));

		assertEquals(0, selector.Factory().CreateFeature("in2.In2.In2()").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateFeature("in2.In2.In2()").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateFeature("in1.In1.In1()").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateFeature("in1.In1.In1()").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateFeature("base.Base.Base()").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateFeature("base.Base.Base()").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateFeature("out1.Out1.Out1()").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateFeature("out1.Out1.Out1()").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateFeature("out2.Out2.Out2()").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreateFeature("out2.Out2.Out2()").getOutboundDependencies().size());

		assertEquals(0, selector.Factory().CreateClass("in2.In2").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("in2.In2").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("in1.In1").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("in1.In1").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("base.Base").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("base.Base").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("out1.Out1").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("out1.Out1").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("out2.Out2").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("out2.Out2").getOutboundDependencies().size());

		assertEquals(0, selector.Factory().CreatePackage("in2").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("in2").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("in1").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("in1").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("base").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("base").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out1").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out1").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out2").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out2").getOutboundDependencies().size());
	}

	public void testFeatureToFeatureFromPackages() {
		scope_criteria.MatchFeature(true);
		filter_criteria.MatchFeature(true);

		Logger.getLogger(getClass()).info("Start f2f test from package list ...");
		selector.traverseNodes(factory.Packages().values());
		Logger.getLogger(getClass()).info("Stop f2f test from package list ...");

		assertEquals(5, selector.Factory().Features().size());
		assertTrue(selector.Factory().Features().values().contains(in2));
		assertTrue(selector.Factory().Features().values().contains(in1));
		assertTrue(selector.Factory().Features().values().contains(base));
		assertTrue(selector.Factory().Features().values().contains(out1));
		assertTrue(selector.Factory().Features().values().contains(out2));

		assertEquals(0, selector.Factory().CreateFeature("in2.In2.In2()").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateFeature("in2.In2.In2()").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateFeature("in1.In1.In1()").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateFeature("in1.In1.In1()").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateFeature("base.Base.Base()").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateFeature("base.Base.Base()").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateFeature("out1.Out1.Out1()").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateFeature("out1.Out1.Out1()").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateFeature("out2.Out2.Out2()").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreateFeature("out2.Out2.Out2()").getOutboundDependencies().size());

		assertEquals(0, selector.Factory().CreateClass("in2.In2").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("in2.In2").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("in1.In1").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("in1.In1").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("base.Base").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("base.Base").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("out1.Out1").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("out1.Out1").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("out2.Out2").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("out2.Out2").getOutboundDependencies().size());

		assertEquals(0, selector.Factory().CreatePackage("in2").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("in2").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("in1").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("in1").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("base").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("base").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out1").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out1").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out2").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out2").getOutboundDependencies().size());
	}

	public void testClassToClassFromClass() {
		scope_criteria.MatchClass(true);
		filter_criteria.MatchClass(true);

		Logger.getLogger(getClass()).info("Start c2c test from class ...");
		base.getClassNode().accept(selector);
		Logger.getLogger(getClass()).info("Stop c2c test from class ...");

		assertEquals(0, selector.Factory().Features().size());

		assertEquals(5, selector.Factory().Classes().size());
		assertTrue(selector.Factory().Classes().values().contains(in2.getClassNode()));
		assertTrue(selector.Factory().Classes().values().contains(in1.getClassNode()));
		assertTrue(selector.Factory().Classes().values().contains(base.getClassNode()));
		assertTrue(selector.Factory().Classes().values().contains(out1.getClassNode()));
		assertTrue(selector.Factory().Classes().values().contains(out2.getClassNode()));

		assertEquals(0, selector.Factory().CreateClass("in2.In2").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("in2.In2").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("in1.In1").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("in1.In1").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("base.Base").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("base.Base").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("out1.Out1").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("out1.Out1").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("out2.Out2").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("out2.Out2").getOutboundDependencies().size());

		assertEquals(0, selector.Factory().CreatePackage("in2").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("in2").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("in1").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("in1").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("base").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("base").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out1").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out1").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out2").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out2").getOutboundDependencies().size());
	}

	public void testClassToClassFromPackageList() {
		scope_criteria.MatchClass(true);
		filter_criteria.MatchClass(true);

		Logger.getLogger(getClass()).info("Start c2c test from package list ...");
		selector.traverseNodes(factory.Packages().values());
		Logger.getLogger(getClass()).info("Stop c2c test from package list ...");

		assertEquals(0, selector.Factory().Features().size());

		assertEquals(5, selector.Factory().Classes().size());
		assertTrue(selector.Factory().Classes().values().contains(in2.getClassNode()));
		assertTrue(selector.Factory().Classes().values().contains(in1.getClassNode()));
		assertTrue(selector.Factory().Classes().values().contains(base.getClassNode()));
		assertTrue(selector.Factory().Classes().values().contains(out1.getClassNode()));
		assertTrue(selector.Factory().Classes().values().contains(out2.getClassNode()));

		assertEquals(0, selector.Factory().CreateClass("in2.In2").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("in2.In2").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("in1.In1").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("in1.In1").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("base.Base").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("base.Base").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("out1.Out1").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("out1.Out1").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreateClass("out2.Out2").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreateClass("out2.Out2").getOutboundDependencies().size());

		assertEquals(0, selector.Factory().CreatePackage("in2").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("in2").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("in1").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("in1").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("base").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("base").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out1").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out1").getOutboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out2").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out2").getOutboundDependencies().size());
	}

	public void testPackageToPackageFromPackage() {
		scope_criteria.MatchPackage(true);
		filter_criteria.MatchPackage(true);

		Logger.getLogger(getClass()).info("Start p2p test from package ...");
		base.getClassNode().getPackageNode().accept(selector);
		Logger.getLogger(getClass()).info("Stop p2p test from package ...");

		assertEquals(0, selector.Factory().Features().size());

		assertEquals(0, selector.Factory().Classes().size());

		assertEquals(5, selector.Factory().Packages().size());
		assertTrue(selector.Factory().Packages().values().contains(in2.getClassNode().getPackageNode()));
		assertTrue(selector.Factory().Packages().values().contains(in1.getClassNode().getPackageNode()));
		assertTrue(selector.Factory().Packages().values().contains(base.getClassNode().getPackageNode()));
		assertTrue(selector.Factory().Packages().values().contains(out1.getClassNode().getPackageNode()));
		assertTrue(selector.Factory().Packages().values().contains(out2.getClassNode().getPackageNode()));

		assertEquals(0, selector.Factory().CreatePackage("in2").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreatePackage("in2").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreatePackage("in1").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreatePackage("in1").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreatePackage("base").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreatePackage("base").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreatePackage("out1").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreatePackage("out1").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreatePackage("out2").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out2").getOutboundDependencies().size());
	}

	public void testPackageToPackageFromPackageList() {
		scope_criteria.MatchPackage(true);
		filter_criteria.MatchPackage(true);

		Logger.getLogger(getClass()).info("Start p2p test from package list ...");
		selector.traverseNodes(factory.Packages().values());
		Logger.getLogger(getClass()).info("Stop p2p test from package list ...");

		assertEquals(0, selector.Factory().Features().size());

		assertEquals(0, selector.Factory().Classes().size());

		assertEquals(5, selector.Factory().Packages().size());
		assertTrue(selector.Factory().Packages().values().contains(in2.getClassNode().getPackageNode()));
		assertTrue(selector.Factory().Packages().values().contains(in1.getClassNode().getPackageNode()));
		assertTrue(selector.Factory().Packages().values().contains(base.getClassNode().getPackageNode()));
		assertTrue(selector.Factory().Packages().values().contains(out1.getClassNode().getPackageNode()));
		assertTrue(selector.Factory().Packages().values().contains(out2.getClassNode().getPackageNode()));

		assertEquals(0, selector.Factory().CreatePackage("in2").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreatePackage("in2").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreatePackage("in1").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreatePackage("in1").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreatePackage("base").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreatePackage("base").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreatePackage("out1").getInboundDependencies().size());
		assertEquals(1, selector.Factory().CreatePackage("out1").getOutboundDependencies().size());
		assertEquals(1, selector.Factory().CreatePackage("out2").getInboundDependencies().size());
		assertEquals(0, selector.Factory().CreatePackage("out2").getOutboundDependencies().size());
	}
}
