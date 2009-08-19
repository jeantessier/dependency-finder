/*
 *  Copyright (c) 2001-2009, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
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

package com.jeantessier.classreader.impl;

import java.io.*;

import com.jeantessier.classreader.*;

public class NameAndType_info extends ConstantPoolEntry implements com.jeantessier.classreader.NameAndType_info {
    private int nameIndex;
    private int typeIndex;

    public NameAndType_info(ConstantPool constantPool, DataInput in) throws IOException {
        super(constantPool);

        nameIndex = in.readUnsignedShort();
        typeIndex = in.readUnsignedShort();
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public UTF8_info getRawName() {
        return (UTF8_info) getConstantPool().get(getNameIndex());
    }

    public String getName() {
        return getRawName().getValue();
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    public UTF8_info getRawType() {
        return (UTF8_info) getConstantPool().get(getTypeIndex());
    }

    public String getType() {
        return getRawType().getValue();
    }

    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append(getName()).append(" -> ").append(getType());

        return result.toString();
    }

    public int hashCode() {
        return getRawName().hashCode() ^ getRawType().hashCode();
    }

    public boolean equals(Object object) {
        boolean result = false;

        if (this == object) {
            result = true;
        } else if (object != null && this.getClass().equals(object.getClass())) {
            NameAndType_info other = (NameAndType_info) object;
            result = this.getRawName().equals(other.getRawName()) && this.getRawType().equals(other.getRawType());
        }

        return result;
    }

    public void accept(Visitor visitor) {
        visitor.visitNameAndType_info(this);
    }
}
