package com.jeantessier.dependencyfinder.cli;

import java.lang.reflect.*;
import java.io.*;

import com.jeantessier.commandline.*;
import com.jeantessier.diff.*;
import org.apache.log4j.*;

public abstract class DiffCommand extends Command {
    public static final String API_STRATEGY = "api";
    public static final String INCOMPATIBLE_STRATEGY = "incompatible";

    public static final String DEFAULT_LEVEL = API_STRATEGY;

    public DiffCommand(String name) throws CommandLineException {
        super(name);
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

    protected DifferenceStrategy getStrategy(String level, DifferenceStrategy baseStrategy) {
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
                Logger.getLogger(getClass()).error("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\"", ex);
                result = getDefaultStrategy(baseStrategy);
            } catch (InstantiationException ex) {
                Logger.getLogger(getClass()).error("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\"", ex);
                result = getDefaultStrategy(baseStrategy);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(getClass()).error("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\"", ex);
                result = getDefaultStrategy(baseStrategy);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(getClass()).error("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\"", ex);
                result = getDefaultStrategy(baseStrategy);
            } catch (ClassCastException ex) {
                Logger.getLogger(getClass()).error("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\"", ex);
                result = getDefaultStrategy(baseStrategy);
            }
        }

        return result;
    }

    protected DifferenceStrategy getBaseStrategy(boolean useCode) {
        DifferenceStrategy result;

        if (useCode) {
            result = new CodeDifferenceStrategy();
        } else {
            result = new NoDifferenceStrategy();
        }

        return result;
    }

    private DifferenceStrategy getDefaultStrategy(DifferenceStrategy strategy) {
        return new APIDifferenceStrategy(strategy);
    }
}
