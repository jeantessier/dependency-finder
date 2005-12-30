package com.jeantessier.dependency;

import java.io.*;

import junit.framework.*;

public class TestNodeNamePrinter extends TestCase {
    public void testPrintPackageName() {
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);
        Visitor visitor = new NodeNamePrinter(out);

        visitor.visitPackageNode(new PackageNode("foobar", true));

        assertEquals("package foobar", buffer.toString());
    }
}
