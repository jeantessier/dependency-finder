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

public class InnerClass implements Visitable {
    public static final int ACC_PUBLIC    = 0x0001;
    public static final int ACC_PRIVATE   = 0x0002;
    public static final int ACC_PROTECTED = 0x0004;
    public static final int ACC_STATIC    = 0x0008;
    public static final int ACC_FINAL     = 0x0010;
    public static final int ACC_INTERFACE = 0x0200;
    public static final int ACC_ABSTRACT  = 0x0400;

    private InnerClasses_attribute inner_classes;
    private int                    inner_class_info_index;
    private int                    outer_class_info_index;
    private int                    inner_name_index;
    private int                    access_flag;

    public InnerClass(InnerClasses_attribute inner_classes, DataInputStream in) throws IOException {
		InnerClasses(inner_classes);

		inner_class_info_index = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("Inner class info index: " + inner_class_info_index + " (" + InnerClassInfo() + ")");

		outer_class_info_index = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("Outer class info index: " + outer_class_info_index + " (" + OuterClassInfo() + ")");

		inner_name_index = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("Inner name index: " + inner_name_index + " (" + InnerName() + ")");

		access_flag = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("Inner class access flag: " + access_flag);
    }

    public InnerClasses_attribute InnerClasses() {
		return inner_classes;
    }

    private void InnerClasses(InnerClasses_attribute inner_classes) {
		this.inner_classes = inner_classes;
    }

    public int InnerClassInfoIndex() {
		return inner_class_info_index;
    }

    public Class_info RawInnerClassInfo() {
		return (Class_info) inner_classes.Classfile().ConstantPool().get(InnerClassInfoIndex());
    }

    public String InnerClassInfo() {
		String result = "";

		if (InnerClassInfoIndex() != 0) {
			result = RawInnerClassInfo().toString();
		}

		return result;
    }

    public int OuterClassInfoIndex() {
		return outer_class_info_index;
    }

    public Class_info RawOuterClassInfo() {
		return (Class_info) inner_classes.Classfile().ConstantPool().get(OuterClassInfoIndex());
    }

    public String OuterClassInfo() {
		String result = "";

		if (OuterClassInfoIndex() != 0) {
			result = RawOuterClassInfo().toString();
		}

		return result;
    }

    public int InnerNameIndex() {
		return inner_name_index;
    }

    public UTF8_info RawInnerName() {
		return (UTF8_info) inner_classes.Classfile().ConstantPool().get(InnerNameIndex());
    }

    public String InnerName() {
		String result = "";

		if (InnerNameIndex() != 0) {
			result = RawInnerName().toString();
		}

		return result;
    }

    public int AccessFlag() {
		return access_flag;
    }

    public String toString() {
		return InnerClassInfo();
    }

    public void Accept(Visitor visitor) {
		visitor.VisitInnerClass(this);
    }
}
