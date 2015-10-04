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

public class TestSelectiveTraversalStrategy extends TestCase {
    private RegularExpressionSelectionCriteria scopeCriteria;
    private RegularExpressionSelectionCriteria filterCriteria;
    private SelectiveTraversalStrategy strategy;

    private PackageNode a;
    private ClassNode a_A;
    private FeatureNode a_A_a;
    
    private PackageNode b;
    private ClassNode b_B;
    private FeatureNode b_B_b;
    
    private PackageNode c;
    private ClassNode c_C;
    private FeatureNode c_C_c;

    private List<String> include;
    private List<String> exclude;

    protected void setUp() throws Exception {
        scopeCriteria = new RegularExpressionSelectionCriteria("//");
        filterCriteria = new RegularExpressionSelectionCriteria("//");
        strategy = new SelectiveTraversalStrategy(scopeCriteria, filterCriteria);

        NodeFactory factory = new NodeFactory();

        a     = factory.createPackage("a");
        a_A   = factory.createClass("a.A");
        a_A_a = factory.createFeature("a.A.a");
        
        b     = factory.createPackage("b");
        b_B   = factory.createClass("b.B");
        b_B_b = factory.createFeature("b.B.b");
        
        c     = factory.createPackage("c");
        c_C   = factory.createClass("c.C");
        c_C_c = factory.createFeature("c.C.c");
        
        include = Collections.singletonList("/^b/");
        exclude = Collections.singletonList("/^c/");
    }
    
    public void testScope() {
        scopeCriteria.setMatchingPackages(true);
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);

        assertTrue("a not in package scope",  strategy.isInScope(a));
        assertTrue("a.A in package scope",   !strategy.isInScope(a_A));
        assertTrue("a.A.a in package scope", !strategy.isInScope(a_A_a));
        assertTrue("b not in package scope",  strategy.isInScope(b));
        assertTrue("b.B in package scope",   !strategy.isInScope(b_B));
        assertTrue("b.B.b in package scope", !strategy.isInScope(b_B_b));
        assertTrue("c not in package scope",  strategy.isInScope(c));
        assertTrue("c.C in package scope",   !strategy.isInScope(c_C));
        assertTrue("c.C.c in package scope", !strategy.isInScope(c_C_c));

        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingClasses(true);
        scopeCriteria.setMatchingFeatures(false);

        assertTrue("a in package scope",       !strategy.isInScope(a));
        assertTrue("a.A not in package scope",  strategy.isInScope(a_A));
        assertTrue("a.A.a in package scope",   !strategy.isInScope(a_A_a));
        assertTrue("b not in package scope",   !strategy.isInScope(b));
        assertTrue("b.B in package scope",      strategy.isInScope(b_B));
        assertTrue("b.B.b in package scope",   !strategy.isInScope(b_B_b));
        assertTrue("c not in package scope",   !strategy.isInScope(c));
        assertTrue("c.C in package scope",      strategy.isInScope(c_C));
        assertTrue("c.C.c in package scope",   !strategy.isInScope(c_C_c));

        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(true);

        assertTrue("a in package scope",         !strategy.isInScope(a));
        assertTrue("a.A in package scope",       !strategy.isInScope(a_A));
        assertTrue("a.A.a not in package scope",  strategy.isInScope(a_A_a));
        assertTrue("b not in package scope",     !strategy.isInScope(b));
        assertTrue("b.B in package scope",       !strategy.isInScope(b_B));
        assertTrue("b.B.b in package scope",      strategy.isInScope(b_B_b));
        assertTrue("c not in package scope",     !strategy.isInScope(c));
        assertTrue("c.C in package scope",       !strategy.isInScope(c_C));
        assertTrue("c.C.c in package scope",      strategy.isInScope(c_C_c));
    }

    public void testScopeIncludes() {
        scopeCriteria.setGlobalIncludes(include);

        scopeCriteria.setMatchingPackages(true);
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);

        assertTrue("a in package scope",     !strategy.isInScope(a));
        assertTrue("a.A in package scope",   !strategy.isInScope(a_A));
        assertTrue("a.A.a in package scope", !strategy.isInScope(a_A_a));
        assertTrue("b not in package scope",  strategy.isInScope(b));
        assertTrue("b.B in package scope",   !strategy.isInScope(b_B));
        assertTrue("b.B.b in package scope", !strategy.isInScope(b_B_b));
        assertTrue("c in package scope",     !strategy.isInScope(c));
        assertTrue("c.C in package scope",   !strategy.isInScope(c_C));
        assertTrue("c.C.c in package scope", !strategy.isInScope(c_C_c));

        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingClasses(true);
        scopeCriteria.setMatchingFeatures(false);

        assertTrue("a in package scope",       !strategy.isInScope(a));
        assertTrue("a.A in package scope",     !strategy.isInScope(a_A));
        assertTrue("a.A.a in package scope",   !strategy.isInScope(a_A_a));
        assertTrue("b in package scope",       !strategy.isInScope(b));
        assertTrue("b.B not in package scope",  strategy.isInScope(b_B));
        assertTrue("b.B.b in package scope",   !strategy.isInScope(b_B_b));
        assertTrue("c in package scope",       !strategy.isInScope(c));
        assertTrue("c.C in package scope",     !strategy.isInScope(c_C));
        assertTrue("c.C.c in package scope",   !strategy.isInScope(c_C_c));

        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(true);

        assertTrue("a in package scope",         !strategy.isInScope(a));
        assertTrue("a.A in package scope",       !strategy.isInScope(a_A));
        assertTrue("a.A.a in package scope",     !strategy.isInScope(a_A_a));
        assertTrue("b in package scope",         !strategy.isInScope(b));
        assertTrue("b.B in package scope",       !strategy.isInScope(b_B));
        assertTrue("b.B.b not in package scope",  strategy.isInScope(b_B_b));
        assertTrue("c in package scope",         !strategy.isInScope(c));
        assertTrue("c.C in package scope",       !strategy.isInScope(c_C));
        assertTrue("c.C.c in package scope",     !strategy.isInScope(c_C_c));
    }

    public void testScopeExcludes() {
        scopeCriteria.setGlobalExcludes(exclude);

        scopeCriteria.setMatchingPackages(true);
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);

        assertTrue("a not in package scope",  strategy.isInScope(a));
        assertTrue("a.A in package scope",   !strategy.isInScope(a_A));
        assertTrue("a.A.a in package scope", !strategy.isInScope(a_A_a));
        assertTrue("b not in package scope",  strategy.isInScope(b));
        assertTrue("b.B in package scope",   !strategy.isInScope(b_B));
        assertTrue("b.B.b in package scope", !strategy.isInScope(b_B_b));
        assertTrue("c in package scope",     !strategy.isInScope(c));
        assertTrue("c.C in package scope",   !strategy.isInScope(c_C));
        assertTrue("c.C.c in package scope", !strategy.isInScope(c_C_c));

        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingClasses(true);
        scopeCriteria.setMatchingFeatures(false);

        assertTrue("a in package scope",       !strategy.isInScope(a));
        assertTrue("a.A not in package scope",  strategy.isInScope(a_A));
        assertTrue("a.A.a in package scope",   !strategy.isInScope(a_A_a));
        assertTrue("b in package scope",       !strategy.isInScope(b));
        assertTrue("b.B not in package scope",  strategy.isInScope(b_B));
        assertTrue("b.B.b in package scope",   !strategy.isInScope(b_B_b));
        assertTrue("c not in package scope",   !strategy.isInScope(c));
        assertTrue("c.C in package scope",     !strategy.isInScope(c_C));
        assertTrue("c.C.c in package scope",   !strategy.isInScope(c_C_c));

        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(true);

        assertTrue("a in package scope",         !strategy.isInScope(a));
        assertTrue("a.A in package scope",       !strategy.isInScope(a_A));
        assertTrue("a.A.a not in package scope",  strategy.isInScope(a_A_a));
        assertTrue("b in package scope",         !strategy.isInScope(b));
        assertTrue("b.B in package scope",       !strategy.isInScope(b_B));
        assertTrue("b.B.b not in package scope",  strategy.isInScope(b_B_b));
        assertTrue("c not in package scope",     !strategy.isInScope(c));
        assertTrue("c.C in package scope",       !strategy.isInScope(c_C));
        assertTrue("c.C.c in package scope",     !strategy.isInScope(c_C_c));
    }

    public void testFilter() {
        filterCriteria.setMatchingPackages(true);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);

        assertTrue("a not in package filter",  strategy.isInFilter(a));
        assertTrue("a.A in package filter",   !strategy.isInFilter(a_A));
        assertTrue("a.A.a in package filter", !strategy.isInFilter(a_A_a));
        assertTrue("b not in package filter",  strategy.isInFilter(b));
        assertTrue("b.B in package filter",   !strategy.isInFilter(b_B));
        assertTrue("b.B.b in package filter", !strategy.isInFilter(b_B_b));
        assertTrue("c not in package filter",  strategy.isInFilter(c));
        assertTrue("c.C in package filter",   !strategy.isInFilter(c_C));
        assertTrue("c.C.c in package filter", !strategy.isInFilter(c_C_c));

        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(true);
        filterCriteria.setMatchingFeatures(false);

        assertTrue("a in package filter",       !strategy.isInFilter(a));
        assertTrue("a.A not in package filter",  strategy.isInFilter(a_A));
        assertTrue("a.A.a in package filter",   !strategy.isInFilter(a_A_a));
        assertTrue("b not in package filter",   !strategy.isInFilter(b));
        assertTrue("b.B in package filter",      strategy.isInFilter(b_B));
        assertTrue("b.B.b in package filter",   !strategy.isInFilter(b_B_b));
        assertTrue("c not in package filter",   !strategy.isInFilter(c));
        assertTrue("c.C in package filter",      strategy.isInFilter(c_C));
        assertTrue("c.C.c in package filter",   !strategy.isInFilter(c_C_c));

        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(true);

        assertTrue("a in package filter",         !strategy.isInFilter(a));
        assertTrue("a.A in package filter",       !strategy.isInFilter(a_A));
        assertTrue("a.A.a not in package filter",  strategy.isInFilter(a_A_a));
        assertTrue("b not in package filter",     !strategy.isInFilter(b));
        assertTrue("b.B in package filter",       !strategy.isInFilter(b_B));
        assertTrue("b.B.b in package filter",      strategy.isInFilter(b_B_b));
        assertTrue("c not in package filter",     !strategy.isInFilter(c));
        assertTrue("c.C in package filter",       !strategy.isInFilter(c_C));
        assertTrue("c.C.c in package filter",      strategy.isInFilter(c_C_c));
    }

    public void testFilterIncludes() {
        filterCriteria.setGlobalIncludes(include);

        filterCriteria.setMatchingPackages(true);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);

        assertTrue("a in package filter",     !strategy.isInFilter(a));
        assertTrue("a.A in package filter",   !strategy.isInFilter(a_A));
        assertTrue("a.A.a in package filter", !strategy.isInFilter(a_A_a));
        assertTrue("b not in package filter",  strategy.isInFilter(b));
        assertTrue("b.B in package filter",   !strategy.isInFilter(b_B));
        assertTrue("b.B.b in package filter", !strategy.isInFilter(b_B_b));
        assertTrue("c in package filter",     !strategy.isInFilter(c));
        assertTrue("c.C in package filter",   !strategy.isInFilter(c_C));
        assertTrue("c.C.c in package filter", !strategy.isInFilter(c_C_c));

        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(true);
        filterCriteria.setMatchingFeatures(false);

        assertTrue("a in package filter",       !strategy.isInFilter(a));
        assertTrue("a.A in package filter",     !strategy.isInFilter(a_A));
        assertTrue("a.A.a in package filter",   !strategy.isInFilter(a_A_a));
        assertTrue("b in package filter",       !strategy.isInFilter(b));
        assertTrue("b.B not in package filter",  strategy.isInFilter(b_B));
        assertTrue("b.B.b in package filter",   !strategy.isInFilter(b_B_b));
        assertTrue("c in package filter",       !strategy.isInFilter(c));
        assertTrue("c.C in package filter",     !strategy.isInFilter(c_C));
        assertTrue("c.C.c in package filter",   !strategy.isInFilter(c_C_c));

        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(true);

        assertTrue("a in package filter",         !strategy.isInFilter(a));
        assertTrue("a.A in package filter",       !strategy.isInFilter(a_A));
        assertTrue("a.A.a in package filter",     !strategy.isInFilter(a_A_a));
        assertTrue("b in package filter",         !strategy.isInFilter(b));
        assertTrue("b.B in package filter",       !strategy.isInFilter(b_B));
        assertTrue("b.B.b not in package filter",  strategy.isInFilter(b_B_b));
        assertTrue("c in package filter",         !strategy.isInFilter(c));
        assertTrue("c.C in package filter",       !strategy.isInFilter(c_C));
        assertTrue("c.C.c in package filter",     !strategy.isInFilter(c_C_c));
    }

    public void testFilterExcludes() {
        filterCriteria.setGlobalExcludes(exclude);

        filterCriteria.setMatchingPackages(true);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);

        assertTrue("a not in package filter",  strategy.isInFilter(a));
        assertTrue("a.A in package filter",   !strategy.isInFilter(a_A));
        assertTrue("a.A.a in package filter", !strategy.isInFilter(a_A_a));
        assertTrue("b not in package filter",  strategy.isInFilter(b));
        assertTrue("b.B in package filter",   !strategy.isInFilter(b_B));
        assertTrue("b.B.b in package filter", !strategy.isInFilter(b_B_b));
        assertTrue("c in package filter",     !strategy.isInFilter(c));
        assertTrue("c.C in package filter",   !strategy.isInFilter(c_C));
        assertTrue("c.C.c in package filter", !strategy.isInFilter(c_C_c));

        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(true);
        filterCriteria.setMatchingFeatures(false);

        assertTrue("a in package filter",       !strategy.isInFilter(a));
        assertTrue("a.A not in package filter",  strategy.isInFilter(a_A));
        assertTrue("a.A.a in package filter",   !strategy.isInFilter(a_A_a));
        assertTrue("b in package filter",       !strategy.isInFilter(b));
        assertTrue("b.B not in package filter",  strategy.isInFilter(b_B));
        assertTrue("b.B.b in package filter",   !strategy.isInFilter(b_B_b));
        assertTrue("c in package filter",       !strategy.isInFilter(c));
        assertTrue("c.C in package filter",     !strategy.isInFilter(c_C));
        assertTrue("c.C.c in package filter",   !strategy.isInFilter(c_C_c));

        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(true);

        assertTrue("a in package filter",         !strategy.isInFilter(a));
        assertTrue("a.A in package filter",       !strategy.isInFilter(a_A));
        assertTrue("a.A.a not in package filter",  strategy.isInFilter(a_A_a));
        assertTrue("b in package filter",         !strategy.isInFilter(b));
        assertTrue("b.B in package filter",       !strategy.isInFilter(b_B));
        assertTrue("b.B.b not in package filter",  strategy.isInFilter(b_B_b));
        assertTrue("c in package filter",         !strategy.isInFilter(c));
        assertTrue("c.C in package filter",       !strategy.isInFilter(c_C));
        assertTrue("c.C.c in package filter",     !strategy.isInFilter(c_C_c));
    }
}
