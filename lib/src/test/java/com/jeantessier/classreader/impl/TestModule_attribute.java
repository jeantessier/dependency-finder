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

package com.jeantessier.classreader.impl;

import org.jmock.*;
import org.junit.jupiter.api.*;

import com.jeantessier.classreader.Visitor;

import static org.junit.jupiter.api.Assertions.*;

public class TestModule_attribute extends TestAttributeBase {
    private static final int MODULE_NAME_INDEX = 123;
    private static final String MODULE_NAME = "abc";
    private static final int MODULE_FLAGS = 456;
    private static final int MODULE_VERSION_INDEX = 789;
    private static final String MODULE_VERSION = "blah";
    private static final int REQUIRES_COUNT = 0;
    private static final int EXPORTS_COUNT = 0;
    private static final int OPENS_COUNT = 0;
    private static final int USES_COUNT = 0;
    private static final int PROVIDES_COUNT = 0;

    private Module_attribute sut;

    @BeforeEach
    void setUp() throws Exception {
        expectReadAttributeLength(16);
        expectReadU2(MODULE_NAME_INDEX);
        allowingLookupModule(MODULE_NAME_INDEX, MODULE_NAME, "module name lookup during construction");
        expectReadU2(MODULE_FLAGS);
        expectReadU2(MODULE_VERSION_INDEX);
        allowingLookupUtf8(MODULE_VERSION_INDEX, MODULE_VERSION, "module version lookup during construction");
        expectReadU2(REQUIRES_COUNT);
        expectReadU2(EXPORTS_COUNT);
        expectReadU2(OPENS_COUNT);
        expectReadU2(USES_COUNT);
        expectReadU2(PROVIDES_COUNT);

        sut = new Module_attribute(mockConstantPool, mockOwner, mockIn);
    }

    @Test
    void testGetModuleNameIndex() {
        assertEquals(MODULE_NAME_INDEX, sut.getModuleNameIndex(), "module name index");
    }

    @Test
    void testGetRawModuleName() {
        allowingLookupModule(MODULE_NAME_INDEX, MODULE_NAME);
        assertNotNull(sut.getRawModuleName(), "raw module name");
    }

    @Test
    void testGetModuleName() {
        expectLookupModule(MODULE_NAME_INDEX, MODULE_NAME);
        assertEquals(MODULE_NAME, sut.getModuleName(), "module name");
    }

    @Test
    void testGetRequires() {
        assertEquals(REQUIRES_COUNT, sut.getRequires().size(), "requires");
    }

    @Test
    void testGetExports() {
        assertEquals(EXPORTS_COUNT, sut.getExports().size(), "exports");
    }

    @Test
    void testGetOpens() {
        assertEquals(OPENS_COUNT, sut.getOpens().size(), "opens");
    }

    @Test
    void testGetUses() {
        assertEquals(USES_COUNT, sut.getUses().size(), "uses");
    }

    @Test
    void testGetProvides() {
        assertEquals(PROVIDES_COUNT, sut.getProvides().size(), "provides");
    }

    @Test
    void testGetAttributeName() {
        assertEquals(AttributeType.MODULE.getAttributeName(), sut.getAttributeName());
    }

    @Test
    void testAccept() {
        var mockVisitor = mock(Visitor.class);

        checking(new Expectations() {{
            oneOf (mockVisitor).visitModule_attribute(sut);
        }});

        sut.accept(mockVisitor);
    }
}
