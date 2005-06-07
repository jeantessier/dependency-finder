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

import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;

public class DifferencesFactory {
    private Validator oldValidator;
    private Validator newValidator;

    private ClassfileLoader oldJar;
    private ClassfileLoader newJar;
    private Classfile oldClass;
    private Classfile newClass;

    public DifferencesFactory(Validator oldValidator, Validator newValidator) {
        this.oldValidator = oldValidator;
        this.newValidator = newValidator;
    }

    public Differences createJarDifferences(String name, String oldVersion, ClassfileLoader oldJar, String newVersion, ClassfileLoader newJar) {
        Logger.getLogger(getClass()).debug("Begin " + name + " (" + oldVersion + " -> " + newVersion + ")");

        JarDifferences jarDifferences = new JarDifferences(name, oldVersion, newVersion);
        Differences    result = jarDifferences;
        
        this.oldJar = oldJar;
        this.newJar = newJar;
        
        Logger.getLogger(getClass()).debug("      Collecting packages ...");
        
        Iterator   i;

        NodeFactory oldFactory = new NodeFactory();
        i = oldJar.getAllClassfiles().iterator();
        while (i.hasNext()) {
            oldFactory.createClass(i.next().toString());
        }

        NodeFactory newFactory = new NodeFactory();
        i = newJar.getAllClassfiles().iterator();
        while (i.hasNext()) {
            newFactory.createClass(i.next().toString());
        }
        
        Collection packageLevel = new TreeSet();
        packageLevel.addAll(oldFactory.getPackages().keySet());
        packageLevel.addAll(newFactory.getPackages().keySet());
        
        Logger.getLogger(getClass()).debug("      Diff'ing packages ...");
        
        i = packageLevel.iterator();
        while (i.hasNext()) {
            String packageName = (String) i.next();
            
            PackageNode oldPackage = (PackageNode) oldFactory.getPackages().get(packageName);
            PackageNode newPackage = (PackageNode) newFactory.getPackages().get(packageName);
            
            Differences differences = createPackageDifferences(packageName, oldPackage, newPackage);
            if (!differences.isEmpty()) {
                jarDifferences.getPackageDifferences().add(differences);
            }
        }
        
        Logger.getLogger(getClass()).debug("End   " + name + " (" + oldVersion + " -> " + newVersion + "): " + (result.isEmpty() ? "empty" : "not empty"));
        
        return result;
    }
    
    public Differences createPackageDifferences(String name, PackageNode oldPackage, PackageNode newPackage) {
        Logger.getLogger(getClass()).debug("Begin " + name);
        
        PackageDifferences packageDifferences = new PackageDifferences(name, oldPackage, newPackage);
        Differences        result = packageDifferences;
        
        if (oldPackage != null && newPackage != null) {
            
            Collection classLevel = new TreeSet();
            Iterator   i;
            
            i = oldPackage.getClasses().iterator();
            while (i.hasNext()) {
                classLevel.add(i.next().toString());
            }
            
            i = newPackage.getClasses().iterator();
            while (i.hasNext()) {
                classLevel.add(i.next().toString());
            }
            
            Logger.getLogger(getClass()).debug("      Diff'ing classes ...");
            
            i = classLevel.iterator();
            while (i.hasNext()) {
                String className = (String) i.next();
                
                Classfile oldClass = oldJar.getClassfile(className);
                Classfile newClass = newJar.getClassfile(className);
                
                Differences differences = createClassDifferences(className, oldClass, newClass);
                if (!differences.isEmpty()) {
                    packageDifferences.getClassDifferences().add(differences);
                }
            }
            
            Logger.getLogger(getClass()).debug("      " + name + " has " + packageDifferences.getClassDifferences().size() + " class(es) that changed.");
            
            if (oldValidator.isAllowed(name) != newValidator.isAllowed(name)) {
                result = new DocumentableDifferences(result, oldValidator, newValidator);
            }
        }
        
        Logger.getLogger(getClass()).debug("End   " + name + ": " + (result.isEmpty() ? "empty" : "not empty"));
        
        return result;
    }
    
    public Differences createClassDifferences(String name, Classfile oldClass, Classfile newClass) {
        Logger.getLogger(getClass()).debug("Begin " + name);

        ClassDifferences classDifferences;
        if (((oldClass != null) && oldClass.isInterface()) || ((newClass != null) && newClass.isInterface())) {
            classDifferences = new InterfaceDifferences(name, oldClass, newClass);
        } else {
            classDifferences = new ClassDifferences(name, oldClass, newClass);
        }
        Differences result = classDifferences;

        this.oldClass = oldClass;
        this.newClass = newClass;
        
        if (oldClass != null && newClass != null) {
            Logger.getLogger(getClass()).debug("      Collecting fields ...");
            
            Map fieldLevel = new TreeMap();
            Iterator i;
            
            i = oldClass.getAllFields().iterator();
            while (i.hasNext()) {
                Field_info field = (Field_info) i.next();
                fieldLevel.put(field.getName(), field.getFullSignature());
            }
            
            i = newClass.getAllFields().iterator();
            while (i.hasNext()) {
                Field_info field = (Field_info) i.next();
                fieldLevel.put(field.getName(), field.getFullSignature());
            }
            
            Logger.getLogger(getClass()).debug("      Diff'ing fields ...");
            
            i = fieldLevel.keySet().iterator();
            while (i.hasNext()) {
                String fieldName     = (String) i.next();
                String fieldFullName = (String) fieldLevel.get(fieldName);
                
                Field_info oldField = oldClass.getField(fieldName);
                Field_info newField = newClass.getField(fieldName);
                
                Differences differences = createFeatureDifferences(fieldFullName, oldField, newField);
                if (!differences.isEmpty()) {
                    classDifferences.getFeatureDifferences().add(differences);
                }
            }
            
            Logger.getLogger(getClass()).debug("      Collecting methods ...");
            
            Map methodLevel = new TreeMap();
            
            i = oldClass.getAllMethods().iterator();
            while (i.hasNext()) {
                Method_info method = (Method_info) i.next();
                methodLevel.put(method.getSignature(), method.getFullSignature());
            }
            
            i = newClass.getAllMethods().iterator();
            while (i.hasNext()) {
                Method_info method = (Method_info) i.next();
                methodLevel.put(method.getSignature(), method.getFullSignature());
            }
            
            Logger.getLogger(getClass()).debug("      Diff'ing methods ...");
            
            i = methodLevel.keySet().iterator();
            while (i.hasNext()) {
                String methodName     = (String) i.next();
                String methodFullName = (String) methodLevel.get(methodName);
                
                Method_info oldMethod = oldClass.getMethod(methodName);
                Method_info newMethod = newClass.getMethod(methodName);
                
                Differences differences = createFeatureDifferences(methodFullName, oldMethod, newMethod);
                if (!differences.isEmpty()) {
                    classDifferences.getFeatureDifferences().add(differences);
                }
            }
            
            Logger.getLogger(getClass()).debug(name + " has " + classDifferences.getFeatureDifferences().size() + " feature(s) that changed.");

            if (oldClass.isDeprecated() != newClass.isDeprecated()) {
                result = new DeprecatableDifferences(result, oldClass, newClass);
            }
            
            if (oldValidator.isAllowed(name) != newValidator.isAllowed(name)) {
                result = new DocumentableDifferences(result, oldValidator, newValidator);
            }
        }

        Logger.getLogger(getClass()).debug("End   " + name + ": " + (result.isEmpty() ? "empty" : "not empty"));

        return result;
    }

    public Differences createFeatureDifferences(String name, Feature_info oldFeature, Feature_info newFeature) {
        Logger.getLogger(getClass()).debug("Begin " + name);

        FeatureDifferences featureDifferences;
        if (oldFeature instanceof Field_info || newFeature instanceof Field_info) {
            featureDifferences = new FieldDifferences(name, oldFeature, newFeature);

            if (featureDifferences.isRemoved() && newClass.locateField(name) != null) {
                featureDifferences.setInherited(true);
            }
        } else {
            if (((oldFeature instanceof Method_info) && ((Method_info) oldFeature).isConstructor()) || ((newFeature instanceof Method_info) && ((Method_info) newFeature).isConstructor())) {
                featureDifferences = new ConstructorDifferences(name, (Method_info) oldFeature, (Method_info) newFeature);
            } else {
                featureDifferences = new MethodDifferences(name, (Method_info) oldFeature, (Method_info) newFeature);
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

            if (oldValidator.isAllowed(name) != newValidator.isAllowed(name)) {
                result = new DocumentableDifferences(result, oldValidator, newValidator);
            }
        }

        Logger.getLogger(getClass()).debug("End   " + name + ": " + (result.isEmpty() ? "empty" : "not empty"));

        return result;
    }
}
