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

package com.jeantessier.dependency;

import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

/**
 *  Traverses a Classfile and extracts dependencies from its code.
 *  Does not see dependencies on static final simple constants
 *  (basic type or String) and does not look at local variables.
 */
public class CodeDependencyCollector extends com.jeantessier.classreader.VisitorBase implements Collector {
	private NodeFactory factory;
	private Node        current;
	private HashSet     dependency_listeners = new HashSet();

	public CodeDependencyCollector() {
		this(new NodeFactory());
	}

	public CodeDependencyCollector(NodeFactory factory) {
		this.factory = factory;
	}

	public NodeFactory Factory() {
		return factory;
	}

	public Collection getCollection() {
		return Factory().Packages().values();
	}

	public void visitClassfile(Classfile classfile) {
		current = Factory().CreateClass(classfile.getClassName());

		fireBeginClass(classfile.toString());
		
		if (classfile.getSuperclassIndex() != 0) {
			classfile.getRawSuperclass().accept(this);
		}

		Iterator i;

		i = classfile.getAllInterfaces().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).accept(this);
		}

		i = classfile.getAllFields().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).accept(this);
		}

		i = classfile.getAllMethods().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).accept(this);
		}

		fireEndClass(classfile.toString());
	}

	public void visitClass_info(Class_info entry) {
		Logger.getLogger(getClass()).debug("VisitClass_info():");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.getName() + "\"");
		if (entry.getName().startsWith("[")) {
			ProcessDescriptor(entry.getName());
		} else {
			Node other = Factory().CreateClass(entry.getName());
			current.addDependency(other);
			Logger.getLogger(getClass()).info("Class_info dependency: " + current + " --> " + other);
			fireDependency(current, other);
		}
	}

	public void visitFieldRef_info(FieldRef_info entry) {
		Logger.getLogger(getClass()).debug("VisitFieldRef_info():");
		Logger.getLogger(getClass()).debug("    class = \"" + entry.getClassName() + "\"");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.getRawNameAndType().getName() + "\"");
		Logger.getLogger(getClass()).debug("    type = \"" + entry.getRawNameAndType().getType() + "\"");
		Node other = Factory().CreateFeature(entry.getFullSignature());
		current.addDependency(other);
		Logger.getLogger(getClass()).info("FieldRef_info dependency: " + current + " --> " + other);
		fireDependency(current, other);

		ProcessDescriptor(entry.getRawNameAndType().getType());
	}

	public void visitMethodRef_info(MethodRef_info entry) {
		Logger.getLogger(getClass()).debug("VisitMethodRef_info():");
		Logger.getLogger(getClass()).debug("    class = \"" + entry.getClassName() + "\"");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.getRawNameAndType().getName() + "\"");
		Logger.getLogger(getClass()).debug("    type = \"" + entry.getRawNameAndType().getType() + "\"");
		if (!entry.isStaticInitializer()) {
			Node other  = Factory().CreateFeature(entry.getFullSignature());
			current.addDependency(other);
			Logger.getLogger(getClass()).info("MethodRef_info dependency: " + current + " --> " + other);
			fireDependency(current, other);

			ProcessDescriptor(entry.getRawNameAndType().getType());
		}
	}

	public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
		Logger.getLogger(getClass()).debug("VisitInterfaceMethodRef_info():");
		Logger.getLogger(getClass()).debug("    class = \"" + entry.getClassName() + "\"");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.getRawNameAndType().getName() + "\"");
		Logger.getLogger(getClass()).debug("    type = \"" + entry.getRawNameAndType().getType() + "\"");
		Node other  = Factory().CreateFeature(entry.getFullSignature());
		current.addDependency(other);
		Logger.getLogger(getClass()).info("InterfaceMethodRef_info dependency: " + current + " --> " + other);
		fireDependency(current, other);

		ProcessDescriptor(entry.getRawNameAndType().getType());
	}

	public void visitField_info(Field_info entry) {
		Logger.getLogger(getClass()).debug("VisitField_info():");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.getName() + "\"");
		Logger.getLogger(getClass()).debug("    descriptor = \"" + entry.getDescriptor() + "\"");

		current = Factory().CreateFeature(entry.getFullSignature());

		ProcessDescriptor(entry.getDescriptor());
	
		super.visitField_info(entry);
	}

	public void visitMethod_info(Method_info entry) {
		Logger.getLogger(getClass()).debug("VisitMethod_info():");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.getName() + "\"");
		Logger.getLogger(getClass()).debug("    descriptor = \"" + entry.getDescriptor() + "\"");
	
		current = Factory().CreateFeature(entry.getFullSignature());

		ProcessDescriptor(entry.getDescriptor());

		super.visitMethod_info(entry);
	}

	public void visitCode_attribute(Code_attribute attribute) {
		Logger.getLogger(getClass()).debug("VisitCode_attribute() ...");

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

		super.visitCode_attribute(attribute);
	}

	public void visitExceptionHandler(ExceptionHandler helper) {
		Logger.getLogger(getClass()).debug(getClass().getName() + "VisitExceptionHandler(): " + helper);
		
		if (helper.getCatchTypeIndex() != 0) {
			helper.getRawCatchType().accept(this);
		}
	}

	private void ProcessDescriptor(String str) {
		int current_pos = 0;
		int start_pos;
		int end_pos;

		while ((start_pos = str.indexOf('L', current_pos)) != -1) {
			if ((end_pos = str.indexOf(';', start_pos)) != -1) {
				String classname = SignatureHelper.path2ClassName(str.substring(start_pos + 1, end_pos));
				Logger.getLogger(getClass()).debug("    Adding \"" + classname + "\"");
				Node other = Factory().CreateClass(classname);
				current.addDependency(other);
				Logger.getLogger(getClass()).info("descriptor dependency: " + current + " --> " + other);
				fireDependency(current, other);
				current_pos = end_pos + 1;
			} else {
				current_pos = start_pos + 1;
			}
		}
	}

	public void addDependencyListener(DependencyListener listener) {
		synchronized(dependency_listeners) {
			dependency_listeners.add(listener);
		}
	}

	public void removeDependencyListener(DependencyListener listener) {
		synchronized(dependency_listeners) {
			dependency_listeners.remove(listener);
		}
	}

	protected void fireBeginSession() {
		DependencyEvent event = new DependencyEvent(this);

		HashSet listeners;
		synchronized(dependency_listeners) {
			listeners = (HashSet) dependency_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((DependencyListener) i.next()).BeginSession(event);
		}
	}
	
	protected void fireBeginClass(String classname) {
		DependencyEvent event = new DependencyEvent(this, classname);

		HashSet listeners;
		synchronized(dependency_listeners) {
			listeners = (HashSet) dependency_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((DependencyListener) i.next()).BeginClass(event);
		}
	}

	protected void fireDependency(Node dependent, Node dependable) {
		DependencyEvent event = new DependencyEvent(this, dependent, dependable);

		HashSet listeners;
		synchronized(dependency_listeners) {
			listeners = (HashSet) dependency_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((DependencyListener) i.next()).Dependency(event);
		}
	}

	protected void fireEndClass(String classname) {
		DependencyEvent event = new DependencyEvent(this, classname);

		HashSet listeners;
		synchronized(dependency_listeners) {
			listeners = (HashSet) dependency_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((DependencyListener) i.next()).EndClass(event);
		}
	}

	protected void fireEndSession() {
		DependencyEvent event = new DependencyEvent(this);

		HashSet listeners;
		synchronized(dependency_listeners) {
			listeners = (HashSet) dependency_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((DependencyListener) i.next()).EndSession(event);
		}
	}

	public void beginSession(LoadEvent event) {
		// Do nothing
	}

	public void beginGroup(LoadEvent event) {
		// Do nothing
	}
	
	public void beginClassfile(LoadEvent event) {
		// Do nothing
	}
	
	public void beginFile(LoadEvent event) {
		// Do nothing
	}
	
	public void endClassfile(LoadEvent event) {
		event.getClassfile().accept(this);
	}
	
	public void endFile(LoadEvent event) {
		// Do nothing
	}
	
	public void endGroup(LoadEvent event) {
		// Do nothing
	}
	
	public void endSession(LoadEvent event) {
		// Do nothing
	}
}
