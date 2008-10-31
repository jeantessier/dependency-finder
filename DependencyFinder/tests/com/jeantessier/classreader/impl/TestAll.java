/*
 *  Copyright (c) 2001-2008, Jean Tessier
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

import junit.framework.*;

public class TestAll extends TestCase {
    public static Test suite() {
        TestSuite result = new TestSuite(TestAll.class.getPackage().getName());

        result.addTestSuite(TestInterfaceMethodRef_info.class);
        result.addTestSuite(TestInstruction.class);
        result.addTestSuite(TestInstructionWithConstantPool.class);
        result.addTestSuite(TestDeprecated_attribute.class);
        result.addTestSuite(TestSynthetic_attribute.class);
        result.addTestSuite(TestSourceDebugExtension_attribute.class);
        result.addTestSuite(TestByteConstantElementValue.class);
        result.addTestSuite(TestCharConstantElementValue.class);
        result.addTestSuite(TestDoubleConstantElementValue.class);
        result.addTestSuite(TestFloatConstantElementValue.class);
        result.addTestSuite(TestIntegerConstantElementValue.class);
        result.addTestSuite(TestLongConstantElementValue.class);
        result.addTestSuite(TestShortConstantElementValue.class);
        result.addTestSuite(TestBooleanConstantElementValue.class);
        result.addTestSuite(TestStringConstantElementValue.class);
        result.addTestSuite(TestEnumElementValue.class);
        result.addTestSuite(TestClassElementValue.class);
        result.addTestSuite(TestAnnotationElementValue.class);
        result.addTestSuite(TestArrayElementValue.class);
        result.addTestSuite(TestElementValueFactory.class);
        result.addTestSuite(TestArrayElementValueWithContent.class);
        result.addTestSuite(TestAnnotation.class);
        result.addTestSuite(TestRuntimeVisibleAnnotations_attribute.class);
        result.addTestSuite(TestRuntimeInvisibleAnnotations_attribute.class);
        result.addTestSuite(TestParameter.class);
        result.addTestSuite(TestRuntimeVisibleParameterAnnotations_attribute.class);
        result.addTestSuite(TestRuntimeInvisibleParameterAnnotations_attribute.class);

        return result;
    }
}
