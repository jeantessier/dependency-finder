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

import com.jeantessier.dependency.*;
import org.apache.logging.log4j.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class DependencyReporter extends GraphTask {
    private String scopeIncludes = "//";
    private String scopeExcludes = "";
    private boolean packageScope = false;
    private String packageScopeIncludes = "";
    private String packageScopeExcludes = "";
    private boolean classScope = false;
    private String classScopeIncludes = "";
    private String classScopeExcludes = "";
    private boolean featureScope = false;
    private String featureScopeIncludes = "";
    private String featureScopeExcludes = "";
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

    private Path scopeIncludesList;
    private Path scopeExcludesList;
    private Path filterIncludesList;
    private Path filterExcludesList;

    private boolean showInbounds = false;
    private boolean showOutbounds = false;
    private boolean showEmptyNodes = false;

    private boolean html = false;
    private boolean json = false;
    private boolean text = false;
    private boolean xml = false;
    private boolean yaml = false;

    private boolean minimize = false;
    private boolean maximize = false;
    private boolean copyOnly = false;
    private boolean includeFilterNodes = false;

    private String urlFormat = HTMLPrinter.DEFAULT_URL_FORMAT;

    private String encoding = XMLPrinter.DEFAULT_ENCODING;
    private String dtdPrefix = XMLPrinter.DEFAULT_DTD_PREFIX;
    private String indentText;

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

    public void setP2p(boolean value) {
        setPackagescope(value);
        setPackagefilter(value);
    }

    public void setC2p(boolean value) {
        setClassscope(value);
        setPackagefilter(value);
    }

    public void setC2c(boolean value) {
        setClassscope(value);
        setClassfilter(value);
    }

    public void setF2f(boolean value) {
        setFeaturescope(value);
        setFeaturefilter(value);
    }

    public void setIncludes(String value) {
        setScopeincludes(value);
        setFilterincludes(value);
    }

    public void setExcludes(String value) {
        setScopeexcludes(value);
        setFilterexcludes(value);
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

    public boolean getShowinbounds() {
        return showInbounds;
    }

    public void setShowinbounds(boolean showInbounds) {
        this.showInbounds = showInbounds;
    }

    public boolean getShowoutbounds() {
        return showOutbounds;
    }

    public void setShowoutbounds(boolean showOutbounds) {
        this.showOutbounds = showOutbounds;
    }

    public boolean getShowemptynodes() {
        return showEmptyNodes;
    }

    public void setShowemptynodes(boolean showEmptyNodes) {
        this.showEmptyNodes = showEmptyNodes;
    }

    public void setShowall(boolean showAll) {
        setShowinbounds(showAll);
        setShowoutbounds(showAll);
        setShowemptynodes(showAll);
    }

    public boolean getHtml() {
        return html;
    }

    public void setHtml(boolean html) {
        this.html = html;
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

    public boolean getCopyonly() {
        return copyOnly;
    }

    public void setCopyonly(boolean copyOnly) {
        this.copyOnly = copyOnly;
    }

    public boolean getIncludefilternodes() {
        return includeFilterNodes;
    }

    public void setIncludefilternodes(boolean includeFilterNodes) {
        this.includeFilterNodes = includeFilterNodes;
    }

    public String getUrlFormat() {
        return urlFormat;
    }

    public void setUrlFormat(String urlFormat) {
        this.urlFormat = urlFormat;
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

    protected void validateParameters() throws BuildException {
        super.validateParameters();

        if (hasScopeRegularExpressionSwitches() && hasScopeListSwitches()) {
            throw new BuildException("Cannot have scope attributes for regular expressions and lists at the same time!");
        }

        if (hasFilterRegularExpressionSwitches() && hasFilterListSwitches()) {
            throw new BuildException("Cannot have filter attributes for regular expressions and lists at the same time!");
        }
    }

    public void execute() throws BuildException {
        // first off, make sure that we've got what we need
        validateParameters();

        VerboseListener verboseListener = new VerboseListener(this);

        try {
            GraphCopier copier;
            if (getCopyonly() || getMaximize()) {
                copier = new GraphCopier(getStrategy());
            } else {
                copier = new GraphSummarizer(getScopeCriteria(), getFilterCriteria());
            }

            for (String filename : getSrc().list()) {
                log("Reading graph from " + filename);

                Collection<PackageNode> packages = Collections.emptyList();

                if (filename.endsWith(".xml")) {
                    NodeLoader loader = new NodeLoader(getValidate());
                    loader.addDependencyListener(verboseListener);
                    packages = loader.load(filename).getPackages().values();
                }

                if (getMaximize()) {
                    log("Maximizing ...");
                    new LinkMaximizer().traverseNodes(packages);
                } else if (getMinimize()) {
                    log("Minimizing ...");
                    new LinkMinimizer().traverseNodes(packages);
                }

                copier.traverseNodes(packages);
            }

            log("Saving dependency graph to " + getDestfile().getAbsolutePath());

            PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));

            Printer printer;
            if (getHtml()) {
                printer = new HTMLPrinter(out, getUrlFormat());
            } else if (getJson()) {
                printer = new JSONPrinter(out);
            } else if (getXml()) {
                printer = new XMLPrinter(out, getEncoding(), getDtdprefix());
            } else if (getYaml()) {
                printer = new YAMLPrinter(out);
            } else {
                printer = new TextPrinter(out);
            }

            if (getIndenttext() != null) {
                printer.setIndentText(getIndenttext());
            }

            if (getShowinbounds() || getShowoutbounds() || getShowemptynodes()) {
                printer.setShowInbounds(getShowinbounds());
                printer.setShowOutbounds(getShowoutbounds());
                printer.setShowEmptyNodes(getShowemptynodes());
            }

            var packages = copier.getScopeFactory().getPackages().values();

            if (getIncludefilternodes()) {
                var nodeFactory = new NodeFactory();
                new GraphCopier(nodeFactory).traverseNodes(packages);
                packages = nodeFactory.getPackages().values();
            }

            printer.traverseNodes(packages);

            out.close();
        } catch (SAXException | ParserConfigurationException | IOException ex) {
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

    protected SelectionCriteria getFilterCriteria() throws BuildException {
        SelectionCriteria result = new ComprehensiveSelectionCriteria();

        if (hasFilterRegularExpressionSwitches()) {
            result = createRegularExpressionFilterCriteria();
        } else if (hasFilterListSwitches()) {
            result = createCollectionSelectionCriteria(getFilterincludeslist(), getFilterexcludeslist());
        }

        return result;
    }

    protected RegularExpressionSelectionCriteria createRegularExpressionScopeCriteria() throws BuildException {
        RegularExpressionSelectionCriteria result = new RegularExpressionSelectionCriteria();

        if (getPackagescope() || getClassscope() || getFeaturescope()) {
            result.setMatchingPackages(getPackagescope());
            result.setMatchingClasses(getClassscope());
            result.setMatchingFeatures(getFeaturescope());
        }

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

    private TraversalStrategy getStrategy() throws BuildException {
        return new SelectiveTraversalStrategy(getScopeCriteria(), getFilterCriteria());
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
