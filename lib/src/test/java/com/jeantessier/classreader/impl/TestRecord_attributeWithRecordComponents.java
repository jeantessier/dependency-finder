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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestRecord_attributeWithRecordComponents extends TestAttributeBase {
    @Test
    void testWithOneRecordComponent() throws Exception {
        // Given
        final int nameIndex = 123;
        final String name = "Abc";
        final int descriptorIndex = 456;
        final String descriptor = "I";

        // and
        expectReadAttributeLength(8);
        expectReadU2(1);
        expectReadU2(nameIndex);
        allowingLookupUtf8(nameIndex, name, "record component name");
        expectReadU2(descriptorIndex);
        allowingLookupUtf8(descriptorIndex, descriptor, "record component descriptor");
        expectReadU2(0);

        // and
        final AttributeFactory mockAttributeFactory = mock(AttributeFactory.class);

        // When
        var sut = new Record_attribute(mockConstantPool, mockOwner, mockIn, mockAttributeFactory);

        // Then
        assertArrayEquals(new int[] {nameIndex}, sut.getRecordComponents().stream().mapToInt(RecordComponent_info::getNameIndex).toArray());
    }

    @Test
    void testWithMultipleRecordComponents() throws Exception {
        // Given
        final int nameIndex1 = 123;
        final String name1 = "Abc";
        final int descriptorIndex1 = 456;
        final String descriptor1 = "I";

        // and
        final int nameIndex2 = 789;
        final String name2 = "Def";
        final int descriptorIndex2 = 987;
        final String descriptor2 = "Ljava/lang/String;";

        // and
        expectReadAttributeLength(14);
        expectReadU2(2);
        expectReadU2(nameIndex1);
        allowingLookupUtf8(nameIndex1, name1, "first record component name");
        expectReadU2(descriptorIndex1);
        allowingLookupUtf8(descriptorIndex1, descriptor1, "first record component descriptor");
        expectReadU2(0);
        expectReadU2(nameIndex2);
        allowingLookupUtf8(nameIndex2, name2, "second record component name");
        expectReadU2(descriptorIndex2);
        allowingLookupUtf8(descriptorIndex2, descriptor2, "second record component descriptor");
        expectReadU2(0);

        // and
        final AttributeFactory mockAttributeFactory = mock(AttributeFactory.class);

        // When
        var sut = new Record_attribute(mockConstantPool, mockOwner, mockIn, mockAttributeFactory);

        // Then
        assertArrayEquals(new int[] {nameIndex1, nameIndex2}, sut.getRecordComponents().stream().mapToInt(RecordComponent_info::getNameIndex).toArray());
    }
}
