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

    SYNTHETIC("Synthetic") {
        public Attribute_info create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
            return new Synthetic_attribute(classfile, owner, in);
        }
    },

    SOURCE_FILE("SourceFile") {
        public Attribute_info create(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
            return new SourceFile_attribute(classfile, owner, in);
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

        for (AttributeType attributeName : values()) {
            if (attributeName.name.equals(name)) {
                result = attributeName;
            }
        }
        
        return result;
    }
}
