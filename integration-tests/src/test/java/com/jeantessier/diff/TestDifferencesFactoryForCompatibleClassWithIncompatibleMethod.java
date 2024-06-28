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

package com.jeantessier.diff;

import java.nio.file.*;
import java.util.*;

import junit.framework.*;

import com.jeantessier.classreader.*;

/**
 * 
 */
public class TestDifferencesFactoryForCompatibleClassWithIncompatibleMethod extends TestCase {
    public static final String OLD_PUBLISHED_CLASSPATH = Paths.get("jarjardiff/old-published/build/libs/old-published.jar").toString();
    public static final String NEW_PUBLISHED_CLASSPATH = Paths.get("jarjardiff/new-published/build/libs/new-published.jar").toString();

    private ClassfileLoader oldPublishedJar;
    private ClassfileLoader newPublishedJar;

    private DifferencesFactory factory;

    public ClassfileLoader getOldPublishedJar() {
        return oldPublishedJar;
    }

    public ClassfileLoader getNewPublishedJar() {
        return newPublishedJar;
    }

    protected void setUp() throws Exception {
        super.setUp();

        oldPublishedJar = new AggregatingClassfileLoader();
        oldPublishedJar.load(Collections.singleton(OLD_PUBLISHED_CLASSPATH));

        newPublishedJar = new AggregatingClassfileLoader();
        newPublishedJar.load(Collections.singleton(NEW_PUBLISHED_CLASSPATH));

        factory = new DifferencesFactory(new IncompatibleDifferenceStrategy(new NoDifferenceStrategy()));
    }

    public void testCompatibleClassWithIncompatibleMethod() {
        String className = "ModifiedPackage.CompatibleClass";
        Classfile oldClass = getOldPublishedJar().getClassfile(className);
        Classfile newClass = getNewPublishedJar().getClassfile(className);

        ClassDifferences classDifferences = (ClassDifferences) factory.createClassDifferences(className, oldClass, newClass);
        assertFalse(classDifferences.isDeclarationModified());
        assertTrue(classDifferences.isModified());

        var methodDifferences = (MethodDifferences) classDifferences.getFeatureDifferences().parallelStream()
                .filter(differences -> differences.getName().equals(className + ".incompatibleMethod()"))
                .findAny()
                .orElseThrow();

        assertTrue(methodDifferences.isModified());
    }
}
