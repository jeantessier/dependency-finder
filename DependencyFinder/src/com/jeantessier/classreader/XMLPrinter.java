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
    private boolean top          = true;

    public XMLPrinter() {
		super();
    }

    public XMLPrinter(String indent_text) {
		super(indent_text);
    }
    
    public void VisitClassfile(Classfile classfile) {
		Iterator i;

		Indent().Append("<CLASSFILE>\n");
		RaiseIndent();

		Indent().Append("<MAGIC-NUMBER>").Append(classfile.MagicNumber()).Append("</MAGIC-NUMBER>\n");
		Indent().Append("<MINOR-VERSION>").Append(classfile.MinorVersion()).Append("</MINOR-VERSION>\n");
		Indent().Append("<MAJOR-VERSION>").Append(classfile.MajorVersion()).Append("</MAJOR-VERSION>\n");

		classfile.ConstantPool().Accept(this);

		Indent().Append("<ACCESS-FLAG>").Append(classfile.AccessFlag()).Append("</ACCESS-FLAG>\n");
		Indent().Append("<THIS-CLASS>").Append(classfile.Class()).Append("</THIS-CLASS>\n");
		Indent().Append("<SUPERCLASS>").Append(classfile.Superclass()).Append("</SUPERCLASS>\n");

		Indent().Append("<INTERFACES>\n");
		RaiseIndent();
		i = classfile.Interfaces().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
		LowerIndent();
		Indent().Append("</INTERFACES>\n");

		Indent().Append("<FIELDS>\n");
		RaiseIndent();
		i = classfile.Fields().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
		LowerIndent();
		Indent().Append("</FIELDS>\n");

		Indent().Append("<METHODS>\n");
		RaiseIndent();
		i = classfile.Methods().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
		LowerIndent();
		Indent().Append("</METHODS>\n");

		Indent().Append("<ATTRIBUTES>\n");
		RaiseIndent();
		i = classfile.Attributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
		LowerIndent();
		Indent().Append("</ATTRIBUTES>\n");

		LowerIndent();
		Indent().Append("</CLASSFILE>\n");
    }

    public void VisitConstantPool(ConstantPool constant_pool) {
		ResetCount();

		Indent().Append("<CONSTANT-POOL>\n");
		RaiseIndent();

		Iterator i = constant_pool.iterator();
		while (i.hasNext()) {
			Visitable entry = (Visitable) i.next();
			if (entry != null) {
				entry.Accept(this);
			}
			RaiseCount();
		}

		LowerIndent();
		Indent().Append("</CONSTANT-POOL>\n");
    }

    public void VisitClass_info(Class_info entry) {
		if (top) {
			top = false;
			Indent();
			Append("<CLASS ID=\"").Append(CurrentCount()).Append("\">");
			entry.RawName().Accept(this);
			Append("</CLASS>\n");
			top = true;
		} else {
			entry.RawName().Accept(this);
		}
    }

    public void VisitFieldRef_info(FieldRef_info entry) {
		Class_info       c   = entry.RawClass();
		NameAndType_info nat = entry.RawNameAndType();

		if (top) {
			top = false;
			Indent();
			Append("<FIELD-REF-INFO ID=\"").Append(CurrentCount()).Append("\">");
			Append("<CLASS>");
			c.Accept(this);
			Append("</CLASS>");
			Append("<TYPE>");
			nat.RawType().Accept(this);
			Append("</TYPE>");
			Append("<NAME>");
			nat.RawName().Accept(this);
			Append("</NAME>");
			Append("</FIELD-REF-INFO>\n");
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
			Indent();
			Append("<METHOD-REF-INFO ID=\"").Append(CurrentCount()).Append("\">");
			Append("<CLASS>");
			c.Accept(this);
			Append("</CLASS>");
			Append("<NAME>");
			nat.RawName().Accept(this);
			Append("</NAME>");
			Append("<TYPE>");
			nat.RawType().Accept(this);
			Append("</TYPE>");
			Append("</METHOD-REF-INFO>\n");
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
			Indent();
			Append("<INTERFACE-METHOD-REF-INFO ID=\"").Append(CurrentCount()).Append("\">");
			Append("<CLASS>");
			c.Accept(this);
			Append("</CLASS>");
			Append("<NAME>");
			nat.RawName().Accept(this);
			Append("</NAME>");
			Append("<TYPE>");
			nat.RawType().Accept(this);
			Append("</TYPE>");
			Append("</INTERFACE-METHOD-REF-INFO>\n");
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
			Indent();
			Append("<STRING-INFO ID=\"").Append(CurrentCount()).Append("\">");
			entry.RawValue().Accept(this);
			Append("</STRING-INFO>\n");
			top = true;
		} else {
			entry.RawValue().Accept(this);
		}
    }

    public void VisitInteger_info(Integer_info entry) {
		if (top) {
			top = false;
			Indent();
			Append("<INTEGER-INFO ID=\"").Append(CurrentCount()).Append("\">");
			Append(entry.Value());
			Append("</INTEGER-INFO>\n");
			top = true;
		} else {
			Append(entry.Value());
		}
    }

    public void VisitFloat_info(Float_info entry) {
		if (top) {
			top = false;
			Indent();
			Append("<FLOAT-INFO ID=\"").Append(CurrentCount()).Append("\">");
			Append(entry.Value());
			Append("</FLOAT-INFO>\n");
			top = true;
		} else {
			Append(entry.Value());
		}
    }

    public void VisitLong_info(Long_info entry) {
		if (top) {
			top = false;
			Indent();
			Append("<LONG-INFO ID=\"").Append(CurrentCount()).Append("\">");
			Append(entry.Value());
			Append("</LONG-INFO>\n");
			top = true;
		} else {
			Append(entry.Value());
		}
    }

    public void VisitDouble_info(Double_info entry) {
		if (top) {
			top = false;
			Indent();
			Append("<DOUBLE-INFO ID=\"").Append(CurrentCount()).Append("\">");
			Append(entry.Value());
			Append("</DOUBLE-INFO>\n");
			top = true;
		} else {
			Append(entry.Value());
		}
    }

    public void VisitNameAndType_info(NameAndType_info entry) {
		if (top) {
			top = false;
			Indent();
			Append("<NAME-AND-TYPE-INFO ID=\"").Append(CurrentCount()).Append("\">");
			Append("<NAME>");
			entry.RawName().Accept(this);
			Append("</NAME>");
			Append("<TYPE>");
			entry.RawType().Accept(this);
			Append("</TYPE>");
			Append("</NAME-AND-TYPE-INFO>\n");
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
			Indent().Append("<UTF8-INFO ID=").Append(CurrentCount()).Append(">");
			Append(entry.Value());
			Append("</UTF8-INFO>\n");
			top = true;
		} else {
			Append(entry.Value());
		}
    }

    public void VisitField_info(Field_info entry) {
		Indent().Append("<FIELD-INFO ACCESS-FLAG=\"").Append(entry.AccessFlag()).Append("\">\n");
		RaiseIndent();

		if (entry.IsPublic())    Indent().Append("<PUBLIC/>\n");
		if (entry.IsProtected()) Indent().Append("<PROTECTED/>\n");
		if (entry.IsPrivate())   Indent().Append("<PRIVATE/>\n");
		if (entry.IsStatic())    Indent().Append("<STATIC/>\n");
		if (entry.IsFinal())     Indent().Append("<FINAL/>\n");
		if (entry.IsVolatile())  Indent().Append("<VOLATILE/>\n");
		if (entry.IsTransient()) Indent().Append("<TRANSIENT/>\n");
	
		Indent().Append("<NAME>").Append(entry.Name()).Append("</NAME>\n");
		Indent().Append("<TYPE>").Append(entry.Type()).Append("</TYPE>\n");

		LowerIndent();
		Indent().Append("</FIELD-INFO>\n");
    }

    public void VisitMethod_info(Method_info entry) {
		Indent().Append("<METHOD-INFO ACCESS-FLAG=\"").Append(entry.AccessFlag()).Append("\">\n");
		RaiseIndent();

		if (entry.IsPublic())       Indent().Append("<PUBLIC/>\n");
		if (entry.IsProtected())    Indent().Append("<PROTECTED/>\n");
		if (entry.IsPrivate())      Indent().Append("<PRIVATE/>\n");
		if (entry.IsStatic())       Indent().Append("<STATIC/>\n");
		if (entry.IsFinal())        Indent().Append("<FINAL/>\n");
		if (entry.IsSynchronized()) Indent().Append("<SYNCHRONIZED/>\n");
		if (entry.IsNative())       Indent().Append("<NATIVE/>\n");
		if (entry.IsAbstract())     Indent().Append("<ABSTRACT/>\n");

		Indent().Append("<NAME>").Append(entry.Name()).Append("</NAME>\n");
		if (!entry.Name().equals("<init>") && !entry.Name().equals("<clinit>")) {
			Indent().Append("<RETURN-TYPE>").Append((entry.ReturnType() != null) ? entry.ReturnType() : "void").Append("</RETURN-TYPE>\n");
		}
		Indent().Append("<SIGNATURE>").Append(entry.Signature()).Append("</SIGNATURE>\n");

		Indent().Append("<EXCEPTIONS>\n");
		RaiseIndent();
		Iterator i = entry.Exceptions().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}		
		LowerIndent();
		Indent().Append("</EXCEPTIONS>\n");

		Indent().Append("<ATTRIBUTES>\n");
		RaiseIndent();
		i = entry.Attributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
		LowerIndent();
		Indent().Append("</ATTRIBUTES>\n");

		LowerIndent();
		Indent().Append("</METHOD-INFO>\n");
    }

    public void VisitConstantValue_attribute(ConstantValue_attribute attribute) {
		Indent().Append("<CONSTANT-VALUE-ATTRIBUTE>\n");
		RaiseIndent();

		attribute.RawValue().Accept(this);

		LowerIndent();
		Indent().Append("</CONSTANT-VALUE-ATTRIBUTE>\n");
    }

    public void VisitCode_attribute(Code_attribute attribute) {
		Iterator i;
		
		Indent().Append("<CODE-ATTRIBUTE>\n");
		RaiseIndent();

		Indent().Append("<LENGTH>").Append(attribute.Code().length).Append("</LENGTH>\n");


		Indent().Append("<INSTRUCTIONS>\n");
		RaiseIndent();
		i = attribute.iterator();
		while (i.hasNext()) {
			Instruction instr = (Instruction) i.next();
			Indent();
			Append("<INSTRUCTION PC=\"").Append(instr.Start()).Append(" LENGTH=\"").Append(instr.Length()).Append("\">");
			switch (instr.Opcode()) {
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
					int index = ((instr.Code()[instr.Start()+1] & 0xff) << 8) | (instr.Code()[instr.Start()+2] & 0xff);
					Append(instr).Append(" ").Append(attribute.Classfile().ConstantPool().get(index));
					break;
				default:
					Append(instr);
					break;
			}
			Append("</INSTRUCTION>\n");
		}
		LowerIndent();
		Indent().Append("</INSTRUCTIONS>\n");

		Indent().Append("<EXCEPTION-HANDLERS>\n");
		RaiseIndent();
		i = attribute.ExceptionHandlers().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
		LowerIndent();
		Indent().Append("</EXCEPTION-HANDLERS>\n");

		Indent().Append("<ATTRIBUTES>\n");
		RaiseIndent();
		i = attribute.Attributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
		LowerIndent();
		Indent().Append("</ATTRIBUTES>\n");

		LowerIndent();
		Indent().Append("</CODE-ATTRIBUTE>\n");
    }

    public void VisitExceptions_attribute(Exceptions_attribute attribute) {
		Indent().Append("<EXCEPTIONS-ATTRIBUTE>\n");
		RaiseIndent();

		Iterator i = attribute.Exceptions().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}

		LowerIndent();
		Indent().Append("</EXCEPTIONS-ATTRIBUTE>\n");
    }

    public void VisitInnerClasses_attribute(InnerClasses_attribute attribute) {
		Indent().Append("<INNER-CLASSES-ATTRIBUTE>\n");
		RaiseIndent();

		Iterator i = attribute.Classes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}

		LowerIndent();
		Indent().Append("</INNER-CLASSES-ATTRIBUTE>\n");
    }

    public void VisitSynthetic_attribute(Synthetic_attribute attribute) {
		Indent().Append("<SYNTHETIC-ATTRIBUTE/>\n");
    }

    public void VisitSourceFile_attribute(SourceFile_attribute attribute) {
		Indent();
		Append("<SOURCE-FILE-ATTRIBUTE>");
		Append(attribute.SourceFile());
		Append("</SOURCE-FILE-ATTRIBUTE>\n");
    }

    public void VisitLineNumberTable_attribute(LineNumberTable_attribute attribute) {
		Indent().Append("<LINE-NUMBER-TABLE-ATTRIBUTE>\n");
		RaiseIndent();

		Iterator i = attribute.LineNumbers().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}

		LowerIndent();
		Indent().Append("</LINE-NUMBER-TABLE-ATTRIBUTE>\n");
    }

    public void VisitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
		Indent().Append("<LOCAL-VARIABLE-TABLE-ATTRIBUTE>\n");
		RaiseIndent();

		Iterator i = attribute.LocalVariables().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}

		LowerIndent();
		Indent().Append("</LOCAL-VARIABLE-TABLE-ATTRIBUTE>\n");
    }

    public void VisitDeprecated_attribute(Deprecated_attribute attribute) {
		Indent().Append("<DEPRECATED-ATTRIBUTE/>\n");
    }

    public void VisitExceptionHandler(ExceptionHandler helper) {
		Indent();
		Append("<EXCEPTION-HANDLER>");
		Append("<START-PC>").Append(helper.StartPC()).Append("</START-PC>");
		Append("<END-PC>").Append(helper.EndPC()).Append("</END-PC>");
		Append("<HANDLER-PC>").Append(helper.HandlerPC()).Append("</HANDLER-PC>");
		Append("<CATCH-TYPE>").Append(helper.CatchType()).Append("</CATCH-TYPE>");
		Append("</EXCEPTION-HANDLER>\n");
    }

    public void VisitInnerClass(InnerClass helper) {
		Indent();
		Append("<INNER-CLASS>");
		Append("</INNER-CLASS>\n");
    }

    public void VisitLineNumber(LineNumber helper) {
		Indent();
		Append("<LINE-NUMBER>");
		Append("<START-PC>").Append(helper.StartPC()).Append("</START-PC>");
		Append("<LINE>").Append(helper.LineNumber()).Append("</LINE>");
		Append("</LINE-NUMBER>\n");
    }

    public void VisitLocalVariable(LocalVariable helper) {
		Indent();
		Append("<LOCAL-VARIABLE PC=\"").Append(helper.StartPC()).Append(" LENGTH=\"").Append(helper.Length()).Append("\">");
		Append("<NAME>").Append(helper.Name()).Append("</NAME>");
		Append("<DESCRIPTOR>").Append(helper.Name()).Append("</DESCRIPTOR>");
		Append("</LOCAL-VARIABLE>\n");
    }
}
