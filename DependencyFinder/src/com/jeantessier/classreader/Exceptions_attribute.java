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

public class Exceptions_attribute extends Attribute_info {
	private Collection exceptions = new LinkedList();

	public Exceptions_attribute(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
		super(classfile, owner);

		int byte_count = in.readInt();
		Category.getInstance(getClass().getName()).debug("Attribute length: " + byte_count);

		int exception_count = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("Reading " + exception_count + " exception(s) ...");
		for (int i=0; i<exception_count; i++) {
			Category.getInstance(getClass().getName()).debug("Exception " + i + ":");
			Class_info exception = (Class_info) classfile.ConstantPool().get(in.readUnsignedShort());
			exceptions.add(exception);
			Category.getInstance(getClass().getName()).debug("Class " + exception);
		}
	}

	public Collection Exceptions() {
		return exceptions;
	}

	public String toString() {
		return "Exceptions";
	}

	public void Accept(Visitor visitor) {
		visitor.VisitExceptions_attribute(this);
	}
}
