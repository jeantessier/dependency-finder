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

import java.io.*;

import org.apache.log4j.*;

public class SourceFile_attribute extends Attribute_info {
    private int source_file_index;

    public SourceFile_attribute(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
		super(classfile, owner);

		int byte_count = in.readInt();
		Category.getInstance(getClass().getName()).debug("Attribute length: " + byte_count);

		source_file_index = in.readUnsignedShort();
		Category.getInstance(getClass().getName()).debug("Source file: " + source_file_index + " (" + SourceFile() + ")");
    }

    public int SourceFileIndex() {
		return source_file_index;
    }

    public UTF8_info RawSourceFile() {
		return (UTF8_info) Classfile().ConstantPool().get(SourceFileIndex());
    }

    public String SourceFile() {
		return RawSourceFile().toString();
    }

    public String toString() {
		return "Source file \"" + SourceFile() + "\"";
    }

    public void Accept(Visitor visitor) {
		visitor.VisitSourceFile_attribute(this);
    }
}

