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

import junit.framework.*;
import org.apache.log4j.*;

public class TestGraphSummarizer extends TestCase {
    private RegularExpressionSelectionCriteria scopeCriteria;
    private RegularExpressionSelectionCriteria filterCriteria;
    private NodeFactory                        factory;
    
    private Node a_package;
    private Node a_A_class;
    private Node a_A_a_method;
    private Node a_B_class;

    private Node b_package;
    private Node b_B_class;
    private Node b_B_b_method;

    private GraphSummarizer summarizer;

    protected void setUp() throws Exception {
        Logger.getLogger(getClass()).info("Starting test: " + getName());

        scopeCriteria  = new RegularExpressionSelectionCriteria("//");
        filterCriteria = new RegularExpressionSelectionCriteria("//");
        factory        = new NodeFactory();

        a_package = factory.createPackage("a");
        a_A_class = factory.createClass("a.A");
        a_A_a_method = factory.createFeature("a.A.a");
        a_B_class = factory.createClass("a.B");
        
        b_package = factory.createPackage("b");
        b_B_class = factory.createClass("b.B");
        b_B_b_method = factory.createFeature("b.B.b");
        
        summarizer = new GraphSummarizer(scopeCriteria, filterCriteria);
    }

    protected void tearDown() throws Exception {
        Logger.getLogger(getClass()).info("End of " + getName());
    }

    public void testP2PasP2P() {
        a_package.addDependency(b_package);
        
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);

        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("b"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().isEmpty());
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().keySet().isEmpty());

        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(1, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().contains(summarizer.getScopeFactory().createPackage("b")));
        assertEquals(1, summarizer.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createPackage("b").getInboundDependencies().contains(summarizer.getScopeFactory().createPackage("a")));
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getOutboundDependencies().size());
    }

    public void testP2PasC2C() {
        a_package.addDependency(b_package);
        
        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingFeatures(false);
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingFeatures(false);

        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("b"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("a.A"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("b.B"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().keySet().isEmpty());

        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createClass("a.A").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("a.A").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("b.B").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("b.B").getOutboundDependencies().size());
    }

    public void testP2PasF2F() {
        a_package.addDependency(b_package);
        
        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(false);

        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("b"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("a.A"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("b.B"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().keySet().contains("a.A.a"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().keySet().contains("b.B.b"));

        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createClass("a.A").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("a.A").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("b.B").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("b.B").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createFeature("a.A.a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createFeature("a.A.a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createFeature("b.B.b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createFeature("b.B.b").getOutboundDependencies().size());
    }

    public void testC2CasP2P() {
        a_A_class.addDependency(b_B_class);
        
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);

        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("b"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().isEmpty());
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().isEmpty());

        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(1, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().contains(summarizer.getScopeFactory().createPackage("b")));
        assertEquals(1, summarizer.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createPackage("b").getInboundDependencies().contains(summarizer.getScopeFactory().createPackage("a")));
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getOutboundDependencies().size());
    }

    public void testC2CasP2CSamePackage() {
        a_A_class.addDependency(a_B_class);
        
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingFeatures(false);

        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("b"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().isEmpty());
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().isEmpty());

        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getOutboundDependencies().size());
    }

    public void testC2CasC2C() {
        a_A_class.addDependency(b_B_class);
        
        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingFeatures(false);
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingFeatures(false);

        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("b"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("a.A"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("b.B"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().isEmpty());

        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createClass("a.A").getInboundDependencies().size());
        assertEquals(1, summarizer.getScopeFactory().createClass("a.A").getOutboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createClass("a.A").getOutboundDependencies().contains(summarizer.getScopeFactory().createClass("b.B")));
        assertEquals(1, summarizer.getScopeFactory().createClass("b.B").getInboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createClass("b.B").getInboundDependencies().contains(summarizer.getScopeFactory().createClass("a.A")));
        assertEquals(0, summarizer.getScopeFactory().createClass("b.B").getOutboundDependencies().size());
    }

    public void testC2CasF2F() {
        a_A_class.addDependency(b_B_class);
        
        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(false);

        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("b"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("a.A"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("b.B"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().keySet().contains("a.A.a"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().keySet().contains("b.B.b"));

        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createClass("a.A").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("a.A").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("b.B").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("b.B").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createFeature("a.A.a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createFeature("a.A.a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createFeature("b.B.b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createFeature("b.B.b").getOutboundDependencies().size());
    }

    public void testF2FasP2P() {
        a_A_a_method.addDependency(b_B_b_method);
        
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);

        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("b"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().isEmpty());
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().isEmpty());

        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(1, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().contains(summarizer.getScopeFactory().createPackage("b")));
        assertEquals(1, summarizer.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createPackage("b").getInboundDependencies().contains(summarizer.getScopeFactory().createPackage("a")));
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getOutboundDependencies().size());
    }

    public void testF2FasC2C() {
        a_A_a_method.addDependency(b_B_b_method);
        
        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingFeatures(false);
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingFeatures(false);

        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("b"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("a.A"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("b.B"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().isEmpty());

        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createClass("a.A").getInboundDependencies().size());
        assertEquals(1, summarizer.getScopeFactory().createClass("a.A").getOutboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createClass("a.A").getOutboundDependencies().contains(summarizer.getScopeFactory().createClass("b.B")));
        assertEquals(1, summarizer.getScopeFactory().createClass("b.B").getInboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createClass("b.B").getInboundDependencies().contains(summarizer.getScopeFactory().createClass("a.A")));
        assertEquals(0, summarizer.getScopeFactory().createClass("b.B").getOutboundDependencies().size());
    }

    public void testF2FasF2F() {
        a_A_a_method.addDependency(b_B_b_method);
        
        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(false);

        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("b"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("a.A"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("b.B"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().keySet().contains("a.A.a"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().keySet().contains("b.B.b"));

        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createClass("a.A").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("a.A").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("b.B").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("b.B").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createFeature("a.A.a").getInboundDependencies().size());
        assertEquals(1, summarizer.getScopeFactory().createFeature("a.A.a").getOutboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createFeature("a.A.a").getOutboundDependencies().contains(summarizer.getScopeFactory().createFeature("b.B.b")));
        assertEquals(1, summarizer.getScopeFactory().createFeature("b.B.b").getInboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createFeature("b.B.b").getInboundDependencies().contains(summarizer.getScopeFactory().createFeature("a.A.a")));
        assertEquals(0, summarizer.getScopeFactory().createFeature("b.B.b").getOutboundDependencies().size());
    }

    public void testF2CasP2P() {
        a_A_a_method.addDependency(b_B_class);
        
        scopeCriteria.setMatchingClasses(false);
        scopeCriteria.setMatchingFeatures(false);
        filterCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingFeatures(false);

        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("b"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().isEmpty());
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().isEmpty());

        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(1, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().contains(summarizer.getScopeFactory().createPackage("b")));
        assertEquals(1, summarizer.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createPackage("b").getInboundDependencies().contains(summarizer.getScopeFactory().createPackage("a")));
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getOutboundDependencies().size());
    }

    public void testF2CasC2C() {
        a_A_a_method.addDependency(b_B_class);
        
        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingFeatures(false);
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingFeatures(false);

        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("b"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("a.A"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("b.B"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().isEmpty());

        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createClass("a.A").getInboundDependencies().size());
        assertEquals(1, summarizer.getScopeFactory().createClass("a.A").getOutboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createClass("a.A").getOutboundDependencies().contains(summarizer.getScopeFactory().createClass("b.B")));
        assertEquals(1, summarizer.getScopeFactory().createClass("b.B").getInboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createClass("b.B").getInboundDependencies().contains(summarizer.getScopeFactory().createClass("a.A")));
        assertEquals(0, summarizer.getScopeFactory().createClass("b.B").getOutboundDependencies().size());
    }

    public void testF2CasF2F() {
        a_A_a_method.addDependency(b_B_class);
        
        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingClasses(false);
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(false);

        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("b"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("a.A"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("b.B"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().keySet().contains("a.A.a"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().keySet().contains("b.B.b"));

        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createClass("a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("b").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createFeature("a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createFeature("a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createFeature("b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createFeature("b").getOutboundDependencies().size());
    }

    public void testF2FasPCF2PCF() {
        a_A_a_method.addDependency(b_B_b_method);
        
        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("b"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("a.A"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("b.B"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().keySet().contains("a.A.a"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().keySet().contains("b.B.b"));

        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createClass("a.A").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("a.A").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("b.B").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("b.B").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createFeature("a.A.a").getInboundDependencies().size());
        assertEquals(1, summarizer.getScopeFactory().createFeature("a.A.a").getOutboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createFeature("a.A.a").getOutboundDependencies().contains(summarizer.getScopeFactory().createFeature("b.B.b")));
        assertEquals(1, summarizer.getScopeFactory().createFeature("b.B.b").getInboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createFeature("b.B.b").getInboundDependencies().contains(summarizer.getScopeFactory().createFeature("a.A.a")));
        assertEquals(0, summarizer.getScopeFactory().createFeature("b.B.b").getOutboundDependencies().size());
    }

    public void testC2CasPCF2PCF() {
        a_A_class.addDependency(b_B_class);
        
        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("b"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("a.A"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("b.B"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().keySet().contains("a.A.a"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().keySet().contains("b.B.b"));

        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createClass("a.A").getInboundDependencies().size());
        assertEquals(1, summarizer.getScopeFactory().createClass("a.A").getOutboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createClass("a.A").getOutboundDependencies().contains(summarizer.getScopeFactory().createClass("b.B")));
        assertEquals(1, summarizer.getScopeFactory().createClass("b.B").getInboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createClass("b.B").getInboundDependencies().contains(summarizer.getScopeFactory().createClass("a.A")));
        assertEquals(0, summarizer.getScopeFactory().createClass("b.B").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createFeature("a.A.a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createFeature("a.A.a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createFeature("b.B.b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createFeature("b.B.b").getOutboundDependencies().size());
    }

    public void testP2PasPCF2PCF() {
        a_package.addDependency(b_package);
        
        summarizer.traverseNodes(factory.getPackages().values());

        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("a"));
        assertTrue(summarizer.getScopeFactory().getPackages().keySet().toString(), summarizer.getScopeFactory().getPackages().keySet().contains("b"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("a.A"));
        assertTrue(summarizer.getScopeFactory().getClasses().keySet().toString(), summarizer.getScopeFactory().getClasses().keySet().contains("b.B"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().keySet().contains("a.A.a"));
        assertTrue(summarizer.getScopeFactory().getFeatures().keySet().toString(), summarizer.getScopeFactory().getFeatures().keySet().contains("b.B.b"));

        assertEquals(0, summarizer.getScopeFactory().createPackage("a").getInboundDependencies().size());
        assertEquals(1, summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createPackage("a").getOutboundDependencies().contains(summarizer.getScopeFactory().createPackage("b")));
        assertEquals(1, summarizer.getScopeFactory().createPackage("b").getInboundDependencies().size());
        assertTrue(summarizer.getScopeFactory().createPackage("b").getInboundDependencies().contains(summarizer.getScopeFactory().createPackage("a")));
        assertEquals(0, summarizer.getScopeFactory().createPackage("b").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createClass("a.A").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("a.A").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("b.B").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createClass("b.B").getOutboundDependencies().size());

        assertEquals(0, summarizer.getScopeFactory().createFeature("a.A.a").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createFeature("a.A.a").getOutboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createFeature("b.B.b").getInboundDependencies().size());
        assertEquals(0, summarizer.getScopeFactory().createFeature("b.B.b").getOutboundDependencies().size());
    }
}
