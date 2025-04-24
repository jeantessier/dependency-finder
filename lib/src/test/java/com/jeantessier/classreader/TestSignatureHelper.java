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

package com.jeantessier.classreader;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestSignatureHelper {
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("()V", "()", 0),
                arguments("<T:Ljava/lang/Object;>(TT;)V", "(T)", 1),
                arguments("<T:Ljava/lang/Object;>(TT;ILjava/lang/String;)V", "(T, int, java.lang.String)", 3),
                arguments("(I)V", "(int)", 1),
                arguments("(II)V", "(int, int)", 2),
                arguments("([I)V", "(int[])", 1),
                arguments("(Ljava/lang/Object;)V", "(java.lang.Object)", 1),
                arguments("([Ljava/lang/Object;)V", "(java.lang.Object[])", 1)
        );
    }

    @DisplayName("SignatureHelper")
    @ParameterizedTest(name="signature for \"{0}\" should be \"{1}\"")
    @MethodSource("dataProvider")
    public void testGetSignature(String descriptor, String expectedSignature, int expectedParameterCount) {
        assertEquals(expectedSignature, SignatureHelper.getSignature(descriptor));
    }

    @DisplayName("SignatureHelper")
    @ParameterizedTest(name="parameter count for \"{0}\" should be {2}")
    @MethodSource("dataProvider")
    public void testGetParameterCount(String descriptor, String expectedSignature, int expectedParameterCount) {
        assertEquals(expectedParameterCount, SignatureHelper.getParameterCount(descriptor));
    }
}
