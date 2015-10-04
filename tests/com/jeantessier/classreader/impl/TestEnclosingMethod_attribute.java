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

package com.jeantessier.classreader.impl;

import org.jmock.*;

import com.jeantessier.classreader.AttributeType;
import com.jeantessier.classreader.*;

public class TestEnclosingMethod_attribute extends TestAttributeBase {
    private static final int CLASS_INDEX = 2;
    private static final String CLASS = "abc";
    private static final int METHOD_INDEX = 3;
    private static final String NAME = "def";
    private static final String TYPE = "ghi";

    private EnclosingMethod_attribute sut;

    protected void setUp() throws Exception {
        super.setUp();

        expectReadAttributeLength(4);
        expectReadU2(CLASS_INDEX);
        expectLookupClass(CLASS_INDEX, CLASS, "class lookup during construction");
        expectReadU2(METHOD_INDEX);
        expectLookupNameAndType(METHOD_INDEX, NAME, TYPE, "name and type lookup during construction");

        sut = new EnclosingMethod_attribute(mockConstantPool, mockOwner, mockIn);
    }

    public void testGetClassInfo() {
        expectLookupClass(CLASS_INDEX, CLASS);

        assertEquals(CLASS, sut.getClassInfo());
    }

    public void testGetMethod() {
        expectLookupNameAndType(METHOD_INDEX, NAME, TYPE);

        assertEquals(NAME + TYPE, sut.getMethod());
    }

    public void testGetAttributeName() {
        assertEquals(AttributeType.ENCLOSING_METHOD.getAttributeName(), sut.getAttributeName());
    }

    public void testAccept() {
        final Visitor mockVisitor = mock(Visitor.class);

        checking(new Expectations() {{
            one (mockVisitor).visitEnclosingMethod_attribute(sut);
        }});

        sut.accept(mockVisitor);
    }
}