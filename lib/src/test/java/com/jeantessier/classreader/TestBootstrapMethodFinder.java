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

import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;

import java.util.*;

public class TestBootstrapMethodFinder extends MockObjectTestCase {
    private static final int BOOTSTRAP_METHOD_INDEX = 1;

    BootstrapMethodFinder sut;

    protected void setUp() throws Exception {
        super.setUp();

        sut = new BootstrapMethodFinder(BOOTSTRAP_METHOD_INDEX);
    }

    public void testVisitBootstrapMethods_attribute() {
        BootstrapMethods_attribute mockAttribute = mock(BootstrapMethods_attribute.class);
        BootstrapMethod otherBootstrapMethod = mock(BootstrapMethod.class, "other bootstrap method");
        BootstrapMethod targetBootstrapMethod = mock(BootstrapMethod.class, "target bootstrap method");

        checking(new Expectations() {{
            oneOf (mockAttribute).getBootstrapMethods();
                will(returnValue(List.of(otherBootstrapMethod, targetBootstrapMethod)));
            oneOf (targetBootstrapMethod).accept(sut);
        }});

        sut.visitBootstrapMethods_attribute(mockAttribute);
    }

    public void testVisitBootstrapMethod() {
        BootstrapMethod mockBootstrapMethod = mock(BootstrapMethod.class);

        sut.visitBootstrapMethod(mockBootstrapMethod);

        assertSame(mockBootstrapMethod, sut.getBootstrapMethod());
    }
}
