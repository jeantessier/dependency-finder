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

import java.io.*;
import java.util.*;

import org.jmock.*;
import org.jmock.integration.junit3.*;
import org.jmock.lib.legacy.*;

public class TestTextPrinter extends MockObjectTestCase {
    private static final int ICONST_1_INSTRUCTION = 0x04; // iconst_1
    private static final int ILOAD_INSTRUCTION = 0x15; // iload
    private static final int ILOAD_1_INSTRUCTION = 0x1b; // iload_1
    private static final int IINC_INSTRUCTION = 0x84; // iinc
    private static final int GOTO_INSTRUCTION = 0xa7; // goto
    private static final int GETFIELD_INSTRUCTION = 0xb4; // getfield
    private static final int WIDE_INSTRUCTION = 0xc4; // wide

    private static final int START = 1;
    private static final String MNEMONIC = "foo";
    private static final int INDEX = 2;
    private static final int OFFSET = 3;
    private static final int VALUE = 4;
    private static final String DESCRIPTOR = "i";

    private ConstantPoolEntry mockConstantPoolEntry;
    private Instruction mockInstruction;
    private LocalVariable mockLocalVariable;
    private PrintWriter mockPrinter;

    private TextPrinter sut;

    protected void setUp() throws Exception {
        super.setUp();

        setImposteriser(ClassImposteriser.INSTANCE);

        mockConstantPoolEntry = mock(ConstantPoolEntry.class);
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

            one (mockMethod).getCode();
                will(returnValue(mockCode));
            one (mockCode).accept(sut);

            ignoring (mockPrinter);
        }});

        sut.visitMethod_info(mockMethod);
    }

    public void testVisitCode_attribute_WithoutExceptionHandlers() {
        final Code_attribute mockCode = mock(Code_attribute.class);

        checking(new Expectations() {{
            one (mockPrinter).print("        CODE");

            one (mockCode).iterator();
            one (mockCode).getExceptionHandlers();
                will(returnValue(Collections.EMPTY_LIST));

            ignoring (mockPrinter).println();
        }});

        sut.visitCode_attribute(mockCode);
    }

    public void testVisitCode_attribute_WithExceptionHandlers() {
        final Code_attribute mockCode = mock(Code_attribute.class);
        final ExceptionHandler mockExceptionHandler = mock(ExceptionHandler.class);

        checking(new Expectations() {{
            one (mockPrinter).print("        CODE");
            one (mockPrinter).print("        EXCEPTION HANDLING");

            one (mockCode).iterator();
            one (mockCode).getExceptionHandlers();
                will(returnValue(Collections.singleton(mockExceptionHandler)));
            one (mockExceptionHandler).accept(sut);

            ignoring (mockPrinter).println();
        }});

        sut.visitCode_attribute(mockCode);
    }

    public void testVisitInstruction_iinc_WithLocalVariableAndIndexAndValue() {
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

    public void testVisitInstruction_iconst_1_WithNothing() {
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

    public void testVisitInstruction_goto_WithNegativeOffset() {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(GOTO_INSTRUCTION));

            atLeast(1).of (mockInstruction).getStart();
                will(returnValue(START));
            one (mockPrinter).print(START);

            one (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));
            one (mockPrinter).print(MNEMONIC);

            atLeast(1).of (mockInstruction).getOffset();
                will(returnValue(-OFFSET));
            one (mockPrinter).print(-OFFSET);
            one (mockPrinter).print(START - OFFSET);

            ignoring (mockPrinter);
        }});

        sut.visitInstruction(mockInstruction);
    }

    public void testVisitInstruction_goto_WithOffsetOfZero() {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(GOTO_INSTRUCTION));

            atLeast(1).of (mockInstruction).getStart();
                will(returnValue(START));
            one (mockPrinter).print(START);

            one (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));
            one (mockPrinter).print(MNEMONIC);

            atLeast(1).of (mockInstruction).getOffset();
                will(returnValue(0));
            one (mockPrinter).print("+");
            one (mockPrinter).print(0);
            one (mockPrinter).print(START);

            ignoring (mockPrinter);
        }});

        sut.visitInstruction(mockInstruction);
    }

    public void testVisitInstruction_goto_WithPositiveOffset() {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(GOTO_INSTRUCTION));

            atLeast(1).of (mockInstruction).getStart();
                will(returnValue(START));
            one (mockPrinter).print(START);

            one (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));
            one (mockPrinter).print(MNEMONIC);

            atLeast(1).of (mockInstruction).getOffset();
                will(returnValue(OFFSET));
            one (mockPrinter).print("+");
            one (mockPrinter).print(OFFSET);
            one (mockPrinter).print(START + OFFSET);

            ignoring (mockPrinter);
        }});

        sut.visitInstruction(mockInstruction);
    }

    public void testVisitInstruction_iload_WithLocalVariableAndIndex() {
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

    public void testVisitInstruction_iload_1_WithLocalVariableButNoIndex() {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(ILOAD_1_INSTRUCTION));

            one (mockInstruction).getStart();
                will(returnValue(START));
            one (mockPrinter).print(START);

            one (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));
            one (mockPrinter).print(MNEMONIC);

            one (mockInstruction).getIndexedLocalVariable();
                will(returnValue(mockLocalVariable));
            one (mockLocalVariable).getDescriptor();
                will(returnValue(DESCRIPTOR));
            one (mockLocalVariable).getName();

            ignoring (mockPrinter);
        }});

        sut.visitInstruction(mockInstruction);
    }

    public void testVisitInstruction_iload_1_WithMissingLocalVariableAndNoIndex() {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(ILOAD_1_INSTRUCTION));

            one (mockInstruction).getStart();
                will(returnValue(START));
            one (mockPrinter).print(START);

            one (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));
            one (mockPrinter).print(MNEMONIC);

            one (mockInstruction).getIndexedLocalVariable();
                will(returnValue(null));

            ignoring (mockPrinter);
        }});

        sut.visitInstruction(mockInstruction);
    }

    public void testVisitInstruction_getfield_WithConstantPoolEntry() {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(GETFIELD_INSTRUCTION));

            one (mockInstruction).getStart();
                will(returnValue(START));
            one (mockPrinter).print(START);

            one (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));
            one (mockPrinter).print(MNEMONIC);

            one (mockInstruction).getIndexedConstantPoolEntry();
                will(returnValue(mockConstantPoolEntry));
            one (mockConstantPoolEntry).accept(sut);

            ignoring (mockPrinter);
        }});

        sut.visitInstruction(mockInstruction);
    }

    public void testVisitInstruction_wide_iinc_WithLocalVariableAndIndexAndValue() {
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

    public void testVisitInstruction_wide_iload_WithLocalVariableAndIndex() {
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

    public void testVisitExceptionHandler_WithCatchType() {
        final int startPc = 1;
        final int endPc = 2;
        final int handlerPc = 3;
        final int catchTypeIndex = 4;
        final String catchType = "foo";

        final ExceptionHandler mockExceptionHandler = mock(ExceptionHandler.class);

        checking(new Expectations() {{
            one (mockExceptionHandler).getStartPC();
                will(returnValue(startPc));
            one (mockPrinter).print(startPc);

            one (mockExceptionHandler).getEndPC();
                will(returnValue(endPc));
            one (mockPrinter).print(endPc);

            one (mockExceptionHandler).getHandlerPC();
                will(returnValue(handlerPc));
            one (mockPrinter).print(handlerPc);

            one (mockExceptionHandler).getCatchTypeIndex();
                will(returnValue(catchTypeIndex));
            one (mockExceptionHandler).getCatchType();
                will(returnValue(catchType));
            one (mockPrinter).print(catchType);

            ignoring (mockPrinter);
        }});

        sut.visitExceptionHandler(mockExceptionHandler);
    }

    public void testVisitExceptionHandler_WithoutCatchType() {
        final int startPc = 1;
        final int endPc = 2;
        final int handlerPc = 3;
        final int catchTypeIndex = 0;

        final ExceptionHandler mockExceptionHandler = mock(ExceptionHandler.class);

        checking(new Expectations() {{
            one (mockExceptionHandler).getStartPC();
                will(returnValue(startPc));
            one (mockPrinter).print(startPc);

            one (mockExceptionHandler).getEndPC();
                will(returnValue(endPc));
            one (mockPrinter).print(endPc);

            one (mockExceptionHandler).getHandlerPC();
                will(returnValue(handlerPc));
            one (mockPrinter).print(handlerPc);

            one (mockExceptionHandler).getCatchTypeIndex();
                will(returnValue(catchTypeIndex));

            ignoring (mockPrinter);
        }});

        sut.visitExceptionHandler(mockExceptionHandler);
    }
}
