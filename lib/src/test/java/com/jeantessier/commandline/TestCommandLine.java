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

public class TestCommandLine {
    private static final String SWITCH_NAME = "switch";
    private static final String SWITCH_DEFAULT_VALUE = "default value";
    private static final String SWITCH_VALUE = "value";

    private final CommandLine commandLine = new CommandLine();

    @Test
    void testAddToggleSwitch() {
        CommandLineSwitch cls = commandLine.addToggleSwitch(SWITCH_NAME);
        assertInstanceOf(ToggleSwitch.class, cls, "Wrong type");
        assertEquals(SWITCH_NAME, cls.getName(), "name");
        assertFalse(cls.isPresent(), "is present");
    }

    @Test
    void addAliasSwitch() {
        ToggleSwitch toggle1 = commandLine.addToggleSwitch("toggle1");
        ToggleSwitch toggle2 = commandLine.addToggleSwitch("toggle2");

        AliasSwitch aliasSwitch = commandLine.addAliasSwitch(SWITCH_NAME, "toggle1", "toggle2");
        assertEquals(2, aliasSwitch.getSwitches().size(), "Nb switches");
        assertTrue(aliasSwitch.getSwitches().contains(toggle1), "Missing toggle1");
        assertTrue(aliasSwitch.getSwitches().contains(toggle2), "Missing toggle2");
    }

    @Test
    void testAddAliasForNonExistingSwitch() {
        assertThrows(IllegalArgumentException.class, () -> commandLine.addAliasSwitch(SWITCH_NAME, "foobar"));
    }

    @Test
    void testParseToggleSwitchTwice() {
        commandLine.addToggleSwitch(SWITCH_NAME);
        String cls = "-" + SWITCH_NAME;
        commandLine.parse(new String[] {cls, cls});
        assertTrue(commandLine.isPresent(SWITCH_NAME), "Missing switch");
    }

    @Test
    void testParseSingleValueSwitchTwice() {
        commandLine.addSingleValueSwitch(SWITCH_NAME);
        String cls = "-" + SWITCH_NAME;
        commandLine.parse(new String[] {cls, SWITCH_VALUE, cls, SWITCH_VALUE});
        assertTrue(commandLine.isPresent(SWITCH_NAME), "Missing switch");
        assertEquals(SWITCH_VALUE, commandLine.getSingleSwitch(SWITCH_NAME), "Single value switch value");
    }

    @Test
    void testParseOptionalValueSwitchTwice() {
        commandLine.addOptionalValueSwitch(SWITCH_NAME, SWITCH_DEFAULT_VALUE);
        String cls = "-" + SWITCH_NAME;
        commandLine.parse(new String[] {cls, SWITCH_VALUE, cls});
        assertTrue(commandLine.isPresent(SWITCH_NAME), "Missing switch");
        assertEquals(SWITCH_DEFAULT_VALUE, commandLine.getOptionalSwitch(SWITCH_NAME), "Optional value switch value");
    }

    @Test
    void testParseMultipleValueSwitchTwice() {
        commandLine.addMultipleValuesSwitch(SWITCH_NAME, SWITCH_DEFAULT_VALUE);
        String cls = "-" + SWITCH_NAME;
        commandLine.parse(new String[] {cls, SWITCH_VALUE, cls, SWITCH_VALUE});
        assertTrue(commandLine.isPresent(SWITCH_NAME), "Missing switch");
        assertEquals(2, commandLine.getMultipleSwitch(SWITCH_NAME).size(), "Multiple value switch value size");
        assertEquals(SWITCH_VALUE, commandLine.getMultipleSwitch(SWITCH_NAME).get(0), "Multiple value switch value 0");
        assertEquals(SWITCH_VALUE, commandLine.getMultipleSwitch(SWITCH_NAME).get(1), "Multiple value switch value 1");
    }

    @Test
    void testParseAliasSwitch() {
        commandLine.addToggleSwitch("toggle1");
        commandLine.addToggleSwitch("toggle2");
        commandLine.addAliasSwitch(SWITCH_NAME, "toggle1", "toggle2");

        String cls = "-" + SWITCH_NAME;
        commandLine.parse(new String[] {cls});
        assertTrue(commandLine.isPresent("toggle1"), "Missing toggle1");
        assertTrue(commandLine.isPresent("toggle2"), "Missing toggle2");
    }

    @Test
    void testParseMissingValueFollowedBySwitch() {
        commandLine.addSingleValueSwitch(SWITCH_NAME);

        String cls = "-" + SWITCH_NAME;
        commandLine.parse(new String[] {cls, cls, SWITCH_VALUE});
    }
}
