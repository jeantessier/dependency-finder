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

public abstract class ConstantPoolEntry implements com.jeantessier.classreader.ConstantPoolEntry {
    public static final byte CONSTANT_Class = 7;
    public static final byte CONSTANT_Fieldref = 9;
    public static final byte CONSTANT_Methodref = 10;
    public static final byte CONSTANT_InterfaceMethodref = 11;
    public static final byte CONSTANT_String = 8;
    public static final byte CONSTANT_Integer = 3;
    public static final byte CONSTANT_Float = 4;
    public static final byte CONSTANT_Long = 5;
    public static final byte CONSTANT_Double = 6;
    public static final byte CONSTANT_NameAndType = 12;
    public static final byte CONSTANT_Utf8 = 1;
    public static final byte CONSTANT_MethodHandle = 15;
    public static final byte CONSTANT_MethodType = 16;
    public static final byte CONSTANT_Dynamic = 17;
    public static final byte CONSTANT_InvokeDynamic = 18;
    public static final byte CONSTANT_Module = 19;
    public static final byte CONSTANT_Package = 20;

    private final ConstantPool constantPool;

    protected ConstantPoolEntry(ConstantPool constantPool) {
        this.constantPool = constantPool;
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }

    public static String stringValueOf(byte tag) {
        return switch (tag) {
            case CONSTANT_Class -> "CONSTANT_Class";
            case CONSTANT_Fieldref -> "CONSTANT_Fieldref";
            case CONSTANT_Methodref -> "CONSTANT_Methodref";
            case CONSTANT_InterfaceMethodref -> "CONSTANT_InterfaceMethodref";
            case CONSTANT_String -> "CONSTANT_String";
            case CONSTANT_Integer -> "CONSTANT_Integer";
            case CONSTANT_Float -> "CONSTANT_Float";
            case CONSTANT_Long -> "CONSTANT_Long";
            case CONSTANT_Double -> "CONSTANT_Double";
            case CONSTANT_NameAndType -> "CONSTANT_NameAndType";
            case CONSTANT_Utf8 -> "CONSTANT_Utf8";
            case CONSTANT_MethodHandle -> "CONSTANT_MethodHandle";
            case CONSTANT_MethodType -> "CONSTANT_MethodType";
            case CONSTANT_Dynamic -> "CONSTANT_Dynamic";
            case CONSTANT_InvokeDynamic -> "CONSTANT_InvokeDynamic";
            case CONSTANT_Module -> "CONSTANT_Module";
            case CONSTANT_Package -> "CONSTANT_Package";
            default -> "<unidentified tag " + tag + ">";
        };
    }
}
