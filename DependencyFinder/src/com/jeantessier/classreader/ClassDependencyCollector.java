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

import java.util.*;

public class ClassDependencyCollector extends CollectorBase {
    private Class_info this_class;
    private boolean    top        = true;

    public void VisitClassfile(Classfile classfile) {
		this_class = classfile.RawClass();

		classfile.ConstantPool().Accept(this);

		classfile.RawSuperclass().Accept(this);

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
    }

    public void VisitClass_info(Class_info entry) {
		String classname = entry.Name();
	
		if (entry != this_class) {
			if (classname.startsWith("[") ) {
				top = false;
				entry.RawName().Accept(this);
				top = true;
			} else {
				Add(classname);
			}
		}
    }

    public void VisitFieldRef_info(FieldRef_info entry) {
		if (top) {
			if (entry.RawClass() == this_class) {
				top = false;
				entry.RawNameAndType().Accept(this);
				top = true;
			}
		} else {
			entry.RawNameAndType().Accept(this);
		}
    }

    public void VisitMethodRef_info(MethodRef_info entry) {
		if (top) {
			if (entry.RawClass() == this_class) {
				top = false;
				entry.RawNameAndType().Accept(this);
				top = true;
			}
		} else {
			entry.RawNameAndType().Accept(this);
		}
    }

    public void VisitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
		if (top) {
			if (entry.RawClass() == this_class) {
				top = false;
				entry.RawNameAndType().Accept(this);
				top = true;
			}
		} else {
			entry.RawNameAndType().Accept(this);
		}
    }

    public void VisitString_info(String_info entry) {
		if (!top) {
			entry.RawValue().Accept(this);
		}
    }

    public void VisitNameAndType_info(NameAndType_info entry) {
		if (!top) {
			entry.RawType().Accept(this);
		}
    }

    public void VisitUTF8_info(UTF8_info entry) {
		if (!top) {
			ProcessSignature(entry.Value());
		}
    }

    public void VisitField_info(Field_info entry) {
		ProcessSignature(entry.Descriptor());
	
		Iterator i = entry.Attributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
    }

    public void VisitMethod_info(Method_info entry) {
		ProcessSignature(entry.Descriptor());
	
		Iterator i = entry.Attributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
    }

    public void VisitCode_attribute(Code_attribute attribute) {
		Iterator i = attribute.Attributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
    }

    public void VisitExceptions_attribute(Exceptions_attribute attribute) {
		Iterator i = attribute.Exceptions().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
    }

    public void VisitInnerClasses_attribute(InnerClasses_attribute attribute) {
		Iterator i = attribute.Classes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
    }

    public void VisitLineNumberTable_attribute(LineNumberTable_attribute attribute) {
		Iterator i = attribute.LineNumbers().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
    }

    public void VisitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
		Iterator i = attribute.LocalVariables().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
    }

    public void VisitLocalVariable(LocalVariable helper) {
		ProcessSignature(helper.Descriptor());
    }

    private void ProcessSignature(String str) {
		int current_pos = 0;
		int start_pos;
		int end_pos;

		while ((start_pos = str.indexOf('L', current_pos)) != -1) {
			if ((end_pos = str.indexOf(';', start_pos)) != -1) {
				String candidate = str.substring(start_pos + 1, end_pos);
				if (!this_class.Name().equals(candidate)) {
					Add(SignatureHelper.Path2ClassName(candidate));
				}
				current_pos = end_pos + 1;
			} else {
				current_pos = start_pos + 1;
			}
		}
    }
}
