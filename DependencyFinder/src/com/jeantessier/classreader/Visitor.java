/*
 *  Dependency Finder - Computes quality factors from compiled Java code
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeantessier.classreader;

public interface Visitor {
    // Classfile
    public void VisitClassfile(Classfile classfile);

    // ConstantPool entries
    public void VisitConstantPool(ConstantPool constant_pool);
    public void VisitClass_info(Class_info entry);
    public void VisitFieldRef_info(FieldRef_info entry);
    public void VisitMethodRef_info(MethodRef_info entry);
    public void VisitInterfaceMethodRef_info(InterfaceMethodRef_info entry);
    public void VisitString_info(String_info entry);
    public void VisitInteger_info(Integer_info entry);
    public void VisitFloat_info(Float_info entry);
    public void VisitLong_info(Long_info entry);
    public void VisitDouble_info(Double_info entry);
    public void VisitNameAndType_info(NameAndType_info entry);
    public void VisitUTF8_info(UTF8_info entry);

    // Features
    public void VisitField_info(Field_info entry);
    public void VisitMethod_info(Method_info entry);

    // Attributes
    public void VisitConstantValue_attribute(ConstantValue_attribute attribute);
    public void VisitCode_attribute(Code_attribute attribute);
    public void VisitExceptions_attribute(Exceptions_attribute attribute);
    public void VisitInnerClasses_attribute(InnerClasses_attribute attribute);
    public void VisitSynthetic_attribute(Synthetic_attribute attribute);
    public void VisitSourceFile_attribute(SourceFile_attribute attribute);
    public void VisitLineNumberTable_attribute(LineNumberTable_attribute attribute);
    public void VisitLocalVariableTable_attribute(LocalVariableTable_attribute attribute);
    public void VisitDeprecated_attribute(Deprecated_attribute attribute);
    public void VisitCustom_attribute(Custom_attribute attribute);

    // Attribute helpers
    public void VisitExceptionHandler(ExceptionHandler helper);
    public void VisitInnerClass(InnerClass helper);
    public void VisitLineNumber(LineNumber helper);
    public void VisitLocalVariable(LocalVariable helper);
}
