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
	
		super.VisitField_info(entry);
	}

	public void VisitMethod_info(Method_info entry) {
		ProcessSignature(entry.Descriptor());
	
		super.VisitMethod_info(entry);
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
