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

import java.util.*;

import org.jmock.*;
import org.junit.jupiter.api.*;

import com.jeantessier.MockObjectTestCase;

import static org.junit.jupiter.api.Assertions.*;

public class TestDeprecationDetector extends MockObjectTestCase {
    private final DeprecationDetector sut = new DeprecationDetector();

    @Test
    void testVisitClassfile() {
        final Classfile mockClassfile = mock(Classfile.class);
        final Attribute_info mockAttribute = mock(Attribute_info.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).getAttributes();
                will(returnValue(Collections.singleton(mockAttribute)));
            oneOf (mockAttribute).accept(sut);
        }});

        sut.visitClassfile(mockClassfile);
        assertFalse(sut.isDeprecated());
    }

    @Test
    void testVisitField_info() {
        final Field_info mockField = mock(Field_info.class);
        final Attribute_info mockAttribute = mock(Attribute_info.class);

        checking(new Expectations() {{
            oneOf (mockField).getAttributes();
                will(returnValue(Collections.singleton(mockAttribute)));
            oneOf (mockAttribute).accept(sut);
        }});

        sut.visitField_info(mockField);
        assertFalse(sut.isDeprecated());
    }

    @Test
    void testVisitMethod_info() {
        final Method_info mockMethod = mock(Method_info.class);
        final Attribute_info mockAttribute = mock(Attribute_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).getAttributes();
                will(returnValue(Collections.singleton(mockAttribute)));
            oneOf (mockAttribute).accept(sut);
        }});

        sut.visitMethod_info(mockMethod);
        assertFalse(sut.isDeprecated());
    }

    @Test
    void testVisitDeprecated_attribute() {
        Deprecated_attribute mockDeprecated = mock(Deprecated_attribute.class);
        sut.visitDeprecated_attribute(mockDeprecated);
        assertTrue(sut.isDeprecated());
    }

    @Test
    void testVisitAnnotation_Deprecated() {
        final Annotation mockAnnotation = mock(Annotation.class);

        checking(new Expectations() {{
            oneOf (mockAnnotation).getType();
                will(returnValue(Deprecated.class.getName()));
        }});

        sut.visitAnnotation(mockAnnotation);
        assertTrue(sut.isDeprecated(), "Should be deprecated after seeing @Deprecated");
    }

    @Test
    void testVisitAnnotation_DeprecatedIsSticky() {
        final Annotation mockDeprecatedAnnotation = mock(Annotation.class, "@Deprecated");
        final Annotation mockOtherAnnotation = mock(Annotation.class);

        checking(new Expectations() {{
            oneOf (mockDeprecatedAnnotation).getType();
                will(returnValue(Deprecated.class.getName()));
            oneOf (mockOtherAnnotation).getType();
        }});

        sut.visitAnnotation(mockDeprecatedAnnotation);
        sut.visitAnnotation(mockOtherAnnotation);
        assertTrue(sut.isDeprecated(), "Should be deprecated after having seen @Deprecated");
    }

    @Test
    void testVisitAnnotation_NotDeprecated() {
        final Annotation mockAnnotation = mock(Annotation.class);

        checking(new Expectations() {{
            oneOf (mockAnnotation).getType();
                will(returnValue("abc"));
        }});

        sut.visitAnnotation(mockAnnotation);
        assertFalse(sut.isDeprecated(), "Should not be deprecated after seeing random annotation");
    }
}
