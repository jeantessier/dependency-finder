package com.jeantessier.classreader;

import java.io.*;
import java.util.*;

import junit.framework.*;

public class TestAnnotation extends TestCase {
    public static final String TEST_CLASS = "test";
    public static final String TEST_ANNOTATION_CLASS = "testannotation";
    public static final String TEST_FILENAME = "classes" + File.separator + TEST_CLASS + ".class";
    public static final String TEST_ANNOTATION_FILENAME = "classes" + File.separator + TEST_ANNOTATION_CLASS + ".class";

    private ClassfileLoader loader;

    protected void setUp() throws Exception {
        super.setUp();

        loader   = new AggregatingClassfileLoader();

        loader.load(Collections.singleton(TEST_FILENAME));
        loader.load(Collections.singleton(TEST_ANNOTATION_FILENAME));
    }

    public void testIsEnum() {
        assertTrue(TEST_CLASS, !loader.getClassfile(TEST_CLASS).isAnnotation());
        assertTrue(TEST_ANNOTATION_CLASS, loader.getClassfile(TEST_ANNOTATION_CLASS).isAnnotation());
    }
}
