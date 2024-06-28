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

package com.jeantessier.metrics;

import com.jeantessier.classreader.*;
import org.apache.logging.log4j.*;
import org.apache.oro.text.perl.Perl5Util;

import java.util.*;
import java.util.stream.Stream;

/**
 *  <p>Collects metrics from Classfile instances.</p>
 *  
 *  <p>This class can only approximate SLOC based on information provided
 *  by the compiler.</p>
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

    private final Collection<MetricsListener> metricsListeners = new HashSet<>();

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
        return Stream.concat(
                Stream.of(getCurrentGroup()),
                getMetricsFactory().getGroupMetrics(className).stream()
        ).toList();
    }

    public void visitClassfiles(Collection<Classfile> classfiles) {
        fireBeginSession(classfiles.size());

        super.visitClassfiles(classfiles);
        
        fireEndSession();
    }
    
    // Classfile
    public void visitClassfile(Classfile classfile) {
        String className = classfile.getClassName();

        LogManager.getLogger(getClass()).debug("visitClassfile():");
        LogManager.getLogger(getClass()).debug("    class = \"{}\"", className);
        LogManager.getLogger(getClass()).debug("    access flag: {}", () -> classfile.getAccessFlags());
        LogManager.getLogger(getClass()).debug("    public: {}", () -> classfile.isPublic());
        LogManager.getLogger(getClass()).debug("    final: {}", () -> classfile.isFinal());
        LogManager.getLogger(getClass()).debug("    super: {}", () -> classfile.isSuper());
        LogManager.getLogger(getClass()).debug("    interface: {}", () -> classfile.isInterface());
        LogManager.getLogger(getClass()).debug("    abstract: {}", () -> classfile.isAbstract());
        LogManager.getLogger(getClass()).debug("    synthetic: {}", () -> classfile.isSynthetic());
        LogManager.getLogger(getClass()).debug("    annotation: {}", () -> classfile.isAnnotation());
        LogManager.getLogger(getClass()).debug("    enum: {}", () -> classfile.isEnum());
        LogManager.getLogger(getClass()).debug("    module: {}", () -> classfile.isModule());

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

        getCurrentProject().addToMeasurement(BasicMeasurements.CLASSES, className);
        groups.forEach(group -> group.addToMeasurement(BasicMeasurements.CLASSES, className));

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

        if (classfile.isAnnotation()) {
            getCurrentProject().addToMeasurement(BasicMeasurements.ANNOTATION_CLASSES, className);
            groups.forEach(group -> group.addToMeasurement(BasicMeasurements.ANNOTATION_CLASSES, className));
        }

        if (classfile.isEnum()) {
            getCurrentProject().addToMeasurement(BasicMeasurements.ENUM_CLASSES, className);
            groups.forEach(group -> group.addToMeasurement(BasicMeasurements.ENUM_CLASSES, className));
        }

        if (classfile.isModule()) {
            getCurrentProject().addToMeasurement(BasicMeasurements.MODULE_CLASSES, className);
            groups.forEach(group -> group.addToMeasurement(BasicMeasurements.MODULE_CLASSES, className));
        }

        if (classfile.isDeprecated()) {
            getCurrentProject().addToMeasurement(BasicMeasurements.DEPRECATED_CLASSES, className);
            groups.forEach(group -> group.addToMeasurement(BasicMeasurements.DEPRECATED_CLASSES, className));
        }

        if (classfile.hasSuperclass()) {
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
        LogManager.getLogger(getClass()).debug("visitClass_info():");
        LogManager.getLogger(getClass()).debug("    name = \"{}\"", () -> entry.getName());
        if (entry.getName().startsWith("[")) {
            addClassDependencies(processDescriptor(entry.getName()));
        } else {
            addClassDependency(entry.getName());
        }
    }
    
    public void visitFieldRef_info(FieldRef_info entry) {
        LogManager.getLogger(getClass()).debug("visitFieldRef_info():");
        LogManager.getLogger(getClass()).debug("    class = \"{}\"", () -> entry.getClassName());
        LogManager.getLogger(getClass()).debug("    name = \"{}\"", () -> entry.getRawNameAndType().getName());
        LogManager.getLogger(getClass()).debug("    type = \"{}\"", () -> entry.getRawNameAndType().getType());

        // Dependencies on attributes are accounted as dependencies on their class
        entry.getRawClass().accept(this);
        addClassDependencies(processDescriptor(entry.getRawNameAndType().getType()));
    }

    public void visitMethodRef_info(MethodRef_info entry) {
        LogManager.getLogger(getClass()).debug("visitMethodRef_info():");
        LogManager.getLogger(getClass()).debug("    class = \"{}\"", () -> entry.getClassName());
        LogManager.getLogger(getClass()).debug("    name = \"{}\"", () -> entry.getRawNameAndType().getName());
        LogManager.getLogger(getClass()).debug("    type = \"{}\"", () -> entry.getRawNameAndType().getType());
        addMethodDependency(entry.getUniqueName());
        addClassDependencies(processDescriptor(entry.getRawNameAndType().getType()));
    }

    public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
        LogManager.getLogger(getClass()).debug("visitInterfaceMethodRef_info():");
        LogManager.getLogger(getClass()).debug("    class = \"{}\"", () -> entry.getClassName());
        LogManager.getLogger(getClass()).debug("    name = \"{}\"", () -> entry.getRawNameAndType().getName());
        LogManager.getLogger(getClass()).debug("    type = \"{}\"", () -> entry.getRawNameAndType().getType());
        addMethodDependency(entry.getUniqueName());
        addClassDependencies(processDescriptor(entry.getRawNameAndType().getType()));
    }

    public void visitField_info(Field_info entry) {
        String uniqueName = entry.getUniqueName();

        LogManager.getLogger(getClass()).debug("visitField_info({})", () -> entry.getFullSignature());
        LogManager.getLogger(getClass()).debug("    current class: {}", () -> getCurrentClass().getName());
        LogManager.getLogger(getClass()).debug("    access flag: {}", () -> entry.getAccessFlags());
        LogManager.getLogger(getClass()).debug("    public: {}", () -> entry.isPublic());
        LogManager.getLogger(getClass()).debug("    private: {}", () -> entry.isPrivate());
        LogManager.getLogger(getClass()).debug("    protected: {}", () -> entry.isProtected());
        LogManager.getLogger(getClass()).debug("    static: {}", () -> entry.isStatic());
        LogManager.getLogger(getClass()).debug("    final: {}", () -> entry.isFinal());
        LogManager.getLogger(getClass()).debug("    volatile: {}", () -> entry.isVolatile());
        LogManager.getLogger(getClass()).debug("    transient: {}", () -> entry.isTransient());
        LogManager.getLogger(getClass()).debug("    synthetic: {}", () -> entry.isSynthetic());
        LogManager.getLogger(getClass()).debug("    enum: {}", () -> entry.isEnum());

        getCurrentClass().addToMeasurement(BasicMeasurements.ATTRIBUTES, uniqueName);

        if (entry.isPublic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PUBLIC_ATTRIBUTES, uniqueName);
        } else if (entry.isPrivate()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PRIVATE_ATTRIBUTES, uniqueName);
        } else if (entry.isProtected()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PROTECTED_ATTRIBUTES, uniqueName);
        } else {
            getCurrentClass().addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, uniqueName);
        }

        if (entry.isFinal()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.FINAL_ATTRIBUTES, uniqueName);
        }

        if (entry.isDeprecated()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.DEPRECATED_ATTRIBUTES, uniqueName);
        }

        if (entry.isSynthetic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.SYNTHETIC_ATTRIBUTES, uniqueName);
        }

        if (entry.isStatic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.STATIC_ATTRIBUTES, uniqueName);
        }

        if (entry.isTransient()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.TRANSIENT_ATTRIBUTES, uniqueName);
        }

        if (entry.isVolatile()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.VOLATILE_ATTRIBUTES, uniqueName);
        }

        if (entry.isEnum()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.ENUM_ATTRIBUTES, uniqueName);
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

        setCurrentMethod(getMetricsFactory().createMethodMetrics(entry.getUniqueName()));
        getMetricsFactory().includeMethodMetrics(getCurrentMethod());
        
        LogManager.getLogger(getClass()).debug("visitMethod_info({})", () -> entry.getFullSignature());
        LogManager.getLogger(getClass()).debug("    current class: {}", () -> getCurrentClass().getName());
        LogManager.getLogger(getClass()).debug("    access flag: {}", () -> entry.getAccessFlags());
        LogManager.getLogger(getClass()).debug("    public: {}", () -> entry.isPublic());
        LogManager.getLogger(getClass()).debug("    private: {}", () -> entry.isPrivate());
        LogManager.getLogger(getClass()).debug("    protected: {}", () -> entry.isProtected());
        LogManager.getLogger(getClass()).debug("    static: {}", () -> entry.isStatic());
        LogManager.getLogger(getClass()).debug("    final: {}", () -> entry.isFinal());
        LogManager.getLogger(getClass()).debug("    synchronized: {}", () -> entry.isSynchronized());
        LogManager.getLogger(getClass()).debug("    bridge: {}", () -> entry.isBridge());
        LogManager.getLogger(getClass()).debug("    varars: {}", () -> entry.isVarargs());
        LogManager.getLogger(getClass()).debug("    native: {}", () -> entry.isNative());
        LogManager.getLogger(getClass()).debug("    abstract: {}", () -> entry.isAbstract());
        LogManager.getLogger(getClass()).debug("    strict: {}", () -> entry.isStrict());
        LogManager.getLogger(getClass()).debug("    synthetic: {}", () -> entry.isSynthetic());

        sloc = 0;

        getCurrentClass().addToMeasurement(BasicMeasurements.METHODS, getCurrentMethod().getName());

        if (entry.isPublic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PUBLIC_METHODS, getCurrentMethod().getName());
        } else if (entry.isPrivate()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PRIVATE_METHODS, getCurrentMethod().getName());
        } else if (entry.isProtected()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PROTECTED_METHODS, getCurrentMethod().getName());
        } else {
            getCurrentClass().addToMeasurement(BasicMeasurements.PACKAGE_METHODS, getCurrentMethod().getName());
        }

        if (entry.isFinal()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.FINAL_METHODS, getCurrentMethod().getName());
        }

        if (entry.isAbstract()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.ABSTRACT_METHODS, getCurrentMethod().getName());
            sloc = 1;
        }

        if (entry.isDeprecated()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.DEPRECATED_METHODS, getCurrentMethod().getName());
        }

        if (entry.isSynthetic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.SYNTHETIC_METHODS, getCurrentMethod().getName());
        }

        if (entry.isStatic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.STATIC_METHODS, getCurrentMethod().getName());
        }

        if (entry.isSynchronized()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.SYNCHRONIZED_METHODS, getCurrentMethod().getName());
        }

        if (entry.isBridge()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.BRIDGE_METHODS, getCurrentMethod().getName());
        }

        if (entry.isVarargs()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.VARARGS_METHODS, getCurrentMethod().getName());
        }

        if (entry.isNative()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.NATIVE_METHODS, getCurrentMethod().getName());
        }

        if (entry.isStrict()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.STRICT_METHODS, getCurrentMethod().getName());
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
        if (helper.hasCatchType()) {
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

        if (helper.hasOuterClassInfo()) {
            result = helper.getOuterClassInfo().equals(getCurrentClass().getName());
        } else {
            result = perl.match("/^" + getCurrentClass().getName() + "\\$\\d+$/", helper.getInnerClassInfo());
        }

        return result;
    }

    public void visitLineNumber(LineNumber helper) {
        sloc++;
    }

    public void visitLocalVariable(LocalVariable helper) {
        LogManager.getLogger(getClass()).debug("visitLocalVariable({})", () -> helper.getName());

        getCurrentMethod().addToMeasurement(BasicMeasurements.LOCAL_VARIABLES, helper.getName());

        addClassDependencies(processDescriptor(helper.getDescriptor()));
    }

    private int computeDepthOfInheritance(Classfile classfile) {
        int result = 1;
        
        if (classfile != null && classfile.hasSuperclass()) {
            Classfile superclass = classfile.getLoader().getClassfile(classfile.getSuperclassName());
            result += computeDepthOfInheritance(superclass);
        }

        return result;
    }
    
    private Collection<String> processDescriptor(String str) {
        Collection<String> result = new LinkedList<>();
        
        LogManager.getLogger(getClass()).debug("processDescriptor(\"{}\")", str);

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

        LogManager.getLogger(getClass()).debug("    Parses to {}", result);
        
        return result;
    }

    private void addClassDependencies(Collection<String> classnames) {
        classnames.forEach(this::addClassDependency);
    }
    
    private void addClassDependency(String name) {
        LogManager.getLogger(getClass()).debug("addClassDependency(\"{}\"):", name);

        if (!getCurrentClass().getName().equals(name) && isInFilter(name)) {
            Metrics other = getMetricsFactory().createClassMetrics(name);
                
            if (getCurrentMethod() != null && isInScope(getCurrentMethod().getName())) {
                LogManager.getLogger(getClass()).debug("    add class dependency {} --> {}", getCurrentMethod().getName(), name);
                
                if (getCurrentClass().getParent().equals(other.getParent())) {
                    LogManager.getLogger(getClass()).debug("    Intra-Package!");
                    getCurrentMethod().addToMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES, other.getName());
                    other.addToMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES, getCurrentMethod().getName());
                } else {
                    LogManager.getLogger(getClass()).debug("    Extra-Package!");
                    getCurrentMethod().addToMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES, other.getName());
                    other.addToMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES, getCurrentMethod().getName());
                }
            } else if (isInScope(getCurrentClass().getName())) {
                LogManager.getLogger(getClass()).debug("    add class dependency {} --> {}", getCurrentClass().getName(), name);
                
                if (getCurrentClass().getParent().equals(other.getParent())) {
                    LogManager.getLogger(getClass()).debug("    Intra-Package!");
                    getCurrentClass().addToMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES, other.getName());
                    other.addToMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES, getCurrentClass().getName());
                } else {
                    LogManager.getLogger(getClass()).debug("    Extra-Package!");
                    getCurrentClass().addToMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES, other.getName());
                    other.addToMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES, getCurrentClass().getName());
                }
            } else {
                LogManager.getLogger(getClass()).debug("    skipping class dependency {} --> {}", getCurrentClass().getName(), name);
            }
        } else {
            LogManager.getLogger(getClass()).debug("    skipping class dependency {} --> {}", getCurrentClass().getName(), name);
        }
    }
    
    private void addMethodDependency(String name) {
        LogManager.getLogger(getClass()).debug("addMethodDependency(\"{}\"):", name);

        if (!getCurrentMethod().getName().equals(name) && isInScope(getCurrentMethod().getName()) && isInFilter(name)) {
            LogManager.getLogger(getClass()).debug("    add method dependency {} --> {}", getCurrentMethod().getName(), name);
            Metrics other = getMetricsFactory().createMethodMetrics(name);
            
            if (getCurrentClass().equals(other.getParent())) {
                LogManager.getLogger(getClass()).debug("    Intra-Class!");
                getCurrentMethod().addToMeasurement(BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES, other.getName());
                other.addToMeasurement(BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES, getCurrentMethod().getName());
            } else if (getCurrentGroup().equals(other.getParent().getParent())) {
                LogManager.getLogger(getClass()).debug("    Intra-Package!");
                getCurrentMethod().addToMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES, other.getName());
                other.addToMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES, getCurrentMethod().getName());
            } else {
                LogManager.getLogger(getClass()).debug("    Extra-Package!");
                getCurrentMethod().addToMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES, other.getName());
                other.addToMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES, getCurrentMethod().getName());
            }
        } else {
            LogManager.getLogger(getClass()).debug("    skipping method dependency {} --> {}", getCurrentMethod().getName(), name);
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
        metricsListeners.add(listener);
    }

    public void removeMetricsListener(MetricsListener listener) {
        metricsListeners.remove(listener);
    }

    protected void fireBeginSession(int size) {
        MetricsEvent event = new MetricsEvent(this, size);
        metricsListeners.forEach(listener -> listener.beginSession(event));
    }

    protected void fireBeginClass(Classfile classfile) {
        MetricsEvent event = new MetricsEvent(this, classfile);
        metricsListeners.forEach(listener -> listener.beginClass(event));
    }

    protected void fireBeginMethod(Method_info method) {
        MetricsEvent event = new MetricsEvent(this, method);
        metricsListeners.forEach(listener -> listener.beginMethod(event));
    }

    protected void fireEndMethod(Method_info method, Metrics metrics) {
        MetricsEvent event = new MetricsEvent(this, method, metrics);
        metricsListeners.forEach(listener -> listener.endMethod(event));
    }

    protected void fireEndClass(Classfile classfile, Metrics metrics) {
        MetricsEvent event = new MetricsEvent(this, classfile, metrics);
        metricsListeners.forEach(listener -> listener.endClass(event));
    }

    protected void fireEndSession() {
        MetricsEvent event = new MetricsEvent(this);
        metricsListeners.forEach(listener -> listener.endSession(event));
    }
}
