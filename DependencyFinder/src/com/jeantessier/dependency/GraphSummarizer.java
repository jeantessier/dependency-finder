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

import org.apache.log4j.*;

public class GraphSummarizer extends GraphCopier {
	private SelectionCriteria scope_criteria;
	private SelectionCriteria filter_criteria;

	public GraphSummarizer(SelectionCriteria scope_criteria, SelectionCriteria filter_criteria) {
		super(new SelectiveTraversalStrategy(scope_criteria, filter_criteria));

		this.scope_criteria  = scope_criteria;
		this.filter_criteria = filter_criteria;
	}
	
	public void visitPackageNode(PackageNode node) {
		Logger.getLogger(getClass()).debug("node = " + node);
		
		boolean in_scope = scope_criteria.matchesPackageName(node.getName());

		if (in_scope) {
			preprocessPackageNode(node);
			
			if (getStrategy().doPreOutboundTraversal()) {
				traverseOutbound(node.getOutboundDependencies());
			}
			
			if (getStrategy().doPreInboundTraversal()) {
				traverseInbound(node.getInboundDependencies());
			}
			
			preprocessAfterDependenciesPackageNode(node);
		}
			
		traverseNodes(node.getClasses());

		if (in_scope) {
			postprocessBeforeDependenciesPackageNode(node);

			if (getStrategy().doPostOutboundTraversal()) {
				traverseOutbound(node.getOutboundDependencies());
			}
			
			if (getStrategy().doPostInboundTraversal()) {
				traverseInbound(node.getInboundDependencies());
			}

			postprocessPackageNode(node);
		}
	}

	protected void preprocessPackageNode(PackageNode node) {
		if (scope_criteria.doesPackageMatching()) {
			super.preprocessPackageNode(node);
		}
	}
	
	protected void postprocessPackageNode(PackageNode node) {
		if (scope_criteria.doesPackageMatching()) {
			super.postprocessPackageNode(node);
		}
	}

	public void visitInboundPackageNode(PackageNode node) {
		if (getCurrentNode() != null && filter_criteria.matchesPackageName(node.getName())) {
			if (filter_criteria.doesPackageMatching()) {
				FilterFactory().CreatePackage(node.getName()).addDependency(getCurrentNode());
			}
		}
	}

	public void visitOutboundPackageNode(PackageNode node) {
		if (getCurrentNode() != null && filter_criteria.matchesPackageName(node.getName())) {
			if (filter_criteria.doesPackageMatching()) {
				getCurrentNode().addDependency(FilterFactory().CreatePackage(node.getName()));
			}
		}
	}

	public void visitClassNode(ClassNode node) {
		boolean in_scope = scope_criteria.matchesClassName(node.getName());
		
		if (in_scope) {
			preprocessClassNode(node);
			
			if (getStrategy().doPreOutboundTraversal()) {
				traverseOutbound(node.getOutboundDependencies());
			}
			
			if (getStrategy().doPreInboundTraversal()) {
				traverseInbound(node.getInboundDependencies());
			}
		
			preprocessAfterDependenciesClassNode(node);
		}
		
		traverseNodes(node.getFeatures());
			
		if (in_scope) {
			postprocessBeforeDependenciesClassNode(node);

			if (getStrategy().doPostOutboundTraversal()) {
				traverseOutbound(node.getOutboundDependencies());
			}
			
			if (getStrategy().doPostInboundTraversal()) {
				traverseInbound(node.getInboundDependencies());
			}
			
			postprocessClassNode(node);
		}
	}

	protected void preprocessClassNode(ClassNode node) {
		if (scope_criteria.doesClassMatching()) {
			super.preprocessClassNode(node);
		}
	}
	
	protected void postprocessClassNode(ClassNode node) {
		if (scope_criteria.doesClassMatching()) {
			super.postprocessClassNode(node);
		}
	}

	public void visitInboundClassNode(ClassNode node) {
		if (getCurrentNode() != null && filter_criteria.matchesClassName(node.getName())) {
			if (filter_criteria.doesPackageMatching()) {
				FilterFactory().CreatePackage(node.getPackageNode().getName()).addDependency(getCurrentNode());
			}
			if (filter_criteria.doesClassMatching()) {
				FilterFactory().CreateClass(node.getName()).addDependency(getCurrentNode());
			}
		}
	}

	public void visitOutboundClassNode(ClassNode node) {
		if (getCurrentNode() != null && filter_criteria.matchesClassName(node.getName())) {
			if (filter_criteria.doesPackageMatching()) {
				getCurrentNode().addDependency(FilterFactory().CreatePackage(node.getPackageNode().getName()));
			}
			if (filter_criteria.doesClassMatching()) {
				getCurrentNode().addDependency(FilterFactory().CreateClass(node.getName()));
			}
		}
	}

	public void visitFeatureNode(FeatureNode node) {
		if (scope_criteria.matchesFeatureName(node.getName())) {
			preprocessFeatureNode(node);
			
			if (getStrategy().doPreOutboundTraversal()) {
				traverseOutbound(node.getOutboundDependencies());
			}
			
			if (getStrategy().doPreInboundTraversal()) {
				traverseInbound(node.getInboundDependencies());
			}
			
			if (getStrategy().doPostOutboundTraversal()) {
				traverseOutbound(node.getOutboundDependencies());
			}
			
			if (getStrategy().doPostInboundTraversal()) {
				traverseInbound(node.getInboundDependencies());
			}
			
			postprocessFeatureNode(node);
		}
	}

	protected void preprocessFeatureNode(FeatureNode node) {
		if (scope_criteria.doesFeatureMatching()) {
			super.preprocessFeatureNode(node);
		}
	}
	
	protected void postprocessFeatureNode(FeatureNode node) {
		if (scope_criteria.doesFeatureMatching()) {
			super.postprocessFeatureNode(node);
		}
	}
	
	public void visitInboundFeatureNode(FeatureNode node) {
		if (getCurrentNode() != null && filter_criteria.matchesFeatureName(node.getName())) {
			if (filter_criteria.doesPackageMatching()) {
				FilterFactory().CreatePackage(node.getClassNode().getPackageNode().getName()).addDependency(getCurrentNode());
			}
			if (filter_criteria.doesClassMatching()) {
				FilterFactory().CreateClass(node.getClassNode().getName()).addDependency(getCurrentNode());
			}
			if (filter_criteria.doesFeatureMatching()) {
				FilterFactory().CreateFeature(node.getName()).addDependency(getCurrentNode());
			}
		}
	}

	public void visitOutboundFeatureNode(FeatureNode node) {
		if (getCurrentNode() != null && filter_criteria.matchesFeatureName(node.getName())) {
			if (filter_criteria.doesPackageMatching()) {
				getCurrentNode().addDependency(FilterFactory().CreatePackage(node.getClassNode().getPackageNode().getName()));
			}
			if (filter_criteria.doesClassMatching()) {
				getCurrentNode().addDependency(FilterFactory().CreateClass(node.getClassNode().getName()));
			}
			if (filter_criteria.doesFeatureMatching()) {
				getCurrentNode().addDependency(FilterFactory().CreateFeature(node.getName()));
			}
		}
	}
}
