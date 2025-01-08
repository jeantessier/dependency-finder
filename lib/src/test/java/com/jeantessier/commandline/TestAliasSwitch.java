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

import org.jmock.*;
import org.junit.jupiter.api.*;

import com.jeantessier.MockObjectTestCase;

import static org.junit.jupiter.api.Assertions.*;

public class TestAliasSwitch extends MockObjectTestCase {
    private static final String SWITCH_NAME = "switch";
    private static final String SWITCH_VALUE = "value";

    private final CommandLineSwitch switch1 = new SingleValueSwitch("switch1");
    private final CommandLineSwitch switch2 = new OptionalValueSwitch("switch2");

    private AliasSwitch aliasSwitch = new AliasSwitch(SWITCH_NAME, switch1, switch2);

    @Test
    void testConstructor() {
        assertEquals(SWITCH_NAME, aliasSwitch.getName(), "Name");
        assertEquals(2, aliasSwitch.getSwitches().size(), "Nb switches");
        assertTrue(aliasSwitch.getSwitches().contains(switch1), "Missing switch1");
        assertTrue(aliasSwitch.getSwitches().contains(switch2), "Missing switch2");
    }

    @Test
    void testSetValueToNull() {
        aliasSwitch.setValue(null);
        assertEquals(switch1.getDefaultValue(), switch1.getValue(), "Switch1 not default value");
        assertEquals(switch2.getDefaultValue(), switch2.getValue(), "Switch2 not default value");
    }

    @Test
    void testSetValueToValue() {
        aliasSwitch.setValue(SWITCH_VALUE);
        assertEquals(SWITCH_VALUE, switch1.getValue(), "Switch1 not new value");
        assertEquals(SWITCH_VALUE, switch2.getValue(), "Switch2 not new value");
    }

    @Test
    void testIsPresent() {
        aliasSwitch.setValue(SWITCH_VALUE);
        assertTrue(switch1.isPresent(), "Switch1 not present");
        assertTrue(switch2.isPresent(), "Switch2 not present");
        assertTrue(aliasSwitch.isPresent(), "Not present");
    }

    @Test
    void testIsPresentWithNoSwitches() {
        aliasSwitch = new AliasSwitch(SWITCH_NAME);
        aliasSwitch.setValue(SWITCH_VALUE);
        assertFalse(aliasSwitch.isPresent(), "Present");
    }

    @Test
    void testParseNull() {
        aliasSwitch = new AliasSwitch(SWITCH_NAME, switch2);
        int step = aliasSwitch.parse(null);
        assertEquals(1, step, "step");
        assertFalse(switch1.isPresent(), "Switch1 was set");
        assertEquals(switch2.getDefaultValue(), switch2.getValue(), "Switch2 not default value");
    }

    @Test
    void testParseNullWithError() {
        assertThrows(CommandLineException.class, () -> aliasSwitch.parse(null));
    }

    @Test
    void testParseValue() {
        int step = aliasSwitch.parse(SWITCH_VALUE);
        assertEquals(2, step, "step");
        assertEquals(SWITCH_VALUE, switch1.getValue(), "Switch1 not new value");
        assertEquals(SWITCH_VALUE, switch2.getValue(), "Switch2 not new value");
    }

    @Test
    void testParseNullWithNoSwitches() {
        aliasSwitch = new AliasSwitch(SWITCH_NAME);
        int step = aliasSwitch.parse(null);
        assertEquals(1, step, "step");
    }

    @Test
    void testParseValueWithNoSwitches() {
        aliasSwitch = new AliasSwitch(SWITCH_NAME);
        int step = aliasSwitch.parse(SWITCH_VALUE);
        assertEquals(1, step, "step");
    }

    @Test
    void testAccept() {
        final Visitor mockVisitor = mock(Visitor.class);

        checking(new Expectations() {{
            oneOf (mockVisitor).visitAliasSwitch(aliasSwitch);
        }});

        aliasSwitch.accept(mockVisitor);
    }
}
