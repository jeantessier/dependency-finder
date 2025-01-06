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

import java.util.*;

import com.jeantessier.classreader.*;
import org.jmock.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestAPIDifferenceStrategy extends TestDifferencesFactoryBase {
    private final DifferenceStrategy mockStrategy = mock(DifferenceStrategy.class);
    private final APIDifferenceStrategy strategy = new APIDifferenceStrategy(mockStrategy);

    @Test
    void testUnmodifiedConstantValue() {
        ConstantValue_attribute oldValue = findField("UnmodifiedPackage.UnmodifiedInterface.unmodifiedField", getOldPackages()).getConstantValue();
        ConstantValue_attribute newValue = findField("UnmodifiedPackage.UnmodifiedInterface.unmodifiedField", getNewPackages()).getConstantValue();

        checking(new Expectations() {{
            oneOf (mockStrategy).isConstantValueDifferent(oldValue, newValue);
        }});

        assertFalse(strategy.isConstantValueDifferent(oldValue, newValue));
    }

    @Test
    void testRemovedConstantValue() {
        ConstantValue_attribute oldValue = findField("ModifiedPackage.ModifiedInterface.removedField", getOldPackages()).getConstantValue();
        ConstantValue_attribute newValue = null;

        checking(new Expectations() {{
            oneOf (mockStrategy).isConstantValueDifferent(oldValue, newValue);
        }});

        assertFalse(strategy.isConstantValueDifferent(oldValue, newValue));
    }

    @Test
    void testModifiedConstantValue() {
        ConstantValue_attribute oldValue = findField("ModifiedPackage.ModifiedInterface.modifiedValueField", getOldPackages()).getConstantValue();
        ConstantValue_attribute newValue = findField("ModifiedPackage.ModifiedInterface.modifiedValueField", getNewPackages()).getConstantValue();

        checking(new Expectations() {{
            oneOf (mockStrategy).isConstantValueDifferent(oldValue, newValue);
        }});

        assertFalse(strategy.isConstantValueDifferent(oldValue, newValue));
    }

    @Test
    void testNewConstantValue() {
        ConstantValue_attribute oldValue = null;
        ConstantValue_attribute newValue = findField("ModifiedPackage.ModifiedInterface.newField", getNewPackages()).getConstantValue();

        checking(new Expectations() {{
            oneOf (mockStrategy).isConstantValueDifferent(oldValue, newValue);
        }});

        assertFalse(strategy.isConstantValueDifferent(oldValue, newValue));
    }

    @Test
    void testUnmodifiedField() {
        Field_info oldFeature = findField("UnmodifiedPackage.UnmodifiedClass.unmodifiedField", getOldPackages());
        Field_info newFeature = findField("UnmodifiedPackage.UnmodifiedClass.unmodifiedField", getNewPackages());

        checking(new Expectations() {{
            oneOf (mockStrategy).isConstantValueDifferent(oldFeature.getConstantValue(), newFeature.getConstantValue());
        }});

        assertFalse(strategy.isFieldDifferent(oldFeature, newFeature));
    }

    @Test
    void testRemovedField() {
        Field_info oldFeature = findField("ModifiedPackage.ModifiedClass.removedField", getOldPackages());
        Field_info newFeature = findField("ModifiedPackage.ModifiedClass.removedField", getNewPackages());

        assertTrue(strategy.isFieldDifferent(oldFeature, newFeature));
    }

    @Test
    void testModifiedField() {
        Field_info oldFeature = findField("ModifiedPackage.ModifiedClass.modifiedField", getOldPackages());
        Field_info newFeature = findField("ModifiedPackage.ModifiedClass.modifiedField", getNewPackages());

        assertTrue(strategy.isFieldDifferent(oldFeature, newFeature));
    }

    @Test
    void testModifiedValueField() {
        Field_info oldFeature = findField("ModifiedPackage.ModifiedInterface.modifiedValueField", getOldPackages());
        Field_info newFeature = findField("ModifiedPackage.ModifiedInterface.modifiedValueField", getNewPackages());

        checking(new Expectations() {{
            oneOf (mockStrategy).isConstantValueDifferent(oldFeature.getConstantValue(), newFeature.getConstantValue());
        }});

        assertFalse(strategy.isFieldDifferent(oldFeature, newFeature));
    }

    @Test
    void testDeprecatedField() {
        Field_info oldFeature = findField("ModifiedPackage.ModifiedInterface.deprecatedField", getOldPackages());
        Field_info newFeature = findField("ModifiedPackage.ModifiedInterface.deprecatedField", getNewPackages());

        assertTrue(strategy.isFieldDifferent(oldFeature, newFeature));
    }

    @Test
    void testUndeprecatedField() {
        Field_info oldFeature = findField("ModifiedPackage.ModifiedInterface.undeprecatedField", getOldPackages());
        Field_info newFeature = findField("ModifiedPackage.ModifiedInterface.undeprecatedField", getNewPackages());

        assertTrue(strategy.isFieldDifferent(oldFeature, newFeature));
    }

    @Test
    void testNewField() {
        Field_info oldFeature = findField("ModifiedPackage.ModifiedClass.newField", getOldPackages());
        Field_info newFeature = findField("ModifiedPackage.ModifiedClass.newField", getNewPackages());

        assertTrue(strategy.isFieldDifferent(oldFeature, newFeature));
    }

    @Test
    void testUnmodifiedCode() {
        Code_attribute oldCode = findMethod("UnmodifiedPackage.UnmodifiedClass.unmodifiedMethod()", getOldPackages()).getCode();
        Code_attribute newCode = findMethod("UnmodifiedPackage.UnmodifiedClass.unmodifiedMethod()", getNewPackages()).getCode();

        checking(new Expectations() {{
            oneOf (mockStrategy).isCodeDifferent(oldCode, newCode);
        }});

        assertFalse(strategy.isCodeDifferent(oldCode, newCode));
    }

    @Test
    void testModifiedCode() {
        Code_attribute oldCode = findMethod("ModifiedPackage.ModifiedClass.modifiedCodeMethod()", getOldPackages()).getCode();
        Code_attribute newCode = findMethod("ModifiedPackage.ModifiedClass.modifiedCodeMethod()", getNewPackages()).getCode();

        checking(new Expectations() {{
            oneOf (mockStrategy).isCodeDifferent(oldCode, newCode);
        }});

        assertFalse(strategy.isCodeDifferent(oldCode, newCode));
    }

    @Test
    void testUnmodifiedConstructor() {
        Method_info oldMethod = findMethod("UnmodifiedPackage.UnmodifiedClass.UnmodifiedClass()", getOldPackages());
        Method_info newMethod = findMethod("UnmodifiedPackage.UnmodifiedClass.UnmodifiedClass()", getNewPackages());

        checking(new Expectations() {{
            oneOf (mockStrategy).isCodeDifferent(oldMethod.getCode(), newMethod.getCode());
        }});

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testRemovedConstructor() {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.ModifiedClass()", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.ModifiedClass()", getNewPackages());

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testModifiedConstructor() {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.ModifiedClass(int, int, int)", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.ModifiedClass(int, int, int)", getNewPackages());

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testModifiedCodeConstructor() {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.ModifiedClass(float)", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.ModifiedClass(float)", getNewPackages());

        checking(new Expectations() {{
            oneOf (mockStrategy).isCodeDifferent(oldMethod.getCode(), newMethod.getCode());
        }});

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testDeprecatedConstructor() {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.ModifiedClass(int)", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.ModifiedClass(int)", getNewPackages());

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testUndeprecatedConstructor() {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.ModifiedClass(int, int, int, int, int)", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.ModifiedClass(int, int, int, int, int)", getNewPackages());

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testNewConstructor() {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.ModifiedClass(int, int, int, int, int, int)", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.ModifiedClass(int, int, int, int, int, int)", getNewPackages());

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testUnmodifiedMethod() {
        Method_info oldMethod = findMethod("UnmodifiedPackage.UnmodifiedClass.unmodifiedMethod()", getOldPackages());
        Method_info newMethod = findMethod("UnmodifiedPackage.UnmodifiedClass.unmodifiedMethod()", getNewPackages());

        checking(new Expectations() {{
            oneOf (mockStrategy).isCodeDifferent(oldMethod.getCode(), newMethod.getCode());
        }});

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testRemovedMethod() {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.removedMethod()", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.removedMethod()", getNewPackages());

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testModifiedMethod() {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.modifiedMethod()", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.modifiedMethod()", getNewPackages());

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testModifiedCodeMethod() {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.modifiedCodeMethod()", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.modifiedCodeMethod()", getNewPackages());

        checking(new Expectations() {{
            oneOf (mockStrategy).isCodeDifferent(oldMethod.getCode(), newMethod.getCode());
        }});

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testDeprecatedMethod() {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.deprecatedMethod()", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.deprecatedMethod()", getNewPackages());

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testUndeprecatedMethod() {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.undeprecatedMethod()", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.undeprecatedMethod()", getNewPackages());

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testNewMethod() {
        Method_info oldMethod = findMethod("ModifiedPackage.ModifiedClass.newMethod()", getOldPackages());
        Method_info newMethod = findMethod("ModifiedPackage.ModifiedClass.newMethod()", getNewPackages());

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testUnmodifiedInterface() {
        Classfile oldClass = findClass("UnmodifiedPackage.UnmodifiedInterface", getOldPackages());
        Classfile newClass = findClass("UnmodifiedPackage.UnmodifiedInterface", getNewPackages());

        checking(new Expectations() {{
            oneOf (mockStrategy).isConstantValueDifferent(oldClass.getField(f -> f.getName().equals("unmodifiedField")).getConstantValue(), newClass.getField(field -> field.getName().equals("unmodifiedField")).getConstantValue());
            oneOf (mockStrategy).isCodeDifferent(oldClass.getMethod(m -> m.getSignature().equals("unmodifiedMethod()")).getCode(), newClass.getMethod(method -> method.getSignature().equals("unmodifiedMethod()")).getCode());
        }});

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testRemovedInterface() {
        Classfile oldClass = findClass("ModifiedPackage.RemovedInterface", getOldPackages());
        Classfile newClass = findClass("ModifiedPackage.RemovedInterface", getNewPackages());

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testModifiedDeclarationInterface() {
        Classfile oldClass = findClass("ModifiedPackage.ModifiedDeclarationInterface", getOldPackages());
        Classfile newClass = findClass("ModifiedPackage.ModifiedDeclarationInterface", getNewPackages());

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testModifiedInterface() {
        Classfile oldClass = findClass("ModifiedPackage.ModifiedInterface", getOldPackages());
        Classfile newClass = findClass("ModifiedPackage.ModifiedInterface", getNewPackages());

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testNewInterface() {
        Classfile oldClass = findClass("ModifiedPackage.NewInterface", getOldPackages());
        Classfile newClass = findClass("ModifiedPackage.NewInterface", getNewPackages());

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testUnmodifiedClass() {
        Classfile oldClass = findClass("UnmodifiedPackage.UnmodifiedClass", getOldPackages());
        Classfile newClass = findClass("UnmodifiedPackage.UnmodifiedClass", getNewPackages());

        checking(new Expectations() {{
            oneOf (mockStrategy).isConstantValueDifferent(oldClass.getField(f -> f.getName().equals("unmodifiedField")).getConstantValue(), newClass.getField(field -> field.getName().equals("unmodifiedField")).getConstantValue());
            oneOf (mockStrategy).isCodeDifferent(oldClass.getMethod(m -> m.getSignature().equals("UnmodifiedClass()")).getCode(), newClass.getMethod(method2 -> method2.getSignature().equals("UnmodifiedClass()")).getCode());
            oneOf (mockStrategy).isCodeDifferent(oldClass.getMethod(m -> m.getSignature().equals("unmodifiedMethod()")).getCode(), newClass.getMethod(method -> method.getSignature().equals("unmodifiedMethod()")).getCode());
        }});

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testRemovedClass() {
        Classfile oldClass = findClass("ModifiedPackage.RemovedClass", getOldPackages());
        Classfile newClass = findClass("ModifiedPackage.RemovedClass", getNewPackages());

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testModifiedDeclarationClass() {
        Classfile oldClass = findClass("ModifiedPackage.ModifiedDeclarationClass", getOldPackages());
        Classfile newClass = findClass("ModifiedPackage.ModifiedDeclarationClass", getNewPackages());

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testModifiedClass() {
        Classfile oldClass = findClass("ModifiedPackage.ModifiedClass", getOldPackages());
        Classfile newClass = findClass("ModifiedPackage.ModifiedClass", getNewPackages());

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testNewClass() {
        Classfile oldClass = findClass("ModifiedPackage.NewClass", getOldPackages());
        Classfile newClass = findClass("ModifiedPackage.NewClass", getNewPackages());

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testUnmodifiedPackage() {
        var oldPackage = Map.of(
                "UnmodifiedPackage.UnmodifiedClass", findClass("UnmodifiedPackage.UnmodifiedClass", getOldPackages()),
                "UnmodifiedPackage.UnmodifiedInterface", findClass("UnmodifiedPackage.UnmodifiedInterface", getOldPackages())
        );
        var newPackage = Map.of(
                "UnmodifiedPackage.UnmodifiedClass", findClass("UnmodifiedPackage.UnmodifiedClass", getNewPackages()),
                "UnmodifiedPackage.UnmodifiedInterface", findClass("UnmodifiedPackage.UnmodifiedInterface", getNewPackages())
        );

        checking(new Expectations() {{
            oneOf (mockStrategy).isConstantValueDifferent(null, null);
            oneOf (mockStrategy).isConstantValueDifferent(with(any(ConstantValue_attribute.class)), with(any(ConstantValue_attribute.class)));
            oneOf (mockStrategy).isCodeDifferent(null, null);
            exactly(2).of (mockStrategy).isCodeDifferent(with(any(Code_attribute.class)), with(any(Code_attribute.class)));
        }});

        assertFalse(strategy.isPackageDifferent(oldPackage, newPackage));
    }

    @Test
    void testRemovedPackage() {
        var oldPackage = Map.of(
                "RemovedPackage.RemovedClass", findClass("RemovedPackage.RemovedClass", getOldPackages())
        );
        var newPackage = Collections.<String, Classfile>emptyMap();

        assertTrue(strategy.isPackageDifferent(oldPackage, newPackage));
    }

    @Test
    void testModifiedPackage() {
        var oldPackage = Map.of(
                "UnmodifiedPackage.UnmodifiedClass", findClass("UnmodifiedPackage.UnmodifiedClass", getOldPackages()),
                "UnmodifiedPackage.UnmodifiedInterface", findClass("UnmodifiedPackage.UnmodifiedInterface", getOldPackages())
        );
        var newPackage = Map.of(
                "UnmodifiedPackage.UnmodifiedClass", findClass("UnmodifiedPackage.UnmodifiedClass", getNewPackages())
        );

        assertTrue(strategy.isPackageDifferent(oldPackage, newPackage));
    }

    @Test
    void testModifiedCodePackage() {
        var oldPackage = Map.of(
                "ModifiedPackage.ModifiedClass", findClass("ModifiedPackage.ModifiedClass", getOldPackages())
        );
        var newPackage = Map.of(
                "ModifiedPackage.ModifiedClass", findClass("ModifiedPackage.ModifiedClass", getNewPackages())
        );

        assertTrue(strategy.isPackageDifferent(oldPackage, newPackage));
    }

    @Test
    void testNewPackage() {
        var oldPackage = Collections.<String, Classfile>emptyMap();
        var newPackage = Map.of(
                "NewPackage.NewClass", findClass("NewPackage.NewClass", getNewPackages())
        );

        assertTrue(strategy.isPackageDifferent(oldPackage, newPackage));
    }
}
