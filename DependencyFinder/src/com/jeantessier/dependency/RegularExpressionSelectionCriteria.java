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

package com.jeantessier.dependency;

import java.util.*;

import org.apache.log4j.*;
import org.apache.oro.text.perl.*;

import com.jeantessier.text.*;

public class RegularExpressionSelectionCriteria implements SelectionCriteria {
	private Perl5Util perl = new Perl5Util(new MaximumCapacityPatternCache());

	private List    global_includes  = new LinkedList();
	private List    global_excludes  = new LinkedList();
	private boolean match_package    = true;
	private List    package_includes = new LinkedList();
	private List    package_excludes = new LinkedList();
	private boolean match_class      = true;
	private List    class_includes   = new LinkedList();
	private List    class_excludes   = new LinkedList();
	private boolean match_feature    = true;
	private List    feature_includes = new LinkedList();
	private List    feature_excludes = new LinkedList();
	
	public RegularExpressionSelectionCriteria() {
		GlobalIncludes().add("//");
	}
	
	public List GlobalIncludes() {
		return global_includes;
	}

	public void GlobalIncludes(String global_includes) {
		GlobalIncludes(ParseRE(global_includes));
	}
	
	public void GlobalIncludes(List global_includes) {
		this.global_includes = global_includes;
	}

	public List GlobalExcludes() {
		return global_excludes;
	}

	public void GlobalExcludes(String global_excludes) {
		GlobalExcludes(ParseRE(global_excludes));
	}

	public void GlobalExcludes(List global_excludes) {
		this.global_excludes = global_excludes;
	}

	public boolean doesPackageMatching() {
		return match_package;
	}

	public void MatchPackage(boolean match_package) {
		this.match_package = match_package;
	}

	public List PackageIncludes() {
		return package_includes;
	}

	public void PackageIncludes(String package_includes) {
		PackageIncludes(ParseRE(package_includes));
	}

	public void PackageIncludes(List package_includes) {
		this.package_includes = package_includes;
	}

	public List PackageExcludes() {
		return package_excludes;
	}

	public void PackageExcludes(String package_excludes) {
		PackageExcludes(ParseRE(package_excludes));
	}

	public void PackageExcludes(List package_excludes) {
		this.package_excludes = package_excludes;
	}

	public boolean doesClassMatching() {
		return match_class;
	}

	public void MatchClass(boolean match_class) {
		this.match_class = match_class;
	}

	public List ClassIncludes() {
		return class_includes;
	}

	public void ClassIncludes(String class_includes) {
		ClassIncludes(ParseRE(class_includes));
	}

	public void ClassIncludes(List class_includes) {
		this.class_includes = class_includes;
	}

	public List ClassExcludes() {
		return class_excludes;
	}

	public void ClassExcludes(String class_excludes) {
		ClassExcludes(ParseRE(class_excludes));
	}

	public void ClassExcludes(List class_excludes) {
		this.class_excludes = class_excludes;
	}

	public boolean doesFeatureMatching() {
		return match_feature;
	}

	public void MatchFeature(boolean match_feature) {
		this.match_feature = match_feature;
	}

	public List FeatureIncludes() {
		return feature_includes;
	}

	public void FeatureIncludes(String feature_includes) {
		FeatureIncludes(ParseRE(feature_includes));
	}

	public void FeatureIncludes(List feature_includes) {
		this.feature_includes = feature_includes;
	}

	public List FeatureExcludes() {
		return feature_excludes;
	}

	public void FeatureExcludes(String feature_excludes) {
		FeatureExcludes(ParseRE(feature_excludes));
	}

	public void FeatureExcludes(List feature_excludes) {
		this.feature_excludes = feature_excludes;
	}

	public boolean matches(PackageNode node) {
		return doesPackageMatching() && matchesPackageName(node.getName());
	}
	
	public boolean matches(ClassNode node) {
		return doesClassMatching() && matchesClassName(node.getName());
	}
	
	public boolean matches(FeatureNode node) {
		return doesFeatureMatching() && matchesFeatureName(node.getName());
	}

	public boolean matchesPackageName(String name) {
		return Match(GlobalIncludes(), PackageIncludes(), name) &&
			!Match(GlobalExcludes(), PackageExcludes(), name);
	}

	public boolean matchesClassName(String name) {
		return Match(GlobalIncludes(), ClassIncludes(), name) &&
			!Match(GlobalExcludes(), ClassExcludes(), name);
	}

	public boolean matchesFeatureName(String name) {
		return Match(GlobalIncludes(), FeatureIncludes(), name) &&
			!Match(GlobalExcludes(), FeatureExcludes(), name);
	}

	private boolean Match(List global_regular_expressions, List regular_expressions, String name) {
		boolean  found = false;
		Iterator i;

		i = global_regular_expressions.iterator();
		while (!found && i.hasNext()) {
			String condition = (String) i.next();
			found = perl.match(condition, name);
		}

		i = regular_expressions.iterator();
		while (!found && i.hasNext()) {
			String condition = (String) i.next();
			found = perl.match(condition, name);
		}

		return found;
	}

	// Should be private, but left at package-level for the unit tests.
	protected static List ParseRE(String re) {
		List result = new LinkedList();

		Logger logger = Logger.getLogger(RegularExpressionSelectionCriteria.class);
		logger.debug("ParseRE \"" + re + "\"");

		int length = re.length();
		int start  = 0;
		int stop   = -1;

		while (start < length && stop < length) {
			String separator = null;
			
			// Locate begining & determine separator
			while (start < length && stop < start) {
				if (re.charAt(start) == 'm' && (start + 1) < length) {
					separator = re.substring(start + 1, start + 2);
					stop = start + 2;
				} else if (re.charAt(start) == '/') {
					separator = "/";
					stop = start + 1;
				} else {
					start++;
				}
			}

			logger.debug("start is " + start);
			logger.debug("separator is " + separator);
			
			// Locate end
			while (stop < length && start < stop) {
				stop = re.indexOf(separator, stop);
				logger.debug("indexOf() is " + stop);
				
				if (stop == -1 || re.charAt(stop - 1) != '\\') {

					if (stop == -1) {
						stop = length;
					} else {
						// Look for modifiers
						stop++;
						while (stop < length && (re.charAt(stop) == 'g' ||
												 re.charAt(stop) == 'i' ||
												 re.charAt(stop) == 'm' ||
												 re.charAt(stop) == 'o' ||
												 re.charAt(stop) == 's' ||
												 re.charAt(stop) == 'x')) {
							stop++;
						}
					}

					logger.debug("stop is " + stop);

					// Add candidate
					logger.debug("candidate is \"" + re.substring(start, stop) + "\"");
					result.add(re.substring(start, stop));
			
					// Move start
					start = stop + 1;
				} else {
					stop++;
				}
			}
		}
		
		return result;
	}
}
