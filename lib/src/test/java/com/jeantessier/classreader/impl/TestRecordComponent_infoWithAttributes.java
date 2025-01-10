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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestRecordComponent_infoWithAttributes extends TestAttributeBase {
    @Test
    void testWithOneAttribute() throws Exception {
        // Given
        var nameIndex = 123;
        var name = "Abc";
        var descriptorIndex = 456;
        var descriptor = "I";

        // and
        expectReadU2(nameIndex);
        allowingLookupUtf8(nameIndex, name, "record component name");
        expectReadU2(descriptorIndex);
        allowingLookupUtf8(descriptorIndex, descriptor, "record component descriptor");
        expectReadU2(1);

        // and
        var mockAttributeFactory = mock(AttributeFactory.class);
        var mockAttribute = mock(Attribute_info.class);
        checking(new Expectations() {{
            oneOf (mockAttributeFactory).create(with(same(mockConstantPool)), with(any(RecordComponent_info.class)), with(same(mockIn)));
                will(returnValue(mockAttribute));
        }});

        // When
        var sut = new RecordComponent_info(mockConstantPool, mockIn, mockAttributeFactory);

        // Then
        assertEquals(List.of(mockAttribute), List.copyOf(sut.getAttributes()));
    }

    @Test
    void testWithMultipleAttributes() throws Exception {
        // Given
        var nameIndex = 123;
        var name = "Abc";
        var descriptorIndex = 456;
        var descriptor = "I";

        // and
        expectReadU2(nameIndex);
        allowingLookupUtf8(nameIndex, name, "first record component name");
        expectReadU2(descriptorIndex);
        allowingLookupUtf8(descriptorIndex, descriptor, "first record component descriptor");
        expectReadU2(2);

        // and
        var mockAttributeFactory = mock(AttributeFactory.class);
        var mockAttribute1 = mock(Attribute_info.class, "first attribute");
        var mockAttribute2 = mock(Attribute_info.class, "second attribute");
        checking(new Expectations() {{
            oneOf (mockAttributeFactory).create(with(same(mockConstantPool)), with(any(RecordComponent_info.class)), with(same(mockIn)));
                will(returnValue(mockAttribute1));
            oneOf (mockAttributeFactory).create(with(same(mockConstantPool)), with(any(RecordComponent_info.class)), with(same(mockIn)));
                will(returnValue(mockAttribute2));
        }});

        // When
        var sut = new RecordComponent_info(mockConstantPool, mockIn, mockAttributeFactory);

        // Then
        assertEquals(List.of(mockAttribute1, mockAttribute2), List.copyOf(sut.getAttributes()));
    }
}
