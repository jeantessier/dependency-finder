/*
 *  Copyright (c) 2001-2005, Jean Tessier
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

public class TestNodeFactory extends TestCase {
    private NodeFactory factory;

    protected void setUp() throws Exception {
        super.setUp();

        Logger.getLogger(getClass()).debug("Begin " + getName());
        
        factory = new NodeFactory();
    }

    protected void tearDown() throws Exception {
        Logger.getLogger(getClass()).debug("End " + getName());

        super.tearDown();
    }

    public void testCreatePackage() {
        PackageNode node = factory.createPackage("a");

        assertEquals("name", "a", node.getName());
        assertEquals("classes", 0, node.getClasses().size());
        assertEquals("inbounds", 0, node.getInboundDependencies().size());
        assertEquals("outbounds", 0, node.getOutboundDependencies().size());
    }

    public void testLookupPackage() {
        Node node1 = factory.createPackage("a");
        Node node2 = factory.createPackage("a");

        assertSame("factory returned different object for same key", node1, node2);
    }

    public void testCreateClass() {
        ClassNode node = factory.createClass("a.A");

        assertEquals("name", "a.A", node.getName());
        assertEquals("package name", "a", node.getPackageNode().getName());
        assertEquals("features", 0, node.getFeatures().size());
        assertEquals("inbounds", 0, node.getInboundDependencies().size());
        assertEquals("outbounds", 0, node.getOutboundDependencies().size());
    }

    public void testCreateClassInDefaultPackage() {
        ClassNode node = factory.createClass("A");

        assertEquals("name", "A", node.getName());
        assertEquals("package name", "", node.getPackageNode().getName());
    }

    public void testCreateIllegalClass() {
        ClassNode node = factory.createClass("");

        assertEquals("name", "", node.getName());
        assertEquals("package name", "", node.getPackageNode().getName());
    }

    public void testLookupClass() {
        Node node1 = factory.createClass("a.A");
        Node node2 = factory.createClass("a.A");

        assertSame("factory returned different object for same key", node1, node2);
    }

    public void testCreateFeature() {
        FeatureNode node = factory.createFeature("a.A.a");

        assertEquals("name", "a.A.a", node.getName());
        assertEquals("class name", "a.A", node.getClassNode().getName());
        assertEquals("pacakge name", "a", node.getClassNode().getPackageNode().getName());
        assertEquals("inbounds", 0, node.getInboundDependencies().size());
        assertEquals("outbounds", 0, node.getOutboundDependencies().size());
    }

    public void testLookupFeature() {
        Node node1 = factory.createFeature("a.A.a");
        Node node2 = factory.createFeature("a.A.a");

        assertSame("factory returned different object for same key", node1, node2);
    }

    public void testCreateFeatureInDefaultPackage() {
        FeatureNode node = factory.createFeature("A.a");

        assertEquals("name", "A.a", node.getName());
        assertEquals("class name", "A", node.getClassNode().getName());
        assertEquals("package name", "", node.getClassNode().getPackageNode().getName());
    }

    public void testCreateIllegalFeature() {
        FeatureNode node = factory.createFeature("");

        assertEquals("name", "", node.getName());
        assertEquals("class name", "", node.getClassNode().getName());
        assertEquals("package name", "", node.getClassNode().getPackageNode().getName());
    }

    public void testCreateReferencedPackageNode() {
        PackageNode node = factory.createPackage("a", false);

        assertFalse("Not referenced", node.isConcrete());
    }

    public void testCreateConcretePackageNode() {
        PackageNode node = factory.createPackage("a", true);

        assertTrue("Not concrete", node.isConcrete());
    }

    public void testCreatePackageNodeDefaultsToReferenced() {
        PackageNode node = factory.createPackage("a");

        assertFalse("Not referenced", node.isConcrete());
    }

    public void testCreateReferencedClassNode() {
        ClassNode node = factory.createClass("a.A", false);

        assertFalse("Not referenced", node.isConcrete());
        assertFalse("Not referenced", node.getPackageNode().isConcrete());
    }

    public void testCreateConcreteClassNode() {
        ClassNode node = factory.createClass("a.A", true);

        assertTrue("Not concrete", node.isConcrete());
        assertTrue("Not concrete", node.getPackageNode().isConcrete());
    }

    public void testCreateClassNodeDefaultsToReferenced() {
        ClassNode node = factory.createClass("a.A");

        assertFalse("Not referenced", node.isConcrete());
        assertFalse("Not referenced", node.getPackageNode().isConcrete());
    }

    public void testCreateReferencedFeatureNode() {
        FeatureNode node = factory.createFeature("a.A.a", false);

        assertFalse("Not referenced", node.isConcrete());
        assertFalse("Not referenced", node.getClassNode().isConcrete());
        assertFalse("Not referenced", node.getClassNode().getPackageNode().isConcrete());
    }

    public void testCreateConcreteFeatureNode() {
        FeatureNode node = factory.createFeature("a.A.a", true);

        assertTrue("Not concrete", node.isConcrete());
        assertTrue("Not concrete", node.getClassNode().isConcrete());
        assertTrue("Not concrete", node.getClassNode().getPackageNode().isConcrete());
    }

    public void testCreateFeatureNodeDefaultsToReferenced() {
        FeatureNode node = factory.createFeature("a.A.a");

        assertFalse("Not referenced", node.isConcrete());
        assertFalse("Not referenced", node.getClassNode().isConcrete());
        assertFalse("Not referenced", node.getClassNode().getPackageNode().isConcrete());
    }

    public void testSwitchPackageNodeFromReferencedToConcrete() {
        PackageNode node;

        node = factory.createPackage("a", false);
        assertFalse("Not referenced", node.isConcrete());

        node = factory.createPackage("a", true);
        assertTrue("Not concrete", node.isConcrete());
    }

    public void testSwitchPackageNodeFromConcreteToReferenced() {
        PackageNode node;

        node = factory.createPackage("a", true);
        assertTrue("Not concrete", node.isConcrete());

        node = factory.createPackage("a", false);
        assertTrue("Not concrete", node.isConcrete());
    }

    public void testMakingPackageNodeConcreteDoesNotChangeItsClasses() {
        PackageNode node;

        node = factory.createPackage("a", false);
        factory.createClass("a.A", false);
        assertFalse("Not referenced", node.isConcrete());
        assertFalse("Not referenced", ((Node) node.getClasses().iterator().next()).isConcrete());

        node = factory.createPackage("a", true);
        assertTrue("Not concrete", node.isConcrete());
        assertFalse("Not referenced", ((Node) node.getClasses().iterator().next()).isConcrete());
    }

    public void testSwitchClassNodeFromReferencedToConcrete() {
        ClassNode node;

        node = factory.createClass("a.A", false);
        assertFalse("Not referenced", node.isConcrete());
        assertFalse("Not referenced", node.getPackageNode().isConcrete());

        node = factory.createClass("a.A", true);
        assertTrue("Not concrete", node.isConcrete());
        assertTrue("Not concrete", node.getPackageNode().isConcrete());
    }

    public void testSwitchClassNodeFromConcreteToReferenced() {
        ClassNode node;

        node = factory.createClass("a.A", true);
        assertTrue("Not concrete", node.isConcrete());
        assertTrue("Not concrete", node.getPackageNode().isConcrete());

        node = factory.createClass("a.A", false);
        assertTrue("Not concrete", node.isConcrete());
        assertTrue("Not concrete", node.getPackageNode().isConcrete());
    }

    public void testMakingClassNodeConcreteDoesNotChangeItsFeatures() {
        ClassNode node;

        node = factory.createClass("a.A", false);
        factory.createFeature("a.A.a", false);
        assertFalse("Not referenced", node.isConcrete());
        assertFalse("Not referenced", ((Node) node.getFeatures().iterator().next()).isConcrete());

        node = factory.createClass("a.A", true);
        assertTrue("Not concrete", node.isConcrete());
        assertFalse("Not referenced", ((Node) node.getFeatures().iterator().next()).isConcrete());
    }

    public void testSwitchFeatureNodeFromReferencedToConcrete() {
        FeatureNode node;

        node = factory.createFeature("a.A.a", false);
        assertFalse("Not referenced", node.isConcrete());
        assertFalse("Not referenced", node.getClassNode().isConcrete());
        assertFalse("Not referenced", node.getClassNode().getPackageNode().isConcrete());

        node = factory.createFeature("a.A.a", true);
        assertTrue("Not concrete", node.isConcrete());
        assertTrue("Not concrete", node.getClassNode().isConcrete());
        assertTrue("Not concrete", node.getClassNode().getPackageNode().isConcrete());
    }

    public void testSwitchFeatureNodeFromConcreteToReferenced() {
        FeatureNode node;

        node = factory.createFeature("a.A.a", true);
        assertTrue("Not concrete", node.isConcrete());
        assertTrue("Not concrete", node.getClassNode().isConcrete());
        assertTrue("Not concrete", node.getClassNode().getPackageNode().isConcrete());

        node = factory.createFeature("a.A.a", false);
        assertTrue("Not concrete", node.isConcrete());
        assertTrue("Not concrete", node.getClassNode().isConcrete());
        assertTrue("Not concrete", node.getClassNode().getPackageNode().isConcrete());
    }
}
