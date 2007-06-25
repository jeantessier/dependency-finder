/*
 *  Copyright (c) 2001-2007, Jean Tessier
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

import org.apache.log4j.*;

import com.jeantessier.commandline.*;
import com.jeantessier.dependency.*;
import com.jeantessier.dependency.Printer;
import com.jeantessier.dependency.TextPrinter;

public class DependencyReporter extends DependencyGraphCommand {
    public DependencyReporter() throws CommandLineException {
        super("DependencyReporter");
    }

    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();
        populateCommandLineSwitchesForXMLOutput(XMLPrinter.DEFAULT_ENCODING, XMLPrinter.DEFAULT_DTD_PREFIX);

        populateCommandLineSwitchesForScoping();
        populateCommandLineSwitchesForFiltering();

        getCommandLine().addAliasSwitch("p2p", "package-scope", "package-filter");
        getCommandLine().addAliasSwitch("c2p", "class-scope", "package-filter");
        getCommandLine().addAliasSwitch("c2c", "class-scope", "class-filter");
        getCommandLine().addAliasSwitch("f2f", "feature-scope", "feature-filter");
        getCommandLine().addAliasSwitch("includes", "scope-includes", "filter-includes");
        getCommandLine().addAliasSwitch("excludes", "scope-excludes", "filter-excludes");

        getCommandLine().addToggleSwitch("show-inbounds");
        getCommandLine().addToggleSwitch("show-outbounds");
        getCommandLine().addToggleSwitch("show-empty-nodes");

        getCommandLine().addToggleSwitch("xml");
        getCommandLine().addToggleSwitch("minimize");
        getCommandLine().addToggleSwitch("maximize");
        getCommandLine().addToggleSwitch("copy-only");
    }

    protected ParameterStrategy getParameterStrategy() {
        return new AtLeastParameterStrategy(1);
    }

    protected Collection<CommandLineException> parseCommandLine(String[] args) {
        Collection<CommandLineException> exceptions = super.parseCommandLine(args);

        exceptions.addAll(validateCommandLineForScoping());
        exceptions.addAll(validateCommandLineForFiltering());

        if (getCommandLine().getToggleSwitch("maximize") && getCommandLine().getToggleSwitch("minimize")) {
            exceptions.add(new CommandLineException("Only one of -maximize or -minimize is allowed"));
        }

        return exceptions;
    }

    protected void doProcessing() throws Exception {
        SelectionCriteria scopeCriteria = getScopeCriteria();
        SelectionCriteria filterCriteria = getFilterCriteria();

        GraphCopier copier;
        if (getCommandLine().getToggleSwitch("copy-only") || getCommandLine().getToggleSwitch("maximize")) {
            copier = new GraphCopier(new SelectiveTraversalStrategy(scopeCriteria, filterCriteria));
        } else {
            copier = new GraphSummarizer(scopeCriteria, filterCriteria);
        }

        for (String filename : getCommandLine().getParameters()) {
            Logger.getLogger(DependencyReporter.class).info("Reading " + filename);
            getVerboseListener().print("Reading " + filename);

            Collection<PackageNode> packages = Collections.emptyList();

            if (filename.endsWith(".xml")) {
                NodeLoader loader = new NodeLoader(getCommandLine().getToggleSwitch("validate"));
                loader.addDependencyListener(getVerboseListener());
                packages = loader.load(filename).getPackages().values();
            }

            Logger.getLogger(DependencyReporter.class).info("Read in " + packages.size() + " package(s) from \"" + filename + "\".");

            if (getCommandLine().getToggleSwitch("maximize")) {
                new LinkMaximizer().traverseNodes(packages);
            } else if (getCommandLine().getToggleSwitch("minimize")) {
                new LinkMinimizer().traverseNodes(packages);
            }

            copier.traverseNodes(packages);
        }

        getVerboseListener().print("Printing the graph ...");

        Printer printer;
        if (getCommandLine().isPresent("xml")) {
            printer = new XMLPrinter(out, getCommandLine().getSingleSwitch("encoding"), getCommandLine().getSingleSwitch("dtd-prefix"));
        } else {
            printer = new TextPrinter(out);
        }

        if (getCommandLine().isPresent("indent-text")) {
            printer.setIndentText(getCommandLine().getSingleSwitch("indent-text"));
        }

        if (getCommandLine().isPresent("show-inbounds") || getCommandLine().isPresent("show-outbounds") || getCommandLine().isPresent("show-empty-nodes")) {
            printer.setShowInbounds(getCommandLine().isPresent("show-inbounds"));
            printer.setShowOutbounds(getCommandLine().isPresent("show-outbounds"));
            printer.setShowEmptyNodes(getCommandLine().isPresent("show-empty-nodes"));
        }

        printer.traverseNodes(copier.getScopeFactory().getPackages().values());
    }

    public static void main(String[] args) throws Exception {
        new DependencyReporter().run(args);
    }
}
