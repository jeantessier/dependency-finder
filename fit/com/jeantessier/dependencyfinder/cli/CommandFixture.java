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

package com.jeantessier.dependencyfinder.cli;

import java.io.*;
import java.util.*;

import fitlibrary.*;

import com.jeantessier.commandline.*;

public class CommandFixture extends DoFixture {
    public Collection switches() {
        Collection<CommandLineSwitch> results = new LinkedList<CommandLineSwitch>();

        // We replace values that are spaces only so Fit can detect them
        for (CommandLineSwitch cls : getCommandLine().getSwitches()) {
          if (cls instanceof SingleValueSwitch && cls.getDefaultValue().equals("    ")) {
            results.add(new SingleValueSwitch(cls.getName(), "****four spaces****", cls.isMandatory()));
          } else {
            results.add(cls);
          }
        }

        return results;
    }

    public CommandLine parse(String argString) throws CommandLineException {
        getCommand().parseCommandLine(safeSplit(argString));
        return getCommandLine();
    }

    public boolean parseAndValidate(String args) throws CommandLineException {
        PrintStream printStream = getNullPrintStream();
        try {
            return getCommand().validateCommandLine(safeSplit(args), printStream);
        } finally {
            printStream.close();
        }
    }

    private Command getCommand() {
        return (Command) getSystemUnderTest();
    }

    private CommandLine getCommandLine() {
        return getCommand().getCommandLine();
    }

    private String[] safeSplit(String argString) {
        String[] args = argString.split("\\s+");
        if ("".equals(argString)) {
            args = new String[0];
        }
        return args;
    }

    private PrintStream getNullPrintStream() {
        return new PrintStream(new OutputStream() {
            public void write(int i) throws IOException {
                // Do nothing
            }
        });
    }
}
