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

package com.jeantessier.metrics;

import com.jeantessier.classreader.ClassNameHelper;
import com.jeantessier.classreader.Class_info;
import com.jeantessier.classreader.Classfile;
import com.jeantessier.classreader.Deprecated_attribute;
import com.jeantessier.classreader.DescriptorHelper;
import com.jeantessier.classreader.ExceptionHandler;
import com.jeantessier.classreader.FieldRef_info;
import com.jeantessier.classreader.Field_info;
import com.jeantessier.classreader.InnerClass;
import com.jeantessier.classreader.Instruction;
import com.jeantessier.classreader.InterfaceMethodRef_info;
import com.jeantessier.classreader.LineNumber;
import com.jeantessier.classreader.LocalVariable;
import com.jeantessier.classreader.MethodRef_info;
import com.jeantessier.classreader.Method_info;
import com.jeantessier.classreader.Synthetic_attribute;
import com.jeantessier.classreader.VisitorBase;
import org.apache.logging.log4j.*;
import org.apache.oro.text.perl.Perl5Util;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

/**
 *  Collects metrics from Classfile instances.
 *  
 *  This class can only approximate SLOC based on information provided
 *  by the compiler.
 */
public class MetricsGatherer extends VisitorBase {
    private static final Perl5Util perl = new Perl5Util();

    private final MetricsFactory factory;

    private Collection<String> scope = null;
    private Collection<String> filter = null;
    
    private Metrics currentProject;
    private Metrics currentGroup;
    private Metrics currentClass;
    private Metrics currentMethod;

    private int sloc;
    private boolean isSynthetic;
    
    private final HashSet<MetricsListener> metricsListeners = new HashSet<>();

    public MetricsGatherer(MetricsFactory factory) {
        this.factory = factory;

        setCurrentProject(getMetricsFactory().createProjectMetrics());
    }

    public MetricsFactory getMetricsFactory() {
        return factory;
    }

    public void setScopeIncludes(Collection<String> scope) {
        this.scope = scope;
    }
    
    public void setFilterIncludes(Collection<String> filter) {
        this.filter = filter;
    }
    
    private Metrics getCurrentProject() {
        return currentProject;
    }

    void setCurrentProject(Metrics currentProject) {
        this.currentProject = currentProject;
    }

    private Metrics getCurrentGroup() {
        return currentGroup;
    }

    void setCurrentGroup(Metrics currentGroup) {
        this.currentGroup = currentGroup;
    }

    private Metrics getCurrentClass() {
        return currentClass;
    }

    void setCurrentClass(Metrics currentClass) {
        this.currentClass = currentClass;
    }

    private Metrics getCurrentMethod() {
        return currentMethod;
    }

    void setCurrentMethod(Metrics currentMethod) {
        this.currentMethod = currentMethod;
    }

    private Collection<Metrics> getAllMatchingGroups(String className) {
        Collection<Metrics> result = new java.util.ArrayList<>();

        result.add(getCurrentGroup());
        result.addAll(getMetricsFactory().getGroupMetrics(className));

        return result;
    }

    public void visitClassfiles(Collection<Classfile> classfiles) {
        fireBeginSession(classfiles.size());

        super.visitClassfiles(classfiles);
        
        fireEndSession();
    }
    
    // Classfile
    public void visitClassfile(Classfile classfile) {
        String className = classfile.getClassName();
        LogManager.getLogger(getClass()).debug("VisitClassfile():");
        LogManager.getLogger(getClass()).debug("    class = \"{}\"", className);

        fireBeginClass(classfile);
        
        setCurrentMethod(null);
        setCurrentClass(getMetricsFactory().createClassMetrics(className));
        setCurrentGroup(getCurrentClass().getParent());
        setCurrentProject(getCurrentGroup().getParent());

        getMetricsFactory().includeClassMetrics(getCurrentClass());

        getCurrentClass().addToMeasurement(BasicMeasurements.MAJOR_VERSION, classfile.getMajorVersion());
        getCurrentClass().addToMeasurement(BasicMeasurements.MINOR_VERSION, classfile.getMinorVersion());

        Collection<Metrics> groups = getAllMatchingGroups(className);

        groups.forEach(group -> group.addToMeasurement(BasicMeasurements.PACKAGES, getCurrentGroup().getName()));

        if (classfile.isPublic()) {
            getCurrentProject().addToMeasurement(BasicMeasurements.PUBLIC_CLASSES, className);
            groups.forEach(group -> group.addToMeasurement(BasicMeasurements.PUBLIC_CLASSES, className));
        } else {
            getCurrentProject().addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, className);
            groups.forEach(group -> group.addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, className));
        }

        if (classfile.isFinal()) {
            getCurrentProject().addToMeasurement(BasicMeasurements.FINAL_CLASSES, className);
            groups.forEach(group -> group.addToMeasurement(BasicMeasurements.FINAL_CLASSES, className));
        }

        if (classfile.isSuper()) {
            getCurrentProject().addToMeasurement(BasicMeasurements.SUPER_CLASSES, className);
            groups.forEach(group -> group.addToMeasurement(BasicMeasurements.SUPER_CLASSES, className));
        }

        if (classfile.isInterface()) {
            getCurrentProject().addToMeasurement(BasicMeasurements.INTERFACES, className);
            groups.forEach(group -> group.addToMeasurement(BasicMeasurements.INTERFACES, className));
        }

        if (classfile.isAbstract()) {
            getCurrentProject().addToMeasurement(BasicMeasurements.ABSTRACT_CLASSES, className);
            groups.forEach(group -> group.addToMeasurement(BasicMeasurements.ABSTRACT_CLASSES, className));
        }

        if (classfile.isSynthetic()) {
            getCurrentProject().addToMeasurement(BasicMeasurements.SYNTHETIC_CLASSES, className);
            groups.forEach(group -> group.addToMeasurement(BasicMeasurements.SYNTHETIC_CLASSES, className));
        }

        if (classfile.getSuperclassIndex() != 0) {
            classfile.getRawSuperclass().accept(this);

            getMetricsFactory().createClassMetrics(classfile.getSuperclassName()).addToMeasurement(BasicMeasurements.SUBCLASSES);

            Classfile superclass = classfile.getLoader().getClassfile(classfile.getSuperclassName());
            if (superclass != null) {
                getCurrentClass().addToMeasurement(BasicMeasurements.DEPTH_OF_INHERITANCE, computeDepthOfInheritance(superclass));
            }
        }

        classfile.getAllInterfaces().forEach(class_info -> class_info.accept(this));
        classfile.getAllFields().forEach(field -> field.accept(this));
        classfile.getAllMethods().forEach(method -> method.accept(this));

        sloc = 1;

        classfile.getAttributes().forEach(attribute -> attribute.accept(this));

        if (!classfile.isSynthetic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.CLASS_SLOC, sloc);
        }

        fireEndClass(classfile, getCurrentClass());
    }
    
    // ConstantPool entries
    public void visitClass_info(Class_info entry) {
        LogManager.getLogger(getClass()).debug("VisitClass_info():");
        LogManager.getLogger(getClass()).debug("    name = \"{}\"", () -> entry.getName());
        if (entry.getName().startsWith("[")) {
            addClassDependencies(processDescriptor(entry.getName()));
        } else {
            addClassDependency(entry.getName());
        }
    }
    
    public void visitFieldRef_info(FieldRef_info entry) {
        LogManager.getLogger(getClass()).debug("VisitFieldRef_info():");
        LogManager.getLogger(getClass()).debug("    class = \"{}\"", () -> entry.getClassName());
        LogManager.getLogger(getClass()).debug("    name = \"{}\"", () -> entry.getRawNameAndType().getName());
        LogManager.getLogger(getClass()).debug("    type = \"{}\"", () -> entry.getRawNameAndType().getType());

        // Dependencies on attributes are accounted as dependencies on their class
        entry.getRawClass().accept(this);
        addClassDependencies(processDescriptor(entry.getRawNameAndType().getType()));
    }

    public void visitMethodRef_info(MethodRef_info entry) {
        LogManager.getLogger(getClass()).debug("VisitMethodRef_info():");
        LogManager.getLogger(getClass()).debug("    class = \"{}\"", () -> entry.getClassName());
        LogManager.getLogger(getClass()).debug("    name = \"{}\"", () -> entry.getRawNameAndType().getName());
        LogManager.getLogger(getClass()).debug("    type = \"{}\"", () -> entry.getRawNameAndType().getType());
        addMethodDependency(entry.getFullSignature());
        addClassDependencies(processDescriptor(entry.getRawNameAndType().getType()));
    }

    public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
        LogManager.getLogger(getClass()).debug("VisitInterfaceMethodRef_info():");
        LogManager.getLogger(getClass()).debug("    class = \"{}\"", () -> entry.getClassName());
        LogManager.getLogger(getClass()).debug("    name = \"{}\"", () -> entry.getRawNameAndType().getName());
        LogManager.getLogger(getClass()).debug("    type = \"{}\"", () -> entry.getRawNameAndType().getType());
        addMethodDependency(entry.getFullSignature());
        addClassDependencies(processDescriptor(entry.getRawNameAndType().getType()));
    }

    public void visitField_info(Field_info entry) {
        String fullName = entry.getFullName();
        getCurrentClass().addToMeasurement(BasicMeasurements.ATTRIBUTES, fullName);

        LogManager.getLogger(getClass()).debug("VisitField_info({})", () -> entry.getFullSignature());
        LogManager.getLogger(getClass()).debug("Current class: {}", () -> getCurrentClass().getName());
        LogManager.getLogger(getClass()).debug("Access flag: {}", () -> entry.getAccessFlags());
        LogManager.getLogger(getClass()).debug("Public: {}", () -> entry.isPublic());
        LogManager.getLogger(getClass()).debug("Private: {}", () -> entry.isPrivate());
        LogManager.getLogger(getClass()).debug("Protected: {}", () -> entry.isProtected());
        LogManager.getLogger(getClass()).debug("Static: {}", () -> entry.isStatic());
        
        if (entry.isPublic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PUBLIC_ATTRIBUTES, fullName);
        } else if (entry.isPrivate()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PRIVATE_ATTRIBUTES, fullName);
        } else if (entry.isProtected()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PROTECTED_ATTRIBUTES, fullName);
        } else {
            getCurrentClass().addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, fullName);
        }

        if (entry.isFinal()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.FINAL_ATTRIBUTES, fullName);
        }

        if (entry.isDeprecated()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.DEPRECATED_ATTRIBUTES, fullName);
        }

        if (entry.isSynthetic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.SYNTHETIC_ATTRIBUTES, fullName);
        }

        if (entry.isStatic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.STATIC_ATTRIBUTES, fullName);
        }

        if (entry.isTransient()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.TRANSIENT_ATTRIBUTES, fullName);
        }

        if (entry.isVolatile()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.VOLATILE_ATTRIBUTES, fullName);
        }

        if (entry.isEnum()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.ENUM_ATTRIBUTES, fullName);
        }

        sloc = 1;

        super.visitField_info(entry);
        
        if (!entry.isSynthetic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.CLASS_SLOC, sloc);
        }

        addClassDependencies(processDescriptor(entry.getDescriptor()));
    }

    public void visitMethod_info(Method_info entry) {
        fireBeginMethod(entry);

        String fullSignature = entry.getFullSignature();
        setCurrentMethod(getMetricsFactory().createMethodMetrics(fullSignature));
        getMetricsFactory().includeMethodMetrics(getCurrentMethod());
        
        LogManager.getLogger(getClass()).debug("VisitMethod_info({})", () -> entry.getFullSignature());
        LogManager.getLogger(getClass()).debug("Current class: {}", () -> getCurrentClass().getName());
        LogManager.getLogger(getClass()).debug("Access flag: {}", () -> entry.getAccessFlags());
        LogManager.getLogger(getClass()).debug("Public: {}", () -> entry.isPublic());
        LogManager.getLogger(getClass()).debug("Private: {}", () -> entry.isPrivate());
        LogManager.getLogger(getClass()).debug("Protected: {}", () -> entry.isProtected());
        LogManager.getLogger(getClass()).debug("Static: {}", () -> entry.isStatic());

        sloc = 0;

        if (entry.isPublic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PUBLIC_METHODS, fullSignature);
        } else if (entry.isPrivate()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PRIVATE_METHODS, fullSignature);
        } else if (entry.isProtected()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PROTECTED_METHODS, fullSignature);
        } else {
            getCurrentClass().addToMeasurement(BasicMeasurements.PACKAGE_METHODS, fullSignature);
        }

        if (entry.isFinal()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.FINAL_METHODS, fullSignature);
        }

        if (entry.isAbstract()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.ABSTRACT_METHODS, fullSignature);
            sloc = 1;
        }

        if (entry.isDeprecated()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.DEPRECATED_METHODS, fullSignature);
        }

        if (entry.isStatic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.STATIC_METHODS, fullSignature);
        }

        if (entry.isSynchronized()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.SYNCHRONIZED_METHODS, fullSignature);
        }

        if (entry.isBridge()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.BRIDGE_METHODS, fullSignature);
        }

        if (entry.isVarargs()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.VARARGS_METHODS, fullSignature);
        }

        if (entry.isNative()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.NATIVE_METHODS, fullSignature);
        }

        if (entry.isStrict()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.STRICT_METHODS, fullSignature);
        }

        getCurrentMethod().addToMeasurement(BasicMeasurements.PARAMETERS, DescriptorHelper.getParameterCount(entry.getDescriptor()));
        
        super.visitMethod_info(entry);
        
        if (!entry.isSynthetic()) {
            getCurrentMethod().addToMeasurement(BasicMeasurements.SLOC, sloc);
        }

        addClassDependencies(processDescriptor(entry.getDescriptor()));

        fireEndMethod(entry, getCurrentMethod());
    }

    // 
    // Attributes
    //

    public void visitSynthetic_attribute(Synthetic_attribute attribute) {
        Object owner = attribute.getOwner();

        isSynthetic = true;
        
        if (owner instanceof Field_info fieldInfo) {
            getCurrentClass().addToMeasurement(BasicMeasurements.SYNTHETIC_ATTRIBUTES, fieldInfo.getFullName());
        } else if (owner instanceof Method_info methodInfo) {
            getCurrentClass().addToMeasurement(BasicMeasurements.SYNTHETIC_METHODS, methodInfo.getFullSignature());
        } else {
            LogManager.getLogger(getClass()).warn("Synthetic attribute on unknown Visitable: {}", () -> owner.getClass().getName());
        }
    }

    public void visitDeprecated_attribute(Deprecated_attribute attribute) {
        Object owner = attribute.getOwner();

        if (owner instanceof Classfile classfile) {
            String className = classfile.getClassName();
            getCurrentProject().addToMeasurement(BasicMeasurements.DEPRECATED_CLASSES, className);
            getAllMatchingGroups(className).forEach(group -> group.addToMeasurement(BasicMeasurements.DEPRECATED_CLASSES, className));
        } else if (owner instanceof Field_info fieldInfo) {
            getCurrentClass().addToMeasurement(BasicMeasurements.DEPRECATED_ATTRIBUTES, fieldInfo.getFullName());
        } else if (owner instanceof Method_info methodInfo) {
            getCurrentClass().addToMeasurement(BasicMeasurements.DEPRECATED_METHODS, methodInfo.getFullSignature());
        } else {
            LogManager.getLogger(getClass()).warn("Deprecated attribute on unknown Visitable: {}", () -> owner.getClass().getName());
        }
    }

    // 
    // Attribute helpers
    //

    public void visitInstruction(Instruction helper) {
        super.visitInstruction(helper);

        /*
         *  We can skip the "new" (0xbb) instruction as it is always
         *  followed by a call to the constructor method.
         */

        switch (helper.getOpcode()) {
            case 0x12: // ldc
            case 0x13: // ldc_w
            case 0xb2: // getstatic
            case 0xb3: // putstatic
            case 0xb4: // getfield
            case 0xb5: // putfield
            case 0xb6: // invokevirtual
            case 0xb7: // invokespecial
            case 0xb8: // invokestatic
            case 0xb9: // invokeinterface
            // case 0xbb: // new
            case 0xbd: // anewarray
            case 0xc0: // checkcast
            case 0xc1: // instanceof
            case 0xc5: // multianewarray
                helper.getIndexedConstantPoolEntry().accept(this);
                break;
            default:
                // Do nothing
                break;
        }
    }

    public void visitExceptionHandler(ExceptionHandler helper) {
        if (helper.getCatchTypeIndex() != 0) {
            helper.getRawCatchType().accept(this);
        }
    }

    public void visitInnerClass(InnerClass helper) {
        if (isInnerClassOfCurrentClass(helper)) {
            String innerClassName = helper.getInnerClassInfo();

            Collection<Metrics> groups = getAllMatchingGroups(innerClassName);

            getCurrentProject().addToMeasurement(BasicMeasurements.INNER_CLASSES, innerClassName);
            groups.forEach(group -> group.addToMeasurement(BasicMeasurements.INNER_CLASSES, innerClassName));
            getCurrentClass().addToMeasurement(BasicMeasurements.INNER_CLASSES, innerClassName);
        
            if (helper.isPublic()) {
                getCurrentProject().addToMeasurement(BasicMeasurements.PUBLIC_INNER_CLASSES, innerClassName);
                groups.forEach(group -> group.addToMeasurement(BasicMeasurements.PUBLIC_INNER_CLASSES, innerClassName));
                getCurrentClass().addToMeasurement(BasicMeasurements.PUBLIC_INNER_CLASSES, innerClassName);
            } else if (helper.isPrivate()) {
                getCurrentProject().addToMeasurement(BasicMeasurements.PRIVATE_INNER_CLASSES, innerClassName);
                groups.forEach(group -> group.addToMeasurement(BasicMeasurements.PRIVATE_INNER_CLASSES, innerClassName));
                getCurrentClass().addToMeasurement(BasicMeasurements.PRIVATE_INNER_CLASSES, innerClassName);
            } else if (helper.isProtected()) {
                getCurrentProject().addToMeasurement(BasicMeasurements.PROTECTED_INNER_CLASSES, innerClassName);
                groups.forEach(group -> group.addToMeasurement(BasicMeasurements.PROTECTED_INNER_CLASSES, innerClassName));
                getCurrentClass().addToMeasurement(BasicMeasurements.PROTECTED_INNER_CLASSES, innerClassName);
            } else {
                getCurrentProject().addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, innerClassName);
                groups.forEach(group -> group.addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, innerClassName));
                getCurrentClass().addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, innerClassName);
            }

            if (helper.isStatic()) {
                getCurrentProject().addToMeasurement(BasicMeasurements.STATIC_INNER_CLASSES, innerClassName);
                groups.forEach(group -> group.addToMeasurement(BasicMeasurements.STATIC_INNER_CLASSES, innerClassName));
                getCurrentClass().addToMeasurement(BasicMeasurements.STATIC_INNER_CLASSES, innerClassName);
            }

            if (helper.isFinal()) {
                getCurrentProject().addToMeasurement(BasicMeasurements.FINAL_INNER_CLASSES, innerClassName);
                groups.forEach(group -> group.addToMeasurement(BasicMeasurements.FINAL_INNER_CLASSES, innerClassName));
                getCurrentClass().addToMeasurement(BasicMeasurements.FINAL_INNER_CLASSES, innerClassName);
            }

            if (helper.isInterface()) {
                getCurrentProject().addToMeasurement(BasicMeasurements.INTERFACE_INNER_CLASSES, innerClassName);
                groups.forEach(group -> group.addToMeasurement(BasicMeasurements.INTERFACE_INNER_CLASSES, innerClassName));
                getCurrentClass().addToMeasurement(BasicMeasurements.INTERFACE_INNER_CLASSES, innerClassName);
            }

            if (helper.isAbstract()) {
                getCurrentProject().addToMeasurement(BasicMeasurements.ABSTRACT_INNER_CLASSES, innerClassName);
                groups.forEach(group -> group.addToMeasurement(BasicMeasurements.ABSTRACT_INNER_CLASSES, innerClassName));
                getCurrentClass().addToMeasurement(BasicMeasurements.ABSTRACT_INNER_CLASSES, innerClassName);
            }

            if (helper.isSynthetic()) {
                getCurrentProject().addToMeasurement(BasicMeasurements.SYNTHETIC_INNER_CLASSES, innerClassName);
                groups.forEach(group -> group.addToMeasurement(BasicMeasurements.SYNTHETIC_INNER_CLASSES, innerClassName));
                getCurrentClass().addToMeasurement(BasicMeasurements.SYNTHETIC_INNER_CLASSES, innerClassName);
            }

            if (helper.isAnnotation()) {
                getCurrentProject().addToMeasurement(BasicMeasurements.ANNOTATION_INNER_CLASSES, innerClassName);
                groups.forEach(group -> group.addToMeasurement(BasicMeasurements.ANNOTATION_INNER_CLASSES, innerClassName));
                getCurrentClass().addToMeasurement(BasicMeasurements.ANNOTATION_INNER_CLASSES, innerClassName);
            }

            if (helper.isEnum()) {
                getCurrentProject().addToMeasurement(BasicMeasurements.ENUM_INNER_CLASSES, innerClassName);
                groups.forEach(group -> group.addToMeasurement(BasicMeasurements.ENUM_INNER_CLASSES, innerClassName));
                getCurrentClass().addToMeasurement(BasicMeasurements.ENUM_INNER_CLASSES, innerClassName);
            }
        }
    }

    // Package-level for testing
    boolean isInnerClassOfCurrentClass(InnerClass helper) {
        boolean result;

        if (helper.getOuterClassInfo().equals("")) {
            result = perl.match("/^" + getCurrentClass().getName() + "\\$\\d+$/", helper.getInnerClassInfo());
        } else {
            result = helper.getOuterClassInfo().equals(getCurrentClass().getName());
        }

        return result;
    }

    public void visitLineNumber(LineNumber helper) {
        sloc++;
    }

    public void visitLocalVariable(LocalVariable helper) {
        getCurrentMethod().addToMeasurement(BasicMeasurements.LOCAL_VARIABLES, helper.getName());

        addClassDependencies(processDescriptor(helper.getDescriptor()));
    }

    private int computeDepthOfInheritance(Classfile classfile) {
        int result = 1;
        
        if (classfile != null && classfile.getSuperclassIndex() != 0) {
            Classfile superclass = classfile.getLoader().getClassfile(classfile.getSuperclassName());
            result += computeDepthOfInheritance(superclass);
        }

        return result;
    }
    
    private Collection<String> processDescriptor(String str) {
        Collection<String> result = new LinkedList<>();
        
        LogManager.getLogger(getClass()).debug("ProcessDescriptor: {}", str);

        int currentPos = 0;
        int startPos;
        int endPos;

        while ((startPos = str.indexOf('L', currentPos)) != -1) {
            if ((endPos = str.indexOf(';', startPos)) != -1) {
                String classname = ClassNameHelper.path2ClassName(str.substring(startPos + 1, endPos));
                result.add(classname);
                currentPos = endPos + 1;
            } else {
                currentPos = startPos + 1;
            }
        }

        LogManager.getLogger(getClass()).debug("ProcessDescriptor: {}", result);
        
        return result;
    }

    private void addClassDependencies(Collection<String> classnames) {
        classnames.forEach(this::addClassDependency);
    }
    
    private void addClassDependency(String name) {
        LogManager.getLogger(getClass()).debug("AddClassDependency(\"{}\") ...", name);

        if (!getCurrentClass().getName().equals(name) && isInFilter(name)) {
            Metrics other = getMetricsFactory().createClassMetrics(name);
                
            if (getCurrentMethod() != null && isInScope(getCurrentMethod().getName())) {
                LogManager.getLogger(getClass()).debug("AddClassDependency {} -> {} ...", getCurrentMethod().getName(), name);
                
                if (getCurrentClass().getParent().equals(other.getParent())) {
                    LogManager.getLogger(getClass()).debug("Intra-Package ...");
                    getCurrentMethod().addToMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES, other.getName());
                    other.addToMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES, getCurrentMethod().getName());
                } else {
                    LogManager.getLogger(getClass()).debug("Extra-Package ...");
                    getCurrentMethod().addToMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES, other.getName());
                    other.addToMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES, getCurrentMethod().getName());
                }
            } else if (isInScope(getCurrentClass().getName())) {
                LogManager.getLogger(getClass()).debug("AddClassDependency {} -> {} ...", getCurrentClass().getName(), name);
                
                if (getCurrentClass().getParent().equals(other.getParent())) {
                    LogManager.getLogger(getClass()).debug("Intra-Package ...");
                    getCurrentClass().addToMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES, other.getName());
                    other.addToMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES, getCurrentClass().getName());
                } else {
                    LogManager.getLogger(getClass()).debug("Extra-Package ...");
                    getCurrentClass().addToMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES, other.getName());
                    other.addToMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES, getCurrentClass().getName());
                }
            }
        }
    }
    
    private void addMethodDependency(String name) {
        LogManager.getLogger(getClass()).debug("AddMethodDependency {} -> {} ...", getCurrentMethod().getName(), name);

        if (!getCurrentMethod().getName().equals(name) && isInScope(getCurrentMethod().getName()) && isInFilter(name)) {
            Metrics other = getMetricsFactory().createMethodMetrics(name);
            
            if (getCurrentClass().equals(other.getParent())) {
                LogManager.getLogger(getClass()).debug("Intra-Class ...");
                getCurrentMethod().addToMeasurement(BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES, other.getName());
                other.addToMeasurement(BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES, getCurrentMethod().getName());
            } else if (getCurrentGroup().equals(other.getParent().getParent())) {
                LogManager.getLogger(getClass()).debug("Intra-Package ...");
                getCurrentMethod().addToMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES, other.getName());
                other.addToMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES, getCurrentMethod().getName());
            } else {
                LogManager.getLogger(getClass()).debug("Extra-Package ...");
                getCurrentMethod().addToMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES, other.getName());
                other.addToMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES, getCurrentMethod().getName());
            }
        }
    }
    
    private boolean isInScope(String name) {
        boolean result = true;

        if (scope != null) {
            result = scope.contains(name);
        }

        return result;
    }
    
    private boolean isInFilter(String name) {
        boolean result = true;

        if (filter != null) {
            result = filter.contains(name);
        }

        return result;
    }

    public void addMetricsListener(MetricsListener listener) {
        synchronized(metricsListeners) {
            metricsListeners.add(listener);
        }
    }

    public void removeMetricsListener(MetricsListener listener) {
        synchronized(metricsListeners) {
            metricsListeners.remove(listener);
        }
    }

    protected void fireBeginSession(int size) {
        MetricsEvent event = new MetricsEvent(this, size);
        cloneListeners().forEach(listener -> listener.beginSession(event));
    }

    protected void fireBeginClass(Classfile classfile) {
        MetricsEvent event = new MetricsEvent(this, classfile);
        cloneListeners().forEach(listener -> listener.beginClass(event));
    }

    protected void fireBeginMethod(Method_info method) {
        MetricsEvent event = new MetricsEvent(this, method);
        cloneListeners().forEach(listener -> listener.beginMethod(event));
    }

    protected void fireEndMethod(Method_info method, Metrics metrics) {
        MetricsEvent event = new MetricsEvent(this, method, metrics);
        cloneListeners().forEach(listener -> listener.endMethod(event));
    }

    protected void fireEndClass(Classfile classfile, Metrics metrics) {
        MetricsEvent event = new MetricsEvent(this, classfile, metrics);
        cloneListeners().forEach(listener -> listener.endClass(event));
    }

    protected void fireEndSession() {
        MetricsEvent event = new MetricsEvent(this);
        cloneListeners().forEach(listener -> listener.endSession(event));
    }

    private Collection<MetricsListener> cloneListeners() {
        Collection<MetricsListener> result;
        synchronized(metricsListeners) {
            result = (Collection<MetricsListener>) metricsListeners.clone();
        }
        return result;
    }
}
