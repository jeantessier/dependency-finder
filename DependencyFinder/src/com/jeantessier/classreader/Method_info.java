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
			result.append("{}");
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
