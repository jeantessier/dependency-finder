/*
 *  Copyright (c) 2001-2004, Jean Tessier
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
 *  	* Neither the name of Jean Tessier nor the names of his contributors
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
		Logger.getLogger(getClass()).debug("Inner class info index: " + inner_class_info_index + " (" + InnerClassInfo() + ")");

		outer_class_info_index = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("Outer class info index: " + outer_class_info_index + " (" + OuterClassInfo() + ")");

		inner_name_index = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("Inner name index: " + inner_name_index + " (" + InnerName() + ")");

		access_flag = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("Inner class access flag: " + access_flag);
	}

	public InnerClasses_attribute InnerClasses() {
		return inner_classes;
	}

	private void InnerClasses(InnerClasses_attribute inner_classes) {
		this.inner_classes = inner_classes;
	}

	public int getInnerClassInfoIndex() {
		return inner_class_info_index;
	}

	public Class_info getRawInnerClassInfo() {
		return (Class_info) inner_classes.getClassfile().getConstantPool().get(getInnerClassInfoIndex());
	}

	public String InnerClassInfo() {
		String result = "";

		if (getInnerClassInfoIndex() != 0) {
			result = getRawInnerClassInfo().toString();
		}

		return result;
	}

	public int getOuterClassInfoIndex() {
		return outer_class_info_index;
	}

	public Class_info getRawOuterClassInfo() {
		return (Class_info) inner_classes.getClassfile().getConstantPool().get(getOuterClassInfoIndex());
	}

	public String OuterClassInfo() {
		String result = "";

		if (getOuterClassInfoIndex() != 0) {
			result = getRawOuterClassInfo().toString();
		}

		return result;
	}

	public int getInnerNameIndex() {
		return inner_name_index;
	}

	public UTF8_info getRawInnerName() {
		return (UTF8_info) inner_classes.getClassfile().getConstantPool().get(getInnerNameIndex());
	}

	public String InnerName() {
		String result = "";

		if (getInnerNameIndex() != 0) {
			result = getRawInnerName().toString();
		}

		return result;
	}

	public int getAccessFlag() {
		return access_flag;
	}

	public boolean isPublic() {
		return (getAccessFlag() & ACC_PUBLIC) != 0;
	}

	public boolean isProtected() {
		return (getAccessFlag() & ACC_PROTECTED) != 0;
	}

	public boolean isPrivate() {
		return (getAccessFlag() & ACC_PRIVATE) != 0;
	}

	public boolean IsPackage() {
		return (getAccessFlag() & (ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE)) == 0;
	}

	public boolean isStatic() {
		return (getAccessFlag() & ACC_STATIC) != 0;
	}

	public boolean isFinal() {
		return (getAccessFlag() & ACC_FINAL) != 0;
	}

	public boolean isInterface() {
		return (getAccessFlag() & ACC_INTERFACE) != 0;
	}

	public boolean isAbstract() {
		return (getAccessFlag() & ACC_ABSTRACT) != 0;
	}

	public String toString() {
		return InnerClassInfo();
	}

	public void accept(Visitor visitor) {
		visitor.visitInnerClass(this);
	}
}
