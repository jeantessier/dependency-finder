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

import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestClassfile {
    private final ClassfileLoader loader = new AggregatingClassfileLoader();

    @BeforeEach
    void setUp() {
        loader.load(Collections.singleton(Paths.get("jarjardiff/new/build/classes/java/main").toString()));
    }

    @Test
    void testIsDeprecated() {
        assertTrue(loader.getClassfile("ModifiedPackage.DeprecatedClassByAnnotation").isDeprecated(), "ModifiedPackage.DeprecatedClassByAnnotation");
        assertTrue(loader.getClassfile("ModifiedPackage.DeprecatedClassByJavadocTag").isDeprecated(), "ModifiedPackage.DeprecatedClassByJavadocTag");
        assertTrue(loader.getClassfile("ModifiedPackage.DeprecatedInterfaceByAnnotation").isDeprecated(), "ModifiedPackage.DeprecatedInterfaceByAnnotation");
        assertTrue(loader.getClassfile("ModifiedPackage.DeprecatedInterfaceByJavadocTag").isDeprecated(), "ModifiedPackage.DeprecatedInterfaceByJavadocTag");
        assertFalse(loader.getClassfile("ModifiedPackage.UndeprecatedClassByAnnotation").isDeprecated(), "ModifiedPackage.UndeprecatedClassByAnnotation");
        assertFalse(loader.getClassfile("ModifiedPackage.UndeprecatedClassByJavadocTag").isDeprecated(), "ModifiedPackage.UndeprecatedClassByJavadocTag");
        assertFalse(loader.getClassfile("ModifiedPackage.UndeprecatedInterfaceByAnnotation").isDeprecated(), "ModifiedPackage.UndeprecatedInterfaceByAnnotation");
        assertFalse(loader.getClassfile("ModifiedPackage.UndeprecatedInterfaceByJavadocTag").isDeprecated(), "ModifiedPackage.UndeprecatedInterfaceByJavadocTag");
    }

    @Test
    void testGetCode() {
        Classfile classfile1 = loader.getClassfile("UnmodifiedPackage.UnmodifiedClass");
        assertNotNull(classfile1.getMethod(m -> m.getSignature().equals("unmodifiedMethod()")).getCode(), "UnmodifiedPackage.UnmodifiedClass.unmodifiedMethod()");
        Classfile classfile = loader.getClassfile("UnmodifiedPackage.UnmodifiedInterface");
        assertNull(classfile.getMethod(m -> m.getSignature().equals("unmodifiedMethod()")).getCode(), "UnmodifiedPackage.UnmodifiedInterface.unmodifiedMethod()");
    }

    @Test
    void testGetConstantValue() {
        Classfile classfile1 = loader.getClassfile("UnmodifiedPackage.UnmodifiedClass");
        assertNull(classfile1.getField(f -> f.getName().equals("unmodifiedField")).getConstantValue(), "UnmodifiedPackage.UnmodifiedClass.unmodifiedField");
        Classfile classfile = loader.getClassfile("UnmodifiedPackage.UnmodifiedInterface");
        assertNotNull(classfile.getField(f -> f.getName().equals("unmodifiedField")).getConstantValue(), "UnmodifiedPackage.UnmodifiedInterface.unmodifiedField");
    }
}
