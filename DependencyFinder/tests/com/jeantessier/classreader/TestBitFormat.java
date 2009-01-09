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

package com.jeantessier.classreader;

import junit.framework.*;

public class TestBitFormat extends TestCase {
    public void testDefault() {
        BitFormat format = new BitFormat();

        assertEquals("0",                 "00000000 00000000 00000000 00000000", format.format(0));
        assertEquals("1",                 "00000000 00000000 00000000 00000001", format.format(1));
        assertEquals("-1",                "11111111 11111111 11111111 11111111", format.format(-1));
        assertEquals("20",                "00000000 00000000 00000000 00010100", format.format(20));
        assertEquals("Byte.MAX_VALUE",    "00000000 00000000 00000000 01111111", format.format(Byte.MAX_VALUE));
        assertEquals("Byte.MIN_VALUE",    "11111111 11111111 11111111 10000000", format.format(Byte.MIN_VALUE));
        assertEquals("Short.MAX_VALUE",   "00000000 00000000 01111111 11111111", format.format(Short.MAX_VALUE));
        assertEquals("Short.MIN_VALUE",   "11111111 11111111 10000000 00000000", format.format(Short.MIN_VALUE));
        assertEquals("Integer.MAX_VALUE", "01111111 11111111 11111111 11111111", format.format(Integer.MAX_VALUE));
        assertEquals("Integer.MIN_VALUE", "10000000 00000000 00000000 00000000", format.format(Integer.MIN_VALUE));
    }

    public void testLength4() {
        BitFormat format = new BitFormat(4);

        assertEquals("0",                 "0000", format.format(0));
        assertEquals("1",                 "0001", format.format(1));
        assertEquals("-1",                "1111", format.format(-1));
        assertEquals("20",                "0100", format.format(20));
        assertEquals("Byte.MAX_VALUE",    "1111", format.format(Byte.MAX_VALUE));
        assertEquals("Byte.MIN_VALUE",    "0000", format.format(Byte.MIN_VALUE));
        assertEquals("Short.MAX_VALUE",   "1111", format.format(Short.MAX_VALUE));
        assertEquals("Short.MIN_VALUE",   "0000", format.format(Short.MIN_VALUE));
        assertEquals("Integer.MAX_VALUE", "1111", format.format(Integer.MAX_VALUE));
        assertEquals("Integer.MIN_VALUE", "0000", format.format(Integer.MIN_VALUE));
    }

    public void testLength16() {
        BitFormat format = new BitFormat(16);

        assertEquals("0",                 "00000000 00000000", format.format(0));
        assertEquals("1",                 "00000000 00000001", format.format(1));
        assertEquals("-1",                "11111111 11111111", format.format(-1));
        assertEquals("20",                "00000000 00010100", format.format(20));
        assertEquals("Byte.MAX_VALUE",    "00000000 01111111", format.format(Byte.MAX_VALUE));
        assertEquals("Byte.MIN_VALUE",    "11111111 10000000", format.format(Byte.MIN_VALUE));
        assertEquals("Short.MAX_VALUE",   "01111111 11111111", format.format(Short.MAX_VALUE));
        assertEquals("Short.MIN_VALUE",   "10000000 00000000", format.format(Short.MIN_VALUE));
        assertEquals("Integer.MAX_VALUE", "11111111 11111111", format.format(Integer.MAX_VALUE));
        assertEquals("Integer.MIN_VALUE", "00000000 00000000", format.format(Integer.MIN_VALUE));
    }

    public void testGroups8() {
        BitFormat format = new BitFormat(BitFormat.DEFAULT_MAX_LENGTH, 4);

        assertEquals("0",                 "0000 0000 0000 0000 0000 0000 0000 0000", format.format(0));
        assertEquals("1",                 "0000 0000 0000 0000 0000 0000 0000 0001", format.format(1));
        assertEquals("-1",                "1111 1111 1111 1111 1111 1111 1111 1111", format.format(-1));
        assertEquals("20",                "0000 0000 0000 0000 0000 0000 0001 0100", format.format(20));
        assertEquals("Byte.MAX_VALUE",    "0000 0000 0000 0000 0000 0000 0111 1111", format.format(Byte.MAX_VALUE));
        assertEquals("Byte.MIN_VALUE",    "1111 1111 1111 1111 1111 1111 1000 0000", format.format(Byte.MIN_VALUE));
        assertEquals("Short.MAX_VALUE",   "0000 0000 0000 0000 0111 1111 1111 1111", format.format(Short.MAX_VALUE));
        assertEquals("Short.MIN_VALUE",   "1111 1111 1111 1111 1000 0000 0000 0000", format.format(Short.MIN_VALUE));
        assertEquals("Integer.MAX_VALUE", "0111 1111 1111 1111 1111 1111 1111 1111", format.format(Integer.MAX_VALUE));
        assertEquals("Integer.MIN_VALUE", "1000 0000 0000 0000 0000 0000 0000 0000", format.format(Integer.MIN_VALUE));
    }

    public void testSeparator() {
        BitFormat format = new BitFormat(BitFormat.DEFAULT_MAX_LENGTH, BitFormat.DEFAULT_GROUP_SIZE, '-');

        assertEquals("0",                 "00000000-00000000-00000000-00000000", format.format(0));
        assertEquals("1",                 "00000000-00000000-00000000-00000001", format.format(1));
        assertEquals("-1",                "11111111-11111111-11111111-11111111", format.format(-1));
        assertEquals("20",                "00000000-00000000-00000000-00010100", format.format(20));
        assertEquals("Byte.MAX_VALUE",    "00000000-00000000-00000000-01111111", format.format(Byte.MAX_VALUE));
        assertEquals("Byte.MIN_VALUE",    "11111111-11111111-11111111-10000000", format.format(Byte.MIN_VALUE));
        assertEquals("Short.MAX_VALUE",   "00000000-00000000-01111111-11111111", format.format(Short.MAX_VALUE));
        assertEquals("Short.MIN_VALUE",   "11111111-11111111-10000000-00000000", format.format(Short.MIN_VALUE));
        assertEquals("Integer.MAX_VALUE", "01111111-11111111-11111111-11111111", format.format(Integer.MAX_VALUE));
        assertEquals("Integer.MIN_VALUE", "10000000-00000000-00000000-00000000", format.format(Integer.MIN_VALUE));
    }
}
