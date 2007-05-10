package com.jeantessier.classreader;

import java.io.*;
import java.util.*;

import junit.framework.*;

public class TestInstructionWithConstantPool extends TestCase {
    private static final String TEST_LDC_CLASS = "testldc";
    private static final String TEST_LDC_FILENAME = "classes" + File.separator + TEST_LDC_CLASS + ".class";

    private byte[] bytecode;
    private Instruction ldc;

    protected void setUp() throws Exception {
        ClassfileLoader loader = new AggregatingClassfileLoader();
        loader.load(Collections.singleton(TEST_LDC_FILENAME));

        // Getting an arbitrary Code_attribute
        Classfile classfile = loader.getClassfile(TEST_LDC_CLASS);
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
