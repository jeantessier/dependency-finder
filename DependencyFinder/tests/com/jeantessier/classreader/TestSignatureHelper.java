package com.jeantessier.classreader;

import junit.framework.*;

public class TestSignatureHelper extends TestCase {
    public void testConvert() {
        assertEquals("int", SignatureHelper.convert("I"));
        assertEquals("int[]", SignatureHelper.convert("[I"));
//        assertEquals(null, SignatureHelper.convert("package/Class"));
        assertEquals("package.Class", SignatureHelper.convert("Lpackage/Class;"));
        assertEquals("package.Class[]", SignatureHelper.convert("[Lpackage/Class;"));
//        assertEquals(null, SignatureHelper.convert("List"));
    }

    public void testConvertClassName() {
        assertEquals("I", SignatureHelper.convertClassName("I"));
        assertEquals("int[]", SignatureHelper.convertClassName("[I"));
        assertEquals("package.Class", SignatureHelper.convertClassName("package/Class"));
        assertEquals("package.Class", SignatureHelper.convertClassName("Lpackage/Class;"));
        assertEquals("package.Class[]", SignatureHelper.convertClassName("[Lpackage/Class;"));
        assertEquals("List", SignatureHelper.convertClassName("List"));
    }
}
