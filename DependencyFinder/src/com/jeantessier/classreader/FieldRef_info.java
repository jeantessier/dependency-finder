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

public class FieldRef_info extends FeatureRef_info {
	public FieldRef_info(ConstantPool constant_pool, DataInputStream in) throws IOException {
		super(constant_pool, in);
	}

	public String NameAndType() {
		StringBuffer result = new StringBuffer();

		NameAndType_info nat = RawNameAndType();

		result.append(nat.Type()).append(" ").append(nat.Name());

		return result.toString();
	}

	public String Name() {
		return RawNameAndType().Name();
	}

	public String Signature() {
		return RawNameAndType().Name();
	}

	public String toString() {
		StringBuffer result = new StringBuffer();

		Class_info       c   = RawClass();
		NameAndType_info nat = RawNameAndType();

		result.append(nat.Type()).append(" ").append(c).append(".").append(nat.Name());

		return result.toString();
	}

	public void Accept(Visitor visitor) {
		visitor.VisitFieldRef_info(this);
	}
}
