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

/**
 *  Creates a sub-graph of Nodes based on a scope and
 *  filtering rules.  To get all transitive dependencies,
 *  the visited graph should be maximized first with
 *  LinkMaximizer.  Otherwise, you will only get a subset
 *  of the explicit dependencies.
 */
public class TransitiveClosure extends VisitorBase {
	public static long DO_NOT_FOLLOW   = 0;
	public static long UNBOUNDED_DEPTH = Long.MAX_VALUE;
	
	private NodeFactory factory              = new NodeFactory();
	private long        maximumInboundDepth  = DO_NOT_FOLLOW;
	private long        maximumOutboundDepth = UNBOUNDED_DEPTH;
	private boolean     singlePath           = false;

	private Set  visitedNodes = new HashSet();
	private long currentDepth = 0;
	
	public TransitiveClosure() {
		super();
	}

	public TransitiveClosure(TraversalStrategy strategy) {
		super(strategy);
	}

	public NodeFactory getFactory() {
		return factory;
	}

	public long getMaximumInboundDepth() {
		return maximumInboundDepth;
	}

	public void setMaximumInboundDepth(long maximumInboundDepth) {
		this.maximumInboundDepth = maximumInboundDepth;
	}

	public long getMaximumOutboundDepth() {
		return maximumOutboundDepth;
	}

	public void setMaximumOutboundDepth(long maximumOutboundDepth) {
		this.maximumOutboundDepth = maximumOutboundDepth;
	}
	
	/*
	 *  If the call to AddDependency() is unconditional, all
	 *  dependencies will be copied in the new graph.  Otherwise,
	 *  only the first dependency that lead to the node will be
	 *  part of the resulting graph.
	 */
	public boolean isSinglePath() {
		return singlePath;
	}

	public void setSinglePath(boolean singlePath) {
		this.singlePath = singlePath;
	}

	public void preprocessPackageNode(PackageNode node) {
		if (!visitedNodes.contains(node.getName())) {
			super.preprocessPackageNode(getFactory().createPackage(node.getName()));
			visitedNodes.add(node.getName());
			traverseOutbound(node.getOutboundDependencies());
			traverseInbound(node.getInboundDependencies());
		}
	}

	public void visitInboundPackageNode(PackageNode node) {
		if (getCurrentNode() != null && currentDepth < getMaximumInboundDepth() && getStrategy().isInFilter(node)) {
			if (!isSinglePath()) {
				getFactory().createPackage(node.getName()).addDependency(getCurrentNode());
			}
		
			if (!visitedNodes.contains(node.getName())) {
				if (isSinglePath()) {
					getFactory().createPackage(node.getName()).addDependency(getCurrentNode());
				}
				currentDepth++;
				preprocessPackageNode(node);
				currentDepth--;
				popNode();
			}
		}
	}

	public void visitOutboundPackageNode(PackageNode node) {
		if (getCurrentNode() != null && currentDepth < getMaximumOutboundDepth() && getStrategy().isInFilter(node)) {
			if (!isSinglePath()) {
				getCurrentNode().addDependency(getFactory().createPackage(node.getName()));
			}
		
			if (!visitedNodes.contains(node.getName())) {
				if (isSinglePath()) {
					getCurrentNode().addDependency(getFactory().createPackage(node.getName()));
				}
				currentDepth++;
				preprocessPackageNode(node);
				currentDepth--;
				popNode();
			}
		}
	}

	public void preprocessClassNode(ClassNode node) {
		if (!visitedNodes.contains(node.getName())) {
			super.preprocessClassNode(getFactory().createClass(node.getName()));
			visitedNodes.add(node.getName());
			traverseOutbound(node.getOutboundDependencies());
			traverseInbound(node.getInboundDependencies());
		}
	}

	public void visitInboundClassNode(ClassNode node) {
		if (getCurrentNode() != null && currentDepth < getMaximumInboundDepth() && getStrategy().isInFilter(node)) {
			if (!isSinglePath()) {
				getFactory().createClass(node.getName()).addDependency(getCurrentNode());
			}
		
			if (!visitedNodes.contains(node.getName())) {
				if (isSinglePath()) {
					getFactory().createClass(node.getName()).addDependency(getCurrentNode());
				}
				currentDepth++;
				preprocessClassNode(node);
				currentDepth--;
				popNode();
			}
		}
	}

	public void visitOutboundClassNode(ClassNode node) {
		if (getCurrentNode() != null && currentDepth < getMaximumOutboundDepth() && getStrategy().isInFilter(node)) {
			
			if (!isSinglePath()) {
				getCurrentNode().addDependency(getFactory().createClass(node.getName()));
			}
		
			if (!visitedNodes.contains(node.getName())) {
				if (isSinglePath()) {
					getCurrentNode().addDependency(getFactory().createClass(node.getName()));
				}
				currentDepth++;
				preprocessClassNode(node);
				currentDepth--;
				popNode();
			}
		}
	}

	public void preprocessFeatureNode(FeatureNode node) {
		if (!visitedNodes.contains(node.getName())) {
			super.preprocessFeatureNode(getFactory().createFeature(node.getName()));
			visitedNodes.add(node.getName());
			traverseOutbound(node.getOutboundDependencies());
			traverseInbound(node.getInboundDependencies());
		}
	}

	public void visitInboundFeatureNode(FeatureNode node) {
		if (getCurrentNode() != null && currentDepth < getMaximumInboundDepth() && getStrategy().isInFilter(node)) {
			if (!isSinglePath()) {
				getFactory().createFeature(node.getName()).addDependency(getCurrentNode());
			}
		
			if (!visitedNodes.contains(node.getName())) {
				if (isSinglePath()) {
					getFactory().createFeature(node.getName()).addDependency(getCurrentNode());
				}
				currentDepth++;
				preprocessFeatureNode(node);
				currentDepth--;
				popNode();
			}
		}
	}

	public void visitOutboundFeatureNode(FeatureNode node) {
		if (getCurrentNode() != null && currentDepth < getMaximumOutboundDepth() && getStrategy().isInFilter(node)) {
			if (!isSinglePath()) {
				getCurrentNode().addDependency(getFactory().createFeature(node.getName()));
			}
		
			if (!visitedNodes.contains(node.getName())) {
				if (isSinglePath()) {
					getCurrentNode().addDependency(getFactory().createFeature(node.getName()));
				}
				currentDepth++;
				preprocessFeatureNode(node);
				currentDepth--;
				popNode();
			}
		}
	}
}
