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
import javax.xml.parsers.*;

import org.xml.sax.*;

import com.jeantessier.dependency.*;

/**
 * TODO Class comments
 */
public abstract class DependencyGraphCommand extends Command {
    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();

        getCommandLine().addToggleSwitch("validate");
    }

    protected void showSpecificUsage(PrintStream out) {
        out.println();
        out.println("Default is text output to the console.");
        out.println();
    }

    protected NodeFactory loadGraph() throws IOException, SAXException, ParserConfigurationException {
        NodeFactory result = new NodeFactory();

        if (getCommandLine().getParameters().isEmpty()) {
            loadGraphFromSystemIn(result);
        } else {
            loadGraphFromFiles(result);
        }

        return result;
    }

    private void loadGraphFromSystemIn(NodeFactory factory) throws IOException, SAXException, ParserConfigurationException {
        getVerboseListener().print("Reading from standard input");

        NodeLoader loader = new NodeLoader(factory, getCommandLine().getToggleSwitch("validate"));
        loader.addDependencyListener(getVerboseListener());
        loader.load(System.in);

        getVerboseListener().print("Read from standard input.");
    }

    private void loadGraphFromFiles(NodeFactory factory) throws IOException, SAXException, ParserConfigurationException {
        for (String filename : getCommandLine().getParameters()) {
            if (filename.endsWith(".xml")) {
                loadGraphFromFile(factory, filename);
            } else {
                getVerboseListener().print("Skipping \"" + filename + "\".");
            }
        }
    }

    private void loadGraphFromFile(NodeFactory factory, String filename) throws IOException, SAXException, ParserConfigurationException {
        getVerboseListener().print("Reading " + filename);

        NodeLoader loader = new NodeLoader(factory, getCommandLine().getToggleSwitch("validate"));
        loader.addDependencyListener(getVerboseListener());
        loader.load(filename);

        getVerboseListener().print("Read \"" + filename + "\".");
    }
}
