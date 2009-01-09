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

import java.util.*;

import com.jeantessier.classreader.*;
import com.jeantessier.commandline.*;
import com.jeantessier.dependency.*;

public class DependencyExtractor extends DirectoryExplorerCommand {
    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();
        populateCommandLineSwitchesForXMLOutput(com.jeantessier.dependency.XMLPrinter.DEFAULT_ENCODING, com.jeantessier.dependency.XMLPrinter.DEFAULT_DTD_PREFIX, com.jeantessier.dependency.XMLPrinter.DEFAULT_INDENT_TEXT);
        populateCommandLineSwitchesForFiltering();

        getCommandLine().addToggleSwitch("maximize");
        getCommandLine().addToggleSwitch("minimize");

        getCommandLine().addToggleSwitch("xml");
    }

    protected Collection<CommandLineException> parseCommandLine(String[] args) {
        Collection<CommandLineException> exceptions = super.parseCommandLine(args);

        exceptions.addAll(validateCommandLineForFiltering());

        if (getCommandLine().getToggleSwitch("maximize") && getCommandLine().getToggleSwitch("minimize")) {
            exceptions.add(new CommandLineException("Only one of -maximize or -minimize is allowed"));
        }

        return exceptions;
    }

    protected void doProcessing() throws Exception {
        SelectionCriteria filterCriteria = getFilterCriteria();

        NodeFactory factory = new NodeFactory();
        CodeDependencyCollector collector = new CodeDependencyCollector(factory, filterCriteria);

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(new LoadListenerVisitorAdapter(collector));
        loader.addLoadListener(getVerboseListener());
        loader.load(getCommandLine().getParameters());

        if (getCommandLine().getToggleSwitch("minimize")) {
            LinkMinimizer minimizer = new LinkMinimizer();
            minimizer.traverseNodes(factory.getPackages().values());
        } else if (getCommandLine().getToggleSwitch("maximize")) {
            LinkMaximizer maximizer = new LinkMaximizer();
            maximizer.traverseNodes(factory.getPackages().values());
        }

        getVerboseListener().print("Printing the graph ...");

        com.jeantessier.dependency.Printer printer;
        if (getCommandLine().getToggleSwitch("xml")) {
            printer = new com.jeantessier.dependency.XMLPrinter(getOut(), getCommandLine().getSingleSwitch("encoding"), getCommandLine().getSingleSwitch("dtd-prefix"));
        } else {
            printer = new com.jeantessier.dependency.TextPrinter(getOut());
        }

        if (getCommandLine().isPresent("indent-text")) {
            printer.setIndentText(getCommandLine().getSingleSwitch("indent-text"));
        }

        printer.traverseNodes(factory.getPackages().values());
    }

    public static void main(String[] args) throws Exception {
        new DependencyExtractor().run(args);
    }
}
