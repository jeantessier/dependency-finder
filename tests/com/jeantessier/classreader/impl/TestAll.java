/*
 *  Copyright (c) 2001-2023, Jean Tessier
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
        TestAnnotation.class,
        TestAnnotationDefault_attribute.class,
        TestAnnotationElementValue.class,
        TestAnnotationWithElementValues.class,
        TestArrayElementValue.class,
        TestArrayElementValueWithContent.class,
        TestAttributeFactory.class,
        TestBooleanConstantElementValue.class,
        TestBootstrapMethod.class,
        TestBootstrapMethodWithMultipleArguments.class,
        TestBootstrapMethodWithOneArgument.class,
        TestBootstrapMethods_attribute.class,
        TestByteConstantElementValue.class,
        TestCharConstantElementValue.class,
        TestClassElementValue.class,
        TestClass_info.class,
        TestClassfile.class,
        TestCode_attribute.class,
        TestConstantValue_attribute.class,
        TestCustom_attribute.class,
        TestDeprecated_attribute.class,
        TestDoubleConstantElementValue.class,
        TestElementValueFactory.class,
        TestElementValuePair.class,
        TestEnclosingMethod_attribute.class,
        TestEnumElementValue.class,
        TestExceptions_attribute.class,
        TestField_info.class,
        TestFloatConstantElementValue.class,
        TestFrameType_create.class,
        TestInnerClasses_attribute.class,
        TestInstruction.class,
        TestIntegerConstantElementValue.class,
        TestInterfaceMethodRef_info.class,
        TestLineNumberTable_attribute.class,
        TestLocalVariableTable_attribute.class,
        TestLocalVariableTypeTable_attribute.class,
        TestLongConstantElementValue.class,
        TestMethod_info.class,
        TestObjectVariableInfo.class,
        TestParameter.class,
        TestParameterWithAnnotations.class,
        TestRuntimeInvisibleAnnotations_attribute.class,
        TestRuntimeInvisibleAnnotations_attributeWithAnnotations.class,
        TestRuntimeInvisibleParameterAnnotations_attribute.class,
        TestRuntimeInvisibleParameterAnnotations_attributeWithAnnotations.class,
        TestRuntimeVisibleAnnotations_attribute.class,
        TestRuntimeVisibleAnnotations_attributeWithAnnotations.class,
        TestRuntimeVisibleParameterAnnotations_attribute.class,
        TestRuntimeVisibleParameterAnnotations_attributeWithAnnotations.class,
        TestShortConstantElementValue.class,
        TestSignature_attribute.class,
        TestSourceDebugExtension_attribute.class,
        TestSourceFile_attribute.class,
        TestStackMapFrameFactory_Invalid.class,
        TestStackMapFrameFactory_create.class,
        TestStringConstantElementValue.class,
        TestSynthetic_attribute.class,
        TestUninitializedVariableInfo.class,
        TestVerificationTypeInfoFactory_Invalid.class,
        TestVerificationTypeInfoFactory_create.class,
        TestVerificationType_Invalid.class,
        TestVerificationType_create.class,
        TestVerificationType_forTag.class,
})
public class TestAll {}
