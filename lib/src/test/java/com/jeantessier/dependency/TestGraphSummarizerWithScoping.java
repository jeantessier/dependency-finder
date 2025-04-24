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

public class TestGraphSummarizerWithScoping {
    private final NodeFactory factory = new NodeFactory();

    private Node b;

    private GraphSummarizer summarizer;

    @BeforeEach
    void setUp() {
        Node a_A_a = factory.createFeature("a.A.a");
        Node a_A_b = factory.createFeature("a.A.b");

        b = factory.createPackage("b");
        Node b_B_b = factory.createFeature("b.B.b");

        a_A_a.addDependency(a_A_b);
        a_A_a.addDependency(b_B_b);

        var scopeCriteria = new RegularExpressionSelectionCriteria("//");
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);
        scopeCriteria.setGlobalIncludes(List.of("/^a/"));

        var filterCriteria = new RegularExpressionSelectionCriteria("//");
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);

        summarizer = new GraphSummarizer(scopeCriteria, filterCriteria);
    }

    @Test
    void testIncludeF2F() {
        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().createPackage("a").getInboundDependencies().isEmpty());
        assertEquals(1, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size(), summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().toString());
        assertTrue(summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().contains(b));
    }
}
