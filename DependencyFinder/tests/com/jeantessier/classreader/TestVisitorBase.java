/*
 *  Copyright (c) 2001-2007, Jean Tessier
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
import java.io.*;

import junit.framework.*;

public class TestVisitorBase extends TestCase {
    public static final String TEST_CLASS    = "test";
    public static final String TEST_FILENAME = "classes" + File.separator + "test.class";

    private Classfile classfile;
    private MockVisitor visitor;

    protected void setUp() throws Exception {
        ClassfileLoader loader = new AggregatingClassfileLoader();
        loader.load(Collections.singleton(TEST_FILENAME));
        classfile = loader.getClassfile(TEST_CLASS);

        visitor = new MockVisitor();
    }

    public void testVisitCode_attribute() {
        Code_attribute code = classfile.getMethod("main(java.lang.String[])").getCode();

        code.accept(visitor);

        assertEquals("visited ConstantValue_attributes", 0, visitor.getVisitedConstantValue_attributes().size());
        assertEquals("visited Code_attributes", 1, visitor.getVisitedCode_attributes().size());
        assertEquals("visited Exceptions_attributes", 0, visitor.getVisitedExceptions_attributes().size());
        assertEquals("visited InnerClasses_attributes", 0, visitor.getVisitedInnerClasses_attributes().size());
        assertEquals("visited Synthetic_attributes", 0, visitor.getVisitedSynthetic_attributes().size());
        assertEquals("visited SourceFile_attributes", 0, visitor.getVisitedSourceFile_attributes().size());
        assertEquals("visited LineNumberTable_attributes", 1, visitor.getVisitedLineNumberTable_attributes().size());
        assertEquals("visited LocalVariableTable_attributes", 1, visitor.getVisitedLocalVariableTable_attributes().size());
        assertEquals("visited Deprecated_attributes", 0, visitor.getVisitedDeprecated_attributes().size());
        assertEquals("visited Custom_attributes", 0, visitor.getVisitedCustom_attributes().size());
        assertEquals("visited Instructions", 11, visitor.getVisitedInstructions().size());
        assertEquals("visited ExceptionHandlers", 1, visitor.getVisitedExceptionHandlers().size());
        assertEquals("visited InnerClasses", 0, visitor.getVisitedInnerClasses().size());
        assertEquals("visited LineNumbers", 5, visitor.getVisitedLineNumbers().size());
        assertEquals("visited LocalVariables", 3, visitor.getVisitedLocalVariables().size());
    }

    public void testVisitInstruction() {
        Iterator i = classfile.getMethod("main(java.lang.String[])").getCode().iterator();
        Instruction instruction = (Instruction) i.next();

        instruction.accept(visitor);

        assertEquals("visited Instructions", 1, visitor.getVisitedInstructions().size());
    }
}
