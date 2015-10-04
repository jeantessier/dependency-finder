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

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

public class BooleanConstantElementValue extends ConstantElementValue implements com.jeantessier.classreader.BooleanConstantElementValue {
    public BooleanConstantElementValue(ConstantPool constantPool, DataInput in) throws IOException {
        super(constantPool, in);
        Logger.getLogger(getClass()).debug("Const value index: " + getConstValueIndex() + " (" + getConstValue() + ")");
    }

    /**
     * The Classfile Format Spec does not tell how to map a CONSTANT_Integer
     * structure to a boolean value.  This method assumes that 0 is false and
     * anything else is true, like in C.  Another possibility would be that
     * the presence of a CONSTANT_Integer is true and its absence is false, but
     * that is not the current implementation.
     *
     * @return false if the matching constant pool entry has value 0, true otherwise.
     */
    public boolean getConstValue() {
        return ((Integer_info) getRawConstValue()).getValue() != 0;
    }

    public char getTag() {
        return ElementValueType.BOOLEAN.getTag();
    }

    public void accept(Visitor visitor) {
        visitor.visitBooleanConstantElementValue(this);
    }
}