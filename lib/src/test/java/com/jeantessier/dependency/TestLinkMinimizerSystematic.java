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

public class TestLinkMinimizerSystematic {
    private final NodeFactory factory = new NodeFactory();

    private final Node a = factory.createPackage("a");
    private final Node a_A = factory.createClass("a.A");
    private final Node a_A_a = factory.createFeature("a.A.a()");

    private final Node b = factory.createPackage("b");
    private final Node b_B = factory.createClass("b.B");
    private final Node b_B_b = factory.createFeature("b.B.b()");

    private final Visitor visitor = new LinkMinimizer();

    @Test
    void testPackagePackage() {
        a.addDependency(b);

        visitor.traverseNodes(factory.getPackages().values());

        assertEquals(1, a.getOutboundDependencies().size(), "a outbound");
        assertTrue(a.getOutboundDependencies().contains(b), "Missing a --> b");
        assertEquals(0, a.getInboundDependencies().size(), "a inbound");
        assertEquals(0, a_A.getOutboundDependencies().size(), "a_A outbound");
        assertEquals(0, a_A.getInboundDependencies().size(), "a_A inbound");
        assertEquals(0, a_A_a.getOutboundDependencies().size(), "a_A_a outbound");
        assertEquals(0, a_A_a.getInboundDependencies().size(), "a_A_a inbound"); 
        assertEquals(0, b.getOutboundDependencies().size(), "b outbound");
        assertEquals(1, b.getInboundDependencies().size(), "b inbound");
        assertTrue(b.getInboundDependencies().contains(a), "Missing b <-- a");
        assertEquals(0, b_B.getOutboundDependencies().size(), "b_B outbound");
        assertEquals(0, b_B.getInboundDependencies().size(), "b_B inbound");
        assertEquals(0, b_B_b.getOutboundDependencies().size(), "b_B_b outbound");
        assertEquals(0, b_B_b.getInboundDependencies().size(), "b_B_b inbound");
    }

    @Test
    void testPackageClass() {
        a.addDependency(b);
        a.addDependency(b_B);

        visitor.traverseNodes(factory.getPackages().values());

        assertEquals(1, a.getOutboundDependencies().size(), "a outbound");
        assertTrue(a.getOutboundDependencies().contains(b_B), "Missing a --> b.B");
        assertEquals(0, a.getInboundDependencies().size(), "a inbound");
        assertEquals(0, a_A.getOutboundDependencies().size(), "a_A outbound");
        assertEquals(0, a_A.getInboundDependencies().size(), "a_A inbound");
        assertEquals(0, a_A_a.getOutboundDependencies().size(), "a_A_a outbound");
        assertEquals(0, a_A_a.getInboundDependencies().size(), "a_A_a inbound");
        assertEquals(0, b.getOutboundDependencies().size(), "b outbound");
        assertEquals(0, b.getInboundDependencies().size(), "b inbound");
        assertEquals(0, b_B.getOutboundDependencies().size(), "b_B outbound");
        assertEquals(1, b_B.getInboundDependencies().size(), "b_B inbound");
        assertTrue(b_B.getInboundDependencies().contains(a), "Missing b.B <-- a");
        assertEquals(0, b_B_b.getOutboundDependencies().size(), "b_B_b outbound");
        assertEquals(0, b_B_b.getInboundDependencies().size(), "b_B_b inbound");
    }

    @Test
    void testPackageFeature() {
        a.addDependency(b);
        a.addDependency(b_B);
        a.addDependency(b_B_b);

        visitor.traverseNodes(factory.getPackages().values());

        assertEquals(1, a.getOutboundDependencies().size(), "a outbound");
        assertTrue(a.getOutboundDependencies().contains(b_B_b), "Missing a --> b.B.b");
        assertEquals(0, a.getInboundDependencies().size(), "a inbound");
        assertEquals(0, a_A.getOutboundDependencies().size(), "a_A outbound");
        assertEquals(0, a_A.getInboundDependencies().size(), "a_A inbound");
        assertEquals(0, a_A_a.getOutboundDependencies().size(), "a_A_a outbound");
        assertEquals(0, a_A_a.getInboundDependencies().size(), "a_A_a inbound");
        assertEquals(0, b.getOutboundDependencies().size(), "b outbound");
        assertEquals(0, b.getInboundDependencies().size(), "b inbound");
        assertEquals(0, b_B.getOutboundDependencies().size(), "b_B outbound");
        assertEquals(0, b_B.getInboundDependencies().size(), "b_B inbound");
        assertEquals(0, b_B_b.getOutboundDependencies().size(), "b_B_b outbound");
        assertEquals(1, b_B_b.getInboundDependencies().size(), "b_B_b inbound");
        assertTrue(b_B_b.getInboundDependencies().contains(a), "Missing b.B.b <-- a");
    }

    @Test
    void testClassPackage() {
        a.addDependency(b);
        a_A.addDependency(b);

        visitor.traverseNodes(factory.getPackages().values());

        assertEquals(0, a.getOutboundDependencies().size(), "a outbound");
        assertEquals(0, a.getInboundDependencies().size(), "a inbound");
        assertEquals(1, a_A.getOutboundDependencies().size(), "a_A outbound");
        assertTrue(a_A.getOutboundDependencies().contains(b), "Missing a.A --> b");
        assertEquals(0, a_A.getInboundDependencies().size(), "a_A inbound");
        assertEquals(0, a_A_a.getOutboundDependencies().size(), "a_A_a outbound");
        assertEquals(0, a_A_a.getInboundDependencies().size(), "a_A_a inbound"); 
        assertEquals(0, b.getOutboundDependencies().size(), "b outbound");
        assertEquals(1, b.getInboundDependencies().size(), "b inbound");
        assertTrue(b.getInboundDependencies().contains(a_A), "Missing b <-- a.A");
        assertEquals(0, b_B.getOutboundDependencies().size(), "b_B outbound");
        assertEquals(0, b_B.getInboundDependencies().size(), "b_B inbound");
        assertEquals(0, b_B_b.getOutboundDependencies().size(), "b_B_b outbound");
        assertEquals(0, b_B_b.getInboundDependencies().size(), "b_B_b inbound");
    }

    @Test
    void testClassClass() {
        a.addDependency(b);
        a.addDependency(b_B);
        a_A.addDependency(b);
        a_A.addDependency(b_B);

        visitor.traverseNodes(factory.getPackages().values());

        assertEquals(0, a.getOutboundDependencies().size(), "a outbound");
        assertEquals(0, a.getInboundDependencies().size(), "a inbound");
        assertEquals(1, a_A.getOutboundDependencies().size(), "a_A outbound");
        assertTrue(a_A.getOutboundDependencies().contains(b_B), "Missing a.A --> b.B");
        assertEquals(0, a_A.getInboundDependencies().size(), "a_A inbound");
        assertEquals(0, a_A_a.getOutboundDependencies().size(), "a_A_a outbound");
        assertEquals(0, a_A_a.getInboundDependencies().size(), "a_A_a inbound");
        assertEquals(0, b.getOutboundDependencies().size(), "b outbound");
        assertEquals(0, b.getInboundDependencies().size(), "b inbound");
        assertEquals(0, b_B.getOutboundDependencies().size(), "b_B outbound");
        assertEquals(1, b_B.getInboundDependencies().size(), "b_B inbound");
        assertTrue(b_B.getInboundDependencies().contains(a_A), "Missing b.B <-- a.A");
        assertEquals(0, b_B_b.getOutboundDependencies().size(), "b_B_b outbound");
        assertEquals(0, b_B_b.getInboundDependencies().size(), "b_B_b inbound");
    }

    @Test
    void testClassClassSparse() {
        a.addDependency(b);
        a_A.addDependency(b_B);

        visitor.traverseNodes(factory.getPackages().values());

        assertEquals(0, a.getOutboundDependencies().size(), "a outbound");
        assertEquals(0, a.getInboundDependencies().size(), "a inbound");
        assertEquals(1, a_A.getOutboundDependencies().size(), "a_A outbound");
        assertTrue(a_A.getOutboundDependencies().contains(b_B), "Missing a.A --> b.B");
        assertEquals(0, a_A.getInboundDependencies().size(), "a_A inbound");
        assertEquals(0, a_A_a.getOutboundDependencies().size(), "a_A_a outbound");
        assertEquals(0, a_A_a.getInboundDependencies().size(), "a_A_a inbound");
        assertEquals(0, b.getOutboundDependencies().size(), "b outbound");
        assertEquals(0, b.getInboundDependencies().size(), "b inbound");
        assertEquals(0, b_B.getOutboundDependencies().size(), "b_B outbound");
        assertEquals(1, b_B.getInboundDependencies().size(), "b_B inbound");
        assertTrue(b_B.getInboundDependencies().contains(a_A), "Missing b.B <-- a_A");
        assertEquals(0, b_B_b.getOutboundDependencies().size(), "b_B_b outbound");
        assertEquals(0, b_B_b.getInboundDependencies().size(), "b_B_b inbound");
    }

    @Test
    void testClassFeature() {
        a.addDependency(b);
        a.addDependency(b_B);
        a.addDependency(b_B_b);
        a_A.addDependency(b);
        a_A.addDependency(b_B);
        a_A.addDependency(b_B_b);

        visitor.traverseNodes(factory.getPackages().values());

        assertEquals(0, a.getOutboundDependencies().size(), "a outbound");
        assertEquals(0, a.getInboundDependencies().size(), "a inbound");
        assertEquals(1, a_A.getOutboundDependencies().size(), "a_A outbound");
        assertTrue(a_A.getOutboundDependencies().contains(b_B_b), "Missing a.A --> b.B.b");
        assertEquals(0, a_A.getInboundDependencies().size(), "a_A inbound");
        assertEquals(0, a_A_a.getOutboundDependencies().size(), "a_A_a outbound");
        assertEquals(0, a_A_a.getInboundDependencies().size(), "a_A_a inbound");
        assertEquals(0, b.getOutboundDependencies().size(), "b outbound");
        assertEquals(0, b.getInboundDependencies().size(), "b inbound");
        assertEquals(0, b_B.getOutboundDependencies().size(), "b_B outbound");
        assertEquals(0, b_B.getInboundDependencies().size(), "b_B inbound");
        assertEquals(0, b_B_b.getOutboundDependencies().size(), "b_B_b outbound");
        assertEquals(1, b_B_b.getInboundDependencies().size(), "b_B_b inbound");
        assertTrue(b_B_b.getInboundDependencies().contains(a_A), "Missing b.B.b <-- a_A");
    }

    @Test
    void testFeaturePackage() {
        a.addDependency(b);
        a_A.addDependency(b);
        a_A_a.addDependency(b);

        visitor.traverseNodes(factory.getPackages().values());

        assertEquals(0, a.getOutboundDependencies().size(), "a outbound");
        assertEquals(0, a.getInboundDependencies().size(), "a inbound");
        assertEquals(0, a_A.getOutboundDependencies().size(), "a_A outbound");
        assertEquals(0, a_A.getInboundDependencies().size(), "a_A inbound");
        assertEquals(1, a_A_a.getOutboundDependencies().size(), "a_A_a outbound");
        assertTrue(a_A_a.getOutboundDependencies().contains(b), "Missing a.A.a --> b");
        assertEquals(0, a_A_a.getInboundDependencies().size(), "a_A_a inbound"); 
        assertEquals(0, b.getOutboundDependencies().size(), "b outbound");
        assertEquals(1, b.getInboundDependencies().size(), "b inbound");
        assertTrue(b.getInboundDependencies().contains(a_A_a), "Missing b <-- a.A.a");
        assertEquals(0, b_B.getOutboundDependencies().size(), "b_B outbound");
        assertEquals(0, b_B.getInboundDependencies().size(), "b_B inbound");
        assertEquals(0, b_B_b.getOutboundDependencies().size(), "b_B_b outbound");
        assertEquals(0, b_B_b.getInboundDependencies().size(), "b_B_b inbound");
    }
    
    @Test
    void testFeatureClass() {
        a.addDependency(b);
        a.addDependency(b_B);
        a_A.addDependency(b);
        a_A.addDependency(b_B);
        a_A_a.addDependency(b);
        a_A_a.addDependency(b_B);

        visitor.traverseNodes(factory.getPackages().values());

        assertEquals(0, a.getOutboundDependencies().size(), "a outbound");
        assertEquals(0, a.getInboundDependencies().size(), "a inbound");
        assertEquals(0, a_A.getOutboundDependencies().size(), "a_A outbound");
        assertEquals(0, a_A.getInboundDependencies().size(), "a_A inbound");
        assertEquals(1, a_A_a.getOutboundDependencies().size(), "a_A_a outbound");
        assertTrue(a_A_a.getOutboundDependencies().contains(b_B), "Missing a.A.a --> b.B");
        assertEquals(0, a_A_a.getInboundDependencies().size(), "a_A_a inbound");
        assertEquals(0, b.getOutboundDependencies().size(), "b outbound");
        assertEquals(0, b.getInboundDependencies().size(), "b inbound");
        assertEquals(0, b_B.getOutboundDependencies().size(), "b_B outbound");
        assertEquals(1, b_B.getInboundDependencies().size(), "b_B inbound");
        assertTrue(b_B.getInboundDependencies().contains(a_A_a), "Missing b.B <-- a.A.a");
        assertEquals(0, b_B_b.getOutboundDependencies().size(), "b_B_b outbound");
        assertEquals(0, b_B_b.getInboundDependencies().size(), "b_B_b inbound");
    }
    
    @Test
    void testFeatureFeature() {
        a.addDependency(b);
        a.addDependency(b_B);
        a.addDependency(b_B_b);
        a_A.addDependency(b);
        a_A.addDependency(b_B);
        a_A.addDependency(b_B_b);
        a_A_a.addDependency(b);
        a_A_a.addDependency(b_B);
        a_A_a.addDependency(b_B_b);

        visitor.traverseNodes(factory.getPackages().values());

        assertEquals(0, a.getOutboundDependencies().size(), "a outbound");
        assertEquals(0, a.getInboundDependencies().size(), "a inbound");
        assertEquals(0, a_A.getOutboundDependencies().size(), "a_A outbound");
        assertEquals(0, a_A.getInboundDependencies().size(), "a_A inbound");
        assertEquals(1, a_A_a.getOutboundDependencies().size(), "a_A_a outbound");
        assertTrue(a_A_a.getOutboundDependencies().contains(b_B_b), "Missing a.A.a --> b.B.b");
        assertEquals(0, a_A_a.getInboundDependencies().size(), "a_A_a inbound");
        assertEquals(0, b.getOutboundDependencies().size(), "b outbound");
        assertEquals(0, b.getInboundDependencies().size(), "b inbound");
        assertEquals(0, b_B.getOutboundDependencies().size(), "b_B outbound");
        assertEquals(0, b_B.getInboundDependencies().size(), "b_B inbound");
        assertEquals(0, b_B_b.getOutboundDependencies().size(), "b_B_b outbound");
        assertEquals(1, b_B_b.getInboundDependencies().size(), "b_B_b inbound");
        assertTrue(b_B_b.getInboundDependencies().contains(a_A_a), "Missing b.B.b <-- a.A.a");
    }
    
    @Test
    void testFeatureFeatureSparse() {
        a.addDependency(b);
        a_A.addDependency(b_B);
        a_A_a.addDependency(b_B_b);

        visitor.traverseNodes(factory.getPackages().values());

        assertEquals(0, a.getOutboundDependencies().size(), "a outbound");
        assertEquals(0, a.getInboundDependencies().size(), "a inbound");
        assertEquals(0, a_A.getOutboundDependencies().size(), "a_A outbound");
        assertEquals(0, a_A.getInboundDependencies().size(), "a_A inbound");
        assertEquals(1, a_A_a.getOutboundDependencies().size(), "a_A_a outbound");
        assertTrue(a_A_a.getOutboundDependencies().contains(b_B_b), "Missing a.A.a --> b.B.b");
        assertEquals(0, a_A_a.getInboundDependencies().size(), "a_A_a inbound");
        assertEquals(0, b.getOutboundDependencies().size(), "b outbound");
        assertEquals(0, b.getInboundDependencies().size(), "b inbound");
        assertEquals(0, b_B.getOutboundDependencies().size(), "b_B outbound");
        assertEquals(0, b_B.getInboundDependencies().size(), "b_B inbound");
        assertEquals(0, b_B_b.getOutboundDependencies().size(), "b_B_b outbound");
        assertEquals(1, b_B_b.getInboundDependencies().size(), "b_B_b inbound");
        assertTrue(b_B_b.getInboundDependencies().contains(a_A_a), "Missing b.B.b <-- a.A.a");
    }
}
