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

package com.jeantessier.classreader;

import com.google.common.annotations.VisibleForTesting;
import com.jeantessier.text.Hex;

import java.io.PrintWriter;
import java.util.Collection;

public class XMLPrinter extends Printer {
    public static final String DEFAULT_ENCODING   = "utf-8";
    public static final String DEFAULT_DTD_PREFIX = "http://depfind.sourceforge.net/dtd";

    private static final BitFormat format = new BitFormat(16);

    private boolean top = true;

    public XMLPrinter(PrintWriter out) {
        this(out, DEFAULT_ENCODING, DEFAULT_DTD_PREFIX);
    }
    
    public XMLPrinter(PrintWriter out, String encoding, String dtdPrefix) {
        super(out);
        
        appendHeader(encoding, dtdPrefix);
    }

    private void appendHeader(String encoding, String dtdPrefix) {
        append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\" ?>").eol();
        eol();
        append("<!DOCTYPE classfiles SYSTEM \"").append(dtdPrefix).append("/classfile.dtd\">").eol();
        eol();
    }

    public void visitClassfiles(Collection<Classfile> classfiles) {
        indent().append("<classfiles>").eol();
        raiseIndent();

        super.visitClassfiles(classfiles);

        lowerIndent();
        indent().append("</classfiles>").eol();
    }
    
    public void visitClassfile(Classfile classfile) {
        indent().append("<classfile magic-number=\"0x").append(Integer.toHexString(classfile.getMagicNumber()).toUpperCase()).append("\" minor-version=\"").append(classfile.getMinorVersion()).append("\" major-version=\"").append(classfile.getMajorVersion()).append("\" access-flag=\"").append(format.format(classfile.getAccessFlag())).append("\">").eol();
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
            for (Field_info field : classfile.getAllFields()) {
                field.accept(this);
            }
            lowerIndent();
            indent().append("</fields>").eol();
        }

        if (!classfile.getAllMethods().isEmpty()) {
            indent().append("<methods>").eol();
            raiseIndent();
            for (Method_info method : classfile.getAllMethods()) {
                method.accept(this);
            }
            lowerIndent();
            indent().append("</methods>").eol();
        }

        if (!classfile.getAttributes().isEmpty()) {
            indent().append("<attributes>").eol();
            raiseIndent();
            for (Attribute_info attribute : classfile.getAttributes()) {
                attribute.accept(this);
            }
            lowerIndent();
            indent().append("</attributes>").eol();
        }

        lowerIndent();
        indent().append("</classfile>").eol();
    }

    public void visitConstantPool(ConstantPool constantPool) {
        resetCount();

        indent().append("<constant-pool>").eol();
        raiseIndent();

        for (ConstantPoolEntry entry : constantPool) {
            if (entry != null) {
                entry.accept(this);
            }
            incrementCount();
        }

        lowerIndent();
        indent().append("</constant-pool>").eol();
    }

    public void visitClass_info(Class_info entry) {
        if (top) {
            top = false;
            indent();
            append("<class index=\"").append(currentCount()).append("\">");
            // entry.getRawName().accept(this);
            append(entry.getName());
            append("</class>").eol();
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
            append("<field-ref-info index=\"").append(currentCount()).append("\">");
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
            append("<method-ref-info index=\"").append(currentCount()).append("\">");
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
            append("<interface-method-ref-info index=\"").append(currentCount()).append("\">");
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
            append("<string-info index=\"").append(currentCount()).append("\">");
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
            append("<integer-info index=\"").append(currentCount()).append("\">");
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
            append("<float-info index=\"").append(currentCount()).append("\">");
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
            append("<long-info index=\"").append(currentCount()).append("\">");
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
            append("<double-info index=\"").append(currentCount()).append("\">");
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
            append("<name-and-type-info index=\"").append(currentCount()).append("\">");
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
            indent().append("<utf8-info index=\"").append(currentCount()).append("\">");
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
            append("<method-handle-info index=\"").append(currentCount()).append("\">");
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
            append("<method-type-info index=\"").append(currentCount()).append("\">");
            entry.getRawDescriptor().accept(this);
            append("</method-type-info>").eol();
            top = true;
        } else {
            entry.getRawDescriptor().accept(this);
        }
    }

    public void visitInvokeDynamic_info(InvokeDynamic_info entry) {
        NameAndType_info nat = entry.getRawNameAndType();

        if (top) {
            top = false;
            indent();
            append("<invoke-dynamic-info index=\"").append(currentCount()).append("\">");
            append("<bootstrap-method-attr index=\"").append(entry.getBootstrapMethodAttrIndex()).append("\">");
            append("</bootstrap-method-attr>");
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

    public void visitField_info(Field_info entry) {
        indent().append("<field-info access-flag=\"").append(format.format(entry.getAccessFlag())).append("\">").eol();
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
        indent().append("<method-info access-flag=\"").append(format.format(entry.getAccessFlag())).append("\">").eol();
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

        indent().append("<class>");
        attribute.getRawClassInfo().accept(this);
        append("</class>").eol();

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

    public void visitAnnotationDefault_attribute(AnnotationDefault_attribute attribute) {
        indent().append("<annotation-default-attribute>").eol();
        raiseIndent();

        super.visitAnnotationDefault_attribute(attribute);

        lowerIndent();
        indent().append("</annotation-default-attribute>").eol();
    }

    public void visitCustom_attribute(Custom_attribute attribute) {
        indent().append("<custom-attribute name=\"").append(escapeXMLCharacters(attribute.getName())).append("\">").append(Hex.toString(attribute.getInfo())).append("</custom-attribute>").eol();
    }

    public void visitInstruction(Instruction instruction) {
        indent();
        append("<instruction pc=\"").append(instruction.getStart()).append("\" length=\"").append(instruction.getLength()).append("\"");
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
        indent().append("<inner-class access-flag=\"").append(format.format(helper.getAccessFlag())).append("\">").eol();
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

    public void visitParameter(Parameter helper) {
        indent().append("<parameter>").eol();
        raiseIndent();

        indent().append("<annotations>").eol();
        raiseIndent();

        super.visitParameter(helper);

        lowerIndent();
        indent().append("</annotations>").eol();

        lowerIndent();
        indent().append("</parameter>").eol();
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

    private void appendLocalVariable(LocalVariable localVariable) {
        if (localVariable != null) {
            append(" ");
            append(DescriptorHelper.getType(localVariable.getDescriptor())).append(" ").append(localVariable.getName());
        }
    }

    @VisibleForTesting
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
            return "<![CDATA[" + result.toString() + "]]>";
        } else {
            return result.toString();
        }
    }
}
