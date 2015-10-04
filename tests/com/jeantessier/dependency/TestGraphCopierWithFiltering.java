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

public class TestGraphCopierWithFiltering extends TestCase {
    private RegularExpressionSelectionCriteria scopeCriteria;
    private RegularExpressionSelectionCriteria filterCriteria;
    private NodeFactory                        factory;
    
    private Node a_A_a;
    private Node b_B_b;
    private Node c_C_c;

    private List<String> includeFilter;
    private List<String> excludeFilter;

    private GraphCopier copier;

    protected void setUp() throws Exception {
        scopeCriteria  = new RegularExpressionSelectionCriteria();
        filterCriteria = new RegularExpressionSelectionCriteria();
        factory        = new NodeFactory();

        a_A_a = factory.createFeature("a.A.a");
        b_B_b = factory.createFeature("b.B.b");
        c_C_c = factory.createFeature("c.C.c");
        
        includeFilter = new LinkedList<String>();
        includeFilter.add("/^b/");
        
        excludeFilter = new LinkedList<String>();
        excludeFilter.add("/^c/");

        copier = new GraphCopier(new SelectiveTraversalStrategy(scopeCriteria, filterCriteria));
    }

    public void testIncludeFilterF2FtoP2P() {
        a_A_a.addDependency(b_B_b);
        a_A_a.addDependency(c_C_c);
        
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);
        filterCriteria.setGlobalIncludes(includeFilter);
        
        copier.traverseNodes(factory.getPackages().values());

        assertTrue(copier.getScopeFactory().createPackage("a").getInboundDependencies().isEmpty());
        assertTrue(copier.getScopeFactory().createPackage("a").getOutboundDependencies().isEmpty());
    }

    public void testExcludeFilterF2FtoP2P() {
        a_A_a.addDependency(b_B_b);
        a_A_a.addDependency(c_C_c);
        
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);
        filterCriteria.setGlobalExcludes(excludeFilter);
        
        copier.traverseNodes(factory.getPackages().values());

        assertTrue(copier.getScopeFactory().createPackage("a").getInboundDependencies().isEmpty());
        assertTrue(copier.getScopeFactory().createPackage("a").getOutboundDependencies().isEmpty());
    }
}
