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

public class UglyPrinter extends Printer {
	public UglyPrinter() {
		super();
	}

	public UglyPrinter(String indent_text) {
		super(indent_text);
	}
	
    public void VisitClassfile(Classfile classfile) {
		classfile.ConstantPool().Accept(this);
    }

    public void VisitClass_info(Class_info entry) {
		Append(CurrentCount()).Append(": ").Append(ConstantPoolEntry.CONSTANT_Class).Append(":");
		Append("Class name=").Append(entry.Name()).Append("\n");
    }

    public void VisitFieldRef_info(FieldRef_info entry) {
		Append(CurrentCount()).Append(": ").Append(ConstantPoolEntry.CONSTANT_Fieldref).Append(":");
		Append("Field class=").Append(entry.Class()).Append(" name&type=").Append(entry.NameAndType()).Append("\n");
    }

    public void VisitMethodRef_info(MethodRef_info entry) {
		Append(CurrentCount()).Append(": ").Append(ConstantPoolEntry.CONSTANT_Methodref).Append(":");
		Append("Method class=").Append(entry.Class()).Append(" name&type=").Append(entry.NameAndType()).Append("\n");
    }

    public void VisitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
		Append(CurrentCount()).Append(": ").Append(ConstantPoolEntry.CONSTANT_InterfaceMethodref).Append(":");
		Append("Interface method class=").Append(entry.Class()).Append(" name&type=").Append(entry.NameAndType()).Append("\n");
    }

    public void VisitString_info(String_info entry) {
		Append(CurrentCount()).Append(": ").Append(ConstantPoolEntry.CONSTANT_String).Append(":");
		Append("String value=").Append(entry.Value()).Append("\n");
    }

    public void VisitInteger_info(Integer_info entry) {
		Append(CurrentCount()).Append(": ").Append(ConstantPoolEntry.CONSTANT_Integer).Append(":");
		Append("Integer value=").Append(entry.Value()).Append("\n");
    }

    public void VisitFloat_info(Float_info entry) {
		Append(CurrentCount()).Append(": ").Append(ConstantPoolEntry.CONSTANT_Float).Append(":");
	    Append("Float value=").Append(entry.Value()).Append("\n");
    }

    public void VisitLong_info(Long_info entry) {
		Append(CurrentCount()).Append(": ").Append(ConstantPoolEntry.CONSTANT_Long).Append(":");
		Append("Long value=").Append(entry.Value()).Append("\n");
    }

    public void VisitDouble_info(Double_info entry) {
		Append(CurrentCount()).Append(": ").Append(ConstantPoolEntry.CONSTANT_Double).Append(":");
	    Append("Double value=").Append(entry.Value()).Append("\n");
    }

    public void VisitNameAndType_info(NameAndType_info entry) {
		Append(CurrentCount()).Append(": ").Append(ConstantPoolEntry.CONSTANT_NameAndType).Append(":");
		Append("NameAndType name=").Append(entry.Name()).Append(" type=").Append(entry.Type()).Append("\n");
    }

    public void VisitUTF8_info(UTF8_info entry) {
		Append(CurrentCount()).Append(": ").Append(ConstantPoolEntry.CONSTANT_Utf8).Append(":");
		Append("\"").Append(entry.Value()).Append("\"").Append("\n");
    }
}
