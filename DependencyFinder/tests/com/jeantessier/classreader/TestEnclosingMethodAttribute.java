package com.jeantessier.classreader;

import java.io.*;
import java.util.*;

import junit.framework.*;

public class TestEnclosingMethodAttribute extends TestCase {
    public static final String TEST_ENUM_CLASS = "testenum";
    public static final String TEST_ENUM_VALUE1_CLASS = "testenum$1";
    public static final String TEST_ENUM_FILENAME = "classes" + File.separator + TEST_ENUM_CLASS + ".class";
    public static final String TEST_ENUM_VALUE1_FILENAME = "classes" + File.separator + TEST_ENUM_VALUE1_CLASS + ".class";

    private ClassfileLoader loader;

    protected void setUp() throws Exception {
        super.setUp();

        loader = new AggregatingClassfileLoader();

        loader.load(Collections.singleton(TEST_ENUM_FILENAME));
        loader.load(Collections.singleton(TEST_ENUM_VALUE1_FILENAME));
    }

    public void testDoesNotHaveEnclosingMethodAttribute() {
        Classfile enumClass = loader.getClassfile(TEST_ENUM_CLASS);

        for (Attribute_info attribute : enumClass.getAttributes()) {
            assertFalse("EnclosingMethod attribute present", attribute instanceof EnclosingMethod_attribute);
        }
    }

    public void testHasEnclosingMethodAttribute() {
        Classfile enumValue1Class = loader.getClassfile(TEST_ENUM_VALUE1_CLASS);

        EnclosingMethod_attribute enclosingMethodAttribute = null;
        for (Attribute_info attribute : enumValue1Class.getAttributes()) {
            if (attribute instanceof EnclosingMethod_attribute) {
                enclosingMethodAttribute = (EnclosingMethod_attribute) attribute;

            }
        }

        assertNotNull("EnclosingMethod attribute missing", enclosingMethodAttribute);
    }
}
