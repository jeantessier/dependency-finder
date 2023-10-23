package com.jeantessier.classreader.impl;

import com.jeantessier.classreader.*;
import com.jeantessier.classreader.Integer_info;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class StackMapTable_attribute extends Attribute_info implements com.jeantessier.classreader.StackMapTable_attribute {
    private final Collection<StackMapFrame> entries = new LinkedList<>();

    public StackMapTable_attribute(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
        this(new StackMapFrameFactory(new VerificationTypeInfoFactory()), constantPool, owner, in);
    }

    public StackMapTable_attribute(StackMapFrameFactory stackMapFrameFactory, ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
        super(constantPool, owner);

        int byteCount = in.readInt();
        Logger.getLogger(getClass()).debug("Attribute length: " + byteCount);

        int numEntries = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + numEntries + " stack map frame(s) ...");
        IntStream.range(0, numEntries).forEach(i -> {
            try {
                Logger.getLogger(getClass()).debug("stack map frame " + i + ":");
                entries.add(stackMapFrameFactory.create(constantPool, in));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Collection<? extends StackMapFrame> getEntries() {
        return entries;
    }

    public String getAttributeName() {
        return AttributeType.STACK_MAP_TABLE.getAttributeName();
    }

    public void accept(Visitor visitor) {
        visitor.visitStackMapTable_attribute(this);
    }
}
