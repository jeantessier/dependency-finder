package com.jeantessier.dependencyfinder.cli;

import java.io.*;

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

    public boolean parseAndValidate(String args) throws CommandLineException, IOException {
        getCommand().parseCommandLine(args.split("\\s+"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);
        try {
            return getCommand().validateCommandLine(printStream);
        } finally {
            printStream.close();
        }
    }

    private Command getCommand() {
        return (Command) getSystemUnderTest();
    }

    private CommandLine getCommandLine() {
        return (getCommand()).getCommandLine();
    }
}
