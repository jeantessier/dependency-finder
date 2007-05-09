package com.jeantessier.classreader;

import java.io.*;
import java.util.*;

import junit.framework.*;

public class TestEnum extends TestCase {
    public static final String TEST_CLASS = "test";
    public static final String TEST_ENUM_CLASS = "testenum";
    public static final String TEST_ENUM_VALUE1_CLASS = "testenum$1";
    public static final String TEST_ENUM_VALUE2_CLASS = "testenum$2";
    public static final String TEST_FILENAME = "classes" + File.separator + TEST_CLASS + ".class";
    public static final String TEST_ENUM_FILENAME = "classes" + File.separator + TEST_ENUM_CLASS + ".class";
    public static final String TEST_ENUM_VALUE1_FILENAME = "classes" + File.separator + TEST_ENUM_VALUE1_CLASS + ".class";
    public static final String TEST_ENUM_VALUE2_FILENAME = "classes" + File.separator + TEST_ENUM_VALUE2_CLASS + ".class";

    private ClassfileLoader loader;

    protected void setUp() throws Exception {
        super.setUp();

        loader   = new AggregatingClassfileLoader();

        loader.load(Collections.singleton(TEST_FILENAME));
        loader.load(Collections.singleton(TEST_ENUM_FILENAME));
        loader.load(Collections.singleton(TEST_ENUM_VALUE1_FILENAME));
        loader.load(Collections.singleton(TEST_ENUM_VALUE2_FILENAME));
    }

    public void testIsEnum() {
        assertTrue(TEST_CLASS, !loader.getClassfile(TEST_CLASS).isEnum());
        assertTrue(TEST_ENUM_CLASS, loader.getClassfile(TEST_ENUM_CLASS).isEnum());
        assertTrue("testenum.VALUE1", loader.getClassfile(TEST_ENUM_VALUE1_CLASS).isEnum());
        assertTrue("testenum.VALUE2", loader.getClassfile(TEST_ENUM_VALUE2_CLASS).isEnum());
    }
}
