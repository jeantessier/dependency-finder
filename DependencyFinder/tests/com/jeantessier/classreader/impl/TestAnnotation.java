/*
 *  Copyright (c) 2001-2008, Jean Tessier
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

import org.jmock.*;

public class TestAnnotation extends TestAttributeBase {
    private static final int TYPE_INDEX = 2;

    public void testConstructorWithZeroElementValuePairs() throws Exception {
        final RuntimeAnnotations_attribute mockAnnotations = mock(RuntimeAnnotations_attribute.class);
        final ConstantPool mockConstantPool = mock(ConstantPool.class);
        final Class_info mockClass_info = mock(Class_info.class);

        expectTypeIndex(TYPE_INDEX);
        expectNumElementValuePairs(0);

        checking(new Expectations() {{
            one (mockAnnotations).getClassfile();
                will(returnValue(mockClassfile));
            one (mockClassfile).getConstantPool();
                will(returnValue(mockConstantPool));
            one (mockConstantPool).get(TYPE_INDEX);
                will(returnValue(mockClass_info));
        }});

        Annotation sut = new Annotation(mockAnnotations, mockIn);
        assertSame("Annotations_attribute", mockAnnotations, sut.getAnnotations_attribute());
        assertTrue("New annotation should not contain element value pairs already", sut.getElementValuePairs().isEmpty());
    }

    private void expectTypeIndex(int typeIndex) throws IOException {
        expectReadU2(typeIndex);
    }

    private void expectNumElementValuePairs(int numElementValuePairs) throws IOException {
        expectReadU2(numElementValuePairs);
    }
}
