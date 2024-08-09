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

package com.jeantessier.classreader;

import org.jmock.*;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit3.*;
import org.jmock.lib.action.CustomAction;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public class TestSymbolGathererWithStrategy extends MockObjectTestCase {
    private SymbolGathererStrategy mockStrategy;

    private SymbolGatherer sut;

    protected void setUp() throws Exception {
        super.setUp();

        mockStrategy = mock(SymbolGathererStrategy.class);

        sut = new SymbolGatherer(mockStrategy);
    }

    public void testVisitClassfile_NotMatching() {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockStrategy).isMatching(mockClassfile); will(returnValue(false));

            ignoring (mockClassfile).getAttributes();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();
        }});

        sut.visitClassfile(mockClassfile);

        assertEquals("Wrong size for " + getGatheredSymbols(), 0, sut.stream().count());
    }

    public void testVisitClassfile_Matching() {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockStrategy).isMatching(mockClassfile); will(returnValue(true));

            ignoring (mockClassfile).getAttributes();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();
        }});

        sut.visitClassfile(mockClassfile);

        assertEquals("Wrong size for " + getGatheredSymbols(), 1, sut.stream().count());
        assertTrue("Missing class name " + getGatheredSymbols(), sut.stream().anyMatch(symbol -> symbol == mockClassfile));
    }

    public void testVisitField_info_NotMatching() {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockStrategy).isMatching(mockField); will(returnValue(false));

            ignoring (mockField).getAttributes();
        }});

        sut.visitField_info(mockField);

        assertEquals("Wrong size for " + getGatheredSymbols(), 0, sut.stream().count());
    }

    public void testVisitField_info_Matching() {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockStrategy).isMatching(mockField); will(returnValue(true));

            ignoring (mockField).getAttributes();
        }});

        sut.visitField_info(mockField);

        assertEquals("Wrong size for " + getGatheredSymbols(), 1, sut.stream().count());
        assertTrue("Missing field name " + getGatheredSymbols(), sut.stream().anyMatch(symbol -> symbol == mockField));
    }

    public void testVisitMethod_info_NotMatching() {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockStrategy).isMatching(mockMethod); will(returnValue(false));

            ignoring (mockMethod).getAttributes();
        }});

        sut.visitMethod_info(mockMethod);

        assertEquals("Wrong size for " + getGatheredSymbols(), 0, sut.stream().count());
    }

    public void testVisitMethod_info_Matching() {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockStrategy).isMatching(mockMethod); will(returnValue(true));

            ignoring (mockMethod).getAttributes();
        }});

        sut.visitMethod_info(mockMethod);

        assertEquals("Wrong size for " + getGatheredSymbols(), 1, sut.stream().count());
        assertTrue("Missing method name " + getGatheredSymbols(), sut.stream().anyMatch(symbol -> symbol == mockMethod));
    }

    public void testVisitLocalVariable_NotMatching() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            oneOf (mockStrategy).isMatching(mockLocalVariable); will(returnValue(false));
        }});

        sut.visitLocalVariable(mockLocalVariable);

        assertEquals("Wrong size for " + getGatheredSymbols(), 0, sut.stream().count());
    }

    public void testVisitLocalVariable_Matching() {
        final Method_info mockMethod = mock(Method_info.class);
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            oneOf (mockStrategy).isMatching(mockLocalVariable); will(returnValue(true));
        }});

        sut.visitLocalVariable(mockLocalVariable);

        assertEquals("Wrong size for " + getGatheredSymbols(), 1, sut.stream().count());
        assertTrue("Missing local variable name " + getGatheredSymbols(), sut.stream().anyMatch(symbol -> symbol == mockLocalVariable));
    }

    public void testVisitInnerClass_NotMatching() {
        final Classfile mockClassfile = mock(Classfile.class);
        final Class_info mockClass_info = mock(Class_info.class);
        final InnerClasses_attribute mockInnerClasses = mock(InnerClasses_attribute.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).getAttributes(); will(returnValue(Collections.singleton(mockInnerClasses)));
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();
            oneOf (mockClassfile).getRawClass(); will(returnValue(mockClass_info));

            oneOf (mockInnerClasses).accept(sut); will(visitInnerClasses_attribute(mockInnerClasses));
            oneOf (mockInnerClasses).getInnerClasses(); will(returnValue(Collections.singleton(mockInnerClass)));

            oneOf (mockInnerClass).accept(sut); will(visitInnerClass(mockInnerClass));
            oneOf (mockInnerClass).getClassfile(); will(returnValue(mockClassfile));
            oneOf (mockInnerClass).getRawInnerClassInfo(); will(returnValue(mockClass_info));

            oneOf (mockStrategy).isMatching(mockInnerClass); will(returnValue(false));
        }});

        sut.visitClassfile(mockClassfile);

        assertEquals("Wrong size for " + getGatheredSymbols(), 0, sut.stream().count());
    }

    public void testVisitInnerClass_Matching() {
        final Classfile mockClassfile = mock(Classfile.class);
        final Class_info mockClass_info = mock(Class_info.class);
        final InnerClasses_attribute mockInnerClasses = mock(InnerClasses_attribute.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).getAttributes(); will(returnValue(Collections.singleton(mockInnerClasses)));
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();
            oneOf (mockClassfile).getRawClass(); will(returnValue(mockClass_info));

            oneOf (mockInnerClasses).accept(sut); will(visitInnerClasses_attribute(mockInnerClasses));
            oneOf (mockInnerClasses).getInnerClasses(); will(returnValue(Collections.singleton(mockInnerClass)));

            oneOf (mockInnerClass).accept(sut); will(visitInnerClass(mockInnerClass));
            oneOf (mockInnerClass).getClassfile(); will(returnValue(mockClassfile));
            oneOf (mockInnerClass).getRawInnerClassInfo(); will(returnValue(mockClass_info));

            oneOf (mockStrategy).isMatching(mockInnerClass); will(returnValue(true));
        }});

        sut.visitClassfile(mockClassfile);

        assertEquals("Wrong size for " + getGatheredSymbols(), 1, sut.stream().count());
        assertTrue("Missing inner class name " + getGatheredSymbols(), sut.stream().anyMatch(symbol -> symbol == mockInnerClass));
    }

    private Action visitInnerClasses_attribute(InnerClasses_attribute attribute) {
        return new CustomAction("visit InnerClasses_attribute") {
            public Object invoke(Invocation invocation) {
                ((Visitor) invocation.getParameter(0)).visitInnerClasses_attribute(attribute);
                return null;
            }
        };
    }

    private Action visitInnerClass(InnerClass helper) {
        return new CustomAction("visit InnerClass") {
            public Object invoke(Invocation invocation) {
                ((Visitor) invocation.getParameter(0)).visitInnerClass(helper);
                return null;
            }
        };
    }

    private Collection<String> getGatheredSymbols() {
        return sut.stream()
                .map(Object::toString)
                .toList();
    }
}
