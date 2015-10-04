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

import javax.xml.parsers.*;

import org.apache.tools.ant.*;

import org.xml.sax.*;

import com.jeantessier.dependency.*;

public class DependencyClosure extends GraphTask {
    private String  startIncludes        = "//";
    private String  startExcludes        = "";
    private String  packageStartIncludes = "";
    private String  packageStartExcludes = "";
    private String  classStartIncludes   = "";
    private String  classStartExcludes   = "";
    private String  featureStartIncludes = "";
    private String  featureStartExcludes = "";
    private String  stopIncludes         = "";
    private String  stopExcludes         = "";
    private String  packageStopIncludes  = "";
    private String  packageStopExcludes  = "";
    private String  classStopIncludes    = "";
    private String  classStopExcludes    = "";
    private String  featureStopIncludes  = "";
    private String  featureStopExcludes  = "";

    private String  maximumInboundDepth  = "";
    private String  maximumOutboundDepth = "";
    private boolean xml                  = false;
    private String  encoding             = XMLPrinter.DEFAULT_ENCODING;
    private String  dtdPrefix            = XMLPrinter.DEFAULT_DTD_PREFIX;
    private String  indentText;

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

    public String getStopincludes() {
        return stopIncludes;
    }

    public void setStopincludes(String stopIncludes) {
        this.stopIncludes = stopIncludes;
    }
    
    public String getStopexcludes() {
        return stopExcludes;
    }

    public void setStopexcludes(String stopExcludes) {
        this.stopExcludes = stopExcludes;
    }
    
    public String getPackagestopincludes() {
        return packageStopIncludes;
    }

    public void setPackagestopincludes(String packageStopIncludes) {
        this.packageStopIncludes = packageStopIncludes;
    }
    
    public String getPackagestopexcludes() {
        return packageStopExcludes;
    }

    public void setPackagestopexcludes(String packageStopExcludes) {
        this.packageStopExcludes = packageStopExcludes;
    }
    
    public String getClassstopincludes() {
        return classStopIncludes;
    }

    public void setClassstopincludes(String classStopIncludes) {
        this.classStopIncludes = classStopIncludes;
    }
    
    public String getClassstopexcludes() {
        return classStopExcludes;
    }

    public void setClassstopexcludes(String classStopExcludes) {
        this.classStopExcludes = classStopExcludes;
    }
    
    public String getFeaturestopincludes() {
        return featureStopIncludes;
    }

    public void setFeaturestopincludes(String featureStopIncludes) {
        this.featureStopIncludes = featureStopIncludes;
    }
    
    public String getFeaturestopexcludes() {
        return featureStopExcludes;
    }

    public void setFeaturestopexcludes(String featureStopExcludes) {
        this.featureStopExcludes = featureStopExcludes;
    }

    public String getMaximuminbounddepth() {
        return maximumInboundDepth;
    }

    public void setMaximuminbounddepth(String maximumInboundDepth) {
        this.maximumInboundDepth = maximumInboundDepth;
    }
    
    public String getMaximumoutbounddepth() {
        return maximumOutboundDepth;
    }

    public void setMaximumoutbounddepth(String maximumOutboundDepth) {
        this.maximumOutboundDepth = maximumOutboundDepth;
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

            TransitiveClosure selector = new TransitiveClosure(getStartCriteria(), getStopCriteria());

            try {
                if (getMaximuminbounddepth() != null) {
                    selector.setMaximumInboundDepth(Long.parseLong(getMaximuminbounddepth()));
                }
            } catch (NumberFormatException ex) {
                selector.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
            }
            
            try {
                if (getMaximumoutbounddepth() != null) {
                    selector.setMaximumOutboundDepth(Long.parseLong(getMaximumoutbounddepth()));
                }
            } catch (NumberFormatException ex) {
                selector.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
            }
                
            selector.traverseNodes(factory.getPackages().values());
        
            log("Saving dependency graph to " + getDestfile().getAbsolutePath());
        
            PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));

            Printer printer;
            if (getXml()) {
                printer = new XMLPrinter(out, getEncoding(), getDtdprefix());
            } else {
                printer = new TextPrinter(out);
            }
                
            if (getIndenttext() != null) {
                printer.setIndentText(getIndenttext());
            }
                
            printer.traverseNodes(selector.getFactory().getPackages().values());
                
            out.close();
        } catch (SAXException ex) {
            throw new BuildException(ex);
        } catch (ParserConfigurationException ex) {
            throw new BuildException(ex);
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    private SelectionCriteria getStartCriteria() throws BuildException {
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

    private SelectionCriteria getStopCriteria() throws BuildException {
        RegularExpressionSelectionCriteria result = new RegularExpressionSelectionCriteria();

        result.setGlobalIncludes(getStopincludes());
        result.setGlobalExcludes(getStopexcludes());
        result.setPackageIncludes(getPackagestopincludes());
        result.setPackageExcludes(getPackagestopexcludes());
        result.setClassIncludes(getClassstopincludes());
        result.setClassExcludes(getClassstopexcludes());
        result.setFeatureIncludes(getFeaturestopincludes());
        result.setFeatureExcludes(getFeaturestopexcludes());

        return result;
    }
}
