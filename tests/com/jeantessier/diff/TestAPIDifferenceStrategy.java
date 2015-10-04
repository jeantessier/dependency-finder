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

import java.util.*;

import com.jeantessier.classreader.*;

public class TestAPIDifferenceStrategy extends TestDifferencesFactoryBase {
    private MockDifferenceStrategy mockStrategy;
    private APIDifferenceStrategy strategy;

    protected void setUp() throws Exception {
        super.setUp();

        mockStrategy = new MockDifferenceStrategy(new NoDifferenceStrategy());
        strategy = new APIDifferenceStrategy(mockStrategy);
    }

    public void testUnmodifiedConstantValue() {
        ConstantValue_attribute oldValue = getOldJar().getClassfile("UnmodifiedPackage.UnmodifiedInterface").getField("unmodifiedField").getConstantValue();
        ConstantValue_attribute newValue = getNewJar().getClassfile("UnmodifiedPackage.UnmodifiedInterface").getField("unmodifiedField").getConstantValue();

        assertFalse(strategy.isConstantValueDifferent(oldValue, newValue));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 1, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testRemovedConstantValue() {
        ConstantValue_attribute oldValue = getOldJar().getClassfile("ModifiedPackage.ModifiedInterface").getField("removedField").getConstantValue();
        ConstantValue_attribute newValue = null;

        assertFalse(strategy.isConstantValueDifferent(oldValue, newValue));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 1, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testModifiedConstantValue() {
        ConstantValue_attribute oldValue = getOldJar().getClassfile("ModifiedPackage.ModifiedInterface").getField("modifiedValueField").getConstantValue();
        ConstantValue_attribute newValue = getNewJar().getClassfile("ModifiedPackage.ModifiedInterface").getField("modifiedValueField").getConstantValue();

        assertFalse(strategy.isConstantValueDifferent(oldValue, newValue));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 1, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testNewConstantValue() {
        ConstantValue_attribute oldValue = null;
        ConstantValue_attribute newValue = getNewJar().getClassfile("ModifiedPackage.ModifiedInterface").getField("newField").getConstantValue();

        assertFalse(strategy.isConstantValueDifferent(oldValue, newValue));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 1, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testUnmodifiedField() {
        Field_info oldFeature = getOldJar().getClassfile("UnmodifiedPackage.UnmodifiedClass").getField("unmodifiedField");
        Field_info newFeature = getNewJar().getClassfile("UnmodifiedPackage.UnmodifiedClass").getField("unmodifiedField");

        assertFalse(strategy.isFieldDifferent(oldFeature, newFeature));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 1, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testRemovedField() {
        Field_info oldFeature = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getField("removedField");
        Field_info newFeature = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getField("removedField");

        assertTrue(strategy.isFieldDifferent(oldFeature, newFeature));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testModifiedField() {
        Field_info oldFeature = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getField("modifiedField");
        Field_info newFeature = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getField("modifiedField");

        assertTrue(strategy.isFieldDifferent(oldFeature, newFeature));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testModifiedValueField() {
        Field_info oldFeature = getOldJar().getClassfile("ModifiedPackage.ModifiedInterface").getField("modifiedValueField");
        Field_info newFeature = getNewJar().getClassfile("ModifiedPackage.ModifiedInterface").getField("modifiedValueField");

        assertFalse(strategy.isFieldDifferent(oldFeature, newFeature));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 1, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testDeprecatedField() {
        Field_info oldFeature = getOldJar().getClassfile("ModifiedPackage.ModifiedInterface").getField("deprecatedField");
        Field_info newFeature = getNewJar().getClassfile("ModifiedPackage.ModifiedInterface").getField("deprecatedField");

        assertTrue(strategy.isFieldDifferent(oldFeature, newFeature));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testUndeprecatedField() {
        Field_info oldFeature = getOldJar().getClassfile("ModifiedPackage.ModifiedInterface").getField("undeprecatedField");
        Field_info newFeature = getNewJar().getClassfile("ModifiedPackage.ModifiedInterface").getField("undeprecatedField");

        assertTrue(strategy.isFieldDifferent(oldFeature, newFeature));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testNewField() {
        Field_info oldFeature = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getField("newField");
        Field_info newFeature = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getField("newField");

        assertTrue(strategy.isFieldDifferent(oldFeature, newFeature));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testUnmodifiedCode() {
        Code_attribute oldCode = getOldJar().getClassfile("UnmodifiedPackage.UnmodifiedClass").getMethod("unmodifiedMethod()").getCode();
        Code_attribute newCode = getNewJar().getClassfile("UnmodifiedPackage.UnmodifiedClass").getMethod("unmodifiedMethod()").getCode();

        assertFalse(strategy.isCodeDifferent(oldCode, newCode));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     1, mockStrategy.getCodeDifferentCount());
    }

    public void testModifiedCode() {
        Code_attribute oldCode = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedCodeMethod()").getCode();
        Code_attribute newCode = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedCodeMethod()").getCode();

        assertFalse(strategy.isCodeDifferent(oldCode, newCode));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     1, mockStrategy.getCodeDifferentCount());
    }

    public void testUnmodifiedConstructor() {
        Method_info oldMethod = getOldJar().getClassfile("UnmodifiedPackage.UnmodifiedClass").getMethod("UnmodifiedClass()");
        Method_info newMethod = getNewJar().getClassfile("UnmodifiedPackage.UnmodifiedClass").getMethod("UnmodifiedClass()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     1, mockStrategy.getCodeDifferentCount());
    }

    public void testRemovedConstructor() {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass()");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testModifiedConstructor() {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(int, int, int)");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(int, int, int)");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testModifiedCodeConstructor() {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(float)");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(float)");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     1, mockStrategy.getCodeDifferentCount());
    }

    public void testDeprecatedConstructor() {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(int)");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(int)");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testUndeprecatedConstructor() {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(int, int, int, int, int)");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(int, int, int, int, int)");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testNewConstructor() {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(int, int, int, int, int, int)");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(int, int, int, int, int, int)");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testUnmodifiedMethod() {
        Method_info oldMethod = getOldJar().getClassfile("UnmodifiedPackage.UnmodifiedClass").getMethod("unmodifiedMethod()");
        Method_info newMethod = getNewJar().getClassfile("UnmodifiedPackage.UnmodifiedClass").getMethod("unmodifiedMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     1, mockStrategy.getCodeDifferentCount());
    }

    public void testRemovedMethod() {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("removedMethod()");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("removedMethod()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testModifiedMethod() {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedMethod()");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedMethod()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testModifiedCodeMethod() {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedCodeMethod()");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("modifiedCodeMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     1, mockStrategy.getCodeDifferentCount());
    }

    public void testDeprecatedMethod() {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("deprecatedMethod()");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("deprecatedMethod()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testUndeprecatedMethod() {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("undeprecatedMethod()");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("undeprecatedMethod()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testNewMethod() {
        Method_info oldMethod = getOldJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("newMethod()");
        Method_info newMethod = getNewJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("newMethod()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testUnmodifiedInterface() {
        Classfile oldClass = getOldJar().getClassfile("UnmodifiedPackage.UnmodifiedInterface");
        Classfile newClass = getNewJar().getClassfile("UnmodifiedPackage.UnmodifiedInterface");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 1, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     1, mockStrategy.getCodeDifferentCount());
    }

    public void testRemovedInterface() {
        Classfile oldClass = getOldJar().getClassfile("ModifiedPackage.RemovedInterface");
        Classfile newClass = getNewJar().getClassfile("ModifiedPackage.RemovedInterface");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testModifiedDeclarationInterface() {
        Classfile oldClass = getOldJar().getClassfile("ModifiedPackage.ModifiedDeclarationInterface");
        Classfile newClass = getNewJar().getClassfile("ModifiedPackage.ModifiedDeclarationInterface");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testModifiedInterface() {
        Classfile oldClass = getOldJar().getClassfile("ModifiedPackage.ModifiedInterface");
        Classfile newClass = getNewJar().getClassfile("ModifiedPackage.ModifiedInterface");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testNewInterface() {
        Classfile oldClass = getOldJar().getClassfile("ModifiedPackage.NewInterface");
        Classfile newClass = getNewJar().getClassfile("ModifiedPackage.NewInterface");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testUnmodifiedClass() {
        Classfile oldClass = getOldJar().getClassfile("UnmodifiedPackage.UnmodifiedClass");
        Classfile newClass = getNewJar().getClassfile("UnmodifiedPackage.UnmodifiedClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 1, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     2, mockStrategy.getCodeDifferentCount());
    }

    public void testRemovedClass() {
        Classfile oldClass = getOldJar().getClassfile("ModifiedPackage.RemovedClass");
        Classfile newClass = getNewJar().getClassfile("ModifiedPackage.RemovedClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testModifiedDeclarationClass() {
        Classfile oldClass = getOldJar().getClassfile("ModifiedPackage.ModifiedDeclarationClass");
        Classfile newClass = getNewJar().getClassfile("ModifiedPackage.ModifiedDeclarationClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testModifiedClass() {
        Classfile oldClass = getOldJar().getClassfile("ModifiedPackage.ModifiedClass");
        Classfile newClass = getNewJar().getClassfile("ModifiedPackage.ModifiedClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testNewClass() {
        Classfile oldClass = getOldJar().getClassfile("ModifiedPackage.NewClass");
        Classfile newClass = getNewJar().getClassfile("ModifiedPackage.NewClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testUnmodifiedPackage() {
        Map oldPackage = new HashMap();
        oldPackage.put("UnmodifiedPackage.UnmodifiedClass",     getOldJar().getClassfile("UnmodifiedPackage.UnmodifiedClass"));
        oldPackage.put("UnmodifiedPackage.UnmodifiedInterface", getOldJar().getClassfile("UnmodifiedPackage.UnmodifiedInterface"));
        Map newPackage = new HashMap();
        newPackage.put("UnmodifiedPackage.UnmodifiedClass",     getNewJar().getClassfile("UnmodifiedPackage.UnmodifiedClass"));
        newPackage.put("UnmodifiedPackage.UnmodifiedInterface", getNewJar().getClassfile("UnmodifiedPackage.UnmodifiedInterface"));

        assertFalse(strategy.isPackageDifferent(oldPackage, newPackage));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 2, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     3, mockStrategy.getCodeDifferentCount());
    }

    public void testRemovedPackage() {
        Map oldPackage = new HashMap();
        oldPackage.put("RemovedPackage.RemovedClass", getOldJar().getClassfile("RemovedPackage.RemovedClass"));
        Map newPackage = new HashMap();

        assertTrue(strategy.isPackageDifferent(oldPackage, newPackage));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testModifiedPackage() {
        Map oldPackage = new HashMap();
        oldPackage.put("UnmodifiedPackage.UnmodifiedClass",     getOldJar().getClassfile("UnmodifiedPackage.UnmodifiedClass"));
        oldPackage.put("UnmodifiedPackage.UnmodifiedInterface", getOldJar().getClassfile("UnmodifiedPackage.UnmodifiedInterface"));
        Map newPackage = new HashMap();
        newPackage.put("UnmodifiedPackage.UnmodifiedClass",     getNewJar().getClassfile("UnmodifiedPackage.UnmodifiedClass"));

        assertTrue(strategy.isPackageDifferent(oldPackage, newPackage));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testModifiedCodePackage() {
        Map oldPackage = new HashMap();
        oldPackage.put("ModifiedPackage.ModifiedClass", getOldJar().getClassfile("ModifiedPackage.ModifiedClass"));
        Map newPackage = new HashMap();
        newPackage.put("ModifiedPackage.ModifiedClass", getNewJar().getClassfile("ModifiedPackage.ModifiedClass"));

        assertTrue(strategy.isPackageDifferent(oldPackage, newPackage));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testNewPackage() {
        Map oldPackage = new HashMap();
        Map newPackage = new HashMap();
        newPackage.put("NewPackage.NewClass", getNewJar().getClassfile("NewPackage.NewClass"));

        assertTrue(strategy.isPackageDifferent(oldPackage, newPackage));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }
}
