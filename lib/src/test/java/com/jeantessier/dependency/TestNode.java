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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestNode {
    private Node a;
    private Node a_A;
    private Node a_A_a;
    private Node a_A_b;
    private Node a_B;
    private Node a_B_a;
    private Node a_B_b;

    private Node b;
    private Node b_B;
    private Node b_B_b;

    @BeforeEach
    void setUp() {
        var factory = new NodeFactory();

        a = factory.createPackage("a");
        a_A = factory.createClass("a.A");
        a_A_a = factory.createFeature("a.A.a");
        a_A_b = factory.createFeature("a.A.b");
        a_B = factory.createClass("a.B");
        a_B_a = factory.createFeature("a.B.a");
        a_B_b = factory.createFeature("a.B.b");

        b = factory.createPackage("b");
        b_B = factory.createClass("b.B");
        b_B_b = factory.createFeature("b.B.b");
    }

    @Test
    void testPackageCanAddDependency() {
        assertFalse(a.canAddDependencyTo(a));
        assertTrue(a.canAddDependencyTo(a_A));
        assertTrue(a.canAddDependencyTo(a_A_a));
        assertTrue(a.canAddDependencyTo(a_A_b));
        assertTrue(a.canAddDependencyTo(a_B));
        assertTrue(a.canAddDependencyTo(a_B_a));
        assertTrue(a.canAddDependencyTo(a_B_b));

        assertTrue(a.canAddDependencyTo(b));
        assertTrue(a.canAddDependencyTo(b_B));
        assertTrue(a.canAddDependencyTo(b_B_b));
    }

    @Test
    void testClassCanAddDependency() {
        assertFalse(a_A.canAddDependencyTo(a));
        assertFalse(a_A.canAddDependencyTo(a_A));
        assertTrue(a_A.canAddDependencyTo(a_A_a));
        assertTrue(a_A.canAddDependencyTo(a_A_b));
        assertTrue(a_A.canAddDependencyTo(a_B));
        assertTrue(a_A.canAddDependencyTo(a_B_a));
        assertTrue(a_A.canAddDependencyTo(a_B_b));

        assertTrue(a_A.canAddDependencyTo(b));
        assertTrue(a_A.canAddDependencyTo(b_B));
        assertTrue(a_A.canAddDependencyTo(b_B_b));
    }

    @Test
    void testFeatureCanAddDependency() {
        assertFalse(a_A_a.canAddDependencyTo(a));
        assertFalse(a_A_a.canAddDependencyTo(a_A));
        assertFalse(a_A_a.canAddDependencyTo(a_A_a));
        assertTrue(a_A_a.canAddDependencyTo(a_A_b));
        assertTrue(a_A_a.canAddDependencyTo(a_B));
        assertTrue(a_A_a.canAddDependencyTo(a_B_a));
        assertTrue(a_A_a.canAddDependencyTo(a_B_b));

        assertTrue(a_A_a.canAddDependencyTo(b));
        assertTrue(a_A_a.canAddDependencyTo(b_B));
        assertTrue(a_A_a.canAddDependencyTo(b_B_b));
    }

    @Test
    void testRemoveOneDependency() {
        a_A_a.addDependency(b_B_b);
        assertTrue(a_A_a.getOutboundDependencies().contains(b_B_b), "Missing a.A.a --> b.B.b");
        assertTrue(b_B_b.getInboundDependencies().contains(a_A_a), "Missing b.B.b <-- a.A.a");

        a_A_a.removeDependency(b_B_b);
        assertFalse(a_A_a.getOutboundDependencies().contains(b_B_b), "Did not remove a.A.a --> b.B.b");
        assertFalse(b_B_b.getInboundDependencies().contains(a_A_a), "Did not remove b.B.b <-- a.A.a");
    }

    @Test
    void testRemoveOneDependencyButNotAnother() {
        a_A_a.addDependency(a_A_b);
        assertTrue(a_A_a.getOutboundDependencies().contains(a_A_b), "Missing a.A.a --> a.A.b");
        assertTrue(a_A_b.getInboundDependencies().contains(a_A_a), "Missing a.A.b <-- a.A.a");

        a_A_a.addDependency(b_B_b);
        assertTrue(a_A_a.getOutboundDependencies().contains(b_B_b), "Missing a.A.a --> b.B.b");
        assertTrue(b_B_b.getInboundDependencies().contains(a_A_a), "Missing b.B.b <-- a.A.a");

        a_A_a.removeDependency(b_B_b);
        assertTrue(a_A_a.getOutboundDependencies().contains(a_A_b), "Missing a.A.a --> a.A.b");
        assertTrue(a_A_b.getInboundDependencies().contains(a_A_a), "Missing a.A.b <-- a.A.a");
        assertFalse(a_A_a.getOutboundDependencies().contains(b_B_b), "Did not remove a.A.a --> b.B.b");
        assertFalse(b_B_b.getInboundDependencies().contains(a_A_a), "Did not remove b.B.b <-- a.A.a");
    }

    @Test
    void testRemoveDependencies() {
        a_A_a.addDependency(a_A_b);
        a_A_a.addDependency(b_B_b);
        assertTrue(a_A_a.getOutboundDependencies().contains(a_A_b), "Missing a.A.a --> a.A.b");
        assertTrue(a_A_b.getInboundDependencies().contains(a_A_a), "Missing a.A.b <-- a.A.a");
        assertTrue(a_A_a.getOutboundDependencies().contains(b_B_b), "Missing a.A.a --> b.B.b");
        assertTrue(b_B_b.getInboundDependencies().contains(a_A_a), "Missing b.B.b <-- a.A.a");

        var dependencies = List.of(a_A_b, b_B_b);
        
        a_A_a.removeDependencies(dependencies);
        assertFalse(a_A_a.getOutboundDependencies().contains(a_A_b), "Did not remove a.A.a --> a.A.b");
        assertFalse(a_A_b.getInboundDependencies().contains(a_A_a), "Did not remove a.A.b <-- a.A.a");
        assertFalse(a_A_a.getOutboundDependencies().contains(b_B_b), "Did not remove a.A.a --> b.B.b");
        assertFalse(b_B_b.getInboundDependencies().contains(a_A_a), "Did not remove b.B.b <-- a.A.a");
    }
}
