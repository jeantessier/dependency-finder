package com.jeantessier.classreader.impl;

import com.jeantessier.classreader.*;
import org.apache.log4j.Logger;

import java.io.DataInput;
import java.io.IOException;
import java.util.*;

public class BootstrapMethod implements com.jeantessier.classreader.BootstrapMethod {
    private final Collection<com.jeantessier.classreader.ConstantPoolEntry> arguments = new LinkedList<>();

    public BootstrapMethod(ConstantPool constantPool, DataInput in) throws IOException {
        var numArgument = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + numArgument + " argument(s) ...");
        for (int i=0; i<numArgument; i++) {
            Logger.getLogger(getClass()).debug("Reading argument " + i);
            var argumentIndex = in.readUnsignedShort();
            var argument = constantPool.get(argumentIndex);
            Logger.getLogger(getClass()).debug("Argument " + i + ": " + argumentIndex + " (" + argument + ")");
            arguments.add(argument);
        }

    }

    public Collection<com.jeantessier.classreader.ConstantPoolEntry> getArguments() {
        return arguments;
    }

    public void accept(Visitor visitor) {
        visitor.visitBootstrapMethod(this);
    }
}
