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

import org.apache.log4j.*;

public class LocalVariable implements Visitable {
    private LocalVariableTable_attribute local_variable_table;
    private int                          start_pc;
    private int                          length;
    private int                          name_index;
    private int                          descriptor_index;
    private int                          index;

    public LocalVariable(LocalVariableTable_attribute local_variable_table, DataInputStream in) throws IOException {
		LocalVariableTable(local_variable_table);

		start_pc = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("start PC: " + start_pc);

		length = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("length: " + length);

		name_index       = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("name: " + name_index + " (" + Name() + ")");

		descriptor_index = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("descriptor: " + descriptor_index + " (" + Descriptor() + ")");

		index = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("index: " + index);
    }

    public LocalVariableTable_attribute LocalVariableTable() {
		return local_variable_table;
    }

    private void LocalVariableTable(LocalVariableTable_attribute local_variable_table) {
		this.local_variable_table = local_variable_table;
    }

    public int StartPC() {
		return start_pc;
    }

    public int Length() {
		return length;
    }

    public int NameIndex() {
		return name_index;
    }

    public UTF8_info RawName() {
		return (UTF8_info) LocalVariableTable().Classfile().ConstantPool().get(name_index);
    }

    public String Name() {
		return RawName().toString();
    }

    public int DescriptorIndex() {
		return descriptor_index;
    }

    public UTF8_info RawDescriptor() {
		return (UTF8_info) LocalVariableTable().Classfile().ConstantPool().get(descriptor_index);
    }

    public String Descriptor() {
		return RawDescriptor().toString();
    }

    public int Index() {
		return index;
    }

    public String toString() {
		return "Local variable " + Descriptor() + " " + Name();
    }

    public void Accept(Visitor visitor) {
		visitor.VisitLocalVariable(this);
    }
}
