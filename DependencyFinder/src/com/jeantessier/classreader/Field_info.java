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

public class Field_info extends Feature_info {
	public static final int ACC_VOLATILE  = 0x0040;
	public static final int ACC_TRANSIENT = 0x0080;

	public Field_info(Classfile classfile, DataInputStream in) throws IOException {
		super(classfile, in);
	}

	public String FeatureType() {
		return "field";
	}

	public boolean IsVolatile() {
		return (AccessFlag() & ACC_VOLATILE) != 0;
	}

	public boolean IsTransient() {
		return (AccessFlag() & ACC_TRANSIENT) != 0;
	}

	public String Type() {
		return SignatureHelper.Type(Descriptor());
	}

	public String Declaration() {
		StringBuffer result = new StringBuffer();

		if (IsPublic()) result.append("public ");
		if (IsProtected()) result.append("protected ");
		if (IsPrivate()) result.append("private ");
		if (IsStatic()) result.append("static ");
		if (IsFinal()) result.append("final ");
		if (IsVolatile()) result.append("volatile ");
		if (IsTransient()) result.append("transient ");
	
		result.append(Type()).append(" ");
		result.append(Name());

		return result.toString();
	}

	public String Signature() {
		return Name();
	}

	public void Accept(Visitor visitor) {
		visitor.VisitField_info(this);
	}
}
