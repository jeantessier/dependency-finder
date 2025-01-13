/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

public class TestFeatureNode {
    private final NodeFactory factory = new NodeFactory();

    @Test
    void testSwitchFeatureNodeFromReferencedToConcrete() {
        var sut = factory.createFeature("a.A.a", false);
        
        assertFalse(sut.getClassNode().getPackageNode().isConfirmed(), "Not referenced");
        assertFalse(sut.getClassNode().isConfirmed(), "Not referenced");
        assertFalse(sut.isConfirmed(), "Not referenced");
        sut.setConfirmed(true);
        assertTrue(sut.getClassNode().getPackageNode().isConfirmed(), "Not concrete");
        assertTrue(sut.getClassNode().isConfirmed(), "Not concrete");
        assertTrue(sut.isConfirmed(), "Not concrete");
    }

    @Test
    void testSwitchOneFeatureNodeOutOfTwoFromReferencedToConcrete() {
        var sut = factory.createFeature("a.A.a", false);
        factory.createFeature("a.A.b", false);
        
        assertFalse(sut.getClassNode().getPackageNode().isConfirmed(), "Not referenced");
        assertFalse(sut.getClassNode().isConfirmed(), "Not referenced");
        assertFalse(sut.isConfirmed(), "Not referenced");
        sut.setConfirmed(true);
        assertTrue(sut.getClassNode().getPackageNode().isConfirmed(), "Not concrete");
        assertTrue(sut.getClassNode().isConfirmed(), "Not concrete");
        assertTrue(sut.isConfirmed(), "Not concrete");
    }

    @Test
    void testSwitchFeatureNodeFromConcreteToReferenced() {
        var sut = factory.createFeature("a.A.a", true);

        assertTrue(sut.getClassNode().getPackageNode().isConfirmed(), "Not concrete");
        assertTrue(sut.getClassNode().isConfirmed(), "Not concrete");
        assertTrue(sut.isConfirmed(), "Not concrete");
        sut.setConfirmed(false);
        assertTrue(sut.getClassNode().getPackageNode().isConfirmed(), "Not concrete");
        assertTrue(sut.getClassNode().isConfirmed(), "Not concrete");
        assertFalse(sut.isConfirmed(), "Not referenced");
    }

    @Test
    void testSwitchOneFeatureNodeOutOfTwoFromConcreteToReferenced() {
        var sut = factory.createFeature("a.A.a", true);
        factory.createFeature("a.A.b", true);
        
        assertTrue(sut.getClassNode().getPackageNode().isConfirmed(), "Not concrete");
        assertTrue(sut.getClassNode().isConfirmed(), "Not concrete");
        assertTrue(sut.isConfirmed(), "Not concrete");
        sut.setConfirmed(false);
        assertTrue(sut.getClassNode().getPackageNode().isConfirmed(), "Not concrete");
        assertTrue(sut.getClassNode().isConfirmed(), "Not concrete");
        assertFalse(sut.isConfirmed(), "Not referenced");
    }

    @Test
    void testGetSimpleName_DefaultPackage() {
        String packageName = "";
        PackageNode packageNode = new PackageNode(packageName, true);

        String className = "Foo";
        ClassNode classNode = new ClassNode(packageNode, className, true);

        String featureName = "foo";
        FeatureNode sut = new FeatureNode(classNode, className + "." + featureName, true);

        assertEquals(featureName, sut.getSimpleName());
    }
    
    @Test
    void testGetSimpleName_SomePackage() {
        String packageName = "foo";
        PackageNode packageNode = new PackageNode(packageName, true);

        String className = "Foo";
        ClassNode classNode = new ClassNode(packageNode, packageName + "." + className, true);

        String featureName = "foo";
        FeatureNode sut = new FeatureNode(classNode, packageName + "." + className + "." + featureName, true);

        assertEquals(featureName, sut.getSimpleName());
    }

    @Test
    void testGetSimpleName_MethodWithParameterOfSameClassType() {
        String packageName = "foo";
        PackageNode packageNode = new PackageNode(packageName, true);

        String className = "Foo";
        ClassNode classNode = new ClassNode(packageNode, packageName + "." + className, true);

        String featureName = "foo(foo.Foo)";
        FeatureNode sut = new FeatureNode(classNode, packageName + "." + className + "." + featureName, true);

        assertEquals(featureName, sut.getSimpleName());
    }
}
