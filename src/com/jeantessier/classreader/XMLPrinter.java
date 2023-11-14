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

import com.jeantessier.text.Hex;

import java.io.*;
import java.util.*;

public class XMLPrinter extends Printer {
    public static final String DEFAULT_ENCODING   = "utf-8";
    public static final String DEFAULT_DTD_PREFIX = "https://depfind.sourceforge.io/dtd";

    private static final BitFormat format = new BitFormat(16);

    private boolean top = true;

    public XMLPrinter(PrintWriter out) {
        this(out, DEFAULT_ENCODING, DEFAULT_DTD_PREFIX);
    }
    
    public XMLPrinter(PrintWriter out, String encoding, String dtdPrefix) {
        super(out);
        
        appendHeader(encoding, dtdPrefix);
    }

    private Printer appendHeader(String encoding, String dtdPrefix) {
        append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\" ?>").eol();
        eol();
        append("<!DOCTYPE classfiles SYSTEM \"").append(dtdPrefix).append("/classfile.dtd\">").eol();
        eol();

        return this;
    }

    public void visitClassfiles(Collection<Classfile> classfiles) {
        indent().append("<classfiles>").eol();
        raiseIndent();

        super.visitClassfiles(classfiles);

        lowerIndent();
        indent().append("</classfiles>").eol();
    }
    
    public void visitClassfile(Classfile classfile) {
        indent().append("<!-- ").append(classfile.getClassName()).append(" -->").eol();
        indent().append("<classfile magic-number=\"0x").append(Integer.toHexString(classfile.getMagicNumber()).toUpperCase()).append("\" minor-version=\"").append(classfile.getMinorVersion()).append("\" major-version=\"").append(classfile.getMajorVersion()).append("\" access-flags=\"").append(format.format(classfile.getAccessFlags())).append("\">").eol();
        raiseIndent();

        top = true;
        classfile.getConstantPool().accept(this);
        top = false;
        
        if (classfile.isPublic())     indent().append("<public/>").eol();
        if (classfile.isFinal())      indent().append("<final/>").eol();
        if (classfile.isSuper())      indent().append("<super/>").eol();
        if (classfile.isInterface())  indent().append("<is-interface/>").eol();
        if (classfile.isAbstract())   indent().append("<abstract/>").eol();
        if (classfile.isSynthetic())  indent().append("<synthetic/>").eol();
        if (classfile.isAnnotation()) indent().append("<is-annotation/>").eol();
        if (classfile.isEnum())       indent().append("<enum/>").eol();
        if (classfile.isModule())     indent().append("<is-module/>").eol();

        indent();
        append("<this-class>");
        classfile.getRawClass().accept(this);
        append("</this-class>").eol();

        indent();
        append("<superclass>");
        if (classfile.getSuperclassIndex() != 0) {
            classfile.getRawSuperclass().accept(this);
        }
        append("</superclass>").eol();

        if (!classfile.getAllInterfaces().isEmpty()) {
            indent().append("<interfaces>").eol();
            raiseIndent();
            for (Class_info class_info : classfile.getAllInterfaces()) {
                indent();
                append("<interface>");
                class_info.accept(this);
                append("</interface>").eol();
            }
            lowerIndent();
            indent().append("</interfaces>").eol();
        }
        
        if (!classfile.getAllFields().isEmpty()) {
            indent().append("<fields>").eol();
            raiseIndent();

            visitClassfileFields(classfile);

            lowerIndent();
            indent().append("</fields>").eol();
        }

        if (!classfile.getAllMethods().isEmpty()) {
            indent().append("<methods>").eol();
            raiseIndent();

            visitClassfileMethods(classfile);

            lowerIndent();
            indent().append("</methods>").eol();
        }

        if (!classfile.getAttributes().isEmpty()) {
            indent().append("<attributes>").eol();
            raiseIndent();

            visitClassfileAttributes(classfile);

            lowerIndent();
            indent().append("</attributes>").eol();
        }

        lowerIndent();
        indent().append("</classfile>").eol();
    }

    public void visitConstantPool(ConstantPool constantPool) {
        indent().append("<constant-pool>").eol();
        raiseIndent();

        super.visitConstantPool(constantPool);

        lowerIndent();
        indent().append("</constant-pool>").eol();
    }

    public void visitClass_info(Class_info entry) {
        if (top) {
            top = false;
            appendClassInfo(currentIndex(), entry);
            top = true;
        } else {
            // entry.getRawName().accept(this);
            append(entry.getName());
        }
    }

    public void visitFieldRef_info(FieldRef_info entry) {
        Class_info       c   = entry.getRawClass();
        NameAndType_info nat = entry.getRawNameAndType();

        if (top) {
            top = false;
            indent();
            append("<field-ref-info index=\"").append(currentIndex()).append("\">");
            append("<class>");
            c.accept(this);
            append("</class>");
            append("<type>");
            nat.getRawType().accept(this);
            append("</type>");
            append("<name>");
            nat.getRawName().accept(this);
            append("</name>");
            append("</field-ref-info>").eol();
            top = true;
        } else {
            append(DescriptorHelper.getType(nat.getType()));
            append(" ");
            append(entry.getFullSignature());
        }
    }

    public void visitMethodRef_info(MethodRef_info entry) {
        Class_info       c   = entry.getRawClass();
        NameAndType_info nat = entry.getRawNameAndType();

        if (top) {
            top = false;
            indent();
            append("<method-ref-info index=\"").append(currentIndex()).append("\">");
            append("<class>");
            c.accept(this);
            append("</class>");
            append("<name>");
            nat.getRawName().accept(this);
            append("</name>");
            append("<type>");
            nat.getRawType().accept(this);
            append("</type>");
            append("</method-ref-info>").eol();
            top = true;
        } else {
            if (!entry.isConstructor() && !entry.isStaticInitializer()) {
                append(DescriptorHelper.getReturnType(nat.getType())).append(" ");
            }
            append(entry.getFullSignature());
        }
    }

    public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
        Class_info       c   = entry.getRawClass();
        NameAndType_info nat = entry.getRawNameAndType();

        if (top) {
            top = false;
            indent();
            append("<interface-method-ref-info index=\"").append(currentIndex()).append("\">");
            append("<class>");
            c.accept(this);
            append("</class>");
            append("<name>");
            nat.getRawName().accept(this);
            append("</name>");
            append("<type>");
            nat.getRawType().accept(this);
            append("</type>");
            append("</interface-method-ref-info>").eol();
            top = true;
        } else {
            append(DescriptorHelper.getReturnType(nat.getType()));
            append(" ");
            append(entry.getFullSignature());
        }
    }

    public void visitString_info(String_info entry) {
        if (top) {
            top = false;
            indent();
            append("<string-info index=\"").append(currentIndex()).append("\">");
            entry.getRawValue().accept(this);
            append("</string-info>").eol();
            top = true;
        } else {
            entry.getRawValue().accept(this);
        }
    }

    public void visitInteger_info(Integer_info entry) {
        if (top) {
            top = false;
            indent();
            append("<integer-info index=\"").append(currentIndex()).append("\">");
            append(entry.getValue());
            append("</integer-info>").eol();
            top = true;
        } else {
            append(entry.getValue());
        }
    }

    public void visitFloat_info(Float_info entry) {
        if (top) {
            top = false;
            indent();
            append("<float-info index=\"").append(currentIndex()).append("\">");
            append(entry.getValue());
            append("</float-info>").eol();
            top = true;
        } else {
            append(entry.getValue());
        }
    }

    public void visitLong_info(Long_info entry) {
        if (top) {
            top = false;
            indent();
            append("<long-info index=\"").append(currentIndex()).append("\">");
            append(entry.getValue());
            append("</long-info>").eol();
            top = true;
        } else {
            append(entry.getValue());
        }
    }

    public void visitDouble_info(Double_info entry) {
        if (top) {
            top = false;
            indent();
            append("<double-info index=\"").append(currentIndex()).append("\">");
            append(entry.getValue());
            append("</double-info>").eol();
            top = true;
        } else {
            append(entry.getValue());
        }
    }

    public void visitNameAndType_info(NameAndType_info entry) {
        if (top) {
            top = false;
            indent();
            append("<name-and-type-info index=\"").append(currentIndex()).append("\">");
            append("<name>");
            entry.getRawName().accept(this);
            append("</name>");
            append("<type>");
            entry.getRawType().accept(this);
            append("</type>");
            append("</name-and-type-info>").eol();
            top = true;
        } else {
            entry.getRawName().accept(this);
            append(" ");
            entry.getRawType().accept(this);
        }
    }

    public void visitUTF8_info(UTF8_info entry) {
        if (top) {
            top = false;
            indent().append("<utf8-info index=\"").append(currentIndex()).append("\">");
            append(escapeXMLCharacters(entry.getValue()));
            append("</utf8-info>").eol();
            top = true;
        } else {
            append(escapeXMLCharacters(entry.getValue()));
        }
    }

    public void visitMethodHandle_info(MethodHandle_info entry) {
        if (top) {
            top = false;
            indent();
            append("<method-handle-info index=\"").append(currentIndex()).append("\">");
            append("<reference-kind kind=\"").append(entry.getRawReferenceKind()).append("\">");
            append(entry.getReferenceKind().getDescription());
            append("</reference-kind>");
            append("<reference index=\"").append(entry.getReferenceIndex()).append("\">");
            entry.getReference().accept(this);
            append("</reference>");
            append("</method-handle-info>").eol();
            top = true;
        } else {
            append(entry.getReferenceKind().getDescription());
            append(" ");
            entry.getReference().accept(this);
        }
    }

    public void visitMethodType_info(MethodType_info entry) {
        if (top) {
            top = false;
            indent();
            append("<method-type-info index=\"").append(currentIndex()).append("\">");
            entry.getRawDescriptor().accept(this);
            append("</method-type-info>").eol();
            top = true;
        } else {
            entry.getRawDescriptor().accept(this);
        }
    }

    public void visitDynamic_info(Dynamic_info entry) {
        NameAndType_info nat = entry.getRawNameAndType();

        if (top) {
            top = false;
            indent();
            append("<dynamic-info index=\"").append(currentIndex()).append("\">");
            append("<bootstrap-method-attr index=\"").append(entry.getBootstrapMethodAttrIndex()).append("\"/>");
            append("<name>");
            nat.getRawName().accept(this);
            append("</name>");
            append("<type>");
            nat.getRawType().accept(this);
            append("</type>");
            append("</dynamic-info>").eol();
            top = true;
        } else {
            if (!entry.isStaticInitializer()) {
                append(DescriptorHelper.getReturnType(nat.getType())).append(" ");
            }
            append(entry.getSignature());
        }
    }

    public void visitInvokeDynamic_info(InvokeDynamic_info entry) {
        NameAndType_info nat = entry.getRawNameAndType();

        if (top) {
            top = false;
            indent();
            append("<invoke-dynamic-info index=\"").append(currentIndex()).append("\">");
            append("<bootstrap-method-attr index=\"").append(entry.getBootstrapMethodAttrIndex()).append("\"/>");
            append("<name>");
            nat.getRawName().accept(this);
            append("</name>");
            append("<type>");
            nat.getRawType().accept(this);
            append("</type>");
            append("</invoke-dynamic-info>").eol();
            top = true;
        } else {
            if (!entry.isStaticInitializer()) {
                append(DescriptorHelper.getReturnType(nat.getType())).append(" ");
            }
            append(entry.getSignature());
        }
    }

    public void visitModule_info(Module_info entry) {
        if (top) {
            top = false;
            indent();
            append("<module index=\"").append(currentIndex()).append("\">");
            // entry.getRawName().accept(this);
            append(entry.getName());
            append("</module>").eol();
            top = true;
        } else {
            // entry.getRawName().accept(this);
            append(entry.getName());
        }
    }

    public void visitPackage_info(Package_info entry) {
        if (top) {
            top = false;
            indent();
            append("<package index=\"").append(currentIndex()).append("\">");
            // entry.getRawName().accept(this);
            append(entry.getName());
            append("</package>").eol();
            top = true;
        } else {
            // entry.getRawName().accept(this);
            append(entry.getName());
        }
    }

    public void visitUnusableEntry(UnusableEntry entry) {
        if (top) {
            top = false;
            indent().append("<unusable index=\"").append(currentIndex()).append("\">").append(entry.getReason()).append("</unusable>").eol();
            top = true;
        } else {
            append(entry);
        }
    }

    public void visitField_info(Field_info entry) {
        indent().append("<field-info access-flags=\"").append(format.format(entry.getAccessFlags())).append("\">").eol();
        raiseIndent();

        if (entry.isPublic())    indent().append("<public/>").eol();
        if (entry.isProtected()) indent().append("<protected/>").eol();
        if (entry.isPrivate())   indent().append("<private/>").eol();
        if (entry.isStatic())    indent().append("<static/>").eol();
        if (entry.isFinal())     indent().append("<final/>").eol();
        if (entry.isVolatile())  indent().append("<volatile/>").eol();
        if (entry.isTransient()) indent().append("<transient/>").eol();
        if (entry.isSynthetic()) indent().append("<synthetic/>").eol();
        if (entry.isEnum())      indent().append("<enum/>").eol();

        indent();
        append("<name>");
        entry.getRawName().accept(this);
        append("</name>").eol();
        
        indent().append("<type>").append(entry.getType()).append("</type>").eol();

        if (!entry.getAttributes().isEmpty()) {
            indent().append("<attributes>").eol();
            raiseIndent();
            super.visitField_info(entry);
            lowerIndent();
            indent().append("</attributes>").eol();
        }

        lowerIndent();
        indent().append("</field-info>").eol();
    }

    public void visitMethod_info(Method_info entry) {
        indent().append("<method-info access-flags=\"").append(format.format(entry.getAccessFlags())).append("\">").eol();
        raiseIndent();

        if (entry.isPublic())       indent().append("<public/>").eol();
        if (entry.isProtected())    indent().append("<protected/>").eol();
        if (entry.isPrivate())      indent().append("<private/>").eol();
        if (entry.isStatic())       indent().append("<static/>").eol();
        if (entry.isFinal())        indent().append("<final/>").eol();
        if (entry.isSynchronized()) indent().append("<synchronized/>").eol();
        if (entry.isBridge())       indent().append("<bridge/>").eol();
        if (entry.isVarargs())      indent().append("<varargs/>").eol();
        if (entry.isNative())       indent().append("<native/>").eol();
        if (entry.isAbstract())     indent().append("<abstract/>").eol();
        if (entry.isStrict())       indent().append("<strict/>").eol();
        if (entry.isSynthetic())    indent().append("<synthetic/>").eol();

        indent();
        append("<name>");
        entry.getRawName().accept(this);
        append("</name>").eol();
        
        if (!entry.getName().equals("<init>") && !entry.getName().equals("<clinit>")) {
            indent().append("<return-type>").append((entry.getReturnType() != null) ? entry.getReturnType() : "void").append("</return-type>").eol();
        }
        indent().append("<signature>").append(entry.getSignature()).append("</signature>").eol();

        if (!entry.getAttributes().isEmpty()) {
            indent().append("<attributes>").eol();
            raiseIndent();
            super.visitMethod_info(entry);
            lowerIndent();
            indent().append("</attributes>").eol();
        }

        lowerIndent();
        indent().append("</method-info>").eol();
    }

    public void visitConstantValue_attribute(ConstantValue_attribute attribute) {
        indent().append("<constant-value-attribute>");

        attribute.getRawValue().accept(this);

        append("</constant-value-attribute>").eol();
    }

    public void visitCode_attribute(Code_attribute attribute) {
        indent().append("<code-attribute>").eol();
        raiseIndent();

        indent().append("<length>").append(attribute.getCode().length).append("</length>").eol();

        indent().append("<instructions>").eol();
        raiseIndent();
        for (Instruction instruction : attribute) {
            instruction.accept(this);
        }
        lowerIndent();
        indent().append("</instructions>").eol();

        if (!attribute.getExceptionHandlers().isEmpty()) {
            indent().append("<exception-handlers>").eol();
            raiseIndent();
            for (ExceptionHandler exceptionHandler : attribute.getExceptionHandlers()) {
                exceptionHandler.accept(this);
            }
            lowerIndent();
            indent().append("</exception-handlers>").eol();
        }
        
        if (!attribute.getAttributes().isEmpty()) {
            indent().append("<attributes>").eol();
            raiseIndent();
            for (Attribute_info attribute_info : attribute.getAttributes()) {
                attribute_info.accept(this);
            }
            lowerIndent();
            indent().append("</attributes>").eol();
        }
        
        lowerIndent();
        indent().append("</code-attribute>").eol();
    }

    public void visitStackMapTable_attribute(StackMapTable_attribute attribute) {
        indent().append("<stack-map-table-attribute>").eol();
        raiseIndent();

        super.visitStackMapTable_attribute(attribute);

        lowerIndent();
        indent().append("</stack-map-table-attribute>").eol();
    }

    public void visitExceptions_attribute(Exceptions_attribute attribute) {
        indent().append("<exceptions-attribute>").eol();
        raiseIndent();

        for (Class_info exception : attribute.getExceptions()) {
            indent();
            append("<exception>");
            exception.accept(this);
            append("</exception>").eol();
        }

        lowerIndent();
        indent().append("</exceptions-attribute>").eol();
    }

    public void visitInnerClasses_attribute(InnerClasses_attribute attribute) {
        indent().append("<inner-classes-attribute>").eol();
        raiseIndent();

        super.visitInnerClasses_attribute(attribute);

        lowerIndent();
        indent().append("</inner-classes-attribute>").eol();
    }

    public void visitEnclosingMethod_attribute(EnclosingMethod_attribute attribute) {
        indent().append("<enclosing-method-attribute>").eol();
        raiseIndent();

        appendClassInfo(attribute.getClassIndex(), attribute.getRawClassInfo());

        indent().append("<method>");
        if (attribute.getMethodIndex() != 0) {
            NameAndType_info nat = attribute.getRawMethod();
            if (nat.getName().equals("<init>")) {
                String className = attribute.getClassInfo();
                className = className.substring(className.lastIndexOf(".") + 1);
                append(className).append(DescriptorHelper.getSignature(nat.getType()));
            } else {
                append(DescriptorHelper.getReturnType(nat.getType())).append(" ").append(nat.getName()).append(DescriptorHelper.getSignature(nat.getType()));
            }
        }
        append("</method>").eol();

        lowerIndent();
        indent().append("</enclosing-method-attribute>").eol();
    }

    public void visitSynthetic_attribute(Synthetic_attribute attribute) {
        indent().append("<synthetic-attribute/>").eol();
    }

    public void visitSignature_attribute(Signature_attribute attribute) {
        indent().append("<signature-attribute>");
        attribute.getRawSignature().accept(this);
        append("</signature-attribute>").eol();
    }

    public void visitSourceFile_attribute(SourceFile_attribute attribute) {
        indent().append("<source-file-attribute>").append(attribute.getSourceFile()).append("</source-file-attribute>").eol();
    }

    public void visitSourceDebugExtension_attribute(SourceDebugExtension_attribute attribute) {
        indent().append("<source-debug-extension>").append(attribute.getDebugExtension()).append("</source-debug-extension>").eol();
    }

    public void visitLineNumberTable_attribute(LineNumberTable_attribute attribute) {
        indent().append("<line-number-table-attribute>").eol();
        raiseIndent();

        super.visitLineNumberTable_attribute(attribute);

        lowerIndent();
        indent().append("</line-number-table-attribute>").eol();
    }

    public void visitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
        indent().append("<local-variable-table-attribute>").eol();
        raiseIndent();

        super.visitLocalVariableTable_attribute(attribute);

        lowerIndent();
        indent().append("</local-variable-table-attribute>").eol();
    }

    public void visitLocalVariableTypeTable_attribute(LocalVariableTypeTable_attribute attribute) {
        indent().append("<local-variable-type-table-attribute>").eol();
        raiseIndent();

        super.visitLocalVariableTypeTable_attribute(attribute);

        lowerIndent();
        indent().append("</local-variable-type-table-attribute>").eol();
    }

    public void visitDeprecated_attribute(Deprecated_attribute attribute) {
        indent().append("<deprecated-attribute/>").eol();
    }

    public void visitRuntimeVisibleAnnotations_attribute(RuntimeVisibleAnnotations_attribute attribute) {
        indent().append("<runtime-visible-annotations-attribute>").eol();
        raiseIndent();

        super.visitRuntimeVisibleAnnotations_attribute(attribute);

        lowerIndent();
        indent().append("</runtime-visible-annotations-attribute>").eol();
    }

    public void visitRuntimeInvisibleAnnotations_attribute(RuntimeInvisibleAnnotations_attribute attribute) {
        indent().append("<runtime-invisible-annotations-attribute>").eol();
        raiseIndent();

        super.visitRuntimeInvisibleAnnotations_attribute(attribute);

        lowerIndent();
        indent().append("</runtime-invisible-annotations-attribute>").eol();
    }

    protected void visitRuntimeAnnotations_attribute(RuntimeAnnotations_attribute attribute) {
        indent().append("<annotations>").eol();
        raiseIndent();

        super.visitRuntimeAnnotations_attribute(attribute);

        lowerIndent();
        indent().append("</annotations>").eol();
    }

    public void visitRuntimeVisibleParameterAnnotations_attribute(RuntimeVisibleParameterAnnotations_attribute attribute) {
        indent().append("<runtime-visible-parameter-annotations-attribute>").eol();
        raiseIndent();

        super.visitRuntimeVisibleParameterAnnotations_attribute(attribute);

        lowerIndent();
        indent().append("</runtime-visible-parameter-annotations-attribute>").eol();
    }

    public void visitRuntimeInvisibleParameterAnnotations_attribute(RuntimeInvisibleParameterAnnotations_attribute attribute) {
        indent().append("<runtime-invisible-parameter-annotations-attribute>").eol();
        raiseIndent();

        super.visitRuntimeInvisibleParameterAnnotations_attribute(attribute);

        lowerIndent();
        indent().append("</runtime-invisible-parameter-annotations-attribute>").eol();
    }

    protected void visitRuntimeParameterAnnotations_attribute(RuntimeParameterAnnotations_attribute attribute) {
        indent().append("<parameter-annotations>").eol();
        raiseIndent();

        super.visitRuntimeParameterAnnotations_attribute(attribute);

        lowerIndent();
        indent().append("</parameter-annotations>").eol();
    }

    public void visitRuntimeVisibleTypeAnnotations_attribute(RuntimeVisibleTypeAnnotations_attribute attribute) {
        indent().append("<runtime-visible-type-annotations-attribute>").eol();
        raiseIndent();

        super.visitRuntimeVisibleTypeAnnotations_attribute(attribute);

        lowerIndent();
        indent().append("</runtime-visible-type-annotations-attribute>").eol();
    }

    public void visitRuntimeInvisibleTypeAnnotations_attribute(RuntimeInvisibleTypeAnnotations_attribute attribute) {
        indent().append("<runtime-invisible-type-annotations-attribute>").eol();
        raiseIndent();

        super.visitRuntimeInvisibleTypeAnnotations_attribute(attribute);

        lowerIndent();
        indent().append("</runtime-invisible-type-annotations-attribute>").eol();
    }

    protected void visitRuntimeTypeAnnotations_attribute(RuntimeTypeAnnotations_attribute attribute) {
        indent().append("<type-annotations>").eol();
        raiseIndent();

        super.visitRuntimeTypeAnnotations_attribute(attribute);

        lowerIndent();
        indent().append("</type-annotations>").eol();
    }

    public void visitAnnotationDefault_attribute(AnnotationDefault_attribute attribute) {
        indent().append("<annotation-default-attribute>").eol();
        raiseIndent();

        super.visitAnnotationDefault_attribute(attribute);

        lowerIndent();
        indent().append("</annotation-default-attribute>").eol();
    }

    public void visitBootstrapMethods_attribute(BootstrapMethods_attribute attribute) {
        indent().append("<bootstrap-methods-attribute>").eol();
        raiseIndent();

        super.visitBootstrapMethods_attribute(attribute);

        lowerIndent();
        indent().append("</bootstrap-methods-attribute>").eol();
    }

    public void visitMethodParameters_attribute(MethodParameters_attribute attribute) {
        indent().append("<method-parameters-attribute>").eol();
        raiseIndent();

        super.visitMethodParameters_attribute(attribute);

        lowerIndent();
        indent().append("</method-parameters-attribute>").eol();
    }

    public void visitModule_attribute(Module_attribute attribute) {
        indent().append("<module-attribute module-flags=\"").append(format.format(attribute.getModuleFlags())).append("\">").eol();
        raiseIndent();

        indent();
        append("<name index=\"").append(attribute.getModuleNameIndex()).append("\">");
        attribute.getRawModuleName().accept(this);
        append("</name>").eol();

        if (attribute.isOpen())      indent().append("<open/>").eol();
        if (attribute.isSynthetic()) indent().append("<synthetic/>").eol();
        if (attribute.isMandated())  indent().append("<mandated/>").eol();

        if (attribute.hasModuleVersion()) {
            indent();
            append("<version index=\"").append(attribute.getModuleVersionIndex()).append("\">");
            attribute.getRawModuleVersion().accept(this);
            append("</version>").eol();
        }

        super.visitModule_attribute(attribute);

        lowerIndent();
        indent().append("</module-attribute>").eol();
    }

    public void visitModulePackages_attribute(ModulePackages_attribute attribute) {
        indent().append("<module-packages-attribute>").eol();
        raiseIndent();

        super.visitModulePackages_attribute(attribute);

        lowerIndent();
        indent().append("</module-packages-attribute>").eol();
    }

    public void visitModuleMainClass_attribute(ModuleMainClass_attribute attribute) {
        indent();
        append("<module-main-class-attribute index=\"").append(attribute.getMainClassIndex()).append("\">");
        attribute.getRawMainClass().accept(this);
        append("</module-main-class-attribute>").eol();
    }

    public void visitNestHost_attribute(NestHost_attribute attribute) {
        indent();
        append("<nest-host-attribute index=\"").append(attribute.getHostClassIndex()).append("\">");
        attribute.getRawHostClass().accept(this);
        append("</nest-host-attribute>").eol();
    }

    public void visitNestMembers_attribute(NestMembers_attribute attribute) {
        indent().append("<nest-members-attribute>").eol();
        raiseIndent();

        super.visitNestMembers_attribute(attribute);

        lowerIndent();
        indent().append("</nest-members-attribute>").eol();
    }

    public void visitRecord_attribute(Record_attribute attribute) {
        indent().append("<record-attribute>").eol();
        raiseIndent();

        super.visitRecord_attribute(attribute);

        lowerIndent();
        indent().append("</record-attribute>").eol();
    }

    public void visitPermittedSubclasses_attribute(PermittedSubclasses_attribute attribute) {
        indent().append("<permitted-subclasses-attribute>").eol();
        raiseIndent();

        super.visitPermittedSubclasses_attribute(attribute);

        lowerIndent();
        indent().append("</permitted-subclasses-attribute>").eol();
    }

    public void visitCustom_attribute(Custom_attribute attribute) {
        indent().append("<custom-attribute name=\"").append(escapeXMLCharacters(attribute.getName())).append("\">").append(Hex.toString(attribute.getInfo())).append("</custom-attribute>").eol();
    }

    public void visitInstruction(Instruction instruction) {
        indent();
        append("<instruction pc=\"").append(instruction.getStart()).append("\" length=\"").append(instruction.getLength()).append("\" op-code=\"0x").append(String.format("%02X", instruction.getOpcode())).append("\"");
        switch (instruction.getOpcode()) {
            case 0x02: // iconst_m1
            case 0x03: // iconst_0
            case 0x04: // iconst_1
            case 0x05: // iconst_2
            case 0x06: // iconst_3
            case 0x07: // iconst_4
            case 0x08: // iconst_5
            case 0x09: // lconst_0
            case 0x0a: // lconst_1
            case 0x0b: // fconst_0
            case 0x0c: // fconst_1
            case 0x0d: // fconst_2
            case 0x0e: // dconst_0
            case 0x0f: // dconst_1
                append(" value=\"").append(instruction.getValue()).append("\">");
                append(instruction);
                break;
            case 0x10: // bipush
            case 0x11: // sipush
                append(" value=\"").append(instruction.getValue()).append("\">");
                append(instruction).append(" ").append(instruction.getValue());
                break;
            case 0x12: // ldc
            case 0x13: // ldc_w
            case 0x14: // ldc2_w
            case 0xb2: // getstatic
            case 0xb3: // putstatic
            case 0xb4: // getfield
            case 0xb5: // putfield
            case 0xb6: // invokevirtual
            case 0xb7: // invokespecial
            case 0xb8: // invokestatic
            case 0xb9: // invokeinterface
            case 0xbb: // new
            case 0xbd: // anewarray
            case 0xc0: // checkcast
            case 0xc1: // instanceof
            case 0xc5: // multianewarray
                append(" index=\"").append(instruction.getIndex()).append("\">");
                append(instruction);
                append(" ");
                instruction.getIndexedConstantPoolEntry().accept(this);
                break;
            case 0x1a: // iload_0
            case 0x1e: // lload_0
            case 0x22: // fload_0
            case 0x26: // dload_0
            case 0x2a: // aload_0
            case 0x3b: // istore_0
            case 0x3f: // lstore_0
            case 0x43: // fstore_0
            case 0x47: // dstore_0
            case 0x4b: // astore_0
            case 0x1b: // iload_1
            case 0x1f: // lload_1
            case 0x23: // fload_1
            case 0x27: // dload_1
            case 0x2b: // aload_1
            case 0x3c: // istore_1
            case 0x40: // lstore_1
            case 0x44: // fstore_1
            case 0x48: // dstore_1
            case 0x4c: // astore_1
            case 0x1c: // iload_2
            case 0x20: // lload_2
            case 0x24: // fload_2
            case 0x28: // dload_2
            case 0x2c: // aload_2
            case 0x3d: // istore_2
            case 0x41: // lstore_2
            case 0x45: // fstore_2
            case 0x49: // dstore_2
            case 0x4d: // astore_2
            case 0x1d: // iload_3
            case 0x21: // lload_3
            case 0x25: // fload_3
            case 0x29: // dload_3
            case 0x2d: // aload_3
            case 0x3e: // istore_3
            case 0x42: // lstore_3
            case 0x46: // fstore_3
            case 0x4a: // dstore_3
            case 0x4e: // astore_3
            case 0x15: // iload
            case 0x16: // llload
            case 0x17: // fload
            case 0x18: // dload
            case 0x19: // aload
            case 0x36: // istore
            case 0x37: // lstore
            case 0x38: // fstore
            case 0x39: // dstore
            case 0x3a: // astore
            case 0xa9: // ret
                append(" index=\"").append(instruction.getIndex()).append("\">");
                append(instruction);
                appendLocalVariable(instruction.getIndexedLocalVariable());
                break;
            case 0x99: // ifeq
            case 0x9a: // ifne
            case 0x9b: // iflt
            case 0x9c: // ifge
            case 0x9d: // ifgt
            case 0x9e: // ifle
            case 0x9f: // if_icmpeq
            case 0xa0: // if_icmpne
            case 0xa1: // if_icmplt
            case 0xa2: // if_icmpge
            case 0xa3: // if_icmpgt
            case 0xa4: // if_icmple
            case 0xa5: // if_acmpeq
            case 0xa6: // if_acmpne
            case 0xa7: // goto
            case 0xa8: // jsr
            case 0xc6: // ifnull
            case 0xc7: // ifnonnull
            case 0xc8: // goto_w
            case 0xc9: // jsr_w
                append(" offset=\"").append(instruction.getOffset()).append("\">");
                append(instruction).append(" ").append(instruction.getStart() + instruction.getOffset());
                break;
            case 0x84: // iinc
                append(" index=\"").append(instruction.getIndex()).append("\" value=\"").append(instruction.getValue()).append("\">");
                append(instruction);
                appendLocalVariable(instruction.getIndexedLocalVariable());
                break;
            case 0xaa: // tableswitch
                append(" padding=\"").append(instruction.getPadding()).append("\" default=\"").appendSwitchDefault(instruction).append("\" low=\"").append(instruction.getLow()).append("\" high=\"").append(instruction.getHigh()).append("\">");
                append(instruction).append(" ").appendTableSwitch(instruction, " | ");
                break;
            case 0xab: // lookupswitch
                append(" padding=\"").append(instruction.getPadding()).append("\" default=\"").appendSwitchDefault(instruction).append("\" npairs=\"").append(instruction.getNPairs()).append("\">");
                append(instruction).append(" ").appendLookupSwitch(instruction, " | ");
                break;
            case 0xba: // invokedynamic
                append(" index=\"").append(instruction.getIndex()).append("\">");
                append(instruction);
                var indexedEntry = instruction.getIndexedConstantPoolEntry();
                if (indexedEntry instanceof Dynamic_info dynamic_info) {
                    append(" ").append(dynamic_info.getName());
                } else if (indexedEntry instanceof InvokeDynamic_info invokeDynamic_info) {
                    append(" ").append(invokeDynamic_info.getName());
                }
                // TODO: Replace with type pattern matching in switch expression in Java 21
                // switch (instruction.getIndexedConstantPoolEntry()) {
                //     case Dynamic_info entry -> append(" ").append(entry.getName());
                //     case InvokeDynamic_info entry -> append(" ").append(entry.getName());
                //     default -> append("");
                // }
                instruction.getDynamicConstantPoolEntries().forEach(entry -> {
                    append(" ");
                    entry.accept(this);
                });
                break;
            case 0xc4: // wide
                if (instruction.getByte(1) == 0x84 /* iinc */) {
                    append(" index=\"").append(instruction.getIndex()).append("\" value=\"").append(instruction.getValue()).append("\">");
                } else {
                    append(" index=\"").append(instruction.getIndex()).append("\">");
                }
                append(instruction);
                appendLocalVariable(instruction.getIndexedLocalVariable());
                break;
            default:
                append(">");
                append(instruction);
                break;
        }
        append("</instruction>").eol();
    }

    public void visitExceptionHandler(ExceptionHandler helper) {
        indent();
        append("<exception-handler>");
        append("<start-pc>").append(helper.getStartPC()).append("</start-pc>");
        append("<end-pc>").append(helper.getEndPC()).append("</end-pc>");
        append("<handler-pc>").append(helper.getHandlerPC()).append("</handler-pc>");

        append("<catch-type>");
        if (helper.getCatchTypeIndex() != 0) {
            helper.getRawCatchType().accept(this);
        }
        append("</catch-type>");

        append("</exception-handler>").eol();
    }

    public void visitInnerClass(InnerClass helper) {
        indent().append("<inner-class access-flags=\"").append(format.format(helper.getAccessFlags())).append("\">").eol();
        raiseIndent();

        if (helper.isPublic())     indent().append("<public/>").eol();
        if (helper.isProtected())  indent().append("<protected/>").eol();
        if (helper.isPrivate())    indent().append("<private/>").eol();
        if (helper.isStatic())     indent().append("<static/>").eol();
        if (helper.isFinal())      indent().append("<final/>").eol();
        if (helper.isInterface())  indent().append("<is-interface/>").eol();
        if (helper.isAbstract())   indent().append("<abstract/>").eol();
        if (helper.isSynthetic())  indent().append("<synthetic/>").eol();
        if (helper.isAnnotation()) indent().append("<is-annotation/>").eol();
        if (helper.isEnum())       indent().append("<enum/>").eol();

        indent();
        append("<inner-class-info>");
        if (helper.getInnerClassInfoIndex() != 0) {
            helper.getRawInnerClassInfo().accept(this);
        }
        append("</inner-class-info>").eol();

        indent();
        append("<outer-class-info>");
        if (helper.getOuterClassInfoIndex() != 0) {
            helper.getRawOuterClassInfo().accept(this);
        }
        append("</outer-class-info>").eol();

        indent();
        append("<inner-name>");
        if (helper.getInnerNameIndex() != 0) {
            helper.getRawInnerName().accept(this);
        }
        append("</inner-name>").eol();

        lowerIndent();
        indent().append("</inner-class>").eol();
    }

    public void visitLineNumber(LineNumber helper) {
        indent();
        append("<line-number>");
        append("<start-pc>").append(helper.getStartPC()).append("</start-pc>");
        append("<line>").append(helper.getLineNumber()).append("</line>");
        append("</line-number>").eol();
    }

    public void visitLocalVariable(LocalVariable helper) {
        indent();
        append("<local-variable pc=\"").append(helper.getStartPC()).append("\" length=\"").append(helper.getLength()).append("\" index=\"").append(helper.getIndex()).append("\">");
        append("<name>");
        helper.getRawName().accept(this);
        append("</name>");

        append("<type>").append(DescriptorHelper.getType(helper.getDescriptor())).append("</type>");
        append("</local-variable>").eol();
    }

    public void visitLocalVariableType(LocalVariableType helper) {
        indent();
        append("<local-variable-type pc=\"").append(helper.getStartPC()).append("\" length=\"").append(helper.getLength()).append("\" index=\"").append(helper.getIndex()).append("\">");
        append("<name>");
        helper.getRawName().accept(this);
        append("</name>");

        append("<signature>");
        helper.getRawSignature().accept(this);
        append("</signature>");
        append("</local-variable-type>").eol();
    }

    public void visitBootstrapMethod(BootstrapMethod helper) {
        indent().append("<bootstrap-method>").eol();
        raiseIndent();

        indent();
        append("<bootstrap-method-ref index=\"").append(helper.getBootstrapMethodRef()).append("\">");
        helper.getBootstrapMethod().accept(this);
        append("</bootstrap-method-ref>").eol();

        indent().append("<arguments>").eol();
        raiseIndent();

        helper.getArgumentIndices()
                .forEach(index -> {
                    indent();
                    append("<argument index=\"").append(index).append("\">");
                    helper.getArgument(index).accept(this);
                    append("</argument>").eol();
                });

        lowerIndent();
        indent().append("</arguments>").eol();

        lowerIndent();
        indent().append("</bootstrap-method>").eol();
    }

    public void visitMethodParameter(MethodParameter helper) {
        indent().append("<method-parameter access-flags=\"").append(format.format(helper.getAccessFlags())).append("\">").eol();
        raiseIndent();

        indent();
        append("<name>");
        helper.getRawName().accept(this);
        append("</name>").eol();

        if (helper.isFinal())     indent().append("<final/>").eol();
        if (helper.isSynthetic()) indent().append("<synthetic/>").eol();
        if (helper.isMandated())  indent().append("<mandated/>").eol();

        lowerIndent();
        indent().append("</method-parameter>").eol();
    }

    public void visitModuleRequires(ModuleRequires helper) {
        indent().append("<module-requires requires-flags=\"").append(format.format(helper.getRequiresFlags())).append("\">").eol();
        raiseIndent();

        indent();
        append("<module index=\"").append(helper.getRequiresIndex()).append("\">");
        helper.getRawRequires().accept(this);
        append("</module>").eol();

        if (helper.isTransitive())  indent().append("<transitive/>").eol();
        if (helper.isStaticPhase()) indent().append("<static-phase/>").eol();
        if (helper.isSynthetic())   indent().append("<synthetic/>").eol();
        if (helper.isMandated())    indent().append("<mandated/>").eol();

        if (helper.hasRequiresVersion()) {
            indent();
            append("<version index=\"").append(helper.getRequiresVersionIndex()).append("\">");
            helper.getRawRequiresVersion().accept(this);
            append("</version>").eol();
        }

        lowerIndent();
        indent().append("</module-requires>").eol();
    }

    public void visitModuleExports(ModuleExports helper) {
        indent().append("<module-exports exports-flags=\"").append(format.format(helper.getExportsFlags())).append("\">").eol();
        raiseIndent();

        indent();
        append("<package index=\"").append(helper.getExportsIndex()).append("\">");
        helper.getRawExports().accept(this);
        append("</package>").eol();

        if (helper.isSynthetic())   indent().append("<synthetic/>").eol();
        if (helper.isMandated())    indent().append("<mandated/>").eol();

        helper.getExportsTos().forEach(moduleExportsTo -> moduleExportsTo.accept(this));

        lowerIndent();
        indent().append("</module-exports>").eol();
    }

    public void visitModuleExportsTo(ModuleExportsTo helper) {
        indent().append("<module-exports-to>").eol();
        raiseIndent();

        indent();
        append("<module index=\"").append(helper.getExportsToIndex()).append("\">");
        helper.getRawExportsTo().accept(this);
        append("</module>").eol();

        lowerIndent();
        indent().append("</module-exports-to>").eol();
    }

    public void visitModuleOpens(ModuleOpens helper) {
        indent().append("<module-opens opens-flags=\"").append(format.format(helper.getOpensFlags())).append("\">").eol();
        raiseIndent();

        indent();
        append("<package index=\"").append(helper.getOpensIndex()).append("\">");
        helper.getRawOpens().accept(this);
        append("</package>").eol();

        if (helper.isSynthetic())   indent().append("<synthetic/>").eol();
        if (helper.isMandated())    indent().append("<mandated/>").eol();

        helper.getOpensTos().forEach(moduleOpensTo -> moduleOpensTo.accept(this));

        lowerIndent();
        indent().append("</module-opens>").eol();
    }

    public void visitModuleOpensTo(ModuleOpensTo helper) {
        indent().append("<module-opens-to>").eol();
        raiseIndent();

        indent();
        append("<module index=\"").append(helper.getOpensToIndex()).append("\">");
        helper.getRawOpensTo().accept(this);
        append("</module>").eol();

        lowerIndent();
        indent().append("</module-opens-to>").eol();
    }

    public void visitModuleUses(ModuleUses helper) {
        indent().append("<module-uses>").eol();
        raiseIndent();

        appendClassInfo(helper.getUsesIndex(), helper.getRawUses());

        lowerIndent();
        indent().append("</module-uses>").eol();
    }

    public void visitModuleProvides(ModuleProvides helper) {
        indent().append("<module-provides>").eol();
        raiseIndent();

        appendClassInfo(helper.getProvidesIndex(), helper.getRawProvides());

        helper.getProvidesWiths().forEach(moduleProvidesWith -> moduleProvidesWith.accept(this));

        lowerIndent();
        indent().append("</module-provides>").eol();
    }

    public void visitModuleProvidesWith(ModuleProvidesWith helper) {
        indent().append("<module-provides-with>").eol();
        raiseIndent();

        appendClassInfo(helper.getProvidesWithIndex(), helper.getRawProvidesWith());

        lowerIndent();
        indent().append("</module-provides-with>").eol();
    }

    public void visitModulePackage(ModulePackage helper) {
        indent();
        append("<package index=\"").append(helper.getPackageIndex()).append("\">");
        helper.getRawPackage().accept(this);
        append("</package>").eol();
    }

    public void visitNestMember(NestMember helper) {
        appendClassInfo(helper.getMemberClassIndex(), helper.getRawMemberClass());
    }

    public void visitRecordComponent_info(RecordComponent_info helper) {
        indent().append("<record-component>").eol();
        raiseIndent();

        indent();
        append("<name index=\"").append(helper.getNameIndex()).append("\">");
        helper.getRawName().accept(this);
        append("</name>").eol();

        indent().append("<type index=\"").append(helper.getDescriptorIndex()).append("\">").append(helper.getType()).append("</type>").eol();

        indent().append("<attributes>").eol();
        raiseIndent();

        super.visitRecordComponent_info(helper);

        lowerIndent();
        indent().append("</attributes>").eol();

        lowerIndent();
        indent().append("</record-component>").eol();
    }


    public void visitPermittedSubclass(PermittedSubclass helper) {
        appendClassInfo(helper.getSubclassIndex(), helper.getRawSubclass());
    }

    public void visitAnnotation(Annotation helper) {
        indent().append("<annotation>").eol();
        raiseIndent();

        indent().append("<type>").append(helper.getType()).append("</type>").eol();

        indent().append("<element-value-pairs>").eol();
        raiseIndent();

        super.visitAnnotation(helper);

        lowerIndent();
        indent().append("</element-value-pairs>").eol();

        lowerIndent();
        indent().append("</annotation>").eol();
    }

    public void visitParameterAnnotation(ParameterAnnotation helper) {
        indent().append("<parameter-annotation>").eol();
        raiseIndent();

        indent().append("<annotations>").eol();
        raiseIndent();

        super.visitParameterAnnotation(helper);

        lowerIndent();
        indent().append("</annotations>").eol();

        lowerIndent();
        indent().append("</parameter-annotation>").eol();
    }

    public void visitTypeAnnotation(TypeAnnotation helper) {
        indent().append("<type-annotation>").eol();
        raiseIndent();

        helper.getTarget().accept(this);

        indent().append("<target-path>").eol();
        raiseIndent();

        helper.getTargetPath().accept(this);

        lowerIndent();
        indent().append("</target-path>").eol();

        indent().append("<element-value-pairs>").eol();
        raiseIndent();

        helper.getElementValuePairs().forEach(elementValuePair -> elementValuePair.accept(this));

        lowerIndent();
        indent().append("</element-value-pairs>").eol();

        lowerIndent();
        indent().append("</type-annotation>").eol();
    }

    public void visitTypeParameterTarget(TypeParameterTarget helper) {
        indent().append("<type-parameter-target target-type=\"").append(helper.getHexTargetType()).append("\">").eol();
        raiseIndent();

        indent().append("<type-parameter-index>").append(helper.getTypeParameterIndex()).append("</type-parameter-index>").eol();

        lowerIndent();
        indent().append("</type-parameter-target>").eol();
    }

    public void visitSupertypeTarget(SupertypeTarget helper) {
        indent().append("<supertype-target target-type=\"").append(helper.getHexTargetType()).append("\">").eol();
        raiseIndent();

        indent().append("<supertype-index>").append(helper.getSupertypeIndex()).append("</supertype-index>").eol();

        lowerIndent();
        indent().append("</supertype-target>").eol();
    }

    public void visitTypeParameterBoundTarget(TypeParameterBoundTarget helper) {
        indent().append("<type-parameter-bound-target target-type=\"").append(helper.getHexTargetType()).append("\">").eol();
        raiseIndent();

        indent().append("<type-parameter-index>").append(helper.getTypeParameterIndex()).append("</type-parameter-index>").eol();
        indent().append("<bound-index>").append(helper.getBoundIndex()).append("</bound-index>").eol();

        lowerIndent();
        indent().append("</type-parameter-bound-target>").eol();
    }

    public void visitEmptyTarget(EmptyTarget helper) {
        indent().append("<empty-target target-type=\"").append(helper.getHexTargetType()).append("\"/>").eol();
    }

    public void visitFormalParameterTarget(FormalParameterTarget helper) {
        indent().append("<formal-parameter-target target-type=\"").append(helper.getHexTargetType()).append("\">").eol();
        raiseIndent();

        indent().append("<formal-parameter-index>").append(helper.getFormalParameterIndex()).append("</formal-parameter-index>").eol();

        lowerIndent();
        indent().append("</formal-parameter-target>").eol();
    }

    public void visitThrowsTarget(ThrowsTarget helper) {
        indent().append("<throws-target target-type=\"").append(helper.getHexTargetType()).append("\">").eol();
        raiseIndent();

        indent().append("<throws-type-index>").append(helper.getThrowsTypeIndex()).append("</throws-type-index>").eol();

        lowerIndent();
        indent().append("</throws-target>").eol();
    }

    public void visitLocalvarTarget(LocalvarTarget helper) {
        indent().append("<localvar-target target-type=\"").append(helper.getHexTargetType()).append("\">").eol();
        raiseIndent();

        super.visitLocalvarTarget(helper);

        lowerIndent();
        indent().append("</localvar-target>").eol();
    }

    public void visitCatchTarget(CatchTarget helper) {
        indent().append("<catch-target target-type=\"").append(helper.getHexTargetType()).append("\">").eol();
        raiseIndent();

        indent().append("<exception-table-index>").append(helper.getExceptionTableIndex()).append("</exception-table-index>").eol();

        lowerIndent();
        indent().append("</catch-target>").eol();
    }

    public void visitOffsetTarget(OffsetTarget helper) {
        indent().append("<offset-target target-type=\"").append(helper.getHexTargetType()).append("\">").eol();
        raiseIndent();

        indent().append("<offset>").append(helper.getOffset()).append("</offset>").eol();

        lowerIndent();
        indent().append("</offset-target>").eol();
    }

    public void visitTypeArgumentTarget(TypeArgumentTarget helper) {
        indent().append("<type-argument-target target-type=\"").append(helper.getHexTargetType()).append("\">").eol();
        raiseIndent();

        indent().append("<offset>").append(helper.getOffset()).append("</offset>").eol();
        indent().append("<type-argument-index>").append(helper.getTypeArgumentIndex()).append("</type-argument-index>").eol();

        lowerIndent();
        indent().append("</type-argument-target>").eol();
    }

    public void visitTypePathEntry(TypePathEntry helper) {
        indent().append("<type-path>").eol();
        raiseIndent();

        indent().append("<type-path-kind>").append(helper.getTypePathKind().getTypePathKind()).append("</type-path-kind>").eol();
        indent().append("<type-argument-index>").append(helper.getTypeArgumentIndex()).append("</type-argument-index>").eol();

        lowerIndent();
        indent().append("</type-path>").eol();
    }

    public void visitElementValuePair(ElementValuePair helper) {
        indent().append("<element-value-pair>").eol();
        raiseIndent();

        indent().append("<element-name>").append(helper.getElementName()).append("</element-name>").eol();

        super.visitElementValuePair(helper);

        lowerIndent();
        indent().append("</element-value-pair>").eol();
    }

    public void visitByteConstantElementValue(ByteConstantElementValue helper) {
        visitConstantElementValue(helper, "byte");
    }

    public void visitCharConstantElementValue(CharConstantElementValue helper) {
        visitConstantElementValue(helper, "char");
    }

    public void visitDoubleConstantElementValue(DoubleConstantElementValue helper) {
        visitConstantElementValue(helper, "double");
    }

    public void visitFloatConstantElementValue(FloatConstantElementValue helper) {
        visitConstantElementValue(helper, "float");
    }

    public void visitIntegerConstantElementValue(IntegerConstantElementValue helper) {
        visitConstantElementValue(helper, "integer");
    }

    public void visitLongConstantElementValue(LongConstantElementValue helper) {
        visitConstantElementValue(helper, "long");
    }

    public void visitShortConstantElementValue(ShortConstantElementValue helper) {
        visitConstantElementValue(helper, "short");
    }

    public void visitBooleanConstantElementValue(BooleanConstantElementValue helper) {
        visitConstantElementValue(helper, "boolean");
    }

    public void visitStringConstantElementValue(StringConstantElementValue helper) {
        visitConstantElementValue(helper, "string");
    }

    private void visitConstantElementValue(ConstantElementValue helper, String type) {
        indent();
        append("<").append(type).append("-element-value tag=\"").append(helper.getTag()).append("\">");
        helper.getRawConstValue().accept(this);
        append("</").append(type).append("-element-value>").eol();
    }

    public void visitEnumElementValue(EnumElementValue helper) {
        indent();
        append("<enum-element-value tag=\"").append(helper.getTag()).append("\">");
        append(helper.getTypeName()).append(".").append(helper.getConstName());
        append("</enum-element-value>").eol();
    }

    public void visitClassElementValue(ClassElementValue helper) {
        indent();
        append("<class-element-value tag=\"").append(helper.getTag()).append("\">");
        append(helper.getClassInfo());
        append("</class-element-value>").eol();
    }

    public void visitAnnotationElementValue(AnnotationElementValue helper) {
        indent().append("<annotation-element-value tag=\"").append(helper.getTag()).append("\">").eol();
        raiseIndent();

        super.visitAnnotationElementValue(helper);

        lowerIndent();
        indent().append("</annotation-element-value>").eol();
    }

    public void visitArrayElementValue(ArrayElementValue helper) {
        indent().append("<array-element-value tag=\"").append(helper.getTag()).append("\">").eol();
        raiseIndent();

        super.visitArrayElementValue(helper);

        lowerIndent();
        indent().append("</array-element-value>").eol();
    }

    public void visitLocalvarTableEntry(LocalvarTableEntry helper) {
        indent().append("<localvar start-pc=\"").append(helper.getStartPc()).append("\" length=\"").append(helper.getLength()).append("\" index=\"").append(helper.getIndex()).append("\"/>").eol();
    }

    public void visitSameFrame(SameFrame helper) {
        indent().append("<same-frame frame-type=\"").append(helper.getFrameType()).append("\"/>").eol();
    }

    public void visitSameLocals1StackItemFrame(SameLocals1StackItemFrame helper) {
        indent().append("<same-locals-1-stack-item-frame frame-type=\"").append(helper.getFrameType()).append("\">").eol();
        raiseIndent();

        indent().append("<stack>").eol();
        raiseIndent();
        super.visitSameLocals1StackItemFrame(helper);
        lowerIndent();
        indent().append("</stack>").eol();

        lowerIndent();
        indent().append("</same-locals-1-stack-item-frame>").eol();
    }

    public void visitSameLocals1StackItemFrameExtended(SameLocals1StackItemFrameExtended helper) {
        indent().append("<same-locals-1-stack-item-frame-extended frame-type=\"").append(helper.getFrameType()).append("\" offset-delta=\"").append(helper.getOffsetDelta()).append("\">").eol();
        raiseIndent();

        indent().append("<stack>").eol();
        raiseIndent();
        super.visitSameLocals1StackItemFrameExtended(helper);
        lowerIndent();
        indent().append("</stack>").eol();

        lowerIndent();
        indent().append("</same-locals-1-stack-item-frame-extended>").eol();
    }

    public void visitChopFrame(ChopFrame helper) {
        indent().append("<chop-frame frame-type=\"").append(helper.getFrameType()).append("\" offset-delta=\"").append(helper.getOffsetDelta()).append("\"/>").eol();
    }

    public void visitSameFrameExtended(SameFrameExtended helper) {
        indent().append("<same-frame-extended frame-type=\"").append(helper.getFrameType()).append("\" offset-delta=\"").append(helper.getOffsetDelta()).append("\"/>").eol();
    }

    public void visitAppendFrame(AppendFrame helper) {
        indent().append("<append-frame frame-type=\"").append(helper.getFrameType()).append("\" offset-delta=\"").append(helper.getOffsetDelta()).append("\">").eol();
        raiseIndent();

        indent().append("<locals>").eol();
        raiseIndent();
        super.visitAppendFrame(helper);
        lowerIndent();
        indent().append("</locals>").eol();

        lowerIndent();
        indent().append("</append-frame>").eol();
    }

    public void visitFullFrame(FullFrame helper) {
        indent().append("<full-frame frame-type=\"").append(helper.getFrameType()).append("\" offset-delta=\"").append(helper.getOffsetDelta()).append("\">").eol();
        raiseIndent();

        indent().append("<locals>").eol();
        raiseIndent();
        helper.getLocals().forEach(local -> local.accept(this));
        lowerIndent();
        indent().append("</locals>").eol();

        indent().append("<stack>").eol();
        raiseIndent();
        helper.getStack().forEach(local -> local.accept(this));
        lowerIndent();
        indent().append("</stack>").eol();

        lowerIndent();
        indent().append("</full-frame>").eol();
    }

    public void visitTopVariableInfo(TopVariableInfo helper) {
        indent().append("<top-variable-info tag=\"").append(helper.getTag()).append("\"/>").eol();
    }

    public void visitIntegerVariableInfo(IntegerVariableInfo helper) {
        indent().append("<integer-variable-info tag=\"").append(helper.getTag()).append("\"/>").eol();
    }

    public void visitFloatVariableInfo(FloatVariableInfo helper) {
        indent().append("<float-variable-info tag=\"").append(helper.getTag()).append("\"/>").eol();
    }

    public void visitLongVariableInfo(LongVariableInfo helper) {
        indent().append("<long-variable-info tag=\"").append(helper.getTag()).append("\"/>").eol();
    }

    public void visitDoubleVariableInfo(DoubleVariableInfo helper) {
        indent().append("<double-variable-info tag=\"").append(helper.getTag()).append("\"/>").eol();
    }

    public void visitNullVariableInfo(NullVariableInfo helper) {
        indent().append("<null-variable-info tag=\"").append(helper.getTag()).append("\"/>").eol();
    }

    public void visitUninitializedThisVariableInfo(UninitializedThisVariableInfo helper) {
        indent().append("<uninitialized-this-variable-info tag=\"").append(helper.getTag()).append("\"/>").eol();
    }

    public void visitObjectVariableInfo(ObjectVariableInfo helper) {
        indent().append("<object-variable-info tag=\"").append(helper.getTag()).append("\">").eol();
        raiseIndent();

        top = true;
        super.visitObjectVariableInfo(helper);
        top = false;

        lowerIndent();
        indent().append("</object-variable-info>").eol();
    }

    public void visitUninitializedVariableInfo(UninitializedVariableInfo helper) {
        indent().append("<uninitialized-variable-info tag=\"").append(helper.getTag()).append("\" offset=\"").append(helper.getOffset()).append("\"/>").eol();
    }

    private Printer appendLocalVariable(LocalVariable localVariable) {
        if (localVariable != null) {
            append(" ");
            append(DescriptorHelper.getType(localVariable.getDescriptor())).append(" ").append(localVariable.getName());
        }
        return this;
    }

    private Printer appendClassInfo(int index, Class_info class_info) {
        indent();
        append("<class index=\"").append(index).append("\">");
        class_info.accept(this);
        append("</class>").eol();
        return this;
    }

    // Visible for testing
    String escapeXMLCharacters(String text) {
        StringBuilder result = new StringBuilder();
        boolean containsControlCharacters = false;

        for (int i=0; i<text.length(); i++) {
            char c = text.charAt(i);
            if (c == '&') {
                result.append("&amp;");
            } else if (c == '<') {
                result.append("&lt;");
            } else if (c == '>') {
                result.append("&gt;");
            } else if (Character.isISOControl(c) || c > 0x9F) {
                containsControlCharacters = true;
                result.append("&#x");
                result.append(Integer.toString(c, 16).toUpperCase());
                result.append(";");
            } else {
                result.append(c);
            }
        }

        if (containsControlCharacters) {
            return "<![CDATA[" + result + "]]>";
        } else {
            return result.toString();
        }
    }
}
