/*
 *  Copyright (c) 2001-2009, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
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

package com.jeantessier.diff;

import com.jeantessier.text.*;

public abstract class Printer extends VisitorBase {
    public static final String DEFAULT_INDENT_TEXT = PrinterBuffer.DEFAULT_INDENT_TEXT;

    private PrinterBuffer buffer = new PrinterBuffer();

    public String getIndentText() {
        return buffer.getIndentText();
    }

    public void setIndentText(String indentText) {
        buffer.setIndentText(indentText);
    }

    protected Printer append(boolean b) {
        buffer.append(b);
        return this;
    }

    protected Printer append(char c) {
        buffer.append(c);
        return this;
    }

    protected Printer append(char[] str) {
        buffer.append(str);
        return this;
    }

    protected Printer append(char[] str, int offset, int len) {
        buffer.append(str, offset, len);
        return this;
    }

    protected Printer append(double d) {
        buffer.append(d);
        return this;
    }

    protected Printer append(float f) {
        buffer.append(f);
        return this;
    }

    protected Printer append(int i) {
        buffer.append(i);
        return this;
    }

    protected Printer append(long l) {
        buffer.append(l);
        return this;
    }

    protected Printer append(Object obj) {
        buffer.append(obj);
        return this;
    }

    protected Printer append(String str) {
        buffer.append(str);
        return this;
    }

    protected Printer indent() {
        buffer.indent();
        return this;
    }

    protected Printer eol() {
        buffer.eol();
        return this;
    }

    protected void raiseIndent() {
        buffer.raiseIndent();
    }

    protected void lowerIndent() {
        buffer.lowerIndent();
    }

    public String toString() {
        return buffer.toString();
    }
}
