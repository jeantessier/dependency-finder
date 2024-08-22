/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

package com.jeantessier.dependencyfinder.ant;

import com.jeantessier.classreader.*;
import com.jeantessier.text.RegularExpressionParser;
import org.apache.logging.log4j.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class ListSymbols extends Task {
    private boolean classes = false;
    private boolean fields = false;
    private boolean methods = false;
    private boolean localVariables = false;
    private boolean innerClasses = false;
    private  boolean publicAccessibility = false;
    private  boolean protectedAccessibility = false;
    private  boolean privateAccessibility = false;
    private  boolean packageAccessibility = false;
    private boolean nonPrivateFields = false;
    private boolean finalMethodsOrClasses = false;
    private List<String> includes = Collections.singletonList("//");
    private Path includesList;
    private List<String> excludes = Collections.emptyList();
    private Path excludesList;
    private File destprefix;
    private Path path;
    private boolean csv = false;
    private boolean json = false;
    private boolean text = false;
    private boolean xml = false;
    private boolean yaml = false;
    private String encoding = XMLSymbolPrinter.DEFAULT_ENCODING;
    private String dtdPrefix = XMLSymbolPrinter.DEFAULT_DTD_PREFIX;
    private String indentText = XMLSymbolPrinter.DEFAULT_INDENT_TEXT;

    public boolean getClasses() {
        return classes;
    }
    
    public void setClasses(boolean classes) {
        this.classes = classes;
    }

    public boolean getFields() {
        return fields;
    }
    
    public void setFields(boolean fields) {
        this.fields = fields;
    }

    public boolean getMethods() {
        return methods;
    }
    
    public void setMethods(boolean methods) {
        this.methods = methods;
    }

    public boolean getLocalvariables() {
        return localVariables;
    }

    public void setLocalvariables(boolean localVariables) {
        this.localVariables = localVariables;
    }

    public boolean getInnerclasses() {
        return innerClasses;
    }

    public void setInnerclasses(boolean innerClasses) {
        this.innerClasses = innerClasses;
    }

    public boolean getPublicaccessibility() {
        return publicAccessibility;
    }

    public void setPublicaccessibility(boolean publicAccessibility) {
        this.publicAccessibility = publicAccessibility;
    }

    public boolean getProtectedaccessibility() {
        return protectedAccessibility;
    }

    public void setProtectedaccessibility(boolean protectedAccessibility) {
        this.protectedAccessibility = protectedAccessibility;
    }

    public boolean getPrivateaccessibility() {
        return privateAccessibility;
    }

    public void setPrivateaccessibility(boolean privateAccessibility) {
        this.privateAccessibility = privateAccessibility;
    }

    public boolean getPackageaccessibility() {
        return packageAccessibility;
    }

    public void setPackageaccessibility(boolean packageAccessibility) {
        this.packageAccessibility = packageAccessibility;
    }

    public boolean getNonprivatefields() {
        return nonPrivateFields;
    }

    public void setNonprivatefields(boolean nonPrivateFields) {
        this.nonPrivateFields = nonPrivateFields;
    }

    public boolean getFinalmethodsorclasses() {
        return finalMethodsOrClasses;
    }

    public void setFinalmethodsorclasses(boolean finalMethodsOrClasses) {
        this.finalMethodsOrClasses = finalMethodsOrClasses;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public void setIncludes(String includes) {
        this.includes = RegularExpressionParser.parseRE(includes);
    }

    public Path createIncludeslist() {
        if (includesList == null) {
            includesList = new Path(getProject());
        }

        return includesList;
    }

    public Path getIncludeslist() {
        return includesList;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(String excludes) {
        this.excludes = RegularExpressionParser.parseRE(excludes);
    }

    public Path createExcludeslist() {
        if (excludesList == null) {
            excludesList = new Path(getProject());
        }

        return excludesList;
    }

    public Path getExcludeslist() {
        return excludesList;
    }

    public File getDestprefix() {
        return destprefix;
    }

    public void setDestprefix(File destprefix) {
        this.destprefix = destprefix;
    }

    public Path createPath() {
        if (path == null) {
            path = new Path(getProject());
        }

        return path;
    }

    public Path getPath() {
        return path;
    }

    public boolean getCsv() {
        return csv;
    }

    public void setCsv(boolean csv) {
        this.csv = csv;
    }

    public boolean getJson() {
        return json;
    }

    public void setJson(boolean json) {
        this.json = json;
    }

    public boolean getText() {
        return text;
    }

    public void setText(boolean text) {
        this.text = text;
    }

    public void setTxt(boolean text) {
        setText(text);
    }

    public boolean getXml() {
        return xml;
    }

    public void setXml(boolean xml) {
        this.xml = xml;
    }

    public boolean getYaml() {
        return yaml;
    }

    public void setYaml(boolean yaml) {
        this.yaml = yaml;
    }

    public void setYml(boolean yaml) {
        setYaml(yaml);
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getDtdprefix() {
        return dtdPrefix;
    }

    public void setDtdprefix(String dtdPrefix) {
        this.dtdPrefix = dtdPrefix;
    }

    public String getIndenttext() {
        return indentText;
    }

    public void setIndenttext(String indentText) {
        this.indentText = indentText;
    }

    // Visible for tests only
    void validateParameters() throws BuildException {
        if (getPath() == null) {
            throw new BuildException("path must be set!");
        }

        if (getDestprefix() == null) {
            throw new BuildException("destprefix must be set!");
        }
    }

    public void execute() throws BuildException {
        validateParameters();

        log("Reading classes from path " + getPath());

        VerboseListener verboseListener = new VerboseListener(this);

        SymbolGatherer gatherer = new SymbolGatherer(createStrategy());

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(new LoadListenerVisitorAdapter(gatherer));
        loader.addLoadListener(verboseListener);
        loader.load(Arrays.asList(getPath().list()));

        if (getCsv()) {
            printCSVFiles(gatherer);
        } else if (getJson()) {
            printJSONFile(gatherer);
        } else if (getText()) {
            printTextFile(gatherer);
        } else if (getXml()) {
            printXMLFile(gatherer);
        } else if (getYaml()) {
            printYAMLFile(gatherer);
        }
    }

    private void printCSVFiles(SymbolGatherer gatherer) throws BuildException {
        log("Saving symbols to " + getDestprefix().getAbsolutePath() + "_*.csv");

        try {
            new CSVSymbolPrinter(
                    null,
                    getClasses(),
                    getFields(),
                    getMethods(),
                    getLocalvariables(),
                    getInnerclasses(),
                    Optional.of(getDestprefix().getAbsolutePath())
            ).print(gatherer);
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    private void printJSONFile(SymbolGatherer gatherer) throws BuildException {
        String filename = getDestprefix().getAbsolutePath() + ".json";
        log("Saving symbols to " + filename);

        try (var out = new PrintWriter(new FileWriter(filename))) {
            new JSONSymbolPrinter(out).print(gatherer);
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    private void printTextFile(SymbolGatherer gatherer) throws BuildException {
        String filename = getDestprefix().getAbsolutePath() + ".txt";
        log("Saving symbols to " + filename);

        try (var out = new PrintWriter(new FileWriter(filename))) {
            new TextSymbolPrinter(out).print(gatherer);
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    private void printXMLFile(SymbolGatherer gatherer) throws BuildException {
        String filename = getDestprefix().getAbsolutePath() + ".xml";
        log("Saving symbols to " + filename);

        try (var out = new PrintWriter(new FileWriter(filename))) {
            new XMLSymbolPrinter(
                    out,
                    getEncoding(),
                    getDtdprefix(),
                    getIndenttext()
            ).print(gatherer);
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    private void printYAMLFile(SymbolGatherer gatherer) throws BuildException {
        String filename = getDestprefix().getAbsolutePath() + ".yaml";
        log("Saving symbols to " + filename);

        try (var out = new PrintWriter(new FileWriter(filename))) {
            new YAMLSymbolPrinter(
                    out,
                    getIndenttext()
            ).print(gatherer);
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    // Visible for testing only
    SymbolGathererStrategy createStrategy() {
        SymbolGathererStrategy result;

        if (getNonprivatefields()) {
            result = new NonPrivateFieldSymbolGathererStrategy();
        } else if (getFinalmethodsorclasses()) {
            result = new FinalMethodOrClassSymbolGathererStrategy();
        } else {
            result = createDefaultSymbolGathererStrategy();
        }

        result = new FilteringSymbolGathererStrategy(result, getIncludes(), loadCollection(getIncludeslist()), getExcludes(), loadCollection(getExcludeslist()));

        if (getPublicaccessibility() || getProtectedaccessibility() || getPrivateaccessibility() || getPackageaccessibility()) {
            result = new AccessibilitySymbolGathererStrategy(result, getPublicaccessibility(), getProtectedaccessibility(), getPrivateaccessibility(), getPackageaccessibility());
        }

        return result;
    }

    private SymbolGathererStrategy createDefaultSymbolGathererStrategy() {
        DefaultSymbolGathererStrategy result = new DefaultSymbolGathererStrategy();

        // Since DefaultSymbolGathererStrategy lists everything by default,
        // we turn them all off if any of the switches are present.
        // This way, if you pass nothing, you get the default behavior and
        // the tool shows everything.  If you pass in one or more, you only
        // see symbols of the kind(s) you specified.
        if (getClasses() || getFields() || getMethods() || getLocalvariables() || getInnerclasses()) {
            result.setMatchingClasses(false);
            result.setMatchingFields(false);
            result.setMatchingMethods(false);
            result.setMatchingLocalVariables(false);
            result.setMatchingInnerClasses(false);
        }

        if (getClasses()) {
            result.setMatchingClasses(true);
        }

        if (getFields()) {
            result.setMatchingFields(true);
        }

        if (getMethods()) {
            result.setMatchingMethods(true);
        }

        if (getLocalvariables()) {
            result.setMatchingLocalVariables(true);
        }

        if (getInnerclasses()) {
            result.setMatchingInnerClasses(true);
        }

        return result;
    }

    private Collection<String> loadCollection(Path path) {
        Collection<String> result = null;

        if (path != null) {
            result = Arrays.stream(path.list())
                    .map(Paths::get)
                    .flatMap(filepath -> {
                        try {
                            return Files.lines(filepath);
                        } catch (IOException ex) {
                            LogManager.getLogger(getClass()).error("Couldn't read file {}", filepath, ex);
                            return Stream.empty();
                        }
                    }).distinct()
                    .toList();
        }

        return result;
    }
}
