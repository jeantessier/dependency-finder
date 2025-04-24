/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

package com.jeantessier.text;

public class PrinterBuffer {
    public static final String DEFAULT_INDENT_TEXT = "    ";

    private final static String EOL = System.getProperty("line.separator", "\n");

    private final StringBuffer buffer = new StringBuffer();
    private final String indentText;
    private int indentLevel = 0;

    public PrinterBuffer() {
        this(DEFAULT_INDENT_TEXT);
    }

    public PrinterBuffer(String indentText) {
        this.indentText = indentText;
    }

    public String getIndentText() {
        return indentText;
    }

    public PrinterBuffer append(boolean b) {
        buffer.append(b);
        return this;
    }

    public PrinterBuffer append(char c) {
        buffer.append(c);
        return this;
    }

    public PrinterBuffer append(char[] str, int offset, int len) {
        buffer.append(str, offset, len);
        return this;
    }

    public PrinterBuffer append(char[] s) {
        buffer.append(s);
        return this;
    }

    public PrinterBuffer append(double d) {
        buffer.append(d);
        return this;
    }

    public PrinterBuffer append(float f) {
        buffer.append(f);
        return this;
    }

    public PrinterBuffer append(int i) {
        buffer.append(i);
        return this;
    }

    public PrinterBuffer append(long l) {
        buffer.append(l);
        return this;
    }

    public PrinterBuffer append(Object obj) {
        buffer.append(obj);
        return this;
    }

    public PrinterBuffer append(String s) {
        buffer.append(s);
        return this;
    }

    public PrinterBuffer indent() {
        buffer.append(getIndentText().repeat(indentLevel));
        return this;
    }

    public PrinterBuffer eol() {
        buffer.append(EOL);
        return this;
    }

    public void raiseIndent() {
        indentLevel++;
    }

    public void lowerIndent() {
        indentLevel--;
    }

    public int length() {
        return buffer.length();
    }

    public String toString() {
        return buffer.toString();
    }
}
