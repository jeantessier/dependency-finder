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

import com.jeantessier.classreader.Visitor;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class FullFrame extends StackMapFrame implements com.jeantessier.classreader.FullFrame {
    private final int offsetDelta;
    private final int numberOfLocals;
    private final Collection<VerificationTypeInfo> locals = new ArrayList<>();
    private final int numberOfStackItems;
    private final Collection<VerificationTypeInfo> stack = new ArrayList<>();

    public FullFrame(int frameType, VerificationTypeInfoFactory verificationTypeInfoFactory, ConstantPool constantPool, DataInput in) throws IOException {
        super(frameType);

        offsetDelta = in.readUnsignedShort();

        numberOfLocals = in.readUnsignedShort();
        for (int i=0; i<numberOfLocals; i++) {
            locals.add(verificationTypeInfoFactory.create(constantPool, in));
        }

        numberOfStackItems = in.readUnsignedShort();
        for (int i=0; i<numberOfStackItems; i++) {
            stack.add(verificationTypeInfoFactory.create(constantPool, in));
        }
    }

    public com.jeantessier.classreader.FrameType getType() {
        return FrameType.SAME.getFrameType();
    }

    public int getOffsetDelta() {
        return offsetDelta;
    }

    public Collection<? extends com.jeantessier.classreader.VerificationTypeInfo> getLocals() {
        return locals;
    }
    public Collection<? extends com.jeantessier.classreader.VerificationTypeInfo> getStack() {
        return stack;
    }

    public void accept(Visitor visitor) {
        visitor.visitFullFrame(this);
    }
}
