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
		Category.getInstance(getClass().getName()).debug("CodeDependencyCollector.VisitClass_info():");
		Category.getInstance(getClass().getName()).debug("    name = \"" + entry.Name() + "\"");
		if (entry.Name().startsWith("[")) {
			ProcessDescriptor(entry.Name());
		} else {
			Node other = Factory().CreateClass(entry.Name());
			current.AddDependency(other);
			Category.getInstance(getClass().getName()).info("Class_info dependency: " + current + " --> " + other);
			fireDependency(current, other);
		}
	}
    
	public void VisitFieldRef_info(FieldRef_info entry) {
		Category.getInstance(getClass().getName()).debug("CodeDependencyCollector.VisitFieldRef_info():");
		Category.getInstance(getClass().getName()).debug("    class = \"" + entry.Class() + "\"");
		Category.getInstance(getClass().getName()).debug("    name = \"" + entry.RawNameAndType().Name() + "\"");
		Category.getInstance(getClass().getName()).debug("    type = \"" + entry.RawNameAndType().Type() + "\"");
		Node other = Factory().CreateFeature(entry.FullSignature());
		current.AddDependency(other);
		Category.getInstance(getClass().getName()).info("FieldRef_info dependency: " + current + " --> " + other);
		fireDependency(current, other);

		ProcessDescriptor(entry.RawNameAndType().Type());
	}

	public void VisitMethodRef_info(MethodRef_info entry) {
		Category.getInstance(getClass().getName()).debug("CodeDependencyCollector.VisitMethodRef_info():");
		Category.getInstance(getClass().getName()).debug("    class = \"" + entry.Class() + "\"");
		Category.getInstance(getClass().getName()).debug("    name = \"" + entry.RawNameAndType().Name() + "\"");
		Category.getInstance(getClass().getName()).debug("    type = \"" + entry.RawNameAndType().Type() + "\"");
		if (!entry.IsStaticInitializer()) {
			Node other  = Factory().CreateFeature(entry.FullSignature());
			current.AddDependency(other);
			Category.getInstance(getClass().getName()).info("MethodRef_info dependency: " + current + " --> " + other);
			fireDependency(current, other);

			ProcessDescriptor(entry.RawNameAndType().Type());
		}
	}

	public void VisitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
		Category.getInstance(getClass().getName()).debug("CodeDependencyCollector.VisitInterfaceMethodRef_info():");
		Category.getInstance(getClass().getName()).debug("    class = \"" + entry.Class() + "\"");
		Category.getInstance(getClass().getName()).debug("    name = \"" + entry.RawNameAndType().Name() + "\"");
		Category.getInstance(getClass().getName()).debug("    type = \"" + entry.RawNameAndType().Type() + "\"");
		Node other  = Factory().CreateFeature(entry.FullSignature());
		current.AddDependency(other);
		Category.getInstance(getClass().getName()).info("InterfaceMethodRef_info dependency: " + current + " --> " + other);
		fireDependency(current, other);

		ProcessDescriptor(entry.RawNameAndType().Type());
	}

	public void VisitField_info(Field_info entry) {
		Category.getInstance(getClass().getName()).debug("CodeDependencyCollector.VisitField_info():");
		Category.getInstance(getClass().getName()).debug("    name = \"" + entry.Name() + "\"");
		Category.getInstance(getClass().getName()).debug("    descriptor = \"" + entry.Descriptor() + "\"");

		current = Factory().CreateFeature(entry.FullSignature());

		ProcessDescriptor(entry.Descriptor());
	
		super.VisitField_info(entry);
	}

	public void VisitMethod_info(Method_info entry) {
		Category.getInstance(getClass().getName()).debug("CodeDependencyCollector.VisitMethod_info():");
		Category.getInstance(getClass().getName()).debug("    name = \"" + entry.Name() + "\"");
		Category.getInstance(getClass().getName()).debug("    descriptor = \"" + entry.Descriptor() + "\"");
	
		if (entry.IsStaticInitializer()) {
			current = Factory().CreateClass(entry.Classfile().Class());
		} else {
			current = Factory().CreateFeature(entry.FullSignature());
		}

		ProcessDescriptor(entry.Descriptor());

		super.VisitMethod_info(entry);
	}

	public void VisitCode_attribute(Code_attribute attribute) {
		Category.getInstance(getClass().getName()).debug("CodeDependencyCollector.VisitCode_attribute() ...");

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
		Category.getInstance(getClass().getName()).debug(getClass().getName() + "VisitExceptionHandler(): " + helper);
		
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
				Category.getInstance(getClass().getName()).debug("    Adding \"" + classname + "\"");
				Node other = Factory().CreateClass(classname);
				current.AddDependency(other);
				Category.getInstance(getClass().getName()).info("descriptor dependency: " + current + " --> " + other);
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
}
