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

import java.io.*;
import java.util.*;

import org.jmock.*;
import org.junit.jupiter.api.*;

import com.jeantessier.classreader.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestListBasedDifferenceStrategy extends TestDifferencesFactoryBase {
    private final DifferenceStrategy mockStrategy = mock(DifferenceStrategy.class);

    @Test
    void testNullFile() throws IOException {
        try {
            new ListBasedDifferenceStrategy(mockStrategy, (BufferedReader) null);
            fail("Created validator with null");
        } catch (NullPointerException ex) {
            // Expected
        }
    }

    @Test
    void testMissingFile1() {
        try {
            new ListBasedDifferenceStrategy(mockStrategy, "no such file");
            fail("Created validator with missing file");
        } catch (IOException ex) {
            // Expected
        }
    }

    @Test
    void testMissingFile2() {
        File file = new File("no such file");
        assertFalse(file.exists(), "File exists");

        try {
            new ListBasedDifferenceStrategy(mockStrategy, file);
            fail("Created validator with missing file");
        } catch (IOException ex) {
            // Expected
        }
    }

    @Test
    void testIsPackageDifferentNotInList() throws IOException {
        var oldPackage = Map.of(
                "ModifiedPackage.ModifiedClass", findClass("ModifiedPackage.ModifiedClass", getOldPackages())
        );
        var newPackage = Map.of(
                "ModifiedPackage.ModifiedClass", findClass("ModifiedPackage.ModifiedClass", getNewPackages())
        );

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));
        strategy.isPackageDifferent(oldPackage, newPackage);
    }

    @Test
    void testIsClassDifferentNotInList() throws IOException {
        Classfile oldClass = findClass("ModifiedPackage.ModifiedClass", getOldPackages());
        Classfile newClass = findClass("ModifiedPackage.ModifiedClass", getNewPackages());

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));
        strategy.isClassDifferent(oldClass, newClass);
    }

    @Test
    void testIsFieldDifferentNotInList() throws IOException {
        Field_info oldField = findField("ModifiedPackage.ModifiedClass.modifiedField", getOldPackages());
        Field_info newField = findField("ModifiedPackage.ModifiedClass.modifiedField", getNewPackages());

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));
        strategy.isFieldDifferent(oldField, newField);
    }

    @Test
    void testIsMethodDifferentNotInList() throws IOException {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.modifiedMethod()", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.modifiedMethod()", getNewPackages());

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));
        strategy.isMethodDifferent(oldMethod, newMethod);
    }

    @Test
    void testIsCodeMethodDifferentNotInList() throws IOException {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.modifiedCodeMethod()", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.modifiedCodeMethod()", getNewPackages());

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));
        strategy.isMethodDifferent(oldMethod, newMethod);
    }

    @Test
    void testIsCodeDifferentNotInList() throws IOException {
        Code_attribute oldCode = findMethod("ModifiedPackage.ModifiedClass.modifiedCodeMethod()", getOldPackages()).getCode();
        Code_attribute newCode = findMethod("ModifiedPackage.ModifiedClass.modifiedCodeMethod()", getNewPackages()).getCode();

        checking(new Expectations() {{
            oneOf (mockStrategy).isCodeDifferent(oldCode, newCode);
        }});

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));
        strategy.isCodeDifferent(oldCode, newCode);
    }

    @Test
    void testIsPackageDifferentInList() throws IOException {
        var oldPackage = Map.of(
                "ModifiedPackage.ModifiedClass", findClass("ModifiedPackage.ModifiedClass", getOldPackages())
        );
        var newPackage = Map.of(
                "ModifiedPackage.ModifiedClass", findClass("ModifiedPackage.ModifiedClass", getNewPackages())
        );

        checking(new Expectations() {{
            oneOf (mockStrategy).isPackageDifferent(oldPackage, newPackage);
        }});

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage")));
        strategy.isPackageDifferent(oldPackage, newPackage);
    }

    @Test
    void testIsClassDifferentInList() throws IOException {
        Classfile oldClass = findClass("ModifiedPackage.ModifiedClass", getOldPackages());
        Classfile newClass = findClass("ModifiedPackage.ModifiedClass", getNewPackages());

        checking(new Expectations() {{
            oneOf (mockStrategy).isClassDifferent(oldClass, newClass);
        }});

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass")));
        strategy.isClassDifferent(oldClass, newClass);
    }

    @Test
    void testIsFieldDifferentInList() throws IOException {
        Field_info oldField = findField("ModifiedPackage.ModifiedClass.modifiedField", getOldPackages());
        Field_info newField = findField("ModifiedPackage.ModifiedClass.modifiedField", getNewPackages());

        checking(new Expectations() {{
            oneOf (mockStrategy).isFieldDifferent(oldField, newField);
        }});

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass.modifiedField")));
        strategy.isFieldDifferent(oldField, newField);
    }

    @Test
    void testIsMethodDifferentInList() throws IOException {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.modifiedMethod()", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.modifiedMethod()", getNewPackages());

        checking(new Expectations() {{
            oneOf (mockStrategy).isMethodDifferent(oldMethod, newMethod);
        }});

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass.modifiedMethod()")));
        strategy.isMethodDifferent(oldMethod, newMethod);
    }

    @Test
    void testIsCodeMethodDifferentInList() throws IOException {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.modifiedCodeMethod()", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.modifiedCodeMethod()", getNewPackages());

        checking(new Expectations() {{
            oneOf (mockStrategy).isMethodDifferent(oldMethod, newMethod);
        }});

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass.modifiedCodeMethod()")));
        strategy.isMethodDifferent(oldMethod, newMethod);
    }

    @Test
    void testIsCodeDifferentInList() throws IOException {
        Code_attribute oldCode = findMethod("ModifiedPackage.ModifiedClass.modifiedCodeMethod()", getOldPackages()).getCode();
        Code_attribute newCode = findMethod("ModifiedPackage.ModifiedClass.modifiedCodeMethod()", getNewPackages()).getCode();

        checking(new Expectations() {{
            oneOf (mockStrategy).isCodeDifferent(oldCode, newCode);
        }});

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass.modifiedCodeMethod()")));
        strategy.isCodeDifferent(oldCode, newCode);
    }

    @Test
    void testIsPackageDifferentInListWithSuffix() throws IOException {
        var oldPackage = Map.of(
                "ModifiedPackage.ModifiedClass", findClass("ModifiedPackage.ModifiedClass", getOldPackages())
        );
        var newPackage = Map.of(
                "ModifiedPackage.ModifiedClass", findClass("ModifiedPackage.ModifiedClass", getNewPackages())
        );

        checking(new Expectations() {{
            oneOf (mockStrategy).isPackageDifferent(oldPackage, newPackage);
        }});

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage [P]")));
        strategy.isPackageDifferent(oldPackage, newPackage);
    }

    @Test
    void testIsClassDifferentInListWithSuffix() throws IOException {
        Classfile oldClass = findClass("ModifiedPackage.ModifiedClass", getOldPackages());
        Classfile newClass = findClass("ModifiedPackage.ModifiedClass", getNewPackages());

        checking(new Expectations() {{
            oneOf (mockStrategy).isClassDifferent(oldClass, newClass);
        }});

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass [C]")));
        strategy.isClassDifferent(oldClass, newClass);
    }

    @Test
    void testIsFieldDifferentInListWithSuffix() throws IOException {
        Field_info oldField = findField("ModifiedPackage.ModifiedClass.modifiedField", getOldPackages());
        Field_info newField = findField("ModifiedPackage.ModifiedClass.modifiedField", getNewPackages());

        checking(new Expectations() {{
            oneOf (mockStrategy).isFieldDifferent(oldField, newField);
        }});

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass.modifiedField [F]")));
        strategy.isFieldDifferent(oldField, newField);
    }

    @Test
    void testIsMethodDifferentInListWithSuffix() throws IOException {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.modifiedMethod()", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.modifiedMethod()", getNewPackages());

        checking(new Expectations() {{
            oneOf (mockStrategy).isMethodDifferent(oldMethod, newMethod);
        }});

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass.modifiedMethod() [F]")));
        strategy.isMethodDifferent(oldMethod, newMethod);
    }

    @Test
    void testIsCodeMethodDifferentInListWithSuffix() throws IOException {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.modifiedCodeMethod()", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.modifiedCodeMethod()", getNewPackages());

        checking(new Expectations() {{
            oneOf (mockStrategy).isMethodDifferent(oldMethod, newMethod);
        }});

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass.modifiedCodeMethod() [F]")));
        strategy.isMethodDifferent(oldMethod, newMethod);
    }

    @Test
    void testIsCodeDifferentInListWithSuffix() throws IOException {
        Code_attribute oldCode = findMethod("ModifiedPackage.ModifiedClass.modifiedCodeMethod()", getOldPackages()).getCode();
        Code_attribute newCode = findMethod("ModifiedPackage.ModifiedClass.modifiedCodeMethod()", getNewPackages()).getCode();

        checking(new Expectations() {{
            oneOf (mockStrategy).isCodeDifferent(oldCode, newCode);
        }});

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("ModifiedPackage.ModifiedClass.modifiedCodeMethod() [F]")));
        strategy.isCodeDifferent(oldCode, newCode);
    }

    @Test
    void testIsConstantValueDifferent() throws IOException {
        ConstantValue_attribute oldConstantValue = findField("ModifiedPackage.ModifiedInterface.modifiedValueField", getOldPackages()).getConstantValue();
        ConstantValue_attribute newConstantValue = findField("ModifiedPackage.ModifiedInterface.modifiedValueField", getNewPackages()).getConstantValue();

        checking(new Expectations() {{
            oneOf (mockStrategy).isConstantValueDifferent(oldConstantValue, newConstantValue);
        }});

        DifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));
        strategy.isConstantValueDifferent(oldConstantValue, newConstantValue);
    }

    @Test
    void testMerge() throws IOException {
        var oldPackage1 = Map.of(
                "ModifiedPackage.ModifiedClass", findClass("ModifiedPackage.ModifiedClass", getOldPackages())
        );
        var newPackage1 = Map.of(
                "ModifiedPackage.ModifiedClass", findClass("ModifiedPackage.ModifiedClass", getNewPackages())
        );

        var oldPackage2 = Map.of(
                "UnmodifiedPackage.UnmodifiedClass", findClass("UnmodifiedPackage.UnmodifiedClass", getOldPackages())
        );
        var newPackage2 = Map.of(
                "UnmodifiedPackage.UnmodifiedClass", findClass("UnmodifiedPackage.UnmodifiedClass", getNewPackages())
        );

        // Step 1

        ListBasedDifferenceStrategy strategy = new ListBasedDifferenceStrategy(mockStrategy, new BufferedReader(new StringReader("")));

        strategy.isPackageDifferent(oldPackage1, newPackage1);
        strategy.isPackageDifferent(oldPackage2, newPackage2);

        // Step 2

        strategy.load(new BufferedReader(new StringReader("ModifiedPackage")));

        checking(new Expectations() {{
            oneOf (mockStrategy).isPackageDifferent(oldPackage1, newPackage1);
        }});

        strategy.isPackageDifferent(oldPackage1, newPackage1);
        strategy.isPackageDifferent(oldPackage2, newPackage2);

        // Step 3

        strategy.load(new BufferedReader(new StringReader("UnmodifiedPackage")));

        checking(new Expectations() {{
            oneOf (mockStrategy).isPackageDifferent(oldPackage1, newPackage1);
            oneOf (mockStrategy).isPackageDifferent(oldPackage2, newPackage2);
        }});

        strategy.isPackageDifferent(oldPackage1, newPackage1);
        strategy.isPackageDifferent(oldPackage2, newPackage2);
    }
}
