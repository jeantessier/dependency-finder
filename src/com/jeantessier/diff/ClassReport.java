/*
 *  Copyright (c) 2001-2016, Jean Tessier
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

package com.jeantessier.diff;

import com.jeantessier.classreader.*;
import org.apache.oro.text.perl.Perl5Util;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

public class ClassReport extends Printer implements Comparable, com.jeantessier.classreader.Visitor {
    private static final Perl5Util perl = new Perl5Util();

    private ClassDifferences differences;

    private Collection<FeatureDifferences> removedFields = new TreeSet<FeatureDifferences>();
    private Collection<FeatureDifferences> removedConstructors = new TreeSet<FeatureDifferences>();
    private Collection<FeatureDifferences> removedMethods = new TreeSet<FeatureDifferences>();

    private Collection<FeatureDifferences> deprecatedFields = new TreeSet<FeatureDifferences>();
    private Collection<FeatureDifferences> deprecatedConstructors = new TreeSet<FeatureDifferences>();
    private Collection<FeatureDifferences> deprecatedMethods = new TreeSet<FeatureDifferences>();

    private Collection<FieldDifferences> modifiedFields = new TreeSet<FieldDifferences>();
    private Collection<CodeDifferences> modifiedConstructors = new TreeSet<CodeDifferences>();
    private Collection<CodeDifferences> modifiedMethods = new TreeSet<CodeDifferences>();

    private Collection<FeatureDifferences> undeprecatedFields = new TreeSet<FeatureDifferences>();
    private Collection<FeatureDifferences> undeprecatedConstructors = new TreeSet<FeatureDifferences>();
    private Collection<FeatureDifferences> undeprecatedMethods = new TreeSet<FeatureDifferences>();

    private Collection<FeatureDifferences> newFields = new TreeSet<FeatureDifferences>();
    private Collection<FeatureDifferences> newConstructors = new TreeSet<FeatureDifferences>();
    private Collection<FeatureDifferences> newMethods = new TreeSet<FeatureDifferences>();

    public void visitClassDifferences(ClassDifferences differences) {
        this.differences = differences;

        for (Differences featureDifference : differences.getFeatureDifferences()) {
            featureDifference.accept(this);
        }
    }

    public void visitInterfaceDifferences(InterfaceDifferences differences) {
        this.differences = differences;

        for (Differences featureDifference : differences.getFeatureDifferences()) {
            featureDifference.accept(this);
        }
    }

    public void visitFieldDifferences(FieldDifferences differences) {
        if (differences.isRemoved()) {
            removedFields.add(differences);
        }
    
        if (differences.isModified()) {
            modifiedFields.add(differences);
        }
    
        if (differences.isNew()) {
            newFields.add(differences);
        }

        if (isDeprecated()) {
            deprecatedFields.add(differences);
        }

        if (isUndeprecated()) {
            undeprecatedFields.add(differences);
        }
    }

    public void visitConstructorDifferences(ConstructorDifferences differences) {
        if (differences.isRemoved()) {
            removedConstructors.add(differences);
        }
    
        if (differences.isModified()) {
            modifiedConstructors.add(differences);
        }
    
        if (differences.isNew()) {
            newConstructors.add(differences);
        }

        if (isDeprecated()) {
            deprecatedConstructors.add(differences);
        }

        if (isUndeprecated()) {
            undeprecatedConstructors.add(differences);
        }
    }

    public void visitMethodDifferences(MethodDifferences differences) {
        if (differences.isRemoved()) {
            removedMethods.add(differences);
        }
    
        if (differences.isModified()) {
            modifiedMethods.add(differences);
        }
    
        if (differences.isNew()) {
            newMethods.add(differences);
        }

        if (isDeprecated()) {
            deprecatedMethods.add(differences);
        }

        if (isUndeprecated()) {
            undeprecatedMethods.add(differences);
        }
    }

    public void visitClassfiles(Collection<Classfile> classfiles) {
        // Do nothing
    }

    public void visitClassfile(Classfile classfile) {
        // Do nothing
    }

    public void visitConstantPool(ConstantPool constantPool) {
        // Do nothing
    }

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
        entry.getRawValue().accept(this);
    }

    public void visitInteger_info(Integer_info entry) {
        append(entry.getValue());
    }

    public void visitFloat_info(Float_info entry) {
        append(entry.getValue());
    }

    public void visitLong_info(Long_info entry) {
        append(entry.getValue());
    }

    public void visitDouble_info(Double_info entry) {
        append(entry.getValue());
    }

    public void visitNameAndType_info(NameAndType_info entry) {
        // Do nothing
    }

    public void visitUTF8_info(UTF8_info entry) {
        append(escapeXMLCharactersInAttributeValue(entry.getValue()));
    }

    public void visitMethodHandle_info(MethodHandle_info entry) {
        // Do nothing
    }

    public void visitMethodType_info(MethodType_info entry) {
        // Do nothing
    }

    public void visitInvokeDynamic_info(InvokeDynamic_info entry) {
        // Do nothing
    }

    public void visitField_info(Field_info entry) {
        if (entry.isPublic())     append(" visibility=\"public\"");
        if (entry.isProtected())  append(" visibility=\"protected\"");
        if (entry.isPackage())    append(" visibility=\"package\"");
        if (entry.isPrivate())    append(" visibility=\"private\"");
        if (entry.isStatic())     append(" static=\"yes\"");
        if (entry.isFinal())      append(" final=\"yes\"");
        if (entry.isVolatile())   append(" volatile=\"yes\"");
        if (entry.isTransient())  append(" transient=\"yes\"");
        if (entry.isSynthetic())  append(" synthetic=\"yes\"");
        if (entry.isDeprecated()) append(" deprecated=\"yes\"");

        append(" type=\"").append(entry.getType()).append("\"");
        append(" name=\"").append(entry.getName()).append("\"");
        append(" signature=\"").append(entry.getSignature()).append("\"");
        append(" full-signature=\"").append(entry.getFullSignature()).append("\"");

        if (entry.getConstantValue() != null) {
            append(" value=\"");
            entry.getConstantValue().accept(this);
            append("\"");
        }
    }

    public void visitMethod_info(Method_info entry) {
        // Do nothing
    }

    public void visitConstantValue_attribute(ConstantValue_attribute attribute) {
        attribute.getRawValue().accept(this);
    }

    public void visitCode_attribute(Code_attribute attribute) {
        // Do nothing
    }

    public void visitExceptions_attribute(Exceptions_attribute attribute) {
        // Do nothing
    }

    public void visitInnerClasses_attribute(InnerClasses_attribute attribute) {
        // Do nothing
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
        // Do nothing
    }

    public void visitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
        // Do nothing
    }

    public void visitLocalVariableTypeTable_attribute(LocalVariableTypeTable_attribute attribute) {
        // Do nothing
    }

    public void visitDeprecated_attribute(Deprecated_attribute attribute) {
        // Do nothing
    }

    public void visitRuntimeVisibleAnnotations_attribute(RuntimeVisibleAnnotations_attribute attribute) {
        // Do nothing
    }

    public void visitRuntimeInvisibleAnnotations_attribute(RuntimeInvisibleAnnotations_attribute attribute) {
        // Do nothing
    }

    public void visitRuntimeVisibleParameterAnnotations_attribute(RuntimeVisibleParameterAnnotations_attribute attribute) {
        // Do nothing
    }

    public void visitRuntimeInvisibleParameterAnnotations_attribute(RuntimeInvisibleParameterAnnotations_attribute attribute) {
        // Do nothing
    }

    public void visitAnnotationDefault_attribute(AnnotationDefault_attribute attribute) {
        // Do nothing
    }

    public void visitCustom_attribute(Custom_attribute attribute) {
        // Do nothing
    }

    public void visitInstruction(Instruction instruction) {
        // Do nothing
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

    public void visitParameter(Parameter helper) {
        // Do nothing
    }

    public void visitAnnotation(Annotation helper) {
        // Do nothing
    }

    public void visitElementValuePair(ElementValuePair helper) {
        // Do nothing
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
        // Do nothing
    }

    public void visitArrayElementValue(ArrayElementValue helper) {
        // Do nothing
    }

    public String render() {
        raiseIndent();
        raiseIndent();

        indent().append("<class>").eol();
        raiseIndent();

        indent().append("<name>").append(differences.getName()).append("</name>").eol();

        if (differences.isDeclarationModified()) {
            indent().append("<modified-declaration>").eol();
            raiseIndent();

            indent().append("<old-declaration").append(breakdownDeclaration(differences.getOldClass())).append(">").append(differences.getOldDeclaration()).append("</old-declaration>").eol();
            indent().append("<new-declaration").append(breakdownDeclaration(differences.getNewClass())).append(">").append(differences.getNewDeclaration()).append("</new-declaration>").eol();

            lowerIndent();
            indent().append("</modified-declaration>").eol();
        }

        if (removedFields.size() != 0) {
            indent().append("<removed-fields>").eol();
            raiseIndent();

            for (FeatureDifferences fd : removedFields) {
                indent();
                append("<declaration");
                fd.getOldFeature().accept(this);
                if (fd.isInherited()) {
                    append(" inherited=\"yes\"");
                }
                append(">");
                append(fd.getOldDeclaration());
                append("</declaration>");
                eol();
            }

            lowerIndent();
            indent().append("</removed-fields>").eol();
        }

        if (removedConstructors.size() != 0) {
            indent().append("<removed-constructors>").eol();
            raiseIndent();

            for (FeatureDifferences fd : removedConstructors) {
                indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getOldFeature())).append(fd.isInherited() ? " inherited=\"yes\"" : "").append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</removed-constructors>").eol();
        }

        if (removedMethods.size() != 0) {
            indent().append("<removed-methods>").eol();
            raiseIndent();

            for (FeatureDifferences fd : removedMethods) {
                indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getOldFeature())).append(fd.isInherited() ? " inherited=\"yes\"" : "").append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</removed-methods>").eol();
        }

        if (deprecatedFields.size() != 0) {
            indent().append("<deprecated-fields>").eol();
            raiseIndent();

            for (FeatureDifferences fd : deprecatedFields) {
                indent().append("<declaration").append(breakdownDeclaration((Field_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</deprecated-fields>").eol();
        }

        if (deprecatedConstructors.size() != 0) {
            indent().append("<deprecated-constructors>").eol();
            raiseIndent();

            for (FeatureDifferences fd : deprecatedConstructors) {
                indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</deprecated-constructors>").eol();
        }

        if (deprecatedMethods.size() != 0) {
            indent().append("<deprecated-methods>").eol();
            raiseIndent();

            for (FeatureDifferences fd : deprecatedMethods) {
                indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</deprecated-methods>").eol();
        }

        if (modifiedFields.size() != 0) {
            indent().append("<modified-fields>").eol();
            raiseIndent();

            for (FieldDifferences fd : modifiedFields) {
                indent().append("<feature>").eol();
                raiseIndent();

                indent().append("<name>").append(fd.getName()).append("</name>").eol();

                indent().append("<modified-declaration>").eol();
                raiseIndent();

                Field_info oldField = (Field_info) fd.getOldFeature();
                indent();
                append("<old-declaration");
                oldField.accept(this);
                append(">");
                if (fd.isConstantValueDifference()) {
                    append(escapeXMLCharactersInTagContent(oldField.getFullDeclaration()));
                } else {
                    append(oldField.getDeclaration());
                }
                append("</old-declaration>");
                eol();

                Field_info newField = (Field_info) fd.getNewFeature();
                indent();
                append("<new-declaration");
                newField.accept(this);
                append(">");
                if (fd.isConstantValueDifference()) {
                    append(escapeXMLCharactersInTagContent(newField.getFullDeclaration()));
                } else {
                    append(newField.getDeclaration());
                }
                append("</new-declaration>");
                eol();

                lowerIndent();
                indent().append("</modified-declaration>").eol();

                lowerIndent();
                indent().append("</feature>").eol();
            }

            lowerIndent();
            indent().append("</modified-fields>").eol();
        }

        if (modifiedConstructors.size() != 0) {
            indent().append("<modified-constructors>").eol();
            raiseIndent();

            for (CodeDifferences cd : modifiedConstructors) {
                indent().append("<feature>").eol();
                raiseIndent();

                indent().append("<name>").append(cd.getName()).append("</name>").eol();

                if (!cd.getOldDeclaration().equals(cd.getNewDeclaration())) {
                    indent().append("<modified-declaration>").eol();
                    raiseIndent();
                    indent().append("<old-declaration").append(breakdownDeclaration((Method_info) cd.getOldFeature())).append(">").append(cd.getOldDeclaration()).append("</old-declaration>").eol();
                    indent().append("<new-declaration").append(breakdownDeclaration((Method_info) cd.getNewFeature())).append(">").append(cd.getNewDeclaration()).append("</new-declaration>").eol();
                    lowerIndent();
                    indent().append("</modified-declaration>").eol();
                }

                if (cd.isCodeDifference()) {
                    indent().append("<modified-code").append(breakdownDeclaration((Method_info) cd.getNewFeature())).append(">").append(cd.getNewDeclaration()).append("</modified-code>").eol();
                }

                lowerIndent();
                indent().append("</feature>").eol();
            }

            lowerIndent();
            indent().append("</modified-constructors>").eol();
        }

        if (modifiedMethods.size() != 0) {
            indent().append("<modified-methods>").eol();
            raiseIndent();

            for (CodeDifferences md : modifiedMethods) {
                indent().append("<feature>").eol();
                raiseIndent();

                indent().append("<name>").append(md.getName()).append("</name>").eol();

                if (!md.getOldDeclaration().equals(md.getNewDeclaration())) {
                    indent().append("<modified-declaration>").eol();
                    raiseIndent();
                    indent().append("<old-declaration").append(breakdownDeclaration((Method_info) md.getOldFeature())).append(">").append(md.getOldDeclaration()).append("</old-declaration>").eol();
                    indent().append("<new-declaration").append(breakdownDeclaration((Method_info) md.getNewFeature())).append(">").append(md.getNewDeclaration()).append("</new-declaration>").eol();
                    lowerIndent();
                    indent().append("</modified-declaration>").eol();
                }

                if (md.isCodeDifference()) {
                    indent().append("<modified-code").append(breakdownDeclaration((Method_info) md.getNewFeature())).append(">").append(md.getNewDeclaration()).append("</modified-code>").eol();
                }

                lowerIndent();
                indent().append("</feature>").eol();
            }

            lowerIndent();
            indent().append("</modified-methods>").eol();
        }

        if (undeprecatedFields.size() != 0) {
            indent().append("<undeprecated-fields>").eol();
            raiseIndent();

            for (FeatureDifferences fd : undeprecatedFields) {
                indent().append("<declaration").append(breakdownDeclaration((Field_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</undeprecated-fields>").eol();
        }

        if (undeprecatedConstructors.size() != 0) {
            indent().append("<undeprecated-constructors>").eol();
            raiseIndent();

            for (FeatureDifferences fd : undeprecatedConstructors) {
                indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</undeprecated-constructors>").eol();
        }

        if (undeprecatedMethods.size() != 0) {
            indent().append("<undeprecated-methods>").eol();
            raiseIndent();

            for (FeatureDifferences fd : undeprecatedMethods) {
                indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</undeprecated-methods>").eol();
        }

        if (newFields.size() != 0) {
            indent().append("<new-fields>").eol();
            raiseIndent();

            for (FeatureDifferences fd : newFields) {
                indent();
                append("<declaration");
                fd.getNewFeature().accept(this);
                append(">");
                append(fd.getNewDeclaration());
                append("</declaration>");
                eol();
            }

            lowerIndent();
            indent().append("</new-fields>").eol();
        }

        if (newConstructors.size() != 0) {
            indent().append("<new-constructors>").eol();
            raiseIndent();

            for (FeatureDifferences fd : newConstructors) {
                indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getNewDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</new-constructors>").eol();
        }

        if (newMethods.size() != 0) {
            indent().append("<new-methods>").eol();
            raiseIndent();

            for (FeatureDifferences fd : newMethods) {
                indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getNewDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</new-methods>").eol();
        }

        lowerIndent();
        indent().append("</class>").eol();

        lowerIndent();
        lowerIndent();

        return super.toString();
    }

    private String breakdownDeclaration(Classfile element) {
        StringBuffer result = new StringBuffer();

        if (element != null) {
            if (element.isPublic())     result.append(" visibility=\"public\"");
            if (element.isPackage())    result.append(" visibility=\"package\"");
            if (element.isFinal())      result.append(" final=\"yes\"");
            if (element.isSuper())      result.append(" super=\"yes\"");
            if (element.isSynthetic())  result.append(" synthetic=\"yes\"");
            if (element.isDeprecated()) result.append(" deprecated=\"yes\"");

            result.append(" name=\"").append(element.getClassName()).append("\"");

            if (element.isInterface()) {
                result.append(" interface=\"yes\"");

                result.append(" extends=\"");
                Iterator i = element.getAllInterfaces().iterator();
                while (i.hasNext()) {
                    result.append(i.next());
                    if (i.hasNext()) {
                        result.append(", ");
                    }
                }
                result.append("\"");
            } else {
                if (element.isAbstract()) result.append(" abstract=\"yes\"");

                result.append(" extends=\"").append(element.getSuperclassName()).append("\"");

                result.append(" implements=\"");
                Iterator i = element.getAllInterfaces().iterator();
                while (i.hasNext()) {
                    result.append(i.next());
                    if (i.hasNext()) {
                        result.append(", ");
                    }
                }
                result.append("\"");
            }
        }

        return result.toString();
    }

    private String breakdownDeclaration(Field_info element) {
        StringBuffer result = new StringBuffer();

        if (element != null) {
            if (element.isPublic())     result.append(" visibility=\"public\"");
            if (element.isProtected())  result.append(" visibility=\"protected\"");
            if (element.isPackage())    result.append(" visibility=\"package\"");
            if (element.isPrivate())    result.append(" visibility=\"private\"");
            if (element.isStatic())     result.append(" static=\"yes\"");
            if (element.isFinal())      result.append(" final=\"yes\"");
            if (element.isVolatile())   result.append(" volatile=\"yes\"");
            if (element.isTransient())  result.append(" transient=\"yes\"");
            if (element.isSynthetic())  result.append(" synthetic=\"yes\"");
            if (element.isDeprecated()) result.append(" deprecated=\"yes\"");

            result.append(" type=\"").append(element.getType()).append("\"");
            result.append(" name=\"").append(element.getName()).append("\"");
            result.append(" signature=\"").append(element.getSignature()).append("\"");
            result.append(" full-signature=\"").append(element.getFullSignature()).append("\"");

            if (element.getConstantValue() != null) {
                result.append(" value=\"").append(element.getConstantValue().getRawValue()).append("\"");
            }
        }

        return result.toString();
    }

    private String breakdownDeclaration(Method_info element) {
        StringBuffer result = new StringBuffer();

        if (element != null) {
            if (element.isPublic())       result.append(" visibility=\"public\"");
            if (element.isProtected())    result.append(" visibility=\"protected\"");
            if (element.isPackage())      result.append(" visibility=\"package\"");
            if (element.isPrivate())      result.append(" visibility=\"private\"");
            if (element.isStatic())       result.append(" static=\"yes\"");
            if (element.isFinal())        result.append(" final=\"yes\"");
            if (element.isSynchronized()) result.append(" synchronized=\"yes\"");
            if (element.isNative())       result.append(" native=\"yes\"");
            if (element.isAbstract())     result.append(" abstract=\"yes\"");
            if (element.isStrict())       result.append(" strict=\"yes\"");
            if (element.isSynthetic())    result.append(" synthetic=\"yes\"");
            if (element.isDeprecated())   result.append(" deprecated=\"yes\"");

            if (!element.getName().equals("<init>") && !element.getName().equals("<clinit>")) {
                result.append(" return-type=\"").append(element.getReturnType()).append("\"");
            }

            result.append(" signature=\"").append(element.getSignature()).append("\"");
            result.append(" full-signature=\"").append(element.getFullSignature()).append("\"");

            result.append(" throws=\"");
            Iterator i = element.getExceptions().iterator();
            while (i.hasNext()) {
                result.append(i.next());
                if (i.hasNext()) {
                    result.append(", ");
                }
            }
            result.append("\"");
        }

        return result.toString();
    }

    public int compareTo(Object other) {
        int result;

        if (other instanceof ClassReport) {
            result = differences.compareTo(((ClassReport) other).differences);
        } else {
            throw new ClassCastException("Unable to compare ClassReport to " + other.getClass().getName());
        }

        return result;
    }

    private String escapeXMLCharactersInTagContent(String text) {
        String result = text;

        result = perl.substitute("s/&/&amp;/g", result);
        result = perl.substitute("s/</&lt;/g", result);
        result = perl.substitute("s/>/&gt;/g", result);

        return result;
    }

    private String escapeXMLCharactersInAttributeValue(String text) {
        String result = escapeXMLCharactersInTagContent(text);

        result = perl.substitute("s/\"/&quot;/g", result);
        result = perl.substitute("s/'/&apos;/g", result);

        return result;
    }
}
