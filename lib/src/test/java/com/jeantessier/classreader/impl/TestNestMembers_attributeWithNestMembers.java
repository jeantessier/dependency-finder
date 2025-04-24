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

public class TestNestMembers_attributeWithNestMembers extends TestAttributeBase {
    @Test
    void testWithOneNestMember() throws Exception {
        // Given
        var classNameIndex = 123;
        var className = "Abc";

        // and
        expectReadAttributeLength(4);
        expectReadU2(1);
        expectReadU2(classNameIndex);
        allowingLookupClass(classNameIndex, className);

        // When
        var sut = new NestMembers_attribute(mockConstantPool, mockOwner, mockIn);

        // Then
        assertArrayEquals(new int[] {classNameIndex}, sut.getMembers().stream().mapToInt(NestMember::getMemberClassIndex).toArray());
    }

    @Test
    void testWithMultipleNestMembers() throws Exception {
        // Given
        var classNameIndex1 = 123;
        var className1 = "Abc";

        // and
        var classNameIndex2 = 456;
        var className2 = "Def";

        // and
        expectReadAttributeLength(6);
        expectReadU2(2);
        expectReadU2(classNameIndex1);
        allowingLookupClass(classNameIndex1, className1, "first nest member");
        expectReadU2(classNameIndex2);
        allowingLookupClass(classNameIndex2, className2, "second nest member");

        // When
        var sut = new NestMembers_attribute(mockConstantPool, mockOwner, mockIn);

        // Then
        assertArrayEquals(new int[] {classNameIndex1, classNameIndex2}, sut.getMembers().stream().mapToInt(NestMember::getMemberClassIndex).toArray());
    }
}
