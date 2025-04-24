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

import org.apache.logging.log4j.*;

public class LocalVariableFinder extends VisitorBase {
    private final int localVariableIndex;
    private final int pc;

    private LocalVariable localVariable;

    public LocalVariableFinder(int localVariableIndex, int pc) {
        this.localVariableIndex = localVariableIndex;
        this.pc = pc;
    }

    public LocalVariable getLocalVariable() {
        return localVariable;
    }

    public void visitCode_attribute(Code_attribute attribute) {
        LogManager.getLogger(getClass()).debug("Visiting {} code attribute(s) ...", attribute.getAttributes().size());
        for (Attribute_info attribute_info : attribute.getAttributes()) {
            attribute_info.accept(this);
        }
    }

    public void visitLocalVariable(LocalVariable helper) {
        super.visitLocalVariable(helper);

        boolean matching = helper.getIndex() == localVariableIndex && helper.getStartPC() <= pc;

        if (matching && helper.getLength() > 0) {
            matching = pc < helper.getStartPC() + helper.getLength();
        }

        if (matching) {
            localVariable = helper;
        }
    }
}
