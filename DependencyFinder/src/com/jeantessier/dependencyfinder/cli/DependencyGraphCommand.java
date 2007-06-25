package com.jeantessier.dependencyfinder.cli;

import java.io.*;
import javax.xml.parsers.*;

import org.xml.sax.*;

import com.jeantessier.commandline.*;
import com.jeantessier.dependency.*;

/**
 * TODO Class comments
 */
public abstract class DependencyGraphCommand extends Command {
    public DependencyGraphCommand(String name) throws CommandLineException {
        super(name);
    }

    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();

        getCommandLine().addToggleSwitch("validate");
    }

    protected void showSpecificUsage(PrintStream out) {
        out.println();
        out.println("Default is text output to the console.");
        out.println();
    }

    protected NodeFactory loadGraphs() throws IOException, SAXException, ParserConfigurationException {
        NodeFactory result = new NodeFactory();

        for (String filename : getCommandLine().getParameters()) {
            if (filename.endsWith(".xml")) {
                getVerboseListener().print("Reading " + filename);

                NodeLoader loader = new NodeLoader(result, getCommandLine().getToggleSwitch("validate"));
                loader.addDependencyListener(getVerboseListener());
                loader.load(filename);

                getVerboseListener().print("Read \"" + filename + "\".");
            } else {
                getVerboseListener().print("Skipping \"" + filename + "\".");
            }
        }

        return result;
    }
}
