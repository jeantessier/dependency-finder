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
    private static final int ICONST_1_INSTRUCTION = 0x04; // iconst_1
    private static final int ILOAD_INSTRUCTION = 0x15; // iload
    private static final int IINC_INSTRUCTION = 0x84; // iinc
    private static final int WIDE_INSTRUCTION = 0xc4; // wide

    private static final int START = 1;
    private static final String MNEMONIC = "foo";
    private static final int INDEX = 2;
    private static final int VALUE = 3;
    private static final String DESCRIPTOR = "i";

    private Instruction mockInstruction;
    private LocalVariable mockLocalVariable;
    private PrintWriter mockPrinter;

    private TextPrinter sut;

    protected void setUp() throws Exception {
        super.setUp();

        setImposteriser(ClassImposteriser.INSTANCE);

        mockInstruction = mock(Instruction.class);
        mockLocalVariable = mock(LocalVariable.class);
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

    public void testVisitCode_attribute() {
        final Code_attribute mockCode = mock(Code_attribute.class);

        checking(new Expectations() {{
            one (mockPrinter).print("        CODE");

            one (mockCode).iterator();
            atLeast(1).of (mockCode).getExceptionHandlers();

            ignoring (mockCode).getAttributes();
            ignoring (mockPrinter);
        }});

        sut.visitCode_attribute(mockCode);
    }

    public void testVisitInstruction_iinc() {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(IINC_INSTRUCTION));

            one (mockInstruction).getStart();
                will(returnValue(START));
            one (mockPrinter).print(START);

            one (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));
            one (mockPrinter).print(MNEMONIC);

            one (mockInstruction).getIndex();
                will(returnValue(INDEX));
            one (mockPrinter).print(INDEX);

            one (mockInstruction).getIndexedLocalVariable();
                will(returnValue(mockLocalVariable));
            one (mockLocalVariable).getDescriptor();
                will(returnValue(DESCRIPTOR));
            one (mockLocalVariable).getName();

            one (mockInstruction).getValue();
                will(returnValue(VALUE));
            one (mockPrinter).print(VALUE);

            ignoring (mockPrinter);
        }});

        sut.visitInstruction(mockInstruction);
    }

    public void testVisitInstruction_iconst_1() {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(ICONST_1_INSTRUCTION));

            one (mockInstruction).getStart();
                will(returnValue(START));
            one (mockPrinter).print(START);

            one (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));
            one (mockPrinter).print(MNEMONIC);

            ignoring (mockPrinter);
        }});

        sut.visitInstruction(mockInstruction);
    }

    public void testVisitInstruction_iload() {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(ILOAD_INSTRUCTION));

            one (mockInstruction).getStart();
                will(returnValue(START));
            one (mockPrinter).print(START);

            one (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));
            one (mockPrinter).print(MNEMONIC);

            one (mockInstruction).getIndex();
                will(returnValue(INDEX));
            one (mockPrinter).print(INDEX);

            one (mockInstruction).getIndexedLocalVariable();
                will(returnValue(mockLocalVariable));
            one (mockLocalVariable).getDescriptor();
                will(returnValue(DESCRIPTOR));
            one (mockLocalVariable).getName();

            ignoring (mockPrinter);
        }});

        sut.visitInstruction(mockInstruction);
    }

    public void testVisitInstruction_wide_iinc() {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(WIDE_INSTRUCTION));

            one (mockInstruction).getStart();
                will(returnValue(START));
            one (mockPrinter).print(START);

            one (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));
            one (mockPrinter).print(MNEMONIC);

            one (mockInstruction).getByte(1);
                will(returnValue(IINC_INSTRUCTION));

            one (mockInstruction).getIndex();
                will(returnValue(INDEX));
            one (mockPrinter).print(INDEX);

            one (mockInstruction).getIndexedLocalVariable();
                will(returnValue(mockLocalVariable));
            one (mockLocalVariable).getDescriptor();
                will(returnValue(DESCRIPTOR));
            one (mockLocalVariable).getName();

            one (mockInstruction).getValue();
                will(returnValue(VALUE));
            one (mockPrinter).print(VALUE);

            ignoring (mockPrinter);
        }});

        sut.visitInstruction(mockInstruction);
    }

    public void testVisitInstruction_wide_iload() {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(WIDE_INSTRUCTION));

            one (mockInstruction).getStart();
                will(returnValue(START));
            one (mockPrinter).print(START);

            one (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));
            one (mockPrinter).print(MNEMONIC);

            one (mockInstruction).getByte(1);
                will(returnValue(ILOAD_INSTRUCTION));

            one (mockInstruction).getIndex();
                will(returnValue(INDEX));
            one (mockPrinter).print(INDEX);

            one (mockInstruction).getIndexedLocalVariable();
                will(returnValue(mockLocalVariable));
            one (mockLocalVariable).getDescriptor();
                will(returnValue(DESCRIPTOR));
            one (mockLocalVariable).getName();

            ignoring (mockPrinter);
        }});

        sut.visitInstruction(mockInstruction);
    }
}
