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

import com.jeantessier.classreader.BootstrapMethodFinder;
import com.jeantessier.classreader.LocalVariableFinder;
import com.jeantessier.classreader.Visitor;

import java.util.*;

public class Instruction implements com.jeantessier.classreader.Instruction {
    private static final int NB_OPCODES = 0x100;

    private static final String[] OPCODE = new String[NB_OPCODES];
    private static final int[] LENGTH = new int[NB_OPCODES];

    static {
        OPCODE[0x00] = "nop";
        LENGTH[0x00] = 1;
        OPCODE[0x01] = "aconst_null";
        LENGTH[0x01] = 1;
        OPCODE[0x02] = "iconst_m1";
        LENGTH[0x02] = 1;
        OPCODE[0x03] = "iconst_0";
        LENGTH[0x03] = 1;
        OPCODE[0x04] = "iconst_1";
        LENGTH[0x04] = 1;
        OPCODE[0x05] = "iconst_2";
        LENGTH[0x05] = 1;
        OPCODE[0x06] = "iconst_3";
        LENGTH[0x06] = 1;
        OPCODE[0x07] = "iconst_4";
        LENGTH[0x07] = 1;
        OPCODE[0x08] = "iconst_5";
        LENGTH[0x08] = 1;
        OPCODE[0x09] = "lconst_0";
        LENGTH[0x09] = 1;
        OPCODE[0x0a] = "lconst_1";
        LENGTH[0x0a] = 1;
        OPCODE[0x0b] = "fconst_0";
        LENGTH[0x0b] = 1;
        OPCODE[0x0c] = "fconst_1";
        LENGTH[0x0c] = 1;
        OPCODE[0x0d] = "fconst_2";
        LENGTH[0x0d] = 1;
        OPCODE[0x0e] = "dconst_0";
        LENGTH[0x0e] = 1;
        OPCODE[0x0f] = "dconst_1";
        LENGTH[0x0f] = 1;

        OPCODE[0x10] = "bipush";
        LENGTH[0x10] = 2;
        OPCODE[0x11] = "sipush";
        LENGTH[0x11] = 3;
        OPCODE[0x12] = "ldc";
        LENGTH[0x12] = 2;
        OPCODE[0x13] = "ldc_w";
        LENGTH[0x13] = 3;
        OPCODE[0x14] = "ldc2_w";
        LENGTH[0x14] = 3;
        OPCODE[0x15] = "iload";
        LENGTH[0x15] = 2;
        OPCODE[0x16] = "lload";
        LENGTH[0x16] = 2;
        OPCODE[0x17] = "fload";
        LENGTH[0x17] = 2;
        OPCODE[0x18] = "dload";
        LENGTH[0x18] = 2;
        OPCODE[0x19] = "aload";
        LENGTH[0x19] = 2;
        OPCODE[0x1a] = "iload_0";
        LENGTH[0x1a] = 1;
        OPCODE[0x1b] = "iload_1";
        LENGTH[0x1b] = 1;
        OPCODE[0x1c] = "iload_2";
        LENGTH[0x1c] = 1;
        OPCODE[0x1d] = "iload_3";
        LENGTH[0x1d] = 1;
        OPCODE[0x1e] = "lload_0";
        LENGTH[0x1e] = 1;
        OPCODE[0x1f] = "lload_1";
        LENGTH[0x1f] = 1;

        OPCODE[0x20] = "lload_2";
        LENGTH[0x20] = 1;
        OPCODE[0x21] = "lload_3";
        LENGTH[0x21] = 1;
        OPCODE[0x22] = "fload_0";
        LENGTH[0x22] = 1;
        OPCODE[0x23] = "fload_1";
        LENGTH[0x23] = 1;
        OPCODE[0x24] = "fload_2";
        LENGTH[0x24] = 1;
        OPCODE[0x25] = "fload_3";
        LENGTH[0x25] = 1;
        OPCODE[0x26] = "dload_0";
        LENGTH[0x26] = 1;
        OPCODE[0x27] = "dload_1";
        LENGTH[0x27] = 1;
        OPCODE[0x28] = "dload_2";
        LENGTH[0x28] = 1;
        OPCODE[0x29] = "dload_3";
        LENGTH[0x29] = 1;
        OPCODE[0x2a] = "aload_0";
        LENGTH[0x2a] = 1;
        OPCODE[0x2b] = "aload_1";
        LENGTH[0x2b] = 1;
        OPCODE[0x2c] = "aload_2";
        LENGTH[0x2c] = 1;
        OPCODE[0x2d] = "aload_3";
        LENGTH[0x2d] = 1;
        OPCODE[0x2e] = "iaload";
        LENGTH[0x2e] = 1;
        OPCODE[0x2f] = "laload";
        LENGTH[0x2f] = 1;

        OPCODE[0x30] = "faload";
        LENGTH[0x30] = 1;
        OPCODE[0x31] = "daload";
        LENGTH[0x31] = 1;
        OPCODE[0x32] = "aaload";
        LENGTH[0x32] = 1;
        OPCODE[0x33] = "baload";
        LENGTH[0x33] = 1;
        OPCODE[0x34] = "caload";
        LENGTH[0x34] = 1;
        OPCODE[0x35] = "saload";
        LENGTH[0x35] = 1;
        OPCODE[0x36] = "istore";
        LENGTH[0x36] = 2;
        OPCODE[0x37] = "lstore";
        LENGTH[0x37] = 2;
        OPCODE[0x38] = "fstore";
        LENGTH[0x38] = 2;
        OPCODE[0x39] = "dstore";
        LENGTH[0x39] = 2;
        OPCODE[0x3a] = "astore";
        LENGTH[0x3a] = 2;
        OPCODE[0x3b] = "istore_0";
        LENGTH[0x3b] = 1;
        OPCODE[0x3c] = "istore_1";
        LENGTH[0x3c] = 1;
        OPCODE[0x3d] = "istore_2";
        LENGTH[0x3d] = 1;
        OPCODE[0x3e] = "istore_3";
        LENGTH[0x3e] = 1;
        OPCODE[0x3f] = "lstore_0";
        LENGTH[0x3f] = 1;

        OPCODE[0x40] = "lstore_1";
        LENGTH[0x40] = 1;
        OPCODE[0x41] = "lstore_2";
        LENGTH[0x41] = 1;
        OPCODE[0x42] = "lstore_3";
        LENGTH[0x42] = 1;
        OPCODE[0x43] = "fstore_0";
        LENGTH[0x43] = 1;
        OPCODE[0x44] = "fstore_1";
        LENGTH[0x44] = 1;
        OPCODE[0x45] = "fstore_2";
        LENGTH[0x45] = 1;
        OPCODE[0x46] = "fstore_3";
        LENGTH[0x46] = 1;
        OPCODE[0x47] = "dstore_0";
        LENGTH[0x47] = 1;
        OPCODE[0x48] = "dstore_1";
        LENGTH[0x48] = 1;
        OPCODE[0x49] = "dstore_2";
        LENGTH[0x49] = 1;
        OPCODE[0x4a] = "dstore_3";
        LENGTH[0x4a] = 1;
        OPCODE[0x4b] = "astore_0";
        LENGTH[0x4b] = 1;
        OPCODE[0x4c] = "astore_1";
        LENGTH[0x4c] = 1;
        OPCODE[0x4d] = "astore_2";
        LENGTH[0x4d] = 1;
        OPCODE[0x4e] = "astore_3";
        LENGTH[0x4e] = 1;
        OPCODE[0x4f] = "iastore";
        LENGTH[0x4f] = 1;

        OPCODE[0x50] = "lastore";
        LENGTH[0x50] = 1;
        OPCODE[0x51] = "fastore";
        LENGTH[0x51] = 1;
        OPCODE[0x52] = "dastore";
        LENGTH[0x52] = 1;
        OPCODE[0x53] = "aastore";
        LENGTH[0x53] = 1;
        OPCODE[0x54] = "bastore";
        LENGTH[0x54] = 1;
        OPCODE[0x55] = "castore";
        LENGTH[0x55] = 1;
        OPCODE[0x56] = "sastore";
        LENGTH[0x56] = 1;
        OPCODE[0x57] = "pop";
        LENGTH[0x57] = 1;
        OPCODE[0x58] = "pop2";
        LENGTH[0x58] = 1;
        OPCODE[0x59] = "dup";
        LENGTH[0x59] = 1;
        OPCODE[0x5a] = "dup_x1";
        LENGTH[0x5a] = 1;
        OPCODE[0x5b] = "dup_x2";
        LENGTH[0x5b] = 1;
        OPCODE[0x5c] = "dup2";
        LENGTH[0x5c] = 1;
        OPCODE[0x5d] = "dup2_x1";
        LENGTH[0x5d] = 1;
        OPCODE[0x5e] = "dup2_x2";
        LENGTH[0x5e] = 1;
        OPCODE[0x5f] = "swap";
        LENGTH[0x5f] = 1;

        OPCODE[0x60] = "iadd";
        LENGTH[0x60] = 1;
        OPCODE[0x61] = "ladd";
        LENGTH[0x61] = 1;
        OPCODE[0x62] = "fadd";
        LENGTH[0x62] = 1;
        OPCODE[0x63] = "dadd";
        LENGTH[0x63] = 1;
        OPCODE[0x64] = "isub";
        LENGTH[0x64] = 1;
        OPCODE[0x65] = "lsub";
        LENGTH[0x65] = 1;
        OPCODE[0x66] = "fsub";
        LENGTH[0x66] = 1;
        OPCODE[0x67] = "dsub";
        LENGTH[0x67] = 1;
        OPCODE[0x68] = "imul";
        LENGTH[0x68] = 1;
        OPCODE[0x69] = "lmul";
        LENGTH[0x69] = 1;
        OPCODE[0x6a] = "fmul";
        LENGTH[0x6a] = 1;
        OPCODE[0x6b] = "dmul";
        LENGTH[0x6b] = 1;
        OPCODE[0x6c] = "idiv";
        LENGTH[0x6c] = 1;
        OPCODE[0x6d] = "ldiv";
        LENGTH[0x6d] = 1;
        OPCODE[0x6e] = "fdiv";
        LENGTH[0x6e] = 1;
        OPCODE[0x6f] = "ddiv";
        LENGTH[0x6f] = 1;

        OPCODE[0x70] = "irem";
        LENGTH[0x70] = 1;
        OPCODE[0x71] = "lrem";
        LENGTH[0x71] = 1;
        OPCODE[0x72] = "frem";
        LENGTH[0x72] = 1;
        OPCODE[0x73] = "drem";
        LENGTH[0x73] = 1;
        OPCODE[0x74] = "ineg";
        LENGTH[0x74] = 1;
        OPCODE[0x75] = "lneg";
        LENGTH[0x75] = 1;
        OPCODE[0x76] = "fneg";
        LENGTH[0x76] = 1;
        OPCODE[0x77] = "dneg";
        LENGTH[0x77] = 1;
        OPCODE[0x78] = "ishl";
        LENGTH[0x78] = 1;
        OPCODE[0x79] = "lshl";
        LENGTH[0x79] = 1;
        OPCODE[0x7a] = "ishr";
        LENGTH[0x7a] = 1;
        OPCODE[0x7b] = "lshr";
        LENGTH[0x7b] = 1;
        OPCODE[0x7c] = "iushr";
        LENGTH[0x7c] = 1;
        OPCODE[0x7d] = "lushr";
        LENGTH[0x7d] = 1;
        OPCODE[0x7e] = "iand";
        LENGTH[0x7e] = 1;
        OPCODE[0x7f] = "land";
        LENGTH[0x7f] = 1;

        OPCODE[0x80] = "ior";
        LENGTH[0x80] = 1;
        OPCODE[0x81] = "lor";
        LENGTH[0x81] = 1;
        OPCODE[0x82] = "ixor";
        LENGTH[0x82] = 1;
        OPCODE[0x83] = "lxor";
        LENGTH[0x83] = 1;
        OPCODE[0x84] = "iinc";
        LENGTH[0x84] = 3;
        OPCODE[0x85] = "i2l";
        LENGTH[0x85] = 1;
        OPCODE[0x86] = "i2f";
        LENGTH[0x86] = 1;
        OPCODE[0x87] = "i2d";
        LENGTH[0x87] = 1;
        OPCODE[0x88] = "l2i";
        LENGTH[0x88] = 1;
        OPCODE[0x89] = "l2f";
        LENGTH[0x89] = 1;
        OPCODE[0x8a] = "l2d";
        LENGTH[0x8a] = 1;
        OPCODE[0x8b] = "f2i";
        LENGTH[0x8b] = 1;
        OPCODE[0x8c] = "f2l";
        LENGTH[0x8c] = 1;
        OPCODE[0x8d] = "f2d";
        LENGTH[0x8d] = 1;
        OPCODE[0x8e] = "d2i";
        LENGTH[0x8e] = 1;
        OPCODE[0x8f] = "d2l";
        LENGTH[0x8f] = 1;

        OPCODE[0x90] = "d2f";
        LENGTH[0x90] = 1;
        OPCODE[0x91] = "i2b";
        LENGTH[0x91] = 1;
        OPCODE[0x92] = "i2c";
        LENGTH[0x92] = 1;
        OPCODE[0x93] = "i2s";
        LENGTH[0x93] = 1;
        OPCODE[0x94] = "lcmp";
        LENGTH[0x94] = 1;
        OPCODE[0x95] = "fcmpl";
        LENGTH[0x95] = 1;
        OPCODE[0x96] = "fcmpg";
        LENGTH[0x96] = 1;
        OPCODE[0x97] = "dcmpl";
        LENGTH[0x97] = 1;
        OPCODE[0x98] = "dcmpg";
        LENGTH[0x98] = 1;
        OPCODE[0x99] = "ifeq";
        LENGTH[0x99] = 3;
        OPCODE[0x9a] = "ifne";
        LENGTH[0x9a] = 3;
        OPCODE[0x9b] = "iflt";
        LENGTH[0x9b] = 3;
        OPCODE[0x9c] = "ifge";
        LENGTH[0x9c] = 3;
        OPCODE[0x9d] = "ifgt";
        LENGTH[0x9d] = 3;
        OPCODE[0x9e] = "ifle";
        LENGTH[0x9e] = 3;
        OPCODE[0x9f] = "if_icmpeq";
        LENGTH[0x9f] = 3;

        OPCODE[0xa0] = "if_icmpne";
        LENGTH[0xa0] = 3;
        OPCODE[0xa1] = "if_icmplt";
        LENGTH[0xa1] = 3;
        OPCODE[0xa2] = "if_icmpge";
        LENGTH[0xa2] = 3;
        OPCODE[0xa3] = "if_icmpgt";
        LENGTH[0xa3] = 3;
        OPCODE[0xa4] = "if_icmple";
        LENGTH[0xa4] = 3;
        OPCODE[0xa5] = "if_acmpeq";
        LENGTH[0xa5] = 3;
        OPCODE[0xa6] = "if_acmpne";
        LENGTH[0xa6] = 3;
        OPCODE[0xa7] = "goto";
        LENGTH[0xa7] = 3;
        OPCODE[0xa8] = "jsr";
        LENGTH[0xa8] = 3;
        OPCODE[0xa9] = "ret";
        LENGTH[0xa9] = 2;
        OPCODE[0xaa] = "tableswitch";
        LENGTH[0xaa] = -1;
        OPCODE[0xab] = "lookupswitch";
        LENGTH[0xab] = -1;
        OPCODE[0xac] = "ireturn";
        LENGTH[0xac] = 1;
        OPCODE[0xad] = "lreturn";
        LENGTH[0xad] = 1;
        OPCODE[0xae] = "freturn";
        LENGTH[0xae] = 1;
        OPCODE[0xaf] = "dreturn";
        LENGTH[0xaf] = 1;

        OPCODE[0xb0] = "areturn";
        LENGTH[0xb0] = 1;
        OPCODE[0xb1] = "return";
        LENGTH[0xb1] = 1;
        OPCODE[0xb2] = "getstatic";
        LENGTH[0xb2] = 3;
        OPCODE[0xb3] = "putstatic";
        LENGTH[0xb3] = 3;
        OPCODE[0xb4] = "getfield";
        LENGTH[0xb4] = 3;
        OPCODE[0xb5] = "putfield";
        LENGTH[0xb5] = 3;
        OPCODE[0xb6] = "invokevirtual";
        LENGTH[0xb6] = 3;
        OPCODE[0xb7] = "invokespecial";
        LENGTH[0xb7] = 3;
        OPCODE[0xb8] = "invokestatic";
        LENGTH[0xb8] = 3;
        OPCODE[0xb9] = "invokeinterface";
        LENGTH[0xb9] = 5;
        OPCODE[0xba] = "invokedynamic";
        LENGTH[0xba] = 5;
        OPCODE[0xbb] = "new";
        LENGTH[0xbb] = 3;
        OPCODE[0xbc] = "newarray";
        LENGTH[0xbc] = 2;
        OPCODE[0xbd] = "anewarray";
        LENGTH[0xbd] = 3;
        OPCODE[0xbe] = "arraylength";
        LENGTH[0xbe] = 1;
        OPCODE[0xbf] = "athrow";
        LENGTH[0xbf] = 1;

        OPCODE[0xc0] = "checkcast";
        LENGTH[0xc0] = 3;
        OPCODE[0xc1] = "instanceof";
        LENGTH[0xc1] = 3;
        OPCODE[0xc2] = "monitorenter";
        LENGTH[0xc2] = 1;
        OPCODE[0xc3] = "monitorexit";
        LENGTH[0xc3] = 1;
        OPCODE[0xc4] = "wide";
        LENGTH[0xc4] = -1;
        OPCODE[0xc5] = "multianewarray";
        LENGTH[0xc5] = 4;
        OPCODE[0xc6] = "ifnull";
        LENGTH[0xc6] = 3;
        OPCODE[0xc7] = "ifnonnull";
        LENGTH[0xc7] = 3;
        OPCODE[0xc8] = "goto_w";
        LENGTH[0xc8] = 5;
        OPCODE[0xc9] = "jsr_w";
        LENGTH[0xc9] = 5;
        OPCODE[0xca] = "breakpoint";
        LENGTH[0xca] = 1;
        OPCODE[0xcb] = "xxxundefinedxxx";
        LENGTH[0xcb] = 1;
        OPCODE[0xcc] = "xxxundefinedxxx";
        LENGTH[0xcc] = 1;
        OPCODE[0xcd] = "xxxundefinedxxx";
        LENGTH[0xcd] = 1;
        OPCODE[0xce] = "xxxundefinedxxx";
        LENGTH[0xce] = 1;
        OPCODE[0xcf] = "xxxundefinedxxx";
        LENGTH[0xcf] = 1;

        OPCODE[0xd0] = "xxxundefinedxxx";
        LENGTH[0xd0] = 1;
        OPCODE[0xd1] = "xxxundefinedxxx";
        LENGTH[0xd1] = 1;
        OPCODE[0xd2] = "xxxundefinedxxx";
        LENGTH[0xd2] = 1;
        OPCODE[0xd3] = "xxxundefinedxxx";
        LENGTH[0xd3] = 1;
        OPCODE[0xd4] = "xxxundefinedxxx";
        LENGTH[0xd4] = 1;
        OPCODE[0xd5] = "xxxundefinedxxx";
        LENGTH[0xd5] = 1;
        OPCODE[0xd6] = "xxxundefinedxxx";
        LENGTH[0xd6] = 1;
        OPCODE[0xd7] = "xxxundefinedxxx";
        LENGTH[0xd7] = 1;
        OPCODE[0xd8] = "xxxundefinedxxx";
        LENGTH[0xd8] = 1;
        OPCODE[0xd9] = "xxxundefinedxxx";
        LENGTH[0xd9] = 1;
        OPCODE[0xda] = "xxxundefinedxxx";
        LENGTH[0xda] = 1;
        OPCODE[0xdb] = "xxxundefinedxxx";
        LENGTH[0xdb] = 1;
        OPCODE[0xdc] = "xxxundefinedxxx";
        LENGTH[0xdc] = 1;
        OPCODE[0xdd] = "xxxundefinedxxx";
        LENGTH[0xdd] = 1;
        OPCODE[0xde] = "xxxundefinedxxx";
        LENGTH[0xde] = 1;
        OPCODE[0xdf] = "xxxundefinedxxx";
        LENGTH[0xdf] = 1;

        OPCODE[0xe0] = "xxxundefinedxxx";
        LENGTH[0xe0] = 1;
        OPCODE[0xe1] = "xxxundefinedxxx";
        LENGTH[0xe1] = 1;
        OPCODE[0xe2] = "xxxundefinedxxx";
        LENGTH[0xe2] = 1;
        OPCODE[0xe3] = "xxxundefinedxxx";
        LENGTH[0xe3] = 1;
        OPCODE[0xe4] = "xxxundefinedxxx";
        LENGTH[0xe4] = 1;
        OPCODE[0xe5] = "xxxundefinedxxx";
        LENGTH[0xe5] = 1;
        OPCODE[0xe6] = "xxxundefinedxxx";
        LENGTH[0xe6] = 1;
        OPCODE[0xe7] = "xxxundefinedxxx";
        LENGTH[0xe7] = 1;
        OPCODE[0xe8] = "xxxundefinedxxx";
        LENGTH[0xe8] = 1;
        OPCODE[0xe9] = "xxxundefinedxxx";
        LENGTH[0xe9] = 1;
        OPCODE[0xea] = "xxxundefinedxxx";
        LENGTH[0xea] = 1;
        OPCODE[0xeb] = "xxxundefinedxxx";
        LENGTH[0xeb] = 1;
        OPCODE[0xec] = "xxxundefinedxxx";
        LENGTH[0xec] = 1;
        OPCODE[0xed] = "xxxundefinedxxx";
        LENGTH[0xed] = 1;
        OPCODE[0xee] = "xxxundefinedxxx";
        LENGTH[0xee] = 1;
        OPCODE[0xef] = "xxxundefinedxxx";
        LENGTH[0xef] = 1;

        OPCODE[0xf0] = "xxxundefinedxxx";
        LENGTH[0xf0] = 1;
        OPCODE[0xf1] = "xxxundefinedxxx";
        LENGTH[0xf1] = 1;
        OPCODE[0xf2] = "xxxundefinedxxx";
        LENGTH[0xf2] = 1;
        OPCODE[0xf3] = "xxxundefinedxxx";
        LENGTH[0xf3] = 1;
        OPCODE[0xf4] = "xxxundefinedxxx";
        LENGTH[0xf4] = 1;
        OPCODE[0xf5] = "xxxundefinedxxx";
        LENGTH[0xf5] = 1;
        OPCODE[0xf6] = "xxxundefinedxxx";
        LENGTH[0xf6] = 1;
        OPCODE[0xf7] = "xxxundefinedxxx";
        LENGTH[0xf7] = 1;
        OPCODE[0xf8] = "xxxundefinedxxx";
        LENGTH[0xf8] = 1;
        OPCODE[0xf9] = "xxxundefinedxxx";
        LENGTH[0xf9] = 1;
        OPCODE[0xfa] = "xxxundefinedxxx";
        LENGTH[0xfa] = 1;
        OPCODE[0xfb] = "xxxundefinedxxx";
        LENGTH[0xfb] = 1;
        OPCODE[0xfc] = "xxxundefinedxxx";
        LENGTH[0xfc] = 1;
        OPCODE[0xfd] = "xxxundefinedxxx";
        LENGTH[0xfd] = 1;
        OPCODE[0xfe] = "impdep1";
        LENGTH[0xfe] = 1;
        OPCODE[0xff] = "impdep2";
        LENGTH[0xff] = 1;
    }

    private final Code_attribute code;
    private final byte[] bytecode;
    private final int start;

    public Instruction(Code_attribute code, byte[] bytecode, int start) {
        this.code = code;
        this.bytecode = bytecode;
        this.start = start;
    }

    public byte[] getBytecode() {
        return bytecode;
    }

    public int getStart() {
        return start;
    }
    
    public int getOpcode() {
        return getByte(0);
    }
    
    public static String getMnemonic(int instruction) {
        return OPCODE[instruction];
    }
        
    public String getMnemonic() {
        String result = getMnemonic(getOpcode());

        if (getOpcode() == 0xc4 /* wide */) {
            result += " " + getMnemonic(getByte(1));
        }

        return result;
    }

    public int getLength() {
        return switch (getOpcode()) {
            case 0xaa: // tableswitch
                yield
                    1 +                             // opcode
                    getPadding() +                  // padding
                    12 +                            // (default, low, high) signed 32-bits values
                    (getHigh() - getLow() + 1) * 4; // (high - low + 1) signed 32-bits values

            case 0xab: // lookupswitch
                yield
                    1 +                 // opcode
                    getPadding() +      // padding
                    8 +                 // (default, npairs) signed 32-bits values
                    (getNPairs() * 8);  // npairs * (match, offset) signed 32-bits value

            case 0xc4: // wide
                yield getByte(1) == 0x84 /* iinc */ ? 6 : 4;

            default:
                yield LENGTH[getOpcode()];
        };
    }

    public int getIndex() {
        return switch (getOpcode()) {
            case 0x13: // ldc_w
            case 0x14: // ldc2_w
            case 0xb2: // getstatic
            case 0xb3: // putstatic
            case 0xb4: // getfield
            case 0xb5: // putfield
            case 0xb6: // invokevirtual
            case 0xb7: // invokespecial
            case 0xb8: // invokestatic
            case 0xb9: // invokeinterface
            case 0xba: // invokedynamic
            case 0xbb: // new
            case 0xbd: // anewarray
            case 0xc0: // checkcast
            case 0xc1: // instanceof
            case 0xc5: // multianewarray
                yield getShort(1);
            case 0x1a: // iload_0
            case 0x1e: // lload_0
            case 0x22: // fload_0
            case 0x26: // dload_0
            case 0x2a: // aload_0
            case 0x3b: // istore_0
            case 0x3f: // lstore_0
            case 0x43: // fstore_0
            case 0x47: // dstore_0
            case 0x4b: // astore_0
                yield 0;
            case 0x1b: // iload_1
            case 0x1f: // lload_1
            case 0x23: // fload_1
            case 0x27: // dload_1
            case 0x2b: // aload_1
            case 0x3c: // istore_1
            case 0x40: // lstore_1
            case 0x44: // fstore_1
            case 0x48: // dstore_1
            case 0x4c: // astore_1
                yield 1;
            case 0x1c: // iload_2
            case 0x20: // lload_2
            case 0x24: // fload_2
            case 0x28: // dload_2
            case 0x2c: // aload_2
            case 0x3d: // istore_2
            case 0x41: // lstore_2
            case 0x45: // fstore_2
            case 0x49: // dstore_2
            case 0x4d: // astore_2
                yield 2;
            case 0x1d: // iload_3
            case 0x21: // lload_3
            case 0x25: // fload_3
            case 0x29: // dload_3
            case 0x2d: // aload_3
            case 0x3e: // istore_3
            case 0x42: // lstore_3
            case 0x46: // fstore_3
            case 0x4a: // dstore_3
            case 0x4e: // astore_3
                yield 3;
            case 0x12: // ldc
            case 0x15: // iload
            case 0x16: // llload
            case 0x17: // fload
            case 0x18: // dload
            case 0x19: // aload
            case 0x36: // istore
            case 0x37: // lstore
            case 0x38: // fstore
            case 0x39: // dstore
            case 0x3a: // astore
            case 0x84: // iinc
            case 0xa9: // ret
                yield getByte(1);
            case 0xc4: // wide
                yield getShort(2);
            default:
                yield -1;
        };
    }

    public int getOffset() {
        return switch (getOpcode()) {
            case 0x99: // ifeq
            case 0x9a: // ifne
            case 0x9b: // iflt
            case 0x9c: // ifge
            case 0x9d: // ifgt
            case 0x9e: // ifle
            case 0x9f: // if_icmpeq
            case 0xa0: // if_icmpne
            case 0xa1: // if_icmplt
            case 0xa2: // if_icmpge
            case 0xa3: // if_icmpgt
            case 0xa4: // if_icmple
            case 0xa5: // if_acmpeq
            case 0xa6: // if_acmpne
            case 0xa7: // goto
            case 0xa8: // jsr
            case 0xc6: // ifnull
            case 0xc7: // ifnonnull
                yield getSignedShort(1);
            case 0xc8: // goto_w
            case 0xc9: // jsr_w
                yield getInt(1);
            default:
                yield 0;
        };
    }

    public int getValue() {
        return switch (getOpcode()) {
            case 0x02: // iconst_m1
                yield -1;
            case 0x03: // iconst_0
            case 0x09: // lconst_0
            case 0x0b: // fconst_0
            case 0x0e: // dconst_0
                yield 0;
            case 0x04: // iconst_1
            case 0x0a: // lconst_1
            case 0x0c: // fconst_1
            case 0x0f: // dconst_1
                yield 1;
            case 0x05: // iconst_2
            case 0x0d: // fconst_2
                yield 2;
            case 0x06: // iconst_3
                yield 3;
            case 0x07: // iconst_4
                yield 4;
            case 0x08: // iconst_5
                yield 5;
            case 0x10: // bipush
                yield getSignedByte(1);
            case 0x11: // sipush
                yield getSignedShort(1);
            case 0x84: // iinc
                yield getSignedByte(2);
            case 0xc4: // wide
                yield getByte(1) == 0x84 /* iinc */ ? getSignedShort(4) : 0;
            default:
                yield 0;
        };
    }

    public int getPadding() {
        return 3 - (start % 4);
    }

    public int getDefault() {
        return getInt(getPadding() + 1);
    }

    public int getLow() {
        return getInt(getPadding() + 5);
    }

    public int getHigh() {
        return getInt(getPadding() + 9);
    }

    public int getNPairs() {
        return getInt(getPadding() + 5);
    }

    private byte getSignedByte(int offset) {
        return getBytecode()[getStart() + offset];
    }

    public int getByte(int offset) {
        return getSignedByte(offset) & 0xff;
    }

    private int getSignedShort(int offset) {
        return (getSignedByte(offset+0) << 8) | (getByte(offset+1));
    }

    private int getShort(int offset) {
        return (getByte(offset+0) << 8) | (getByte(offset+1));
    }

    public int getInt(int offset) {
        return (getByte(offset+0) << 24) | (getByte(offset+1) << 16) | (getByte(offset+2) << 8) | (getByte(offset+3));
    }

    public com.jeantessier.classreader.ConstantPoolEntry getIndexedConstantPoolEntry() {
        return switch (getOpcode()) {
            case 0x12: // ldc
            case 0x13: // ldc_w
            case 0x14: // ldc2_w
            case 0xb2: // getstatic
            case 0xb3: // putstatic
            case 0xb4: // getfield
            case 0xb5: // putfield
            case 0xb6: // invokevirtual
            case 0xb7: // invokespecial
            case 0xb8: // invokestatic
            case 0xb9: // invokeinterface
            case 0xba: // invokedynamic
            case 0xbb: // new
            case 0xbd: // anewarray
            case 0xc0: // checkcast
            case 0xc1: // instanceof
            case 0xc5: // multianewarray
                yield code.getConstantPool().get(getIndex());
            default:
                yield null;
        };
    }

    public Collection<? extends ConstantPoolEntry> getDynamicConstantPoolEntries() {
        return Optional.ofNullable(getIndexedConstantPoolEntry())
                .<Collection<? extends ConstantPoolEntry>>map(indexed -> switch (indexed) {
                    case Dynamic_info entry -> findMethodHandleReferences(entry.getBootstrapMethodAttrIndex());
                    case InvokeDynamic_info entry -> findMethodHandleReferences(entry.getBootstrapMethodAttrIndex());
                    default -> Collections.emptyList();
                })
                .orElse(Collections.emptyList());
    }

    private List<? extends ConstantPoolEntry> findMethodHandleReferences(int bootstrapMethodAttrIndex) {
        BootstrapMethodFinder finder = new BootstrapMethodFinder(bootstrapMethodAttrIndex);
        code.getConstantPool().getClassfile().accept(finder);
        return finder.getBootstrapMethod().getArguments().stream()
                .filter(argument -> argument instanceof MethodHandle_info)
                .map(methodHandle -> ((MethodHandle_info) methodHandle).getReference())
                .toList();
    }

    public com.jeantessier.classreader.LocalVariable getIndexedLocalVariable() {
        return switch (getOpcode()) {
            case 0x1a: // iload_0
            case 0x1e: // lload_0
            case 0x22: // fload_0
            case 0x26: // dload_0
            case 0x2a: // aload_0
            case 0x1b: // iload_1
            case 0x1f: // lload_1
            case 0x23: // fload_1
            case 0x27: // dload_1
            case 0x2b: // aload_1
            case 0x1c: // iload_2
            case 0x20: // lload_2
            case 0x24: // fload_2
            case 0x28: // dload_2
            case 0x2c: // aload_2
            case 0x1d: // iload_3
            case 0x21: // lload_3
            case 0x25: // fload_3
            case 0x29: // dload_3
            case 0x2d: // aload_3
            case 0x15: // iload
            case 0x16: // llload
            case 0x17: // fload
            case 0x18: // dload
            case 0x19: // aload
            case 0x84: // iinc
            case 0xa9: // ret
                yield locateLocalVariable(getStart());
            case 0x3b: // istore_0
            case 0x3f: // lstore_0
            case 0x43: // fstore_0
            case 0x47: // dstore_0
            case 0x4b: // astore_0
            case 0x3c: // istore_1
            case 0x40: // lstore_1
            case 0x44: // fstore_1
            case 0x48: // dstore_1
            case 0x4c: // astore_1
            case 0x3d: // istore_2
            case 0x41: // lstore_2
            case 0x45: // fstore_2
            case 0x49: // dstore_2
            case 0x4d: // astore_2
            case 0x3e: // istore_3
            case 0x42: // lstore_3
            case 0x46: // fstore_3
            case 0x4a: // dstore_3
            case 0x4e: // astore_3
            case 0x36: // istore
            case 0x37: // lstore
            case 0x38: // fstore
            case 0x39: // dstore
            case 0x3a: // astore
                yield locateLocalVariable(getStart() + getLength());
            case 0xc4: // wide
                if (getByte(1) >= 0x36 && getByte(1) <= 0x3a) {
                    yield locateLocalVariable(getStart() + getLength());
                } else {
                    yield locateLocalVariable(getStart());
                }
            default:
                yield null;
        };
    }

    private com.jeantessier.classreader.LocalVariable locateLocalVariable(int pc) {
        LocalVariableFinder finder = new LocalVariableFinder(getIndex(), pc);
        code.accept(finder);
        return finder.getLocalVariable();
    }

    public int hashCode() {
        int result = getOpcode();

        if (getIndexedConstantPoolEntry() != null) {
            result ^= getIndexedConstantPoolEntry().hashCode();
        } else {
            for (int i=1; i<getLength(); i++) {
                result ^= bytecode[start+i];
            }
        }

        return result;
    }

    public boolean equals(Object object) {
        boolean result;

        if (this == object) {
            result = true;
        } else if (object == null || getClass() != object.getClass()) {
            result = false;
        } else {
            Instruction other = (Instruction) object;
            result = getOpcode() == other.getOpcode();

            ConstantPoolEntry thisEntry = (ConstantPoolEntry) ((code != null) ? getIndexedConstantPoolEntry() : null);
            ConstantPoolEntry otherEntry = (ConstantPoolEntry) ((other.code != null) ? other.getIndexedConstantPoolEntry() : null);

            if (result && thisEntry != null && otherEntry != null) {
                result = thisEntry.equals(otherEntry);
            } else {
                for (int i=1; result && i<getLength(); i++) {
                    result = bytecode[start+i] == other.bytecode[other.start+i];
                }
            }
        }

        return result;
    }

    public String toString() {
        return getMnemonic();
    }

    public void accept(Visitor visitor) {
        visitor.visitInstruction(this);
    }
}
