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

import org.apache.log4j.*;

public class LineNumber implements Visitable {
	private LineNumberTable_attribute line_number_table;
	private int                       start_pc;
	private int                       line_number;

	public LineNumber(LineNumberTable_attribute line_number_table, DataInputStream in) throws IOException {
		LineNumberTable(line_number_table);

		start_pc = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("Line number table start PC: " + start_pc);

		line_number = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("Line number: " + line_number);
	}

	public LineNumberTable_attribute LineNumberTable() {
		return line_number_table;
	}

	private void LineNumberTable(LineNumberTable_attribute line_number_table) {
		this.line_number_table = line_number_table;
	}

	public int StartPC() {
		return start_pc;
	}

	public int LineNumber() {
		return line_number;
	}

	public String toString() {
		return "Line number";
	}

	public void Accept(Visitor visitor) {
		visitor.VisitLineNumber(this);
	}
}
