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

package com.jeantessier.classreader.impl;

import fit.ColumnFixture;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class InstructionFixture extends ColumnFixture {
    private static final int PADDING_BYTE = 0xA;
    private static final int PADDING_LENGTH = 16;

    public String opCode;
    public String data;

    public String mnemonic() {
        return new Instruction(null, buildDataBytes(), PADDING_LENGTH).getMnemonic();
    }

    public int length() {
        return new Instruction(null, buildDataBytes(), PADDING_LENGTH).getLength();
    }

    public int index() {
        return new Instruction(null, buildDataBytes(), PADDING_LENGTH).getIndex();
    }

    public int offset() {
        return new Instruction(null, buildDataBytes(), PADDING_LENGTH).getOffset();
    }

    public int value() {
        return new Instruction(null, buildDataBytes(), PADDING_LENGTH).getValue();
    }

    private byte[] buildDataBytes() {
        byte[] result;

        List<Integer> bytes = new ArrayList<Integer>();
        for (int i=0; i<PADDING_LENGTH; i++) {
            bytes.add(PADDING_BYTE);
        }
        bytes.add(Integer.parseInt(opCode, 16));

        try {
            StringTokenizer tokens = new StringTokenizer(data);
            while (tokens.hasMoreTokens()) {
                bytes.add(Integer.parseInt(tokens.nextToken(), 16));
            }
        } catch (NumberFormatException e) {
            // Ignore
        }

        result = new byte[bytes.size()];
        int i = 0;
        for (int b : bytes) {
            result[i++] = (byte) b;
        }

        return result;
    }
}
