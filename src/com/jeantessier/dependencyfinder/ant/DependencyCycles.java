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

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.apache.log4j.*;
import org.xml.sax.*;

import com.jeantessier.dependency.*;

public class DependencyCycles extends GraphTask {
    private String startIncludes = "//";
    private String startExcludes = "";
    private String packageStartIncludes = "";
    private String packageStartExcludes = "";
    private String classStartIncludes = "";
    private String classStartExcludes = "";
    private String featureStartIncludes = "";
    private String featureStartExcludes = "";

    private Path startIncludesList;
    private Path startExcludesList;

    private String  maximumCycleLenth  = "";

    private boolean xml = false;
    private String encoding = XMLPrinter.DEFAULT_ENCODING;
    private String dtdPrefix = XMLPrinter.DEFAULT_DTD_PREFIX;
    private String indentText;

    public String getStartincludes() {
        return startIncludes;
    }

    public void setStartincludes(String startIncludes) {
        this.startIncludes = startIncludes;
    }

    public String getStartexcludes() {
        return startExcludes;
    }

    public void setStartexcludes(String startExcludes) {
        this.startExcludes = startExcludes;
    }

    public String getPackagestartincludes() {
        return packageStartIncludes;
    }

    public void setPackagestartincludes(String packageStartIncludes) {
        this.packageStartIncludes = packageStartIncludes;
    }

    public String getPackagestartexcludes() {
        return packageStartExcludes;
    }

    public void setPackagestartexcludes(String packageStartExcludes) {
        this.packageStartExcludes = packageStartExcludes;
    }

    public String getClassstartincludes() {
        return classStartIncludes;
    }

    public void setClassstartincludes(String classStartIncludes) {
        this.classStartIncludes = classStartIncludes;
    }

    public String getClassstartexcludes() {
        return classStartExcludes;
    }

    public void setClassstartexcludes(String classStartExcludes) {
        this.classStartExcludes = classStartExcludes;
    }

    public String getFeaturestartincludes() {
        return featureStartIncludes;
    }

    public void setFeaturestartincludes(String featureStartIncludes) {
        this.featureStartIncludes = featureStartIncludes;
    }

    public String getFeaturestartexcludes() {
        return featureStartExcludes;
    }

    public void setFeaturestartexcludes(String featureStartExcludes) {
        this.featureStartExcludes = featureStartExcludes;
    }

    public Path createStartincludeslist() {
        if (startIncludesList == null) {
            startIncludesList = new Path(getProject());
        }

        return startIncludesList;
    }

    public Path getStartincludeslist() {
        return startIncludesList;
    }

    public Path createStartexcludeslist() {
        if (startExcludesList == null) {
            startExcludesList = new Path(getProject());
        }

        return startExcludesList;
    }

    public Path getStartexcludeslist() {
        return startExcludesList;
    }

    public String getMaximumcyclelength() {
        return maximumCycleLenth;
    }

    public void setMaximumcyclelength(String maximumCycleLenth) {
        this.maximumCycleLenth = maximumCycleLenth;
    }

    public boolean getXml() {
        return xml;
    }

    public void setXml(boolean xml) {
        this.xml = xml;
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

    protected void validateParameters() throws BuildException {
        super.validateParameters();

        if (hasStartRegularExpressionSwitches() && hasStartListSwitches()) {
            throw new BuildException("Cannot have start attributes for regular expressions and lists at the same time!");
        }
    }

    public void execute() throws BuildException {
        // first off, make sure that we've got what we need
        validateParameters();

        VerboseListener verboseListener = new VerboseListener(this);

        try {
            NodeFactory factory = new NodeFactory();

            for (String filename : getSrc().list()) {
                log("Reading graph from " + filename);

                if (filename.endsWith(".xml")) {
                    NodeLoader loader = new NodeLoader(factory, getValidate());
                    loader.addDependencyListener(verboseListener);
                    loader.load(filename);
                }
            }

            CycleDetector detector = new CycleDetector(getStartCriteria());

            if (getMaximumcyclelength() != null) {
                detector.setMaximumCycleLength(Integer.parseInt(getMaximumcyclelength()));
            }

            detector.traverseNodes(factory.getPackages().values());

            log("Saving dependency cycles to " + getDestfile().getAbsolutePath());

            PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));

            CyclePrinter printer;
            if (getXml()) {
                printer = new XMLCyclePrinter(out, getEncoding(), getDtdprefix());
            } else {
                printer = new TextCyclePrinter(out);
            }

            if (getIndenttext() != null) {
                printer.setIndentText(getIndenttext());
            }

            printer.visitCycles(detector.getCycles());

            out.close();
        } catch (SAXException ex) {
            throw new BuildException(ex);
        } catch (ParserConfigurationException ex) {
            throw new BuildException(ex);
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    protected SelectionCriteria getStartCriteria() throws BuildException {
        SelectionCriteria result = new ComprehensiveSelectionCriteria();

        if (hasStartRegularExpressionSwitches()) {
            result = createRegularExpressionStartCriteria();
        } else if (hasStartListSwitches()) {
            result = createCollectionSelectionCriteria(getStartincludeslist(), getStartexcludeslist());
        }

        return result;
    }

    protected RegularExpressionSelectionCriteria createRegularExpressionStartCriteria() throws BuildException {
        RegularExpressionSelectionCriteria result = new RegularExpressionSelectionCriteria();

        result.setGlobalIncludes(getStartincludes());
        result.setGlobalExcludes(getStartexcludes());
        result.setPackageIncludes(getPackagestartincludes());
        result.setPackageExcludes(getPackagestartexcludes());
        result.setClassIncludes(getClassstartincludes());
        result.setClassExcludes(getClassstartexcludes());
        result.setFeatureIncludes(getFeaturestartincludes());
        result.setFeatureExcludes(getFeaturestartexcludes());

        return result;
    }

    private CollectionSelectionCriteria createCollectionSelectionCriteria(Path includes, Path excludes) {
        return new CollectionSelectionCriteria(loadCollection(includes), loadCollection(excludes));
    }

    private boolean hasStartRegularExpressionSwitches() {
        return
                !getStartincludes().equals("//") ||
                !getStartexcludes().equals("") ||
                !getPackagestartincludes().equals("") ||
                !getPackagestartexcludes().equals("") ||
                !getClassstartincludes().equals("") ||
                !getClassstartexcludes().equals("") ||
                !getFeaturestartincludes().equals("") ||
                !getFeaturestartexcludes().equals("");
    }

    private boolean hasStartListSwitches() {
        return
                getStartincludeslist() != null ||
                getStartexcludeslist() != null;
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
