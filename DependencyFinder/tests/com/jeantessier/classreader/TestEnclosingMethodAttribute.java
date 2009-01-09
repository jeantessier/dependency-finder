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
