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

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestDeprecationPrinter {
    private static final String NEW_CLASSPATH = Paths.get("jarjardiff/new/build/libs/new.jar").toString();

    private ClassfileLoader loader = new AggregatingClassfileLoader();
    private final StringWriter writer = new StringWriter();
    private final DeprecationPrinter printer = new DeprecationPrinter(new PrintWriter(writer));
    
    @BeforeEach
    void setUp() {
        loader.load(NEW_CLASSPATH);
    }
    
    @Test
    void testOneNonDeprecatedClass() {
        var expectedLines = Stream.<String>empty();

        loader.getClassfile("NewPackage.NewClass").accept(printer);

        assertLinesMatch(expectedLines, writer.toString().lines());
    }
    
    @Test
    void testOneDeprecatedInterface() {
        var expectedLines = Stream.of(
                "ModifiedPackage.DeprecatedInterfaceByAnnotation"
        );

        loader.getClassfile("ModifiedPackage.DeprecatedInterfaceByAnnotation").accept(printer);

        assertLinesMatch(expectedLines, writer.toString().lines());
    }
    
    @Test
    void testDeprecatedMethods() {
        var expectedLines = Stream.of(
                "ModifiedPackage.ModifiedClass.deprecatedFieldByAnnotation",
                "ModifiedPackage.ModifiedClass.deprecatedFieldByJavadocTag",
                "ModifiedPackage.ModifiedClass.ModifiedClass(int)",
                "ModifiedPackage.ModifiedClass.ModifiedClass(long)",
                "ModifiedPackage.ModifiedClass.deprecatedMethodByAnnotation()",
                "ModifiedPackage.ModifiedClass.deprecatedMethodByJavadocTag()"
        );

        loader.getClassfile("ModifiedPackage.ModifiedClass").accept(printer);

        assertLinesMatch(expectedLines, writer.toString().lines());
    }
    
    @Test
    void testListenerBehavior() {
        var expectedLines = Stream.of(
                "ModifiedPackage.DeprecatedClassByAnnotation",
                "ModifiedPackage.DeprecatedClassByAnnotation.DeprecatedClassByAnnotation()",
                "ModifiedPackage.DeprecatedClassByJavadocTag",
                "ModifiedPackage.DeprecatedClassByJavadocTag.DeprecatedClassByJavadocTag()",
                "ModifiedPackage.DeprecatedInterfaceByAnnotation",
                "ModifiedPackage.DeprecatedInterfaceByJavadocTag",
                "ModifiedPackage.ModifiedClass.deprecatedFieldByAnnotation",
                "ModifiedPackage.ModifiedClass.deprecatedFieldByJavadocTag",
                "ModifiedPackage.ModifiedClass.ModifiedClass(int)",
                "ModifiedPackage.ModifiedClass.ModifiedClass(long)",
                "ModifiedPackage.ModifiedClass.deprecatedMethodByAnnotation()",
                "ModifiedPackage.ModifiedClass.deprecatedMethodByJavadocTag()",
                "ModifiedPackage.ModifiedInterface.deprecatedFieldByAnnotation",
                "ModifiedPackage.ModifiedInterface.deprecatedFieldByJavadocTag",
                "ModifiedPackage.ModifiedInterface.deprecatedMethodByAnnotation()",
                "ModifiedPackage.ModifiedInterface.deprecatedMethodByJavadocTag()"
        );

        loader = new TransientClassfileLoader();
        loader.addLoadListener(new LoadListenerVisitorAdapter(printer));
        loader.load(NEW_CLASSPATH);

        assertLinesMatch(expectedLines, writer.toString().lines());
    }
}
