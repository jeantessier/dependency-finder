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

public class Module_attribute extends Attribute_info implements com.jeantessier.classreader.Module_attribute {
    private static final int ACC_OPEN = 0x0020;
    private static final int ACC_SYNTHETIC = 0x1000;
    private static final int ACC_MANDATED = 0x8000;

    private final int moduleNameIndex;
    private final int moduleFlags;
    private final int moduleVersionIndex;

    private final Collection<ModuleRequires> requires = new LinkedList<>();
    private final Collection<ModuleExports> exports = new LinkedList<>();
    private final Collection<ModuleOpens> opens = new LinkedList<>();
    private final Collection<ModuleUses> uses = new LinkedList<>();
    private final Collection<ModuleProvides> provides = new LinkedList<>();

    public Module_attribute(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
        super(constantPool, owner);

        int byteCount = in.readInt();
        LogManager.getLogger(getClass()).debug("Attribute length: " + byteCount);

        moduleNameIndex = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Module name: " + moduleNameIndex + " (" + getModuleName() + ")");

        moduleFlags = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Module flags: " + moduleFlags);

        moduleVersionIndex = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Module version: " + moduleVersionIndex + " (" + getModuleVersion() + ")");

        var numRequires = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Reading " + numRequires + " module requires ...");
        IntStream.range(0, numRequires).forEach(i -> {
            try {
                LogManager.getLogger(getClass()).debug("requires " + i + ":");
                requires.add(new ModuleRequires(constantPool, in));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        var numExports = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Reading " + numExports + " module exports ...");
        IntStream.range(0, numExports).forEach(i -> {
            try {
                LogManager.getLogger(getClass()).debug("exports " + i + ":");
                exports.add(new ModuleExports(constantPool, in));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        var numOpens = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Reading " + numOpens + " module opens ...");
        IntStream.range(0, numOpens).forEach(i -> {
            try {
                LogManager.getLogger(getClass()).debug("opens " + i + ":");
                opens.add(new ModuleOpens(constantPool, in));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        var numUses = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Reading " + numUses + " module uses ...");
        IntStream.range(0, numUses).forEach(i -> {
            try {
                LogManager.getLogger(getClass()).debug("uses " + i + ":");
                uses.add(new ModuleUses(constantPool, in));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        var numProvides = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Reading " + numProvides + " module provides ...");
        IntStream.range(0, numProvides).forEach(i -> {
            try {
                LogManager.getLogger(getClass()).debug("provides " + i + ":");
                provides.add(new ModuleProvides(constantPool, in));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public int getModuleNameIndex() {
        return moduleNameIndex;
    }

    public Module_info getRawModuleName() {
        return (Module_info) getConstantPool().get(getModuleNameIndex());
    }

    public String getModuleName() {
        return getRawModuleName().getName();
    }

    public int getModuleFlags() {
        return moduleFlags;
    }

    public boolean isOpen() {
        return (getModuleFlags() & ACC_OPEN) != 0;
    }

    public boolean isSynthetic() {
        return (getModuleFlags() & ACC_SYNTHETIC) != 0;
    }

    public boolean isMandated() {
        return (getModuleFlags() & ACC_MANDATED) != 0;
    }

    public boolean hasModuleVersion() {
        return moduleVersionIndex != 0;
    }

    public int getModuleVersionIndex() {
        return moduleVersionIndex;
    }

    public UTF8_info getRawModuleVersion() {
        return hasModuleVersion() ? (UTF8_info) getConstantPool().get(getModuleVersionIndex()) : null;
    }

    public String getModuleVersion() {
        return hasModuleVersion() ? getRawModuleVersion().getValue() : null;
    }

    public Collection<? extends ModuleRequires> getRequires() {
        return requires;
    }

    public Collection<? extends ModuleExports> getExports() {
        return exports;
    }

    public Collection<? extends ModuleOpens> getOpens() {
        return opens;
    }

    public Collection<? extends ModuleUses> getUses() {
        return uses;
    }

    public Collection<? extends ModuleProvides> getProvides() {
        return provides;
    }

    public String getAttributeName() {
        return AttributeType.MODULE.getAttributeName();
    }

    public void accept(Visitor visitor) {
        visitor.visitModule_attribute(this);
    }
}
