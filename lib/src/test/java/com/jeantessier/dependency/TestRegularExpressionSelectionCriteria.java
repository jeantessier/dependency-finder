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

public class TestRegularExpressionSelectionCriteria {
    private final NodeFactory factory = new NodeFactory();

    private final PackageNode a = factory.createPackage("a");
    private final ClassNode a_A = factory.createClass("a.A");
    private final FeatureNode a_A_a = factory.createFeature("a.A.a");

    private final PackageNode b = factory.createPackage("b");
    private final ClassNode b_B = factory.createClass("b.B");
    private final FeatureNode b_B_b = factory.createFeature("b.B.b");

    private final PackageNode c = factory.createPackage("c");
    private final ClassNode c_C = factory.createClass("c.C");
    private final FeatureNode c_C_c = factory.createFeature("c.C.c");

    private final List<String> include = List.of("/^b/");
    private final List<String> exclude = List.of("/^c/");

    private final RegularExpressionSelectionCriteria criteria = new RegularExpressionSelectionCriteria();

    @Test
    void testMatch() {
        criteria.setGlobalIncludes("//");
        criteria.setMatchingPackages(true);
        criteria.setMatchingClasses(false);
        criteria.setMatchingFeatures(false);

        assertTrue(criteria.matches(a), "a not in package scope");
        assertFalse(criteria.matches(a_A), "a.A in package scope");
        assertFalse(criteria.matches(a_A_a), "a.A.a in package scope");
        assertTrue(criteria.matches(b), "b not in package scope");
        assertFalse(criteria.matches(b_B), "b.B in package scope");
        assertFalse(criteria.matches(b_B_b), "b.B.b in package scope");
        assertTrue(criteria.matches(c), "c not in package scope");
        assertFalse(criteria.matches(c_C), "c.C in package scope");
        assertFalse(criteria.matches(c_C_c), "c.C.c in package scope");

        criteria.setMatchingPackages(false);
        criteria.setMatchingClasses(true);
        criteria.setMatchingFeatures(false);

        assertFalse(criteria.matches(a), "a in package scope");
        assertTrue(criteria.matches(a_A), "a.A not in package scope");
        assertFalse(criteria.matches(a_A_a), "a.A.a in package scope");
        assertFalse(criteria.matches(b), "b not in package scope");
        assertTrue(criteria.matches(b_B), "b.B in package scope");
        assertFalse(criteria.matches(b_B_b), "b.B.b in package scope");
        assertFalse(criteria.matches(c), "c not in package scope");
        assertTrue(criteria.matches(c_C), "c.C in package scope");
        assertFalse(criteria.matches(c_C_c), "c.C.c in package scope");

        criteria.setMatchingPackages(false);
        criteria.setMatchingClasses(false);
        criteria.setMatchingFeatures(true);

        assertFalse(criteria.matches(a), "a in package scope");
        assertFalse(criteria.matches(a_A), "a.A in package scope");
        assertTrue(criteria.matches(a_A_a), "a.A.a not in package scope");
        assertFalse(criteria.matches(b), "b not in package scope");
        assertFalse(criteria.matches(b_B), "b.B in package scope");
        assertTrue(criteria.matches(b_B_b), "b.B.b in package scope");
        assertFalse(criteria.matches(c), "c not in package scope");
        assertFalse(criteria.matches(c_C), "c.C in package scope");
        assertTrue(criteria.matches(c_C_c), "c.C.c in package scope");
    }

    @Test
    void testGlobalIncludes() {
        criteria.setGlobalIncludes(include);

        criteria.setMatchingPackages(true);
        criteria.setMatchingClasses(false);
        criteria.setMatchingFeatures(false);

        assertFalse(criteria.matches(a), "a in package scope");
        assertFalse(criteria.matches(a_A), "a.A in package scope");
        assertFalse(criteria.matches(a_A_a), "a.A.a in package scope");
        assertTrue(criteria.matches(b), "b not in package scope");
        assertFalse(criteria.matches(b_B), "b.B in package scope");
        assertFalse(criteria.matches(b_B_b), "b.B.b in package scope");
        assertFalse(criteria.matches(c), "c in package scope");
        assertFalse(criteria.matches(c_C), "c.C in package scope");
        assertFalse(criteria.matches(c_C_c), "c.C.c in package scope");

        criteria.setMatchingPackages(false);
        criteria.setMatchingClasses(true);
        criteria.setMatchingFeatures(false);

        assertFalse(criteria.matches(a), "a in package scope");
        assertFalse(criteria.matches(a_A), "a.A in package scope");
        assertFalse(criteria.matches(a_A_a), "a.A.a in package scope");
        assertFalse(criteria.matches(b), "b in package scope");
        assertTrue(criteria.matches(b_B), "b.B not in package scope");
        assertFalse(criteria.matches(b_B_b), "b.B.b in package scope");
        assertFalse(criteria.matches(c), "c in package scope");
        assertFalse(criteria.matches(c_C), "c.C in package scope");
        assertFalse(criteria.matches(c_C_c), "c.C.c in package scope");

        criteria.setMatchingPackages(false);
        criteria.setMatchingClasses(false);
        criteria.setMatchingFeatures(true);

        assertFalse(criteria.matches(a), "a in package scope");
        assertFalse(criteria.matches(a_A), "a.A in package scope");
        assertFalse(criteria.matches(a_A_a), "a.A.a in package scope");
        assertFalse(criteria.matches(b), "b in package scope");
        assertFalse(criteria.matches(b_B), "b.B in package scope");
        assertTrue(criteria.matches(b_B_b), "b.B.b not in package scope");
        assertFalse(criteria.matches(c), "c in package scope");
        assertFalse(criteria.matches(c_C), "c.C in package scope");
        assertFalse(criteria.matches(c_C_c), "c.C.c in package scope");
    }

    @Test
    void testGlobalExcludes() {
        criteria.setGlobalIncludes("//");
        criteria.setGlobalExcludes(exclude);

        criteria.setMatchingPackages(true);
        criteria.setMatchingClasses(false);
        criteria.setMatchingFeatures(false);

        assertTrue(criteria.matches(a), "a not in package scope");
        assertFalse(criteria.matches(a_A), "a.A in package scope");
        assertFalse(criteria.matches(a_A_a), "a.A.a in package scope");
        assertTrue(criteria.matches(b), "b not in package scope");
        assertFalse(criteria.matches(b_B), "b.B in package scope");
        assertFalse(criteria.matches(b_B_b), "b.B.b in package scope");
        assertFalse(criteria.matches(c), "c in package scope");
        assertFalse(criteria.matches(c_C), "c.C in package scope");
        assertFalse(criteria.matches(c_C_c), "c.C.c in package scope");

        criteria.setMatchingPackages(false);
        criteria.setMatchingClasses(true);
        criteria.setMatchingFeatures(false);

        assertFalse(criteria.matches(a), "a in package scope");
        assertTrue(criteria.matches(a_A), "a.A not in package scope");
        assertFalse(criteria.matches(a_A_a), "a.A.a in package scope");
        assertFalse(criteria.matches(b), "b in package scope");
        assertTrue(criteria.matches(b_B), "b.B not in package scope");
        assertFalse(criteria.matches(b_B_b), "b.B.b in package scope");
        assertFalse(criteria.matches(c), "c not in package scope");
        assertFalse(criteria.matches(c_C), "c.C in package scope");
        assertFalse(criteria.matches(c_C_c), "c.C.c in package scope");

        criteria.setMatchingPackages(false);
        criteria.setMatchingClasses(false);
        criteria.setMatchingFeatures(true);

        assertFalse(criteria.matches(a), "a in package scope");
        assertFalse(criteria.matches(a_A), "a.A in package scope");
        assertTrue(criteria.matches(a_A_a), "a.A.a not in package scope");
        assertFalse(criteria.matches(b), "b in package scope");
        assertFalse(criteria.matches(b_B), "b.B in package scope");
        assertTrue(criteria.matches(b_B_b), "b.B.b not in package scope");
        assertFalse(criteria.matches(c), "c not in package scope");
        assertFalse(criteria.matches(c_C), "c.C in package scope");
        assertFalse(criteria.matches(c_C_c), "c.C.c in package scope");
    }
}
