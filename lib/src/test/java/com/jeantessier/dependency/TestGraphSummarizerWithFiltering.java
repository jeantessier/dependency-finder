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

public class TestGraphSummarizerWithFiltering {
    private final RegularExpressionSelectionCriteria scopeCriteria = new RegularExpressionSelectionCriteria("//");
    private final RegularExpressionSelectionCriteria filterCriteria = new RegularExpressionSelectionCriteria("//");
    private final NodeFactory factory = new NodeFactory();
    
    private final Node a_A_a = factory.createFeature("a.A.a");
    private final Node b = factory.createPackage("b");
    private final Node b_B_b = factory.createFeature("b.B.b");
    private final Node c_C_c = factory.createFeature("c.C.c");

    private final List<String> includeScope = List.of("/^a/");
    private final List<String> includeFilter = List.of("/^b/");
    private final List<String> excludeFilter = List.of("/^c/");

    private final GraphCopier copier = new GraphSummarizer(scopeCriteria, filterCriteria);

    @Test
    void testIncludeFilterF2FtoP2P() {
        a_A_a.addDependency(b_B_b);
        a_A_a.addDependency(c_C_c);
        
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);
        filterCriteria.setGlobalIncludes(includeFilter);
        
        copier.traverseNodes(factory.getPackages().values());

        assertTrue(copier.getScopeFactory().createPackage("a").getInboundDependencies().isEmpty());
        assertEquals(1, copier.getScopeFactory().createPackage("a").getOutboundDependencies().size(), copier.getScopeFactory().createPackage("a").getOutboundDependencies().toString());
        assertTrue(copier.getScopeFactory().createPackage("a").getOutboundDependencies().contains(b));
    }

    @Test
    void testExcludeFilterF2FtoP2P() {
        a_A_a.addDependency(b_B_b);
        a_A_a.addDependency(c_C_c);
        
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);
        scopeCriteria.setGlobalIncludes(includeScope);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);
        filterCriteria.setGlobalExcludes(excludeFilter);

        assertFalse(filterCriteria.matchesFeatureName(c_C_c.getName()));
        
        copier.traverseNodes(factory.getPackages().values());

        assertTrue(copier.getScopeFactory().createPackage("a").getInboundDependencies().isEmpty());
        assertEquals(1, copier.getScopeFactory().createPackage("a").getOutboundDependencies().size(), copier.getScopeFactory().createPackage("a").getOutboundDependencies().toString());
        assertTrue(copier.getScopeFactory().createPackage("a").getOutboundDependencies().contains(b));
    }
}
