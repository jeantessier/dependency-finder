/*
 *  Copyright (c) 2001-2005, Jean Tessier
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
	private String         projectName;
	private MetricsFactory factory;

	private Collection scope  = null;
	private Collection filter = null;
	
	private Metrics currentProject;
	private Metrics currentGroup;
	private Metrics currentClass;
	private Metrics currentMethod;

	private int     sloc;
	private boolean isSynthetic;
	
	private HashSet metricsListeners = new HashSet();

	public MetricsGatherer(String projectName, MetricsFactory factory) {
		this.projectName = projectName;
		this.factory     = factory;

		setCurrentProject(getMetricsFactory().createProjectMetrics(getProjectName()));
	}

	public String getProjectName() {
		return projectName;
	}
	
	public MetricsFactory getMetricsFactory() {
		return factory;
	}

	public void setScopeIncludes(Collection scope) {
		this.scope = scope;
	}
	
	public void setFilterIncludes(Collection filter) {
		this.filter = filter;
	}
	
	private Metrics getCurrentProject() {
		return currentProject;
	}

	private void setCurrentProject(Metrics currentProject) {
		this.currentProject = currentProject;
	}

	private Metrics getCurrentGroup() {
		return currentGroup;
	}

	private void setCurrentGroup(Metrics currentGroup) {
		this.currentGroup = currentGroup;
	}

	private Metrics getCurrentClass() {
		return currentClass;
	}

	private void setCurrentClass(Metrics currentClass) {
		this.currentClass = currentClass;
	}

	private Metrics getCurrentMethod() {
		return currentMethod;
	}

	private void setCurrentMethod(Metrics currentMethod) {
		this.currentMethod = currentMethod;
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
		
		setCurrentMethod(null);
		setCurrentClass(getMetricsFactory().createClassMetrics(classfile.getClassName()));
		setCurrentGroup(getCurrentClass().getParent());
		setCurrentProject(getCurrentGroup().getParent());

		getMetricsFactory().includeClassMetrics(getCurrentClass());

		getCurrentProject().addToMeasurement(Metrics.PACKAGES, getCurrentGroup().getName());
		
		if ((classfile.getAccessFlag() & Classfile.ACC_PUBLIC) != 0) {
			getCurrentProject().addToMeasurement(Metrics.PUBLIC_CLASSES);
			getCurrentGroup().addToMeasurement(Metrics.PUBLIC_CLASSES);
		}

		if ((classfile.getAccessFlag() & Classfile.ACC_FINAL) != 0) {
			getCurrentProject().addToMeasurement(Metrics.FINAL_CLASSES);
			getCurrentGroup().addToMeasurement(Metrics.FINAL_CLASSES);
		}

		if ((classfile.getAccessFlag() & Classfile.ACC_INTERFACE) != 0) {
			getCurrentProject().addToMeasurement(Metrics.INTERFACES);
			getCurrentGroup().addToMeasurement(Metrics.INTERFACES);
		}

		if ((classfile.getAccessFlag() & Classfile.ACC_ABSTRACT) != 0) {
			getCurrentProject().addToMeasurement(Metrics.ABSTRACT_CLASSES);
			getCurrentGroup().addToMeasurement(Metrics.ABSTRACT_CLASSES);
		}

		if (classfile.getSuperclassIndex() != 0) {
			classfile.getRawSuperclass().accept(this);

			Classfile superclass = classfile.getLoader().getClassfile(classfile.getSuperclassName());

			if (superclass != null) {
				getMetricsFactory().createClassMetrics(superclass.getClassName()).addToMeasurement(Metrics.SUBCLASSES);
			}
			getCurrentClass().addToMeasurement(Metrics.DEPTH_OF_INHERITANCE, computeDepthOfInheritance(superclass));
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
		isSynthetic = false;

		i = classfile.getAttributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).accept(this);
		}
		
		if (!isSynthetic) {
			getCurrentClass().addToMeasurement(Metrics.CLASS_SLOC, sloc);
		}

		fireEndClass(classfile, getCurrentClass());
	}
	
	// ConstantPool entries
	public void visitClass_info(Class_info entry) {
		Logger.getLogger(getClass()).debug("VisitClass_info():");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.getName() + "\"");
		if (entry.getName().startsWith("[")) {
			addClassDependencies(processDescriptor(entry.getName()));
		} else {
			addClassDependency(entry.getName());
		}
	}
	
	public void visitFieldRef_info(FieldRef_info entry) {
		Logger.getLogger(getClass()).debug("VisitFieldRef_info():");
		Logger.getLogger(getClass()).debug("    class = \"" + entry.getClassName() + "\"");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.getRawNameAndType().getName() + "\"");
		Logger.getLogger(getClass()).debug("    type = \"" + entry.getRawNameAndType().getType() + "\"");

		// Dependencies on attributes are accounted as dependencies on their class
		entry.getRawClass().accept(this);
		addClassDependencies(processDescriptor(entry.getRawNameAndType().getType()));
	}

	public void visitMethodRef_info(MethodRef_info entry) {
		Logger.getLogger(getClass()).debug("VisitMethodRef_info():");
		Logger.getLogger(getClass()).debug("    class = \"" + entry.getClassName() + "\"");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.getRawNameAndType().getName() + "\"");
		Logger.getLogger(getClass()).debug("    type = \"" + entry.getRawNameAndType().getType() + "\"");
		addMethodDependency(entry.getFullSignature());
		addClassDependencies(processDescriptor(entry.getRawNameAndType().getType()));
	}

	public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
		Logger.getLogger(getClass()).debug("VisitInterfaceMethodRef_info():");
		Logger.getLogger(getClass()).debug("    class = \"" + entry.getClassName() + "\"");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.getRawNameAndType().getName() + "\"");
		Logger.getLogger(getClass()).debug("    type = \"" + entry.getRawNameAndType().getType() + "\"");
		addMethodDependency(entry.getFullSignature());
		addClassDependencies(processDescriptor(entry.getRawNameAndType().getType()));
	}

	public void visitField_info(Field_info entry) {
		getCurrentClass().addToMeasurement(Metrics.ATTRIBUTES);

		Logger.getLogger(getClass()).debug("VisitField_info(" + entry.getFullSignature() + ")");
		Logger.getLogger(getClass()).debug("Current class: " + getCurrentClass().getName());
		Logger.getLogger(getClass()).debug("Access flag: " + entry.getAccessFlag());
		Logger.getLogger(getClass()).debug("Public: " + (entry.getAccessFlag() & Method_info.ACC_PUBLIC));
		Logger.getLogger(getClass()).debug("Private: " + (entry.getAccessFlag() & Method_info.ACC_PRIVATE));
		Logger.getLogger(getClass()).debug("Protected: " + (entry.getAccessFlag() & Method_info.ACC_PROTECTED));
		Logger.getLogger(getClass()).debug("Static: " + (entry.getAccessFlag() & Method_info.ACC_STATIC));
		
		if ((entry.getAccessFlag() & Field_info.ACC_PUBLIC) != 0) {
			getCurrentClass().addToMeasurement(Metrics.PUBLIC_ATTRIBUTES);
		} else if ((entry.getAccessFlag() & Field_info.ACC_PRIVATE) != 0) {
			getCurrentClass().addToMeasurement(Metrics.PRIVATE_ATTRIBUTES);
		} else if ((entry.getAccessFlag() & Field_info.ACC_PROTECTED) != 0) {
			getCurrentClass().addToMeasurement(Metrics.PROTECTED_ATTRIBUTES);
		} else {
			getCurrentClass().addToMeasurement(Metrics.PACKAGE_ATTRIBUTES);
		}

		if ((entry.getAccessFlag() & Field_info.ACC_STATIC) != 0) {
			getCurrentClass().addToMeasurement(Metrics.STATIC_ATTRIBUTES);
		}

		if ((entry.getAccessFlag() & Field_info.ACC_FINAL) != 0) {
			getCurrentClass().addToMeasurement(Metrics.FINAL_ATTRIBUTES);
		}

		if ((entry.getAccessFlag() & Field_info.ACC_VOLATILE) != 0) {
			getCurrentClass().addToMeasurement(Metrics.VOLATILE_ATTRIBUTES);
		}

		if ((entry.getAccessFlag() & Field_info.ACC_TRANSIENT) != 0) {
			getCurrentClass().addToMeasurement(Metrics.TRANSIENT_ATTRIBUTES);
		}

		sloc = 1;
		isSynthetic = false;
		
		super.visitField_info(entry);
		
		if (!isSynthetic) {
			getCurrentClass().addToMeasurement(Metrics.CLASS_SLOC, sloc);
		}

		addClassDependencies(processDescriptor(entry.getDescriptor()));
	}

	public void visitMethod_info(Method_info entry) {
		fireBeginMethod(entry);
		
		setCurrentMethod(getMetricsFactory().createMethodMetrics(entry.getFullSignature()));
		getMetricsFactory().includeMethodMetrics(getCurrentMethod());
		
		Logger.getLogger(getClass()).debug("VisitMethod_info(" + entry.getFullSignature() + ")");
		Logger.getLogger(getClass()).debug("Current class: " + getCurrentClass().getName());
		Logger.getLogger(getClass()).debug("Access flag: " + entry.getAccessFlag());
		Logger.getLogger(getClass()).debug("Public: " + (entry.getAccessFlag() & Method_info.ACC_PUBLIC));
		Logger.getLogger(getClass()).debug("Private: " + (entry.getAccessFlag() & Method_info.ACC_PRIVATE));
		Logger.getLogger(getClass()).debug("Protected: " + (entry.getAccessFlag() & Method_info.ACC_PROTECTED));
		Logger.getLogger(getClass()).debug("Static: " + (entry.getAccessFlag() & Method_info.ACC_STATIC));

		sloc = 0;
		isSynthetic = false;
		
		if ((entry.getAccessFlag() & Method_info.ACC_PUBLIC) != 0) {
			getCurrentClass().addToMeasurement(Metrics.PUBLIC_METHODS);
		} else if ((entry.getAccessFlag() & Method_info.ACC_PRIVATE) != 0) {
			getCurrentClass().addToMeasurement(Metrics.PRIVATE_METHODS);
		} else if ((entry.getAccessFlag() & Method_info.ACC_PROTECTED) != 0) {
			getCurrentClass().addToMeasurement(Metrics.PROTECTED_METHODS);
		} else {
			getCurrentClass().addToMeasurement(Metrics.PACKAGE_METHODS);
		}

		if ((entry.getAccessFlag() & Method_info.ACC_STATIC) != 0) {
			getCurrentClass().addToMeasurement(Metrics.STATIC_METHODS);
		}

		if ((entry.getAccessFlag() & Method_info.ACC_FINAL) != 0) {
			getCurrentClass().addToMeasurement(Metrics.FINAL_METHODS);
		}

		if ((entry.getAccessFlag() & Method_info.ACC_SYNCHRONIZED) != 0) {
			getCurrentClass().addToMeasurement(Metrics.SYNCHRONIZED_METHODS);
		}

		if ((entry.getAccessFlag() & Method_info.ACC_NATIVE) != 0) {
			getCurrentClass().addToMeasurement(Metrics.NATIVE_METHODS);
		}

		if ((entry.getAccessFlag() & Method_info.ACC_ABSTRACT) != 0) {
			getCurrentClass().addToMeasurement(Metrics.ABSTRACT_METHODS);
			sloc = 1;
		}

		getCurrentMethod().addToMeasurement(Metrics.PARAMETERS, SignatureHelper.getParameterCount(entry.getDescriptor()));
		
		super.visitMethod_info(entry);
		
		if (!isSynthetic) {
			getCurrentMethod().addToMeasurement(Metrics.SLOC, sloc);
		}

		addClassDependencies(processDescriptor(entry.getDescriptor()));

		fireEndMethod(entry, getCurrentMethod());
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
		Object owner = attribute.getOwner();

		isSynthetic = true;
		
		if (owner instanceof Classfile) {
			getCurrentProject().addToMeasurement(Metrics.SYNTHETIC_CLASSES);
			getCurrentGroup().addToMeasurement(Metrics.SYNTHETIC_CLASSES);
		} else if (owner instanceof Field_info) {
			getCurrentClass().addToMeasurement(Metrics.SYNTHETIC_ATTRIBUTES);
		} else if (owner instanceof Method_info) {
			getCurrentClass().addToMeasurement(Metrics.SYNTHETIC_METHODS);
		} else {
			Logger.getLogger(getClass()).warn("Synthetic attribute on unknown Visitable: " + owner.getClass().getName());
		}
	}

	public void visitDeprecated_attribute(Deprecated_attribute attribute) {
		Object owner = attribute.getOwner();
	
		if (owner instanceof Classfile) {
			getCurrentProject().addToMeasurement(Metrics.DEPRECATED_CLASSES);
			getCurrentGroup().addToMeasurement(Metrics.DEPRECATED_CLASSES);
		} else if (owner instanceof Field_info) {
			getCurrentClass().addToMeasurement(Metrics.DEPRECATED_ATTRIBUTES);
		} else if (owner instanceof Method_info) {
			getCurrentClass().addToMeasurement(Metrics.DEPRECATED_METHODS);
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
		if ((helper.getInnerClassInfoIndex() != helper.getInnerClasses().getClassfile().getClassIndex()) && (helper.getInnerClassInfo().startsWith(helper.getInnerClasses().getClassfile().getClassName()))) {
			getCurrentProject().addToMeasurement(Metrics.INNER_CLASSES);
			getCurrentGroup().addToMeasurement(Metrics.INNER_CLASSES);
			getCurrentClass().addToMeasurement(Metrics.INNER_CLASSES);
		
			if ((helper.getAccessFlag() & InnerClass.ACC_PUBLIC) != 0) {
				getCurrentProject().addToMeasurement(Metrics.PUBLIC_INNER_CLASSES);
				getCurrentGroup().addToMeasurement(Metrics.PUBLIC_INNER_CLASSES);
				getCurrentClass().addToMeasurement(Metrics.PUBLIC_INNER_CLASSES);
			} else if ((helper.getAccessFlag() & InnerClass.ACC_PRIVATE) != 0) {
				getCurrentProject().addToMeasurement(Metrics.PRIVATE_INNER_CLASSES);
				getCurrentGroup().addToMeasurement(Metrics.PRIVATE_INNER_CLASSES);
				getCurrentClass().addToMeasurement(Metrics.PRIVATE_INNER_CLASSES);
			} else if ((helper.getAccessFlag() & InnerClass.ACC_PROTECTED) != 0) {
				getCurrentProject().addToMeasurement(Metrics.PROTECTED_INNER_CLASSES);
				getCurrentGroup().addToMeasurement(Metrics.PROTECTED_INNER_CLASSES);
				getCurrentClass().addToMeasurement(Metrics.PROTECTED_INNER_CLASSES);
			} else {
				getCurrentProject().addToMeasurement(Metrics.PACKAGE_INNER_CLASSES);
				getCurrentGroup().addToMeasurement(Metrics.PACKAGE_INNER_CLASSES);
				getCurrentClass().addToMeasurement(Metrics.PACKAGE_INNER_CLASSES);
			}

			if ((helper.getAccessFlag() & InnerClass.ACC_STATIC) != 0) {
				getCurrentProject().addToMeasurement(Metrics.STATIC_INNER_CLASSES);
				getCurrentGroup().addToMeasurement(Metrics.STATIC_INNER_CLASSES);
				getCurrentClass().addToMeasurement(Metrics.STATIC_INNER_CLASSES);
			}

			if ((helper.getAccessFlag() & InnerClass.ACC_FINAL) != 0) {
				getCurrentProject().addToMeasurement(Metrics.FINAL_INNER_CLASSES);
				getCurrentGroup().addToMeasurement(Metrics.FINAL_INNER_CLASSES);
				getCurrentClass().addToMeasurement(Metrics.FINAL_INNER_CLASSES);
			}

			if ((helper.getAccessFlag() & InnerClass.ACC_ABSTRACT) != 0) {
				getCurrentProject().addToMeasurement(Metrics.ABSTRACT_INNER_CLASSES);
				getCurrentGroup().addToMeasurement(Metrics.ABSTRACT_INNER_CLASSES);
				getCurrentClass().addToMeasurement(Metrics.ABSTRACT_INNER_CLASSES);
			}
		}
	}

	public void visitLineNumber(LineNumber helper) {
		sloc++;
	}

	public void visitLocalVariable(LocalVariable helper) {
		getCurrentMethod().addToMeasurement(Metrics.LOCAL_VARIABLES);

		addClassDependencies(processDescriptor(helper.getDescriptor()));
	}

	private int computeDepthOfInheritance(Classfile classfile) {
		int result = 1;
		
		if (classfile != null && classfile.getSuperclassIndex() != 0) {
			Classfile superclass = classfile.getLoader().getClassfile(classfile.getSuperclassName());
			result += computeDepthOfInheritance(superclass);
		}

		return result;
	}
	
	private Collection processDescriptor(String str) {
		Collection result = new LinkedList();
		
		Logger.getLogger(getClass()).debug("ProcessDescriptor: " + str);

		int currentPos = 0;
		int startPos;
		int endPos;

		while ((startPos = str.indexOf('L', currentPos)) != -1) {
			if ((endPos = str.indexOf(';', startPos)) != -1) {
				String classname = SignatureHelper.path2ClassName(str.substring(startPos + 1, endPos));
				result.add(classname);
				currentPos = endPos + 1;
			} else {
				currentPos = startPos + 1;
			}
		}

		Logger.getLogger(getClass()).debug("ProcessDescriptor: " + result);
		
		return result;
	}

	private void addClassDependencies(Collection classnames) {
		Iterator i = classnames.iterator();
		while (i.hasNext()) {
			addClassDependency((String) i.next());
		}
	}
	
	private void addClassDependency(String name) {
		Logger.getLogger(getClass()).debug("AddClassDependency(\"" + name + "\") ...");

		if (!getCurrentClass().getName().equals(name) && isInFilter(name)) {
			Metrics other = getMetricsFactory().createClassMetrics(name);
				
			if (getCurrentMethod() != null && isInScope(getCurrentMethod().getName())) {
				Logger.getLogger(getClass()).debug("AddClassDependency " + getCurrentMethod().getName() + " -> " + name + " ...");
				
				if (getCurrentClass().getParent().equals(other.getParent())) {
					Logger.getLogger(getClass()).debug("Intra-Package ...");
					getCurrentMethod().addToMeasurement(Metrics.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES, other.getName());
					other.addToMeasurement(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES, getCurrentMethod().getName());
				} else {
					Logger.getLogger(getClass()).debug("Extra-Package ...");
					getCurrentMethod().addToMeasurement(Metrics.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES, other.getName());
					other.addToMeasurement(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES, getCurrentMethod().getName());
				}
			} else if (isInScope(getCurrentClass().getName())) {
				Logger.getLogger(getClass()).debug("AddClassDependency " + getCurrentClass().getName() + " -> " + name + " ...");
				
				if (getCurrentClass().getParent().equals(other.getParent())) {
					Logger.getLogger(getClass()).debug("Intra-Package ...");
					getCurrentClass().addToMeasurement(Metrics.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES, other.getName());
					other.addToMeasurement(Metrics.INBOUND_INTRA_PACKAGE_DEPENDENCIES, getCurrentClass().getName());
				} else {
					Logger.getLogger(getClass()).debug("Extra-Package ...");
					getCurrentClass().addToMeasurement(Metrics.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES, other.getName());
					other.addToMeasurement(Metrics.INBOUND_EXTRA_PACKAGE_DEPENDENCIES, getCurrentClass().getName());
				}
			}
		}
	}
	
	private void addMethodDependency(String name) {
		Logger.getLogger(getClass()).debug("AddMethodDependency " + getCurrentMethod().getName() + " -> " + name + " ...");

		if (!getCurrentMethod().getName().equals(name) && isInScope(getCurrentMethod().getName()) && isInFilter(name)) {
			Metrics other = getMetricsFactory().createMethodMetrics(name);
			
			if (getCurrentClass().equals(other.getParent())) {
				Logger.getLogger(getClass()).debug("Intra-Class ...");
				getCurrentMethod().addToMeasurement(Metrics.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES, other.getName());
				other.addToMeasurement(Metrics.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES, getCurrentMethod().getName());
			} else if (getCurrentGroup().equals(other.getParent().getParent())) {
				Logger.getLogger(getClass()).debug("Intra-Package ...");
				getCurrentMethod().addToMeasurement(Metrics.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES, other.getName());
				other.addToMeasurement(Metrics.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES, getCurrentMethod().getName());
			} else {
				Logger.getLogger(getClass()).debug("Extra-Package ...");
				getCurrentMethod().addToMeasurement(Metrics.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES, other.getName());
				other.addToMeasurement(Metrics.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES, getCurrentMethod().getName());
			}
		}
	}
	
	private boolean isInScope(String name) {
		boolean result = true;

		if (scope != null) {
			result = scope.contains(name);
		}

		return result;
	}
	
	private boolean isInFilter(String name) {
		boolean result = true;

		if (filter != null) {
			result = filter.contains(name);
		}

		return result;
	}

	public void addMetricsListener(MetricsListener listener) {
		synchronized(metricsListeners) {
			metricsListeners.add(listener);
		}
	}

	public void removeMetricsListener(MetricsListener listener) {
		synchronized(metricsListeners) {
			metricsListeners.remove(listener);
		}
	}

	protected void fireBeginSession(int size) {
		MetricsEvent event = new MetricsEvent(this, size);

		HashSet listeners;
		synchronized(metricsListeners) {
			listeners = (HashSet) metricsListeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((MetricsListener) i.next()).beginSession(event);
		}
	}

	protected void fireBeginClass(Classfile classfile) {
		MetricsEvent event = new MetricsEvent(this, classfile);

		HashSet listeners;
		synchronized(metricsListeners) {
			listeners = (HashSet) metricsListeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((MetricsListener) i.next()).beginClass(event);
		}
	}

	protected void fireBeginMethod(Method_info method) {
		MetricsEvent event = new MetricsEvent(this, method);

		HashSet listeners;
		synchronized(metricsListeners) {
			listeners = (HashSet) metricsListeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((MetricsListener) i.next()).beginMethod(event);
		}
	}

	protected void fireEndMethod(Method_info method, Metrics metrics) {
		MetricsEvent event = new MetricsEvent(this, method, metrics);

		HashSet listeners;
		synchronized(metricsListeners) {
			listeners = (HashSet) metricsListeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((MetricsListener) i.next()).endMethod(event);
		}
	}

	protected void fireEndClass(Classfile classfile, Metrics metrics) {
		MetricsEvent event = new MetricsEvent(this, classfile, metrics);

		HashSet listeners;
		synchronized(metricsListeners) {
			listeners = (HashSet) metricsListeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((MetricsListener) i.next()).endClass(event);
		}
	}

	protected void fireEndSession() {
		MetricsEvent event = new MetricsEvent(this);

		HashSet listeners;
		synchronized(metricsListeners) {
			listeners = (HashSet) metricsListeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((MetricsListener) i.next()).endSession(event);
		}
	}
}
