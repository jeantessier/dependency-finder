/*
 *  Copyright (c) 2001-2007, Jean Tessier
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

import java.io.*;
import java.util.*;

import junit.framework.*;

import com.jeantessier.classreader.*;

public class TestInstructionWithConstantPool extends TestCase {
    private static final String TEST_LDC_CLASS = "testldc";
    private static final String TEST_LDC_FILENAME = "classes" + File.separator + TEST_LDC_CLASS + ".class";

    private byte[] bytecode;
    private Instruction ldc;

    protected void setUp() throws Exception {
        ClassfileLoader loader = new AggregatingClassfileLoader();
        loader.load(Collections.singleton(TEST_LDC_FILENAME));

        // Getting an arbitrary Code_attribute
        Classfile classfile = (Classfile) loader.getClassfile(TEST_LDC_CLASS);
        Method_info method = classfile.getMethod(TEST_LDC_CLASS + "()");
        Code_attribute code = method.getCode();

        bytecode = new byte[] {(byte) 0x12, (byte) 0x00};

        ldc = new Instruction(code, bytecode, 0);
    }

    public void testLdcWithIntConstant() {
        bytecode[1] = 0x09;

        Integer_info constant = (Integer_info) ldc.getIndexedConstantPoolEntry();
        assertNotNull(constant);
    }

    public void testLdcWithStringConstant() {
        bytecode[1] = 0x06;

        UTF8_info constant = (UTF8_info) ldc.getIndexedConstantPoolEntry();
        assertNotNull(constant);
    }

    public void testLdcWithClassConstant() {
        bytecode[1] = 0x02;

        Class_info constant = (Class_info) ldc.getIndexedConstantPoolEntry();
        assertNotNull(constant);
    }
}
