/*
 *  Copyright (c) 2001-2023, Jean Tessier
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

public class MockDifferenceStrategy extends DifferenceStrategyDecorator {
    private int packageDifferentCount       = 0;
    private int classDifferentCount         = 0;
    private int fieldDifferentCount         = 0;
    private int constantValueDifferentCount = 0;
    private int methodDifferentCount        = 0;
    private int codeDifferentCount          = 0;

    public MockDifferenceStrategy(DifferenceStrategy delegate) {
        super(delegate);
    }

    public int getPackageDifferentCount() {
        return packageDifferentCount;
    }

    public int getClassDifferentCount() {
        return classDifferentCount;
    }

    public int getFieldDifferentCount() {
        return fieldDifferentCount;
    }

    public int getConstantValueDifferentCount() {
        return constantValueDifferentCount;
    }

    public int getMethodDifferentCount() {
        return methodDifferentCount;
    }

    public int getCodeDifferentCount() {
        return codeDifferentCount;
    }

    public boolean isPackageDifferent(Map<String, Classfile> oldPackage, Map<String, Classfile> newPackage) {
        packageDifferentCount++;

        return super.isPackageDifferent(oldPackage, newPackage);
    }

    public boolean isClassDifferent(Classfile oldClass, Classfile newClass) {
        classDifferentCount++;

        return super.isClassDifferent(oldClass, newClass);
    }

    public boolean isFieldDifferent(Field_info oldFeature, Field_info newFeature) {
        fieldDifferentCount++;

        return super.isFieldDifferent(oldFeature, newFeature);
    }

    public boolean isConstantValueDifferent(ConstantValue_attribute oldValue, ConstantValue_attribute newValue) {
        constantValueDifferentCount++;

        return super.isConstantValueDifferent(oldValue, newValue);
    }

    public boolean isMethodDifferent(Method_info oldMethod, Method_info newMethod) {
        methodDifferentCount++;

        return super.isMethodDifferent(oldMethod, newMethod);
    }

    public boolean isCodeDifferent(Code_attribute oldCode, Code_attribute newCode) {
        codeDifferentCount++;

        return super.isCodeDifferent(oldCode, newCode);
    }
}
