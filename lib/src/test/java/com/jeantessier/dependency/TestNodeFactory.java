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

import static org.junit.jupiter.api.Assertions.*;

public class TestNodeFactory {
    private final NodeFactory factory = new NodeFactory();

    @Test
    void testCreatePackage() {
        PackageNode node = factory.createPackage("a");

        assertEquals("a", node.getName(), "name");
        assertEquals(0, node.getClasses().size(), "classes");
        assertEquals(0, node.getInboundDependencies().size(), "inbounds");
        assertEquals(0, node.getOutboundDependencies().size(), "outbounds");
    }

    @Test
    void testLookupPackage() {
        Node node1 = factory.createPackage("a");
        Node node2 = factory.createPackage("a");

        assertSame(node1, node2, "factory returned different object for same key");
    }

    @Test
    void testCreateClass() {
        ClassNode node = factory.createClass("a.A");

        assertEquals("a.A", node.getName(), "name");
        assertEquals("a", node.getPackageNode().getName(), "package name");
        assertEquals(0, node.getFeatures().size(), "features");
        assertEquals(0, node.getInboundDependencies().size(), "inbounds");
        assertEquals(0, node.getOutboundDependencies().size(), "outbounds");
    }

    @Test
    void testCreateClassInDefaultPackage() {
        ClassNode node = factory.createClass("A");

        assertEquals("A", node.getName(), "name");
        assertEquals("", node.getPackageNode().getName(), "package name");
    }

    @Test
    void testCreateIllegalClass() {
        ClassNode node = factory.createClass("");

        assertEquals("", node.getName(), "name");
        assertEquals("", node.getPackageNode().getName(), "package name");
    }

    @Test
    void testLookupClass() {
        Node node1 = factory.createClass("a.A");
        Node node2 = factory.createClass("a.A");

        assertSame(node1, node2, "factory returned different object for same key");
    }

    @Test
    void testCreateFeature() {
        FeatureNode node = factory.createFeature("a.A.a");

        assertEquals("a.A.a", node.getName(), "name");
        assertEquals("a.A", node.getClassNode().getName(), "class name");
        assertEquals("a", node.getClassNode().getPackageNode().getName(), "package name");
        assertEquals(0, node.getInboundDependencies().size(), "inbounds");
        assertEquals(0, node.getOutboundDependencies().size(), "outbounds");
    }

    @Test
    void testLookupFeature() {
        Node node1 = factory.createFeature("a.A.a");
        Node node2 = factory.createFeature("a.A.a");

        assertSame(node1, node2, "factory returned different object for same key");
    }

    @Test
    void testCreateFeatureInDefaultPackage() {
        FeatureNode node = factory.createFeature("A.a");

        assertEquals("A.a", node.getName(), "name");
        assertEquals("A", node.getClassNode().getName(), "class name");
        assertEquals("", node.getClassNode().getPackageNode().getName(), "package name");
    }

    @Test
    void testCreateIllegalFeature() {
        FeatureNode node = factory.createFeature("");

        assertEquals("", node.getName(), "name");
        assertEquals("", node.getClassNode().getName(), "class name");
        assertEquals("", node.getClassNode().getPackageNode().getName(), "package name");
    }

    @Test
    void testCreateReferencedPackageNode() {
        PackageNode node = factory.createPackage("a", false);

        assertFalse(node.isConfirmed(), "Not referenced");
    }

    @Test
    void testCreateConcretePackageNode() {
        PackageNode node = factory.createPackage("a", true);

        assertTrue(node.isConfirmed(), "Not concrete");
    }

    @Test
    void testCreatePackageNodeDefaultsToReferenced() {
        PackageNode node = factory.createPackage("a");

        assertFalse(node.isConfirmed(), "Not referenced");
    }

    @Test
    void testCreateReferencedClassNode() {
        ClassNode node = factory.createClass("a.A", false);

        assertFalse(node.isConfirmed(), "Not referenced");
        assertFalse(node.getPackageNode().isConfirmed(), "Not referenced");
    }

    @Test
    void testCreateConcreteClassNode() {
        ClassNode node = factory.createClass("a.A", true);

        assertTrue(node.isConfirmed(), "Not concrete");
        assertTrue(node.getPackageNode().isConfirmed(), "Not concrete");
    }

    @Test
    void testCreateClassNodeDefaultsToReferenced() {
        ClassNode node = factory.createClass("a.A");

        assertFalse(node.isConfirmed(), "Not referenced");
        assertFalse(node.getPackageNode().isConfirmed(), "Not referenced");
    }

    @Test
    void testCreateReferencedFeatureNode() {
        FeatureNode node = factory.createFeature("a.A.a", false);

        assertFalse(node.isConfirmed(), "Not referenced");
        assertFalse(node.getClassNode().isConfirmed(), "Not referenced");
        assertFalse(node.getClassNode().getPackageNode().isConfirmed(), "Not referenced");
    }

    @Test
    void testCreateConcreteFeatureNode() {
        FeatureNode node = factory.createFeature("a.A.a", true);

        assertTrue(node.isConfirmed(), "Not concrete");
        assertTrue(node.getClassNode().isConfirmed(), "Not concrete");
        assertTrue(node.getClassNode().getPackageNode().isConfirmed(), "Not concrete");
    }

    @Test
    void testCreateFeatureNodeDefaultsToReferenced() {
        FeatureNode node = factory.createFeature("a.A.a");

        assertFalse(node.isConfirmed(), "Not referenced");
        assertFalse(node.getClassNode().isConfirmed(), "Not referenced");
        assertFalse(node.getClassNode().getPackageNode().isConfirmed(), "Not referenced");
    }

    @Test
    void testSwitchPackageNodeFromReferencedToConcrete() {
        PackageNode node;

        node = factory.createPackage("a", false);
        assertFalse(node.isConfirmed(), "Not referenced");

        node = factory.createPackage("a", true);
        assertTrue(node.isConfirmed(), "Not concrete");
    }

    @Test
    void testSwitchPackageNodeFromConcreteToReferenced() {
        PackageNode node;

        node = factory.createPackage("a", true);
        assertTrue(node.isConfirmed(), "Not concrete");

        node = factory.createPackage("a", false);
        assertTrue(node.isConfirmed(), "Not concrete");
    }

    @Test
    void testMakingPackageNodeConcreteDoesNotChangeItsClasses() {
        PackageNode node;

        node = factory.createPackage("a", false);
        factory.createClass("a.A", false);
        assertFalse(node.isConfirmed(), "Not referenced");
        assertFalse(node.getClasses().iterator().next().isConfirmed(), "Not referenced");

        node = factory.createPackage("a", true);
        assertTrue(node.isConfirmed(), "Not concrete");
        assertFalse(node.getClasses().iterator().next().isConfirmed(), "Not referenced");
    }

    @Test
    void testSwitchClassNodeFromReferencedToConcrete() {
        ClassNode node;

        node = factory.createClass("a.A", false);
        assertFalse(node.isConfirmed(), "Not referenced");
        assertFalse(node.getPackageNode().isConfirmed(), "Not referenced");

        node = factory.createClass("a.A", true);
        assertTrue(node.isConfirmed(), "Not concrete");
        assertTrue(node.getPackageNode().isConfirmed(), "Not concrete");
    }

    @Test
    void testSwitchClassNodeFromConcreteToReferenced() {
        ClassNode node;

        node = factory.createClass("a.A", true);
        assertTrue(node.isConfirmed(), "Not concrete");
        assertTrue(node.getPackageNode().isConfirmed(), "Not concrete");

        node = factory.createClass("a.A", false);
        assertTrue(node.isConfirmed(), "Not concrete");
        assertTrue(node.getPackageNode().isConfirmed(), "Not concrete");
    }

    @Test
    void testMakingClassNodeConcreteDoesNotChangeItsFeatures() {
        ClassNode node;

        node = factory.createClass("a.A", false);
        factory.createFeature("a.A.a", false);
        assertFalse(node.isConfirmed(), "Not referenced");
        assertFalse(node.getFeatures().iterator().next().isConfirmed(), "Not referenced");

        node = factory.createClass("a.A", true);
        assertTrue(node.isConfirmed(), "Not concrete");
        assertFalse(node.getFeatures().iterator().next().isConfirmed(), "Not referenced");
    }

    @Test
    void testSwitchFeatureNodeFromReferencedToConcrete() {
        FeatureNode node;

        node = factory.createFeature("a.A.a", false);
        assertFalse(node.isConfirmed(), "Not referenced");
        assertFalse(node.getClassNode().isConfirmed(), "Not referenced");
        assertFalse(node.getClassNode().getPackageNode().isConfirmed(), "Not referenced");

        node = factory.createFeature("a.A.a", true);
        assertTrue(node.isConfirmed(), "Not concrete");
        assertTrue(node.getClassNode().isConfirmed(), "Not concrete");
        assertTrue(node.getClassNode().getPackageNode().isConfirmed(), "Not concrete");
    }

    @Test
    void testSwitchFeatureNodeFromConcreteToReferenced() {
        FeatureNode node;

        node = factory.createFeature("a.A.a", true);
        assertTrue(node.isConfirmed(), "Not concrete");
        assertTrue(node.getClassNode().isConfirmed(), "Not concrete");
        assertTrue(node.getClassNode().getPackageNode().isConfirmed(), "Not concrete");

        node = factory.createFeature("a.A.a", false);
        assertTrue(node.isConfirmed(), "Not concrete");
        assertTrue(node.getClassNode().isConfirmed(), "Not concrete");
        assertTrue(node.getClassNode().getPackageNode().isConfirmed(), "Not concrete");
    }
}
