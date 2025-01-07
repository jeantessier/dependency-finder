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

public class TestPackageMapper {
    private static final Path TEST_CLASSPATH = Paths.get("jarjardiff/old/build/classes/java/main");
    private static final String TEST_PACKAGENAME1 = "ModifiedPackage";
    private static final String TEST_FILENAME1 = TEST_CLASSPATH.resolve(TEST_PACKAGENAME1 + "/ModifiedClass.class").toString();
    private static final String TEST_CLASSNAME1 = TEST_PACKAGENAME1 + ".ModifiedClass";
    private static final String TEST_FILENAME2 = TEST_CLASSPATH.resolve(TEST_PACKAGENAME1 + "/ModifiedInterface.class").toString();
    private static final String TEST_CLASSNAME2 = TEST_PACKAGENAME1 + ".ModifiedInterface";
    private static final String TEST_PACKAGENAME3 = "UnmodifiedPackage";
    private static final String TEST_FILENAME3 = TEST_CLASSPATH.resolve(TEST_PACKAGENAME3 + "/UnmodifiedClass.class").toString();
    private static final String TEST_CLASSNAME3 = TEST_PACKAGENAME3 + ".UnmodifiedClass";

    private final PackageMapper mapper = new PackageMapper();

    private final ClassfileLoader loader = new AggregatingClassfileLoader();

    @BeforeEach
    void setUp() {
        loader.load(List.of(TEST_FILENAME1, TEST_FILENAME2, TEST_FILENAME3));
    }

    @Test
    void testBeginSession() {
        mapper.beginSession(new LoadEvent(loader, null, null, null));

        assertEquals(0, mapper.getPackageNames().size(), "Nb package names");
    }

    @Test
    void testBeginGroup() {
        mapper.beginGroup(new LoadEvent(loader, TEST_CLASSPATH.toString(), null, null));

        assertEquals(0, mapper.getPackageNames().size(), "Nb package names");
    }

    @Test
    void testBeginFile() {
        mapper.beginFile(new LoadEvent(loader, TEST_CLASSPATH.toString(), TEST_FILENAME1, null));

        assertEquals(0, mapper.getPackageNames().size(), "Nb package names");
    }

    @Test
    void testBeginClassfile() {
        mapper.beginClassfile(new LoadEvent(loader, TEST_CLASSPATH.toString(), TEST_FILENAME1, null));

        assertEquals(0, mapper.getPackageNames().size(), "Nb package names");
    }

    @Test
    void testOneClassfile() {
        Classfile classfile = loader.getClassfile(TEST_CLASSNAME1);

        mapper.endClassfile(new LoadEvent(loader, TEST_CLASSPATH.toString(), TEST_FILENAME1, classfile));

        assertEquals(1, mapper.getPackageNames().size(), "Nb package names");
        assertTrue(mapper.getPackageNames().contains(TEST_PACKAGENAME1), TEST_PACKAGENAME1 + " not in " + mapper.getPackageNames());
        assertEquals(1, mapper.getPackage(TEST_PACKAGENAME1).size(), "Nb classes in " + TEST_PACKAGENAME1);
        assertSame(classfile, mapper.getPackage(TEST_PACKAGENAME1).get(TEST_CLASSNAME1), TEST_CLASSNAME1);
    }

    @Test
    void testTwoClassfilesInSamePackages() {
        Classfile classfile1 = loader.getClassfile(TEST_CLASSNAME1);
        Classfile classfile2 = loader.getClassfile(TEST_CLASSNAME2);

        mapper.endClassfile(new LoadEvent(loader, TEST_CLASSPATH.toString(), TEST_FILENAME1, classfile1));
        mapper.endClassfile(new LoadEvent(loader, TEST_CLASSPATH.toString(), TEST_FILENAME2, classfile2));

        assertEquals(1, mapper.getPackageNames().size(), "Nb package names");
        assertTrue(mapper.getPackageNames().contains(TEST_PACKAGENAME1), TEST_PACKAGENAME1);
        assertEquals(2, mapper.getPackage(TEST_PACKAGENAME1).size(), "Nb classes in " + TEST_PACKAGENAME1);
        assertSame(classfile1, mapper.getPackage(TEST_PACKAGENAME1).get(TEST_CLASSNAME1), TEST_CLASSNAME1);
        assertSame(classfile2, mapper.getPackage(TEST_PACKAGENAME1).get(TEST_CLASSNAME2), TEST_CLASSNAME2);
    }

    @Test
    void testTwoClassfilesInDifferentPackages() {
        Classfile classfile1 = loader.getClassfile(TEST_CLASSNAME1);
        Classfile classfile3 = loader.getClassfile(TEST_CLASSNAME3);

        mapper.endClassfile(new LoadEvent(loader, TEST_CLASSPATH.toString(), TEST_FILENAME1, classfile1));
        mapper.endClassfile(new LoadEvent(loader, TEST_CLASSPATH.toString(), TEST_FILENAME3, classfile3));

        assertEquals(2, mapper.getPackageNames().size(), "Nb package names");
        assertTrue(mapper.getPackageNames().contains(TEST_PACKAGENAME1), TEST_PACKAGENAME1);
        assertTrue(mapper.getPackageNames().contains(TEST_PACKAGENAME3), TEST_PACKAGENAME3);
        assertEquals(1, mapper.getPackage(TEST_PACKAGENAME1).size(), "Nb classes in " + TEST_PACKAGENAME1);
        assertSame(classfile1, mapper.getPackage(TEST_PACKAGENAME1).get(TEST_CLASSNAME1), TEST_CLASSNAME1);
        assertEquals(1, mapper.getPackage(TEST_PACKAGENAME3).size(), "Nb classes in " + TEST_PACKAGENAME3);
        assertSame(classfile3, mapper.getPackage(TEST_PACKAGENAME3).get(TEST_CLASSNAME3), TEST_CLASSNAME3);
    }

    @Test
    void testEndFile() {
        mapper.endFile(new LoadEvent(loader, TEST_CLASSPATH.toString(), TEST_FILENAME1, null));

        assertEquals(0, mapper.getPackageNames().size(), "Nb package names");
    }

    @Test
    void testEndGroup() {
        mapper.endGroup(new LoadEvent(loader, TEST_CLASSPATH.toString(), null, null));

        assertEquals(0, mapper.getPackageNames().size(), "Nb package names");
    }

    @Test
    void testEndSession() {
        mapper.endSession(new LoadEvent(loader, null, null, null));

        assertEquals(0, mapper.getPackageNames().size(), "Nb package names");
    }
}
