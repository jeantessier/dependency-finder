/*
 *  Copyright (c) 2001-2006, Jean Tessier
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

import org.apache.log4j.*;

import com.jeantessier.dependency.*;

public class DependencyReporter extends Command {
    public DependencyReporter() {
        super("DependencyReporter");
    }

    protected void showSpecificUsage(PrintStream out) {
        out.println();
        out.println("-p2p shorthand for the combination:");
        out.println("    -package-scope");
        out.println("    -package-filter");
        out.println();
        out.println("-c2p shorthand for the combination:");
        out.println("    -class-scope");
        out.println("    -package-filter");
        out.println();
        out.println("-c2c shorthand for the combination:");
        out.println("    -class-scope");
        out.println("    -class-filter");
        out.println();
        out.println("-f2f shorthand for the combination:");
        out.println("    -feature-scope");
        out.println("    -feature-filter");
        out.println();
        out.println("-includes \"str\" shorthand for the combination:");
        out.println("    -scope-includes \"str\"");
        out.println("    -filter-includes \"str\"");
        out.println();
        out.println("-excludes \"str\" shorthand for the combination:");
        out.println("    -scope-excludes \"str\"");
        out.println("    -filter-excludes \"str\"");
        out.println();
        out.println("Default is text output to the console.");
        out.println();
    }

    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();
        populateCommandLineSwitchesForXMLOutput(XMLPrinter.DEFAULT_ENCODING, XMLPrinter.DEFAULT_DTD_PREFIX);

        populateCommandLineSwitchesForScoping();
        populateCommandLineSwitchesForFiltering();

        getCommandLine().addToggleSwitch("p2p");
        getCommandLine().addToggleSwitch("c2p");
        getCommandLine().addToggleSwitch("c2c");
        getCommandLine().addToggleSwitch("f2f");
        getCommandLine().addMultipleValuesSwitch("includes", DEFAULT_INCLUDES);
        getCommandLine().addMultipleValuesSwitch("excludes");

        getCommandLine().addToggleSwitch("show-inbounds");
        getCommandLine().addToggleSwitch("show-outbounds");
        getCommandLine().addToggleSwitch("show-empty-nodes");

        getCommandLine().addToggleSwitch("xml");
        getCommandLine().addToggleSwitch("validate");
        getCommandLine().addToggleSwitch("minimize");
        getCommandLine().addToggleSwitch("maximize");
        getCommandLine().addToggleSwitch("copy-only");
    }

    public static void main(String[] args) throws Exception {
        new DependencyReporter().run(args);
    }

    protected void doProcessing() throws Exception {
        if (getCommandLine().isPresent("p2p")) {
            getCommandLine().getSwitch("package-scope").setValue(true);
            getCommandLine().getSwitch("package-filter").setValue(true);
        }

        if (getCommandLine().isPresent("c2p")) {
            getCommandLine().getSwitch("class-scope").setValue(true);
            getCommandLine().getSwitch("package-filter").setValue(true);
        }

        if (getCommandLine().isPresent("c2c")) {
            getCommandLine().getSwitch("class-scope").setValue(true);
            getCommandLine().getSwitch("class-filter").setValue(true);
        }

        if (getCommandLine().isPresent("f2f")) {
            getCommandLine().getSwitch("feature-scope").setValue(true);
            getCommandLine().getSwitch("feature-filter").setValue(true);
        }

        if (getCommandLine().isPresent("includes")) {
            for (String value : getCommandLine().getMultipleSwitch("includes")) {
                getCommandLine().getSwitch("scope-includes").setValue(value);
                getCommandLine().getSwitch("filter-includes").setValue(value);
            }
        }

        if (getCommandLine().isPresent("excludes")) {
            for (String value : getCommandLine().getMultipleSwitch("excludes")) {
                getCommandLine().getSwitch("scope-excludes").setValue(value);
                getCommandLine().getSwitch("filter-excludes").setValue(value);
            }
        }

        if (getCommandLine().getToggleSwitch("maximize") && getCommandLine().getToggleSwitch("minimize")) {
            showError(System.err, "Only one of -maximize or -minimize allowed");
        }

        if (hasScopeRegularExpressionSwitches() && hasScopeListSwitches()) {
            showError(System.err, "You can use switches for regular expressions or lists for scope, but not at the same time");
        }

        if (hasFilterRegularExpressionSwitches() && hasFilterListSwitches()) {
            showError(System.err, "You can use switches for regular expressions or lists for filter, but not at the same time");
        }

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

        Logger.getLogger(DependencyReporter.class).info("Reporting " + copier.getScopeFactory().getPackages().values().size() + " package(s) ...");
    
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
}
