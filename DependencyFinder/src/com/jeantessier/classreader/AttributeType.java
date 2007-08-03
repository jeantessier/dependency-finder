/*
 *  Copyright (c) 2001-2007, Jean Tessier
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

package com.jeantessier.classreader;

import java.io.*;

public enum AttributeType {
    CONSTANT_VALUE("ConstantValue") {
        public Attribute_info create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
            return new ConstantValue_attribute(classfile, owner, in);
        }
    },

    CODE("Code") {
        public Attribute_info create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
            return new Code_attribute(classfile, owner, in);
        }
    },

    EXCEPTIONS("Exceptions") {
        public Attribute_info create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
            return new Exceptions_attribute(classfile, owner, in);
        }
    },

    INNER_CLASSES("InnerClasses") {
        public Attribute_info create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
            return new InnerClasses_attribute(classfile, owner, in);
        }
    },

    ENCLOSING_METHOD("EnclosingMethod") {
        public Attribute_info create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
            return new EnclosingMethod_attribute(classfile, owner, in);
        }
    },

    SYNTHETIC("Synthetic") {
        public Attribute_info create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
            return new Synthetic_attribute(classfile, owner, in);
        }
    },

    SIGNATURE("Signature") {
        public Attribute_info create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
            return new Signature_attribute(classfile, owner, in);
        }
    },

    SOURCE_FILE("SourceFile") {
        public Attribute_info create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
            return new SourceFile_attribute(classfile, owner, in);
        }
    },

    SOURCE_DEBUG_EXTENSION("SourceDebugExtension") {
        public Attribute_info create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
            return new SourceDebugExtension_attribute(classfile, owner, in);
        }
    },

    LINE_NUMBER_TABLE("LineNumberTable") {
        public Attribute_info create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
            return new LineNumberTable_attribute(classfile, owner, in);
        }
    },

    LOCAL_VARIABLE_TABLE("LocalVariableTable") {
        public Attribute_info create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
            return new LocalVariableTable_attribute(classfile, owner, in);
        }
    },

    LOCAL_VARIABLE_TYPE_TABLE("LocalVariableTypeTable") {
        public Attribute_info create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
            return new LocalVariableTypeTable_attribute(classfile, owner, in);
        }
    },

    DEPRECATED("Deprecated") {
        public Attribute_info create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
            return new Deprecated_attribute(classfile, owner, in);
        }
    };

    private final String name;

    AttributeType(String name) {
        this.name = name;
    }

    public abstract Attribute_info create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException;

    public static AttributeType forName(String name) {
        AttributeType result = null;

        for (AttributeType attributeType : values()) {
            if (attributeType.name.equals(name)) {
                result = attributeType;
            }
        }
        
        return result;
    }
}
