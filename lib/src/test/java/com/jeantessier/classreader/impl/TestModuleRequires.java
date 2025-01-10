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

package com.jeantessier.classreader.impl;

import org.jmock.*;
import org.junit.jupiter.api.*;

import com.jeantessier.classreader.Visitor;

import static org.junit.jupiter.api.Assertions.*;

public class TestModuleRequires extends TestAttributeBase {
    private static final int REQUIRES_INDEX = 123;
    private static final String MODULE_NAME = "abc";
    private static final int REQUIRES_FLAGS = 456;
    private static final int REQUIRES_VERSION_INDEX = 789;
    private static final String REQUIRES_VERSION = "blah";

    private ModuleRequires sut;

    @BeforeEach
    void setUp() throws Exception {
        expectReadU2(REQUIRES_INDEX);
        allowingLookupModule(REQUIRES_INDEX, MODULE_NAME, "requires lookup during construction");
        expectReadU2(REQUIRES_FLAGS);
        expectReadU2(REQUIRES_VERSION_INDEX);
        allowingLookupUtf8(REQUIRES_VERSION_INDEX, REQUIRES_VERSION, "requires version lookup during construction");

        sut = new ModuleRequires(mockConstantPool, mockIn);
    }

    @Test
    void testGetRequiresIndex() {
        assertEquals(REQUIRES_INDEX, sut.getRequiresIndex(), "requires index");
    }

    @Test
    void testGetRawRequires() {
        allowingLookupModule(REQUIRES_INDEX, MODULE_NAME);
        assertNotNull(sut.getRawRequires());
    }

    @Test
    void testGetRequires() {
        expectLookupModule(REQUIRES_INDEX, MODULE_NAME);
        assertEquals(MODULE_NAME, sut.getRequires(), "requires");
    }

    @Test
    void testAccept() {
        var mockVisitor = mock(Visitor.class);

        checking(new Expectations() {{
            oneOf (mockVisitor).visitModuleRequires(sut);
        }});

        sut.accept(mockVisitor);
    }
}
