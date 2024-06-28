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

import com.jeantessier.classreader.AccessibilitySymbolGathererStrategy;
import com.jeantessier.classreader.ClassfileLoader;
import com.jeantessier.classreader.DefaultSymbolGathererStrategy;
import com.jeantessier.classreader.FilteringSymbolGathererStrategy;
import com.jeantessier.classreader.FinalMethodOrClassSymbolGathererStrategy;
import com.jeantessier.classreader.LoadListenerVisitorAdapter;
import com.jeantessier.classreader.NonPrivateFieldSymbolGathererStrategy;
import com.jeantessier.classreader.SymbolGatherer;
import com.jeantessier.classreader.SymbolGathererStrategy;
import com.jeantessier.classreader.TransientClassfileLoader;
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
    private boolean classNames = false;
    private boolean fieldNames = false;
    private boolean methodNames = false;
    private boolean localNames = false;
    private boolean innerClassNames = false;
    private  boolean publicAccessibility = false;
    private  boolean protectedAccessibility = false;
    private  boolean privateAccessibility = false;
    private  boolean packageAccessibility = false;
    private boolean nonPrivateFieldNames = false;
    private boolean finalMethodOrClassNames = false;
    private List<String> includes = Collections.singletonList("//");
    private Path includesList;
    private List<String> excludes = Collections.emptyList();
    private Path excludesList;
    private File destfile;
    private Path path;

    public boolean getClassnames() {
        return classNames;
    }
    
    public void setClassnames(boolean classNames) {
        this.classNames = classNames;
    }

    public boolean getFieldnames() {
        return fieldNames;
    }
    
    public void setFieldnames(boolean fieldNames) {
        this.fieldNames = fieldNames;
    }

    public boolean getMethodnames() {
        return methodNames;
    }
    
    public void setMethodnames(boolean methodNames) {
        this.methodNames = methodNames;
    }

    public boolean getLocalnames() {
        return localNames;
    }

    public void setInnerclassnames(boolean innerClassNames) {
        this.innerClassNames = innerClassNames;
    }

    public boolean getInnerclassnames() {
        return innerClassNames;
    }

    public void setLocalnames(boolean localNames) {
        this.localNames = localNames;
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

    public boolean getNonprivatefieldnames() {
        return nonPrivateFieldNames;
    }

    public void setNonprivatefieldnames(boolean nonPrivateFieldNames) {
        this.nonPrivateFieldNames = nonPrivateFieldNames;
    }

    public boolean getFinalmethodorclassnames() {
        return finalMethodOrClassNames;
    }

    public void setFinalmethodorclassnames(boolean finalMethodOrClassNames) {
        this.finalMethodOrClassNames = finalMethodOrClassNames;
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

    public File getDestfile() {
        return destfile;
    }

    public void setDestfile(File destfile) {
        this.destfile = destfile;
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

    // Visible for tests only
    void validateParameters() throws BuildException {
        if (getPath() == null) {
            throw new BuildException("path must be set!");
        }

        if (getDestfile() == null) {
            throw new BuildException("destfile must be set!");
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

        log("Saving symbols to " + getDestfile().getAbsolutePath());

        try {
            PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));
            for (String symbol : gatherer.getCollection()) {
                out.println(symbol);
            }
            out.close();
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    // Visible for testing only
    SymbolGathererStrategy createStrategy() {
        SymbolGathererStrategy result;

        if (getNonprivatefieldnames()) {
            result = new NonPrivateFieldSymbolGathererStrategy();
        } else if (getFinalmethodorclassnames()) {
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
        if (getClassnames() || getFieldnames() || getMethodnames() || getLocalnames() || getInnerclassnames()) {
            result.setMatchingClassNames(false);
            result.setMatchingFieldNames(false);
            result.setMatchingMethodNames(false);
            result.setMatchingLocalNames(false);
            result.setMatchingInnerClassNames(false);
        }

        if (getClassnames()) {
            result.setMatchingClassNames(true);
        }

        if (getFieldnames()) {
            result.setMatchingFieldNames(true);
        }

        if (getMethodnames()) {
            result.setMatchingMethodNames(true);
        }

        if (getLocalnames()) {
            result.setMatchingLocalNames(true);
        }

        if (getInnerclassnames()) {
            result.setMatchingInnerClassNames(true);
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
