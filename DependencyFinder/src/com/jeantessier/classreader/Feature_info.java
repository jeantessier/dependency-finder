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
		Logger.getLogger(getClass()).debug(FeatureType() + " access flag: " + access_flag);

		name_index       = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug(FeatureType() + " name: " + name_index + " (" + Name() + ")");

		descriptor_index = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug(FeatureType() + " Descriptor: " + descriptor_index + " (" + Descriptor() + ")");

		int attribute_count = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("Reading " + attribute_count + " " + FeatureType() + " attribute(s)");
		for (int i=0; i<attribute_count; i++) {
			Logger.getLogger(getClass()).debug(FeatureType() + " attribute " + i + ":");
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
