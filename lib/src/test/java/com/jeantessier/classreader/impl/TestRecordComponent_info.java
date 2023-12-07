/*
 *  Copyright (c) 2001-2023, Jean Tessier
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

import com.jeantessier.classreader.Visitor;
import org.jmock.Expectations;

public class TestRecordComponent_info extends TestAttributeBase {
    private static final int NAME_INDEX = 123;
    private static final String NAME = "Abc";
    private static final int DESCRIPTOR_INDEX = 456;
    private static final String DESCRIPTOR = "I";
    private static final String DECODED_DESCRIPTOR = "int";

    private RecordComponent_info sut;

    protected void setUp() throws Exception {
        super.setUp();

        expectReadU2(NAME_INDEX);
        allowingLookupUtf8(NAME_INDEX, NAME, "name lookup during construction");
        expectReadU2(DESCRIPTOR_INDEX);
        allowingLookupUtf8(DESCRIPTOR_INDEX, DESCRIPTOR, "descriptor lookup during construction");
        expectReadU2(0);

        final AttributeFactory mockAttributeFactory = mock(AttributeFactory.class);

        sut = new RecordComponent_info(mockConstantPool, mockIn, mockAttributeFactory);
    }

    public void testGetNameIndex() {
        assertEquals("name index", NAME_INDEX, sut.getNameIndex());
    }

    public void testGetName() {
        expectLookupUtf8(NAME_INDEX, NAME);
        assertEquals("name", NAME, sut.getName());
    }

    public void testGetDescriptorIndex() {
        assertEquals("descriptor index", DESCRIPTOR_INDEX, sut.getDescriptorIndex());
    }

    public void testGetDescriptor() {
        expectLookupUtf8(DESCRIPTOR_INDEX, DESCRIPTOR);
        assertEquals("descriptor", DESCRIPTOR, sut.getDescriptor());
    }

    public void testGetType() {
        expectLookupUtf8(DESCRIPTOR_INDEX, DESCRIPTOR);
        assertEquals("type", DECODED_DESCRIPTOR, sut.getType());
    }

    public void testAccept() {
        final Visitor mockVisitor = mock(Visitor.class);

        checking(new Expectations() {{
            oneOf (mockVisitor).visitRecordComponent_info(sut);
        }});

        sut.accept(mockVisitor);
    }
}
