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
import java.util.*;

import org.apache.logging.log4j.*;

import com.jeantessier.classreader.*;

public class ArrayElementValue extends ElementValue implements com.jeantessier.classreader.ArrayElementValue {
    private final Collection<ElementValue> values = new ArrayList<>();

    public ArrayElementValue(ConstantPool constantPool, DataInput in) throws IOException {
        this(constantPool, in, new ElementValueFactory());
    }

    public ArrayElementValue(ConstantPool constantPool, DataInput in, ElementValueFactory elementValueFactory) throws IOException {
        super(constantPool);

        int numValues = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Reading {} value(s) ...", numValues);
        for (int i = 0; i < numValues; i++) {
            LogManager.getLogger(getClass()).debug("value {}:", i);
            values.add(elementValueFactory.create(constantPool, in));
        }
    }

    public Collection<? extends ElementValue> getValues() {
        return values;
    }

    public char getTag() {
        return ElementValueType.ARRAY.getTag();
    }

    public void accept(Visitor visitor) {
        visitor.visitArrayElementValue(this);
    }
}
