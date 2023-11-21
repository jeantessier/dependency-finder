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

package com.jeantessier.diff;

import com.jeantessier.classreader.*;
import org.apache.oro.text.perl.Perl5Util;

import java.util.*;

public class ClassReport extends Printer implements Comparable<ClassReport>, com.jeantessier.classreader.Visitor {
    private static final Perl5Util perl = new Perl5Util();

    private ClassDifferences differences;

    private final Collection<FeatureDifferences> removedFields = new TreeSet<>();
    private final Collection<FeatureDifferences> removedConstructors = new TreeSet<>();
    private final Collection<FeatureDifferences> removedMethods = new TreeSet<>();

    private final Collection<FeatureDifferences> deprecatedFields = new TreeSet<>();
    private final Collection<FeatureDifferences> deprecatedConstructors = new TreeSet<>();
    private final Collection<FeatureDifferences> deprecatedMethods = new TreeSet<>();

    private final Collection<FieldDifferences> modifiedFields = new TreeSet<>();
    private final Collection<CodeDifferences> modifiedConstructors = new TreeSet<>();
    private final Collection<CodeDifferences> modifiedMethods = new TreeSet<>();

    private final Collection<FeatureDifferences> undeprecatedFields = new TreeSet<>();
    private final Collection<FeatureDifferences> undeprecatedConstructors = new TreeSet<>();
    private final Collection<FeatureDifferences> undeprecatedMethods = new TreeSet<>();

    private final Collection<FeatureDifferences> newFields = new TreeSet<>();
    private final Collection<FeatureDifferences> newConstructors = new TreeSet<>();
    private final Collection<FeatureDifferences> newMethods = new TreeSet<>();

    public ClassReport() {
        super();
    }

    public ClassReport(String indentText) {
        super(indentText);
    }

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

    public void visitUTF8_info(UTF8_info entry) {
        append(escapeXMLCharactersInAttributeValue(entry.getValue()));
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

    public void visitConstantValue_attribute(ConstantValue_attribute attribute) {
        attribute.getRawValue().accept(this);
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

        if (!removedFields.isEmpty()) {
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

        if (!removedConstructors.isEmpty()) {
            indent().append("<removed-constructors>").eol();
            raiseIndent();

            for (FeatureDifferences fd : removedConstructors) {
                indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getOldFeature())).append(fd.isInherited() ? " inherited=\"yes\"" : "").append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</removed-constructors>").eol();
        }

        if (!removedMethods.isEmpty()) {
            indent().append("<removed-methods>").eol();
            raiseIndent();

            for (FeatureDifferences fd : removedMethods) {
                indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getOldFeature())).append(fd.isInherited() ? " inherited=\"yes\"" : "").append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</removed-methods>").eol();
        }

        if (!deprecatedFields.isEmpty()) {
            indent().append("<deprecated-fields>").eol();
            raiseIndent();

            for (FeatureDifferences fd : deprecatedFields) {
                indent().append("<declaration").append(breakdownDeclaration((Field_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</deprecated-fields>").eol();
        }

        if (!deprecatedConstructors.isEmpty()) {
            indent().append("<deprecated-constructors>").eol();
            raiseIndent();

            for (FeatureDifferences fd : deprecatedConstructors) {
                indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</deprecated-constructors>").eol();
        }

        if (!deprecatedMethods.isEmpty()) {
            indent().append("<deprecated-methods>").eol();
            raiseIndent();

            for (FeatureDifferences fd : deprecatedMethods) {
                indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</deprecated-methods>").eol();
        }

        if (!modifiedFields.isEmpty()) {
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

        if (!modifiedConstructors.isEmpty()) {
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

        if (!modifiedMethods.isEmpty()) {
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

        if (!undeprecatedFields.isEmpty()) {
            indent().append("<undeprecated-fields>").eol();
            raiseIndent();

            for (FeatureDifferences fd : undeprecatedFields) {
                indent().append("<declaration").append(breakdownDeclaration((Field_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</undeprecated-fields>").eol();
        }

        if (!undeprecatedConstructors.isEmpty()) {
            indent().append("<undeprecated-constructors>").eol();
            raiseIndent();

            for (FeatureDifferences fd : undeprecatedConstructors) {
                indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</undeprecated-constructors>").eol();
        }

        if (!undeprecatedMethods.isEmpty()) {
            indent().append("<undeprecated-methods>").eol();
            raiseIndent();

            for (FeatureDifferences fd : undeprecatedMethods) {
                indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</undeprecated-methods>").eol();
        }

        if (!newFields.isEmpty()) {
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

        if (!newConstructors.isEmpty()) {
            indent().append("<new-constructors>").eol();
            raiseIndent();

            for (FeatureDifferences fd : newConstructors) {
                indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getNewDeclaration()).append("</declaration>").eol();
            }

            lowerIndent();
            indent().append("</new-constructors>").eol();
        }

        if (!newMethods.isEmpty()) {
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
        StringBuilder result = new StringBuilder();

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
                Iterator<? extends Class_info> i = element.getAllInterfaces().iterator();
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
                Iterator<? extends Class_info> i = element.getAllInterfaces().iterator();
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
        StringBuilder result = new StringBuilder();

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
        StringBuilder result = new StringBuilder();

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
            Iterator<? extends Class_info> i = element.getExceptions().iterator();
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

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        return compareTo((ClassReport) object) == 0;
    }

    public int hashCode() {
        return differences.hashCode();
    }

    public int compareTo(ClassReport other) {
        if (this == other) {
            return 0;
        }

        if (other == null) {
            throw new ClassCastException("compareTo: expected a " + getClass().getName() + " but got null");
        }

        return differences.compareTo(other.differences);
    }
}
