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

package com.jeantessier.classreader;

public enum AttributeType {
    CONSTANT_VALUE("ConstantValue"),
    CODE("Code"),
    EXCEPTIONS("Exceptions"),
    INNER_CLASSES("InnerClasses"),
    ENCLOSING_METHOD("EnclosingMethod"),
    SYNTHETIC("Synthetic"),
    SIGNATURE("Signature"),
    SOURCE_FILE("SourceFile"),
    SOURCE_DEBUG_EXTENSION("SourceDebugExtension"),
    LINE_NUMBER_TABLE("LineNumberTable"),
    LOCAL_VARIABLE_TABLE("LocalVariableTable"),
    LOCAL_VARIABLE_TYPE_TABLE("LocalVariableTypeTable"),
    DEPRECATED("Deprecated"),
    RUNTIME_VISIBLE_ANNOTATIONS("RuntimeVisibleAnnotations"),
    RUNTIME_INVISIBLE_ANNOTATIONS("RuntimeInvisibleAnnotations"),
    RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS("RuntimeVisibleParameterAnnotations"),
    RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS("RuntimeInvisibleParameterAnnotations"),
    ANNOTATION_DEFAULT("AnnotationDefault");

    private final String attributeName;

    AttributeType(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public static AttributeType forName(String attributeName) {
        AttributeType result = null;

        for (AttributeType attributeType : values()) {
            if (attributeType.attributeName.equals(attributeName)) {
                result = attributeType;
            }
        }
        
        return result;
    }
}
