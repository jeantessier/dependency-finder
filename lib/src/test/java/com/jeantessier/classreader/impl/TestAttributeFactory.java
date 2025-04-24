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

package com.jeantessier.classreader.impl;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestAttributeFactory extends TestAttributeBase {
    private static final int ATTRIBUTE_NAME_INDEX = 2;

    private final AttributeFactory sut = new AttributeFactory();

    @Test
    void testCreateRuntimeVisibleAnnotations_attribute() throws Exception {
        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.RUNTIME_VISIBLE_ANNOTATIONS.getAttributeName());
        expectReadAttributeLength(2);
        expectReadNumAnnotations(0);

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull(attribute, "AttributeFactory returned null");
        assertInstanceOf(RuntimeVisibleAnnotations_attribute.class, attribute);
        assertEquals(0, ((RuntimeAnnotations_attribute) attribute).getAnnotations().size(), "Num annotations");
    }

    @Test
    void testCreateRuntimeInvisibleAnnotations_attribute() throws Exception {
        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.RUNTIME_INVISIBLE_ANNOTATIONS.getAttributeName());
        expectReadAttributeLength(2);
        expectReadNumAnnotations(0);

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull(attribute, "AttributeFactory returned null");
        assertInstanceOf(RuntimeInvisibleAnnotations_attribute.class, attribute);
        assertEquals(0, ((RuntimeAnnotations_attribute) attribute).getAnnotations().size(), "Num annotations");
    }

    @Test
    void testCreateRuntimeVisibleParameterAnnotations_attribute() throws Exception {
        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS.getAttributeName());
        expectReadAttributeLength(2);
        expectReadNumParameters(0);

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull(attribute, "AttributeFactory returned null");
        assertInstanceOf(RuntimeVisibleParameterAnnotations_attribute.class, attribute);
        assertEquals(0, ((RuntimeParameterAnnotations_attribute) attribute).getParameterAnnotations().size(), "Num parameter annotations");
    }

    @Test
    void testCreateRuntimeInvisibleParameterAnnotations_attribute() throws Exception {
        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS.getAttributeName());
        expectReadAttributeLength(2);
        expectReadNumParameters(0);

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull(attribute, "AttributeFactory returned null");
        assertInstanceOf(RuntimeInvisibleParameterAnnotations_attribute.class, attribute);
        assertEquals(0, ((RuntimeParameterAnnotations_attribute) attribute).getParameterAnnotations().size(), "Num parameter annotations");
    }

    @Test
    void testCreateAnnotationDefault_attribute() throws Exception {
        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.ANNOTATION_DEFAULT.getAttributeName());
        expectReadAttributeLength(3);
        expectReadU1('B');
        expectReadU2(2);
        expectLookupInteger(2, 3);

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull(attribute, "AttributeFactory returned null");
        assertInstanceOf(AnnotationDefault_attribute.class, attribute);
        ElementValue elementValue = ((AnnotationDefault_attribute) attribute).getElemementValue();
        assertNotNull(elementValue, "Element value is null");
        assertInstanceOf(ByteConstantElementValue.class, elementValue);
    }

    @Test
    void testCreateModule_attribute() throws Exception {
        var moduleNameIndex = 123;
        var moduleName = "abc";
        var moduleFlags = 456;
        var moduleVersionIndex = 789;
        var moduleVersion = "blah";

        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.MODULE.getAttributeName(), "attribute name");
        expectReadAttributeLength(18);
        expectReadU2(moduleNameIndex);
        allowingLookupModule(moduleNameIndex, moduleName, "module name");
        expectReadU2(moduleFlags);
        expectReadU2(moduleVersionIndex);
        allowingLookupUtf8(moduleVersionIndex, moduleVersion, "module version");
        expectReadU2(0);
        expectReadU2(0);
        expectReadU2(0);
        expectReadU2(0);
        expectReadU2(0);

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull(attribute, "AttributeFactory returned null");
        assertInstanceOf(Module_attribute.class, attribute);
        assertEquals(moduleNameIndex, ((Module_attribute) attribute).getModuleNameIndex(), "Module name index");
    }

    @Test
    void testCreateModulePackages_attribute() throws Exception {
        var numPackages = 0;

        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.MODULE_PACKAGES.getAttributeName(), "attribute name");
        expectReadAttributeLength(2);
        expectReadU2(numPackages);

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull(attribute, "AttributeFactory returned null");
        assertInstanceOf(ModulePackages_attribute.class, attribute);
        assertEquals(numPackages, ((ModulePackages_attribute) attribute).getPackages().size(), "number of packages");
    }

    @Test
    void testCreateModuleMainClass_attribute() throws Exception {
        var mainClassIndex = 123;
        var mainClassName = "Abc";

        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.MODULE_MAIN_CLASS.getAttributeName(), "attribute name");
        expectReadAttributeLength(2);
        expectReadU2(mainClassIndex);
        expectLookupClass(mainClassIndex, mainClassName, "lookup during construction");

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull(attribute, "AttributeFactory returned null");
        assertInstanceOf(ModuleMainClass_attribute.class, attribute);
        assertEquals(mainClassIndex, ((ModuleMainClass_attribute) attribute).getMainClassIndex(), "main class index");
    }

    @Test
    void testCreateNestHost_attribute() throws Exception {
        var hostClassIndex = 123;
        var hostClassName = "Abc";

        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.NEST_HOST.getAttributeName(), "attribute name");
        expectReadAttributeLength(2);
        expectReadU2(hostClassIndex);
        expectLookupClass(hostClassIndex, hostClassName, "lookup during construction");

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull(attribute, "AttributeFactory returned null");
        assertInstanceOf(NestHost_attribute.class, attribute);
        assertEquals(hostClassIndex, ((NestHost_attribute) attribute).getHostClassIndex(), "host class index");
    }

    @Test
    void testCreateNestMembers_attribute() throws Exception {
        var numNestMembers = 0;

        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.NEST_MEMBERS.getAttributeName(), "attribute name");
        expectReadAttributeLength(2);
        expectReadU2(numNestMembers);

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull(attribute, "AttributeFactory returned null");
        assertInstanceOf(NestMembers_attribute.class, attribute);
        assertEquals(numNestMembers, ((NestMembers_attribute) attribute).getMembers().size(), "number of nest members");
    }

    @Test
    void testCreateRecord_attribute() throws Exception {
        var numRecordComponents = 0;

        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.RECORD.getAttributeName(), "attribute name");
        expectReadAttributeLength(2);
        expectReadU2(numRecordComponents);

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull(attribute, "AttributeFactory returned null");
        assertInstanceOf(Record_attribute.class, attribute);
        assertEquals(numRecordComponents, ((Record_attribute) attribute).getRecordComponents().size(), "number of record components");
    }

    @Test
    void testCreatePermittedSubclasses_attribute() throws Exception {
        var numSubclasses = 0;

        expectReadU2(ATTRIBUTE_NAME_INDEX);
        expectLookupUtf8(ATTRIBUTE_NAME_INDEX, AttributeType.PERMITTED_SUBCLASSES.getAttributeName(), "attribute name");
        expectReadAttributeLength(2);
        expectReadU2(numSubclasses);

        Attribute_info attribute = sut.create(mockConstantPool, mockOwner, mockIn);
        assertNotNull(attribute, "AttributeFactory returned null");
        assertInstanceOf(PermittedSubclasses_attribute.class, attribute);
        assertEquals(numSubclasses, ((PermittedSubclasses_attribute) attribute).getSubclasses().size(), "number of subclasses");
    }
}
