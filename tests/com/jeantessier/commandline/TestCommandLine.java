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

import junit.framework.*;

public class TestCommandLine extends TestCase {
    private static final String SWITCH_NAME = "switch";
    private static final String SWITCH_DEFAULT_VALUE = "default value";
    private static final String SWITCH_VALUE = "value";

    private CommandLine commandLine;

    protected void setUp() throws Exception {
        super.setUp();

        commandLine = new CommandLine();
    }

    public void testAddToggleSwitch() {
        CommandLineSwitch cls = commandLine.addToggleSwitch(SWITCH_NAME);
        assertTrue("Wrong type", cls instanceof ToggleSwitch);
        assertEquals("name", SWITCH_NAME, cls.getName());
        assertFalse("is present", cls.isPresent());
    }

    public void addAliasSwitch() throws CommandLineException {
        ToggleSwitch toggle1 = commandLine.addToggleSwitch("toggle1");
        ToggleSwitch toggle2 = commandLine.addToggleSwitch("toggle2");

        AliasSwitch aliasSwitch = commandLine.addAliasSwitch(SWITCH_NAME, "toogle1", "toggle2");
        assertEquals("Nb switches", 2, aliasSwitch.getSwitches().size());
        assertTrue("Missing toggle1", aliasSwitch.getSwitches().contains(toggle1));
        assertTrue("Missing toggle2", aliasSwitch.getSwitches().contains(toggle2));
    }

    public void testAddAliasForNonExistingSwitch() {
        try {
            commandLine.addAliasSwitch(SWITCH_NAME, "foobar");
            fail("Added alias to non-existing switch");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testParseToggleSwitchTwice() throws CommandLineException {
        commandLine.addToggleSwitch(SWITCH_NAME);
        String cls = "-" + SWITCH_NAME;
        commandLine.parse(new String[] {cls, cls});
        assertTrue("Missing switch", commandLine.isPresent(SWITCH_NAME));
    }

    public void testParseSingleValueSwitchTwice() throws CommandLineException {
        commandLine.addSingleValueSwitch(SWITCH_NAME);
        String cls = "-" + SWITCH_NAME;
        commandLine.parse(new String[] {cls, SWITCH_VALUE, cls, SWITCH_VALUE});
        assertTrue("Missing switch", commandLine.isPresent(SWITCH_NAME));
        assertEquals("Single value switch value", SWITCH_VALUE, commandLine.getSingleSwitch(SWITCH_NAME));
    }

    public void testParseOptionalValueSwitchTwice() throws CommandLineException {
        commandLine.addOptionalValueSwitch(SWITCH_NAME, SWITCH_DEFAULT_VALUE);
        String cls = "-" + SWITCH_NAME;
        commandLine.parse(new String[] {cls, SWITCH_VALUE, cls});
        assertTrue("Missing switch", commandLine.isPresent(SWITCH_NAME));
        assertEquals("Optional value switch value", SWITCH_DEFAULT_VALUE, commandLine.getOptionalSwitch(SWITCH_NAME));
    }

    public void testParseMultipleValueSwitchTwice() throws CommandLineException {
        commandLine.addMultipleValuesSwitch(SWITCH_NAME, SWITCH_DEFAULT_VALUE);
        String cls = "-" + SWITCH_NAME;
        commandLine.parse(new String[] {cls, SWITCH_VALUE, cls, SWITCH_VALUE});
        assertTrue("Missing switch", commandLine.isPresent(SWITCH_NAME));
        assertEquals("Multiple value switch value size", 2, commandLine.getMultipleSwitch(SWITCH_NAME).size());
        assertEquals("Multiple value switch value 0", SWITCH_VALUE, commandLine.getMultipleSwitch(SWITCH_NAME).get(0));
        assertEquals("Multiple value switch value 1", SWITCH_VALUE, commandLine.getMultipleSwitch(SWITCH_NAME).get(1));
    }

    public void testParseAliasSwitch() throws CommandLineException {
        commandLine.addToggleSwitch("toggle1");
        commandLine.addToggleSwitch("toggle2");
        commandLine.addAliasSwitch(SWITCH_NAME, "toggle1", "toggle2");

        String cls = "-" + SWITCH_NAME;
        commandLine.parse(new String[] {cls});
        assertTrue("Missing toggle1", commandLine.isPresent("toggle1"));
        assertTrue("Missing toggle2", commandLine.isPresent("toggle2"));
    }

    public void testParseMissingValueFollowedBySwitch() throws CommandLineException {
        commandLine.addSingleValueSwitch(SWITCH_NAME);

        String cls = "-" + SWITCH_NAME;
        commandLine.parse(new String[] {cls, cls, SWITCH_VALUE});
    }
}
