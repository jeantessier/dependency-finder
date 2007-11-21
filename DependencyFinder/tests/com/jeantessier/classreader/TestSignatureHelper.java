package com.jeantessier.classreader;

import junit.framework.*;

public class TestSignatureHelper extends TestCase {
    public void testGetSignature() {
        assertEquals("(T)V", SignatureHelper.getSignature("<T:Ljava/lang/Object;>(TT;)V"));
        assertEquals("(T, int, java.lang.String)V", SignatureHelper.getSignature("<T:Ljava/lang/Object;>(TT;ILjava/lang/String;)V"));
//        assertEquals("(int)", SignatureHelper.getSignature("(I)V"));
//        assertEquals("(int, int)", SignatureHelper.getSignature("(II)V"));
//        assertEquals("(int[])", SignatureHelper.getSignature("([I)V"));
//        assertEquals("(java.lang.Object)", SignatureHelper.getSignature("(Ljava/lang/Object;)V"));
//        assertEquals("(java.lang.Object[])", SignatureHelper.getSignature("([Ljava/lang/Object;)V"));
    }
}
