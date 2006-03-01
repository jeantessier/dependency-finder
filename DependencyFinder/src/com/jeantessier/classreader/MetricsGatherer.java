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

package com.jeantessier.classreader;

import java.util.*;

import org.apache.log4j.*;

public class MetricsGatherer extends VisitorBase {
    private Collection classes                = new LinkedList();
    private Collection interfaces             = new LinkedList();
    private Collection methods                = new LinkedList();
    private Collection fields                 = new LinkedList();
    private Collection syntheticClasses       = new LinkedList();
    private Collection syntheticFields        = new LinkedList();
    private Collection syntheticMethods       = new LinkedList();
    private Collection deprecatedClasses      = new LinkedList();
    private Collection deprecatedFields       = new LinkedList();
    private Collection deprecatedMethods      = new LinkedList();
    private Collection publicClasses          = new LinkedList();
    private Collection publicFields           = new LinkedList();
    private Collection publicMethods          = new LinkedList();
    private Collection publicInnerClasses     = new LinkedList();
    private Collection protectedFields        = new LinkedList();
    private Collection protectedMethods       = new LinkedList();
    private Collection protectedInnerClasses  = new LinkedList();
    private Collection privateFields          = new LinkedList();
    private Collection privateMethods         = new LinkedList();
    private Collection privateInnerClasses    = new LinkedList();
    private Collection packageClasses         = new LinkedList();
    private Collection packageFields          = new LinkedList();
    private Collection packageMethods         = new LinkedList();
    private Collection packageInnerClasses    = new LinkedList();
    private Collection abstractClasses        = new LinkedList();
    private Collection abstractMethods        = new LinkedList();
    private Collection abstractInnerClasses   = new LinkedList();
    private Collection staticFields           = new LinkedList();
    private Collection staticMethods          = new LinkedList();
    private Collection staticInnerClasses     = new LinkedList();
    private Collection finalClasses           = new LinkedList();
    private Collection finalFields            = new LinkedList();
    private Collection finalMethods           = new LinkedList();
    private Collection finalInnerClasses      = new LinkedList();
    private Collection synchronizedMethods    = new LinkedList();
    private Collection nativeMethods          = new LinkedList();
    private Collection volatileFields         = new LinkedList();
    private Collection transientFields        = new LinkedList();
    private Collection customAttributes       = new LinkedList();
    private long[]     instructionCounts           = new long[256];
    
    public Collection getClasses() {
        return classes;
    }

    public Collection getInterfaces() {
        return interfaces;
    }

    public Collection getMethods() {
        return methods;
    }

    public Collection getFields() {
        return fields;
    }

    public Collection getSyntheticClasses() {
        return syntheticClasses;
    }

    public Collection getSyntheticFields() {
        return syntheticFields;
    }

    public Collection getSyntheticMethods() {
        return syntheticMethods;
    }

    public Collection getDeprecatedClasses() {
        return deprecatedClasses;
    }

    public Collection getDeprecatedFields() {
        return deprecatedFields;
    }

    public Collection getDeprecatedMethods() {
        return deprecatedMethods;
    }

    public Collection getPublicClasses() {
        return publicClasses;
    }

    public Collection getPublicFields() {
        return publicFields;
    }

    public Collection getPublicMethods() {
        return publicMethods;
    }

    public Collection getPublicInnerClasses() {
        return publicInnerClasses;
    }

    public Collection getProtectedFields() {
        return protectedFields;
    }

    public Collection getProtectedMethods() {
        return protectedMethods;
    }

    public Collection getProtectedInnerClasses() {
        return protectedInnerClasses;
    }

    public Collection getPrivateFields() {
        return privateFields;
    }

    public Collection getPrivateMethods() {
        return privateMethods;
    }

    public Collection getPrivateInnerClasses() {
        return privateInnerClasses;
    }

    public Collection getPackageClasses() {
        return packageClasses;
    }

    public Collection getPackageFields() {
        return packageFields;
    }

    public Collection getPackageMethods() {
        return packageMethods;
    }

    public Collection getPackageInnerClasses() {
        return packageInnerClasses;
    }

    public Collection getAbstractClasses() {
        return abstractClasses;
    }

    public Collection getAbstractMethods() {
        return abstractMethods;
    }

    public Collection getAbstractInnerClasses() {
        return abstractInnerClasses;
    }

    public Collection getStaticFields() {
        return staticFields;
    }

    public Collection getStaticMethods() {
        return staticMethods;
    }

    public Collection getStaticInnerClasses() {
        return staticInnerClasses;
    }

    public Collection getFinalClasses() {
        return finalClasses;
    }

    public Collection getFinalFields() {
        return finalFields;
    }

    public Collection getFinalMethods() {
        return finalMethods;
    }

    public Collection getFinalInnerClasses() {
        return finalInnerClasses;
    }

    public Collection getSynchronizedMethods() {
        return synchronizedMethods;
    }

    public Collection getNativeMethods() {
        return nativeMethods;
    }

    public Collection getVolatileFields() {
        return volatileFields;
    }

    public Collection getTransientFields() {
        return transientFields;
    }

    public Collection getCustomAttributes() {
        return customAttributes;
    }

    public long[] getInstructionCounts() {
        return instructionCounts;
    }
    
    // Classfile
    public void visitClassfile(Classfile classfile) {
        if ((classfile.getAccessFlag() & Classfile.ACC_PUBLIC) != 0) {
            publicClasses.add(classfile);
        } else {
            packageClasses.add(classfile);
        }

        if ((classfile.getAccessFlag() & Classfile.ACC_FINAL) != 0) {
            finalClasses.add(classfile);
        }

        if ((classfile.getAccessFlag() & Classfile.ACC_INTERFACE) != 0) {
            interfaces.add(classfile);
        } else {
            classes.add(classfile);
        }

        if ((classfile.getAccessFlag() & Classfile.ACC_ABSTRACT) != 0) {
            abstractClasses.add(classfile);
        }

        super.visitClassfile(classfile);
    }

    // Features
    public void visitField_info(Field_info entry) {
        fields.add(entry);

        if ((entry.getAccessFlag() & Field_info.ACC_PUBLIC) != 0) {
            publicFields.add(entry);
        } else if ((entry.getAccessFlag() & Field_info.ACC_PRIVATE) != 0) {
            privateFields.add(entry);
        } else if ((entry.getAccessFlag() & Field_info.ACC_PROTECTED) != 0) {
            protectedFields.add(entry);
        } else {
            packageFields.add(entry);
        }

        if ((entry.getAccessFlag() & Field_info.ACC_STATIC) != 0) {
            staticFields.add(entry);
        }

        if ((entry.getAccessFlag() & Field_info.ACC_FINAL) != 0) {
            finalFields.add(entry);
        }

        if ((entry.getAccessFlag() & Field_info.ACC_VOLATILE) != 0) {
            volatileFields.add(entry);
        }

        if ((entry.getAccessFlag() & Field_info.ACC_TRANSIENT) != 0) {
            transientFields.add(entry);
        }

        super.visitField_info(entry);
    }

    public void visitMethod_info(Method_info entry) {
        methods.add(entry);

        if ((entry.getAccessFlag() & Method_info.ACC_PUBLIC) != 0) {
            publicMethods.add(entry);
        } else if ((entry.getAccessFlag() & Method_info.ACC_PRIVATE) != 0) {
            privateMethods.add(entry);
        } else if ((entry.getAccessFlag() & Method_info.ACC_PROTECTED) != 0) {
            protectedMethods.add(entry);
        } else {
            packageMethods.add(entry);
        }

        if ((entry.getAccessFlag() & Method_info.ACC_STATIC) != 0) {
            staticMethods.add(entry);
        }

        if ((entry.getAccessFlag() & Method_info.ACC_FINAL) != 0) {
            finalMethods.add(entry);
        }

        if ((entry.getAccessFlag() & Method_info.ACC_SYNCHRONIZED) != 0) {
            synchronizedMethods.add(entry);
        }

        if ((entry.getAccessFlag() & Method_info.ACC_NATIVE) != 0) {
            nativeMethods.add(entry);
        }

        if ((entry.getAccessFlag() & Method_info.ACC_ABSTRACT) != 0) {
            abstractMethods.add(entry);
        }

        super.visitMethod_info(entry);
    }

    // Attributes
    public void visitCode_attribute(Code_attribute attribute) {
        Iterator i = attribute.iterator();
        while (i.hasNext()) {
            Instruction instr = (Instruction) i.next();

            getInstructionCounts()[instr.getOpcode()]++;
        }

        super.visitCode_attribute(attribute);
    }

    public void visitSynthetic_attribute(Synthetic_attribute attribute) {
        Object owner = attribute.getOwner();
    
        if (owner instanceof Classfile) {
            syntheticClasses.add(owner);
        } else if (owner instanceof Field_info) {
            syntheticFields.add(owner);
        } else if (owner instanceof Method_info) {
            syntheticMethods.add(owner);
        } else {
            Logger.getLogger(getClass()).warn("Synthetic attribute on unknown Visitable: " + owner.getClass().getName());
        }
    }

    public void visitDeprecated_attribute(Deprecated_attribute attribute) {
        Object owner = attribute.getOwner();
    
        if (owner instanceof Classfile) {
            deprecatedClasses.add(owner);
        } else if (owner instanceof Field_info) {
            deprecatedFields.add(owner);
        } else if (owner instanceof Method_info) {
            deprecatedMethods.add(owner);
        } else {
            Logger.getLogger(getClass()).warn("Deprecated attribute on unknown Visitable: " + owner.getClass().getName());
        }
    }

    public void visitCustom_attribute(Custom_attribute attribute) {
        customAttributes.add(attribute);
    }

    // Attribute helpers
    public void visitInnerClass(InnerClass helper) {
        if ((helper.getAccessFlag() & InnerClass.ACC_PUBLIC) != 0) {
            publicInnerClasses.add(helper);
        } else if ((helper.getAccessFlag() & InnerClass.ACC_PRIVATE) != 0) {
            privateInnerClasses.add(helper);
        } else if ((helper.getAccessFlag() & InnerClass.ACC_PROTECTED) != 0) {
            protectedInnerClasses.add(helper);
        } else {
            packageInnerClasses.add(helper);
        }

        if ((helper.getAccessFlag() & InnerClass.ACC_STATIC) != 0) {
            staticInnerClasses.add(helper);
        }

        if ((helper.getAccessFlag() & InnerClass.ACC_FINAL) != 0) {
            finalInnerClasses.add(helper);
        }

        if ((helper.getAccessFlag() & InnerClass.ACC_INTERFACE) != 0) {
            interfaces.add(helper);
        } else {
            classes.add(helper);
        }

        if ((helper.getAccessFlag() & InnerClass.ACC_ABSTRACT) != 0) {
            abstractInnerClasses.add(helper);
        }
    }
}
