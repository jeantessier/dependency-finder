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

public class TestFeatureNode extends TestCase {
    private NodeFactory factory;
    private FeatureNode node;
    
    protected void setUp() throws Exception {
        factory = new NodeFactory();
    }

    public void testSwitchFeatureNodeFromReferencedToConcrete() {
        node = factory.createFeature("a.A.a", false);
        
        assertFalse("Not referenced", node.getClassNode().getPackageNode().isConfirmed());
        assertFalse("Not referenced", node.getClassNode().isConfirmed());
        assertFalse("Not referenced", node.isConfirmed());
        node.setConfirmed(true);
        assertTrue("Not concrete", node.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Not concrete", node.getClassNode().isConfirmed());
        assertTrue("Not concrete", node.isConfirmed());
    }

    public void testSwitchOneFeatureNodeOutOfTwoFromReferencedToConcrete() {
        node = factory.createFeature("a.A.a", false);
        factory.createFeature("a.A.b", false);
        
        assertFalse("Not referenced", node.getClassNode().getPackageNode().isConfirmed());
        assertFalse("Not referenced", node.getClassNode().isConfirmed());
        assertFalse("Not referenced", node.isConfirmed());
        node.setConfirmed(true);
        assertTrue("Not concrete", node.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Not concrete", node.getClassNode().isConfirmed());
        assertTrue("Not concrete", node.isConfirmed());
    }

    public void testSwitchFeatureNodeFromConcreteToReferenced() {
        node = factory.createFeature("a.A.a", true);

        assertTrue("Not concrete", node.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Not concrete", node.getClassNode().isConfirmed());
        assertTrue("Not concrete", node.isConfirmed());
        node.setConfirmed(false);
        assertTrue("Not concrete", node.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Not concrete", node.getClassNode().isConfirmed());
        assertFalse("Not referenced", node.isConfirmed());
    }

    public void testSwitchOneFeatureNodeOutOfTwoFromConcreteToReferenced() {
        node = factory.createFeature("a.A.a", true);
        factory.createFeature("a.A.b", true);
        
        assertTrue("Not concrete", node.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Not concrete", node.getClassNode().isConfirmed());
        assertTrue("Not concrete", node.isConfirmed());
        node.setConfirmed(false);
        assertTrue("Not concrete", node.getClassNode().getPackageNode().isConfirmed());
        assertTrue("Not concrete", node.getClassNode().isConfirmed());
        assertFalse("Not referenced", node.isConfirmed());
    }

    public void testGetSimpleName_DefaultPackage() {
        String packageName = "";
        PackageNode packageNode = new PackageNode(packageName, true);

        String className = "Foo";
        ClassNode classNode = new ClassNode(packageNode, className, true);

        String featureName = "foo";
        FeatureNode sut = new FeatureNode(classNode, className + "." + featureName, true);

        assertEquals(featureName, sut.getSimpleName());
    }
    
    public void testGetSimpleName_SomePackage() {
        String packageName = "foo";
        PackageNode packageNode = new PackageNode(packageName, true);

        String className = "Foo";
        ClassNode classNode = new ClassNode(packageNode, packageName + "." + className, true);

        String featureName = "foo";
        FeatureNode sut = new FeatureNode(classNode, packageName + "." + className + "." + featureName, true);

        assertEquals(featureName, sut.getSimpleName());
    }

    public void testGetSimpleName_MethodWithParameterOfSameClassType() {
        String packageName = "foo";
        PackageNode packageNode = new PackageNode(packageName, true);

        String className = "Foo";
        ClassNode classNode = new ClassNode(packageNode, packageName + "." + className, true);

        String featureName = "foo(foo.Foo)";
        FeatureNode sut = new FeatureNode(classNode, packageName + "." + className + "." + featureName, true);

        assertEquals(featureName, sut.getSimpleName());
    }
}
