/*
 *  Copyright (c) 2001-2006, Jean Tessier
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

package com.jeantessier.dependency;

import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

/**
 *  Traverses a Classfile and extracts dependencies from its code.
 *  Does not see dependencies on static final simple constants
 *  (basic type or String) and does not look at local variables.
 */
public class CodeDependencyCollector extends CollectorBase {
    private NodeFactory factory;
    private Node current;
    private HashSet<DependencyListener> dependencyListeners = new HashSet<DependencyListener>();

    public CodeDependencyCollector() {
        this(new NodeFactory());
    }

    public CodeDependencyCollector(NodeFactory factory) {
        this.factory = factory;
    }

    public NodeFactory getFactory() {
        return factory;
    }

    public Collection getCollection() {
        return getFactory().getPackages().values();
    }

    public void visitClassfile(Classfile classfile) {
        current = getFactory().createClass(classfile.getClassName(), true);

        fireBeginClass(classfile.toString());
        
        if (classfile.getSuperclassIndex() != 0) {
            classfile.getRawSuperclass().accept(this);
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

        fireEndClass(classfile.toString());
    }

    public void visitClass_info(Class_info entry) {
        if (Logger.getLogger(getClass()).isDebugEnabled()) {
            Logger.getLogger(getClass()).debug("VisitClass_info():");
            Logger.getLogger(getClass()).debug("    name = \"" + entry.getName() + "\"");
        }

        if (entry.getName().startsWith("[")) {
            processDescriptor(entry.getName());
        } else {
            Node other = getFactory().createClass(entry.getName());
            current.addDependency(other);
            if (Logger.getLogger(getClass()).isDebugEnabled()) {
                Logger.getLogger(getClass()).info("Class_info dependency: " + current + " --> " + other);
            }
            fireDependency(current, other);
        }
    }

    public void visitFieldRef_info(FieldRef_info entry) {
        if (Logger.getLogger(getClass()).isDebugEnabled()) {
            Logger.getLogger(getClass()).debug("VisitFieldRef_info():");
            Logger.getLogger(getClass()).debug("    class = \"" + entry.getClassName() + "\"");
            Logger.getLogger(getClass()).debug("    name = \"" + entry.getRawNameAndType().getName() + "\"");
            Logger.getLogger(getClass()).debug("    type = \"" + entry.getRawNameAndType().getType() + "\"");
        }

        Node other = getFactory().createFeature(entry.getFullSignature());
        current.addDependency(other);
        if (Logger.getLogger(getClass()).isDebugEnabled()) {
            Logger.getLogger(getClass()).info("FieldRef_info dependency: " + current + " --> " + other);
        }
        fireDependency(current, other);

        processDescriptor(entry.getRawNameAndType().getType());
    }

    public void visitMethodRef_info(MethodRef_info entry) {
        if (Logger.getLogger(getClass()).isDebugEnabled()) {
            Logger.getLogger(getClass()).debug("VisitMethodRef_info():");
            Logger.getLogger(getClass()).debug("    class = \"" + entry.getClassName() + "\"");
            Logger.getLogger(getClass()).debug("    name = \"" + entry.getRawNameAndType().getName() + "\"");
            Logger.getLogger(getClass()).debug("    type = \"" + entry.getRawNameAndType().getType() + "\"");
        }

        if (!entry.isStaticInitializer()) {
            Node other  = getFactory().createFeature(entry.getFullSignature());
            current.addDependency(other);
            if (Logger.getLogger(getClass()).isDebugEnabled()) {
                Logger.getLogger(getClass()).info("MethodRef_info dependency: " + current + " --> " + other);
            }
            fireDependency(current, other);

            processDescriptor(entry.getRawNameAndType().getType());
        }
    }

    public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
        if (Logger.getLogger(getClass()).isDebugEnabled()) {
            Logger.getLogger(getClass()).debug("VisitInterfaceMethodRef_info():");
            Logger.getLogger(getClass()).debug("    class = \"" + entry.getClassName() + "\"");
            Logger.getLogger(getClass()).debug("    name = \"" + entry.getRawNameAndType().getName() + "\"");
            Logger.getLogger(getClass()).debug("    type = \"" + entry.getRawNameAndType().getType() + "\"");
        }

        Node other  = getFactory().createFeature(entry.getFullSignature());
        current.addDependency(other);
        if (Logger.getLogger(getClass()).isDebugEnabled()) {
            Logger.getLogger(getClass()).info("InterfaceMethodRef_info dependency: " + current + " --> " + other);
        }
        fireDependency(current, other);

        processDescriptor(entry.getRawNameAndType().getType());
    }

    public void visitField_info(Field_info entry) {
        if (Logger.getLogger(getClass()).isDebugEnabled()) {
            Logger.getLogger(getClass()).debug("VisitField_info():");
            Logger.getLogger(getClass()).debug("    name = \"" + entry.getName() + "\"");
            Logger.getLogger(getClass()).debug("    descriptor = \"" + entry.getDescriptor() + "\"");
        }

        current = getFactory().createFeature(entry.getFullSignature(), true);

        processDescriptor(entry.getDescriptor());
    
        super.visitField_info(entry);
    }

    public void visitMethod_info(Method_info entry) {
        if (Logger.getLogger(getClass()).isDebugEnabled()) {
            Logger.getLogger(getClass()).debug("VisitMethod_info():");
            Logger.getLogger(getClass()).debug("    name = \"" + entry.getName() + "\"");
            Logger.getLogger(getClass()).debug("    descriptor = \"" + entry.getDescriptor() + "\"");
        }

        current = getFactory().createFeature(entry.getFullSignature(), true);

        processDescriptor(entry.getDescriptor());

        super.visitMethod_info(entry);
    }

    public void visitInstruction(Instruction helper) {
        Logger.getLogger(getClass()).debug("VisitInstruction() ...");

        /*
         *  We can skip the "new" (0xbb) instruction as it is always
         *  followed by a call to the constructor method.
         */
        
        switch (helper.getOpcode()) {
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

        super.visitInstruction(helper);
    }

    public void visitExceptionHandler(ExceptionHandler helper) {
        if (Logger.getLogger(getClass()).isDebugEnabled()) {
            Logger.getLogger(getClass()).debug(getClass().getName() + "VisitExceptionHandler(): " + helper);
        }

        if (helper.getCatchTypeIndex() != 0) {
            helper.getRawCatchType().accept(this);
        }
    }

    private void processDescriptor(String str) {
        int currentPos = 0;
        int startPos;
        int endPos;

        while ((startPos = str.indexOf('L', currentPos)) != -1) {
            if ((endPos = str.indexOf(';', startPos)) != -1) {
                String classname = SignatureHelper.path2ClassName(str.substring(startPos + 1, endPos));
                if (Logger.getLogger(getClass()).isDebugEnabled()) {
                    Logger.getLogger(getClass()).debug("    Adding \"" + classname + "\"");
                }
                Node other = getFactory().createClass(classname);
                current.addDependency(other);
                if (Logger.getLogger(getClass()).isDebugEnabled()) {
                    Logger.getLogger(getClass()).info("descriptor dependency: " + current + " --> " + other);
                }
                fireDependency(current, other);
                currentPos = endPos + 1;
            } else {
                currentPos = startPos + 1;
            }
        }
    }

    public void addDependencyListener(DependencyListener listener) {
        synchronized(dependencyListeners) {
            dependencyListeners.add(listener);
        }
    }

    public void removeDependencyListener(DependencyListener listener) {
        synchronized(dependencyListeners) {
            dependencyListeners.remove(listener);
        }
    }

    protected void fireBeginSession() {
        DependencyEvent event = new DependencyEvent(this);

        HashSet<DependencyListener> listeners;
        synchronized(dependencyListeners) {
            listeners = (HashSet<DependencyListener>) dependencyListeners.clone();
        }

        for (DependencyListener listener : listeners) {
            listener.beginSession(event);
        }
    }
    
    protected void fireBeginClass(String classname) {
        DependencyEvent event = new DependencyEvent(this, classname);

        HashSet<DependencyListener> listeners;
        synchronized(dependencyListeners) {
            listeners = (HashSet<DependencyListener>) dependencyListeners.clone();
        }

        for (DependencyListener listener : listeners) {
            listener.beginClass(event);
        }
    }

    protected void fireDependency(Node dependent, Node dependable) {
        DependencyEvent event = new DependencyEvent(this, dependent, dependable);

        HashSet<DependencyListener> listeners;
        synchronized(dependencyListeners) {
            listeners = (HashSet<DependencyListener>) dependencyListeners.clone();
        }

        for (DependencyListener listener : listeners) {
            listener.dependency(event);
        }
    }

    protected void fireEndClass(String classname) {
        DependencyEvent event = new DependencyEvent(this, classname);

        HashSet<DependencyListener> listeners;
        synchronized(dependencyListeners) {
            listeners = (HashSet<DependencyListener>) dependencyListeners.clone();
        }

        for (DependencyListener listener : listeners) {
            listener.endClass(event);
        }
    }

    protected void fireEndSession() {
        DependencyEvent event = new DependencyEvent(this);

        HashSet<DependencyListener> listeners;
        synchronized(dependencyListeners) {
            listeners = (HashSet<DependencyListener>) dependencyListeners.clone();
        }

        for (DependencyListener listener : listeners) {
            listener.endSession(event);
        }
    }
}
