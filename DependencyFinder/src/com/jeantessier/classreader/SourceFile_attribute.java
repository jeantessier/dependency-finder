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

public class SourceFile_attribute extends Attribute_info {
    private int source_file_index;

    public SourceFile_attribute(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
		super(classfile, owner);

		int byte_count = in.readInt();
		Category.getInstance(getClass().getName()).debug("Attribute length: " + byte_count);

		source_file_index = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("Source file: " + source_file_index + " (" + SourceFile() + ")");
    }

    public int SourceFileIndex() {
		return source_file_index;
    }

    public UTF8_info RawSourceFile() {
		return (UTF8_info) Classfile().ConstantPool().get(SourceFileIndex());
    }

    public String SourceFile() {
		return RawSourceFile().toString();
    }

    public String toString() {
		return "Source file \"" + SourceFile() + "\"";
    }

    public void Accept(Visitor visitor) {
		visitor.VisitSourceFile_attribute(this);
    }
}

