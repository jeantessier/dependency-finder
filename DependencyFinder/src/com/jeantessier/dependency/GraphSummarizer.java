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

public class GraphSummarizer extends GraphCopier {
	public GraphSummarizer(SelectiveTraversalStrategy strategy) {
		super(strategy);
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
				FilterFactory().CreatePackage(node.Name()).AddDependency(CurrentNode());
			}
		}
	}

	public void VisitOutboundPackageNode(PackageNode node) {
		if (CurrentNode() != null && ((SelectiveTraversalStrategy) Strategy()).PackageFilterMatch(node.Name())) {
			if (((SelectiveTraversalStrategy) Strategy()).PackageFilter()) {
				CurrentNode().AddDependency(FilterFactory().CreatePackage(node.Name()));
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
				FilterFactory().CreatePackage(node.Package().Name()).AddDependency(CurrentNode());
			}
			if (((SelectiveTraversalStrategy) Strategy()).ClassFilter()) {
				FilterFactory().CreateClass(node.Name()).AddDependency(CurrentNode());
			}
		}
	}

	public void VisitOutboundClassNode(ClassNode node) {
		if (CurrentNode() != null && ((SelectiveTraversalStrategy) Strategy()).ClassFilterMatch(node.Name())) {
			if (((SelectiveTraversalStrategy) Strategy()).PackageFilter()) {
				CurrentNode().AddDependency(FilterFactory().CreatePackage(node.Package().Name()));
			}
			if (((SelectiveTraversalStrategy) Strategy()).ClassFilter()) {
				CurrentNode().AddDependency(FilterFactory().CreateClass(node.Name()));
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
				FilterFactory().CreatePackage(node.Class().Package().Name()).AddDependency(CurrentNode());
			}
			if (((SelectiveTraversalStrategy) Strategy()).ClassFilter()) {
				FilterFactory().CreateClass(node.Class().Name()).AddDependency(CurrentNode());
			}
			if (((SelectiveTraversalStrategy) Strategy()).FeatureFilter()) {
				FilterFactory().CreateFeature(node.Name()).AddDependency(CurrentNode());
			}
		}
	}

	public void VisitOutboundFeatureNode(FeatureNode node) {
		if (CurrentNode() != null && ((SelectiveTraversalStrategy) Strategy()).FeatureFilterMatch(node.Name())) {
			if (((SelectiveTraversalStrategy) Strategy()).PackageFilter()) {
				CurrentNode().AddDependency(FilterFactory().CreatePackage(node.Class().Package().Name()));
			}
			if (((SelectiveTraversalStrategy) Strategy()).ClassFilter()) {
				CurrentNode().AddDependency(FilterFactory().CreateClass(node.Class().Name()));
			}
			if (((SelectiveTraversalStrategy) Strategy()).FeatureFilter()) {
				CurrentNode().AddDependency(FilterFactory().CreateFeature(node.Name()));
			}
		}
	}
}
