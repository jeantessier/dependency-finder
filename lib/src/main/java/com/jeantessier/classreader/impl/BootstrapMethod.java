/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

public class BootstrapMethod implements com.jeantessier.classreader.BootstrapMethod {
    private final BootstrapMethods_attribute bootstrapMethods;
    private final int bootstrapMethodRef;
    private final Collection<Integer> argumentIndices = new LinkedList<>();

    public BootstrapMethod(BootstrapMethods_attribute bootstrapMethods, DataInput in) throws IOException {
        this.bootstrapMethods = bootstrapMethods;

        bootstrapMethodRef = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Bootstrap method ref: {} ({})", bootstrapMethodRef, getBootstrapMethod());

        var numArgument = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Reading {} argument(s) ...", numArgument);
        for (int i=0; i<numArgument; i++) {
            LogManager.getLogger(getClass()).debug("Reading argument {}", i);
            var argumentIndex = in.readUnsignedShort();
            var argument = bootstrapMethods.getConstantPool().get(argumentIndex);
            LogManager.getLogger(getClass()).debug("Argument {}: {} ({})", i, argumentIndex, argument);
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
