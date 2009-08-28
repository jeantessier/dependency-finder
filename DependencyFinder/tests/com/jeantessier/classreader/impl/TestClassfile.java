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

import java.util.*;

import static org.hamcrest.Matchers.*;
import org.jmock.*;
import org.jmock.integration.junit4.*;
import org.jmock.lib.legacy.*;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.*;

import com.jeantessier.classreader.*;

@RunWith(JMock.class)
public class TestClassfile {
    private static final String TEST_METHOD_SIGNATURE = "foo.Foo.foo()";

    private Mockery context;

    private ClassfileLoader loader;
    private ConstantPool constantPool;

    @Before
    public void setUp() throws Exception {
        context = new Mockery();
        context.setImposteriser(ClassImposteriser.INSTANCE);

        loader = context.mock(ClassfileLoader.class);
        constantPool = context.mock(ConstantPool.class);
    }

    @Test
    public void testLocateMethod_localMethod_succeed() throws Exception {
        final Method_info expectedMethod = context.mock(Method_info.class, "located method");

        Collection<Method_info> methods = Collections.singletonList(expectedMethod);

        context.checking(new Expectations() {{
            one (expectedMethod).getSignature();
                will(returnValue(TEST_METHOD_SIGNATURE));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), methods, Collections.<Attribute_info>emptyList());

        Method_info actualMethod = (Method_info) sut.locateMethod(TEST_METHOD_SIGNATURE);
        assertThat("", actualMethod, is(expectedMethod));
    }

    @Test
    public void testLocateMethod_publicInheritedMethod_succeed() throws Exception {
        final String superclassName = "superclassInfo";
        final Class_info superclassInfo = context.mock(Class_info.class, "superclassInfo info");
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Method_info expectedMethod = context.mock(Method_info.class, "located method");

        context.checking(new Expectations() {{
            one (constantPool).get(2);
                will(returnValue(superclassInfo));
            one (superclassInfo).getName();
                will(returnValue(superclassName));
            one (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            one (superclass).locateMethod(TEST_METHOD_SIGNATURE);
                will(returnValue(expectedMethod));
            one (expectedMethod).isPublic();
                will(returnValue(true));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), Collections.<Method_info>emptyList(), Collections.<Attribute_info>emptyList());

        Method_info actualMethod = (Method_info) sut.locateMethod(TEST_METHOD_SIGNATURE);
        assertThat("", actualMethod, is(expectedMethod));
    }

    @Test
    public void testLocateMethod_protectedInheritedMethod_succeed() throws Exception {
        final String superclassName = "superclassInfo";
        final Class_info superclassInfo = context.mock(Class_info.class, "superclassInfo info");
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Method_info expectedMethod = context.mock(Method_info.class, "located method");

        context.checking(new Expectations() {{
            one (constantPool).get(2);
                will(returnValue(superclassInfo));
            one (superclassInfo).getName();
                will(returnValue(superclassName));
            one (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            one (superclass).locateMethod(TEST_METHOD_SIGNATURE);
                will(returnValue(expectedMethod));
            one (expectedMethod).isPublic();
                will(returnValue(false));
            one (expectedMethod).isProtected();
                will(returnValue(true));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), Collections.<Method_info>emptyList(), Collections.<Attribute_info>emptyList());

        Method_info actualMethod = (Method_info) sut.locateMethod(TEST_METHOD_SIGNATURE);
        assertThat("", actualMethod, is(expectedMethod));
    }

    @Test
    public void testLocateMethod_privateInheritedMethod_fail() throws Exception {
        final String superclassName = "superclassInfo";
        final Class_info superclassInfo = context.mock(Class_info.class, "superclassInfo info");
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Method_info expectedMethod = context.mock(Method_info.class, "located method");

        context.checking(new Expectations() {{
            one (constantPool).get(2);
                will(returnValue(superclassInfo));
            one (superclassInfo).getName();
                will(returnValue(superclassName));
            one (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            one (superclass).locateMethod(TEST_METHOD_SIGNATURE);
                will(returnValue(expectedMethod));
            one (expectedMethod).isPublic();
                will(returnValue(false));
            one (expectedMethod).isProtected();
                will(returnValue(false));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), Collections.<Method_info>emptyList(), Collections.<Attribute_info>emptyList());

        Method_info actualMethod = (Method_info) sut.locateMethod(TEST_METHOD_SIGNATURE);
        assertThat("", actualMethod, is(nullValue()));
    }
}
