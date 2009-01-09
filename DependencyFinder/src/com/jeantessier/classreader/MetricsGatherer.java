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

import org.apache.log4j.*;

public class MetricsGatherer extends VisitorBase {
    private Collection<Object> classes = new LinkedList<Object>();
    private Collection<Object> interfaces = new LinkedList<Object>();
    private Collection<Method_info> methods = new LinkedList<Method_info>();
    private Collection<Field_info> fields = new LinkedList<Field_info>();
    private Collection<Classfile> syntheticClasses = new LinkedList<Classfile>();
    private Collection<Field_info> syntheticFields = new LinkedList<Field_info>();
    private Collection<Method_info> syntheticMethods = new LinkedList<Method_info>();
    private Collection<Classfile> deprecatedClasses = new LinkedList<Classfile>();
    private Collection<Field_info> deprecatedFields = new LinkedList<Field_info>();
    private Collection<Method_info> deprecatedMethods = new LinkedList<Method_info>();
    private Collection<Classfile> publicClasses = new LinkedList<Classfile>();
    private Collection<Field_info> publicFields = new LinkedList<Field_info>();
    private Collection<Method_info> publicMethods = new LinkedList<Method_info>();
    private Collection<InnerClass> publicInnerClasses = new LinkedList<InnerClass>();
    private Collection<Field_info> protectedFields = new LinkedList<Field_info>();
    private Collection<Method_info> protectedMethods = new LinkedList<Method_info>();
    private Collection<InnerClass> protectedInnerClasses = new LinkedList<InnerClass>();
    private Collection<Field_info> privateFields = new LinkedList<Field_info>();
    private Collection<Method_info> privateMethods = new LinkedList<Method_info>();
    private Collection<InnerClass> privateInnerClasses = new LinkedList<InnerClass>();
    private Collection<Classfile> packageClasses = new LinkedList<Classfile>();
    private Collection<Field_info> packageFields = new LinkedList<Field_info>();
    private Collection<Method_info> packageMethods = new LinkedList<Method_info>();
    private Collection<InnerClass> packageInnerClasses = new LinkedList<InnerClass>();
    private Collection<Classfile> abstractClasses = new LinkedList<Classfile>();
    private Collection<Method_info> abstractMethods = new LinkedList<Method_info>();
    private Collection<InnerClass> abstractInnerClasses = new LinkedList<InnerClass>();
    private Collection<Field_info> staticFields = new LinkedList<Field_info>();
    private Collection<Method_info> staticMethods = new LinkedList<Method_info>();
    private Collection<InnerClass> staticInnerClasses = new LinkedList<InnerClass>();
    private Collection<Classfile> finalClasses = new LinkedList<Classfile>();
    private Collection<Field_info> finalFields = new LinkedList<Field_info>();
    private Collection<Method_info> finalMethods = new LinkedList<Method_info>();
    private Collection<InnerClass> finalInnerClasses = new LinkedList<InnerClass>();
    private Collection<Method_info> synchronizedMethods = new LinkedList<Method_info>();
    private Collection<Method_info> nativeMethods = new LinkedList<Method_info>();
    private Collection<Field_info> volatileFields = new LinkedList<Field_info>();
    private Collection<Field_info> transientFields = new LinkedList<Field_info>();
    private Map<String, Long> attributeCounts = new HashMap<String, Long>();
    private Collection<Custom_attribute> customAttributes = new LinkedList<Custom_attribute>();
    private long[] instructionCounts = new long[256];

    public MetricsGatherer() {
        for (AttributeType attributeType : AttributeType.values()) {
            attributeCounts.put(attributeType.getAttributeName(), 0L);
        }
        attributeCounts.put("custom", 0L);
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
        visitAttribute("ConstantValue");
    }

    public void visitCode_attribute(Code_attribute attribute) {
        super.visitCode_attribute(attribute);
        visitAttribute("Code");
    }

    public void visitExceptions_attribute(Exceptions_attribute attribute) {
        super.visitExceptions_attribute(attribute);
        visitAttribute("Exceptions");
    }

    public void visitInnerClasses_attribute(InnerClasses_attribute attribute) {
        super.visitInnerClasses_attribute(attribute);
        visitAttribute("InnerClasses");
    }

    public void visitEnclosingMethod_attribute(EnclosingMethod_attribute attribute) {
        super.visitEnclosingMethod_attribute(attribute);
        visitAttribute("EnclosingMethod");
    }

    public void visitSynthetic_attribute(Synthetic_attribute attribute) {
        super.visitSynthetic_attribute(attribute);
        visitAttribute("Synthetic");

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
        visitAttribute("Signature");
    }

    public void visitSourceFile_attribute(SourceFile_attribute attribute) {
        super.visitSourceFile_attribute(attribute);
        visitAttribute("SourceFile");
    }

    public void visitSourceDebugExtension_attribute(SourceDebugExtension_attribute attribute) {
        super.visitSourceDebugExtension_attribute(attribute);
        visitAttribute("SourceDebugExtension");
    }

    public void visitLineNumberTable_attribute(LineNumberTable_attribute attribute) {
        super.visitLineNumberTable_attribute(attribute);
        visitAttribute("LineNumberTable");
    }

    public void visitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
        super.visitLocalVariableTable_attribute(attribute);
        visitAttribute("LocalVariableTable");
    }

    public void visitLocalVariableType(LocalVariableType helper) {
        super.visitLocalVariableType(helper);
        visitAttribute("LocalVariableTypeTable");
    }

    public void visitDeprecated_attribute(Deprecated_attribute attribute) {
        super.visitDeprecated_attribute(attribute);
        visitAttribute("Deprecated");

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

    public void visitCustom_attribute(Custom_attribute attribute) {
        super.visitCustom_attribute(attribute);
        visitAttribute("custom");
        customAttributes.add(attribute);
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
