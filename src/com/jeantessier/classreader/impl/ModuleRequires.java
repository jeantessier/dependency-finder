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
import org.apache.log4j.Logger;

import java.io.*;

public class ModuleRequires implements com.jeantessier.classreader.ModuleRequires {
    private static final int ACC_TRANSITIVE = 0x0020;
    private static final int ACC_STATIC_PHASE = 0x0040;
    private static final int ACC_SYNTHETIC = 0x1000;
    private static final int ACC_MANDATED = 0x8000;

    private final ConstantPool constantPool;

    private final int requiresIndex;
    private final int requiresFlags;
    private final int requiresVersionIndex;

    public ModuleRequires(ConstantPool constantPool, DataInput in) throws IOException {
        this.constantPool = constantPool;

        requiresIndex = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Requires: " + requiresIndex + " (" + getRequires() + ")");

        requiresFlags = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Requires flags: " + requiresFlags);

        requiresVersionIndex = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Requires version: " + requiresVersionIndex + " (" + getRequiresVersion() + ")");
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }

    public int getRequiresIndex() {
        return requiresIndex;
    }

    public Module_info getRawRequires() {
        return (Module_info) getConstantPool().get(getRequiresIndex());
    }

    public String getRequires() {
        return getRawRequires().getName();
    }

    public int getRequiresFlags() {
        return requiresFlags;
    }

    public boolean isTransitive() {
        return (getRequiresFlags() & ACC_TRANSITIVE) != 0;
    }

    public boolean isStaticPhase() {
        return (getRequiresFlags() & ACC_STATIC_PHASE) != 0;
    }

    public boolean isSynthetic() {
        return (getRequiresFlags() & ACC_SYNTHETIC) != 0;
    }

    public boolean isMandated() {
        return (getRequiresFlags() & ACC_MANDATED) != 0;
    }

    public int getRequiresVersionIndex() {
        return requiresVersionIndex;
    }

    public UTF8_info getRawRequiresVersion() {
        return getRequiresVersionIndex() != 0 ? (UTF8_info) getConstantPool().get(getRequiresVersionIndex()) : null;
    }

    public String getRequiresVersion() {
        return getRequiresVersionIndex() != 0 ? getRawRequiresVersion().getValue() : null;
    }

    public void accept(Visitor visitor) {
        visitor.visitModuleRequires(this);
    }
}
