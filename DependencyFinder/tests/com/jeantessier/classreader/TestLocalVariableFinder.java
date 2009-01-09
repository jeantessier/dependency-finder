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

import java.util.*;

import org.jmock.*;
import org.jmock.integration.junit3.*;

public class TestLocalVariableFinder extends MockObjectTestCase {
    private static final int LOCAL_VARIABLE_INDEX = 1;
    private static final int START_PC = 3;
    private static final int LENGTH = 5;

    public void testVisitCode_attribute() {
        final Code_attribute mockCode_attribute = mock(Code_attribute.class);
        final Attribute_info mockAttribute = mock(Attribute_info.class);

        final LocalVariableFinder sut = new LocalVariableFinder(LOCAL_VARIABLE_INDEX, START_PC + 1);

        checking(new Expectations() {{
            atLeast(1).of (mockCode_attribute).getAttributes();
                will(returnValue(Collections.singleton(mockAttribute)));
            one (mockAttribute).accept(sut);
        }});

        sut.visitCode_attribute(mockCode_attribute);
    }

    public void testVisitLocalVariable_MatchingIndexMatchingPcRange() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            one (mockLocalVariable).getIndex();
                will(returnValue(LOCAL_VARIABLE_INDEX));
            atLeast(1).of (mockLocalVariable).getStartPC();
                will(returnValue(START_PC));
            atLeast(1).of (mockLocalVariable).getLength();
                will(returnValue(LENGTH));
        }});

        LocalVariableFinder sut = new LocalVariableFinder(LOCAL_VARIABLE_INDEX, START_PC + 1);
        sut.visitLocalVariable(mockLocalVariable);
        assertSame(mockLocalVariable, sut.getLocalVariable());
    }

    public void testVisitLocalVariable_NotMatchingIndex() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            one (mockLocalVariable).getIndex();
                will(returnValue(LOCAL_VARIABLE_INDEX + 1));
        }});

        LocalVariableFinder sut = new LocalVariableFinder(LOCAL_VARIABLE_INDEX, START_PC);
        sut.visitLocalVariable(mockLocalVariable);
        assertNull(sut.getLocalVariable());
    }

    public void testVisitLocalVariable_InstructionIsBeforeValidRange() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            one (mockLocalVariable).getIndex();
                will(returnValue(LOCAL_VARIABLE_INDEX));
            one (mockLocalVariable).getStartPC();
                will(returnValue(START_PC));
        }});

        LocalVariableFinder sut = new LocalVariableFinder(LOCAL_VARIABLE_INDEX, START_PC - 1);
        sut.visitLocalVariable(mockLocalVariable);
        assertNull(sut.getLocalVariable());
    }

    public void testVisitLocalVariable_InstructionIsAtBeginningOfValidRange() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            one (mockLocalVariable).getIndex();
                will(returnValue(LOCAL_VARIABLE_INDEX));
            atLeast(1).of (mockLocalVariable).getStartPC();
                will(returnValue(START_PC));
            atLeast(1).of (mockLocalVariable).getLength();
                will(returnValue(LENGTH));
        }});

        LocalVariableFinder sut = new LocalVariableFinder(LOCAL_VARIABLE_INDEX, START_PC);
        sut.visitLocalVariable(mockLocalVariable);
        assertSame(mockLocalVariable, sut.getLocalVariable());
    }

    public void testVisitLocalVariable_InstructionIsAtBeginningOfValidRangeOfLengthZero() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            one (mockLocalVariable).getIndex();
                will(returnValue(LOCAL_VARIABLE_INDEX));
            atLeast(1).of (mockLocalVariable).getStartPC();
                will(returnValue(START_PC));
            atLeast(1).of (mockLocalVariable).getLength();
                will(returnValue(0));
        }});

        LocalVariableFinder sut = new LocalVariableFinder(LOCAL_VARIABLE_INDEX, START_PC);
        sut.visitLocalVariable(mockLocalVariable);
        assertSame(mockLocalVariable, sut.getLocalVariable());
    }

    public void testVisitLocalVariable_InstructionIsJustAfterPcRange() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            one (mockLocalVariable).getIndex();
                will(returnValue(LOCAL_VARIABLE_INDEX));
            atLeast(1).of (mockLocalVariable).getStartPC();
                will(returnValue(START_PC));
            atLeast(1).of (mockLocalVariable).getLength();
                will(returnValue(LENGTH));
        }});

        LocalVariableFinder sut = new LocalVariableFinder(LOCAL_VARIABLE_INDEX, START_PC + LENGTH);
        sut.visitLocalVariable(mockLocalVariable);
        assertNull(sut.getLocalVariable());
    }

    public void testVisitLocalVariable_InstructionIsWellAfterPcRange() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            one (mockLocalVariable).getIndex();
                will(returnValue(LOCAL_VARIABLE_INDEX));
            atLeast(1).of (mockLocalVariable).getStartPC();
                will(returnValue(START_PC));
            atLeast(1).of (mockLocalVariable).getLength();
                will(returnValue(LENGTH));
        }});

        LocalVariableFinder sut = new LocalVariableFinder(LOCAL_VARIABLE_INDEX, START_PC + LENGTH + 1);
        sut.visitLocalVariable(mockLocalVariable);
        assertNull(sut.getLocalVariable());
    }
}
