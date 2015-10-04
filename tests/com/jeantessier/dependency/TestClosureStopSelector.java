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

public class TestClosureStopSelector extends TestCase {
    private PackageNode a;
    private ClassNode a_A;
    private FeatureNode a_A_a;
    
    private PackageNode b;
    private ClassNode b_B;
    private FeatureNode b_B_b;
    
    private PackageNode c;
    private ClassNode c_C;
    private FeatureNode c_C_c;

    protected void setUp() throws Exception {
        super.setUp();
        
        NodeFactory factory = new NodeFactory();

        a = factory.createPackage("a");
        a_A = factory.createClass("a.A");
        a_A_a = factory.createFeature("a.A.a");
        
        b = factory.createPackage("b");
        b_B = factory.createClass("b.B");
        b_B_b = factory.createFeature("b.B.b");
        
        c = factory.createPackage("c");
        c_C = factory.createClass("c.C");
        c_C_c = factory.createFeature("c.C.c");

        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);
    }

    public void testEmpty() {
        RegularExpressionSelectionCriteria localCriteria = new RegularExpressionSelectionCriteria();
        localCriteria.setGlobalIncludes("/b.B.b/");

        ClosureStopSelector selector = new ClosureStopSelector(localCriteria);
        selector.traverseNodes(Collections.<Node>emptySet());

        assertTrue("Failed to recognize empty collection", selector.isDone());
    }

    public void testPositive() {
        RegularExpressionSelectionCriteria localCriteria = new RegularExpressionSelectionCriteria();
        localCriteria.setGlobalIncludes("/b.B.b/");

        ClosureStopSelector selector = new ClosureStopSelector(localCriteria);
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertTrue("Failed to recognize target", selector.isDone());
    }

    public void testNegative() {
        RegularExpressionSelectionCriteria localCriteria = new RegularExpressionSelectionCriteria();
        localCriteria.setGlobalIncludes("/b.B.b/");

        ClosureStopSelector selector = new ClosureStopSelector(localCriteria);
        selector.traverseNodes(Collections.singleton(a_A_a));

        assertFalse("Failed to ignore non-target", selector.isDone());
    }

    public void testMultiple() {
        RegularExpressionSelectionCriteria localCriteria = new RegularExpressionSelectionCriteria();
        localCriteria.setGlobalIncludes("/b.B.b/");

        Collection<Node> targets = new ArrayList<Node>();
        targets.add(a_A_a);
        targets.add(b_B_b);
        
        ClosureStopSelector selector = new ClosureStopSelector(localCriteria);
        selector.traverseNodes(targets);

        assertTrue("Failed to recognize target", selector.isDone());
    }
}
