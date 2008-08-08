/*
 *  Copyright (c) 2001-2008, Jean Tessier
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

package com.jeantessier.dependency;

import java.util.*;

import org.jmock.*;
import org.jmock.api.*;
import org.jmock.integration.junit3.*;
import org.jmock.lib.action.*;
import org.jmock.lib.legacy.*;

import com.jeantessier.classreader.*;

public class TestCodeDependencyCollectorUsingMocks extends MockObjectTestCase {
    private static final String TEST_CLASS_NAME = "a.A";
    private static final String TEST_SUPERCLASS_NAME = "a.Parent";
    private static final String TEST_INTERFACE_NAME = "a.I";

    private NodeFactory mockFactory;
    private Classfile mockClassfile;
    private ClassNode mockClassNode;

    private CodeDependencyCollector sut;

    protected void setUp() throws Exception {
        super.setUp();

        setImposteriser(ClassImposteriser.INSTANCE);

        mockFactory = mock(NodeFactory.class);
        mockClassfile = mock(Classfile.class);
        mockClassNode = mock(ClassNode.class, "testclass");

        sut = new CodeDependencyCollector(mockFactory);
    }
    
    public void testVisitClassfile_withoutsuperclass() {
        checking(new Expectations() {{
            atLeast(1).of (mockClassfile).getClassName();
                will(returnValue(TEST_CLASS_NAME));
            one (mockFactory).createClass(TEST_CLASS_NAME, true);
                will(returnValue(mockClassNode));
            one (mockClassfile).getSuperclassIndex();
                will(returnValue(0));
            never (mockClassNode).addParent(with(any(ClassNode.class)));
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();
        }});

        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClassfile_withsuperclass() {
        final Class_info mockRawSuperclass = mock(Class_info.class);
        final ClassNode mockSuperclassNode = mock(ClassNode.class, "superclass");

        checking(new Expectations() {{
            atLeast(1).of (mockClassfile).getClassName();
                will(returnValue(TEST_CLASS_NAME));
            one (mockFactory).createClass(TEST_CLASS_NAME, true);
                will(returnValue(mockClassNode));
            one (mockClassfile).getSuperclassIndex();
                will(returnValue(1));
            one (mockClassfile).getRawSuperclass();
                will(returnValue(mockRawSuperclass));
            one (mockRawSuperclass).accept(sut);
            atLeast(1).of (mockRawSuperclass).getName();
                will(returnValue(TEST_SUPERCLASS_NAME));
            atLeast(1).of (mockFactory).createClass(TEST_SUPERCLASS_NAME);
                will(returnValue(mockSuperclassNode));
            one (mockClassNode).addParent(mockSuperclassNode);
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();
        }});

        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClassfile_withinterface() {
        final Class_info mockInterface = mock(Class_info.class);
        final ClassNode mockInterfaceNode = mock(ClassNode.class, "interface");

        final Collection<Class_info> allInterfaces = new ArrayList<Class_info>();
        allInterfaces.add(mockInterface);

        checking(new Expectations() {{
            atLeast(1).of (mockClassfile).getClassName();
                will(returnValue(TEST_CLASS_NAME));
            one (mockFactory).createClass(TEST_CLASS_NAME, true);
                will(returnValue(mockClassNode));
            one (mockClassfile).getSuperclassIndex();
                will(returnValue(0));
            one (mockClassfile).getAllInterfaces();
                will(returnValue(allInterfaces));
            one (mockInterface).accept(sut);
            atLeast(1).of (mockInterface).getName();
                will(returnValue(TEST_INTERFACE_NAME));
            atLeast(1).of (mockFactory).createClass(TEST_INTERFACE_NAME);
                will(returnValue(mockInterfaceNode));
            one (mockClassNode).addParent(mockInterfaceNode);
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();
        }});

        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClassfile_fireevents() {
        final DependencyListener mockListener = mock(DependencyListener.class);

        checking(new Expectations() {{
            atLeast(1).of (mockClassfile).getClassName();
                will(returnValue(TEST_CLASS_NAME));

            one (mockListener).beginClass(with(any(DependencyEvent.class)));
                will(createEventCheckingAction(TEST_CLASS_NAME));

            one (mockFactory).createClass(TEST_CLASS_NAME, true);

            ignoring (mockClassfile).getSuperclassIndex();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();

            one (mockListener).endClass(with(any(DependencyEvent.class)));
                will(createEventCheckingAction(TEST_CLASS_NAME));

        }});

        sut.addDependencyListener(mockListener);
        sut.visitClassfile(mockClassfile);
    }

    private Action createEventCheckingAction(final String expectedClassName) {
        return new CustomAction("event for " + expectedClassName) {
            public Object invoke(Invocation invocation) throws Throwable {
                DependencyEvent event = (DependencyEvent) invocation.getParameter(0);
                assertEquals("source", sut, event.getSource());
                assertEquals("classname", expectedClassName, event.getClassName());
                assertNull("dependent", event.getDependent());
                assertNull("dependable", event.getDependable());
                return null;
            }
        };
    }
}
