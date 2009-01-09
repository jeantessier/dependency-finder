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

public class TestTransitiveClosureEngine extends TestCase {
    private NodeFactory factory;

    private PackageNode a;
    private ClassNode   a_A;
    private FeatureNode a_A_a;
    
    private PackageNode b;
    private ClassNode   b_B;
    private FeatureNode b_B_b;
    
    private PackageNode c;
    private ClassNode   c_C;
    private FeatureNode c_C_c;

    private RegularExpressionSelectionCriteria startCriteria;
    private RegularExpressionSelectionCriteria stopCriteria;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        factory = new NodeFactory();

        a     = factory.createPackage("a");
        a_A   = factory.createClass("a.A");
        a_A_a = factory.createFeature("a.A.a");
        
        b     = factory.createPackage("b");
        b_B   = factory.createClass("b.B");
        b_B_b = factory.createFeature("b.B.b");
        
        c     = factory.createPackage("c");
        c_C   = factory.createClass("c.C");
        c_C_c = factory.createFeature("c.C.c");

        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);

        startCriteria = new RegularExpressionSelectionCriteria();
        stopCriteria  = new RegularExpressionSelectionCriteria();
        stopCriteria.setGlobalIncludes("");
    }

    public void testSelectScope() {
        startCriteria.setGlobalIncludes("/a.A.a/");

        GraphCopier copier = new GraphCopier(new SelectiveTraversalStrategy(startCriteria, new RegularExpressionSelectionCriteria()));

        copier.traverseNodes(factory.getPackages().values());
        
        assertEquals("packages in scope: " , 1, copier.getScopeFactory().getPackages().values().size());
        assertEquals("classes in scope"    , 1, copier.getScopeFactory().getClasses().values().size());
        assertEquals("features in scope"   , 1, copier.getScopeFactory().getFeatures().values().size());

        assertEquals("package b in scope"    , a,     copier.getScopeFactory().getPackages().get("a"));
        assertEquals("class a.A in scope"    , a_A,   copier.getScopeFactory().getClasses().get("a.A"));
        assertEquals("feature a.A.a in scope", a_A_a, copier.getScopeFactory().getFeatures().get("a.A.a"));
    }

    public void testOutboundStartingPoint() {
        startCriteria.setGlobalIncludes("/a.A.a/");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureOutboundSelector());

        assertEquals("Nb layers", 1, engine.getNbLayers());

        assertEquals("layer 0", 1, engine.getLayer(0).size());
        assertEquals("a.A.a in layer 0", a_A_a, engine.getLayer(0).iterator().next());
        assertNotSame("a.A.a in layer 0", a_A_a, engine.getLayer(0).iterator().next());

        assertEquals("Nb outbounds from a.A.a", 0, ((Node) engine.getLayer(0).iterator().next()).getOutboundDependencies().size());
        
        assertEquals("packages in scope: ", 1, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   1, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   1, engine.getFactory().getFeatures().values().size());

        assertEquals("package a in scope",     a,     engine.getFactory().getPackages().get("a"));
        assertEquals("class a.A in scope",     a_A,   engine.getFactory().getClasses().get("a.A"));
        assertEquals("feature a.A.a in scope", a_A_a, engine.getFactory().getFeatures().get("a.A.a"));
    }

    public void testOneOutboundLayer() {
        startCriteria.setGlobalIncludes("/a.A.a/");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureOutboundSelector());
        engine.computeNextLayer();

        assertEquals("Nb layers", 2, engine.getNbLayers());

        assertEquals("layer 1", 1, engine.getLayer(1).size());
        assertEquals("b.B.b in layer 1", b_B_b, engine.getLayer(1).iterator().next());
        assertNotSame("b.B.b in layer 1", b_B_b, engine.getLayer(1).iterator().next());

        assertEquals("Nb outbounds from a.A.a", a_A_a.getOutboundDependencies().size(), ((Node) engine.getLayer(0).iterator().next()).getOutboundDependencies().size());
        assertEquals("Nb outbounds from b.B.b", 0,                       ((Node) engine.getLayer(1).iterator().next()).getOutboundDependencies().size());
        
        assertEquals("packages in scope: ", 2, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   2, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   2, engine.getFactory().getFeatures().values().size());

        assertEquals("package a in scope",     a,     engine.getFactory().getPackages().get("a"));
        assertEquals("class a.A in scope",     a_A,   engine.getFactory().getClasses().get("a.A"));
        assertEquals("feature a.A.a in scope", a_A_a, engine.getFactory().getFeatures().get("a.A.a"));
        assertEquals("package b in scope",     b,     engine.getFactory().getPackages().get("b"));
        assertEquals("class b.B in scope",     b_B,   engine.getFactory().getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, engine.getFactory().getFeatures().get("b.B.b"));
    }

    public void testTwoOutboundLayers() {
        startCriteria.setGlobalIncludes("/a.A.a/");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureOutboundSelector());
        engine.computeNextLayer();
        engine.computeNextLayer();

        assertEquals("Nb layers", 3, engine.getNbLayers());

        assertEquals("layer 2", 1, engine.getLayer(1).size());
        assertEquals("c.C.c in layer 2", c_C_c, engine.getLayer(2).iterator().next());
        assertNotSame("c.C.c in layer 2", c_C_c, engine.getLayer(2).iterator().next());

        assertEquals("Nb outbounds from a.A.a", a_A_a.getOutboundDependencies().size(), ((Node) engine.getLayer(0).iterator().next()).getOutboundDependencies().size());
        assertEquals("Nb outbounds from b.B.b", b_B_b.getOutboundDependencies().size(), ((Node) engine.getLayer(1).iterator().next()).getOutboundDependencies().size());
        assertEquals("Nb outbounds from c.C.c", 0,                       ((Node) engine.getLayer(2).iterator().next()).getOutboundDependencies().size());
        
        assertEquals("packages in scope: ", 3, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   3, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   3, engine.getFactory().getFeatures().values().size());

        assertEquals("package a in scope",     a,     engine.getFactory().getPackages().get("a"));
        assertEquals("class a.A in scope",     a_A,   engine.getFactory().getClasses().get("a.A"));
        assertEquals("feature a.A.a in scope", a_A_a, engine.getFactory().getFeatures().get("a.A.a"));
        assertEquals("package b in scope",     b,     engine.getFactory().getPackages().get("b"));
        assertEquals("class b.B in scope",     b_B,   engine.getFactory().getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, engine.getFactory().getFeatures().get("b.B.b"));
        assertEquals("package c in scope",     c,     engine.getFactory().getPackages().get("c"));
        assertEquals("class c.C in scope",     c_C,   engine.getFactory().getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, engine.getFactory().getFeatures().get("c.C.c"));
    }

    public void testThreeOutboundLayers() {
        startCriteria.setGlobalIncludes("/a.A.a/");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureOutboundSelector());
        engine.computeNextLayer();
        engine.computeNextLayer();
        engine.computeNextLayer();

        assertEquals("Nb layers", 3, engine.getNbLayers());

        assertEquals("Nb outbounds from a.A.a", a_A_a.getOutboundDependencies().size(), ((Node) engine.getLayer(0).iterator().next()).getOutboundDependencies().size());
        assertEquals("Nb outbounds from b.B.b", b_B_b.getOutboundDependencies().size(), ((Node) engine.getLayer(1).iterator().next()).getOutboundDependencies().size());
        assertEquals("Nb outbounds from c.C.c", c_C_c.getOutboundDependencies().size(), ((Node) engine.getLayer(2).iterator().next()).getOutboundDependencies().size());
        
        assertEquals("packages in scope: ", 3, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   3, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   3, engine.getFactory().getFeatures().values().size());

        assertEquals("package a in scope",     a,     engine.getFactory().getPackages().get("a"));
        assertEquals("class a.A in scope",     a_A,   engine.getFactory().getClasses().get("a.A"));
        assertEquals("feature a.A.a in scope", a_A_a, engine.getFactory().getFeatures().get("a.A.a"));
        assertEquals("package b in scope",     b,     engine.getFactory().getPackages().get("b"));
        assertEquals("class b.B in scope",     b_B,   engine.getFactory().getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, engine.getFactory().getFeatures().get("b.B.b"));
        assertEquals("package c in scope",     c,     engine.getFactory().getPackages().get("c"));
        assertEquals("class c.C in scope",     c_C,   engine.getFactory().getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, engine.getFactory().getFeatures().get("c.C.c"));
    }

    public void testFourOutboundLayers() {
        startCriteria.setGlobalIncludes("/a.A.a/");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureOutboundSelector());
        engine.computeNextLayer();
        engine.computeNextLayer();
        engine.computeNextLayer();
        engine.computeNextLayer();

        assertEquals("Nb layers", 3, engine.getNbLayers());

        assertEquals("Nb outbounds from a.A.a", a_A_a.getOutboundDependencies().size(), ((Node) engine.getLayer(0).iterator().next()).getOutboundDependencies().size());
        assertEquals("Nb outbounds from b.B.b", b_B_b.getOutboundDependencies().size(), ((Node) engine.getLayer(1).iterator().next()).getOutboundDependencies().size());
        assertEquals("Nb outbounds from c.C.c", c_C_c.getOutboundDependencies().size(), ((Node) engine.getLayer(2).iterator().next()).getOutboundDependencies().size());
        
        assertEquals("packages in scope: ", 3, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   3, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   3, engine.getFactory().getFeatures().values().size());

        assertEquals("package a in scope",     a,     engine.getFactory().getPackages().get("a"));
        assertEquals("class a.A in scope",     a_A,   engine.getFactory().getClasses().get("a.A"));
        assertEquals("feature a.A.a in scope", a_A_a, engine.getFactory().getFeatures().get("a.A.a"));
        assertEquals("package b in scope",     b,     engine.getFactory().getPackages().get("b"));
        assertEquals("class b.B in scope",     b_B,   engine.getFactory().getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, engine.getFactory().getFeatures().get("b.B.b"));
        assertEquals("package c in scope",     c,     engine.getFactory().getPackages().get("c"));
        assertEquals("class c.C in scope",     c_C,   engine.getFactory().getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, engine.getFactory().getFeatures().get("c.C.c"));
    }

    public void testInboundStartingPoint() {
        startCriteria.setGlobalIncludes("/c.C.c/");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());

        assertEquals("Nb layers", 1, engine.getNbLayers());

        assertEquals("layer 0", 1, engine.getLayer(0).size());
        assertEquals("c.C.c in layer 0", c_C_c, engine.getLayer(0).iterator().next());
        assertNotSame("c.C.c in layer 0", c_C_c, engine.getLayer(0).iterator().next());

        assertEquals("Nb inbounds from c.C.c", 0, ((Node) engine.getLayer(0).iterator().next()).getInboundDependencies().size());
        
        assertEquals("packages in scope: ", 1, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   1, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   1, engine.getFactory().getFeatures().values().size());

        assertEquals("package c in scope",     c,     engine.getFactory().getPackages().get("c"));
        assertEquals("class c.C in scope",     c_C,   engine.getFactory().getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, engine.getFactory().getFeatures().get("c.C.c"));
    }

    public void testOneInboundLayer() {
        startCriteria.setGlobalIncludes("/c.C.c/");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeNextLayer();

        assertEquals("Nb layers", 2, engine.getNbLayers());

        assertEquals("layer 1", 1, engine.getLayer(1).size());
        assertEquals("b.B.b in layer 1", b_B_b, engine.getLayer(1).iterator().next());
        assertNotSame("b.B.b in layer 1", b_B_b, engine.getLayer(1).iterator().next());

        assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.getLayer(0).iterator().next()).getInboundDependencies().size());
        assertEquals("Nb inbounds from b.B.b", 0,                      ((Node) engine.getLayer(1).iterator().next()).getInboundDependencies().size());
        
        assertEquals("packages in scope: ", 2, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   2, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   2, engine.getFactory().getFeatures().values().size());

        assertEquals("package b in scope",     b,     engine.getFactory().getPackages().get("b"));
        assertEquals("class b.B in scope",     b_B,   engine.getFactory().getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, engine.getFactory().getFeatures().get("b.B.b"));
        assertEquals("package c in scope",     c,     engine.getFactory().getPackages().get("c"));
        assertEquals("class c.C in scope",     c_C,   engine.getFactory().getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, engine.getFactory().getFeatures().get("c.C.c"));
    }

    public void testTwoInboundLayers() {
        startCriteria.setGlobalIncludes("/c.C.c/");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeNextLayer();
        engine.computeNextLayer();

        assertEquals("Nb layers", 3, engine.getNbLayers());

        assertEquals("layer 2", 1, engine.getLayer(1).size());
        assertEquals("a.A.a in layer 2", a_A_a, engine.getLayer(2).iterator().next());
        assertNotSame("a.A.a in layer 2", a_A_a, engine.getLayer(2).iterator().next());

        assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.getLayer(0).iterator().next()).getInboundDependencies().size());
        assertEquals("Nb inbounds from b.B.b", b_B_b.getInboundDependencies().size(), ((Node) engine.getLayer(1).iterator().next()).getInboundDependencies().size());
        assertEquals("Nb inbounds from a.A.a", 0,                      ((Node) engine.getLayer(2).iterator().next()).getInboundDependencies().size());
        
        assertEquals("packages in scope: ", 3, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   3, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   3, engine.getFactory().getFeatures().values().size());

        assertEquals("package a in scope",     a,     engine.getFactory().getPackages().get("a"));
        assertEquals("class a.A in scope",     a_A,   engine.getFactory().getClasses().get("a.A"));
        assertEquals("feature a.A.a in scope", a_A_a, engine.getFactory().getFeatures().get("a.A.a"));
        assertEquals("package b in scope",     b,     engine.getFactory().getPackages().get("b"));
        assertEquals("class b.B in scope",     b_B,   engine.getFactory().getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, engine.getFactory().getFeatures().get("b.B.b"));
        assertEquals("package c in scope",     c,     engine.getFactory().getPackages().get("c"));
        assertEquals("class c.C in scope",     c_C,   engine.getFactory().getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, engine.getFactory().getFeatures().get("c.C.c"));
    }

    public void testThreeInboundLayers() {
        startCriteria.setGlobalIncludes("/c.C.c/");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeNextLayer();
        engine.computeNextLayer();
        engine.computeNextLayer();

        assertEquals("Nb layers", 3, engine.getNbLayers());

        assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.getLayer(0).iterator().next()).getInboundDependencies().size());
        assertEquals("Nb inbounds from b.B.b", b_B_b.getInboundDependencies().size(), ((Node) engine.getLayer(1).iterator().next()).getInboundDependencies().size());
        assertEquals("Nb inbounds from a.A.a", a_A_a.getInboundDependencies().size(), ((Node) engine.getLayer(2).iterator().next()).getInboundDependencies().size());
        
        assertEquals("packages in scope: ", 3, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   3, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   3, engine.getFactory().getFeatures().values().size());

        assertEquals("package a in scope",     a,     engine.getFactory().getPackages().get("a"));
        assertEquals("class a.A in scope",     a_A,   engine.getFactory().getClasses().get("a.A"));
        assertEquals("feature a.A.a in scope", a_A_a, engine.getFactory().getFeatures().get("a.A.a"));
        assertEquals("package b in scope",     b,     engine.getFactory().getPackages().get("b"));
        assertEquals("class b.B in scope",     b_B,   engine.getFactory().getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, engine.getFactory().getFeatures().get("b.B.b"));
        assertEquals("package c in scope",     c,     engine.getFactory().getPackages().get("c"));
        assertEquals("class c.C in scope",     c_C,   engine.getFactory().getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, engine.getFactory().getFeatures().get("c.C.c"));
    }

    public void testFourInboundLayers() {
        startCriteria.setGlobalIncludes("/c.C.c/");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeNextLayer();
        engine.computeNextLayer();
        engine.computeNextLayer();
        engine.computeNextLayer();

        assertEquals("Nb layers", 3, engine.getNbLayers());

        assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.getLayer(0).iterator().next()).getInboundDependencies().size());
        assertEquals("Nb inbounds from b.B.b", b_B_b.getInboundDependencies().size(), ((Node) engine.getLayer(1).iterator().next()).getInboundDependencies().size());
        assertEquals("Nb inbounds from a.A.a", a_A_a.getInboundDependencies().size(), ((Node) engine.getLayer(2).iterator().next()).getInboundDependencies().size());
        
        assertEquals("packages in scope: ", 3, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   3, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   3, engine.getFactory().getFeatures().values().size());

        assertEquals("package a in scope",     a,     engine.getFactory().getPackages().get("a"));
        assertEquals("class a.A in scope",     a_A,   engine.getFactory().getClasses().get("a.A"));
        assertEquals("feature a.A.a in scope", a_A_a, engine.getFactory().getFeatures().get("a.A.a"));
        assertEquals("package b in scope",     b,     engine.getFactory().getPackages().get("b"));
        assertEquals("class b.B in scope",     b_B,   engine.getFactory().getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, engine.getFactory().getFeatures().get("b.B.b"));
        assertEquals("package c in scope",     c,     engine.getFactory().getPackages().get("c"));
        assertEquals("class c.C in scope",     c_C,   engine.getFactory().getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, engine.getFactory().getFeatures().get("c.C.c"));
    }

    public void testStopCriteria() {
        startCriteria.setGlobalIncludes("/c.C.c/");
        stopCriteria.setGlobalIncludes("/b.B.b/");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeNextLayer();
        engine.computeNextLayer();
        engine.computeNextLayer();
        engine.computeNextLayer();

        assertEquals("Nb layers", 2, engine.getNbLayers());

        assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.getLayer(0).iterator().next()).getInboundDependencies().size());
        assertEquals("Nb inbounds from b.B.b", 0,                      ((Node) engine.getLayer(1).iterator().next()).getInboundDependencies().size());
        
        assertEquals("packages in scope: ", 2, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   2, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   2, engine.getFactory().getFeatures().values().size());

        assertEquals("package b in scope",     b,     engine.getFactory().getPackages().get("b"));
        assertEquals("class b.B in scope",     b_B,   engine.getFactory().getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, engine.getFactory().getFeatures().get("b.B.b"));
        assertEquals("package c in scope",     c,     engine.getFactory().getPackages().get("c"));
        assertEquals("class c.C in scope",     c_C,   engine.getFactory().getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, engine.getFactory().getFeatures().get("c.C.c"));
    }

    public void testComputeAllLayers() {
        startCriteria.setGlobalIncludes("/c.C.c/");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeAllLayers();

        assertEquals("Nb layers", 3, engine.getNbLayers());

        assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.getLayer(0).iterator().next()).getInboundDependencies().size());
        assertEquals("Nb inbounds from b.B.b", b_B_b.getInboundDependencies().size(), ((Node) engine.getLayer(1).iterator().next()).getInboundDependencies().size());
        assertEquals("Nb inbounds from a.A.a", a_A_a.getInboundDependencies().size(), ((Node) engine.getLayer(2).iterator().next()).getInboundDependencies().size());
        
        assertEquals("packages in scope: ", 3, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   3, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   3, engine.getFactory().getFeatures().values().size());

        assertEquals("package a in scope",     a,     engine.getFactory().getPackages().get("a"));
        assertEquals("class a.A in scope",     a_A,   engine.getFactory().getClasses().get("a.A"));
        assertEquals("feature a.A.a in scope", a_A_a, engine.getFactory().getFeatures().get("a.A.a"));
        assertEquals("package b in scope",     b,     engine.getFactory().getPackages().get("b"));
        assertEquals("class b.B in scope",     b_B,   engine.getFactory().getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, engine.getFactory().getFeatures().get("b.B.b"));
        assertEquals("package c in scope",     c,     engine.getFactory().getPackages().get("c"));
        assertEquals("class c.C in scope",     c_C,   engine.getFactory().getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, engine.getFactory().getFeatures().get("c.C.c"));
    }

    public void testComputeAllLayersWithStopCriteria() {
        startCriteria.setGlobalIncludes("/c.C.c/");
        stopCriteria.setGlobalIncludes("/b.B.b/");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeAllLayers();

        assertEquals("Nb layers", 2, engine.getNbLayers());

        assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.getLayer(0).iterator().next()).getInboundDependencies().size());
        assertEquals("Nb inbounds from b.B.b", 0,                      ((Node) engine.getLayer(1).iterator().next()).getInboundDependencies().size());
        
        assertEquals("packages in scope: ", 2, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   2, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   2, engine.getFactory().getFeatures().values().size());

        assertEquals("package b in scope",     b,     engine.getFactory().getPackages().get("b"));
        assertEquals("class b.B in scope",     b_B,   engine.getFactory().getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, engine.getFactory().getFeatures().get("b.B.b"));
        assertEquals("package c in scope",     c,     engine.getFactory().getPackages().get("c"));
        assertEquals("class c.C in scope",     c_C,   engine.getFactory().getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, engine.getFactory().getFeatures().get("c.C.c"));
    }

    public void testComputeAllLayersUntilStartCriteria() {
        startCriteria.setGlobalIncludes("/c.C.c/");
        stopCriteria.setGlobalIncludes("//");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeAllLayers();

        assertEquals("Nb layers", 1, engine.getNbLayers());

        assertEquals("Nb inbounds from c.C.c", 0, ((Node) engine.getLayer(0).iterator().next()).getInboundDependencies().size());
        
        assertEquals("packages in scope: ", 1, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   1, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   1, engine.getFactory().getFeatures().values().size());

        assertEquals("package c in scope",     c,     engine.getFactory().getPackages().get("c"));
        assertEquals("class c.C in scope",     c_C,   engine.getFactory().getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, engine.getFactory().getFeatures().get("c.C.c"));
    }

    public void testCompute1LayerOnly() {
        startCriteria.setGlobalIncludes("/c.C.c/");
        stopCriteria.setGlobalIncludes("");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeLayers(1);

        assertEquals("Nb layers", 2, engine.getNbLayers());

        assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.getLayer(0).iterator().next()).getInboundDependencies().size());
        assertEquals("Nb inbounds from b.B.b", 0,                      ((Node) engine.getLayer(1).iterator().next()).getInboundDependencies().size());
        
        assertEquals("packages in scope: ", 2, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   2, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   2, engine.getFactory().getFeatures().values().size());

        assertEquals("package b in scope",     b,     engine.getFactory().getPackages().get("b"));
        assertEquals("class b.B in scope",     b_B,   engine.getFactory().getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, engine.getFactory().getFeatures().get("b.B.b"));
        assertEquals("package c in scope",     c,     engine.getFactory().getPackages().get("c"));
        assertEquals("class c.C in scope",     c_C,   engine.getFactory().getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, engine.getFactory().getFeatures().get("c.C.c"));
    }

    public void testCompute4LayersWithStopCriteria() {
        startCriteria.setGlobalIncludes("/c.C.c/");
        stopCriteria.setGlobalIncludes("/b.B.b/");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeLayers(4);

        assertEquals("Nb layers", 2, engine.getNbLayers());

        assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), ((Node) engine.getLayer(0).iterator().next()).getInboundDependencies().size());
        assertEquals("Nb inbounds from b.B.b", 0,                      ((Node) engine.getLayer(1).iterator().next()).getInboundDependencies().size());
        
        assertEquals("packages in scope: ", 2, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   2, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   2, engine.getFactory().getFeatures().values().size());

        assertEquals("package b in scope",     b,     engine.getFactory().getPackages().get("b"));
        assertEquals("class b.B in scope",     b_B,   engine.getFactory().getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, engine.getFactory().getFeatures().get("b.B.b"));
        assertEquals("package c in scope",     c,     engine.getFactory().getPackages().get("c"));
        assertEquals("class c.C in scope",     c_C,   engine.getFactory().getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, engine.getFactory().getFeatures().get("c.C.c"));
    }

    public void testComputeAllOutboundLayersP2P() {
        startCriteria.setGlobalIncludes("/^a/");
        stopCriteria.setGlobalIncludes("/^c/");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureOutboundSelector());
        engine.computeAllLayers();

        assertEquals("Nb layers", 3, engine.getNbLayers());

        Node node = null;
        Iterator i = engine.getLayer(0).iterator();
        while (i.hasNext()) {
            Node temp = (Node) i.next();
            if (temp.equals(a_A_a)) {
                node = temp;
            }
        }

        assertEquals("Layer 0 size", 3, engine.getLayer(0).size());
        assertEquals("Layer 0 content", a_A_a.getName(), node.getName());
        assertEquals("Nb outbounds from a.A.a", a_A_a.getOutboundDependencies().size(), node.getOutboundDependencies().size());
        assertEquals("Layer 1 size", 1, engine.getLayer(1).size());
        assertEquals("Layer 1 content", b_B_b.getName(), ((Node) engine.getLayer(1).iterator().next()).getName());
        assertEquals("Nb outbounds from b.B.b", b_B_b.getOutboundDependencies().size(), ((Node) engine.getLayer(1).iterator().next()).getOutboundDependencies().size());
        assertEquals("Layer 2 size", 1, engine.getLayer(2).size());
        assertEquals("Layer 2 content", c_C_c.getName(), ((Node) engine.getLayer(2).iterator().next()).getName());
        assertEquals("Nb outbounds from c.C.c", c_C_c.getOutboundDependencies().size(), ((Node) engine.getLayer(2).iterator().next()).getOutboundDependencies().size());
        
        assertEquals("packages in scope: ", 3, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   3, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   3, engine.getFactory().getFeatures().values().size());

        assertEquals("package a in scope",     a,     engine.getFactory().getPackages().get("a"));
        assertEquals("class a.A in scope",     a_A,   engine.getFactory().getClasses().get("a.A"));
        assertEquals("feature a.A.a in scope", a_A_a, engine.getFactory().getFeatures().get("a.A.a"));
        assertEquals("package b in scope",     b,     engine.getFactory().getPackages().get("b"));
        assertEquals("class b.B in scope",     b_B,   engine.getFactory().getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, engine.getFactory().getFeatures().get("b.B.b"));
        assertEquals("package c in scope",     c,     engine.getFactory().getPackages().get("c"));
        assertEquals("class c.C in scope",     c_C,   engine.getFactory().getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, engine.getFactory().getFeatures().get("c.C.c"));
    }

    public void testComputeAllInboundLayersP2P() {
        startCriteria.setGlobalIncludes("/^c/");
        stopCriteria.setGlobalIncludes("/^a/");

        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory.getPackages().values(), startCriteria, stopCriteria, new ClosureInboundSelector());
        engine.computeAllLayers();

        assertEquals("Nb layers", 3, engine.getNbLayers());

        Node node = null;
        Iterator i = engine.getLayer(0).iterator();
        while (i.hasNext()) {
            Node temp = (Node) i.next();
            if (temp.equals(c_C_c)) {
                node = temp;
            }
        }

        assertEquals("Layer 0 size", 3, engine.getLayer(0).size());
        assertEquals("Layer 0 content", c_C_c.getName(), node.getName());
        assertEquals("Nb inbounds from c.C.c", c_C_c.getInboundDependencies().size(), node.getInboundDependencies().size());
        assertEquals("Layer 1 size", 1, engine.getLayer(1).size());
        assertEquals("Layer 1 content", b_B_b.getName(), ((Node) engine.getLayer(1).iterator().next()).getName());
        assertEquals("Nb inbounds from b.B.b", b_B_b.getInboundDependencies().size(), ((Node) engine.getLayer(1).iterator().next()).getInboundDependencies().size());
        assertEquals("Layer 2 size", 1, engine.getLayer(1).size());
        assertEquals("Layer 2 content", a_A_a.getName(), ((Node) engine.getLayer(2).iterator().next()).getName());
        assertEquals("Nb inbounds from a.A.a", a_A_a.getInboundDependencies().size(), ((Node) engine.getLayer(2).iterator().next()).getInboundDependencies().size());
        
        assertEquals("packages in scope: ", 3, engine.getFactory().getPackages().values().size());
        assertEquals("classes in scope" ,   3, engine.getFactory().getClasses().values().size());
        assertEquals("features in scope",   3, engine.getFactory().getFeatures().values().size());

        assertEquals("package a in scope",     a,     engine.getFactory().getPackages().get("a"));
        assertEquals("class a.A in scope",     a_A,   engine.getFactory().getClasses().get("a.A"));
        assertEquals("feature a.A.a in scope", a_A_a, engine.getFactory().getFeatures().get("a.A.a"));
        assertEquals("package b in scope",     b,     engine.getFactory().getPackages().get("b"));
        assertEquals("class b.B in scope",     b_B,   engine.getFactory().getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, engine.getFactory().getFeatures().get("b.B.b"));
        assertEquals("package c in scope",     c,     engine.getFactory().getPackages().get("c"));
        assertEquals("class c.C in scope",     c_C,   engine.getFactory().getClasses().get("c.C"));
        assertEquals("feature c.C.c in scope", c_C_c, engine.getFactory().getFeatures().get("c.C.c"));
    }
}
