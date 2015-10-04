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

import com.jeantessier.commandline.CollectingParameterStrategy;
import com.jeantessier.commandline.CommandLine;
import com.jeantessier.commandline.CommandLineException;
import com.jeantessier.commandline.CommandLineUsage;
import com.jeantessier.commandline.ParameterStrategy;
import com.jeantessier.commandline.Printer;
import com.jeantessier.commandline.TextPrinter;
import com.jeantessier.dependency.CollectionSelectionCriteria;
import com.jeantessier.dependency.ComprehensiveSelectionCriteria;
import com.jeantessier.dependency.NullSelectionCriteria;
import com.jeantessier.dependency.RegularExpressionSelectionCriteria;
import com.jeantessier.dependency.SelectionCriteria;
import com.jeantessier.dependencyfinder.Version;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public abstract class Command {
    public static final String DEFAULT_LOGFILE = "System.out";
    public static final String DEFAULT_INCLUDES = "//";

    private CommandLine commandLine;
    private CommandLineUsage commandLineUsage;

    private Date startTime;
    private VerboseListener verboseListener;
    private PrintWriter out;

    public String getName() {
        return getClass().getSimpleName();
    }

    private void resetCommandLine() {
        commandLine = new CommandLine(getParameterStrategy());
        populateCommandLineSwitches();
    }

    protected ParameterStrategy getParameterStrategy() {
        return new CollectingParameterStrategy();
    }

    protected CommandLine getCommandLine() {
        if (commandLine == null) {
            resetCommandLine();
        }

        return commandLine;
    }

    public CommandLineUsage getCommandLineUsage() {
        if (commandLineUsage == null) {
            commandLineUsage = new CommandLineUsage(getName());
            getCommandLine().accept(commandLineUsage);
        }

        return commandLineUsage;
    }

    protected VerboseListener getVerboseListener() {
        return verboseListener;
    }

    public void run(String[] args) throws Exception {
        if (validateCommandLine(args, System.err)) {
            process();
        } else {
            System.exit(1);
        }
    }

    protected void populateCommandLineSwitches() {
        getCommandLine().addToggleSwitch("echo");
        getCommandLine().addToggleSwitch("help");
        getCommandLine().addSingleValueSwitch("out");
        getCommandLine().addToggleSwitch("time");
        getCommandLine().addOptionalValueSwitch("verbose", DEFAULT_LOGFILE);
        getCommandLine().addToggleSwitch("version");
    }

    protected void populateCommandLineSwitchesForXMLOutput(String defaultEncoding, String defaultDTDPrefix, String defaultIndentText) {
        getCommandLine().addSingleValueSwitch("encoding", defaultEncoding);
        getCommandLine().addSingleValueSwitch("dtd-prefix", defaultDTDPrefix);
        getCommandLine().addSingleValueSwitch("indent-text", defaultIndentText);
    }

    protected Collection<CommandLineException> parseCommandLine(String[] args) {
        resetCommandLine();
        return getCommandLine().parse(args);
    }

    protected boolean validateCommandLine(String[] args, PrintStream out) {
        boolean result = true;

        Collection<CommandLineException> exceptions = parseCommandLine(args);

        if (getCommandLine().getToggleSwitch("version")) {
            showVersion(out);
            result = false;
        }

        if (getCommandLine().getToggleSwitch("help")) {
            showError(out);
            result = false;
        }

        if (getCommandLine().getToggleSwitch("echo")) {
            echo(out);
            result = false;
        }

        if (result) {
            for (CommandLineException exception : exceptions) {
                result = false;
                Logger.getLogger(getClass()).error(exception);
            }
        }

        return result;
    }

    protected Collection<CommandLineException> validateCommandLineForScoping() {
        Collection<CommandLineException> exceptions = new ArrayList<CommandLineException>();

        if (hasScopeRegularExpressionSwitches() && hasScopeListSwitches()) {
            exceptions.add(new CommandLineException("You can use switches for regular expressions or lists for scope, but not at the same time"));
        }

        return exceptions;
    }

    protected Collection<CommandLineException> validateCommandLineForFiltering() {
        Collection<CommandLineException> exceptions = new ArrayList<CommandLineException>();

        if (hasFilterRegularExpressionSwitches() && hasFilterListSwitches()) {
            exceptions.add(new CommandLineException("You can use switches for regular expressions or lists for filter, but not at the same time"));
        }

        return exceptions;
    }

    private void process() throws Exception {
        startProcessing();
        doProcessing();
        stopProcessing();
    }

    private void startProcessing() throws IOException {
        startVerboseListener();
        // Output is started lazily the first time it is requested.
        startTimer();
    }

    protected abstract void doProcessing() throws Exception;

    private void stopProcessing() throws IOException {
        stopTimer();
        stopOutput();
        stopVerboseListener();
    }

    private void startVerboseListener() throws IOException {
        verboseListener = new VerboseListener();
        if (commandLine.isPresent("verbose")) {
            if (DEFAULT_LOGFILE.equals(commandLine.getOptionalSwitch("verbose"))) {
                verboseListener.setWriter(new OutputStreamWriter(System.out));
            } else {
                verboseListener.setWriter(new FileWriter(commandLine.getOptionalSwitch("verbose")));
            }
        }
    }

    private void stopVerboseListener() {
        verboseListener.close();
    }

    private void startTimer() {
        startTime = new Date();
    }

    private void stopTimer() {
        if (commandLine.getToggleSwitch("time")) {
            Date end = new Date();
            System.err.println(getClass().getName() + ": " + ((end.getTime() - (double) startTime.getTime()) / 1000) + " secs.");
        }
    }

    private void startOutput() throws IOException {
        if (getCommandLine().isPresent("out")) {
            out = new PrintWriter(new FileWriter(getCommandLine().getSingleSwitch("out")));
        } else {
            out = new PrintWriter(new OutputStreamWriter(System.out));
        }
    }

    private void stopOutput() throws IOException {
        if (out != null) {
            out.close();
        }
    }

    protected void echo() {
        echo(System.err);
    }

    protected void echo(PrintStream out) {
        Printer printer = new TextPrinter(getClass().getSimpleName());
        getCommandLine().accept(printer);
        out.println(printer);
    }

    protected void showError() {
        showError(System.err);
    }

    protected void showError(PrintStream out) {
        out.println(getCommandLineUsage());
        showSpecificUsage(out);
    }

    protected void showError(String msg) {
        showError(System.err, msg);
    }

    protected void showError(PrintStream out, String msg) {
        out.println(msg);
        showError(out);
    }

    protected abstract void showSpecificUsage(PrintStream out);

    protected void showVersion() {
        showVersion(System.err);
    }

    protected void showVersion(PrintStream out) {
        Version version = new Version();

        out.print(version.getImplementationTitle());
        out.print(" ");
        out.print(version.getImplementationVersion());
        out.print(" (c) ");
        out.print(version.getCopyrightDate());
        out.print(" ");
        out.print(version.getCopyrightHolder());
        out.println();

        out.print(version.getImplementationURL());
        out.println();

        out.print("Compiled on ");
        out.print(version.getImplementationDate());
        out.println();
    }

    protected void populateCommandLineSwitchesForScoping() {
        populateRegularExpressionCommandLineSwitches("scope", true, DEFAULT_INCLUDES);
        populateListCommandLineSwitches("scope");
    }

    protected void populateCommandLineSwitchesForFiltering() {
        populateRegularExpressionCommandLineSwitches("filter", true, DEFAULT_INCLUDES);
        populateListCommandLineSwitches("filter");
    }

    protected void populateCommandLineSwitchesForStartCondition() {
        populateRegularExpressionCommandLineSwitches("start", false, DEFAULT_INCLUDES);
        populateListCommandLineSwitches("start");
    }

    protected void populateCommandLineSwitchesForStopCondition() {
        populateRegularExpressionCommandLineSwitches("stop", false, null);
        populateListCommandLineSwitches("stop");
    }

    protected void populateRegularExpressionCommandLineSwitches(String name, boolean addToggles, String defaultIncludes) {
        if (defaultIncludes != null) {
            getCommandLine().addMultipleValuesSwitch(name + "-includes", defaultIncludes);
        } else {
            getCommandLine().addMultipleValuesSwitch(name + "-includes");
        }
        getCommandLine().addMultipleValuesSwitch(name + "-excludes");
        getCommandLine().addMultipleValuesSwitch("package-" + name + "-includes");
        getCommandLine().addMultipleValuesSwitch("package-" + name + "-excludes");
        getCommandLine().addMultipleValuesSwitch("class-" + name + "-includes");
        getCommandLine().addMultipleValuesSwitch("class-" + name + "-excludes");
        getCommandLine().addMultipleValuesSwitch("feature-" + name + "-includes");
        getCommandLine().addMultipleValuesSwitch("feature-" + name + "-excludes");

        if (addToggles) {
            getCommandLine().addToggleSwitch("package-" + name);
            getCommandLine().addToggleSwitch("class-" + name);
            getCommandLine().addToggleSwitch("feature-" + name);
        }
    }

    protected void populateListCommandLineSwitches(String name) {
        getCommandLine().addMultipleValuesSwitch(name + "-includes-list");
        getCommandLine().addMultipleValuesSwitch(name + "-excludes-list");
    }

    protected SelectionCriteria getScopeCriteria() {
        return getSelectionCriteria("scope", new ComprehensiveSelectionCriteria());
    }

    protected SelectionCriteria getFilterCriteria() {
        return getSelectionCriteria("filter", new ComprehensiveSelectionCriteria());
    }

    protected SelectionCriteria getStartCriteria() {
        return getSelectionCriteria("start", new ComprehensiveSelectionCriteria());
    }

    protected SelectionCriteria getStopCriteria() {
        return getSelectionCriteria("stop", new NullSelectionCriteria());
    }

    protected SelectionCriteria getSelectionCriteria(String name, SelectionCriteria defaultSelectionCriteria) {
        SelectionCriteria result = defaultSelectionCriteria;

        if (hasRegularExpressionSwitches(name)) {
            RegularExpressionSelectionCriteria regularExpressionFilterCriteria = new RegularExpressionSelectionCriteria();

            if (getCommandLine().isPresent("package-" + name) || getCommandLine().isPresent("class-" + name) || getCommandLine().isPresent("feature-" + name)) {
                regularExpressionFilterCriteria.setMatchingPackages(getCommandLine().getToggleSwitch("package-" + name));
                regularExpressionFilterCriteria.setMatchingClasses(getCommandLine().getToggleSwitch("class-" + name));
                regularExpressionFilterCriteria.setMatchingFeatures(getCommandLine().getToggleSwitch("feature-" + name));
            }

            if (getCommandLine().isPresent(name + "-includes") || (!getCommandLine().isPresent("package-" + name + "-includes") && !getCommandLine().isPresent("class-" + name + "-includes") && !getCommandLine().isPresent("feature-" + name + "-includes"))) {
                // Only use the default if nothing else has been specified.
                regularExpressionFilterCriteria.setGlobalIncludes(getCommandLine().getMultipleSwitch(name + "-includes"));
            }
            regularExpressionFilterCriteria.setGlobalExcludes(getCommandLine().getMultipleSwitch(name + "-excludes"));
            regularExpressionFilterCriteria.setPackageIncludes(getCommandLine().getMultipleSwitch("package-" + name + "-includes"));
            regularExpressionFilterCriteria.setPackageExcludes(getCommandLine().getMultipleSwitch("package-" + name + "-excludes"));
            regularExpressionFilterCriteria.setClassIncludes(getCommandLine().getMultipleSwitch("class-" + name + "-includes"));
            regularExpressionFilterCriteria.setClassExcludes(getCommandLine().getMultipleSwitch("class-" + name + "-excludes"));
            regularExpressionFilterCriteria.setFeatureIncludes(getCommandLine().getMultipleSwitch("feature-" + name + "-includes"));
            regularExpressionFilterCriteria.setFeatureExcludes(getCommandLine().getMultipleSwitch("feature-" + name + "-excludes"));

            result = regularExpressionFilterCriteria;
        } else if (hasListSwitches(name)) {
            result = createCollectionSelectionCriteria(getCommandLine().getMultipleSwitch(name + "-includes-list"), getCommandLine().getMultipleSwitch(name + "-excludes-list"));
        }
        
        return result;
    }

    protected boolean hasScopeRegularExpressionSwitches() {
        return hasRegularExpressionSwitches("scope");
    }

    protected boolean hasFilterRegularExpressionSwitches() {
        return hasRegularExpressionSwitches("filter");
    }

    protected boolean hasRegularExpressionSwitches(String name) {
        Collection<String> switches = getCommandLine().getPresentSwitches();

        return
            switches.contains(name + "-includes") ||
            switches.contains(name + "-excludes") ||
            switches.contains("package-" + name) ||
            switches.contains("package-" + name + "-includes") ||
            switches.contains("package-" + name + "-excludes") ||
            switches.contains("class-" + name) ||
            switches.contains("class-" + name + "-includes") ||
            switches.contains("class-" + name + "-excludes") ||
            switches.contains("feature-" + name) ||
            switches.contains("feature-" + name + "-includes") ||
            switches.contains("feature-" + name + "-excludes");
    }

    protected boolean hasScopeListSwitches() {
        return hasListSwitches("scope");
    }

    protected boolean hasFilterListSwitches() {
        return hasListSwitches("filter");
    }

    protected boolean hasListSwitches(String name) {
        Collection<String> switches = getCommandLine().getPresentSwitches();

        return
            switches.contains(name + "-includes-list") ||
            switches.contains(name + "-excludes-list");
    }

    protected CollectionSelectionCriteria createCollectionSelectionCriteria(Collection<String> includes, Collection<String> excludes) {
        return new CollectionSelectionCriteria(loadCollection(includes), loadCollection(excludes));
    }

    protected Collection<String> loadCollection(Collection<String> filenames) {
        Collection<String> result = null;

        if (!filenames.isEmpty()) {
            result = new HashSet<String>();

            for (String filename : filenames) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(filename));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.add(line);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(getClass()).error("Couldn't read file " + filename, ex);
                } finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(getClass()).error("Couldn't close file " + filename, ex);
                    }
                }
            }
        }

        return result;
    }

    protected PrintWriter getOut() throws IOException {
        if (out == null) {
            startOutput();
        }

        return out;
    }

    protected void setOut(PrintWriter out) {
        this.out = out;
    }
}
