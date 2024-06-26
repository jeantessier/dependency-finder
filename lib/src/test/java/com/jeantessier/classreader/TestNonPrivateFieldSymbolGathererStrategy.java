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

import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;

public class TestNonPrivateFieldSymbolGathererStrategy extends MockObjectTestCase {
    private NonPrivateFieldSymbolGathererStrategy sut;

    protected void setUp() throws Exception {
        super.setUp();

        sut = new NonPrivateFieldSymbolGathererStrategy();
    }

    public void testIsMatching_class() {
        Classfile mockClassfile = mock(Classfile.class);
        assertFalse("Should not match classes", sut.isMatching(mockClassfile));
    }

    public void testIsMatching_field_privatenormal() {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isPrivate(); will(returnValue(true));
        }});

        assertFalse("Should not match normal, private fields", sut.isMatching(mockField));
    }

    public void testIsMatching_field_publicstatic() {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isPrivate(); will(returnValue(false));
            oneOf (mockField).isStatic(); will(returnValue(true));
        }});

        assertFalse("Should not match public static fields", sut.isMatching(mockField));
    }

    public void testIsMatching_field_synthetic() {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isPrivate(); will(returnValue(false));
            oneOf (mockField).isStatic(); will(returnValue(false));
            oneOf (mockField).isSynthetic(); will(returnValue(true));
        }});

        assertFalse("Should not match synthetic fields", sut.isMatching(mockField));
    }

    public void testIsMatching_field_publicnormal() {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isPrivate(); will(returnValue(false));
            oneOf (mockField).isStatic(); will(returnValue(false));
            oneOf (mockField).isSynthetic(); will(returnValue(false));
        }});

        assertTrue("Should have matched public normal fields", sut.isMatching(mockField));
    }

    public void testIsMatching_method() {
        Method_info mockMethod = mock(Method_info.class);
        assertFalse("Should not match methods", sut.isMatching(mockMethod));
    }

    public void testIsMatching_local() {
        LocalVariable mockLocalVariable = mock(LocalVariable.class);
        assertFalse("Should not match local variables", sut.isMatching(mockLocalVariable));
    }

    public void testIsMatching_innerClass() {
        InnerClass mockInnerClass = mock(InnerClass.class);
        assertFalse("Should not match inner classes", sut.isMatching(mockInnerClass));
    }
}
