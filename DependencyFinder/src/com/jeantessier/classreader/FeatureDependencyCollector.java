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

import org.apache.oro.text.perl.*;

public class FeatureDependencyCollector extends CollectorBase {
    private static final Perl5Util perl = new Perl5Util();

    private Class_info this_class;

    public void VisitClassfile(Classfile classfile) {
		this_class = classfile.RawClass();

		classfile.ConstantPool().Accept(this);
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
