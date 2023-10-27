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

public class TestModule_attributeWithModuleUses extends TestAttributeBase {
    private static final int MODULE_NAME_INDEX = 123;
    private static final String MODULE_NAME = "abc";
    private static final int MODULE_FLAGS = 456;
    private static final int MODULE_VERSION_INDEX = 789;
    private static final String MODULE_VERSION = "blah";
    private static final int REQUIRES_COUNT = 0;
    private static final int EXPORTS_COUNT = 0;
    private static final int OPENS_COUNT = 0;
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
        expectReadU2(OPENS_COUNT);

        // And
        var usesCount = 1;
        expectReadU2(usesCount);

        // And
        var usesIndex = 234;
        var className = "Def";
        expectReadU2(usesIndex);
        allowingLookupClass(usesIndex, className, "uses lookup during construction");

        // And
        expectReadU2(PROVIDES_COUNT);

        // And
        var sut = new Module_attribute(mockConstantPool, mockOwner, mockIn);

        // When
        var actualUses = sut.getUses();

        //Then
        assertEquals("number of uses", usesCount, actualUses.size());
        assertEquals(
                "opens",
                usesIndex,
                actualUses.stream()
                        .mapToInt(ModuleUses::getUsesIndex)
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
        expectReadU2(OPENS_COUNT);

        // And
        var usesCount = 2;
        expectReadU2(usesCount);

        // And
        var usesIndex1 = 234;
        var className1 = "Def";
        expectReadU2(usesIndex1);
        allowingLookupClass(usesIndex1, className1, "first uses lookup during construction");

        // And
        var usesIndex2 = 345;
        var className2 = "Ghi";
        expectReadU2(usesIndex2);
        allowingLookupClass(usesIndex2, className2, "second uses lookup during construction");

        // And
        expectReadU2(PROVIDES_COUNT);

        // And
        var sut = new Module_attribute(mockConstantPool, mockOwner, mockIn);

        // When
        var actualUses = sut.getUses();

        //Then
        assertEquals("number of uses", usesCount, actualUses.size());
        assertEquals(
                "first uses",
                usesIndex1,
                actualUses.stream()
                        .mapToInt(ModuleUses::getUsesIndex)
                        .findFirst()
                        .orElseThrow());
        assertEquals(
                "second uses",
                usesIndex2,
                actualUses.stream()
                        .skip(1)
                        .mapToInt(ModuleUses::getUsesIndex)
                        .findFirst()
                        .orElseThrow());
    }
}
