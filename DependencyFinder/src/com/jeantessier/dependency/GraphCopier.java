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

public class GraphCopier extends VisitorBase {
	private NodeFactory factory;

	public GraphCopier() {
		this(new NodeFactory());
	}

	public GraphCopier(TraversalStrategy strategy) {
		this(strategy, new NodeFactory());
	}

	public GraphCopier(NodeFactory factory) {
		super();

		this.factory = factory;
	}
	
	public GraphCopier(TraversalStrategy strategy, NodeFactory factory) {
		super(strategy);

		this.factory = factory;
	}
	
	public NodeFactory Factory() {
		return factory;
	}

	protected void PreprocessPackageNode(PackageNode node) {
		super.PreprocessPackageNode(Factory().CreatePackage(node.Name()));
	}

	public void VisitInboundPackageNode(PackageNode node) {
		if (Strategy().InFilter(node)) {
			Factory().CreatePackage(node.Name()).AddDependency(CurrentNode());
		}
	}

	public void VisitOutboundPackageNode(PackageNode node) {
		if (Strategy().InFilter(node)) {
			CurrentNode().AddDependency(Factory().CreatePackage(node.Name()));
		}
	}

	protected void PreprocessClassNode(ClassNode node) {
		super.PreprocessClassNode(Factory().CreateClass(node.Name()));
	}

	public void VisitInboundClassNode(ClassNode node) {
		if (Strategy().InFilter(node)) {
			Factory().CreateClass(node.Name()).AddDependency(CurrentNode());
		}
	}

	public void VisitOutboundClassNode(ClassNode node) {
		if (Strategy().InFilter(node)) {
			CurrentNode().AddDependency(Factory().CreateClass(node.Name()));
		}
	}

	protected void PreprocessFeatureNode(FeatureNode node) {
		super.PreprocessFeatureNode(Factory().CreateFeature(node.Name()));
	}

	public void VisitInboundFeatureNode(FeatureNode node) {
		if (Strategy().InFilter(node)) {
			Factory().CreateFeature(node.Name()).AddDependency(CurrentNode());
		}
	}

	public void VisitOutboundFeatureNode(FeatureNode node) {
		if (Strategy().InFilter(node)) {
			CurrentNode().AddDependency(Factory().CreateFeature(node.Name()));
		}
	}
}
