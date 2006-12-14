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

import javax.xml.parsers.*;

import org.apache.log4j.*;
import org.xml.sax.*;

import com.jeantessier.commandline.*;
import com.jeantessier.dependency.*;
import com.jeantessier.dependency.Printer;
import com.jeantessier.dependency.TextPrinter;

public class DependencyClosure extends Command {
    public static final String DEFAULT_START_INCLUDES = "//";

    public DependencyClosure() {
        super("DependencyClosure");
    }

    protected void showSpecificUsage(PrintStream out) {
        out.println();
        out.println("Defaults is text output to the console.");
        out.println();
    }

    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();
        populateCommandLineSwitchesForXMLOutput(XMLPrinter.DEFAULT_ENCODING, XMLPrinter.DEFAULT_DTD_PREFIX);

        getCommandLine().addMultipleValuesSwitch("start-includes", DEFAULT_START_INCLUDES);
        getCommandLine().addMultipleValuesSwitch("start-excludes");
        getCommandLine().addMultipleValuesSwitch("package-start-includes");
        getCommandLine().addMultipleValuesSwitch("package-start-excludes");
        getCommandLine().addMultipleValuesSwitch("class-start-includes");
        getCommandLine().addMultipleValuesSwitch("class-start-excludes");
        getCommandLine().addMultipleValuesSwitch("feature-start-includes");
        getCommandLine().addMultipleValuesSwitch("feature-start-excludes");
        getCommandLine().addMultipleValuesSwitch("stop-includes");
        getCommandLine().addMultipleValuesSwitch("stop-excludes");
        getCommandLine().addMultipleValuesSwitch("package-stop-includes");
        getCommandLine().addMultipleValuesSwitch("package-stop-excludes");
        getCommandLine().addMultipleValuesSwitch("class-stop-includes");
        getCommandLine().addMultipleValuesSwitch("class-stop-excludes");
        getCommandLine().addMultipleValuesSwitch("feature-stop-includes");
        getCommandLine().addMultipleValuesSwitch("feature-stop-excludes");

        getCommandLine().addMultipleValuesSwitch("start-includes-list");
        getCommandLine().addMultipleValuesSwitch("start-excludes-list");
        getCommandLine().addMultipleValuesSwitch("stop-includes-list");
        getCommandLine().addMultipleValuesSwitch("stop-excludes-list");

        getCommandLine().addOptionalValueSwitch("maximum-inbound-depth");
        getCommandLine().addOptionalValueSwitch("maximum-outbound-depth");

        getCommandLine().addToggleSwitch("xml");
        getCommandLine().addToggleSwitch("validate");
    }

    protected void doProcessing() throws IOException {
        NodeFactory factory = new NodeFactory();

        for (String filename : getCommandLine().getParameters()) {
            if (filename.endsWith(".xml")) {
                getVerboseListener().print("Reading " + filename);
                try {
                    NodeLoader loader = new NodeLoader(factory, getCommandLine().getToggleSwitch("validate"));
                    loader.addDependencyListener(getVerboseListener());
                    loader.load(filename);
                } catch (SAXException e) {
                    String message = "Problem with " + filename;
                    getVerboseListener().print(message + ": " + e.getMessage());
                    Logger.getLogger(getClass()).error(message, e);
                } catch (ParserConfigurationException e) {
                    String message = "Problem with " + filename;
                    getVerboseListener().print(message + ": " + e.getMessage());
                    Logger.getLogger(getClass()).error(message, e);
                }
                getVerboseListener().print("Read \"" + filename + "\".");
            } else {
                getVerboseListener().print("Skipping \"" + filename + "\".");
            }
        }

        SelectionCriteria startCriteria;
        if (hasStartRegularExpressionSwitches(getCommandLine())) {
            RegularExpressionSelectionCriteria regularExpressionStartCriteria = new RegularExpressionSelectionCriteria();

            if (getCommandLine().isPresent("start-includes") || (!getCommandLine().isPresent("package-start-includes") && !getCommandLine().isPresent("class-start-includes") && !getCommandLine().isPresent("feature-start-includes"))) {
                // Only use the default if nothing else has been specified.
                regularExpressionStartCriteria.setGlobalIncludes(getCommandLine().getMultipleSwitch("start-includes"));
            }
            regularExpressionStartCriteria.setGlobalExcludes(getCommandLine().getMultipleSwitch("start-excludes"));
            regularExpressionStartCriteria.setPackageIncludes(getCommandLine().getMultipleSwitch("package-start-includes"));
            regularExpressionStartCriteria.setPackageExcludes(getCommandLine().getMultipleSwitch("package-start-excludes"));
            regularExpressionStartCriteria.setClassIncludes(getCommandLine().getMultipleSwitch("class-start-includes"));
            regularExpressionStartCriteria.setClassExcludes(getCommandLine().getMultipleSwitch("class-start-excludes"));
            regularExpressionStartCriteria.setFeatureIncludes(getCommandLine().getMultipleSwitch("feature-start-includes"));
            regularExpressionStartCriteria.setFeatureExcludes(getCommandLine().getMultipleSwitch("feature-start-excludes"));

            startCriteria = regularExpressionStartCriteria;
        } else if (hasStartListSwitches(getCommandLine())) {
            startCriteria = createCollectionSelectionCriteria(getCommandLine().getMultipleSwitch("start-includes-list"), getCommandLine().getMultipleSwitch("start-excludes-list"));
        } else {
            startCriteria = new ComprehensiveSelectionCriteria();
        }

        SelectionCriteria stopCriteria;
        if (hasStopRegularExpressionSwitches(getCommandLine())) {
            RegularExpressionSelectionCriteria regularExpressionStopCriteria = new RegularExpressionSelectionCriteria();

            regularExpressionStopCriteria.setGlobalIncludes(getCommandLine().getMultipleSwitch("stop-includes"));
            regularExpressionStopCriteria.setGlobalExcludes(getCommandLine().getMultipleSwitch("stop-excludes"));
            regularExpressionStopCriteria.setPackageIncludes(getCommandLine().getMultipleSwitch("package-stop-includes"));
            regularExpressionStopCriteria.setPackageExcludes(getCommandLine().getMultipleSwitch("package-stop-excludes"));
            regularExpressionStopCriteria.setClassIncludes(getCommandLine().getMultipleSwitch("class-stop-includes"));
            regularExpressionStopCriteria.setClassExcludes(getCommandLine().getMultipleSwitch("class-stop-excludes"));
            regularExpressionStopCriteria.setFeatureIncludes(getCommandLine().getMultipleSwitch("feature-stop-includes"));
            regularExpressionStopCriteria.setFeatureExcludes(getCommandLine().getMultipleSwitch("feature-stop-excludes"));

            stopCriteria = regularExpressionStopCriteria;
        } else if (hasStopListSwitches(getCommandLine())) {
            stopCriteria = createCollectionSelectionCriteria(getCommandLine().getMultipleSwitch("stop-includes-list"), getCommandLine().getMultipleSwitch("stop-excludes-list"));
        } else {
            stopCriteria = new NullSelectionCriteria();
        }

        TransitiveClosure selector = new TransitiveClosure(startCriteria, stopCriteria);

        try {
            if (getCommandLine().isPresent("maximum-inbound-depth")) {
                selector.setMaximumInboundDepth(Long.parseLong(getCommandLine().getSingleSwitch("maximum-inbound-depth")));
            }
        } catch (NumberFormatException ex) {
            selector.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        }

        try {
            if (getCommandLine().isPresent("maximum-outbound-depth")) {
                selector.setMaximumOutboundDepth(Long.parseLong(getCommandLine().getSingleSwitch("maximum-outbound-depth")));
            }
        } catch (NumberFormatException ex) {
            selector.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        }

        Logger.getLogger(getClass()).info("Operating on " + factory.getPackages().values().size() + " package(s) ...");

        selector.traverseNodes(factory.getPackages().values());

        Logger.getLogger(getClass()).info("Reporting " + selector.getFactory().getPackages().values().size() + " package(s) ...");

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

        printer.traverseNodes(selector.getFactory().getPackages().values());
    }

    private static boolean hasStartRegularExpressionSwitches(CommandLine commandLine) {
        Collection<String> switches = commandLine.getPresentSwitches();

        return
            switches.contains("start-includes") ||
            switches.contains("start-excludes") ||
            switches.contains("package-start-includes") ||
            switches.contains("package-start-excludes") ||
            switches.contains("class-start-includes") ||
            switches.contains("class-start-excludes") ||
            switches.contains("feature-start-includes") ||
            switches.contains("feature-start-excludes");
    }

    private static boolean hasStartListSwitches(CommandLine commandLine) {
        Collection<String> switches = commandLine.getPresentSwitches();

        return
            switches.contains("start-includes-list") ||
            switches.contains("start-excludes-list");
    }

    private static boolean hasStopRegularExpressionSwitches(CommandLine commandLine) {
        Collection<String> switches = commandLine.getPresentSwitches();

        return
            switches.contains("stop-includes") ||
            switches.contains("stop-excludes") ||
            switches.contains("package-stop-includes") ||
            switches.contains("package-stop-excludes") ||
            switches.contains("class-stop-includes") ||
            switches.contains("class-stop-excludes") ||
            switches.contains("feature-stop-includes") ||
            switches.contains("feature-stop-excludes");
    }

    private static boolean hasStopListSwitches(CommandLine commandLine) {
        Collection<String> switches = commandLine.getPresentSwitches();

        return
            switches.contains("stop-includes-list") ||
            switches.contains("stop-excludes-list");
    }

    public static void main(String[] args) throws Exception {
        new DependencyClosure().run(args);
    }
}
