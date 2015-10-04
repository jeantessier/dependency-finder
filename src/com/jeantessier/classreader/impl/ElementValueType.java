/*
 *  Copyright (c) 2001-2009, Jean Tessier
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

public enum ElementValueType {
    BYTE(com.jeantessier.classreader.ElementValueType.BYTE) {
        public ElementValue create(ConstantPool constantPool, DataInput in) throws IOException {
            return new ByteConstantElementValue(constantPool, in);
        }
    },

    CHAR(com.jeantessier.classreader.ElementValueType.CHAR) {
        public ElementValue create(ConstantPool constantPool, DataInput in) throws IOException {
            return new CharConstantElementValue(constantPool, in);
        }
    },

    DOUBLE(com.jeantessier.classreader.ElementValueType.DOUBLE) {
        public ElementValue create(ConstantPool constantPool, DataInput in) throws IOException {
            return new DoubleConstantElementValue(constantPool, in);
        }
    },

    FLOAT(com.jeantessier.classreader.ElementValueType.FLOAT) {
        public ElementValue create(ConstantPool constantPool, DataInput in) throws IOException {
            return new FloatConstantElementValue(constantPool, in);
        }
    },

    INTEGER(com.jeantessier.classreader.ElementValueType.INTEGER) {
        public ElementValue create(ConstantPool constantPool, DataInput in) throws IOException {
            return new IntegerConstantElementValue(constantPool, in);
        }
    },

    LONG(com.jeantessier.classreader.ElementValueType.LONG) {
        public ElementValue create(ConstantPool constantPool, DataInput in) throws IOException {
            return new LongConstantElementValue(constantPool, in);
        }
    },

    SHORT(com.jeantessier.classreader.ElementValueType.SHORT) {
        public ElementValue create(ConstantPool constantPool, DataInput in) throws IOException {
            return new ShortConstantElementValue(constantPool, in);
        }
    },

    BOOLEAN(com.jeantessier.classreader.ElementValueType.BOOLEAN) {
        public ElementValue create(ConstantPool constantPool, DataInput in) throws IOException {
            return new BooleanConstantElementValue(constantPool, in);
        }
    },

    STRING(com.jeantessier.classreader.ElementValueType.STRING) {
        public ElementValue create(ConstantPool constantPool, DataInput in) throws IOException {
            return new StringConstantElementValue(constantPool, in);
        }
    },

    ENUM(com.jeantessier.classreader.ElementValueType.ENUM) {
        public ElementValue create(ConstantPool constantPool, DataInput in) throws IOException {
            return new EnumElementValue(constantPool, in);
        }
    },

    CLASS(com.jeantessier.classreader.ElementValueType.CLASS) {
        public ElementValue create(ConstantPool constantPool, DataInput in) throws IOException {
            return new ClassElementValue(constantPool, in);
        }
    },

    ANNOTATION(com.jeantessier.classreader.ElementValueType.ANNOTATION) {
        public ElementValue create(ConstantPool constantPool, DataInput in) throws IOException {
            return new AnnotationElementValue(constantPool, in);
        }
    },

    ARRAY(com.jeantessier.classreader.ElementValueType.ARRAY) {
        public ElementValue create(ConstantPool constantPool, DataInput in) throws IOException {
            return new ArrayElementValue(constantPool, in);
        }
    };

    private final com.jeantessier.classreader.ElementValueType elementValueType;

    ElementValueType(com.jeantessier.classreader.ElementValueType elementValueType) {
        this.elementValueType = elementValueType;
    }

    public char getTag() {
        return elementValueType.getTag();
    }

    public abstract ElementValue create(ConstantPool constantPool, DataInput in) throws IOException;

    public static ElementValueType forTag(char tag) {
        ElementValueType result = null;

        for (ElementValueType elementValueType : values()) {
            if (elementValueType.getTag() == tag) {
                result = elementValueType;
            }
        }

        return result;
    }
}
