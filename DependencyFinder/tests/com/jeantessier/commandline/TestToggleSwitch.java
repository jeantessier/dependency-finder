package com.jeantessier.commandline;

import junit.framework.*;

public class TestToggleSwitch extends TestCase {
    private ToggleSwitch commandLineSwitch;

    protected void setUp() throws Exception {
        super.setUp();

        commandLineSwitch = new ToggleSwitch("switch");
    }

    public void testSetToNull() {
        assertFalse(commandLineSwitch.isPresent());
        assertEquals(false, commandLineSwitch.getValue());
        commandLineSwitch.setValue(null);
        assertTrue(commandLineSwitch.isPresent());
        assertEquals(false, commandLineSwitch.getValue());
    }

    public void testSetToObject() {
        assertFalse(commandLineSwitch.isPresent());
        assertEquals(false, commandLineSwitch.getValue());
        Object expectedValue = new Object();
        commandLineSwitch.setValue(expectedValue);
        assertTrue(commandLineSwitch.isPresent());
        assertEquals(expectedValue, commandLineSwitch.getValue());
    }

    public void testParseNull() throws CommandLineException {
        assertFalse(commandLineSwitch.isPresent());
        assertEquals(false, commandLineSwitch.getValue());
        commandLineSwitch.parse(null);
        assertTrue(commandLineSwitch.isPresent());
        assertEquals(true, commandLineSwitch.getValue());
    }

    public void testParseEmptyString() throws CommandLineException {
        assertFalse(commandLineSwitch.isPresent());
        assertEquals(false, commandLineSwitch.getValue());
        commandLineSwitch.parse("");
        assertTrue(commandLineSwitch.isPresent());
        assertEquals(true, commandLineSwitch.getValue());
    }

    public void testParseString() throws CommandLineException {
        assertFalse(commandLineSwitch.isPresent());
        assertEquals(false, commandLineSwitch.getValue());
        commandLineSwitch.parse("foobar");
        assertTrue(commandLineSwitch.isPresent());
        assertEquals(true, commandLineSwitch.getValue());
    }

    public void testValidateWhenNotMandatory() throws CommandLineException {
        commandLineSwitch.validate();
        commandLineSwitch.parse(null);
        commandLineSwitch.validate();
    }

    public void testValidateWhenMandatory() throws CommandLineException {
        commandLineSwitch = new ToggleSwitch("switch", false, true);
        try {
            commandLineSwitch.validate();
            fail("Missing mandatory switch should not validate.");
        } catch (CommandLineException e) {
            // Expected
        }
        commandLineSwitch.parse(null);
        commandLineSwitch.validate();
    }
}
