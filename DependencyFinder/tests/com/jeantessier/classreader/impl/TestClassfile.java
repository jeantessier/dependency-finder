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

import com.jeantessier.classreader.ClassfileLoader;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class TestClassfile {
    private static final String TEST_PACKAGE_NAME = "foo";
    private static final String TEST_CLASS_NAME = TEST_PACKAGE_NAME + ".Foo";
    private static final String TEST_FIELD_NAME = TEST_CLASS_NAME + ".foo";
    private static final String TEST_METHOD_SIGNATURE = TEST_CLASS_NAME + ".foo()";

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
    public void testGetPackageName() {
        final Class_info classInfo = context.mock(Class_info.class);

        context.checking(new Expectations() {{
            one (constantPool).get(1);
                will(returnValue(classInfo));
            one (classInfo).getPackageName();
                will(returnValue(TEST_PACKAGE_NAME));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), Collections.<Method_info>emptyList(), Collections.<Attribute_info>emptyList());

        String actualValue = sut.getPackageName();
        assertThat("package name", actualValue, is(TEST_PACKAGE_NAME));
    }

    @Test
    public void testLocateField_localField_succeed() throws Exception {
        final Field_info expectedField = context.mock(Field_info.class, "located field");

        Collection<Field_info> fields = Collections.singletonList(expectedField);

        context.checking(new Expectations() {{
            one (expectedField).getName();
                will(returnValue(TEST_FIELD_NAME));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), fields, Collections.<Method_info>emptyList(), Collections.<Attribute_info>emptyList());

        Field_info actualField = (Field_info) sut.locateField(TEST_FIELD_NAME);
        assertThat("local field", actualField, is(expectedField));
    }

    @Test
    public void testLocateField_publicInheritedField_succeed() throws Exception {
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Field_info expectedField = context.mock(Field_info.class, "located field");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            one (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            one (superclass).locateField(TEST_FIELD_NAME);
                will(returnValue(expectedField));
            one (expectedField).isPublic();
                will(returnValue(true));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), Collections.<Method_info>emptyList(), Collections.<Attribute_info>emptyList());

        Field_info actualField = (Field_info) sut.locateField(TEST_FIELD_NAME);
        assertThat("public field", actualField, is(expectedField));
    }

    @Test
    public void testLocateField_protectedInheritedField_succeed() throws Exception {
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Field_info expectedField = context.mock(Field_info.class, "located field");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            one (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            one (superclass).locateField(TEST_FIELD_NAME);
                will(returnValue(expectedField));
            one (expectedField).isPublic();
                will(returnValue(false));
            one (expectedField).isProtected();
                will(returnValue(true));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), Collections.<Method_info>emptyList(), Collections.<Attribute_info>emptyList());

        Field_info actualField = (Field_info) sut.locateField(TEST_FIELD_NAME);
        assertThat("protected field", actualField, is(expectedField));
    }

    @Test
    public void testLocateField_packageInheritedField_succeed() throws Exception {
        final Class_info classInfo = context.mock(Class_info.class, "class info");
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Field_info expectedField = context.mock(Field_info.class, "located field");

        final Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), Collections.<Method_info>emptyList(), Collections.<Attribute_info>emptyList());

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            one (constantPool).get(1);
                will(returnValue(classInfo));
            one (classInfo).getPackageName();
                will(returnValue(TEST_PACKAGE_NAME));
            one (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            one (superclass).getPackageName();
                will(returnValue(TEST_PACKAGE_NAME));
            one (superclass).locateField(TEST_FIELD_NAME);
                will(returnValue(expectedField));
            one (expectedField).isPublic();
                will(returnValue(false));
            one (expectedField).isProtected();
                will(returnValue(false));
            one (expectedField).isPackage();
                will(returnValue(true));
            one (expectedField).getClassfile();
                will(returnValue(sut));
        }});

        Field_info actualField = (Field_info) sut.locateField(TEST_FIELD_NAME);
        assertThat("package field", actualField, is(expectedField));
    }

    @Test
    public void testLocateField_packageInheritedField_fail() throws Exception {
        final Class_info classInfo = context.mock(Class_info.class, "class info");
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Field_info expectedField = context.mock(Field_info.class, "located field");

        final Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), Collections.<Method_info>emptyList(), Collections.<Attribute_info>emptyList());

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            one (constantPool).get(1);
                will(returnValue(classInfo));
            one (classInfo).getPackageName();
                will(returnValue(TEST_PACKAGE_NAME));
            one (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            one (superclass).getPackageName();
                will(returnValue(""));
            one (superclass).locateField(TEST_FIELD_NAME);
                will(returnValue(expectedField));
            one (expectedField).isPublic();
                will(returnValue(false));
            one (expectedField).isProtected();
                will(returnValue(false));
            one (expectedField).isPackage();
                will(returnValue(true));
            one (expectedField).getClassfile();
                will(returnValue(sut));
        }});

        Field_info actualField = (Field_info) sut.locateField(TEST_FIELD_NAME);
        assertThat("package field", actualField, is(nullValue()));
    }

    @Test
    public void testLocateField_privateInheritedField_fail() throws Exception {
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Field_info expectedField = context.mock(Field_info.class, "located field");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            one (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            one (superclass).locateField(TEST_FIELD_NAME);
                will(returnValue(expectedField));
            one (expectedField).isPublic();
                will(returnValue(false));
            one (expectedField).isProtected();
                will(returnValue(false));
            one (expectedField).isPackage();
                will(returnValue(false));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), Collections.<Method_info>emptyList(), Collections.<Attribute_info>emptyList());

        Field_info actualField = (Field_info) sut.locateField(TEST_FIELD_NAME);
        assertThat("local field", actualField, is(nullValue()));
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
        assertThat("local method", actualMethod, is(expectedMethod));
    }

    @Test
    public void testLocateMethod_publicInheritedMethod_succeed() throws Exception {
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Method_info expectedMethod = context.mock(Method_info.class, "located method");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            one (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            one (superclass).locateMethod(TEST_METHOD_SIGNATURE);
                will(returnValue(expectedMethod));
            one (expectedMethod).isPublic();
                will(returnValue(true));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), Collections.<Method_info>emptyList(), Collections.<Attribute_info>emptyList());

        Method_info actualMethod = (Method_info) sut.locateMethod(TEST_METHOD_SIGNATURE);
        assertThat("public method", actualMethod, is(expectedMethod));
    }

    @Test
    public void testLocateMethod_protectedInheritedMethod_succeed() throws Exception {
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Method_info expectedMethod = context.mock(Method_info.class, "located method");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
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
        assertThat("protected method", actualMethod, is(expectedMethod));
    }

    @Test
    public void testLocateMethod_packageInheritedMethod_succeed() throws Exception {
        final Class_info classInfo = context.mock(Class_info.class, "class info");
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Method_info expectedMethod = context.mock(Method_info.class, "located method");

        final Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), Collections.<Method_info>emptyList(), Collections.<Attribute_info>emptyList());

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            one (constantPool).get(1);
                will(returnValue(classInfo));
            one (classInfo).getPackageName();
                will(returnValue(TEST_PACKAGE_NAME));
            one (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            one (superclass).getPackageName();
                will(returnValue(TEST_PACKAGE_NAME));
            one (superclass).locateMethod(TEST_METHOD_SIGNATURE);
                will(returnValue(expectedMethod));
            one (expectedMethod).isPublic();
                will(returnValue(false));
            one (expectedMethod).isProtected();
                will(returnValue(false));
            one (expectedMethod).isPackage();
                will(returnValue(true));
            one (expectedMethod).getClassfile();
                will(returnValue(sut));
        }});

        Method_info actualMethod = (Method_info) sut.locateMethod(TEST_METHOD_SIGNATURE);
        assertThat("package method", actualMethod, is(expectedMethod));
    }

    @Test
    public void testLocateMethod_packageInheritedMethod_fail() throws Exception {
        final Class_info classInfo = context.mock(Class_info.class, "class info");
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Method_info expectedMethod = context.mock(Method_info.class, "located method");

        final Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), Collections.<Method_info>emptyList(), Collections.<Attribute_info>emptyList());

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            one (constantPool).get(1);
                will(returnValue(classInfo));
            one (classInfo).getPackageName();
                will(returnValue(TEST_PACKAGE_NAME));
            one (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            one (superclass).getPackageName();
                will(returnValue(""));
            one (superclass).locateMethod(TEST_METHOD_SIGNATURE);
                will(returnValue(expectedMethod));
            one (expectedMethod).isPublic();
                will(returnValue(false));
            one (expectedMethod).isProtected();
                will(returnValue(false));
            one (expectedMethod).isPackage();
                will(returnValue(true));
            one (expectedMethod).getClassfile();
                will(returnValue(sut));
        }});

        Method_info actualMethod = (Method_info) sut.locateMethod(TEST_METHOD_SIGNATURE);
        assertThat("package method", actualMethod, is(nullValue()));
    }

    @Test
    public void testLocateMethod_privateInheritedMethod_fail() throws Exception {
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Method_info expectedMethod = context.mock(Method_info.class, "located method");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            one (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            one (superclass).locateMethod(TEST_METHOD_SIGNATURE);
                will(returnValue(expectedMethod));
            one (expectedMethod).isPublic();
                will(returnValue(false));
            one (expectedMethod).isProtected();
                will(returnValue(false));
            one (expectedMethod).isPackage();
                will(returnValue(false));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), Collections.<Method_info>emptyList(), Collections.<Attribute_info>emptyList());

        Method_info actualMethod = (Method_info) sut.locateMethod(TEST_METHOD_SIGNATURE);
        assertThat("private method", actualMethod, is(nullValue()));
    }

    @Test
    public void testIsInnerClass_matchingInnerClassInfo_returnsTrue() throws Exception {
        final InnerClasses_attribute innerClasses_attribute = context.mock(InnerClasses_attribute.class);
        final InnerClass innerClass = context.mock(InnerClass.class);

        expectClassNameLookup(1, TEST_CLASS_NAME);

        context.checking(new Expectations() {{
            one (innerClasses_attribute).getInnerClasses();
                will(returnValue(Collections.<InnerClass>singleton(innerClass)));
            one (innerClass).getInnerClassInfo();
                will(returnValue(TEST_CLASS_NAME));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), Collections.<Method_info>emptyList(), Collections.<Attribute_info>singleton(innerClasses_attribute));

        assertThat("inner class", sut.isInnerClass(), is(true));
    }

    @Test
    public void testIsInnerClass_emptyInnerClassInfo_returnsFalse() throws Exception {
        final InnerClasses_attribute innerClasses_attribute = context.mock(InnerClasses_attribute.class);
        final InnerClass innerClass = context.mock(InnerClass.class);

        expectClassNameLookup(1, TEST_CLASS_NAME);

        context.checking(new Expectations() {{
            one (innerClasses_attribute).getInnerClasses();
                will(returnValue(Collections.<InnerClass>singleton(innerClass)));
            one (innerClass).getInnerClassInfo();
                will(returnValue(""));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), Collections.<Method_info>emptyList(), Collections.<Attribute_info>singleton(innerClasses_attribute));

        assertThat("inner class", sut.isInnerClass(), is(false));
    }

    @Test
    public void testIsInnerClass_noInnerClassInfo_returnsFalse() throws Exception {
        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.<Class_info>emptyList(), Collections.<Field_info>emptyList(), Collections.<Method_info>emptyList(), Collections.<Attribute_info>emptyList());

        assertThat("inner class", sut.isInnerClass(), is(false));
    }

    private void expectClassNameLookup(final int index, final String value) {
        final Class_info class_info = context.mock(Class_info.class);

        context.checking(new Expectations() {{
            one (constantPool).get(index);
                will(returnValue(class_info));
            one (class_info).getName();
                will(returnValue(value));
        }});
    }
}
