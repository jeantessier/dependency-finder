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

package com.jeantessier.dependency;

import com.jeantessier.classreader.*;
import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.integration.junit3.MockObjectTestCase;

import java.util.*;

import static org.jmock.lib.script.ScriptedAction.perform;

public class TestCodeDependencyCollectorUsingMocks extends MockObjectTestCase {
    private static final String TEST_CLASS_NAME = "a.A";
    private static final String TEST_SUPERCLASS_NAME = "a.Parent";
    private static final String TEST_INTERFACE_NAME = "a.I";
    private static final String TEST_EXCEPTION_NAME = "java.lang.Exception";

    private NodeFactory mockFactory;
    private Classfile mockClassfile;
    private ClassNode mockClassNode;

    private CodeDependencyCollector sut;

    protected void setUp() throws Exception {
        super.setUp();

        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);

        mockFactory = mock(NodeFactory.class);
        mockClassfile = mock(Classfile.class);
        mockClassNode = mock(ClassNode.class, "testclass");

        sut = new CodeDependencyCollector(mockFactory);
    }

    public void testVisitClass_info_exceptions() {
        final Class_info mockException = mock(Class_info.class);
        final ClassNode mockExceptionNode = mock(ClassNode.class);

        checking(new Expectations() {{
            oneOf (mockException).getName();
                will(returnValue(TEST_EXCEPTION_NAME));
            oneOf (mockFactory).createClass(TEST_EXCEPTION_NAME);
                will(returnValue(mockExceptionNode));
            oneOf (mockClassNode).addDependency(mockExceptionNode);
        }});

        sut.setCurrent(mockClassNode);
        sut.visitClass_info(mockException);
    }

    public void testVisitClassfile_withoutSuperclass() {
        expectClassNodeForClassname();

        checking(new Expectations() {{
            atLeast(1).of (mockClassfile).getClassName();
                will(returnValue(TEST_CLASS_NAME));
            oneOf (mockFactory).createClass(TEST_CLASS_NAME, true);
                will(returnValue(mockClassNode));
            oneOf (mockClassfile).getSuperclassIndex();
                will(returnValue(0));
            never (mockClassNode).addParent(with(any(ClassNode.class)));
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();
            ignoring (mockClassfile).getAttributes();
        }});

        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClassfile_withSuperclass() {
        final Class_info mockRawSuperclass = mock(Class_info.class);
        final ClassNode mockSuperclassNode = mock(ClassNode.class, "superclass");

        expectClassNodeForClassname();

        checking(new Expectations() {{
            atLeast(1).of (mockClassfile).getClassName();
                will(returnValue(TEST_CLASS_NAME));
            oneOf (mockFactory).createClass(TEST_CLASS_NAME, true);
                will(returnValue(mockClassNode));
            oneOf (mockClassfile).getSuperclassIndex();
                will(returnValue(1));
            oneOf (mockClassfile).getRawSuperclass();
                will(returnValue(mockRawSuperclass));
            oneOf (mockRawSuperclass).accept(sut);
            atLeast(1).of (mockRawSuperclass).getName();
                will(returnValue(TEST_SUPERCLASS_NAME));
            atLeast(1).of (mockFactory).createClass(TEST_SUPERCLASS_NAME);
                will(returnValue(mockSuperclassNode));
            oneOf (mockClassNode).addParent(mockSuperclassNode);
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();
            ignoring (mockClassfile).getAttributes();
        }});

        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClassfile_withInterface() {
        final Class_info mockInterface = mock(Class_info.class);
        final ClassNode mockInterfaceNode = mock(ClassNode.class, "interface");

        final Collection<Class_info> allInterfaces = new ArrayList<>();
        allInterfaces.add(mockInterface);

        expectClassNodeForClassname();

        checking(new Expectations() {{
            atLeast(1).of (mockClassfile).getClassName();
                will(returnValue(TEST_CLASS_NAME));
            oneOf (mockFactory).createClass(TEST_CLASS_NAME, true);
                will(returnValue(mockClassNode));
            oneOf (mockClassfile).getSuperclassIndex();
                will(returnValue(0));
            oneOf (mockClassfile).getAllInterfaces();
                will(returnValue(allInterfaces));
            oneOf (mockInterface).accept(sut);
            atLeast(1).of (mockInterface).getName();
                will(returnValue(TEST_INTERFACE_NAME));
            atLeast(1).of (mockFactory).createClass(TEST_INTERFACE_NAME);
                will(returnValue(mockInterfaceNode));
            oneOf (mockClassNode).addParent(mockInterfaceNode);
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();
            ignoring (mockClassfile).getAttributes();
        }});

        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClassfile_fireEvents() {
        final DependencyListener mockListener = mock(DependencyListener.class);

        expectClassNodeForClassname();

        checking(new Expectations() {{
            atLeast(1).of (mockClassfile).getClassName();
                will(returnValue(TEST_CLASS_NAME));

            oneOf (mockListener).beginClass(with(any(DependencyEvent.class)));
                will(createEventCheckingAction(TEST_CLASS_NAME));

            oneOf (mockFactory).createClass(TEST_CLASS_NAME, true);

            ignoring (mockClassfile).getSuperclassIndex();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();
            ignoring (mockClassfile).getAttributes();

            oneOf (mockListener).endClass(with(any(DependencyEvent.class)));
                will(createEventCheckingAction(TEST_CLASS_NAME));
        }});

        sut.addDependencyListener(mockListener);
        sut.visitClassfile(mockClassfile);
    }

    public void testVisitExceptionHandler_finally() {
        final ExceptionHandler mockExceptionHandler = mock(ExceptionHandler.class);

        checking(new Expectations() {{
            oneOf (mockExceptionHandler).getCatchTypeIndex();
                will(returnValue(0));
        }});

        sut.visitExceptionHandler(mockExceptionHandler);
    }

    public void testVisitExceptionHandler_catch() {
        final ExceptionHandler mockExceptionHandler = mock(ExceptionHandler.class);
        final Class_info mockCatchType = mock(Class_info.class);

        checking(new Expectations() {{
            oneOf (mockExceptionHandler).getCatchTypeIndex();
                will(returnValue(1));
            oneOf (mockExceptionHandler).getRawCatchType();
                will(returnValue(mockCatchType));
            oneOf (mockCatchType).accept(sut);
        }});

        sut.visitExceptionHandler(mockExceptionHandler);
    }

    public void testVisitAnnotation() {
        final Annotation mockAnnotation = mock(Annotation.class);
        final ClassNode mockDependableClassNode = mock(ClassNode.class, "dependable");

        checking(new Expectations() {{
            oneOf (mockAnnotation).getType();
                will(returnValue("dependable.Dependable"));
            oneOf (mockFactory).createClass("dependable.Dependable");
                will(returnValue(mockDependableClassNode));
            oneOf (mockClassNode).addDependency(mockDependableClassNode);

            ignoring (mockAnnotation).getElementValuePairs();
        }});

        sut.setCurrent(mockClassNode);
        sut.visitAnnotation(mockAnnotation);
    }

    public void testVisitEnumElementValue() {
        final EnumElementValue mockEnumElementValue = mock(EnumElementValue.class);
        final FeatureNode mockDependableFeatureNode = mock(FeatureNode.class, "dependable.CONSTANT");

        checking(new Expectations() {{
            oneOf (mockEnumElementValue).getTypeName();
                will(returnValue("dependable.Dependable"));
            oneOf (mockEnumElementValue).getConstName();
                will(returnValue("CONSTANT"));
            oneOf (mockFactory).createFeature("dependable.Dependable.CONSTANT");
                will(returnValue(mockDependableFeatureNode));
            oneOf (mockClassNode).addDependency(mockDependableFeatureNode);
        }});

        sut.setCurrent(mockClassNode);
        sut.visitEnumElementValue(mockEnumElementValue);
    }

    public void testVisitClassElementValue() {
        final ClassElementValue mockClassElementValue = mock(ClassElementValue.class);
        final ClassNode mockDependableClassNode = mock(ClassNode.class, "dependable");

        checking(new Expectations() {{
            oneOf (mockClassElementValue).getClassInfo();
                will(returnValue("dependable.Dependable"));
            oneOf (mockFactory).createClass("dependable.Dependable");
                will(returnValue(mockDependableClassNode));
            oneOf (mockClassNode).addDependency(mockDependableClassNode);
        }});

        sut.setCurrent(mockClassNode);
        sut.visitClassElementValue(mockClassElementValue);
    }

    public void testVisitClassAnnotations() {
        expectClassNodeForClassname();

        checking(new Expectations() {{
            ignoring (mockClassfile).getAttributes();
        }});

        sut.visitClassfileAttributes(mockClassfile);
    }

    public void testVisitInstruction_invokedynamic() {
        final Instruction mockInstruction = mock(Instruction.class);
        final ConstantPoolEntry mockConstantPoolEntry = mock(ConstantPoolEntry.class);

        checking(new Expectations() {{
            oneOf (mockInstruction).getOpcode();
                will(returnValue(0xba /* invokedynamic */));
            oneOf (mockInstruction).getDynamicConstantPoolEntries();
                will(returnValue(Collections.singleton(mockConstantPoolEntry)));
            oneOf (mockConstantPoolEntry).accept(sut);
        }});

        sut.visitInstruction(mockInstruction);
    }

    private void expectClassNodeForClassname() {
        checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
                will(returnValue(TEST_CLASS_NAME));
            oneOf (mockFactory).createClass(TEST_CLASS_NAME);
                will(returnValue(mockClassNode));
        }});
    }

    private Action createEventCheckingAction(final String expectedClassName) {
        return
            perform(
                "junit.framework.Assert.assertEquals(\"source\", sut, $0.getSource());" +
                "junit.framework.Assert.assertEquals(\"classname\", expectedClassName, $0.getClassName());" +
                "junit.framework.Assert.assertNull(\"dependent\", $0.getDependent());" +
                "junit.framework.Assert.assertNull(\"dependable\", $0.getDependable())")
                .where("sut", sut)
                .where("expectedClassName", expectedClassName);
    }
}
