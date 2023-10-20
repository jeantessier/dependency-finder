package com.jeantessier.classreader.impl;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

public class BootstrapMethod implements com.jeantessier.classreader.BootstrapMethod {
    private final BootstrapMethods_attribute bootstrapMethods;
    private final int bootstrapMethodRef;
    private final Collection<Integer> argumentIndices = new LinkedList<>();

    public BootstrapMethod(BootstrapMethods_attribute bootstrapMethods, DataInput in) throws IOException {
        this.bootstrapMethods = bootstrapMethods;

        bootstrapMethodRef = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Bootstrap method ref: " + bootstrapMethodRef + " (" + getBootstrapMethod() + ")");

        var numArgument = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + numArgument + " argument(s) ...");
        for (int i=0; i<numArgument; i++) {
            Logger.getLogger(getClass()).debug("Reading argument " + i);
            var argumentIndex = in.readUnsignedShort();
            var argument = bootstrapMethods.getConstantPool().get(argumentIndex);
            Logger.getLogger(getClass()).debug("Argument " + i + ": " + argumentIndex + " (" + argument + ")");
            argumentIndices.add(argumentIndex);
        }
    }

    public BootstrapMethods_attribute getBootstrapMethods() {
        return bootstrapMethods;
    }

    public int getBootstrapMethodRef() { return bootstrapMethodRef; }

    public com.jeantessier.classreader.MethodHandle_info getBootstrapMethod() {
        return (MethodHandle_info) bootstrapMethods.getConstantPool().get(bootstrapMethodRef);
    }

    public Collection<Integer> getArgumentIndices() {
        return Collections.unmodifiableCollection(argumentIndices);
    }

    public com.jeantessier.classreader.ConstantPoolEntry getArgument(int index) {
        return getBootstrapMethods().getConstantPool().get(index);
    }

    public Collection<com.jeantessier.classreader.ConstantPoolEntry> getArguments() {
        return argumentIndices.stream()
                .map(this::getArgument)
                .toList();
    }

    public void accept(Visitor visitor) {
        visitor.visitBootstrapMethod(this);
    }
}
