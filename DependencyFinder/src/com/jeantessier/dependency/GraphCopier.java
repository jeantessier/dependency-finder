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

public class GraphCopier extends VisitorBase {
	private NodeFactory scope_factory  = new NodeFactory();
	private NodeFactory filter_factory = new NodeFactory();

	public GraphCopier() {
		super();
	}

	public GraphCopier(TraversalStrategy strategy) {
		super(strategy);
	}
	
	public NodeFactory ScopeFactory() {
		return scope_factory;
	}
	
	public NodeFactory FilterFactory() {
		return filter_factory;
	}

	protected void preprocessPackageNode(PackageNode node) {
		super.preprocessPackageNode(ScopeFactory().CreatePackage(node.getName()));
	}

	public void visitInboundPackageNode(PackageNode node) {
		if (getStrategy().isInFilter(node)) {
			FilterFactory().CreatePackage(node.getName()).addDependency(getCurrentNode());
		}
	}

	public void visitOutboundPackageNode(PackageNode node) {
		if (getStrategy().isInFilter(node)) {
			getCurrentNode().addDependency(FilterFactory().CreatePackage(node.getName()));
		}
	}

	protected void preprocessClassNode(ClassNode node) {
		super.preprocessClassNode(ScopeFactory().CreateClass(node.getName()));
	}

	public void visitInboundClassNode(ClassNode node) {
		if (getStrategy().isInFilter(node)) {
			FilterFactory().CreateClass(node.getName()).addDependency(getCurrentNode());
		}
	}

	public void visitOutboundClassNode(ClassNode node) {
		if (getStrategy().isInFilter(node)) {
			getCurrentNode().addDependency(FilterFactory().CreateClass(node.getName()));
		}
	}

	protected void preprocessFeatureNode(FeatureNode node) {
		super.preprocessFeatureNode(ScopeFactory().CreateFeature(node.getName()));
	}

	public void visitInboundFeatureNode(FeatureNode node) {
		if (getStrategy().isInFilter(node)) {
			FilterFactory().CreateFeature(node.getName()).addDependency(getCurrentNode());
		}
	}

	public void visitOutboundFeatureNode(FeatureNode node) {
		if (getStrategy().isInFilter(node)) {
			getCurrentNode().addDependency(FilterFactory().CreateFeature(node.getName()));
		}
	}
}
