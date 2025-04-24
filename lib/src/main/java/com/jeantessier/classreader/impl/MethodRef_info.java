/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

public class MethodRef_info extends FeatureRef_info implements com.jeantessier.classreader.MethodRef_info {
    public MethodRef_info(ConstantPool constantPool, DataInput in) throws IOException {
        super(constantPool, in);
    }

    public boolean isConstructor() {
        return getRawNameAndType().getName().equals("<init>");
    }

    public boolean isStaticInitializer() {
        return getRawNameAndType().getName().equals("<clinit>");
    }

    public String getReturnType() {
        return DescriptorHelper.getReturnType(getRawNameAndType().getType());
    }

    public String getNameAndType() {
        var nat = getRawNameAndType();
        return nat.getName() + nat.getType();
    }

    public String getName() {
        String result;

        if (isConstructor()) {
            result = getClassSimpleName();
        } else if (isStaticInitializer()) {
            result = "static {}";
        } else {
            result = getRawNameAndType().getName();
        }

        return result;
    }

    public String getSignature() {
        StringBuilder result = new StringBuilder();

        result.append(getName());
        if (!isStaticInitializer()) {
            result.append(DescriptorHelper.getSignature(getRawNameAndType().getType()));
        }

        return result.toString();
    }

    public String getUniqueName() {
        if (isConstructor() || isStaticInitializer()) {
            return super.getUniqueName();
        }

        return super.getUniqueName() + ": " + getReturnType();
    }

    public void accept(Visitor visitor) {
        visitor.visitMethodRef_info(this);
    }
}
