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

public abstract class Printer extends VisitorBase {
    private StringBuffer buffer       = new StringBuffer();
	private String       indent_text;
	private int          indent_level = 0;

	public Printer() {
		this("    ");
	}

	public Printer(String indent_text) {
		this.indent_text = indent_text;
	}
	
    protected Printer Append(boolean b) {
		buffer.append(b);
		return this;
    }

    protected Printer Append(char c) {
		buffer.append(c);
		return this;
    }

    protected Printer Append(char[] str) {
		buffer.append(str);
		return this;
    }

    protected Printer Append(char[] str, int offset, int len) {
		buffer.append(str, offset, len);
		return this;
    }

    protected Printer Append(double d) {
		buffer.append(d);
		return this;
    }

    protected Printer Append(float f) {
		buffer.append(f);
		return this;
    }

    protected Printer Append(int i) {
		buffer.append(i);
		return this;
    }

    protected Printer Append(long l) {
		buffer.append(l);
		return this;
    }

    protected Printer Append(Object obj) {
		buffer.append(obj);
		return this;
    }

    protected Printer Append(String str) {
		buffer.append(str);
		return this;
    }

	protected Printer Indent() {
		for (int i=0; i<indent_level; i++) {
			Append(indent_text);
		}

		return this;
	}

	protected void RaiseIndent() {
		indent_level++;
	}

	protected void LowerIndent() {
		indent_level--;
	}

    public String toString() {
		return buffer.toString();
    }
}
