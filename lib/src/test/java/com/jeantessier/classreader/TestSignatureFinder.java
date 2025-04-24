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

import org.jmock.*;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.jmock.Expectations.aNull;

public class TestSignatureFinder {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();

    @Test
    public void testVisitCode_attribute() {
        Code_attribute mockCode_attribute = context.mock(Code_attribute.class);

        SignatureFinder sut = new SignatureFinder();
        sut.visitCode_attribute(mockCode_attribute);
        assertThat("signature", sut.getSignature(), is(aNull(String.class)));
    }

    @Test
    public void testVisitSignature_attribute() {
        final Signature_attribute mockSignature_attribute = context.mock(Signature_attribute.class);

        context.checking(new Expectations() {{
            oneOf (mockSignature_attribute).getSignature();
                will(returnValue("()V"));
        }});

        SignatureFinder sut = new SignatureFinder();
        sut.visitSignature_attribute(mockSignature_attribute);
        assertThat("signature", sut.getSignature(), is("()V"));
    }
}
