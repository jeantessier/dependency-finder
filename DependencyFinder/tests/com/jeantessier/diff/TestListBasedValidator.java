/*
 *  Copyright (c) 2001-2005, Jean Tessier
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

import junit.framework.*;

public class TestListBasedValidator extends TestCase {
    public void testDefault() throws IOException {
        Validator validator = new ListBasedValidator();

        assertTrue("package", validator.isPackageAllowed("foobar"));
        assertTrue("class",   validator.isClassAllowed("foobar"));
        assertTrue("class",   validator.isClassAllowed("foobar.foobar"));
        assertTrue("feature", validator.isFeatureAllowed("foobar"));
        assertTrue("feature", validator.isFeatureAllowed("foobar.foobar"));
        assertTrue("feature", validator.isFeatureAllowed("foobar.foobar.foobar"));

        assertTrue("package", validator.isPackageAllowed("barfoo"));
        assertTrue("class",   validator.isClassAllowed("barfoo"));
        assertTrue("class",   validator.isClassAllowed("barfoo.barfoo"));
        assertTrue("feature", validator.isFeatureAllowed("barfoo"));
        assertTrue("feature", validator.isFeatureAllowed("barfoo.barfoo"));
        assertTrue("feature", validator.isFeatureAllowed("barfoo.barfoo.barfoo"));
    }

    public void testNullFile() throws IOException {
        Validator validator;

        try {
            validator = new ListBasedValidator((BufferedReader) null);
            fail("Created PackageValidator with null");
        } catch (NullPointerException ex) {
            // Ignore
        }

        validator = new ListBasedValidator(new BufferedReader(new StringReader("")));

        assertTrue("package", validator.isPackageAllowed("foobar"));
        assertTrue("class",   validator.isClassAllowed("foobar"));
        assertTrue("class",   validator.isClassAllowed("foobar.foobar"));
        assertTrue("feature", validator.isFeatureAllowed("foobar"));
        assertTrue("feature", validator.isFeatureAllowed("foobar.foobar"));
        assertTrue("feature", validator.isFeatureAllowed("foobar.foobar.foobar"));

        assertTrue("package", validator.isPackageAllowed("barfoo"));
        assertTrue("class",   validator.isClassAllowed("barfoo"));
        assertTrue("class",   validator.isClassAllowed("barfoo.barfoo"));
        assertTrue("feature", validator.isFeatureAllowed("barfoo"));
        assertTrue("feature", validator.isFeatureAllowed("barfoo.barfoo"));
        assertTrue("feature", validator.isFeatureAllowed("barfoo.barfoo.barfoo"));
    }

    public void testConstructor() throws IOException {
        Validator validator = new ListBasedValidator(new BufferedReader(new StringReader("foobar\n")));

        assertTrue("package", validator.isPackageAllowed("foobar"));
        assertTrue("class",   validator.isClassAllowed("foobar"));
        assertTrue("class",   !validator.isClassAllowed("foobar.foobar"));
        assertTrue("feature", validator.isFeatureAllowed("foobar"));
        assertTrue("feature", !validator.isFeatureAllowed("foobar.foobar"));
        assertTrue("feature", !validator.isFeatureAllowed("foobar.foobar.foobar"));

        assertTrue("package", !validator.isPackageAllowed("barfoo"));
        assertTrue("class",   !validator.isClassAllowed("barfoo"));
        assertTrue("class",   !validator.isClassAllowed("barfoo.barfoo"));
        assertTrue("feature", !validator.isFeatureAllowed("barfoo"));
        assertTrue("feature", !validator.isFeatureAllowed("barfoo.barfoo"));
        assertTrue("feature", !validator.isFeatureAllowed("barfoo.barfoo.barfoo"));
    }

    public void testMissingFile1() throws IOException {
        Validator validator = new ListBasedValidator("no such file");

        assertTrue("package", validator.isPackageAllowed("foobar"));
        assertTrue("class",   validator.isClassAllowed("foobar"));
        assertTrue("class",   validator.isClassAllowed("foobar.foobar"));
        assertTrue("feature", validator.isFeatureAllowed("foobar"));
        assertTrue("feature", validator.isFeatureAllowed("foobar.foobar"));
        assertTrue("feature", validator.isFeatureAllowed("foobar.foobar.foobar"));

        assertTrue("package", validator.isPackageAllowed("barfoo"));
        assertTrue("class",   validator.isClassAllowed("barfoo"));
        assertTrue("class",   validator.isClassAllowed("barfoo.barfoo"));
        assertTrue("feature", validator.isFeatureAllowed("barfoo"));
        assertTrue("feature", validator.isFeatureAllowed("barfoo.barfoo"));
        assertTrue("feature", validator.isFeatureAllowed("barfoo.barfoo.barfoo"));
    }

    public void testMissingFile2() throws IOException {
        File file = new File("no such file");
        assertFalse("File exists", file.exists());
        
        Validator validator = new ListBasedValidator(file);

        assertTrue("package", validator.isPackageAllowed("foobar"));
        assertTrue("class",   validator.isClassAllowed("foobar"));
        assertTrue("class",   validator.isClassAllowed("foobar.foobar"));
        assertTrue("feature", validator.isFeatureAllowed("foobar"));
        assertTrue("feature", validator.isFeatureAllowed("foobar.foobar"));
        assertTrue("feature", validator.isFeatureAllowed("foobar.foobar.foobar"));

        assertTrue("package", validator.isPackageAllowed("barfoo"));
        assertTrue("class",   validator.isClassAllowed("barfoo"));
        assertTrue("class",   validator.isClassAllowed("barfoo.barfoo"));
        assertTrue("feature", validator.isFeatureAllowed("barfoo"));
        assertTrue("feature", validator.isFeatureAllowed("barfoo.barfoo"));
        assertTrue("feature", validator.isFeatureAllowed("barfoo.barfoo.barfoo"));
    }

    public void testMerge() throws IOException {
        ListBasedValidator validator = new ListBasedValidator();

        validator.load(new BufferedReader(new StringReader("foo\nbar")));

        assertTrue("package foo",     validator.isPackageAllowed("foo"));
        assertTrue("package bar",     validator.isPackageAllowed("bar"));
        assertTrue("package foobar", !validator.isClassAllowed("foobar"));
        assertTrue("package barfoo", !validator.isClassAllowed("barfoo"));

        validator.load(new BufferedReader(new StringReader("foobar")));

        assertTrue("package foo",     validator.isPackageAllowed("foo"));
        assertTrue("package bar",     validator.isPackageAllowed("bar"));
        assertTrue("package foobar",  validator.isClassAllowed("foobar"));
        assertTrue("package barfoo", !validator.isClassAllowed("barfoo"));
    }

    public void testConstructorWithSuffix1() throws IOException {
        Validator validator = new ListBasedValidator(new BufferedReader(new StringReader("foobar [P]\n")));

        assertTrue("package", validator.isPackageAllowed("foobar"));
        assertTrue("class",   validator.isClassAllowed("foobar"));
        assertTrue("class",   !validator.isClassAllowed("foobar.foobar"));
        assertTrue("feature", validator.isFeatureAllowed("foobar"));
        assertTrue("feature", !validator.isFeatureAllowed("foobar.foobar"));
        assertTrue("feature", !validator.isFeatureAllowed("foobar.foobar.foobar"));

        assertTrue("package", !validator.isPackageAllowed("barfoo"));
        assertTrue("class",   !validator.isClassAllowed("barfoo"));
        assertTrue("class",   !validator.isClassAllowed("barfoo.barfoo"));
        assertTrue("feature", !validator.isFeatureAllowed("barfoo"));
        assertTrue("feature", !validator.isFeatureAllowed("barfoo.barfoo"));
        assertTrue("feature", !validator.isFeatureAllowed("barfoo.barfoo.barfoo"));
    }

    public void testConstructorWithSuffix2() throws IOException {
        Validator validator = new ListBasedValidator(new BufferedReader(new StringReader("foobar [C]\n")));

        assertTrue("package", validator.isPackageAllowed("foobar"));
        assertTrue("class",   validator.isClassAllowed("foobar"));
        assertTrue("class",   !validator.isClassAllowed("foobar.foobar"));
        assertTrue("feature", validator.isFeatureAllowed("foobar"));
        assertTrue("feature", !validator.isFeatureAllowed("foobar.foobar"));
        assertTrue("feature", !validator.isFeatureAllowed("foobar.foobar.foobar"));

        assertTrue("package", !validator.isPackageAllowed("barfoo"));
        assertTrue("class",   !validator.isClassAllowed("barfoo"));
        assertTrue("class",   !validator.isClassAllowed("barfoo.barfoo"));
        assertTrue("feature", !validator.isFeatureAllowed("barfoo"));
        assertTrue("feature", !validator.isFeatureAllowed("barfoo.barfoo"));
        assertTrue("feature", !validator.isFeatureAllowed("barfoo.barfoo.barfoo"));
    }

    public void testMergeWithSuffix() throws IOException {
        ListBasedValidator validator = new ListBasedValidator();

        validator.load(new BufferedReader(new StringReader("foo [P]\nbar [P]")));

        assertTrue("package foo",     validator.isPackageAllowed("foo"));
        assertTrue("package bar",     validator.isPackageAllowed("bar"));
        assertTrue("package foobar", !validator.isClassAllowed("foobar"));
        assertTrue("package barfoo", !validator.isClassAllowed("barfoo"));

        validator.load(new BufferedReader(new StringReader("foobar [P]")));

        assertTrue("package foo",     validator.isPackageAllowed("foo"));
        assertTrue("package bar",     validator.isPackageAllowed("bar"));
        assertTrue("package foobar",  validator.isClassAllowed("foobar"));
        assertTrue("package barfoo", !validator.isClassAllowed("barfoo"));
    }
}
