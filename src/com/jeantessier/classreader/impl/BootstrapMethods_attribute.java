package com.jeantessier.classreader.impl;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

public class BootstrapMethods_attribute extends Attribute_info implements com.jeantessier.classreader.BootstrapMethods_attribute {
    private final Collection<BootstrapMethod> bootstrapMethods = new LinkedList<>();

    public BootstrapMethods_attribute(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
        super(constantPool, owner);

        int byteCount = in.readInt();
        Logger.getLogger(getClass()).debug("Attribute length: " + byteCount);

        int numBootstrapMethods = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + numBootstrapMethods + " bootstrap method(s) ...");
        IntStream.range(0, numBootstrapMethods).forEach(i -> {
            try {
                Logger.getLogger(getClass()).debug("bootstrap method " + i + ":");
                bootstrapMethods.add(new BootstrapMethod(this, in));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Collection<? extends BootstrapMethod> getBootstrapMethods() {
        return bootstrapMethods;
    }

    public String getAttributeName() {
        return AttributeType.BOOTSTRAP_METHODS.getAttributeName();
    }

    public void accept(Visitor visitor) {
        visitor.visitBootstrapMethods_attribute(this);
    }
}
