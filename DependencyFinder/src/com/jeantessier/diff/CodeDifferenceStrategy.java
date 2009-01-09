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

package com.jeantessier.diff;

import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;
import com.jeantessier.text.*;

/**
 * TODO class comment
 */
public class CodeDifferenceStrategy implements DifferenceStrategy {
    public boolean isPackageDifferent(Map<String, Classfile> oldPackage, Map<String, Classfile> newPackage) {
        return false;
    }

    public boolean isClassDifferent(Classfile oldClass, Classfile newClass) {
        return false;
    }

    public boolean isDeclarationModified(Classfile oldClass, Classfile newClass) {
        return false;
    }

    public boolean isFieldDifferent(Field_info oldField, Field_info newField) {
        return false;
    }

    public boolean isConstantValueDifferent(ConstantValue_attribute oldValue, ConstantValue_attribute newValue) {
        boolean result;

        if (oldValue != null) {
            result = !oldValue.equals(newValue);
        } else {
            result = oldValue != newValue;
        }

        return result;
    }

    public boolean isMethodDifferent(Method_info oldMethod, Method_info newMethod) {
        return false;
    }

    public boolean isCodeDifferent(Code_attribute oldCode, Code_attribute newCode) {
        boolean result;

        if (oldCode != null && newCode != null) {
            result = oldCode.getCode().length != newCode.getCode().length;

            Iterator<Instruction> oldIterator = oldCode.iterator();
            Iterator<Instruction> newIterator = newCode.iterator();

            while (!result && oldIterator.hasNext() && newIterator.hasNext()) {
                Instruction oldInstruction = oldIterator.next();
                Instruction newInstruction = newIterator.next();
                result = !oldInstruction.equals(newInstruction);
            }

            if (Logger.getLogger(getClass()).isDebugEnabled()) {
                Logger.getLogger(getClass()).debug("Code compare for " + oldCode.getOwner());
                Logger.getLogger(getClass()).debug("old code: " + Hex.toString(oldCode.getCode()));
                Logger.getLogger(getClass()).debug("new code: " + Hex.toString(newCode.getCode()));
                Logger.getLogger(getClass()).debug(result ? "[different]" : "[same]");
            }
        } else {
            result = oldCode != newCode;
        }

        return result;
    }
}
