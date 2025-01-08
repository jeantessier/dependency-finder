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

package com.jeantessier.commandline;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestSingleValueSwitch {
    private static final String DEFAULT_VALUE = "default value";
    private static final String EXPECTED_VALUE = "expected value";

    private SingleValueSwitch commandLineSwitch = new SingleValueSwitch("switch", DEFAULT_VALUE);

    @Test
    void testSetToNull() {
        assertFalse(commandLineSwitch.isPresent());
        assertEquals(DEFAULT_VALUE, commandLineSwitch.getValue());
        commandLineSwitch.setValue(null);
        assertTrue(commandLineSwitch.isPresent());
        assertEquals(DEFAULT_VALUE, commandLineSwitch.getValue());
    }

    @Test
    void testSetToObject() {
        assertFalse(commandLineSwitch.isPresent());
        assertEquals(DEFAULT_VALUE, commandLineSwitch.getValue());
        commandLineSwitch.setValue(EXPECTED_VALUE);
        assertTrue(commandLineSwitch.isPresent());
        assertEquals(EXPECTED_VALUE, commandLineSwitch.getValue());
    }

    @Test
    void testParseNull() {
        assertThrows(CommandLineException.class, () -> commandLineSwitch.parse(null));
    }

    @Test
    void testParseEmptyString() {
        assertFalse(commandLineSwitch.isPresent());
        assertEquals(DEFAULT_VALUE, commandLineSwitch.getValue());
        commandLineSwitch.parse("");
        assertTrue(commandLineSwitch.isPresent());
        assertEquals("", commandLineSwitch.getValue());
    }

    @Test
    void testParseString() {
        assertFalse(commandLineSwitch.isPresent());
        assertEquals(DEFAULT_VALUE, commandLineSwitch.getValue());
        commandLineSwitch.parse(EXPECTED_VALUE);
        assertTrue(commandLineSwitch.isPresent());
        assertEquals(EXPECTED_VALUE, commandLineSwitch.getValue());
    }

    @Test
    void testValidateWhenNotMandatory() {
        commandLineSwitch.validate();
        commandLineSwitch.parse(EXPECTED_VALUE);
        commandLineSwitch.validate();
    }

    @Test
    void testValidateWhenMandatory() {
        commandLineSwitch = new SingleValueSwitch("switch", "default", true);
        assertThrows(CommandLineException.class, () -> commandLineSwitch.validate());

        commandLineSwitch.parse(EXPECTED_VALUE);
        commandLineSwitch.validate();
    }
}
