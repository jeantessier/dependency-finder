/*
 *  Copyright (c) 2001-2023, Jean Tessier
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
import org.jmock.*;
import org.jmock.imposters.*;
import org.jmock.integration.junit4.*;
import org.junit.*;

import java.util.*;

import static org.junit.Assert.*;

public class TestClassfile {
    private static final String TEST_PACKAGE_NAME = "foo";
    private static final String TEST_CLASS_NAME = TEST_PACKAGE_NAME + ".Foo";
    private static final String TEST_FIELD_NAME = TEST_CLASS_NAME + ".foo";
    private static final String TEST_METHOD_SIGNATURE = TEST_CLASS_NAME + ".foo()";

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private ClassfileLoader loader;
    private ConstantPool constantPool;

    @Before
    public void setUp() {
        loader = context.mock(ClassfileLoader.class);
        constantPool = context.mock(ConstantPool.class);
    }

    @Test
    public void testGetPackageName() {
        final Class_info classInfo = context.mock(Class_info.class);

        context.checking(new Expectations() {{
            oneOf (constantPool).get(1);
                will(returnValue(classInfo));
            oneOf (classInfo).getPackageName();
                will(returnValue(TEST_PACKAGE_NAME));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        String actualValue = sut.getPackageName();
        assertEquals("package name", actualValue, TEST_PACKAGE_NAME);
    }

    @Test
    public void testLocateField_localField_succeed() {
        final Field_info expectedField = context.mock(Field_info.class, "located field");

        Collection<Field_info> fields = Collections.singletonList(expectedField);

        context.checking(new Expectations() {{
            oneOf (expectedField).getName();
                will(returnValue(TEST_FIELD_NAME));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), fields, Collections.emptyList(), Collections.emptyList());

        Field_info actualField = (Field_info) sut.locateField(TEST_FIELD_NAME);
        assertEquals("local field", actualField, expectedField);
    }

    @Test
    public void testLocateField_publicInheritedField_succeed() {
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Field_info expectedField = context.mock(Field_info.class, "located field");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).locateField(TEST_FIELD_NAME);
                will(returnValue(expectedField));
            oneOf (expectedField).isPublic();
                will(returnValue(true));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        Field_info actualField = (Field_info) sut.locateField(TEST_FIELD_NAME);
        assertEquals("public field", actualField, expectedField);
    }

    @Test
    public void testLocateField_protectedInheritedField_succeed() {
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Field_info expectedField = context.mock(Field_info.class, "located field");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).locateField(TEST_FIELD_NAME);
                will(returnValue(expectedField));
            oneOf (expectedField).isPublic();
                will(returnValue(false));
            oneOf (expectedField).isProtected();
                will(returnValue(true));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        Field_info actualField = (Field_info) sut.locateField(TEST_FIELD_NAME);
        assertEquals("protected field", actualField, expectedField);
    }

    @Test
    public void testLocateField_packageInheritedField_succeed() {
        final Class_info classInfo = context.mock(Class_info.class, "class info");
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Field_info expectedField = context.mock(Field_info.class, "located field");

        final Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (constantPool).get(1);
                will(returnValue(classInfo));
            oneOf (classInfo).getPackageName();
                will(returnValue(TEST_PACKAGE_NAME));
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).getPackageName();
                will(returnValue(TEST_PACKAGE_NAME));
            oneOf (superclass).locateField(TEST_FIELD_NAME);
                will(returnValue(expectedField));
            oneOf (expectedField).isPublic();
                will(returnValue(false));
            oneOf (expectedField).isProtected();
                will(returnValue(false));
            oneOf (expectedField).isPackage();
                will(returnValue(true));
            oneOf (expectedField).getClassfile();
                will(returnValue(sut));
        }});

        Field_info actualField = (Field_info) sut.locateField(TEST_FIELD_NAME);
        assertEquals("package field", actualField, expectedField);
    }

    @Test
    public void testLocateField_packageInheritedField_fail() {
        final Class_info classInfo = context.mock(Class_info.class, "class info");
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Field_info expectedField = context.mock(Field_info.class, "located field");

        final Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (constantPool).get(1);
                will(returnValue(classInfo));
            oneOf (classInfo).getPackageName();
                will(returnValue(TEST_PACKAGE_NAME));
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).getPackageName();
                will(returnValue(""));
            oneOf (superclass).locateField(TEST_FIELD_NAME);
                will(returnValue(expectedField));
            oneOf (expectedField).isPublic();
                will(returnValue(false));
            oneOf (expectedField).isProtected();
                will(returnValue(false));
            oneOf (expectedField).isPackage();
                will(returnValue(true));
            oneOf (expectedField).getClassfile();
                will(returnValue(sut));
        }});

        Field_info actualField = (Field_info) sut.locateField(TEST_FIELD_NAME);
        assertNull("package field", actualField);
    }

    @Test
    public void testLocateField_privateInheritedField_fail() {
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Field_info expectedField = context.mock(Field_info.class, "located field");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).locateField(TEST_FIELD_NAME);
                will(returnValue(expectedField));
            oneOf (expectedField).isPublic();
                will(returnValue(false));
            oneOf (expectedField).isProtected();
                will(returnValue(false));
            oneOf (expectedField).isPackage();
                will(returnValue(false));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        Field_info actualField = (Field_info) sut.locateField(TEST_FIELD_NAME);
        assertNull("local field", actualField);
    }

    @Test
    public void testLocateMethod_localMethod_succeed() {
        final Method_info expectedMethod = context.mock(Method_info.class, "located method");

        Collection<Method_info> methods = Collections.singletonList(expectedMethod);

        context.checking(new Expectations() {{
            oneOf (expectedMethod).getSignature();
                will(returnValue(TEST_METHOD_SIGNATURE));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), methods, Collections.emptyList());

        Method_info actualMethod = (Method_info) sut.locateMethod(TEST_METHOD_SIGNATURE);
        assertEquals("local method", actualMethod, expectedMethod);
    }

    @Test
    public void testLocateMethod_publicInheritedMethod_succeed() {
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Method_info expectedMethod = context.mock(Method_info.class, "located method");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).locateMethod(TEST_METHOD_SIGNATURE);
                will(returnValue(expectedMethod));
            oneOf (expectedMethod).isPublic();
                will(returnValue(true));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        Method_info actualMethod = (Method_info) sut.locateMethod(TEST_METHOD_SIGNATURE);
        assertEquals("public method", actualMethod, expectedMethod);
    }

    @Test
    public void testLocateMethod_protectedInheritedMethod_succeed() {
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Method_info expectedMethod = context.mock(Method_info.class, "located method");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).locateMethod(TEST_METHOD_SIGNATURE);
                will(returnValue(expectedMethod));
            oneOf (expectedMethod).isPublic();
                will(returnValue(false));
            oneOf (expectedMethod).isProtected();
                will(returnValue(true));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        Method_info actualMethod = (Method_info) sut.locateMethod(TEST_METHOD_SIGNATURE);
        assertEquals("protected method", actualMethod, expectedMethod);
    }

    @Test
    public void testLocateMethod_packageInheritedMethod_succeed() {
        final Class_info classInfo = context.mock(Class_info.class, "class info");
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Method_info expectedMethod = context.mock(Method_info.class, "located method");

        final Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (constantPool).get(1);
                will(returnValue(classInfo));
            oneOf (classInfo).getPackageName();
                will(returnValue(TEST_PACKAGE_NAME));
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).getPackageName();
                will(returnValue(TEST_PACKAGE_NAME));
            oneOf (superclass).locateMethod(TEST_METHOD_SIGNATURE);
                will(returnValue(expectedMethod));
            oneOf (expectedMethod).isPublic();
                will(returnValue(false));
            oneOf (expectedMethod).isProtected();
                will(returnValue(false));
            oneOf (expectedMethod).isPackage();
                will(returnValue(true));
            oneOf (expectedMethod).getClassfile();
                will(returnValue(sut));
        }});

        Method_info actualMethod = (Method_info) sut.locateMethod(TEST_METHOD_SIGNATURE);
        assertEquals("package method", actualMethod, expectedMethod);
    }

    @Test
    public void testLocateMethod_packageInheritedMethod_fail() {
        final Class_info classInfo = context.mock(Class_info.class, "class info");
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Method_info expectedMethod = context.mock(Method_info.class, "located method");

        final Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (constantPool).get(1);
                will(returnValue(classInfo));
            oneOf (classInfo).getPackageName();
                will(returnValue(TEST_PACKAGE_NAME));
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).getPackageName();
                will(returnValue(""));
            oneOf (superclass).locateMethod(TEST_METHOD_SIGNATURE);
                will(returnValue(expectedMethod));
            oneOf (expectedMethod).isPublic();
                will(returnValue(false));
            oneOf (expectedMethod).isProtected();
                will(returnValue(false));
            oneOf (expectedMethod).isPackage();
                will(returnValue(true));
            oneOf (expectedMethod).getClassfile();
                will(returnValue(sut));
        }});

        Method_info actualMethod = (Method_info) sut.locateMethod(TEST_METHOD_SIGNATURE);
        assertNull("package method", actualMethod);
    }

    @Test
    public void testLocateMethod_privateInheritedMethod_fail() {
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Method_info expectedMethod = context.mock(Method_info.class, "located method");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).locateMethod(TEST_METHOD_SIGNATURE);
                will(returnValue(expectedMethod));
            oneOf (expectedMethod).isPublic();
                will(returnValue(false));
            oneOf (expectedMethod).isProtected();
                will(returnValue(false));
            oneOf (expectedMethod).isPackage();
                will(returnValue(false));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        Method_info actualMethod = (Method_info) sut.locateMethod(TEST_METHOD_SIGNATURE);
        assertNull("private method", actualMethod);
    }

    @Test
    public void testIsInnerClass_matchingInnerClassInfo_returnsTrue() {
        final InnerClasses_attribute innerClasses_attribute = context.mock(InnerClasses_attribute.class);
        final InnerClass innerClass = context.mock(InnerClass.class);

        expectClassNameLookup(1, TEST_CLASS_NAME);

        context.checking(new Expectations() {{
            oneOf (innerClasses_attribute).getInnerClasses();
                will(returnValue(Collections.singleton(innerClass)));
            oneOf (innerClass).getInnerClassInfo();
                will(returnValue(TEST_CLASS_NAME));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.singleton(innerClasses_attribute));

        assertTrue("inner class", sut.isInnerClass());
    }

    @Test
    public void testIsInnerClass_emptyInnerClassInfo_returnsFalse() {
        final InnerClasses_attribute innerClasses_attribute = context.mock(InnerClasses_attribute.class);
        final InnerClass innerClass = context.mock(InnerClass.class);

        expectClassNameLookup(1, TEST_CLASS_NAME);

        context.checking(new Expectations() {{
            oneOf (innerClasses_attribute).getInnerClasses();
                will(returnValue(Collections.singleton(innerClass)));
            oneOf (innerClass).getInnerClassInfo();
                will(returnValue(""));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.singleton(innerClasses_attribute));

        assertFalse("inner class", sut.isInnerClass());
    }

    @Test
    public void testIsInnerClass_noInnerClassInfo_returnsFalse() {
        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        assertFalse("inner class", sut.isInnerClass());
    }

    private void expectClassNameLookup(final int index, final String value) {
        final Class_info class_info = context.mock(Class_info.class);

        context.checking(new Expectations() {{
            oneOf (constantPool).get(index);
                will(returnValue(class_info));
            oneOf (class_info).getName();
                will(returnValue(value));
        }});
    }
}
