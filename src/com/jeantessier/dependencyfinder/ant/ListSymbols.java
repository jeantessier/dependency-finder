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

package com.jeantessier.dependencyfinder.ant;

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
import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class ListSymbols extends Task {
    private boolean classNames = false;
    private boolean fieldNames = false;
    private boolean methodNames = false;
    private boolean localNames = false;
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
    
    public void setLocalnames(boolean localNames) {
        this.localNames = localNames;
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

        return result;
    }

    private SymbolGathererStrategy createDefaultSymbolGathererStrategy() {
        DefaultSymbolGathererStrategy result = new DefaultSymbolGathererStrategy();

        // Since DefaultSymbolGathererStrategy lists everything by default,
        // we turn them all off if any of the switches are present.
        // This way, if you pass nothing, you get the default behavior and
        // the tool shows everything.  If you pass in one or more, you only
        // see symbols of the kind(s) you specified.
        if (getClassnames() || getFieldnames() || getMethodnames() || getLocalnames()) {
            result.setMatchingClassNames(false);
            result.setMatchingFieldNames(false);
            result.setMatchingMethodNames(false);
            result.setMatchingLocalNames(false);
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

        return result;
    }

    private Collection<String> loadCollection(Path path) {
        Collection<String> result = null;

        if (path != null) {
            result = new HashSet<String>();

            for (String filename : path.list()) {
                BufferedReader reader = null;
                String line;

                try {
                    reader = new BufferedReader(new FileReader(filename));
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
