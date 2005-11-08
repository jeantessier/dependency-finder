/*
 *  Copyright (c) 2001-2005, Jean Tessier
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
        classfile.getConstantPool().accept(this);

        append(classfile.getDeclaration()).append(" {").eol();

        Iterator i;

        i = classfile.getAllFields().iterator();
        while (i.hasNext()) {
            ((Visitable) i.next()).accept(this);
        }

        i = classfile.getAllMethods().iterator();
        while (i.hasNext()) {
            ((Visitable) i.next()).accept(this);
        }

        append("}").eol();
    }

    public void visitClass_info(Class_info entry) {
        if (top) {
            top = false;
            append(currentCount()).append(": ");
            append("Class ");
            entry.getRawName().accept(this);
            eol();
            top = true;
        } else {
            entry.getRawName().accept(this);
        }
    }

    public void visitFieldRef_info(FieldRef_info entry) {
        Class_info       c   = entry.getRawClass();
        NameAndType_info nat = entry.getRawNameAndType();

        if (top) {
            top = false;
            append(currentCount()).append(": ");
            append("Field ");
            nat.getRawType().accept(this);
            append(" ");
            c.accept(this);
            append(".");
            nat.getRawName().accept(this);
            eol();
            top = true;
        } else {
            nat.getRawType().accept(this);
            append(" ");
            c.accept(this);
            append(".");
            nat.getRawName().accept(this);
        }
    }

    public void visitMethodRef_info(MethodRef_info entry) {
        Class_info       c   = entry.getRawClass();
        NameAndType_info nat = entry.getRawNameAndType();

        if (top) {
            top = false;
            append(currentCount()).append(": ");
            append("Method ");
            c.accept(this);
            append(".");
            nat.getRawName().accept(this);
            nat.getRawType().accept(this);
            eol();
            top = true;
        } else {
            c.accept(this);
            append(".");
            nat.getRawName().accept(this);
            nat.getRawType().accept(this);
        }
    }

    public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
        Class_info       c   = entry.getRawClass();
        NameAndType_info nat = entry.getRawNameAndType();

        if (top) {
            top = false;
            append(currentCount()).append(": ");
            append("Interface Method ");
            c.accept(this);
            append(".");
            nat.getRawName().accept(this);
            nat.getRawType().accept(this);
            eol();
            top = true;
        } else {
            c.accept(this);
            append(".");
            nat.getRawName().accept(this);
            nat.getRawType().accept(this);
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

    public void visitField_info(Field_info entry) {
        append("    ").append(entry.getDeclaration()).append(";").eol();
    }

    public void visitMethod_info(Method_info entry) {
        append("    ").append(entry.getDeclaration()).append(";").eol();
    }
}
