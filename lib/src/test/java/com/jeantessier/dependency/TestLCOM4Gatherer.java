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


import java.util.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestLCOM4Gatherer {
    private final NodeFactory factory = new NodeFactory();

    private final LCOM4Gatherer sut = new LCOM4Gatherer();

    @Test
    void testNothing() {
        sut.traverseNodes(factory.getPackages().values());

        var actualResults = sut.getResults();
        assertTrue(actualResults.isEmpty());
    }

    @Test
    void testEmptyPackage() {
        factory.createPackage("", true);

        sut.traverseNodes(factory.getPackages().values());

        var actualResults = sut.getResults();
        assertTrue(actualResults.isEmpty());
    }

    @Test
    void testEmptyClass() {
        ClassNode classNode = factory.createClass("Empty", true);

        sut.traverseNodes(factory.getPackages().values());

        var actualResults = sut.getResults();
        assertEquals(1, actualResults.size(), "nb results");
        assertTrue(actualResults.containsKey(classNode), classNode.getName() + " is missing from " + actualResults);

        Collection<Collection<FeatureNode>> components = actualResults.get(classNode);
        assertEquals(0, components.size(), "LCOM4 of empty class");
    }

    /**
     * Tests One.one, where class One will have an LCOM4 of 1 and
     * the component will be [One.one].
     */
    @Test
    void testOneFeature() {
        FeatureNode featureNode = factory.createFeature("One.one", true);

        sut.traverseNodes(factory.getPackages().values());

        var actualResults = sut.getResults();
        assertEquals(1, actualResults.size(), "nb results");
        assertTrue(actualResults.containsKey(featureNode.getClassNode()), featureNode.getClassNode().getName() + " is missing from " + actualResults);

        var components = actualResults.get(featureNode.getClassNode());
        assertEquals(1, components.size(), "LCOM4 of class w/ one feature");

        var component = components.iterator().next();
        assertEquals(1, component.size(), "Size of first component");
        assertTrue(component.contains(featureNode));
    }

    /**
     * Tests One.one and One.two, where class One will have an LCOM4 of 2 and
     * the components will be [One.one] and [One.two].
     */
    @Test
    void testTwoDisjointFeatures() {
        FeatureNode featureNode1 = factory.createFeature("Two.one", true);
        FeatureNode featureNode2 = factory.createFeature("Two.two", true);

        sut.traverseNodes(factory.getPackages().values());

        var actualResults = sut.getResults();
        assertEquals(1, actualResults.size(), "nb results");
        assertTrue(actualResults.containsKey(featureNode1.getClassNode()), featureNode1.getClassNode().getName() + " is missing from " + actualResults);

        var components = actualResults.get(featureNode1.getClassNode());
        assertEquals(2, components.size(), "LCOM4 of class w/ two disjoint features");

        assertAtLeastOneComponentEquals(components, featureNode1);
        assertAtLeastOneComponentEquals(components, featureNode2);
    }

    /**
     * Tests One.one --> One.two, where class One will have an LCOM4 of 1 and
     * the component will be [One.one, One.two].
     */
    @Test
    void testTwoConnectedFeatures() {
        FeatureNode featureNode1 = factory.createFeature("Two.one", true);
        FeatureNode featureNode2 = factory.createFeature("Two.two", true);

        featureNode1.addDependency(featureNode2);

        sut.traverseNodes(factory.getPackages().values());

        var actualResults = sut.getResults();
        assertEquals(1, actualResults.size(), "nb results");
        assertTrue(actualResults.containsKey(featureNode1.getClassNode()), featureNode1.getClassNode().getName() + " is missing from " + actualResults);

        var components = actualResults.get(featureNode1.getClassNode());
        assertEquals(1, components.size(), "LCOM4 of class w/ two connected features");

        assertAtLeastOneComponentEquals(components, featureNode1, featureNode2);
    }

    /**
     * Tests One.one --> One.two --> One.three, where class One will have an
     * LCOM4 of 1 and the component will be [One.one, One.two, One.three].
     */
    @Test
    void testThreeConnectedFeatures() {
        FeatureNode featureNode1 = factory.createFeature("Three.one", true);
        FeatureNode featureNode2 = factory.createFeature("Three.two", true);
        FeatureNode featureNode3 = factory.createFeature("Three.three", true);

        featureNode1.addDependency(featureNode2);
        featureNode2.addDependency(featureNode3);

        sut.traverseNodes(factory.getPackages().values());

        var actualResults = sut.getResults();
        assertEquals(1, actualResults.size(), "nb results");
        assertTrue(actualResults.containsKey(featureNode1.getClassNode()), featureNode1.getClassNode().getName() + " is missing from " + actualResults);

        var components = actualResults.get(featureNode1.getClassNode());
        assertEquals(1, components.size(), "LCOM4 of class w/ three connected features");

        assertAtLeastOneComponentEquals(components, featureNode1, featureNode2, featureNode3);
    }

    /**
     * Tests One.one --> Two.two, where class One will have an LCOM4 of 1 and
     * the component will be [One.one].  Two.two is not included because it is
     * in another class.
     */
    @Test
    void testTwoConnectedFeaturesInSeparateClasses() {
        FeatureNode featureNode1 = factory.createFeature("One.one", true);
        FeatureNode featureNode2 = factory.createFeature("Two.two", true);

        featureNode1.addDependency(featureNode2);

        sut.traverseNodes(factory.getPackages().values());

        var actualResults = sut.getResults();
        assertEquals(2, actualResults.size(), "nb results");
        assertTrue(actualResults.containsKey(featureNode1.getClassNode()), featureNode1.getClassNode().getName() + " is missing from " + actualResults);

        var components = actualResults.get(featureNode1.getClassNode());
        assertEquals(1, components.size(), "LCOM4 of class w/ feature connected to other class");

        assertAtLeastOneComponentEquals(components, featureNode1);
    }

    /**
     * Tests One.one --> Two.two --> One.two, where class One will have an
     * LCOM4 of 2 and the components will be [One.one] and [One.two].  There
     * are two components because the link between the methods is outside
     * class One.
     */
    @Test
    void testTwoIndirectlyConnectedFeatures() {
        FeatureNode featureNode1 = factory.createFeature("One.one", true);
        FeatureNode featureNode2 = factory.createFeature("Two.two", true);
        FeatureNode featureNode3 = factory.createFeature("One.two", true);

        featureNode1.addDependency(featureNode2);
        featureNode2.addDependency(featureNode3);

        sut.traverseNodes(factory.getPackages().values());

        var actualResults = sut.getResults();
        assertEquals(2, actualResults.size(), "nb results");
        assertTrue(actualResults.containsKey(featureNode1.getClassNode()), featureNode1.getClassNode().getName() + " is missing from " + actualResults);

        var components = actualResults.get(featureNode1.getClassNode());
        assertEquals(2, components.size(), "LCOM4 of class w/ features connected through other class");

        assertAtLeastOneComponentEquals(components, featureNode1);
        assertAtLeastOneComponentEquals(components, featureNode3);
    }

    @Test
    void testIgnoreConstructor() {
        ClassNode classNode = factory.createClass("One", true);
        factory.createFeature("One.One()", true);

        sut.traverseNodes(factory.getPackages().values());

        var actualResults = sut.getResults();
        assertEquals(1, actualResults.size(), "nb results");
        assertTrue(actualResults.containsKey(classNode), classNode.getName() + " is missing from " + actualResults);

        var components = actualResults.get(classNode);
        assertEquals(0, components.size(), "LCOM4 of class w/ only constructors");
    }

    @Test
    void testIgnoreConstructor_WithinGraph() {
        ClassNode classNode = factory.createClass("Three", true);
        FeatureNode constructorNode = factory.createFeature("Three.Three()", true);
        FeatureNode featureNode1 = factory.createFeature("Three.one", true);
        FeatureNode featureNode2 = factory.createFeature("Three.two", true);

        constructorNode.addDependency(featureNode1);
        constructorNode.addDependency(featureNode2);

        sut.traverseNodes(factory.getPackages().values());

        var actualResults = sut.getResults();
        assertEquals(1, actualResults.size(), "nb results");
        assertTrue(actualResults.containsKey(classNode), classNode.getName() + " is missing from " + actualResults);

        var components = actualResults.get(classNode);
        assertEquals(2, components.size(), "LCOM4 of class w/ two features connected through the constructor " + components);

        assertAtLeastOneComponentEquals(components, featureNode1);
        assertAtLeastOneComponentEquals(components, featureNode2);
    }

    private void assertAtLeastOneComponentEquals(Collection<Collection<FeatureNode>> components, FeatureNode ... expectedNodes) {
        assertTrue(
                components.stream()
                        .anyMatch(component -> checkComponentEquals(component, expectedNodes)));
    }

    private boolean checkComponentEquals(Collection<FeatureNode> component, FeatureNode ... expectedNodes) {
        return expectedNodes.length == component.size() &&
                Arrays.stream(expectedNodes)
                        .allMatch(component::contains);
    }
}
