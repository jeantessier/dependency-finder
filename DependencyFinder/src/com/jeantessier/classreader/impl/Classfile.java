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

package com.jeantessier.classreader.impl;

import com.jeantessier.classreader.ClassfileLoader;
import com.jeantessier.classreader.Visitor;
import org.apache.log4j.Logger;

import java.io.DataInput;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

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
    private Collection<Class_info> interfaces = new LinkedList<Class_info>();
    private Collection<Field_info> fields = new LinkedList<Field_info>();
    private Collection<Method_info> methods = new LinkedList<Method_info>();
    private Collection<Attribute_info> attributes = new LinkedList<Attribute_info>();

    /**
     *  Parses the input stream and extracts the class description.
     *  You should only call this constructor from a ClassfileLoader.
     */
    public Classfile(ClassfileLoader loader, DataInput in) throws IOException {
        this(loader, in, new AttributeFactory());
    }

    public Classfile(ClassfileLoader loader, DataInput in, AttributeFactory attributeFactory) throws IOException {
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
            interfaces.add(interfaceInfo);
        }

        // Retrieving the fields
        int fieldCount = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + fieldCount + " field(s)");
        for (int i=0; i<fieldCount; i++) {
            Logger.getLogger(getClass()).debug("Field " + i + ":");
            fields.add(new Field_info(this, in));
        }

        // Retrieving the methods
        int methodCount = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + methodCount + " method(s)");
        for (int i=0; i<methodCount; i++) {
            Logger.getLogger(getClass()).debug("Method " + i + ":");
            methods.add(new Method_info(this, in));
        }

        // Retrieving the attributes
        int attributeCount = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + attributeCount + " class attribute(s)");
        for (int i=0; i<attributeCount; i++) {
            Logger.getLogger(getClass()).debug("Attribute " + i + ":");
            attributes.add(attributeFactory.create(constantPool, this, in));
        }
    }

    /**
     * For testing only
     */
    Classfile(ClassfileLoader loader, ConstantPool constantPool, int accessFlag, int classIndex, int superclassIndex, Iterable<Class_info> interfaces, Iterable<Field_info> fields, Iterable<Method_info> methods, Iterable<Attribute_info> attributes) {
        this.loader = loader;
        this.constantPool = constantPool;
        this.accessFlag = accessFlag;
        this.classIndex = classIndex;
        this.superclassIndex = superclassIndex;

        for (Class_info interfaceInfo : interfaces) {
            this.interfaces.add(interfaceInfo);
        }

        for (Field_info field : fields) {
            this.fields.add(field);
        }

        for (Method_info method : methods) {
            this.methods.add(method);
        }

        for (Attribute_info attribute : attributes) {
            this.attributes.add(attribute);
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
        return getRawClass().getName();
    }

    public String getPackageName() {
        return getRawClass().getPackageName();
    }

    public String getSimpleName() {
        return getRawClass().getSimpleName();
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
            result = getRawSuperclass().getName();
        }
        
        return result;
    }

    public Class_info getInterface(String name) {
        for (Class_info interfaceInfo : interfaces) {
            if (interfaceInfo.getName().equals(name)) {
                return interfaceInfo;
            }
        }

        return null;
    }

    public Collection<Class_info> getAllInterfaces() {
        return interfaces;
    }

    public Collection<Field_info> getAllFields() {
        return fields;
    }

    public Field_info getField(String name) {
        for (Field_info field : fields) {
            if (field.getName().equals(name)) {
                return field;
            }
        }

        return null;
    }

    public com.jeantessier.classreader.Field_info locateField(String name) {
        com.jeantessier.classreader.Field_info localField = getField(name);
        if (localField != null) {
            return localField;
        }

        com.jeantessier.classreader.Classfile superclass = getLoader().getClassfile(getSuperclassName());
        if (superclass != null) {
            com.jeantessier.classreader.Field_info inheritedField = superclass.locateField(name);
            if (inheritedField != null && (inheritedField.isPublic() || inheritedField.isProtected() || (inheritedField.isPackage() && inheritedField.getClassfile().getPackageName().equals(superclass.getPackageName())))) {
                return inheritedField;
            }
        }


        for (com.jeantessier.classreader.Class_info interfaceInfo : getAllInterfaces()) {
            com.jeantessier.classreader.Classfile interfaceClassfile = getLoader().getClassfile(interfaceInfo.getName());
            if (interfaceClassfile != null) {
                com.jeantessier.classreader.Field_info interfaceField = interfaceClassfile.locateField(name);
                if (interfaceField != null && (interfaceField.isPublic() || interfaceField.isProtected())) {
                    return interfaceField;
                }
            }
        }

        return null;
    }

    public Collection<Method_info> getAllMethods() {
        return methods;
    }

    public Method_info getMethod(String signature) {
        for (Method_info method : methods) {
            if (method.getSignature().equals(signature)) {
                return method;
            }
        }

        return null;
    }

    public com.jeantessier.classreader.Method_info locateMethod(String signature) {
        com.jeantessier.classreader.Method_info localMethod = getMethod(signature);
        if (localMethod != null) {
            return localMethod;
        }

        com.jeantessier.classreader.Classfile superclass = getLoader().getClassfile(getSuperclassName());
        if (superclass != null) {
            com.jeantessier.classreader.Method_info inheritedMethod = superclass.locateMethod(signature);
            if (inheritedMethod != null && (inheritedMethod.isPublic() || inheritedMethod.isProtected() || (inheritedMethod.isPackage() && inheritedMethod.getClassfile().getPackageName().equals(superclass.getPackageName())))) {
                return inheritedMethod;
            }
        }

        for (com.jeantessier.classreader.Class_info inferfaceInfo : getAllInterfaces()) {
            com.jeantessier.classreader.Classfile interfaceClassfile = getLoader().getClassfile(inferfaceInfo.getName());
            if (interfaceClassfile != null) {
                com.jeantessier.classreader.Method_info interfaceMethod = interfaceClassfile.locateMethod(signature);
                if (interfaceMethod != null && (interfaceMethod.isPublic() || interfaceMethod.isProtected())) {
                    return interfaceMethod;
                }
            }
        }

        return null;
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
        return getMatchingInnerClass() != null;
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
