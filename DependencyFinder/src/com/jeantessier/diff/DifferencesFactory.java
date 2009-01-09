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

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

public class DifferencesFactory {
    private Classfile oldClass;
    private Classfile newClass;

    private DifferenceStrategy strategy;

    /**
     * For tests only.
     */
    DifferencesFactory() {
        this(new APIDifferenceStrategy(new CodeDifferenceStrategy()));
    }

    public DifferencesFactory(DifferenceStrategy strategy) {
        this.strategy = strategy;
    }

    public Differences createProjectDifferences(String name, String oldVersion, PackageMapper oldPackages, String newVersion, PackageMapper newPackages) {
        Logger.getLogger(getClass()).debug("Begin " + name + " (" + oldVersion + " -> " + newVersion + ")");

        ProjectDifferences projectDifferences = new ProjectDifferences(name, oldVersion, newVersion);

        Logger.getLogger(getClass()).debug("      Collecting packages ...");

        Collection<String> packageNames = new TreeSet<String>();
        packageNames.addAll(oldPackages.getPackageNames());
        packageNames.addAll(newPackages.getPackageNames());

        Logger.getLogger(getClass()).debug("      Diff'ing packages ...");

        for (String packageName : packageNames) {
            Map<String, Classfile> oldPackage = oldPackages.getPackage(packageName);
            if (oldPackage == null) {
                oldPackage = Collections.emptyMap();
            }

            Map<String, Classfile> newPackage = newPackages.getPackage(packageName);
            if (newPackage == null) {
                newPackage = Collections.emptyMap();
            }

            if (strategy.isPackageDifferent(oldPackage, newPackage)) {
                projectDifferences.getPackageDifferences().add(createPackageDifferences(packageName, oldPackage, newPackage));
            }
        }

        Logger.getLogger(getClass()).debug("End   " + name + " (" + oldVersion + " -> " + newVersion + ")");

        return projectDifferences;
    }

    public Differences createPackageDifferences(String name, Map<String, Classfile> oldPackage, Map<String, Classfile> newPackage) {
        Logger.getLogger(getClass()).debug("Begin " + name);

        PackageDifferences packageDifferences = new PackageDifferences(name, oldPackage, newPackage);

        if (oldPackage != null && !oldPackage.isEmpty() && newPackage != null && !newPackage.isEmpty()) {
            Logger.getLogger(getClass()).debug("      Diff'ing classes ...");

            Collection<String> classNames = new TreeSet<String>();
            classNames.addAll(oldPackage.keySet());
            classNames.addAll(newPackage.keySet());

            for (String className : classNames) {
                Classfile oldClass = oldPackage.get(className);
                Classfile newClass = newPackage.get(className);

                if (strategy.isClassDifferent(oldClass, newClass)) {
                    packageDifferences.getClassDifferences().add(createClassDifferences(className, oldClass, newClass));
                }
            }

            Logger.getLogger(getClass()).debug("      " + name + " has " + packageDifferences.getClassDifferences().size() + " class(es) that changed.");
        }

        Logger.getLogger(getClass()).debug("End   " + name);

        return packageDifferences;
    }

    public Differences createClassDifferences(String name, Classfile oldClass, Classfile newClass) {
        Logger.getLogger(getClass()).debug("Begin " + name);

        ClassDifferences classDifferences;
        if (((oldClass != null) && oldClass.isInterface()) || ((newClass != null) && newClass.isInterface())) {
            classDifferences = new InterfaceDifferences(name, oldClass, newClass);
        } else {
            classDifferences = new ClassDifferences(name, oldClass, newClass);
        }

        if (!classDifferences.isRemoved() && !classDifferences.isNew() && strategy.isDeclarationModified(oldClass, newClass)) {
            classDifferences.setDeclarationModified(true);
        }

        Differences result = classDifferences;

        this.oldClass = oldClass;
        this.newClass = newClass;

        if (oldClass != null && newClass != null) {
            Logger.getLogger(getClass()).debug("      Collecting fields ...");

            Map<String, String> fieldLevel = new TreeMap<String, String>();

            for (Field_info field : oldClass.getAllFields()) {
                fieldLevel.put(field.getName(), field.getFullSignature());
            }

            for (Field_info field : newClass.getAllFields()) {
                fieldLevel.put(field.getName(), field.getFullSignature());
            }

            Logger.getLogger(getClass()).debug("      Diff'ing fields ...");

            for (Map.Entry<String, String> fieldEntry : fieldLevel.entrySet()) {
                Field_info oldField = oldClass.getField(fieldEntry.getKey());
                Field_info newField = newClass.getField(fieldEntry.getKey());

                if (strategy.isFieldDifferent(oldField, newField)) {
                    classDifferences.getFeatureDifferences().add(createFeatureDifferences(fieldEntry.getValue(), oldField, newField));
                }
            }

            Logger.getLogger(getClass()).debug("      Collecting methods ...");

            Map<String, String> methodLevel = new TreeMap<String, String>();

            for (Method_info method : oldClass.getAllMethods()) {
                methodLevel.put(method.getSignature(), method.getFullSignature());
            }

            for (Method_info method : newClass.getAllMethods()) {
                methodLevel.put(method.getSignature(), method.getFullSignature());
            }

            Logger.getLogger(getClass()).debug("      Diff'ing methods ...");

            for (Map.Entry<String, String> methodEntry : methodLevel.entrySet()) {
                Method_info oldMethod = oldClass.getMethod(methodEntry.getKey());
                Method_info newMethod = newClass.getMethod(methodEntry.getKey());

                if (strategy.isMethodDifferent(oldMethod, newMethod)) {
                    classDifferences.getFeatureDifferences().add(createFeatureDifferences(methodEntry.getValue(), oldMethod, newMethod));
                }
            }

            Logger.getLogger(getClass()).debug(name + " has " + classDifferences.getFeatureDifferences().size() + " feature(s) that changed.");

            if (oldClass.isDeprecated() != newClass.isDeprecated()) {
                result = new DeprecatableDifferences(result, oldClass, newClass);
            }
        }

        Logger.getLogger(getClass()).debug("End   " + name);

        return result;
    }

    public Differences createFeatureDifferences(String name, Feature_info oldFeature, Feature_info newFeature) {
        Logger.getLogger(getClass()).debug("Begin " + name);

        FeatureDifferences featureDifferences;
        if (oldFeature instanceof Field_info || newFeature instanceof Field_info) {
            featureDifferences = new FieldDifferences(name, (Field_info) oldFeature, (Field_info) newFeature);

            if (!featureDifferences.isRemoved() && !featureDifferences.isNew() && strategy.isConstantValueDifferent(((Field_info) oldFeature).getConstantValue(), ((Field_info) newFeature).getConstantValue())) {
                ((FieldDifferences) featureDifferences).setConstantValueDifference(true);
            }

            if (featureDifferences.isRemoved() && newClass.locateField(name) != null) {
                featureDifferences.setInherited(true);
            }
        } else {
            if (((oldFeature instanceof Method_info) && ((Method_info) oldFeature).isConstructor()) || ((newFeature instanceof Method_info) && ((Method_info) newFeature).isConstructor())) {
                featureDifferences = new ConstructorDifferences(name, (Method_info) oldFeature, (Method_info) newFeature);
            } else {
                featureDifferences = new MethodDifferences(name, (Method_info) oldFeature, (Method_info) newFeature);
            }

            if (!featureDifferences.isRemoved() && !featureDifferences.isNew() && strategy.isCodeDifferent(((Method_info) oldFeature).getCode(), ((Method_info) newFeature).getCode())) {
                ((CodeDifferences) featureDifferences).setCodeDifference(true);
            }

            if (featureDifferences.isRemoved()) {
                Method_info attempt = newClass.locateMethod(name);
                if ((attempt != null) && (oldFeature.getClassfile().isInterface() == attempt.getClassfile().isInterface())) {
                    featureDifferences.setInherited(true);
                }
            }
        }

        Differences result = featureDifferences;
        
        if (oldFeature != null && newFeature != null) {
            if (oldFeature.isDeprecated() != newFeature.isDeprecated()) {
                result = new DeprecatableDifferences(result, oldFeature, newFeature);
            }
        }

        Logger.getLogger(getClass()).debug("End   " + name);

        return result;
    }
}
