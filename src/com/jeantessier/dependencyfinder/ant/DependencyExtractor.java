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
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.apache.log4j.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;

public class DependencyExtractor extends Task {
    private String filterIncludes = "//";
    private String filterExcludes = "";
    private boolean packageFilter = false;
    private String packageFilterIncludes = "";
    private String packageFilterExcludes = "";
    private boolean classFilter = false;
    private String classFilterIncludes = "";
    private String classFilterExcludes = "";
    private boolean featureFilter = false;
    private String featureFilterIncludes = "";
    private String featureFilterExcludes = "";

    private Path filterIncludesList;
    private Path filterExcludesList;

    private boolean xml        = false;
    private boolean minimize   = false;
    private boolean maximize   = false;
    private String  encoding   = com.jeantessier.dependency.XMLPrinter.DEFAULT_ENCODING;
    private String  dtdPrefix  = com.jeantessier.dependency.XMLPrinter.DEFAULT_DTD_PREFIX;
    private String  indentText;
    private File    destfile;
    private Path    path;

    public String getFilterincludes() {
        return filterIncludes;
    }

    public void setFilterincludes(String filterIncludes) {
        this.filterIncludes = filterIncludes;
    }

    public String getFilterexcludes() {
        return filterExcludes;
    }

    public void setFilterexcludes(String filterExcludes) {
        this.filterExcludes = filterExcludes;
    }

    public boolean getPackagefilter() {
        return packageFilter;
    }

    public void setPackagefilter(boolean packageFilter) {
        this.packageFilter = packageFilter;
    }

    public String getPackagefilterincludes() {
        return packageFilterIncludes;
    }

    public void setPackagefilterincludes(String packageFilterIncludes) {
        this.packageFilterIncludes = packageFilterIncludes;
    }

    public String getPackagefilterexcludes() {
        return packageFilterExcludes;
    }

    public void setPackagefilterexcludes(String packageFilterExcludes) {
        this.packageFilterExcludes = packageFilterExcludes;
    }

    public boolean getClassfilter() {
        return classFilter;
    }

    public void setClassfilter(boolean classFilter) {
        this.classFilter = classFilter;
    }

    public String getClassfilterincludes() {
        return classFilterIncludes;
    }

    public void setClassfilterincludes(String classFilterIncludes) {
        this.classFilterIncludes = classFilterIncludes;
    }

    public String getClassfilterexcludes() {
        return classFilterExcludes;
    }

    public void setClassfilterexcludes(String classFilterExcludes) {
        this.classFilterExcludes = classFilterExcludes;
    }

    public boolean getFeaturefilter() {
        return featureFilter;
    }

    public void setFeaturefilter(boolean featureFilter) {
        this.featureFilter = featureFilter;
    }

    public String getFeaturefilterincludes() {
        return featureFilterIncludes;
    }

    public void setFeaturefilterincludes(String featureFilterIncludes) {
        this.featureFilterIncludes = featureFilterIncludes;
    }

    public String getFeaturefilterexcludes() {
        return featureFilterExcludes;
    }

    public void setFeaturefilterexcludes(String featureFilterExcludes) {
        this.featureFilterExcludes = featureFilterExcludes;
    }

    public Path createFilterincludeslist() {
        if (filterIncludesList == null) {
            filterIncludesList = new Path(getProject());
        }

        return filterIncludesList;
    }

    public Path getFilterincludeslist() {
        return filterIncludesList;
    }

    public Path createFilterexcludeslist() {
        if (filterExcludesList == null) {
            filterExcludesList = new Path(getProject());
        }

        return filterExcludesList;
    }

    public Path getFilterexcludeslist() {
        return filterExcludesList;
    }

    public boolean getXml() {
        return xml;
    }

    public void setXml(boolean xml) {
        this.xml = xml;
    }

    public boolean getMinimize() {
        return minimize;
    }

    public void setMinimize(boolean minimize) {
        this.minimize = minimize;
    }

    public boolean getMaximize() {
        return maximize;
    }

    public void setMaximize(boolean maximize) {
        this.maximize = maximize;
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

        if (hasFilterRegularExpressionSwitches() && hasFilterListSwitches()) {
            throw new BuildException("Cannot have filter attributes for regular expressions and lists at the same time!");
        }

        log("Reading classes from path " + getPath());

        VerboseListener verboseListener = new VerboseListener(this);

        NodeFactory factory = new NodeFactory();
        CodeDependencyCollector collector = new CodeDependencyCollector(factory, getFilterCriteria());
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(new LoadListenerVisitorAdapter(collector));
        loader.addLoadListener(verboseListener);
        loader.load(Arrays.asList(getPath().list()));

        if (getMinimize()) {
            LinkMinimizer minimizer = new LinkMinimizer();
            minimizer.traverseNodes(factory.getPackages().values());
        } else if (getMaximize()) {
            LinkMaximizer maximizer = new LinkMaximizer();
            maximizer.traverseNodes(factory.getPackages().values());
        }

        log("Saving dependency graph to " + getDestfile().getAbsolutePath());
        
        try {
            PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));

            com.jeantessier.dependency.Printer printer;
            if (getXml()) {
                printer = new com.jeantessier.dependency.XMLPrinter(out, getEncoding(), getDtdprefix());
            } else {
                printer = new com.jeantessier.dependency.TextPrinter(out);
            }
                
            if (getIndenttext() != null) {
                printer.setIndentText(getIndenttext());
            }
                
            printer.traverseNodes(factory.getPackages().values());
                
            out.close();
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    protected SelectionCriteria getFilterCriteria() throws BuildException {
        SelectionCriteria result = new ComprehensiveSelectionCriteria();

        if (hasFilterRegularExpressionSwitches()) {
            result = createRegularExpressionFilterCriteria();
        } else if (hasFilterListSwitches()) {
            result = createCollectionSelectionCriteria(getFilterincludeslist(), getFilterexcludeslist());
        }

        return result;
    }

    protected RegularExpressionSelectionCriteria createRegularExpressionFilterCriteria() throws BuildException {
        RegularExpressionSelectionCriteria result = new RegularExpressionSelectionCriteria();

        if (getPackagefilter() || getClassfilter() || getFeaturefilter()) {
            result.setMatchingPackages(getPackagefilter());
            result.setMatchingClasses(getClassfilter());
            result.setMatchingFeatures(getFeaturefilter());
        }

        result.setGlobalIncludes(getFilterincludes());
        result.setGlobalExcludes(getFilterexcludes());
        result.setPackageIncludes(getPackagefilterincludes());
        result.setPackageExcludes(getPackagefilterexcludes());
        result.setClassIncludes(getClassfilterincludes());
        result.setClassExcludes(getClassfilterexcludes());
        result.setFeatureIncludes(getFeaturefilterincludes());
        result.setFeatureExcludes(getFeaturefilterexcludes());

        return result;
    }

    private boolean hasFilterRegularExpressionSwitches() {
        return
                !getFilterincludes().equals("//") ||
                !getFilterexcludes().equals("") ||
                getPackagefilter() ||
                !getPackagefilterincludes().equals("") ||
                !getPackagefilterexcludes().equals("") ||
                getClassfilter() ||
                !getClassfilterincludes().equals("") ||
                !getClassfilterexcludes().equals("") ||
                getFeaturefilter() ||
                !getFeaturefilterincludes().equals("") ||
                !getFeaturefilterexcludes().equals("");
    }

    private boolean hasFilterListSwitches() {
        return
                getFilterincludeslist() != null ||
                getFilterexcludeslist() != null;
    }

    private CollectionSelectionCriteria createCollectionSelectionCriteria(Path includes, Path excludes) {
        return new CollectionSelectionCriteria(loadCollection(includes), loadCollection(excludes));
    }

    private Collection<String> loadCollection(Path path) {
        Collection<String> result = null;

        if (path != null) {
            result = new HashSet<String>();

            String[] filenames = path.list();
            for (int i = 0; i < filenames.length; i++) {
                BufferedReader reader = null;
                String line;

                try {
                    reader = new BufferedReader(new FileReader(filenames[i]));
                    while ((line = reader.readLine()) != null) {
                        result.add(line);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(getClass()).error("Couldn't read file " + filenames[i], ex);
                } finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(getClass()).error("Couldn't close file " + filenames[i], ex);
                    }
                }
            }
        }

        return result;
    }
}
