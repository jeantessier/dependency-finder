/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import org.apache.logging.log4j.*;

import com.jeantessier.classreader.*;

public class ExceptionHandler implements com.jeantessier.classreader.ExceptionHandler {
    private final Code_attribute code;
    private final int            startPC;
    private final int            endPC;
    private final int            handlerPC;
    private final int            catchTypeIndex;

    public ExceptionHandler(Code_attribute code, DataInput in) throws IOException {
        this.code = code;

        startPC = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("start PC: {}", startPC);

        endPC = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("end PC: {}", endPC);

        handlerPC = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("handler PC: {}", handlerPC);

        catchTypeIndex = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("catch type index: {} ({})", catchTypeIndex, getCatchType());
    }

    public Code_attribute getCode() {
        return code;
    }

    public int getStartPC() {
        return startPC;
    }

    public int getEndPC() {
        return endPC;
    }

    public int getHandlerPC() {
        return handlerPC;
    }

    public boolean hasCatchType() {
        return catchTypeIndex != 0;
    }

    public int getCatchTypeIndex() {
        return catchTypeIndex;
    }

    public Class_info getRawCatchType() {
        return (Class_info) code.getConstantPool().get(getCatchTypeIndex());
    }

    public String getCatchType() {
        String result = "<none>";

        if (hasCatchType()) {
            result = getRawCatchType().getName();
        }

        return result;
    }

    public String toString() {
        return "ExceptionHandler for " + getCatchType();
    }

    public void accept(Visitor visitor) {
        visitor.visitExceptionHandler(this);
    }
}
