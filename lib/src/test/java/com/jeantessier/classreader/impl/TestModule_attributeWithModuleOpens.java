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

public class TestModule_attributeWithModuleOpens extends TestAttributeBase {
    private static final int MODULE_NAME_INDEX = 123;
    private static final String MODULE_NAME = "abc";
    private static final int MODULE_FLAGS = 456;
    private static final int MODULE_VERSION_INDEX = 789;
    private static final String MODULE_VERSION = "blah";
    private static final int REQUIRES_COUNT = 0;
    private static final int EXPORTS_COUNT = 0;
    private static final int USES_COUNT = 0;
    private static final int PROVIDES_COUNT = 0;

    public void testOneOpens() throws Exception {
        // Given
        expectReadAttributeLength(22);
        expectReadU2(MODULE_NAME_INDEX);
        allowingLookupModule(MODULE_NAME_INDEX, MODULE_NAME, "module name lookup during construction");
        expectReadU2(MODULE_FLAGS);
        expectReadU2(MODULE_VERSION_INDEX);
        allowingLookupUtf8(MODULE_VERSION_INDEX, MODULE_VERSION, "module version lookup during construction");
        expectReadU2(REQUIRES_COUNT);
        expectReadU2(EXPORTS_COUNT);

        // And
        var opensCount = 1;
        expectReadU2(opensCount);

        // And
        var opensIndex = 234;
        var packageName = "def";
        var opensFlags = 567;
        expectReadU2(opensIndex);
        allowingLookupPackage(opensIndex, packageName, "opens lookup during construction");
        expectReadU2(opensFlags);
        expectReadU2(0);

        // And
        expectReadU2(USES_COUNT);
        expectReadU2(PROVIDES_COUNT);

        // And
        var sut = new Module_attribute(mockConstantPool, mockOwner, mockIn);

        // When
        var actualOpens = sut.getOpens();

        //Then
        assertEquals("number of opens", opensCount, actualOpens.size());
        assertEquals(
                "opens",
                opensIndex,
                actualOpens.stream()
                        .mapToInt(ModuleOpens::getOpensIndex)
                        .findFirst()
                        .orElseThrow());
    }

    public void testMultipleOpens() throws Exception {
        // Given
        expectReadAttributeLength(28);
        expectReadU2(MODULE_NAME_INDEX);
        allowingLookupModule(MODULE_NAME_INDEX, MODULE_NAME, "module name lookup during construction");
        expectReadU2(MODULE_FLAGS);
        expectReadU2(MODULE_VERSION_INDEX);
        allowingLookupUtf8(MODULE_VERSION_INDEX, MODULE_VERSION, "module version lookup during construction");
        expectReadU2(REQUIRES_COUNT);
        expectReadU2(EXPORTS_COUNT);

        // And
        var opensCount = 2;
        expectReadU2(opensCount);

        // And
        var opensIndex1 = 234;
        var packageName1 = "def";
        var opensFlags1 = 567;
        expectReadU2(opensIndex1);
        allowingLookupPackage(opensIndex1, packageName1, "first opens lookup during construction");
        expectReadU2(opensFlags1);
        expectReadU2(0);

        // And
        var opensIndex2 = 345;
        var packageName2 = "ghi";
        var opensFlags2 = 678;
        expectReadU2(opensIndex2);
        allowingLookupPackage(opensIndex2, packageName2, "second opens lookup during construction");
        expectReadU2(opensFlags2);
        expectReadU2(0);

        // And
        expectReadU2(USES_COUNT);
        expectReadU2(PROVIDES_COUNT);

        // And
        var sut = new Module_attribute(mockConstantPool, mockOwner, mockIn);

        // When
        var actualOpens = sut.getOpens();

        //Then
        assertEquals("number of opens", opensCount, actualOpens.size());
        assertEquals(
                "first opens",
                opensIndex1,
                actualOpens.stream()
                        .mapToInt(ModuleOpens::getOpensIndex)
                        .findFirst()
                        .orElseThrow());
        assertEquals(
                "second opens",
                opensIndex2,
                actualOpens.stream()
                        .skip(1)
                        .mapToInt(ModuleOpens::getOpensIndex)
                        .findFirst()
                        .orElseThrow());
    }
}
