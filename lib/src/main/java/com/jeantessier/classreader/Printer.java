/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

package com.jeantessier.classreader;

import java.io.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class Printer extends VisitorBase {
    public static final String DEFAULT_INDENT_TEXT = "    ";

    private final PrintWriter out;
    private String indentText = DEFAULT_INDENT_TEXT;
    private int indentLevel = 0;

    public Printer(PrintWriter out) {
        this.out = out;
    }
    
    public String getIndentText() {
        return indentText;
    }

    public void setIndentText(String indentText) {
        this.indentText = indentText;
    }
    
    protected Printer append(boolean b) {
        out.print(b);
        return this;
    }

    protected Printer append(char c) {
        out.print(c);
        return this;
    }

    protected Printer append(char[] s) {
        out.print(s);
        return this;
    }

    protected Printer append(double d) {
        out.print(d);
        return this;
    }

    protected Printer append(float f) {
        out.print(f);
        return this;
    }

    protected Printer append(int i) {
        out.print(i);
        return this;
    }

    protected Printer append(long l) {
        out.print(l);
        return this;
    }

    protected Printer append(Object obj) {
        out.print(obj);
        return this;
    }

    protected Printer append(String s) {
        out.print(s);
        return this;
    }

    protected Printer indent() {
        append(getIndentText().repeat(indentLevel));
        return this;
    }

    protected Printer eol() {
        out.println();
        return this;
    }

    private record SwitchEntry(int key, int offset, int jump) {};

    protected Printer appendSwitchDefault(Instruction instruction) {
        return append(String.format("%+d[%d]", instruction.getDefault(), instruction.getStart() + instruction.getDefault()));
    }

    protected Printer appendLookupSwitch(Instruction instruction) {
        return appendLookupSwitch(instruction, " ");
    }

    protected Printer appendLookupSwitch(Instruction instruction, String delimiter) {
        return append(IntStream.range(0, instruction.getNPairs())
                .map(i -> instruction.getPadding() + 8 + (i * 8)) // Calculate the entry's offset in the instruction
                .mapToObj(offset -> new SwitchEntry(instruction.getInt(offset + 1), offset, instruction.getInt(offset + 5))) // Lookup key and jump values
                .map(entry -> String.format("%d:%+d[%d]", entry.key, entry.jump, instruction.getStart() + entry.jump)) // Convert the entry to text
                .collect(Collectors.joining(delimiter)));
    }

    protected Printer appendTableSwitch(Instruction instruction) {
        return appendTableSwitch(instruction, " ");
    }

    protected Printer appendTableSwitch(Instruction instruction, String delimiter) {
        return append(IntStream.rangeClosed(instruction.getLow(), instruction.getHigh())
                .mapToObj(key -> new SwitchEntry(key, instruction.getPadding() + 12 + ((key - instruction.getLow()) * 4), -1)) // Calculate the entry's offset in the instruction
                .map(partialEntry -> new SwitchEntry(partialEntry.key, partialEntry.offset, instruction.getInt(partialEntry.offset + 1))) // Lookup the jump value
                .map(entry -> String.format("%d:%+d[%d]", entry.key, entry.jump, instruction.getStart() + entry.jump)) // Convert the entry to text
                .collect(Collectors.joining(delimiter)));
    }

    protected void raiseIndent() {
        indentLevel++;
    }

    protected void lowerIndent() {
        indentLevel--;
    }
}
