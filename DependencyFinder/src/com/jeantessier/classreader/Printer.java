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

public abstract class Printer extends VisitorBase {
    private StringBuffer buffer;

	public Printer() {
		this(new StringBuffer());
	}

	public Printer(String header) {
		this(new StringBuffer(header));
	}

	public Printer(StringBuffer buffer) {
		this.buffer = buffer;
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

    public String toString() {
		return buffer.toString();
    }
}
