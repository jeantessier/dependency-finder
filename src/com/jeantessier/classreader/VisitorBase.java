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

import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.*;

public abstract class VisitorBase implements Visitor {
    protected static final int STARTING_INDEX = 1;

    private int currentIndex = STARTING_INDEX;

    protected void resetIndex() {
        currentIndex = STARTING_INDEX;
    }

    protected void incrementIndex() {
        currentIndex++;
    }

    protected int currentIndex() {
        return currentIndex;
    }

    public void visitConstantPool(ConstantPool constantPool) {
        resetIndex();

        constantPool.stream()
                .skip(1) // Constant pool indices start at 1
                .forEach(entry -> {
                    Logger.getLogger(getClass()).debug("Visiting constant pool entry " + currentIndex() + ": " + entry);
                    entry.accept(this);
                    incrementIndex();
                });
    }

    // Classfile
    public void visitClassfiles(Collection<Classfile> classfiles) {
        classfiles.forEach(classfile -> classfile.accept(this));
    }

    public void visitClassfile(Classfile classfile) {
        visitClassfileFields(classfile);
        visitClassfileMethods(classfile);
        visitClassfileAttributes(classfile);
    }

    protected void visitClassfileFields(Classfile classfile) {
        classfile.getAllFields().forEach(field -> field.accept(this));
    }

    protected void visitClassfileMethods(Classfile classfile) {
        classfile.getAllMethods().forEach(method -> method.accept(this));
    }

    protected void visitClassfileAttributes(Classfile classfile) {
        visitAttributes(classfile.getAttributes());
    }

    // ConstantPool entries

    public void visitClass_info(Class_info entry) {
        // Do nothing
    }

    public void visitFieldRef_info(FieldRef_info entry) {
        // Do nothing
    }

    public void visitMethodRef_info(MethodRef_info entry) {
        // Do nothing
    }

    public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
        // Do nothing
    }

    public void visitString_info(String_info entry) {
        // Do nothing
    }

    public void visitInteger_info(Integer_info entry) {
        // Do nothing
    }

    public void visitFloat_info(Float_info entry) {
        // Do nothing
    }

    public void visitLong_info(Long_info entry) {
        // Do nothing
    }

    public void visitDouble_info(Double_info entry) {
        // Do nothing
    }

    public void visitNameAndType_info(NameAndType_info entry) {
        // Do nothing
    }

    public void visitUTF8_info(UTF8_info entry) {
        // Do nothing
    }

    public void visitMethodHandle_info(MethodHandle_info entry) {
        // Do nothing
    }

    public void visitMethodType_info(MethodType_info entry) {
        // Do nothing
    }

    public void visitDynamic_info(Dynamic_info entry) {
        // Do nothing
    }

    public void visitInvokeDynamic_info(InvokeDynamic_info entry) {
        // Do nothing
    }

    public void visitModule_info(Module_info entry) {
        // Do nothing
    }

    public void visitPackage_info(Package_info entry) {
        // Do nothing
    }

    public void visitUnusableEntry(UnusableEntry entry) {
        // Do nothing
    }

    // Features

    public void visitField_info(Field_info entry) {
        visitAttributes(entry.getAttributes());
    }

    public void visitMethod_info(Method_info entry) {
        visitAttributes(entry.getAttributes());
    }

    // Attributes

    protected void visitAttributes(Collection<? extends Attribute_info> attributes) {
        attributes.forEach(attributeInfo -> attributeInfo.accept(this));
    }

    public void visitConstantValue_attribute(ConstantValue_attribute attribute) {
        // Do nothing
    }

    public void visitCode_attribute(Code_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting instruction(s) ...");
        visitInstructions(attribute);

        Collection<? extends ExceptionHandler> exceptionHandlers = attribute.getExceptionHandlers();
        Logger.getLogger(getClass()).debug("Visiting " + exceptionHandlers.size() + " exception handler(s) ...");
        visitExceptionHandlers(exceptionHandlers);

        Collection<? extends Attribute_info> attributes = attribute.getAttributes();
        Logger.getLogger(getClass()).debug("Visiting " + attributes.size() + " code attribute(s) ...");
        visitAttributes(attributes);
    }

    public void visitExceptions_attribute(Exceptions_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getExceptions().size() + " exception class(es) ...");
        attribute.getExceptions().forEach(exception -> exception.accept(this));
    }

    public void visitInnerClasses_attribute(InnerClasses_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getInnerClasses().size() + " inner class(es) ...");
        attribute.getInnerClasses().forEach(innerClass -> innerClass.accept(this));
    }

    public void visitEnclosingMethod_attribute(EnclosingMethod_attribute attribute) {
        // Do nothing
    }

    public void visitSynthetic_attribute(Synthetic_attribute attribute) {
        // Do nothing
    }

    public void visitSignature_attribute(Signature_attribute attribute) {
        // Do nothing
    }

    public void visitSourceFile_attribute(SourceFile_attribute attribute) {
        // Do nothing
    }

    public void visitSourceDebugExtension_attribute(SourceDebugExtension_attribute attribute) {
        // Do nothing
    }

    public void visitLineNumberTable_attribute(LineNumberTable_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getLineNumbers().size() + " line number(s) ...");
        attribute.getLineNumbers().forEach(lineNumber -> lineNumber.accept(this));
    }

    public void visitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getLocalVariables().size() + " local variable(s) ...");
        attribute.getLocalVariables().forEach(localVariable -> localVariable.accept(this));
    }

    public void visitLocalVariableTypeTable_attribute(LocalVariableTypeTable_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getLocalVariableTypes().size() + " local variable type(s) ...");
        attribute.getLocalVariableTypes().forEach(localVariableType -> localVariableType.accept(this));
    }

    public void visitDeprecated_attribute(Deprecated_attribute attribute) {
        // Do nothing
    }

    public void visitRuntimeVisibleAnnotations_attribute(RuntimeVisibleAnnotations_attribute attribute) {
        visitRuntimeAnnotations_attribute(attribute);
    }

    public void visitRuntimeInvisibleAnnotations_attribute(RuntimeInvisibleAnnotations_attribute attribute) {
        visitRuntimeAnnotations_attribute(attribute);
    }

    protected void visitRuntimeAnnotations_attribute(RuntimeAnnotations_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getAnnotations().size() + " annotation(s) ...");
        attribute.getAnnotations().forEach(annotation -> annotation.accept(this));
    }

    public void visitRuntimeVisibleTypeAnnotations_attribute(RuntimeVisibleTypeAnnotations_attribute attribute) {
        visitRuntimeTypeAnnotations_attribute(attribute);
    }

    public void visitRuntimeInvisibleTypeAnnotations_attribute(RuntimeInvisibleTypeAnnotations_attribute attribute) {
        visitRuntimeTypeAnnotations_attribute(attribute);
    }

    protected void visitRuntimeTypeAnnotations_attribute(RuntimeTypeAnnotations_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getTypeAnnotations().size() + " type annotation(s) ...");
        attribute.getTypeAnnotations().forEach(parameterAnnotation -> parameterAnnotation.accept(this));
    }

    public void visitRuntimeVisibleParameterAnnotations_attribute(RuntimeVisibleParameterAnnotations_attribute attribute) {
        visitRuntimeParameterAnnotations_attribute(attribute);
    }

    public void visitRuntimeInvisibleParameterAnnotations_attribute(RuntimeInvisibleParameterAnnotations_attribute attribute) {
        visitRuntimeParameterAnnotations_attribute(attribute);
    }

    protected void visitRuntimeParameterAnnotations_attribute(RuntimeParameterAnnotations_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getParameterAnnotations().size() + " parameter annotation(s) ...");
        attribute.getParameterAnnotations().forEach(parameterAnnotation -> parameterAnnotation.accept(this));
    }

    public void visitAnnotationDefault_attribute(AnnotationDefault_attribute attribute) {
        attribute.getElemementValue().accept(this);
    }

    public void visitStackMapTable_attribute(StackMapTable_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getEntries().size() + " stack map frame(s) ...");
        attribute.getEntries().forEach(stackMapFrame -> stackMapFrame.accept(this));
    }

    public void visitBootstrapMethods_attribute(BootstrapMethods_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getBootstrapMethods().size() + " bootstrap method(s) ...");
        attribute.getBootstrapMethods().forEach(bootstrapMethod -> bootstrapMethod.accept(this));
    }

    public void visitMethodParameters_attribute(MethodParameters_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getMethodParameters().size() + " method parameter(s) ...");
        attribute.getMethodParameters().forEach(methodParameter -> methodParameter.accept(this));
    }

    public void visitModule_attribute(Module_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getRequires().size() + " module requires ...");
        attribute.getRequires().forEach(moduleRequires -> moduleRequires.accept(this));

        Logger.getLogger(getClass()).debug("Visiting " + attribute.getExports().size() + " module exports ...");
        attribute.getExports().forEach(moduleExports -> moduleExports.accept(this));

        Logger.getLogger(getClass()).debug("Visiting " + attribute.getOpens().size() + " module opens ...");
        attribute.getOpens().forEach(moduleOpens -> moduleOpens.accept(this));

        Logger.getLogger(getClass()).debug("Visiting " + attribute.getUses().size() + " module uses ...");
        attribute.getUses().forEach(moduleUses -> moduleUses.accept(this));

        Logger.getLogger(getClass()).debug("Visiting " + attribute.getProvides().size() + " module provides ...");
        attribute.getProvides().forEach(moduleProvides -> moduleProvides.accept(this));
    }

    public void visitModulePackages_attribute(ModulePackages_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getPackages().size() + " module packages ...");
        attribute.getPackages().forEach(modulePackage -> modulePackage.accept(this));
    }

    public void visitModuleMainClass_attribute(ModuleMainClass_attribute attribute) {
        // Do nothing
    }

    public void visitNestHost_attribute(NestHost_attribute attribute) {
        // Do nothing
    }

    public void visitNestMembers_attribute(NestMembers_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getMembers().size() + " nest members ...");
        attribute.getMembers().forEach(nestMember -> nestMember.accept(this));
    }

    public void visitCustom_attribute(Custom_attribute attribute) {
        // Do nothing
    }

    // Attribute helpers

    protected void visitInstructions(Code_attribute attribute) {
        attribute.forEach(instruction -> instruction.accept(this));
    }

    public void visitInstruction(Instruction helper) {
        // Do nothing
    }

    protected void visitExceptionHandlers(Collection<? extends ExceptionHandler> exceptionHandlers) {
        exceptionHandlers.forEach(exceptionHandler -> exceptionHandler.accept(this));
    }

    public void visitExceptionHandler(ExceptionHandler helper) {
        // Do nothing
    }

    public void visitInnerClass(InnerClass helper) {
        // Do nothing
    }

    public void visitLineNumber(LineNumber helper) {
        // Do nothing
    }

    public void visitLocalVariable(LocalVariable helper) {
        // Do nothing
    }

    public void visitLocalVariableType(LocalVariableType helper) {
        // Do nothing
    }

    public void visitBootstrapMethod(BootstrapMethod helper) {
        Logger.getLogger(getClass()).debug("Visiting bootstrap method handle ...");
        helper.getBootstrapMethod().accept(this);

        Logger.getLogger(getClass()).debug("Visiting " + helper.getArguments().size() + " argument(s) ...");
        helper.getArguments().forEach(argument -> argument.accept(this));
    }

    public void visitMethodParameter(MethodParameter helper) {
        // Do nothing
    }

    public void visitModuleRequires(ModuleRequires helper) {
        // Do nothing
    }

    public void visitModuleExports(ModuleExports helper) {
        Logger.getLogger(getClass()).debug("Visiting " + helper.getExportsTos().size() + " exports to(s) ...");
        helper.getExportsTos().forEach(moduleExportsTo -> moduleExportsTo.accept(this));
    }

    public void visitModuleExportsTo(ModuleExportsTo helper) {
        // Do nothing
    }

    public void visitModuleOpens(ModuleOpens helper) {
        Logger.getLogger(getClass()).debug("Visiting " + helper.getOpensTos().size() + " opens to(s) ...");
        helper.getOpensTos().forEach(moduleOpensTo -> moduleOpensTo.accept(this));
    }

    public void visitModuleOpensTo(ModuleOpensTo helper) {
        // Do nothing
    }

    public void visitModuleUses(ModuleUses helper) {
        // Do nothing
    }

    public void visitModuleProvides(ModuleProvides helper) {
        Logger.getLogger(getClass()).debug("Visiting " + helper.getProvidesWiths().size() + " provides with(s) ...");
        helper.getProvidesWiths().forEach(moduleProvidesWith -> moduleProvidesWith.accept(this));
    }

    public void visitModuleProvidesWith(ModuleProvidesWith helper) {
        // Do nothing
    }

    public void visitModulePackage(ModulePackage helper) {
        // Do nothing
    }

    public void visitNestMember(NestMember helper) {
        // Do nothing
    }

    public void visitAnnotation(Annotation helper) {
        Logger.getLogger(getClass()).debug("Visiting " + helper.getElementValuePairs().size() + " element value pair(s) ...");
        helper.getElementValuePairs().forEach(elementValuePair -> elementValuePair.accept(this));
    }

    public void visitParameterAnnotation(ParameterAnnotation helper) {
        Logger.getLogger(getClass()).debug("Visiting " + helper.getAnnotations().size() + " annotation(s) ...");
        helper.getAnnotations().forEach(annotation -> annotation.accept(this));
    }

    public void visitTypeAnnotation(TypeAnnotation helper) {
        helper.getTarget().accept(this);
        helper.getTargetPath().accept(this);

        Logger.getLogger(getClass()).debug("Visiting " + helper.getElementValuePairs().size() + " element value pair(s) ...");
        helper.getElementValuePairs().forEach(elementValuePair -> elementValuePair.accept(this));
    }

    public void visitElementValuePair(ElementValuePair helper) {
        helper.getElementValue().accept(this);
    }

    public void visitByteConstantElementValue(ByteConstantElementValue helper) {
        // Do nothing
    }

    public void visitCharConstantElementValue(CharConstantElementValue helper) {
        // Do nothing
    }

    public void visitDoubleConstantElementValue(DoubleConstantElementValue helper) {
        // Do nothing
    }

    public void visitFloatConstantElementValue(FloatConstantElementValue helper) {
        // Do nothing
    }

    public void visitIntegerConstantElementValue(IntegerConstantElementValue helper) {
        // Do nothing
    }

    public void visitLongConstantElementValue(LongConstantElementValue helper) {
        // Do nothing
    }

    public void visitShortConstantElementValue(ShortConstantElementValue helper) {
        // Do nothing
    }

    public void visitBooleanConstantElementValue(BooleanConstantElementValue helper) {
        // Do nothing
    }

    public void visitStringConstantElementValue(StringConstantElementValue helper) {
        // Do nothing
    }

    public void visitEnumElementValue(EnumElementValue helper) {
        // Do nothing
    }

    public void visitClassElementValue(ClassElementValue helper) {
        // Do nothing
    }

    public void visitAnnotationElementValue(AnnotationElementValue helper) {
        helper.getAnnotation().accept(this);
    }

    public void visitArrayElementValue(ArrayElementValue helper) {
        Logger.getLogger(getClass()).debug("Visiting " + helper.getValues().size() + " value(s) ...");
        helper.getValues().forEach(elementValue -> elementValue.accept(this));
    }

    public void visitTypeParameterTarget(TypeParameterTarget helper) {
        // Do nothing
    }

    public void visitSupertypeTarget(SupertypeTarget helper) {
        // Do nothing
    }

    public void visitTypeParameterBoundTarget(TypeParameterBoundTarget helper) {
        // Do nothing
    }

    public void visitEmptyTarget(EmptyTarget helper) {
        // Do nothing
    }

    public void visitFormalParameterTarget(FormalParameterTarget helper) {
        // Do nothing
    }

    public void visitThrowsTarget(ThrowsTarget helper) {
        // Do nothing
    }

    public void visitLocalvarTarget(LocalvarTarget helper) {
        Logger.getLogger(getClass()).debug("Visiting " + helper.getTable().size() + " table entry(ies) ...");
        helper.getTable().forEach(entry -> entry.accept(this));
    }

    public void visitCatchTarget(CatchTarget helper) {
        // Do nothing
    }

    public void visitOffsetTarget(OffsetTarget helper) {
        // Do nothing
    }

    public void visitTypeArgumentTarget(TypeArgumentTarget helper) {
        // Do nothing
    }

    public void visitLocalvarTableEntry(LocalvarTableEntry helper) {
        // Do nothing
    }

    public void visitTypePath(TypePath helper) {
        Logger.getLogger(getClass()).debug("Visiting " + helper.getPath().size() + " path entry(ies) ...");
        helper.getPath().forEach(entry -> entry.accept(this));
    }

    public void visitTypePathEntry(TypePathEntry helper) {
        // Do nothing
    }

    public void visitSameFrame(SameFrame helper) {
        // Do nothing
    }

    public void visitSameLocals1StackItemFrame(SameLocals1StackItemFrame helper) {
        visitVerificationTypeInfos(Stream.of(helper.getStack()));
    }

    public void visitSameLocals1StackItemFrameExtended(SameLocals1StackItemFrameExtended helper) {
        visitVerificationTypeInfos(Stream.of(helper.getStack()));
    }

    public void visitChopFrame(ChopFrame helper) {
        // Do nothing
    }

    public void visitSameFrameExtended(SameFrameExtended helper) {
        // Do nothing
    }

    public void visitAppendFrame(AppendFrame helper) {
        visitVerificationTypeInfos(helper.getLocals().stream());
    }

    public void visitFullFrame(FullFrame helper) {
        visitVerificationTypeInfos(Stream.concat(helper.getLocals().stream(), helper.getStack().stream()));
    }

    protected void visitVerificationTypeInfos(Stream<? extends VerificationTypeInfo> stacks) {
        stacks.forEach(stack -> stack.accept(this));
    }

    public void visitTopVariableInfo(TopVariableInfo helper) {
        // Do nothing
    }

    public void visitIntegerVariableInfo(IntegerVariableInfo helper) {
        // Do nothing
    }

    public void visitFloatVariableInfo(FloatVariableInfo helper) {
        // Do nothing
    }

    public void visitLongVariableInfo(LongVariableInfo helper) {
        // Do nothing
    }

    public void visitDoubleVariableInfo(DoubleVariableInfo helper) {
        // Do nothing
    }

    public void visitNullVariableInfo(NullVariableInfo helper) {
        // Do nothing
    }

    public void visitUninitializedThisVariableInfo(UninitializedThisVariableInfo helper) {
        // Do nothing
    }

    public void visitObjectVariableInfo(ObjectVariableInfo helper) {
        helper.getClassInfo().accept(this);
    }

    public void visitUninitializedVariableInfo(UninitializedVariableInfo helper) {
        // Do nothing
    }
}
