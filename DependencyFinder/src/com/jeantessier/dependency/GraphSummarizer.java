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

public class GraphSummarizer extends GraphCopier {
	public GraphSummarizer(SelectiveTraversalStrategy strategy) {
		super(strategy);
	}

	public GraphSummarizer(SelectiveTraversalStrategy strategy, NodeFactory factory) {
		super(strategy, factory);
	}
	
	public void VisitPackageNode(PackageNode node) {
		boolean in_scope = ((SelectiveTraversalStrategy) Strategy()).PackageScopeMatch(node.Name());

		if (in_scope) {
			PreprocessPackageNode(node);
			
			if (Strategy().PreOutboundTraversal()) {
				TraverseOutbound(node.Outbound());
			}
			
			if (Strategy().PreInboundTraversal()) {
				TraverseInbound(node.Inbound());
			}
			
			PreprocessAfterDependenciesPackageNode(node);
		}
			
		TraverseNodes(node.Classes());

		if (in_scope) {
			PostprocessBeforeDependenciesPackageNode(node);

			if (Strategy().PostOutboundTraversal()) {
				TraverseOutbound(node.Outbound());
			}
			
			if (Strategy().PostInboundTraversal()) {
				TraverseInbound(node.Inbound());
			}

			PostprocessPackageNode(node);
		}
	}

	protected void PreprocessPackageNode(PackageNode node) {
		if (((SelectiveTraversalStrategy) Strategy()).PackageScope()) {
			super.PreprocessPackageNode(node);
		}
	}
	
	protected void PostprocessPackageNode(PackageNode node) {
		if (((SelectiveTraversalStrategy) Strategy()).PackageScope()) {
			super.PostprocessPackageNode(node);
		}
	}

	public void VisitInboundPackageNode(PackageNode node) {
		if (CurrentNode() != null && ((SelectiveTraversalStrategy) Strategy()).PackageFilterMatch(node.Name())) {
			if (((SelectiveTraversalStrategy) Strategy()).PackageFilter()) {
				Factory().CreatePackage(node.Name()).AddDependency(CurrentNode());
			}
		}
	}

	public void VisitOutboundPackageNode(PackageNode node) {
		if (CurrentNode() != null && ((SelectiveTraversalStrategy) Strategy()).PackageFilterMatch(node.Name())) {
			if (((SelectiveTraversalStrategy) Strategy()).PackageFilter()) {
				CurrentNode().AddDependency(Factory().CreatePackage(node.Name()));
			}
		}
	}

	public void VisitClassNode(ClassNode node) {
		boolean in_scope = ((SelectiveTraversalStrategy) Strategy()).ClassScopeMatch(node.Name());
		
		if (in_scope) {
			PreprocessClassNode(node);
			
			if (Strategy().PreOutboundTraversal()) {
				TraverseOutbound(node.Outbound());
			}
			
			if (Strategy().PreInboundTraversal()) {
				TraverseInbound(node.Inbound());
			}
		
			PreprocessAfterDependenciesClassNode(node);
		}
		
		TraverseNodes(node.Features());
			
		if (in_scope) {
			PostprocessBeforeDependenciesClassNode(node);

			if (Strategy().PostOutboundTraversal()) {
				TraverseOutbound(node.Outbound());
			}
			
			if (Strategy().PostInboundTraversal()) {
				TraverseInbound(node.Inbound());
			}
			
			PostprocessClassNode(node);
		}
	}

	protected void PreprocessClassNode(ClassNode node) {
		if (((SelectiveTraversalStrategy) Strategy()).ClassScope()) {
			super.PreprocessClassNode(node);
		}
	}
	
	protected void PostprocessClassNode(ClassNode node) {
		if (((SelectiveTraversalStrategy) Strategy()).ClassScope()) {
			super.PostprocessClassNode(node);
		}
	}

	public void VisitInboundClassNode(ClassNode node) {
		if (CurrentNode() != null && ((SelectiveTraversalStrategy) Strategy()).ClassFilterMatch(node.Name())) {
			if (((SelectiveTraversalStrategy) Strategy()).PackageFilter()) {
				Factory().CreatePackage(node.Package().Name()).AddDependency(CurrentNode());
			}
			if (((SelectiveTraversalStrategy) Strategy()).ClassFilter()) {
				Factory().CreateClass(node.Name()).AddDependency(CurrentNode());
			}
		}
	}

	public void VisitOutboundClassNode(ClassNode node) {
		if (CurrentNode() != null && ((SelectiveTraversalStrategy) Strategy()).ClassFilterMatch(node.Name())) {
			if (((SelectiveTraversalStrategy) Strategy()).PackageFilter()) {
				CurrentNode().AddDependency(Factory().CreatePackage(node.Package().Name()));
			}
			if (((SelectiveTraversalStrategy) Strategy()).ClassFilter()) {
				CurrentNode().AddDependency(Factory().CreateClass(node.Name()));
			}
		}
	}

	public void VisitFeatureNode(FeatureNode node) {
		if (((SelectiveTraversalStrategy) Strategy()).FeatureScopeMatch(node.Name())) {
			PreprocessFeatureNode(node);
			
			if (Strategy().PreOutboundTraversal()) {
				TraverseOutbound(node.Outbound());
			}
			
			if (Strategy().PreInboundTraversal()) {
				TraverseInbound(node.Inbound());
			}
			
			if (Strategy().PostOutboundTraversal()) {
				TraverseOutbound(node.Outbound());
			}
			
			if (Strategy().PostInboundTraversal()) {
				TraverseInbound(node.Inbound());
			}
			
			PostprocessFeatureNode(node);
		}
	}

	protected void PreprocessFeatureNode(FeatureNode node) {
		if (((SelectiveTraversalStrategy) Strategy()).FeatureScope()) {
			super.PreprocessFeatureNode(node);
		}
	}
	
	protected void PostprocessFeatureNode(FeatureNode node) {
		if (((SelectiveTraversalStrategy) Strategy()).FeatureScope()) {
			super.PostprocessFeatureNode(node);
		}
	}
	
	public void VisitInboundFeatureNode(FeatureNode node) {
		if (CurrentNode() != null && ((SelectiveTraversalStrategy) Strategy()).FeatureFilterMatch(node.Name())) {
			if (((SelectiveTraversalStrategy) Strategy()).PackageFilter()) {
				Factory().CreatePackage(node.Class().Package().Name()).AddDependency(CurrentNode());
			}
			if (((SelectiveTraversalStrategy) Strategy()).ClassFilter()) {
				Factory().CreateClass(node.Class().Name()).AddDependency(CurrentNode());
			}
			if (((SelectiveTraversalStrategy) Strategy()).FeatureFilter()) {
				Factory().CreateFeature(node.Name()).AddDependency(CurrentNode());
			}
		}
	}

	public void VisitOutboundFeatureNode(FeatureNode node) {
		if (CurrentNode() != null && ((SelectiveTraversalStrategy) Strategy()).FeatureFilterMatch(node.Name())) {
			if (((SelectiveTraversalStrategy) Strategy()).PackageFilter()) {
				CurrentNode().AddDependency(Factory().CreatePackage(node.Class().Package().Name()));
			}
			if (((SelectiveTraversalStrategy) Strategy()).ClassFilter()) {
				CurrentNode().AddDependency(Factory().CreateClass(node.Class().Name()));
			}
			if (((SelectiveTraversalStrategy) Strategy()).FeatureFilter()) {
				CurrentNode().AddDependency(Factory().CreateFeature(node.Name()));
			}
		}
	}
}
