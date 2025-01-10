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

public class TestModuleExports extends TestAttributeBase {
    private static final int EXPORTS_INDEX = 123;
    private static final String PACKAGE_NAME = "abc";
    private static final int EXPORTS_FLAGS = 456;

    private ModuleExports sut;

    @BeforeEach
    void setUp() throws Exception {
        expectReadU2(EXPORTS_INDEX);
        allowingLookupPackage(EXPORTS_INDEX, PACKAGE_NAME, "exports lookup during construction");
        expectReadU2(EXPORTS_FLAGS);
        expectReadU2(0);

        sut = new ModuleExports(mockConstantPool, mockIn);
    }

    @Test
    void testGetExportsIndex() {
        assertEquals(EXPORTS_INDEX, sut.getExportsIndex(), "exports index");
    }

    @Test
    void testGetRawExports() {
        allowingLookupPackage(EXPORTS_INDEX, PACKAGE_NAME);
        assertNotNull(sut.getRawExports());
    }

    @Test
    void testGetExports() {
        expectLookupPackage(EXPORTS_INDEX, PACKAGE_NAME);
        assertEquals(PACKAGE_NAME, sut.getExports(), "exports");
    }

    @Test
    void testAccept() {
        var mockVisitor = mock(Visitor.class);

        checking(new Expectations() {{
            oneOf (mockVisitor).visitModuleExports(sut);
        }});

        sut.accept(mockVisitor);
    }
}
