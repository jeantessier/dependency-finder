package com.jeantessier.dependencyfinder.cli;

import java.io.*;

import fitlibrary.*;

import com.jeantessier.commandline.*;

public class CommandFixture extends DoFixture {
    public ArrayFixture switches() {
        return new ArrayFixture(getCommandLine().getSwitches());
    }

    public CommandLine parse(String argString) throws CommandLineException {
        String[] args = argString.split("\\s+");
        if ("".equals(argString)) {
            args = new String[0];
        }
        getCommand().parseCommandLine(args);
        return getCommandLine();
    }

    public boolean parseAndValidate(String args) throws CommandLineException {
        parse(args);

        PrintStream printStream = new PrintStream(new ByteArrayOutputStream());
        try {
            return getCommand().validateCommandLine(printStream);
        } finally {
            printStream.close();
        }
    }

    private Command getCommand() {
        return (Command) getSystemUnderTest();
    }

    public CommandLine getCommandLine() {
        return getCommand().getCommandLine();
    }
}
