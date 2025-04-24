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

public class TestCollectionSelectionCriteria {
    private final NodeFactory factory = new NodeFactory();

    private final PackageNode a = factory.createPackage("a");
    private final ClassNode a_A = factory.createClass("a.A");
    private final FeatureNode a_A_a = factory.createFeature("a.A.a");
    
    private final PackageNode b = factory.createPackage("b");
    private final ClassNode b_B = factory.createClass("b.B");
    private final FeatureNode b_B_b = factory.createFeature("b.B.b");

    private final Collection<String> include = new HashSet<>();
    private final Collection<String> exclude = new HashSet<>();

    private CollectionSelectionCriteria criteria = new CollectionSelectionCriteria(include, exclude);

    @Test
    void testEmptyInclude() {
        assertFalse(criteria.matches(a), "a");
        assertFalse(criteria.matches(a_A), "a.A");
        assertFalse(criteria.matches(a_A_a), "a.A.a");

        assertFalse(criteria.matches(b), "b");
        assertFalse(criteria.matches(b_B), "b.B");
        assertFalse(criteria.matches(b_B_b), "b.B.b");
    }

    @Test
    void testNullInclude() {
        criteria = new CollectionSelectionCriteria(null, exclude);

        assertTrue(criteria.matches(a), "a");
        assertTrue(criteria.matches(a_A), "a.A");
        assertTrue(criteria.matches(a_A_a), "a.A.a");

        assertTrue(criteria.matches(b), "b");
        assertTrue(criteria.matches(b_B), "b.B");
        assertTrue(criteria.matches(b_B_b), "b.B.b");
    }

    @Test
    void testMatchPackageNode() {
        include.add("a");

        assertTrue(criteria.matches(a), "a");
        assertFalse(criteria.matches(a_A), "a.A");
        assertFalse(criteria.matches(a_A_a), "a.A.a");

        assertFalse(criteria.matches(b), "b");
        assertFalse(criteria.matches(b_B), "b.B");
        assertFalse(criteria.matches(b_B_b), "b.B.b");
    }

    @Test
    void testMatchClassNode() {
        include.add("a.A");

        assertFalse(criteria.matches(a), "a");
        assertTrue(criteria.matches(a_A), "a.A");
        assertFalse(criteria.matches(a_A_a), "a.A.a");

        assertFalse(criteria.matches(b), "b");
        assertFalse(criteria.matches(b_B), "b.B");
        assertFalse(criteria.matches(b_B_b), "b.B.b");
    }

    @Test
    void testMatchFeatureNode() {
        include.add("a.A.a");

        assertFalse(criteria.matches(a), "a");
        assertFalse(criteria.matches(a_A), "a.A");
        assertTrue(criteria.matches(a_A_a), "a.A.a");

        assertFalse(criteria.matches(b), "b");
        assertFalse(criteria.matches(b_B), "b.B");
        assertFalse(criteria.matches(b_B_b), "b.B.b");
    }

    @Test
    void testMatchPackageName() {
        include.add("a");

        assertTrue(criteria.matchesPackageName("a"), "a");
        assertFalse(criteria.matchesClassName("a.A"), "a.A");
        assertFalse(criteria.matchesFeatureName("a.A.a"), "a.A.a");

        assertFalse(criteria.matchesPackageName("b"), "b");
        assertFalse(criteria.matchesClassName("b.B"), "b.B");
        assertFalse(criteria.matchesFeatureName("b.B.b"), "b.B.b");
    }

    @Test
    void testMatchClassName() {
        include.add("a.A");

        assertFalse(criteria.matchesPackageName("a"), "a");
        assertTrue(criteria.matchesClassName("a.A"), "a.A");
        assertFalse(criteria.matchesFeatureName("a.A.a"), "a.A.a");

        assertFalse(criteria.matchesPackageName("b"), "b");
        assertFalse(criteria.matchesClassName("b.B"), "b.B");
        assertFalse(criteria.matchesFeatureName("b.B.b"), "b.B.b");
    }

    @Test
    void testMatchFeatureName() {
        include.add("a.A.a");

        assertFalse(criteria.matchesPackageName("a"), "a");
        assertFalse(criteria.matchesClassName("a.A"), "a.A");
        assertTrue(criteria.matchesFeatureName("a.A.a"), "a.A.a");

        assertFalse(criteria.matchesPackageName("b"), "b");
        assertFalse(criteria.matchesClassName("b.B"), "b.B");
        assertFalse(criteria.matchesFeatureName("b.B.b"), "b.B.b");
    }

    @Test
    void testExclude() {
        include.add("a");
        include.add("a.A");
        include.add("a.A.a");
        exclude.add("a.A.a");

        assertTrue(criteria.matches(a), "a");
        assertTrue(criteria.matches(a_A), "a.A");
        assertFalse(criteria.matches(a_A_a), "a.A.a");
    }

    @Test
    void testExcludeOnly() {
        exclude.add("a.A.a");

        criteria = new CollectionSelectionCriteria(null, exclude);

        assertTrue(criteria.matches(a), "a");
        assertTrue(criteria.matches(a_A), "a.A");
        assertFalse(criteria.matches(a_A_a), "a.A.a");
    }
}
