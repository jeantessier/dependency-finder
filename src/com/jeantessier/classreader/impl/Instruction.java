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

import com.jeantessier.classreader.LocalVariableFinder;
import com.jeantessier.classreader.Visitor;

public class Instruction implements com.jeantessier.classreader.Instruction {
    private static final int NB_OPCODES = 0x100;

    private static String[] opcode = new String[NB_OPCODES];
    private static int[] length = new int[NB_OPCODES];

    static {
        opcode[0x00] = "nop";
        length[0x00] = 1;
        opcode[0x01] = "aconst_null";
        length[0x01] = 1;
        opcode[0x02] = "iconst_m1";
        length[0x02] = 1;
        opcode[0x03] = "iconst_0";
        length[0x03] = 1;
        opcode[0x04] = "iconst_1";
        length[0x04] = 1;
        opcode[0x05] = "iconst_2";
        length[0x05] = 1;
        opcode[0x06] = "iconst_3";
        length[0x06] = 1;
        opcode[0x07] = "iconst_4";
        length[0x07] = 1;
        opcode[0x08] = "iconst_5";
        length[0x08] = 1;
        opcode[0x09] = "lconst_0";
        length[0x09] = 1;
        opcode[0x0a] = "lconst_1";
        length[0x0a] = 1;
        opcode[0x0b] = "fconst_0";
        length[0x0b] = 1;
        opcode[0x0c] = "fconst_1";
        length[0x0c] = 1;
        opcode[0x0d] = "fconst_2";
        length[0x0d] = 1;
        opcode[0x0e] = "dconst_0";
        length[0x0e] = 1;
        opcode[0x0f] = "dconst_1";
        length[0x0f] = 1;

        opcode[0x10] = "bipush";
        length[0x10] = 2;
        opcode[0x11] = "sipush";
        length[0x11] = 3;
        opcode[0x12] = "ldc";
        length[0x12] = 2;
        opcode[0x13] = "ldc_w";
        length[0x13] = 3;
        opcode[0x14] = "ldc2_w";
        length[0x14] = 3;
        opcode[0x15] = "iload";
        length[0x15] = 2;
        opcode[0x16] = "lload";
        length[0x16] = 2;
        opcode[0x17] = "fload";
        length[0x17] = 2;
        opcode[0x18] = "dload";
        length[0x18] = 2;
        opcode[0x19] = "aload";
        length[0x19] = 2;
        opcode[0x1a] = "iload_0";
        length[0x1a] = 1;
        opcode[0x1b] = "iload_1";
        length[0x1b] = 1;
        opcode[0x1c] = "iload_2";
        length[0x1c] = 1;
        opcode[0x1d] = "iload_3";
        length[0x1d] = 1;
        opcode[0x1e] = "lload_0";
        length[0x1e] = 1;
        opcode[0x1f] = "lload_1";
        length[0x1f] = 1;

        opcode[0x20] = "lload_2";
        length[0x20] = 1;
        opcode[0x21] = "lload_3";
        length[0x21] = 1;
        opcode[0x22] = "fload_0";
        length[0x22] = 1;
        opcode[0x23] = "fload_1";
        length[0x23] = 1;
        opcode[0x24] = "fload_2";
        length[0x24] = 1;
        opcode[0x25] = "fload_3";
        length[0x25] = 1;
        opcode[0x26] = "dload_0";
        length[0x26] = 1;
        opcode[0x27] = "dload_1";
        length[0x27] = 1;
        opcode[0x28] = "dload_2";
        length[0x28] = 1;
        opcode[0x29] = "dload_3";
        length[0x29] = 1;
        opcode[0x2a] = "aload_0";
        length[0x2a] = 1;
        opcode[0x2b] = "aload_1";
        length[0x2b] = 1;
        opcode[0x2c] = "aload_2";
        length[0x2c] = 1;
        opcode[0x2d] = "aload_3";
        length[0x2d] = 1;
        opcode[0x2e] = "iaload";
        length[0x2e] = 1;
        opcode[0x2f] = "laload";
        length[0x2f] = 1;

        opcode[0x30] = "faload";
        length[0x30] = 1;
        opcode[0x31] = "daload";
        length[0x31] = 1;
        opcode[0x32] = "aaload";
        length[0x32] = 1;
        opcode[0x33] = "baload";
        length[0x33] = 1;
        opcode[0x34] = "caload";
        length[0x34] = 1;
        opcode[0x35] = "saload";
        length[0x35] = 1;
        opcode[0x36] = "istore";
        length[0x36] = 2;
        opcode[0x37] = "lstore";
        length[0x37] = 2;
        opcode[0x38] = "fstore";
        length[0x38] = 2;
        opcode[0x39] = "dstore";
        length[0x39] = 2;
        opcode[0x3a] = "astore";
        length[0x3a] = 2;
        opcode[0x3b] = "istore_0";
        length[0x3b] = 1;
        opcode[0x3c] = "istore_1";
        length[0x3c] = 1;
        opcode[0x3d] = "istore_2";
        length[0x3d] = 1;
        opcode[0x3e] = "istore_3";
        length[0x3e] = 1;
        opcode[0x3f] = "lstore_0";
        length[0x3f] = 1;

        opcode[0x40] = "lstore_1";
        length[0x40] = 1;
        opcode[0x41] = "lstore_2";
        length[0x41] = 1;
        opcode[0x42] = "lstore_3";
        length[0x42] = 1;
        opcode[0x43] = "fstore_0";
        length[0x43] = 1;
        opcode[0x44] = "fstore_1";
        length[0x44] = 1;
        opcode[0x45] = "fstore_2";
        length[0x45] = 1;
        opcode[0x46] = "fstore_3";
        length[0x46] = 1;
        opcode[0x47] = "dstore_0";
        length[0x47] = 1;
        opcode[0x48] = "dstore_1";
        length[0x48] = 1;
        opcode[0x49] = "dstore_2";
        length[0x49] = 1;
        opcode[0x4a] = "dstore_3";
        length[0x4a] = 1;
        opcode[0x4b] = "astore_0";
        length[0x4b] = 1;
        opcode[0x4c] = "astore_1";
        length[0x4c] = 1;
        opcode[0x4d] = "astore_2";
        length[0x4d] = 1;
        opcode[0x4e] = "astore_3";
        length[0x4e] = 1;
        opcode[0x4f] = "iastore";
        length[0x4f] = 1;

        opcode[0x50] = "lastore";
        length[0x50] = 1;
        opcode[0x51] = "fastore";
        length[0x51] = 1;
        opcode[0x52] = "dastore";
        length[0x52] = 1;
        opcode[0x53] = "aastore";
        length[0x53] = 1;
        opcode[0x54] = "bastore";
        length[0x54] = 1;
        opcode[0x55] = "castore";
        length[0x55] = 1;
        opcode[0x56] = "sastore";
        length[0x56] = 1;
        opcode[0x57] = "pop";
        length[0x57] = 1;
        opcode[0x58] = "pop2";
        length[0x58] = 1;
        opcode[0x59] = "dup";
        length[0x59] = 1;
        opcode[0x5a] = "dup_x1";
        length[0x5a] = 1;
        opcode[0x5b] = "dup_x2";
        length[0x5b] = 1;
        opcode[0x5c] = "dup2";
        length[0x5c] = 1;
        opcode[0x5d] = "dup2_x1";
        length[0x5d] = 1;
        opcode[0x5e] = "dup2_x2";
        length[0x5e] = 1;
        opcode[0x5f] = "swap";
        length[0x5f] = 1;

        opcode[0x60] = "iadd";
        length[0x60] = 1;
        opcode[0x61] = "ladd";
        length[0x61] = 1;
        opcode[0x62] = "fadd";
        length[0x62] = 1;
        opcode[0x63] = "dadd";
        length[0x63] = 1;
        opcode[0x64] = "isub";
        length[0x64] = 1;
        opcode[0x65] = "lsub";
        length[0x65] = 1;
        opcode[0x66] = "fsub";
        length[0x66] = 1;
        opcode[0x67] = "dsub";
        length[0x67] = 1;
        opcode[0x68] = "imul";
        length[0x68] = 1;
        opcode[0x69] = "lmul";
        length[0x69] = 1;
        opcode[0x6a] = "fmul";
        length[0x6a] = 1;
        opcode[0x6b] = "dmul";
        length[0x6b] = 1;
        opcode[0x6c] = "idiv";
        length[0x6c] = 1;
        opcode[0x6d] = "ldiv";
        length[0x6d] = 1;
        opcode[0x6e] = "fdiv";
        length[0x6e] = 1;
        opcode[0x6f] = "ddiv";
        length[0x6f] = 1;

        opcode[0x70] = "irem";
        length[0x70] = 1;
        opcode[0x71] = "lrem";
        length[0x71] = 1;
        opcode[0x72] = "frem";
        length[0x72] = 1;
        opcode[0x73] = "drem";
        length[0x73] = 1;
        opcode[0x74] = "ineg";
        length[0x74] = 1;
        opcode[0x75] = "lneg";
        length[0x75] = 1;
        opcode[0x76] = "fneg";
        length[0x76] = 1;
        opcode[0x77] = "dneg";
        length[0x77] = 1;
        opcode[0x78] = "ishl";
        length[0x78] = 1;
        opcode[0x79] = "lshl";
        length[0x79] = 1;
        opcode[0x7a] = "ishr";
        length[0x7a] = 1;
        opcode[0x7b] = "lshr";
        length[0x7b] = 1;
        opcode[0x7c] = "iushr";
        length[0x7c] = 1;
        opcode[0x7d] = "lushr";
        length[0x7d] = 1;
        opcode[0x7e] = "iand";
        length[0x7e] = 1;
        opcode[0x7f] = "land";
        length[0x7f] = 1;

        opcode[0x80] = "ior";
        length[0x80] = 1;
        opcode[0x81] = "lor";
        length[0x81] = 1;
        opcode[0x82] = "ixor";
        length[0x82] = 1;
        opcode[0x83] = "lxor";
        length[0x83] = 1;
        opcode[0x84] = "iinc";
        length[0x84] = 3;
        opcode[0x85] = "i2l";
        length[0x85] = 1;
        opcode[0x86] = "i2f";
        length[0x86] = 1;
        opcode[0x87] = "i2d";
        length[0x87] = 1;
        opcode[0x88] = "l2i";
        length[0x88] = 1;
        opcode[0x89] = "l2f";
        length[0x89] = 1;
        opcode[0x8a] = "l2d";
        length[0x8a] = 1;
        opcode[0x8b] = "f2i";
        length[0x8b] = 1;
        opcode[0x8c] = "f2l";
        length[0x8c] = 1;
        opcode[0x8d] = "f2d";
        length[0x8d] = 1;
        opcode[0x8e] = "d2i";
        length[0x8e] = 1;
        opcode[0x8f] = "d2l";
        length[0x8f] = 1;

        opcode[0x90] = "d2f";
        length[0x90] = 1;
        opcode[0x91] = "i2b";
        length[0x91] = 1;
        opcode[0x92] = "i2c";
        length[0x92] = 1;
        opcode[0x93] = "i2s";
        length[0x93] = 1;
        opcode[0x94] = "lcmp";
        length[0x94] = 1;
        opcode[0x95] = "fcmpl";
        length[0x95] = 1;
        opcode[0x96] = "fcmpg";
        length[0x96] = 1;
        opcode[0x97] = "dcmpl";
        length[0x97] = 1;
        opcode[0x98] = "dcmpg";
        length[0x98] = 1;
        opcode[0x99] = "ifeq";
        length[0x99] = 3;
        opcode[0x9a] = "ifne";
        length[0x9a] = 3;
        opcode[0x9b] = "iflt";
        length[0x9b] = 3;
        opcode[0x9c] = "ifge";
        length[0x9c] = 3;
        opcode[0x9d] = "ifgt";
        length[0x9d] = 3;
        opcode[0x9e] = "ifle";
        length[0x9e] = 3;
        opcode[0x9f] = "if_icmpeq";
        length[0x9f] = 3;

        opcode[0xa0] = "if_icmpne";
        length[0xa0] = 3;
        opcode[0xa1] = "if_icmplt";
        length[0xa1] = 3;
        opcode[0xa2] = "if_icmpge";
        length[0xa2] = 3;
        opcode[0xa3] = "if_icmpgt";
        length[0xa3] = 3;
        opcode[0xa4] = "if_icmple";
        length[0xa4] = 3;
        opcode[0xa5] = "if_acmpeq";
        length[0xa5] = 3;
        opcode[0xa6] = "if_acmpne";
        length[0xa6] = 3;
        opcode[0xa7] = "goto";
        length[0xa7] = 3;
        opcode[0xa8] = "jsr";
        length[0xa8] = 3;
        opcode[0xa9] = "ret";
        length[0xa9] = 2;
        opcode[0xaa] = "tableswitch";
        length[0xaa] = -1;
        opcode[0xab] = "lookupswitch";
        length[0xab] = -1;
        opcode[0xac] = "ireturn";
        length[0xac] = 1;
        opcode[0xad] = "lreturn";
        length[0xad] = 1;
        opcode[0xae] = "freturn";
        length[0xae] = 1;
        opcode[0xaf] = "dreturn";
        length[0xaf] = 1;

        opcode[0xb0] = "areturn";
        length[0xb0] = 1;
        opcode[0xb1] = "return";
        length[0xb1] = 1;
        opcode[0xb2] = "getstatic";
        length[0xb2] = 3;
        opcode[0xb3] = "putstatic";
        length[0xb3] = 3;
        opcode[0xb4] = "getfield";
        length[0xb4] = 3;
        opcode[0xb5] = "putfield";
        length[0xb5] = 3;
        opcode[0xb6] = "invokevirtual";
        length[0xb6] = 3;
        opcode[0xb7] = "invokespecial";
        length[0xb7] = 3;
        opcode[0xb8] = "invokestatic";
        length[0xb8] = 3;
        opcode[0xb9] = "invokeinterface";
        length[0xb9] = 5;
        opcode[0xba] = "invokedynamic";
        length[0xba] = 3;
        opcode[0xbb] = "new";
        length[0xbb] = 3;
        opcode[0xbc] = "newarray";
        length[0xbc] = 2;
        opcode[0xbd] = "anewarray";
        length[0xbd] = 3;
        opcode[0xbe] = "arraylength";
        length[0xbe] = 1;
        opcode[0xbf] = "athrow";
        length[0xbf] = 1;

        opcode[0xc0] = "checkcast";
        length[0xc0] = 3;
        opcode[0xc1] = "instanceof";
        length[0xc1] = 3;
        opcode[0xc2] = "monitorenter";
        length[0xc2] = 1;
        opcode[0xc3] = "monitorexit";
        length[0xc3] = 1;
        opcode[0xc4] = "wide";
        length[0xc4] = -1;
        opcode[0xc5] = "multianewarray";
        length[0xc5] = 4;
        opcode[0xc6] = "ifnull";
        length[0xc6] = 3;
        opcode[0xc7] = "ifnonnull";
        length[0xc7] = 3;
        opcode[0xc8] = "goto_w";
        length[0xc8] = 5;
        opcode[0xc9] = "jsr_w";
        length[0xc9] = 5;
        opcode[0xca] = "breakpoint";
        length[0xca] = 1;
        opcode[0xcb] = "xxxundefinedxxx";
        length[0xcb] = 1;
        opcode[0xcc] = "xxxundefinedxxx";
        length[0xcc] = 1;
        opcode[0xcd] = "xxxundefinedxxx";
        length[0xcd] = 1;
        opcode[0xce] = "xxxundefinedxxx";
        length[0xce] = 1;
        opcode[0xcf] = "xxxundefinedxxx";
        length[0xcf] = 1;

        opcode[0xd0] = "xxxundefinedxxx";
        length[0xd0] = 1;
        opcode[0xd1] = "xxxundefinedxxx";
        length[0xd1] = 1;
        opcode[0xd2] = "xxxundefinedxxx";
        length[0xd2] = 1;
        opcode[0xd3] = "xxxundefinedxxx";
        length[0xd3] = 1;
        opcode[0xd4] = "xxxundefinedxxx";
        length[0xd4] = 1;
        opcode[0xd5] = "xxxundefinedxxx";
        length[0xd5] = 1;
        opcode[0xd6] = "xxxundefinedxxx";
        length[0xd6] = 1;
        opcode[0xd7] = "xxxundefinedxxx";
        length[0xd7] = 1;
        opcode[0xd8] = "xxxundefinedxxx";
        length[0xd8] = 1;
        opcode[0xd9] = "xxxundefinedxxx";
        length[0xd9] = 1;
        opcode[0xda] = "xxxundefinedxxx";
        length[0xda] = 1;
        opcode[0xdb] = "xxxundefinedxxx";
        length[0xdb] = 1;
        opcode[0xdc] = "xxxundefinedxxx";
        length[0xdc] = 1;
        opcode[0xdd] = "xxxundefinedxxx";
        length[0xdd] = 1;
        opcode[0xde] = "xxxundefinedxxx";
        length[0xde] = 1;
        opcode[0xdf] = "xxxundefinedxxx";
        length[0xdf] = 1;

        opcode[0xe0] = "xxxundefinedxxx";
        length[0xe0] = 1;
        opcode[0xe1] = "xxxundefinedxxx";
        length[0xe1] = 1;
        opcode[0xe2] = "xxxundefinedxxx";
        length[0xe2] = 1;
        opcode[0xe3] = "xxxundefinedxxx";
        length[0xe3] = 1;
        opcode[0xe4] = "xxxundefinedxxx";
        length[0xe4] = 1;
        opcode[0xe5] = "xxxundefinedxxx";
        length[0xe5] = 1;
        opcode[0xe6] = "xxxundefinedxxx";
        length[0xe6] = 1;
        opcode[0xe7] = "xxxundefinedxxx";
        length[0xe7] = 1;
        opcode[0xe8] = "xxxundefinedxxx";
        length[0xe8] = 1;
        opcode[0xe9] = "xxxundefinedxxx";
        length[0xe9] = 1;
        opcode[0xea] = "xxxundefinedxxx";
        length[0xea] = 1;
        opcode[0xeb] = "xxxundefinedxxx";
        length[0xeb] = 1;
        opcode[0xec] = "xxxundefinedxxx";
        length[0xec] = 1;
        opcode[0xed] = "xxxundefinedxxx";
        length[0xed] = 1;
        opcode[0xee] = "xxxundefinedxxx";
        length[0xee] = 1;
        opcode[0xef] = "xxxundefinedxxx";
        length[0xef] = 1;

        opcode[0xf0] = "xxxundefinedxxx";
        length[0xf0] = 1;
        opcode[0xf1] = "xxxundefinedxxx";
        length[0xf1] = 1;
        opcode[0xf2] = "xxxundefinedxxx";
        length[0xf2] = 1;
        opcode[0xf3] = "xxxundefinedxxx";
        length[0xf3] = 1;
        opcode[0xf4] = "xxxundefinedxxx";
        length[0xf4] = 1;
        opcode[0xf5] = "xxxundefinedxxx";
        length[0xf5] = 1;
        opcode[0xf6] = "xxxundefinedxxx";
        length[0xf6] = 1;
        opcode[0xf7] = "xxxundefinedxxx";
        length[0xf7] = 1;
        opcode[0xf8] = "xxxundefinedxxx";
        length[0xf8] = 1;
        opcode[0xf9] = "xxxundefinedxxx";
        length[0xf9] = 1;
        opcode[0xfa] = "xxxundefinedxxx";
        length[0xfa] = 1;
        opcode[0xfb] = "xxxundefinedxxx";
        length[0xfb] = 1;
        opcode[0xfc] = "xxxundefinedxxx";
        length[0xfc] = 1;
        opcode[0xfd] = "xxxundefinedxxx";
        length[0xfd] = 1;
        opcode[0xfe] = "impdep1";
        length[0xfe] = 1;
        opcode[0xff] = "impdep2";
        length[0xff] = 1;
    }

    private Code_attribute code;
    private byte[] bytecode;
    private int start;

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
        return opcode[instruction];
    }
        
    public String getMnemonic() {
        String result = getMnemonic(getOpcode());

        if (getOpcode() == 0xc4 /* wide */) {
            result += " " + getMnemonic(getByte(1));
        }

        return result;
    }

    public int getLength() {
        int result = length[getOpcode()];

        int padding, low, high, npairs;
    
        switch (getOpcode()) {
            case 0xaa:
                // tableswitch
                padding = 3 - (start % 4);
                low =
                    (getByte(padding+5) << 24) |
                    (getByte(padding+6) << 16) |
                    (getByte(padding+7) << 8) |
                    (getByte(padding+8));
                high =
                    (getByte(padding+9) << 24) |
                    (getByte(padding+10) << 16) |
                    (getByte(padding+11) << 8) |
                    (getByte(padding+12));
                result =
                    1 +                   // opcode
                    padding +             // padding
                    12 +                  // default + low + high
                    (high - low + 1) * 4; // (high - low + 1) * offset
                break;

            case 0xab:
                // lookupswitch
                padding = 3 - (start % 4);
                npairs =
                    (getByte(padding+5) << 24) |
                    (getByte(padding+6) << 16) |
                    (getByte(padding+7) << 8) |
                    (getByte(padding+8));
                result =
                    1 +            // opcode
                    padding +      // padding
                    8 +            // default + npairs
                    (npairs * 8);  // npairs * (match + offset)
                break;

            case 0xc4:
                // wide
                if (getByte(1) == 0x84 /* iinc */) {
                    result = 6;
                } else {
                    result = 4;
                }
                break;

            default:
                // Do nothing
                break;
        }

        return result;
    }

    public int getIndex() {
        int result;

        switch (getOpcode()) {
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
                result = (getByte(1) << 8) | getByte(2);
                break;
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
                result = 0;
                break;
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
                result = 1;
                break;
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
                result = 2;
                break;
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
                result = 3;
                break;
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
                result = getByte(1);
                break;
            case 0xc4: // wide
                result = (getByte(2) << 8) | getByte(3);
                break;
            default:
                result = -1;
                break;
        }

        return result;
    }

    public int getOffset() {
        int result;

        switch(getOpcode()) {
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
                result = (getSignedByte(1) << 8) | getByte(2);
                break;
            case 0xc8: // goto_w
            case 0xc9: // jsr_w
                result = (getSignedByte(1) << 24) | (getByte(2) << 16) | (getByte(3) << 8) | getByte(4);
                break;
            default:
                result = 0;
                break;
        }

        return result;
    }

    public int getValue() {
        int result;

        switch(getOpcode()) {
            case 0x02: // iconst_m1
                result = -1;
                break;
            case 0x03: // iconst_0
            case 0x09: // lconst_0
            case 0x0b: // fconst_0
            case 0x0e: // dconst_0
                result = 0;
                break;
            case 0x04: // iconst_1
            case 0x0a: // lconst_1
            case 0x0c: // fconst_1
            case 0x0f: // dconst_1
                result = 1;
                break;
            case 0x05: // iconst_2
            case 0x0d: // fconst_2
                result = 2;
                break;
            case 0x06: // iconst_3
                result = 3;
                break;
            case 0x07: // iconst_4
                result = 4;
                break;
            case 0x08: // iconst_5
                result = 5;
                break;
            case 0x10: // bipush
                result = getSignedByte(1);
                break;
            case 0x11: // sipush
                result = (getSignedByte(1) << 8) | getByte(2);
                break;
            case 0x84: // iinc
                result = getSignedByte(2);
                break;
            case 0xc4: // wide
                if (getByte(1) == 0x84 /* iinc */) {
                    result = (getSignedByte(4) << 8) | getByte(5);
                } else {
                    result = 0;
                }
                break;
            default:
                result = 0;
                break;
        }

        return result;
    }

    public int getByte(int offset) {
        return getSignedByte(offset) & 0xff;
    }

    private byte getSignedByte(int offset) {
        return getBytecode()[getStart() + offset];
    }

    public com.jeantessier.classreader.ConstantPoolEntry getIndexedConstantPoolEntry() {
        com.jeantessier.classreader.ConstantPoolEntry result;

        switch (getOpcode()) {
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
                result = code.getConstantPool().get(getIndex());
                break;
            default:
                result = null;
                break;
        }

        return result;
    }

    public com.jeantessier.classreader.LocalVariable getIndexedLocalVariable() {
        com.jeantessier.classreader.LocalVariable result;

        switch(getOpcode()) {
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
                result = locateLocalVariable(getStart());
                break;
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
                result = locateLocalVariable(getStart() + getLength());
                break;
            case 0xc4: // wide
                if (getByte(1) >= 0x36 && getByte(1) <= 0x3a) {
                    result = locateLocalVariable(getStart() + getLength());
                } else {
                    result = locateLocalVariable(getStart());
                }
                break;
            default:
                result = null;
                break;
        }

        return result;
    }

    private com.jeantessier.classreader.LocalVariable locateLocalVariable(int pc) {
        com.jeantessier.classreader.LocalVariable result;

        LocalVariableFinder finder = new LocalVariableFinder(getIndex(), pc);
        code.accept(finder);
        result = finder.getLocalVariable();

        return result;
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
