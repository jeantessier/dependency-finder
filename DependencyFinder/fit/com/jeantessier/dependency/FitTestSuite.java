package com.jeantessier.dependency;

import java.io.*;

import junit.framework.*;

public class FitTestSuite extends com.jeantessier.fit.FitTestSuite {
    public FitTestSuite(File inDir, File outDir) {
        super(inDir, outDir);
    }

    public static TestSuite suite() {
        return suite("dependency");
    }
}
