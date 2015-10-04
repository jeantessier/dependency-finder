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

package com.jeantessier.classreader;

import java.util.*;

import org.jmock.*;
import org.jmock.integration.junit3.*;

public class TestVisitorBase extends MockObjectTestCase {
    private VisitorBase sut;

    protected void setUp() throws Exception {
        sut = new VisitorBase() {};
    }

    public void testIncrementCount() {
        int oldValue = sut.currentCount();
        sut.incrementCount();
        int newValue = sut.currentCount();
        assertEquals("count", oldValue + 1, newValue);
    }

    public void testResetCount() {
        sut.incrementCount();
        assertTrue("count should not be zero", sut.currentCount() != 0);
        sut.resetCount();
        assertEquals("count", 0, sut.currentCount());
    }

    public void testVisitConstantPool() {
        final ConstantPool mockConstantPool = mock(ConstantPool.class);
        final ConstantPoolEntry mockEntry = mock(ConstantPoolEntry.class);

        checking(new Expectations() {{
            one (mockConstantPool).iterator();
                will(returnIterator(mockEntry));
            one (mockEntry).accept(sut);
        }});

        sut.visitConstantPool(mockConstantPool);
        assertEquals("current count", 1, sut.currentCount());
    }

    public void testVisitConstantPool_ResetCountBetweenCalls() {
        final ConstantPool mockConstantPool = mock(ConstantPool.class);
        final ConstantPoolEntry mockEntry = mock(ConstantPoolEntry.class);

        checking(new Expectations() {{
            exactly(2).of (mockConstantPool).iterator();
                will(returnIterator(mockEntry));
            exactly(2).of (mockEntry).accept(sut);
        }});

        sut.visitConstantPool(mockConstantPool);
        sut.visitConstantPool(mockConstantPool);
        assertEquals("current count", 1, sut.currentCount());
    }

    public void testVisitClassfiles() {
        final Classfile mockClassfile1 = mock(Classfile.class, "classfile1");
        final Classfile mockClassfile2 = mock(Classfile.class, "classfile2");

        Collection<Classfile> classfiles = new ArrayList<Classfile>();
        classfiles.add(mockClassfile1);
        classfiles.add(mockClassfile2);

        checking(new Expectations() {{
            one (mockClassfile1).accept(sut);
            one (mockClassfile2).accept(sut);
        }});

        sut.visitClassfiles(classfiles);
    }

    public void testVisitClassfile() {
        final Classfile mockClassfile = mock(Classfile.class);
        final Attribute_info mockAttribute = mock(Attribute_info.class);
        final Field_info mockField = mock(Field_info.class);
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockClassfile).getAttributes();
                will(returnValue(Collections.singleton(mockAttribute)));
            one (mockAttribute).accept(sut);
            one (mockClassfile).getAllFields();
                will(returnValue(Collections.singleton(mockField)));
            one (mockField).accept(sut);
            one (mockClassfile).getAllMethods();
                will(returnValue(Collections.singleton(mockMethod)));
            one (mockMethod).accept(sut);
        }});

        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClass_info() {
        Class_info mockClass = mock(Class_info.class);
        sut.visitClass_info(mockClass);
    }

    public void testVisitFieldRef_info() {
        FieldRef_info mockFieldRef = mock(FieldRef_info.class);
        sut.visitFieldRef_info(mockFieldRef);
    }

    public void testVisitMethodRef_info() {
        MethodRef_info mockMethodRef = mock(MethodRef_info.class);
        sut.visitMethodRef_info(mockMethodRef);
    }

    public void testVisitInterfaceMethodRef_info() {
        InterfaceMethodRef_info mockInterfaceMethodRef = mock(InterfaceMethodRef_info.class);
        sut.visitInterfaceMethodRef_info(mockInterfaceMethodRef);
    }

    public void testVisitString_info() {
        String_info mockString = mock(String_info.class);
        sut.visitString_info(mockString);
    }

    public void testVisitInteger_info() {
        Integer_info mockInteger = mock(Integer_info.class);
        sut.visitInteger_info(mockInteger);
    }

    public void testVisitFloat_info() {
        Float_info mockFloat = mock(Float_info.class);
        sut.visitFloat_info(mockFloat);
    }

    public void testVisitLong_info() {
        Long_info mockLong = mock(Long_info.class);
        sut.visitLong_info(mockLong);
    }

    public void testVisitDouble_info() {
        Double_info mockDouble = mock(Double_info.class);
        sut.visitDouble_info(mockDouble);
    }

    public void testVisitNameAndType_info() {
        NameAndType_info mockNameAndType = mock(NameAndType_info.class);
        sut.visitNameAndType_info(mockNameAndType);
    }

    public void testVisitUTF8_info() {
        UTF8_info mockUTF8 = mock(UTF8_info.class);
        sut.visitUTF8_info(mockUTF8);
    }

    public void testVisitField_info() {
        final Field_info mockField = mock(Field_info.class);
        final Attribute_info mockAttribute = mock(Attribute_info.class);

        checking(new Expectations() {{
            one (mockField).getAttributes();
                will(returnValue(Collections.singleton(mockAttribute)));
            one (mockAttribute).accept(sut);
        }});

        sut.visitField_info(mockField);
    }

    public void testVisitMethod_info() {
        final Method_info mockMethod = mock(Method_info.class);
        final Attribute_info mockAttribute = mock(Attribute_info.class);

        checking(new Expectations() {{
            one (mockMethod).getAttributes();
                will(returnValue(Collections.singleton(mockAttribute)));
            one (mockAttribute).accept(sut);
        }});

        sut.visitMethod_info(mockMethod);
    }
    
    public void testVisitConstantValue_attribute() {
        ConstantValue_attribute mockConstantValue = mock(ConstantValue_attribute.class);
        sut.visitConstantValue_attribute(mockConstantValue);
    }

    public void testVisitCode_attribute() {
        final Code_attribute mockCode = mock(Code_attribute.class);
        final Instruction mockInstruction = mock(Instruction.class);
        final ExceptionHandler mockExceptionHandler = mock(ExceptionHandler.class);
        final Attribute_info mockAttribute = mock(Attribute_info.class);

        checking(new Expectations() {{
            one (mockCode).iterator();
                will(returnIterator(mockInstruction));
            one (mockInstruction).accept(sut);
            one (mockCode).getExceptionHandlers();
                will(returnValue(Collections.singleton(mockExceptionHandler)));
            one (mockExceptionHandler).accept(sut);
            one (mockCode).getAttributes();
                will(returnValue(Collections.singleton(mockAttribute)));
            one (mockAttribute).accept(sut);
        }});

        sut.visitCode_attribute(mockCode);
    }

    public void testVisitExceptions_attribute() {
        final Exceptions_attribute mockExceptions = mock(Exceptions_attribute.class);
        final Class_info mockClass = mock(Class_info.class);

        checking(new Expectations() {{
            atLeast(1).of (mockExceptions).getExceptions();
                will(returnValue(Collections.singleton(mockClass)));
            one (mockClass).accept(sut);
        }});

        sut.visitExceptions_attribute(mockExceptions);
    }

    public void testVisitInnerClasses_attribute() {
        final InnerClasses_attribute mockInnerClasses = mock(InnerClasses_attribute.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            atLeast(1).of (mockInnerClasses).getInnerClasses();
                will(returnValue(Collections.singleton(mockInnerClass)));
            one (mockInnerClass).accept(sut);
        }});

        sut.visitInnerClasses_attribute(mockInnerClasses);
    }

    public void testVisitEnclosingMethod_attribute() {
        EnclosingMethod_attribute mockEnclosingMethod = mock(EnclosingMethod_attribute.class);
        sut.visitEnclosingMethod_attribute(mockEnclosingMethod);
    }

    public void testVisitSynthetic_attribute() {
        Synthetic_attribute mockSynthetic = mock(Synthetic_attribute.class);
        sut.visitSynthetic_attribute(mockSynthetic);
    }

    public void testVisitSignature_attribute() {
        Signature_attribute mockSignature = mock(Signature_attribute.class);
        sut.visitSignature_attribute(mockSignature);
    }

    public void testVisitSourceFile_attribute() {
        SourceFile_attribute mockSourceFile = mock(SourceFile_attribute.class);
        sut.visitSourceFile_attribute(mockSourceFile);
    }

    public void testVisitSourceDebugExtension_attribute() {
        SourceDebugExtension_attribute mockSourceDebugExtension = mock(SourceDebugExtension_attribute.class);
        sut.visitSourceDebugExtension_attribute(mockSourceDebugExtension);
    }

    public void testVisitLineNumberTable_attribute() {
        final LineNumberTable_attribute mockLineNumberTable = mock(LineNumberTable_attribute.class);
        final LineNumber mockLineNumber = mock(LineNumber.class);

        checking(new Expectations() {{
            atLeast(1).of (mockLineNumberTable).getLineNumbers();
                will(returnValue(Collections.singleton(mockLineNumber)));
            one (mockLineNumber).accept(sut);
        }});

        sut.visitLineNumberTable_attribute(mockLineNumberTable);
    }

    public void testVisitLocalVariableTable_attribute() {
        final LocalVariableTable_attribute mockLocalVariableTable = mock(LocalVariableTable_attribute.class);
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            atLeast(1).of (mockLocalVariableTable).getLocalVariables();
                will(returnValue(Collections.singleton(mockLocalVariable)));
            one (mockLocalVariable).accept(sut);
        }});

        sut.visitLocalVariableTable_attribute(mockLocalVariableTable);
    }

    public void testVisitLocalVariableTypeTable_attribute() {
        final LocalVariableTypeTable_attribute mockLocalVariableTypeTable = mock(LocalVariableTypeTable_attribute.class);
        final LocalVariableType mockLocalVariableType = mock(LocalVariableType.class);

        checking(new Expectations() {{
            atLeast(1).of (mockLocalVariableTypeTable).getLocalVariableTypes();
                will(returnValue(Collections.singleton(mockLocalVariableType)));
            one (mockLocalVariableType).accept(sut);
        }});

        sut.visitLocalVariableTypeTable_attribute(mockLocalVariableTypeTable);
    }

    public void testVisitDeprecated_attribute() {
        Deprecated_attribute mockDeprecated = mock(Deprecated_attribute.class);
        sut.visitDeprecated_attribute(mockDeprecated);
    }

    public void testVisitRuntimeVisibleAnnotations_attribute() {
        final RuntimeVisibleAnnotations_attribute mockRuntimeVisibleAnnotations = mock(RuntimeVisibleAnnotations_attribute.class);
        final Annotation mockAnnotation = mock(Annotation.class);

        checking(new Expectations() {{
            atLeast(1).of (mockRuntimeVisibleAnnotations).getAnnotations();
                will(returnValue(Collections.singleton(mockAnnotation)));
            one (mockAnnotation).accept(sut);
        }});

        sut.visitRuntimeVisibleAnnotations_attribute(mockRuntimeVisibleAnnotations);
    }

    public void testVisitRuntimeInvisibleAnnotations_attribute() {
        final RuntimeInvisibleAnnotations_attribute mockRuntimeInvisibleAnnotations = mock(RuntimeInvisibleAnnotations_attribute.class);
        final Annotation mockAnnotation = mock(Annotation.class);

        checking(new Expectations() {{
            atLeast(1).of (mockRuntimeInvisibleAnnotations).getAnnotations();
                will(returnValue(Collections.singleton(mockAnnotation)));
            one (mockAnnotation).accept(sut);
        }});

        sut.visitRuntimeInvisibleAnnotations_attribute(mockRuntimeInvisibleAnnotations);
    }

    public void testVisitRuntimeVisibleParameterAnnotations_attribute() {
        final RuntimeVisibleParameterAnnotations_attribute mockRuntimeVisibleParameterAnnotations = mock(RuntimeVisibleParameterAnnotations_attribute.class);
        final Parameter mockParameter = mock(Parameter.class);

        checking(new Expectations() {{
            atLeast(1).of (mockRuntimeVisibleParameterAnnotations).getParameterAnnotations();
                will(returnValue(Collections.singletonList(mockParameter)));
            one (mockParameter).accept(sut);
        }});

        sut.visitRuntimeVisibleParameterAnnotations_attribute(mockRuntimeVisibleParameterAnnotations);
    }

    public void testVisitRuntimeInvisibleParameterAnnotations_attribute() {
        final RuntimeInvisibleParameterAnnotations_attribute mockRuntimeInvisibleParameterAnnotations = mock(RuntimeInvisibleParameterAnnotations_attribute.class);
        final Parameter mockParameter = mock(Parameter.class);

        checking(new Expectations() {{
            atLeast(1).of (mockRuntimeInvisibleParameterAnnotations).getParameterAnnotations();
                will(returnValue(Collections.singletonList(mockParameter)));
            one (mockParameter).accept(sut);
        }});

        sut.visitRuntimeInvisibleParameterAnnotations_attribute(mockRuntimeInvisibleParameterAnnotations);
    }

    public void testVisitAnnotationDefault_attribute() {
        final AnnotationDefault_attribute mockAnnotationDefault = mock(AnnotationDefault_attribute.class);
        final ElementValue mockElementValue = mock(ElementValue.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAnnotationDefault).getElemementValue();
                will(returnValue(mockElementValue));
            one (mockElementValue).accept(sut);
        }});

        sut.visitAnnotationDefault_attribute(mockAnnotationDefault);
    }

    public void testVisitCustom_attribute() {
        Custom_attribute mockCustom = mock(Custom_attribute.class);
        sut.visitCustom_attribute(mockCustom);
    }

    public void testVisitInstruction() {
        Instruction mockInstruction = mock(Instruction.class);
        sut.visitInstruction(mockInstruction);
    }

    public void testVisitExceptionHandler() {
        ExceptionHandler mockExceptionHandler = mock(ExceptionHandler.class);
        sut.visitExceptionHandler(mockExceptionHandler);
    }

    public void testVisitInnerClass() {
        InnerClass mockInnerClass = mock(InnerClass.class);
        sut.visitInnerClass(mockInnerClass);
    }

    public void testVisitLineNumber() {
        LineNumber mockLineNumber = mock(LineNumber.class);
        sut.visitLineNumber(mockLineNumber);
    }

    public void testVisitLocalVariable() {
        LocalVariable mockLocalVariable = mock(LocalVariable.class);
        sut.visitLocalVariable(mockLocalVariable);
    }

    public void testVisitLocalVariableType() {
        LocalVariableType mockLocalVariableType = mock(LocalVariableType.class);
        sut.visitLocalVariableType(mockLocalVariableType);
    }

    public void testVisitParameter() {
        final Parameter mockParameter = mock(Parameter.class);
        final Annotation mockAnnotation = mock(Annotation.class);

        checking(new Expectations() {{
            atLeast(1).of (mockParameter).getAnnotations();
                will(returnValue(Collections.singleton(mockAnnotation)));
            one (mockAnnotation).accept(sut);
        }});

        sut.visitParameter(mockParameter);
    }

    public void testVisitAnnotation() {
        final Annotation mockAnnotation = mock(Annotation.class);
        final ElementValuePair mockElementValuePair = mock(ElementValuePair.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAnnotation).getElementValuePairs();
                will(returnValue(Collections.singleton(mockElementValuePair)));
            one (mockElementValuePair).accept(sut);
        }});

        sut.visitAnnotation(mockAnnotation);
    }

    public void testVisitElementValuePair() {
        final ElementValuePair mockElementValuePair = mock(ElementValuePair.class);
        final ElementValue mockElementValue = mock(ElementValue.class);

        checking(new Expectations() {{
            atLeast(1).of (mockElementValuePair).getElementValue();
                will(returnValue(mockElementValue));
            one (mockElementValue).accept(sut);
        }});

        sut.visitElementValuePair(mockElementValuePair);
    }

    public void testVisitByteConstantElementValue() {
        ByteConstantElementValue mockConstantElementValue = mock(ByteConstantElementValue.class);
        sut.visitByteConstantElementValue(mockConstantElementValue);
    }

    public void testVisitCharConstantElementValue() {
        CharConstantElementValue mockConstantElementValue = mock(CharConstantElementValue.class);
        sut.visitCharConstantElementValue(mockConstantElementValue);
    }

    public void testVisitDoubleConstantElementValue() {
        DoubleConstantElementValue mockConstantElementValue = mock(DoubleConstantElementValue.class);
        sut.visitDoubleConstantElementValue(mockConstantElementValue);
    }

    public void testVisitFloatConstantElementValue() {
        FloatConstantElementValue mockConstantElementValue = mock(FloatConstantElementValue.class);
        sut.visitFloatConstantElementValue(mockConstantElementValue);
    }

    public void testVisitIntegerConstantElementValue() {
        IntegerConstantElementValue mockConstantElementValue = mock(IntegerConstantElementValue.class);
        sut.visitIntegerConstantElementValue(mockConstantElementValue);
    }

    public void testVisitLongConstantElementValue() {
        LongConstantElementValue mockConstantElementValue = mock(LongConstantElementValue.class);
        sut.visitLongConstantElementValue(mockConstantElementValue);
    }

    public void testVisitShortConstantElementValue() {
        ShortConstantElementValue mockConstantElementValue = mock(ShortConstantElementValue.class);
        sut.visitShortConstantElementValue(mockConstantElementValue);
    }

    public void testVisitBooleanConstantElementValue() {
        BooleanConstantElementValue mockConstantElementValue = mock(BooleanConstantElementValue.class);
        sut.visitBooleanConstantElementValue(mockConstantElementValue);
    }

    public void testVisitStringConstantElementValue() {
        StringConstantElementValue mockConstantElementValue = mock(StringConstantElementValue.class);
        sut.visitStringConstantElementValue(mockConstantElementValue);
    }

    public void testVisitEnumElementValue() {
        EnumElementValue mockEnumElementValue = mock(EnumElementValue.class);
        sut.visitEnumElementValue(mockEnumElementValue);
    }

    public void testVisitClassElementValue() {
        ClassElementValue mockClassElementValue = mock(ClassElementValue.class);
        sut.visitClassElementValue(mockClassElementValue);
    }

    public void testVisitAnnotationElementValue() {
        final AnnotationElementValue mockAnnotationElementValue = mock(AnnotationElementValue.class);
        final Annotation mockAnnotation = mock(Annotation.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAnnotationElementValue).getAnnotation();
                will(returnValue(mockAnnotation));
            one (mockAnnotation).accept(sut);
        }});

        sut.visitAnnotationElementValue(mockAnnotationElementValue);
    }

    public void testVisitArrayElementValue() {
        final ArrayElementValue mockArrayElementValue = mock(ArrayElementValue.class);
        final ElementValue mockElementValue= mock(ElementValue.class);

        checking(new Expectations() {{
            atLeast(1).of (mockArrayElementValue).getValues();
                will(returnValue(Collections.singleton(mockElementValue)));
            one (mockElementValue).accept(sut);
        }});

        sut.visitArrayElementValue(mockArrayElementValue);
    }
}
