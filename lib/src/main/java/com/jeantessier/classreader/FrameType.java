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

public enum FrameType {
    SAME(0, 63),
    SAME_LOCALS_1_STACK_ITEM(64, 127),
    SAME_LOCALS_1_STACK_ITEM_EXTENDED(247),
    CHOP(248, 250),
    SAME_FRAME_EXTENDED(251),
    APPEND(252, 254),
    FULL_FRAME(255);

    private final int rangeStart;
    private final int rangeStop;

    FrameType(int tag) {
        this(tag, tag);
    }

    FrameType(int rangeStart, int rangeStop) {
        this.rangeStart = rangeStart;
        this.rangeStop = rangeStop;
    }

    public int getRangeStart() {
        return rangeStart;
    }

    public int getRangeStop() {
        return rangeStop;
    }

    public boolean inRange(int tag) {
        return tag >= rangeStart && tag <= rangeStop;
    }

    public static FrameType forTag(int tag) {
        return Arrays.stream(values())
                .filter(frameType -> frameType.inRange(tag))
                .findFirst()
                .orElse(null);
    }
}
