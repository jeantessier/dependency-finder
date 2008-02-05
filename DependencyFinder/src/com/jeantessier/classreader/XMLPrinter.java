/*
 *  Copyright (c) 2001-2008, Jean Tessier
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

import java.io.*;
import java.util.*;

import org.apache.oro.text.perl.*;

import com.jeantessier.text.*;

public class XMLPrinter extends Printer {
    public static final String DEFAULT_ENCODING   = "utf-8";
    public static final String DEFAULT_DTD_PREFIX = "http://depfind.sourceforge.net/dtd";

    private static final BitFormat format = new BitFormat(16);
    private static final Perl5Util perl = new Perl5Util();

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
            append("<class id=\"").append(currentCount()).append("\">");
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
            append("<field-ref-info id=\"").append(currentCount()).append("\">");
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
            append("<method-ref-info id=\"").append(currentCount()).append("\">");
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
            append("<interface-method-ref-info id=\"").append(currentCount()).append("\">");
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
            append("<string-info id=\"").append(currentCount()).append("\">");
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
            append("<integer-info id=\"").append(currentCount()).append("\">");
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
            append("<float-info id=\"").append(currentCount()).append("\">");
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
            append("<long-info id=\"").append(currentCount()).append("\">");
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
            append("<double-info id=\"").append(currentCount()).append("\">");
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
            append("<name-and-type-info id=\"").append(currentCount()).append("\">");
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
            indent().append("<utf8-info id=\"").append(currentCount()).append("\">");
            append(escapeXMLCharacters(entry.getValue()));
            append("</utf8-info>").eol();
            top = true;
        } else {
            append(escapeXMLCharacters(entry.getValue()));
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
        if (entry.isNative())       indent().append("<native/>").eol();
        if (entry.isAbstract())     indent().append("<abstract/>").eol();
        if (entry.isStrict())       indent().append("<strict/>").eol();

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

        for (InnerClass innerClass : attribute.getInnerClasses()) {
            innerClass.accept(this);
        }

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
            attribute.getRawMethod().accept(this);
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

        for (LineNumber lineNumber : attribute.getLineNumbers()) {
            lineNumber.accept(this);
        }

        lowerIndent();
        indent().append("</line-number-table-attribute>").eol();
    }

    public void visitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
        indent().append("<local-variable-table-attribute>").eol();
        raiseIndent();

        for (LocalVariable localVariable : attribute.getLocalVariables()) {
            localVariable.accept(this);
        }

        lowerIndent();
        indent().append("</local-variable-table-attribute>").eol();
    }

    public void visitLocalVariableTypeTable_attribute(LocalVariableTypeTable_attribute attribute) {
        indent().append("<local-variable-type-table-attribute>").eol();
        raiseIndent();

        for (LocalVariableType localVariableType : attribute.getLocalVariableTypes()) {
            localVariableType.accept(this);
        }

        lowerIndent();
        indent().append("</local-variable-type-table-attribute>").eol();
    }

    public void visitDeprecated_attribute(Deprecated_attribute attribute) {
        indent().append("<deprecated-attribute/>").eol();
    }

    public void visitCustom_attribute(Custom_attribute attribute) {
        indent().append("<custom-attribute name=\"").append(escapeXMLCharacters(attribute.getName())).append("\">").append(Hex.toString(attribute.getInfo())).append("</custom-attribute>").eol();
    }

    public void visitInstruction(Instruction instruction) {
        indent();
        append("<instruction pc=\"").append(instruction.getStart()).append("\" length=\"").append(instruction.getLength()).append("\"");
        switch (instruction.getOpcode()) {
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

        if (helper.isPublic())    indent().append("<public/>").eol();
        if (helper.isProtected()) indent().append("<protected/>").eol();
        if (helper.isPrivate())   indent().append("<private/>").eol();
        if (helper.isStatic())    indent().append("<static/>").eol();
        if (helper.isFinal())     indent().append("<final/>").eol();
        if (helper.isInterface()) indent().append("<is-interface/>").eol();
        if (helper.isAbstract())  indent().append("<abstract/>").eol();

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

    private String escapeXMLCharacters(String text) {
        String result = text;

        result = perl.substitute("s/&/&amp;/g", result);
        result = perl.substitute("s/</&lt;/g", result);
        result = perl.substitute("s/>/&gt;/g", result);
        
        return result;
    }
}
