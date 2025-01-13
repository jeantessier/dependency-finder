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

import static org.junit.jupiter.api.Assertions.*;

public class TestGraphSummarizerWithConfirmed {
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

    private final CollectionSelectionCriteria scopeSelector = new CollectionSelectionCriteria(null, null);
    private final CollectionSelectionCriteria filterSelector = new CollectionSelectionCriteria(null, null);
    private final GraphSummarizer summarizer = new GraphSummarizer(scopeSelector, filterSelector);

    @Test
    void testConfirmedPackage2ConfirmedPackage() {
        a.addDependency(b);
        b.addDependency(c);

        factory.createPackage("a", true);
        factory.createPackage("b", true);
        factory.createPackage("c", true);
        
        b.accept(summarizer);

        assertTrue(summarizer.getFilterFactory().getPackages().get("a").isConfirmed());
        assertNull(summarizer.getFilterFactory().getClasses().get("a.A"));
        assertNull(summarizer.getFilterFactory().getFeatures().get("a.A.a"));
        assertTrue(summarizer.getScopeFactory().getPackages().get("b").isConfirmed());
        assertFalse(summarizer.getScopeFactory().getClasses().get("b.B").isConfirmed());
        assertFalse(summarizer.getScopeFactory().getFeatures().get("b.B.b").isConfirmed());
        assertTrue(summarizer.getFilterFactory().getPackages().get("c").isConfirmed());
        assertNull(summarizer.getFilterFactory().getClasses().get("c.C"));
        assertNull(summarizer.getFilterFactory().getFeatures().get("c.C.c"));
    }

    @Test
    void testUnconfirmedPackage2UnconfirmedPackage() {
        a.addDependency(b);
        b.addDependency(c);

        b.accept(summarizer);

        assertFalse(summarizer.getFilterFactory().getPackages().get("a").isConfirmed());
        assertNull(summarizer.getFilterFactory().getClasses().get("a.A"));
        assertNull(summarizer.getFilterFactory().getFeatures().get("a.A.a"));
        assertFalse(summarizer.getScopeFactory().getPackages().get("b").isConfirmed());
        assertFalse(summarizer.getScopeFactory().getClasses().get("b.B").isConfirmed());
        assertFalse(summarizer.getScopeFactory().getFeatures().get("b.B.b").isConfirmed());
        assertFalse(summarizer.getFilterFactory().getPackages().get("c").isConfirmed());
        assertNull(summarizer.getFilterFactory().getClasses().get("c.C"));
        assertNull(summarizer.getFilterFactory().getFeatures().get("c.C.c"));
    }

    @Test
    void testConfirmedClass2ConfirmedClass() {
        a_A.addDependency(b_B);
        b_B.addDependency(c_C);

        factory.createClass("a.A", true);
        factory.createClass("b.B", true);
        factory.createClass("c.C", true);
        
        b.accept(summarizer);

        assertTrue(summarizer.getFilterFactory().getPackages().get("a").isConfirmed());
        assertTrue(summarizer.getFilterFactory().getClasses().get("a.A").isConfirmed());
        assertNull(summarizer.getFilterFactory().getFeatures().get("a.A.a"));
        assertTrue(summarizer.getScopeFactory().getPackages().get("b").isConfirmed());
        assertTrue(summarizer.getScopeFactory().getClasses().get("b.B").isConfirmed());
        assertFalse(summarizer.getScopeFactory().getFeatures().get("b.B.b").isConfirmed());
        assertTrue(summarizer.getFilterFactory().getPackages().get("c").isConfirmed());
        assertTrue(summarizer.getFilterFactory().getClasses().get("c.C").isConfirmed());
        assertNull(summarizer.getFilterFactory().getFeatures().get("c.C.c"));
    }

    @Test
    void testUnconfirmedClass2UnconfirmedClass() {
        a_A.addDependency(b_B);
        b_B.addDependency(c_C);

        b.accept(summarizer);

        assertFalse(summarizer.getFilterFactory().getPackages().get("a").isConfirmed());
        assertFalse(summarizer.getFilterFactory().getClasses().get("a.A").isConfirmed());
        assertNull(summarizer.getFilterFactory().getFeatures().get("a.A.a"));
        assertFalse(summarizer.getScopeFactory().getPackages().get("b").isConfirmed());
        assertFalse(summarizer.getScopeFactory().getClasses().get("b.B").isConfirmed());
        assertFalse(summarizer.getScopeFactory().getFeatures().get("b.B.b").isConfirmed());
        assertFalse(summarizer.getFilterFactory().getPackages().get("c").isConfirmed());
        assertFalse(summarizer.getFilterFactory().getClasses().get("c.C").isConfirmed());
        assertNull(summarizer.getFilterFactory().getFeatures().get("c.C.c"));
    }

    @Test
    void testConfirmedPackage2ConfirmedPackageFromClasses() {
        scopeSelector.setMatchingPackages(true);
        scopeSelector.setMatchingClasses(false);
        scopeSelector.setMatchingFeatures(false);
        filterSelector.setMatchingPackages(true);
        filterSelector.setMatchingClasses(false);
        filterSelector.setMatchingFeatures(false);
        
        a_A.addDependency(b_B);
        b_B.addDependency(c_C);

        factory.createClass("a.A", true);
        factory.createClass("b.B", true);
        factory.createClass("c.C", true);
        
        b.accept(summarizer);

        assertTrue(summarizer.getFilterFactory().getPackages().get("a").isConfirmed());
        assertNull(summarizer.getFilterFactory().getClasses().get("a.A"));
        assertNull(summarizer.getFilterFactory().getFeatures().get("a.A.a"));
        assertTrue(summarizer.getScopeFactory().getPackages().get("b").isConfirmed());
        assertNull(summarizer.getScopeFactory().getClasses().get("b.B"));
        assertNull(summarizer.getScopeFactory().getFeatures().get("b.B.b"));
        assertTrue(summarizer.getFilterFactory().getPackages().get("c").isConfirmed());
        assertNull(summarizer.getFilterFactory().getClasses().get("c.C"));
        assertNull(summarizer.getFilterFactory().getFeatures().get("c.C.c"));
    }

    @Test
    void testUnconfirmedPackage2UnconfirmedPackageFromClasses() {
        scopeSelector.setMatchingPackages(true);
        scopeSelector.setMatchingClasses(false);
        scopeSelector.setMatchingFeatures(false);
        filterSelector.setMatchingPackages(true);
        filterSelector.setMatchingClasses(false);
        filterSelector.setMatchingFeatures(false);
        
        a_A.addDependency(b_B);
        b_B.addDependency(c_C);

        b.accept(summarizer);

        assertFalse(summarizer.getFilterFactory().getPackages().get("a").isConfirmed());
        assertNull(summarizer.getFilterFactory().getClasses().get("a.A"));
        assertNull(summarizer.getFilterFactory().getFeatures().get("a.A.a"));
        assertFalse(summarizer.getScopeFactory().getPackages().get("b").isConfirmed());
        assertNull(summarizer.getScopeFactory().getClasses().get("b.B"));
        assertNull(summarizer.getScopeFactory().getFeatures().get("b.B.b"));
        assertFalse(summarizer.getFilterFactory().getPackages().get("c").isConfirmed());
        assertNull(summarizer.getFilterFactory().getClasses().get("c.C"));
        assertNull(summarizer.getFilterFactory().getFeatures().get("c.C.c"));
    }

    @Test
    void testConfirmedFeature2ConfirmedFeature() {
        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);

        factory.createFeature("a.A.a", true);
        factory.createFeature("b.B.b", true);
        factory.createFeature("c.C.c", true);
        
        b.accept(summarizer);

        assertTrue(summarizer.getFilterFactory().createPackage("a").isConfirmed());
        assertTrue(summarizer.getFilterFactory().createClass("a.A").isConfirmed());
        assertTrue(summarizer.getFilterFactory().createFeature("a.A.a").isConfirmed());
        assertTrue(summarizer.getScopeFactory().createPackage("b").isConfirmed());
        assertTrue(summarizer.getScopeFactory().createClass("b.B").isConfirmed());
        assertTrue(summarizer.getScopeFactory().createFeature("b.B.b").isConfirmed());
        assertTrue(summarizer.getFilterFactory().createPackage("c").isConfirmed());
        assertTrue(summarizer.getFilterFactory().createClass("c.C").isConfirmed());
        assertTrue(summarizer.getFilterFactory().createFeature("c.C.c").isConfirmed());
    }

    @Test
    void testUnconfirmedFeature2UnconfirmedFeature() {
        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);

        b.accept(summarizer);

        assertFalse(summarizer.getFilterFactory().createPackage("a").isConfirmed());
        assertFalse(summarizer.getFilterFactory().createClass("a.A").isConfirmed());
        assertFalse(summarizer.getFilterFactory().createFeature("a.A.a").isConfirmed());
        assertFalse(summarizer.getScopeFactory().createPackage("b").isConfirmed());
        assertFalse(summarizer.getScopeFactory().createClass("b.B").isConfirmed());
        assertFalse(summarizer.getScopeFactory().createFeature("b.B.b").isConfirmed());
        assertFalse(summarizer.getFilterFactory().createPackage("c").isConfirmed());
        assertFalse(summarizer.getFilterFactory().createClass("c.C").isConfirmed());
        assertFalse(summarizer.getFilterFactory().createFeature("c.C.c").isConfirmed());
    }

    @Test
    void testConfirmedClass2ConfirmedClassFromFeature() {
        scopeSelector.setMatchingPackages(false);
        scopeSelector.setMatchingClasses(true);
        scopeSelector.setMatchingFeatures(false);
        filterSelector.setMatchingPackages(false);
        filterSelector.setMatchingClasses(true);
        filterSelector.setMatchingFeatures(false);
        
        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);

        factory.createFeature("a.A.a", true);
        factory.createFeature("b.B.b", true);
        factory.createFeature("c.C.c", true);
        
        b.accept(summarizer);

        assertTrue(summarizer.getFilterFactory().getPackages().get("a").isConfirmed());
        assertTrue(summarizer.getFilterFactory().getClasses().get("a.A").isConfirmed());
        assertNull(summarizer.getFilterFactory().getFeatures().get("a.A.a"));
        assertTrue(summarizer.getScopeFactory().getPackages().get("b").isConfirmed());
        assertTrue(summarizer.getScopeFactory().getClasses().get("b.B").isConfirmed());
        assertNull(summarizer.getScopeFactory().getFeatures().get("b.B.b"));
        assertTrue(summarizer.getFilterFactory().getPackages().get("c").isConfirmed());
        assertTrue(summarizer.getFilterFactory().getClasses().get("c.C").isConfirmed());
        assertNull(summarizer.getFilterFactory().getFeatures().get("c.C.c"));
    }

    @Test
    void testUnconfirmedClass2UnconfirmedClassFromFeature() {
        scopeSelector.setMatchingPackages(false);
        scopeSelector.setMatchingClasses(true);
        scopeSelector.setMatchingFeatures(false);
        filterSelector.setMatchingPackages(false);
        filterSelector.setMatchingClasses(true);
        filterSelector.setMatchingFeatures(false);
        
        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);

        b.accept(summarizer);

        assertFalse(summarizer.getFilterFactory().getPackages().get("a").isConfirmed());
        assertFalse(summarizer.getFilterFactory().getClasses().get("a.A").isConfirmed());
        assertNull(summarizer.getFilterFactory().getFeatures().get("a.A.a"));
        assertFalse(summarizer.getScopeFactory().getPackages().get("b").isConfirmed());
        assertFalse(summarizer.getScopeFactory().getClasses().get("b.B").isConfirmed());
        assertNull(summarizer.getScopeFactory().getFeatures().get("b.B.b"));
        assertFalse(summarizer.getFilterFactory().getPackages().get("c").isConfirmed());
        assertFalse(summarizer.getFilterFactory().getClasses().get("c.C").isConfirmed());
        assertNull(summarizer.getFilterFactory().getFeatures().get("c.C.c"));
    }

    @Test
    void testConfirmedPackage2ConfirmedPackageFromFeature() {
        scopeSelector.setMatchingPackages(true);
        scopeSelector.setMatchingClasses(false);
        scopeSelector.setMatchingFeatures(false);
        filterSelector.setMatchingPackages(true);
        filterSelector.setMatchingClasses(false);
        filterSelector.setMatchingFeatures(false);
        
        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);

        factory.createFeature("a.A.a", true);
        factory.createFeature("b.B.b", true);
        factory.createFeature("c.C.c", true);
        
        b.accept(summarizer);

        assertTrue(summarizer.getFilterFactory().getPackages().get("a").isConfirmed());
        assertNull(summarizer.getFilterFactory().getClasses().get("a.A"));
        assertNull(summarizer.getFilterFactory().getFeatures().get("a.A.a"));
        assertTrue(summarizer.getScopeFactory().getPackages().get("b").isConfirmed());
        assertNull(summarizer.getScopeFactory().getClasses().get("b.B"));
        assertNull(summarizer.getScopeFactory().getFeatures().get("b.B.b"));
        assertTrue(summarizer.getFilterFactory().getPackages().get("c").isConfirmed());
        assertNull(summarizer.getFilterFactory().getClasses().get("c.C"));
        assertNull(summarizer.getFilterFactory().getFeatures().get("c.C.c"));
    }

    @Test
    void testUnconfirmedPackage2UnconfirmedPackageFromFeature() {
        scopeSelector.setMatchingPackages(true);
        scopeSelector.setMatchingClasses(false);
        scopeSelector.setMatchingFeatures(false);
        filterSelector.setMatchingPackages(true);
        filterSelector.setMatchingClasses(false);
        filterSelector.setMatchingFeatures(false);
        
        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);

        b.accept(summarizer);

        assertFalse(summarizer.getFilterFactory().getPackages().get("a").isConfirmed());
        assertNull(summarizer.getFilterFactory().getClasses().get("a.A"));
        assertNull(summarizer.getFilterFactory().getFeatures().get("a.A.a"));
        assertFalse(summarizer.getScopeFactory().getPackages().get("b").isConfirmed());
        assertNull(summarizer.getScopeFactory().getClasses().get("b.B"));
        assertNull(summarizer.getScopeFactory().getFeatures().get("b.B.b"));
        assertFalse(summarizer.getFilterFactory().getPackages().get("c").isConfirmed());
        assertNull(summarizer.getFilterFactory().getClasses().get("c.C"));
        assertNull(summarizer.getFilterFactory().getFeatures().get("c.C.c"));
    }

    @Test
    void testUnconfirmedFeatureInConfirmedClass2UnconfirmedFeature() {
        scopeSelector.setMatchingPackages(false);
        scopeSelector.setMatchingClasses(false);
        scopeSelector.setMatchingFeatures(true);
        filterSelector.setMatchingPackages(false);
        filterSelector.setMatchingClasses(false);
        filterSelector.setMatchingFeatures(true);

        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);

        factory.createClass("a.A", true);
        factory.createClass("b.B", true);
        factory.createClass("c.C", true);

        b.accept(summarizer);

        assertTrue(summarizer.getFilterFactory().createPackage("a").isConfirmed());
        assertTrue(summarizer.getFilterFactory().createClass("a.A").isConfirmed());
        assertFalse(summarizer.getFilterFactory().createFeature("a.A.a").isConfirmed());
        assertTrue(summarizer.getScopeFactory().createPackage("b").isConfirmed());
        assertTrue(summarizer.getScopeFactory().createClass("b.B").isConfirmed());
        assertFalse(summarizer.getScopeFactory().createFeature("b.B.b").isConfirmed());
        assertTrue(summarizer.getFilterFactory().createPackage("c").isConfirmed());
        assertTrue(summarizer.getFilterFactory().createClass("c.C").isConfirmed());
        assertFalse(summarizer.getFilterFactory().createFeature("c.C.c").isConfirmed());
    }
}
