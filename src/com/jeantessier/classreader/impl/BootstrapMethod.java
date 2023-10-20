package com.jeantessier.classreader.impl;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

public class BootstrapMethod implements com.jeantessier.classreader.BootstrapMethod {
    private final BootstrapMethods_attribute bootstrapMethods;
    private final MethodHandle_info bootstrapMethod;
    private final Collection<com.jeantessier.classreader.ConstantPoolEntry> arguments = new LinkedList<>();

    public BootstrapMethod(BootstrapMethods_attribute bootstrapMethods, DataInput in) throws IOException {
        this.bootstrapMethods = bootstrapMethods;

        var bootstrapMethodRef = in.readUnsignedShort();
        bootstrapMethod = (MethodHandle_info) bootstrapMethods.getConstantPool().get(bootstrapMethodRef);
        Logger.getLogger(getClass()).debug("Bootstrap method ref: " + bootstrapMethodRef + " (" + bootstrapMethod + ")");

        var numArgument = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + numArgument + " argument(s) ...");
        for (int i=0; i<numArgument; i++) {
            Logger.getLogger(getClass()).debug("Reading argument " + i);
            var argumentIndex = in.readUnsignedShort();
            var argument = bootstrapMethods.getConstantPool().get(argumentIndex);
            Logger.getLogger(getClass()).debug("Argument " + i + ": " + argumentIndex + " (" + argument + ")");
            arguments.add(argument);
        }

    }

    public BootstrapMethods_attribute getBootstrapMethods() {
        return bootstrapMethods;
    }

    public com.jeantessier.classreader.MethodHandle_info getBootstrapMethod() {
        return bootstrapMethod;
    }

    public Collection<com.jeantessier.classreader.ConstantPoolEntry> getArguments() {
        return arguments;
    }

    public void accept(Visitor visitor) {
        visitor.visitBootstrapMethod(this);
    }
}
