/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

public class TestClassfile_locateMethodDeclaration {
    @RegisterExtension
    public JUnit5Mockery context = new JUnit5Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private ClassfileLoader loader;
    private ConstantPool constantPool;
    private Predicate<com.jeantessier.classreader.Method_info> filter;

    @BeforeEach
    void setUp() {
        loader = context.mock(ClassfileLoader.class);
        constantPool = context.mock(ConstantPool.class);
        filter = context.mock(Predicate.class);
    }

    @Test
    void testLocateMethodDeclaration_fromInterface() {
        final Class_info classInfo = context.mock(Class_info.class, "class info");
        final String implementedInterfaceName = "interface";
        final Classfile implementedInterface = context.mock(Classfile.class, "interface");
        final Method_info expectedMethod = context.mock(Method_info.class, "method");
        final Collection<Method_info> declarationsFromInterface = Collections.singleton(expectedMethod);

        context.checking(new Expectations() {{
            oneOf (loader).getClassfile(implementedInterfaceName);
                will(returnValue(implementedInterface));
            allowing (loader).getClassfile("");
                will(returnValue(null));
            oneOf (classInfo).getName();
                will(returnValue(implementedInterfaceName));
            oneOf (implementedInterface).locateMethodDeclarations(filter);
                will(returnValue(declarationsFromInterface));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 0, Collections.singleton(classInfo), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        var actualDeclarations = sut.locateMethodDeclarations(filter);
        assertEquals(1, actualDeclarations.size(), "nb of declarations from interface");
        assertEquals(expectedMethod, actualDeclarations.stream().findFirst().orElseThrow(), "declarations from interface");
    }

    @Test
    void testLocateMethodDeclaration_fromSuperclass() {
        final String superclassName = "superclass";
        final Classfile superclass = context.mock(Classfile.class, "superclass");
        final Method_info expectedMethod = context.mock(Method_info.class, "method");
        final Collection<Method_info> declarationsFromSuperclass = Collections.singleton(expectedMethod);

        expectClassNameLookup(2, superclassName);

        context.checking(new Expectations() {{
            oneOf (loader).getClassfile(superclassName);
                will(returnValue(superclass));
            oneOf (superclass).locateMethodDeclarations(filter);
                will(returnValue(declarationsFromSuperclass));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        var actualDeclarations = sut.locateMethodDeclarations(filter);
        assertEquals(1, actualDeclarations.size(), "nb of method from superclass");
        assertEquals(expectedMethod, actualDeclarations.stream().findFirst().orElseThrow(), "method from superclass");
    }

    @Test
    void testLocateMethodDeclaration_fromClass() {
        final Method_info expectedMethod = context.mock(Method_info.class, "method");

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 0, Collections.emptyList(), Collections.emptyList(), Collections.singleton(expectedMethod), Collections.emptyList());

        context.checking(new Expectations() {{
            allowing (loader).getClassfile("");
                will(returnValue(null));
            oneOf (filter).test(expectedMethod);
                will(returnValue(true));
        }});

        var actualDeclarations = sut.locateMethodDeclarations(filter);
        assertEquals(1, actualDeclarations.size(), "nb of method from class itself");
        assertEquals(expectedMethod, actualDeclarations.stream().findFirst().orElseThrow(), "method from class itself");
    }

    @Test
    void testLocateMethodDeclaration_failure() {
        context.checking(new Expectations() {{
            allowing (loader).getClassfile("");
                will(returnValue(null));
        }});

        Classfile sut = new Classfile(loader, constantPool, 0x0, 1, 0, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        var actualDeclarations = sut.locateMethodDeclarations(filter);
        assertEquals(0, actualDeclarations.size(), "nb of missing method");
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
