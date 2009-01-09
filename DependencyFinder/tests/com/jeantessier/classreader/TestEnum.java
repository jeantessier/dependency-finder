/*
 *  Copyright (c) 2001-2009, Jean Tessier
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

    public void testClassfileIsEnum() {
        assertTrue(TEST_CLASS, !loader.getClassfile(TEST_CLASS).isEnum());
        assertTrue(TEST_ENUM_CLASS, loader.getClassfile(TEST_ENUM_CLASS).isEnum());
        assertTrue("testenum.VALUE1", loader.getClassfile(TEST_ENUM_VALUE1_CLASS).isEnum());
        assertTrue("testenum.VALUE2", loader.getClassfile(TEST_ENUM_VALUE2_CLASS).isEnum());
    }

    public void testFieldIsEnum() {
        Classfile testenum = loader.getClassfile(TEST_ENUM_CLASS);

        assertTrue(TEST_ENUM_CLASS + ".$VALUES", !testenum.getField("$VALUES").isEnum());
        assertTrue(TEST_ENUM_CLASS + ".VALUE1", testenum.getField("VALUE1").isEnum());
    }

    public void testInnerClassIsEnum() {
        Classfile testenum = loader.getClassfile(TEST_ENUM_CLASS);

        boolean found = false;
        for (Attribute_info attribute : testenum.getAttributes()) {
            if (attribute instanceof InnerClasses_attribute) {
                found = true;
                for (InnerClass innerClass : ((InnerClasses_attribute) attribute).getInnerClasses()) {
                    assertTrue(innerClass.getInnerClassInfo(), innerClass.isEnum());
                }
            }
        }

        assertTrue("Did not find any InnerClass structures in " + TEST_ENUM_CLASS, found);
    }
}
