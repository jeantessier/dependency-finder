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

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public class TestTargetType_forTargetType {
    @Parameters(name="TargetType from value {0}")
    public static Object[][] data() {
        return new Object[][] {
            {"0x00", 0x00, TargetType.PARAMETER_OF_CLASS},
            {"0x01", 0x01, TargetType.PARAMETER_OF_GENERIC_METHOD},
            {"0x10", 0x10, TargetType.EXTENDS_OR_IMPLEMENTS},
            {"0x11", 0x11, TargetType.BOUND_GENERIC_CLASS},
            {"0x12", 0x12, TargetType.BOUND_GENERIC_METHOD},
            {"0x13", 0x13, TargetType.FIELD},
            {"0x14", 0x14, TargetType.RETURN_TYPE},
            {"0x15", 0x15, TargetType.RECEIVER},
            {"0x16", 0x16, TargetType.FORMAL_PARAMETER_OF_METHOD},
            {"0x17", 0x17, TargetType.THROWS_CLAUSE},
            {"0x40", 0x40, TargetType.LOCAL_VARIABLE},
            {"0x41", 0x41, TargetType.RESOURCE_VARIABLE},
            {"0x42", 0x42, TargetType.EXCEPTION_PARAMETER},
            {"0x43", 0x43, TargetType.INSTANCEOF_EXPRESSION},
            {"0x44", 0x44, TargetType.NEW_EXPRESSION},
            {"0x45", 0x45, TargetType.METHOD_REFERENCE_USING_NEW},
            {"0x46", 0x46, TargetType.METHOD_REFERENCE_USING_IDENTIFIER},
            {"0x47", 0x47, TargetType.CAST_EXPRESSION},
            {"0x48", 0x48, TargetType.ARGUMENT_FOR_GENERIC_CONSTRUCTOR},
            {"0x49", 0x49, TargetType.ARGUMENT_FOR_GENERIC_METHOD_INVOCATION},
            {"0x4A", 0x4A, TargetType.ARGUMENT_FOR_GENERIC_CONSTRUCTOR_USING_NEW},
            {"0x4B", 0x4B, TargetType.ARGUMENT_FOR_GENERIC_METHOD_REFERENCE_USING_IDENTIFIER},
        };
    }

    @Parameter(0)
    public String label;

    @Parameter(1)
    public int targetType;

    @Parameter(2)
    public TargetType expectedResult;

    private TargetType sut;

    @Before
    public void setUp() {
        sut = TargetType.forTargetType(targetType);
    }

    @Test
    public void testEnumValue() {
        assertEquals(label, expectedResult, sut);
    }

    @Test
    public void testRawValue() {
        assertEquals(label, targetType, sut.getTargetType().getTargetType());
    }

    @Test
    public void testHexValue() {
        assertEquals(label, sut.getHexTargetType());
    }
}
