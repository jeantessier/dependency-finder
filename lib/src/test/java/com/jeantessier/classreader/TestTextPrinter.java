/*
 *  Copyright (c) 2001-2025, Jean Tessier
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
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import com.jeantessier.MockObjectTestCase;

import static org.junit.jupiter.api.Assertions.*;

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

    private final ConstantPoolEntry mockConstantPoolEntry = mock(ConstantPoolEntry.class);
    private final Instruction mockInstruction = mock(Instruction.class);
    private final LocalVariable mockLocalVariable = mock(LocalVariable.class);
    private final StringWriter out = new StringWriter();

    private final TextPrinter sut = new TextPrinter(new PrintWriter(out));

    @Test
    void testVisitClassfiles_TwoClassfiles_ResetConstantPoolIndices() {
        var mockClassfile1 = mock(Classfile.class, "classfile 1");
        var mockClassfile2 = mock(Classfile.class, "classfile 2");

        List<Classfile> classfiles = new LinkedList<>();
        classfiles.add(mockClassfile1);
        classfiles.add(mockClassfile2);

        checking(new Expectations() {{
            oneOf (mockClassfile1).accept(sut);
            oneOf (mockClassfile2).accept(sut);
        }});

        sut.visitClassfiles(classfiles);

        assertOut();
    }

    @Test
    void testVisitMethod_info_AbstractMethodDoesNotCallToCode_attribute() {
        var methodDeclaration = "int foo()";

        var mockMethod = mock(Method_info.class);

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

    @Test
    void testVisitMethod_info_NativeMethodDoesNotCallsToCode_attribute() {
        var methodDeclaration = "int foo()";

        var mockMethod = mock(Method_info.class);

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

    @Test
    void testVisitMethod_info_CallsToCode_attribute() {
        var methodDeclaration = "int foo()";

        var mockMethod = mock(Method_info.class);
        var mockCode = mock(Code_attribute.class);

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

    @Test
    void testVisitMethod_info_StaticInitializer() {
        var methodDeclaration = "static {}";

        var mockMethod = mock(Method_info.class);
        var mockCode = mock(Code_attribute.class);

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

    @Test
    void testVisitCode_attribute_WithoutExceptionHandlers() {
        var mockCode = mock(Code_attribute.class);

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

    @Test
    void testVisitCode_attribute_WithExceptionHandlers() {
        var mockCode = mock(Code_attribute.class);
        var mockExceptionHandler = mock(ExceptionHandler.class);

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

    @Test
    void testVisitInstruction_iinc_WithLocalVariableAndIndexAndValue() {
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

    @Test
    void testVisitInstruction_iconst_1_WithNothing() {
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

    @Test
    void testVisitInstruction_goto_WithNegativeOffset() {
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

    @Test
    void testVisitInstruction_goto_WithOffsetOfZero() {
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

    @Test
    void testVisitInstruction_goto_WithPositiveOffset() {
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

    @Test
    void testVisitInstruction_iload_WithLocalVariableAndIndex() {
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

    @Test
    void testVisitInstruction_iload_1_WithLocalVariableButNoIndex() {
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

    @Test
    void testVisitInstruction_iload_1_WithMissingLocalVariableAndNoIndex() {
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

    @Test
    void testVisitInstruction_getfield_WithConstantPoolEntry() {
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

    @Test
    void testVisitInstruction_wide_iinc_WithLocalVariableAndIndexAndValue() {
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

    @Test
    void testVisitInstruction_wide_iload_WithLocalVariableAndIndex() {
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

    @Test
    void testVisitExceptionHandler_WithCatchType() {
        var startPc = 1;
        var endPc = 2;
        var handlerPc = 3;
        var catchType = "foo";

        var mockExceptionHandler = mock(ExceptionHandler.class);

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

    @Test
    void testVisitExceptionHandler_WithoutCatchType() {
        var startPc = 1;
        var endPc = 2;
        var handlerPc = 3;

        var mockExceptionHandler = mock(ExceptionHandler.class);

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

    private void assertOut(String ...expectedLines) {
        assertLinesMatch(Stream.of(expectedLines), out.toString().lines());
    }
}
