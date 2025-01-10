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

public class TestModulePackages_attributeWithModulePackages extends TestAttributeBase {
    @Test
    void testWithOneModulePackage() throws Exception {
        // Given
        final int packageIndex = 123;
        final String packageName = "abc";

        // And
        expectReadAttributeLength(4);
        expectReadU2(1);
        expectReadU2(packageIndex);
        allowingLookupPackage(packageIndex, packageName);

        // When
        var sut = new ModulePackages_attribute(mockConstantPool, mockOwner, mockIn);

        // Then
        assertArrayEquals(new int[] {packageIndex}, sut.getPackages().stream().mapToInt(ModulePackage::getPackageIndex).toArray());
    }

    @Test
    void testWithMultipleModulePackages() throws Exception {
        // Given
        final int packageIndex1 = 123;
        final String packageName1 = "abc";

        // And
        final int packageIndex2 = 456;
        final String packageName2 = "def";

        // And
        expectReadAttributeLength(6);
        expectReadU2(2);
        expectReadU2(packageIndex1);
        allowingLookupPackage(packageIndex1, packageName1, "first module package");
        expectReadU2(packageIndex2);
        allowingLookupPackage(packageIndex2, packageName2, "second module package");

        // When
        var sut = new ModulePackages_attribute(mockConstantPool, mockOwner, mockIn);

        // Then
        assertArrayEquals(new int[] {packageIndex1, packageIndex2}, sut.getPackages().stream().mapToInt(ModulePackage::getPackageIndex).toArray());
    }
}
