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

import java.io.*;

public class MethodRef_info extends FeatureRef_info {
	public MethodRef_info(ConstantPool constant_pool, DataInputStream in) throws IOException {
		super(constant_pool, in);
	}

	public boolean IsConstructor() {
		return RawNameAndType().Name().equals("<init>");
	}

	public boolean IsStaticInitializer() {
		return RawNameAndType().Name().equals("<clinit>");
	}

	public String Name() {
		String result = null;

		if (IsConstructor()) {
			result = Class().substring(Class().lastIndexOf(".") + 1);
		} else if (IsStaticInitializer()) {
			result = "static {}";
		} else {
			result = RawNameAndType().Name();
		}

		return result;
	}

	public String Signature() {
		StringBuffer result = new StringBuffer();

		result.append(Name());
		if (!IsStaticInitializer()) {
			result.append(SignatureHelper.Signature(RawNameAndType().Type()));
		}

		return result.toString();
	}

	public void Accept(Visitor visitor) {
		visitor.VisitMethodRef_info(this);
	}
}
