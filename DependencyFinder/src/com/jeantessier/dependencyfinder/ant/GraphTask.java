/*
 *  Copyright (c) 2001-2003, Jean Tessier
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
	private String  scope_includes = "//";
	private String  scope_excludes = "";
	private boolean package_scope;
	private String  package_scope_includes = "";
	private String  package_scope_excludes = "";
	private boolean class_scope;
	private String  class_scope_includes = "";
	private String  class_scope_excludes = "";
	private boolean feature_scope;
	private String  feature_scope_includes = "";
	private String  feature_scope_excludes = "";
	private String  filter_includes = "//";
	private String  filter_excludes = "";
	private boolean package_filter;
	private String  package_filter_includes = "";
	private String  package_filter_excludes = "";
	private boolean class_filter;
	private String  class_filter_includes = "";
	private String  class_filter_excludes = "";
	private boolean feature_filter;
	private String  feature_filter_includes = "";
	private String  feature_filter_excludes = "";

	private boolean validate   = false;
	private Path    src;
	private File    destfile;

	public String getScopeincludes() {
		return scope_includes;
	}

	public void setScopeincludes(String scope_includes) {
		this.scope_includes = scope_includes;
	}
	
	public String getScopeexcludes() {
		return scope_excludes;
	}

	public void setScopeexcludes(String scope_excludes) {
		this.scope_excludes = scope_excludes;
	}

	public boolean getPackagescope() {
		return package_scope;
	}

	public void setPackagescope(boolean package_scope) {
		this.package_scope = package_scope;
	}
	
	public String getPackagescopeincludes() {
		return package_scope_includes;
	}

	public void setPackagescopeincludes(String package_scope_includes) {
		this.package_scope_includes = package_scope_includes;
	}
	
	public String getPackagescopeexcludes() {
		return package_scope_excludes;
	}

	public void setPackagescopeexcludes(String package_scope_excludes) {
		this.package_scope_excludes = package_scope_excludes;
	}

	public boolean getClassscope() {
		return class_scope;
	}

	public void setClassscope(boolean class_scope) {
		this.class_scope = class_scope;
	}
	
	public String getClassscopeincludes() {
		return class_scope_includes;
	}

	public void setClassscopeincludes(String class_scope_includes) {
		this.class_scope_includes = class_scope_includes;
	}
	
	public String getClassscopeexcludes() {
		return class_scope_excludes;
	}

	public void setClassscopeexcludes(String class_scope_excludes) {
		this.class_scope_excludes = class_scope_excludes;
	}

	public boolean getFeaturescope() {
		return feature_scope;
	}

	public void setFeaturescope(boolean feature_scope) {
		this.feature_scope = feature_scope;
	}
	
	public String getFeaturescopeincludes() {
		return feature_scope_includes;
	}

	public void setFeaturescopeincludes(String feature_scope_includes) {
		this.feature_scope_includes = feature_scope_includes;
	}
	
	public String getFeaturescopeexcludes() {
		return feature_scope_excludes;
	}

	public void setFeaturescopeexcludes(String feature_scope_excludes) {
		this.feature_scope_excludes = feature_scope_excludes;
	}

	public String getFilterincludes() {
		return filter_includes;
	}

	public void setFilterincludes(String filter_includes) {
		this.filter_includes = filter_includes;
	}
	
	public String getFilterexcludes() {
		return filter_excludes;
	}

	public void setFilterexcludes(String filter_excludes) {
		this.filter_excludes = filter_excludes;
	}

	public boolean getPackagefilter() {
		return package_filter;
	}

	public void setPackagefilter(boolean package_filter) {
		this.package_filter = package_filter;
	}
	
	public String getPackagefilterincludes() {
		return package_filter_includes;
	}

	public void setPackagefilterincludes(String package_filter_includes) {
		this.package_filter_includes = package_filter_includes;
	}
	
	public String getPackagefilterexcludes() {
		return package_filter_excludes;
	}

	public void setPackagefilterexcludes(String package_filter_excludes) {
		this.package_filter_excludes = package_filter_excludes;
	}

	public boolean getClassfilter() {
		return class_filter;
	}

	public void setClassfilter(boolean class_filter) {
		this.class_filter = class_filter;
	}
	
	public String getClassfilterincludes() {
		return class_filter_includes;
	}

	public void setClassfilterincludes(String class_filter_includes) {
		this.class_filter_includes = class_filter_includes;
	}
	
	public String getClassfilterexcludes() {
		return class_filter_excludes;
	}

	public void setClassfilterexcludes(String class_filter_excludes) {
		this.class_filter_excludes = class_filter_excludes;
	}

	public boolean getFeaturefilter() {
		return feature_filter;
	}

	public void setFeaturefilter(boolean feature_filter) {
		this.feature_filter = feature_filter;
	}
	
	public String getFeaturefilterincludes() {
		return feature_filter_includes;
	}

	public void setFeaturefilterincludes(String feature_filter_includes) {
		this.feature_filter_includes = feature_filter_includes;
	}
	
	public String getFeaturefilterexcludes() {
		return feature_filter_excludes;
	}

	public void setFeaturefilterexcludes(String feature_filter_excludes) {
		this.feature_filter_excludes = feature_filter_excludes;
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

	protected void CheckParameters() throws BuildException {
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

	protected SelectionCriteria ScopeCriteria() throws BuildException {
		RegularExpressionSelectionCriteria result = new RegularExpressionSelectionCriteria();

		result.MatchPackage(getPackagescope());
		result.MatchClass(getClassscope());
		result.MatchFeature(getFeaturescope());

		result.GlobalIncludes(getScopeincludes());
		result.GlobalExcludes(getScopeexcludes());
		result.PackageIncludes(getPackagescopeincludes());
		result.PackageExcludes(getPackagescopeexcludes());
		result.ClassIncludes(getClassscopeincludes());
		result.ClassExcludes(getClassscopeexcludes());
		result.FeatureIncludes(getFeaturescopeincludes());
		result.FeatureExcludes(getFeaturescopeexcludes());

		return result;
	}

	protected SelectionCriteria FilterCriteria() throws BuildException {
		RegularExpressionSelectionCriteria result = new RegularExpressionSelectionCriteria();

		result.MatchPackage(getPackagefilter());
		result.MatchClass(getClassfilter());
		result.MatchFeature(getFeaturefilter());

		result.GlobalIncludes(getFilterincludes());
		result.GlobalExcludes(getFilterexcludes());
		result.PackageIncludes(getPackagefilterincludes());
		result.PackageExcludes(getPackagefilterexcludes());
		result.ClassIncludes(getClassfilterincludes());
		result.ClassExcludes(getClassfilterexcludes());
		result.FeatureIncludes(getFeaturefilterincludes());
		result.FeatureExcludes(getFeaturefilterexcludes());

		return result;
	}
	
	protected TraversalStrategy Strategy() throws BuildException {
		return new SelectiveTraversalStrategy(ScopeCriteria(), FilterCriteria());
	}
}
