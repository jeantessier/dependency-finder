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

package com.jeantessier.classreader;

import org.jmock.*;
import org.junit.jupiter.api.*;

import com.jeantessier.MockObjectTestCase;

public class TestSymbolGathererStrategyDecorator extends MockObjectTestCase {
    private final SymbolGathererStrategy mockStrategy = mock(SymbolGathererStrategy.class);

    private final SymbolGathererStrategyDecorator sut = new SymbolGathererStrategyDecorator(mockStrategy);

    @Test
    void testIsMatching_class() {
        var mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockStrategy).isMatching(mockClassfile);
        }});

        sut.isMatching(mockClassfile);
    }

    @Test
    void testIsMatching_field() {
        var mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockStrategy).isMatching(mockField);
        }});

        sut.isMatching(mockField);
    }

    @Test
    void testIsMatching_method() {
        var mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockStrategy).isMatching(mockMethod);
        }});

        sut.isMatching(mockMethod);
    }

    @Test
    void testIsMatching_local() {
        var mockLocalVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            oneOf (mockStrategy).isMatching(mockLocalVariable);
        }});

        sut.isMatching(mockLocalVariable);
    }

    @Test
    void testIsMatching_innerClass() {
        var mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockStrategy).isMatching(mockInnerClass);
        }});

        sut.isMatching(mockInnerClass);
    }
}
