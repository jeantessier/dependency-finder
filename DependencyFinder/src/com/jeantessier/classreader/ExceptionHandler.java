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

public class ExceptionHandler implements Visitable {
	private Code_attribute code;
	private int            start_pc;
	private int            end_pc;
	private int            handler_pc;
	private int            catch_type_index;

	public ExceptionHandler(Code_attribute code, DataInputStream in) throws IOException {
		Code(code);

		start_pc = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("start PC: " + start_pc);

		end_pc = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("end PC: " + end_pc);

		handler_pc = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("handler PC: " + handler_pc);

		catch_type_index = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("catch type index: " + catch_type_index + " (" + CatchType() + ")");
	}

	public Code_attribute Code() {
		return code;
	}

	private void Code(Code_attribute code) {
		this.code = code;
	}

	public int StartPC() {
		return start_pc;
	}

	public int EndPC() {
		return end_pc;
	}

	public int HandlerPC() {
		return handler_pc;
	}

	public int CatchTypeIndex() {
		return catch_type_index;
	}

	public Class_info RawCatchType() {
		return (Class_info) code.Classfile().ConstantPool().get(CatchTypeIndex());
	}

	public String CatchType() {
		String result = "<none>";

		if (CatchTypeIndex() != 0) {
			result = RawCatchType().toString();
		}

		return result;
	}

	public String toString() {
		return "ExceptionHandler for " + CatchType();
	}

	public void Accept(Visitor visitor) {
		visitor.VisitExceptionHandler(this);
	}
}
