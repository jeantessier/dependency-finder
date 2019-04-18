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

package com.jeantessier.commandline;

import java.util.*;

import junit.framework.*;

public class TestMultipleValuesSwitch extends TestCase {
    private static final String DEFAULT_VALUE1 = "default value 1";
    private static final String DEFAULT_VALUE2 = "default value 2";
    private static final List<String> DEFAULT_VALUE = new ArrayList<String>();

    private static final String EXPECTED_VALUE1 = "expected value 1";
    private static final String EXPECTED_VALUE2 = "expected value 2";
    private static final List<String> EXPECTED_VALUE = new ArrayList<String>();

    private static final List<String> NULL_VALUE = new ArrayList<String>();

    static {
        DEFAULT_VALUE.add(DEFAULT_VALUE1);
        DEFAULT_VALUE.add(DEFAULT_VALUE2);

        EXPECTED_VALUE.add(EXPECTED_VALUE1);
        EXPECTED_VALUE.add(EXPECTED_VALUE2);

        NULL_VALUE.add(null);
    }

    private MultipleValuesSwitch commandLineSwitch;

    protected void setUp() throws Exception {
        super.setUp();

        commandLineSwitch = new MultipleValuesSwitch("switch", DEFAULT_VALUE);
    }

    public void testSetToNull() {
        assertFalse(commandLineSwitch.isPresent());
        assertEquals(DEFAULT_VALUE, commandLineSwitch.getValue());
        commandLineSwitch.setValue(null);
        assertTrue(commandLineSwitch.isPresent());
        assertEquals(NULL_VALUE, commandLineSwitch.getValue());
    }

    public void testSetToObject() {
        assertFalse(commandLineSwitch.isPresent());
        assertEquals(DEFAULT_VALUE, commandLineSwitch.getValue());
        commandLineSwitch.setValue(EXPECTED_VALUE1);
        commandLineSwitch.setValue(EXPECTED_VALUE2);
        assertTrue(commandLineSwitch.isPresent());
        assertEquals(EXPECTED_VALUE, commandLineSwitch.getValue());
    }

    public void testParseNull() throws CommandLineException {
        try {
            commandLineSwitch.parse(null);
            fail("Parsed without a value");
        } catch (CommandLineException e) {
            // Expected
        }
    }

    public void testParseEmptyString() throws CommandLineException {
        assertFalse(commandLineSwitch.isPresent());
        assertEquals(DEFAULT_VALUE, commandLineSwitch.getValue());
        commandLineSwitch.parse("");
        assertTrue(commandLineSwitch.isPresent());
        assertEquals(Collections.singletonList(""), commandLineSwitch.getValue());
    }

    public void testParseString() throws CommandLineException {
        assertFalse(commandLineSwitch.isPresent());
        assertEquals(DEFAULT_VALUE, commandLineSwitch.getValue());
        commandLineSwitch.parse(EXPECTED_VALUE1);
        commandLineSwitch.parse(EXPECTED_VALUE2);
        assertTrue(commandLineSwitch.isPresent());
        assertEquals(EXPECTED_VALUE, commandLineSwitch.getValue());
    }

    public void testValidateWhenNotMandatory() throws CommandLineException {
        commandLineSwitch.validate();
        commandLineSwitch.parse(EXPECTED_VALUE1);
        commandLineSwitch.parse(EXPECTED_VALUE2);
        commandLineSwitch.validate();
    }

    public void testValidateWhenMandatory() throws CommandLineException {
        commandLineSwitch = new MultipleValuesSwitch("switch", "default", true);
        try {
            commandLineSwitch.validate();
            fail("Missing mandatory switch should not validate.");
        } catch (CommandLineException e) {
            // Expected
        }
        commandLineSwitch.parse(EXPECTED_VALUE1);
        commandLineSwitch.parse(EXPECTED_VALUE2);
        commandLineSwitch.validate();
    }
}
