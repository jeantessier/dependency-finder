/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import org.apache.logging.log4j.*;

import com.jeantessier.commandline.*;
import com.jeantessier.diff.*;

public abstract class DiffCommand extends Command {
    public static final String API_STRATEGY = "api";
    public static final String INCOMPATIBLE_STRATEGY = "incompatible";

    public static final String DEFAULT_LEVEL = API_STRATEGY;

    protected void showSpecificUsage(PrintStream out) {
        out.println();
        out.println("Defaults is text output to the console.");
        out.println();
    }

    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();
        populateCommandLineSwitchesForXMLOutput(Report.DEFAULT_ENCODING, Report.DEFAULT_DTD_PREFIX, Report.DEFAULT_INDENT_TEXT);

        getCommandLine().addSingleValueSwitch("name");
        getCommandLine().addMultipleValuesSwitch("old", true);
        getCommandLine().addSingleValueSwitch("old-label");
        getCommandLine().addMultipleValuesSwitch("new", true);
        getCommandLine().addSingleValueSwitch("new-label");
        getCommandLine().addSingleValueSwitch("filter");
        getCommandLine().addToggleSwitch("code");
        getCommandLine().addSingleValueSwitch("level", DEFAULT_LEVEL);
    }

    protected Collection<CommandLineException> parseCommandLine(String[] args) {
        Collection<CommandLineException> exceptions = super.parseCommandLine(args);

        if (!getCommandLine().isPresent("old-label")) {
            getCommandLine().getSwitch("old-label").setValue(getCommandLine().getMultipleSwitch("old").toString());
        }

        if (!getCommandLine().isPresent("new-label")) {
            getCommandLine().getSwitch("new-label").setValue(getCommandLine().getMultipleSwitch("new").toString());
        }

        return exceptions;
    }

    protected DifferencesFactory getDifferencesFactory() throws IOException {
        var baseStrategy = getBaseStrategy(getCommandLine().getToggleSwitch("code"));
        var strategy = getStrategy(getCommandLine().getSingleSwitch("level"), baseStrategy);

        if (getCommandLine().isPresent("filter")) {
            strategy = new ListBasedDifferenceStrategy(strategy, getCommandLine().getSingleSwitch("filter"));
        }

        return new DifferencesFactory(strategy);
    }

    private DifferenceStrategy getBaseStrategy(boolean useCode) {
        return useCode ? new CodeDifferenceStrategy() : new NoDifferenceStrategy();
    }

    private DifferenceStrategy getStrategy(String level, DifferenceStrategy baseStrategy) {
        return switch (level) {
            case API_STRATEGY -> new APIDifferenceStrategy(baseStrategy);
            case INCOMPATIBLE_STRATEGY -> new IncompatibleDifferenceStrategy(baseStrategy);
            default -> {
                try {
                    try {
                        yield (DifferenceStrategy) Class.forName(level).getConstructor(DifferenceStrategy.class).newInstance(baseStrategy);
                    } catch (NoSuchMethodException ex) {
                        yield (DifferenceStrategy) Class.forName(level).getDeclaredConstructor().newInstance();
                    }
                } catch (ReflectiveOperationException | ClassCastException ex) {
                    LogManager.getLogger(getClass()).error("Unknown level \"{}\", using default level \"{}\"", level, DEFAULT_LEVEL, ex);
                    yield getStrategy(DEFAULT_LEVEL, baseStrategy);
                }
            }
        };
    }
}
