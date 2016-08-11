/*
 *  Copyright (c) 2001-2016, Jean Tessier
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

public enum ReferenceKind {
    GET_FIELD(1, "REF_getField"),
    GET_STATIC(2, "REF_getStatic"),
    PUT_FIELD(3, "REF_putField"),
    PUT_STATIC(4, "REF_putStatic"),
    INVOKE_VIRTUAL(5, "REF_invokeVirtual"),
    INVOKE_STATIC(6, "REF_invokeStatic"),
    INVOKE_SPECIAL(7, "REF_invokeSpecial"),
    NEW_INVOKE_SPECIAL(8, "REF_newInvokeSpecial"),
    INVOKE_INTERFACE(9, "REF_invokeInterface");

    private final int kind;
    private final String description;

    ReferenceKind(int kind, String description) {
        this.kind = kind;
        this.description = description;
    }

    public int getKind() {
        return kind;
    }

    public String getDescription() { return description; }

    public static ReferenceKind forKind(int kind) {
        ReferenceKind result = null;

        for (ReferenceKind referenceKind : values()) {
            if (referenceKind.kind == kind) {
                result = referenceKind;
            }
        }
        
        return result;
    }
}
