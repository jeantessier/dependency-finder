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

import org.apache.oro.text.perl.*;

public class FeatureDependencyCollector extends CollectorBase {
    private static final Perl5Util perl = new Perl5Util();

    private Class_info this_class;

    public void VisitClassfile(Classfile classfile) {
		this_class = classfile.RawClass();

		classfile.ConstantPool().Accept(this);

		/*
		  i = classfile.Methods().iterator();
		  while (i.hasNext()) {
		  ((Visitable) i.next()).Accept(this);
		  }
		*/
    }

    public void VisitFieldRef_info(FieldRef_info entry) {
		if (entry.RawClass() != this_class) {
			Add(entry.Class() + "." + entry.RawNameAndType().Name());
		}
    }

    public void VisitMethodRef_info(MethodRef_info entry) {
		if ((entry.RawClass() != this_class) && !perl.match("/<.*init>/", entry.RawNameAndType().Name())) {
			Add(entry.Class() + "." + entry.RawNameAndType().Name());
		}
    }

    public void VisitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
		if (entry.RawClass() != this_class) {
			Add(entry.Class() + "." + entry.RawNameAndType().Name());
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

		byte[] code = attribute.Code();

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
					int start = instr.Start();
					int index = (code[start+1] << 8) | code[start+2];
					((Visitable) attribute.Classfile().ConstantPool().get(index)).Accept(this);
					break;
				default:
					// Do nothing
					break;
			}
		}

		Iterator i = attribute.Attributes().iterator();
		while (i.hasNext()) {
			((Visitable) i.next()).Accept(this);
		}
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
