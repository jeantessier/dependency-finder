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

public class ModuleMainClass_attribute extends Attribute_info implements com.jeantessier.classreader.ModuleMainClass_attribute {
    private final int mainClassIndex;

    public ModuleMainClass_attribute(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
        super(constantPool, owner);

        int byteCount = in.readInt();
        LogManager.getLogger(getClass()).debug("Attribute length: " + byteCount);

        mainClassIndex = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Main class: " + mainClassIndex + " (" + getMainClass() + ")");
    }

    public int getMainClassIndex() {
        return mainClassIndex;
    }

    public Class_info getRawMainClass() {
        return (Class_info) getConstantPool().get(getMainClassIndex());
    }

    public String getMainClass() {
        return getRawMainClass().getName();
    }

    public String toString() {
        return "Module Main Class \"" + getMainClass() + "\"";
    }

    public String getAttributeName() {
        return AttributeType.MODULE_MAIN_CLASS.getAttributeName();
    }

    public void accept(Visitor visitor) {
        visitor.visitModuleMainClass_attribute(this);
    }
}
