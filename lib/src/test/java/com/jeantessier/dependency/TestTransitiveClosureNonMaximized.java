/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import org.apache.logging.log4j.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestTransitiveClosureNonMaximized {
    private final Logger logger = LogManager.getLogger();

    private final NodeFactory factory = new NodeFactory();

    private final FeatureNode in2 = factory.createFeature("in2.In2.In2()");
    private final FeatureNode in1 = factory.createFeature("in1.In1.In1()");
    private final FeatureNode base = factory.createFeature("base.Base.Base()");
    private final FeatureNode out1 = factory.createFeature("out1.Out1.Out1()");
    private final FeatureNode out2 = factory.createFeature("out2.Out2.Out2()");
    
    private final RegularExpressionSelectionCriteria startCriteria = new RegularExpressionSelectionCriteria();
    private final RegularExpressionSelectionCriteria stopCriteria = new RegularExpressionSelectionCriteria();

    @BeforeEach
    void setUp() {
        in2.addDependency(in1);
        in1.addDependency(base);
        base.addDependency(out1);
        out1.addDependency(out2);

        startCriteria.setMatchingPackages(false);
        startCriteria.setMatchingClasses(false);
        startCriteria.setMatchingFeatures(false);
        startCriteria.setGlobalIncludes(List.of("/^base/"));

        stopCriteria.setMatchingPackages(false);
        stopCriteria.setMatchingClasses(false);
        stopCriteria.setMatchingFeatures(false);
    }

    @Test
    void testFeatureToFeatureFromFeature() {
        startCriteria.setMatchingFeatures(true);
        stopCriteria.setMatchingFeatures(true);

        logger.info("Start f2f test from feature ...");
        var resultFactory = compute(Collections.singleton(base));
        logger.info("Stop f2f test from feature ...");

        assertEquals(5, resultFactory.getFeatures().size());
        assertTrue(resultFactory.getFeatures().containsValue(in2));
        assertTrue(resultFactory.getFeatures().containsValue(in1));
        assertTrue(resultFactory.getFeatures().containsValue(base));
        assertTrue(resultFactory.getFeatures().containsValue(out1));
        assertTrue(resultFactory.getFeatures().containsValue(out2));

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

    @Test
    void testFeatureToFeatureFromPackages() {
        startCriteria.setMatchingFeatures(true);
        stopCriteria.setMatchingFeatures(true);

        logger.info("Start f2f test from package list ...");
        var resultFactory = compute(factory.getPackages().values());
        logger.info("Stop f2f test from package list ...");

        assertEquals(5, resultFactory.getFeatures().size());
        assertTrue(resultFactory.getFeatures().containsValue(in2));
        assertTrue(resultFactory.getFeatures().containsValue(in1));
        assertTrue(resultFactory.getFeatures().containsValue(base));
        assertTrue(resultFactory.getFeatures().containsValue(out1));
        assertTrue(resultFactory.getFeatures().containsValue(out2));

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

    @Test
    void testClassToClassFromClass() {
        startCriteria.setMatchingClasses(true);
        stopCriteria.setMatchingClasses(true);

        logger.info("Start c2c test from class ...");
        var resultFactory = compute(Collections.singleton(base.getClassNode()));
        logger.info("Stop c2c test from class ...");

        assertEquals(0, resultFactory.getFeatures().size());

        assertEquals(5, resultFactory.getClasses().size());
        assertTrue(resultFactory.getClasses().containsValue(in2.getClassNode()));
        assertTrue(resultFactory.getClasses().containsValue(in1.getClassNode()));
        assertTrue(resultFactory.getClasses().containsValue(base.getClassNode()));
        assertTrue(resultFactory.getClasses().containsValue(out1.getClassNode()));
        assertTrue(resultFactory.getClasses().containsValue(out2.getClassNode()));

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

    @Test
    void testClassToClassFromPackageList() {
        startCriteria.setMatchingClasses(true);
        stopCriteria.setMatchingClasses(true);

        logger.info("Start c2c test from package list ...");
        var resultFactory = compute(factory.getPackages().values());
        logger.info("Stop c2c test from package list ...");

        assertEquals(0, resultFactory.getFeatures().size());

        assertEquals(5, resultFactory.getClasses().size());
        assertTrue(resultFactory.getClasses().containsValue(in2.getClassNode()));
        assertTrue(resultFactory.getClasses().containsValue(in1.getClassNode()));
        assertTrue(resultFactory.getClasses().containsValue(base.getClassNode()));
        assertTrue(resultFactory.getClasses().containsValue(out1.getClassNode()));
        assertTrue(resultFactory.getClasses().containsValue(out2.getClassNode()));

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

    @Test
    void testPackageToPackageFromPackage() {
        startCriteria.setMatchingPackages(true);
        stopCriteria.setMatchingPackages(true);

        logger.info("Start p2p test from package ...");
        var resultFactory = compute(Collections.singleton(base.getClassNode().getPackageNode()));
        logger.info("Stop p2p test from package ...");

        assertEquals(0, resultFactory.getFeatures().size());

        assertEquals(0, resultFactory.getClasses().size());

        assertEquals(5, resultFactory.getPackages().size());
        assertTrue(resultFactory.getPackages().containsValue(in2.getClassNode().getPackageNode()));
        assertTrue(resultFactory.getPackages().containsValue(in1.getClassNode().getPackageNode()));
        assertTrue(resultFactory.getPackages().containsValue(base.getClassNode().getPackageNode()));
        assertTrue(resultFactory.getPackages().containsValue(out1.getClassNode().getPackageNode()));
        assertTrue(resultFactory.getPackages().containsValue(out2.getClassNode().getPackageNode()));

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

    @Test
    void testPackageToPackageFromPackageList() {
        startCriteria.setMatchingPackages(true);
        stopCriteria.setMatchingPackages(true);

        logger.info("Start p2p test from package list ...");
        var resultFactory = compute(factory.getPackages().values());
        logger.info("Stop p2p test from package list ...");

        assertEquals(0, resultFactory.getFeatures().size());

        assertEquals(0, resultFactory.getClasses().size());

        assertEquals(5, resultFactory.getPackages().size());
        assertTrue(resultFactory.getPackages().containsValue(in2.getClassNode().getPackageNode()));
        assertTrue(resultFactory.getPackages().containsValue(in1.getClassNode().getPackageNode()));
        assertTrue(resultFactory.getPackages().containsValue(base.getClassNode().getPackageNode()));
        assertTrue(resultFactory.getPackages().containsValue(out1.getClassNode().getPackageNode()));
        assertTrue(resultFactory.getPackages().containsValue(out2.getClassNode().getPackageNode()));

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

    private NodeFactory compute(Collection<? extends Node> nodes) {
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

        return summarizer.getScopeFactory();
    }
}
