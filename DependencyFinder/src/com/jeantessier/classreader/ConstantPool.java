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

import java.io.*;
import java.util.*;

import org.apache.oro.text.perl.*;

public class ConstantPool extends ArrayList implements Visitable {
    private Classfile classfile;

    public ConstantPool(Classfile classfile, DataInputStream in) throws IOException {
		Classfile(classfile);

		int count = in.readUnsignedShort();

		ensureCapacity(count);

		// Entry 0 is null
		add(null);

		for (int i=1; i<count; i++) {
			byte tag = in.readByte();

			switch(tag) {
				case ConstantPoolEntry.CONSTANT_Class:
					add(new Class_info(this, in));
					break;
				case ConstantPoolEntry.CONSTANT_Fieldref:
					add(new FieldRef_info(this, in));
					break;
				case ConstantPoolEntry.CONSTANT_Methodref:
					add(new MethodRef_info(this, in));
					break;
				case ConstantPoolEntry.CONSTANT_InterfaceMethodref:
					add(new InterfaceMethodRef_info(this, in));
					break;
				case ConstantPoolEntry.CONSTANT_String:
					add(new String_info(this, in));
					break;
				case ConstantPoolEntry.CONSTANT_Integer:
					add(new Integer_info(this, in));
					break;
				case ConstantPoolEntry.CONSTANT_Float:
					add(new Float_info(this, in));
					break;
				case ConstantPoolEntry.CONSTANT_Long:
					add(new Long_info(this, in));
					i++;
					add(null);
					break;
				case ConstantPoolEntry.CONSTANT_Double:
					add(new Double_info(this, in));
					i++;
					add(null);
					break;
				case ConstantPoolEntry.CONSTANT_NameAndType:
					add(new NameAndType_info(this, in));
					break;
				case ConstantPoolEntry.CONSTANT_Utf8:
					add(new UTF8_info(this, in));
					break;
				default:
					System.out.println("Unknown Tag " + tag);
			}
		}
    }
    
    public Classfile Classfile() {
		return classfile;
    }

    private void Classfile(Classfile classfile) {
		this.classfile = classfile;
    }

    public void Accept(Visitor visitor) {
		visitor.VisitConstantPool(this);
    }

	public String toString() {
		Printer printer = new UglyPrinter("Constant Pool:\n");
		Accept(printer);
		return printer.toString();
	}		
}
