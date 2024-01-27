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

import com.jeantessier.classreader.*;
import org.apache.logging.log4j.*;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class RecordComponent_info implements com.jeantessier.classreader.RecordComponent_info {
    private final ConstantPool constantPool;

    private final int nameIndex;
    private final int descriptorIndex;
    private final Collection<Attribute_info> attributes = new LinkedList<>();


    public RecordComponent_info(ConstantPool constantPool, DataInput in, AttributeFactory attributeFactory) throws IOException {
        this.constantPool = constantPool;

        nameIndex = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("name index: {} ({})", nameIndex, getName());

        descriptorIndex = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("descriptor index: {} ({})", descriptorIndex, getDescriptor());

        int attributeCount = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Reading {} record component attribute(s)", attributeCount);
        IntStream.range(0, attributeCount).forEach(i -> {
            try {
                LogManager.getLogger(getClass()).debug("record component attribute {}:", i);
                attributes.add(attributeFactory.create(getConstantPool(), this, in));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public ConstantPool getConstantPool() {
        return constantPool;
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

    public int getDescriptorIndex() {
        return descriptorIndex;
    }

    public UTF8_info getRawDescriptor() {
        return (UTF8_info) getConstantPool().get(getDescriptorIndex());
    }

    public String getDescriptor() {
        return getRawDescriptor().getValue();
    }

    public String getType() {
        return DescriptorHelper.getType(getDescriptor());
    }

    public Collection<? extends Attribute_info> getAttributes() {
        return attributes;
    }

    public void accept(Visitor visitor) {
        visitor.visitRecordComponent_info(this);
    }
}
