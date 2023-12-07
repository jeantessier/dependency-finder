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

public class TestModuleExportsWithExportsTos extends TestAttributeBase {
    private static final int EXPORTS_INDEX = 123;
    private static final String PACKAGE_NAME = "abc";
    private static final int EXPORTS_FLAGS = 456;

    public void testOneExportTo() throws Exception {
        // Given
        expectReadU2(EXPORTS_INDEX);
        allowingLookupPackage(EXPORTS_INDEX, PACKAGE_NAME, "package name lookup during construction");
        expectReadU2(EXPORTS_FLAGS);

        // And
        var exportsToCount = 1;
        expectReadU2(exportsToCount);

        // And
        var exportsToIndex = 234;
        expectReadU2(exportsToIndex);
        var exportsTo = "def";
        allowingLookupModule(exportsToIndex, exportsTo, "lookup during construction");

        // And
        var sut = new ModuleExports(mockConstantPool, mockIn);

        // When
        var actualExportsTos = sut.getExportsTos();

        //Then
        assertEquals("number of export tos", exportsToCount, actualExportsTos.size());
        assertEquals(
                "exports to",
                exportsToIndex,
                actualExportsTos.stream()
                        .mapToInt(ModuleExportsTo::getExportsToIndex)
                        .findFirst()
                        .orElseThrow());
    }

    public void testMultipleExportTos() throws Exception {
        // Given
        expectReadU2(EXPORTS_INDEX);
        allowingLookupPackage(EXPORTS_INDEX, PACKAGE_NAME, "package name lookup during construction");
        expectReadU2(EXPORTS_FLAGS);

        // And
        var exportsToCount = 2;
        expectReadU2(exportsToCount);

        // And
        var exportsToIndex1 = 234;
        expectReadU2(exportsToIndex1);
        var exportsTo1 = "def";
        allowingLookupModule(exportsToIndex1, exportsTo1, "first exports to lookup during construction");

        // And
        var exportsToIndex2 = 567;
        expectReadU2(exportsToIndex2);
        var exportsTo2 = "ghi";
        allowingLookupModule(exportsToIndex2, exportsTo2, "second exports to lookup during construction");

        // And
        var sut = new ModuleExports(mockConstantPool, mockIn);

        // When
        var actualExportsTos = sut.getExportsTos();

        //Then
        assertEquals("number of export tos", exportsToCount, actualExportsTos.size());
        assertEquals(
                "first exports to",
                exportsToIndex1,
                actualExportsTos.stream()
                        .mapToInt(ModuleExportsTo::getExportsToIndex)
                        .findFirst()
                        .orElseThrow());
        assertEquals(
                "second exports to",
                exportsToIndex2,
                actualExportsTos.stream()
                        .skip(1)
                        .mapToInt(ModuleExportsTo::getExportsToIndex)
                        .findFirst()
                        .orElseThrow());
    }
}
