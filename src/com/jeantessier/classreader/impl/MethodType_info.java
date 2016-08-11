/*
 *  Copyright (c) 2001-2016, Jean Tessier
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

import com.jeantessier.classreader.UTF8_info;
import com.jeantessier.classreader.Visitor;

import java.io.DataInput;
import java.io.IOException;

public class MethodType_info extends ConstantPoolEntry implements com.jeantessier.classreader.MethodType_info {
    private int descriptorIndex;

    public MethodType_info(ConstantPool constantPool, DataInput in) throws IOException {
        super(constantPool);

        descriptorIndex = in.readUnsignedShort();
    }

    public int getDescriptorIndex() { return descriptorIndex; }

    public UTF8_info getRawDescriptor() {
        return (UTF8_info) getConstantPool().get(getDescriptorIndex());
    }

    public String getDescriptor() { return getRawDescriptor().getValue(); }

    public int hashCode() {
        return getRawDescriptor().hashCode();
    }

    public boolean equals(Object object) {
        boolean result = false;

        if (this == object) {
            result = true;
        } else if (object != null && this.getClass().equals(object.getClass())) {
            MethodType_info other = (MethodType_info) object;
            result = this.getRawDescriptor().equals(other.getRawDescriptor());
        }

        return result;
    }

    public void accept(Visitor visitor) { visitor.visitMethodType_info(this); }
}
