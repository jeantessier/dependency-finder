/*
 *  Copyright (c) 2001-2004, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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

import com.jeantessier.dependency.*;

public abstract class GraphTask extends Task {
	private String  scopeIncludes = "//";
	private String  scopeExcludes = "";
	private boolean packageScope;
	private String  packageScopeIncludes = "";
	private String  packageScopeExcludes = "";
	private boolean classScope;
	private String  classScopeIncludes = "";
	private String  classScopeExcludes = "";
	private boolean featureScope;
	private String  featureScopeIncludes = "";
	private String  featureScopeExcludes = "";
	private String  filterIncludes = "//";
	private String  filterExcludes = "";
	private boolean packageFilter;
	private String  packageFilterIncludes = "";
	private String  packageFilterExcludes = "";
	private boolean classFilter;
	private String  classFilterIncludes = "";
	private String  classFilterExcludes = "";
	private boolean featureFilter;
	private String  featureFilterIncludes = "";
	private String  featureFilterExcludes = "";

	private boolean validate = false;
	private Path    src;
	private File    destfile;

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

	public void setAll(boolean value) {
		setPackagescope(value);
		setClassscope(value);
		setFeaturescope(value);
		setPackagefilter(value);
		setClassfilter(value);
		setFeaturefilter(value);
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

	public boolean getValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}
	
	public Path createSrc() {
		if (src == null) {
			src = new Path(getProject());
		}

		return src;
	}
	
	public Path getSrc() {
		return src;
	}

	public Path getSrcfile() {
		return src;
	}
	
	public void setSrcfile(Path srcfile) {
		if (src == null) {
			src = srcfile;
		} else {
			src.append(srcfile);
		}
	}

	public File getDestfile() {
		return destfile;
	}
	
	public void setDestfile(File destfile) {
		this.destfile = destfile;
	}

	protected void validateParameters() throws BuildException {
		if (getSrcfile() == null) {
			throw new BuildException("src or srcfile must be set!");
		}
		
		if (getSrc().size() == 0) {
			throw new BuildException("src and srcfile are both empty!");
		}

		if (getDestfile() == null) {
			throw new BuildException("destfile must be set!");
		}
	}

	protected SelectionCriteria getScopeCriteria() throws BuildException {
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

	protected SelectionCriteria getFilterCriteria() throws BuildException {
		RegularExpressionSelectionCriteria result = new RegularExpressionSelectionCriteria();

		result.setMatchingPackages(getPackagefilter());
		result.setMatchingClasses(getClassfilter());
		result.setMatchingFeatures(getFeaturefilter());

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
	
	protected TraversalStrategy getStrategy() throws BuildException {
		return new SelectiveTraversalStrategy(getScopeCriteria(), getFilterCriteria());
	}
}
