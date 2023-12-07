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

public interface Visitor {
    /*
     * Classfile
     */

    default void visitClassfiles(Collection<Classfile> classfiles) {
        classfiles.forEach(classfile -> classfile.accept(this));
    }

    default void visitClassfile(Classfile classfile) {
        // Do nothing
    }

    /*
     * ConstantPool entries
     */

    default void visitConstantPool(ConstantPool constantPool) {
        // Do nothing
    }

    default void visitClass_info(Class_info entry) {
        // Do nothing
    }

    default void visitFieldRef_info(FieldRef_info entry) {
        // Do nothing
    }

    default void visitMethodRef_info(MethodRef_info entry) {
        // Do nothing
    }

    default void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
        // Do nothing
    }

    default void visitString_info(String_info entry) {
        // Do nothing
    }

    default void visitInteger_info(Integer_info entry) {
        // Do nothing
    }

    default void visitFloat_info(Float_info entry) {
        // Do nothing
    }

    default void visitLong_info(Long_info entry) {
        // Do nothing
    }

    default void visitDouble_info(Double_info entry) {
        // Do nothing
    }

    default void visitNameAndType_info(NameAndType_info entry) {
        // Do nothing
    }

    default void visitUTF8_info(UTF8_info entry) {
        // Do nothing
    }

    default void visitMethodHandle_info(MethodHandle_info entry) {
        // Do nothing
    }

    default void visitMethodType_info(MethodType_info entry) {
        // Do nothing
    }

    default void visitDynamic_info(Dynamic_info entry) {
        // Do nothing
    }

    default void visitInvokeDynamic_info(InvokeDynamic_info entry) {
        // Do nothing
    }

    default void visitModule_info(Module_info entry) {
        // Do nothing
    }

    default void visitPackage_info(Package_info entry) {
        // Do nothing
    }

    default void visitUnusableEntry(UnusableEntry entry) {
        // Do nothing
    }

    /*
     * Features
     */

    default void visitField_info(Field_info entry) {
        // Do nothing
    }

    default void visitMethod_info(Method_info entry) {
        // Do nothing
    }

    /*
     * Attributes
     */

    default void visitConstantValue_attribute(ConstantValue_attribute attribute) {
        // Do nothing
    }

    default void visitCode_attribute(Code_attribute attribute) {
        // Do nothing
    }

    default void visitExceptions_attribute(Exceptions_attribute attribute) {
        attribute.getExceptions().forEach(exception -> exception.accept(this));
    }

    default void visitInnerClasses_attribute(InnerClasses_attribute attribute) {
        attribute.getInnerClasses().forEach(innerClass -> innerClass.accept(this));
    }

    default void visitEnclosingMethod_attribute(EnclosingMethod_attribute attribute) {
        // Do nothing
    }

    default void visitSynthetic_attribute(Synthetic_attribute attribute) {
        // Do nothing
    }

    default void visitSignature_attribute(Signature_attribute attribute) {
        // Do nothing
    }

    default void visitSourceFile_attribute(SourceFile_attribute attribute) {
        // Do nothing
    }

    default void visitSourceDebugExtension_attribute(SourceDebugExtension_attribute attribute) {
        // Do nothing
    }

    default void visitLineNumberTable_attribute(LineNumberTable_attribute attribute) {
        attribute.getLineNumbers().forEach(lineNumber -> lineNumber.accept(this));
    }

    default void visitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
        attribute.getLocalVariables().forEach(localVariable -> localVariable.accept(this));
    }

    default void visitLocalVariableTypeTable_attribute(LocalVariableTypeTable_attribute attribute) {
        // Do nothing
    }

    default void visitDeprecated_attribute(Deprecated_attribute attribute) {
        // Do nothing
    }

    default void visitRuntimeVisibleAnnotations_attribute(RuntimeVisibleAnnotations_attribute attribute) {
        // Do nothing
    }

    default void visitRuntimeInvisibleAnnotations_attribute(RuntimeInvisibleAnnotations_attribute attribute) {
        // Do nothing
    }

    default void visitRuntimeVisibleParameterAnnotations_attribute(RuntimeVisibleParameterAnnotations_attribute attribute) {
        // Do nothing
    }

    default void visitRuntimeInvisibleParameterAnnotations_attribute(RuntimeInvisibleParameterAnnotations_attribute attribute) {
        // Do nothing
    }

    default void visitRuntimeVisibleTypeAnnotations_attribute(RuntimeVisibleTypeAnnotations_attribute attribute) {
        // Do nothing
    }

    default void visitRuntimeInvisibleTypeAnnotations_attribute(RuntimeInvisibleTypeAnnotations_attribute attribute) {
        // Do nothing
    }

    default void visitAnnotationDefault_attribute(AnnotationDefault_attribute attribute) {
        attribute.getElemementValue().accept(this);
    }

    default void visitStackMapTable_attribute(StackMapTable_attribute attribute) {
        attribute.getEntries().forEach(stackMapFrame -> stackMapFrame.accept(this));
    }

    default void visitBootstrapMethods_attribute(BootstrapMethods_attribute attribute) {
        attribute.getBootstrapMethods().forEach(bootstrapMethod -> bootstrapMethod.accept(this));
    }

    default void visitMethodParameters_attribute(MethodParameters_attribute attribute) {
        attribute.getMethodParameters().forEach(methodParameter -> methodParameter.accept(this));
    }

    default void visitModule_attribute(Module_attribute attribute) {
        attribute.getRequires().forEach(moduleRequires -> moduleRequires.accept(this));
        attribute.getExports().forEach(moduleExports -> moduleExports.accept(this));
        attribute.getOpens().forEach(moduleOpens -> moduleOpens.accept(this));
        attribute.getUses().forEach(moduleUses -> moduleUses.accept(this));
        attribute.getProvides().forEach(moduleProvides -> moduleProvides.accept(this));
    }

    default void visitModulePackages_attribute(ModulePackages_attribute attribute) {
        attribute.getPackages().forEach(modulePackage -> modulePackage.accept(this));
    }

    default void visitModuleMainClass_attribute(ModuleMainClass_attribute attribute) {
        // Do nothing
    }

    default void visitNestHost_attribute(NestHost_attribute attribute) {
        // Do nothing
    }

    default void visitNestMembers_attribute(NestMembers_attribute attribute) {
        attribute.getMembers().forEach(nestMember -> nestMember.accept(this));
    }

    default void visitRecord_attribute(Record_attribute attribute) {
        attribute.getRecordComponents().forEach(recordComponent -> recordComponent.accept(this));
    }

    default void visitPermittedSubclasses_attribute(PermittedSubclasses_attribute attribute) {
        attribute.getSubclasses().forEach(permittedSubclass -> permittedSubclass.accept(this));
    }

    default void visitCustom_attribute(Custom_attribute attribute) {
        // Do nothing
    }

    /*
     * Attribute helpers
     */

    default void visitInstruction(Instruction instruction) {
        // Do nothing
    }

    default void visitExceptionHandler(ExceptionHandler helper) {
        // Do nothing
    }

    default void visitInnerClass(InnerClass helper) {
        // Do nothing
    }

    default void visitLineNumber(LineNumber helper) {
        // Do nothing
    }

    default void visitLocalVariable(LocalVariable helper) {
        // Do nothing
    }

    default void visitLocalVariableType(LocalVariableType helper) {
        // Do nothing
    }

    default void visitBootstrapMethod(BootstrapMethod helper) {
        helper.getBootstrapMethod().accept(this);
        helper.getArguments().forEach(argument -> argument.accept(this));
    }

    default void visitMethodParameter(MethodParameter helper) {
        // Do nothing
    }

    default void visitModuleRequires(ModuleRequires helper) {
        // Do nothing
    }

    default void visitModuleExports(ModuleExports helper) {
        helper.getExportsTos().forEach(moduleExportsTo -> moduleExportsTo.accept(this));
    }

    default void visitModuleExportsTo(ModuleExportsTo helper) {
        // Do nothing
    }

    default void visitModuleOpens(ModuleOpens helper) {
        helper.getOpensTos().forEach(moduleOpensTo -> moduleOpensTo.accept(this));
    }

    default void visitModuleOpensTo(ModuleOpensTo helper) {
        // Do nothing
    }

    default void visitModuleUses(ModuleUses helper) {
        // Do nothing
    }

    default void visitModuleProvides(ModuleProvides helper) {
        helper.getProvidesWiths().forEach(moduleProvidesWith -> moduleProvidesWith.accept(this));
    }

    default void visitModuleProvidesWith(ModuleProvidesWith helper) {
        // Do nothing
    }

    default void visitModulePackage(ModulePackage helper) {
        // Do nothing
    }

    default void visitNestMember(NestMember helper) {
        // Do nothing
    }

    default void visitRecordComponent_info(RecordComponent_info helper) {
        helper.getAttributes().forEach(attribute -> attribute.accept(this));
    }

    default void visitPermittedSubclass(PermittedSubclass helper) {
        // Do nothing
    }

    /*
     * Annotations
     */

    default void visitAnnotation(Annotation helper) {
        helper.getElementValuePairs().forEach(elementValuePair -> elementValuePair.accept(this));
    }

    default void visitParameterAnnotation(ParameterAnnotation helper) {
        helper.getAnnotations().forEach(annotation -> annotation.accept(this));
    }

    default void visitTypeAnnotation(TypeAnnotation helper) {
        helper.getTarget().accept(this);
        helper.getTargetPath().accept(this);
        helper.getElementValuePairs().forEach(elementValuePair -> elementValuePair.accept(this));
    }

    default void visitElementValuePair(ElementValuePair helper) {
        helper.getElementValue().accept(this);
    }

    default void visitByteConstantElementValue(ByteConstantElementValue helper) {
        // Do nothing
    }

    default void visitCharConstantElementValue(CharConstantElementValue helper) {
        // Do nothing
    }

    default void visitDoubleConstantElementValue(DoubleConstantElementValue helper) {
        // Do nothing
    }

    default void visitFloatConstantElementValue(FloatConstantElementValue helper) {
        // Do nothing
    }

    default void visitIntegerConstantElementValue(IntegerConstantElementValue helper) {
        // Do nothing
    }

    default void visitLongConstantElementValue(LongConstantElementValue helper) {
        // Do nothing
    }

    default void visitShortConstantElementValue(ShortConstantElementValue helper) {
        // Do nothing
    }

    default void visitBooleanConstantElementValue(BooleanConstantElementValue helper) {
        // Do nothing
    }

    default void visitStringConstantElementValue(StringConstantElementValue helper) {
        // Do nothing
    }

    default void visitEnumElementValue(EnumElementValue helper) {
        // Do nothing
    }

    default void visitClassElementValue(ClassElementValue helper) {
        // Do nothing
    }

    default void visitAnnotationElementValue(AnnotationElementValue helper) {
        helper.getAnnotation().accept(this);
    }

    default void visitArrayElementValue(ArrayElementValue helper) {
        helper.getValues().forEach(elementValue -> elementValue.accept(this));
    }

    default void visitTypeParameterTarget(TypeParameterTarget helper) {
        // Do nothing
    }

    default void visitSupertypeTarget(SupertypeTarget helper) {
        // Do nothing
    }

    default void visitTypeParameterBoundTarget(TypeParameterBoundTarget helper) {
        // Do nothing
    }

    default void visitEmptyTarget(EmptyTarget helper) {
        // Do nothing
    }

    default void visitFormalParameterTarget(FormalParameterTarget helper) {
        // Do nothing
    }

    default void visitThrowsTarget(ThrowsTarget helper) {
        // Do nothing
    }

    default void visitLocalvarTarget(LocalvarTarget helper) {
        helper.getTable().forEach(entry -> entry.accept(this));
    }

    default void visitCatchTarget(CatchTarget helper) {
        // Do nothing
    }

    default void visitOffsetTarget(OffsetTarget helper) {
        // Do nothing
    }

    default void visitTypeArgumentTarget(TypeArgumentTarget helper) {
        // Do nothing
    }

    default void visitLocalvarTableEntry(LocalvarTableEntry helper) {
        // Do nothing
    }

    default void visitTypePath(TypePath helper) {
        helper.getPath().forEach(entry -> entry.accept(this));
    }

    default void visitTypePathEntry(TypePathEntry helper) {
        // Do nothing
    }

    default void visitSameFrame(SameFrame helper) {
        // Do nothing
    }

    default void visitSameLocals1StackItemFrame(SameLocals1StackItemFrame helper) {
        // Do nothing
    }

    default void visitSameLocals1StackItemFrameExtended(SameLocals1StackItemFrameExtended helper) {
        // Do nothing
    }

    default void visitChopFrame(ChopFrame helper) {
        // Do nothing
    }

    default void visitSameFrameExtended(SameFrameExtended helper) {
        // Do nothing
    }

    default void visitAppendFrame(AppendFrame helper) {
        // Do nothing
    }

    default void visitFullFrame(FullFrame helper) {
        // Do nothing
    }

    default void visitTopVariableInfo(TopVariableInfo helper) {
        // Do nothing
    }

    default void visitIntegerVariableInfo(IntegerVariableInfo helper) {
        // Do nothing
    }

    default void visitFloatVariableInfo(FloatVariableInfo helper) {
        // Do nothing
    }

    default void visitLongVariableInfo(LongVariableInfo helper) {
        // Do nothing
    }

    default void visitDoubleVariableInfo(DoubleVariableInfo helper) {
        // Do nothing
    }

    default void visitNullVariableInfo(NullVariableInfo helper) {
        // Do nothing
    }

    default void visitUninitializedThisVariableInfo(UninitializedThisVariableInfo helper) {
        // Do nothing
    }

    default void visitObjectVariableInfo(ObjectVariableInfo helper) {
        helper.getClassInfo().accept(this);
    }

    default void visitUninitializedVariableInfo(UninitializedVariableInfo helper) {
        // Do nothing
    }
}
