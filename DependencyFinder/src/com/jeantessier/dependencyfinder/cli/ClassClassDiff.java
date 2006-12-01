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
import com.jeantessier.diff.*;
import org.apache.log4j.*;

public class ClassClassDiff extends Command {
    public static final String API_STRATEGY = "api";
    public static final String INCOMPATIBLE_STRATEGY = "incompatible";

    public static final String DEFAULT_LEVEL = API_STRATEGY;

    public ClassClassDiff() {
        super("ClassClassDiff");
    }

    protected void showSpecificUsage(PrintStream out) {
        out.println();
        out.println("Defaults is text output to the console.");
        out.println();
    }

    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();
        populateCommandLineSwitchesForXMLOutput(Report.DEFAULT_ENCODING, Report.DEFAULT_DTD_PREFIX);

        getCommandLine().addSingleValueSwitch("name");
        getCommandLine().addMultipleValuesSwitch("old", true);
        getCommandLine().addMultipleValuesSwitch("new", true);
        getCommandLine().addSingleValueSwitch("filter");
        getCommandLine().addToggleSwitch("code");
        getCommandLine().addSingleValueSwitch("level", DEFAULT_LEVEL);
    }

    public void doProcessing() throws IOException {
        // Collecting data, first classfiles from JARs,
        // then package/class trees using NodeFactory.

        ClassfileLoader oldJar = new AggregatingClassfileLoader();
        oldJar.addLoadListener(getVerboseListener());
        oldJar.load(getCommandLine().getMultipleSwitch("old"));

        ClassfileLoader newJar = new AggregatingClassfileLoader();
        newJar.addLoadListener(getVerboseListener());
        newJar.load(getCommandLine().getMultipleSwitch("new"));

        DifferenceStrategy strategy;
        if (getCommandLine().getToggleSwitch("code")) {
            strategy = new CodeDifferenceStrategy();
        } else {
            strategy = new NoDifferenceStrategy();
        }

        String level = getCommandLine().getSingleSwitch("level");
        if (API_STRATEGY.equals(level)) {
            strategy = new APIDifferenceStrategy(strategy);
        } else if (INCOMPATIBLE_STRATEGY.equals(level)) {
            strategy = new IncompatibleDifferenceStrategy(strategy);
        } else if (level != null) {
            try {
                Constructor constructor;
                try {
                    constructor = Class.forName(level).getConstructor(DifferenceStrategy.class);
                    strategy = (DifferenceStrategy) constructor.newInstance(strategy);
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

        if (getCommandLine().isPresent("filter")) {
            strategy = new ListBasedDifferenceStrategy(strategy, getCommandLine().getSingleSwitch("filter"));
        }

        // Starting to compare, first at package level,
        // then descending to class level for packages
        // that are in both the old and the new codebase.

        Logger.getLogger(JarJarDiff.class).info("Comparing ...");
        getVerboseListener().print("Comparing ...");

        String name = getCommandLine().getSingleSwitch("name");
        Classfile oldClass = oldJar.getAllClassfiles().iterator().next();
        Classfile newClass = newJar.getAllClassfiles().iterator().next();

        DifferencesFactory factory = new DifferencesFactory(strategy);
        Differences differences = factory.createClassDifferences(name, oldClass, newClass);

        Logger.getLogger(JarJarDiff.class).info("Printing results ...");
        getVerboseListener().print("Printing results ...");

        com.jeantessier.diff.Printer printer = new Report(getCommandLine().getSingleSwitch("encoding"), getCommandLine().getSingleSwitch("dtd-prefix"));
        if (getCommandLine().isPresent("indent-text")) {
            printer.setIndentText(getCommandLine().getSingleSwitch("indent-text"));
        }

        differences.accept(printer);
        out.print(printer);
    }

    public static void main(String[] args) throws Exception {
        new ClassClassDiff().run(args);
    }
}
