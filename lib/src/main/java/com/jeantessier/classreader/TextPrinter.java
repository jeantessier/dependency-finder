/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

        visitClassfileFields(classfile);
        visitClassfileMethods(classfile);

        append("}").eol();
    }

    public void visitClass_info(Class_info entry) {
        if (top) {
            top = false;
            append(currentIndex()).append(": Class ").append(entry).eol();
            top = true;
        } else {
            append(entry);
        }
    }

    public void visitFieldRef_info(FieldRef_info entry) {
        if (top) {
            top = false;
            append(currentIndex()).append(": Field ").append(entry).eol();
            top = true;
        } else {
            append(entry);
        }
    }

    public void visitMethodRef_info(MethodRef_info entry) {
        if (top) {
            top = false;
            append(currentIndex()).append(": Method ").append(entry).eol();
            top = true;
        } else {
            append(entry);
        }
    }

    public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
        if (top) {
            top = false;
            append(currentIndex()).append(": Interface Method ").append(entry).eol();
            top = true;
        } else {
            append(entry);
        }
    }

    public void visitString_info(String_info entry) {
        if (top) {
            top = false;
            append(currentIndex()).append(": String \"");
            entry.getRawValue().accept(this);
            append("\"").eol();
            top = true;
        } else {
            entry.getRawValue().accept(this);
        }
    }

    public void visitInteger_info(Integer_info entry) {
        if (top) {
            append(currentIndex()).append(": Integer ").append(entry.getValue()).eol();
        } else {
            append(entry.getValue());
        }
    }

    public void visitFloat_info(Float_info entry) {
        if (top) {
            append(currentIndex()).append(": Float ").append(entry.getValue()).eol();
        } else {
            append(entry.getValue());
        }
    }

    public void visitLong_info(Long_info entry) {
        if (top) {
            append(currentIndex()).append(": Long ").append(entry.getValue()).eol();
        } else {
            append(entry.getValue());
        }
    }

    public void visitDouble_info(Double_info entry) {
        if (top) {
            append(currentIndex()).append(": Double ").append(entry.getValue()).eol();
        } else {
            append(entry.getValue());
        }
    }

    public void visitNameAndType_info(NameAndType_info entry) {
        if (top) {
            top = false;
            append(currentIndex()).append(": Name and Type ");
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
            append(currentIndex()).append(": \"").append(entry.getValue()).append("\"").eol();
        } else {
            append(entry.getValue());
        }
    }

    public void visitMethodHandle_info(MethodHandle_info entry) {
        if (top) {
            top = false;
            append(currentIndex()).append(": Method Handle ");
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
            append(currentIndex()).append(": Method Type ").append(entry).eol();
            top = true;
        } else {
            append(entry);
        }
    }

    public void visitDynamic_info(Dynamic_info entry) {
        if (top) {
            top = false;
            append(currentIndex()).append(": Dynamic ");
            append(entry.getBootstrapMethodAttrIndex());
            append(" ");
            append(entry);
            eol();
            top = true;
        } else {
            append(entry);
        }
    }

    public void visitInvokeDynamic_info(InvokeDynamic_info entry) {
        if (top) {
            top = false;
            append(currentIndex()).append(": Invoke Dynamic ");
            append(entry.getBootstrapMethodAttrIndex());
            append(" ");
            append(entry);
            eol();
            top = true;
        } else {
            append(entry);
        }
    }

    public void visitModule_info(Module_info entry) {
        if (top) {
            top = false;
            append(currentIndex()).append(": Module ").append(entry).eol();
            top = true;
        } else {
            append(entry);
        }
    }

    public void visitPackage_info(Package_info entry) {
        if (top) {
            top = false;
            append(currentIndex()).append(": Package ").append(entry).eol();
            top = true;
        } else {
            append(entry);
        }
    }

    public void visitUnusableEntry(UnusableEntry entry) {
        if (top) {
            append(currentIndex()).append(": ").append(entry).eol();
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

    public void visitInstruction(Instruction instruction) {
        append("        ").append(instruction.getStart()).append(":\t").append(instruction.getMnemonic());
        appendIndexedConstantPoolEntry(instruction);
        appendIndexedLocalVariable(instruction);
        appendDynamicConstantPoolEntries(instruction);
        appendOffset(instruction);
        appendValue(instruction);
        eol();

        super.visitInstruction(instruction);
    }

    public void visitExceptionHandler(ExceptionHandler handler) {
        append("        ").append(handler.getStartPC()).append("-").append(handler.getEndPC()).append(": ").append(handler.getHandlerPC());
        if (handler.hasCatchType()) {
            append(" (").append(handler.getCatchType()).append(")");
        }
        eol();
    }

    private Printer appendIndexedConstantPoolEntry(Instruction instruction) {
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
                append(" ");
                instruction.getIndexedConstantPoolEntry().accept(this);
                break;
            case 0xba: // invokedynamic
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
                break;
            default:
                // Do nothing
                break;
        }
        return this;
    }

    private Printer appendIndexedLocalVariable(Instruction instruction) {
        switch (instruction.getOpcode()) {
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
                appendLocalVariable(instruction.getIndexedLocalVariable());
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
                appendLocalVariable(instruction.getIndexedLocalVariable());
                append(" (#").append(instruction.getIndex()).append(")");
                break;
            default:
                // Do nothing
                break;
        }
        return this;
    }

    private Printer appendDynamicConstantPoolEntries(Instruction instruction) {
        switch (instruction.getOpcode()) {
            case 0xba: // invokedynamic
                instruction.getDynamicConstantPoolEntries().forEach(entry -> {
                    append(" ");
                    entry.accept(this);
                });
                break;
            default:
                // Do nothing
                break;
        }
        return this;
    }

    private Printer appendLocalVariable(LocalVariable localVariable) {
        if (localVariable != null) {
            append(" ").append(DescriptorHelper.getType(localVariable.getDescriptor())).append(" ").append(localVariable.getName());
        }
        return this;
    }

    private Printer appendOffset(Instruction instruction) {
        switch (instruction.getOpcode()) {
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
                append(" ").append(instruction.getStart() + instruction.getOffset()).append(" (");
                if (instruction.getOffset() >= 0) {
                    append("+");
                }
                append(instruction.getOffset());
                append(")");
                break;
            default:
                // Do nothing
                break;
        }
        return this;
    }

    private Printer appendValue(Instruction instruction) {
        switch (instruction.getOpcode()) {
            case 0x10: // bipush
            case 0x11: // sipush
            case 0x84: // iinc
                append(" ").append(instruction.getValue());
                break;
            case 0xaa: // tableswitch
                append(" default:").appendSwitchDefault(instruction);
                append(" | ");
                appendTableSwitch(instruction, " | ");
                break;
            case 0xab: // lookupswitch
                append(" default:").appendSwitchDefault(instruction);
                append(" | ");
                appendLookupSwitch(instruction, " | ");
                break;
            case 0xc4: // wide
                if (instruction.getByte(1) == 0x84 /* iinc */) {
                    append(" ").append(instruction.getValue());
                }
                break;
            default:
                // Do nothing
                break;
        }
        return this;
    }
}
