package com.jeantessier.dependencyfinder.cli;

import fitlibrary.*;

public class CommandFixture extends DoFixture {
    public ArrayFixture switches() {
        return new ArrayFixture(((Command) getSystemUnderTest()).getCommandLine().getSwitches());
    }
}
