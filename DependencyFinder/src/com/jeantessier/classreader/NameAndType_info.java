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

public class NameAndType_info extends ConstantPoolEntry {
    private int name_index;
    private int type_index;

    public NameAndType_info(ConstantPool constant_pool, DataInputStream in) throws IOException {
		super(constant_pool);

		name_index = in.readUnsignedShort();
		type_index = in.readUnsignedShort();
    }

    public int NameIndex() {
		return name_index;
    }

    public UTF8_info RawName() {
		return (UTF8_info) ConstantPool().get(NameIndex());
    }

    public String Name() {
		return RawName().toString();
    }

    public int TypeIndex() {
		return type_index;
    }

    public UTF8_info RawType() {
		return (UTF8_info) ConstantPool().get(TypeIndex());
    }

    public String Type() {
		return RawType().toString();
    }

    public String toString() {
		StringBuffer result = new StringBuffer();

		result.append(Name()).append(" -> ").append(Type());

		return result.toString();
    }

    public void Accept(Visitor visitor) {
		visitor.VisitNameAndType_info(this);
    }
}
