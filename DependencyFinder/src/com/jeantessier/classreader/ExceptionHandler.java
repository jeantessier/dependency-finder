/*
 *  Copyright (c) 2001-2004, Jean Tessier
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
 *  	* Neither the name of Jean Tessier nor the names of his contributors
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
		Logger.getLogger(getClass()).debug("start PC: " + start_pc);

		end_pc = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("end PC: " + end_pc);

		handler_pc = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("handler PC: " + handler_pc);

		catch_type_index = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("catch type index: " + catch_type_index + " (" + CatchType() + ")");
	}

	public Code_attribute Code() {
		return code;
	}

	private void Code(Code_attribute code) {
		this.code = code;
	}

	public int getStartPC() {
		return start_pc;
	}

	public int getEndPC() {
		return end_pc;
	}

	public int getHandlerPC() {
		return handler_pc;
	}

	public int getCatchTypeIndex() {
		return catch_type_index;
	}

	public Class_info getRawCatchType() {
		return (Class_info) code.getClassfile().getConstantPool().get(getCatchTypeIndex());
	}

	public String CatchType() {
		String result = "<none>";

		if (getCatchTypeIndex() != 0) {
			result = getRawCatchType().toString();
		}

		return result;
	}

	public String toString() {
		return "ExceptionHandler for " + CatchType();
	}

	public void accept(Visitor visitor) {
		visitor.visitExceptionHandler(this);
	}
}
