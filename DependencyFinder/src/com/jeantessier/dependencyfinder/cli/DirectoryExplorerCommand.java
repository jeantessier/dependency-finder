package com.jeantessier.dependencyfinder.cli;

import java.io.*;

import com.jeantessier.commandline.*;

/**
 * TODO: javadocs
 */
public abstract class DirectoryExplorerCommand extends Command {
    public DirectoryExplorerCommand(String name) throws CommandLineException {
        super(name);
    }

    protected void showSpecificUsage(PrintStream out) {
        out.println();
        out.println("If no files are specified, it processes the current directory.");
        out.println();
        out.println("If file is a directory, it is recusively scanned for files");
        out.println("ending in \".class\".");
        out.println();
        out.println("Defaults is text output to the console.");
        out.println();
    }

    protected boolean validateCommandLine(String[] args, PrintStream out) {
        boolean result = super.validateCommandLine(args, out);

        if (result && getCommandLine().getParameters().isEmpty()) {
            try {
                getCommandLine().getParameterStrategy().accept(".");
            } catch (CommandLineException e) {
                result = false;
            }
        }

        return result;
    }
}
