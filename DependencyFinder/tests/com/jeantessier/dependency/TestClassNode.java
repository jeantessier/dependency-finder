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

public class TestClassNode extends TestCase {
    private NodeFactory factory;
    private ClassNode   node;
    
    protected void setUp() throws Exception {
        factory = new NodeFactory();
    }

    public void testSwitchClassNodeFromReferencedToConcrete() {
        node = factory.createClass("a.A", false);
        
        assertFalse("Not referenced", node.getPackageNode().isConcrete());
        assertFalse("Not referenced", node.isConcrete());
        node.setConcrete(true);
        assertTrue("Not concrete", node.getPackageNode().isConcrete());
        assertTrue("Not concrete", node.isConcrete());
    }

    public void testSwitchOneClassNodeOutOfTwoFromReferencedToConcrete() {
        node = factory.createClass("a.A", false);
        factory.createClass("a.B", false);
        
        assertFalse("Not referenced", node.getPackageNode().isConcrete());
        assertFalse("Not referenced", node.isConcrete());
        node.setConcrete(true);
        assertTrue("Not concrete", node.getPackageNode().isConcrete());
        assertTrue("Not concrete", node.isConcrete());
    }

    public void testMakingClassNodeConcreteDoesNotChangeItsFeatures() {
        node = factory.createClass("a.A", false);
        factory.createFeature("a.A.a", false);

        assertFalse("Not referenced", node.getPackageNode().isConcrete());
        assertFalse("Not referenced", node.isConcrete());
        assertFalse("Not referenced", ((Node) node.getFeatures().iterator().next()).isConcrete());
        node.setConcrete(true);
        assertTrue("Not concrete", node.getPackageNode().isConcrete());
        assertTrue("Not concrete", node.isConcrete());
        assertFalse("Not referenced", ((Node) node.getFeatures().iterator().next()).isConcrete());
    }

    public void testSwitchEmptyClassNodeFromConcreteToReferenced() {
        node = factory.createClass("a.A", true);

        assertTrue("Not concrete", node.getPackageNode().isConcrete());
        assertTrue("Not concrete", node.isConcrete());
        node.setConcrete(false);
        assertFalse("Not referenced", node.getPackageNode().isConcrete());
        assertFalse("Not referenced", node.isConcrete());
    }

    public void testSwitchOneClassNodeOutOfTwoFromConcreteToReferenced() {
        node = factory.createClass("a.A", true);
        factory.createClass("a.B", true);
        
        assertTrue("Not concrete", node.getPackageNode().isConcrete());
        assertTrue("Not concrete", node.isConcrete());
        node.setConcrete(false);
        assertTrue("Not concrete", node.getPackageNode().isConcrete());
        assertFalse("Not referenced", node.isConcrete());
    }

    public void testSwitchClassNodeWithConcreteFeatureFromConcreteToReferenced() {
        node = factory.createClass("a.A", true);
        factory.createFeature("a.A.a", true);

        assertTrue("Not concrete", node.getPackageNode().isConcrete());
        assertTrue("Not concrete", node.isConcrete());
        assertTrue("Not concrete", ((Node) node.getFeatures().iterator().next()).isConcrete());
        node.setConcrete(false);
        assertFalse("Not referenced", node.getPackageNode().isConcrete());
        assertFalse("Not referenced", node.isConcrete());
        assertFalse("Not referenced", ((Node) node.getFeatures().iterator().next()).isConcrete());
    }

    public void testSwitchClassNodeWithReferencedClassFromConcreteToReferenced() {
        node = factory.createClass("a.A", true);
        factory.createFeature("a.A.a", false);

        assertTrue("Not concrete", node.getPackageNode().isConcrete());
        assertTrue("Not concrete", node.isConcrete());
        assertFalse("Not referenced", ((Node) node.getFeatures().iterator().next()).isConcrete());
        node.setConcrete(false);
        assertFalse("Not referenced", node.getPackageNode().isConcrete());
        assertFalse("Not referenced", node.isConcrete());
        assertFalse("Not referenced", ((Node) node.getFeatures().iterator().next()).isConcrete());
    }
}
