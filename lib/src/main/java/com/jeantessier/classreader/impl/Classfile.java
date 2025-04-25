/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import com.jeantessier.classreader.*;
import org.apache.logging.log4j.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;

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
    private final int accessFlags;
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
    public Classfile(ClassfileLoader loader, DataInput in, AttributeFactory attributeFactory) throws IOException {
        this.loader = loader;

        magicNumber = in.readInt();
        LogManager.getLogger(getClass()).debug("magic number = 0x{}", () -> Integer.toHexString(magicNumber).toUpperCase());

        if (magicNumber != 0xCAFEBABE) {
            throw new IOException("Bad magic number");
        }
        
        // Reading the file format's version number
        minorVersion = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("minor version = {}", minorVersion);
        majorVersion = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("major version = {}", majorVersion);

        // Reading the constant pool
        LogManager.getLogger(getClass()).debug("Reading the constant pool ...");
        constantPool = new ConstantPool(this, in);
        LogManager.getLogger(getClass()).debug(constantPool);

        // Skipping the access flags
        accessFlags = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("accessFlags = {}", accessFlags);

        // Retrieving this class's name
        classIndex = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("thisClass = {} ({})", classIndex, getClassName());

        // Retrieving this class's superclass
        superclassIndex = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("superclass = {} ({})", superclassIndex, getSuperclassName());

        // Retrieving the interfaces
        int interfaceCount = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Reading {} interface(s)", interfaceCount);
        IntStream.range(0, interfaceCount).forEach(i -> {
            try {
                Class_info interfaceInfo = (Class_info) constantPool.get(in.readUnsignedShort());
                LogManager.getLogger(getClass()).debug("    {}", interfaceInfo.getName());
                interfaces.add(interfaceInfo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Retrieving the fields
        int fieldCount = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Reading {} field(s)", fieldCount);
        IntStream.range(0, fieldCount).forEach(i -> {
            try {
                LogManager.getLogger(getClass()).debug("Field {}:", i);
                fields.add(new Field_info(this, in, attributeFactory));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Retrieving the methods
        int methodCount = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Reading {} method(s)", methodCount);
        IntStream.range(0, methodCount).forEach(i -> {
            try {
                LogManager.getLogger(getClass()).debug("Method {}:", i);
                methods.add(new Method_info(this, in, attributeFactory));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Retrieving the attributes
        int attributeCount = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Reading {} class attribute(s)", attributeCount);
        IntStream.range(0, attributeCount).forEach(i -> {
            try {
                LogManager.getLogger(getClass()).debug("Attribute {}:", i);
                attributes.add(attributeFactory.create(constantPool, this, in));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * For testing only
     */
    Classfile(ClassfileLoader loader, ConstantPool constantPool, int accessFlags, int classIndex, int superclassIndex, Collection<Class_info> interfaces, Collection<Field_info> fields, Collection<Method_info> methods, Collection<Attribute_info> attributes) {
        this.loader = loader;
        this.constantPool = constantPool;

        this.magicNumber = 0xCAFEBABE;
        this.minorVersion = Integer.MIN_VALUE;
        this.majorVersion = Integer.MAX_VALUE;

        this.accessFlags = accessFlags;
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

    public int getAccessFlags() {
        return accessFlags;
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

    public boolean hasSuperclass() {
        return getSuperclassIndex() != 0;
    }

    public Class_info getRawSuperclass() {
        return (Class_info) getConstantPool().get(getSuperclassIndex());
    }

    public String getSuperclassName() {
        if (hasSuperclass()) {
            return getRawSuperclass().getName();
        }
        
        return "";
    }

    public Class_info getInterface(String name) {
        return interfaces.parallelStream()
                .filter(interfaceInfo -> interfaceInfo.getName().equals(name))
                .findAny()
                .orElse(null);
    }

    public Collection<Class_info> getAllInterfaces() {
        return interfaces;
    }

    public Collection<Field_info> getAllFields() {
        return fields;
    }

    public Field_info getField(Predicate<com.jeantessier.classreader.Field_info> filter) {
        return fields.parallelStream()
                .filter(filter)
                .findAny()
                .orElse(null);
    }

    public com.jeantessier.classreader.Field_info locateField(Predicate<com.jeantessier.classreader.Field_info> filter) {
        var localField = getField(filter);
        if (localField != null) {
            return localField;
        }

        var superclass = getLoader().getClassfile(getSuperclassName());
        if (superclass != null) {
            var inheritedField = superclass.locateField(filter);
            if (inheritedField != null && (inheritedField.isPublic() || inheritedField.isProtected() || (inheritedField.isPackage() && inheritedField.getClassfile().getPackageName().equals(superclass.getPackageName())))) {
                return inheritedField;
            }
        }

        for (var interfaceInfo : getAllInterfaces()) {
            var interfaceClassfile = getLoader().getClassfile(interfaceInfo.getName());
            if (interfaceClassfile != null) {
                var interfaceField = interfaceClassfile.locateField(filter);
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

    public Method_info getMethod(Predicate<com.jeantessier.classreader.Method_info> filter) {
        return methods.parallelStream()
                .filter(filter)
                .findAny()
                .orElse(null);
    }

    public com.jeantessier.classreader.Method_info locateMethod(Predicate<com.jeantessier.classreader.Method_info> filter) {
        var localMethod = getMethod(filter);
        if (localMethod != null) {
            return localMethod;
        }

        var superclass = getLoader().getClassfile(getSuperclassName());
        if (superclass != null) {
            var inheritedMethod = superclass.locateMethod(filter);
            if (inheritedMethod != null && (inheritedMethod.isPublic() || inheritedMethod.isProtected() || (inheritedMethod.isPackage() && inheritedMethod.getClassfile().getPackageName().equals(superclass.getPackageName())))) {
                return inheritedMethod;
            }
        }

        for (var interfaceInfo : getAllInterfaces()) {
            var interfaceClassfile = getLoader().getClassfile(interfaceInfo.getName());
            if (interfaceClassfile != null) {
                var interfaceMethod = interfaceClassfile.locateMethod(filter);
                if (interfaceMethod != null && (interfaceMethod.isPublic() || interfaceMethod.isProtected())) {
                    return interfaceMethod;
                }
            }
        }

        return null;
    }

    public Collection<? extends com.jeantessier.classreader.Method_info> locateMethodDeclarations(Predicate<com.jeantessier.classreader.Method_info> filter) {
        var declarations = Stream.concat(
                    getAllInterfaces().stream().map(Class_info::getName),
                    Stream.of(getSuperclassName())
                )
                .map(className -> getLoader().getClassfile(className))
                .filter(Objects::nonNull)
                .flatMap(classfile -> classfile.locateMethodDeclarations(filter).stream())
                .toList();

        if (!declarations.isEmpty()) {
            return declarations;
        }

        return getAllMethods().stream()
                .filter(filter)
                .toList();
    }

    public Collection<Attribute_info> getAttributes() {
        return attributes;
    }

    public boolean isPublic() {
        return (getAccessFlags() & ACC_PUBLIC) != 0;
    }

    public boolean isPackage() {
        return (getAccessFlags() & ACC_PUBLIC) == 0;
    }

    public boolean isFinal() {
        return (getAccessFlags() & ACC_FINAL) != 0;
    }

    public boolean isSuper() {
        return (getAccessFlags() & ACC_SUPER) != 0;
    }

    public boolean isInterface() {
        return (getAccessFlags() & ACC_INTERFACE) != 0;
    }

    public boolean isAbstract() {
        return (getAccessFlags() & ACC_ABSTRACT) != 0;
    }

    public boolean isSynthetic() {
        return isSyntheticFromAccessFlags() || isSyntheticFromAttribute();
    }

    public boolean isAnnotation() {
        return (getAccessFlags() & ACC_ANNOTATION) != 0;
    }

    public boolean isEnum() {
        return (getAccessFlags() & ACC_ENUM) != 0;
    }

    public boolean isModule() {
        return (getAccessFlags() & ACC_MODULE) != 0;
    }

    private boolean isSyntheticFromAccessFlags() {
        return (getAccessFlags() & ACC_SYNTHETIC) != 0;
    }

    private boolean isSyntheticFromAttribute() {
        return getAttributes().parallelStream().anyMatch(attribute -> attribute instanceof Synthetic_attribute);
    }

    public boolean isDeprecated() {
        return getAttributes().parallelStream().anyMatch(attribute -> attribute instanceof Deprecated_attribute);
    }

    public boolean isGeneric() {
        return getAttributes().parallelStream().anyMatch(attribute -> attribute instanceof Signature_attribute);
    }

    public String getDeclaration() {
        var result = new StringBuilder();

        if (isPublic()) result.append("public ");
        if (isFinal()) result.append("final ");

        if (isInterface()) {
            result.append("interface ").append(getClassName());

            if (!getAllInterfaces().isEmpty()) {
                result.append(" extends ");
                result.append(getAllInterfaces().stream().map(Class_info::toString).collect(joining(", ")));
            }
        } else {
            if (isAbstract()) result.append("abstract ");
            result.append("class ").append(getClassName());

            if (hasSuperclass()) {
                result.append(" extends ").append(getSuperclassName());
            }
            
            if (!getAllInterfaces().isEmpty()) {
                result.append(" implements ");
                result.append(getAllInterfaces().stream().map(Class_info::toString).collect(joining(", ")));
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

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        return compareTo((Classfile) object) == 0;
    }

    public int hashCode() {
        return getClassName().hashCode();
    }

    public int compareTo(com.jeantessier.classreader.Classfile other) {
        if (this == other) {
            return 0;
        }

        if (other == null) {
            throw new ClassCastException("compareTo: expected a " + getClass().getName() + " but got null");
        }

        return getClassName().compareTo(other.getClassName());
    }
}
