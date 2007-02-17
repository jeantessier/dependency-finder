package com.jeantessier.dependencyfinder.cli;

import java.io.*;
import java.util.*;

import com.jeantessier.commandline.*;
import com.jeantessier.commandline.Printer;
import com.jeantessier.commandline.TextPrinter;
import com.jeantessier.dependencyfinder.*;
import com.jeantessier.dependency.*;
import org.apache.log4j.*;

public abstract class Command {
    public static final String DEFAULT_LOGFILE = "System.out";
    public static final String DEFAULT_INCLUDES = "//";

    private String name;
    private CommandLine commandLine;
    private CommandLineUsage commandLineUsage;

    private Date startTime;
    private VerboseListener verboseListener;
    protected PrintWriter out;

    public Command(String name) throws CommandLineException {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private void resetCommandLine() {
        commandLine = new CommandLine();
        populateCommandLineSwitches();
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

    public void run(String[] args) {
        try {
            parseCommandLine(args);
            if (validateCommandLine(System.err)) {
                process();
            } else {
                System.exit(1);
            }
        } catch (Exception e) {
            Logger.getLogger(getClass()).error(e);
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

    protected void populateCommandLineSwitchesForXMLOutput(String defaultEncoding, String defaultDTDPrefix) {
        getCommandLine().addSingleValueSwitch("encoding", defaultEncoding);
        getCommandLine().addSingleValueSwitch("dtd-prefix", defaultDTDPrefix);
        getCommandLine().addSingleValueSwitch("indent-text");
    }

    protected void parseCommandLine(String[] args) throws CommandLineException {
        resetCommandLine();
        getCommandLine().parse(args);
    }

    protected boolean validateCommandLine(PrintStream out) {
        boolean result = true;

        if (getCommandLine().getToggleSwitch("echo")) {
            echo(out);
            result = false;
        }

        if (getCommandLine().getToggleSwitch("help")) {
            showError(out);
            result = false;
        }

        if (getCommandLine().getToggleSwitch("version")) {
            showVersion(out);
            result = false;
        }

        return result;
    }

    protected boolean validateCommandLineForScoping(PrintStream out) {
        boolean result = true;

        if (hasScopeRegularExpressionSwitches() && hasScopeListSwitches()) {
            showError(out, "You can use switches for regular expressions or lists for scope, but not at the same time");
            result = false;
        }

        return result;
    }

    protected boolean validateCommandLineForFiltering(PrintStream out) {
        boolean result = true;

        if (hasFilterRegularExpressionSwitches() && hasFilterListSwitches()) {
            showError(out, "You can use switches for regular expressions or lists for filter, but not at the same time");
            result = false;
        }

        return result;
    }

    private void process() throws Exception {
        startProcessing();
        doProcessing();
        stopProcessing();
    }

    private void startProcessing() throws IOException {
        startVerboseListener();
        startTimer();
        startOutput();
    }

    protected abstract void doProcessing() throws Exception;

    private void stopProcessing() {
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

    private void stopOutput() {
        out.close();
    }

    protected void echo() {
        echo(System.err);
    }

    protected void echo(PrintStream out) {
        Printer printer = new TextPrinter(getClass().getName());
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

    private Collection<String> loadCollection(Collection<String> filenames) {
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
}
