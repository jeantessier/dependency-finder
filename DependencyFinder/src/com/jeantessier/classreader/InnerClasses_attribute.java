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

public class InnerClasses_attribute extends Attribute_info {
	private Collection classes = new LinkedList();

	public InnerClasses_attribute(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
		super(classfile, owner);

		int byte_count = in.readInt();
		Category.getInstance(getClass().getName()).debug("Attribute length: " + byte_count);

		int class_count = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("Reading " + class_count + " inner class(es) ...");
		for (int i=0; i<class_count; i++) {
			Category.getInstance(getClass().getName()).debug("Inner class " + i + ":");
			classes.add(new InnerClass(this, in));
		}
	}

	public Collection Classes() {
		return classes;
	}

	public String toString() {
		return "InnerClasses";
	}

	public void Accept(Visitor visitor) {
		visitor.VisitInnerClasses_attribute(this);
	}
}
