/*
 *  Copyright (c) 2001-2003, Jean Tessier
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
 *  	* Neither the name of Jean Tessier nor the names of his contributors
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

import org.apache.oro.text.perl.*;

public class XMLPrinter extends Printer {
	public static final String DEFAULT_DTD_PREFIX = "http://depfind.sourceforge.net/dtd";

	private static final BitFormat format = new BitFormat(16);
	private static final Perl5Util perl = new Perl5Util();

	private String  dtd_prefix;
	
	private boolean top = true;

	public XMLPrinter() {
		this(DEFAULT_DTD_PREFIX);
	}
	
	public XMLPrinter(String dtd_prefix) {
		AppendHeader(dtd_prefix);
	}

	private void AppendHeader(String dtd_prefix) {
		Append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>").EOL();
		EOL();
		Append("<!DOCTYPE classfile SYSTEM \"").Append(dtd_prefix).Append("/classfile.dtd\">").EOL();
		EOL();
	}
    
	public void VisitClassfile(Classfile classfile) {
		Iterator i;

		Indent().Append("<classfile magic-number=\"").Append(classfile.MagicNumber()).Append("\" minor-version=\"").Append(classfile.MinorVersion()).Append("\" major-version=\"").Append(classfile.MajorVersion()).Append("\" access-number=\"").Append(format.format(classfile.AccessFlag())).Append("\">").EOL();
		RaiseIndent();

		top = true;
		classfile.ConstantPool().Accept(this);
		top = false;
		
		if (classfile.IsPublic())     Indent().Append("<public/>").EOL();
		if (classfile.IsFinal())      Indent().Append("<final/>").EOL();
		if (classfile.IsSuper())      Indent().Append("<super/>").EOL();
		if (classfile.IsInterface())  Indent().Append("<is-interface/>").EOL();
		if (classfile.IsAbstract())   Indent().Append("<abstract/>").EOL();

		Indent();
		Append("<this-class>");
		classfile.RawClass().Accept(this);
		Append("</this-class>").EOL();

		Indent();
		Append("<superclass>");
		if (classfile.SuperclassIndex() != 0) {
			classfile.RawSuperclass().Accept(this);
		}
		Append("</superclass>").EOL();

		if (!classfile.Interfaces().isEmpty()) {
			Indent().Append("<interfaces>").EOL();
			RaiseIndent();
			i = classfile.Interfaces().iterator();
			while (i.hasNext()) {
				Indent();
				Append("<interface>");
				((Visitable) i.next()).Accept(this);
				Append("</interface>").EOL();
			}
			LowerIndent();
			Indent().Append("</interfaces>").EOL();
		}
		
		if (!classfile.Fields().isEmpty()) {
			Indent().Append("<fields>").EOL();
			RaiseIndent();
			i = classfile.Fields().iterator();
			while (i.hasNext()) {
				((Visitable) i.next()).Accept(this);
			}
			LowerIndent();
			Indent().Append("</fields>").EOL();
		}

		if (!classfile.Methods().isEmpty()) {
			Indent().Append("<methods>").EOL();
			RaiseIndent();
			i = classfile.Methods().iterator();
			while (i.hasNext()) {
				((Visitable) i.next()).Accept(this);
			}
			LowerIndent();
			Indent().Append("</methods>").EOL();
		}

		if (!classfile.Attributes().isEmpty()) {
			Indent().Append("<attributes>").EOL();
			RaiseIndent();
			i = classfile.Attributes().iterator();
			while (i.hasNext()) {
				((Visitable) i.next()).Accept(this);
			}
			LowerIndent();
			Indent().Append("</attributes>").EOL();
		}

		LowerIndent();
		Indent().Append("</classfile>").EOL();
	}

	public void VisitConstantPool(ConstantPool constant_pool) {
		ResetCount();

		Indent().Append("<constant-pool>").EOL();
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
		Indent().Append("</constant-pool>").EOL();
	}

	public void VisitClass_info(Class_info entry) {
		if (top) {
			top = false;
			Indent();
			Append("<class id=\"").Append(CurrentCount()).Append("\">");
			// entry.RawName().Accept(this);
			Append(entry.Name());
			Append("</class>").EOL();
			top = true;
		} else {
			// entry.RawName().Accept(this);
			Append(entry.Name());
		}
	}

	public void VisitFieldRef_info(FieldRef_info entry) {
		Class_info       c   = entry.RawClass();
		NameAndType_info nat = entry.RawNameAndType();

		if (top) {
			top = false;
			Indent();
			Append("<field-ref-info id=\"").Append(CurrentCount()).Append("\">");
			Append("<class>");
			c.Accept(this);
			Append("</class>");
			Append("<type>");
			nat.RawType().Accept(this);
			Append("</type>");
			Append("<name>");
			nat.RawName().Accept(this);
			Append("</name>");
			Append("</field-ref-info>").EOL();
			top = true;
		} else {
			Append(SignatureHelper.Type(nat.Type()));
			Append(" ");
			Append(entry.FullSignature());
		}
	}

	public void VisitMethodRef_info(MethodRef_info entry) {
		Class_info       c   = entry.RawClass();
		NameAndType_info nat = entry.RawNameAndType();

		if (top) {
			top = false;
			Indent();
			Append("<method-ref-info id=\"").Append(CurrentCount()).Append("\">");
			Append("<class>");
			c.Accept(this);
			Append("</class>");
			Append("<name>");
			nat.RawName().Accept(this);
			Append("</name>");
			Append("<type>");
			nat.RawType().Accept(this);
			Append("</type>");
			Append("</method-ref-info>").EOL();
			top = true;
		} else {
			if (!entry.IsConstructor() && !entry.IsStaticInitializer()) {
				Append(SignatureHelper.ReturnType(nat.Type())).Append(" ");
			}
			Append(entry.FullSignature());
		}
	}

	public void VisitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
		Class_info       c   = entry.RawClass();
		NameAndType_info nat = entry.RawNameAndType();

		if (top) {
			top = false;
			Indent();
			Append("<interface-method-ref-info id=\"").Append(CurrentCount()).Append("\">");
			Append("<class>");
			c.Accept(this);
			Append("</class>");
			Append("<name>");
			nat.RawName().Accept(this);
			Append("</name>");
			Append("<type>");
			nat.RawType().Accept(this);
			Append("</type>");
			Append("</interface-method-ref-info>").EOL();
			top = true;
		} else {
			Append(SignatureHelper.ReturnType(nat.Type()));
			Append(" ");
			Append(entry.FullSignature());
		}
	}

	public void VisitString_info(String_info entry) {
		if (top) {
			top = false;
			Indent();
			Append("<string-info id=\"").Append(CurrentCount()).Append("\">");
			entry.RawValue().Accept(this);
			Append("</string-info>").EOL();
			top = true;
		} else {
			entry.RawValue().Accept(this);
		}
	}

	public void VisitInteger_info(Integer_info entry) {
		if (top) {
			top = false;
			Indent();
			Append("<integer-info id=\"").Append(CurrentCount()).Append("\">");
			Append(entry.Value());
			Append("</integer-info>").EOL();
			top = true;
		} else {
			Append(entry.Value());
		}
	}

	public void VisitFloat_info(Float_info entry) {
		if (top) {
			top = false;
			Indent();
			Append("<float-info id=\"").Append(CurrentCount()).Append("\">");
			Append(entry.Value());
			Append("</float-info>").EOL();
			top = true;
		} else {
			Append(entry.Value());
		}
	}

	public void VisitLong_info(Long_info entry) {
		if (top) {
			top = false;
			Indent();
			Append("<long-info id=\"").Append(CurrentCount()).Append("\">");
			Append(entry.Value());
			Append("</long-info>").EOL();
			top = true;
		} else {
			Append(entry.Value());
		}
	}

	public void VisitDouble_info(Double_info entry) {
		if (top) {
			top = false;
			Indent();
			Append("<double-info id=\"").Append(CurrentCount()).Append("\">");
			Append(entry.Value());
			Append("</double-info>").EOL();
			top = true;
		} else {
			Append(entry.Value());
		}
	}

	public void VisitNameAndType_info(NameAndType_info entry) {
		if (top) {
			top = false;
			Indent();
			Append("<name-and-type-info id=\"").Append(CurrentCount()).Append("\">");
			Append("<name>");
			entry.RawName().Accept(this);
			Append("</name>");
			Append("<type>");
			entry.RawType().Accept(this);
			Append("</type>");
			Append("</name-and-type-info>").EOL();
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
			Indent().Append("<utf8-info id=\"").Append(CurrentCount()).Append("\">");
			Append(EscapeXMLCharacters(entry.Value()));
			Append("</utf8-info>").EOL();
			top = true;
		} else {
			Append(EscapeXMLCharacters(entry.Value()));
		}
	}

	public void VisitField_info(Field_info entry) {
		Indent().Append("<field-info access-flag=\"").Append(format.format(entry.AccessFlag())).Append("\">").EOL();
		RaiseIndent();

		if (entry.IsPublic())    Indent().Append("<public/>").EOL();
		if (entry.IsProtected()) Indent().Append("<protected/>").EOL();
		if (entry.IsPrivate())   Indent().Append("<private/>").EOL();
		if (entry.IsStatic())    Indent().Append("<static/>").EOL();
		if (entry.IsFinal())     Indent().Append("<final/>").EOL();
		if (entry.IsVolatile())  Indent().Append("<volatile/>").EOL();
		if (entry.IsTransient()) Indent().Append("<transient/>").EOL();
	
		Indent();
		Append("<name>");
		entry.RawName().Accept(this);
		Append("</name>").EOL();
		
		Indent().Append("<type>").Append(entry.Type()).Append("</type>").EOL();

		if (!entry.Attributes().isEmpty()) {
			Indent().Append("<attributes>").EOL();
			RaiseIndent();
			super.VisitField_info(entry);
			LowerIndent();
			Indent().Append("</attributes>").EOL();
		}

		LowerIndent();
		Indent().Append("</field-info>").EOL();
	}

	public void VisitMethod_info(Method_info entry) {
		Indent().Append("<method-info access-flag=\"").Append(format.format(entry.AccessFlag())).Append("\">").EOL();
		RaiseIndent();

		if (entry.IsPublic())       Indent().Append("<public/>").EOL();
		if (entry.IsProtected())    Indent().Append("<protected/>").EOL();
		if (entry.IsPrivate())      Indent().Append("<private/>").EOL();
		if (entry.IsStatic())       Indent().Append("<static/>").EOL();
		if (entry.IsFinal())        Indent().Append("<final/>").EOL();
		if (entry.IsSynchronized()) Indent().Append("<synchronized/>").EOL();
		if (entry.IsNative())       Indent().Append("<native/>").EOL();
		if (entry.IsAbstract())     Indent().Append("<abstract/>").EOL();
		if (entry.IsStrict())       Indent().Append("<strict/>").EOL();

		Indent();
		Append("<name>");
		entry.RawName().Accept(this);
		Append("</name>").EOL();
		
		if (!entry.Name().equals("<init>") && !entry.Name().equals("<clinit>")) {
			Indent().Append("<return-type>").Append((entry.ReturnType() != null) ? entry.ReturnType() : "void").Append("</return-type>").EOL();
		}
		Indent().Append("<signature>").Append(entry.Signature()).Append("</signature>").EOL();

		if (!entry.Attributes().isEmpty()) {
			Indent().Append("<attributes>").EOL();
			RaiseIndent();
			super.VisitMethod_info(entry);
			LowerIndent();
			Indent().Append("</attributes>").EOL();
		}

		LowerIndent();
		Indent().Append("</method-info>").EOL();
	}

	public void VisitConstantValue_attribute(ConstantValue_attribute attribute) {
		Indent().Append("<constant-value-attribute>").EOL();
		RaiseIndent();

		attribute.RawValue().Accept(this);

		LowerIndent();
		Indent().Append("</constant-value-attribute>").EOL();
	}

	public void VisitCode_attribute(Code_attribute attribute) {
		Iterator i;
		
		Indent().Append("<code-attribute>").EOL();
		RaiseIndent();

		Indent().Append("<length>").Append(attribute.Code().length).Append("</length>").EOL();

		Indent().Append("<instructions>").EOL();
		RaiseIndent();
		i = attribute.iterator();
		while (i.hasNext()) {
			Instruction instr = (Instruction) i.next();
			Indent();
			Append("<instruction pc=\"").Append(instr.Start()).Append("\" length=\"").Append(instr.Length()).Append("\">");
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
					Append(instr);
					Append(" ");
					((ConstantPoolEntry) attribute.Classfile().ConstantPool().get(index)).Accept(this);
					break;
				default:
					Append(instr);
					break;
			}
			Append("</instruction>").EOL();
		}
		LowerIndent();
		Indent().Append("</instructions>").EOL();

		if (!attribute.ExceptionHandlers().isEmpty()) {
			Indent().Append("<exception-handlers>").EOL();
			RaiseIndent();
			i = attribute.ExceptionHandlers().iterator();
			while (i.hasNext()) {
				((Visitable) i.next()).Accept(this);
			}
			LowerIndent();
			Indent().Append("</exception-handlers>").EOL();
		}
		
		if (!attribute.Attributes().isEmpty()) {
			Indent().Append("<attributes>").EOL();
			RaiseIndent();
			i = attribute.Attributes().iterator();
			while (i.hasNext()) {
				((Visitable) i.next()).Accept(this);
			}
			LowerIndent();
			Indent().Append("</attributes>").EOL();
		}
		
		LowerIndent();
		Indent().Append("</code-attribute>").EOL();
	}

	public void VisitExceptions_attribute(Exceptions_attribute attribute) {
		Indent().Append("<exceptions-attribute>").EOL();
		RaiseIndent();

		Iterator i = attribute.Exceptions().iterator();
		while (i.hasNext()) {
			Indent();
			Append("<exception>");
			((Visitable) i.next()).Accept(this);
			Append("</exception>").EOL();
		}

		LowerIndent();
		Indent().Append("</exceptions-attribute>").EOL();
	}

	public void VisitInnerClasses_attribute(InnerClasses_attribute attribute) {
		Indent().Append("<inner-classes-attribute>").EOL();
		RaiseIndent();

		Iterator i = attribute.Classes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}

		LowerIndent();
		Indent().Append("</inner-classes-attribute>").EOL();
	}

	public void VisitSynthetic_attribute(Synthetic_attribute attribute) {
		Indent().Append("<synthetic-attribute/>").EOL();
	}

	public void VisitSourceFile_attribute(SourceFile_attribute attribute) {
		Indent().Append("<source-file-attribute>").Append(attribute.SourceFile()).Append("</source-file-attribute>").EOL();
	}

	public void VisitLineNumberTable_attribute(LineNumberTable_attribute attribute) {
		Indent().Append("<line-number-table-attribute>").EOL();
		RaiseIndent();

		Iterator i = attribute.LineNumbers().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}

		LowerIndent();
		Indent().Append("</line-number-table-attribute>").EOL();
	}

	public void VisitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
		Indent().Append("<local-variable-table-attribute>").EOL();
		RaiseIndent();

		Iterator i = attribute.LocalVariables().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}

		LowerIndent();
		Indent().Append("</local-variable-table-attribute>").EOL();
	}

	public void VisitDeprecated_attribute(Deprecated_attribute attribute) {
		Indent().Append("<deprecated-attribute/>").EOL();
	}

	public void VisitExceptionHandler(ExceptionHandler helper) {
		Indent();
		Append("<exception-handler>");
		Append("<start-pc>").Append(helper.StartPC()).Append("</start-pc>");
		Append("<end-pc>").Append(helper.EndPC()).Append("</end-pc>");
		Append("<handler-pc>").Append(helper.HandlerPC()).Append("</handler-pc>");

		Append("<catch-type>");
		if (helper.CatchTypeIndex() != 0) {
			helper.RawCatchType().Accept(this);
		}
		Append("</catch-type>");

		Append("</exception-handler>").EOL();
	}

	public void VisitInnerClass(InnerClass helper) {
		Indent().Append("<inner-class access-number=\"").Append(format.format(helper.AccessFlag())).Append("\">").EOL();
		RaiseIndent();

		if (helper.IsPublic())    Indent().Append("<public/>").EOL();
		if (helper.IsProtected()) Indent().Append("<protected/>").EOL();
		if (helper.IsPrivate())   Indent().Append("<private/>").EOL();
		if (helper.IsStatic())    Indent().Append("<static/>").EOL();
		if (helper.IsFinal())     Indent().Append("<final/>").EOL();
		if (helper.IsInterface()) Indent().Append("<is-interface/>").EOL();
		if (helper.IsAbstract())  Indent().Append("<abstract/>").EOL();

		Indent();
		Append("<inner-class-info>");
		if (helper.InnerClassInfoIndex() != 0) {
			helper.RawInnerClassInfo().Accept(this);
		}
		Append("</inner-class-info>").EOL();

		Indent();
		Append("<outer-class-info>");
		if (helper.OuterClassInfoIndex() != 0) {
			helper.RawOuterClassInfo().Accept(this);
		}
		Append("</outer-class-info>").EOL();

		Indent();
		Append("<inner-name>");
		if (helper.InnerNameIndex() != 0) {
			helper.RawInnerName().Accept(this);
		}
		Append("</inner-name>").EOL();

		LowerIndent();
		Indent().Append("</inner-class>").EOL();
	}

	public void VisitLineNumber(LineNumber helper) {
		Indent();
		Append("<line-number>");
		Append("<start-pc>").Append(helper.StartPC()).Append("</start-pc>");
		Append("<line>").Append(helper.LineNumber()).Append("</line>");
		Append("</line-number>").EOL();
	}

	public void VisitLocalVariable(LocalVariable helper) {
		Indent();
		Append("<local-variable pc=\"").Append(helper.StartPC()).Append("\" length=\"").Append(helper.Length()).Append("\">");
		Append("<name>");
		helper.RawName().Accept(this);
		Append("</name>");
		
		// Append("<descriptor>");
		// helper.RawDescriptor().Accept(this);
		// Append("</descriptor>");

		Append("<type>").Append(SignatureHelper.Type(helper.Descriptor())).Append("</type>");
		Append("</local-variable>").EOL();
	}

	private String EscapeXMLCharacters(String text) {
		String result = text;

		result = perl.substitute("s/&/&amp;/g", result);
		result = perl.substitute("s/</&lt;/g", result);
		result = perl.substitute("s/>/&gt;/g", result);
		
		return result;
	}
}
