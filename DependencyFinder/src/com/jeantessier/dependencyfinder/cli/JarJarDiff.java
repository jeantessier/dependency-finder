/*
 *  Copyright (c) 2001-2005, Jean Tessier
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
import java.lang.reflect.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;
import com.jeantessier.commandline.*;
import com.jeantessier.dependencyfinder.*;
import com.jeantessier.diff.*;

public class JarJarDiff {
    public static final String API_STRATEGY          = "api";
    public static final String INCOMPATIBLE_STRATEGY = "incompatible";

    public static final String DEFAULT_OLD_DOCUMENTATION = "old_documentation.txt";
    public static final String DEFAULT_NEW_DOCUMENTATION = "new_documentation.txt";
    public static final String DEFAULT_LOGFILE           = "System.out";
    public static final String DEFAULT_LEVEL             = API_STRATEGY;

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
        CommandLine commandLine = new CommandLine(new NullParameterStrategy());
        commandLine.addSingleValueSwitch("name");
        commandLine.addMultipleValuesSwitch("old", true);
        commandLine.addSingleValueSwitch("old-label");
        commandLine.addMultipleValuesSwitch("new", true);
        commandLine.addSingleValueSwitch("new-label");
        commandLine.addSingleValueSwitch("filter");
        commandLine.addToggleSwitch("code");
        commandLine.addSingleValueSwitch("level",      DEFAULT_LEVEL);
        commandLine.addSingleValueSwitch("encoding",   Report.DEFAULT_ENCODING);
        commandLine.addSingleValueSwitch("dtd-prefix", Report.DEFAULT_DTD_PREFIX);
        commandLine.addSingleValueSwitch("indent-text");
        commandLine.addToggleSwitch("time");
        commandLine.addSingleValueSwitch("out");
        commandLine.addToggleSwitch("help");
        commandLine.addOptionalValueSwitch("verbose",  DEFAULT_LOGFILE);
        commandLine.addToggleSwitch("version");

        CommandLineUsage usage = new CommandLineUsage("JarJarDiff");
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

        // Collecting data, first classfiles from JARs,
        // then package/class trees using NodeFactory.

        PackageMapper oldPackages = new PackageMapper();
        ClassfileLoader oldJar = new AggregatingClassfileLoader();
        oldJar.addLoadListener(oldPackages);
        oldJar.addLoadListener(verboseListener);
        oldJar.load(commandLine.getMultipleSwitch("old"));

        PackageMapper newPackages = new PackageMapper();
        ClassfileLoader newJar = new AggregatingClassfileLoader();
        newJar.addLoadListener(newPackages);
        newJar.addLoadListener(verboseListener);
        newJar.load(commandLine.getMultipleSwitch("new"));

        DifferenceStrategy strategy = null;
        if (commandLine.getToggleSwitch("code")) {
            strategy = new CodeDifferenceStrategy();
        } else {
            strategy = new NoDifferenceStrategy();
        }

        String level = commandLine.getSingleSwitch("level");
        if (API_STRATEGY.equals(level)) {
            strategy = new APIDifferenceStrategy(strategy);
        } else if (INCOMPATIBLE_STRATEGY.equals(level)) {
            strategy = new IncompatibleDifferenceStrategy(strategy);
        } else if (level != null) {
            try {
                Constructor constructor;
                try {
                    constructor = Class.forName(level).getConstructor(new Class[] {DifferenceStrategy.class});
                    strategy = (DifferenceStrategy) constructor.newInstance(new Object[] {strategy});
                } catch (NoSuchMethodException ex) {
                    strategy = (DifferenceStrategy) Class.forName(level).newInstance();
                }
            } catch (InvocationTargetException ex) {
                Logger.getLogger(JarJarDiff.class).error("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\"", ex);
                strategy = new APIDifferenceStrategy(strategy);
            } catch (InstantiationException ex) {
                Logger.getLogger(JarJarDiff.class).error("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\"", ex);
                strategy = new APIDifferenceStrategy(strategy);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(JarJarDiff.class).error("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\"", ex);
                strategy = new APIDifferenceStrategy(strategy);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(JarJarDiff.class).error("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\"", ex);
                strategy = new APIDifferenceStrategy(strategy);
            } catch (ClassCastException ex) {
                Logger.getLogger(JarJarDiff.class).error("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\"", ex);
                strategy = new APIDifferenceStrategy(strategy);
            }
        }

        if (commandLine.isPresent("filter")) {
            strategy = new ListBasedDifferenceStrategy(strategy, commandLine.getSingleSwitch("filter"));
        }

        // Starting to compare, first at package level,
        // then descending to class level for packages
        // that are in both the old and the new codebase.
    
        Logger.getLogger(JarJarDiff.class).info("Comparing ...");
        verboseListener.print("Comparing ...");

        String name     = commandLine.getSingleSwitch("name");
        String oldLabel = commandLine.isPresent("old-label") ? commandLine.getSingleSwitch("old-label") : commandLine.getSwitch("old").toString();
        String newLabel = commandLine.isPresent("new-label") ? commandLine.getSingleSwitch("new-label") : commandLine.getSwitch("new").toString();

        DifferencesFactory factory = new DifferencesFactory(strategy);
        Differences differences = factory.createProjectDifferences(name, oldLabel, oldPackages, newLabel, newPackages);

        Logger.getLogger(JarJarDiff.class).info("Printing results ...");
        verboseListener.print("Printing results ...");

        PrintWriter out;
        if (commandLine.isPresent("out")) {
            out = new PrintWriter(new FileWriter(commandLine.getSingleSwitch("out")));
        } else {
            out = new PrintWriter(new OutputStreamWriter(System.out));
        }

        com.jeantessier.diff.Printer printer = new Report(commandLine.getSingleSwitch("encoding"), commandLine.getSingleSwitch("dtd-prefix"));
        if (commandLine.isPresent("indent-text")) {
            printer.setIndentText(commandLine.getSingleSwitch("indent-text"));
        }

        differences.accept(printer);
        out.print(printer);

        Date end = new Date();

        if (commandLine.getToggleSwitch("time")) {
            System.err.println(JarJarDiff.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
        }

        out.close();

        verboseListener.close();
    }
}
