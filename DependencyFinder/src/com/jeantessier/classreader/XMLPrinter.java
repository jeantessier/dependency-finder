/*
 *  Copyright (c) 2001-2002, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of the Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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

public class XMLPrinter extends Printer {
    private String  indent;
    private int     indent_level = 0;
    private boolean top          = true;

    public XMLPrinter() {
		this("    ");
    }

    public XMLPrinter(String indent) {
		this.indent = indent;
    }
    
    public void VisitClassfile(Classfile classfile) {
		Iterator i;

		Indent().Append("<CLASSFILE>\n");
		indent_level++;

		Indent().Append("<MAGIC_NUMBER>").Append(classfile.MagicNumber()).Append("</MAGIC_NUMBER>\n");
		Indent().Append("<MINOR_VERSION>").Append(classfile.MinorVersion()).Append("</MINOR_VERSION>\n");
		Indent().Append("<MAJOR_VERSION>").Append(classfile.MajorVersion()).Append("</MAJOR_VERSION>\n");

		classfile.ConstantPool().Accept(this);

		Indent().Append("<ACCESS_FLAG>").Append(classfile.AccessFlag()).Append("</ACCESS_FLAG>\n");
		Indent().Append("<THIS_CLASS>").Append(classfile.Class()).Append("</THIS_CLASS>\n");
		Indent().Append("<SUPERCLASS>").Append(classfile.Superclass()).Append("</SUPERCLASS>\n");

		Indent().Append("<INTERFACES>\n");
		indent_level++;
		i = classfile.Interfaces().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
		indent_level--;
		Indent().Append("</INTERFACES>\n");

		indent_level--;
		Indent().Append("</CLASSFILE>\n");
    }

    public void VisitConstantPool(ConstantPool constant_pool) {
		ResetCount();

		Indent().Append("<CONSTANT_POOL>\n");
		indent_level++;

		Iterator i = constant_pool.iterator();
		while (i.hasNext()) {
			Visitable entry = (Visitable) i.next();
			if (entry != null) {
				entry.Accept(this);
			}
			RaiseCount();
		}

		indent_level--;
		Indent().Append("</CONSTANT_POOL>\n");
    }

    public void VisitClass_info(Class_info entry) {
		if (top) {
			top = false;
			Indent();
			Append("<CLASS ID=").Append(CurrentCount()).Append(">");
			entry.RawName().Accept(this);
			Append("</CLASS>\n");
			top = false;
		} else {
			entry.RawName().Accept(this);
		}
    }

    public void VisitFieldRef_info(FieldRef_info entry) {
		Class_info       c   = entry.RawClass();
		NameAndType_info nat = entry.RawNameAndType();

		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Field ");
			nat.RawType().Accept(this);
			Append(" ");
			c.Accept(this);
			Append(".");
			nat.RawName().Accept(this);
			Append("\n");
			top = true;
		} else {
			nat.RawType().Accept(this);
			Append(" ");
			c.Accept(this);
			Append(".");
			nat.RawName().Accept(this);
		}
    }

    public void VisitMethodRef_info(MethodRef_info entry) {
		Class_info       c   = entry.RawClass();
		NameAndType_info nat = entry.RawNameAndType();

		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Method ");
			c.Accept(this);
			Append(".");
			nat.RawName().Accept(this);
			nat.RawType().Accept(this);
			Append("\n");
			top = true;
		} else {
			c.Accept(this);
			Append(".");
			nat.RawName().Accept(this);
			nat.RawType().Accept(this);
		}
    }

    public void VisitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
		Class_info       c   = entry.RawClass();
		NameAndType_info nat = entry.RawNameAndType();

		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Interface Method ");
			c.Accept(this);
			Append(".");
			nat.RawName().Accept(this);
			nat.RawType().Accept(this);
			Append("\n");
			top = true;
		} else {
			c.Accept(this);
			Append(".");
			nat.RawName().Accept(this);
			nat.RawType().Accept(this);
		}
    }

    public void VisitString_info(String_info entry) {
		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("String ");
			entry.RawValue().Accept(this);
			Append("\n");
			top = true;
		} else {
			entry.RawValue().Accept(this);
		}
    }

    public void VisitInteger_info(Integer_info entry) {
		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Integer ").Append(entry.Value());
			Append("\n");
			top = true;
		} else {
			Append(entry.Value());
		}
    }

    public void VisitFloat_info(Float_info entry) {
		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Float ").Append(entry.Value());
			Append("\n");
			top = true;
		} else {
			Append(entry.Value());
		}
    }

    public void VisitLong_info(Long_info entry) {
		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Long ").Append(entry.Value());
			Append("\n");
			top = true;
		} else {
			Append(entry.Value());
		}
    }

    public void VisitDouble_info(Double_info entry) {
		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Double ").Append(entry.Value());
			Append("\n");
			top = true;
		} else {
			Append(entry.Value());
		}
    }

    public void VisitNameAndType_info(NameAndType_info entry) {
		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Name and Type ");
			entry.RawName().Accept(this);
			Append(" ");
			entry.RawType().Accept(this);
			Append("\n");
			top = true;
		} else {
			entry.RawName().Accept(this);
			Append(" ");
			entry.RawType().Accept(this);
		}
    }

    public void VisitUTF8_info(UTF8_info entry) {
		if (top) {
			top = false;
			Indent().Append("<UTF8 ID=").Append(CurrentCount()).Append(">").Append(entry.Value()).Append("</UTF8>\n");
			top = true;
		} else {
			Append(entry.Value());
		}
    }

    public void VisitField_info(Field_info entry) {
    }

    public void VisitMethod_info(Method_info entry) {
    }

    public void VisitConstantValue_attribute(ConstantValue_attribute attribute) {
    }

    public void VisitCode_attribute(Code_attribute attribute) {
    }

    public void VisitExceptions_attribute(Exceptions_attribute attribute) {
    }

    public void VisitInnerClasses_attribute(InnerClasses_attribute attribute) {
    }

    public void VisitSynthetic_attribute(Synthetic_attribute attribute) {
    }

    public void VisitSourceFile_attribute(SourceFile_attribute attribute) {
    }

    public void VisitLineNumberTable_attribute(LineNumberTable_attribute attribute) {
    }

    public void VisitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
    }

    public void VisitDeprecated_attribute(Deprecated_attribute attribute) {
    }

    public void VisitExceptionHandler(ExceptionHandler helper) {
    }

    public void VisitInnerClass(InnerClass helper) {
    }

    public void VisitLineNumber(LineNumber helper) {
    }

    public void VisitLocalVariable(LocalVariable helper) {
    }

    private Printer Indent() {
		for (int i=0; i<indent_level; i++) {
			Append(indent);
		}

		return this;
    }
}
