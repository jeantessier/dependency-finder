/*
 *  Copyright (c) 2001-2003, Jean Tessier
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

public class LineNumber implements Visitable {
	private LineNumberTable_attribute line_number_table;
	private int                       start_pc;
	private int                       line_number;

	public LineNumber(LineNumberTable_attribute line_number_table, DataInputStream in) throws IOException {
		LineNumberTable(line_number_table);

		start_pc = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("Line number table start PC: " + start_pc);

		line_number = in.readUnsignedShort();
		Logger.getLogger(getClass()).debug("Line number: " + line_number);
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
