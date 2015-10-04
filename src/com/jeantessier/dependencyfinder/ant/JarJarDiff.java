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

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import com.jeantessier.classreader.*;
import com.jeantessier.diff.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

public class JarJarDiff extends Task {
    public static final String API_STRATEGY = "api";
    public static final String INCOMPATIBLE_STRATEGY = "incompatible";

    public static final String DEFAULT_LEVEL = API_STRATEGY;

    private String name = "";
    private Path oldPath;
    private String oldLabel;
    private Path newPath;
    private String newLabel;
    private File filter;
    private String level = DEFAULT_LEVEL;
    private boolean code;
    private String encoding = Report.DEFAULT_ENCODING;
    private String dtdPrefix = Report.DEFAULT_DTD_PREFIX;
    private String indentText;
    private File destfile;

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Path createOld() {
        if (oldPath == null) {
            oldPath = new Path(getProject());
        }

        return oldPath;
    }
    
    public Path getOld() {
        return oldPath;
    }

    public String getOldlabel() {
        return oldLabel;
    }
    
    public void setOldlabel(String oldLabel) {
        this.oldLabel = oldLabel;
    }

    public Path createNew() {
        if (newPath == null) {
            newPath = new Path(getProject());
        }

        return newPath;
    }
    
    public Path getNew() {
        return newPath;
    }

    public String getNewlabel() {
        return newLabel;
    }
    
    public void setNewlabel(String newLabel) {
        this.newLabel = newLabel;
    }

    public File getFilter() {
        return filter;
    }

    public void setfilter(File filter) {
        this.filter = filter;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean getCode() {
        return code;
    }

    public void setCode(boolean code) {
        this.code = code;
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
    
    public void setIntenttext(String indentText) {
        this.indentText = indentText;
    }

    public File getDestfile() {
        return destfile;
    }
    
    public void setDestfile(File destfile) {
        this.destfile = destfile;
    }

    // Visible for tests only
    void validateParameters() throws BuildException {
        if (getOld() == null) {
            throw new BuildException("old must be set!");
        }

        if (getOldlabel() == null) {
            setOldlabel(getOld().toString());
        }

        if (getNew() == null) {
            throw new BuildException("new must be set!");
        }

        if (getNewlabel() == null) {
            setNewlabel(getNew().toString());
        }

        if (getDestfile() == null) {
            throw new BuildException("destfile must be set!");
        }
    }

    public void execute() throws BuildException {
        validateParameters();

        VerboseListener verboseListener = new VerboseListener(this);

        try {
            // Collecting data, first classfiles from JARs,
            // then package/class trees using NodeFactory.

            log("Loading old classes from path " + getOld());
            PackageMapper oldPackages = new PackageMapper();
            ClassfileLoader oldJar = new AggregatingClassfileLoader();
            oldJar.addLoadListener(oldPackages);
            oldJar.addLoadListener(verboseListener);
            oldJar.load(Arrays.asList(getOld().list()));

            log("Loading new classes from path " + getNew());
            PackageMapper newPackages = new PackageMapper();
            ClassfileLoader newJar = new AggregatingClassfileLoader();
            newJar.addLoadListener(newPackages);
            newJar.addLoadListener(verboseListener);
            newJar.load(Arrays.asList(getNew().list()));

            // Starting to compare, first at package level,
            // then descending to class level for packages
            // that are in both the old and the new codebase.

            log("Comparing old and new classes ...");

            Differences differences = getDifferencesFactory().createProjectDifferences(getName(), getOldlabel(), oldPackages, getNewlabel(), newPackages);

            log("Saving difference report to " + getDestfile().getAbsolutePath());

            Report report = new Report(getEncoding(), getDtdprefix());
            if (getIndenttext() != null) {
                report.setIndentText(getIndenttext());
            }

            differences.accept(report);

            PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));
            out.print(report.render());
            out.close();
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    private DifferencesFactory getDifferencesFactory() throws IOException {
        DifferenceStrategy baseStrategy = getBaseStrategy(getCode());
        DifferenceStrategy strategy = getStrategy(getLevel(), baseStrategy);

        if (getFilter() != null) {
            strategy = new ListBasedDifferenceStrategy(strategy, getFilter());
        }

        return new DifferencesFactory(strategy);
    }

    private DifferenceStrategy getBaseStrategy(boolean useCode) {
        DifferenceStrategy baseStrategy;
        if (useCode) {
            baseStrategy = new CodeDifferenceStrategy();
        } else {
            baseStrategy = new NoDifferenceStrategy();
        }
        return baseStrategy;
    }

    private DifferenceStrategy getStrategy(String level, DifferenceStrategy baseStrategy) {
        DifferenceStrategy strategy;
        if (API_STRATEGY.equals(level)) {
            strategy = new APIDifferenceStrategy(baseStrategy);
        } else if (INCOMPATIBLE_STRATEGY.equals(level)) {
            strategy = new IncompatibleDifferenceStrategy(baseStrategy);
        } else {
            try {
                Constructor constructor;
                try {
                    constructor = Class.forName(level).getConstructor(DifferenceStrategy.class);
                    strategy = (DifferenceStrategy) constructor.newInstance(baseStrategy);
                } catch (NoSuchMethodException ex) {
                    strategy = (DifferenceStrategy) Class.forName(level).newInstance();
                }
            } catch (InvocationTargetException ex) {
                log("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\": " + ex.getMessage());
                strategy = getDefaultStrategy(baseStrategy);
            } catch (InstantiationException ex) {
                log("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\": " + ex.getMessage());
                strategy = getDefaultStrategy(baseStrategy);
            } catch (IllegalAccessException ex) {
                log("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\": " + ex.getMessage());
                strategy = getDefaultStrategy(baseStrategy);
            } catch (ClassNotFoundException ex) {
                log("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\": " + ex.getMessage());
                strategy = getDefaultStrategy(baseStrategy);
            } catch (ClassCastException ex) {
                log("Unknown level \"" + level + "\", using default level \"" + DEFAULT_LEVEL + "\": " + ex.getMessage());
                strategy = getDefaultStrategy(baseStrategy);
            }
        }
        return strategy;
    }

    private APIDifferenceStrategy getDefaultStrategy(DifferenceStrategy baseStrategy) {
        return new APIDifferenceStrategy(baseStrategy);
    }
}
