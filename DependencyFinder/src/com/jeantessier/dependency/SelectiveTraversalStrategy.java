/*
 *  Dependency Finder - Computes quality factors from compiled Java code
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeantessier.dependency;

import java.util.*;

import org.apache.oro.text.perl.*;

public class SelectiveTraversalStrategy implements TraversalStrategy {
	private static final Perl5Util perl = new Perl5Util();
	
	protected static Perl5Util Perl() {
		return perl;
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

	public void ScopeIncludes(List scope_includes) {
		this.scope_includes = scope_includes;
	}

	public List ScopeExcludes() {
		return scope_excludes;
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

	public void PackageScopeIncludes(List package_scope_includes) {
		this.package_scope_includes = package_scope_includes;
	}

	public List PackageScopeExcludes() {
		return package_scope_excludes;
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

	public void ClassScopeIncludes(List class_scope_includes) {
		this.class_scope_includes = class_scope_includes;
	}

	public List ClassScopeExcludes() {
		return class_scope_excludes;
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

	public void FeatureScopeIncludes(List feature_scope_includes) {
		this.feature_scope_includes = feature_scope_includes;
	}

	public List FeatureScopeExcludes() {
		return feature_scope_excludes;
	}

	public void FeatureScopeExcludes(List feature_scope_excludes) {
		this.feature_scope_excludes = feature_scope_excludes;
	}
	
	public List FilterIncludes() {
		return filter_includes;
	}

	public void FilterIncludes(List filter_includes) {
		this.filter_includes = filter_includes;
	}

	public List FilterExcludes() {
		return filter_excludes;
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

	public void PackageFilterIncludes(List package_filter_includes) {
		this.package_filter_includes = package_filter_includes;
	}

	public List PackageFilterExcludes() {
		return package_filter_excludes;
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

	public void ClassFilterIncludes(List class_filter_includes) {
		this.class_filter_includes = class_filter_includes;
	}

	public List ClassFilterExcludes() {
		return class_filter_excludes;
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

	public void FeatureFilterIncludes(List feature_filter_includes) {
		this.feature_filter_includes = feature_filter_includes;
	}

	public List FeatureFilterExcludes() {
		return feature_filter_excludes;
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
}
