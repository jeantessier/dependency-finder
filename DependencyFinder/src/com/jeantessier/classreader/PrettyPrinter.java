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

public class PrettyPrinter extends Printer {
	private boolean top = true;

	public PrettyPrinter() {
		super();
	}

	public PrettyPrinter(String header) {
		super(header);
	}

	public PrettyPrinter(StringBuffer buffer) {
		super(buffer);
	}
    
	public void VisitClassfile(Classfile classfile) {
		classfile.ConstantPool().Accept(this);

		Append(classfile.Declaration()).Append(" {\n");

		Iterator i;

		i = classfile.Fields().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}

		i = classfile.Methods().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}

		Append("}\n");
	}

	public void VisitClass_info(Class_info entry) {
		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Class ");
			entry.RawName().Accept(this);
			Append("\n");
			top = true;
		} else {
			entry.RawName().Accept(this);
		}
	}

	public void VisitFieldRef_info(FieldRef_info entry) {
		Class_info       c   = entry.RawClass();
		NameAndType_info nat = entry.RawNameAndType();

		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Field ");
			nat.RawType().Accept(this);
			Append(" ");
			c.Accept(this);
			Append(".");
			nat.RawName().Accept(this);
			Append("\n");
			top = true;
		} else {
			nat.RawType().Accept(this);
			Append(" ");
			c.Accept(this);
			Append(".");
			nat.RawName().Accept(this);
		}
	}

	public void VisitMethodRef_info(MethodRef_info entry) {
		Class_info       c   = entry.RawClass();
		NameAndType_info nat = entry.RawNameAndType();

		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Method ");
			c.Accept(this);
			Append(".");
			nat.RawName().Accept(this);
			nat.RawType().Accept(this);
			Append("\n");
			top = true;
		} else {
			c.Accept(this);
			Append(".");
			nat.RawName().Accept(this);
			nat.RawType().Accept(this);
		}
	}

	public void VisitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
		Class_info       c   = entry.RawClass();
		NameAndType_info nat = entry.RawNameAndType();

		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Interface Method ");
			c.Accept(this);
			Append(".");
			nat.RawName().Accept(this);
			nat.RawType().Accept(this);
			Append("\n");
			top = true;
		} else {
			c.Accept(this);
			Append(".");
			nat.RawName().Accept(this);
			nat.RawType().Accept(this);
		}
	}

	public void VisitString_info(String_info entry) {
		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("String ");
			entry.RawValue().Accept(this);
			Append("\n");
			top = true;
		} else {
			entry.RawValue().Accept(this);
		}
	}

	public void VisitInteger_info(Integer_info entry) {
		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Integer ").Append(entry.Value());
			Append("\n");
			top = true;
		} else {
			Append(entry.Value());
		}
	}

	public void VisitFloat_info(Float_info entry) {
		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Float ").Append(entry.Value());
			Append("\n");
			top = true;
		} else {
			Append(entry.Value());
		}
	}

	public void VisitLong_info(Long_info entry) {
		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Long ").Append(entry.Value());
			Append("\n");
			top = true;
		} else {
			Append(entry.Value());
		}
	}

	public void VisitDouble_info(Double_info entry) {
		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Double ").Append(entry.Value());
			Append("\n");
			top = true;
		} else {
			Append(entry.Value());
		}
	}

	public void VisitNameAndType_info(NameAndType_info entry) {
		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Name and Type ");
			entry.RawName().Accept(this);
			Append(" ");
			entry.RawType().Accept(this);
			Append("\n");
			top = true;
		} else {
			entry.RawName().Accept(this);
			Append(" ");
			entry.RawType().Accept(this);
		}
	}

	public void VisitUTF8_info(UTF8_info entry) {
		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("\"").Append(entry.Value()).Append("\"").Append("\n");
			top = true;
		} else {
			Append(entry.Value());
		}
	}

	public void VisitField_info(Field_info entry) {
		Append("    ").Append(entry.Declaration()).Append(";\n");
	}

	public void VisitMethod_info(Method_info entry) {
		Append("    ").Append(entry.Declaration()).Append(";\n");
	}
}
