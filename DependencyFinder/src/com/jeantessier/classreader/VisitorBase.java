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

import org.apache.log4j.*;

public abstract class VisitorBase implements Visitor {
	private int current_count;

	protected void ResetCount() {
		current_count = 0;
	}

	protected void RaiseCount() {
		current_count++;
	}

	protected int CurrentCount() {
		return current_count;
	}

	public void VisitConstantPool(ConstantPool constant_pool) {
		Iterator i = constant_pool.iterator();
		while (i.hasNext()) {
			Visitable entry = (Visitable) i.next();
			if (entry != null) {
				entry.Accept(this);
			}
			RaiseCount();
		}
	}

	// Classfile
	public void VisitClassfile(Classfile classfile) {}

	// ConstantPool entries
	public void VisitClass_info(Class_info entry) {}
	public void VisitFieldRef_info(FieldRef_info entry) {}
	public void VisitMethodRef_info(MethodRef_info entry) {}
	public void VisitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {}
	public void VisitString_info(String_info entry) {}
	public void VisitInteger_info(Integer_info entry) {}
	public void VisitFloat_info(Float_info entry) {}
	public void VisitLong_info(Long_info entry) {}
	public void VisitDouble_info(Double_info entry) {}
	public void VisitNameAndType_info(NameAndType_info entry) {}
	public void VisitUTF8_info(UTF8_info entry) {}

	// Features
	public void VisitField_info(Field_info entry) {
		Iterator i = entry.Attributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
	}

	public void VisitMethod_info(Method_info entry) {
		Iterator i = entry.Attributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
	}

	// Attributes
	public void VisitConstantValue_attribute(ConstantValue_attribute attribute) {
		// Do nothing
	}

	public void VisitCode_attribute(Code_attribute attribute) {
		Logger.getLogger(getClass()).debug("Visiting " + attribute.ExceptionHandlers().size() + " exception handler(s) ...");
		
		Iterator i;

		i = attribute.ExceptionHandlers().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
		
		Logger.getLogger(getClass()).debug("Visiting " + attribute.Attributes().size() + " code attribute(s) ...");
		
		i = attribute.Attributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
	}

	public void VisitExceptions_attribute(Exceptions_attribute attribute) {
		Logger.getLogger(getClass()).debug("Visiting " + attribute.Exceptions().size() + " exception class(es) ...");

		Iterator i = attribute.Exceptions().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
	}
	
	public void VisitInnerClasses_attribute(InnerClasses_attribute attribute) {
		Logger.getLogger(getClass()).debug("Visiting " + attribute.Classes().size() + " inner class(es) ...");

		Iterator i = attribute.Classes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
	}
	
	public void VisitSynthetic_attribute(Synthetic_attribute attribute) {
		// Do nothing
	}
	
	public void VisitSourceFile_attribute(SourceFile_attribute attribute) {
		// Do nothing
	}
	
	public void VisitLineNumberTable_attribute(LineNumberTable_attribute attribute) {
		Logger.getLogger(getClass()).debug("Visiting " + attribute.LineNumbers().size() + " line number(s) ...");

		Iterator i = attribute.LineNumbers().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
	}
	
	public void VisitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
		Logger.getLogger(getClass()).debug("Visiting " + attribute.LocalVariables().size() + " local variable(s) ...");

		Iterator i = attribute.LocalVariables().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
	}
	
	public void VisitDeprecated_attribute(Deprecated_attribute attribute) {
		// Do nothing
	}
	
	public void VisitCustom_attribute(Custom_attribute attribute) {
		// Do nothing
	}

	// Attribute helpers
	public void VisitExceptionHandler(ExceptionHandler helper) {}
	public void VisitInnerClass(InnerClass helper) {}
	public void VisitLineNumber(LineNumber helper) {}
	public void VisitLocalVariable(LocalVariable helper) {}
}
