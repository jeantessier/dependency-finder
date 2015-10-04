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
import javax.xml.parsers.*;

import com.jeantessier.classreader.*;
import com.jeantessier.metrics.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.xml.sax.*;

public class OOMetrics extends Task {
    public static final String DEFAULT_PROJECT_NAME = "Project";
    public static final String DEFAULT_SORT = "name";

    private String projectName = DEFAULT_PROJECT_NAME;
    private File configuration;
    private boolean csv = false;
    private boolean txt = false;
    private boolean xml = false;
    private boolean validate = false;
    private String encoding = com.jeantessier.metrics.XMLPrinter.DEFAULT_ENCODING;
    private String dtdPrefix = com.jeantessier.metrics.XMLPrinter.DEFAULT_DTD_PREFIX;
    private String indentText;
    private boolean projectMetrics = false;
    private boolean groupMetrics = false;
    private boolean classMetrics = false;
    private boolean methodMetrics = false;
    private Path scopeIncludesList;
    private Path scopeExcludesList;
    private Path filterIncludesList;
    private Path filterExcludesList;
    private boolean showAllMetrics = false;
    private boolean showEmptyMetrics = false;
    private boolean showHiddenMeasurements = false;
    private String sort = DEFAULT_SORT;
    private boolean expand = false;
    private boolean reverse = false;
    private boolean enableCrossClassMeasurements = false;
    private File destprefix;
    private Path path;

    public String getProjectname() {
        return projectName;
    }
    
    public void setProjectname(String projectName) {
        this.projectName = projectName;
    }

    public File getConfiguration() {
        return configuration;
    }
    
    public void setConfiguration(File configuration) {
        this.configuration = configuration;
    }

    public boolean getCsv() {
        return csv;
    }
    
    public void setCsv(boolean csv) {
        this.csv = csv;
    }

    public boolean getTxt() {
        return txt;
    }
    
    public void setTxt(boolean txt) {
        this.txt = txt;
    }

    public boolean getXml() {
        return xml;
    }

    public void setXml(boolean xml) {
        this.xml = xml;
    }

    public boolean getValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
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

    public boolean getProjectmetrics() {
        return projectMetrics;
    }
    
    public void setProjectmetrics(boolean projectMetrics) {
        this.projectMetrics = projectMetrics;
    }

    public boolean getGroupmetrics() {
        return groupMetrics;
    }
    
    public void setGroupmetrics(boolean groupMetrics) {
        this.groupMetrics = groupMetrics;
    }

    public boolean getClassmetrics() {
        return classMetrics;
    }
    
    public void setClassmetrics(boolean classMetrics) {
        this.classMetrics = classMetrics;
    }

    public boolean getMethodmetrics() {
        return methodMetrics;
    }
    
    public void setMethodmetrics(boolean methodMetrics) {
        this.methodMetrics = methodMetrics;
    }

    public void setAllmetrics(boolean allMetrics) {
        setProjectmetrics(allMetrics);
        setGroupmetrics(allMetrics);
        setClassmetrics(allMetrics);
        setMethodmetrics(allMetrics);
    }
    
    public Path createScopeincludeslist() {
        if (scopeIncludesList == null) {
            scopeIncludesList = new Path(getProject());
        }

        return scopeIncludesList;
    }
    
    public Path getScopeincludeslist() {
        return scopeIncludesList;
    }
    
    public Path createScopeexcludeslist() {
        if (scopeExcludesList == null) {
            scopeExcludesList = new Path(getProject());
        }

        return scopeExcludesList;
    }
    
    public Path getScopeexcludeslist() {
        return scopeExcludesList;
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

    public boolean getShowallmetrics() {
        return showAllMetrics;
    }
    
    public void setShowallmetrics(boolean showAllMetrics) {
        this.showAllMetrics = showAllMetrics;
    }

    public boolean getShowemptymetrics() {
        return showEmptyMetrics;
    }
    
    public void setShowemptymetrics(boolean showEmptyMetrics) {
        this.showEmptyMetrics = showEmptyMetrics;
    }

    public boolean getShowhiddenmeasurements() {
        return showHiddenMeasurements;
    }
    
    public void setShowhiddenmeasurements(boolean showHiddenMeasurements) {
        this.showHiddenMeasurements = showHiddenMeasurements;
    }
    
    public String getSort() {
        return sort;
    }
    
    public void setSort(String sort) {
        this.sort = sort;
    }

    public boolean getExpand() {
        return expand;
    }
    
    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public boolean getReverse() {
        return reverse;
    }
    
    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public boolean getEnablecrossclassmeasurements() {
        return enableCrossClassMeasurements;
    }

    public void setEnablecrossclassmeasurements(boolean enableCrossClassMeasurements) {
        this.enableCrossClassMeasurements = enableCrossClassMeasurements;
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
    
    public void execute() throws BuildException {
        // first off, make sure that we've got what we need

        if (getConfiguration() == null) {
            throw new BuildException("configuration must be set!");
        }
        
        if (!getConfiguration().exists()) {
            throw new BuildException("configuration does not exist!");
        }
        
        if (!getConfiguration().isFile()) {
            throw new BuildException("configuration is not a file!");
        }

        if (getPath() == null) {
            throw new BuildException("path must be set!");
        }

        if (getDestprefix() == null) {
            throw new BuildException("destprefix must be set!");
        }

        try {
            VerboseListener verboseListener = new VerboseListener(this);
            
            log("Reading configuration ...");
            MetricsFactory factory = new MetricsFactory(getProjectname(), new MetricsConfigurationLoader(getValidate()).load(getConfiguration().getAbsolutePath()));
            
            com.jeantessier.metrics.MetricsGatherer gatherer = new com.jeantessier.metrics.MetricsGatherer(factory);
            if (getScopeincludeslist() != null || getScopeexcludeslist() != null) {
                gatherer.setScopeIncludes(createCollection(getScopeincludeslist(), getScopeexcludeslist()));
            }
            if (getFilterincludeslist() != null || getFilterexcludeslist() != null) {
                gatherer.setFilterIncludes(createCollection(getFilterincludeslist(), getFilterexcludeslist()));
            }
            gatherer.addMetricsListener(verboseListener);

            if (getEnablecrossclassmeasurements()) {
                log("Reading in all classes from path " + getPath());
                ClassfileLoader loader = new AggregatingClassfileLoader();
                loader.addLoadListener(verboseListener);
                loader.load(Arrays.asList(getPath().list()));

                log("Computing metrics ...");
                gatherer.visitClassfiles(loader.getAllClassfiles());
            } else {
                ClassfileLoader loader = new TransientClassfileLoader();
                loader.addLoadListener(verboseListener);
                loader.addLoadListener(new LoadListenerVisitorAdapter(gatherer));

                log("Reading classes and computing metrics as we go ...");
                loader.load(Arrays.asList(getPath().list()));
            }

            if (getShowallmetrics()) {
                for (Metrics metrics : gatherer.getMetricsFactory().getAllClassMetrics()) {
                    gatherer.getMetricsFactory().includeClassMetrics(metrics);
                }

                for (Metrics metrics : gatherer.getMetricsFactory().getAllMethodMetrics()) {
                    gatherer.getMetricsFactory().includeMethodMetrics(metrics);
                }
            }

            if (getCsv()) {
                printCSVFiles(gatherer.getMetricsFactory());
            } else if (getTxt()) {
                printTextFile(gatherer.getMetricsFactory());
            } else if (getXml()) {
                printXMLFile(gatherer.getMetricsFactory());
            }
        } catch (SAXException ex) {
            throw new BuildException(ex);
        } catch (ParserConfigurationException ex) {
            throw new BuildException(ex);
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    private Collection<String> createCollection(Path includes, Path excludes) throws IOException {
        Collection<String> result = new HashSet<String>();

        if (includes != null) {
            String[] filenames = includes.list();
            for (String filename : filenames) {
                BufferedReader reader = new BufferedReader(new FileReader(filename));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.add(line);
                }
                reader.close();
            }
        }
        
        if (excludes != null) {
            String[] filenames = excludes.list();
            for (String filename : filenames) {
                BufferedReader reader = new BufferedReader(new FileReader(filename));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.remove(line);
                }
                reader.close();
            }
        }
        
        return result;
    }

    private void printCSVFiles(MetricsFactory factory) throws IOException {
        MetricsComparator comparator = new MetricsComparator(getSort());
        if (getReverse()) {
            comparator.reverse();
        }

        List<Metrics>                   metrics;
        com.jeantessier.metrics.Printer printer;

        if (getProjectmetrics()) {
            String filename = getDestprefix().getAbsolutePath() + "_project.csv";
            log("Saving metrics to " + filename);

            PrintWriter out = new PrintWriter(new FileWriter(filename));
            
            metrics = new ArrayList<Metrics>(factory.getProjectMetrics());
            Collections.sort(metrics, comparator);
            printer = new com.jeantessier.metrics.CSVPrinter(out, factory.getConfiguration().getProjectMeasurements());
            printer.setShowEmptyMetrics(getShowemptymetrics());
            printer.setShowHiddenMeasurements(getShowhiddenmeasurements());
            if (getIndenttext() != null) {
                printer.setIndentText(getIndenttext());
            }

            printer.visitMetrics(metrics);

            out.close();
        }

        if (getGroupmetrics()) {
            String filename = getDestprefix().getAbsolutePath() + "_groups.csv";
            log("Saving metrics to " + filename);

            PrintWriter out = new PrintWriter(new FileWriter(filename));

            metrics = new ArrayList<Metrics>(factory.getGroupMetrics());
            Collections.sort(metrics, comparator);
            printer = new com.jeantessier.metrics.CSVPrinter(out, factory.getConfiguration().getGroupMeasurements());
            printer.setShowEmptyMetrics(getShowemptymetrics());
            printer.setShowHiddenMeasurements(getShowhiddenmeasurements());
            if (getIndenttext() != null) {
                printer.setIndentText(getIndenttext());
            }

            printer.visitMetrics(metrics);

            out.close();
        }

        if (getClassmetrics()) {
            String filename = getDestprefix().getAbsolutePath() + "_classes.csv";
            log("Saving metrics to " + filename);

            PrintWriter out = new PrintWriter(new FileWriter(filename));

            metrics = new ArrayList<Metrics>(factory.getClassMetrics());
            Collections.sort(metrics, comparator);
            printer = new com.jeantessier.metrics.CSVPrinter(out, factory.getConfiguration().getClassMeasurements());
            printer.setShowEmptyMetrics(getShowemptymetrics());
            printer.setShowHiddenMeasurements(getShowhiddenmeasurements());
            if (getIndenttext() != null) {
                printer.setIndentText(getIndenttext());
            }

            printer.visitMetrics(metrics);

            out.close();
        }

        if (getMethodmetrics()) {
            String filename = getDestprefix().getAbsolutePath() + "_methods.csv";
            log("Saving metrics to " + filename);

            PrintWriter out = new PrintWriter(new FileWriter(filename));

            metrics = new ArrayList<Metrics>(factory.getMethodMetrics());
            Collections.sort(metrics, comparator);
            printer = new com.jeantessier.metrics.CSVPrinter(out, factory.getConfiguration().getMethodMeasurements());
            printer.setShowEmptyMetrics(getShowemptymetrics());
            printer.setShowHiddenMeasurements(getShowhiddenmeasurements());
            if (getIndenttext() != null) {
                printer.setIndentText(getIndenttext());
            }

            printer.visitMetrics(metrics);

            out.close();
        }
    }

    private void printTextFile(MetricsFactory factory) throws IOException {
        MetricsComparator comparator = new MetricsComparator(getSort());
        if (getReverse()) {
            comparator.reverse();
        }

        String filename = getDestprefix().getAbsolutePath() + ".txt";
        log("Saving metrics to " + filename);
        
        PrintWriter out = new PrintWriter(new FileWriter(filename));

        List<Metrics> metrics;

        if (getProjectmetrics()) {
            out.println("Project metrics");
            out.println("---------------");
            metrics = new ArrayList<Metrics>(factory.getProjectMetrics());
            Collections.sort(metrics, comparator);
            com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(out, factory.getConfiguration().getProjectMeasurements());
            printer.setShowEmptyMetrics(getShowemptymetrics());
            printer.setShowHiddenMeasurements(getShowhiddenmeasurements());
            printer.setExpandCollectionMeasurements(getExpand());
            if (getIndenttext() != null) {
                printer.setIndentText(getIndenttext());
            }

            printer.visitMetrics(metrics);

            out.println();
        }

        if (getGroupmetrics()) {
            out.println("Group metrics");
            out.println("-------------");
            metrics = new ArrayList<Metrics>(factory.getGroupMetrics());
            Collections.sort(metrics, comparator);
            com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(out, factory.getConfiguration().getGroupMeasurements());
            printer.setShowEmptyMetrics(getShowemptymetrics());
            printer.setShowHiddenMeasurements(getShowhiddenmeasurements());
            printer.setExpandCollectionMeasurements(getExpand());
            if (getIndenttext() != null) {
                printer.setIndentText(getIndenttext());
            }

            printer.visitMetrics(metrics);

            out.println();
        }

        if (getClassmetrics()) {
            out.println("Class metrics");
            out.println("-------------");
            metrics = new ArrayList<Metrics>(factory.getClassMetrics());
            Collections.sort(metrics, comparator);
            com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(out, factory.getConfiguration().getClassMeasurements());
            printer.setShowEmptyMetrics(getShowemptymetrics());
            printer.setShowHiddenMeasurements(getShowhiddenmeasurements());
            printer.setExpandCollectionMeasurements(getExpand());
            if (getIndenttext() != null) {
                printer.setIndentText(getIndenttext());
            }

            printer.visitMetrics(metrics);

            out.println();
        }
        
        if (getMethodmetrics()) {
            out.println("Method metrics");
            out.println("--------------");
            metrics = new ArrayList<Metrics>(factory.getMethodMetrics());
            Collections.sort(metrics, comparator);
            com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(out, factory.getConfiguration().getMethodMeasurements());
            printer.setShowEmptyMetrics(getShowemptymetrics());
            printer.setShowHiddenMeasurements(getShowhiddenmeasurements());
            printer.setExpandCollectionMeasurements(getExpand());
            if (getIndenttext() != null) {
                printer.setIndentText(getIndenttext());
            }

            printer.visitMetrics(metrics);

            out.println();
        }
        
        out.close();
    }

    private void printXMLFile(MetricsFactory factory) throws IOException {
        MetricsComparator comparator = new MetricsComparator(getSort());
        if (getReverse()) {
            comparator.reverse();
        }

        String filename = getDestprefix().getAbsolutePath() + ".xml";
        log("Saving metrics to " + filename);
        
        PrintWriter out = new PrintWriter(new FileWriter(filename));

        List<Metrics> metrics = new ArrayList<Metrics>(factory.getProjectMetrics());
        Collections.sort(metrics, comparator);
        com.jeantessier.metrics.Printer printer = new com.jeantessier.metrics.XMLPrinter(out, factory.getConfiguration(), getEncoding(), getDtdprefix());
        printer.setShowEmptyMetrics(getShowemptymetrics());
        printer.setShowHiddenMeasurements(getShowhiddenmeasurements());
        if (getIndenttext() != null) {
            printer.setIndentText(getIndenttext());
        }

        printer.visitMetrics(metrics);

        out.close();
    }
}
