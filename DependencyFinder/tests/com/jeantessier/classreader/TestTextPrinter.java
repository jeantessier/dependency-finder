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

package com.jeantessier.classreader;

import java.io.*;
import java.util.*;

import org.jmock.*;
import org.jmock.integration.junit3.*;
import org.jmock.lib.legacy.*;

public class TestTextPrinter extends MockObjectTestCase {
    private PrintWriter mockPrinter;

    private TextPrinter sut;

    protected void setUp() throws Exception {
        super.setUp();

        setImposteriser(ClassImposteriser.INSTANCE);

        mockPrinter = mock(PrintWriter.class);

        sut = new TextPrinter(mockPrinter);
    }

    public void testVisitMethod_info_CallsToCode_attribute() {
        final String methodDeclaration = "int foo();";

        final Method_info mockMethod = mock(Method_info.class);
        final Code_attribute mockCode = mock(Code_attribute.class);

        checking(new Expectations() {{
            one (mockMethod).getDeclaration();
                will(returnValue(methodDeclaration));
            one (mockPrinter).print(methodDeclaration);

            one (mockMethod).getAttributes();
                will(returnValue(Collections.singleton(mockCode)));
            one (mockCode).accept(sut);

            ignoring (mockPrinter);
        }});

        sut.visitMethod_info(mockMethod);
    }

    public void testVisitInstruction_iinc() {
        final int opcode = 0x84; // iinc
        final int start = 1;
        final String mnemonic = "foo";
        final int index = 2;
        final int value = 3;
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);
        final String descriptor = "i";

        final Instruction mockInstruction = mock(Instruction.class);

        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(opcode));

            one (mockInstruction).getStart();
                will(returnValue(start));
            one (mockPrinter).print(start);

            one (mockInstruction).getMnemonic();
                will(returnValue(mnemonic));
            one (mockPrinter).print(mnemonic);

            one (mockInstruction).getIndex();
                will(returnValue(index));
            one (mockPrinter).print(index);

            one (mockInstruction).getIndexedLocalVariable();
                will(returnValue(mockLocalVariable));
            one (mockLocalVariable).getDescriptor();
                will(returnValue(descriptor));
            one (mockLocalVariable).getName();

            one (mockInstruction).getValue();
                will(returnValue(value));
            one (mockPrinter).print(value);

            ignoring (mockPrinter);
        }});

        sut.visitInstruction(mockInstruction);
    }

    public void testVisitInstruction_iconst_1() {
        final int opcode = 0x04; // iconst_1
        final int start = 1;
        final String mnemonic = "foo";

        final Instruction mockInstruction = mock(Instruction.class);

        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(opcode));

            one (mockInstruction).getStart();
                will(returnValue(start));
            one (mockPrinter).print(start);

            one (mockInstruction).getMnemonic();
                will(returnValue(mnemonic));
            one (mockPrinter).print(mnemonic);

            ignoring (mockPrinter);
        }});

        sut.visitInstruction(mockInstruction);
    }
}
