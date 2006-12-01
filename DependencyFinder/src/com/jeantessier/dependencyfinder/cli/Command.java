package com.jeantessier.dependencyfinder.cli;

import java.io.*;
import java.util.*;

import com.jeantessier.commandline.*;
import com.jeantessier.dependencyfinder.*;
import com.jeantessier.dependency.*;
import org.apache.log4j.*;

public abstract class Command {
    public static final String DEFAULT_LOGFILE = "System.out";
    public static final String DEFAULT_FILTER_INCLUDES = "//";

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
        getCommandLine().addMultipleValuesSwitch("filter-includes", DEFAULT_FILTER_INCLUDES);
        getCommandLine().addMultipleValuesSwitch("filter-excludes");
        getCommandLine().addToggleSwitch("package-filter");
        getCommandLine().addMultipleValuesSwitch("package-filter-includes");
        getCommandLine().addMultipleValuesSwitch("package-filter-excludes");
        getCommandLine().addToggleSwitch("class-filter");
        getCommandLine().addMultipleValuesSwitch("class-filter-includes");
        getCommandLine().addMultipleValuesSwitch("class-filter-excludes");
        getCommandLine().addToggleSwitch("feature-filter");
        getCommandLine().addMultipleValuesSwitch("feature-filter-includes");
        getCommandLine().addMultipleValuesSwitch("feature-filter-excludes");

        getCommandLine().addMultipleValuesSwitch("filter-includes-list");
        getCommandLine().addMultipleValuesSwitch("filter-excludes-list");
    }

    protected SelectionCriteria getFilterCriteria() {
        SelectionCriteria result = new ComprehensiveSelectionCriteria();

        if (hasFilterRegularExpressionSwitches(getCommandLine())) {
            RegularExpressionSelectionCriteria regularExpressionFilterCriteria = new RegularExpressionSelectionCriteria();

            if (getCommandLine().isPresent("package-filter") || getCommandLine().isPresent("class-filter") || getCommandLine().isPresent("feature-filter")) {
                regularExpressionFilterCriteria.setMatchingPackages(getCommandLine().getToggleSwitch("package-filter"));
                regularExpressionFilterCriteria.setMatchingClasses(getCommandLine().getToggleSwitch("class-filter"));
                regularExpressionFilterCriteria.setMatchingFeatures(getCommandLine().getToggleSwitch("feature-filter"));
            }

            if (getCommandLine().isPresent("filter-includes") || (!getCommandLine().isPresent("package-filter-includes") && !getCommandLine().isPresent("class-filter-includes") && !getCommandLine().isPresent("feature-filter-includes"))) {
                // Only use the default if nothing else has been specified.
                regularExpressionFilterCriteria.setGlobalIncludes(getCommandLine().getMultipleSwitch("filter-includes"));
            }
            regularExpressionFilterCriteria.setGlobalExcludes(getCommandLine().getMultipleSwitch("filter-excludes"));
            regularExpressionFilterCriteria.setPackageIncludes(getCommandLine().getMultipleSwitch("package-filter-includes"));
            regularExpressionFilterCriteria.setPackageExcludes(getCommandLine().getMultipleSwitch("package-filter-excludes"));
            regularExpressionFilterCriteria.setClassIncludes(getCommandLine().getMultipleSwitch("class-filter-includes"));
            regularExpressionFilterCriteria.setClassExcludes(getCommandLine().getMultipleSwitch("class-filter-excludes"));
            regularExpressionFilterCriteria.setFeatureIncludes(getCommandLine().getMultipleSwitch("feature-filter-includes"));
            regularExpressionFilterCriteria.setFeatureExcludes(getCommandLine().getMultipleSwitch("feature-filter-excludes"));

            result = regularExpressionFilterCriteria;
        } else if (hasFilterListSwitches(getCommandLine())) {
            result = createCollectionSelectionCriteria(getCommandLine().getMultipleSwitch("filter-includes-list"), getCommandLine().getMultipleSwitch("filter-excludes-list"));
        }
        return result;
    }

    protected boolean hasFilterRegularExpressionSwitches(CommandLine commandLine) {
        Collection<String> switches = commandLine.getPresentSwitches();

        return
            switches.contains("filter-includes") ||
            switches.contains("filter-excludes") ||
            switches.contains("package-filter") ||
            switches.contains("package-filter-includes") ||
            switches.contains("package-filter-excludes") ||
            switches.contains("class-filter") ||
            switches.contains("class-filter-includes") ||
            switches.contains("class-filter-excludes") ||
            switches.contains("feature-filter") ||
            switches.contains("feature-filter-includes") ||
            switches.contains("feature-filter-excludes");
    }

    protected boolean hasFilterListSwitches(CommandLine commandLine) {
        Collection<String> switches = commandLine.getPresentSwitches();

        return
            switches.contains("filter-includes-list") ||
            switches.contains("filter-excludes-list");
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
