/*
 *  Copyright (c) 2001-2006, Jean Tessier
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
    private String scopeIncludes = "//";
    private String scopeExcludes = "";
    private boolean packageScope;
    private String packageScopeIncludes = "";
    private String packageScopeExcludes = "";
    private boolean classScope;
    private String classScopeIncludes = "";
    private String classScopeExcludes = "";
    private boolean featureScope;
    private String featureScopeIncludes = "";
    private String featureScopeExcludes = "";

    private Path scopeIncludesList;
    private Path scopeExcludesList;

    private String  maximumCycleLenth  = "";

    private boolean xml                  = false;
    private String  encoding             = XMLPrinter.DEFAULT_ENCODING;
    private String  dtdPrefix            = XMLPrinter.DEFAULT_DTD_PREFIX;
    private String  indentText;

    public String getScopeincludes() {
        return scopeIncludes;
    }

    public void setScopeincludes(String scopeIncludes) {
        this.scopeIncludes = scopeIncludes;
    }

    public String getScopeexcludes() {
        return scopeExcludes;
    }

    public void setScopeexcludes(String scopeExcludes) {
        this.scopeExcludes = scopeExcludes;
    }

    public boolean getPackagescope() {
        return packageScope;
    }

    public void setPackagescope(boolean packageScope) {
        this.packageScope = packageScope;
    }

    public String getPackagescopeincludes() {
        return packageScopeIncludes;
    }

    public void setPackagescopeincludes(String packageScopeIncludes) {
        this.packageScopeIncludes = packageScopeIncludes;
    }

    public String getPackagescopeexcludes() {
        return packageScopeExcludes;
    }

    public void setPackagescopeexcludes(String packageScopeExcludes) {
        this.packageScopeExcludes = packageScopeExcludes;
    }

    public boolean getClassscope() {
        return classScope;
    }

    public void setClassscope(boolean classScope) {
        this.classScope = classScope;
    }

    public String getClassscopeincludes() {
        return classScopeIncludes;
    }

    public void setClassscopeincludes(String classScopeIncludes) {
        this.classScopeIncludes = classScopeIncludes;
    }

    public String getClassscopeexcludes() {
        return classScopeExcludes;
    }

    public void setClassscopeexcludes(String classScopeExcludes) {
        this.classScopeExcludes = classScopeExcludes;
    }

    public boolean getFeaturescope() {
        return featureScope;
    }

    public void setFeaturescope(boolean featureScope) {
        this.featureScope = featureScope;
    }

    public String getFeaturescopeincludes() {
        return featureScopeIncludes;
    }

    public void setFeaturescopeincludes(String featureScopeIncludes) {
        this.featureScopeIncludes = featureScopeIncludes;
    }

    public String getFeaturescopeexcludes() {
        return featureScopeExcludes;
    }

    public void setFeaturescopeexcludes(String featureScopeExcludes) {
        this.featureScopeExcludes = featureScopeExcludes;
    }

    public void setAll(boolean value) {
        setPackagescope(value);
        setClassscope(value);
        setFeaturescope(value);
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

        if (hasScopeRegularExpressionSwitches() && hasScopeListSwitches()) {
            throw new BuildException("Cannot have scope attributes for regular expressions and lists at the same time!");
        }
    }

    public void execute() throws BuildException {
        // first off, make sure that we've got what we need
        validateParameters();

        VerboseListener verboseListener = new VerboseListener(this);

        try {
            NodeFactory factory = new NodeFactory();

            String[] filenames = getSrc().list();
            for (int i=0; i<filenames.length; i++) {
                log("Reading graph from " + filenames[i]);

                if (filenames[i].endsWith(".xml")) {
                    NodeLoader loader = new NodeLoader(factory, getValidate());
                    loader.addDependencyListener(verboseListener);
                    loader.load(filenames[i]);
                }
            }

            CycleDetector detector = new CycleDetector(getScopeCriteria());

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

    protected SelectionCriteria getScopeCriteria() throws BuildException {
        SelectionCriteria result = new ComprehensiveSelectionCriteria();

        if (hasScopeRegularExpressionSwitches()) {
            result = createRegularExpressionScopeCriteria();
        } else if (hasScopeListSwitches()) {
            result = createCollectionSelectionCriteria(getScopeincludeslist(), getScopeexcludeslist());
        }

        return result;
    }

    protected RegularExpressionSelectionCriteria createRegularExpressionScopeCriteria() throws BuildException {
        RegularExpressionSelectionCriteria result = new RegularExpressionSelectionCriteria();

        result.setMatchingPackages(getPackagescope());
        result.setMatchingClasses(getClassscope());
        result.setMatchingFeatures(getFeaturescope());

        result.setGlobalIncludes(getScopeincludes());
        result.setGlobalExcludes(getScopeexcludes());
        result.setPackageIncludes(getPackagescopeincludes());
        result.setPackageExcludes(getPackagescopeexcludes());
        result.setClassIncludes(getClassscopeincludes());
        result.setClassExcludes(getClassscopeexcludes());
        result.setFeatureIncludes(getFeaturescopeincludes());
        result.setFeatureExcludes(getFeaturescopeexcludes());

        return result;
    }

    private CollectionSelectionCriteria createCollectionSelectionCriteria(Path includes, Path excludes) {
        return new CollectionSelectionCriteria(loadCollection(includes), loadCollection(excludes));
    }

    private boolean hasScopeRegularExpressionSwitches() {
        return
                !getScopeincludes().equals("//") ||
                !getScopeexcludes().equals("") ||
                getPackagescope() ||
                !getPackagescopeincludes().equals("") ||
                !getPackagescopeexcludes().equals("") ||
                getClassscope() ||
                !getClassscopeincludes().equals("") ||
                !getClassscopeexcludes().equals("") ||
                getFeaturescope() ||
                !getFeaturescopeincludes().equals("") ||
                !getFeaturescopeexcludes().equals("");
    }

    private boolean hasScopeListSwitches() {
        return
                getScopeincludeslist() != null ||
                getScopeexcludeslist() != null;
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
