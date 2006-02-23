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

package com.jeantessier.text;

import java.io.*;

import junit.framework.*;

public class TestHex extends TestCase {
    private StringWriter sw;
    private PrintWriter pw;

    protected void setUp() throws Exception {
        super.setUp();

        sw = new StringWriter();
        pw = new PrintWriter(sw);
    }

    public void testPrintNull() {
        try {
            Hex.print(pw, null);
            fail("Printed null byte array");
        } catch (NullPointerException ex) {
            // Expected
        }
    }

    public void testPrintEmpty() {
        Hex.print(pw, new byte[0]);
        pw.close();
        assertEquals("", sw.toString());
    }

    public void testPrintOneByte() {
        Hex.print(pw, new byte[] {0});
        pw.close();
        assertEquals("00", sw.toString());
    }

    public void testPrintFourBits() {
        Hex.print(pw, new byte[] {7});
        pw.close();
        assertEquals("07", sw.toString());
    }

    public void testPrintGeneratesCapitals() {
        Hex.print(pw, new byte[] {10});
        pw.close();
        assertEquals("0A", sw.toString());
    }

    public void testPrintEightBits() {
        Hex.print(pw, new byte[] {(byte) 255});
        pw.close();
        assertEquals("FF", sw.toString());
    }

    public void testPrintTwoBytes() {
        Hex.print(pw, new byte[] {0, 1});
        pw.close();
        assertEquals("0001", sw.toString());
    }

    public void testNullToString() {
        try {
            Hex.toString(null);
            fail("Printed null byte array");
        } catch (NullPointerException ex) {
            // Expected
        }
    }

    public void testEmptyToString() {
        assertEquals("", Hex.toString(new byte[0]));
    }

    public void testOneByteToString() {
        assertEquals("00", Hex.toString(new byte[] {0}));
    }

    public void testFourBitsToString() {
        assertEquals("07", Hex.toString(new byte[] {7}));
    }

    public void testToStringGeneratesCapitals() {
        assertEquals("0A", Hex.toString(new byte[] {10}));
    }

    public void testEightBitsToString() {
        assertEquals("FF", Hex.toString(new byte[] {(byte) 255}));
    }

    public void testTwoBytesToString() {
        assertEquals("0001", Hex.toString(new byte[] {0, 1}));
    }
}
