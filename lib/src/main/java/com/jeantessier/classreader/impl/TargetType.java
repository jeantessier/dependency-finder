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

import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;

public enum TargetType {
    PARAMETER_OF_CLASS(com.jeantessier.classreader.TargetType.PARAMETER_OF_CLASS) {
        public Target_info create(DataInput in) throws IOException {
            return new TypeParameterTarget(this, in);
        }
    },

    PARAMETER_OF_GENERIC_METHOD(com.jeantessier.classreader.TargetType.PARAMETER_OF_GENERIC_METHOD) {
        public Target_info create(DataInput in) throws IOException {
            return new TypeParameterTarget(this, in);
        }
    },

    EXTENDS_OR_IMPLEMENTS(com.jeantessier.classreader.TargetType.EXTENDS_OR_IMPLEMENTS) {
        public Target_info create(DataInput in) throws IOException {
            return new SupertypeTarget(this, in);
        }
    },

    BOUND_GENERIC_CLASS(com.jeantessier.classreader.TargetType.BOUND_GENERIC_CLASS) {
        public Target_info create(DataInput in) throws IOException {
            return new TypeParameterBoundTarget(this, in);
        }
    },

    BOUND_GENERIC_METHOD(com.jeantessier.classreader.TargetType.BOUND_GENERIC_METHOD) {
        public Target_info create(DataInput in) throws IOException {
            return new TypeParameterBoundTarget(this, in);
        }
    },

    FIELD(com.jeantessier.classreader.TargetType.FIELD) {
        public Target_info create(DataInput in) throws IOException {
            return new EmptyTarget(this, in);
        }
    },

    RETURN_TYPE(com.jeantessier.classreader.TargetType.RETURN_TYPE) {
        public Target_info create(DataInput in) throws IOException {
            return new EmptyTarget(this, in);
        }
    },

    RECEIVER(com.jeantessier.classreader.TargetType.RECEIVER) {
        public Target_info create(DataInput in) throws IOException {
            return new EmptyTarget(this, in);
        }
    },

    FORMAL_PARAMETER_OF_METHOD(com.jeantessier.classreader.TargetType.FORMAL_PARAMETER_OF_METHOD) {
        public Target_info create(DataInput in) throws IOException {
            return new FormalParameterTarget(this, in);
        }
    },

    THROWS_CLAUSE(com.jeantessier.classreader.TargetType.THROWS_CLAUSE) {
        public Target_info create(DataInput in) throws IOException {
            return new ThrowsTarget(this, in);
        }
    },

    LOCAL_VARIABLE(com.jeantessier.classreader.TargetType.LOCAL_VARIABLE) {
        public Target_info create(DataInput in) throws IOException {
            return new LocalvarTarget(this, in);
        }
    },

    RESOURCE_VARIABLE(com.jeantessier.classreader.TargetType.RESOURCE_VARIABLE) {
        public Target_info create(DataInput in) throws IOException {
            return new LocalvarTarget(this, in);
        }
    },

    EXCEPTION_PARAMETER(com.jeantessier.classreader.TargetType.EXCEPTION_PARAMETER) {
        public Target_info create(DataInput in) throws IOException {
            return new CatchTarget(this, in);
        }
    },

    INSTANCEOF_EXPRESSION(com.jeantessier.classreader.TargetType.INSTANCEOF_EXPRESSION) {
        public Target_info create(DataInput in) throws IOException {
            return new OffsetTarget(this, in);
        }
    },

    NEW_EXPRESSION(com.jeantessier.classreader.TargetType.NEW_EXPRESSION) {
        public Target_info create(DataInput in) throws IOException {
            return new OffsetTarget(this, in);
        }
    },

    METHOD_REFERENCE_USING_NEW(com.jeantessier.classreader.TargetType.METHOD_REFERENCE_USING_NEW) {
        public Target_info create(DataInput in) throws IOException {
            return new OffsetTarget(this, in);
        }
    },

    METHOD_REFERENCE_USING_IDENTIFIER(com.jeantessier.classreader.TargetType.METHOD_REFERENCE_USING_IDENTIFIER) {
        public Target_info create(DataInput in) throws IOException {
            return new OffsetTarget(this, in);
        }
    },

    CAST_EXPRESSION(com.jeantessier.classreader.TargetType.CAST_EXPRESSION) {
        public Target_info create(DataInput in) throws IOException {
            return new TypeArgumentTarget(this, in);
        }
    },

    ARGUMENT_FOR_GENERIC_CONSTRUCTOR(com.jeantessier.classreader.TargetType.ARGUMENT_FOR_GENERIC_CONSTRUCTOR) {
        public Target_info create(DataInput in) throws IOException {
            return new TypeArgumentTarget(this, in);
        }
    },

    ARGUMENT_FOR_GENERIC_METHOD_INVOCATION(com.jeantessier.classreader.TargetType.ARGUMENT_FOR_GENERIC_METHOD_INVOCATION) {
        public Target_info create(DataInput in) throws IOException {
            return new TypeArgumentTarget(this, in);
        }
    },

    ARGUMENT_FOR_GENERIC_CONSTRUCTOR_USING_NEW(com.jeantessier.classreader.TargetType.ARGUMENT_FOR_GENERIC_CONSTRUCTOR_USING_NEW) {
        public Target_info create(DataInput in) throws IOException {
            return new TypeArgumentTarget(this, in);
        }
    },

    ARGUMENT_FOR_GENERIC_METHOD_REFERENCE_USING_IDENTIFIER(com.jeantessier.classreader.TargetType.ARGUMENT_FOR_GENERIC_METHOD_REFERENCE_USING_IDENTIFIER) {
        public Target_info create(DataInput in) throws IOException {
            return new TypeArgumentTarget(this, in);
        }
    };

    private final com.jeantessier.classreader.TargetType targetType;

    TargetType(com.jeantessier.classreader.TargetType targetType) {
        this.targetType = targetType;
    }

    public com.jeantessier.classreader.TargetType getTargetType() {
        return targetType;
    }

    public String getHexTargetType() {
        return getTargetType().getHexTargetType();
    }

    public String getDescription() {
        return getTargetType().getDescription();
    }

    public abstract Target_info create(DataInput in) throws IOException;

    public static TargetType forTargetType(int tag) {
        return Arrays.stream(values())
                .filter(value -> value.getTargetType().getTargetType() == tag)
                .findFirst()
                .orElse(null);
    }
}
