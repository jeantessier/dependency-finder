/*
 *  Copyright (c) 2001-2023, Jean Tessier
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

import com.jeantessier.classreader.ClassNameHelper;
import com.jeantessier.classreader.Visitor;
import org.apache.logging.log4j.*;

import java.io.DataInput;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class TypeAnnotation implements com.jeantessier.classreader.TypeAnnotation {
    private final ConstantPool constantPool;

    private final TargetType targetType;

    private final Target_info target;

    private final TypePath typePath;

    private final int typeIndex;

    private final Collection<ElementValuePair> elementValuePairs = new LinkedList<>();

    public TypeAnnotation(ConstantPool constantPool, DataInput in) throws IOException {
        this(constantPool, in, new ElementValueFactory());
    }

    public TypeAnnotation(ConstantPool constantPool, DataInput in, ElementValueFactory elementValueFactory) throws IOException {
        this.constantPool = constantPool;

        var rawTargetType = in.readUnsignedShort();
        targetType = TargetType.forTargetType(rawTargetType);
        LogManager.getLogger(getClass()).debug("Target type: {}({})", targetType.getHexTargetType(), targetType.getDescription());

        target = targetType.create(in);

        typePath = new TypePath(in);

        typeIndex = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Type index: {} ({})", typeIndex, getType());

        int numElementValuePairs = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Reading {} element value pair(s) ...", numElementValuePairs);
        for (int i=0; i<numElementValuePairs; i++) {
            LogManager.getLogger(getClass()).debug("Element value pair {}:", i);
            elementValuePairs.add(new ElementValuePair(constantPool, in, elementValueFactory));
        }
    }

    public com.jeantessier.classreader.TargetType getTargetType() {
        return targetType.getTargetType();
    }

    public Target_info getTarget() {
        return target;
    }

    public com.jeantessier.classreader.TypePath getTargetPath() {
        return typePath;
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    public UTF8_info getRawType() {
        return (UTF8_info) constantPool.get(getTypeIndex());
    }

    public String getType() {
        String result = "";

        if (getTypeIndex() != 0) {
            result = ClassNameHelper.convertClassName(getRawType().getValue());
        }

        return result;
    }

    public Collection<? extends ElementValuePair> getElementValuePairs() {
        return elementValuePairs;
    }

    public void accept(Visitor visitor) {
        visitor.visitTypeAnnotation(this);
    }
}
