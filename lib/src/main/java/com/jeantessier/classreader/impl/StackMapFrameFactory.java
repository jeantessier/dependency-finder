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

import org.apache.logging.log4j.*;

import java.io.DataInput;
import java.io.IOException;

public class StackMapFrameFactory {
    private final VerificationTypeInfoFactory verificationTypeInfoFactory;

    public StackMapFrameFactory(VerificationTypeInfoFactory verificationTypeInfoFactory) {
        this.verificationTypeInfoFactory = verificationTypeInfoFactory;
    }

    public StackMapFrame create(ConstantPool constantPool, DataInput in) throws IOException {
        StackMapFrame result;

        int tag = in.readUnsignedByte();
        FrameType frameType = FrameType.forTag(tag);
        LogManager.getLogger(getClass()).debug("tag {} ({})", tag, frameType);
        if (frameType != null) {
            result = frameType.create(tag, verificationTypeInfoFactory, constantPool, in);
        } else {
            String message = "Unknown frame type '" + tag + "'";
            LogManager.getLogger(AttributeFactory.class).warn(message);
            throw new IOException(message);
        }

        return result;
    }
}
