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

	private int     sloc;
	private boolean is_synthetic;
	
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
		Logger.getLogger(getClass()).debug("VisitClassfile():");
		Logger.getLogger(getClass()).debug("    class = \"" + classfile.Class() + "\"");

		CurrentMethod(null);
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
			classfile.RawSuperclass().Accept(this);

			Classfile superclass = classfile.Loader().Classfile(classfile.Superclass());

			if (superclass != null) {
				MetricsFactory().CreateClassMetrics(superclass.Class()).AddToMeasurement(Metrics.SUBCLASSES);
			}
			CurrentClass().AddToMeasurement(Metrics.DEPTH_OF_INHERITANCE, ComputeDepthOfInheritance(superclass));
		}

		Iterator i;

		i = classfile.Interfaces().iterator();
		while (i.hasNext()) {
			((Class_info) i.next()).Accept(this);
		}

		i = classfile.Fields().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}

		i = classfile.Methods().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}

		sloc = 1;
		is_synthetic = false;

		i = classfile.Attributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
		
		if (!is_synthetic) {
			CurrentClass().AddToMeasurement(Metrics.CLASS_SLOC, sloc);
		}
	}
	
	// ConstantPool entries
	public void VisitClass_info(Class_info entry) {
		Logger.getLogger(getClass()).debug("VisitClass_info():");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.Name() + "\"");
		if (entry.Name().startsWith("[")) {
			AddClassDependencies(ProcessDescriptor(entry.Name()));
		} else {
			AddClassDependency(entry.Name());
		}
	}
	
	public void VisitFieldRef_info(FieldRef_info entry) {
		Logger.getLogger(getClass()).debug("VisitFieldRef_info():");
		Logger.getLogger(getClass()).debug("    class = \"" + entry.Class() + "\"");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.RawNameAndType().Name() + "\"");
		Logger.getLogger(getClass()).debug("    type = \"" + entry.RawNameAndType().Type() + "\"");

		// Dependencies on attributes are accounted as dependencies on their class
		entry.RawClass().Accept(this);
		AddClassDependencies(ProcessDescriptor(entry.RawNameAndType().Type()));
	}

	public void VisitMethodRef_info(MethodRef_info entry) {
		Logger.getLogger(getClass()).debug("VisitMethodRef_info():");
		Logger.getLogger(getClass()).debug("    class = \"" + entry.Class() + "\"");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.RawNameAndType().Name() + "\"");
		Logger.getLogger(getClass()).debug("    type = \"" + entry.RawNameAndType().Type() + "\"");
		AddMethodDependency(entry.FullSignature());
		AddClassDependencies(ProcessDescriptor(entry.RawNameAndType().Type()));
	}

	public void VisitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
		Logger.getLogger(getClass()).debug("VisitInterfaceMethodRef_info():");
		Logger.getLogger(getClass()).debug("    class = \"" + entry.Class() + "\"");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.RawNameAndType().Name() + "\"");
		Logger.getLogger(getClass()).debug("    type = \"" + entry.RawNameAndType().Type() + "\"");
		AddMethodDependency(entry.FullSignature());
		AddClassDependencies(ProcessDescriptor(entry.RawNameAndType().Type()));
	}

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

		sloc = 1;
		is_synthetic = false;
		
		super.VisitField_info(entry);
		
		if (!is_synthetic) {
			CurrentClass().AddToMeasurement(Metrics.CLASS_SLOC, sloc);
		}

		AddClassDependencies(ProcessDescriptor(entry.Descriptor()));
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
			sloc = 1;
		}

		CurrentMethod().AddToMeasurement(Metrics.PARAMETERS, SignatureHelper.ParameterCount(entry.Descriptor()));

		sloc = 0;
		is_synthetic = false;
		
		super.VisitMethod_info(entry);
		
		if (!is_synthetic) {
			CurrentMethod().AddToMeasurement(Metrics.SLOC, sloc);
		}

		AddClassDependencies(ProcessDescriptor(entry.Descriptor()));
	}

	// 
	// Attributes
	//

	public void VisitCode_attribute(Code_attribute attribute) {
		super.VisitCode_attribute(attribute);

		Logger.getLogger(getClass()).debug("Walking bytecode ...");

		byte[] code = attribute.Code();

		/*
		 *  We can skip the "new" (0xbb) instruction as it is always
		 *  followed by a call to the constructor method.
		 */
		
		Iterator ci = attribute.iterator();
		while (ci.hasNext()) {
			Instruction instr = (Instruction) ci.next();
			switch (instr.Opcode()) {
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
					int start = instr.Start();
					int index = ((code[start+1] & 0xff) << 8) | (code[start+2] & 0xff);
					((Visitable) attribute.Classfile().ConstantPool().get(index)).Accept(this);
					break;
				default:
					// Do nothing
					break;
			}
		}
	}

	public void VisitSynthetic_attribute(Synthetic_attribute attribute) {
		Object owner = attribute.Owner();

		is_synthetic = true;
		
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

	// 
	// Attribute helpers
	//
	
	public void VisitExceptionHandler(ExceptionHandler helper) {
		if (helper.CatchTypeIndex() != 0) {
			helper.RawCatchType().Accept(this);
		}
		
		// The lines in the catch{} block are caught in
		// the line number table.  This adds one for the
		// catch{} line itself.  Adding one for the try{
		// line will be difficult.
		sloc++;
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

	public void VisitLineNumber(LineNumber helper) {
		sloc++;
	}

	public void VisitLocalVariable(LocalVariable helper) {
		CurrentMethod().AddToMeasurement(Metrics.LOCAL_VARIABLES);

		AddClassDependencies(ProcessDescriptor(helper.Descriptor()));
	}

	private int ComputeDepthOfInheritance(Classfile classfile) {
		int result = 1;
		
		if (classfile != null && classfile.SuperclassIndex() != 0) {
			Classfile superclass = classfile.Loader().Classfile(classfile.Superclass());
			result += ComputeDepthOfInheritance(superclass);
		}

		return result;
	}
	
	private Collection ProcessDescriptor(String str) {
		Collection result = new LinkedList();
		
		Logger.getLogger(getClass()).debug("ProcessDescriptor: " + str);

		int current_pos = 0;
		int start_pos;
		int end_pos;

		while ((start_pos = str.indexOf('L', current_pos)) != -1) {
			if ((end_pos = str.indexOf(';', start_pos)) != -1) {
				String classname = SignatureHelper.Path2ClassName(str.substring(start_pos + 1, end_pos));
				result.add(classname);
				current_pos = end_pos + 1;
			} else {
				current_pos = start_pos + 1;
			}
		}

		Logger.getLogger(getClass()).debug("ProcessDescriptor: " + result);
		
		return result;
	}

	private void AddClassDependencies(Collection classnames) {
		Iterator i = classnames.iterator();
		while (i.hasNext()) {
			AddClassDependency((String) i.next());
		}
	}
	
	private void AddClassDependency(String name) {
		Logger.getLogger(getClass()).debug("AddClassDependency(\"" + name + "\") ...");

		if (!CurrentClass().Name().equals(name)) {
			Metrics other = MetricsFactory().CreateClassMetrics(name);
				
			if (CurrentMethod() != null) {
				Logger.getLogger(getClass()).debug("AddClassDependency " + CurrentMethod().Name() + " -> " + name + " ...");
				
				if (CurrentClass().Parent().equals(other.Parent())) {
					Logger.getLogger(getClass()).debug("Intra-Package ...");
					CurrentMethod().AddToMeasurement(Metrics.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES, other.Name());
					other.AddToMeasurement(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES, CurrentMethod().Name());
				} else {
					Logger.getLogger(getClass()).debug("Extra-Package ...");
					CurrentMethod().AddToMeasurement(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES, other.Name());
					other.AddToMeasurement(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES, CurrentMethod().Name());
				}
			} else {
				Logger.getLogger(getClass()).debug("AddClassDependency " + CurrentClass().Name() + " -> " + name + " ...");
				
				if (CurrentClass().Parent().equals(other.Parent())) {
					Logger.getLogger(getClass()).debug("Intra-Package ...");
					CurrentClass().AddToMeasurement(Metrics.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES, other.Name());
					other.AddToMeasurement(Metrics.INBOUND_INTRA_PACKAGE_DEPENDENCIES, CurrentClass().Name());
				} else {
					Logger.getLogger(getClass()).debug("Extra-Package ...");
					CurrentClass().AddToMeasurement(Metrics.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES, other.Name());
					other.AddToMeasurement(Metrics.INBOUND_EXTRA_PACKAGE_DEPENDENCIES, CurrentClass().Name());
				}
			}
		}
	}
	
	private void AddMethodDependency(String name) {
		Logger.getLogger(getClass()).debug("AddMethodDependency " + CurrentMethod().Name() + " -> " + name + " ...");

		if (!CurrentMethod().Name().equals(name)) {
			Metrics other = MetricsFactory().CreateMethodMetrics(name);
			
			if (CurrentClass().equals(other.Parent())) {
				Logger.getLogger(getClass()).debug("Intra-Class ...");
				CurrentMethod().AddToMeasurement(Metrics.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES, other.Name());
				other.AddToMeasurement(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES, CurrentMethod().Name());
			} else if (CurrentGroup().equals(other.Parent().Parent())) {
				Logger.getLogger(getClass()).debug("Intra-Package ...");
				CurrentMethod().AddToMeasurement(Metrics.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES, other.Name());
				other.AddToMeasurement(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES, CurrentMethod().Name());
			} else {
				Logger.getLogger(getClass()).debug("Extra-Package ...");
				CurrentMethod().AddToMeasurement(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES, other.Name());
				other.AddToMeasurement(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES, CurrentMethod().Name());
			}
		}
	}
}
