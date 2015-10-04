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

import org.jmock.integration.junit3.*;
import org.jmock.*;

public class TestAliasSwitch extends MockObjectTestCase {
    private static final String SWITCH_NAME = "switch";
    private static final String SWITCH_VALUE = "value";

    private CommandLineSwitch switch1;
    private CommandLineSwitch switch2;

    private AliasSwitch aliasSwitch;

    protected void setUp() throws Exception {
        super.setUp();

        switch1 = new SingleValueSwitch("switch1");
        switch2 = new OptionalValueSwitch("switch2");

        aliasSwitch = new AliasSwitch(SWITCH_NAME, switch1, switch2);
    }
    
    public void testConstructor() {
        assertEquals("Name", SWITCH_NAME, aliasSwitch.getName());
        assertEquals("Nb switches", 2, aliasSwitch.getSwitches().size());
        assertTrue("Missing switch1", aliasSwitch.getSwitches().contains(switch1));
        assertTrue("Missing switch2", aliasSwitch.getSwitches().contains(switch2));
    }

    public void testSetValueToNull() {
        aliasSwitch.setValue(null);
        assertEquals("Switch1 not default value", switch1.getDefaultValue(), switch1.getValue());
        assertEquals("Switch2 not default value", switch2.getDefaultValue(), switch2.getValue());
    }

    public void testSetValueToValue() {
        aliasSwitch.setValue(SWITCH_VALUE);
        assertEquals("Switch1 not new value", SWITCH_VALUE, switch1.getValue());
        assertEquals("Switch2 not new value", SWITCH_VALUE, switch2.getValue());
    }

    public void testIsPresent() {
        aliasSwitch.setValue(SWITCH_VALUE);
        assertTrue("Switch1 not present", switch1.isPresent());
        assertTrue("Switch2 not present", switch2.isPresent());
        assertTrue("Not present", aliasSwitch.isPresent());
    }

    public void testIsPresentWithNoSwitches() {
        aliasSwitch = new AliasSwitch(SWITCH_NAME);
        aliasSwitch.setValue(SWITCH_VALUE);
        assertFalse("Present", aliasSwitch.isPresent());
    }

    public void testParseNull() throws CommandLineException {
        aliasSwitch = new AliasSwitch(SWITCH_NAME, switch2);
        int step = aliasSwitch.parse(null);
        assertEquals("step", 1, step);
        assertFalse("Switch1 was set", switch1.isPresent());
        assertEquals("Switch2 not default value", switch2.getDefaultValue(), switch2.getValue());
    }

    public void testParseNullWithError() throws CommandLineException {
        try {
            aliasSwitch.parse(null);
            fail("Alias with SingleValueSwitch parsed null");
        } catch (CommandLineException e) {
            // Expected
        }
    }

    public void testParseValue() throws CommandLineException {
        int step = aliasSwitch.parse(SWITCH_VALUE);
        assertEquals("step", 2, step);
        assertEquals("Switch1 not new value", SWITCH_VALUE, switch1.getValue());
        assertEquals("Switch2 not new value", SWITCH_VALUE, switch2.getValue());
    }

    public void testParseNullWithNoSwitches() throws CommandLineException {
        aliasSwitch = new AliasSwitch(SWITCH_NAME);
        int step = aliasSwitch.parse(null);
        assertEquals("step", 1, step);
    }

    public void testParseValueWithNoSwitches() throws CommandLineException {
        aliasSwitch = new AliasSwitch(SWITCH_NAME);
        int step = aliasSwitch.parse(SWITCH_VALUE);
        assertEquals("step", 1, step);
    }

    public void testAccept() {
        final Visitor mockVisitor = mock(Visitor.class);

        checking(new Expectations() {{
            one (mockVisitor).visitAliasSwitch(aliasSwitch);
        }});

        aliasSwitch.accept(mockVisitor);
    }
}
