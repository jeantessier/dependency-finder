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

package com.jeantessier.dependency;

import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

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

	public Collection Collection() {
		return Factory().Packages().values();
	}

	public void VisitClassfile(Classfile classfile) {
		current = Factory().CreateClass(classfile.Class());

		fireStartClass(classfile.toString());
		
		if (classfile.SuperclassIndex() != 0) {
			classfile.RawSuperclass().Accept(this);
		}

		Iterator i;

		i = classfile.Interfaces().iterator();
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

		fireStopClass(classfile.toString());
	}

	public void VisitClass_info(Class_info entry) {
		Logger.getLogger(getClass()).debug("VisitClass_info():");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.Name() + "\"");
		if (entry.Name().startsWith("[")) {
			ProcessDescriptor(entry.Name());
		} else {
			Node other = Factory().CreateClass(entry.Name());
			current.AddDependency(other);
			Logger.getLogger(getClass()).info("Class_info dependency: " + current + " --> " + other);
			fireDependency(current, other);
		}
	}
    
	public void VisitFieldRef_info(FieldRef_info entry) {
		Logger.getLogger(getClass()).debug("VisitFieldRef_info():");
		Logger.getLogger(getClass()).debug("    class = \"" + entry.Class() + "\"");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.RawNameAndType().Name() + "\"");
		Logger.getLogger(getClass()).debug("    type = \"" + entry.RawNameAndType().Type() + "\"");
		Node other = Factory().CreateFeature(entry.FullSignature());
		current.AddDependency(other);
		Logger.getLogger(getClass()).info("FieldRef_info dependency: " + current + " --> " + other);
		fireDependency(current, other);

		ProcessDescriptor(entry.RawNameAndType().Type());
	}

	public void VisitMethodRef_info(MethodRef_info entry) {
		Logger.getLogger(getClass()).debug("VisitMethodRef_info():");
		Logger.getLogger(getClass()).debug("    class = \"" + entry.Class() + "\"");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.RawNameAndType().Name() + "\"");
		Logger.getLogger(getClass()).debug("    type = \"" + entry.RawNameAndType().Type() + "\"");
		if (!entry.IsStaticInitializer()) {
			Node other  = Factory().CreateFeature(entry.FullSignature());
			current.AddDependency(other);
			Logger.getLogger(getClass()).info("MethodRef_info dependency: " + current + " --> " + other);
			fireDependency(current, other);

			ProcessDescriptor(entry.RawNameAndType().Type());
		}
	}

	public void VisitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
		Logger.getLogger(getClass()).debug("VisitInterfaceMethodRef_info():");
		Logger.getLogger(getClass()).debug("    class = \"" + entry.Class() + "\"");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.RawNameAndType().Name() + "\"");
		Logger.getLogger(getClass()).debug("    type = \"" + entry.RawNameAndType().Type() + "\"");
		Node other  = Factory().CreateFeature(entry.FullSignature());
		current.AddDependency(other);
		Logger.getLogger(getClass()).info("InterfaceMethodRef_info dependency: " + current + " --> " + other);
		fireDependency(current, other);

		ProcessDescriptor(entry.RawNameAndType().Type());
	}

	public void VisitField_info(Field_info entry) {
		Logger.getLogger(getClass()).debug("VisitField_info():");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.Name() + "\"");
		Logger.getLogger(getClass()).debug("    descriptor = \"" + entry.Descriptor() + "\"");

		current = Factory().CreateFeature(entry.FullSignature());

		ProcessDescriptor(entry.Descriptor());
	
		super.VisitField_info(entry);
	}

	public void VisitMethod_info(Method_info entry) {
		Logger.getLogger(getClass()).debug("VisitMethod_info():");
		Logger.getLogger(getClass()).debug("    name = \"" + entry.Name() + "\"");
		Logger.getLogger(getClass()).debug("    descriptor = \"" + entry.Descriptor() + "\"");
	
		current = Factory().CreateFeature(entry.FullSignature());

		ProcessDescriptor(entry.Descriptor());

		super.VisitMethod_info(entry);
	}

	public void VisitCode_attribute(Code_attribute attribute) {
		Logger.getLogger(getClass()).debug("VisitCode_attribute() ...");

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

		super.VisitCode_attribute(attribute);
	}

	public void VisitExceptionHandler(ExceptionHandler helper) {
		Logger.getLogger(getClass()).debug(getClass().getName() + "VisitExceptionHandler(): " + helper);
		
		if (helper.CatchTypeIndex() != 0) {
			helper.RawCatchType().Accept(this);
		}
	}

	private void ProcessDescriptor(String str) {
		int current_pos = 0;
		int start_pos;
		int end_pos;

		while ((start_pos = str.indexOf('L', current_pos)) != -1) {
			if ((end_pos = str.indexOf(';', start_pos)) != -1) {
				String classname = SignatureHelper.Path2ClassName(str.substring(start_pos + 1, end_pos));
				Logger.getLogger(getClass()).debug("    Adding \"" + classname + "\"");
				Node other = Factory().CreateClass(classname);
				current.AddDependency(other);
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

	protected void fireStartClass(String classname) {
		DependencyEvent event = new DependencyEvent(this, classname);

		HashSet listeners;
		synchronized(dependency_listeners) {
			listeners = (HashSet) dependency_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((DependencyListener) i.next()).StartClass(event);
		}
	}

	protected void fireStopClass(String classname) {
		DependencyEvent event = new DependencyEvent(this, classname);

		HashSet listeners;
		synchronized(dependency_listeners) {
			listeners = (HashSet) dependency_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((DependencyListener) i.next()).StopClass(event);
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

	public void LoadStart(LoadEvent event) {
		// Do nothing
	}
	
	public void LoadElement(LoadEvent event) {
		// Do nothing
	}
	
	public void LoadedClassfile(LoadEvent event) {
		event.Classfile().Accept(this);
	}
	
	public void LoadStop(LoadEvent event) {
		// Do nothing
	}
}
