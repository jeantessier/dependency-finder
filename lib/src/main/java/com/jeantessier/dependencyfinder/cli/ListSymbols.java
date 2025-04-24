/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import com.jeantessier.classreader.*;
import com.jeantessier.commandline.CommandLineException;

import java.io.*;
import java.util.*;

public class ListSymbols extends DirectoryExplorerCommand {
    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();
        populateCommandLineSwitchesForXMLOutput(XMLSymbolPrinter.DEFAULT_ENCODING, XMLSymbolPrinter.DEFAULT_DTD_PREFIX, XMLSymbolPrinter.DEFAULT_INDENT_TEXT);

        getCommandLine().addToggleSwitch("classes");
        getCommandLine().addToggleSwitch("fields");
        getCommandLine().addToggleSwitch("methods");
        getCommandLine().addToggleSwitch("local-variables");
        getCommandLine().addToggleSwitch("inner-classes");

        getCommandLine().addToggleSwitch("public-accessibility");
        getCommandLine().addToggleSwitch("protected-accessibility");
        getCommandLine().addToggleSwitch("private-accessibility");
        getCommandLine().addToggleSwitch("package-accessibility");

        getCommandLine().addToggleSwitch("non-private-fields");
        getCommandLine().addToggleSwitch("final-methods-or-classes");

        getCommandLine().addMultipleValuesSwitch("includes", DEFAULT_INCLUDES);
        getCommandLine().addMultipleValuesSwitch("includes-list");
        getCommandLine().addMultipleValuesSwitch("excludes");
        getCommandLine().addMultipleValuesSwitch("excludes-list");

        getCommandLine().addToggleSwitch("csv");
        getCommandLine().addToggleSwitch("json");
        getCommandLine().addToggleSwitch("text");
        getCommandLine().addToggleSwitch("txt");
        getCommandLine().addToggleSwitch("xml");
        getCommandLine().addToggleSwitch("yaml");
        getCommandLine().addToggleSwitch("yml");
    }

    protected Collection<CommandLineException> parseCommandLine(String[] args) {
        Collection<CommandLineException> exceptions = super.parseCommandLine(args);

        if (!getCommandLine().isPresent("classes") && !getCommandLine().isPresent("fields") && !getCommandLine().isPresent("methods") && !getCommandLine().isPresent("local-variables") && !getCommandLine().isPresent("inner-classes")) {
            getCommandLine().getSwitch("classes").setValue(true);
            getCommandLine().getSwitch("fields").setValue(true);
            getCommandLine().getSwitch("methods").setValue(true);
            getCommandLine().getSwitch("local-variables").setValue(true);
            getCommandLine().getSwitch("inner-classes").setValue(true);
        }

        int modeSwitch = 0;

        if (getCommandLine().getToggleSwitch("csv")) {
            modeSwitch++;
        }
        if (getCommandLine().getToggleSwitch("json")) {
            modeSwitch++;
        }
        if (getCommandLine().getToggleSwitch("text")) {
            modeSwitch++;
        }
        if (getCommandLine().getToggleSwitch("txt")) {
            modeSwitch++;
        }
        if (getCommandLine().getToggleSwitch("xml")) {
            modeSwitch++;
        }
        if (getCommandLine().getToggleSwitch("yaml")) {
            modeSwitch++;
        }
        if (getCommandLine().getToggleSwitch("yml")) {
            modeSwitch++;
        }
        if (modeSwitch != 1) {
            exceptions.add(new CommandLineException("Must have one and only one of -csv, -json, -text, -txt, -xml, -yml, or -yaml"));
        }

        return exceptions;
    }

    protected void doProcessing() throws Exception {
        SymbolGathererStrategy gathererStrategy;

        if (getCommandLine().getToggleSwitch("non-private-fields")) {
            gathererStrategy = new NonPrivateFieldSymbolGathererStrategy();
        } else if (getCommandLine().getToggleSwitch("final-methods-or-classes")) {
            gathererStrategy = new FinalMethodOrClassSymbolGathererStrategy();
        } else {
            DefaultSymbolGathererStrategy defaultGathererStrategy = new DefaultSymbolGathererStrategy();
            defaultGathererStrategy.setMatchingClasses(getCommandLine().getToggleSwitch("classes"));
            defaultGathererStrategy.setMatchingFields(getCommandLine().getToggleSwitch("fields"));
            defaultGathererStrategy.setMatchingMethods(getCommandLine().getToggleSwitch("methods"));
            defaultGathererStrategy.setMatchingLocalVariables(getCommandLine().getToggleSwitch("local-variables"));
            defaultGathererStrategy.setMatchingInnerClasses(getCommandLine().getToggleSwitch("inner-classes"));

            gathererStrategy = defaultGathererStrategy;
        }

        if (getCommandLine().isPresent("includes") || getCommandLine().isPresent("includes-list") || getCommandLine().isPresent("excludes") || getCommandLine().isPresent("excludes-list")) {
            gathererStrategy = new FilteringSymbolGathererStrategy(gathererStrategy, getCommandLine().getMultipleSwitch("includes"), loadCollection(getCommandLine().getMultipleSwitch("includes-list")), getCommandLine().getMultipleSwitch("excludes"), loadCollection(getCommandLine().getMultipleSwitch("excludes-list")));
        }

        if (getCommandLine().isPresent("public-accessibility") || getCommandLine().isPresent("protected-accessibility") || getCommandLine().isPresent("private-accessibility") || getCommandLine().isPresent("package-accessibility")) {
            gathererStrategy = new AccessibilitySymbolGathererStrategy(gathererStrategy, getCommandLine().getToggleSwitch("public-accessibility"), getCommandLine().getToggleSwitch("protected-accessibility"), getCommandLine().getToggleSwitch("private-accessibility"), getCommandLine().getToggleSwitch("package-accessibility"));
        }

        SymbolGatherer gatherer = new SymbolGatherer(gathererStrategy);

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(new LoadListenerVisitorAdapter(gatherer));
        loader.addLoadListener(getVerboseListener());
        loader.load(getCommandLine().getParameters());

        if (getCommandLine().isPresent("csv")) {
            printCSVFiles(gatherer);
        } else if (getCommandLine().isPresent("json")) {
            printJsonFile(gatherer);
        } else if (getCommandLine().isPresent("text") || getCommandLine().isPresent("txt")) {
            printTextFile(gatherer);
        } else if (getCommandLine().isPresent("xml")) {
            printXMLFile(gatherer);
        } else if (getCommandLine().isPresent("yaml") || getCommandLine().isPresent("yml")) {
            printYAMLFile(gatherer);
        }
    }

    private void printCSVFiles(SymbolGatherer gatherer) throws IOException {
        new CSVSymbolPrinter(
                getOut(),
                getCommandLine().getToggleSwitch("classes"),
                getCommandLine().getToggleSwitch("fields"),
                getCommandLine().getToggleSwitch("methods"),
                getCommandLine().getToggleSwitch("local-variables"),
                getCommandLine().getToggleSwitch("inner-classes"),
                getCommandLine().isPresent("out") ?
                        Optional.of(getCommandLine().getSingleSwitch("out")) :
                        Optional.empty()
        ).print(gatherer);
    }

    private void printJsonFile(SymbolGatherer gatherer) throws IOException {
        new JSONSymbolPrinter(getOut()).print(gatherer);
    }

    private void printTextFile(SymbolGatherer gatherer) throws IOException {
        new TextSymbolPrinter(getOut()).print(gatherer);
    }

    private void printXMLFile(SymbolGatherer gatherer) throws IOException {
        new XMLSymbolPrinter(
                getOut(),
                getCommandLine().getSingleSwitch("encoding"),
                getCommandLine().getSingleSwitch("dtd-prefix"),
                getCommandLine().getSingleSwitch("indent-text")
        ).print(gatherer);
    }

    private void printYAMLFile(SymbolGatherer gatherer) throws IOException {
        new YAMLSymbolPrinter(
                getOut(),
                getCommandLine().getSingleSwitch("indent-text")
        ).print(gatherer);
    }

    public static void main(String[] args) throws Exception {
        new ListSymbols().run(args);
    }
}
