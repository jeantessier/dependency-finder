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

public class AttributeFactory {
	private static final String CONSTANT_VALUE       = "ConstantValue";
	private static final String CODE                 = "Code";
	private static final String EXCEPTIONS           = "Exceptions";
	private static final String INNER_CLASSES        = "InnerClasses";
	private static final String SYNTHETIC            = "Synthetic";
	private static final String SOURCE_FILE          = "SourceFile";
	private static final String LINE_NUMBER_TABLE    = "LineNumberTable";
	private static final String LOCAL_VARIABLE_TABLE = "LocalVariableTable";
	private static final String DEPRECATED           = "Deprecated";

	public static Attribute_info Create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
		Attribute_info result = null;

		int name_index = in.readUnsignedShort();
		if (name_index > 0) {
			String name = ((UTF8_info) classfile.ConstantPool().get(name_index)).Value();
			Category.getInstance(AttributeFactory.class.getName()).debug("Attribute name index: " + name_index + " (" + name + ")");
	    
			if (CONSTANT_VALUE.equals(name)) {
				result = new ConstantValue_attribute(classfile, owner, in);
			} else if (CODE.equals(name)) {
				result = new Code_attribute(classfile, owner, in);
			} else if (EXCEPTIONS.equals(name)) {
				result = new Exceptions_attribute(classfile, owner, in);
			} else if (INNER_CLASSES.equals(name)) {
				result = new InnerClasses_attribute(classfile, owner, in);
			} else if (SYNTHETIC.equals(name)) {
				result = new Synthetic_attribute(classfile, owner, in);
			} else if (SOURCE_FILE.equals(name)) {
				result = new SourceFile_attribute(classfile, owner, in);
			} else if (LINE_NUMBER_TABLE.equals(name)) {
				result = new LineNumberTable_attribute(classfile, owner, in);
			} else if (LOCAL_VARIABLE_TABLE.equals(name)) {
				result = new LocalVariableTable_attribute(classfile, owner, in);
			} else if (DEPRECATED.equals(name)) {
				result = new Deprecated_attribute(classfile, owner, in);
			} else {
				Category.getInstance(AttributeFactory.class.getName()).warn("Unknown attribute name \"" + name + "\"");
				result = new Custom_attribute(name, classfile, owner, in);
			}
		} else {
			Category.getInstance(AttributeFactory.class.getName()).debug("Attribute name index: " + name_index);

			Category.getInstance(AttributeFactory.class.getName()).warn("Unknown attribute with no name");
			result = new Custom_attribute(classfile, owner, in);
		}

		return result;
	}
}
