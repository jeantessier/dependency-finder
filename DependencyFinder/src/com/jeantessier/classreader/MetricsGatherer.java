/*
 *  Copyright (c) 2001-2002, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of the Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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
	private Collection classes                 = new LinkedList();
	private Collection interfaces              = new LinkedList();
	private Collection methods                 = new LinkedList();
	private Collection fields                  = new LinkedList();
	private Collection synthetic_classes       = new LinkedList();
	private Collection synthetic_fields        = new LinkedList();
	private Collection synthetic_methods       = new LinkedList();
	private Collection deprecated_classes      = new LinkedList();
	private Collection deprecated_fields       = new LinkedList();
	private Collection deprecated_methods      = new LinkedList();
	private Collection public_classes          = new LinkedList();
	private Collection public_fields           = new LinkedList();
	private Collection public_methods          = new LinkedList();
	private Collection public_inner_classes    = new LinkedList();
	private Collection protected_fields        = new LinkedList();
	private Collection protected_methods       = new LinkedList();
	private Collection protected_inner_classes = new LinkedList();
	private Collection private_fields          = new LinkedList();
	private Collection private_methods         = new LinkedList();
	private Collection private_inner_classes   = new LinkedList();
	private Collection package_classes         = new LinkedList();
	private Collection package_fields          = new LinkedList();
	private Collection package_methods         = new LinkedList();
	private Collection package_inner_classes   = new LinkedList();
	private Collection abstract_classes        = new LinkedList();
	private Collection abstract_methods        = new LinkedList();
	private Collection abstract_inner_classes  = new LinkedList();
	private Collection static_fields           = new LinkedList();
	private Collection static_methods          = new LinkedList();
	private Collection static_inner_classes    = new LinkedList();
	private Collection final_classes           = new LinkedList();
	private Collection final_fields            = new LinkedList();
	private Collection final_methods           = new LinkedList();
	private Collection final_inner_classes     = new LinkedList();
	private Collection synchronized_methods    = new LinkedList();
	private Collection native_methods          = new LinkedList();
	private Collection volatile_fields         = new LinkedList();
	private Collection transient_fields        = new LinkedList();
	private Collection custom_attributes       = new LinkedList();

	public Collection Classes() {
		return classes;
	}

	public Collection Interfaces() {
		return interfaces;
	}

	public Collection Methods() {
		return methods;
	}

	public Collection Fields() {
		return fields;
	}

	public Collection SyntheticClasses() {
		return synthetic_classes;
	}

	public Collection SyntheticFields() {
		return synthetic_fields;
	}

	public Collection SyntheticMethods() {
		return synthetic_methods;
	}

	public Collection DeprecatedClasses() {
		return deprecated_classes;
	}

	public Collection DeprecatedFields() {
		return deprecated_fields;
	}

	public Collection DeprecatedMethods() {
		return deprecated_methods;
	}

	public Collection PublicClasses() {
		return public_classes;
	}

	public Collection PublicFields() {
		return public_fields;
	}

	public Collection PublicMethods() {
		return public_methods;
	}

	public Collection PublicInnerClasses() {
		return public_inner_classes;
	}

	public Collection ProtectedFields() {
		return protected_fields;
	}

	public Collection ProtectedMethods() {
		return protected_methods;
	}

	public Collection ProtectedInnerClasses() {
		return protected_inner_classes;
	}

	public Collection PrivateFields() {
		return private_fields;
	}

	public Collection PrivateMethods() {
		return private_methods;
	}

	public Collection PrivateInnerClasses() {
		return private_inner_classes;
	}

	public Collection PackageClasses() {
		return package_classes;
	}

	public Collection PackageFields() {
		return package_fields;
	}

	public Collection PackageMethods() {
		return package_methods;
	}

	public Collection PackageInnerClasses() {
		return package_inner_classes;
	}

	public Collection AbstractClasses() {
		return abstract_classes;
	}

	public Collection AbstractMethods() {
		return abstract_methods;
	}

	public Collection AbstractInnerClasses() {
		return abstract_inner_classes;
	}

	public Collection StaticFields() {
		return static_fields;
	}

	public Collection StaticMethods() {
		return static_methods;
	}

	public Collection StaticInnerClasses() {
		return static_inner_classes;
	}

	public Collection FinalClasses() {
		return final_classes;
	}

	public Collection FinalFields() {
		return final_fields;
	}

	public Collection FinalMethods() {
		return final_methods;
	}

	public Collection FinalInnerClasses() {
		return final_inner_classes;
	}

	public Collection SynchronizedMethods() {
		return synchronized_methods;
	}

	public Collection NativeMethods() {
		return native_methods;
	}

	public Collection VolatileFields() {
		return volatile_fields;
	}

	public Collection TransientFields() {
		return transient_fields;
	}

	public Collection CustomAttributes() {
		return custom_attributes;
	}

	// Classfile
	public void VisitClassfile(Classfile classfile) {
		if ((classfile.AccessFlag() & Classfile.ACC_PUBLIC) != 0) {
			public_classes.add(classfile);
		} else {
			package_classes.add(classfile);
		}

		if ((classfile.AccessFlag() & Classfile.ACC_FINAL) != 0) {
			final_classes.add(classfile);
		}

		if ((classfile.AccessFlag() & Classfile.ACC_INTERFACE) != 0) {
			interfaces.add(classfile);
		} else {
			classes.add(classfile);
		}

		if ((classfile.AccessFlag() & Classfile.ACC_ABSTRACT) != 0) {
			abstract_classes.add(classfile);
		}

		Iterator i;

		i = classfile.Attributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}

		i = classfile.Fields().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}

		i = classfile.Methods().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
	}

	// ConstantPool entries
	public void VisitClass_info(Class_info entry) {}
	public void VisitFieldRef_info(FieldRef_info entry) {}
	public void VisitMethodRef_info(MethodRef_info entry) {}
	public void VisitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {}
	public void VisitString_info(String_info entry) {}
	public void VisitInteger_info(Integer_info entry) {}
	public void VisitFloat_info(Float_info entry) {}
	public void VisitLong_info(Long_info entry) {}
	public void VisitDouble_info(Double_info entry) {}
	public void VisitNameAndType_info(NameAndType_info entry) {}
	public void VisitUTF8_info(UTF8_info entry) {}

	// Features
	public void VisitField_info(Field_info entry) {
		fields.add(entry);

		if ((entry.AccessFlag() & Field_info.ACC_PUBLIC) != 0) {
			public_fields.add(entry);
		} else if ((entry.AccessFlag() & Field_info.ACC_PRIVATE) != 0) {
			private_fields.add(entry);
		} else if ((entry.AccessFlag() & Field_info.ACC_PROTECTED) != 0) {
			protected_fields.add(entry);
		} else {
			package_fields.add(entry);
		}

		if ((entry.AccessFlag() & Field_info.ACC_STATIC) != 0) {
			static_fields.add(entry);
		}

		if ((entry.AccessFlag() & Field_info.ACC_FINAL) != 0) {
			final_fields.add(entry);
		}

		if ((entry.AccessFlag() & Field_info.ACC_VOLATILE) != 0) {
			volatile_fields.add(entry);
		}

		if ((entry.AccessFlag() & Field_info.ACC_TRANSIENT) != 0) {
			transient_fields.add(entry);
		}

		super.VisitField_info(entry);
	}

	public void VisitMethod_info(Method_info entry) {
		methods.add(entry);

		if ((entry.AccessFlag() & Method_info.ACC_PUBLIC) != 0) {
			public_methods.add(entry);
		} else if ((entry.AccessFlag() & Method_info.ACC_PRIVATE) != 0) {
			private_methods.add(entry);
		} else if ((entry.AccessFlag() & Method_info.ACC_PROTECTED) != 0) {
			protected_methods.add(entry);
		} else {
			package_methods.add(entry);
		}

		if ((entry.AccessFlag() & Method_info.ACC_STATIC) != 0) {
			static_methods.add(entry);
		}

		if ((entry.AccessFlag() & Method_info.ACC_FINAL) != 0) {
			final_methods.add(entry);
		}

		if ((entry.AccessFlag() & Method_info.ACC_SYNCHRONIZED) != 0) {
			synchronized_methods.add(entry);
		}

		if ((entry.AccessFlag() & Method_info.ACC_NATIVE) != 0) {
			native_methods.add(entry);
		}

		if ((entry.AccessFlag() & Method_info.ACC_ABSTRACT) != 0) {
			abstract_methods.add(entry);
		}

		super.VisitMethod_info(entry);
	}

	// Attributes
	public void VisitConstantValue_attribute(ConstantValue_attribute attribute) {}

	public void VisitCode_attribute(Code_attribute attribute) {
		Iterator i = attribute.Attributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
	}

	public void VisitExceptions_attribute(Exceptions_attribute attribute) {}

	public void VisitInnerClasses_attribute(InnerClasses_attribute attribute) {
		Iterator i = attribute.Classes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
	}

	public void VisitSynthetic_attribute(Synthetic_attribute attribute) {
		Object owner = attribute.Owner();
	
		if (owner instanceof Classfile) {
			synthetic_classes.add(owner);
		} else if (owner instanceof Field_info) {
			synthetic_fields.add(owner);
		} else if (owner instanceof Method_info) {
			synthetic_methods.add(owner);
		} else {
			Logger.getLogger(getClass()).warn("Synthetic attribute on unknown Visitable: " + owner.getClass().getName());
		}
	}

	public void VisitSourceFile_attribute(SourceFile_attribute attribute) {}
	public void VisitLineNumberTable_attribute(LineNumberTable_attribute attribute) {}
	public void VisitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {}

	public void VisitDeprecated_attribute(Deprecated_attribute attribute) {
		Object owner = attribute.Owner();
	
		if (owner instanceof Classfile) {
			deprecated_classes.add(owner);
		} else if (owner instanceof Field_info) {
			deprecated_fields.add(owner);
		} else if (owner instanceof Method_info) {
			deprecated_methods.add(owner);
		} else {
			Logger.getLogger(getClass()).warn("Deprecated attribute on unknown Visitable: " + owner.getClass().getName());
		}
	}

	public void VisitCustom_attribute(Custom_attribute attribute) {
		custom_attributes.add(attribute);
	}

	// Attribute helpers
	public void VisitExceptionHandler(ExceptionHandler helper) {}

	public void VisitInnerClass(InnerClass helper) {
		if ((helper.AccessFlag() & InnerClass.ACC_PUBLIC) != 0) {
			public_inner_classes.add(helper);
		} else if ((helper.AccessFlag() & InnerClass.ACC_PRIVATE) != 0) {
			private_inner_classes.add(helper);
		} else if ((helper.AccessFlag() & InnerClass.ACC_PROTECTED) != 0) {
			protected_inner_classes.add(helper);
		} else {
			package_inner_classes.add(helper);
		}

		if ((helper.AccessFlag() & InnerClass.ACC_STATIC) != 0) {
			static_inner_classes.add(helper);
		}

		if ((helper.AccessFlag() & InnerClass.ACC_FINAL) != 0) {
			final_inner_classes.add(helper);
		}

		if ((helper.AccessFlag() & InnerClass.ACC_INTERFACE) != 0) {
			interfaces.add(helper);
		} else {
			classes.add(helper);
		}

		if ((helper.AccessFlag() & InnerClass.ACC_ABSTRACT) != 0) {
			abstract_inner_classes.add(helper);
		}

	}

	public void VisitLineNumber(LineNumber helper) {}
	public void VisitLocalVariable(LocalVariable helper) {}
}
