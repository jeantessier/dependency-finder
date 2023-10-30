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
    // Classfile
    public void visitClassfiles(Collection<Classfile> classfiles);
    public void visitClassfile(Classfile classfile);

    // ConstantPool entries
    public void visitConstantPool(ConstantPool constantPool);
    public void visitClass_info(Class_info entry);
    public void visitFieldRef_info(FieldRef_info entry);
    public void visitMethodRef_info(MethodRef_info entry);
    public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry);
    public void visitString_info(String_info entry);
    public void visitInteger_info(Integer_info entry);
    public void visitFloat_info(Float_info entry);
    public void visitLong_info(Long_info entry);
    public void visitDouble_info(Double_info entry);
    public void visitNameAndType_info(NameAndType_info entry);
    public void visitUTF8_info(UTF8_info entry);
    public void visitMethodHandle_info(MethodHandle_info entry);
    public void visitMethodType_info(MethodType_info entry);
    public void visitDynamic_info(Dynamic_info entry);
    public void visitInvokeDynamic_info(InvokeDynamic_info entry);
    public void visitModule_info(Module_info entry);
    public void visitPackage_info(Package_info entry);
    public void visitUnusableEntry(UnusableEntry entry);

    // Features
    public void visitField_info(Field_info entry);
    public void visitMethod_info(Method_info entry);

    // Attributes
    public void visitConstantValue_attribute(ConstantValue_attribute attribute);
    public void visitCode_attribute(Code_attribute attribute);
    public void visitExceptions_attribute(Exceptions_attribute attribute);
    public void visitInnerClasses_attribute(InnerClasses_attribute attribute);
    public void visitEnclosingMethod_attribute(EnclosingMethod_attribute attribute);
    public void visitSynthetic_attribute(Synthetic_attribute attribute);
    public void visitSignature_attribute(Signature_attribute attribute);
    public void visitSourceFile_attribute(SourceFile_attribute attribute);
    public void visitSourceDebugExtension_attribute(SourceDebugExtension_attribute attribute);
    public void visitLineNumberTable_attribute(LineNumberTable_attribute attribute);
    public void visitLocalVariableTable_attribute(LocalVariableTable_attribute attribute);
    public void visitLocalVariableTypeTable_attribute(LocalVariableTypeTable_attribute attribute);
    public void visitDeprecated_attribute(Deprecated_attribute attribute);
    public void visitRuntimeVisibleAnnotations_attribute(RuntimeVisibleAnnotations_attribute attribute);
    public void visitRuntimeInvisibleAnnotations_attribute(RuntimeInvisibleAnnotations_attribute attribute);
    public void visitRuntimeVisibleParameterAnnotations_attribute(RuntimeVisibleParameterAnnotations_attribute attribute);
    public void visitRuntimeInvisibleParameterAnnotations_attribute(RuntimeInvisibleParameterAnnotations_attribute attribute);
    public void visitRuntimeVisibleTypeAnnotations_attribute(RuntimeVisibleTypeAnnotations_attribute attribute);
    public void visitRuntimeInvisibleTypeAnnotations_attribute(RuntimeInvisibleTypeAnnotations_attribute attribute);
    public void visitAnnotationDefault_attribute(AnnotationDefault_attribute attribute);
    public void visitStackMapTable_attribute(StackMapTable_attribute attribute);
    public void visitBootstrapMethods_attribute(BootstrapMethods_attribute attribute);
    public void visitMethodParameters_attribute(MethodParameters_attribute attribute);
    public void visitModule_attribute(Module_attribute attribute);
    public void visitModulePackages_attribute(ModulePackages_attribute attribute);
    public void visitModuleMainClass_attribute(ModuleMainClass_attribute attribute);
    public void visitNestHost_attribute(NestHost_attribute attribute);
    public void visitNestMembers_attribute(NestMembers_attribute attribute);
    public void visitCustom_attribute(Custom_attribute attribute);

    // Attribute helpers
    public void visitInstruction(Instruction instruction);
    public void visitExceptionHandler(ExceptionHandler helper);
    public void visitInnerClass(InnerClass helper);
    public void visitLineNumber(LineNumber helper);
    public void visitLocalVariable(LocalVariable helper);
    public void visitLocalVariableType(LocalVariableType helper);
    public void visitBootstrapMethod(BootstrapMethod helper);
    public void visitMethodParameter(MethodParameter helper);
    public void visitModuleRequires(ModuleRequires helper);
    public void visitModuleExports(ModuleExports helper);
    public void visitModuleExportsTo(ModuleExportsTo helper);
    public void visitModuleOpens(ModuleOpens helper);
    public void visitModuleOpensTo(ModuleOpensTo helper);
    public void visitModuleUses(ModuleUses helper);
    public void visitModuleProvides(ModuleProvides helper);
    public void visitModuleProvidesWith(ModuleProvidesWith helper);
    public void visitModulePackage(ModulePackage helper);
    public void visitNestMember(NestMember helper);

    // Annotations
    public void visitAnnotation(Annotation helper);
    public void visitParameterAnnotation(ParameterAnnotation helper);
    public void visitTypeAnnotation(TypeAnnotation helper);
    public void visitElementValuePair(ElementValuePair helper);
    public void visitByteConstantElementValue(ByteConstantElementValue helper);
    public void visitCharConstantElementValue(CharConstantElementValue helper);
    public void visitDoubleConstantElementValue(DoubleConstantElementValue helper);
    public void visitFloatConstantElementValue(FloatConstantElementValue helper);
    public void visitIntegerConstantElementValue(IntegerConstantElementValue helper);
    public void visitLongConstantElementValue(LongConstantElementValue helper);
    public void visitShortConstantElementValue(ShortConstantElementValue helper);
    public void visitBooleanConstantElementValue(BooleanConstantElementValue helper);
    public void visitStringConstantElementValue(StringConstantElementValue helper);
    public void visitEnumElementValue(EnumElementValue helper);
    public void visitClassElementValue(ClassElementValue helper);
    public void visitAnnotationElementValue(AnnotationElementValue helper);
    public void visitArrayElementValue(ArrayElementValue helper);
    public void visitTypeParameterTarget(TypeParameterTarget helper);
    public void visitSupertypeTarget(SupertypeTarget helper);
    public void visitTypeParameterBoundTarget(TypeParameterBoundTarget helper);
    public void visitEmptyTarget(EmptyTarget helper);
    public void visitFormalParameterTarget(FormalParameterTarget helper);
    public void visitThrowsTarget(ThrowsTarget helper);
    public void visitLocalvarTarget(LocalvarTarget helper);
    public void visitCatchTarget(CatchTarget helper);
    public void visitOffsetTarget(OffsetTarget helper);
    public void visitTypeArgumentTarget(TypeArgumentTarget helper);
    public void visitLocalvarTableEntry(LocalvarTableEntry helper);
    public void visitTypePath(TypePath helper);
    public void visitTypePathEntry(TypePathEntry helper);
    public void visitSameFrame(SameFrame helper);
    public void visitSameLocals1StackItemFrame(SameLocals1StackItemFrame helper);
    public void visitSameLocals1StackItemFrameExtended(SameLocals1StackItemFrameExtended helper);
    public void visitChopFrame(ChopFrame helper);
    public void visitSameFrameExtended(SameFrameExtended helper);
    public void visitAppendFrame(AppendFrame helper);
    public void visitFullFrame(FullFrame helper);
    public void visitTopVariableInfo(TopVariableInfo helper);
    public void visitIntegerVariableInfo(IntegerVariableInfo helper);
    public void visitFloatVariableInfo(FloatVariableInfo helper);
    public void visitLongVariableInfo(LongVariableInfo helper);
    public void visitDoubleVariableInfo(DoubleVariableInfo helper);
    public void visitNullVariableInfo(NullVariableInfo helper);
    public void visitUninitializedThisVariableInfo(UninitializedThisVariableInfo helper);
    public void visitObjectVariableInfo(ObjectVariableInfo helper);
    public void visitUninitializedVariableInfo(UninitializedVariableInfo helper);
}
