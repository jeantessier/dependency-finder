/*
 *  Dependency Finder - Computes quality factors from compiled Java code
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeantessier.classreader;

public class UglyPrinter extends Printer {
    public void VisitClassfile(Classfile classfile) {
		classfile.ConstantPool().Accept(this);
    }

	public UglyPrinter() {
		super();
	}

	public UglyPrinter(String header) {
		super(header);
	}

	public UglyPrinter(StringBuffer buffer) {
		super(buffer);
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
