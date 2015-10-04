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

public class TestTransitiveClosure extends TestCase {
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

    private TransitiveClosure selector;

    protected void setUp() throws Exception {
        super.setUp();

        Logger.getLogger(getClass()).debug("Begin " + getName());
        
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
        
        selector = new TransitiveClosure(startCriteria, stopCriteria);

        Logger.getLogger(getClass()).debug("Setup " + getName());
    }
    
    protected void tearDown() throws Exception {
        Logger.getLogger(getClass()).debug("Tear down " + getName());

        super.tearDown();
    }

    public void testZeroOutbound() {
        startCriteria.setGlobalIncludes("/a.A.a/");
        stopCriteria.setGlobalIncludes("/c.C.c/");

        selector.setMaximumInboundDepth(TransitiveClosure.DO_NOT_FOLLOW);
        selector.setMaximumOutboundDepth(0);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals("packages", 1, selector.getFactory().getPackages().size());
        assertEquals("classes",  1, selector.getFactory().getClasses().size());
        assertEquals("features", 1, selector.getFactory().getFeatures().size());
    }

    public void testOneOutbound() {
        startCriteria.setGlobalIncludes("/a.A.a/");
        stopCriteria.setGlobalIncludes("/c.C.c/");

        selector.setMaximumInboundDepth(TransitiveClosure.DO_NOT_FOLLOW);
        selector.setMaximumOutboundDepth(1);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals("packages", 2, selector.getFactory().getPackages().size());
        assertEquals("classes",  2, selector.getFactory().getClasses().size());
        assertEquals("features", 2, selector.getFactory().getFeatures().size());
    }

    public void testAllOutbound() {
        startCriteria.setGlobalIncludes("/a.A.a/");
        stopCriteria.setGlobalIncludes("/c.C.c/");

        selector.setMaximumInboundDepth(TransitiveClosure.DO_NOT_FOLLOW);
        selector.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals("packages", factory.getPackages().size(), selector.getFactory().getPackages().size());
        assertEquals("classes",  factory.getClasses().size(),  selector.getFactory().getClasses().size());
        assertEquals("features", factory.getFeatures().size(), selector.getFactory().getFeatures().size());
    }

    public void testZeroInbound() {
        startCriteria.setGlobalIncludes("/c.C.c/");
        stopCriteria.setGlobalIncludes("/a.A.a/");

        selector.setMaximumInboundDepth(0);
        selector.setMaximumOutboundDepth(TransitiveClosure.DO_NOT_FOLLOW);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals("packages", 1, selector.getFactory().getPackages().size());
        assertEquals("classes",  1, selector.getFactory().getClasses().size());
        assertEquals("features", 1, selector.getFactory().getFeatures().size());
    }

    public void testOneInbound() {
        startCriteria.setGlobalIncludes("/c.C.c/");
        stopCriteria.setGlobalIncludes("/a.A.a/");

        selector.setMaximumInboundDepth(1);
        selector.setMaximumOutboundDepth(TransitiveClosure.DO_NOT_FOLLOW);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals("packages", 2, selector.getFactory().getPackages().size());
        assertEquals("classes",  2, selector.getFactory().getClasses().size());
        assertEquals("features", 2, selector.getFactory().getFeatures().size());
    }

    public void testAllInbound() {
        startCriteria.setGlobalIncludes("/c.C.c/");
        stopCriteria.setGlobalIncludes("/a.A.a/");

        selector.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        selector.setMaximumOutboundDepth(TransitiveClosure.DO_NOT_FOLLOW);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals("packages", factory.getPackages().size(), selector.getFactory().getPackages().size());
        assertEquals("classes",  factory.getClasses().size(),  selector.getFactory().getClasses().size());
        assertEquals("features", factory.getFeatures().size(), selector.getFactory().getFeatures().size());
    }

    public void testZeroBothDirections() {
        startCriteria.setGlobalIncludes("/b.B.b/");
        stopCriteria.setGlobalIncludes(Collections.<String>emptyList());

        selector.setMaximumInboundDepth(0);
        selector.setMaximumOutboundDepth(0);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals("packages", 1, selector.getFactory().getPackages().size());
        assertEquals("classes",  1, selector.getFactory().getClasses().size());
        assertEquals("features", 1, selector.getFactory().getFeatures().size());
    }

    public void testOneBothDirections() {
        startCriteria.setGlobalIncludes("/b.B.b/");
        stopCriteria.setGlobalIncludes(Collections.<String>emptyList());

        selector.setMaximumInboundDepth(1);
        selector.setMaximumOutboundDepth(1);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals("packages", factory.getPackages().size(), selector.getFactory().getPackages().size());
        assertEquals("classes",  factory.getClasses().size(),  selector.getFactory().getClasses().size());
        assertEquals("features", factory.getFeatures().size(), selector.getFactory().getFeatures().size());
    }

    public void testAllBothDirections() {
        startCriteria.setGlobalIncludes("/b.B.b/");
        stopCriteria.setGlobalIncludes(Collections.<String>emptyList());

        selector.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        selector.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals("packages", factory.getPackages().size(), selector.getFactory().getPackages().size());
        assertEquals("classes",  factory.getClasses().size(),  selector.getFactory().getClasses().size());
        assertEquals("features", factory.getFeatures().size(), selector.getFactory().getFeatures().size());
    }
}
