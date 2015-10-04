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

public class TestCollectionSelectionCriteria extends TestCase {
    private Collection                  include;
    private Collection                  exclude;
    private CollectionSelectionCriteria criteria;

    private PackageNode a;
    private ClassNode   a_A;
    private FeatureNode a_A_a;
    
    private PackageNode b;
    private ClassNode   b_B;
    private FeatureNode b_B_b;

    protected void setUp() throws Exception {
        include  = new HashSet();
        exclude  = new HashSet();
        criteria = new CollectionSelectionCriteria(include, exclude);

        NodeFactory factory = new NodeFactory();

        a     = factory.createPackage("a");
        a_A   = factory.createClass("a.A");
        a_A_a = factory.createFeature("a.A.a");
        
        b     = factory.createPackage("b");
        b_B   = factory.createClass("b.B");
        b_B_b = factory.createFeature("b.B.b");
    }

    public void testEmptyInclude() {
        assertFalse("a",     criteria.matches(a));
        assertFalse("a.A",   criteria.matches(a_A));
        assertFalse("a.A.a", criteria.matches(a_A_a));

        assertFalse("b",     criteria.matches(b));
        assertFalse("b.B",   criteria.matches(b_B));
        assertFalse("b.B.b", criteria.matches(b_B_b));
    }

    public void testNullInclude() {
        criteria = new CollectionSelectionCriteria(null, exclude);

        assertTrue("a",     criteria.matches(a));
        assertTrue("a.A",   criteria.matches(a_A));
        assertTrue("a.A.a", criteria.matches(a_A_a));

        assertTrue("b",     criteria.matches(b));
        assertTrue("b.B",   criteria.matches(b_B));
        assertTrue("b.B.b", criteria.matches(b_B_b));
    }

    public void testMatchPackageNode() {
        include.add("a");

        assertTrue("a",      criteria.matches(a));
        assertFalse("a.A",   criteria.matches(a_A));
        assertFalse("a.A.a", criteria.matches(a_A_a));

        assertFalse("b",     criteria.matches(b));
        assertFalse("b.B",   criteria.matches(b_B));
        assertFalse("b.B.b", criteria.matches(b_B_b));
    }

    public void testMatchClassNode() {
        include.add("a.A");

        assertFalse("a",     criteria.matches(a));
        assertTrue("a.A",    criteria.matches(a_A));
        assertFalse("a.A.a", criteria.matches(a_A_a));

        assertFalse("b",     criteria.matches(b));
        assertFalse("b.B",   criteria.matches(b_B));
        assertFalse("b.B.b", criteria.matches(b_B_b));
    }

    public void testMatchFeatureNode() {
        include.add("a.A.a");

        assertFalse("a",     criteria.matches(a));
        assertFalse("a.A",   criteria.matches(a_A));
        assertTrue("a.A.a",  criteria.matches(a_A_a));

        assertFalse("b",     criteria.matches(b));
        assertFalse("b.B",   criteria.matches(b_B));
        assertFalse("b.B.b", criteria.matches(b_B_b));
    }

    public void testMatchPackageName() {
        include.add("a");

        assertTrue("a",      criteria.matchesPackageName("a"));
        assertFalse("a.A",   criteria.matchesClassName("a.A"));
        assertFalse("a.A.a", criteria.matchesFeatureName("a.A.a"));

        assertFalse("b",     criteria.matchesPackageName("b"));
        assertFalse("b.B",   criteria.matchesClassName("b.B"));
        assertFalse("b.B.b", criteria.matchesFeatureName("b.B.b"));
    }

    public void testMatchClassName() {
        include.add("a.A");

        assertFalse("a",     criteria.matchesPackageName("a"));
        assertTrue("a.A",    criteria.matchesClassName("a.A"));
        assertFalse("a.A.a", criteria.matchesFeatureName("a.A.a"));

        assertFalse("b",     criteria.matchesPackageName("b"));
        assertFalse("b.B",   criteria.matchesClassName("b.B"));
        assertFalse("b.B.b", criteria.matchesFeatureName("b.B.b"));
    }

    public void testMatchFeatureName() {
        include.add("a.A.a");

        assertFalse("a",     criteria.matchesPackageName("a"));
        assertFalse("a.A",   criteria.matchesClassName("a.A"));
        assertTrue("a.A.a",  criteria.matchesFeatureName("a.A.a"));

        assertFalse("b",     criteria.matchesPackageName("b"));
        assertFalse("b.B",   criteria.matchesClassName("b.B"));
        assertFalse("b.B.b", criteria.matchesFeatureName("b.B.b"));
    }

    public void testExclude() {
        include.add("a");
        include.add("a.A");
        include.add("a.A.a");
        exclude.add("a.A.a");

        assertTrue("a",      criteria.matches(a));
        assertTrue("a.A",    criteria.matches(a_A));
        assertFalse("a.A.a", criteria.matches(a_A_a));
    }

    public void testExcludeOnly() {
        exclude.add("a.A.a");

        criteria = new CollectionSelectionCriteria(null, exclude);

        assertTrue("a",      criteria.matches(a));
        assertTrue("a.A",    criteria.matches(a_A));
        assertFalse("a.A.a", criteria.matches(a_A_a));
    }
}
