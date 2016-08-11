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

import java.io.PrintWriter;
import java.util.Collection;

public class TextPrinter extends Printer {
    private boolean top = true;

    public TextPrinter(PrintWriter out) {
        super(out);
    }
    
    public void visitClassfile(Classfile classfile) {
        top = true;
        classfile.getConstantPool().accept(this);
        top = false;

        eol();

        append(classfile.getDeclaration()).append(" {").eol();

        for (Field_info field : classfile.getAllFields()) {
            field.accept(this);
        }

        for (Method_info method : classfile.getAllMethods()) {
            method.accept(this);
        }

        append("}").eol();
    }

    public void visitClass_info(Class_info entry) {
        if (top) {
            top = false;
            append(currentCount()).append(": Class ");
            append(entry);
            eol();
            top = true;
        } else {
            append(entry);
        }
    }

    public void visitFieldRef_info(FieldRef_info entry) {
        if (top) {
            top = false;
            append(currentCount()).append(": Field ");
            append(entry);
            eol();
            top = true;
        } else {
            append(entry);
        }
    }

    public void visitMethodRef_info(MethodRef_info entry) {
        if (top) {
            top = false;
            append(currentCount()).append(": Method ");
            append(entry);
            eol();
            top = true;
        } else {
            append(entry);
        }
    }

    public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
        if (top) {
            top = false;
            append(currentCount()).append(": Interface Method ");
            append(entry);
            eol();
            top = true;
        } else {
            append(entry);
        }
    }

    public void visitString_info(String_info entry) {
        if (top) {
            top = false;
            append(currentCount()).append(": String \"");
            entry.getRawValue().accept(this);
            append("\"").eol();
            top = true;
        } else {
            entry.getRawValue().accept(this);
        }
    }

    public void visitInteger_info(Integer_info entry) {
        if (top) {
            append(currentCount()).append(": Integer ").append(entry.getValue()).eol();
        } else {
            append(entry.getValue());
        }
    }

    public void visitFloat_info(Float_info entry) {
        if (top) {
            append(currentCount()).append(": Float ").append(entry.getValue()).eol();
        } else {
            append(entry.getValue());
        }
    }

    public void visitLong_info(Long_info entry) {
        if (top) {
            append(currentCount()).append(": Long ").append(entry.getValue()).eol();
        } else {
            append(entry.getValue());
        }
    }

    public void visitDouble_info(Double_info entry) {
        if (top) {
            append(currentCount()).append(": Double ").append(entry.getValue()).eol();
        } else {
            append(entry.getValue());
        }
    }

    public void visitNameAndType_info(NameAndType_info entry) {
        if (top) {
            top = false;
            append(currentCount()).append(": Name and Type ");
            entry.getRawName().accept(this);
            append(" ");
            entry.getRawType().accept(this);
            eol();
            top = true;
        } else {
            entry.getRawName().accept(this);
            append(" ");
            entry.getRawType().accept(this);
        }
    }

    public void visitUTF8_info(UTF8_info entry) {
        if (top) {
            append(currentCount()).append(": \"").append(entry.getValue()).append("\"").eol();
        } else {
            append(entry.getValue());
        }
    }

    public void visitMethodHandle_info(MethodHandle_info entry) {
        if (top) {
            top = false;
            append(currentCount()).append(": Method Handle ");
            append(entry.getReferenceKind().getDescription());
            append(" ");
            entry.getReference().accept(this);
            eol();
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
            append(currentCount()).append(": Method Type ");
            entry.getRawDescriptor().accept(this);
            eol();
            top = true;
        } else {
            entry.getRawDescriptor().accept(this);
        }
    }

    public void visitInvokeDynamic_info(InvokeDynamic_info entry) {
        if (top) {
            top = false;
            append(currentCount()).append(": Invoke Dynamic ");
            append(entry.getBootstrapMethodAttrIndex());
            append(" ");
            append(entry);
            eol();
            top = true;
        } else {
            append(entry);
        }
    }

    public void visitField_info(Field_info entry) {
        append("    ").append(entry.getDeclaration()).append(";").eol();
    }

    public void visitMethod_info(Method_info entry) {
        eol();
        append("    ");
        append(entry.getDeclaration());
        if (!entry.isStaticInitializer()) {
            append(";");
        }
        eol();

        // As per the Class File Format (paragraph 4.8.3):
        // - abstract and native methods must *not* have a Code attribute
        // - all other methods must have exactly one Code attribute.
        if (!entry.isAbstract() && !entry.isNative()) {
            entry.getCode().accept(this);
        }
    }

    public void visitCode_attribute(Code_attribute attribute) {
        append("        CODE").eol();
        visitInstructions(attribute);

        Collection<? extends ExceptionHandler> exceptionHandlers = attribute.getExceptionHandlers();
        if (!exceptionHandlers.isEmpty()) {
            append("        EXCEPTION HANDLING").eol();
            visitExceptionHandlers(exceptionHandlers);
        }
    }

    public void visitInstruction(Instruction helper) {
        append("        ").append(helper.getStart()).append(":\t").append(helper.getMnemonic());
        appendIndexedConstantPoolEntry(helper);
        appendIndexedLocalVariable(helper);
        appendOffset(helper);
        appendValue(helper);
        eol();

        super.visitInstruction(helper);
    }

    public void visitExceptionHandler(ExceptionHandler helper) {
        append("        ").append(helper.getStartPC()).append("-").append(helper.getEndPC()).append(": ").append(helper.getHandlerPC());
        if (helper.getCatchTypeIndex() != 0) {
            append(" (").append(helper.getCatchType()).append(")");
        }
        eol();
    }

    private void appendIndexedConstantPoolEntry(Instruction helper) {
        switch (helper.getOpcode()) {
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
                append(" ");
                helper.getIndexedConstantPoolEntry().accept(this);
                break;
            default:
                // Do nothing
                break;
        }
    }

    private void appendIndexedLocalVariable(Instruction helper) {
        switch (helper.getOpcode()) {
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
                appendLocalVariable(helper.getIndexedLocalVariable());
                break;
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
            case 0x84: // iinc
            case 0xc4: // wide
                appendLocalVariable(helper.getIndexedLocalVariable());
                append(" (#").append(helper.getIndex()).append(")");
                break;
            default:
                // Do nothing
                break;
        }
    }

    private void appendLocalVariable(LocalVariable localVariable) {
        if (localVariable != null) {
            append(" ").append(DescriptorHelper.getType(localVariable.getDescriptor())).append(" ").append(localVariable.getName());
        }
    }

    private void appendOffset(Instruction helper) {
        switch (helper.getOpcode()) {
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
                append(" ").append(helper.getStart() + helper.getOffset()).append(" (");
                if (helper.getOffset() >= 0) {
                    append("+");
                }
                append(helper.getOffset());
                append(")");
                break;
            default:
                // Do nothing
                break;
        }
    }

    private void appendValue(Instruction helper) {
        switch (helper.getOpcode()) {
            case 0x10: // bipush
            case 0x11: // sipush
            case 0x84: // iinc
                append(" ").append(helper.getValue());
                break;
            case 0xc4: // wide
                if (helper.getByte(1) == 0x84 /* iinc */) {
                    append(" ").append(helper.getValue());
                }
                break;
            default:
                // Do nothing
                break;
        }
    }
}
