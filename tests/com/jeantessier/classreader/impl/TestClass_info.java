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

package com.jeantessier.classreader.impl;

import static org.hamcrest.Matchers.*;
import org.jmock.*;
import org.jmock.integration.junit4.*;
import org.jmock.lib.legacy.*;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.*;

@RunWith(JMock.class)
public class TestClass_info {
    private static final int CLASS_NAME_INDEX = 1;
    private static final String PACKAGE_NAME = "foo";
    private static final String SIMPLE_CLASS_NAME = "Foo";
    private static final String ENCODED_SIMPLE_CLASS_NAME = "L" + SIMPLE_CLASS_NAME + ";";
    private static final String ENCODED_FULL_CLASS_NAME = "L" + PACKAGE_NAME + "/" + SIMPLE_CLASS_NAME + ";";
    private static final String FULL_CLASS_NAME = PACKAGE_NAME + "." + SIMPLE_CLASS_NAME;

    private Mockery context;

    private ConstantPool constantPool;

    private Class_info sut;

    @Before
    public void setUp() throws Exception {
        context = new Mockery();
        context.setImposteriser(ClassImposteriser.INSTANCE);

        constantPool = context.mock(ConstantPool.class);

        sut = new Class_info(constantPool, CLASS_NAME_INDEX);
    }

    @Test
    public void testGetName_defaultPackage() {
        expectClassNameLookup(CLASS_NAME_INDEX, ENCODED_SIMPLE_CLASS_NAME);

        String actualValue = sut.getName();
        assertThat("full name", actualValue, is(SIMPLE_CLASS_NAME));
    }

    @Test
    public void testGetName_withPackageName() {
        expectClassNameLookup(CLASS_NAME_INDEX, ENCODED_FULL_CLASS_NAME);

        String actualValue = sut.getName();
        assertThat("full name", actualValue, is(FULL_CLASS_NAME));
    }

    @Test
    public void testGetPackageName_defaultPackage() {
        expectClassNameLookup(CLASS_NAME_INDEX, ENCODED_SIMPLE_CLASS_NAME);

        String actualValue = sut.getPackageName();
        assertThat("package", actualValue, is(""));
    }

    @Test
    public void testGetPackageName_withPackageName() {
        expectClassNameLookup(CLASS_NAME_INDEX, ENCODED_FULL_CLASS_NAME);

        String actualValue = sut.getPackageName();
        assertThat("package", actualValue, is(PACKAGE_NAME));
    }

    @Test
    public void testGetSimpleName_defaultPackage() {
        expectClassNameLookup(CLASS_NAME_INDEX, ENCODED_SIMPLE_CLASS_NAME);

        String actualValue = sut.getSimpleName();
        assertThat("simple name", actualValue, is(SIMPLE_CLASS_NAME));
    }

    @Test
    public void testGetSimpleName_withPackageName() {
        expectClassNameLookup(CLASS_NAME_INDEX, ENCODED_FULL_CLASS_NAME);

        String actualValue = sut.getSimpleName();
        assertThat("simple name", actualValue, is(SIMPLE_CLASS_NAME));
    }

    private void expectClassNameLookup(final int index, final String value) {
        final UTF8_info utf8_info = context.mock(UTF8_info.class);

        context.checking(new Expectations() {{
            one (constantPool).get(index);
                will(returnValue(utf8_info));
            one (utf8_info).getValue();
                will(returnValue(value));
        }});
    }
}
