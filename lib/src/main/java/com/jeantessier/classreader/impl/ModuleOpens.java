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

import java.io.DataInput;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.IntStream;

public class ModuleOpens implements com.jeantessier.classreader.ModuleOpens {
    private static final int ACC_SYNTHETIC = 0x1000;
    private static final int ACC_MANDATED = 0x8000;

    private final ConstantPool constantPool;

    private final int opensIndex;
    private final int opensFlags;
    private final Collection<ModuleOpensTo> opensTos = new LinkedList<>();

    public ModuleOpens(ConstantPool constantPool, DataInput in) throws IOException {
        this.constantPool = constantPool;

        opensIndex = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Opens: " + opensIndex + " (" + getOpens() + ")");

        opensFlags = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Opens flags: " + opensFlags);

        var numOpensTo = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Reading " + numOpensTo + " opens to ...");
        IntStream.range(0, numOpensTo).forEach(i -> {
            try {
                LogManager.getLogger(getClass()).debug("opens to " + i + ":");
                opensTos.add(new ModuleOpensTo(constantPool, in));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }

    public int getOpensIndex() {
        return opensIndex;
    }

    public Package_info getRawOpens() {
        return (Package_info) getConstantPool().get(getOpensIndex());
    }

    public String getOpens() {
        return getRawOpens().getName();
    }

    public int getOpensFlags() {
        return opensFlags;
    }

    public boolean isSynthetic() {
        return (getOpensFlags() & ACC_SYNTHETIC) != 0;
    }

    public boolean isMandated() {
        return (getOpensFlags() & ACC_MANDATED) != 0;
    }

    public Collection<? extends ModuleOpensTo> getOpensTos() {
        return opensTos;
    }

    public void accept(Visitor visitor) {
        visitor.visitModuleOpens(this);
    }
}
