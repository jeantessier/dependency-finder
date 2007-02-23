package com.jeantessier.dependencyfinder.cli;

import java.io.*;

import fitlibrary.*;

import com.jeantessier.commandline.*;

public class CommandFixture extends DoFixture {
    public ArrayFixture switches() {
        return new ArrayFixture(getCommandLine().getSwitches());
    }

    public CommandLine parse(String argString) throws CommandLineException {
        getCommand().parseCommandLine(safeSplit(argString));
        return getCommandLine();
    }

    public boolean parseAndValidate(String args) throws CommandLineException {
        PrintStream printStream = new PrintStream(new ByteArrayOutputStream());
        try {
            return getCommand().validateCommandLine(safeSplit(args), printStream);
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

    private String[] safeSplit(String argString) {
        String[] args = argString.split("\\s+");
        if ("".equals(argString)) {
            args = new String[0];
        }
        return args;
    }
}
