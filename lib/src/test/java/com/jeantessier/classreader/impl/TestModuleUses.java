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

public class TestModuleUses extends TestAttributeBase {
    private static final int USES_INDEX = 123;
    private static final String CLASS_NAME = "Abc";

    private ModuleUses sut;

    @BeforeEach
    void setUp() throws Exception {
        expectReadU2(USES_INDEX);
        allowingLookupClass(USES_INDEX, CLASS_NAME, "lookup during construction");

        sut = new ModuleUses(mockConstantPool, mockIn);
    }

    @Test
    void testGetUsesIndex() {
        assertEquals(USES_INDEX, sut.getUsesIndex(), "uses index");
    }

    @Test
    void testGetRawUses() {
        allowingLookupClass(USES_INDEX, CLASS_NAME);
        assertNotNull(sut.getRawUses());
    }

    @Test
    void testGetUses() {
        expectLookupClass(USES_INDEX, CLASS_NAME);
        assertEquals(CLASS_NAME, sut.getUses(), "uses");
    }

    @Test
    void testAccept() {
        final Visitor mockVisitor = mock(Visitor.class);

        checking(new Expectations() {{
            oneOf (mockVisitor).visitModuleUses(sut);
        }});

        sut.accept(mockVisitor);
    }
}
