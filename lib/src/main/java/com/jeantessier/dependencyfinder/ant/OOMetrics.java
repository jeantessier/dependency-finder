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

package com.jeantessier.dependencyfinder.ant;

import com.jeantessier.classreader.AggregatingClassfileLoader;
import com.jeantessier.classreader.ClassfileLoader;
import com.jeantessier.classreader.LoadListenerVisitorAdapter;
import com.jeantessier.classreader.TransientClassfileLoader;
import com.jeantessier.metrics.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;

public class OOMetrics extends Task {
    public static final String DEFAULT_PROJECT_NAME = "Project";
    public static final String DEFAULT_SORT = "name";

    private String projectName = DEFAULT_PROJECT_NAME;
    private File configuration;
    private boolean csv = false;
    private boolean json = false;
    private boolean text = false;
    private boolean xml = false;
    private boolean yaml = false;
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
    
    public void setIndenttext(String indentText) {
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
                gatherer.getMetricsFactory().getAllClassMetrics().forEach(metrics -> gatherer.getMetricsFactory().includeClassMetrics(metrics));
                gatherer.getMetricsFactory().getAllMethodMetrics().forEach(metrics -> gatherer.getMetricsFactory().includeMethodMetrics(metrics));
            }

            if (getCsv()) {
                printCSVFiles(gatherer.getMetricsFactory());
            } else if (getJson()) {
                printJSONFile(gatherer.getMetricsFactory());
            } else if (getText()) {
                printTextFile(gatherer.getMetricsFactory());
            } else if (getXml()) {
                printXMLFile(gatherer.getMetricsFactory());
            } else if (getYaml()) {
                printYAMLFile(gatherer.getMetricsFactory());
            }
        } catch (SAXException | ParserConfigurationException | IOException ex) {
            throw new BuildException(ex);
        }
    }

    private Collection<String> createCollection(Path includes, Path excludes) throws IOException {
        Collection<String> result = new HashSet<>();

        if (includes != null) {
            for (String filename : includes.list()) {
                result.addAll(Files.readAllLines(Paths.get(filename)));
            }
        }
        
        if (excludes != null) {
            for (String filename : excludes.list()) {
                result.removeAll(Files.readAllLines(Paths.get(filename)));
            }
        }
        
        return result;
    }

    private void printCSVFiles(MetricsFactory factory) throws IOException {
        printCSVFile(getProjectmetrics(), "project", factory.getConfiguration().getProjectMeasurements(), factory.getProjectMetrics());
        printCSVFile(getGroupmetrics(),   "groups",  factory.getConfiguration().getGroupMeasurements(),   factory.getGroupMetrics());
        printCSVFile(getClassmetrics(),   "classes", factory.getConfiguration().getClassMeasurements(),   factory.getClassMetrics());
        printCSVFile(getMethodmetrics(),  "methods", factory.getConfiguration().getMethodMeasurements(),  factory.getMethodMetrics());
    }

    private void printCSVFile(boolean flag, String name, List<MeasurementDescriptor> descriptors, Collection<Metrics> metrics) throws IOException {
        if (flag) {
            String filename = getDestprefix().getAbsolutePath() + "_" + name + ".csv";
            log("Saving metrics to " + filename);

            try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
                printMetrics(descriptors, metrics, CSVPrinter.class, out);
            }
        }
    }

    private void printJSONFile(MetricsFactory factory) throws IOException {
        String filename = getDestprefix().getAbsolutePath() + ".json";
        log("Saving metrics to " + filename);

        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            printMetrics(factory.getProjectMetrics(), new com.jeantessier.metrics.JSONPrinter(out, factory.getConfiguration()));
        }
    }

    private void printTextFile(MetricsFactory factory) throws IOException {
        String filename = getDestprefix().getAbsolutePath() + ".txt";
        log("Saving metrics to " + filename);
        
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            printTextFile(getProjectmetrics(), "Project metrics", factory.getConfiguration().getProjectMeasurements(), factory.getProjectMetrics(), out);
            printTextFile(getGroupmetrics(),   "Group metrics",   factory.getConfiguration().getGroupMeasurements(),   factory.getGroupMetrics(),   out);
            printTextFile(getClassmetrics(),   "Class metrics",   factory.getConfiguration().getClassMeasurements(),   factory.getClassMetrics(),   out);
            printTextFile(getMethodmetrics(),  "Method metrics",  factory.getConfiguration().getMethodMeasurements(),  factory.getMethodMetrics(),  out);
        }
    }

    private void printTextFile(boolean flag, String label, List<MeasurementDescriptor> descriptors, Collection<Metrics> metrics, PrintWriter out) {
        if (flag) {
            out.println(label);
            out.println(label.replaceAll(".", "-"));
            printMetrics(descriptors, metrics, TextPrinter.class, out);
            out.println();
        }
    }

    private void printXMLFile(MetricsFactory factory) throws IOException {
        String filename = getDestprefix().getAbsolutePath() + ".xml";
        log("Saving metrics to " + filename);

        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            printMetrics(factory.getProjectMetrics(), new com.jeantessier.metrics.XMLPrinter(out, factory.getConfiguration(), getEncoding(), getDtdprefix()));
        }
    }

    private void printYAMLFile(MetricsFactory factory) throws IOException {
        String filename = getDestprefix().getAbsolutePath() + ".yaml";
        log("Saving metrics to " + filename);

        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            printMetrics(factory.getProjectMetrics(), new com.jeantessier.metrics.YAMLPrinter(out, factory.getConfiguration()));
        }
    }

    private void printMetrics(List<MeasurementDescriptor> descriptors, Collection<Metrics> metrics, Class<? extends Printer> clazz, PrintWriter out) {
        try {
            printMetrics(metrics, clazz.getConstructor(PrintWriter.class, List.class).newInstance(out, descriptors));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new BuildException(ex);
        }
    }

    private void printMetrics(Collection<Metrics> metrics, Printer printer) {
        printer.setExpandCollectionMeasurements(getExpand());
        printer.setShowEmptyMetrics(getShowemptymetrics());
        printer.setShowHiddenMeasurements(getShowhiddenmeasurements());
        if (getIndenttext() != null) {
            printer.setIndentText(getIndenttext());
        }

        var comparator = new MetricsComparator(getSort());
        if (getReverse()) {
            comparator.reverse();
        }
        printer.setComparator(comparator);

        printer.visitMetrics(metrics);
    }
}
