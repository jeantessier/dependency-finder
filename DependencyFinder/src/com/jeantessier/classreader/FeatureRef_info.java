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

public abstract class FeatureRef_info extends ConstantPoolEntry {
    private int class_index;
    private int name_and_type_index;

    public FeatureRef_info(ConstantPool constant_pool, DataInputStream in) throws IOException {
		super(constant_pool);

		class_index = in.readUnsignedShort();
		name_and_type_index = in.readUnsignedShort();
    }

    public int ClassIndex() {
		return class_index;
    }

    public Class_info RawClass() {
		return (Class_info) ConstantPool().get(ClassIndex());
    }

    public String Class() {
		return RawClass().toString();
    }

    public int NameAndTypeIndex() {
		return name_and_type_index;
    }

    public NameAndType_info RawNameAndType() {
		return (NameAndType_info) ConstantPool().get(NameAndTypeIndex());
    }

    public String NameAndType() {
		StringBuffer result = new StringBuffer();

		NameAndType_info nat = RawNameAndType();

		result.append(nat.Name()).append(nat.Type());

		return result.toString();
    }

    public abstract String Name();

    public String FullName() {
		return Class() + "." + Name();
    }

    public abstract String Signature();

    public String FullSignature() {
		return Class() + "." + Signature();
    }

    public String toString() {
		StringBuffer result = new StringBuffer();

		Class_info       c   = RawClass();
		NameAndType_info nat = RawNameAndType();

		result.append(c).append(".").append(nat.Name()).append(nat.Type());

		return result.toString();
    }
}
