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

public class TestTransitiveClosure {
    private final NodeFactory factory = new NodeFactory();

    private final RegularExpressionSelectionCriteria startCriteria = new RegularExpressionSelectionCriteria();
    private final RegularExpressionSelectionCriteria stopCriteria = new RegularExpressionSelectionCriteria();

    private final TransitiveClosure selector = new TransitiveClosure(startCriteria, stopCriteria);

    @BeforeEach
    void setUp() {
        var a_A_a = factory.createFeature("a.A.a");
        var b_B_b = factory.createFeature("b.B.b");
        var c_C_c = factory.createFeature("c.C.c");

        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);
    }
    
    @Test
    void testZeroOutbound() {
        startCriteria.setGlobalIncludes("/a.A.a/");
        stopCriteria.setGlobalIncludes("/c.C.c/");

        selector.setMaximumInboundDepth(TransitiveClosure.DO_NOT_FOLLOW);
        selector.setMaximumOutboundDepth(0);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals(1, selector.getFactory().getPackages().size(), "packages");
        assertEquals(1, selector.getFactory().getClasses().size(), "classes");
        assertEquals(1, selector.getFactory().getFeatures().size(), "features");
    }

    @Test
    void testOneOutbound() {
        startCriteria.setGlobalIncludes("/a.A.a/");
        stopCriteria.setGlobalIncludes("/c.C.c/");

        selector.setMaximumInboundDepth(TransitiveClosure.DO_NOT_FOLLOW);
        selector.setMaximumOutboundDepth(1);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals(2, selector.getFactory().getPackages().size(), "packages");
        assertEquals(2, selector.getFactory().getClasses().size(), "classes");
        assertEquals(2, selector.getFactory().getFeatures().size(), "features");
    }

    @Test
    void testAllOutbound() {
        startCriteria.setGlobalIncludes("/a.A.a/");
        stopCriteria.setGlobalIncludes("/c.C.c/");

        selector.setMaximumInboundDepth(TransitiveClosure.DO_NOT_FOLLOW);
        selector.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals(factory.getPackages().size(), selector.getFactory().getPackages().size(), "packages");
        assertEquals(factory.getClasses().size(), selector.getFactory().getClasses().size(), "classes");
        assertEquals(factory.getFeatures().size(), selector.getFactory().getFeatures().size(), "features");
    }

    @Test
    void testZeroInbound() {
        startCriteria.setGlobalIncludes("/c.C.c/");
        stopCriteria.setGlobalIncludes("/a.A.a/");

        selector.setMaximumInboundDepth(0);
        selector.setMaximumOutboundDepth(TransitiveClosure.DO_NOT_FOLLOW);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals(1, selector.getFactory().getPackages().size(), "packages");
        assertEquals(1, selector.getFactory().getClasses().size(), "classes");
        assertEquals(1, selector.getFactory().getFeatures().size(), "features");
    }

    @Test
    void testOneInbound() {
        startCriteria.setGlobalIncludes("/c.C.c/");
        stopCriteria.setGlobalIncludes("/a.A.a/");

        selector.setMaximumInboundDepth(1);
        selector.setMaximumOutboundDepth(TransitiveClosure.DO_NOT_FOLLOW);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals(2, selector.getFactory().getPackages().size(), "packages");
        assertEquals(2, selector.getFactory().getClasses().size(), "classes");
        assertEquals(2, selector.getFactory().getFeatures().size(), "features");
    }

    @Test
    void testAllInbound() {
        startCriteria.setGlobalIncludes("/c.C.c/");
        stopCriteria.setGlobalIncludes("/a.A.a/");

        selector.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        selector.setMaximumOutboundDepth(TransitiveClosure.DO_NOT_FOLLOW);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals(factory.getPackages().size(), selector.getFactory().getPackages().size(), "packages");
        assertEquals(factory.getClasses().size(), selector.getFactory().getClasses().size(), "classes");
        assertEquals(factory.getFeatures().size(), selector.getFactory().getFeatures().size(), "features");
    }

    @Test
    void testZeroBothDirections() {
        startCriteria.setGlobalIncludes("/b.B.b/");
        stopCriteria.setGlobalIncludes(Collections.emptyList());

        selector.setMaximumInboundDepth(0);
        selector.setMaximumOutboundDepth(0);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals(1, selector.getFactory().getPackages().size(), "packages");
        assertEquals(1, selector.getFactory().getClasses().size(), "classes");
        assertEquals(1, selector.getFactory().getFeatures().size(), "features");
    }

    @Test
    void testOneBothDirections() {
        startCriteria.setGlobalIncludes("/b.B.b/");
        stopCriteria.setGlobalIncludes(Collections.emptyList());

        selector.setMaximumInboundDepth(1);
        selector.setMaximumOutboundDepth(1);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals(factory.getPackages().size(), selector.getFactory().getPackages().size(), "packages");
        assertEquals(factory.getClasses().size(), selector.getFactory().getClasses().size(), "classes");
        assertEquals(factory.getFeatures().size(), selector.getFactory().getFeatures().size(), "features");
    }

    @Test
    void testAllBothDirections() {
        startCriteria.setGlobalIncludes("/b.B.b/");
        stopCriteria.setGlobalIncludes(Collections.emptyList());

        selector.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        selector.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        
        selector.traverseNodes(factory.getPackages().values());

        assertEquals(factory.getPackages().size(), selector.getFactory().getPackages().size(), "packages");
        assertEquals(factory.getClasses().size(), selector.getFactory().getClasses().size(), "classes");
        assertEquals(factory.getFeatures().size(), selector.getFactory().getFeatures().size(), "features");
    }
}
