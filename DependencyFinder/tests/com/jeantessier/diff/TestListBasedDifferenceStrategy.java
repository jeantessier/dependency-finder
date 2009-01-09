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

import com.jeantessier.classreader.*;

public class TestListBasedDifferenceStrategy extends TestDifferencesFactoryBase {
    private MockDifferenceStrategy mockStrategy;

    protected void setUp() throws Exception {
        super.setUp();

        mockStrategy = new MockDifferenceStrategy(new CodeDifferenceStrategy());
    }

    public void testNullFile() throws IOException {
        try {
            new ListBasedDifferenceStrategy(mockStrategy, (BufferedReader) null);
            fail("Created validator with null");
        } catch (NullPointerException ex) {
            // Expected
        }
    }

    public void testMissingFile1() {
        try {
            new ListBasedDifferenceStrategy(mockStrategy, "no such file");
            fail("Created validator with missing file");
        } catch (IOException ex) {
            // Expected
        }
    }

    public void testMissingFile2() {
        File file = new File("no such file");
        assertFalse("File exists", file.exists());

        try {
            new ListBasedDifferenceStrategy(mockStrategy, file);
            fail("Created validator with missing file");
        } catch (IOException ex) {
            // Expected
        }
    }

    public void testIsPackageDifferentNotInList() throws IOException {
        Map oldPackage = new HashMap();
        oldPackage.put("ModifiedPackage.ModifiedClass", getOldJar().getClassfile("ModifiedPackage.ModifiedClass"));
        Map newPackage = new HashMap();
        newPackage.put("ModifiedPackage.ModifiedClass", getNewJar().getClassfile("ModifiedPackage.ModifiedClass"));

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));
        strategy.isPackageDifferent(oldPackage, newPackage);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testIsClassDifferentNotInList() throws IOException {
        Classfile oldClass = getOldJar().getClassfile("ModifiedPackage.ModifiedClass");
        Classfile newClass = getNewJar().getClassfile("ModifiedPackage.ModifiedClass");

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));
        strategy.isClassDifferent(oldClass, newClass);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testIsFieldDifferentNotInList() throws IOException {
        Field_info oldField = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getField("modifiedField");
        Field_info newField = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getField("modifiedField");

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));
        strategy.isFieldDifferent(oldField, newField);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testIsMethodDifferentNotInList() throws IOException {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedMethod()");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedMethod()");

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));
        strategy.isMethodDifferent(oldMethod, newMethod);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testIsCodeMethodDifferentNotInList() throws IOException {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedCodeMethod()");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedCodeMethod()");

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));
        strategy.isMethodDifferent(oldMethod, newMethod);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testIsCodeDifferentNotInList() throws IOException {
        Code_attribute oldCode = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedCodeMethod()").getCode();
        Code_attribute newCode = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedCodeMethod()").getCode();

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));
        strategy.isCodeDifferent(oldCode, newCode);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     1, mockStrategy.getCodeDifferentCount());
    }

    public void testIsPackageDifferentInList() throws IOException {
        Map oldPackage = new HashMap();
        oldPackage.put("ModifiedPackage.ModifiedClass", getOldJar().getClassfile("ModifiedPackage.ModifiedClass"));
        Map newPackage = new HashMap();
        newPackage.put("ModifiedPackage.ModifiedClass", getNewJar().getClassfile("ModifiedPackage.ModifiedClass"));

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage")));
        strategy.isPackageDifferent(oldPackage, newPackage);

        assertEquals("package",  1, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testIsClassDifferentInList() throws IOException {
        Classfile oldClass = getOldJar().getClassfile("ModifiedPackage.ModifiedClass");
        Classfile newClass = getNewJar().getClassfile("ModifiedPackage.ModifiedClass");

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass")));
        strategy.isClassDifferent(oldClass, newClass);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    1, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testIsFieldDifferentInList() throws IOException {
        Field_info oldField = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getField("modifiedField");
        Field_info newField = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getField("modifiedField");

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass.modifiedField")));
        strategy.isFieldDifferent(oldField, newField);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    1, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testIsMethodDifferentInList() throws IOException {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedMethod()");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedMethod()");

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass.modifiedMethod()")));
        strategy.isMethodDifferent(oldMethod, newMethod);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   1, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testIsCodeMethodDifferentInList() throws IOException {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedCodeMethod()");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedCodeMethod()");

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass.modifiedCodeMethod()")));
        strategy.isMethodDifferent(oldMethod, newMethod);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   1, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testIsCodeDifferentInList() throws IOException {
        Code_attribute oldCode = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedCodeMethod()").getCode();
        Code_attribute newCode = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedCodeMethod()").getCode();

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass.modifiedCodeMethod()")));
        strategy.isCodeDifferent(oldCode, newCode);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     1, mockStrategy.getCodeDifferentCount());
    }

    public void testIsPackageDifferentInListWithSuffix() throws IOException {
        Map oldPackage = new HashMap();
        oldPackage.put("ModifiedPackage.ModifiedClass", getOldJar().getClassfile("ModifiedPackage.ModifiedClass"));
        Map newPackage = new HashMap();
        newPackage.put("ModifiedPackage.ModifiedClass", getNewJar().getClassfile("ModifiedPackage.ModifiedClass"));

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage [P]")));
        strategy.isPackageDifferent(oldPackage, newPackage);

        assertEquals("package",  1, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testIsClassDifferentInListWithSuffix() throws IOException {
        Classfile oldClass = getOldJar().getClassfile("ModifiedPackage.ModifiedClass");
        Classfile newClass = getNewJar().getClassfile("ModifiedPackage.ModifiedClass");

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass [C]")));
        strategy.isClassDifferent(oldClass, newClass);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    1, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testIsFieldDifferentInListWithSuffix() throws IOException {
        Field_info oldField = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getField("modifiedField");
        Field_info newField = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getField("modifiedField");

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass.modifiedField [F]")));
        strategy.isFieldDifferent(oldField, newField);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    1, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testIsMethodDifferentInListWithSuffix() throws IOException {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedMethod()");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedMethod()");

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass.modifiedMethod() [F]")));
        strategy.isMethodDifferent(oldMethod, newMethod);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   1, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testIsCodeMethodDifferentInListWithSuffix() throws IOException {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedCodeMethod()");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedCodeMethod()");

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass.modifiedCodeMethod() [F]")));
        strategy.isMethodDifferent(oldMethod, newMethod);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   1, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testIsCodeDifferentInListWithSuffix() throws IOException {
        Code_attribute oldCode = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedCodeMethod()").getCode();
        Code_attribute newCode = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedCodeMethod()").getCode();

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass.modifiedCodeMethod() [F]")));
        strategy.isCodeDifferent(oldCode, newCode);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     1, mockStrategy.getCodeDifferentCount());
    }

    public void testIsConstantValueDifferent() throws IOException {
        ConstantValue_attribute oldConstantValue = getOldJar().getClassfile("ModifiedPackage.ModifiedInterface").getField("modifiedValueField").getConstantValue();
        ConstantValue_attribute newConstantValue = getNewJar().getClassfile("ModifiedPackage.ModifiedInterface").getField("modifiedValueField").getConstantValue();

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));
        strategy.isConstantValueDifferent(oldConstantValue, newConstantValue);

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 1, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testMerge() throws IOException {
        Map oldPackage1 = new HashMap();
        oldPackage1.put("ModifiedPackage.ModifiedClass", getOldJar().getClassfile("ModifiedPackage.ModifiedClass"));
        Map newPackage1 = new HashMap();
        newPackage1.put("ModifiedPackage.ModifiedClass", getNewJar().getClassfile("ModifiedPackage.ModifiedClass"));

        Map oldPackage2 = new HashMap();
        oldPackage2.put("UnmodifiedPackage.UnmodifiedClass", getOldJar().getClassfile("UnmodifiedPackage.UnmodifiedClass"));
        Map newPackage2 = new HashMap();
        newPackage2.put("UnmodifiedPackage.UnmodifiedClass", getNewJar().getClassfile("UnmodifiedPackage.UnmodifiedClass"));

        ListBasedDifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));

        strategy.isPackageDifferent(oldPackage1, newPackage1);
        strategy.isPackageDifferent(oldPackage2, newPackage2);
        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());

        strategy.load(new BufferedReader(new StringReader("ModifiedPackage")));

        strategy.isPackageDifferent(oldPackage1, newPackage1);
        strategy.isPackageDifferent(oldPackage2, newPackage2);
        assertEquals("package",  1, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());

        strategy.load(new BufferedReader(new StringReader("UnmodifiedPackage")));

        strategy.isPackageDifferent(oldPackage1, newPackage1);
        strategy.isPackageDifferent(oldPackage2, newPackage2);
        assertEquals("package",  3, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }
}
