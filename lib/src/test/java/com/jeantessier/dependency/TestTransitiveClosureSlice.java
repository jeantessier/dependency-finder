/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestTransitiveClosureSlice {
    private final NodeFactory factory = new NodeFactory();

    private final Node in3 = factory.createPackage("in3");
    private final Node in2 = factory.createPackage("in2");
    private final Node in1 = factory.createPackage("in1");
    private final Node base = factory.createPackage("base");
    private final Node out1 = factory.createPackage("out1");
    private final Node out2 = factory.createPackage("out2");
    private final Node out3 = factory.createPackage("out3");

    private final Collection<Node> nodes = Collections.singleton(base);

    private final TransitiveClosure selector = new TransitiveClosure(new RegularExpressionSelectionCriteria("//"), new NullSelectionCriteria());

    @BeforeEach
    void setUp() {
        in3.addDependency(in2);
        in2.addDependency(in1);
        in1.addDependency(base);
        base.addDependency(out1);
        out1.addDependency(out2);
        out2.addDependency(out3);
    }

    @Test
    void testDefaultDepth() {
        selector.traverseNodes(nodes);

        assertEquals(4, selector.getFactory().getPackages().size(), "number of packages");
        assertEquals(0, selector.getFactory().getPackages().get("base").getInboundDependencies().size(), "base.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("base").getOutboundDependencies().size(), "base.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out1").getInboundDependencies().size(), "out1.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out1").getOutboundDependencies().size(), "out1.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out2").getInboundDependencies().size(), "out2.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out2").getOutboundDependencies().size(), "out2.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out3").getInboundDependencies().size(), "out3.Inbound()");
        assertEquals(0, selector.getFactory().getPackages().get("out3").getOutboundDependencies().size(), "out3.Outbound()");
    }

    @Test
    void testUnboundedDepthInboundOutbound() {
        selector.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        selector.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        
        selector.traverseNodes(nodes);

        assertEquals(7, selector.getFactory().getPackages().size(), "number of packages");
        assertEquals(0, selector.getFactory().getPackages().get("in3").getInboundDependencies().size(), "in3.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in3").getOutboundDependencies().size(), "in3.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in2").getInboundDependencies().size(), "in2.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in2").getOutboundDependencies().size(), "in2.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in1").getInboundDependencies().size(), "in1.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in1").getOutboundDependencies().size(), "in1.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("base").getInboundDependencies().size(), "base.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("base").getOutboundDependencies().size(), "base.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out1").getInboundDependencies().size(), "out1.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out1").getOutboundDependencies().size(), "out1.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out2").getInboundDependencies().size(), "out2.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out2").getOutboundDependencies().size(), "out2.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out3").getInboundDependencies().size(), "out3.Inbound()");
        assertEquals(0, selector.getFactory().getPackages().get("out3").getOutboundDependencies().size(), "out3.Outbound()");
    }

    @Test
    void testUnboundedDepthInbound() {
        selector.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        selector.setMaximumOutboundDepth(TransitiveClosure.DO_NOT_FOLLOW);
        
        selector.traverseNodes(nodes);

        assertEquals(4, selector.getFactory().getPackages().size(), "number of packages");
        assertEquals(0, selector.getFactory().getPackages().get("in3").getInboundDependencies().size(), "in3.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in3").getOutboundDependencies().size(), "in3.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in2").getInboundDependencies().size(), "in2.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in2").getOutboundDependencies().size(), "in2.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in1").getInboundDependencies().size(), "in1.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in1").getOutboundDependencies().size(), "in1.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("base").getInboundDependencies().size(), "base.Inbound()");
        assertEquals(0, selector.getFactory().getPackages().get("base").getOutboundDependencies().size(), "base.Outbound()");
    }

    @Test
    void testUnboundedDepthOutbound() {
        selector.setMaximumInboundDepth(TransitiveClosure.DO_NOT_FOLLOW);
        selector.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);

        selector.traverseNodes(nodes);

        assertEquals(4, selector.getFactory().getPackages().size(), "number of packages");
        assertEquals(0, selector.getFactory().getPackages().get("base").getInboundDependencies().size(), "base.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("base").getOutboundDependencies().size(), "base.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out1").getInboundDependencies().size(), "out1.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out1").getOutboundDependencies().size(), "out1.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out2").getInboundDependencies().size(), "out2.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out2").getOutboundDependencies().size(), "out2.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out3").getInboundDependencies().size(), "out3.Inbound()");
        assertEquals(0, selector.getFactory().getPackages().get("out3").getOutboundDependencies().size(), "out3.Outbound()");
    }

    @Test
    void testZeroDepthInboundOutbound() {
        selector.setMaximumInboundDepth(0);
        selector.setMaximumOutboundDepth(0);
        
        selector.traverseNodes(nodes);

        assertEquals(1, selector.getFactory().getPackages().size(), "number of packages");
        assertEquals(0, selector.getFactory().getPackages().get("base").getInboundDependencies().size(), "base.Inbound()");
        assertEquals(0, selector.getFactory().getPackages().get("base").getOutboundDependencies().size(), "base.Outbound()");
    }

    @Test
    void testSingleDepthInboundOutbound() {
        selector.setMaximumInboundDepth(1);
        selector.setMaximumOutboundDepth(1);
        
        selector.traverseNodes(nodes);

        assertEquals(3, selector.getFactory().getPackages().size(), "number of packages");
        assertEquals(0, selector.getFactory().getPackages().get("in1").getInboundDependencies().size(), "in1.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in1").getOutboundDependencies().size(), "in1.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("base").getInboundDependencies().size(), "base.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("base").getOutboundDependencies().size(), "base.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out1").getInboundDependencies().size(), "out1.Inbound()");
        assertEquals(0, selector.getFactory().getPackages().get("out1").getOutboundDependencies().size(), "out1.Outbound()");
    }

    @Test
    void testDoubleDepthInboundOutbound() {
        selector.setMaximumInboundDepth(2);
        selector.setMaximumOutboundDepth(2);
        
        selector.traverseNodes(nodes);

        assertEquals(5, selector.getFactory().getPackages().size(), "number of packages");
        assertEquals(0, selector.getFactory().getPackages().get("in2").getInboundDependencies().size(), "in2.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in2").getOutboundDependencies().size(), "in2.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in1").getInboundDependencies().size(), "in1.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in1").getOutboundDependencies().size(), "in1.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("base").getInboundDependencies().size(), "base.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("base").getOutboundDependencies().size(), "base.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out1").getInboundDependencies().size(), "out1.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out1").getOutboundDependencies().size(), "out1.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out2").getInboundDependencies().size(), "out2.Inbound()");
        assertEquals(0, selector.getFactory().getPackages().get("out2").getOutboundDependencies().size(), "out2.Outbound()");
    }

    @Test
    void testExactDepthInboundOutbound() {
        selector.setMaximumInboundDepth(3);
        selector.setMaximumOutboundDepth(3);
        
        selector.traverseNodes(nodes);

        assertEquals(7, selector.getFactory().getPackages().size(), "number of packages");
        assertEquals(0, selector.getFactory().getPackages().get("in3").getInboundDependencies().size(), "in3.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in3").getOutboundDependencies().size(), "in3.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in2").getInboundDependencies().size(), "in2.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in2").getOutboundDependencies().size(), "in2.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in1").getInboundDependencies().size(), "in1.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in1").getOutboundDependencies().size(), "in1.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("base").getInboundDependencies().size(), "base.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("base").getOutboundDependencies().size(), "base.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out1").getInboundDependencies().size(), "out1.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out1").getOutboundDependencies().size(), "out1.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out2").getInboundDependencies().size(), "out2.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out2").getOutboundDependencies().size(), "out2.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out3").getInboundDependencies().size(), "out3.Inbound()");
        assertEquals(0, selector.getFactory().getPackages().get("out3").getOutboundDependencies().size(), "out3.Outbound()");
    }

    @Test
    void testOverDepthInboundOutbound() {
        selector.setMaximumInboundDepth(4);
        selector.setMaximumOutboundDepth(4);
        
        selector.traverseNodes(nodes);

        assertEquals(7, selector.getFactory().getPackages().size(), "number of packages");
        assertEquals(0, selector.getFactory().getPackages().get("in3").getInboundDependencies().size(), "in3.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in3").getOutboundDependencies().size(), "in3.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in2").getInboundDependencies().size(), "in2.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in2").getOutboundDependencies().size(), "in2.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in1").getInboundDependencies().size(), "in1.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("in1").getOutboundDependencies().size(), "in1.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("base").getInboundDependencies().size(), "base.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("base").getOutboundDependencies().size(), "base.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out1").getInboundDependencies().size(), "out1.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out1").getOutboundDependencies().size(), "out1.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out2").getInboundDependencies().size(), "out2.Inbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out2").getOutboundDependencies().size(), "out2.Outbound()");
        assertEquals(1, selector.getFactory().getPackages().get("out3").getInboundDependencies().size(), "out3.Inbound()");
        assertEquals(0, selector.getFactory().getPackages().get("out3").getOutboundDependencies().size(), "out3.Outbound()");
    }
}
