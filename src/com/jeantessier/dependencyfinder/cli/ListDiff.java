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

import com.jeantessier.diff.*;
import com.jeantessier.commandline.*;

public class ListDiff extends Command {
    protected void showSpecificUsage(PrintStream out) {
        out.println();
        out.println("Defaults is text output to the console.");
        out.println();
    }

    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();
        populateCommandLineSwitchesForXMLOutput(ListDiffPrinter.DEFAULT_ENCODING, ListDiffPrinter.DEFAULT_DTD_PREFIX, ListDiffPrinter.DEFAULT_INDENT_TEXT);

        getCommandLine().addSingleValueSwitch("name");
        getCommandLine().addSingleValueSwitch("old-label");
        getCommandLine().addSingleValueSwitch("old", true);
        getCommandLine().addSingleValueSwitch("new-label");
        getCommandLine().addSingleValueSwitch("new", true);
        getCommandLine().addToggleSwitch("compress");
    }

    protected Collection<CommandLineException> parseCommandLine(String[] args) {
        Collection<CommandLineException> exceptions = super.parseCommandLine(args);

        if (!getCommandLine().isPresent("old-label")) {
            getCommandLine().getSwitch("old-label").setValue(getCommandLine().getSingleSwitch("old"));
        }

        if (!getCommandLine().isPresent("new-label")) {
            getCommandLine().getSwitch("new-label").setValue(getCommandLine().getSingleSwitch("new"));
        }

        return exceptions;
    }

    protected void doProcessing() throws Exception {
        String line;
        
        getVerboseListener().print("Loading old list ...");
        Collection<String> oldAPI = new TreeSet<String>();
        BufferedReader oldIn = new BufferedReader(new FileReader(getCommandLine().getSingleSwitch("old")));
        while((line = oldIn.readLine()) != null) {
            oldAPI.add(line);
        }
        oldIn.close();

        getVerboseListener().print("Loading new list ...");
        Collection<String> newAPI = new TreeSet<String>();
        BufferedReader newIn = new BufferedReader(new FileReader(getCommandLine().getSingleSwitch("new")));
        while((line = newIn.readLine()) != null) {
            newAPI.add(line);
        }
        newIn.close();

        ListDiffPrinter printer = new ListDiffPrinter(getCommandLine().getToggleSwitch("compress"), getCommandLine().getSingleSwitch("encoding"), getCommandLine().getSingleSwitch("dtd-prefix"));
        printer.setName(getCommandLine().getSingleSwitch("name"));
        printer.setOldVersion(getCommandLine().getSingleSwitch("old-label"));
        printer.setNewVersion(getCommandLine().getSingleSwitch("new-label"));
        if (getCommandLine().isPresent("indent-text")) {
            printer.setIndentText(getCommandLine().getSingleSwitch("indent-text"));
        }

        getVerboseListener().print("Computing removed elements ...");
        for (String name : oldAPI) {
            if (!newAPI.contains(name)) {
                printer.remove(name);
            }
        }

        getVerboseListener().print("Computing added elements ...");
        for (String name : newAPI) {
            if (!oldAPI.contains(name)) {
                printer.add(name);
            }
        }

        getVerboseListener().print("Printing results ...");
        getOut().print(printer);
    }

    public static void main(String[] args) throws Exception {
        new ListDiff().run(args);
    }
}
