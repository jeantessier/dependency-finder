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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        TestInterfaceMethodRef_info.class,
        TestField_info.class,
        TestMethod_info.class,
        TestInstruction.class,
        TestConstantValue_attribute.class,
        TestCode_attribute.class,
        TestExceptions_attribute.class,
        TestInnerClasses_attribute.class,
        TestEnclosingMethod_attribute.class,
        TestSynthetic_attribute.class,
        TestSignature_attribute.class,
        TestSourceFile_attribute.class,
        TestSourceDebugExtension_attribute.class,
        TestLineNumberTable_attribute.class,
        TestLocalVariableTable_attribute.class,
        TestLocalVariableTypeTable_attribute.class,
        TestDeprecated_attribute.class,
        TestByteConstantElementValue.class,
        TestCharConstantElementValue.class,
        TestDoubleConstantElementValue.class,
        TestFloatConstantElementValue.class,
        TestIntegerConstantElementValue.class,
        TestLongConstantElementValue.class,
        TestShortConstantElementValue.class,
        TestBooleanConstantElementValue.class,
        TestStringConstantElementValue.class,
        TestEnumElementValue.class,
        TestClassElementValue.class,
        TestAnnotationElementValue.class,
        TestArrayElementValue.class,
        TestElementValueFactory.class,
        TestArrayElementValueWithContent.class,
        TestElementValuePair.class,
        TestAnnotation.class,
        TestAnnotationWithElementValues.class,
        TestRuntimeVisibleAnnotations_attribute.class,
        TestRuntimeVisibleAnnotations_attributeWithAnnotations.class,
        TestRuntimeInvisibleAnnotations_attribute.class,
        TestRuntimeInvisibleAnnotations_attributeWithAnnotations.class,
        TestParameter.class,
        TestParameterWithAnnotations.class,
        TestRuntimeVisibleParameterAnnotations_attribute.class,
        TestRuntimeVisibleParameterAnnotations_attributeWithAnnotations.class,
        TestRuntimeInvisibleParameterAnnotations_attribute.class,
        TestRuntimeInvisibleParameterAnnotations_attributeWithAnnotations.class,
        TestAnnotationDefault_attribute.class,
        TestCustom_attribute.class,
        TestAttributeFactory.class,
        TestClass_info.class,
        TestClassfile.class
})
public class TestAll {}
