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

public class TestAttributeFactory extends TestAttributeBase {
    private static final int ATTRIBUTE_NAME_INDEX = 2;

    private AttributeFactory sut;

    protected void setUp() throws Exception {
        super.setUp();

        sut = new AttributeFactory();
    }

    public void testCreateRuntimeVisibleAnnotations_attribute() throws Exception {
        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.RUNTIME_VISIBLE_ANNOTATIONS.getAttributeName());
        expectReadAttributeLength(2);
        expectReadNumAnnotations(0);

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull("AttributeFactory returned null", attribute);
        assertTrue("Not a " + RuntimeVisibleAnnotations_attribute.class.getSimpleName(), RuntimeVisibleAnnotations_attribute.class.isInstance(attribute));
        assertEquals("Num annotations", 0, ((RuntimeAnnotations_attribute) attribute).getAnnotations().size());
    }

    public void testCreateRuntimeInvisibleAnnotations_attribute() throws Exception {
        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.RUNTIME_INVISIBLE_ANNOTATIONS.getAttributeName());
        expectReadAttributeLength(2);
        expectReadNumAnnotations(0);

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull("AtributeFactory returned null", attribute);
        assertTrue("Not a " + RuntimeInvisibleAnnotations_attribute.class.getSimpleName(), RuntimeInvisibleAnnotations_attribute.class.isInstance(attribute));
        assertEquals("Num annotations", 0, ((RuntimeAnnotations_attribute) attribute).getAnnotations().size());
    }

    public void testCreateRuntimeVisibleParameterAnnotations_attribute() throws Exception {
        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS.getAttributeName());
        expectReadAttributeLength(2);
        expectReadNumParameters(0);

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull("AttributeFactory returned null", attribute);
        assertTrue("Not a " + RuntimeVisibleParameterAnnotations_attribute.class.getSimpleName(), RuntimeVisibleParameterAnnotations_attribute.class.isInstance(attribute));
        assertEquals("Num parameter annotations", 0, ((RuntimeParameterAnnotations_attribute) attribute).getParameterAnnotations().size());
    }

    public void testCreateRuntimeInvisibleParameterAnnotations_attribute() throws Exception {
        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS.getAttributeName());
        expectReadAttributeLength(2);
        expectReadNumParameters(0);

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull("AtributeFactory returned null", attribute);
        assertTrue("Not a " + RuntimeInvisibleParameterAnnotations_attribute.class.getSimpleName(), RuntimeInvisibleParameterAnnotations_attribute.class.isInstance(attribute));
        assertEquals("Num parameter annotations", 0, ((RuntimeParameterAnnotations_attribute) attribute).getParameterAnnotations().size());
    }

    public void testCreateAnnotationDefault_attribute() throws Exception {
        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.ANNOTATION_DEFAULT.getAttributeName());
        expectReadAttributeLength(3);
        expectReadU1('B');
        expectReadU2(2);
        expectLookupInteger(2, 3);

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull("AtributeFactory returned null", attribute);
        assertTrue("Not a " + AnnotationDefault_attribute.class.getSimpleName(), AnnotationDefault_attribute.class.isInstance(attribute));
        ElementValue elementValue = ((AnnotationDefault_attribute) attribute).getElemementValue();
        assertNotNull("Element value is null", elementValue);
        assertTrue("Element value not the expected " + ByteConstantElementValue.class.getSimpleName(), ByteConstantElementValue.class.isInstance(elementValue));
    }
}