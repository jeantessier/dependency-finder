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

public class TestPackageNode {
    private final NodeFactory factory = new NodeFactory();

    @Test
    void testSwitchPackageNodeFromReferencedToConcrete() {
        var sut = factory.createPackage("a", false);
        
        assertFalse(sut.isConfirmed(), "Not referenced");
        sut.setConfirmed(true);
        assertTrue(sut.isConfirmed(), "Not concrete");
    }

    @Test
    void testMakingPackageNodeConcreteDoesNotChangeItsClasses() {
        var sut = factory.createPackage("a", false);
        factory.createClass("a.A", false);

        assertFalse(sut.isConfirmed(), "Not referenced");
        assertFalse(sut.getClasses().iterator().next().isConfirmed(), "Not referenced");
        sut.setConfirmed(true);
        assertTrue(sut.isConfirmed(), "Not concrete");
        assertFalse(sut.getClasses().iterator().next().isConfirmed(), "Not referenced");
    }

    @Test
    void testSwitchEmptyPackageNodeFromConcreteToReferenced() {
        var sut = factory.createPackage("a", true);

        assertTrue(sut.isConfirmed(), "Not concrete");
        sut.setConfirmed(false);
        assertFalse(sut.isConfirmed(), "Concrete");
    }

    @Test
    void testSwitchPackageNodeWithConcreteClassFromConcreteToReferenced() {
        var sut = factory.createPackage("a", true);
        factory.createClass("a.A", true);

        assertTrue(sut.isConfirmed(), "Not concrete");
        assertTrue(sut.getClasses().iterator().next().isConfirmed(), "Not concrete");
        sut.setConfirmed(false);
        assertTrue(sut.isConfirmed(), "Not concrete");
        assertTrue(sut.getClasses().iterator().next().isConfirmed(), "Not concrete");
    }

    @Test
    void testSwitchPackageNodeWithReferencedClassFromConcreteToReferenced() {
        var sut = factory.createPackage("a", true);
        factory.createClass("a.A", false);

        assertTrue(sut.isConfirmed(), "Not concrete");
        assertFalse(sut.getClasses().iterator().next().isConfirmed(), "Not referenced");
        sut.setConfirmed(false);
        assertFalse(sut.isConfirmed(), "Not referenced");
        assertFalse(sut.getClasses().iterator().next().isConfirmed(), "Not referenced");
    }
}
