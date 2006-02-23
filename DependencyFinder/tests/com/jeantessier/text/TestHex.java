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
