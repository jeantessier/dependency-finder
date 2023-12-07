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

public class TestModuleProvidesWithProvidesWiths extends TestAttributeBase {
    private static final int PROVIDES_INDEX = 123;
    private static final String SERVICE_NAME = "Abc";

    public void testOneProvideTo() throws Exception {
        // Given
        expectReadU2(PROVIDES_INDEX);
        allowingLookupClass(PROVIDES_INDEX, SERVICE_NAME, "service name lookup during construction");

        // And
        var providesWithCount = 1;
        expectReadU2(providesWithCount);

        // And
        var providesWithIndex = 234;
        expectReadU2(providesWithIndex);
        var className = "Def";
        allowingLookupClass(providesWithIndex, className, "lookup during construction");

        // And
        var sut = new ModuleProvides(mockConstantPool, mockIn);

        // When
        var actualProvidesWiths = sut.getProvidesWiths();

        //Then
        assertEquals("number of provide withs", providesWithCount, actualProvidesWiths.size());
        assertEquals("provides with", providesWithIndex, actualProvidesWiths.stream().findFirst().orElseThrow().getProvidesWithIndex());
    }

    public void testMultipleProvideTos() throws Exception {
        // Given
        expectReadU2(PROVIDES_INDEX);
        allowingLookupClass(PROVIDES_INDEX, SERVICE_NAME, "service name lookup during construction");

        // And
        var providesWithCount = 2;
        expectReadU2(providesWithCount);

        // And
        var providesWithIndex1 = 234;
        expectReadU2(providesWithIndex1);
        var className1 = "Def";
        allowingLookupClass(providesWithIndex1, className1, "first provides with lookup during construction");

        // And
        var providesWithIndex2 = 567;
        expectReadU2(providesWithIndex2);
        var className2 = "Ghi";
        allowingLookupClass(providesWithIndex2, className2, "second provides with lookup during construction");

        // And
        var sut = new ModuleProvides(mockConstantPool, mockIn);

        // When
        var actualProvidesWiths = sut.getProvidesWiths();

        //Then
        assertEquals("number of provide withs", providesWithCount, actualProvidesWiths.size());
        assertEquals(
                "first provides with",
                providesWithIndex1,
                actualProvidesWiths.stream()
                        .mapToInt(ModuleProvidesWith::getProvidesWithIndex)
                        .findFirst()
                        .orElseThrow());
        assertEquals(
                "second provides with",
                providesWithIndex2,
                actualProvidesWiths.stream()
                        .skip(1)
                        .mapToInt(ModuleProvidesWith::getProvidesWithIndex)
                        .findFirst()
                        .orElseThrow());
    }
}
