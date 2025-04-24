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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestTransitiveClosureEngine {
    private final NodeFactory factory = new NodeFactory();

    private final Node a = factory.createPackage("a");
    private final Node a_A = factory.createClass("a.A");
    private final Node a_A_a = factory.createFeature("a.A.a");

    private final Node b = factory.createPackage("b");
    private final Node b_B = factory.createClass("b.B");
    private final Node b_B_b = factory.createFeature("b.B.b");

    private final Node c = factory.createPackage("c");
    private final Node c_C = factory.createClass("c.C");
    private final Node c_C_c = factory.createFeature("c.C.c");

    private final RegularExpressionSelectionCriteria startCriteria = new RegularExpressionSelectionCriteria();
    private final RegularExpressionSelectionCriteria stopCriteria = new RegularExpressionSelectionCriteria();
    
    @BeforeEach
    void setUp() {
        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);

        stopCriteria.setGlobalIncludes("");
    }

    @Test
    void testSelectScope() {
        startCriteria.setGlobalIncludes("/a.A.a/");

        GraphCopier copier = new GraphCopier(new SelectiveTraversalStrategy(startCriteria, new RegularExpressionSelectionCriteria()));

        copier.traverseNodes(factory.getPackages().values());
        
        assertEquals(1, copier.getScopeFactory().getPackages().size(), "packages in scope: ");
        assertEquals(1, copier.getScopeFactory().getClasses().size(), "classes in scope");
        assertEquals(1, copier.getScopeFactory().getFeatures().size(), "features in scope");

        assertEquals(a, copier.getScopeFactory().getPackages().get("a"), "package b in scope");
        assertEquals(a_A, copier.getScopeFactory().getClasses().get("a.A"), "class a.A in scope");
        assertEquals(a_A_a, copier.getScopeFactory().getFeatures().get("a.A.a"), "feature a.A.a in scope");
    }

    @Test
    void testOutboundStartingPoint() {
        startCriteria.setGlobalIncludes("/a.A.a/");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureOutboundSelector());

        assertEquals(1, engine.getNbLayers(), "Nb layers");

        assertEquals(1, engine.getLayer(0).size(), "layer 0");
        assertEquals(a_A_a, engine.getLayer(0).iterator().next(), "a.A.a in layer 0");
        assertNotSame(a_A_a, engine.getLayer(0).iterator().next(), "a.A.a in layer 0");

        assertEquals(0, engine.getLayer(0).iterator().next().getOutboundDependencies().size(), "Nb outbounds from a.A.a");
        
        assertEquals(1, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(1, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(1, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(a, engine.getFactory().getPackages().get("a"), "package a in scope");
        assertEquals(a_A, engine.getFactory().getClasses().get("a.A"), "class a.A in scope");
        assertEquals(a_A_a, engine.getFactory().getFeatures().get("a.A.a"), "feature a.A.a in scope");
    }

    @Test
    void testOneOutboundLayer() {
        startCriteria.setGlobalIncludes("/a.A.a/");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureOutboundSelector());
        engine.computeNextLayer();

        assertEquals(2, engine.getNbLayers(), "Nb layers");

        assertEquals(1, engine.getLayer(1).size(), "layer 1");
        assertEquals(b_B_b, engine.getLayer(1).iterator().next(), "b.B.b in layer 1");
        assertNotSame(b_B_b, engine.getLayer(1).iterator().next(), "b.B.b in layer 1");

        assertEquals(a_A_a.getOutboundDependencies().size(), engine.getLayer(0).iterator().next().getOutboundDependencies().size(), "Nb outbounds from a.A.a");
        assertEquals(0, engine.getLayer(1).iterator().next().getOutboundDependencies().size(), "Nb outbounds from b.B.b");
        
        assertEquals(2, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(2, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(2, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(a, engine.getFactory().getPackages().get("a"), "package a in scope");
        assertEquals(a_A, engine.getFactory().getClasses().get("a.A"), "class a.A in scope");
        assertEquals(a_A_a, engine.getFactory().getFeatures().get("a.A.a"), "feature a.A.a in scope");
        assertEquals(b, engine.getFactory().getPackages().get("b"), "package b in scope");
        assertEquals(b_B, engine.getFactory().getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, engine.getFactory().getFeatures().get("b.B.b"), "feature b.B.b in scope");
    }

    @Test
    void testTwoOutboundLayers() {
        startCriteria.setGlobalIncludes("/a.A.a/");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureOutboundSelector());
        engine.computeNextLayer();
        engine.computeNextLayer();

        assertEquals(3, engine.getNbLayers(), "Nb layers");

        assertEquals(1, engine.getLayer(1).size(), "layer 2");
        assertEquals(c_C_c, engine.getLayer(2).iterator().next(), "c.C.c in layer 2");
        assertNotSame(c_C_c, engine.getLayer(2).iterator().next(), "c.C.c in layer 2");

        assertEquals(a_A_a.getOutboundDependencies().size(), engine.getLayer(0).iterator().next().getOutboundDependencies().size(), "Nb outbounds from a.A.a");
        assertEquals(b_B_b.getOutboundDependencies().size(), engine.getLayer(1).iterator().next().getOutboundDependencies().size(), "Nb outbounds from b.B.b");
        assertEquals(0, engine.getLayer(2).iterator().next().getOutboundDependencies().size(), "Nb outbounds from c.C.c");
        
        assertEquals(3, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(3, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(3, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(a, engine.getFactory().getPackages().get("a"), "package a in scope");
        assertEquals(a_A, engine.getFactory().getClasses().get("a.A"), "class a.A in scope");
        assertEquals(a_A_a, engine.getFactory().getFeatures().get("a.A.a"), "feature a.A.a in scope");
        assertEquals(b, engine.getFactory().getPackages().get("b"), "package b in scope");
        assertEquals(b_B, engine.getFactory().getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, engine.getFactory().getFeatures().get("b.B.b"), "feature b.B.b in scope");
        assertEquals(c, engine.getFactory().getPackages().get("c"), "package c in scope");
        assertEquals(c_C, engine.getFactory().getClasses().get("c.C"), "class c.C in scope");
        assertEquals(c_C_c, engine.getFactory().getFeatures().get("c.C.c"), "feature c.C.c in scope");
    }

    @Test
    void testThreeOutboundLayers() {
        startCriteria.setGlobalIncludes("/a.A.a/");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureOutboundSelector());
        engine.computeNextLayer();
        engine.computeNextLayer();
        engine.computeNextLayer();

        assertEquals(3, engine.getNbLayers(), "Nb layers");

        assertEquals(a_A_a.getOutboundDependencies().size(), engine.getLayer(0).iterator().next().getOutboundDependencies().size(), "Nb outbounds from a.A.a");
        assertEquals(b_B_b.getOutboundDependencies().size(), engine.getLayer(1).iterator().next().getOutboundDependencies().size(), "Nb outbounds from b.B.b");
        assertEquals(c_C_c.getOutboundDependencies().size(), engine.getLayer(2).iterator().next().getOutboundDependencies().size(), "Nb outbounds from c.C.c");
        
        assertEquals(3, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(3, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(3, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(a, engine.getFactory().getPackages().get("a"), "package a in scope");
        assertEquals(a_A, engine.getFactory().getClasses().get("a.A"), "class a.A in scope");
        assertEquals(a_A_a, engine.getFactory().getFeatures().get("a.A.a"), "feature a.A.a in scope");
        assertEquals(b, engine.getFactory().getPackages().get("b"), "package b in scope");
        assertEquals(b_B, engine.getFactory().getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, engine.getFactory().getFeatures().get("b.B.b"), "feature b.B.b in scope");
        assertEquals(c, engine.getFactory().getPackages().get("c"), "package c in scope");
        assertEquals(c_C, engine.getFactory().getClasses().get("c.C"), "class c.C in scope");
        assertEquals(c_C_c, engine.getFactory().getFeatures().get("c.C.c"), "feature c.C.c in scope");
    }

    @Test
    void testFourOutboundLayers() {
        startCriteria.setGlobalIncludes("/a.A.a/");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureOutboundSelector());
        engine.computeNextLayer();
        engine.computeNextLayer();
        engine.computeNextLayer();
        engine.computeNextLayer();

        assertEquals(3, engine.getNbLayers(), "Nb layers");

        assertEquals(a_A_a.getOutboundDependencies().size(), engine.getLayer(0).iterator().next().getOutboundDependencies().size(), "Nb outbounds from a.A.a");
        assertEquals(b_B_b.getOutboundDependencies().size(), engine.getLayer(1).iterator().next().getOutboundDependencies().size(), "Nb outbounds from b.B.b");
        assertEquals(c_C_c.getOutboundDependencies().size(), engine.getLayer(2).iterator().next().getOutboundDependencies().size(), "Nb outbounds from c.C.c");
        
        assertEquals(3, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(3, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(3, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(a, engine.getFactory().getPackages().get("a"), "package a in scope");
        assertEquals(a_A, engine.getFactory().getClasses().get("a.A"), "class a.A in scope");
        assertEquals(a_A_a, engine.getFactory().getFeatures().get("a.A.a"), "feature a.A.a in scope");
        assertEquals(b, engine.getFactory().getPackages().get("b"), "package b in scope");
        assertEquals(b_B, engine.getFactory().getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, engine.getFactory().getFeatures().get("b.B.b"), "feature b.B.b in scope");
        assertEquals(c, engine.getFactory().getPackages().get("c"), "package c in scope");
        assertEquals(c_C, engine.getFactory().getClasses().get("c.C"), "class c.C in scope");
        assertEquals(c_C_c, engine.getFactory().getFeatures().get("c.C.c"), "feature c.C.c in scope");
    }

    @Test
    void testInboundStartingPoint() {
        startCriteria.setGlobalIncludes("/c.C.c/");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());

        assertEquals(1, engine.getNbLayers(), "Nb layers");

        assertEquals(1, engine.getLayer(0).size(), "layer 0");
        assertEquals(c_C_c, engine.getLayer(0).iterator().next(), "c.C.c in layer 0");
        assertNotSame(c_C_c, engine.getLayer(0).iterator().next(), "c.C.c in layer 0");

        assertEquals(0, engine.getLayer(0).iterator().next().getInboundDependencies().size(), "Nb inbounds from c.C.c");
        
        assertEquals(1, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(1, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(1, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(c, engine.getFactory().getPackages().get("c"), "package c in scope");
        assertEquals(c_C, engine.getFactory().getClasses().get("c.C"), "class c.C in scope");
        assertEquals(c_C_c, engine.getFactory().getFeatures().get("c.C.c"), "feature c.C.c in scope");
    }

    @Test
    void testOneInboundLayer() {
        startCriteria.setGlobalIncludes("/c.C.c/");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeNextLayer();

        assertEquals(2, engine.getNbLayers(), "Nb layers");

        assertEquals(1, engine.getLayer(1).size(), "layer 1");
        assertEquals(b_B_b, engine.getLayer(1).iterator().next(), "b.B.b in layer 1");
        assertNotSame(b_B_b, engine.getLayer(1).iterator().next(), "b.B.b in layer 1");

        assertEquals(c_C_c.getInboundDependencies().size(), engine.getLayer(0).iterator().next().getInboundDependencies().size(), "Nb inbounds from c.C.c");
        assertEquals(0, engine.getLayer(1).iterator().next().getInboundDependencies().size(), "Nb inbounds from b.B.b");
        
        assertEquals(2, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(2, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(2, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(b, engine.getFactory().getPackages().get("b"), "package b in scope");
        assertEquals(b_B, engine.getFactory().getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, engine.getFactory().getFeatures().get("b.B.b"), "feature b.B.b in scope");
        assertEquals(c, engine.getFactory().getPackages().get("c"), "package c in scope");
        assertEquals(c_C, engine.getFactory().getClasses().get("c.C"), "class c.C in scope");
        assertEquals(c_C_c, engine.getFactory().getFeatures().get("c.C.c"), "feature c.C.c in scope");
    }

    @Test
    void testTwoInboundLayers() {
        startCriteria.setGlobalIncludes("/c.C.c/");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeNextLayer();
        engine.computeNextLayer();

        assertEquals(3, engine.getNbLayers(), "Nb layers");

        assertEquals(1, engine.getLayer(1).size(), "layer 2");
        assertEquals(a_A_a, engine.getLayer(2).iterator().next(), "a.A.a in layer 2");
        assertNotSame(a_A_a, engine.getLayer(2).iterator().next(), "a.A.a in layer 2");

        assertEquals(c_C_c.getInboundDependencies().size(), engine.getLayer(0).iterator().next().getInboundDependencies().size(), "Nb inbounds from c.C.c");
        assertEquals(b_B_b.getInboundDependencies().size(), engine.getLayer(1).iterator().next().getInboundDependencies().size(), "Nb inbounds from b.B.b");
        assertEquals(0, engine.getLayer(2).iterator().next().getInboundDependencies().size(), "Nb inbounds from a.A.a");
        
        assertEquals(3, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(3, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(3, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(a, engine.getFactory().getPackages().get("a"), "package a in scope");
        assertEquals(a_A, engine.getFactory().getClasses().get("a.A"), "class a.A in scope");
        assertEquals(a_A_a, engine.getFactory().getFeatures().get("a.A.a"), "feature a.A.a in scope");
        assertEquals(b, engine.getFactory().getPackages().get("b"), "package b in scope");
        assertEquals(b_B, engine.getFactory().getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, engine.getFactory().getFeatures().get("b.B.b"), "feature b.B.b in scope");
        assertEquals(c, engine.getFactory().getPackages().get("c"), "package c in scope");
        assertEquals(c_C, engine.getFactory().getClasses().get("c.C"), "class c.C in scope");
        assertEquals(c_C_c, engine.getFactory().getFeatures().get("c.C.c"), "feature c.C.c in scope");
    }

    @Test
    void testThreeInboundLayers() {
        startCriteria.setGlobalIncludes("/c.C.c/");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeNextLayer();
        engine.computeNextLayer();
        engine.computeNextLayer();

        assertEquals(3, engine.getNbLayers(), "Nb layers");

        assertEquals(c_C_c.getInboundDependencies().size(), engine.getLayer(0).iterator().next().getInboundDependencies().size(), "Nb inbounds from c.C.c");
        assertEquals(b_B_b.getInboundDependencies().size(), engine.getLayer(1).iterator().next().getInboundDependencies().size(), "Nb inbounds from b.B.b");
        assertEquals(a_A_a.getInboundDependencies().size(), engine.getLayer(2).iterator().next().getInboundDependencies().size(), "Nb inbounds from a.A.a");
        
        assertEquals(3, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(3, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(3, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(a, engine.getFactory().getPackages().get("a"), "package a in scope");
        assertEquals(a_A, engine.getFactory().getClasses().get("a.A"), "class a.A in scope");
        assertEquals(a_A_a, engine.getFactory().getFeatures().get("a.A.a"), "feature a.A.a in scope");
        assertEquals(b, engine.getFactory().getPackages().get("b"), "package b in scope");
        assertEquals(b_B, engine.getFactory().getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, engine.getFactory().getFeatures().get("b.B.b"), "feature b.B.b in scope");
        assertEquals(c, engine.getFactory().getPackages().get("c"), "package c in scope");
        assertEquals(c_C, engine.getFactory().getClasses().get("c.C"), "class c.C in scope");
        assertEquals(c_C_c, engine.getFactory().getFeatures().get("c.C.c"), "feature c.C.c in scope");
    }

    @Test
    void testFourInboundLayers() {
        startCriteria.setGlobalIncludes("/c.C.c/");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeNextLayer();
        engine.computeNextLayer();
        engine.computeNextLayer();
        engine.computeNextLayer();

        assertEquals(3, engine.getNbLayers(), "Nb layers");

        assertEquals(c_C_c.getInboundDependencies().size(), engine.getLayer(0).iterator().next().getInboundDependencies().size(), "Nb inbounds from c.C.c");
        assertEquals(b_B_b.getInboundDependencies().size(), engine.getLayer(1).iterator().next().getInboundDependencies().size(), "Nb inbounds from b.B.b");
        assertEquals(a_A_a.getInboundDependencies().size(), engine.getLayer(2).iterator().next().getInboundDependencies().size(), "Nb inbounds from a.A.a");
        
        assertEquals(3, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(3, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(3, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(a, engine.getFactory().getPackages().get("a"), "package a in scope");
        assertEquals(a_A, engine.getFactory().getClasses().get("a.A"), "class a.A in scope");
        assertEquals(a_A_a, engine.getFactory().getFeatures().get("a.A.a"), "feature a.A.a in scope");
        assertEquals(b, engine.getFactory().getPackages().get("b"), "package b in scope");
        assertEquals(b_B, engine.getFactory().getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, engine.getFactory().getFeatures().get("b.B.b"), "feature b.B.b in scope");
        assertEquals(c, engine.getFactory().getPackages().get("c"), "package c in scope");
        assertEquals(c_C, engine.getFactory().getClasses().get("c.C"), "class c.C in scope");
        assertEquals(c_C_c, engine.getFactory().getFeatures().get("c.C.c"), "feature c.C.c in scope");
    }

    @Test
    void testStopCriteria() {
        startCriteria.setGlobalIncludes("/c.C.c/");
        stopCriteria.setGlobalIncludes("/b.B.b/");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeNextLayer();
        engine.computeNextLayer();
        engine.computeNextLayer();
        engine.computeNextLayer();

        assertEquals(2, engine.getNbLayers(), "Nb layers");

        assertEquals(c_C_c.getInboundDependencies().size(), engine.getLayer(0).iterator().next().getInboundDependencies().size(), "Nb inbounds from c.C.c");
        assertEquals(0, engine.getLayer(1).iterator().next().getInboundDependencies().size(), "Nb inbounds from b.B.b");
        
        assertEquals(2, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(2, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(2, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(b, engine.getFactory().getPackages().get("b"), "package b in scope");
        assertEquals(b_B, engine.getFactory().getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, engine.getFactory().getFeatures().get("b.B.b"), "feature b.B.b in scope");
        assertEquals(c, engine.getFactory().getPackages().get("c"), "package c in scope");
        assertEquals(c_C, engine.getFactory().getClasses().get("c.C"), "class c.C in scope");
        assertEquals(c_C_c, engine.getFactory().getFeatures().get("c.C.c"), "feature c.C.c in scope");
    }

    @Test
    void testComputeAllLayers() {
        startCriteria.setGlobalIncludes("/c.C.c/");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeAllLayers();

        assertEquals(3, engine.getNbLayers(), "Nb layers");

        assertEquals(c_C_c.getInboundDependencies().size(), engine.getLayer(0).iterator().next().getInboundDependencies().size(), "Nb inbounds from c.C.c");
        assertEquals(b_B_b.getInboundDependencies().size(), engine.getLayer(1).iterator().next().getInboundDependencies().size(), "Nb inbounds from b.B.b");
        assertEquals(a_A_a.getInboundDependencies().size(), engine.getLayer(2).iterator().next().getInboundDependencies().size(), "Nb inbounds from a.A.a");
        
        assertEquals(3, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(3, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(3, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(a, engine.getFactory().getPackages().get("a"), "package a in scope");
        assertEquals(a_A, engine.getFactory().getClasses().get("a.A"), "class a.A in scope");
        assertEquals(a_A_a, engine.getFactory().getFeatures().get("a.A.a"), "feature a.A.a in scope");
        assertEquals(b, engine.getFactory().getPackages().get("b"), "package b in scope");
        assertEquals(b_B, engine.getFactory().getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, engine.getFactory().getFeatures().get("b.B.b"), "feature b.B.b in scope");
        assertEquals(c, engine.getFactory().getPackages().get("c"), "package c in scope");
        assertEquals(c_C, engine.getFactory().getClasses().get("c.C"), "class c.C in scope");
        assertEquals(c_C_c, engine.getFactory().getFeatures().get("c.C.c"), "feature c.C.c in scope");
    }

    @Test
    void testComputeAllLayersWithStopCriteria() {
        startCriteria.setGlobalIncludes("/c.C.c/");
        stopCriteria.setGlobalIncludes("/b.B.b/");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeAllLayers();

        assertEquals(2, engine.getNbLayers(), "Nb layers");

        assertEquals(c_C_c.getInboundDependencies().size(), engine.getLayer(0).iterator().next().getInboundDependencies().size(), "Nb inbounds from c.C.c");
        assertEquals(0, engine.getLayer(1).iterator().next().getInboundDependencies().size(), "Nb inbounds from b.B.b");
        
        assertEquals(2, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(2, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(2, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(b, engine.getFactory().getPackages().get("b"), "package b in scope");
        assertEquals(b_B, engine.getFactory().getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, engine.getFactory().getFeatures().get("b.B.b"), "feature b.B.b in scope");
        assertEquals(c, engine.getFactory().getPackages().get("c"), "package c in scope");
        assertEquals(c_C, engine.getFactory().getClasses().get("c.C"), "class c.C in scope");
        assertEquals(c_C_c, engine.getFactory().getFeatures().get("c.C.c"), "feature c.C.c in scope");
    }

    @Test
    void testComputeAllLayersUntilStartCriteria() {
        startCriteria.setGlobalIncludes("/c.C.c/");
        stopCriteria.setGlobalIncludes("//");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeAllLayers();

        assertEquals(1, engine.getNbLayers(), "Nb layers");

        assertEquals(0, engine.getLayer(0).iterator().next().getInboundDependencies().size(), "Nb inbounds from c.C.c");
        
        assertEquals(1, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(1, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(1, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(c, engine.getFactory().getPackages().get("c"), "package c in scope");
        assertEquals(c_C, engine.getFactory().getClasses().get("c.C"), "class c.C in scope");
        assertEquals(c_C_c, engine.getFactory().getFeatures().get("c.C.c"), "feature c.C.c in scope");
    }

    @Test
    void testCompute1LayerOnly() {
        startCriteria.setGlobalIncludes("/c.C.c/");
        stopCriteria.setGlobalIncludes("");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeLayers(1);

        assertEquals(2, engine.getNbLayers(), "Nb layers");

        assertEquals(c_C_c.getInboundDependencies().size(), engine.getLayer(0).iterator().next().getInboundDependencies().size(), "Nb inbounds from c.C.c");
        assertEquals(0, engine.getLayer(1).iterator().next().getInboundDependencies().size(), "Nb inbounds from b.B.b");
        
        assertEquals(2, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(2, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(2, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(b, engine.getFactory().getPackages().get("b"), "package b in scope");
        assertEquals(b_B, engine.getFactory().getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, engine.getFactory().getFeatures().get("b.B.b"), "feature b.B.b in scope");
        assertEquals(c, engine.getFactory().getPackages().get("c"), "package c in scope");
        assertEquals(c_C, engine.getFactory().getClasses().get("c.C"), "class c.C in scope");
        assertEquals(c_C_c, engine.getFactory().getFeatures().get("c.C.c"), "feature c.C.c in scope");
    }

    @Test
    void testCompute4LayersWithStopCriteria() {
        startCriteria.setGlobalIncludes("/c.C.c/");
        stopCriteria.setGlobalIncludes("/b.B.b/");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeLayers(4);

        assertEquals(2, engine.getNbLayers(), "Nb layers");

        assertEquals(c_C_c.getInboundDependencies().size(), engine.getLayer(0).iterator().next().getInboundDependencies().size(), "Nb inbounds from c.C.c");
        assertEquals(0, engine.getLayer(1).iterator().next().getInboundDependencies().size(), "Nb inbounds from b.B.b");
        
        assertEquals(2, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(2, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(2, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(b, engine.getFactory().getPackages().get("b"), "package b in scope");
        assertEquals(b_B, engine.getFactory().getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, engine.getFactory().getFeatures().get("b.B.b"), "feature b.B.b in scope");
        assertEquals(c, engine.getFactory().getPackages().get("c"), "package c in scope");
        assertEquals(c_C, engine.getFactory().getClasses().get("c.C"), "class c.C in scope");
        assertEquals(c_C_c, engine.getFactory().getFeatures().get("c.C.c"), "feature c.C.c in scope");
    }

    @Test
    void testComputeAllOutboundLayersP2P() {
        startCriteria.setGlobalIncludes("/^a/");
        stopCriteria.setGlobalIncludes("/^c/");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureOutboundSelector());
        engine.computeAllLayers();

        assertEquals(3, engine.getNbLayers(), "Nb layers");

        var node = engine.getLayer(0).stream()
                .filter(n -> n.equals(a_A_a))
                .findAny()
                .orElse(null);

        assertEquals(3, engine.getLayer(0).size(), "Layer 0 size");
        assertEquals(a_A_a.getName(), node.getName(), "Layer 0 content");
        assertEquals(a_A_a.getOutboundDependencies().size(), node.getOutboundDependencies().size(), "Nb outbounds from a.A.a");
        assertEquals(1, engine.getLayer(1).size(), "Layer 1 size");
        assertEquals(b_B_b.getName(), engine.getLayer(1).iterator().next().getName(), "Layer 1 content");
        assertEquals(b_B_b.getOutboundDependencies().size(), engine.getLayer(1).iterator().next().getOutboundDependencies().size(), "Nb outbounds from b.B.b");
        assertEquals(1, engine.getLayer(2).size(), "Layer 2 size");
        assertEquals(c_C_c.getName(), engine.getLayer(2).iterator().next().getName(), "Layer 2 content");
        assertEquals(c_C_c.getOutboundDependencies().size(), engine.getLayer(2).iterator().next().getOutboundDependencies().size(), "Nb outbounds from c.C.c");
        
        assertEquals(3, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(3, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(3, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(a, engine.getFactory().getPackages().get("a"), "package a in scope");
        assertEquals(a_A, engine.getFactory().getClasses().get("a.A"), "class a.A in scope");
        assertEquals(a_A_a, engine.getFactory().getFeatures().get("a.A.a"), "feature a.A.a in scope");
        assertEquals(b, engine.getFactory().getPackages().get("b"), "package b in scope");
        assertEquals(b_B, engine.getFactory().getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, engine.getFactory().getFeatures().get("b.B.b"), "feature b.B.b in scope");
        assertEquals(c, engine.getFactory().getPackages().get("c"), "package c in scope");
        assertEquals(c_C, engine.getFactory().getClasses().get("c.C"), "class c.C in scope");
        assertEquals(c_C_c, engine.getFactory().getFeatures().get("c.C.c"), "feature c.C.c in scope");
    }

    @Test
    void testComputeAllInboundLayersP2P() {
        startCriteria.setGlobalIncludes("/^c/");
        stopCriteria.setGlobalIncludes("/^a/");

        var engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeAllLayers();

        assertEquals(3, engine.getNbLayers(), "Nb layers");

        var node = engine.getLayer(0).stream()
                .filter(n -> n.equals(c_C_c))
                .findAny()
                .orElse(null);

        assertEquals(3, engine.getLayer(0).size(), "Layer 0 size");
        assertEquals(c_C_c.getName(), node.getName(), "Layer 0 content");
        assertEquals(c_C_c.getInboundDependencies().size(), node.getInboundDependencies().size(), "Nb inbounds from c.C.c");
        assertEquals(1, engine.getLayer(1).size(), "Layer 1 size");
        assertEquals(b_B_b.getName(), engine.getLayer(1).iterator().next().getName(), "Layer 1 content");
        assertEquals(b_B_b.getInboundDependencies().size(), engine.getLayer(1).iterator().next().getInboundDependencies().size(), "Nb inbounds from b.B.b");
        assertEquals(1, engine.getLayer(1).size(), "Layer 2 size");
        assertEquals(a_A_a.getName(), engine.getLayer(2).iterator().next().getName(), "Layer 2 content");
        assertEquals(a_A_a.getInboundDependencies().size(), engine.getLayer(2).iterator().next().getInboundDependencies().size(), "Nb inbounds from a.A.a");
        
        assertEquals(3, engine.getFactory().getPackages().size(), "packages in scope: ");
        assertEquals(3, engine.getFactory().getClasses().size(), "classes in scope");
        assertEquals(3, engine.getFactory().getFeatures().size(), "features in scope");

        assertEquals(a, engine.getFactory().getPackages().get("a"), "package a in scope");
        assertEquals(a_A, engine.getFactory().getClasses().get("a.A"), "class a.A in scope");
        assertEquals(a_A_a, engine.getFactory().getFeatures().get("a.A.a"), "feature a.A.a in scope");
        assertEquals(b, engine.getFactory().getPackages().get("b"), "package b in scope");
        assertEquals(b_B, engine.getFactory().getClasses().get("b.B"), "class b.B in scope");
        assertEquals(b_B_b, engine.getFactory().getFeatures().get("b.B.b"), "feature b.B.b in scope");
        assertEquals(c, engine.getFactory().getPackages().get("c"), "package c in scope");
        assertEquals(c_C, engine.getFactory().getClasses().get("c.C"), "class c.C in scope");
        assertEquals(c_C_c, engine.getFactory().getFeatures().get("c.C.c"), "feature c.C.c in scope");
    }
}
