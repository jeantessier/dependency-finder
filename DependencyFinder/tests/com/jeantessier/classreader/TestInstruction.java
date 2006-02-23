/*
 *  Copyright (c) 2001-2006, Jean Tessier
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

import junit.framework.*;

public class TestInstruction extends TestCase {
    private Instruction instruction1;
    private Instruction instruction2;
    private Instruction instruction3;
    private Instruction instruction4;
    private Instruction instruction5;
    private Instruction instruction6;

    protected void setUp() throws Exception {
        super.setUp();

        byte[] code1 = new byte[] {(byte) 0xb6, (byte) 0x00, (byte) 0xFF};
        byte[] code2 = new byte[] {(byte) 0x13, (byte) 0x01, (byte) 0xCA};
        byte[] code3 = new byte[] {(byte) 0xb6, (byte) 0x00, (byte) 0xFF, (byte) 0x13, (byte) 0x01, (byte) 0xCA};
        byte[] code4 = new byte[] {(byte) 0x13, (byte) 0x01, (byte) 0x74};
        byte[] code5 = new byte[] {(byte) 0xB1};

        instruction1 = new Instruction(code1, 0);
        instruction2 = new Instruction(code2, 0);
        instruction3 = new Instruction(code3, 3);
        instruction4 = new Instruction(code3, 0);
        instruction5 = new Instruction(code4, 0);
        instruction6 = new Instruction(code5, 0);
    }

    public void testEquals() {
        assertEquals("same", instruction1, instruction1);
        assertEquals("identical", instruction1, instruction4);
        assertFalse("different opcode", instruction1.equals(instruction2));
        assertEquals("offset", instruction2, instruction3);
        assertFalse("different index", instruction2.equals(instruction5));
        assertFalse("different size", instruction1.equals(instruction6));
    }
}
