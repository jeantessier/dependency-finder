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

package com.jeantessier.classreader.impl;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

public class Classfile implements com.jeantessier.classreader.Classfile {
    private static final int ACC_PUBLIC = 0x0001;
    private static final int ACC_FINAL = 0x0010;
    private static final int ACC_SUPER = 0x0020;
    private static final int ACC_INTERFACE = 0x0200;
    private static final int ACC_ABSTRACT = 0x0400;
    private static final int ACC_SYNTHETIC = 0x1000;
    private static final int ACC_ANNOTATION = 0x2000;
    private static final int ACC_ENUM = 0x4000;

    private ClassfileLoader loader;

    private int magicNumber;
    private int minorVersion;
    private int majorVersion;
    private ConstantPool constantPool;
    private int accessFlag;
    private int classIndex;
    private int superclassIndex;
    private Map<String, Class_info> interfaces = new TreeMap<String, Class_info>();
    private Map<String, Field_info> fields = new TreeMap<String, Field_info>();
    private Map<String, Method_info> methods = new TreeMap<String, Method_info>();
    private Collection<Attribute_info> attributes = new LinkedList<Attribute_info>();

    /**
     *  Parses the input stream and extracts the class description.
     *  You should only call this constructor from a ClassfileLoader.
     */
    public Classfile(ClassfileLoader loader, DataInputStream in) throws IOException {
        this.loader = loader;

        magicNumber = in.readInt();
        Logger.getLogger(getClass()).debug("magic number = 0x" + Integer.toHexString(magicNumber).toUpperCase());

        if (magicNumber != 0xCAFEBABE) {
            throw new IOException("Bad magic number");
        }
        
        // Reading the file format's version number
        minorVersion = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("minor version = " + minorVersion);
        majorVersion = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("major version = " + majorVersion);

        // Reading the constant pool
        Logger.getLogger(getClass()).debug("Reading the constant pool ...");
        constantPool = new ConstantPool(this, in);
        Logger.getLogger(getClass()).debug(constantPool);

        // Skipping the access flag
        accessFlag = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("accessFlag = " + accessFlag);

        // Retrieving this class's name
        classIndex = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("thisClass = " + classIndex + " (" + getClassName() + ")");

        // Retrieving this class's superclass
        superclassIndex = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("superclass = " + superclassIndex + " (" + getSuperclassName() + ")");

        // Retrieving the inferfaces
        int interfaceCount = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + interfaceCount + " interface(s)");
        for (int i=0; i<interfaceCount; i++) {
            Class_info interfaceInfo = (Class_info) constantPool.get(in.readUnsignedShort());
            Logger.getLogger(getClass()).debug("    " + interfaceInfo.getName());
            interfaces.put(interfaceInfo.getName(), interfaceInfo);
        }

        // Retrieving the fields
        int fieldCount = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + fieldCount + " field(s)");
        for (int i=0; i<fieldCount; i++) {
            Logger.getLogger(getClass()).debug("Field " + i + ":");
            Field_info fieldInfo = new Field_info(this, in);
            fields.put(fieldInfo.getName(), fieldInfo);
        }

        // Retrieving the methods
        int methodCount = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + methodCount + " method(s)");
        for (int i=0; i<methodCount; i++) {
            Logger.getLogger(getClass()).debug("Method " + i + ":");
            Method_info methodInfo = new Method_info(this, in);
            methods.put(methodInfo.getSignature(), methodInfo);
        }

        // Retrieving the attributes
        int attributeCount = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + attributeCount + " class attribute(s)");
        for (int i=0; i<attributeCount; i++) {
            Logger.getLogger(getClass()).debug("Attribute " + i + ":");
            attributes.add(AttributeFactory.create(this, this, in));
        }
    }

    public ClassfileLoader getLoader() {
        return loader;
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }

    public int getAccessFlag() {
        return accessFlag;
    }

    public int getClassIndex() {
        return classIndex;
    }

    public Class_info getRawClass() {
        return (Class_info) getConstantPool().get(getClassIndex());
    }

    public String getClassName() {
        return getRawClass().toString();
    }

    public String getSimpleName() {
        return getClassName().substring(getClassName().lastIndexOf(".") + 1);
    }

    public int getSuperclassIndex() {
        return superclassIndex;
    }

    public Class_info getRawSuperclass() {
        return (Class_info) getConstantPool().get(getSuperclassIndex());
    }

    public String getSuperclassName() {
        String result = "";

        if (getSuperclassIndex() != 0) {
            result = getRawSuperclass().toString();
        }
        
        return result;
    }

    public Class_info getInterface(String name) {
        return interfaces.get(name);
    }

    public Collection<Class_info> getAllInterfaces() {
        return interfaces.values();
    }

    public Collection<Field_info> getAllFields() {
        return fields.values();
    }

    public Field_info getField(String name) {
        return fields.get(name);
    }

    public com.jeantessier.classreader.Field_info locateField(String name) {
        com.jeantessier.classreader.Field_info result = getField(name);

        if (result == null) {
            com.jeantessier.classreader.Classfile classfile = getLoader().getClassfile(getSuperclassName());
            if (classfile != null) {
                com.jeantessier.classreader.Field_info attempt = classfile.locateField(name);
                if (attempt != null && (attempt.isPublic() || attempt.isProtected())) {
                    result = attempt;
                }
            }
        }

        Iterator i = getAllInterfaces().iterator();
        while (result == null && i.hasNext()) {
            com.jeantessier.classreader.Classfile classfile = getLoader().getClassfile(i.next().toString());
            if (classfile != null) {
                com.jeantessier.classreader.Field_info attempt = classfile.locateField(name);
                if (attempt != null && (attempt.isPublic() || attempt.isProtected())) {
                    result = attempt;
                }
            }
        }

        return result;
    }

    public Collection<Method_info> getAllMethods() {
        return methods.values();
    }

    public Method_info getMethod(String signature) {
        return methods.get(signature);
    }

    public com.jeantessier.classreader.Method_info locateMethod(String signature) {
        com.jeantessier.classreader.Method_info result = getMethod(signature);

        if (result == null) {
            com.jeantessier.classreader.Classfile classfile = getLoader().getClassfile(getSuperclassName());
            if (classfile != null) {
                com.jeantessier.classreader.Method_info attempt = classfile.locateMethod(signature);
                if (attempt != null && (attempt.isPublic() || attempt.isProtected())) {
                    result = attempt;
                }
            }
        }

        Iterator i = getAllInterfaces().iterator();
        while (result == null && i.hasNext()) {
            com.jeantessier.classreader.Classfile classfile = getLoader().getClassfile(i.next().toString());
            if (classfile != null) {
                com.jeantessier.classreader.Method_info attempt = classfile.locateMethod(signature);
                if (attempt != null && (attempt.isPublic() || attempt.isProtected())) {
                    result = attempt;
                }
            }
        }

        return result;
    }

    public Collection<Attribute_info> getAttributes() {
        return attributes;
    }

    public boolean isPublic() {
        return (getAccessFlag() & ACC_PUBLIC) != 0;
    }

    public boolean isPackage() {
        return (getAccessFlag() & ACC_PUBLIC) == 0;
    }

    public boolean isFinal() {
        return (getAccessFlag() & ACC_FINAL) != 0;
    }

    public boolean isSuper() {
        return (getAccessFlag() & ACC_SUPER) != 0;
    }

    public boolean isInterface() {
        return (getAccessFlag() & ACC_INTERFACE) != 0;
    }

    public boolean isAbstract() {
        return (getAccessFlag() & ACC_ABSTRACT) != 0;
    }

    public boolean isAnnotation() {
        return (getAccessFlag() & ACC_ANNOTATION) != 0;
    }

    public boolean isEnum() {
        return (getAccessFlag() & ACC_ENUM) != 0;
    }

    public boolean isSynthetic() {
        return isSyntheticFromAccessFlag() || isSyntheticFromAttribute();
    }

    private boolean isSyntheticFromAccessFlag() {
        return (getAccessFlag() & ACC_SYNTHETIC) != 0;
    }

    private boolean isSyntheticFromAttribute() {
        boolean result = false;

        Iterator i = getAttributes().iterator();
        while (!result && i.hasNext()) {
            result = i.next() instanceof Synthetic_attribute;
        }

        return result;
    }

    public boolean isDeprecated() {
        boolean result = false;

        Iterator i = getAttributes().iterator();
        while (!result && i.hasNext()) {
            result = i.next() instanceof Deprecated_attribute;
        }
    
        return result;
    }

    public boolean isGeneric() {
        boolean result = false;

        Iterator i = getAttributes().iterator();
        while (!result && i.hasNext()) {
            result = i.next() instanceof Signature_attribute;
        }

        return result;
    }

    public String getDeclaration() {
        StringBuffer result = new StringBuffer();

        if (isPublic()) result.append("public ");
        if (isFinal()) result.append("final ");

        if (isInterface()) {
            result.append("interface ").append(getClassName());

            if (getAllInterfaces().size() != 0) {
                result.append(" extends ");
                Iterator i = getAllInterfaces().iterator();
                while (i.hasNext()) {
                    result.append(i.next());
                    if (i.hasNext()) {
                        result.append(", ");
                    }
                }
            }
        } else {
            if (isAbstract()) result.append("abstract ");
            result.append("class ").append(getClassName());

            if (getSuperclassIndex() != 0) {
                result.append(" extends ").append(getSuperclassName());
            }
            
            if (getAllInterfaces().size() != 0) {
                result.append(" implements ");
                Iterator i = getAllInterfaces().iterator();
                while (i.hasNext()) {
                    result.append(i.next());
                    if (i.hasNext()) {
                        result.append(", ");
                    }
                }
            }
        }

        return result.toString();
    }

    public void accept(Visitor visitor) {
        visitor.visitClassfile(this);
    }

    public String toString() {
        return getClassName();
    }

    public boolean isInnerClass() {
        return getMatchingInnerClass() == null;
    }

    public boolean isMemberClass() {
        boolean result = false;

        InnerClass innerClass = getMatchingInnerClass();
        if (innerClass != null) {
            result = innerClass.isMemberClass();
        }

        return result;
    }

    public boolean isLocalClass() {
        boolean result = false;

        InnerClass innerClass = getMatchingInnerClass();
        EnclosingMethod_attribute enclosingMethod = getEnclosingMethod();
        if (innerClass != null && enclosingMethod != null) {
            result = !innerClass.isAnonymousClass();
        }

        return result;
    }

    public boolean isAnonymousClass() {
        boolean result = false;

        InnerClass innerClass = getMatchingInnerClass();
        if (innerClass != null) {
            result = innerClass.isAnonymousClass();
        }

        return result;
    }

    private InnerClass getMatchingInnerClass() {
        InnerClass result = null;

        for (Attribute_info attribute : getAttributes()) {
            if (attribute instanceof InnerClasses_attribute) {
                for (InnerClass innerClass : ((InnerClasses_attribute) attribute).getInnerClasses()) {
                    if (innerClass.getInnerClassInfo().equals(getClassName())) {
                        result = innerClass;
                    }
                }
            }
        }

        return result;
    }

    private EnclosingMethod_attribute getEnclosingMethod() {
        EnclosingMethod_attribute result = null;

        for (Attribute_info attribute : getAttributes()) {
            if (attribute instanceof EnclosingMethod_attribute) {
                result = (EnclosingMethod_attribute) attribute;
            }
        }

        return result;
    }
}
