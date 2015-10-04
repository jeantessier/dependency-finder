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

public class TestClassNode extends TestCase {
    private NodeFactory factory;

    protected void setUp() throws Exception {
        super.setUp();
        
        factory = new NodeFactory();
    }

    public void testSwitchClassNodeFromReferencedToConcrete() {
        ClassNode classNode = factory.createClass("a.A", false);
        
        assertFalse("Not referenced", classNode.getPackageNode().isConfirmed());
        assertFalse("Not referenced", classNode.isConfirmed());
        classNode.setConfirmed(true);
        assertTrue("Not concrete", classNode.getPackageNode().isConfirmed());
        assertTrue("Not concrete", classNode.isConfirmed());
    }

    public void testSwitchOneClassNodeOutOfTwoFromReferencedToConcrete() {
        ClassNode classNode = factory.createClass("a.A", false);
        ClassNode otherClassNode = factory.createClass("a.B", false);
        assertSame("Different package node", classNode.getPackageNode(), otherClassNode.getPackageNode());

        assertFalse("Not referenced", classNode.getPackageNode().isConfirmed());
        assertFalse("Not referenced", classNode.isConfirmed());
        assertFalse("Not referenced", otherClassNode.getPackageNode().isConfirmed());
        assertFalse("Not referenced", otherClassNode.isConfirmed());
        classNode.setConfirmed(true);
        assertTrue("Not concrete", classNode.getPackageNode().isConfirmed());
        assertTrue("Not concrete", classNode.isConfirmed());
        assertTrue("Not concrete", otherClassNode.getPackageNode().isConfirmed());
        assertFalse("Not referenced", otherClassNode.isConfirmed());
    }

    public void testMakingClassNodeConcreteDoesNotChangeItsFeatures() {
        ClassNode classNode = factory.createClass("a.A", false);
        FeatureNode featureNode = factory.createFeature("a.A.a", false);

        assertFalse("Not referenced", classNode.getPackageNode().isConfirmed());
        assertFalse("Not referenced", classNode.isConfirmed());
        assertFalse("Not referenced", featureNode.isConfirmed());
        classNode.setConfirmed(true);
        assertTrue("Not concrete", classNode.getPackageNode().isConfirmed());
        assertTrue("Not concrete", classNode.isConfirmed());
        assertFalse("Not referenced", featureNode.isConfirmed());
    }

    public void testSwitchEmptyClassNodeFromConcreteToReferenced() {
        ClassNode classNode = factory.createClass("a.A", true);

        assertTrue("Not concrete", classNode.getPackageNode().isConfirmed());
        assertTrue("Not concrete", classNode.isConfirmed());
        classNode.setConfirmed(false);
        assertFalse("Not referenced", classNode.getPackageNode().isConfirmed());
        assertFalse("Not referenced", classNode.isConfirmed());
    }

    public void testSwitchOneClassNodeOutOfTwoFromConcreteToReferenced() {
        ClassNode classNode = factory.createClass("a.A", true);
        ClassNode otherClassNode = factory.createClass("a.B", true);
        assertSame("Different package node", classNode.getPackageNode(), otherClassNode.getPackageNode());

        assertTrue("Not concrete", classNode.getPackageNode().isConfirmed());
        assertTrue("Not concrete", classNode.isConfirmed());
        assertTrue("Not concrete", otherClassNode.getPackageNode().isConfirmed());
        assertTrue("Not concrete", otherClassNode.isConfirmed());
        classNode.setConfirmed(false);
        assertTrue("Not concrete", classNode.getPackageNode().isConfirmed());
        assertFalse("Not referenced", classNode.isConfirmed());
        assertTrue("Not concrete", classNode.getPackageNode().isConfirmed());
        assertTrue("Not concrete", otherClassNode.isConfirmed());
    }

    public void testSwitchClassNodeWithConcreteFeatureFromConcreteToReferenced() {
        ClassNode classNode = factory.createClass("a.A", true);
        FeatureNode featureNode = factory.createFeature("a.A.a", true);

        assertTrue("Not concrete", classNode.getPackageNode().isConfirmed());
        assertTrue("Not concrete", classNode.isConfirmed());
        assertTrue("Not concrete", featureNode.isConfirmed());
        classNode.setConfirmed(false);
        assertFalse("Not referenced", classNode.getPackageNode().isConfirmed());
        assertFalse("Not referenced", classNode.isConfirmed());
        assertFalse("Not referenced", featureNode.isConfirmed());
    }

    public void testSwitchClassNodeWithReferencedClassFromConcreteToReferenced() {
        ClassNode classNode = factory.createClass("a.A", true);
        FeatureNode featureNode = factory.createFeature("a.A.a", false);

        assertTrue("Not concrete", classNode.getPackageNode().isConfirmed());
        assertTrue("Not concrete", classNode.isConfirmed());
        assertFalse("Not referenced", featureNode.isConfirmed());
        classNode.setConfirmed(false);
        assertFalse("Not referenced", classNode.getPackageNode().isConfirmed());
        assertFalse("Not referenced", classNode.isConfirmed());
        assertFalse("Not referenced", featureNode.isConfirmed());
    }

    public void testAddParent() {
        ClassNode classNode = factory.createClass("a.A");
        ClassNode parentClass = factory.createClass("a.B");

        classNode.addParent(parentClass);
        assertTrue(classNode.getParents().contains(parentClass));
        assertTrue(parentClass.getChildren().contains(classNode));
    }

    public void testGetSimpleName_DefaultPackage() {
        String packageName = "";
        PackageNode packageNode = new PackageNode(packageName, true);

        String className = "Foo";
        ClassNode sut = new ClassNode(packageNode, className, true);

        assertEquals(className, sut.getSimpleName());
    }
    
    public void testGetSimpleName_SomePackage() {
        String packageName = "foo";
        PackageNode packageNode = new PackageNode(packageName, true);

        String className = "Foo";
        ClassNode sut = new ClassNode(packageNode, packageName + "." + className, true);

        assertEquals(className, sut.getSimpleName());
    }

    public void testGetFeature_Trivial() {
        String packageName = "foo";
        PackageNode packageNode = new PackageNode(packageName, true);

        String className = "Foo";
        ClassNode sut = new ClassNode(packageNode, packageName + "." + className, true);

        String featureName = "foo";
        FeatureNode featureNode = new FeatureNode(sut, packageName + "." + className + "." + featureName, true);
        sut.addFeature(featureNode);

        FeatureNode actualFeatureNode = sut.getFeature(featureName);
        assertSame(featureNode, actualFeatureNode);
    }

    public void testGetFeature_NoSuchFeature() {
        String packageName = "foo";
        PackageNode packageNode = new PackageNode(packageName, true);

        String className = "Foo";
        ClassNode sut = new ClassNode(packageNode, packageName + "." + className, true);

        String featureName = "foo";

        FeatureNode actualFeatureNode = sut.getFeature(featureName);
        assertNull(actualFeatureNode);
    }

    public void testGetFeature_MethodWithParameterOfSameClassType() {
        String packageName = "foo";
        PackageNode packageNode = new PackageNode(packageName, true);

        String className = "Foo";
        ClassNode sut = new ClassNode(packageNode, packageName + "." + className, true);

        String featureName = "foo(foo.Foo)";
        FeatureNode featureNode = new FeatureNode(sut, packageName + "." + className + "." + featureName, true);
        sut.addFeature(featureNode);

        FeatureNode actualFeatureNode = sut.getFeature(featureName);
        assertSame(featureNode, actualFeatureNode);
    }

    public void testGetInheritedFeatures_FeatureOnlyInParent() {
        PackageNode packageNode = new PackageNode("", true);

        ClassNode parentNode = new ClassNode(packageNode, "Foo", true);

        String featureName = "foo";
        FeatureNode parentFeatureNode = new FeatureNode(parentNode, parentNode.getName() + "." + featureName, true);
        parentNode.addFeature(parentFeatureNode);

        ClassNode childNode = new ClassNode(packageNode, "Bar", true);
        childNode.addParent(parentNode);

        Collection<FeatureNode> actualFeatureNodes = childNode.getInheritedFeatures(featureName);
        assertEquals(1, actualFeatureNodes.size());
        assertSame(parentFeatureNode, actualFeatureNodes.iterator().next());
    }

    public void testGetInheritedFeatures_FeatureInParentAndChild() {
        PackageNode packageNode = new PackageNode("", true);

        ClassNode parentNode = new ClassNode(packageNode, "Foo", true);

        String featureName = "foo";
        FeatureNode parentFeatureNode = new FeatureNode(parentNode, parentNode.getName() + "." + featureName, true);
        parentNode.addFeature(parentFeatureNode);

        ClassNode childNode = new ClassNode(packageNode, "Bar", true);
        childNode.addParent(parentNode);

        FeatureNode childFeatureNode = new FeatureNode(childNode, childNode.getName() + "." + featureName, true);
        childNode.addFeature(childFeatureNode);

        Collection<FeatureNode> actualFeatureNodes = childNode.getInheritedFeatures(featureName);
        assertEquals(2, actualFeatureNodes.size());
        Iterator<FeatureNode> i = actualFeatureNodes.iterator();
        assertSame(childFeatureNode, i.next());
        assertSame(parentFeatureNode, i.next());
    }

    public void testGetInheritedFeatures_FeatureOnlyInAncestor() {
        PackageNode packageNode = new PackageNode("", true);

        ClassNode ancestorNode = new ClassNode(packageNode, "F", true);

        String featureName = "foo";
        FeatureNode parentFeatureNode = new FeatureNode(ancestorNode, ancestorNode.getName() + "." + featureName, true);
        ancestorNode.addFeature(parentFeatureNode);

        ClassNode parentNode = new ClassNode(packageNode, "Foo", true);
        parentNode.addParent(ancestorNode);

        ClassNode childNode = new ClassNode(packageNode, "Bar", true);
        childNode.addParent(parentNode);

        Collection<FeatureNode> actualFeatureNodes = childNode.getInheritedFeatures(featureName);
        assertEquals(1, actualFeatureNodes.size());
        assertSame(parentFeatureNode, actualFeatureNodes.iterator().next());
    }
}
