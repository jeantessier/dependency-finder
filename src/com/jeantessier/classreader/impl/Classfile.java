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

package com.jeantessier.classreader.impl;

import com.jeantessier.classreader.ClassfileLoader;
import com.jeantessier.classreader.Visitor;
import org.apache.log4j.Logger;

import java.io.DataInput;
import java.io.IOException;
import java.util.Collection;
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
    private static final int ACC_MODULE = 0x8000;

    private final ClassfileLoader loader;

    private final int magicNumber;
    private final int minorVersion;
    private final int majorVersion;
    private final ConstantPool constantPool;
    private final int accessFlag;
    private final int classIndex;
    private final int superclassIndex;
    private final Collection<Class_info> interfaces = new LinkedList<>();
    private final Collection<Field_info> fields = new LinkedList<>();
    private final Collection<Method_info> methods = new LinkedList<>();
    private final Collection<Attribute_info> attributes = new LinkedList<>();

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

        // Retrieving the interfaces
        int interfaceCount = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + interfaceCount + " interface(s)");
        for (var i=0; i<interfaceCount; i++) {
            Class_info interfaceInfo = (Class_info) constantPool.get(in.readUnsignedShort());
            Logger.getLogger(getClass()).debug("    " + interfaceInfo.getName());
            interfaces.add(interfaceInfo);
        }

        // Retrieving the fields
        int fieldCount = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + fieldCount + " field(s)");
        for (var i=0; i<fieldCount; i++) {
            Logger.getLogger(getClass()).debug("Field " + i + ":");
            fields.add(new Field_info(this, in));
        }

        // Retrieving the methods
        int methodCount = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + methodCount + " method(s)");
        for (var i=0; i<methodCount; i++) {
            Logger.getLogger(getClass()).debug("Method " + i + ":");
            methods.add(new Method_info(this, in));
        }

        // Retrieving the attributes
        int attributeCount = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + attributeCount + " class attribute(s)");
        for (var i=0; i<attributeCount; i++) {
            Logger.getLogger(getClass()).debug("Attribute " + i + ":");
            attributes.add(attributeFactory.create(constantPool, this, in));
        }
    }

    /**
     * For testing only
     */
    Classfile(ClassfileLoader loader, ConstantPool constantPool, int accessFlag, int classIndex, int superclassIndex, Collection<Class_info> interfaces, Collection<Field_info> fields, Collection<Method_info> methods, Collection<Attribute_info> attributes) {
        this.loader = loader;
        this.constantPool = constantPool;

        this.magicNumber = 0xCAFEBABE;
        this.minorVersion = Integer.MIN_VALUE;
        this.majorVersion = Integer.MAX_VALUE;

        this.accessFlag = accessFlag;
        this.classIndex = classIndex;
        this.superclassIndex = superclassIndex;

        this.interfaces.addAll(interfaces);
        this.fields.addAll(fields);
        this.methods.addAll(methods);
        this.attributes.addAll(attributes);
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
        if (getSuperclassIndex() != 0) {
            return getRawSuperclass().getName();
        }
        
        return "";
    }

    public Class_info getInterface(String name) {
        return interfaces.stream()
                .filter(interfaceInfo -> interfaceInfo.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Collection<Class_info> getAllInterfaces() {
        return interfaces;
    }

    public Collection<Field_info> getAllFields() {
        return fields;
    }

    public Field_info getField(String name) {
        return fields.stream()
                .filter(field -> field.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public com.jeantessier.classreader.Field_info locateField(String name) {
        var localField = getField(name);
        if (localField != null) {
            return localField;
        }

        var superclass = getLoader().getClassfile(getSuperclassName());
        if (superclass != null) {
            var inheritedField = superclass.locateField(name);
            if (inheritedField != null && (inheritedField.isPublic() || inheritedField.isProtected() || (inheritedField.isPackage() && inheritedField.getClassfile().getPackageName().equals(superclass.getPackageName())))) {
                return inheritedField;
            }
        }

        for (var interfaceInfo : getAllInterfaces()) {
            var interfaceClassfile = getLoader().getClassfile(interfaceInfo.getName());
            if (interfaceClassfile != null) {
                var interfaceField = interfaceClassfile.locateField(name);
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
        return methods.stream()
                .filter(method -> method.getSignature().equals(signature))
                .findFirst()
                .orElse(null);
    }

    public com.jeantessier.classreader.Method_info locateMethod(String signature) {
        var localMethod = getMethod(signature);
        if (localMethod != null) {
            return localMethod;
        }

        var superclass = getLoader().getClassfile(getSuperclassName());
        if (superclass != null) {
            var inheritedMethod = superclass.locateMethod(signature);
            if (inheritedMethod != null && (inheritedMethod.isPublic() || inheritedMethod.isProtected() || (inheritedMethod.isPackage() && inheritedMethod.getClassfile().getPackageName().equals(superclass.getPackageName())))) {
                return inheritedMethod;
            }
        }

        for (var interfaceInfo : getAllInterfaces()) {
            var interfaceClassfile = getLoader().getClassfile(interfaceInfo.getName());
            if (interfaceClassfile != null) {
                var interfaceMethod = interfaceClassfile.locateMethod(signature);
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

    public boolean isSynthetic() {
        return isSyntheticFromAccessFlag() || isSyntheticFromAttribute();
    }

    public boolean isAnnotation() {
        return (getAccessFlag() & ACC_ANNOTATION) != 0;
    }

    public boolean isEnum() {
        return (getAccessFlag() & ACC_ENUM) != 0;
    }

    public boolean isModule() {
        return (getAccessFlag() & ACC_MODULE) != 0;
    }

    private boolean isSyntheticFromAccessFlag() {
        return (getAccessFlag() & ACC_SYNTHETIC) != 0;
    }

    private boolean isSyntheticFromAttribute() {
        return getAttributes().stream().anyMatch(attribute -> attribute instanceof Synthetic_attribute);
    }

    public boolean isDeprecated() {
        return getAttributes().stream().anyMatch(attribute -> attribute instanceof Deprecated_attribute);
    }

    public boolean isGeneric() {
        return getAttributes().stream().anyMatch(attribute -> attribute instanceof Signature_attribute);
    }

    public String getDeclaration() {
        var result = new StringBuilder();

        if (isPublic()) result.append("public ");
        if (isFinal()) result.append("final ");

        if (isInterface()) {
            result.append("interface ").append(getClassName());

            if (!getAllInterfaces().isEmpty()) {
                result.append(" extends ");
                result.append(String.join(", ", getAllInterfaces().stream().map(Class_info::toString).toList()));
            }
        } else {
            if (isAbstract()) result.append("abstract ");
            result.append("class ").append(getClassName());

            if (getSuperclassIndex() != 0) {
                result.append(" extends ").append(getSuperclassName());
            }
            
            if (!getAllInterfaces().isEmpty()) {
                result.append(" implements ");
                result.append(String.join(", ", getAllInterfaces().stream().map(Class_info::toString).toList()));
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
        var innerClass = getMatchingInnerClass();
        if (innerClass != null) {
            return innerClass.isMemberClass();
        }

        return false;
    }

    public boolean isLocalClass() {
        var innerClass = getMatchingInnerClass();
        var enclosingMethod = getEnclosingMethod();
        if (innerClass != null && enclosingMethod != null) {
            return !innerClass.isAnonymousClass();
        }

        return false;
    }

    public boolean isAnonymousClass() {
        var innerClass = getMatchingInnerClass();
        if (innerClass != null) {
            return innerClass.isAnonymousClass();
        }

        return false;
    }

    private InnerClass getMatchingInnerClass() {
        for (var attribute : getAttributes()) {
            if (attribute instanceof InnerClasses_attribute innerClasses_attribute) {
                for (var innerClass : innerClasses_attribute.getInnerClasses()) {
                    if (innerClass.getInnerClassInfo().equals(getClassName())) {
                        return innerClass;
                    }
                }
            }
        }

        return null;
    }

    private EnclosingMethod_attribute getEnclosingMethod() {
        for (var attribute : getAttributes()) {
            if (attribute instanceof EnclosingMethod_attribute enclosingMethod_attribute) {
                return enclosingMethod_attribute;
            }
        }

        return null;
    }
}
