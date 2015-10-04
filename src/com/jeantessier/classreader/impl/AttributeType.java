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

import com.jeantessier.classreader.*;

public enum AttributeType {
    CONSTANT_VALUE(com.jeantessier.classreader.AttributeType.CONSTANT_VALUE) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new ConstantValue_attribute(constantPool, owner, in);
        }
    },

    CODE(com.jeantessier.classreader.AttributeType.CODE) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new Code_attribute(constantPool, owner, in);
        }
    },

    EXCEPTIONS(com.jeantessier.classreader.AttributeType.EXCEPTIONS) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new Exceptions_attribute(constantPool, owner, in);
        }
    },

    INNER_CLASSES(com.jeantessier.classreader.AttributeType.INNER_CLASSES) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new InnerClasses_attribute(constantPool, owner, in);
        }
    },

    ENCLOSING_METHOD(com.jeantessier.classreader.AttributeType.ENCLOSING_METHOD) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new EnclosingMethod_attribute(constantPool, owner, in);
        }
    },

    SYNTHETIC(com.jeantessier.classreader.AttributeType.SYNTHETIC) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new Synthetic_attribute(constantPool, owner, in);
        }
    },

    SIGNATURE(com.jeantessier.classreader.AttributeType.SIGNATURE) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new Signature_attribute(constantPool, owner, in);
        }
    },

    SOURCE_FILE(com.jeantessier.classreader.AttributeType.SOURCE_FILE) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new SourceFile_attribute(constantPool, owner, in);
        }
    },

    SOURCE_DEBUG_EXTENSION(com.jeantessier.classreader.AttributeType.SOURCE_DEBUG_EXTENSION) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new SourceDebugExtension_attribute(constantPool, owner, in);
        }
    },

    LINE_NUMBER_TABLE(com.jeantessier.classreader.AttributeType.LINE_NUMBER_TABLE) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new LineNumberTable_attribute(constantPool, owner, in);
        }
    },

    LOCAL_VARIABLE_TABLE(com.jeantessier.classreader.AttributeType.LOCAL_VARIABLE_TABLE) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new LocalVariableTable_attribute(constantPool, owner, in);
        }
    },

    LOCAL_VARIABLE_TYPE_TABLE(com.jeantessier.classreader.AttributeType.LOCAL_VARIABLE_TYPE_TABLE) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new LocalVariableTypeTable_attribute(constantPool, owner, in);
        }
    },

    DEPRECATED(com.jeantessier.classreader.AttributeType.DEPRECATED) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new Deprecated_attribute(constantPool, owner, in);
        }
    },

    RUNTIME_VISIBLE_ANNOTATIONS(com.jeantessier.classreader.AttributeType.RUNTIME_VISIBLE_ANNOTATIONS) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new RuntimeVisibleAnnotations_attribute(constantPool, owner, in);
        }
    },

    RUNTIME_INVISIBLE_ANNOTATIONS(com.jeantessier.classreader.AttributeType.RUNTIME_INVISIBLE_ANNOTATIONS) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new RuntimeInvisibleAnnotations_attribute(constantPool, owner, in);
        }
    },

    RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS(com.jeantessier.classreader.AttributeType.RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new RuntimeVisibleParameterAnnotations_attribute(constantPool, owner, in);
        }
    },

    RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS(com.jeantessier.classreader.AttributeType.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new RuntimeInvisibleParameterAnnotations_attribute(constantPool, owner, in);
        }
    },

    ANNOTATION_DEFAULT(com.jeantessier.classreader.AttributeType.ANNOTATION_DEFAULT) {
        public Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
            return new AnnotationDefault_attribute(constantPool, owner, in);
        }
    };

    private final com.jeantessier.classreader.AttributeType attributeType;

    AttributeType(com.jeantessier.classreader.AttributeType attributeType) {
        this.attributeType = attributeType;
    }

    public String getAttributeName() {
        return attributeType.getAttributeName();
    }

    public abstract Attribute_info create(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException;

    public static AttributeType forName(String attributeName) {
        AttributeType result = null;

        for (AttributeType attributeType : values()) {
            if (attributeType.getAttributeName().equals(attributeName)) {
                result = attributeType;
            }
        }
        
        return result;
    }
}
