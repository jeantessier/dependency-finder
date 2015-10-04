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

import junit.framework.*;

public class TestPackageMapper extends TestCase {
    private static final String TEST_CLASSPATH = "tests" + File.separator + "JarJarDiff" + File.separator + "old";
    private static final String TEST_PACKAGENAME1 = "ModifiedPackage";
    private static final String TEST_FILENAME1  = TEST_CLASSPATH + File.separator + TEST_PACKAGENAME1 + File.separator + "ModifiedClass.class";
    private static final String TEST_CLASSNAME1 = TEST_PACKAGENAME1 + ".ModifiedClass";
    private static final String TEST_FILENAME2 = TEST_CLASSPATH + File.separator + TEST_PACKAGENAME1 + File.separator + "ModifiedInterface.class";
    private static final String TEST_CLASSNAME2 = TEST_PACKAGENAME1 + ".ModifiedInterface";
    private static final String TEST_PACKAGENAME3 = "UnmodifiedPackage";
    private static final String TEST_FILENAME3 = TEST_CLASSPATH + File.separator + TEST_PACKAGENAME3 + File.separator + "UnmodifiedClass.class";
    private static final String TEST_CLASSNAME3 = TEST_PACKAGENAME3 + ".UnmodifiedClass";

    private PackageMapper mapper;

    private ClassfileLoader loader;

    protected void setUp() throws Exception {
        super.setUp();

        mapper = new PackageMapper();

        loader = new AggregatingClassfileLoader();
        Collection<String> filenames = new ArrayList<String>();
        filenames.add(TEST_FILENAME1);
        filenames.add(TEST_FILENAME2);
        filenames.add(TEST_FILENAME3);
        loader.load(filenames);
    }

    public void testBeginSession() {
        mapper.beginSession(new LoadEvent(loader, null, null, null));

        assertEquals("Nb package names", 0, mapper.getPackageNames().size());
    }

    public void testBeginGroup() {
        mapper.beginGroup(new LoadEvent(loader, TEST_CLASSPATH, null, null));

        assertEquals("Nb package names", 0, mapper.getPackageNames().size());
    }

    public void testBeginFile() {
        mapper.beginFile(new LoadEvent(loader, TEST_CLASSPATH, TEST_FILENAME1, null));

        assertEquals("Nb package names", 0, mapper.getPackageNames().size());
    }

    public void testBeginClassfile() {
        mapper.beginClassfile(new LoadEvent(loader, TEST_CLASSPATH, TEST_FILENAME1, null));

        assertEquals("Nb package names", 0, mapper.getPackageNames().size());
    }

    public void testOneClassfile() {
        Classfile classfile = loader.getClassfile(TEST_CLASSNAME1);

        mapper.endClassfile(new LoadEvent(loader, TEST_CLASSPATH, TEST_FILENAME1, classfile));

        assertEquals("Nb package names", 1, mapper.getPackageNames().size());
        assertTrue(TEST_PACKAGENAME1 + " not in " + mapper.getPackageNames(), mapper.getPackageNames().contains(TEST_PACKAGENAME1));
        assertEquals("Nb classes in " + TEST_PACKAGENAME1, 1, mapper.getPackage(TEST_PACKAGENAME1).size());
        assertSame(TEST_CLASSNAME1, classfile, mapper.getPackage(TEST_PACKAGENAME1).get(TEST_CLASSNAME1));
    }

    public void testTwoClassfilesInSamePackages() {
        Classfile classfile1 = loader.getClassfile(TEST_CLASSNAME1);
        Classfile classfile2 = loader.getClassfile(TEST_CLASSNAME2);

        mapper.endClassfile(new LoadEvent(loader, TEST_CLASSPATH, TEST_FILENAME1, classfile1));
        mapper.endClassfile(new LoadEvent(loader, TEST_CLASSPATH, TEST_FILENAME2, classfile2));

        assertEquals("Nb package names", 1, mapper.getPackageNames().size());
        assertTrue(TEST_PACKAGENAME1, mapper.getPackageNames().contains(TEST_PACKAGENAME1));
        assertEquals("Nb classes in " + TEST_PACKAGENAME1, 2, mapper.getPackage(TEST_PACKAGENAME1).size());
        assertSame(TEST_CLASSNAME1, classfile1, mapper.getPackage(TEST_PACKAGENAME1).get(TEST_CLASSNAME1));
        assertSame(TEST_CLASSNAME2, classfile2, mapper.getPackage(TEST_PACKAGENAME1).get(TEST_CLASSNAME2));
    }

    public void testTwoClassfilesInDifferentPackages() {
        Classfile classfile1 = loader.getClassfile(TEST_CLASSNAME1);
        Classfile classfile3 = loader.getClassfile(TEST_CLASSNAME3);

        mapper.endClassfile(new LoadEvent(loader, TEST_CLASSPATH, TEST_FILENAME1, classfile1));
        mapper.endClassfile(new LoadEvent(loader, TEST_CLASSPATH, TEST_FILENAME3, classfile3));

        assertEquals("Nb package names", 2, mapper.getPackageNames().size());
        assertTrue(TEST_PACKAGENAME1, mapper.getPackageNames().contains(TEST_PACKAGENAME1));
        assertTrue(TEST_PACKAGENAME3, mapper.getPackageNames().contains(TEST_PACKAGENAME3));
        assertEquals("Nb classes in " + TEST_PACKAGENAME1, 1, mapper.getPackage(TEST_PACKAGENAME1).size());
        assertSame(TEST_CLASSNAME1, classfile1, mapper.getPackage(TEST_PACKAGENAME1).get(TEST_CLASSNAME1));
        assertEquals("Nb classes in " + TEST_PACKAGENAME3, 1, mapper.getPackage(TEST_PACKAGENAME3).size());
        assertSame(TEST_CLASSNAME3, classfile3, mapper.getPackage(TEST_PACKAGENAME3).get(TEST_CLASSNAME3));
    }

    public void testEndFile() {
        mapper.endFile(new LoadEvent(loader, TEST_CLASSPATH, TEST_FILENAME1, null));

        assertEquals("Nb package names", 0, mapper.getPackageNames().size());
    }

    public void testEndGroup() {
        mapper.endGroup(new LoadEvent(loader, TEST_CLASSPATH, null, null));

        assertEquals("Nb package names", 0, mapper.getPackageNames().size());
    }

    public void testSession() {
        mapper.endSession(new LoadEvent(loader, null, null, null));

        assertEquals("Nb package names", 0, mapper.getPackageNames().size());
    }
}
