/*
 *  Copyright (c) 2001-2009, Jean Tessier
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

public class TestElementValueFactory extends TestAnnotationsBase {
    private static final int CONST_VALUE_INDEX = 2;
    private static final int CONST_VALUE = 3;
    private static final int TYPE_NAME_INDEX = 3;
    private static final int CONST_NAME_INDEX = 4;
    private static final int CLASS_INFO_INDEX = 5;
    private static final int TYPE_INDEX = 6;
    private static final String TYPE = "Labc;";

    private ElementValueFactory sut;

    protected void setUp() throws Exception {
        super.setUp();

        sut = new ElementValueFactory();
    }

    public void testCreateByteConstantElementValue() throws Exception {
        expectLookupInteger(CONST_VALUE_INDEX, CONST_VALUE);
        doTestCreateConstantElementValue('B', ByteConstantElementValue.class);
    }

    public void testCreateCharConstantElementValue() throws Exception {
        expectLookupInteger(CONST_VALUE_INDEX, CONST_VALUE);
        doTestCreateConstantElementValue('C', CharConstantElementValue.class);
    }

    public void testCreateDoubleConstantElementValue() throws Exception {
        expectLookupDouble(CONST_VALUE_INDEX, CONST_VALUE);
        doTestCreateConstantElementValue('D', DoubleConstantElementValue.class);
    }

    public void testCreateFloatConstantElementValue() throws Exception {
        expectLookupFloat(CONST_VALUE_INDEX, CONST_VALUE);
        doTestCreateConstantElementValue('F', FloatConstantElementValue.class);
    }

    public void testCreateIntegerConstantElementValue() throws Exception {
        expectLookupInteger(CONST_VALUE_INDEX, CONST_VALUE);
        doTestCreateConstantElementValue('I', IntegerConstantElementValue.class);
    }

    public void testCreateLongConstantElementValue() throws Exception {
        expectLookupLong(CONST_VALUE_INDEX, CONST_VALUE);
        doTestCreateConstantElementValue('J', LongConstantElementValue.class);
    }

    public void testCreateShortConstantElementValue() throws Exception {
        expectLookupInteger(CONST_VALUE_INDEX, CONST_VALUE);
        doTestCreateConstantElementValue('S', ShortConstantElementValue.class);
    }

    public void testCreateBooleanConstantElementValue() throws Exception {
        expectLookupInteger(CONST_VALUE_INDEX, CONST_VALUE);
        doTestCreateConstantElementValue('Z', BooleanConstantElementValue.class);
    }

    public void testCreateStringConstantElementValue() throws Exception {
        expectLookupUtf8(CONST_VALUE_INDEX, "abc");
        doTestCreateConstantElementValue('s', StringConstantElementValue.class);
    }

    public void testCreateEnumElementValue() throws Exception {
        expectReadTag('e');
        expectReadU2(TYPE_NAME_INDEX);
        expectReadU2(CONST_NAME_INDEX);

        ElementValue elementValue = sut.create(mockConstantPool, mockIn);
        assertNotNull("ElementValueFactory returned null", elementValue);
        assertTrue("Not a " + EnumElementValue.class.getSimpleName(), EnumElementValue.class.isInstance(elementValue));
        assertEquals("Type name index", TYPE_NAME_INDEX, ((EnumElementValue) elementValue).getTypeNameIndex());
        assertEquals("Const name index", CONST_NAME_INDEX, ((EnumElementValue) elementValue).getConstNameIndex());
    }

    public void testCreateClassElementValue() throws Exception {
        expectReadTag('c');
        expectReadClassInfoIndex(CLASS_INFO_INDEX);

        ElementValue elementValue = sut.create(mockConstantPool, mockIn);
        assertNotNull("ElementValueFactory returned null", elementValue);
        assertTrue("Not a " + ClassElementValue.class.getSimpleName(), ClassElementValue.class.isInstance(elementValue));
        assertEquals("Class info index", CLASS_INFO_INDEX, ((ClassElementValue) elementValue).getClassInfoIndex());
    }

    public void testCreateAnnotationElementValue() throws Exception {
        expectReadTag('@');
        expectReadTypeIndex(TYPE_INDEX);
        expectLookupUtf8(TYPE_INDEX, TYPE);
        expectReadNumElementValuePairs(0);

        ElementValue elementValue = sut.create(mockConstantPool, mockIn);
        assertNotNull("ElementValueFactory returned null", elementValue);
        assertTrue("Not a " + AnnotationElementValue.class.getSimpleName(), AnnotationElementValue.class.isInstance(elementValue));
        assertNotNull("Annotation value", ((AnnotationElementValue) elementValue).getAnnotation());
        assertEquals("Type index", TYPE_INDEX, ((AnnotationElementValue) elementValue).getAnnotation().getTypeIndex());
        assertEquals("Number of element value pairs", 0, ((AnnotationElementValue) elementValue).getAnnotation().getElementValuePairs().size());
    }

    public void testCreateArrayElementValue() throws Exception {
        expectReadTag('[');
        expectReadNumValues(0);

        ElementValue elementValue = sut.create(mockConstantPool, mockIn);
        assertNotNull("ElementValueFactory returned null", elementValue);
        assertTrue("Not a " + ArrayElementValue.class.getSimpleName(), ArrayElementValue.class.isInstance(elementValue));
        assertNotNull("Number of element values", ((ArrayElementValue) elementValue).getValues().size());
    }

    public void testCreateWithUnknownTag() throws Exception {
        expectReadTag('A');

        try {
            sut.create(mockConstantPool, mockIn);
            fail("Did not fail on illegal tag value");
        } catch (IOException ex) {
            // Expected
        }
    }

    private void doTestCreateConstantElementValue(char tag, Class<? extends ElementValue> elementValueClass) throws IOException {
        expectReadTag(tag);
        expectReadU2(CONST_VALUE_INDEX);

        ElementValue elementValue = sut.create(mockConstantPool, mockIn);
        assertNotNull("ElementValueFactory returned null", elementValue);
        assertTrue("Not a " + elementValueClass.getSimpleName(), elementValueClass.isInstance(elementValue));
        assertEquals("Const value index", CONST_VALUE_INDEX, ((ConstantElementValue) elementValue).getConstValueIndex());
    }
}
