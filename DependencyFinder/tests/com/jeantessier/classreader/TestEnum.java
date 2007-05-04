package com.jeantessier.classreader;

import java.io.*;
import java.util.*;

import junit.framework.*;

public class TestEnum extends TestCase {
    public static final String TEST_CLASS = "test";
    public static final String TEST_ENUM_CLASS = "TestEnum";
    public static final String TEST_ENUM_VALUE1_CLASS = "TestEnum$1";
    public static final String TEST_ENUM_VALUE2_CLASS = "TestEnum$2";
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
        assertTrue("test", !loader.getClassfile("test").isEnum());
        assertTrue("TestEnum", loader.getClassfile("TestEnum").isEnum());
        assertTrue("TestEnum.VALUE1", loader.getClassfile("TestEnum$1").isEnum());
        assertTrue("TestEnum.VALUE2", loader.getClassfile("TestEnum$2").isEnum());
    }
}
