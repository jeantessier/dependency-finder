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

package com.jeantessier.diff;

import java.io.*;
import java.util.*;

import junit.framework.*;

import com.jeantessier.classreader.*;

public class TestDeprecatableDifferences extends TestCase {
    private DifferencesFactory factory;
    private ClassfileLoader    oldLoader;
    private ClassfileLoader    newLoader;

    protected void setUp() throws Exception {
        factory = new DifferencesFactory();
        
        oldLoader = new AggregatingClassfileLoader();
        oldLoader.load(Collections.singleton("tests" + File.separator + "JarJarDiff" + File.separator + "old"));

        newLoader = new AggregatingClassfileLoader();
        newLoader.load(Collections.singleton("tests" + File.separator + "JarJarDiff" + File.separator + "new"));
    }

    public void testNotDeprecatedNotDeprecatedDifferent() {
        String name = "ModifiedPackage.ModifiedClass";
        Classfile oldClassfile = oldLoader.getClassfile(name);
        assertNotNull(oldClassfile);
        Classfile newClassfile = newLoader.getClassfile(name);
        assertNotNull(newClassfile);
        Differences componentDifferences = factory.createClassDifferences(name, oldClassfile, newClassfile);

        DeprecatableDifferences deprecatedDifferences = new DeprecatableDifferences(componentDifferences, oldClassfile, newClassfile);
        assertTrue("deprecated NewDeprecation()",     !deprecatedDifferences.isNewDeprecation());
        assertTrue("deprecated RemovedDeprecation()", !deprecatedDifferences.isRemovedDeprecation());
    }

    public void testNotDeprecatedNotDeprecatedSame() {
        String name = "ModifiedPackage.ModifiedClass";
        Classfile oldClassfile = newLoader.getClassfile(name);
        assertNotNull(oldClassfile);
        Classfile newClassfile = newLoader.getClassfile(name);
        assertNotNull(newClassfile);
        Differences componentDifferences = new ClassDifferences(name, oldClassfile, newClassfile);

        DeprecatableDifferences deprecatedDifferences = new DeprecatableDifferences(componentDifferences, oldClassfile, newClassfile);
        assertTrue("deprecated NewDeprecation()",     !deprecatedDifferences.isNewDeprecation());
        assertTrue("deprecated RemovedDeprecation()", !deprecatedDifferences.isRemovedDeprecation());
    }

    public void testDeprecatedNotDeprecated() {
        String name = "ModifiedPackage.UndeprecatedClass";
        Classfile oldClassfile = oldLoader.getClassfile(name);
        assertNotNull(oldClassfile);
        Classfile newClassfile = newLoader.getClassfile(name);
        assertNotNull(newClassfile);
        Differences componentDifferences = new ClassDifferences(name, oldClassfile, newClassfile);

        DeprecatableDifferences deprecatedDifferences = new DeprecatableDifferences(componentDifferences, oldClassfile, newClassfile);
        assertTrue("deprecated NewDeprecation()",     !deprecatedDifferences.isNewDeprecation());
        assertTrue("deprecated RemovedDeprecation()",  deprecatedDifferences.isRemovedDeprecation());
    }

    public void testNotDeprecatedDeprecated() {
        String name = "ModifiedPackage.DeprecatedClass";
        Classfile oldClassfile = oldLoader.getClassfile(name);
        assertNotNull(oldClassfile);
        Classfile newClassfile = newLoader.getClassfile(name);
        assertNotNull(newClassfile);
        Differences componentDifferences = new ClassDifferences(name, oldClassfile, newClassfile);

        DeprecatableDifferences deprecatedDifferences = new DeprecatableDifferences(componentDifferences, oldClassfile, newClassfile);
        assertTrue("deprecated NewDeprecation()",      deprecatedDifferences.isNewDeprecation());
        assertTrue("deprecated RemovedDeprecation()", !deprecatedDifferences.isRemovedDeprecation());
    }

    public void testDeprecatedDeprecated() {
        String name = "ModifiedPackage.DeprecatedClass";
        Classfile oldClassfile = newLoader.getClassfile(name);
        assertNotNull(oldClassfile);
        Classfile newClassfile = newLoader.getClassfile(name);
        assertNotNull(newClassfile);
        Differences componentDifferences = new ClassDifferences(name, oldClassfile, newClassfile);

        DeprecatableDifferences deprecatedDifferences = new DeprecatableDifferences(componentDifferences, oldClassfile, newClassfile);
        assertTrue("deprecated NewDeprecation()",     !deprecatedDifferences.isNewDeprecation());
        assertTrue("deprecated RemovedDeprecation()", !deprecatedDifferences.isRemovedDeprecation());
    }
}
