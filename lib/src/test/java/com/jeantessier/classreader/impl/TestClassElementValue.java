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

import com.jeantessier.classreader.*;

public class TestClassElementValue extends TestAnnotationsBase {
    private static final int CLASS_INFO_INDEX = 2;
    private static final String ENCODED_CLASS_INFO = "Labc;";
    private static final String CLASS_INFO = "abc";

    private ClassElementValue sut;

    protected void setUp() throws Exception {
        super.setUp();

        expectReadU2(CLASS_INFO_INDEX);
        expectLookupUtf8(CLASS_INFO_INDEX, ENCODED_CLASS_INFO, "lookup during construction");

        sut = new ClassElementValue(mockConstantPool, mockIn);
    }

    public void testGetTypeName() {
        expectLookupUtf8(CLASS_INFO_INDEX, ENCODED_CLASS_INFO);

        assertEquals(CLASS_INFO, sut.getClassInfo());
    }

    public void testGetTypeName_void() {
        expectLookupUtf8(CLASS_INFO_INDEX, "V");

        assertEquals("void", sut.getClassInfo());
    }

    public void testGetTag() {
        assertEquals(ElementValueType.CLASS.getTag(), sut.getTag());
    }

    public void testAccept() {
        final Visitor mockVisitor = mock(Visitor.class);

        checking(new Expectations() {{
            oneOf (mockVisitor).visitClassElementValue(sut);
        }});

        sut.accept(mockVisitor);
    }
}
