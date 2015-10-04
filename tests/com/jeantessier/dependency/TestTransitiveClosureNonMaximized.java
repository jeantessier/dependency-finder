/*
 *  Copyright (c) 2001-2009, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
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
    private NodeFactory factory;

    private FeatureNode in2;
    private FeatureNode in1;
    private FeatureNode base;
    private FeatureNode out1;
    private FeatureNode out2;
    
    private RegularExpressionSelectionCriteria startCriteria;
    private RegularExpressionSelectionCriteria stopCriteria;

    private NodeFactory resultFactory;

    protected void setUp() throws Exception {
        super.setUp();

        factory = new NodeFactory();

        in2  = factory.createFeature("in2.In2.In2()");
        in1  = factory.createFeature("in1.In1.In1()");
        base = factory.createFeature("base.Base.Base()");
        out1 = factory.createFeature("out1.Out1.Out1()");
        out2 = factory.createFeature("out2.Out2.Out2()");

        in2.addDependency(in1);
        in1.addDependency(base);
        base.addDependency(out1);
        out1.addDependency(out2);
        
        List<String> scopeIncludes = new ArrayList<String>(1);
        scopeIncludes.add("/^base/");
        List<String> filterIncludes = Collections.emptyList();
        
        startCriteria = new RegularExpressionSelectionCriteria();
        startCriteria.setMatchingPackages(false);
        startCriteria.setMatchingClasses(false);
        startCriteria.setMatchingFeatures(false);
        startCriteria.setGlobalIncludes(scopeIncludes);

        stopCriteria = new RegularExpressionSelectionCriteria();
        stopCriteria.setMatchingPackages(false);
        stopCriteria.setMatchingClasses(false);
        stopCriteria.setMatchingFeatures(false);
        stopCriteria.setGlobalIncludes(filterIncludes);
    }

    public void testFeatureToFeatureFromFeature() {
        startCriteria.setMatchingFeatures(true);
        stopCriteria.setMatchingFeatures(true);

        Logger.getLogger(getClass()).info("Start f2f test from feature ...");
        compute(Collections.singleton(base));
        Logger.getLogger(getClass()).info("Stop f2f test from feature ...");

        assertEquals(5, resultFactory.getFeatures().size());
        assertTrue(resultFactory.getFeatures().values().contains(in2));
        assertTrue(resultFactory.getFeatures().values().contains(in1));
        assertTrue(resultFactory.getFeatures().values().contains(base));
        assertTrue(resultFactory.getFeatures().values().contains(out1));
        assertTrue(resultFactory.getFeatures().values().contains(out2));

        assertEquals(0, resultFactory.createFeature("in2.In2.In2()").getInboundDependencies().size());
        assertEquals(1, resultFactory.createFeature("in2.In2.In2()").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createFeature("in1.In1.In1()").getInboundDependencies().size());
        assertEquals(1, resultFactory.createFeature("in1.In1.In1()").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createFeature("base.Base.Base()").getInboundDependencies().size());
        assertEquals(1, resultFactory.createFeature("base.Base.Base()").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createFeature("out1.Out1.Out1()").getInboundDependencies().size());
        assertEquals(1, resultFactory.createFeature("out1.Out1.Out1()").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createFeature("out2.Out2.Out2()").getInboundDependencies().size());
        assertEquals(0, resultFactory.createFeature("out2.Out2.Out2()").getOutboundDependencies().size());

        assertEquals(0, resultFactory.createClass("in2.In2").getInboundDependencies().size());
        assertEquals(0, resultFactory.createClass("in2.In2").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createClass("in1.In1").getInboundDependencies().size());
        assertEquals(0, resultFactory.createClass("in1.In1").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createClass("base.Base").getInboundDependencies().size());
        assertEquals(0, resultFactory.createClass("base.Base").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createClass("out1.Out1").getInboundDependencies().size());
        assertEquals(0, resultFactory.createClass("out1.Out1").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createClass("out2.Out2").getInboundDependencies().size());
        assertEquals(0, resultFactory.createClass("out2.Out2").getOutboundDependencies().size());

        assertEquals(0, resultFactory.createPackage("in2").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("in2").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("in1").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("in1").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("base").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("base").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out1").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out1").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out2").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out2").getOutboundDependencies().size());
    }

    public void testFeatureToFeatureFromPackages() {
        startCriteria.setMatchingFeatures(true);
        stopCriteria.setMatchingFeatures(true);

        Logger.getLogger(getClass()).info("Start f2f test from package list ...");
        compute(factory.getPackages().values());
        Logger.getLogger(getClass()).info("Stop f2f test from package list ...");

        assertEquals(5, resultFactory.getFeatures().size());
        assertTrue(resultFactory.getFeatures().values().contains(in2));
        assertTrue(resultFactory.getFeatures().values().contains(in1));
        assertTrue(resultFactory.getFeatures().values().contains(base));
        assertTrue(resultFactory.getFeatures().values().contains(out1));
        assertTrue(resultFactory.getFeatures().values().contains(out2));

        assertEquals(0, resultFactory.createFeature("in2.In2.In2()").getInboundDependencies().size());
        assertEquals(1, resultFactory.createFeature("in2.In2.In2()").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createFeature("in1.In1.In1()").getInboundDependencies().size());
        assertEquals(1, resultFactory.createFeature("in1.In1.In1()").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createFeature("base.Base.Base()").getInboundDependencies().size());
        assertEquals(1, resultFactory.createFeature("base.Base.Base()").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createFeature("out1.Out1.Out1()").getInboundDependencies().size());
        assertEquals(1, resultFactory.createFeature("out1.Out1.Out1()").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createFeature("out2.Out2.Out2()").getInboundDependencies().size());
        assertEquals(0, resultFactory.createFeature("out2.Out2.Out2()").getOutboundDependencies().size());

        assertEquals(0, resultFactory.createClass("in2.In2").getInboundDependencies().size());
        assertEquals(0, resultFactory.createClass("in2.In2").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createClass("in1.In1").getInboundDependencies().size());
        assertEquals(0, resultFactory.createClass("in1.In1").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createClass("base.Base").getInboundDependencies().size());
        assertEquals(0, resultFactory.createClass("base.Base").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createClass("out1.Out1").getInboundDependencies().size());
        assertEquals(0, resultFactory.createClass("out1.Out1").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createClass("out2.Out2").getInboundDependencies().size());
        assertEquals(0, resultFactory.createClass("out2.Out2").getOutboundDependencies().size());

        assertEquals(0, resultFactory.createPackage("in2").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("in2").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("in1").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("in1").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("base").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("base").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out1").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out1").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out2").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out2").getOutboundDependencies().size());
    }

    public void testClassToClassFromClass() {
        startCriteria.setMatchingClasses(true);
        stopCriteria.setMatchingClasses(true);

        Logger.getLogger(getClass()).info("Start c2c test from class ...");
        compute(Collections.singleton(base.getClassNode()));
        Logger.getLogger(getClass()).info("Stop c2c test from class ...");

        assertEquals(0, resultFactory.getFeatures().size());

        assertEquals(5, resultFactory.getClasses().size());
        assertTrue(resultFactory.getClasses().values().contains(in2.getClassNode()));
        assertTrue(resultFactory.getClasses().values().contains(in1.getClassNode()));
        assertTrue(resultFactory.getClasses().values().contains(base.getClassNode()));
        assertTrue(resultFactory.getClasses().values().contains(out1.getClassNode()));
        assertTrue(resultFactory.getClasses().values().contains(out2.getClassNode()));

        assertEquals(0, resultFactory.createClass("in2.In2").getInboundDependencies().size());
        assertEquals(1, resultFactory.createClass("in2.In2").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createClass("in1.In1").getInboundDependencies().size());
        assertEquals(1, resultFactory.createClass("in1.In1").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createClass("base.Base").getInboundDependencies().size());
        assertEquals(1, resultFactory.createClass("base.Base").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createClass("out1.Out1").getInboundDependencies().size());
        assertEquals(1, resultFactory.createClass("out1.Out1").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createClass("out2.Out2").getInboundDependencies().size());
        assertEquals(0, resultFactory.createClass("out2.Out2").getOutboundDependencies().size());

        assertEquals(0, resultFactory.createPackage("in2").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("in2").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("in1").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("in1").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("base").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("base").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out1").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out1").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out2").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out2").getOutboundDependencies().size());
    }

    public void testClassToClassFromPackageList() {
        startCriteria.setMatchingClasses(true);
        stopCriteria.setMatchingClasses(true);

        Logger.getLogger(getClass()).info("Start c2c test from package list ...");
        compute(factory.getPackages().values());
        Logger.getLogger(getClass()).info("Stop c2c test from package list ...");

        assertEquals(0, resultFactory.getFeatures().size());

        assertEquals(5, resultFactory.getClasses().size());
        assertTrue(resultFactory.getClasses().values().contains(in2.getClassNode()));
        assertTrue(resultFactory.getClasses().values().contains(in1.getClassNode()));
        assertTrue(resultFactory.getClasses().values().contains(base.getClassNode()));
        assertTrue(resultFactory.getClasses().values().contains(out1.getClassNode()));
        assertTrue(resultFactory.getClasses().values().contains(out2.getClassNode()));

        assertEquals(0, resultFactory.createClass("in2.In2").getInboundDependencies().size());
        assertEquals(1, resultFactory.createClass("in2.In2").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createClass("in1.In1").getInboundDependencies().size());
        assertEquals(1, resultFactory.createClass("in1.In1").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createClass("base.Base").getInboundDependencies().size());
        assertEquals(1, resultFactory.createClass("base.Base").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createClass("out1.Out1").getInboundDependencies().size());
        assertEquals(1, resultFactory.createClass("out1.Out1").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createClass("out2.Out2").getInboundDependencies().size());
        assertEquals(0, resultFactory.createClass("out2.Out2").getOutboundDependencies().size());

        assertEquals(0, resultFactory.createPackage("in2").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("in2").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("in1").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("in1").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("base").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("base").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out1").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out1").getOutboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out2").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out2").getOutboundDependencies().size());
    }

    public void testPackageToPackageFromPackage() {
        startCriteria.setMatchingPackages(true);
        stopCriteria.setMatchingPackages(true);

        Logger.getLogger(getClass()).info("Start p2p test from package ...");
        compute(Collections.singleton(base.getClassNode().getPackageNode()));
        Logger.getLogger(getClass()).info("Stop p2p test from package ...");

        assertEquals(0, resultFactory.getFeatures().size());

        assertEquals(0, resultFactory.getClasses().size());

        assertEquals(5, resultFactory.getPackages().size());
        assertTrue(resultFactory.getPackages().values().contains(in2.getClassNode().getPackageNode()));
        assertTrue(resultFactory.getPackages().values().contains(in1.getClassNode().getPackageNode()));
        assertTrue(resultFactory.getPackages().values().contains(base.getClassNode().getPackageNode()));
        assertTrue(resultFactory.getPackages().values().contains(out1.getClassNode().getPackageNode()));
        assertTrue(resultFactory.getPackages().values().contains(out2.getClassNode().getPackageNode()));

        assertEquals(0, resultFactory.createPackage("in2").getInboundDependencies().size());
        assertEquals(1, resultFactory.createPackage("in2").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createPackage("in1").getInboundDependencies().size());
        assertEquals(1, resultFactory.createPackage("in1").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createPackage("base").getInboundDependencies().size());
        assertEquals(1, resultFactory.createPackage("base").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createPackage("out1").getInboundDependencies().size());
        assertEquals(1, resultFactory.createPackage("out1").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createPackage("out2").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out2").getOutboundDependencies().size());
    }

    public void testPackageToPackageFromPackageList() {
        startCriteria.setMatchingPackages(true);
        stopCriteria.setMatchingPackages(true);

        Logger.getLogger(getClass()).info("Start p2p test from package list ...");
        compute(factory.getPackages().values());
        Logger.getLogger(getClass()).info("Stop p2p test from package list ...");

        assertEquals(0, resultFactory.getFeatures().size());

        assertEquals(0, resultFactory.getClasses().size());

        assertEquals(5, resultFactory.getPackages().size());
        assertTrue(resultFactory.getPackages().values().contains(in2.getClassNode().getPackageNode()));
        assertTrue(resultFactory.getPackages().values().contains(in1.getClassNode().getPackageNode()));
        assertTrue(resultFactory.getPackages().values().contains(base.getClassNode().getPackageNode()));
        assertTrue(resultFactory.getPackages().values().contains(out1.getClassNode().getPackageNode()));
        assertTrue(resultFactory.getPackages().values().contains(out2.getClassNode().getPackageNode()));

        assertEquals(0, resultFactory.createPackage("in2").getInboundDependencies().size());
        assertEquals(1, resultFactory.createPackage("in2").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createPackage("in1").getInboundDependencies().size());
        assertEquals(1, resultFactory.createPackage("in1").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createPackage("base").getInboundDependencies().size());
        assertEquals(1, resultFactory.createPackage("base").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createPackage("out1").getInboundDependencies().size());
        assertEquals(1, resultFactory.createPackage("out1").getOutboundDependencies().size());
        assertEquals(1, resultFactory.createPackage("out2").getInboundDependencies().size());
        assertEquals(0, resultFactory.createPackage("out2").getOutboundDependencies().size());
    }

    private void compute(Collection<? extends Node> nodes) {
        RegularExpressionSelectionCriteria localStartCriteria = new RegularExpressionSelectionCriteria();
        localStartCriteria.setGlobalIncludes(startCriteria.getGlobalIncludes());
        RegularExpressionSelectionCriteria localStopCriteria  = new RegularExpressionSelectionCriteria();
        localStopCriteria.setGlobalIncludes(stopCriteria.getGlobalIncludes());

        TransitiveClosure closure = new TransitiveClosure(localStartCriteria, localStopCriteria);
        closure.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        closure.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        closure.traverseNodes(nodes);

        RegularExpressionSelectionCriteria localScopeCriteria  = new RegularExpressionSelectionCriteria();
        localScopeCriteria.setMatchingPackages(startCriteria.isMatchingPackages());
        localScopeCriteria.setMatchingClasses(startCriteria.isMatchingClasses());
        localScopeCriteria.setMatchingFeatures(startCriteria.isMatchingFeatures());
        localScopeCriteria.setGlobalIncludes("//");
        RegularExpressionSelectionCriteria localFilterCriteria = new RegularExpressionSelectionCriteria();
        localFilterCriteria.setMatchingPackages(stopCriteria.isMatchingPackages());
        localFilterCriteria.setMatchingClasses(stopCriteria.isMatchingClasses());
        localFilterCriteria.setMatchingFeatures(stopCriteria.isMatchingFeatures());
        localFilterCriteria.setGlobalIncludes("//");

        GraphSummarizer summarizer = new GraphSummarizer(localScopeCriteria, localFilterCriteria);
        summarizer.traverseNodes(closure.getFactory().getPackages().values());

        resultFactory = summarizer.getScopeFactory();
    }
}
