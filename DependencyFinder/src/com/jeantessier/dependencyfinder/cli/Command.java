package com.jeantessier.dependencyfinder.cli;

import java.io.*;
import java.util.*;

import com.jeantessier.commandline.*;
import com.jeantessier.dependencyfinder.*;
import com.jeantessier.dependency.*;
import org.apache.log4j.*;

public abstract class Command {
    public static final String DEFAULT_LOGFILE = "System.out";
    public static final String DEFAULT_INCLUDES = "//";

    private CommandLine commandLine;
    private CommandLineUsage commandLineUsage;

    private Date startTime;
    private VerboseListener verboseListener;
    protected PrintWriter out;

    public Command(String name) {
        commandLine = new CommandLine();
        populateCommandLineSwitches();

        commandLineUsage = new CommandLineUsage(name);
        getCommandLine().accept(commandLineUsage);
    }

    protected CommandLine getCommandLine() {
        return commandLine;
    }

    protected VerboseListener getVerboseListener() {
        return verboseListener;
    }

    public void run(String[] args) throws Exception {
        parseCommandLine(args);
        validateCommandLine();

        process();
    }

    protected void populateCommandLineSwitches() {
        getCommandLine().addToggleSwitch("time");
        getCommandLine().addSingleValueSwitch("out");
        getCommandLine().addToggleSwitch("help");
        getCommandLine().addOptionalValueSwitch("verbose", DEFAULT_LOGFILE);
        getCommandLine().addToggleSwitch("version");
    }

    protected void populateCommandLineSwitchesForXMLOutput(String defaultEncoding, String defaultDTDPrefix) {
        getCommandLine().addSingleValueSwitch("encoding", defaultEncoding);
        getCommandLine().addSingleValueSwitch("dtd-prefix", defaultDTDPrefix);
        getCommandLine().addSingleValueSwitch("indent-text");
    }

    protected void parseCommandLine(String[] args) {
        try {
            getCommandLine().parse(args);
        } catch (IllegalArgumentException ex) {
            showError(ex.toString());
            System.exit(1);
        } catch (CommandLineException ex) {
            showError(ex.toString());
            System.exit(1);
        }
    }

    protected void validateCommandLine() throws IOException {
        if (getCommandLine().getToggleSwitch("help")) {
            showError();
        }

        if (getCommandLine().getToggleSwitch("version")) {
            showVersion();
        }

        if (getCommandLine().getToggleSwitch("help") || getCommandLine().getToggleSwitch("version")) {
            System.exit(1);
        }
    }

    private void process() throws IOException {
        startProcessing();
        doProcessing();
        stopProcessing();
    }

    private void startProcessing() throws IOException {
        startVerboseListener();
        startTimer();
        startOutput();
    }

    protected abstract void doProcessing() throws IOException;

    private void stopProcessing() {
        stopTimer();
        stopOutput();
        stopVerboseListener();
    }

    private void startVerboseListener() throws IOException {
        verboseListener = new VerboseListener();
        if (commandLine.isPresent("verbose")) {
            if (DEFAULT_LOGFILE.equals(commandLine.getOptionalSwitch("verbose"))) {
                verboseListener.setWriter(System.out);
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

    protected void showError() {
        showError(System.err);
    }

    protected void showError(PrintStream out) {
        out.println(commandLineUsage);
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

        if (hasRegularExpressionSwitches(getCommandLine(), name)) {
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
        } else if (hasListSwitches(getCommandLine(), name)) {
            result = createCollectionSelectionCriteria(getCommandLine().getMultipleSwitch(name + "-includes-list"), getCommandLine().getMultipleSwitch(name + "-excludes-list"));
        }
        
        return result;
    }

    protected boolean hasFilterRegularExpressionSwitches(CommandLine commandLine) {
        return hasRegularExpressionSwitches(commandLine,  "filter");
    }

    protected boolean hasRegularExpressionSwitches(CommandLine commandLine, String name) {
        Collection<String> switches = commandLine.getPresentSwitches();

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

    protected boolean hasFilterListSwitches(CommandLine commandLine) {
        return hasListSwitches(commandLine,  "filter");
    }

    protected boolean hasListSwitches(CommandLine commandLine, String name) {
        Collection<String> switches = commandLine.getPresentSwitches();

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
