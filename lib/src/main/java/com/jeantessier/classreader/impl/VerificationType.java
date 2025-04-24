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
import java.util.*;

public enum VerificationType {
    TOP(com.jeantessier.classreader.VerificationType.TOP) {
        public VerificationTypeInfo create(ConstantPool constantPool, DataInput in) throws IOException {
            return new TopVariableInfo(constantPool, in);
        }
    },

    INTEGER(com.jeantessier.classreader.VerificationType.INTEGER) {
        public VerificationTypeInfo create(ConstantPool constantPool, DataInput in) throws IOException {
            return new IntegerVariableInfo(constantPool, in);
        }
    },

    FLOAT(com.jeantessier.classreader.VerificationType.FLOAT) {
        public VerificationTypeInfo create(ConstantPool constantPool, DataInput in) throws IOException {
            return new FloatVariableInfo(constantPool, in);
        }
    },

    LONG(com.jeantessier.classreader.VerificationType.LONG) {
        public VerificationTypeInfo create(ConstantPool constantPool, DataInput in) throws IOException {
            return new LongVariableInfo(constantPool, in);
        }
    },

    DOUBLE(com.jeantessier.classreader.VerificationType.DOUBLE) {
        public VerificationTypeInfo create(ConstantPool constantPool, DataInput in) throws IOException {
            return new DoubleVariableInfo(constantPool, in);
        }
    },

    NULL(com.jeantessier.classreader.VerificationType.NULL) {
        public VerificationTypeInfo create(ConstantPool constantPool, DataInput in) throws IOException {
            return new NullVariableInfo(constantPool, in);
        }
    },

    UNINITIALIZED_THIS(com.jeantessier.classreader.VerificationType.UNINITIALIZED_THIS) {
        public VerificationTypeInfo create(ConstantPool constantPool, DataInput in) throws IOException {
            return new UninitializedThisVariableInfo(constantPool, in);
        }
    },

    OBJECT(com.jeantessier.classreader.VerificationType.OBJECT) {
        public VerificationTypeInfo create(ConstantPool constantPool, DataInput in) throws IOException {
            return new ObjectVariableInfo(constantPool, in);
        }
    },

    UNINITIALIZED(com.jeantessier.classreader.VerificationType.UNINITIALIZED) {
        public VerificationTypeInfo create(ConstantPool constantPool, DataInput in) throws IOException {
            return new UninitializedVariableInfo(constantPool, in);
        }
    };

    private final com.jeantessier.classreader.VerificationType verificationType;

    VerificationType(com.jeantessier.classreader.VerificationType verificationType) {
        this.verificationType = verificationType;
    }

    public int getTag() {
        return verificationType.getTag();
    }

    public abstract VerificationTypeInfo create(ConstantPool constantPool, DataInput in) throws IOException;

    public static VerificationType forTag(int tag) {
        return Arrays.stream(values())
                .filter(verificationType -> verificationType.getTag() == tag)
                .findFirst()
                .orElse(null);
    }
}
