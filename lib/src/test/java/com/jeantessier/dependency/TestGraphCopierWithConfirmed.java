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

import junit.framework.*;

public class TestGraphCopierWithConfirmed extends TestCase {
    private NodeFactory factory;
    
    private Node a;
    private Node a_A;
    private Node a_A_a;
    
    private Node b;
    private Node b_B;
    private Node b_B_b;
    
    private Node c;
    private Node c_C;
    private Node c_C_c;

    private GraphCopier copier;

    protected void setUp() throws Exception {
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
        
        copier = new GraphCopier();
    }

    public void testConfirmedPackage2ConfirmedPackage() {
        a.addDependency(b);
        b.addDependency(c);

        factory.createPackage("a", true);
        factory.createPackage("b", true);
        factory.createPackage("c", true);
        
        b.accept(copier);

        assertTrue(copier.getFilterFactory().getPackages().get("a").isConfirmed());
        assertNull(copier.getFilterFactory().getClasses().get("a.A"));
        assertNull(copier.getFilterFactory().getFeatures().get("a.A.a"));
        assertTrue(copier.getScopeFactory().getPackages().get("b").isConfirmed());
        assertFalse(copier.getScopeFactory().getClasses().get("b.B").isConfirmed());
        assertFalse(copier.getScopeFactory().getFeatures().get("b.B.b").isConfirmed());
        assertTrue(copier.getFilterFactory().getPackages().get("c").isConfirmed());
        assertNull(copier.getFilterFactory().getClasses().get("c.C"));
        assertNull(copier.getFilterFactory().getFeatures().get("c.C.c"));
    }

    public void testUnconfirmedPackage2UnconfirmedPackage() {
        a.addDependency(b);
        b.addDependency(c);

        b.accept(copier);

        assertFalse(copier.getFilterFactory().getPackages().get("a").isConfirmed());
        assertNull(copier.getFilterFactory().getClasses().get("a.A"));
        assertNull(copier.getFilterFactory().getFeatures().get("a.A.a"));
        assertFalse(copier.getScopeFactory().getPackages().get("b").isConfirmed());
        assertFalse(copier.getScopeFactory().getClasses().get("b.B").isConfirmed());
        assertFalse(copier.getScopeFactory().getFeatures().get("b.B.b").isConfirmed());
        assertFalse(copier.getFilterFactory().getPackages().get("c").isConfirmed());
        assertNull(copier.getFilterFactory().getClasses().get("c.C"));
        assertNull(copier.getFilterFactory().getFeatures().get("c.C.c"));
    }

    public void testConfirmedClass2ConfirmedClass() {
        a_A.addDependency(b_B);
        b_B.addDependency(c_C);

        factory.createClass("a.A", true);
        factory.createClass("b.B", true);
        factory.createClass("c.C", true);
        
        b.accept(copier);

        assertTrue(copier.getFilterFactory().getPackages().get("a").isConfirmed());
        assertTrue(copier.getFilterFactory().getClasses().get("a.A").isConfirmed());
        assertNull(copier.getFilterFactory().getFeatures().get("a.A.a"));
        assertTrue(copier.getScopeFactory().getPackages().get("b").isConfirmed());
        assertTrue(copier.getScopeFactory().getClasses().get("b.B").isConfirmed());
        assertFalse(copier.getScopeFactory().getFeatures().get("b.B.b").isConfirmed());
        assertTrue(copier.getFilterFactory().getPackages().get("c").isConfirmed());
        assertTrue(copier.getFilterFactory().getClasses().get("c.C").isConfirmed());
        assertNull(copier.getFilterFactory().getFeatures().get("c.C.c"));
    }

    public void testUnconfirmedClass2UnconfirmedClass() {
        a_A.addDependency(b_B);
        b_B.addDependency(c_C);

        b.accept(copier);

        assertFalse(copier.getFilterFactory().getPackages().get("a").isConfirmed());
        assertFalse(copier.getFilterFactory().getClasses().get("a.A").isConfirmed());
        assertNull(copier.getFilterFactory().getFeatures().get("a.A.a"));
        assertFalse(copier.getScopeFactory().getPackages().get("b").isConfirmed());
        assertFalse(copier.getScopeFactory().getClasses().get("b.B").isConfirmed());
        assertFalse(copier.getScopeFactory().getFeatures().get("b.B.b").isConfirmed());
        assertFalse(copier.getFilterFactory().getPackages().get("c").isConfirmed());
        assertFalse(copier.getFilterFactory().getClasses().get("c.C").isConfirmed());
        assertNull(copier.getFilterFactory().getFeatures().get("c.C.c"));
    }

    public void testConfirmedFeature2ConfirmedFeature() {
        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);

        factory.createFeature("a.A.a", true);
        factory.createFeature("b.B.b", true);
        factory.createFeature("c.C.c", true);
        
        b.accept(copier);

        assertTrue(copier.getFilterFactory().createPackage("a").isConfirmed());
        assertTrue(copier.getFilterFactory().createClass("a.A").isConfirmed());
        assertTrue(copier.getFilterFactory().createFeature("a.A.a").isConfirmed());
        assertTrue(copier.getScopeFactory().createPackage("b").isConfirmed());
        assertTrue(copier.getScopeFactory().createClass("b.B").isConfirmed());
        assertTrue(copier.getScopeFactory().createFeature("b.B.b").isConfirmed());
        assertTrue(copier.getFilterFactory().createPackage("c").isConfirmed());
        assertTrue(copier.getFilterFactory().createClass("c.C").isConfirmed());
        assertTrue(copier.getFilterFactory().createFeature("c.C.c").isConfirmed());
    }

    public void testUnconfirmedFeature2UnconfirmedFeature() {
        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);

        b.accept(copier);

        assertFalse(copier.getFilterFactory().createPackage("a").isConfirmed());
        assertFalse(copier.getFilterFactory().createClass("a.A").isConfirmed());
        assertFalse(copier.getFilterFactory().createFeature("a.A.a").isConfirmed());
        assertFalse(copier.getScopeFactory().createPackage("b").isConfirmed());
        assertFalse(copier.getScopeFactory().createClass("b.B").isConfirmed());
        assertFalse(copier.getScopeFactory().createFeature("b.B.b").isConfirmed());
        assertFalse(copier.getFilterFactory().createPackage("c").isConfirmed());
        assertFalse(copier.getFilterFactory().createClass("c.C").isConfirmed());
        assertFalse(copier.getFilterFactory().createFeature("c.C.c").isConfirmed());
    }

    public void testUnconfirmedFeatureInConfirmedClass2UnconfirmedFeature() {
        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);

        factory.createClass("a.A", true);
        factory.createClass("b.B", true);
        factory.createClass("c.C", true);

        b.accept(copier);

        assertTrue(copier.getFilterFactory().createPackage("a").isConfirmed());
        assertTrue(copier.getFilterFactory().createClass("a.A").isConfirmed());
        assertFalse(copier.getFilterFactory().createFeature("a.A.a").isConfirmed());
        assertTrue(copier.getScopeFactory().createPackage("b").isConfirmed());
        assertTrue(copier.getScopeFactory().createClass("b.B").isConfirmed());
        assertFalse(copier.getScopeFactory().createFeature("b.B.b").isConfirmed());
        assertTrue(copier.getFilterFactory().createPackage("c").isConfirmed());
        assertTrue(copier.getFilterFactory().createClass("c.C").isConfirmed());
        assertFalse(copier.getFilterFactory().createFeature("c.C.c").isConfirmed());
    }
}
