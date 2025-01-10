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

public class TestModule_attributeWithModuleRequires extends TestAttributeBase {
    private static final int MODULE_NAME_INDEX = 123;
    private static final String MODULE_NAME = "abc";
    private static final int MODULE_FLAGS = 456;
    private static final int MODULE_VERSION_INDEX = 789;
    private static final String MODULE_VERSION = "blah";
    private static final int EXPORTS_COUNT = 0;
    private static final int OPENS_COUNT = 0;
    private static final int USES_COUNT = 0;
    private static final int PROVIDES_COUNT = 0;

    @Test
    void testOneRequires() throws Exception {
        // Given
        expectReadAttributeLength(22);
        expectReadU2(MODULE_NAME_INDEX);
        allowingLookupModule(MODULE_NAME_INDEX, MODULE_NAME, "module name lookup during construction");
        expectReadU2(MODULE_FLAGS);
        expectReadU2(MODULE_VERSION_INDEX);
        allowingLookupUtf8(MODULE_VERSION_INDEX, MODULE_VERSION, "module version lookup during construction");

        // And
        var requiresCount = 1;
        expectReadU2(requiresCount);

        // And
        var requiresIndex = 234;
        var requiresFlags = 567;
        var requiresVersionIndex = 890;
        var requiresVersion = "blah";
        expectReadU2(requiresIndex);
        allowingLookupModule(requiresIndex, MODULE_NAME, "requires lookup during construction");
        expectReadU2(requiresFlags);
        expectReadU2(requiresVersionIndex);
        allowingLookupUtf8(requiresVersionIndex, requiresVersion, "requires version lookup during construction");

        // And
        expectReadU2(EXPORTS_COUNT);
        expectReadU2(OPENS_COUNT);
        expectReadU2(USES_COUNT);
        expectReadU2(PROVIDES_COUNT);

        // And
        var sut = new Module_attribute(mockConstantPool, mockOwner, mockIn);

        // When
        var actualRequires = sut.getRequires();

        //Then
        assertArrayEquals(new int[] {requiresIndex}, actualRequires.stream().mapToInt(ModuleRequires::getRequiresIndex).toArray());
    }

    @Test
    void testMultipleRequires() throws Exception {
        // Given
        expectReadAttributeLength(28);
        expectReadU2(MODULE_NAME_INDEX);
        allowingLookupModule(MODULE_NAME_INDEX, MODULE_NAME, "module name lookup during construction");
        expectReadU2(MODULE_FLAGS);
        expectReadU2(MODULE_VERSION_INDEX);
        allowingLookupUtf8(MODULE_VERSION_INDEX, MODULE_VERSION, "module version lookup during construction");

        // and
        var requiresCount = 2;
        expectReadU2(requiresCount);

        // and
        var requiresIndex1 = 234;
        var requiresFlags1 = 567;
        var requiresVersionIndex1 = 890;
        var requiresVersion1 = "blah";
        expectReadU2(requiresIndex1);
        allowingLookupModule(requiresIndex1, MODULE_NAME, "first requires lookup during construction");
        expectReadU2(requiresFlags1);
        expectReadU2(requiresVersionIndex1);
        allowingLookupUtf8(requiresVersionIndex1, requiresVersion1, "first requires version lookup during construction");

        // and
        var requiresIndex2 = 345;
        var requiresFlags2 = 678;
        var requiresVersionIndex2 = 909;
        var requiresVersion2 = "blah blah";
        expectReadU2(requiresIndex2);
        allowingLookupModule(requiresIndex2, MODULE_NAME, "second requires lookup during construction");
        expectReadU2(requiresFlags2);
        expectReadU2(requiresVersionIndex2);
        allowingLookupUtf8(requiresVersionIndex2, requiresVersion2, "second requires version lookup during construction");

        // and
        expectReadU2(EXPORTS_COUNT);
        expectReadU2(OPENS_COUNT);
        expectReadU2(USES_COUNT);
        expectReadU2(PROVIDES_COUNT);

        // and
        var sut = new Module_attribute(mockConstantPool, mockOwner, mockIn);

        // When
        var actualRequires = sut.getRequires();

        //Then
        assertArrayEquals(new int[] {requiresIndex1, requiresIndex2}, actualRequires.stream().mapToInt(ModuleRequires::getRequiresIndex).toArray());
    }
}
