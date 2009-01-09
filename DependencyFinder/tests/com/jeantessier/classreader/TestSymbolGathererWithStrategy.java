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

import org.jmock.*;
import org.jmock.integration.junit3.*;

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
            one (mockStrategy).isMatching(mockClassfile); will(returnValue(false));
            
            one (mockClassfile).getAttributes();
            one (mockClassfile).getAllFields();
            one (mockClassfile).getAllMethods();
        }});

        sut.visitClassfile(mockClassfile);

        assertTrue("Added non-matching class " + sut.getCollection(), sut.getCollection().isEmpty());
    }

    public void testVisitClassfile_Matching() {
        final String expectedName = "Foobar";

        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            one (mockStrategy).isMatching(mockClassfile); will(returnValue(true));
            one (mockClassfile).getClassName(); will(returnValue(expectedName));

            one (mockClassfile).getAttributes();
            one (mockClassfile).getAllFields();
            one (mockClassfile).getAllMethods();
        }});

        sut.visitClassfile(mockClassfile);

        assertEquals("Wrong size for " + sut.getCollection(), 1, sut.getCollection().size());
        assertTrue("Missing class name " + sut.getCollection(), sut.getCollection().contains(expectedName));
    }

    public void testVisitField_info_NotMatching() {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockStrategy).isMatching(mockField); will(returnValue(false));

            one (mockField).getAttributes();
        }});

        sut.visitField_info(mockField);

        assertTrue("Added non-matching class " + sut.getCollection(), sut.getCollection().isEmpty());
    }

    public void testVisitField_info_Matching() {
        final String expectedName = "Foobar.foobar";

        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockStrategy).isMatching(mockField); will(returnValue(true));
            one (mockField).getFullSignature(); will(returnValue(expectedName));

            one (mockField).getAttributes();
        }});

        sut.visitField_info(mockField);

        assertEquals("Wrong size for " + sut.getCollection(), 1, sut.getCollection().size());
        assertTrue("Missing class name " + sut.getCollection(), sut.getCollection().contains(expectedName));
    }

    public void testVisitMethod_info_NotMatching() {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockStrategy).isMatching(mockMethod); will(returnValue(false));

            one (mockMethod).getAttributes();
        }});

        sut.visitMethod_info(mockMethod);

        assertTrue("Added non-matching class " + sut.getCollection(), sut.getCollection().isEmpty());
    }

    public void testVisitMethod_info_Matching() {
        final String expectedName = "Foobar.foobar";

        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockStrategy).isMatching(mockMethod); will(returnValue(true));
            one (mockMethod).getFullSignature(); will(returnValue(expectedName));

            one (mockMethod).getAttributes();
        }});

        sut.visitMethod_info(mockMethod);

        assertEquals("Wrong size for " + sut.getCollection(), 1, sut.getCollection().size());
        assertTrue("Missing class name " + sut.getCollection(), sut.getCollection().contains(expectedName));
    }


    public void testVisitLocalVariable_NotMatching() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            one (mockStrategy).isMatching(mockLocalVariable); will(returnValue(false));
        }});

        sut.visitLocalVariable(mockLocalVariable);

        assertTrue("Added non-matching class " + sut.getCollection(), sut.getCollection().isEmpty());
    }

    public void testVisitLocalVariable_Matching() {
        final String expectedName = "Foobar.foobar";

        final Method_info mockMethod = mock(Method_info.class);
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            one (mockStrategy).isMatching(mockLocalVariable); will(returnValue(true));
            one (mockMethod).getFullSignature(); will(returnValue(expectedName));
            one (mockLocalVariable).getName(); will(returnValue(expectedName));
        }});

        sut.setCurrentMethodForTesting(mockMethod);
        sut.visitLocalVariable(mockLocalVariable);

        assertEquals("Wrong size for " + sut.getCollection(), 1, sut.getCollection().size());
    }
}
