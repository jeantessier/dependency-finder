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

package com.jeantessier.diff;

import java.util.*;

import com.jeantessier.classreader.*;

/**
 * TODO class comments
 */
public class IncompatibleDifferenceStrategy extends APIDifferenceStrategy {
    public IncompatibleDifferenceStrategy(DifferenceStrategy strategy) {
        super(strategy);
    }

    public boolean isClassDifferent(Classfile oldClass, Classfile newClass) {
        return isRemoved(oldClass, newClass) ||
               (!isNew(oldClass, newClass) &&
                isClassModified(oldClass, newClass));
    }

    public boolean isFieldDifferent(Field_info oldField, Field_info newField) {
        return isRemoved(oldField, newField) ||
               isDeprecationModified(oldField, newField) ||
               isDeclarationModified(oldField, newField);
    }

    public boolean isMethodDifferent(Method_info oldMethod, Method_info newMethod) {
        return isRemoved(oldMethod, newMethod) ||
               isDeprecationModified(oldMethod, newMethod) ||
               isDeclarationModified(oldMethod, newMethod);
    }

    public boolean isPackageDifferent(Map<String, Classfile> oldPackage, Map<String, Classfile> newPackage) {
        return isPackageRemoved(oldPackage, newPackage) ||
               (!isPackageNew(oldPackage, newPackage) &&
                isPackageModified(oldPackage, newPackage));
    }

    public boolean isDeclarationModified(Classfile oldClass, Classfile newClass) {
        return oldClass != null && newClass != null &&
               ((oldClass.isPublic() && !newClass.isPublic()) ||
                (oldClass.isInterface() != newClass.isInterface()) ||
                (!oldClass.isAbstract() && newClass.isAbstract()) ||
                (!oldClass.isFinal() && newClass.isFinal()) ||
                isExtendsClauseModified(oldClass, newClass) ||
                isImplementsClauseModified(oldClass, newClass));
    }

    private boolean isExtendsClauseModified(Classfile oldClass, Classfile newClass) {
        return !oldClass.getSuperclassName().equals(newClass.getSuperclassName());
    }

    private boolean isImplementsClauseModified(Classfile oldClass, Classfile newClass) {
        return !oldClass.getAllInterfaces().containsAll(newClass.getAllInterfaces()) ||
               !newClass.getAllInterfaces().containsAll(oldClass.getAllInterfaces());
    }

    private boolean isDeclarationModified(Field_info oldField, Field_info newField) {
        return oldField != null && newField != null &&
               ((oldField.isPublic() && !newField.isPublic()) ||
                (oldField.isProtected() && (newField.isPackage() || newField.isPrivate())) ||
                (!oldField.isFinal() && newField.isFinal()) ||
                !oldField.getType().equals(newField.getType()));
    }

    private boolean isDeclarationModified(Method_info oldMethod, Method_info newMethod) {
        return oldMethod != null && newMethod != null &&
               ((oldMethod.isPublic() && !newMethod.isPublic()) ||
                (oldMethod.isProtected() && (newMethod.isPackage() || newMethod.isPrivate())) ||
                (!oldMethod.isAbstract() && newMethod.isAbstract()) ||
                (!oldMethod.isStatic() && newMethod.isStatic()) ||
                (!oldMethod.isFinal() && newMethod.isFinal()) ||
                !oldMethod.getReturnType().equals(newMethod.getReturnType()) ||
                isThrowsClauseModified(oldMethod, newMethod));
    }

    private boolean isThrowsClauseModified(Method_info oldMethod, Method_info newMethod) {
        return !oldMethod.getExceptions().containsAll(newMethod.getExceptions()) ||
               !newMethod.getExceptions().containsAll(oldMethod.getExceptions());
    }

    protected boolean isDeprecationModified(Deprecatable oldItem, Deprecatable newItem) {
        return oldItem != null && newItem != null && !oldItem.isDeprecated() && newItem.isDeprecated();
    }
}
