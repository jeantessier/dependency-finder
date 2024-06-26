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

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.integration.junit3.MockObjectTestCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

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
    private StringWriter out;

    private TextPrinter sut;

    protected void setUp() throws Exception {
        super.setUp();

        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);

        mockConstantPoolEntry = mock(ConstantPoolEntry.class);
        mockInstruction = mock(Instruction.class);
        mockLocalVariable = mock(LocalVariable.class);
        out = new StringWriter();

        sut = new TextPrinter(new PrintWriter(out));
    }

    public void testVisitClassfiles_TwoClassfiles_ResetConstantPoolIndices() throws IOException {
        final Classfile mockClassfile1 = mock(Classfile.class, "classfile 1");
        final Classfile mockClassfile2 = mock(Classfile.class, "classfile 2");

        List<Classfile> classfiles = new LinkedList<Classfile>();
        classfiles.add(mockClassfile1);
        classfiles.add(mockClassfile2);

        checking(new Expectations() {{
            oneOf (mockClassfile1).accept(sut);
            oneOf (mockClassfile2).accept(sut);
        }});

        sut.visitClassfiles(classfiles);

        assertOut();
    }

    public void testVisitMethod_info_AbstractMethodDoesNotCallToCode_attribute() throws IOException {
        final String methodDeclaration = "int foo()";

        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).getDeclaration();
                will(returnValue(methodDeclaration));
            oneOf (mockMethod).isStaticInitializer();
                will(returnValue(false));
            oneOf (mockMethod).isAbstract();
                will(returnValue(true));
        }});

        sut.visitMethod_info(mockMethod);

        assertOut(
                "",
                "    " + methodDeclaration + ";"
        );
    }

    public void testVisitMethod_info_NativeMethodDoesNotCallsToCode_attribute() throws IOException {
        final String methodDeclaration = "int foo()";

        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).getDeclaration();
                will(returnValue(methodDeclaration));
            oneOf (mockMethod).isStaticInitializer();
                will(returnValue(false));
            oneOf (mockMethod).isAbstract();
                will(returnValue(false));
            oneOf (mockMethod).isNative();
                will(returnValue(true));
        }});

        sut.visitMethod_info(mockMethod);

        assertOut(
                "",
                "    " + methodDeclaration + ";"
        );
    }

    public void testVisitMethod_info_CallsToCode_attribute() throws IOException {
        final String methodDeclaration = "int foo()";

        final Method_info mockMethod = mock(Method_info.class);
        final Code_attribute mockCode = mock(Code_attribute.class);

        checking(new Expectations() {{
            oneOf (mockMethod).getDeclaration();
                will(returnValue(methodDeclaration));
            oneOf (mockMethod).isStaticInitializer();
                will(returnValue(false));
            oneOf (mockMethod).isAbstract();
                will(returnValue(false));
            oneOf (mockMethod).isNative();
                will(returnValue(false));
            oneOf (mockMethod).getCode();
                will(returnValue(mockCode));
            oneOf (mockCode).accept(sut);
        }});

        sut.visitMethod_info(mockMethod);

        assertOut(
                "",
                "    " + methodDeclaration + ";"
        );
    }

    public void testVisitMethod_info_StaticInitializer() throws IOException {
        final String methodDeclaration = "static {}";

        final Method_info mockMethod = mock(Method_info.class);
        final Code_attribute mockCode = mock(Code_attribute.class);

        checking(new Expectations() {{
            oneOf (mockMethod).getDeclaration();
                will(returnValue(methodDeclaration));
            oneOf (mockMethod).isStaticInitializer();
                will(returnValue(true));
            oneOf (mockMethod).isAbstract();
                will(returnValue(false));
            oneOf (mockMethod).isNative();
                will(returnValue(false));
            oneOf (mockMethod).getCode();
                will(returnValue(mockCode));
            oneOf (mockCode).accept(sut);
        }});

        sut.visitMethod_info(mockMethod);

        assertOut(
                "",
                "    " + methodDeclaration
        );
    }

    public void testVisitCode_attribute_WithoutExceptionHandlers() throws IOException {
        final Code_attribute mockCode = mock(Code_attribute.class);

        checking(new Expectations() {{
            oneOf (mockCode).forEach(with(any(Consumer.class)));
            oneOf (mockCode).getExceptionHandlers();
                will(returnValue(Collections.EMPTY_LIST));
        }});

        sut.visitCode_attribute(mockCode);

        assertOut(
                "        CODE"
        );
    }

    public void testVisitCode_attribute_WithExceptionHandlers() throws IOException {
        final Code_attribute mockCode = mock(Code_attribute.class);
        final ExceptionHandler mockExceptionHandler = mock(ExceptionHandler.class);

        checking(new Expectations() {{
            oneOf (mockCode).forEach(with(any(Consumer.class)));
            oneOf (mockCode).getExceptionHandlers();
                will(returnValue(Collections.singleton(mockExceptionHandler)));
            oneOf (mockExceptionHandler).accept(sut);
        }});

        sut.visitCode_attribute(mockCode);

        assertOut(
                "        CODE",
                "        EXCEPTION HANDLING"
        );
    }

    public void testVisitInstruction_iinc_WithLocalVariableAndIndexAndValue() throws IOException {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(IINC_INSTRUCTION));

            oneOf (mockInstruction).getStart();
                will(returnValue(START));

            oneOf (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));

            oneOf (mockInstruction).getIndex();
                will(returnValue(INDEX));

            oneOf (mockInstruction).getIndexedLocalVariable();
                will(returnValue(mockLocalVariable));
            oneOf (mockLocalVariable).getDescriptor();
                will(returnValue(DESCRIPTOR));
            oneOf (mockLocalVariable).getName();

            oneOf (mockInstruction).getValue();
                will(returnValue(VALUE));
        }});

        sut.visitInstruction(mockInstruction);

        assertOut(
                "        " + START + ":\t" + MNEMONIC + " null  (#" + INDEX + ") " + VALUE
        );
    }

    public void testVisitInstruction_iconst_1_WithNothing() throws IOException {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(ICONST_1_INSTRUCTION));

            oneOf (mockInstruction).getStart();
                will(returnValue(START));

            oneOf (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));
        }});

        sut.visitInstruction(mockInstruction);

        assertOut(
                "        " + START + ":\t" + MNEMONIC
        );
    }

    public void testVisitInstruction_goto_WithNegativeOffset() throws IOException {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(GOTO_INSTRUCTION));

            atLeast(1).of (mockInstruction).getStart();
                will(returnValue(START));

            oneOf (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));

            atLeast(1).of (mockInstruction).getOffset();
                will(returnValue(-OFFSET));
        }});

        sut.visitInstruction(mockInstruction);

        assertOut(
                "        " + START + ":\t" + MNEMONIC + " " + (START - OFFSET) + " (-" + OFFSET + ")"
        );
    }

    public void testVisitInstruction_goto_WithOffsetOfZero() throws IOException {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(GOTO_INSTRUCTION));

            atLeast(1).of (mockInstruction).getStart();
                will(returnValue(START));

            oneOf (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));

            atLeast(1).of (mockInstruction).getOffset();
                will(returnValue(0));
        }});

        sut.visitInstruction(mockInstruction);

        assertOut(
                "        " + START + ":\t" + MNEMONIC + " " + START + " (+0)"
        );
    }

    public void testVisitInstruction_goto_WithPositiveOffset() throws IOException {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(GOTO_INSTRUCTION));

            atLeast(1).of (mockInstruction).getStart();
                will(returnValue(START));

            oneOf (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));

            atLeast(1).of (mockInstruction).getOffset();
                will(returnValue(OFFSET));
        }});

        sut.visitInstruction(mockInstruction);

        assertOut(
                "        " + START + ":\t" + MNEMONIC + " " + (START + OFFSET) + " (+" + OFFSET + ")"
        );
    }

    public void testVisitInstruction_iload_WithLocalVariableAndIndex() throws IOException {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(ILOAD_INSTRUCTION));

            oneOf (mockInstruction).getStart();
                will(returnValue(START));

            oneOf (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));

            oneOf (mockInstruction).getIndex();
                will(returnValue(INDEX));

            oneOf (mockInstruction).getIndexedLocalVariable();
                will(returnValue(mockLocalVariable));
            oneOf (mockLocalVariable).getDescriptor();
                will(returnValue(DESCRIPTOR));
            oneOf (mockLocalVariable).getName();
        }});

        sut.visitInstruction(mockInstruction);

        assertOut(
                "        " + START + ":\t" + MNEMONIC + " null  (#" + INDEX + ")"
        );
    }

    public void testVisitInstruction_iload_1_WithLocalVariableButNoIndex() throws IOException {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(ILOAD_1_INSTRUCTION));

            oneOf (mockInstruction).getStart();
                will(returnValue(START));

            oneOf (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));

            oneOf (mockInstruction).getIndexedLocalVariable();
                will(returnValue(mockLocalVariable));
            oneOf (mockLocalVariable).getDescriptor();
                will(returnValue(DESCRIPTOR));
            oneOf (mockLocalVariable).getName();
        }});

        sut.visitInstruction(mockInstruction);

        assertOut(
                "        " + START + ":\t" + MNEMONIC + " null "
        );
    }

    public void testVisitInstruction_iload_1_WithMissingLocalVariableAndNoIndex() throws IOException {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(ILOAD_1_INSTRUCTION));

            oneOf (mockInstruction).getStart();
                will(returnValue(START));

            oneOf (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));

            oneOf (mockInstruction).getIndexedLocalVariable();
                will(returnValue(null));
        }});

        sut.visitInstruction(mockInstruction);

        assertOut(
                "        " + START + ":\t" + MNEMONIC
        );
    }

    public void testVisitInstruction_getfield_WithConstantPoolEntry() throws IOException {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(GETFIELD_INSTRUCTION));

            oneOf (mockInstruction).getStart();
                will(returnValue(START));

            oneOf (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));

            oneOf (mockInstruction).getIndexedConstantPoolEntry();
                will(returnValue(mockConstantPoolEntry));
            oneOf (mockConstantPoolEntry).accept(sut);
        }});

        sut.visitInstruction(mockInstruction);

        assertOut(
                "        " + START + ":\t" + MNEMONIC + " "
        );
    }

    public void testVisitInstruction_wide_iinc_WithLocalVariableAndIndexAndValue() throws IOException {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(WIDE_INSTRUCTION));

            oneOf (mockInstruction).getStart();
                will(returnValue(START));

            oneOf (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));

            oneOf (mockInstruction).getByte(1);
                will(returnValue(IINC_INSTRUCTION));

            oneOf (mockInstruction).getIndex();
                will(returnValue(INDEX));

            oneOf (mockInstruction).getIndexedLocalVariable();
                will(returnValue(mockLocalVariable));
            oneOf (mockLocalVariable).getDescriptor();
                will(returnValue(DESCRIPTOR));
            oneOf (mockLocalVariable).getName();

            oneOf (mockInstruction).getValue();
                will(returnValue(VALUE));
        }});

        sut.visitInstruction(mockInstruction);

        assertOut(
                "        " + START + ":\t" + MNEMONIC + " null  (#" + INDEX + ") " + VALUE
        );
    }

    public void testVisitInstruction_wide_iload_WithLocalVariableAndIndex() throws IOException {
        checking(new Expectations() {{
            atLeast(1).of (mockInstruction).getOpcode();
                will(returnValue(WIDE_INSTRUCTION));

            oneOf (mockInstruction).getStart();
                will(returnValue(START));

            oneOf (mockInstruction).getMnemonic();
                will(returnValue(MNEMONIC));

            oneOf (mockInstruction).getByte(1);
                will(returnValue(ILOAD_INSTRUCTION));

            oneOf (mockInstruction).getIndex();
                will(returnValue(INDEX));

            oneOf (mockInstruction).getIndexedLocalVariable();
                will(returnValue(mockLocalVariable));
            oneOf (mockLocalVariable).getDescriptor();
                will(returnValue(DESCRIPTOR));
            oneOf (mockLocalVariable).getName();
        }});

        sut.visitInstruction(mockInstruction);

        assertOut(
                "        " + START + ":\t" + MNEMONIC + " null  (#" + INDEX + ")"
        );
    }

    public void testVisitExceptionHandler_WithCatchType() throws IOException {
        final int startPc = 1;
        final int endPc = 2;
        final int handlerPc = 3;
        final String catchType = "foo";

        final ExceptionHandler mockExceptionHandler = mock(ExceptionHandler.class);

        checking(new Expectations() {{
            oneOf (mockExceptionHandler).getStartPC();
                will(returnValue(startPc));

            oneOf (mockExceptionHandler).getEndPC();
                will(returnValue(endPc));

            oneOf (mockExceptionHandler).getHandlerPC();
                will(returnValue(handlerPc));

            oneOf (mockExceptionHandler).hasCatchType();
                will(returnValue(true));
            oneOf (mockExceptionHandler).getCatchType();
                will(returnValue(catchType));
        }});

        sut.visitExceptionHandler(mockExceptionHandler);

        assertOut(
                "        " + startPc + "-" + endPc + ": " + handlerPc + " (" + catchType + ")"
        );
    }

    public void testVisitExceptionHandler_WithoutCatchType() throws IOException {
        final int startPc = 1;
        final int endPc = 2;
        final int handlerPc = 3;

        final ExceptionHandler mockExceptionHandler = mock(ExceptionHandler.class);

        checking(new Expectations() {{
            oneOf (mockExceptionHandler).getStartPC();
                will(returnValue(startPc));

            oneOf (mockExceptionHandler).getEndPC();
                will(returnValue(endPc));

            oneOf (mockExceptionHandler).getHandlerPC();
                will(returnValue(handlerPc));

            oneOf (mockExceptionHandler).hasCatchType();
                will(returnValue(false));
        }});

        sut.visitExceptionHandler(mockExceptionHandler);

        assertOut(
                "        " + startPc + "-" + endPc + ": " + handlerPc
        );
    }

    private void assertOut(String ...expectedLines) throws IOException {
        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        for (String expectedLine : expectedLines) {
            assertEquals("line " + ++lineNumber, expectedLine, in.readLine());
        }

        assertEquals("End of file", null, in.readLine());
    }
}
