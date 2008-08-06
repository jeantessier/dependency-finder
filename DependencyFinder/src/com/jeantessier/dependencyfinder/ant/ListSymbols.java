/*
 *  Copyright (c) 2001-2008, Jean Tessier
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

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

import com.jeantessier.classreader.*;
import com.jeantessier.text.*;

public class ListSymbols extends Task {
    private boolean classNames = false;
    private boolean fieldNames = false;
    private boolean methodNames = false;
    private boolean localNames = false;
    private boolean nonPrivateFieldNames = false;
    private boolean finalMethodOrClassNames = false;
    private List<String> includes = Collections.singletonList("//");
    private List<String> excludes = Collections.emptyList();
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

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(String excludes) {
        this.excludes = RegularExpressionParser.parseRE(excludes);
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

    public void execute() throws BuildException {
        // first off, make sure that we've got what we need

        if (getPath() == null) {
            throw new BuildException("path must be set!");
        }

        if (getDestfile() == null) {
            throw new BuildException("destfile must be set!");
        }

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

        result = new FilteringSymbolGathererStrategy(result, getIncludes(), getExcludes());

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
}
