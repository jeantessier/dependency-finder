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

public class Classfile implements Visitable {
	public static final int ACC_PUBLIC    = 0x0001;
	public static final int ACC_FINAL     = 0x0010;
	public static final int ACC_SUPER     = 0x0020;
	public static final int ACC_INTERFACE = 0x0200;
	public static final int ACC_ABSTRACT  = 0x0400;

	private ClassfileLoader loader;
    
	private int          magic_number;
	private int          minor_version;
	private int          major_version;
	private ConstantPool constant_pool;
	private int          access_flag;
	private int          class_index;
	private int          superclass_index;
	private Map          interfaces = new TreeMap();
	private Map          fields     = new TreeMap();
	private Map          methods    = new TreeMap();
	private Collection   attributes = new LinkedList();

	public Classfile(ClassfileLoader loader, byte[] bytes) throws IOException {
		this(loader, new ByteArrayInputStream(bytes));
	}

	public Classfile(ClassfileLoader loader, String filename) throws IOException {
		this(loader, new FileInputStream(filename));
	}

	public Classfile(ClassfileLoader loader, File file) throws IOException {
		this(loader, new FileInputStream(file));
	}

	public Classfile(ClassfileLoader loader, InputStream in) throws IOException {
		this(loader, new DataInputStream(in));
	}

	public Classfile(ClassfileLoader loader, DataInputStream in) throws IOException {
		this.loader = loader;

		magic_number = in.readInt();
		Logger.getLogger(getClass()).debug("magic number = " + magic_number);

		// Reading the file format's version number
		minor_version = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("minor version = " + minor_version);
		major_version = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("major version = " + major_version);

		// Reading the constant pool
		Logger.getLogger(getClass()).debug("Reading the constant pool ...");
		constant_pool = new ConstantPool(this, in);
		Logger.getLogger(getClass()).debug(constant_pool);

		// Skipping the access flag
		access_flag = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("access_flag = " + access_flag);

		// Retrieving this class's name
		class_index = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("this_class = " + class_index + " (" + Class() + ")");

		// Retrieving this class's superclass
		superclass_index = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("superclass = " + superclass_index + " (" + Superclass() + ")");

		// Retrieving the inferfaces
		int interface_count = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("Reading " + interface_count + " interface(s)");
		for (int i=0; i<interface_count; i++) {
			Class_info interface_info = (Class_info) constant_pool.get(in.readUnsignedShort());
			Logger.getLogger(getClass()).debug("    " + interface_info.Name());
			interfaces.put(interface_info.Name(), interface_info);
		}

		// Retrieving the fields
		int field_count = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("Reading " + field_count + " field(s)");
		for (int i=0; i<field_count; i++) {
			Logger.getLogger(getClass()).debug("Field " + i + ":");
			Field_info field_info = new Field_info(this, in);
			fields.put(field_info.Name(), field_info);
		}

		// Retrieving the methods
		int method_count = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("Reading " + method_count + " method(s)");
		for (int i=0; i<method_count; i++) {
			Logger.getLogger(getClass()).debug("Method " + i + ":");
			Method_info method_info = new Method_info(this, in);
			methods.put(method_info.Signature(), method_info);
		}

		// Retrieving the attributes
		int attribute_count = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("Reading " + attribute_count + " class attribute(s)");
		for (int i=0; i<attribute_count; i++) {
			Logger.getLogger(getClass()).debug("Attribute " + i + ":");
			attributes.add(AttributeFactory.Create(this, this, in));
		}
	}

	public ClassfileLoader Loader() {
		return loader;
	}

	public int MagicNumber() {
		return magic_number;
	}

	public int MinorVersion() {
		return minor_version;
	}

	public int MajorVersion() {
		return major_version;
	}

	public ConstantPool ConstantPool() {
		return constant_pool;
	}

	public int AccessFlag() {
		return access_flag;
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

	public int SuperclassIndex() {
		return superclass_index;
	}

	public Class_info RawSuperclass() {
		return (Class_info) ConstantPool().get(SuperclassIndex());
	}

	public String Superclass() {
		String result = "";

		if (SuperclassIndex() != 0) {
			result = RawSuperclass().toString();
		}
		
		return result;
	}

	public Class_info Interface(String name) {
		return (Class_info) interfaces.get(name);
	}

	public Collection Interfaces() {
		return interfaces.values();
	}

	public Collection Fields() {
		return fields.values();
	}

	public Field_info Field(String name) {
		return (Field_info) fields.get(name);
	}

	public Field_info LocateField(String name) {
		Field_info result = Field(name);

		if (result == null) {
			Classfile classfile = Loader().Classfile(Superclass());
			if (classfile != null) {
				Field_info attempt = classfile.LocateField(name);
				if (attempt != null && (attempt.IsPublic() || attempt.IsProtected())) {
					result = attempt;
				}
			}
		}

		Iterator i = Interfaces().iterator();
		while (result == null && i.hasNext()) {
			Classfile classfile = Loader().Classfile(i.next().toString());
			if (classfile != null) {
				Field_info attempt = classfile.LocateField(name);
				if (attempt != null && (attempt.IsPublic() || attempt.IsProtected())) {
					result = attempt;
				}
			}
		}

		return result;
	}

	public Collection Methods() {
		return methods.values();
	}

	public Method_info Method(String signature) {
		return (Method_info) methods.get(signature);
	}

	public Method_info LocateMethod(String signature) {
		Method_info result = Method(signature);

		if (result == null) {
			Classfile classfile = Loader().Classfile(Superclass());
			if (classfile != null) {
				Method_info attempt = classfile.LocateMethod(signature);
				if (attempt != null && (attempt.IsPublic() || attempt.IsProtected())) {
					result = attempt;
				}
			}
		}

		Iterator i = Interfaces().iterator();
		while (result == null && i.hasNext()) {
			Classfile classfile = Loader().Classfile(i.next().toString());
			if (classfile != null) {
				Method_info attempt = classfile.LocateMethod(signature);
				if (attempt != null && (attempt.IsPublic() || attempt.IsProtected())) {
					result = attempt;
				}
			}
		}

		return result;
	}

	public Collection Attributes() {
		return attributes;
	}

	public boolean IsPublic() {
		return (AccessFlag() & ACC_PUBLIC) != 0;
	}

	public boolean IsPackage() {
		return (AccessFlag() & ACC_PUBLIC) == 0;
	}

	public boolean IsFinal() {
		return (AccessFlag() & ACC_FINAL) != 0;
	}

	public boolean IsSuper() {
		return (AccessFlag() & ACC_SUPER) != 0;
	}

	public boolean IsInterface() {
		return (AccessFlag() & ACC_INTERFACE) != 0;
	}

	public boolean IsAbstract() {
		return (AccessFlag() & ACC_ABSTRACT) != 0;
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

	public String Declaration() {
		StringBuffer result = new StringBuffer();

		if (IsPublic()) result.append("public ");
		if (IsFinal()) result.append("final ");

		if (IsInterface()) {
			result.append("interface ").append(Class());

			if (Interfaces().size() != 0) {
				result.append(" extends ");
				Iterator i = Interfaces().iterator();
				while (i.hasNext()) {
					result.append(i.next());
					if (i.hasNext()) {
						result.append(", ");
					}
				}
			}
		} else {
			if (IsAbstract()) result.append("abstract ");
			result.append("class ").append(Class());

			if (SuperclassIndex() != 0) {
				result.append(" extends ").append(Superclass());
			}
	    
			if (Interfaces().size() != 0) {
				result.append(" implements ");
				Iterator i = Interfaces().iterator();
				while (i.hasNext()) {
					result.append(i.next());
					if (i.hasNext()) {
						result.append(", ");
					}
				}
			}
		}

		return result.toString();
	}

	public void Accept(Visitor visitor) {
		visitor.VisitClassfile(this);
	}

	public String toString() {
		return Class();
	}
}
