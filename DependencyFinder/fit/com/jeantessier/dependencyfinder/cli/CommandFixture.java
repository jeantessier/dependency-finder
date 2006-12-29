package com.jeantessier.dependencyfinder.cli;

import fitlibrary.*;

import com.jeantessier.commandline.*;

public class CommandFixture extends DoFixture {
    public ArrayFixture switches() {
        return new ArrayFixture(getCommandLine().getSwitches());
    }

    public CommandLine parse(String args) throws CommandLineException {
        getCommand().parseCommandLine(args.split("\\s+"));
        return getCommandLine();
    }

    private Command getCommand() {
        return (Command) getSystemUnderTest();
    }

    private CommandLine getCommandLine() {
        return (getCommand()).getCommandLine();
    }
}
