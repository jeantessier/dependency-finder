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

public class Code_attribute extends Attribute_info {
	private int        max_stack;
	private int        max_locals;
	private byte[]     code;
	private Collection exception_handlers = new LinkedList();
	private Collection attributes         = new LinkedList();

	public Code_attribute(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
		super(classfile, owner);

		int byte_count = in.readInt();
		Category.getInstance(getClass().getName()).debug("Attribute length: " + byte_count);

		max_stack = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("Code max stack: " + max_stack);

		max_locals = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("Code max locals: " + max_locals);

		int code_length = in.readInt();
		Category.getInstance(getClass().getName()).debug("Code length: " + code_length);
		code = new byte[code_length];
		int bytes_read = in.read(code);
		Category.getInstance(getClass().getName()).debug("Bytes read: " + bytes_read);


		Iterator ci = iterator();
		while (ci.hasNext()) {
			Instruction instr = (Instruction) ci.next();
			int         start = instr.Start();
			
			switch (instr.Opcode()) {
				case 0xb2: // getstatic
				case 0xb3: // putstatic
				case 0xb4: // getfield
				case 0xb5: // putfield
				case 0xb6: // invokevirtual
				case 0xb7: // invokespecial
				case 0xb8: // invokestatic
				case 0xb9: // invokeinterface
				case 0xbb: // new
				case 0xbd: // anewarray
				case 0xc0: // checkcast
				case 0xc1: // instanceof
				case 0xc5: // multianewarray
					int index = ((code[start+1] & 0xff) << 8) | (code[start+2] & 0xff);
					Category.getInstance(getClass().getName()).debug("    " + start + ": " + instr + " " + index + " (" + Classfile().ConstantPool().get(index) + ")");
					break;
				default:
					Category.getInstance(getClass().getName()).debug("    " + start + ": " + instr + " (" + instr.Length() + " byte(s))");
					break;
			}
		}

		int exception_table_length = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("Reading " + exception_table_length + " exception handler(s) ...");
		for (int i=0; i<exception_table_length; i++) {
			Category.getInstance(getClass().getName()).debug("Exception handler " + i + ":");
			exception_handlers.add(new ExceptionHandler(this, in));
		}

		int attribute_count = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("Reading " + attribute_count + " code attribute(s)");
		for (int i=0; i<attribute_count; i++) {
			Category.getInstance(getClass().getName()).debug("code attribute " + i + ":");
			attributes.add(AttributeFactory.Create(Classfile(), this, in));
		}
	}

	public int MaxStack() {
		return max_stack;
	}

	public int MaxLocals() {
		return max_locals;
	}

	public byte[] Code() {
		return code;
	}

	public Iterator iterator() {
		return new CodeIterator(code);
	}

	public Collection ExceptionHandlers() {
		return exception_handlers;
	}

	public Collection Attributes() {
		return attributes;
	}

	public String toString() {
		return "Code";
	}

	public void Accept(Visitor visitor) {
		visitor.VisitCode_attribute(this);
	}
}
