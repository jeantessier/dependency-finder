/*
 *  Copyright (c) 2001-2008, Jean Tessier
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

import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

/**
 *  Collects metrics from Classfile instances.
 *  
 *  This class can only approximate SLOC based on information provided
 *  by the compiler.
 */
public class MetricsGatherer extends VisitorBase {
    private String projectName;
    private MetricsFactory factory;

    private Collection<String> scope = null;
    private Collection<String> filter = null;
    
    private Metrics currentProject;
    private Metrics currentGroup;
    private Metrics currentClass;
    private Metrics currentMethod;

    private int sloc;
    private boolean isSynthetic;
    
    private HashSet<MetricsListener> metricsListeners = new HashSet<MetricsListener>();

    public MetricsGatherer(String projectName, MetricsFactory factory) {
        this.projectName = projectName;
        this.factory = factory;

        setCurrentProject(getMetricsFactory().createProjectMetrics(getProjectName()));
    }

    public String getProjectName() {
        return projectName;
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

    private void setCurrentProject(Metrics currentProject) {
        this.currentProject = currentProject;
    }

    private Metrics getCurrentGroup() {
        return currentGroup;
    }

    private void setCurrentGroup(Metrics currentGroup) {
        this.currentGroup = currentGroup;
    }

    private Metrics getCurrentClass() {
        return currentClass;
    }

    private void setCurrentClass(Metrics currentClass) {
        this.currentClass = currentClass;
    }

    private Metrics getCurrentMethod() {
        return currentMethod;
    }

    private void setCurrentMethod(Metrics currentMethod) {
        this.currentMethod = currentMethod;
    }

    public void visitClassfiles(Collection<Classfile> classfiles) {
        fireBeginSession(classfiles.size());

        for (Classfile classfile : classfiles) {
            classfile.accept(this);
        }
        
        fireEndSession();
    }
    
    // Classfile
    public void visitClassfile(Classfile classfile) {
        Logger.getLogger(getClass()).debug("VisitClassfile():");
        Logger.getLogger(getClass()).debug("    class = \"" + classfile.getClassName() + "\"");

        fireBeginClass(classfile);
        
        setCurrentMethod(null);
        setCurrentClass(getMetricsFactory().createClassMetrics(classfile.getClassName()));
        setCurrentGroup(getCurrentClass().getParent());
        setCurrentProject(getCurrentGroup().getParent());

        getMetricsFactory().includeClassMetrics(getCurrentClass());

        getCurrentProject().addToMeasurement(BasicMeasurements.PACKAGES, getCurrentGroup().getName());

        if (classfile.isPublic()) {
            getCurrentProject().addToMeasurement(BasicMeasurements.PUBLIC_CLASSES);
            getCurrentGroup().addToMeasurement(BasicMeasurements.PUBLIC_CLASSES);
        }

        if (classfile.isPublic()) {
            getCurrentProject().addToMeasurement(BasicMeasurements.FINAL_CLASSES);
            getCurrentGroup().addToMeasurement(BasicMeasurements.FINAL_CLASSES);
        }

        if (classfile.isInterface()) {
            getCurrentProject().addToMeasurement(BasicMeasurements.INTERFACES);
            getCurrentGroup().addToMeasurement(BasicMeasurements.INTERFACES);
        }

        if (classfile.isAbstract()) {
            getCurrentProject().addToMeasurement(BasicMeasurements.ABSTRACT_CLASSES);
            getCurrentGroup().addToMeasurement(BasicMeasurements.ABSTRACT_CLASSES);
        }

        if (classfile.getSuperclassIndex() != 0) {
            classfile.getRawSuperclass().accept(this);

            getMetricsFactory().createClassMetrics(classfile.getSuperclassName()).addToMeasurement(BasicMeasurements.SUBCLASSES);

            Classfile superclass = classfile.getLoader().getClassfile(classfile.getSuperclassName());
            if (superclass != null) {
                getCurrentClass().addToMeasurement(BasicMeasurements.DEPTH_OF_INHERITANCE, computeDepthOfInheritance(superclass));
            }
        }

        for (Class_info class_info : classfile.getAllInterfaces()) {
            class_info.accept(this);
        }

        for (Field_info field : classfile.getAllFields()) {
            field.accept(this);
        }

        for (Method_info method : classfile.getAllMethods()) {
            method.accept(this);
        }

        sloc = 1;
        isSynthetic = false;

        for (Attribute_info attribute : classfile.getAttributes()) {
            attribute.accept(this);
        }
        
        if (!isSynthetic) {
            getCurrentClass().addToMeasurement(BasicMeasurements.CLASS_SLOC, sloc);
        }

        fireEndClass(classfile, getCurrentClass());
    }
    
    // ConstantPool entries
    public void visitClass_info(Class_info entry) {
        Logger.getLogger(getClass()).debug("VisitClass_info():");
        Logger.getLogger(getClass()).debug("    name = \"" + entry.getName() + "\"");
        if (entry.getName().startsWith("[")) {
            addClassDependencies(processDescriptor(entry.getName()));
        } else {
            addClassDependency(entry.getName());
        }
    }
    
    public void visitFieldRef_info(FieldRef_info entry) {
        Logger.getLogger(getClass()).debug("VisitFieldRef_info():");
        Logger.getLogger(getClass()).debug("    class = \"" + entry.getClassName() + "\"");
        Logger.getLogger(getClass()).debug("    name = \"" + entry.getRawNameAndType().getName() + "\"");
        Logger.getLogger(getClass()).debug("    type = \"" + entry.getRawNameAndType().getType() + "\"");

        // Dependencies on attributes are accounted as dependencies on their class
        entry.getRawClass().accept(this);
        addClassDependencies(processDescriptor(entry.getRawNameAndType().getType()));
    }

    public void visitMethodRef_info(MethodRef_info entry) {
        Logger.getLogger(getClass()).debug("VisitMethodRef_info():");
        Logger.getLogger(getClass()).debug("    class = \"" + entry.getClassName() + "\"");
        Logger.getLogger(getClass()).debug("    name = \"" + entry.getRawNameAndType().getName() + "\"");
        Logger.getLogger(getClass()).debug("    type = \"" + entry.getRawNameAndType().getType() + "\"");
        addMethodDependency(entry.getFullSignature());
        addClassDependencies(processDescriptor(entry.getRawNameAndType().getType()));
    }

    public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
        Logger.getLogger(getClass()).debug("VisitInterfaceMethodRef_info():");
        Logger.getLogger(getClass()).debug("    class = \"" + entry.getClassName() + "\"");
        Logger.getLogger(getClass()).debug("    name = \"" + entry.getRawNameAndType().getName() + "\"");
        Logger.getLogger(getClass()).debug("    type = \"" + entry.getRawNameAndType().getType() + "\"");
        addMethodDependency(entry.getFullSignature());
        addClassDependencies(processDescriptor(entry.getRawNameAndType().getType()));
    }

    public void visitField_info(Field_info entry) {
        getCurrentClass().addToMeasurement(BasicMeasurements.ATTRIBUTES);

        Logger.getLogger(getClass()).debug("VisitField_info(" + entry.getFullSignature() + ")");
        Logger.getLogger(getClass()).debug("Current class: " + getCurrentClass().getName());
        Logger.getLogger(getClass()).debug("Access flag: " + entry.getAccessFlag());
        Logger.getLogger(getClass()).debug("Public: " + entry.isPublic());
        Logger.getLogger(getClass()).debug("Private: " + entry.isPrivate());
        Logger.getLogger(getClass()).debug("Protected: " + entry.isProtected());
        Logger.getLogger(getClass()).debug("Static: " + entry.isStatic());
        
        if (entry.isPublic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PUBLIC_ATTRIBUTES);
        } else if (entry.isPrivate()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PRIVATE_ATTRIBUTES);
        } else if (entry.isProtected()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PROTECTED_ATTRIBUTES);
        } else {
            getCurrentClass().addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES);
        }

        if (entry.isStatic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.STATIC_ATTRIBUTES);
        }

        if (entry.isFinal()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.FINAL_ATTRIBUTES);
        }

        if (entry.isVolatile()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.VOLATILE_ATTRIBUTES);
        }

        if (entry.isTransient()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.TRANSIENT_ATTRIBUTES);
        }

        sloc = 1;
        isSynthetic = entry.isSynthetic();
        
        super.visitField_info(entry);
        
        if (!isSynthetic) {
            getCurrentClass().addToMeasurement(BasicMeasurements.CLASS_SLOC, sloc);
        }

        addClassDependencies(processDescriptor(entry.getDescriptor()));
    }

    public void visitMethod_info(Method_info entry) {
        fireBeginMethod(entry);
        
        setCurrentMethod(getMetricsFactory().createMethodMetrics(entry.getFullSignature()));
        getMetricsFactory().includeMethodMetrics(getCurrentMethod());
        
        Logger.getLogger(getClass()).debug("VisitMethod_info(" + entry.getFullSignature() + ")");
        Logger.getLogger(getClass()).debug("Current class: " + getCurrentClass().getName());
        Logger.getLogger(getClass()).debug("Access flag: " + entry.getAccessFlag());
        Logger.getLogger(getClass()).debug("Public: " + entry.isPublic());
        Logger.getLogger(getClass()).debug("Private: " + entry.isPrivate());
        Logger.getLogger(getClass()).debug("Protected: " + entry.isProtected());
        Logger.getLogger(getClass()).debug("Static: " + entry.isStatic());

        sloc = 0;
        isSynthetic = entry.isSynthetic();
        
        if (entry.isPublic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PUBLIC_METHODS);
        } else if (entry.isPrivate()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PRIVATE_METHODS);
        } else if (entry.isProtected()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.PROTECTED_METHODS);
        } else {
            getCurrentClass().addToMeasurement(BasicMeasurements.PACKAGE_METHODS);
        }

        if (entry.isStatic()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.STATIC_METHODS);
        }

        if (entry.isFinal()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.FINAL_METHODS);
        }

        if (entry.isSynchronized()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.SYNCHRONIZED_METHODS);
        }

        if (entry.isNative()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.NATIVE_METHODS);
        }

        if (entry.isAbstract()) {
            getCurrentClass().addToMeasurement(BasicMeasurements.ABSTRACT_METHODS);
            sloc = 1;
        }

        getCurrentMethod().addToMeasurement(BasicMeasurements.PARAMETERS, DescriptorHelper.getParameterCount(entry.getDescriptor()));
        
        super.visitMethod_info(entry);
        
        if (!isSynthetic) {
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
        
        if (owner instanceof Classfile) {
            getCurrentProject().addToMeasurement(BasicMeasurements.SYNTHETIC_CLASSES);
            getCurrentGroup().addToMeasurement(BasicMeasurements.SYNTHETIC_CLASSES);
        } else if (owner instanceof Field_info) {
            getCurrentClass().addToMeasurement(BasicMeasurements.SYNTHETIC_ATTRIBUTES);
        } else if (owner instanceof Method_info) {
            getCurrentClass().addToMeasurement(BasicMeasurements.SYNTHETIC_METHODS);
        } else {
            Logger.getLogger(getClass()).warn("Synthetic attribute on unknown Visitable: " + owner.getClass().getName());
        }
    }

    public void visitDeprecated_attribute(Deprecated_attribute attribute) {
        Object owner = attribute.getOwner();
    
        if (owner instanceof Classfile) {
            getCurrentProject().addToMeasurement(BasicMeasurements.DEPRECATED_CLASSES);
            getCurrentGroup().addToMeasurement(BasicMeasurements.DEPRECATED_CLASSES);
        } else if (owner instanceof Field_info) {
            getCurrentClass().addToMeasurement(BasicMeasurements.DEPRECATED_ATTRIBUTES);
        } else if (owner instanceof Method_info) {
            getCurrentClass().addToMeasurement(BasicMeasurements.DEPRECATED_METHODS);
        } else {
            Logger.getLogger(getClass()).warn("Deprecated attribute on unknown Visitable: " + owner.getClass().getName());
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
        if ((helper.getInnerClassInfoIndex() != helper.getInnerClasses().getClassfile().getClassIndex()) && (helper.getInnerClassInfo().startsWith(helper.getInnerClasses().getClassfile().getClassName()))) {
            getCurrentProject().addToMeasurement(BasicMeasurements.INNER_CLASSES);
            getCurrentGroup().addToMeasurement(BasicMeasurements.INNER_CLASSES);
            getCurrentClass().addToMeasurement(BasicMeasurements.INNER_CLASSES);
        
            if (helper.isPublic()) {
                getCurrentProject().addToMeasurement(BasicMeasurements.PUBLIC_INNER_CLASSES);
                getCurrentGroup().addToMeasurement(BasicMeasurements.PUBLIC_INNER_CLASSES);
                getCurrentClass().addToMeasurement(BasicMeasurements.PUBLIC_INNER_CLASSES);
            } else if (helper.isPrivate()) {
                getCurrentProject().addToMeasurement(BasicMeasurements.PRIVATE_INNER_CLASSES);
                getCurrentGroup().addToMeasurement(BasicMeasurements.PRIVATE_INNER_CLASSES);
                getCurrentClass().addToMeasurement(BasicMeasurements.PRIVATE_INNER_CLASSES);
            } else if (helper.isProtected()) {
                getCurrentProject().addToMeasurement(BasicMeasurements.PROTECTED_INNER_CLASSES);
                getCurrentGroup().addToMeasurement(BasicMeasurements.PROTECTED_INNER_CLASSES);
                getCurrentClass().addToMeasurement(BasicMeasurements.PROTECTED_INNER_CLASSES);
            } else {
                getCurrentProject().addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES);
                getCurrentGroup().addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES);
                getCurrentClass().addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES);
            }

            if (helper.isStatic()) {
                getCurrentProject().addToMeasurement(BasicMeasurements.STATIC_INNER_CLASSES);
                getCurrentGroup().addToMeasurement(BasicMeasurements.STATIC_INNER_CLASSES);
                getCurrentClass().addToMeasurement(BasicMeasurements.STATIC_INNER_CLASSES);
            }

            if (helper.isFinal()) {
                getCurrentProject().addToMeasurement(BasicMeasurements.FINAL_INNER_CLASSES);
                getCurrentGroup().addToMeasurement(BasicMeasurements.FINAL_INNER_CLASSES);
                getCurrentClass().addToMeasurement(BasicMeasurements.FINAL_INNER_CLASSES);
            }

            if (helper.isAbstract()) {
                getCurrentProject().addToMeasurement(BasicMeasurements.ABSTRACT_INNER_CLASSES);
                getCurrentGroup().addToMeasurement(BasicMeasurements.ABSTRACT_INNER_CLASSES);
                getCurrentClass().addToMeasurement(BasicMeasurements.ABSTRACT_INNER_CLASSES);
            }
        }
    }

    public void visitLineNumber(LineNumber helper) {
        sloc++;
    }

    public void visitLocalVariable(LocalVariable helper) {
        getCurrentMethod().addToMeasurement(BasicMeasurements.LOCAL_VARIABLES);

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
        Collection<String> result = new LinkedList<String>();
        
        Logger.getLogger(getClass()).debug("ProcessDescriptor: " + str);

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

        Logger.getLogger(getClass()).debug("ProcessDescriptor: " + result);
        
        return result;
    }

    private void addClassDependencies(Collection<String> classnames) {
        for (String classname : classnames) {
            addClassDependency(classname);
        }
    }
    
    private void addClassDependency(String name) {
        Logger.getLogger(getClass()).debug("AddClassDependency(\"" + name + "\") ...");

        if (!getCurrentClass().getName().equals(name) && isInFilter(name)) {
            Metrics other = getMetricsFactory().createClassMetrics(name);
                
            if (getCurrentMethod() != null && isInScope(getCurrentMethod().getName())) {
                Logger.getLogger(getClass()).debug("AddClassDependency " + getCurrentMethod().getName() + " -> " + name + " ...");
                
                if (getCurrentClass().getParent().equals(other.getParent())) {
                    Logger.getLogger(getClass()).debug("Intra-Package ...");
                    getCurrentMethod().addToMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES, other.getName());
                    other.addToMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES, getCurrentMethod().getName());
                } else {
                    Logger.getLogger(getClass()).debug("Extra-Package ...");
                    getCurrentMethod().addToMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES, other.getName());
                    other.addToMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES, getCurrentMethod().getName());
                }
            } else if (isInScope(getCurrentClass().getName())) {
                Logger.getLogger(getClass()).debug("AddClassDependency " + getCurrentClass().getName() + " -> " + name + " ...");
                
                if (getCurrentClass().getParent().equals(other.getParent())) {
                    Logger.getLogger(getClass()).debug("Intra-Package ...");
                    getCurrentClass().addToMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES, other.getName());
                    other.addToMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES, getCurrentClass().getName());
                } else {
                    Logger.getLogger(getClass()).debug("Extra-Package ...");
                    getCurrentClass().addToMeasurement(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES, other.getName());
                    other.addToMeasurement(BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES, getCurrentClass().getName());
                }
            }
        }
    }
    
    private void addMethodDependency(String name) {
        Logger.getLogger(getClass()).debug("AddMethodDependency " + getCurrentMethod().getName() + " -> " + name + " ...");

        if (!getCurrentMethod().getName().equals(name) && isInScope(getCurrentMethod().getName()) && isInFilter(name)) {
            Metrics other = getMetricsFactory().createMethodMetrics(name);
            
            if (getCurrentClass().equals(other.getParent())) {
                Logger.getLogger(getClass()).debug("Intra-Class ...");
                getCurrentMethod().addToMeasurement(BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES, other.getName());
                other.addToMeasurement(BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES, getCurrentMethod().getName());
            } else if (getCurrentGroup().equals(other.getParent().getParent())) {
                Logger.getLogger(getClass()).debug("Intra-Package ...");
                getCurrentMethod().addToMeasurement(BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES, other.getName());
                other.addToMeasurement(BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES, getCurrentMethod().getName());
            } else {
                Logger.getLogger(getClass()).debug("Extra-Package ...");
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
        for (MetricsListener listener : cloneListeners()) {
            listener.beginSession(event);
        }
    }

    protected void fireBeginClass(Classfile classfile) {
        MetricsEvent event = new MetricsEvent(this, classfile);
        for (MetricsListener listener : cloneListeners()) {
            listener.beginClass(event);
        }
    }

    protected void fireBeginMethod(Method_info method) {
        MetricsEvent event = new MetricsEvent(this, method);
        for (MetricsListener listener : cloneListeners()) {
            listener.beginMethod(event);
        }
    }

    protected void fireEndMethod(Method_info method, Metrics metrics) {
        MetricsEvent event = new MetricsEvent(this, method, metrics);
        for (MetricsListener listener : cloneListeners()) {
            listener.endMethod(event);
        }
    }

    protected void fireEndClass(Classfile classfile, Metrics metrics) {
        MetricsEvent event = new MetricsEvent(this, classfile, metrics);
        for (MetricsListener listener : cloneListeners()) {
            listener.endClass(event);
        }
    }

    protected void fireEndSession() {
        MetricsEvent event = new MetricsEvent(this);
        for (MetricsListener listener : cloneListeners()) {
            listener.endSession(event);
        }
    }

    private Collection<MetricsListener> cloneListeners() {
        Collection<MetricsListener> result;
        synchronized(metricsListeners) {
            result = (Collection<MetricsListener>) metricsListeners.clone();
        }
        return result;
    }
}
