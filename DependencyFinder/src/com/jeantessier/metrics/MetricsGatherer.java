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
 *  	* Neither the name of Jean Tessier nor the names of his contributors
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

package com.jeantessier.metrics;

import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

public class MetricsGatherer extends VisitorBase {
	private String         project_name;
	private MetricsFactory factory;

	private Metrics current_project;
	private Metrics current_group;
	private Metrics current_class;
	private Metrics current_method;

	public MetricsGatherer(String project_name, MetricsFactory factory) {
		this.project_name = project_name;
		this.factory      = factory;

		CurrentProject(MetricsFactory().CreateProjectMetrics(ProjectName()));
	}

	public String ProjectName() {
		return project_name;
	}
	
	public MetricsFactory MetricsFactory() {
		return factory;
	}

	private Metrics CurrentProject() {
		return current_project;
	}

	private void CurrentProject(Metrics current_project) {
		this.current_project = current_project;
	}

	private Metrics CurrentGroup() {
		return current_group;
	}

	private void CurrentGroup(Metrics current_group) {
		this.current_group = current_group;
	}

	private Metrics CurrentClass() {
		return current_class;
	}

	private void CurrentClass(Metrics current_class) {
		this.current_class = current_class;
	}

	private Metrics CurrentMethod() {
		return current_method;
	}

	private void CurrentMethod(Metrics current_method) {
		this.current_method = current_method;
	}
	
	// Classfile
	public void VisitClassfile(Classfile classfile) {
		CurrentClass(MetricsFactory().CreateClassMetrics(classfile.Class()));
		CurrentGroup(CurrentClass().Parent());
		CurrentProject(CurrentGroup().Parent());

		CurrentProject().AddToMeasurement(Metrics.PACKAGES, CurrentGroup().Name());
		
		if ((classfile.AccessFlag() & Classfile.ACC_PUBLIC) != 0) {
			CurrentProject().AddToMeasurement(Metrics.PUBLIC_CLASSES);
			CurrentGroup().AddToMeasurement(Metrics.PUBLIC_CLASSES);
		}

		if ((classfile.AccessFlag() & Classfile.ACC_FINAL) != 0) {
			CurrentProject().AddToMeasurement(Metrics.FINAL_CLASSES);
			CurrentGroup().AddToMeasurement(Metrics.FINAL_CLASSES);
		}

		if ((classfile.AccessFlag() & Classfile.ACC_INTERFACE) != 0) {
			CurrentProject().AddToMeasurement(Metrics.INTERFACES);
			CurrentGroup().AddToMeasurement(Metrics.INTERFACES);
		}

		if ((classfile.AccessFlag() & Classfile.ACC_ABSTRACT) != 0) {
			CurrentProject().AddToMeasurement(Metrics.ABSTRACT_CLASSES);
			CurrentGroup().AddToMeasurement(Metrics.ABSTRACT_CLASSES);
		}

		if (classfile.SuperclassIndex() != 0) {
			Classfile superclass = classfile.Loader().Classfile(classfile.Superclass());

			if (superclass != null) {
				MetricsFactory().CreateClassMetrics(superclass.Class()).AddToMeasurement(Metrics.SUBCLASSES);
			}
			CurrentClass().AddToMeasurement(Metrics.DEPTH_OF_INHERITANCE, ComputeDepthOfInheritance(superclass));
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

	private int ComputeDepthOfInheritance(Classfile classfile) {
		int result = 1;
		
		if (classfile != null && classfile.SuperclassIndex() != 0) {
			Classfile superclass = classfile.Loader().Classfile(classfile.Superclass());
			result += ComputeDepthOfInheritance(superclass);
		}

		return result;
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
		CurrentClass().AddToMeasurement(Metrics.ATTRIBUTES);

		Logger.getLogger(getClass()).debug("VisitField_info(" + entry.FullSignature() + ")");
		Logger.getLogger(getClass()).debug("Current class: " + CurrentClass().Name());
		Logger.getLogger(getClass()).debug("Access flag: " + entry.AccessFlag());
		Logger.getLogger(getClass()).debug("Public: " + (entry.AccessFlag() & Method_info.ACC_PUBLIC));
		Logger.getLogger(getClass()).debug("Private: " + (entry.AccessFlag() & Method_info.ACC_PRIVATE));
		Logger.getLogger(getClass()).debug("Protected: " + (entry.AccessFlag() & Method_info.ACC_PROTECTED));
		Logger.getLogger(getClass()).debug("Static: " + (entry.AccessFlag() & Method_info.ACC_STATIC));
		
		if ((entry.AccessFlag() & Field_info.ACC_PUBLIC) != 0) {
			CurrentClass().AddToMeasurement(Metrics.PUBLIC_ATTRIBUTES);
		} else if ((entry.AccessFlag() & Field_info.ACC_PRIVATE) != 0) {
			CurrentClass().AddToMeasurement(Metrics.PRIVATE_ATTRIBUTES);
		} else if ((entry.AccessFlag() & Field_info.ACC_PROTECTED) != 0) {
			CurrentClass().AddToMeasurement(Metrics.PROTECTED_ATTRIBUTES);
		} else {
			CurrentClass().AddToMeasurement(Metrics.PACKAGE_ATTRIBUTES);
		}

		if ((entry.AccessFlag() & Field_info.ACC_STATIC) != 0) {
			CurrentClass().AddToMeasurement(Metrics.STATIC_ATTRIBUTES);
		}

		if ((entry.AccessFlag() & Field_info.ACC_FINAL) != 0) {
			CurrentClass().AddToMeasurement(Metrics.FINAL_ATTRIBUTES);
		}

		if ((entry.AccessFlag() & Field_info.ACC_VOLATILE) != 0) {
			CurrentClass().AddToMeasurement(Metrics.VOLATILE_ATTRIBUTES);
		}

		if ((entry.AccessFlag() & Field_info.ACC_TRANSIENT) != 0) {
			CurrentClass().AddToMeasurement(Metrics.TRANSIENT_ATTRIBUTES);
		}

		Iterator i = entry.Attributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
	}

	public void VisitMethod_info(Method_info entry) {
		CurrentMethod(MetricsFactory().CreateMethodMetrics(entry.FullSignature()));

		Logger.getLogger(getClass()).debug("VisitMethod_info(" + entry.FullSignature() + ")");
		Logger.getLogger(getClass()).debug("Current class: " + CurrentClass().Name());
		Logger.getLogger(getClass()).debug("Access flag: " + entry.AccessFlag());
		Logger.getLogger(getClass()).debug("Public: " + (entry.AccessFlag() & Method_info.ACC_PUBLIC));
		Logger.getLogger(getClass()).debug("Private: " + (entry.AccessFlag() & Method_info.ACC_PRIVATE));
		Logger.getLogger(getClass()).debug("Protected: " + (entry.AccessFlag() & Method_info.ACC_PROTECTED));
		Logger.getLogger(getClass()).debug("Static: " + (entry.AccessFlag() & Method_info.ACC_STATIC));
		
		if ((entry.AccessFlag() & Method_info.ACC_PUBLIC) != 0) {
			CurrentClass().AddToMeasurement(Metrics.PUBLIC_METHODS);
		} else if ((entry.AccessFlag() & Method_info.ACC_PRIVATE) != 0) {
			CurrentClass().AddToMeasurement(Metrics.PRIVATE_METHODS);
		} else if ((entry.AccessFlag() & Method_info.ACC_PROTECTED) != 0) {
			CurrentClass().AddToMeasurement(Metrics.PROTECTED_METHODS);
		} else {
			CurrentClass().AddToMeasurement(Metrics.PACKAGE_METHODS);
		}

		if ((entry.AccessFlag() & Method_info.ACC_STATIC) != 0) {
			CurrentClass().AddToMeasurement(Metrics.STATIC_METHODS);
		}

		if ((entry.AccessFlag() & Method_info.ACC_FINAL) != 0) {
			CurrentClass().AddToMeasurement(Metrics.FINAL_METHODS);
		}

		if ((entry.AccessFlag() & Method_info.ACC_SYNCHRONIZED) != 0) {
			CurrentClass().AddToMeasurement(Metrics.SYNCHRONIZED_METHODS);
		}

		if ((entry.AccessFlag() & Method_info.ACC_NATIVE) != 0) {
			CurrentClass().AddToMeasurement(Metrics.NATIVE_METHODS);
		}

		if ((entry.AccessFlag() & Method_info.ACC_ABSTRACT) != 0) {
			CurrentClass().AddToMeasurement(Metrics.ABSTRACT_METHODS);
		}

		CurrentMethod().AddToMeasurement(Metrics.PARAMETERS, SignatureHelper.ParameterCount(entry.Descriptor()));
		
		Iterator i = entry.Attributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
	}

	// 
	// Attributes
	//
	
	public void VisitConstantValue_attribute(ConstantValue_attribute attribute) {
		// Do nothing
	}

// 	public void VisitCode_attribute(Code_attribute attribute) {
// 		Iterator i = attribute.Attributes().iterator();
// 		while (i.hasNext()) {
// 			((Visitable) i.next()).Accept(this);
// 		}
// 	}

	public void VisitExceptions_attribute(Exceptions_attribute attribute) {
		// Do nothing
	}

	public void VisitInnerClasses_attribute(InnerClasses_attribute attribute) {
		Iterator i = attribute.Classes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
	}

	public void VisitSynthetic_attribute(Synthetic_attribute attribute) {
		Object owner = attribute.Owner();
	
		if (owner instanceof Classfile) {
			CurrentProject().AddToMeasurement(Metrics.SYNTHETIC_CLASSES);
			CurrentGroup().AddToMeasurement(Metrics.SYNTHETIC_CLASSES);
		} else if (owner instanceof Field_info) {
			CurrentClass().AddToMeasurement(Metrics.SYNTHETIC_ATTRIBUTES);
		} else if (owner instanceof Method_info) {
			CurrentClass().AddToMeasurement(Metrics.SYNTHETIC_METHODS);
		} else {
			Logger.getLogger(getClass()).warn("Synthetic attribute on unknown Visitable: " + owner.getClass().getName());
		}
	}

	public void VisitSourceFile_attribute(SourceFile_attribute attribute) {
		// Do nothing
	}

	public void VisitLineNumberTable_attribute(LineNumberTable_attribute attribute) {
		CurrentMethod().AddToMeasurement(Metrics.SLOC, attribute.LineNumbers().size());
	}

	public void VisitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
		CurrentMethod().AddToMeasurement(Metrics.LOCAL_VARIABLES, attribute.LocalVariables().size());
	}

	public void VisitDeprecated_attribute(Deprecated_attribute attribute) {
		Object owner = attribute.Owner();
	
		if (owner instanceof Classfile) {
			CurrentProject().AddToMeasurement(Metrics.DEPRECATED_CLASSES);
			CurrentGroup().AddToMeasurement(Metrics.DEPRECATED_CLASSES);
		} else if (owner instanceof Field_info) {
			CurrentClass().AddToMeasurement(Metrics.DEPRECATED_ATTRIBUTES);
		} else if (owner instanceof Method_info) {
			CurrentClass().AddToMeasurement(Metrics.DEPRECATED_METHODS);
		} else {
			Logger.getLogger(getClass()).warn("Deprecated attribute on unknown Visitable: " + owner.getClass().getName());
		}
	}

	public void VisitCustom_attribute(Custom_attribute attribute) {
		// Do nothing
	}

	// 
	// Attribute helpers
	//
	
	public void VisitExceptionHandler(ExceptionHandler helper) {
		// The lines in the catch{} block are caught in
		// the line number table.  This adds one for the
		// catch{} line itself.  Adding one for the try{
		// line will be difficult.
		CurrentMethod().AddToMeasurement(Metrics.SLOC);
	}

	public void VisitInnerClass(InnerClass helper) {
		if ((helper.InnerClassInfoIndex() != helper.InnerClasses().Classfile().ClassIndex()) && (helper.InnerClassInfo().startsWith(helper.InnerClasses().Classfile().Class()))) {
			CurrentProject().AddToMeasurement(Metrics.INNER_CLASSES);
			CurrentGroup().AddToMeasurement(Metrics.INNER_CLASSES);
			CurrentClass().AddToMeasurement(Metrics.INNER_CLASSES);
		
			if ((helper.AccessFlag() & InnerClass.ACC_PUBLIC) != 0) {
				CurrentProject().AddToMeasurement(Metrics.PUBLIC_INNER_CLASSES);
				CurrentGroup().AddToMeasurement(Metrics.PUBLIC_INNER_CLASSES);
				CurrentClass().AddToMeasurement(Metrics.PUBLIC_INNER_CLASSES);
			} else if ((helper.AccessFlag() & InnerClass.ACC_PRIVATE) != 0) {
				CurrentProject().AddToMeasurement(Metrics.PRIVATE_INNER_CLASSES);
				CurrentGroup().AddToMeasurement(Metrics.PRIVATE_INNER_CLASSES);
				CurrentClass().AddToMeasurement(Metrics.PRIVATE_INNER_CLASSES);
			} else if ((helper.AccessFlag() & InnerClass.ACC_PROTECTED) != 0) {
				CurrentProject().AddToMeasurement(Metrics.PROTECTED_INNER_CLASSES);
				CurrentGroup().AddToMeasurement(Metrics.PROTECTED_INNER_CLASSES);
				CurrentClass().AddToMeasurement(Metrics.PROTECTED_INNER_CLASSES);
			} else {
				CurrentProject().AddToMeasurement(Metrics.PACKAGE_INNER_CLASSES);
				CurrentGroup().AddToMeasurement(Metrics.PACKAGE_INNER_CLASSES);
				CurrentClass().AddToMeasurement(Metrics.PACKAGE_INNER_CLASSES);
			}

			if ((helper.AccessFlag() & InnerClass.ACC_STATIC) != 0) {
				CurrentProject().AddToMeasurement(Metrics.STATIC_INNER_CLASSES);
				CurrentGroup().AddToMeasurement(Metrics.STATIC_INNER_CLASSES);
				CurrentClass().AddToMeasurement(Metrics.STATIC_INNER_CLASSES);
			}

			if ((helper.AccessFlag() & InnerClass.ACC_FINAL) != 0) {
				CurrentProject().AddToMeasurement(Metrics.FINAL_INNER_CLASSES);
				CurrentGroup().AddToMeasurement(Metrics.FINAL_INNER_CLASSES);
				CurrentClass().AddToMeasurement(Metrics.FINAL_INNER_CLASSES);
			}

			if ((helper.AccessFlag() & InnerClass.ACC_ABSTRACT) != 0) {
				CurrentProject().AddToMeasurement(Metrics.ABSTRACT_INNER_CLASSES);
				CurrentGroup().AddToMeasurement(Metrics.ABSTRACT_INNER_CLASSES);
				CurrentClass().AddToMeasurement(Metrics.ABSTRACT_INNER_CLASSES);
			}
		}
	}
}
