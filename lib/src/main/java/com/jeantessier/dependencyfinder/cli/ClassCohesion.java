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

import com.jeantessier.commandline.CommandLineException;
import com.jeantessier.dependency.ClassNode;
import com.jeantessier.dependency.FeatureNode;
import com.jeantessier.dependency.LCOM4Gatherer;
import com.jeantessier.dependency.Node;
import org.apache.logging.log4j.*;

import java.io.IOException;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;

public class ClassCohesion extends DependencyGraphCommand {
    private static final String EOL = System.getProperty("line.separator", "\n");

    private static final String DEFAULT_ENCODING = "utf-8";
    private static final String DEFAULT_DTD_PREFIX = "https://jeantessier.github.io/dependency-finder/dtd";
    private static final String DEFAULT_INDENT_TEXT = "    ";

    protected void populateCommandLineSwitches()  {
        super.populateCommandLineSwitches();
        populateCommandLineSwitchesForXMLOutput(DEFAULT_ENCODING, DEFAULT_DTD_PREFIX, DEFAULT_INDENT_TEXT);

        getCommandLine().addToggleSwitch("csv");
        getCommandLine().addToggleSwitch("json");
        getCommandLine().addToggleSwitch("text");
        getCommandLine().addToggleSwitch("txt");
        getCommandLine().addToggleSwitch("xml");
        getCommandLine().addToggleSwitch("yaml");
        getCommandLine().addToggleSwitch("yml");
        getCommandLine().addToggleSwitch("list");
    }

    protected Collection<CommandLineException> parseCommandLine(String[] args) {
        var exceptions = super.parseCommandLine(args);
        int modeSwitch = 0;

        if (getCommandLine().getToggleSwitch("csv")) {
            modeSwitch++;
        }
        if (getCommandLine().getToggleSwitch("json")) {
            modeSwitch++;
        }
        if (getCommandLine().getToggleSwitch("text")) {
            modeSwitch++;
        }
        if (getCommandLine().getToggleSwitch("txt")) {
            modeSwitch++;
        }
        if (getCommandLine().getToggleSwitch("xml")) {
            modeSwitch++;
        }
        if (getCommandLine().getToggleSwitch("yaml")) {
            modeSwitch++;
        }
        if (getCommandLine().getToggleSwitch("yml")) {
            modeSwitch++;
        }
        if (modeSwitch != 1) {
            exceptions.add(new CommandLineException("Must have one and only one of -csv, -json, -text, -txt, -xml, -yaml, or -yml"));
        }

        return exceptions;
    }

    public void doProcessing() throws Exception {
        var gatherer = new LCOM4Gatherer();

        LogManager.getLogger(OOMetrics.class).debug("Reading classes and computing metrics as we go ...");
        getVerboseListener().print("Reading classes and computing metrics as we go ...");
        gatherer.traverseNodes(loadGraph().getPackages().values());

        LogManager.getLogger(OOMetrics.class).debug("Printing results ...");
        getVerboseListener().print("Printing results ...");

        if (getCommandLine().isPresent("csv")) {
            printCSVFiles(gatherer.getResults());
        } else if (getCommandLine().isPresent("json")) {
             printJSONFile(gatherer.getResults());
        } else if (getCommandLine().isPresent("text") || getCommandLine().isPresent("txt")) {
            printTextFile(gatherer.getResults());
        } else if (getCommandLine().isPresent("xml")) {
            printXMLFile(gatherer.getResults());
        } else if (getCommandLine().isPresent("yaml") || getCommandLine().isPresent("yml")) {
             printYAMLFile(gatherer.getResults());
        }

        LogManager.getLogger(OOMetrics.class).debug("Done.");
    }

    private void printCSVFiles(Map<ClassNode, Collection<Collection<FeatureNode>>> results) throws IOException {
        getOut().println("class, LCOM4");
        getOut().println(results.entrySet().stream()
                .map(entry -> entry.getKey().getName() + ", " + entry.getValue().size())
                .collect(joining(EOL)));
    }

    private void printJSONFile(Map<ClassNode, Collection<Collection<FeatureNode>>> results) throws IOException {
        getOut().print("[");
        getOut().print(results.entrySet().stream()
                .map(entry -> entryToJSON(entry.getKey(), entry.getValue()))
                .collect(joining(", ")));
        getOut().println("]");
    }

    private String entryToJSON(ClassNode classNode, Collection<Collection<FeatureNode>> components) {
        var builder = new StringBuilder();

        builder.append("{\"class\": \"").append(classNode.getName()).append("\"");
        builder.append(", \"LCOM4\": ").append(components.size());

        if (components.size() > 1 && getCommandLine().isPresent("list")) {
            builder.append(", \"components\": [");
            builder.append(componentsToJSON(components));
            builder.append("]");
        }
        builder.append("}");

        return builder.toString();
    }

    private String componentsToJSON(Collection<Collection<FeatureNode>> components) {
        return components.stream()
                .map(this::componentToJSON)
                .map(component -> "[" + component + "]")
                .collect(joining(", "));
    }

    private String componentToJSON(Collection<FeatureNode> nodes) {
        return nodes.stream()
                .map(Node::getName)
                .map(name -> "\"" + name + "\"")
                .collect(joining(", "));
    }

    private void printTextFile(Map<ClassNode, Collection<Collection<FeatureNode>>> results) throws IOException {
        getOut().println(results.entrySet().stream()
                .flatMap(entry -> entryToText(entry.getKey(), entry.getValue()))
                .collect(joining(EOL)));
    }

    private Stream<String> entryToText(ClassNode classNode, Collection<Collection<FeatureNode>> components) {
        return Stream.concat(
                Stream.of(classNode.getName() + ": " + components.size()),
                componentsToText(components)
        );
    }

    private Stream<String> componentsToText(Collection<Collection<FeatureNode>> components) {
        if (components.size() > 1 && getCommandLine().isPresent("list")) {
            return Stream.concat(
                    Stream.of(getTextSeparator()),
                    components.stream()
                            .flatMap(component -> Stream.concat(
                                    componentToText(component),
                                    Stream.of(getTextSeparator())
                            ))
            );
        } else {
            return Stream.empty();
        }
    }

    private Stream<String> componentToText(Collection<FeatureNode> nodes) {
        return nodes.stream()
                .map(feature -> feature.getName().substring(feature.getClassNode().getName().length() + 1))
                .map(name -> getIndentText() + name);
    }

    private String getTextSeparator() {
        return getIndentText() + "--------";
    }

    private void printXMLFile(Map<ClassNode, Collection<Collection<FeatureNode>>> results) throws IOException {
        getOut().println("<?xml version=\"1.0\" encoding=\"" + getEncoding() + "\" ?>");
        getOut().println();
        getOut().println("<!DOCTYPE classes SYSTEM \"" + getDTDPrefix() + "/cohesion.dtd\">");
        getOut().println();
        getOut().println("<classes>");
        getOut().println(results.entrySet().stream()
                .flatMap(entry -> entryToXML(entry.getKey(), entry.getValue()))
                .collect(joining(EOL)));
        getOut().println("</classes>");
    }

    private Stream<String> entryToXML(ClassNode classNode, Collection<Collection<FeatureNode>> components) {
            if (components.size() > 1 && getCommandLine().isPresent("list")) {
                return Stream.of(
                            Stream.of(getIndentText() + "<class name=\"" + classNode.getName() + "\" lcom4=\"" + components.size() + "\">"),
                            componentsToXML(components),
                            Stream.of(getIndentText() + "</class>")
                        )
                        .flatMap(Function.identity());
            } else {
                return Stream.of(getIndentText() + "<class name=\"" + classNode.getName() + "\" lcom4=\"" + components.size() + "\"/>");
            }
    }

    private Stream<String> componentsToXML(Collection<Collection<FeatureNode>> components) {
        return components.stream()
                .flatMap(component -> Stream.of(
                        Stream.of(getIndentText().repeat(2) + "<component>"),
                        componentToXML(component),
                        Stream.of(getIndentText().repeat(2) + "</component>")
                )
                .flatMap(Function.identity()));
    }

    private Stream<String> componentToXML(Collection<FeatureNode> nodes) {
        return nodes.stream()
                .map(Node::getName)
                .map(name -> getIndentText().repeat(3) + "<feature name=\"" + name + "\"/>");
    }

    private void printYAMLFile(Map<ClassNode, Collection<Collection<FeatureNode>>> results) throws IOException {
        getOut().println(results.entrySet().stream()
                .flatMap(entry -> entryToYAML(entry.getKey(), entry.getValue()))
                .collect(joining(EOL)));
    }

    private Stream<String> entryToYAML(ClassNode classNode, Collection<Collection<FeatureNode>> components) {
        return Stream.concat(
                Stream.of(
                        "-",
                        getIndentText().repeat(1) + "name: " + classNode.getName(),
                        getIndentText().repeat(1) + "lcom4: " + components.size()
                ),
                componentsToYAML(components)
        );
    }

    private Stream<String> componentsToYAML(Collection<Collection<FeatureNode>> components) {
        if (components.size() > 1 && getCommandLine().isPresent("list")) {
            return Stream.concat(
                    Stream.of(getIndentText().repeat(1) + "components:"),
                    components.stream()
                            .flatMap(this::componentToYAML));
        } else {
            return Stream.empty();
        }
    }

    private Stream<String> componentToYAML(Collection<FeatureNode> nodes) {
        return Stream.concat(
                Stream.of(getIndentText().repeat(2) + "-"),
                nodes.stream()
                        .map(Node::getName)
                        .map(name -> getIndentText().repeat(3) + "- " + name));
    }

    private String getEncoding() {
        return getCommandLine().getSingleSwitch("encoding");
    }

    private String getDTDPrefix() {
        return getCommandLine().getSingleSwitch("dtd-prefix");
    }

    private String getIndentText() {
        return getCommandLine().getSingleSwitch("indent-text");
    }

    public static void main(String[] args) throws Exception {
        new ClassCohesion().run(args);
    }
}
