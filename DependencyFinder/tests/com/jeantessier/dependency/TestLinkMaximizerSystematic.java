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

import junit.framework.*;

public class TestLinkMaximizerSystematic extends TestCase {
    private NodeFactory factory;

    private PackageNode a;
    private ClassNode a_A;
    private FeatureNode a_A_a;

    private PackageNode b;
    private ClassNode b_B;
    private FeatureNode b_B_b;

    protected void setUp() throws Exception {
        factory = new NodeFactory();

        a     = factory.createPackage("a");
        a_A   = factory.createClass("a.A");
        a_A_a = factory.createFeature("a.A.a()");

        b     = factory.createPackage("b");
        b_B   = factory.createClass("b.B");
        b_B_b = factory.createFeature("b.B.b()");
    }

    public void testPackagePackage() {
        a.addDependency(b);

        Visitor visitor = new LinkMaximizer();
        visitor.traverseNodes(factory.getPackages().values());

        assertEquals("a outbound",     1, a.getOutboundDependencies().size());
        assertTrue("Missing a --> b", a.getOutboundDependencies().contains(b));
        assertEquals("a inbound",      0, a.getInboundDependencies().size());
        assertEquals("a_A outbound",   0, a_A.getOutboundDependencies().size());
        assertEquals("a_A inbound",    0, a_A.getInboundDependencies().size());
        assertEquals("a_A_a outbound", 0, a_A_a.getOutboundDependencies().size());
        assertEquals("a_A_a inbound",  0, a_A_a.getInboundDependencies().size()); 
        assertEquals("b outbound",     0, b.getOutboundDependencies().size());
        assertEquals("b inbound",      1, b.getInboundDependencies().size());
        assertTrue("Missing b <-- a", b.getInboundDependencies().contains(a));
        assertEquals("b_B outbound",   0, b_B.getOutboundDependencies().size());
        assertEquals("b_B inbound",    0, b_B.getInboundDependencies().size());
        assertEquals("b_B_b outbound", 0, b_B_b.getOutboundDependencies().size());
        assertEquals("b_B_b inbound",  0, b_B_b.getInboundDependencies().size());
    }

    public void testPackageClass() {
        a.addDependency(b_B);

        Visitor visitor = new LinkMaximizer();
        visitor.traverseNodes(factory.getPackages().values());

        assertEquals("a outbound",     2, a.getOutboundDependencies().size());
        assertTrue("Missing a --> b",   a.getOutboundDependencies().contains(b));
        assertTrue("Missing a --> b.B", a.getOutboundDependencies().contains(b_B));
        assertEquals("a inbound",      0, a.getInboundDependencies().size());
        assertEquals("a_A outbound",   0, a_A.getOutboundDependencies().size());
        assertEquals("a_A inbound",    0, a_A.getInboundDependencies().size());
        assertEquals("a_A_a outbound", 0, a_A_a.getOutboundDependencies().size());
        assertEquals("a_A_a inbound",  0, a_A_a.getInboundDependencies().size());
        assertEquals("b outbound",     0, b.getOutboundDependencies().size());
        assertEquals("b inbound",      1, b.getInboundDependencies().size());
        assertTrue("Missing b <-- a", b.getInboundDependencies().contains(a));
        assertEquals("b_B outbound",   0, b_B.getOutboundDependencies().size());
        assertEquals("b_B inbound",    1, b_B.getInboundDependencies().size());
        assertTrue("Missing b.B <-- a", b_B.getInboundDependencies().contains(a));
        assertEquals("b_B_b outbound", 0, b_B_b.getOutboundDependencies().size());
        assertEquals("b_B_b inbound",  0, b_B_b.getInboundDependencies().size());
    }

    public void testPackageFeature() {
        a.addDependency(b_B_b);

        Visitor visitor = new LinkMaximizer();
        visitor.traverseNodes(factory.getPackages().values());

        assertEquals("a outbound",     3, a.getOutboundDependencies().size());
        assertTrue("Missing a --> b",     a.getOutboundDependencies().contains(b));
        assertTrue("Missing a --> b.B",   a.getOutboundDependencies().contains(b_B));
        assertTrue("Missing a --> b.B.b", a.getOutboundDependencies().contains(b_B_b));
        assertEquals("a inbound",      0, a.getInboundDependencies().size());
        assertEquals("a_A outbound",   0, a_A.getOutboundDependencies().size());
        assertEquals("a_A inbound",    0, a_A.getInboundDependencies().size());
        assertEquals("a_A_a outbound", 0, a_A_a.getOutboundDependencies().size());
        assertEquals("a_A_a inbound",  0, a_A_a.getInboundDependencies().size());
        assertEquals("b outbound",     0, b.getOutboundDependencies().size());
        assertEquals("b inbound",      1, b.getInboundDependencies().size());
        assertTrue("Missing b <-- a", b.getInboundDependencies().contains(a));
        assertEquals("b_B outbound",   0, b_B.getOutboundDependencies().size());
        assertEquals("b_B inbound",    1, b_B.getInboundDependencies().size());
        assertTrue("Missing b.B <-- a", b_B.getInboundDependencies().contains(a));
        assertEquals("b_B_b outbound", 0, b_B_b.getOutboundDependencies().size());
        assertEquals("b_B_b inbound",  1, b_B_b.getInboundDependencies().size());
        assertTrue("Missing b.B.b <-- a", b_B_b.getInboundDependencies().contains(a));
    }

    public void testClassPackage() {
        a_A.addDependency(b);

        Visitor visitor = new LinkMaximizer();
        visitor.traverseNodes(factory.getPackages().values());

        assertEquals("a outbound",     1, a.getOutboundDependencies().size());
        assertTrue("Missing a --> b", a.getOutboundDependencies().contains(b));
        assertEquals("a inbound",      0, a.getInboundDependencies().size());
        assertEquals("a_A outbound",   1, a_A.getOutboundDependencies().size());
        assertTrue("Missing a.A --> b", a_A.getOutboundDependencies().contains(b));
        assertEquals("a_A inbound",    0, a_A.getInboundDependencies().size());
        assertEquals("a_A_a outbound", 0, a_A_a.getOutboundDependencies().size());
        assertEquals("a_A_a inbound",  0, a_A_a.getInboundDependencies().size()); 
        assertEquals("b outbound",     0, b.getOutboundDependencies().size());
        assertEquals("b inbound",      2, b.getInboundDependencies().size());
        assertTrue("Missing b <-- a", b.getInboundDependencies().contains(a));
        assertTrue("Missing b <-- a.A", b.getInboundDependencies().contains(a_A));
        assertEquals("b_B outbound",   0, b_B.getOutboundDependencies().size());
        assertEquals("b_B inbound",    0, b_B.getInboundDependencies().size());
        assertEquals("b_B_b outbound", 0, b_B_b.getOutboundDependencies().size());
        assertEquals("b_B_b inbound",  0, b_B_b.getInboundDependencies().size());
    }

    public void testClassClass() {
        a_A.addDependency(b_B);

        Visitor visitor = new LinkMaximizer();
        visitor.traverseNodes(factory.getPackages().values());

        assertEquals("a outbound",     2, a.getOutboundDependencies().size());
        assertTrue("Missing a --> b",   a.getOutboundDependencies().contains(b));
        assertTrue("Missing a --> b.B", a.getOutboundDependencies().contains(b_B));
        assertEquals("a inbound",      0, a.getInboundDependencies().size());
        assertEquals("a_A outbound",   2, a_A.getOutboundDependencies().size());
        assertTrue("Missing a.A --> b",   a_A.getOutboundDependencies().contains(b));
        assertTrue("Missing a.A --> b.B", a_A.getOutboundDependencies().contains(b_B));
        assertEquals("a_A inbound",    0, a_A.getInboundDependencies().size());
        assertEquals("a_A_a outbound", 0, a_A_a.getOutboundDependencies().size());
        assertEquals("a_A_a inbound",  0, a_A_a.getInboundDependencies().size());
        assertEquals("b outbound",     0, b.getOutboundDependencies().size());
        assertEquals("b inbound",      2, b.getInboundDependencies().size());
        assertTrue("Missing b <-- a",   b.getInboundDependencies().contains(a));
        assertTrue("Missing b <-- a.A", b.getInboundDependencies().contains(a_A));
        assertEquals("b_B outbound",   0, b_B.getOutboundDependencies().size());
        assertEquals("b_B inbound",    2, b_B.getInboundDependencies().size());
        assertTrue("Missing b.B <-- a",   b_B.getInboundDependencies().contains(a));
        assertTrue("Missing b.B <-- a.A", b_B.getInboundDependencies().contains(a_A));
        assertEquals("b_B_b outbound", 0, b_B_b.getOutboundDependencies().size());
        assertEquals("b_B_b inbound",  0, b_B_b.getInboundDependencies().size());
    }

    public void testClassClassSparse() {
        a.addDependency(b);
        a_A.addDependency(b_B);

        Visitor visitor = new LinkMaximizer();
        visitor.traverseNodes(factory.getPackages().values());

        assertEquals("a outbound",     2, a.getOutboundDependencies().size());
        assertTrue("Missing a --> b",   a.getOutboundDependencies().contains(b));
        assertTrue("Missing a --> b.B", a.getOutboundDependencies().contains(b_B));
        assertEquals("a inbound",      0, a.getInboundDependencies().size());
        assertEquals("a_A outbound",   2, a_A.getOutboundDependencies().size());
        assertTrue("Missing a.A --> b",   a_A.getOutboundDependencies().contains(b));
        assertTrue("Missing a.A --> b.B", a_A.getOutboundDependencies().contains(b_B));
        assertEquals("a_A inbound",    0, a_A.getInboundDependencies().size());
        assertEquals("a_A_a outbound", 0, a_A_a.getOutboundDependencies().size());
        assertEquals("a_A_a inbound",  0, a_A_a.getInboundDependencies().size());
        assertEquals("b outbound",     0, b.getOutboundDependencies().size());
        assertEquals("b inbound",      2, b.getInboundDependencies().size());
        assertTrue("Missing b <-- a",   b.getInboundDependencies().contains(a));
        assertTrue("Missing b <-- a.A", b.getInboundDependencies().contains(a_A));
        assertEquals("b_B outbound",   0, b_B.getOutboundDependencies().size());
        assertEquals("b_B inbound",    2, b_B.getInboundDependencies().size());
        assertTrue("Missing b.B <-- a",   b_B.getInboundDependencies().contains(a));
        assertTrue("Missing b.B <-- a.A", b_B.getInboundDependencies().contains(a_A));
        assertEquals("b_B_b outbound", 0, b_B_b.getOutboundDependencies().size());
        assertEquals("b_B_b inbound",  0, b_B_b.getInboundDependencies().size());
    }

    public void testClassFeature() {
        a_A.addDependency(b_B_b);

        Visitor visitor = new LinkMaximizer();
        visitor.traverseNodes(factory.getPackages().values());

        assertEquals("a outbound",     3, a.getOutboundDependencies().size());
        assertTrue("Missing a --> b",     a.getOutboundDependencies().contains(b));
        assertTrue("Missing a --> b.B",   a.getOutboundDependencies().contains(b_B));
        assertTrue("Missing a --> b.B.b", a.getOutboundDependencies().contains(b_B_b));
        assertEquals("a inbound",      0, a.getInboundDependencies().size());
        assertEquals("a_A outbound",   3, a_A.getOutboundDependencies().size());
        assertTrue("Missing a.A --> b",     a_A.getOutboundDependencies().contains(b));
        assertTrue("Missing a.A --> b.B",   a_A.getOutboundDependencies().contains(b_B));
        assertTrue("Missing a.A --> b.B.b", a_A.getOutboundDependencies().contains(b_B_b));
        assertEquals("a_A inbound",    0, a_A.getInboundDependencies().size());
        assertEquals("a_A_a outbound", 0, a_A_a.getOutboundDependencies().size());
        assertEquals("a_A_a inbound",  0, a_A_a.getInboundDependencies().size());
        assertEquals("b outbound",     0, b.getOutboundDependencies().size());
        assertEquals("b inbound",      2, b.getInboundDependencies().size());
        assertTrue("Missing b <-- a",   b.getInboundDependencies().contains(a));
        assertTrue("Missing b <-- a_A", b.getInboundDependencies().contains(a_A));
        assertEquals("b_B outbound",   0, b_B.getOutboundDependencies().size());
        assertEquals("b_B inbound",    2, b_B.getInboundDependencies().size());
        assertTrue("Missing b.B <-- a",   b_B.getInboundDependencies().contains(a));
        assertTrue("Missing b.B <-- a_A", b_B.getInboundDependencies().contains(a_A));
        assertEquals("b_B_b outbound", 0, b_B_b.getOutboundDependencies().size());
        assertEquals("b_B_b inbound",  2, b_B_b.getInboundDependencies().size());
        assertTrue("Missing b.B.b <-- a",   b_B_b.getInboundDependencies().contains(a));
        assertTrue("Missing b.B.b <-- a_A", b_B_b.getInboundDependencies().contains(a_A));
    }

    public void testFeaturePackage() {
        a_A_a.addDependency(b);

        Visitor visitor = new LinkMaximizer();
        visitor.traverseNodes(factory.getPackages().values());

        assertEquals("a outbound",     1, a.getOutboundDependencies().size());
        assertTrue("Missing a --> b", a.getOutboundDependencies().contains(b));
        assertEquals("a inbound",      0, a.getInboundDependencies().size());
        assertEquals("a_A outbound",   1, a_A.getOutboundDependencies().size());
        assertTrue("Missing a.A --> b", a_A.getOutboundDependencies().contains(b));
        assertEquals("a_A inbound",    0, a_A.getInboundDependencies().size());
        assertEquals("a_A_a outbound", 1, a_A_a.getOutboundDependencies().size());
        assertTrue("Missing a.A.a --> b", a_A_a.getOutboundDependencies().contains(b));
        assertEquals("a_A_a inbound",  0, a_A_a.getInboundDependencies().size()); 
        assertEquals("b outbound",     0, b.getOutboundDependencies().size());
        assertEquals("b inbound",      3, b.getInboundDependencies().size());
        assertTrue("Missing b <-- a",     b.getInboundDependencies().contains(a));
        assertTrue("Missing b <-- a.A",   b.getInboundDependencies().contains(a_A));
        assertTrue("Missing b <-- a.A.a", b.getInboundDependencies().contains(a_A_a));
        assertEquals("b_B outbound",   0, b_B.getOutboundDependencies().size());
        assertEquals("b_B inbound",    0, b_B.getInboundDependencies().size());
        assertEquals("b_B_b outbound", 0, b_B_b.getOutboundDependencies().size());
        assertEquals("b_B_b inbound",  0, b_B_b.getInboundDependencies().size());
    }
    
    public void testFeatureClass() {
        a_A_a.addDependency(b_B);

        Visitor visitor = new LinkMaximizer();
        visitor.traverseNodes(factory.getPackages().values());

        assertEquals("a outbound",     2, a.getOutboundDependencies().size());
        assertTrue("Missing a --> b",   a.getOutboundDependencies().contains(b));
        assertTrue("Missing a --> b.B", a.getOutboundDependencies().contains(b_B));
        assertEquals("a inbound",      0, a.getInboundDependencies().size());
        assertEquals("a_A outbound",   2, a_A.getOutboundDependencies().size());
        assertTrue("Missing a.A --> b",   a_A.getOutboundDependencies().contains(b));
        assertTrue("Missing a.A --> b.B", a_A.getOutboundDependencies().contains(b_B));
        assertEquals("a_A inbound",    0, a_A.getInboundDependencies().size());
        assertEquals("a_A_a outbound", 2, a_A_a.getOutboundDependencies().size());
        assertTrue("Missing a.A.a --> b",   a_A_a.getOutboundDependencies().contains(b));
        assertTrue("Missing a.A.a --> b.B", a_A_a.getOutboundDependencies().contains(b_B));
        assertEquals("a_A_a inbound",  0, a_A_a.getInboundDependencies().size());
        assertEquals("b outbound",     0, b.getOutboundDependencies().size());
        assertEquals("b inbound",      3, b.getInboundDependencies().size());
        assertTrue("Missing b <-- a",     b.getInboundDependencies().contains(a));
        assertTrue("Missing b <-- a.A",   b.getInboundDependencies().contains(a_A));
        assertTrue("Missing b <-- a.A.a", b.getInboundDependencies().contains(a_A_a));
        assertEquals("b_B outbound",   0, b_B.getOutboundDependencies().size());
        assertEquals("b_B inbound",    3, b_B.getInboundDependencies().size());
        assertTrue("Missing b.B <-- a",     b_B.getInboundDependencies().contains(a));
        assertTrue("Missing b.B <-- a.A",   b_B.getInboundDependencies().contains(a_A));
        assertTrue("Missing b.B <-- a.A.a", b_B.getInboundDependencies().contains(a_A_a));
        assertEquals("b_B_b outbound", 0, b_B_b.getOutboundDependencies().size());
        assertEquals("b_B_b inbound",  0, b_B_b.getInboundDependencies().size());
    }
    
    public void testFeatureFeature() {
        a_A_a.addDependency(b_B_b);

        Visitor visitor = new LinkMaximizer();
        visitor.traverseNodes(factory.getPackages().values());

        assertEquals("a outbound",     3, a.getOutboundDependencies().size());
        assertTrue("Missing a --> b",     a.getOutboundDependencies().contains(b));
        assertTrue("Missing a --> b.B",   a.getOutboundDependencies().contains(b_B));
        assertTrue("Missing a --> b.B.b", a.getOutboundDependencies().contains(b_B_b));
        assertEquals("a inbound",      0, a.getInboundDependencies().size());
        assertEquals("a_A outbound",   3, a_A.getOutboundDependencies().size());
        assertTrue("Missing a.A --> b",     a_A.getOutboundDependencies().contains(b));
        assertTrue("Missing a.A --> b.B",   a_A.getOutboundDependencies().contains(b_B));
        assertTrue("Missing a.A --> b.B.b", a_A.getOutboundDependencies().contains(b_B_b));
        assertEquals("a_A inbound",    0, a_A.getInboundDependencies().size());
        assertEquals("a_A_a outbound", 3, a_A_a.getOutboundDependencies().size());
        assertTrue("Missing a.A.a --> b",     a_A_a.getOutboundDependencies().contains(b));
        assertTrue("Missing a.A.a --> b.B",   a_A_a.getOutboundDependencies().contains(b_B));
        assertTrue("Missing a.A.a --> b.B.b", a_A_a.getOutboundDependencies().contains(b_B_b));
        assertEquals("a_A_a inbound",  0, a_A_a.getInboundDependencies().size());
        assertEquals("b outbound",     0, b.getOutboundDependencies().size());
        assertEquals("b inbound",      3, b.getInboundDependencies().size());
        assertTrue("Missing b <-- a",     b.getInboundDependencies().contains(a));
        assertTrue("Missing b <-- a.A",   b.getInboundDependencies().contains(a_A));
        assertTrue("Missing b <-- a.A.a", b.getInboundDependencies().contains(a_A_a));
        assertEquals("b_B outbound",   0, b_B.getOutboundDependencies().size());
        assertEquals("b_B inbound",    3, b_B.getInboundDependencies().size());
        assertTrue("Missing b.B <-- a",     b_B.getInboundDependencies().contains(a));
        assertTrue("Missing b.B <-- a.A",   b_B.getInboundDependencies().contains(a_A));
        assertTrue("Missing b.B <-- a.A.a", b_B.getInboundDependencies().contains(a_A_a));
        assertEquals("b_B_b outbound", 0, b_B_b.getOutboundDependencies().size());
        assertEquals("b_B_b inbound",  3, b_B_b.getInboundDependencies().size());
        assertTrue("Missing b.B.b <-- a",     b_B_b.getInboundDependencies().contains(a));
        assertTrue("Missing b.B.b <-- a.A",   b_B_b.getInboundDependencies().contains(a_A));
        assertTrue("Missing b.B.b <-- a.A.a", b_B_b.getInboundDependencies().contains(a_A_a));
    }
    
    public void testFeatureFeatureSparse() {
        a.addDependency(b);
        a_A.addDependency(b_B);
        a_A_a.addDependency(b_B_b);

        Visitor visitor = new LinkMaximizer();
        visitor.traverseNodes(factory.getPackages().values());

        assertEquals("a outbound",     3, a.getOutboundDependencies().size());
        assertTrue("Missing a --> b",     a.getOutboundDependencies().contains(b));
        assertTrue("Missing a --> b.B",   a.getOutboundDependencies().contains(b_B));
        assertTrue("Missing a --> b.B.b", a.getOutboundDependencies().contains(b_B_b));
        assertEquals("a inbound",      0, a.getInboundDependencies().size());
        assertEquals("a_A outbound",   3, a_A.getOutboundDependencies().size());
        assertTrue("Missing a.A --> b",     a_A.getOutboundDependencies().contains(b));
        assertTrue("Missing a.A --> b.B",   a_A.getOutboundDependencies().contains(b_B));
        assertTrue("Missing a.A --> b.B.b", a_A.getOutboundDependencies().contains(b_B_b));
        assertEquals("a_A inbound",    0, a_A.getInboundDependencies().size());
        assertEquals("a_A_a outbound", 3, a_A_a.getOutboundDependencies().size());
        assertTrue("Missing a.A.a --> b",     a_A_a.getOutboundDependencies().contains(b));
        assertTrue("Missing a.A.a --> b.B",   a_A_a.getOutboundDependencies().contains(b_B));
        assertTrue("Missing a.A.a --> b.B.b", a_A_a.getOutboundDependencies().contains(b_B_b));
        assertEquals("a_A_a inbound",  0, a_A_a.getInboundDependencies().size());
        assertEquals("b outbound",     0, b.getOutboundDependencies().size());
        assertEquals("b inbound",      3, b.getInboundDependencies().size());
        assertTrue("Missing b <-- a",     b.getInboundDependencies().contains(a));
        assertTrue("Missing b <-- a.A",   b.getInboundDependencies().contains(a_A));
        assertTrue("Missing b <-- a.A.a", b.getInboundDependencies().contains(a_A_a));
        assertEquals("b_B outbound",   0, b_B.getOutboundDependencies().size());
        assertEquals("b_B inbound",    3, b_B.getInboundDependencies().size());
        assertTrue("Missing b.B <-- a",     b_B.getInboundDependencies().contains(a));
        assertTrue("Missing b.B <-- a.A",   b_B.getInboundDependencies().contains(a_A));
        assertTrue("Missing b.B <-- a.A.a", b_B.getInboundDependencies().contains(a_A_a));
        assertEquals("b_B_b outbound", 0, b_B_b.getOutboundDependencies().size());
        assertEquals("b_B_b inbound",  3, b_B_b.getInboundDependencies().size());
        assertTrue("Missing b.B.b <-- a",     b_B_b.getInboundDependencies().contains(a));
        assertTrue("Missing b.B.b <-- a.A",   b_B_b.getInboundDependencies().contains(a_A));
        assertTrue("Missing b.B.b <-- a.A.a", b_B_b.getInboundDependencies().contains(a_A_a));
    }
}
