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
import java.lang.reflect.*;

import com.jeantessier.classreader.*;
import com.jeantessier.commandline.*;
import com.jeantessier.diff.*;
import org.apache.log4j.*;

public class JarJarDiff extends Command {
    public static final String API_STRATEGY = "api";
    public static final String INCOMPATIBLE_STRATEGY = "incompatible";
    public static final String DEFAULT_LEVEL = API_STRATEGY;

    public JarJarDiff() throws CommandLineException {
        super("JarJarDiff");
    }

    protected void showSpecificUsage(PrintStream out) {
        out.println();
        out.println("Defaults is text output to the console.");
        out.println();
    }

    protected void populateCommandLineSwitches() throws CommandLineException {
        super.populateCommandLineSwitches();
        populateCommandLineSwitchesForXMLOutput(Report.DEFAULT_ENCODING, Report.DEFAULT_DTD_PREFIX);

        getCommandLine().addSingleValueSwitch("name");
        getCommandLine().addMultipleValuesSwitch("old", true);
        getCommandLine().addSingleValueSwitch("old-label");
        getCommandLine().addMultipleValuesSwitch("new", true);
        getCommandLine().addSingleValueSwitch("new-label");
        getCommandLine().addSingleValueSwitch("filter");
        getCommandLine().addToggleSwitch("code");
        getCommandLine().addSingleValueSwitch("level", DEFAULT_LEVEL);
    }

    protected void doProcessing() throws Exception {
        // Collecting data, first classfiles from JARs,
        // then package/class trees using NodeFactory.

        PackageMapper oldPackages = new PackageMapper();
        ClassfileLoader oldJar = new AggregatingClassfileLoader();
        oldJar.addLoadListener(oldPackages);
        oldJar.addLoadListener(getVerboseListener());
        oldJar.load(getCommandLine().getMultipleSwitch("old"));

        PackageMapper newPackages = new PackageMapper();
        ClassfileLoader newJar = new AggregatingClassfileLoader();
        newJar.addLoadListener(newPackages);
        newJar.addLoadListener(getVerboseListener());
        newJar.load(getCommandLine().getMultipleSwitch("new"));

        DifferenceStrategy baseStrategy = getBaseStrategy(getCommandLine().getToggleSwitch("code"));
        DifferenceStrategy strategy = getStrategy(getCommandLine().getSingleSwitch("level"), baseStrategy);

        if (getCommandLine().isPresent("filter")) {
            strategy = new ListBasedDifferenceStrategy(strategy, getCommandLine().getSingleSwitch("filter"));
        }

        // Starting to compare, first at package level,
        // then descending to class level for packages
        // that are in both the old and the new codebase.

        Logger.getLogger(JarJarDiff.class).info("Comparing ...");
        getVerboseListener().print("Comparing ...");

        String name = getCommandLine().getSingleSwitch("name");
        String oldLabel = getCommandLine().isPresent("old-label") ? getCommandLine().getSingleSwitch("old-label") : getCommandLine().getSwitch("old").toString();
        String newLabel = getCommandLine().isPresent("new-label") ? getCommandLine().getSingleSwitch("new-label") : getCommandLine().getSwitch("new").toString();

        DifferencesFactory factory = new DifferencesFactory(strategy);
        Differences differences = factory.createProjectDifferences(name, oldLabel, oldPackages, newLabel, newPackages);

        Logger.getLogger(JarJarDiff.class).info("Printing results ...");
        getVerboseListener().print("Printing results ...");

        com.jeantessier.diff.Printer printer = new Report(getCommandLine().getSingleSwitch("encoding"), getCommandLine().getSingleSwitch("dtd-prefix"));
        if (getCommandLine().isPresent("indent-text")) {
            printer.setIndentText(getCommandLine().getSingleSwitch("indent-text"));
        }

        differences.accept(printer);
        out.print(printer);
    }

    private static DifferenceStrategy getStrategy(String level, DifferenceStrategy baseStrategy) {
        DifferenceStrategy result;

        if (API_STRATEGY.equals(level)) {
            result = new APIDifferenceStrategy(baseStrategy);
        } else if (INCOMPATIBLE_STRATEGY.equals(level)) {
            result = new IncompatibleDifferenceStrategy(baseStrategy);
        } else {
            try {
                Constructor constructor;
                try {
                    constructor = Class.forName(level).getConstructor(DifferenceStrategy.class);
                    result = (DifferenceStrategy) constructor.newInstance(baseStrategy);
                } catch (NoSuchMethodException ex) {
                    result = (DifferenceStrategy) Class.forName(level).newInstance();
                }
            } catch (InvocationTargetException ex) {
                Logger.getLogger(JarJarDiff.class).error("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\"", ex);
                result = getDefaultStrategy(baseStrategy);
            } catch (InstantiationException ex) {
                Logger.getLogger(JarJarDiff.class).error("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\"", ex);
                result = getDefaultStrategy(baseStrategy);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(JarJarDiff.class).error("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\"", ex);
                result = getDefaultStrategy(baseStrategy);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(JarJarDiff.class).error("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\"", ex);
                result = getDefaultStrategy(baseStrategy);
            } catch (ClassCastException ex) {
                Logger.getLogger(JarJarDiff.class).error("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\"", ex);
                result = getDefaultStrategy(baseStrategy);
            }
        }

        return result;
    }

    private static DifferenceStrategy getBaseStrategy(boolean useCode) {
        DifferenceStrategy result;

        if (useCode) {
            result = new CodeDifferenceStrategy();
        } else {
            result = new NoDifferenceStrategy();
        }

        return result;
    }

    private static DifferenceStrategy getDefaultStrategy(DifferenceStrategy strategy) {
        return new APIDifferenceStrategy(strategy);
    }

    public static void main(String[] args) throws Exception {
        new JarJarDiff().run(args);
    }
}
