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
		Logger.getLogger(getClass()).debug("Attribute length: " + byte_count);

		max_stack = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("Code max stack: " + max_stack);

		max_locals = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("Code max locals: " + max_locals);

		int code_length = in.readInt();
		Logger.getLogger(getClass()).debug("Code length: " + code_length);
		code = new byte[code_length];
		int bytes_read = in.read(code);
		Logger.getLogger(getClass()).debug("Bytes read: " + bytes_read);


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
					Logger.getLogger(getClass()).debug("    " + start + ": " + instr + " " + index + " (" + Classfile().ConstantPool().get(index) + ")");
					break;
				default:
					Logger.getLogger(getClass()).debug("    " + start + ": " + instr + " (" + instr.Length() + " byte(s))");
					break;
			}
		}

		int exception_table_length = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("Reading " + exception_table_length + " exception handler(s) ...");
		for (int i=0; i<exception_table_length; i++) {
			Logger.getLogger(getClass()).debug("Exception handler " + i + ":");
			exception_handlers.add(new ExceptionHandler(this, in));
		}

		int attribute_count = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("Reading " + attribute_count + " code attribute(s)");
		for (int i=0; i<attribute_count; i++) {
			Logger.getLogger(getClass()).debug("code attribute " + i + ":");
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
