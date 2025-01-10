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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestModuleOpensWithOpensTos extends TestAttributeBase {
    private static final int OPENS_INDEX = 123;
    private static final String PACKAGE_NAME = "abc";
    private static final int OPENS_FLAGS = 456;

    @Test
    void testOneOpensTo() throws Exception {
        // Given
        expectReadU2(OPENS_INDEX);
        allowingLookupPackage(OPENS_INDEX, PACKAGE_NAME, "package name lookup during construction");
        expectReadU2(OPENS_FLAGS);

        // and
        var opensToCount = 1;
        expectReadU2(opensToCount);

        // and
        var opensToIndex = 234;
        expectReadU2(opensToIndex);
        var opensTo = "def";
        allowingLookupModule(opensToIndex, opensTo, "lookup during construction");

        // and
        var sut = new ModuleOpens(mockConstantPool, mockIn);

        // When
        var actualOpensTos = sut.getOpensTos();

        //Then
        assertArrayEquals(new int[] {opensToIndex}, actualOpensTos.stream().mapToInt(ModuleOpensTo::getOpensToIndex).toArray());
    }

    @Test
    void testMultipleOpensTos() throws Exception {
        // Given
        expectReadU2(OPENS_INDEX);
        allowingLookupPackage(OPENS_INDEX, PACKAGE_NAME, "package name lookup during construction");
        expectReadU2(OPENS_FLAGS);

        // and
        var opensToCount = 2;
        expectReadU2(opensToCount);

        // and
        var opensToIndex1 = 234;
        expectReadU2(opensToIndex1);
        var opensTo1 = "def";
        allowingLookupModule(opensToIndex1, opensTo1, "first opens to lookup during construction");

        // and
        var opensToIndex2 = 567;
        expectReadU2(opensToIndex2);
        var opensTo2 = "ghi";
        allowingLookupModule(opensToIndex2, opensTo2, "second opens to lookup during construction");

        // and
        var sut = new ModuleOpens(mockConstantPool, mockIn);

        // When
        var actualOpensTos = sut.getOpensTos();

        //Then
        assertArrayEquals(new int[] {opensToIndex1, opensToIndex2}, actualOpensTos.stream().mapToInt(ModuleOpensTo::getOpensToIndex).toArray());
    }
}
