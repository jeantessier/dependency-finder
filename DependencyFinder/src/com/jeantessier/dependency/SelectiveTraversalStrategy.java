/*
 *  Copyright (c) 2001-2002, Jean Tessier
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
 *  	* Neither the name of the Jean Tessier nor the names of his contributors
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

public class SelectiveTraversalStrategy implements TraversalStrategy {
	private static final Perl5Util perl = new Perl5Util();
	
	protected static Perl5Util Perl() {
		return perl;
	}

	protected static List ParseRE(String re) {
		List result = new LinkedList();

		Logger logger = Logger.getLogger(SelectiveTraversalStrategy.class);
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
	
	private boolean pre_outbound_traversal  = true;
	private boolean pre_inbound_traversal   = true;
	private boolean post_outbound_traversal = false;
	private boolean post_inbound_traversal  = false;

	private List    scope_includes          = new LinkedList();
	private List    scope_excludes          = new LinkedList();
	private boolean package_scope           = true;
	private List    package_scope_includes  = new LinkedList();
	private List    package_scope_excludes  = new LinkedList();
	private boolean class_scope             = true;
	private List    class_scope_includes    = new LinkedList();
	private List    class_scope_excludes    = new LinkedList();
	private boolean feature_scope           = true;
	private List    feature_scope_includes  = new LinkedList();
	private List    feature_scope_excludes  = new LinkedList();

	private List    filter_includes         = new LinkedList();
	private List    filter_excludes         = new LinkedList();
	private boolean package_filter          = true;
	private List    package_filter_includes = new LinkedList();
	private List    package_filter_excludes = new LinkedList();
	private boolean class_filter            = true;
	private List    class_filter_includes   = new LinkedList();
	private List    class_filter_excludes   = new LinkedList();
	private boolean feature_filter          = true;
	private List    feature_filter_includes = new LinkedList();
	private List    feature_filter_excludes = new LinkedList();
	
	public SelectiveTraversalStrategy() {
		ScopeIncludes().add("//");
		FilterIncludes().add("//");
	}

	public boolean PreOutboundTraversal() {
		return pre_outbound_traversal;
	}

	public void PreOutboundTraversal(boolean pre_outbound_traversal) {
		this.pre_outbound_traversal = pre_outbound_traversal;
	}

	public boolean PreInboundTraversal() {
		return pre_inbound_traversal;
	}

	public void PreInboundTraversal(boolean pre_inbound_traversal) {
		this.pre_inbound_traversal = pre_inbound_traversal;
	}

	public boolean PostOutboundTraversal() {
		return post_outbound_traversal;
	}

	public void PostOutboundTraversal(boolean post_outbound_traversal) {
		this.post_outbound_traversal = post_outbound_traversal;
	}

	public boolean PostInboundTraversal() {
		return post_inbound_traversal;
	}

	public void PostInboundTraversal(boolean post_inbound_traversal) {
		this.post_inbound_traversal = post_inbound_traversal;
	}

	public List ScopeIncludes() {
		return scope_includes;
	}

	public void ScopeIncludes(String scope_includes) {
		ScopeIncludes(ParseRE(scope_includes));
	}
	
	public void ScopeIncludes(List scope_includes) {
		this.scope_includes = scope_includes;
	}

	public List ScopeExcludes() {
		return scope_excludes;
	}

	public void ScopeExcludes(String scope_excludes) {
		ScopeExcludes(ParseRE(scope_excludes));
	}

	public void ScopeExcludes(List scope_excludes) {
		this.scope_excludes = scope_excludes;
	}

	public boolean PackageScope() {
		return package_scope;
	}

	public void PackageScope(boolean package_scope) {
		this.package_scope = package_scope;
	}

	public List PackageScopeIncludes() {
		return package_scope_includes;
	}

	public void PackageScopeIncludes(String package_scope_includes) {
		PackageScopeIncludes(ParseRE(package_scope_includes));
	}

	public void PackageScopeIncludes(List package_scope_includes) {
		this.package_scope_includes = package_scope_includes;
	}

	public List PackageScopeExcludes() {
		return package_scope_excludes;
	}

	public void PackageScopeExcludes(String package_scope_excludes) {
		PackageScopeExcludes(ParseRE(package_scope_excludes));
	}

	public void PackageScopeExcludes(List package_scope_excludes) {
		this.package_scope_excludes = package_scope_excludes;
	}

	public boolean ClassScope() {
		return class_scope;
	}

	public void ClassScope(boolean class_scope) {
		this.class_scope = class_scope;
	}

	public List ClassScopeIncludes() {
		return class_scope_includes;
	}

	public void ClassScopeIncludes(String class_scope_includes) {
		ClassScopeIncludes(ParseRE(class_scope_includes));
	}

	public void ClassScopeIncludes(List class_scope_includes) {
		this.class_scope_includes = class_scope_includes;
	}

	public List ClassScopeExcludes() {
		return class_scope_excludes;
	}

	public void ClassScopeExcludes(String class_scope_excludes) {
		ClassScopeExcludes(ParseRE(class_scope_excludes));
	}

	public void ClassScopeExcludes(List class_scope_excludes) {
		this.class_scope_excludes = class_scope_excludes;
	}

	public boolean FeatureScope() {
		return feature_scope;
	}

	public void FeatureScope(boolean feature_scope) {
		this.feature_scope = feature_scope;
	}

	public List FeatureScopeIncludes() {
		return feature_scope_includes;
	}

	public void FeatureScopeIncludes(String feature_scope_includes) {
		FeatureScopeIncludes(ParseRE(feature_scope_includes));
	}

	public void FeatureScopeIncludes(List feature_scope_includes) {
		this.feature_scope_includes = feature_scope_includes;
	}

	public List FeatureScopeExcludes() {
		return feature_scope_excludes;
	}

	public void FeatureScopeExcludes(String feature_scope_excludes) {
		FeatureScopeExcludes(ParseRE(feature_scope_excludes));
	}

	public void FeatureScopeExcludes(List feature_scope_excludes) {
		this.feature_scope_excludes = feature_scope_excludes;
	}
	
	public List FilterIncludes() {
		return filter_includes;
	}

	public void FilterIncludes(String filter_includes) {
		FilterIncludes(ParseRE(filter_includes));
	}

	public void FilterIncludes(List filter_includes) {
		this.filter_includes = filter_includes;
	}

	public List FilterExcludes() {
		return filter_excludes;
	}

	public void FilterExcludes(String filter_excludes) {
		FilterExcludes(ParseRE(filter_excludes));
	}

	public void FilterExcludes(List filter_excludes) {
		this.filter_excludes = filter_excludes;
	}

	public boolean PackageFilter() {
		return package_filter;
	}

	public void PackageFilter(boolean package_filter) {
		this.package_filter = package_filter;
	}

	public List PackageFilterIncludes() {
		return package_filter_includes;
	}

	public void PackageFilterIncludes(String package_filter_includes) {
		PackageFilterIncludes(ParseRE(package_filter_includes));
	}

	public void PackageFilterIncludes(List package_filter_includes) {
		this.package_filter_includes = package_filter_includes;
	}

	public List PackageFilterExcludes() {
		return package_filter_excludes;
	}

	public void PackageFilterExcludes(String package_filter_excludes) {
		PackageFilterExcludes(ParseRE(package_filter_excludes));
	}

	public void PackageFilterExcludes(List package_filter_excludes) {
		this.package_filter_excludes = package_filter_excludes;
	}

	public boolean ClassFilter() {
		return class_filter;
	}

	public void ClassFilter(boolean class_filter) {
		this.class_filter = class_filter;
	}

	public List ClassFilterIncludes() {
		return class_filter_includes;
	}

	public void ClassFilterIncludes(String class_filter_includes) {
		ClassFilterIncludes(ParseRE(class_filter_includes));
	}

	public void ClassFilterIncludes(List class_filter_includes) {
		this.class_filter_includes = class_filter_includes;
	}

	public List ClassFilterExcludes() {
		return class_filter_excludes;
	}

	public void ClassFilterExcludes(String class_filter_excludes) {
		ClassFilterExcludes(ParseRE(class_filter_excludes));
	}

	public void ClassFilterExcludes(List class_filter_excludes) {
		this.class_filter_excludes = class_filter_excludes;
	}

	public boolean FeatureFilter() {
		return feature_filter;
	}

	public void FeatureFilter(boolean feature_filter) {
		this.feature_filter = feature_filter;
	}

	public List FeatureFilterIncludes() {
		return feature_filter_includes;
	}

	public void FeatureFilterIncludes(String feature_filter_includes) {
		FeatureFilterIncludes(ParseRE(feature_filter_includes));
	}

	public void FeatureFilterIncludes(List feature_filter_includes) {
		this.feature_filter_includes = feature_filter_includes;
	}

	public List FeatureFilterExcludes() {
		return feature_filter_excludes;
	}

	public void FeatureFilterExcludes(String feature_filter_excludes) {
		FeatureFilterExcludes(ParseRE(feature_filter_excludes));
	}

	public void FeatureFilterExcludes(List feature_filter_excludes) {
		this.feature_filter_excludes = feature_filter_excludes;
	}

	public boolean InScope(PackageNode node) {
		return PackageScope() && PackageScopeMatch(node.Name());
	}
	
	public boolean InScope(ClassNode node) {
		return ClassScope() && ClassScopeMatch(node.Name());
	}
	
	public boolean InScope(FeatureNode node) {
		return FeatureScope() && FeatureScopeMatch(node.Name());
	}

	public boolean PackageScopeMatch(String name) {
		return Match(ScopeIncludes(), PackageScopeIncludes(), name) &&
			!Match(ScopeExcludes(), PackageScopeExcludes(), name);
	}

	public boolean ClassScopeMatch(String name) {
		return Match(ScopeIncludes(), ClassScopeIncludes(), name) &&
			!Match(ScopeExcludes(), ClassScopeExcludes(), name);
	}

	public boolean FeatureScopeMatch(String name) {
		return Match(ScopeIncludes(), FeatureScopeIncludes(), name) &&
			!Match(ScopeExcludes(), FeatureScopeExcludes(), name);
	}

	public boolean InFilter(PackageNode node) {
		return PackageFilter() && PackageFilterMatch(node.Name());
	}
	
	public boolean InFilter(ClassNode node) {
		return ClassFilter() && ClassFilterMatch(node.Name());
	}
	
	public boolean InFilter(FeatureNode node) {
		return FeatureFilter() && FeatureFilterMatch(node.Name());
	}

	public boolean PackageFilterMatch(String name) {
		return Match(FilterIncludes(), PackageFilterIncludes(), name) &&
			!Match(FilterExcludes(), PackageFilterExcludes(), name);
	}

	public boolean ClassFilterMatch(String name) {
		return Match(FilterIncludes(), ClassFilterIncludes(), name) &&
			!Match(FilterExcludes(), ClassFilterExcludes(), name);
	}

	public boolean FeatureFilterMatch(String name) {
		return Match(FilterIncludes(), FeatureFilterIncludes(), name) &&
			!Match(FilterExcludes(), FeatureFilterExcludes(), name);
	}

	public Collection Order(Collection collection) {
		return new ArrayList(collection);
	}
	
	private boolean Match(List global_criteria, List criteria, String name) {
		boolean  found = false;
		Iterator i;

		i = global_criteria.iterator();
		while (!found && i.hasNext()) {
			String condition = (String) i.next();
			found = Perl().match(condition, name);
		}

		i = criteria.iterator();
		while (!found && i.hasNext()) {
			String condition = (String) i.next();
			found = Perl().match(condition, name);
		}

		return found;
	}

	public String toString() {
		StringBuffer result = new StringBuffer();

		result.append(getClass().getName()).append(System.getProperty("line.separator", "\n"));
		result.append(System.getProperty("line.separator", "\n"));
		result.append("scope_includes: ").append(scope_includes).append(System.getProperty("line.separator", "\n"));
		result.append("scope_excludes: ").append(scope_excludes).append(System.getProperty("line.separator", "\n"));
		result.append("package_scope: ").append(package_scope).append(System.getProperty("line.separator", "\n"));
		result.append("package_scope_includes: ").append(package_scope_includes).append(System.getProperty("line.separator", "\n"));
		result.append("package_scope_excludes: ").append(package_scope_excludes).append(System.getProperty("line.separator", "\n"));
		result.append("class_scope: ").append(class_scope).append(System.getProperty("line.separator", "\n"));
		result.append("class_scope_includes: ").append(class_scope_includes).append(System.getProperty("line.separator", "\n"));
		result.append("class_scope_excludes: ").append(class_scope_excludes).append(System.getProperty("line.separator", "\n"));
		result.append("feature_scope: ").append(feature_scope).append(System.getProperty("line.separator", "\n"));
		result.append("feature_scope_includes: ").append(feature_scope_includes).append(System.getProperty("line.separator", "\n"));
		result.append("feature_scope_excludes: ").append(feature_scope_excludes).append(System.getProperty("line.separator", "\n"));
		result.append(System.getProperty("line.separator", "\n"));
		result.append("filter_includes: ").append(filter_includes).append(System.getProperty("line.separator", "\n"));
		result.append("filter_excludes: ").append(filter_excludes).append(System.getProperty("line.separator", "\n"));
		result.append("package_filter: ").append(package_filter).append(System.getProperty("line.separator", "\n"));
		result.append("package_filter_includes: ").append(package_filter_includes).append(System.getProperty("line.separator", "\n"));
		result.append("package_filter_excludes: ").append(package_filter_excludes).append(System.getProperty("line.separator", "\n"));
		result.append("class_filter: ").append(class_filter).append(System.getProperty("line.separator", "\n"));
		result.append("class_filter_includes: ").append(class_filter_includes).append(System.getProperty("line.separator", "\n"));
		result.append("class_filter_excludes: ").append(class_filter_excludes).append(System.getProperty("line.separator", "\n"));
		result.append("feature_filter: ").append(feature_filter).append(System.getProperty("line.separator", "\n"));
		result.append("feature_filter_includes: ").append(feature_filter_includes).append(System.getProperty("line.separator", "\n"));
		result.append("feature_filter_excludes: ").append(feature_filter_excludes).append(System.getProperty("line.separator", "\n"));
		
		return result.toString();
	}
}
