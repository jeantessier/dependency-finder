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

import org.jmock.*;
import org.junit.jupiter.api.*;

import com.jeantessier.MockObjectTestCase;

import static org.junit.jupiter.api.Assertions.*;

public class TestNonPrivateFieldSymbolGathererStrategy extends MockObjectTestCase {
    private final NonPrivateFieldSymbolGathererStrategy sut = new NonPrivateFieldSymbolGathererStrategy();

    @Test
    void testIsMatching_class() {
        Classfile mockClassfile = mock(Classfile.class);
        assertFalse(sut.isMatching(mockClassfile), "Should not match classes");
    }

    @Test
    void testIsMatching_field_privatenormal() {
        var mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isPrivate(); will(returnValue(true));
        }});

        assertFalse(sut.isMatching(mockField), "Should not match normal, private fields");
    }

    @Test
    void testIsMatching_field_publicstatic() {
        var mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isPrivate(); will(returnValue(false));
            oneOf (mockField).isStatic(); will(returnValue(true));
        }});

        assertFalse(sut.isMatching(mockField), "Should not match public static fields");
    }

    @Test
    void testIsMatching_field_synthetic() {
        var mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isPrivate(); will(returnValue(false));
            oneOf (mockField).isStatic(); will(returnValue(false));
            oneOf (mockField).isSynthetic(); will(returnValue(true));
        }});

        assertFalse(sut.isMatching(mockField), "Should not match synthetic fields");
    }

    @Test
    void testIsMatching_field_publicnormal() {
        var mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isPrivate(); will(returnValue(false));
            oneOf (mockField).isStatic(); will(returnValue(false));
            oneOf (mockField).isSynthetic(); will(returnValue(false));
        }});

        assertTrue(sut.isMatching(mockField), "Should have matched public normal fields");
    }

    @Test
    void testIsMatching_method() {
        Method_info mockMethod = mock(Method_info.class);
        assertFalse(sut.isMatching(mockMethod), "Should not match methods");
    }

    @Test
    void testIsMatching_local() {
        LocalVariable mockLocalVariable = mock(LocalVariable.class);
        assertFalse(sut.isMatching(mockLocalVariable), "Should not match local variables");
    }

    @Test
    void testIsMatching_innerClass() {
        InnerClass mockInnerClass = mock(InnerClass.class);
        assertFalse(sut.isMatching(mockInnerClass), "Should not match inner classes");
    }
}
