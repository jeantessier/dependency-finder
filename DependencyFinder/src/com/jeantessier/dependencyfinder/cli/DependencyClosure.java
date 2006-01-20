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

import com.jeantessier.commandline.*;
import com.jeantessier.dependency.*;
import com.jeantessier.dependencyfinder.*;

public class DependencyClosure {
    public static final String DEFAULT_START_INCLUDES  = "//";
    public static final String DEFAULT_LOGFILE         = "System.out";

    public static void showError(CommandLineUsage clu, String msg) {
        System.err.println(msg);
        showError(clu);
    }

    public static void showError(CommandLineUsage clu) {
        System.err.println(clu);
        System.err.println();
        System.err.println("Defaults is text output to the console.");
        System.err.println();
    }

    public static void showVersion() {
        Version version = new Version();
        
        System.err.print(version.getImplementationTitle());
        System.err.print(" ");
        System.err.print(version.getImplementationVersion());
        System.err.print(" (c) ");
        System.err.print(version.getCopyrightDate());
        System.err.print(" ");
        System.err.print(version.getCopyrightHolder());
        System.err.println();
        
        System.err.print(version.getImplementationURL());
        System.err.println();
        
        System.err.print("Compiled on ");
        System.err.print(version.getImplementationDate());
        System.err.println();
    }

    public static void main(String[] args) throws Exception {
        // Parsing the command line
        CommandLine commandLine = new CommandLine(new AtLeastParameterStrategy(1));
        commandLine.addMultipleValuesSwitch("start-includes",          DEFAULT_START_INCLUDES);
        commandLine.addMultipleValuesSwitch("start-excludes");
        commandLine.addMultipleValuesSwitch("package-start-includes");
        commandLine.addMultipleValuesSwitch("package-start-excludes");
        commandLine.addMultipleValuesSwitch("class-start-includes");
        commandLine.addMultipleValuesSwitch("class-start-excludes");
        commandLine.addMultipleValuesSwitch("feature-start-includes");
        commandLine.addMultipleValuesSwitch("feature-start-excludes");
        commandLine.addMultipleValuesSwitch("stop-includes");
        commandLine.addMultipleValuesSwitch("stop-excludes");
        commandLine.addMultipleValuesSwitch("package-stop-includes");
        commandLine.addMultipleValuesSwitch("package-stop-excludes");
        commandLine.addMultipleValuesSwitch("class-stop-includes");
        commandLine.addMultipleValuesSwitch("class-stop-excludes");
        commandLine.addMultipleValuesSwitch("feature-stop-includes");
        commandLine.addMultipleValuesSwitch("feature-stop-excludes");

        commandLine.addOptionalValueSwitch("maximum-inbound-depth");
        commandLine.addOptionalValueSwitch("maximum-outbound-depth");
        
        commandLine.addToggleSwitch("xml");
        commandLine.addToggleSwitch("validate");
        commandLine.addSingleValueSwitch("encoding",                   XMLPrinter.DEFAULT_ENCODING);
        commandLine.addSingleValueSwitch("dtd-prefix",                 XMLPrinter.DEFAULT_DTD_PREFIX);
        commandLine.addSingleValueSwitch("indent-text");
        commandLine.addToggleSwitch("time");
        commandLine.addSingleValueSwitch("out");
        commandLine.addToggleSwitch("help");
        commandLine.addOptionalValueSwitch("verbose",                  DEFAULT_LOGFILE);
        commandLine.addToggleSwitch("version");

        CommandLineUsage usage = new CommandLineUsage("DependencyClosure");
        commandLine.accept(usage);

        try {
            commandLine.parse(args);
        } catch (IllegalArgumentException ex) {
            showError(usage, ex.toString());
            System.exit(1);
        } catch (CommandLineException ex) {
            showError(usage, ex.toString());
            System.exit(1);
        }

        if (commandLine.getToggleSwitch("help")) {
            showError(usage);
        }
        
        if (commandLine.getToggleSwitch("version")) {
            showVersion();
        }

        if (commandLine.getToggleSwitch("help") || commandLine.getToggleSwitch("version")) {
            System.exit(1);
        }

        VerboseListener verboseListener = new VerboseListener();
        if (commandLine.isPresent("verbose")) {
            if ("System.out".equals(commandLine.getOptionalSwitch("verbose"))) {
                verboseListener.setWriter(System.out);
            } else {
                verboseListener.setWriter(new FileWriter(commandLine.getOptionalSwitch("verbose")));
            }
        }

        /*
         *  Beginning of main processing
         */

        Date start = new Date();

        NodeFactory factory = new NodeFactory();
        
        Iterator i = commandLine.getParameters().iterator();
        while (i.hasNext()) {
            String filename = (String) i.next();
            Logger.getLogger(DependencyClosure.class).info("Reading " + filename);
            verboseListener.print("Reading " + filename);

            if (filename.endsWith(".xml")) {
                NodeLoader loader = new NodeLoader(factory, commandLine.getToggleSwitch("validate"));
                loader.addDependencyListener(verboseListener);
                loader.load(filename);
            }

            Logger.getLogger(DependencyClosure.class).info("Read \"" + filename + "\".");
        }

        RegularExpressionSelectionCriteria startCriteria = new RegularExpressionSelectionCriteria();
        
        if (commandLine.isPresent("start-includes") || (!commandLine.isPresent("package-start-includes") && !commandLine.isPresent("class-start-includes") && !commandLine.isPresent("feature-start-includes"))) {
            // Only use the default if nothing else has been specified.
            startCriteria.setGlobalIncludes(commandLine.getMultipleSwitch("start-includes"));
        }
        startCriteria.setGlobalExcludes(commandLine.getMultipleSwitch("start-excludes"));
        startCriteria.setPackageIncludes(commandLine.getMultipleSwitch("package-start-includes"));
        startCriteria.setPackageExcludes(commandLine.getMultipleSwitch("package-start-excludes"));
        startCriteria.setClassIncludes(commandLine.getMultipleSwitch("class-start-includes"));
        startCriteria.setClassExcludes(commandLine.getMultipleSwitch("class-start-excludes"));
        startCriteria.setFeatureIncludes(commandLine.getMultipleSwitch("feature-start-includes"));
        startCriteria.setFeatureExcludes(commandLine.getMultipleSwitch("feature-start-excludes"));

        RegularExpressionSelectionCriteria stopCriteria = new RegularExpressionSelectionCriteria();

        stopCriteria.setGlobalIncludes(commandLine.getMultipleSwitch("stop-includes"));
        stopCriteria.setGlobalExcludes(commandLine.getMultipleSwitch("stop-excludes"));
        stopCriteria.setPackageIncludes(commandLine.getMultipleSwitch("package-stop-includes"));
        stopCriteria.setPackageExcludes(commandLine.getMultipleSwitch("package-stop-excludes"));
        stopCriteria.setClassIncludes(commandLine.getMultipleSwitch("class-stop-includes"));
        stopCriteria.setClassExcludes(commandLine.getMultipleSwitch("class-stop-excludes"));
        stopCriteria.setFeatureIncludes(commandLine.getMultipleSwitch("feature-stop-includes"));
        stopCriteria.setFeatureExcludes(commandLine.getMultipleSwitch("feature-stop-excludes"));

        TransitiveClosure selector = new TransitiveClosure(startCriteria, stopCriteria);

        try {
            if (commandLine.isPresent("maximum-inbound-depth")) {
                selector.setMaximumInboundDepth(Long.parseLong(commandLine.getSingleSwitch("maximum-inbound-depth")));
            }
        } catch (NumberFormatException ex) {
            selector.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        }

        try {
            if (commandLine.isPresent("maximum-outbound-depth")) {
                selector.setMaximumOutboundDepth(Long.parseLong(commandLine.getSingleSwitch("maximum-outbound-depth")));
            }
        } catch (NumberFormatException ex) {
            selector.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        }

        Logger.getLogger(DependencyClosure.class).info("Operating on " + factory.getPackages().values().size() + " package(s) ...");

        selector.traverseNodes(factory.getPackages().values());

        Logger.getLogger(DependencyClosure.class).info("Reporting " + selector.getFactory().getPackages().values().size() + " package(s) ...");
    
        verboseListener.print("Printing the graph ...");

        PrintWriter out;
        if (commandLine.isPresent("out")) {
            out = new PrintWriter(new FileWriter(commandLine.getSingleSwitch("out")));
        } else {
            out = new PrintWriter(System.out);
        }

        Printer printer;
        if (commandLine.isPresent("xml")) {
            printer = new XMLPrinter(out, commandLine.getSingleSwitch("encoding"), commandLine.getSingleSwitch("dtd-prefix"));
        } else {
            printer = new TextPrinter(out);
        }

        if (commandLine.isPresent("indent-text")) {
            printer.setIndentText(commandLine.getSingleSwitch("indent-text"));
        }

        printer.traverseNodes(selector.getFactory().getPackages().values());

        out.close();

        Date end = new Date();

        if (commandLine.getToggleSwitch("time")) {
            System.err.println(DependencyClosure.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
        }

        verboseListener.close();
    }
}
