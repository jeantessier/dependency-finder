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

import org.apache.log4j.*;

public abstract class Feature_info implements Visitable {
	public static final int ACC_PUBLIC    = 0x0001;
	public static final int ACC_PRIVATE   = 0x0002;
	public static final int ACC_PROTECTED = 0x0004;
	public static final int ACC_STATIC    = 0x0008;
	public static final int ACC_FINAL     = 0x0010;

	private Classfile  classfile;
	private int        access_flag;
	private int        name_index;
	private int        descriptor_index;
	private Collection attributes = new LinkedList();

	public Feature_info(Classfile classfile, DataInputStream in) throws IOException {
		Classfile(classfile);

		access_flag = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug(FeatureType() + " access flag: " + access_flag);

		name_index       = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug(FeatureType() + " name: " + name_index + " (" + Name() + ")");

		descriptor_index = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug(FeatureType() + " Descriptor: " + descriptor_index + " (" + Descriptor() + ")");

		int attribute_count = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("Reading " + attribute_count + " " + FeatureType() + " attribute(s)");
		for (int i=0; i<attribute_count; i++) {
			Category.getInstance(getClass().getName()).debug(FeatureType() + " attribute " + i + ":");
			attributes.add(AttributeFactory.Create(Classfile(), this, in));
		}
	}

	public Classfile Classfile() {
		return classfile;
	}

	private void Classfile(Classfile classfile) {
		this.classfile = classfile;
	}

	public int AccessFlag() {
		return access_flag;
	}

	public boolean IsPublic() {
		return (AccessFlag() & ACC_PUBLIC) != 0;
	}

	public boolean IsProtected() {
		return (AccessFlag() & ACC_PROTECTED) != 0;
	}

	public boolean IsPrivate() {
		return (AccessFlag() & ACC_PRIVATE) != 0;
	}

	public boolean IsPackage() {
		return (AccessFlag() & (ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE)) == 0;
	}

	public boolean IsStatic() {
		return (AccessFlag() & ACC_STATIC) != 0;
	}

	public boolean IsFinal() {
		return (AccessFlag() & ACC_FINAL) != 0;
	}

	public int NameIndex() {
		return name_index;
	}

	public UTF8_info RawName() {
		return (UTF8_info) Classfile().ConstantPool().get(name_index);
	}

	public String Name() {
		return RawName().toString();
	}

	public String FullName() {
		return Classfile().Class() + "." + Name();
	}

	public int DescriptorIndex() {
		return descriptor_index;
	}

	public UTF8_info RawDescriptor() {
		return (UTF8_info) Classfile().ConstantPool().get(descriptor_index);
	}

	public String Descriptor() {
		return RawDescriptor().toString();
	}

	public Collection Attributes() {
		return attributes;
	}

	public boolean IsSynthetic() {
		boolean result = false;

		Iterator i = Attributes().iterator();
		while (!result && i.hasNext()) {
			result = i.next() instanceof Synthetic_attribute;
		}
	
		return result;
	}

	public boolean IsDeprecated() {
		boolean result = false;

		Iterator i = Attributes().iterator();
		while (!result && i.hasNext()) {
			result = i.next() instanceof Deprecated_attribute;
		}
	
		return result;
	}

	public String toString() {
		return FullName();
	}

	public abstract String FeatureType();
	public abstract String Declaration();
	public abstract String Signature();

	public String FullSignature() {
		return Classfile().Class() + "." + Signature();
	}
}
