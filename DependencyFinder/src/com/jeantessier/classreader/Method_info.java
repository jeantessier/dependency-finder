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

public class Method_info extends Feature_info {
	public static final int ACC_SYNCHRONIZED = 0x0020;
	public static final int ACC_NATIVE       = 0x0100;
	public static final int ACC_ABSTRACT     = 0x0400;
	public static final int ACC_STRICT       = 0x0800;

	public Method_info(Classfile classfile, DataInputStream in) throws IOException {
		super(classfile, in);
	}

	public String FeatureType() {
		return "method";
	}

	public boolean IsSynchronized() {
		return (AccessFlag() & ACC_SYNCHRONIZED) != 0;
	}

	public boolean IsNative() {
		return (AccessFlag() & ACC_NATIVE) != 0;
	}

	public boolean IsAbstract() {
		return (AccessFlag() & ACC_ABSTRACT) != 0;
	}

	public boolean IsStrict() {
		return (AccessFlag() & ACC_STRICT) != 0;
	}

	public boolean IsConstructor() {
		return Name().equals("<init>");
	}

	public boolean IsStaticInitializer() {
		return Name().equals("<clinit>");
	}

	public Collection Exceptions() {
		Collection result = Collections.EMPTY_LIST;

		Iterator i = Attributes().iterator();
		while (i.hasNext()) {
			Object obj = i.next();
			if (obj instanceof Exceptions_attribute) {
				result = ((Exceptions_attribute) obj).Exceptions();
			}
		}

		return result;
	}

	public String Signature() {
		StringBuffer result = new StringBuffer();

		if (IsConstructor()) {
			result.append(Classfile().Class().substring(Classfile().Class().lastIndexOf(".") + 1));
			result.append(SignatureHelper.Signature(Descriptor()));
		} else if (IsStaticInitializer()) {
			result.append("static {}");
		} else {
			result.append(Name());
			result.append(SignatureHelper.Signature(Descriptor()));
		}

		return result.toString();
	}

	public String ReturnType() {
		return SignatureHelper.ReturnType(Descriptor());
	}

	public String Declaration() {
		StringBuffer result = new StringBuffer();

		if (IsPublic()) result.append("public ");
		if (IsProtected()) result.append("protected ");
		if (IsPrivate()) result.append("private ");
		if (IsStatic()) result.append("static ");
		if (IsFinal()) result.append("final ");
		if (IsSynchronized()) result.append("synchronized ");
		if (IsNative()) result.append("native ");
		if (IsAbstract()) result.append("abstract ");

		if (!Name().equals("<init>") && !Name().equals("<clinit>")) {
			result.append((ReturnType() != null) ? ReturnType() : "void").append(" ");
		}

		result.append(Signature());

		if (Exceptions().size() != 0) {
			result.append(" throws ");
			Iterator i = Exceptions().iterator();
			while (i.hasNext()) {
				result.append(i.next());
				if (i.hasNext()) {
					result.append(", ");
				}
			}
		}		

		return result.toString();
	}

	public void Accept(Visitor visitor) {
		visitor.VisitMethod_info(this);
	}
}
