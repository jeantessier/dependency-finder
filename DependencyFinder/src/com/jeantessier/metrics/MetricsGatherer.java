/*
 *  Copyright (c) 2001-2004, Jean Tessier
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

/**
 *  Collects metrics from Classfile instances.
 *  
 *  This class can only approximate SLOC based on information provided
 *  by the compiler.
 */
public class MetricsGatherer extends VisitorBase {
	private String         project_name;
	private MetricsFactory factory;

	private Collection scope  = null;
	private Collection filter = null;
	
	private Metrics current_project;
	private Metrics current_group;
	private Metrics current_class;
	private Metrics current_method;

	private int     sloc;
	private boolean is_synthetic;
	
	private HashSet metrics_listeners = new HashSet();

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

	public void ScopeIncludes(Collection scope) {
		this.scope = scope;
	}
	
	public void FilterIncludes(Collection filter) {
		this.filter = filter;
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

	public void visitClassfiles(Collection classfiles) {
		fireBeginSession(classfiles.size());
		
		Iterator i = classfiles.iterator();
		while (i.hasNext()) {
			((Classfile) i.next()).accept(this);
		}
		
		fireEndSession();
	}
	
	// Classfile
	public void visitClassfile(Classfile classfile) {
		Logger.getLogger(getClass()).debug("VisitClassfile():");
		Logger.getLogger(getClass()).debug("    class = \"" + classfile.getClassName() + "\"");

		fireBeginClass(classfile);
		
		CurrentMethod(null);
		CurrentClass(MetricsFactory().CreateClassMetrics(classfile.getClassName()));
		CurrentGroup(CurrentClass().Parent());
		CurrentProject(CurrentGroup().Parent());

		MetricsFactory().IncludeClassMetrics(CurrentClass());

		CurrentProject().AddToMeasurement(Metrics.PACKAGES, CurrentGroup().Name());
		
		if ((classfile.getAccessFlag() & Classfile.ACC_PUBLIC) != 0) {
			CurrentProject().AddToMeasurement(Metrics.PUBLIC_CLASSES);
			CurrentGroup().AddToMeasurement(Metrics.PUBLIC_CLASSES);
		}

		if ((classfile.getAccessFlag() & Classfile.ACC_FINAL) != 0) {
			CurrentProject().AddToMeasurement(Metrics.FINAL_CLASSES);
			CurrentGroup().AddToMeasurement(Metrics.FINAL_CLASSES);
		}

		if ((classfile.getAccessFlag() & Classfile.ACC_INTERFACE) != 0) {
			CurrentProject().AddToMeasurement(Metrics.INTERFACES);
			CurrentGroup().AddToMeasurement(Metrics.INTERFACES);
		}

		if ((classfile.getAccessFlag() & Classfile.ACC_ABSTRACT) != 0) {
			CurrentProject().AddToMeasurement(Metrics.ABSTRACT_CLASSES);
			CurrentGroup().AddToMeasurement(Metrics.ABSTRACT_CLASSES);
		}

		if (classfile.getSuperclassIndex() != 0) {
			classfile.getRawSuperclass().accept(this);

			Classfile superclass = classfile.getLoader().getClassfile(classfile.getSuperclassName());

			if (superclass != null) {
				MetricsFactory().CreateClassMetrics(superclass.getClassName()).AddToMeasurement(Metrics.SUBCLASSES);
			}
			CurrentClass().AddToMeasurement(Metrics.DEPTH_OF_INHERITANCE, ComputeDepthOfInheritance(superclass));
		}

		Iterator i;

		i = classfile.getAllInterfaces().iterator();
		while (i.hasNext()) {
			((Class_info) i.next()).accept(this);
		}

		i = classfile.getAllFields().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).accept(this);
		}

		i = classfile.getAllMethods().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).accept(this);
		}

		sloc = 1;
		is_synthetic = false;

		i = classfile.getAttributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).accept(this);
		}
		
		if (!is_synthetic) {
			CurrentClass().AddToMeasurement(Metrics.CLASS_SLOC, sloc);
		}

		fireEndClass(classfile, CurrentClass());
	}
	
	// ConstantPool entries
	public void visitClass_info(Class_info entry) {
		Logger.getLogger(getClass()).debug("VisitClass_info():");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.getName() + "\"");
		if (entry.getName().startsWith("[")) {
			AddClassDependencies(ProcessDescriptor(entry.getName()));
		} else {
			AddClassDependency(entry.getName());
		}
	}
	
	public void visitFieldRef_info(FieldRef_info entry) {
		Logger.getLogger(getClass()).debug("VisitFieldRef_info():");
		Logger.getLogger(getClass()).debug("    class = \"" + entry.Class() + "\"");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.getRawNameAndType().Name() + "\"");
		Logger.getLogger(getClass()).debug("    type = \"" + entry.getRawNameAndType().getType() + "\"");

		// Dependencies on attributes are accounted as dependencies on their class
		entry.getRawClass().accept(this);
		AddClassDependencies(ProcessDescriptor(entry.getRawNameAndType().getType()));
	}

	public void visitMethodRef_info(MethodRef_info entry) {
		Logger.getLogger(getClass()).debug("VisitMethodRef_info():");
		Logger.getLogger(getClass()).debug("    class = \"" + entry.Class() + "\"");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.getRawNameAndType().Name() + "\"");
		Logger.getLogger(getClass()).debug("    type = \"" + entry.getRawNameAndType().getType() + "\"");
		AddMethodDependency(entry.getFullSignature());
		AddClassDependencies(ProcessDescriptor(entry.getRawNameAndType().getType()));
	}

	public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
		Logger.getLogger(getClass()).debug("VisitInterfaceMethodRef_info():");
		Logger.getLogger(getClass()).debug("    class = \"" + entry.Class() + "\"");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.getRawNameAndType().Name() + "\"");
		Logger.getLogger(getClass()).debug("    type = \"" + entry.getRawNameAndType().getType() + "\"");
		AddMethodDependency(entry.getFullSignature());
		AddClassDependencies(ProcessDescriptor(entry.getRawNameAndType().getType()));
	}

	public void visitField_info(Field_info entry) {
		CurrentClass().AddToMeasurement(Metrics.ATTRIBUTES);

		Logger.getLogger(getClass()).debug("VisitField_info(" + entry.getFullSignature() + ")");
		Logger.getLogger(getClass()).debug("Current class: " + CurrentClass().Name());
		Logger.getLogger(getClass()).debug("Access flag: " + entry.getAccessFlag());
		Logger.getLogger(getClass()).debug("Public: " + (entry.getAccessFlag() & Method_info.ACC_PUBLIC));
		Logger.getLogger(getClass()).debug("Private: " + (entry.getAccessFlag() & Method_info.ACC_PRIVATE));
		Logger.getLogger(getClass()).debug("Protected: " + (entry.getAccessFlag() & Method_info.ACC_PROTECTED));
		Logger.getLogger(getClass()).debug("Static: " + (entry.getAccessFlag() & Method_info.ACC_STATIC));
		
		if ((entry.getAccessFlag() & Field_info.ACC_PUBLIC) != 0) {
			CurrentClass().AddToMeasurement(Metrics.PUBLIC_ATTRIBUTES);
		} else if ((entry.getAccessFlag() & Field_info.ACC_PRIVATE) != 0) {
			CurrentClass().AddToMeasurement(Metrics.PRIVATE_ATTRIBUTES);
		} else if ((entry.getAccessFlag() & Field_info.ACC_PROTECTED) != 0) {
			CurrentClass().AddToMeasurement(Metrics.PROTECTED_ATTRIBUTES);
		} else {
			CurrentClass().AddToMeasurement(Metrics.PACKAGE_ATTRIBUTES);
		}

		if ((entry.getAccessFlag() & Field_info.ACC_STATIC) != 0) {
			CurrentClass().AddToMeasurement(Metrics.STATIC_ATTRIBUTES);
		}

		if ((entry.getAccessFlag() & Field_info.ACC_FINAL) != 0) {
			CurrentClass().AddToMeasurement(Metrics.FINAL_ATTRIBUTES);
		}

		if ((entry.getAccessFlag() & Field_info.ACC_VOLATILE) != 0) {
			CurrentClass().AddToMeasurement(Metrics.VOLATILE_ATTRIBUTES);
		}

		if ((entry.getAccessFlag() & Field_info.ACC_TRANSIENT) != 0) {
			CurrentClass().AddToMeasurement(Metrics.TRANSIENT_ATTRIBUTES);
		}

		sloc = 1;
		is_synthetic = false;
		
		super.visitField_info(entry);
		
		if (!is_synthetic) {
			CurrentClass().AddToMeasurement(Metrics.CLASS_SLOC, sloc);
		}

		AddClassDependencies(ProcessDescriptor(entry.getDescriptor()));
	}

	public void visitMethod_info(Method_info entry) {
		fireBeginMethod(entry);
		
		CurrentMethod(MetricsFactory().CreateMethodMetrics(entry.getFullSignature()));
		MetricsFactory().IncludeMethodMetrics(CurrentMethod());
		
		Logger.getLogger(getClass()).debug("VisitMethod_info(" + entry.getFullSignature() + ")");
		Logger.getLogger(getClass()).debug("Current class: " + CurrentClass().Name());
		Logger.getLogger(getClass()).debug("Access flag: " + entry.getAccessFlag());
		Logger.getLogger(getClass()).debug("Public: " + (entry.getAccessFlag() & Method_info.ACC_PUBLIC));
		Logger.getLogger(getClass()).debug("Private: " + (entry.getAccessFlag() & Method_info.ACC_PRIVATE));
		Logger.getLogger(getClass()).debug("Protected: " + (entry.getAccessFlag() & Method_info.ACC_PROTECTED));
		Logger.getLogger(getClass()).debug("Static: " + (entry.getAccessFlag() & Method_info.ACC_STATIC));

		sloc = 0;
		is_synthetic = false;
		
		if ((entry.getAccessFlag() & Method_info.ACC_PUBLIC) != 0) {
			CurrentClass().AddToMeasurement(Metrics.PUBLIC_METHODS);
		} else if ((entry.getAccessFlag() & Method_info.ACC_PRIVATE) != 0) {
			CurrentClass().AddToMeasurement(Metrics.PRIVATE_METHODS);
		} else if ((entry.getAccessFlag() & Method_info.ACC_PROTECTED) != 0) {
			CurrentClass().AddToMeasurement(Metrics.PROTECTED_METHODS);
		} else {
			CurrentClass().AddToMeasurement(Metrics.PACKAGE_METHODS);
		}

		if ((entry.getAccessFlag() & Method_info.ACC_STATIC) != 0) {
			CurrentClass().AddToMeasurement(Metrics.STATIC_METHODS);
		}

		if ((entry.getAccessFlag() & Method_info.ACC_FINAL) != 0) {
			CurrentClass().AddToMeasurement(Metrics.FINAL_METHODS);
		}

		if ((entry.getAccessFlag() & Method_info.ACC_SYNCHRONIZED) != 0) {
			CurrentClass().AddToMeasurement(Metrics.SYNCHRONIZED_METHODS);
		}

		if ((entry.getAccessFlag() & Method_info.ACC_NATIVE) != 0) {
			CurrentClass().AddToMeasurement(Metrics.NATIVE_METHODS);
		}

		if ((entry.getAccessFlag() & Method_info.ACC_ABSTRACT) != 0) {
			CurrentClass().AddToMeasurement(Metrics.ABSTRACT_METHODS);
			sloc = 1;
		}

		CurrentMethod().AddToMeasurement(Metrics.PARAMETERS, SignatureHelper.getParameterCount(entry.getDescriptor()));
		
		super.visitMethod_info(entry);
		
		if (!is_synthetic) {
			CurrentMethod().AddToMeasurement(Metrics.SLOC, sloc);
		}

		AddClassDependencies(ProcessDescriptor(entry.getDescriptor()));

		fireEndMethod(entry, CurrentMethod());
	}

	// 
	// Attributes
	//

	public void visitCode_attribute(Code_attribute attribute) {
		super.visitCode_attribute(attribute);

		Logger.getLogger(getClass()).debug("Walking bytecode ...");

		byte[] code = attribute.getCode();

		/*
		 *  We can skip the "new" (0xbb) instruction as it is always
		 *  followed by a call to the constructor method.
		 */
		
		Iterator ci = attribute.iterator();
		while (ci.hasNext()) {
			Instruction instr = (Instruction) ci.next();
			switch (instr.getOpcode()) {
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
					int start = instr.getStart();
					int index = ((code[start+1] & 0xff) << 8) | (code[start+2] & 0xff);
					((Visitable) attribute.getClassfile().getConstantPool().get(index)).accept(this);
					break;
				default:
					// Do nothing
					break;
			}
		}
	}

	public void visitSynthetic_attribute(Synthetic_attribute attribute) {
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

	public void visitDeprecated_attribute(Deprecated_attribute attribute) {
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
	
	public void visitExceptionHandler(ExceptionHandler helper) {
		if (helper.getCatchTypeIndex() != 0) {
			helper.getRawCatchType().accept(this);
		}
	}

	public void visitInnerClass(InnerClass helper) {
		if ((helper.getInnerClassInfoIndex() != helper.InnerClasses().getClassfile().getClassIndex()) && (helper.InnerClassInfo().startsWith(helper.InnerClasses().getClassfile().getClassName()))) {
			CurrentProject().AddToMeasurement(Metrics.INNER_CLASSES);
			CurrentGroup().AddToMeasurement(Metrics.INNER_CLASSES);
			CurrentClass().AddToMeasurement(Metrics.INNER_CLASSES);
		
			if ((helper.getAccessFlag() & InnerClass.ACC_PUBLIC) != 0) {
				CurrentProject().AddToMeasurement(Metrics.PUBLIC_INNER_CLASSES);
				CurrentGroup().AddToMeasurement(Metrics.PUBLIC_INNER_CLASSES);
				CurrentClass().AddToMeasurement(Metrics.PUBLIC_INNER_CLASSES);
			} else if ((helper.getAccessFlag() & InnerClass.ACC_PRIVATE) != 0) {
				CurrentProject().AddToMeasurement(Metrics.PRIVATE_INNER_CLASSES);
				CurrentGroup().AddToMeasurement(Metrics.PRIVATE_INNER_CLASSES);
				CurrentClass().AddToMeasurement(Metrics.PRIVATE_INNER_CLASSES);
			} else if ((helper.getAccessFlag() & InnerClass.ACC_PROTECTED) != 0) {
				CurrentProject().AddToMeasurement(Metrics.PROTECTED_INNER_CLASSES);
				CurrentGroup().AddToMeasurement(Metrics.PROTECTED_INNER_CLASSES);
				CurrentClass().AddToMeasurement(Metrics.PROTECTED_INNER_CLASSES);
			} else {
				CurrentProject().AddToMeasurement(Metrics.PACKAGE_INNER_CLASSES);
				CurrentGroup().AddToMeasurement(Metrics.PACKAGE_INNER_CLASSES);
				CurrentClass().AddToMeasurement(Metrics.PACKAGE_INNER_CLASSES);
			}

			if ((helper.getAccessFlag() & InnerClass.ACC_STATIC) != 0) {
				CurrentProject().AddToMeasurement(Metrics.STATIC_INNER_CLASSES);
				CurrentGroup().AddToMeasurement(Metrics.STATIC_INNER_CLASSES);
				CurrentClass().AddToMeasurement(Metrics.STATIC_INNER_CLASSES);
			}

			if ((helper.getAccessFlag() & InnerClass.ACC_FINAL) != 0) {
				CurrentProject().AddToMeasurement(Metrics.FINAL_INNER_CLASSES);
				CurrentGroup().AddToMeasurement(Metrics.FINAL_INNER_CLASSES);
				CurrentClass().AddToMeasurement(Metrics.FINAL_INNER_CLASSES);
			}

			if ((helper.getAccessFlag() & InnerClass.ACC_ABSTRACT) != 0) {
				CurrentProject().AddToMeasurement(Metrics.ABSTRACT_INNER_CLASSES);
				CurrentGroup().AddToMeasurement(Metrics.ABSTRACT_INNER_CLASSES);
				CurrentClass().AddToMeasurement(Metrics.ABSTRACT_INNER_CLASSES);
			}
		}
	}

	public void visitLineNumber(LineNumber helper) {
		sloc++;
	}

	public void visitLocalVariable(LocalVariable helper) {
		CurrentMethod().AddToMeasurement(Metrics.LOCAL_VARIABLES);

		AddClassDependencies(ProcessDescriptor(helper.getDescriptor()));
	}

	private int ComputeDepthOfInheritance(Classfile classfile) {
		int result = 1;
		
		if (classfile != null && classfile.getSuperclassIndex() != 0) {
			Classfile superclass = classfile.getLoader().getClassfile(classfile.getSuperclassName());
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
				String classname = SignatureHelper.path2ClassName(str.substring(start_pos + 1, end_pos));
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

		if (!CurrentClass().Name().equals(name) && Filter(name)) {
			Metrics other = MetricsFactory().CreateClassMetrics(name);
				
			if (CurrentMethod() != null && Scope(CurrentMethod().Name())) {
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
			} else if (Scope(CurrentClass().Name())) {
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

		if (!CurrentMethod().Name().equals(name) && Scope(CurrentMethod().Name()) && Filter(name)) {
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
	
	private boolean Scope(String name) {
		boolean result = true;

		if (scope != null) {
			result = scope.contains(name);
		}

		return result;
	}
	
	private boolean Filter(String name) {
		boolean result = true;

		if (filter != null) {
			result = filter.contains(name);
		}

		return result;
	}

	public void addMetricsListener(MetricsListener listener) {
		synchronized(metrics_listeners) {
			metrics_listeners.add(listener);
		}
	}

	public void removeMetricsListener(MetricsListener listener) {
		synchronized(metrics_listeners) {
			metrics_listeners.remove(listener);
		}
	}

	protected void fireBeginSession(int size) {
		MetricsEvent event = new MetricsEvent(this, size);

		HashSet listeners;
		synchronized(metrics_listeners) {
			listeners = (HashSet) metrics_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((MetricsListener) i.next()).BeginSession(event);
		}
	}

	protected void fireBeginClass(Classfile classfile) {
		MetricsEvent event = new MetricsEvent(this, classfile);

		HashSet listeners;
		synchronized(metrics_listeners) {
			listeners = (HashSet) metrics_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((MetricsListener) i.next()).BeginClass(event);
		}
	}

	protected void fireBeginMethod(Method_info method) {
		MetricsEvent event = new MetricsEvent(this, method);

		HashSet listeners;
		synchronized(metrics_listeners) {
			listeners = (HashSet) metrics_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((MetricsListener) i.next()).BeginMethod(event);
		}
	}

	protected void fireEndMethod(Method_info method, Metrics metrics) {
		MetricsEvent event = new MetricsEvent(this, method, metrics);

		HashSet listeners;
		synchronized(metrics_listeners) {
			listeners = (HashSet) metrics_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((MetricsListener) i.next()).EndMethod(event);
		}
	}

	protected void fireEndClass(Classfile classfile, Metrics metrics) {
		MetricsEvent event = new MetricsEvent(this, classfile, metrics);

		HashSet listeners;
		synchronized(metrics_listeners) {
			listeners = (HashSet) metrics_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((MetricsListener) i.next()).EndClass(event);
		}
	}

	protected void fireEndSession() {
		MetricsEvent event = new MetricsEvent(this);

		HashSet listeners;
		synchronized(metrics_listeners) {
			listeners = (HashSet) metrics_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((MetricsListener) i.next()).EndSession(event);
		}
	}
}
