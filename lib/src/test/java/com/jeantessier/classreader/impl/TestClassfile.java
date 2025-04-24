/*
 *  Copyright (c) 2001-2025, Jean Tessier
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
import org.jmock.junit5.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestClassfile {
    private static final String TEST_PACKAGE_NAME = "foo";
    private static final String TEST_CLASS_NAME = TEST_PACKAGE_NAME + ".Foo";
    private static final String TEST_FIELD_NAME = TEST_CLASS_NAME + ".foo";
    private static final String TEST_METHOD_SIGNATURE = TEST_CLASS_NAME + ".foo()";

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private ClassfileLoader loader;
    private ConstantPool constantPool;

    @BeforeEach
    void setUp() {
        loader = context.mock(ClassfileLoader.class);
        constantPool = context.mock(ConstantPool.class);
    }

    @Test
    void testGetPackageName() {
        var classInfo = context.mock(Class_info.class);

        context.checking(new Expectations() {{
            oneOf (constantPool).get(1);
                will(returnValue(classInfo));
            oneOf (classInfo).getPackageName();
                will(returnValue(TEST_PACKAGE_NAME));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        String actualValue = sut.getPackageName();
        assertEquals(TEST_PACKAGE_NAME, actualValue, "package name");
    }

    @Test
    void testLocateField_localField_succeed() {
        var expectedField = context.mock(Field_info.class, "located field");

        Collection<Field_info> fields = Collections.singletonList(expectedField);

        context.checking(new Expectations() {{
            oneOf (expectedField).getName();
                will(returnValue(TEST_FIELD_NAME));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), fields, Collections.emptyList(), Collections.emptyList());

        Field_info actualField = (Field_info) sut.locateField(field -> field.getName().equals(TEST_FIELD_NAME));
        assertEquals(expectedField, actualField, "local field");
    }

    @Test
    void testLocateField_publicInheritedField_succeed() {
        var superclassName = "superclass";
        var superclass = context.mock(Classfile.class, "superclass");
        var expectedField = context.mock(Field_info.class, "located field");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).locateField(with(any(Predicate.class)));
                will(returnValue(expectedField));
            oneOf (expectedField).isPublic();
                will(returnValue(true));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        Field_info actualField = (Field_info) sut.locateField(field -> field.getName().equals(TEST_FIELD_NAME));
        assertEquals(expectedField, actualField, "public field");
    }

    @Test
    void testLocateField_protectedInheritedField_succeed() {
        var superclassName = "superclass";
        var superclass = context.mock(Classfile.class, "superclass");
        var expectedField = context.mock(Field_info.class, "located field");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).locateField(with(any(Predicate.class)));
                will(returnValue(expectedField));
            oneOf (expectedField).isPublic();
                will(returnValue(false));
            oneOf (expectedField).isProtected();
                will(returnValue(true));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        Field_info actualField = (Field_info) sut.locateField(field -> field.getName().equals(TEST_FIELD_NAME));
        assertEquals(expectedField, actualField, "protected field");
    }

    @Test
    void testLocateField_packageInheritedField_succeed() {
        var classInfo = context.mock(Class_info.class, "class info");
        var superclassName = "superclass";
        var superclass = context.mock(Classfile.class, "superclass");
        var expectedField = context.mock(Field_info.class, "located field");

        var sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

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
            oneOf (superclass).locateField(with(any(Predicate.class)));
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

        Field_info actualField = (Field_info) sut.locateField(field -> field.getName().equals(TEST_FIELD_NAME));
        assertEquals(expectedField, actualField, "package field");
    }

    @Test
    void testLocateField_packageInheritedField_fail() {
        var classInfo = context.mock(Class_info.class, "class info");
        var superclassName = "superclass";
        var superclass = context.mock(Classfile.class, "superclass");
        var expectedField = context.mock(Field_info.class, "located field");

        var sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

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
            oneOf (superclass).locateField(with(any(Predicate.class)));
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

        Field_info actualField = (Field_info) sut.locateField(field -> field.getName().equals(TEST_FIELD_NAME));
        assertNull(actualField, "package field");
    }

    @Test
    void testLocateField_privateInheritedField_fail() {
        var superclassName = "superclass";
        var superclass = context.mock(Classfile.class, "superclass");
        var expectedField = context.mock(Field_info.class, "located field");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).locateField(with(any(Predicate.class)));
                will(returnValue(expectedField));
            oneOf (expectedField).isPublic();
                will(returnValue(false));
            oneOf (expectedField).isProtected();
                will(returnValue(false));
            oneOf (expectedField).isPackage();
                will(returnValue(false));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        Field_info actualField = (Field_info) sut.locateField(field -> field.getName().equals(TEST_FIELD_NAME));
        assertNull(actualField, "local field");
    }

    @Test
    void testLocateMethod_localMethod_succeed() {
        var expectedMethod = context.mock(Method_info.class, "located method");

        Collection<Method_info> methods = Collections.singletonList(expectedMethod);

        context.checking(new Expectations() {{
            oneOf (expectedMethod).getSignature();
                will(returnValue(TEST_METHOD_SIGNATURE));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), methods, Collections.emptyList());

        Method_info actualMethod = (Method_info) sut.locateMethod(method -> method.getSignature().equals(TEST_METHOD_SIGNATURE));
        assertEquals(expectedMethod, actualMethod, "local method");
    }

    @Test
    void testLocateMethod_publicInheritedMethod_succeed() {
        var superclassName = "superclass";
        var superclass = context.mock(Classfile.class, "superclass");
        var expectedMethod = context.mock(Method_info.class, "located method");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).locateMethod(with(any(Predicate.class)));
                will(returnValue(expectedMethod));
            oneOf (expectedMethod).isPublic();
                will(returnValue(true));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        Method_info actualMethod = (Method_info) sut.locateMethod(method -> method.getSignature().equals(TEST_METHOD_SIGNATURE));
        assertEquals(expectedMethod, actualMethod, "public method");
    }

    @Test
    void testLocateMethod_protectedInheritedMethod_succeed() {
        var superclassName = "superclass";
        var superclass = context.mock(Classfile.class, "superclass");
        var expectedMethod = context.mock(Method_info.class, "located method");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).locateMethod(with(any(Predicate.class)));
                will(returnValue(expectedMethod));
            oneOf (expectedMethod).isPublic();
                will(returnValue(false));
            oneOf (expectedMethod).isProtected();
                will(returnValue(true));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        Method_info actualMethod = (Method_info) sut.locateMethod(method -> method.getSignature().equals(TEST_METHOD_SIGNATURE));
        assertEquals(expectedMethod, actualMethod, "protected method");
    }

    @Test
    void testLocateMethod_packageInheritedMethod_succeed() {
        var classInfo = context.mock(Class_info.class, "class info");
        var superclassName = "superclass";
        var superclass = context.mock(Classfile.class, "superclass");
        var expectedMethod = context.mock(Method_info.class, "located method");

        var sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

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
            oneOf (superclass).locateMethod(with(any(Predicate.class)));
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

        Method_info actualMethod = (Method_info) sut.locateMethod(method -> method.getSignature().equals(TEST_METHOD_SIGNATURE));
        assertEquals(expectedMethod, actualMethod, "package method");
    }

    @Test
    void testLocateMethod_packageInheritedMethod_fail() {
        var classInfo = context.mock(Class_info.class, "class info");
        var superclassName = "superclass";
        var superclass = context.mock(Classfile.class, "superclass");
        var expectedMethod = context.mock(Method_info.class, "located method");

        var sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

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
            oneOf (superclass).locateMethod(with(any(Predicate.class)));
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

        Method_info actualMethod = (Method_info) sut.locateMethod(method -> method.getSignature().equals(TEST_METHOD_SIGNATURE));
        assertNull(actualMethod, "package method");
    }

    @Test
    void testLocateMethod_privateInheritedMethod_fail() {
        var superclassName = "superclass";
        var superclass = context.mock(Classfile.class, "superclass");
        var expectedMethod = context.mock(Method_info.class, "located method");

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).locateMethod(with(any(Predicate.class)));
                will(returnValue(expectedMethod));
            oneOf (expectedMethod).isPublic();
                will(returnValue(false));
            oneOf (expectedMethod).isProtected();
                will(returnValue(false));
            oneOf (expectedMethod).isPackage();
                will(returnValue(false));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        Method_info actualMethod = (Method_info) sut.locateMethod(method -> method.getSignature().equals(TEST_METHOD_SIGNATURE));
        assertNull(actualMethod, "private method");
    }

    @Test
    void testIsInnerClass_matchingInnerClassInfo_returnsTrue() {
        var innerClasses_attribute = context.mock(InnerClasses_attribute.class);
        var innerClass = context.mock(InnerClass.class);

        expectClassNameLookup(1, TEST_CLASS_NAME);

        context.checking(new Expectations() {{
            oneOf (innerClasses_attribute).getInnerClasses();
                will(returnValue(Collections.singleton(innerClass)));
            oneOf (innerClass).getInnerClassInfo();
                will(returnValue(TEST_CLASS_NAME));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.singleton(innerClasses_attribute));

        assertTrue(sut.isInnerClass(), "inner class");
    }

    @Test
    void testIsInnerClass_emptyInnerClassInfo_returnsFalse() {
        var innerClasses_attribute = context.mock(InnerClasses_attribute.class);
        var innerClass = context.mock(InnerClass.class);

        expectClassNameLookup(1, TEST_CLASS_NAME);

        context.checking(new Expectations() {{
            oneOf (innerClasses_attribute).getInnerClasses();
                will(returnValue(Collections.singleton(innerClass)));
            oneOf (innerClass).getInnerClassInfo();
                will(returnValue(""));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.singleton(innerClasses_attribute));

        assertFalse(sut.isInnerClass(), "inner class");
    }

    @Test
    void testIsInnerClass_noInnerClassInfo_returnsFalse() {
        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        assertFalse(sut.isInnerClass(), "inner class");
    }

    private void expectClassNameLookup(int index, String value) {
        var class_info = context.mock(Class_info.class);

        context.checking(new Expectations() {{
            oneOf (constantPool).get(index);
                will(returnValue(class_info));
            oneOf (class_info).getName();
                will(returnValue(value));
        }});
    }
}
