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

package com.jeantessier.classreader;

import java.util.*;

public class PrettyPrinter extends Printer {
	private boolean top = true;

	public void VisitClassfile(Classfile classfile) {
		classfile.ConstantPool().Accept(this);

		Append(classfile.Declaration()).Append(" {").EOL();

		Iterator i;

		i = classfile.Fields().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}

		i = classfile.Methods().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}

		Append("}").EOL();
	}

	public void VisitClass_info(Class_info entry) {
		if (top) {
			top = false;
			Append(CurrentCount()).Append(": ");
			Append("Class ");
			entry.RawName().Accept(this);
			EOL();
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
			EOL();
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
			EOL();
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
			EOL();
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
			Append(CurrentCount()).Append(": String ");
			entry.RawValue().Accept(this);
			EOL();
			top = true;
		} else {
			entry.RawValue().Accept(this);
		}
	}

	public void VisitInteger_info(Integer_info entry) {
		if (top) {
			Append(CurrentCount()).Append(": Integer ").Append(entry.Value()).EOL();
		} else {
			Append(entry.Value());
		}
	}

	public void VisitFloat_info(Float_info entry) {
		if (top) {
			Append(CurrentCount()).Append(": Float ").Append(entry.Value()).EOL();
		} else {
			Append(entry.Value());
		}
	}

	public void VisitLong_info(Long_info entry) {
		if (top) {
			Append(CurrentCount()).Append(": Long ").Append(entry.Value()).EOL();
		} else {
			Append(entry.Value());
		}
	}

	public void VisitDouble_info(Double_info entry) {
		if (top) {
			Append(CurrentCount()).Append(": Double ").Append(entry.Value()).EOL();
		} else {
			Append(entry.Value());
		}
	}

	public void VisitNameAndType_info(NameAndType_info entry) {
		if (top) {
			top = false;
			Append(CurrentCount()).Append(": Name and Type ");
			entry.RawName().Accept(this);
			Append(" ");
			entry.RawType().Accept(this);
			EOL();
			top = true;
		} else {
			entry.RawName().Accept(this);
			Append(" ");
			entry.RawType().Accept(this);
		}
	}

	public void VisitUTF8_info(UTF8_info entry) {
		if (top) {
			Append(CurrentCount()).Append(": \"").Append(entry.Value()).Append("\"").EOL();
		} else {
			Append(entry.Value());
		}
	}

	public void VisitField_info(Field_info entry) {
		Append("    ").Append(entry.Declaration()).Append(";").EOL();
	}

	public void VisitMethod_info(Method_info entry) {
		Append("    ").Append(entry.Declaration()).Append(";").EOL();
	}
}
