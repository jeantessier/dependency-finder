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

package com.jeantessier.classreader;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.jmock.*;
import org.jmock.api.*;
import org.jmock.integration.junit3.*;
import org.jmock.lib.action.*;

public class TestVisitorBase extends MockObjectTestCase {
    private VisitorBase sut;

    protected void setUp() throws Exception {
        sut = new VisitorBase() {};
    }

    public void testIncrementIndex() {
        int oldValue = sut.currentIndex();
        sut.incrementIndex();
        int newValue = sut.currentIndex();
        assertEquals("index", oldValue + 1, newValue);
    }

    public void testResetIndex() {
        sut.incrementIndex();
        assertTrue("index should not be the starting index", sut.currentIndex() != sut.STARTING_INDEX);
        sut.resetIndex();
        assertEquals("index", 1, sut.currentIndex());
    }

    public void testVisitConstantPool() {
        final ConstantPool mockConstantPool = mock(ConstantPool.class);
        final ConstantPoolEntry mockEntry = mock(ConstantPoolEntry.class);

        checking(new Expectations() {{
            oneOf (mockConstantPool).stream();
                will(returnValue(Stream.of(null, mockEntry)));
            oneOf (mockEntry).accept(sut);
        }});

        sut.visitConstantPool(mockConstantPool);
        assertEquals("current index", sut.STARTING_INDEX + 1, sut.currentIndex());
    }

    public void testVisitConstantPool_ResetIndexBetweenCalls() {
        final ConstantPool mockConstantPool = mock(ConstantPool.class);
        final ConstantPoolEntry mockEntry = mock(ConstantPoolEntry.class);

        checking(new Expectations() {{
            // If I use exactly(2).of, they share the return value.
            // The test ends up trying to operate over a closed stream.
            oneOf (mockConstantPool).stream();
                will(returnValue(Stream.of(null, mockEntry)));
            oneOf (mockConstantPool).stream();
                will(returnValue(Stream.of(null, mockEntry)));
            exactly(2).of (mockEntry).accept(sut);
        }});

        sut.visitConstantPool(mockConstantPool);
        sut.visitConstantPool(mockConstantPool);
        assertEquals("current index", sut.STARTING_INDEX + 1, sut.currentIndex());
    }

    public void testVisitClassfiles() {
        final Classfile mockClassfile1 = mock(Classfile.class, "classfile1");
        final Classfile mockClassfile2 = mock(Classfile.class, "classfile2");

        Collection<Classfile> classfiles = new ArrayList<>();
        classfiles.add(mockClassfile1);
        classfiles.add(mockClassfile2);

        checking(new Expectations() {{
            oneOf (mockClassfile1).accept(sut);
            oneOf (mockClassfile2).accept(sut);
        }});

        sut.visitClassfiles(classfiles);
    }

    public void testVisitClassfile() {
        final Classfile mockClassfile = mock(Classfile.class);
        final Attribute_info mockAttribute = mock(Attribute_info.class);
        final Field_info mockField = mock(Field_info.class);
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).getAttributes();
                will(returnValue(Collections.singleton(mockAttribute)));
            oneOf (mockAttribute).accept(sut);
            oneOf (mockClassfile).getAllFields();
                will(returnValue(Collections.singleton(mockField)));
            oneOf (mockField).accept(sut);
            oneOf (mockClassfile).getAllMethods();
                will(returnValue(Collections.singleton(mockMethod)));
            oneOf (mockMethod).accept(sut);
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

    public void testVisitMethodHandle_info() {
        MethodHandle_info mockMethodHandle = mock(MethodHandle_info.class);
        sut.visitMethodHandle_info(mockMethodHandle);
    }

    public void testVisitMethodType_info() {
        MethodType_info mockMethodType = mock(MethodType_info.class);
        sut.visitMethodType_info(mockMethodType);
    }

    public void testVisitDynamic_info() {
        Dynamic_info mockDynamic = mock(Dynamic_info.class);
        sut.visitDynamic_info(mockDynamic);
    }

    public void testVisitInvokeDynamic_info() {
        InvokeDynamic_info mockInvokeDynamic = mock(InvokeDynamic_info.class);
        sut.visitInvokeDynamic_info(mockInvokeDynamic);
    }

    public void testVisitModule_info() {
        Module_info mockModule = mock(Module_info.class);
        sut.visitModule_info(mockModule);
    }

    public void testVisitPackage_info() {
        Package_info mockPackage = mock(Package_info.class);
        sut.visitPackage_info(mockPackage);
    }

    public void testVisitField_info() {
        final Field_info mockField = mock(Field_info.class);
        final Attribute_info mockAttribute = mock(Attribute_info.class);

        checking(new Expectations() {{
            oneOf (mockField).getAttributes();
                will(returnValue(Collections.singleton(mockAttribute)));
            oneOf (mockAttribute).accept(sut);
        }});

        sut.visitField_info(mockField);
    }

    public void testVisitMethod_info() {
        final Method_info mockMethod = mock(Method_info.class);
        final Attribute_info mockAttribute = mock(Attribute_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).getAttributes();
                will(returnValue(Collections.singleton(mockAttribute)));
            oneOf (mockAttribute).accept(sut);
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
            oneOf (mockCode).forEach(with(any(Consumer.class)));
                will(visitInstruction(mockInstruction));
            oneOf (mockInstruction).accept(sut);
            oneOf (mockCode).getExceptionHandlers();
                will(returnValue(Collections.singleton(mockExceptionHandler)));
            oneOf (mockExceptionHandler).accept(sut);
            oneOf (mockCode).getAttributes();
                will(returnValue(Collections.singleton(mockAttribute)));
            oneOf (mockAttribute).accept(sut);
        }});

        sut.visitCode_attribute(mockCode);
    }

    public void testVisitStackMapTable_attribute() {
        final StackMapTable_attribute mockStackMapTable = mock(StackMapTable_attribute.class);
        final StackMapFrame mockStackMapFrame = mock(StackMapFrame.class);

        checking(new Expectations() {{
            atLeast(1).of (mockStackMapTable).getEntries();
                will(returnValue(Collections.singletonList(mockStackMapFrame)));
            oneOf (mockStackMapFrame).accept(sut);
        }});

        sut.visitStackMapTable_attribute(mockStackMapTable);
    }

    public void testVisitExceptions_attribute() {
        final Exceptions_attribute mockExceptions = mock(Exceptions_attribute.class);
        final Class_info mockClass = mock(Class_info.class);

        checking(new Expectations() {{
            atLeast(1).of (mockExceptions).getExceptions();
                will(returnValue(Collections.singleton(mockClass)));
            oneOf (mockClass).accept(sut);
        }});

        sut.visitExceptions_attribute(mockExceptions);
    }

    public void testVisitInnerClasses_attribute() {
        final InnerClasses_attribute mockInnerClasses = mock(InnerClasses_attribute.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            atLeast(1).of (mockInnerClasses).getInnerClasses();
                will(returnValue(Collections.singleton(mockInnerClass)));
            oneOf (mockInnerClass).accept(sut);
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
            oneOf (mockLineNumber).accept(sut);
        }});

        sut.visitLineNumberTable_attribute(mockLineNumberTable);
    }

    public void testVisitLocalVariableTable_attribute() {
        final LocalVariableTable_attribute mockLocalVariableTable = mock(LocalVariableTable_attribute.class);
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            atLeast(1).of (mockLocalVariableTable).getLocalVariables();
                will(returnValue(Collections.singleton(mockLocalVariable)));
            oneOf (mockLocalVariable).accept(sut);
        }});

        sut.visitLocalVariableTable_attribute(mockLocalVariableTable);
    }

    public void testVisitLocalVariableTypeTable_attribute() {
        final LocalVariableTypeTable_attribute mockLocalVariableTypeTable = mock(LocalVariableTypeTable_attribute.class);
        final LocalVariableType mockLocalVariableType = mock(LocalVariableType.class);

        checking(new Expectations() {{
            atLeast(1).of (mockLocalVariableTypeTable).getLocalVariableTypes();
                will(returnValue(Collections.singleton(mockLocalVariableType)));
            oneOf (mockLocalVariableType).accept(sut);
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
            oneOf (mockAnnotation).accept(sut);
        }});

        sut.visitRuntimeVisibleAnnotations_attribute(mockRuntimeVisibleAnnotations);
    }

    public void testVisitRuntimeInvisibleAnnotations_attribute() {
        final RuntimeInvisibleAnnotations_attribute mockRuntimeInvisibleAnnotations = mock(RuntimeInvisibleAnnotations_attribute.class);
        final Annotation mockAnnotation = mock(Annotation.class);

        checking(new Expectations() {{
            atLeast(1).of (mockRuntimeInvisibleAnnotations).getAnnotations();
                will(returnValue(Collections.singleton(mockAnnotation)));
            oneOf (mockAnnotation).accept(sut);
        }});

        sut.visitRuntimeInvisibleAnnotations_attribute(mockRuntimeInvisibleAnnotations);
    }

    public void testVisitRuntimeVisibleParameterAnnotations_attribute() {
        final RuntimeVisibleParameterAnnotations_attribute mockRuntimeVisibleParameterAnnotations = mock(RuntimeVisibleParameterAnnotations_attribute.class);
        final ParameterAnnotation mockParameterAnnotation = mock(ParameterAnnotation.class);

        checking(new Expectations() {{
            atLeast(1).of (mockRuntimeVisibleParameterAnnotations).getParameterAnnotations();
                will(returnValue(Collections.singletonList(mockParameterAnnotation)));
            oneOf (mockParameterAnnotation).accept(sut);
        }});

        sut.visitRuntimeVisibleParameterAnnotations_attribute(mockRuntimeVisibleParameterAnnotations);
    }

    public void testVisitRuntimeInvisibleParameterAnnotations_attribute() {
        final RuntimeInvisibleParameterAnnotations_attribute mockRuntimeInvisibleParameterAnnotations = mock(RuntimeInvisibleParameterAnnotations_attribute.class);
        final ParameterAnnotation mockParameterAnnotation = mock(ParameterAnnotation.class);

        checking(new Expectations() {{
            atLeast(1).of (mockRuntimeInvisibleParameterAnnotations).getParameterAnnotations();
                will(returnValue(Collections.singletonList(mockParameterAnnotation)));
            oneOf (mockParameterAnnotation).accept(sut);
        }});

        sut.visitRuntimeInvisibleParameterAnnotations_attribute(mockRuntimeInvisibleParameterAnnotations);
    }

    public void testVisitRuntimeVisibleTypeAnnotations_attribute_WithoutAnnotations() {
        final RuntimeVisibleTypeAnnotations_attribute attribute = mock(RuntimeVisibleTypeAnnotations_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (attribute).getTypeAnnotations();
        }});

        sut.visitRuntimeVisibleTypeAnnotations_attribute(attribute);
    }

    public void testVisitRuntimeVisibleTypeAnnotations_attribute_WithAnAnnotation() {
        final RuntimeVisibleTypeAnnotations_attribute attribute = mock(RuntimeVisibleTypeAnnotations_attribute.class);
        final TypeAnnotation typeAnnotation = mock(TypeAnnotation.class);

        checking(new Expectations() {{
            atLeast(1).of (attribute).getTypeAnnotations();
                will(returnValue(Collections.singletonList(typeAnnotation)));
            oneOf (typeAnnotation).accept(sut);
        }});

        sut.visitRuntimeVisibleTypeAnnotations_attribute(attribute);
    }

    public void testVisitRuntimeInvisibleTypeAnnotations_attribute_WithoutParameterAnnotations() {
        final RuntimeInvisibleTypeAnnotations_attribute attribute = mock(RuntimeInvisibleTypeAnnotations_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (attribute).getTypeAnnotations();
        }});

        sut.visitRuntimeInvisibleTypeAnnotations_attribute(attribute);
    }

    public void testVisitRuntimeInvisibleTypeAnnotations_attribute_WithAParameterAnnotation() {
        final RuntimeInvisibleTypeAnnotations_attribute attribute = mock(RuntimeInvisibleTypeAnnotations_attribute.class);
        final TypeAnnotation typeAnnotation = mock(TypeAnnotation.class);

        checking(new Expectations() {{
            atLeast(1).of (attribute).getTypeAnnotations();
                will(returnValue(Collections.singletonList(typeAnnotation)));
            oneOf (typeAnnotation).accept(sut);
        }});

        sut.visitRuntimeInvisibleTypeAnnotations_attribute(attribute);
    }

    public void testVisitAnnotationDefault_attribute() {
        final AnnotationDefault_attribute mockAnnotationDefault = mock(AnnotationDefault_attribute.class);
        final ElementValue mockElementValue = mock(ElementValue.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAnnotationDefault).getElemementValue();
                will(returnValue(mockElementValue));
            oneOf (mockElementValue).accept(sut);
        }});

        sut.visitAnnotationDefault_attribute(mockAnnotationDefault);
    }

    public void testVisitBootstrapMethods_attribute() {
        final BootstrapMethods_attribute mockBootstrapMethods = mock(BootstrapMethods_attribute.class);
        final BootstrapMethod mockBootstrapMethod = mock(BootstrapMethod.class);

        checking(new Expectations() {{
            atLeast(1).of (mockBootstrapMethods).getBootstrapMethods();
                will(returnValue(Collections.singleton(mockBootstrapMethod)));
            oneOf (mockBootstrapMethod).accept(sut);
        }});

        sut.visitBootstrapMethods_attribute(mockBootstrapMethods);
    }

    public void testVisitMethodParameters_attribute_noMethodParameters() {
        final MethodParameters_attribute mockAttribute = mock(MethodParameters_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAttribute).getMethodParameters();
                will(returnValue(Collections.emptyList()));
        }});

        sut.visitMethodParameters_attribute(mockAttribute);
    }

    public void testVisitMethodParameters_attribute_oneMethodParameter() {
        final MethodParameters_attribute mockAttribute = mock(MethodParameters_attribute.class);
        final MethodParameter mockMethodParameter = mock(MethodParameter.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAttribute).getMethodParameters();
                will(returnValue(Collections.singleton(mockMethodParameter)));
            oneOf (mockMethodParameter).accept(sut);
        }});

        sut.visitMethodParameters_attribute(mockAttribute);
    }

    public void testVisitModule_attribute() {
        final Module_attribute mockAttribute = mock(Module_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAttribute).getRequires();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getExports();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getOpens();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getUses();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getProvides();
                will(returnValue(Collections.emptyList()));
        }});

        sut.visitModule_attribute(mockAttribute);
    }

    public void testVisitModule_attributeWithRequires() {
        final Module_attribute mockAttribute = mock(Module_attribute.class);
        final ModuleRequires mockRequires = mock(ModuleRequires.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAttribute).getRequires();
                will(returnValue(Collections.singleton(mockRequires)));
            atLeast(1).of (mockAttribute).getExports();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getOpens();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getUses();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getProvides();
                will(returnValue(Collections.emptyList()));
            oneOf (mockRequires).accept(sut);
        }});

        sut.visitModule_attribute(mockAttribute);
    }

    public void testVisitModule_attributeWithExports() {
        final Module_attribute mockAttribute = mock(Module_attribute.class);
        final ModuleExports mockExports = mock(ModuleExports.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAttribute).getRequires();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getExports();
                will(returnValue(Collections.singleton(mockExports)));
            atLeast(1).of (mockAttribute).getOpens();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getUses();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getProvides();
                will(returnValue(Collections.emptyList()));
            oneOf (mockExports).accept(sut);
        }});

        sut.visitModule_attribute(mockAttribute);
    }

    public void testVisitModule_attributeWithOpens() {
        final Module_attribute mockAttribute = mock(Module_attribute.class);
        final ModuleOpens mockOpens = mock(ModuleOpens.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAttribute).getRequires();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getExports();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getOpens();
                will(returnValue(Collections.singleton(mockOpens)));
            atLeast(1).of (mockAttribute).getUses();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getProvides();
                will(returnValue(Collections.emptyList()));
            oneOf (mockOpens).accept(sut);
        }});

        sut.visitModule_attribute(mockAttribute);
    }

    public void testVisitModule_attributeWithUses() {
        final Module_attribute mockAttribute = mock(Module_attribute.class);
        final ModuleUses mockUses = mock(ModuleUses.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAttribute).getRequires();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getExports();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getOpens();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getUses();
                will(returnValue(Collections.singleton(mockUses)));
            atLeast(1).of (mockAttribute).getProvides();
                will(returnValue(Collections.emptyList()));
            oneOf (mockUses).accept(sut);
        }});

        sut.visitModule_attribute(mockAttribute);
    }

    public void testVisitModule_attributeWithProvides() {
        final Module_attribute mockAttribute = mock(Module_attribute.class);
        final ModuleProvides mockProvides = mock(ModuleProvides.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAttribute).getRequires();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getExports();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getOpens();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getUses();
                will(returnValue(Collections.emptyList()));
            atLeast(1).of (mockAttribute).getProvides();
                will(returnValue(Collections.singleton(mockProvides)));
            oneOf (mockProvides).accept(sut);
        }});

        sut.visitModule_attribute(mockAttribute);
    }

    public void testVisitModulePackages_attribute() {
        final ModulePackages_attribute mockAttribute = mock(ModulePackages_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAttribute).getPackages();
                will(returnValue(Collections.emptyList()));
        }});

        sut.visitModulePackages_attribute(mockAttribute);
    }

    public void testVisitModulePackages_attributeWithModulePackage() {
        final ModulePackages_attribute mockAttribute = mock(ModulePackages_attribute.class);
        final ModulePackage mockPackage = mock(ModulePackage.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAttribute).getPackages();
                will(returnValue(Collections.singleton(mockPackage)));
            oneOf (mockPackage).accept(sut);
        }});

        sut.visitModulePackages_attribute(mockAttribute);
    }

    public void testVisitModuleMainClass_attribute() {
        final ModuleMainClass_attribute mockAttribute = mock(ModuleMainClass_attribute.class);
        sut.visitModuleMainClass_attribute(mockAttribute);
    }

    public void testVisitNestHost_attribute() {
        final NestHost_attribute mockAttribute = mock(NestHost_attribute.class);
        sut.visitNestHost_attribute(mockAttribute);
    }

    public void testVisitNestMembers_attribute() {
        final NestMembers_attribute mockAttribute = mock(NestMembers_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAttribute).getMembers();
                will(returnValue(Collections.emptyList()));
        }});

        sut.visitNestMembers_attribute(mockAttribute);
    }

    public void testVisitNestMembers_attributeWithNestMember() {
        final NestMembers_attribute mockAttribute = mock(NestMembers_attribute.class);
        final NestMember mockMember = mock(NestMember.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAttribute).getMembers();
                will(returnValue(Collections.singleton(mockMember)));
            oneOf (mockMember).accept(sut);
        }});

        sut.visitNestMembers_attribute(mockAttribute);
    }

    public void testVisitRecord_attribute() {
        final Record_attribute mockAttribute = mock(Record_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAttribute).getRecordComponents();
                will(returnValue(Collections.emptyList()));
        }});

        sut.visitRecord_attribute(mockAttribute);
    }

    public void testVisitRecord_attributeWithRecordComponent() {
        final Record_attribute mockAttribute = mock(Record_attribute.class);
        final RecordComponent_info mockRecordComponent = mock(RecordComponent_info.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAttribute).getRecordComponents();
                will(returnValue(Collections.singleton(mockRecordComponent)));
            oneOf (mockRecordComponent).accept(sut);
        }});

        sut.visitRecord_attribute(mockAttribute);
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

    public void testVisitBootstrapMethod() {
        final BootstrapMethod mockBootstrapMethod = mock(BootstrapMethod.class);
        final MethodHandle_info mockMethodHandle = mock(MethodHandle_info.class);
        final ConstantPoolEntry mockArgument = mock(Integer_info.class);

        checking(new Expectations() {{
            atLeast(1).of (mockBootstrapMethod).getBootstrapMethod();
            will(returnValue(mockMethodHandle));
            atLeast(1).of (mockBootstrapMethod).getArguments();
            will(returnValue(Collections.singleton(mockArgument)));
            oneOf (mockMethodHandle).accept(sut);
            oneOf (mockArgument).accept(sut);
        }});

        sut.visitBootstrapMethod(mockBootstrapMethod);
    }

    public void testVisitMethodParameter() {
        final MethodParameter mockMethodParameter = mock(MethodParameter.class);
        sut.visitMethodParameter(mockMethodParameter);
    }

    public void testVisitModuleRequires() {
        final ModuleRequires mockRequires = mock(ModuleRequires.class);
        sut.visitModuleRequires(mockRequires);
    }

    public void testVisitModuleExports_noExportsTos() {
        final ModuleExports mockExports = mock(ModuleExports.class);

        checking(new Expectations() {{
            atLeast(1).of (mockExports).getExportsTos();
                will(returnValue(Collections.emptyList()));
        }});

        sut.visitModuleExports(mockExports);
    }

    public void testVisitModuleExports_oneExportsTo() {
        final ModuleExports mockExports = mock(ModuleExports.class);
        final ModuleExportsTo mockExportsTo = mock(ModuleExportsTo.class);

        checking(new Expectations() {{
            atLeast(1).of (mockExports).getExportsTos();
                will(returnValue(Collections.singleton(mockExportsTo)));
            oneOf (mockExportsTo).accept(sut);
        }});

        sut.visitModuleExports(mockExports);
    }

    public void testVisitModuleExportsTo() {
        final ModuleExportsTo mockExportsTo = mock(ModuleExportsTo.class);
        sut.visitModuleExportsTo(mockExportsTo);
    }

    public void testVisitModuleOpens_noOpensTos() {
        final ModuleOpens mockOpens = mock(ModuleOpens.class);

        checking(new Expectations() {{
            atLeast(1).of (mockOpens).getOpensTos();
                will(returnValue(Collections.emptyList()));
        }});

        sut.visitModuleOpens(mockOpens);
    }

    public void testVisitModuleOpens_oneOpensTo() {
        final ModuleOpens mockOpens = mock(ModuleOpens.class);
        final ModuleOpensTo mockOpensTo = mock(ModuleOpensTo.class);

        checking(new Expectations() {{
            atLeast(1).of (mockOpens).getOpensTos();
                will(returnValue(Collections.singleton(mockOpensTo)));
            oneOf (mockOpensTo).accept(sut);
        }});

        sut.visitModuleOpens(mockOpens);
    }

    public void testVisitModuleOpensTo() {
        final ModuleOpensTo mockOpensTo = mock(ModuleOpensTo.class);
        sut.visitModuleOpensTo(mockOpensTo);
    }

    public void testVisitModuleUses() {
        final ModuleUses mockUses = mock(ModuleUses.class);
        sut.visitModuleUses(mockUses);
    }

    public void testVisitModuleProvides_noProvidesWiths() {
        final ModuleProvides mockProvides = mock(ModuleProvides.class);

        checking(new Expectations() {{
            atLeast(1).of (mockProvides).getProvidesWiths();
                will(returnValue(Collections.emptyList()));
        }});

        sut.visitModuleProvides(mockProvides);
    }

    public void testVisitModuleProvides_oneProvidesWith() {
        final ModuleProvides mockProvides = mock(ModuleProvides.class);
        final ModuleProvidesWith mockProvidesWith = mock(ModuleProvidesWith.class);

        checking(new Expectations() {{
            atLeast(1).of (mockProvides).getProvidesWiths();
                will(returnValue(Collections.singleton(mockProvidesWith)));
            oneOf (mockProvidesWith).accept(sut);
        }});

        sut.visitModuleProvides(mockProvides);
    }

    public void testVisitModuleProvidesWith() {
        final ModuleProvidesWith mockProvidesWith = mock(ModuleProvidesWith.class);
        sut.visitModuleProvidesWith(mockProvidesWith);
    }

    public void testVisitModulePackage() {
        final ModulePackage mockPackage = mock(ModulePackage.class);
        sut.visitModulePackage(mockPackage);
    }

    public void testVisitNestMember() {
        final NestMember mockMember = mock(NestMember.class);
        sut.visitNestMember(mockMember);
    }

    public void testVisitRecordComponent_info() {
        final RecordComponent_info mockRecordComponent = mock(RecordComponent_info.class);

        checking(new Expectations() {{
            atLeast(1).of (mockRecordComponent).getAttributes();
                will(returnValue(Collections.emptyList()));
        }});

        sut.visitRecordComponent_info(mockRecordComponent);
    }

    public void testVisitRecordComponent_infoWithRecordComponent() {
        final RecordComponent_info mockAttribute = mock(RecordComponent_info.class);
        final Attribute_info mockRecordComponentAttribute = mock(Attribute_info.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAttribute).getAttributes();
                will(returnValue(Collections.singleton(mockRecordComponentAttribute)));
            oneOf (mockRecordComponentAttribute).accept(sut);
        }});

        sut.visitRecordComponent_info(mockAttribute);
    }

    public void testVisitAnnotation() {
        final Annotation mockAnnotation = mock(Annotation.class);
        final ElementValuePair mockElementValuePair = mock(ElementValuePair.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAnnotation).getElementValuePairs();
                will(returnValue(Collections.singleton(mockElementValuePair)));
            oneOf (mockElementValuePair).accept(sut);
        }});

        sut.visitAnnotation(mockAnnotation);
    }

    public void testVisitParameterAnnotation() {
        final ParameterAnnotation mockParameterAnnotation = mock(ParameterAnnotation.class);
        final Annotation mockAnnotation = mock(Annotation.class);

        checking(new Expectations() {{
            atLeast(1).of (mockParameterAnnotation).getAnnotations();
                will(returnValue(Collections.singleton(mockAnnotation)));
            oneOf (mockAnnotation).accept(sut);
        }});

        sut.visitParameterAnnotation(mockParameterAnnotation);
    }

    public void testVisitTypePath_noEntries() {
        final TypePath typePath = mock(TypePath.class);

        checking(new Expectations() {{
            atLeast(1).of (typePath).getPath();
                will(returnValue(Collections.emptyList()));
        }});

        sut.visitTypePath(typePath);
    }

    public void testVisitTypePath_oneEntry() {
        final TypePath typePath = mock(TypePath.class);
        final TypePathEntry mockTypePathEntry = mock(TypePathEntry.class);

        checking(new Expectations() {{
            atLeast(1).of (typePath).getPath();
                will(returnValue(Collections.singleton(mockTypePathEntry)));
            oneOf (mockTypePathEntry).accept(sut);
        }});

        sut.visitTypePath(typePath);
    }

    public void testVisitElementValuePair() {
        final ElementValuePair mockElementValuePair = mock(ElementValuePair.class);
        final ElementValue mockElementValue = mock(ElementValue.class);

        checking(new Expectations() {{
            atLeast(1).of (mockElementValuePair).getElementValue();
                will(returnValue(mockElementValue));
            oneOf (mockElementValue).accept(sut);
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
            oneOf (mockAnnotation).accept(sut);
        }});

        sut.visitAnnotationElementValue(mockAnnotationElementValue);
    }

    public void testVisitArrayElementValue() {
        final ArrayElementValue mockArrayElementValue = mock(ArrayElementValue.class);
        final ElementValue mockElementValue= mock(ElementValue.class);

        checking(new Expectations() {{
            atLeast(1).of (mockArrayElementValue).getValues();
                will(returnValue(Collections.singleton(mockElementValue)));
            oneOf (mockElementValue).accept(sut);
        }});

        sut.visitArrayElementValue(mockArrayElementValue);
    }

    public void testVisitSameFrame() {
        final SameFrame mockSameFrame = mock(SameFrame.class);
        sut.visitSameFrame(mockSameFrame);
    }

    public void testVisitSameLocals1StackItemFrame() {
        final SameLocals1StackItemFrame mockSameLocals1StackItemFrame = mock(SameLocals1StackItemFrame.class);
        final VerificationTypeInfo mockVerificationTypeInfo = mock(VerificationTypeInfo.class);

        checking(new Expectations() {{
            atLeast(1).of (mockSameLocals1StackItemFrame).getStack();
                will(returnValue(mockVerificationTypeInfo));
            oneOf (mockVerificationTypeInfo).accept(sut);
        }});

        sut.visitSameLocals1StackItemFrame(mockSameLocals1StackItemFrame);
    }

    public void testVisitSameLocals1StackItemFrameExtended() {
        final SameLocals1StackItemFrameExtended mockSameLocals1StackItemFrameExtended = mock(SameLocals1StackItemFrameExtended.class);
        final VerificationTypeInfo mockVerificationTypeInfo = mock(VerificationTypeInfo.class);

        checking(new Expectations() {{
            atLeast(1).of (mockSameLocals1StackItemFrameExtended).getStack();
                will(returnValue(mockVerificationTypeInfo));
            oneOf (mockVerificationTypeInfo).accept(sut);
        }});

        sut.visitSameLocals1StackItemFrameExtended(mockSameLocals1StackItemFrameExtended);
    }

    public void testVisitChopFrame() {
        final ChopFrame mockChopFrame = mock(ChopFrame.class);
        sut.visitChopFrame(mockChopFrame);
    }

    public void testVisitSameFrameExtended() {
        final SameFrameExtended mockSameFrameExtended = mock(SameFrameExtended.class);
        sut.visitSameFrameExtended(mockSameFrameExtended);
    }

    public void testVisitAppendFrame() {
        final AppendFrame mockAppendFrame = mock(AppendFrame.class);
        final VerificationTypeInfo mockVerificationTypeInfo = mock(VerificationTypeInfo.class);

        checking(new Expectations() {{
            atLeast(1).of (mockAppendFrame).getLocals();
                will(returnValue(Collections.singleton(mockVerificationTypeInfo)));
            oneOf (mockVerificationTypeInfo).accept(sut);
        }});

        sut.visitAppendFrame(mockAppendFrame);
    }

    public void testVisitFullFrame() {
        final FullFrame mockFullFrame = mock(FullFrame.class);
        final VerificationTypeInfo mockLocals = mock(VerificationTypeInfo.class, "locals");
        final VerificationTypeInfo mockStack = mock(VerificationTypeInfo.class, "stack");

        checking(new Expectations() {{
            atLeast(1).of (mockFullFrame).getLocals();
                will(returnValue(Collections.singleton(mockLocals)));
            oneOf (mockLocals).accept(sut);
            atLeast(1).of (mockFullFrame).getStack();
                will(returnValue(Collections.singleton(mockStack)));
            oneOf (mockStack).accept(sut);
        }});

        sut.visitFullFrame(mockFullFrame);
    }

    public void testVisitTopVariableInfo() {
        final TopVariableInfo mockTopVariableInfo = mock(TopVariableInfo.class);
        sut.visitTopVariableInfo(mockTopVariableInfo);
    }

    public void testVisitIntegerVariableInfo() {
        final IntegerVariableInfo mockIntegerVariableInfo = mock(IntegerVariableInfo.class);
        sut.visitIntegerVariableInfo(mockIntegerVariableInfo);
    }

    public void testVisitFloatVariableInfo() {
        final FloatVariableInfo mockFloatVariableInfo = mock(FloatVariableInfo.class);
        sut.visitFloatVariableInfo(mockFloatVariableInfo);
    }

    public void testVisitLongVariableInfo() {
        final LongVariableInfo mockLongVariableInfo = mock(LongVariableInfo.class);
        sut.visitLongVariableInfo(mockLongVariableInfo);
    }

    public void testVisitDoubleVariableInfo() {
        final DoubleVariableInfo mockDoubleVariableInfo = mock(DoubleVariableInfo.class);
        sut.visitDoubleVariableInfo(mockDoubleVariableInfo);
    }

    public void testVisitNullVariableInfo() {
        final NullVariableInfo mockNullVariableInfo = mock(NullVariableInfo.class);
        sut.visitNullVariableInfo(mockNullVariableInfo);
    }

    public void testVisitUninitializedThisVariableInfo() {
        final UninitializedThisVariableInfo mockUninitializedThisVariableInfo = mock(UninitializedThisVariableInfo.class);
        sut.visitUninitializedThisVariableInfo(mockUninitializedThisVariableInfo);
    }

    public void testVisitObjectVariableInfo() {
        final ObjectVariableInfo mockObjectVariableInfo = mock(ObjectVariableInfo.class);
        final Class_info mockClassInfo = mock(Class_info.class);

        checking(new Expectations() {{
            atLeast(1).of (mockObjectVariableInfo).getClassInfo();
                will(returnValue(mockClassInfo));
            oneOf (mockClassInfo).accept(sut);
        }});

        sut.visitObjectVariableInfo(mockObjectVariableInfo);
    }

    public void testVisitUninitializedVariableInfo() {
        final UninitializedVariableInfo mockUninitializedVariableInfo = mock(UninitializedVariableInfo.class);
        sut.visitUninitializedVariableInfo(mockUninitializedVariableInfo);
    }

    private Action visitInstruction(Instruction instruction) {
        return new CustomAction("Iterate over instruction") {
            public Object invoke(Invocation invocation) {
                ((Consumer<Instruction>) invocation.getParameter(0)).accept(instruction);
                return null;
            }
        };
    }
}
