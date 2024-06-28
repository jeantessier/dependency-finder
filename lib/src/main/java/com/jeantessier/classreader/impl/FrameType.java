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

public enum FrameType {
    SAME(com.jeantessier.classreader.FrameType.SAME) {
        public StackMapFrame create(int frameType, VerificationTypeInfoFactory verificationTypeInfoFactory, ConstantPool constantPool, DataInput in) throws IOException {
            return new SameFrame(frameType, verificationTypeInfoFactory, constantPool, in);
        }
    },

    SAME_LOCALS_1_STACK_ITEM(com.jeantessier.classreader.FrameType.SAME_LOCALS_1_STACK_ITEM) {
        public StackMapFrame create(int frameType, VerificationTypeInfoFactory verificationTypeInfoFactory, ConstantPool constantPool, DataInput in) throws IOException {
            return new SameLocals1StackItemFrame(frameType, verificationTypeInfoFactory, constantPool, in);
        }
    },

    SAME_LOCALS_1_STACK_ITEM_EXTENDED(com.jeantessier.classreader.FrameType.SAME_LOCALS_1_STACK_ITEM_EXTENDED) {
        public StackMapFrame create(int frameType, VerificationTypeInfoFactory verificationTypeInfoFactory, ConstantPool constantPool, DataInput in) throws IOException {
            return new SameLocals1StackItemFrameExtended(frameType, verificationTypeInfoFactory, constantPool, in);
        }
    },

    CHOP(com.jeantessier.classreader.FrameType.CHOP) {
        public StackMapFrame create(int frameType, VerificationTypeInfoFactory verificationTypeInfoFactory, ConstantPool constantPool, DataInput in) throws IOException {
            return new ChopFrame(frameType, verificationTypeInfoFactory, constantPool, in);
        }
    },

    SAME_FRAME_EXTENDED(com.jeantessier.classreader.FrameType.SAME_FRAME_EXTENDED) {
        public StackMapFrame create(int frameType, VerificationTypeInfoFactory verificationTypeInfoFactory, ConstantPool constantPool, DataInput in) throws IOException {
            return new SameFrameExtended(frameType, verificationTypeInfoFactory, constantPool, in);
        }
    },

    APPEND(com.jeantessier.classreader.FrameType.APPEND) {
        public StackMapFrame create(int frameType, VerificationTypeInfoFactory verificationTypeInfoFactory, ConstantPool constantPool, DataInput in) throws IOException {
            return new AppendFrame(frameType, verificationTypeInfoFactory, constantPool, in);
        }
    },

    FULL_FRAME(com.jeantessier.classreader.FrameType.FULL_FRAME) {
        public StackMapFrame create(int frameType, VerificationTypeInfoFactory verificationTypeInfoFactory, ConstantPool constantPool, DataInput in) throws IOException {
            return new FullFrame(frameType, verificationTypeInfoFactory, constantPool, in);
        }
    };

    private final com.jeantessier.classreader.FrameType frameType;

    FrameType(com.jeantessier.classreader.FrameType frameType) {
        this.frameType = frameType;
    }

    public com.jeantessier.classreader.FrameType getFrameType() {
        return frameType;
    }

    public int getRangeStart() {
        return getFrameType().getRangeStart();
    }

    public int getRangeStop() {
        return getFrameType().getRangeStop();
    }

    public boolean inRange(int tag) {
        return getFrameType().inRange(tag);
    }

    public abstract StackMapFrame create(int frameType, VerificationTypeInfoFactory verificationTypeInfoFactory, ConstantPool constantPool, DataInput in) throws IOException;

    public static FrameType forTag(int tag) {
        return Arrays.stream(values())
                .filter(frameType -> frameType.inRange(tag))
                .findFirst()
                .orElse(null);
    }
}
