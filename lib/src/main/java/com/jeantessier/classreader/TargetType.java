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

package com.jeantessier.classreader;

import java.util.Arrays;

public enum TargetType {
    PARAMETER_OF_CLASS(0x00, "type parameter declaration of generic class or interface"),
    PARAMETER_OF_GENERIC_METHOD(0x01, "type parameter declaration of generic method or constructor"),
    EXTENDS_OR_IMPLEMENTS(0x10, "type in extends or implements clause of class declaration (including the direct superclass or direct superinterface of an anonymous class declaration), or in extends clause of interface declaration"),
    BOUND_GENERIC_CLASS(0x11, "type in bound of type parameter declaration of generic class or interface"),
    BOUND_GENERIC_METHOD(0x12, "type in bound of type parameter declaration of generic method or constructor"),
    FIELD(0x13, "type in field or record component declaration"),
    RETURN_TYPE(0x14, "return type of method, or type of newly constructed object"),
    RECEIVER(0x15, "receiver type of method or constructor"),
    FORMAL_PARAMETER_OF_METHOD(0x16, "type in formal parameter declaration of method, constructor, or lambda expression"),
    THROWS_CLAUSE(0x17, "type in throws clause of method or constructor"),
    LOCAL_VARIABLE(0x40, "type in local variable declaration"),
    RESOURCE_VARIABLE(0x41, "type in resource variable declaration"),
    EXCEPTION_PARAMETER(0x42, "type in exception parameter declaration"),
    INSTANCEOF_EXPRESSION(0x43, "type in instanceof expression"),
    NEW_EXPRESSION(0x44, "type in new expression"),
    METHOD_REFERENCE_USING_NEW(0x45, "type in method reference expression using ::new"),
    METHOD_REFERENCE_USING_IDENTIFIER(0x46, "type in method reference expression using ::Identifier"),
    CAST_EXPRESSION(0x47, "type in cast expression"),
    ARGUMENT_FOR_GENERIC_CONSTRUCTOR(0x48, "type argument for generic constructor in new expression or explicit constructor invocation statement"),
    ARGUMENT_FOR_GENERIC_METHOD_INVOCATION(0x49, "type argument for generic method in method invocation expression"),
    ARGUMENT_FOR_GENERIC_CONSTRUCTOR_USING_NEW(0x4A, "type argument for generic constructor in method reference expression using ::new"),
    ARGUMENT_FOR_GENERIC_METHOD_REFERENCE_USING_IDENTIFIER(0x4B, "type argument for generic method in method reference expression using ::Identifier");

    private final int targetType;
    private final String description;

    TargetType(int targetType, String description) {
        this.targetType = targetType;
        this.description = description;
    }

    public int getTargetType() {
        return targetType;
    }

    public String getHexTargetType() {
        return String.format("0x%02X", getTargetType());
    }

    public String getDescription() {
        return description;
    }

    public static TargetType forTargetType(int tag) {
        return Arrays.stream(values())
                .filter(value -> value.getTargetType() == tag)
                .findFirst()
                .orElse(null);
    }
}
