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

import com.jeantessier.classreader.ReferenceKind;
import com.jeantessier.classreader.Visitor;

import java.io.DataInput;
import java.io.IOException;

public class MethodHandle_info extends ConstantPoolEntry implements com.jeantessier.classreader.MethodHandle_info {
    private int referenceKind;
    private int referenceIndex;

    public MethodHandle_info(ConstantPool constantPool, DataInput in) throws IOException {
        super(constantPool);

        referenceKind = in.readByte();
        referenceIndex = in.readUnsignedShort();
    }

    public int getRawReferenceKind() { return referenceKind; }

    public ReferenceKind getReferenceKind() { return ReferenceKind.forKind(getRawReferenceKind()); }

    public String getReferenceKindDescription() {
        return null;
    }

    public int getReferenceIndex() { return referenceIndex; }

    public FeatureRef_info getReference() {
        return (FeatureRef_info) getConstantPool().get(getReferenceIndex());
    }

    public int hashCode() {
        return Integer.valueOf(getRawReferenceKind()).hashCode() ^ getReference().hashCode();
    }

    public boolean equals(Object object) {
        boolean result = false;

        if (this == object) {
            result = true;
        } else if (object != null && this.getClass().equals(object.getClass())) {
            MethodHandle_info other = (MethodHandle_info) object;
            result = this.getRawReferenceKind() == other.getRawReferenceKind() && this.getReference().equals(other.getReference());
        }

        return result;
    }

    public void accept(Visitor visitor) { visitor.visitMethodHandle_info(this); }
}
