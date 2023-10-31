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
import java.util.stream.*;

import org.apache.log4j.*;

public class MetricsGatherer extends VisitorBase {
    private final Collection<Object> classes = new LinkedList<>();
    private final Collection<Object> interfaces = new LinkedList<>();
    private final Collection<Method_info> methods = new LinkedList<>();
    private final Collection<Field_info> fields = new LinkedList<>();
    private final Collection<Classfile> syntheticClasses = new LinkedList<>();
    private final Collection<Field_info> syntheticFields = new LinkedList<>();
    private final Collection<Method_info> syntheticMethods = new LinkedList<>();
    private final Collection<Classfile> deprecatedClasses = new LinkedList<>();
    private final Collection<Field_info> deprecatedFields = new LinkedList<>();
    private final Collection<Method_info> deprecatedMethods = new LinkedList<>();
    private final Collection<Classfile> publicClasses = new LinkedList<>();
    private final Collection<Field_info> publicFields = new LinkedList<>();
    private final Collection<Method_info> publicMethods = new LinkedList<>();
    private final Collection<InnerClass> publicInnerClasses = new LinkedList<>();
    private final Collection<Field_info> protectedFields = new LinkedList<>();
    private final Collection<Method_info> protectedMethods = new LinkedList<>();
    private final Collection<InnerClass> protectedInnerClasses = new LinkedList<>();
    private final Collection<Field_info> privateFields = new LinkedList<>();
    private final Collection<Method_info> privateMethods = new LinkedList<>();
    private final Collection<InnerClass> privateInnerClasses = new LinkedList<>();
    private final Collection<Classfile> packageClasses = new LinkedList<>();
    private final Collection<Field_info> packageFields = new LinkedList<>();
    private final Collection<Method_info> packageMethods = new LinkedList<>();
    private final Collection<InnerClass> packageInnerClasses = new LinkedList<>();
    private final Collection<Classfile> abstractClasses = new LinkedList<>();
    private final Collection<Method_info> abstractMethods = new LinkedList<>();
    private final Collection<InnerClass> abstractInnerClasses = new LinkedList<>();
    private final Collection<Field_info> staticFields = new LinkedList<>();
    private final Collection<Method_info> staticMethods = new LinkedList<>();
    private final Collection<InnerClass> staticInnerClasses = new LinkedList<>();
    private final Collection<Classfile> finalClasses = new LinkedList<>();
    private final Collection<Field_info> finalFields = new LinkedList<>();
    private final Collection<Method_info> finalMethods = new LinkedList<>();
    private final Collection<InnerClass> finalInnerClasses = new LinkedList<>();
    private final Collection<Method_info> synchronizedMethods = new LinkedList<>();
    private final Collection<Method_info> nativeMethods = new LinkedList<>();
    private final Collection<Field_info> volatileFields = new LinkedList<>();
    private final Collection<Field_info> transientFields = new LinkedList<>();
    private final Map<Integer, Long> constantPoolEntryCounts = new HashMap<>();
    private final Map<String, Long> attributeCounts = new HashMap<>();
    private final Collection<Custom_attribute> customAttributes = new LinkedList<>();
    private final long[] instructionCounts = new long[256];

    private boolean visitingConstantPool = false;

    public MetricsGatherer() {
        IntStream.rangeClosed(1, 20).filter(tag -> tag != 2 && tag != 13 && tag != 14).forEach(tag -> constantPoolEntryCounts.put(tag, 0L));
        Arrays.stream(AttributeType.values()).forEach(attributeType -> attributeCounts.put(attributeType.getAttributeName(), 0L));
        attributeCounts.put("Custom", 0L);
    }

    public Collection<Object> getClasses() {
        return classes;
    }

    public Collection<Object> getInterfaces() {
        return interfaces;
    }

    public Collection<Method_info> getMethods() {
        return methods;
    }

    public Collection<Field_info> getFields() {
        return fields;
    }

    public Collection<Classfile> getSyntheticClasses() {
        return syntheticClasses;
    }

    public Collection<Field_info> getSyntheticFields() {
        return syntheticFields;
    }

    public Collection<Method_info> getSyntheticMethods() {
        return syntheticMethods;
    }

    public Collection<Classfile> getDeprecatedClasses() {
        return deprecatedClasses;
    }

    public Collection<Field_info> getDeprecatedFields() {
        return deprecatedFields;
    }

    public Collection<Method_info> getDeprecatedMethods() {
        return deprecatedMethods;
    }

    public Collection<Classfile> getPublicClasses() {
        return publicClasses;
    }

    public Collection<Field_info> getPublicFields() {
        return publicFields;
    }

    public Collection<Method_info> getPublicMethods() {
        return publicMethods;
    }

    public Collection<InnerClass> getPublicInnerClasses() {
        return publicInnerClasses;
    }

    public Collection<Field_info> getProtectedFields() {
        return protectedFields;
    }

    public Collection<Method_info> getProtectedMethods() {
        return protectedMethods;
    }

    public Collection<InnerClass> getProtectedInnerClasses() {
        return protectedInnerClasses;
    }

    public Collection<Field_info> getPrivateFields() {
        return privateFields;
    }

    public Collection<Method_info> getPrivateMethods() {
        return privateMethods;
    }

    public Collection<InnerClass> getPrivateInnerClasses() {
        return privateInnerClasses;
    }

    public Collection<Classfile> getPackageClasses() {
        return packageClasses;
    }

    public Collection<Field_info> getPackageFields() {
        return packageFields;
    }

    public Collection<Method_info> getPackageMethods() {
        return packageMethods;
    }

    public Collection<InnerClass> getPackageInnerClasses() {
        return packageInnerClasses;
    }

    public Collection<Classfile> getAbstractClasses() {
        return abstractClasses;
    }

    public Collection<Method_info> getAbstractMethods() {
        return abstractMethods;
    }

    public Collection<InnerClass> getAbstractInnerClasses() {
        return abstractInnerClasses;
    }

    public Collection<Field_info> getStaticFields() {
        return staticFields;
    }

    public Collection<Method_info> getStaticMethods() {
        return staticMethods;
    }

    public Collection<InnerClass> getStaticInnerClasses() {
        return staticInnerClasses;
    }

    public Collection<Classfile> getFinalClasses() {
        return finalClasses;
    }

    public Collection<Field_info> getFinalFields() {
        return finalFields;
    }

    public Collection<Method_info> getFinalMethods() {
        return finalMethods;
    }

    public Collection<InnerClass> getFinalInnerClasses() {
        return finalInnerClasses;
    }

    public Collection<Method_info> getSynchronizedMethods() {
        return synchronizedMethods;
    }

    public Collection<Method_info> getNativeMethods() {
        return nativeMethods;
    }

    public Collection<Field_info> getVolatileFields() {
        return volatileFields;
    }

    public Collection<Field_info> getTransientFields() {
        return transientFields;
    }

    public Map<Integer, Long> getConstantPoolEntryCounts() {
        return constantPoolEntryCounts;
    }
    public Map<String, Long> getAttributeCounts() {
        return attributeCounts;
    }

    public Collection<Custom_attribute> getCustomAttributes() {
        return customAttributes;
    }

    public long[] getInstructionCounts() {
        return instructionCounts;
    }
    
    // Classfile
    public void visitClassfile(Classfile classfile) {
        classfile.getConstantPool().accept(this);

        if (classfile.isPublic()) {
            publicClasses.add(classfile);
        } else {
            packageClasses.add(classfile);
        }

        if (classfile.isFinal()) {
            finalClasses.add(classfile);
        }

        if (classfile.isInterface()) {
            interfaces.add(classfile);
        } else {
            classes.add(classfile);
        }

        if (classfile.isAbstract()) {
            abstractClasses.add(classfile);
        }

        super.visitClassfile(classfile);
    }

    // ConstantPool

    public void visitConstantPool(ConstantPool constantPool) {
        visitingConstantPool = true;
        super.visitConstantPool(constantPool);
        visitingConstantPool = false;
    }

    public void visitClass_info(Class_info entry) {
        super.visitClass_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_Class);
    }

    public void visitFieldRef_info(FieldRef_info entry) {
        super.visitFieldRef_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_Fieldref);
    }

    public void visitMethodRef_info(MethodRef_info entry) {
        super.visitMethodRef_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_Methodref);
    }

    public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
        super.visitInterfaceMethodRef_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_InterfaceMethodref);
    }

    public void visitString_info(String_info entry) {
        super.visitString_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_String);
    }

    public void visitInteger_info(Integer_info entry) {
        super.visitInteger_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_Integer);
    }

    public void visitFloat_info(Float_info entry) {
        super.visitFloat_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_Float);
    }

    public void visitLong_info(Long_info entry) {
        super.visitLong_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_Long);
    }

    public void visitDouble_info(Double_info entry) {
        super.visitDouble_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_Double);
    }

    public void visitNameAndType_info(NameAndType_info entry) {
        super.visitNameAndType_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_NameAndType);
    }

    public void visitUTF8_info(UTF8_info entry) {
        super.visitUTF8_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_Utf8);
    }

    public void visitMethodHandle_info(MethodHandle_info entry) {
        super.visitMethodHandle_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_MethodHandle);
    }

    public void visitMethodType_info(MethodType_info entry) {
        super.visitMethodType_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_MethodType);
    }

    public void visitDynamic_info(Dynamic_info entry) {
        super.visitDynamic_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_Dynamic);
    }

    public void visitInvokeDynamic_info(InvokeDynamic_info entry) {
        super.visitInvokeDynamic_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_InvokeDynamic);
    }

    public void visitModule_info(Module_info entry) {
        super.visitModule_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_Module);
    }

    public void visitPackage_info(Package_info entry) {
        super.visitPackage_info(entry);
        visitConstantPoolEntry(com.jeantessier.classreader.impl.ConstantPoolEntry.CONSTANT_Package);
    }

    // Features
    public void visitField_info(Field_info entry) {
        fields.add(entry);

        if (entry.isPublic()) {
            publicFields.add(entry);
        } else if (entry.isPrivate()) {
            privateFields.add(entry);
        } else if (entry.isProtected()) {
            protectedFields.add(entry);
        } else {
            packageFields.add(entry);
        }

        if (entry.isStatic()) {
            staticFields.add(entry);
        }

        if (entry.isFinal()) {
            finalFields.add(entry);
        }

        if (entry.isVolatile()) {
            volatileFields.add(entry);
        }

        if (entry.isTransient()) {
            transientFields.add(entry);
        }

        super.visitField_info(entry);
    }

    public void visitMethod_info(Method_info entry) {
        methods.add(entry);

        if (entry.isPublic()) {
            publicMethods.add(entry);
        } else if (entry.isPrivate()) {
            privateMethods.add(entry);
        } else if (entry.isProtected()) {
            protectedMethods.add(entry);
        } else {
            packageMethods.add(entry);
        }

        if (entry.isStatic()) {
            staticMethods.add(entry);
        }

        if (entry.isFinal()) {
            finalMethods.add(entry);
        }

        if (entry.isSynchronized()) {
            synchronizedMethods.add(entry);
        }

        if (entry.isNative()) {
            nativeMethods.add(entry);
        }

        if (entry.isAbstract()) {
            abstractMethods.add(entry);
        }

        super.visitMethod_info(entry);
    }

    // Attributes
    public void visitConstantValue_attribute(ConstantValue_attribute attribute) {
        super.visitConstantValue_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitCode_attribute(Code_attribute attribute) {
        super.visitCode_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitStackMapTable_attribute(StackMapTable_attribute attribute) {
        super.visitStackMapTable_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitExceptions_attribute(Exceptions_attribute attribute) {
        super.visitExceptions_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitInnerClasses_attribute(InnerClasses_attribute attribute) {
        super.visitInnerClasses_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitEnclosingMethod_attribute(EnclosingMethod_attribute attribute) {
        super.visitEnclosingMethod_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitSynthetic_attribute(Synthetic_attribute attribute) {
        super.visitSynthetic_attribute(attribute);
        visitAttribute(attribute.getAttributeName());

        Object owner = attribute.getOwner();

        if (owner instanceof Classfile) {
            syntheticClasses.add((Classfile) owner);
        } else if (owner instanceof Field_info) {
            syntheticFields.add((Field_info) owner);
        } else if (owner instanceof Method_info) {
            syntheticMethods.add((Method_info) owner);
        } else {
            Logger.getLogger(getClass()).warn("Synthetic attribute on unknown Visitable: " + owner.getClass().getName());
        }
    }

    public void visitSignature_attribute(Signature_attribute attribute) {
        super.visitSignature_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitSourceFile_attribute(SourceFile_attribute attribute) {
        super.visitSourceFile_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitSourceDebugExtension_attribute(SourceDebugExtension_attribute attribute) {
        super.visitSourceDebugExtension_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitLineNumberTable_attribute(LineNumberTable_attribute attribute) {
        super.visitLineNumberTable_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
        super.visitLocalVariableTable_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitLocalVariableTypeTable_attribute(LocalVariableTypeTable_attribute attribute) {
        super.visitLocalVariableTypeTable_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitDeprecated_attribute(Deprecated_attribute attribute) {
        super.visitDeprecated_attribute(attribute);
        visitAttribute(attribute.getAttributeName());

        Object owner = attribute.getOwner();

        if (owner instanceof Classfile) {
            deprecatedClasses.add((Classfile) owner);
        } else if (owner instanceof Field_info) {
            deprecatedFields.add((Field_info) owner);
        } else if (owner instanceof Method_info) {
            deprecatedMethods.add((Method_info) owner);
        } else {
            Logger.getLogger(getClass()).warn("Deprecated attribute on unknown Visitable: " + owner.getClass().getName());
        }
    }

    public void visitRuntimeVisibleAnnotations_attribute(RuntimeVisibleAnnotations_attribute attribute) {
        super.visitRuntimeVisibleAnnotations_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitRuntimeInvisibleAnnotations_attribute(RuntimeInvisibleAnnotations_attribute attribute) {
        super.visitRuntimeInvisibleAnnotations_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitRuntimeVisibleParameterAnnotations_attribute(RuntimeVisibleParameterAnnotations_attribute attribute) {
        super.visitRuntimeVisibleParameterAnnotations_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitRuntimeInvisibleParameterAnnotations_attribute(RuntimeInvisibleParameterAnnotations_attribute attribute) {
        super.visitRuntimeInvisibleParameterAnnotations_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitAnnotationDefault_attribute(AnnotationDefault_attribute attribute) {
        super.visitAnnotationDefault_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitBootstrapMethods_attribute(BootstrapMethods_attribute attribute) {
        super.visitBootstrapMethods_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitMethodParameters_attribute(MethodParameters_attribute attribute) {
        super.visitMethodParameters_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitModule_attribute(Module_attribute attribute) {
        super.visitModule_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitModulePackages_attribute(ModulePackages_attribute attribute) {
        super.visitModulePackages_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitModuleMainClass_attribute(ModuleMainClass_attribute attribute) {
        super.visitModuleMainClass_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitNestHost_attribute(NestHost_attribute attribute) {
        super.visitNestHost_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitNestMembers_attribute(NestMembers_attribute attribute) {
        super.visitNestMembers_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitRecord_attribute(Record_attribute attribute) {
        super.visitRecord_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
    }

    public void visitCustom_attribute(Custom_attribute attribute) {
        super.visitCustom_attribute(attribute);
        visitAttribute(attribute.getAttributeName());
        customAttributes.add(attribute);
    }

    private void visitConstantPoolEntry(int tag) {
        if (visitingConstantPool) {
            constantPoolEntryCounts.put(tag, constantPoolEntryCounts.get(tag) + 1);
        }
    }

    private void visitAttribute(String attributeName) {
        attributeCounts.put(attributeName, attributeCounts.get(attributeName) + 1);
    }

    // Attribute helpers
    public void visitInstruction(Instruction helper) {
        getInstructionCounts()[helper.getOpcode()]++;

        super.visitInstruction(helper);
    }

    public void visitInnerClass(InnerClass helper) {
        if (helper.isPublic()) {
            publicInnerClasses.add(helper);
        } else if (helper.isPrivate()) {
            privateInnerClasses.add(helper);
        } else if (helper.isProtected()) {
            protectedInnerClasses.add(helper);
        } else {
            packageInnerClasses.add(helper);
        }

        if (helper.isStatic()) {
            staticInnerClasses.add(helper);
        }

        if (helper.isFinal()) {
            finalInnerClasses.add(helper);
        }

        if (helper.isInterface()) {
            interfaces.add(helper);
        } else {
            classes.add(helper);
        }

        if (helper.isAbstract()) {
            abstractInnerClasses.add(helper);
        }
    }
}
