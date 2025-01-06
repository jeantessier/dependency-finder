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

import org.junit.jupiter.api.*;

import com.jeantessier.classreader.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestCodeDifferenceStrategy extends TestDifferencesFactoryBase {
    private final CodeDifferenceStrategy strategy = new CodeDifferenceStrategy();

    @Test
    void testUnmodifiedConstantValue() {
        Classfile classfile1 = oldLoader.getClassfile("UnmodifiedPackage.UnmodifiedInterface");
        ConstantValue_attribute oldValue = classfile1.getField(f -> f.getName().equals("unmodifiedField")).getConstantValue();
        Classfile classfile = newLoader.getClassfile("UnmodifiedPackage.UnmodifiedInterface");
        ConstantValue_attribute newValue = classfile.getField(f -> f.getName().equals("unmodifiedField")).getConstantValue();

        assertFalse(strategy.isConstantValueDifferent(oldValue, newValue));
    }

    @Test
    void testRemovedConstantValue() {
        Classfile classfile = oldLoader.getClassfile("ModifiedPackage.ModifiedInterface");
        ConstantValue_attribute oldValue = classfile.getField(f -> f.getName().equals("removedField")).getConstantValue();
        ConstantValue_attribute newValue = null;

        assertTrue(strategy.isConstantValueDifferent(oldValue, newValue));
    }

    @Test
    void testModifiedConstantValue() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedInterface");
        ConstantValue_attribute oldValue = classfile1.getField(f -> f.getName().equals("modifiedValueField")).getConstantValue();
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedInterface");
        ConstantValue_attribute newValue = classfile.getField(f -> f.getName().equals("modifiedValueField")).getConstantValue();

        assertTrue(strategy.isConstantValueDifferent(oldValue, newValue));
    }

    @Test
    void testNewConstantValue() {
        ConstantValue_attribute oldValue = null;
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedInterface");
        ConstantValue_attribute newValue = classfile.getField(f -> f.getName().equals("newField")).getConstantValue();

        assertTrue(strategy.isConstantValueDifferent(oldValue, newValue));
    }

    @Test
    void testUnmodifiedCode() {
        Classfile classfile1 = oldLoader.getClassfile("UnmodifiedPackage.UnmodifiedClass");
        Code_attribute oldCode = classfile1.getMethod(m -> m.getSignature().equals("unmodifiedMethod()")).getCode();
        Classfile classfile = newLoader.getClassfile("UnmodifiedPackage.UnmodifiedClass");
        Code_attribute newCode = classfile.getMethod(m -> m.getSignature().equals("unmodifiedMethod()")).getCode();

        assertFalse(strategy.isCodeDifferent(oldCode, newCode));
    }

    @Test
    void testModifiedCode() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Code_attribute oldCode = classfile1.getMethod(m -> m.getSignature().equals("modifiedCodeMethod()")).getCode();
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Code_attribute newCode = classfile.getMethod(m -> m.getSignature().equals("modifiedCodeMethod()")).getCode();

        assertTrue(strategy.isCodeDifferent(oldCode, newCode));
    }
}
