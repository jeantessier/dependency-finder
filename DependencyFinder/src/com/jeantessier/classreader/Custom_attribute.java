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

public class Custom_attribute extends Attribute_info {
    private String name;
    private byte[] info;

    public Custom_attribute(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
		this("", classfile, owner, in);
    }

    public Custom_attribute(String name, Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
		super(classfile, owner);

		this.name = name;

		int byte_count = in.readInt();
		Category.getInstance(getClass().getName()).debug("Attribute length: " + byte_count);

		this.info = new byte[byte_count];
		int bytes_read = in.read(info);
		Category.getInstance(getClass().getName()).debug("Bytes read: " + bytes_read);
    }

    public String Name() {
		return name;
    }

    public byte[] Info() {
		return info;
    }

    public String toString() {
		return "Custom \"" + name + "\" " + Info().length + " byte(s)";
    }

    public void Accept(Visitor visitor) {
		visitor.VisitCustom_attribute(this);
    }
}

