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

package com.jeantessier.text;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestRegularExpressionParser {
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("normal RE", "/test/", List.of("/test/")),
                arguments("multiple matches", "/test/g", List.of("/test/g")),
                arguments("ignore case", "/test/i", List.of("/test/i")),
                arguments("multiline", "/test/m", List.of("/test/m")),
                arguments("multiple modifiers", "/test/gim", List.of("/test/gim")),
                arguments("unknown modifier", "/test/a", List.of("/test/")),
                arguments("broken RE", "/test", List.of("/test")),
                arguments("multiple REs", "/test1/,/test2/", List.of("/test1/", "/test2/")),
                arguments("multiple REs with space", "/test1/, /test2/", List.of("/test1/", "/test2/")),
                arguments("multiple REs with spaces", " /test1/, /test2/ ", List.of("/test1/", "/test2/")),
                arguments("embedded separator", "/test1\\/test2/", List.of("/test1\\/test2/")),
                arguments("custom separator", "m=test=", List.of("m=test=")),
                arguments("false start", "m", Collections.emptyList()),
                arguments("not an RE", "test", Collections.emptyList()),
                arguments("empty string", "", Collections.emptyList())
        );
    }

    @DisplayName("RegularExpressionParser")
    @ParameterizedTest(name="when the input is {0}")
    @MethodSource("dataProvider")
    void test(String variation, String inputs, List<String> expected) {
        var actual = RegularExpressionParser.parseRE(inputs);

        assertLinesMatch(expected, actual);
    }
}
