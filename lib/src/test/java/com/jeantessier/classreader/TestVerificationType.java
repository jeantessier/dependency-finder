/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public class TestVerificationType {
    @Parameters(name="VerificationType from tag {0}")
    public static Object[][] data() {
        return new Object[][] {
            {"ITEM_Top", 0, VerificationType.TOP},
            {"ITEM_Integer", 1, VerificationType.INTEGER},
            {"ITEM_Float", 2, VerificationType.FLOAT},
            {"ITEM_Null", 5, VerificationType.NULL},
            {"ITEM_UninitializedThis", 6, VerificationType.UNINITIALIZED_THIS},
            {"ITEM_Object", 7, VerificationType.OBJECT},
            {"ITEM_Uninitialized", 8, VerificationType.UNINITIALIZED},
            {"ITEM_Long", 4, VerificationType.LONG},
            {"ITEM_Double", 3, VerificationType.DOUBLE},
        };
    }

    @Parameter(0)
    public String label;

    @Parameter(1)
    public int tag;

    @Parameter(2)
    public VerificationType expectedResult;

    @Test
    public void test() {
        assertEquals(label, expectedResult, VerificationType.forTag(tag));
        assertEquals(label, tag, VerificationType.forTag(tag).getTag());
    }
}
