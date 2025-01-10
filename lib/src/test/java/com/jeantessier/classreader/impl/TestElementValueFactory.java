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

import java.io.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestElementValueFactory extends TestAnnotationsBase {
    private static final int CONST_VALUE_INDEX = 1;
    private static final int CONST_VALUE = 2;
    private static final int TYPE_NAME_INDEX = 3;
    private static final String ENCODED_TYPE_NAME = "LAbc;";
    private static final int CONST_NAME_INDEX = 4;
    private static final String CONST_NAME = "DEF";
    private static final int CLASS_INFO_INDEX = 5;
    private static final String CLASS_INFO = "LAbc;";
    private static final int TYPE_INDEX = 6;
    private static final String TYPE = "Labc;";

    private final ElementValueFactory sut = new ElementValueFactory();

    @Test
    void testCreateByteConstantElementValue() throws Exception {
        expectLookupInteger(CONST_VALUE_INDEX, CONST_VALUE);
        doTestCreateConstantElementValue('B', ByteConstantElementValue.class);
    }

    @Test
    void testCreateCharConstantElementValue() throws Exception {
        expectLookupInteger(CONST_VALUE_INDEX, CONST_VALUE);
        doTestCreateConstantElementValue('C', CharConstantElementValue.class);
    }

    @Test
    void testCreateDoubleConstantElementValue() throws Exception {
        expectLookupDouble(CONST_VALUE_INDEX, CONST_VALUE);
        doTestCreateConstantElementValue('D', DoubleConstantElementValue.class);
    }

    @Test
    void testCreateFloatConstantElementValue() throws Exception {
        expectLookupFloat(CONST_VALUE_INDEX, CONST_VALUE);
        doTestCreateConstantElementValue('F', FloatConstantElementValue.class);
    }

    @Test
    void testCreateIntegerConstantElementValue() throws Exception {
        expectLookupInteger(CONST_VALUE_INDEX, CONST_VALUE);
        doTestCreateConstantElementValue('I', IntegerConstantElementValue.class);
    }

    @Test
    void testCreateLongConstantElementValue() throws Exception {
        expectLookupLong(CONST_VALUE_INDEX, CONST_VALUE);
        doTestCreateConstantElementValue('J', LongConstantElementValue.class);
    }

    @Test
    void testCreateShortConstantElementValue() throws Exception {
        expectLookupInteger(CONST_VALUE_INDEX, CONST_VALUE);
        doTestCreateConstantElementValue('S', ShortConstantElementValue.class);
    }

    @Test
    void testCreateBooleanConstantElementValue() throws Exception {
        expectLookupInteger(CONST_VALUE_INDEX, CONST_VALUE);
        doTestCreateConstantElementValue('Z', BooleanConstantElementValue.class);
    }

    @Test
    void testCreateStringConstantElementValue() throws Exception {
        expectLookupUtf8(CONST_VALUE_INDEX, "abc");
        doTestCreateConstantElementValue('s', StringConstantElementValue.class);
    }

    @Test
    void testCreateEnumElementValue() throws Exception {
        expectReadTag('e');
        expectReadU2(TYPE_NAME_INDEX);
        expectLookupUtf8(TYPE_NAME_INDEX, ENCODED_TYPE_NAME, "lookup type name during construction");
        expectReadU2(CONST_NAME_INDEX);
        expectLookupUtf8(CONST_NAME_INDEX, CONST_NAME, "lookup const name during construction");

        ElementValue elementValue = sut.create(mockConstantPool, mockIn);
        assertNotNull(elementValue, "ElementValueFactory returned null");
        assertInstanceOf(EnumElementValue.class, elementValue);
        assertEquals(TYPE_NAME_INDEX, ((EnumElementValue) elementValue).getTypeNameIndex(), "Type name index");
        assertEquals(CONST_NAME_INDEX, ((EnumElementValue) elementValue).getConstNameIndex(), "Const name index");
    }

    @Test
    void testCreateClassElementValue() throws Exception {
        expectReadTag('c');
        expectReadClassInfoIndex(CLASS_INFO_INDEX);
        expectLookupUtf8(CLASS_INFO_INDEX, CLASS_INFO, "lookup during construction");

        ElementValue elementValue = sut.create(mockConstantPool, mockIn);
        assertNotNull(elementValue, "ElementValueFactory returned null");
        assertInstanceOf(ClassElementValue.class, elementValue);
        assertEquals(CLASS_INFO_INDEX, ((ClassElementValue) elementValue).getClassInfoIndex(), "Class info index");
    }

    @Test
    void testCreateAnnotationElementValue() throws Exception {
        expectReadTag('@');
        expectReadTypeIndex(TYPE_INDEX);
        expectLookupUtf8(TYPE_INDEX, TYPE);
        expectReadNumElementValuePairs(0);

        ElementValue elementValue = sut.create(mockConstantPool, mockIn);
        assertNotNull(elementValue, "ElementValueFactory returned null");
        assertInstanceOf(AnnotationElementValue.class, elementValue);
        assertNotNull(((AnnotationElementValue) elementValue).getAnnotation(), "Annotation value");
        assertEquals(TYPE_INDEX, ((AnnotationElementValue) elementValue).getAnnotation().getTypeIndex(), "Type index");
        assertEquals(0, ((AnnotationElementValue) elementValue).getAnnotation().getElementValuePairs().size(), "Number of element value pairs");
    }

    @Test
    void testCreateArrayElementValue() throws Exception {
        expectReadTag('[');
        expectReadNumValues(0);

        ElementValue elementValue = sut.create(mockConstantPool, mockIn);
        assertNotNull(elementValue, "ElementValueFactory returned null");
        assertInstanceOf(ArrayElementValue.class, elementValue);
        assertEquals(0, ((ArrayElementValue) elementValue).getValues().size(), "Number of element values");
    }

    @Test
    void testCreateWithUnknownTag() throws Exception {
        expectReadTag('A');

        assertThrows(IOException.class, () -> sut.create(mockConstantPool, mockIn));
    }

    private void doTestCreateConstantElementValue(char tag, Class<? extends ElementValue> elementValueClass) throws IOException {
        expectReadTag(tag);
        expectReadU2(CONST_VALUE_INDEX);

        ElementValue elementValue = sut.create(mockConstantPool, mockIn);
        assertNotNull(elementValue, "ElementValueFactory returned null");
        assertInstanceOf(elementValueClass, elementValue);
        assertEquals(CONST_VALUE_INDEX, ((ConstantElementValue) elementValue).getConstValueIndex(), "Const value index");
    }
}
