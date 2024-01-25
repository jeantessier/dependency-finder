/*
 *  Copyright (c) 2001-2023, Jean Tessier
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

import com.jeantessier.classreader.Visitor;
import org.apache.logging.log4j.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class LocalvarTarget extends Target_info implements com.jeantessier.classreader.LocalvarTarget {
    private final TargetType targetType;
    private final Collection<LocalvarTableEntry> table = new LinkedList<>();

    public LocalvarTarget(TargetType targetType, DataInput in) throws IOException {
        this.targetType = targetType;

        var tableLength = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Reading " + tableLength + " localvar table entry(ies) ...");
        IntStream.range(0, tableLength).forEach(i -> {
            try {
                LogManager.getLogger(getClass()).debug("entry " + i + ":");
                table.add(new LocalvarTableEntry(in));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public com.jeantessier.classreader.TargetType getTargetType() {
        return targetType.getTargetType();
    }

    public Collection<? extends com.jeantessier.classreader.LocalvarTableEntry> getTable() {
        return table;
    }

    public void accept(Visitor visitor) {
        visitor.visitLocalvarTarget(this);
    }
}
