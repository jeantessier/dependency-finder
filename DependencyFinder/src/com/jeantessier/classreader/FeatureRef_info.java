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
