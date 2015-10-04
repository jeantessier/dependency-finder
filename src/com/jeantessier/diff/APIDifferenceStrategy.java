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

/**
 * TODO class comments
 */
public class APIDifferenceStrategy extends DifferenceStrategyDecorator {
    public APIDifferenceStrategy(DifferenceStrategy delegate) {
        super(delegate);
    }

    public boolean isClassDifferent(Classfile oldClass, Classfile newClass) {
        return isRemoved(oldClass, newClass) ||
               isNew(oldClass, newClass) ||
               isClassModified(oldClass, newClass);
    }

    protected boolean isClassModified(Classfile oldClass, Classfile newClass) {
        return isDeclarationModified(oldClass, newClass) ||
               isDeprecationModified(oldClass, newClass) ||
               checkForDifferentFeatures(oldClass, newClass);
    }

    public boolean isDeclarationModified(Classfile oldClass, Classfile newClass) {
        return !oldClass.getDeclaration().equals(newClass.getDeclaration());
    }

    private boolean checkForDifferentFeatures(Classfile oldClass, Classfile newClass) {
        return checkForDifferentFields(oldClass, newClass) ||
               checkForDifferentMethods(oldClass, newClass);
    }

    private boolean checkForDifferentFields(Classfile oldClass, Classfile newClass) {
        boolean result = false;

        Iterator<String> fieldNames = collectFeatures(oldClass.getAllFields(), newClass.getAllFields(), new NameMapper());
        while (!result && fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            Field_info oldField = oldClass.getField(fieldName);
            Field_info newField = newClass.getField(fieldName);

            result = isFieldDifferent(oldField, newField);
        }

        return result;
    }

    private boolean checkForDifferentMethods(Classfile oldClass, Classfile newClass) {
        boolean result = false;

        Iterator<String> methodSignatures = collectFeatures(oldClass.getAllMethods(), newClass.getAllMethods(), new SignatureMapper());
        while (!result && methodSignatures.hasNext()) {
            String methodSignature = methodSignatures.next();
            Method_info oldMethod = oldClass.getMethod(methodSignature);
            Method_info newMethod = newClass.getMethod(methodSignature);

            result = isMethodDifferent(oldMethod, newMethod);
        }

        return result;
    }

    public boolean isFieldDifferent(Field_info oldField, Field_info newField) {
        return isRemoved(oldField, newField) ||
               isNew(oldField, newField) ||
               isDeprecationModified(oldField, newField) ||
               isDeclarationModified(oldField, newField) ||
               isConstantValueDifferent(oldField.getConstantValue(), newField.getConstantValue());
    }

    public boolean isMethodDifferent(Method_info oldMethod, Method_info newMethod) {
        return isRemoved(oldMethod, newMethod) ||
               isNew(oldMethod, newMethod) ||
               isDeprecationModified(oldMethod, newMethod) ||
               isDeclarationModified(oldMethod, newMethod) ||
               isCodeDifferent(oldMethod.getCode(), newMethod.getCode());
    }

    protected boolean isRemoved(Object oldElement, Object newElement) {
        return oldElement != null && newElement == null;
    }

    protected boolean isDeprecationModified(Deprecatable oldItem, Deprecatable newItem) {
        return oldItem.isDeprecated() != newItem.isDeprecated();
    }

    private boolean isDeclarationModified(Feature_info oldFeature, Feature_info newFeature) {
        return !oldFeature.getDeclaration().equals(newFeature.getDeclaration());
    }

    protected boolean isNew(Object oldElement, Object newElement) {
        return oldElement == null && newElement != null;
    }

    public boolean isPackageDifferent(Map<String, Classfile> oldPackage, Map<String, Classfile> newPackage) {
        return isPackageRemoved(oldPackage, newPackage) ||
               isPackageNew(oldPackage, newPackage) ||
               isPackageModified(oldPackage, newPackage);
    }

    protected boolean isPackageRemoved(Map<String, Classfile> oldPackage, Map<String, Classfile> newPackage) {
        return !oldPackage.isEmpty() && newPackage.isEmpty();
    }

    protected boolean isPackageNew(Map<String, Classfile> oldPackage, Map<String, Classfile> newPackage) {
        return oldPackage.isEmpty() && !newPackage.isEmpty();
    }

    protected boolean isPackageModified(Map<String, Classfile> oldPackage, Map<String, Classfile> newPackage) {
        return oldPackage.size() != newPackage.size() ||
               checkForDifferentClasses(oldPackage, newPackage);
    }

    private boolean checkForDifferentClasses(Map<String, Classfile> oldPackage, Map<String, Classfile> newPackage) {
        boolean result = false;

        Set<String> classNames = new HashSet<String>();
        classNames.addAll(oldPackage.keySet());
        classNames.addAll(newPackage.keySet());

        Iterator<String> i = classNames.iterator();
        while (!result && i.hasNext()) {
            String    className = i.next();
            Classfile oldClass  = oldPackage.get(className);
            Classfile newClass  = newPackage.get(className);

            result = isClassDifferent(oldClass, newClass);
        }

        return result;
    }

    private Iterator<String> collectFeatures(Collection<? extends Feature_info> oldFeatures, Collection<? extends Feature_info> newFeatures, FeatureMapper<String> mapper) {
        Set<String> features = new HashSet<String>();

        for (Feature_info feature : oldFeatures) {
            features.add(mapper.map(feature));
        }
        for (Feature_info feature : newFeatures) {
            features.add(mapper.map(feature));
        }

        return features.iterator();
    }
}
