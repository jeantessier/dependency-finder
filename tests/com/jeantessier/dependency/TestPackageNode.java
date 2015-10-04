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

public class TestPackageNode extends TestCase {
    private NodeFactory factory;
    private PackageNode node;
    
    protected void setUp() throws Exception {
        factory = new NodeFactory();
    }

    public void testSwitchPackageNodeFromReferencedToConcrete() {
        node = factory.createPackage("a", false);
        
        assertFalse("Not referenced", node.isConfirmed());
        node.setConfirmed(true);
        assertTrue("Not concrete", node.isConfirmed());
    }

    public void testMakingPackageNodeConcreteDoesNotChangeItsClasses() {
        node = factory.createPackage("a", false);
        factory.createClass("a.A", false);

        assertFalse("Not referenced", node.isConfirmed());
        assertFalse("Not referenced", node.getClasses().iterator().next().isConfirmed());
        node.setConfirmed(true);
        assertTrue("Not concrete", node.isConfirmed());
        assertFalse("Not referenced", node.getClasses().iterator().next().isConfirmed());
    }

    public void testSwitchEmptyPackageNodeFromConcreteToReferenced() {
        node = factory.createPackage("a", true);

        assertTrue("Not concrete", node.isConfirmed());
        node.setConfirmed(false);
        assertFalse("Concrete", node.isConfirmed());
    }

    public void testSwitchPackageNodeWithConcreteClassFromConcreteToReferenced() {
        node = factory.createPackage("a", true);
        factory.createClass("a.A", true);

        assertTrue("Not concrete", node.isConfirmed());
        assertTrue("Not concrete", node.getClasses().iterator().next().isConfirmed());
        node.setConfirmed(false);
        assertTrue("Not concrete", node.isConfirmed());
        assertTrue("Not concrete", node.getClasses().iterator().next().isConfirmed());
    }

    public void testSwitchPackageNodeWithReferencedClassFromConcreteToReferenced() {
        node = factory.createPackage("a", true);
        factory.createClass("a.A", false);

        assertTrue("Not concrete", node.isConfirmed());
        assertFalse("Not referenced", node.getClasses().iterator().next().isConfirmed());
        node.setConfirmed(false);
        assertFalse("Not referenced", node.isConfirmed());
        assertFalse("Not referenced", node.getClasses().iterator().next().isConfirmed());
    }
}
