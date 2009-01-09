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

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.apache.oro.text.perl.*;

public class TestClosureStartSelector extends TestCase {
    private NodeFactory                        factory;
    private NodeFactory                        localFactory;
    private RegularExpressionSelectionCriteria localCriteria;

    private PackageNode a;
    private ClassNode   a_A;
    private FeatureNode a_A_a;
    
    private PackageNode b;
    private ClassNode   b_B;
    private FeatureNode b_B_b;
    
    private PackageNode c;
    private ClassNode   c_C;
    private FeatureNode c_C_c;

    protected void setUp() throws Exception {
        factory       = new NodeFactory();
        localFactory  = new NodeFactory();
        localCriteria = new RegularExpressionSelectionCriteria();

        a     = factory.createPackage("a");
        a_A   = factory.createClass("a.A");
        a_A_a = factory.createFeature("a.A.a");
        
        b     = factory.createPackage("b");
        b_B   = factory.createClass("b.B");
        b_B_b = factory.createFeature("b.B.b");
        
        c     = factory.createPackage("c");
        c_C   = factory.createClass("c.C");
        c_C_c = factory.createFeature("c.C.c");

        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);
    }

    public void testOneSelectedNode() {
        localCriteria.setGlobalIncludes("/b.B.b/");

        ClosureStartSelector selector = new ClosureStartSelector(localFactory, localCriteria);
        selector.traverseNodes(factory.getPackages().values());

        assertEquals("nodes in selection", 1, selector.getSelectedNodes().size());
        assertEquals("b.B.b in selection", b_B_b, selector.getSelectedNodes().iterator().next());
        assertSame("b.B.b in selection", b_B_b, selector.getSelectedNodes().iterator().next());
    }

    public void testOneCopiedNode() {
        localCriteria.setGlobalIncludes("/b.B.b/");

        ClosureStartSelector selector = new ClosureStartSelector(localFactory, localCriteria);
        selector.traverseNodes(factory.getPackages().values());

        assertEquals("packages in scope", 1, localFactory.getPackages().size());
        assertEquals("classes in scope" , 1, localFactory.getClasses().size());
        assertEquals("features in scope", 1, localFactory.getFeatures().size());

        assertEquals("package b in scope"    , b,     localFactory.getPackages().get("b"));
        assertEquals("class b.B in scope"    , b_B,   localFactory.getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, localFactory.getFeatures().get("b.B.b"));

        assertNotSame("package b in scope"    , b,     localFactory.getPackages().get("b"));
        assertNotSame("class b.B in scope"    , b_B,   localFactory.getClasses().get("b.B"));
        assertNotSame("feature b.B.b in scope", b_B_b, localFactory.getFeatures().get("b.B.b"));

        assertEquals("nodes in selection", 1, selector.getCopiedNodes().size());
        assertEquals("b.B.b in selection", b_B_b, selector.getCopiedNodes().iterator().next());
        assertNotSame("b.B.b in selection", b_B_b, selector.getCopiedNodes().iterator().next());
        assertSame("b.B.b in selection", localFactory.getFeatures().get("b.B.b"), selector.getCopiedNodes().iterator().next());
    }

    public void testMultipleSelectedNodes() {
        localCriteria.setGlobalIncludes("/a.A.a/, /^b/");

        ClosureStartSelector selector = new ClosureStartSelector(localFactory, localCriteria);
        selector.traverseNodes(factory.getPackages().values());

        assertEquals("nodes in selection", 4, selector.getSelectedNodes().size());
        assertTrue("a.A.a in selection", selector.getSelectedNodes().contains(a_A_a));
        assertTrue("b in selection",     selector.getSelectedNodes().contains(b));
        assertTrue("b.B in selection",   selector.getSelectedNodes().contains(b_B));
        assertTrue("b.B.b in selection", selector.getSelectedNodes().contains(b_B_b));
    }

    public void testMultipleCopiedNodes() {
        localCriteria.setGlobalIncludes("/a.A.a/, /^b/");

        ClosureStartSelector selector = new ClosureStartSelector(localFactory, localCriteria);
        selector.traverseNodes(factory.getPackages().values());

        assertEquals("packages in scope", 2, localFactory.getPackages().size());
        assertEquals("classes in scope" , 2, localFactory.getClasses().size());
        assertEquals("features in scope", 2, localFactory.getFeatures().size());

        assertEquals("package a in scope"    , a,     localFactory.getPackages().get("a"));
        assertEquals("class a.A in scope"    , a_A,   localFactory.getClasses().get("a.A"));
        assertEquals("feature a.A.a in scope", a_A_a, localFactory.getFeatures().get("a.A.a"));
        assertEquals("package b in scope"    , b,     localFactory.getPackages().get("b"));
        assertEquals("class b.B in scope"    , b_B,   localFactory.getClasses().get("b.B"));
        assertEquals("feature b.B.b in scope", b_B_b, localFactory.getFeatures().get("b.B.b"));

        assertNotSame("package a in scope"    , a,     localFactory.getPackages().get("a"));
        assertNotSame("class a.A in scope"    , a_A,   localFactory.getClasses().get("a.A"));
        assertNotSame("feature a.A.a in scope", a_A_a, localFactory.getFeatures().get("a.A.a"));
        assertNotSame("package b in scope"    , b,     localFactory.getPackages().get("b"));
        assertNotSame("class b.B in scope"    , b_B,   localFactory.getClasses().get("b.B"));
        assertNotSame("feature b.B.b in scope", b_B_b, localFactory.getFeatures().get("b.B.b"));

        assertEquals("nodes in selection", 4, selector.getCopiedNodes().size());
        assertTrue("a.A.a in selection", selector.getCopiedNodes().contains(a_A_a));
        assertTrue("b in selection",     selector.getCopiedNodes().contains(b));
        assertTrue("b.B in selection",   selector.getCopiedNodes().contains(b_B));
        assertTrue("b.B.b in selection", selector.getCopiedNodes().contains(b_B_b));
    }

    public void testVisitInferredPackage() {
        ClosureStartSelector selector = new ClosureStartSelector(localFactory, new ComprehensiveSelectionCriteria());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals("package.isConfirmed()", b.isConfirmed(), ((Node) localFactory.getPackages().get(b.getName())).isConfirmed());
    }

    public void testVisitConfirmedPackage() {
        b.setConfirmed(true);
        
        ClosureStartSelector selector = new ClosureStartSelector(localFactory, new ComprehensiveSelectionCriteria());
        selector.traverseNodes(Collections.singleton(b));

        assertEquals("package.isConfirmed()", b.isConfirmed(), ((Node) localFactory.getPackages().get(b.getName())).isConfirmed());
    }

    public void testVisitInferredClass() {
        ClosureStartSelector selector = new ClosureStartSelector(localFactory, new ComprehensiveSelectionCriteria());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals("class.isConfirmed()", b_B.isConfirmed(), ((Node) localFactory.getClasses().get(b_B.getName())).isConfirmed());
    }

    public void testVisitConfirmedClass() {
        b_B.setConfirmed(true);
        
        ClosureStartSelector selector = new ClosureStartSelector(localFactory, new ComprehensiveSelectionCriteria());
        selector.traverseNodes(Collections.singleton(b_B));

        assertEquals("class.isConfirmed()", b_B.isConfirmed(), ((Node) localFactory.getClasses().get(b_B.getName())).isConfirmed());
    }

    public void testVisitInferredFeature() {
        ClosureStartSelector selector = new ClosureStartSelector(localFactory, new ComprehensiveSelectionCriteria());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("feature.isConfirmed()", b_B_b.isConfirmed(), ((Node) localFactory.getFeatures().get(b_B_b.getName())).isConfirmed());
    }

    public void testVisitConfirmedFeature() {
        b_B_b.setConfirmed(true);
        
        ClosureStartSelector selector = new ClosureStartSelector(localFactory, new ComprehensiveSelectionCriteria());
        selector.traverseNodes(Collections.singleton(b_B_b));

        assertEquals("feature.isConfirmed()", b_B_b.isConfirmed(), ((Node) localFactory.getFeatures().get(b_B_b.getName())).isConfirmed());
    }
}
