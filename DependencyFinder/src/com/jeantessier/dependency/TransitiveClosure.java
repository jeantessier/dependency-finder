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
	
	private NodeFactory factory                = new NodeFactory();
	private long        maximum_inbound_depth  = DO_NOT_FOLLOW;
	private long        maximum_outbound_depth = UNBOUNDED_DEPTH;
	private boolean     single_path            = false;

	private Set  visited_nodes = new HashSet();
	private long current_depth = 0;
	
	public TransitiveClosure() {
		super();
	}

	public TransitiveClosure(TraversalStrategy strategy) {
		super(strategy);
	}

	public NodeFactory Factory() {
		return factory;
	}

	public long MaximumInboundDepth() {
		return maximum_inbound_depth;
	}

	public void MaximumInboundDepth(long maximum_inbound_depth) {
		this.maximum_inbound_depth = maximum_inbound_depth;
	}

	public long MaximumOutboundDepth() {
		return maximum_outbound_depth;
	}

	public void MaximumOutboundDepth(long maximum_outbound_depth) {
		this.maximum_outbound_depth = maximum_outbound_depth;
	}
	
	/*
	 *  If the call to AddDependency() is unconditional, all
	 *  dependencies will be copied in the new graph.  Otherwise,
	 *  only the first dependency that lead to the node will be
	 *  part of the resulting graph.
	 */
	public boolean SinglePath() {
		return single_path;
	}

	public void SinglePath(boolean single_path) {
		this.single_path = single_path;
	}

	public void PreprocessPackageNode(PackageNode node) {
		if (!visited_nodes.contains(node.Name())) {
			super.PreprocessPackageNode(Factory().CreatePackage(node.Name()));
			visited_nodes.add(node.Name());
			TraverseOutbound(node.Outbound());
			TraverseInbound(node.Inbound());
		}
	}

	public void VisitInboundPackageNode(PackageNode node) {
		if (CurrentNode() != null && current_depth < MaximumInboundDepth() && Strategy().InFilter(node)) {
			if (!SinglePath()) {
				Factory().CreatePackage(node.Name()).AddDependency(CurrentNode());
			}
		
			if (!visited_nodes.contains(node.Name())) {
				if (SinglePath()) {
					Factory().CreatePackage(node.Name()).AddDependency(CurrentNode());
				}
				current_depth++;
				PreprocessPackageNode(node);
				current_depth--;
				PopNode();
			}
		}
	}

	public void VisitOutboundPackageNode(PackageNode node) {
		if (CurrentNode() != null && current_depth < MaximumOutboundDepth() && Strategy().InFilter(node)) {
			if (!SinglePath()) {
				CurrentNode().AddDependency(Factory().CreatePackage(node.Name()));
			}
		
			if (!visited_nodes.contains(node.Name())) {
				if (SinglePath()) {
					CurrentNode().AddDependency(Factory().CreatePackage(node.Name()));
				}
				current_depth++;
				PreprocessPackageNode(node);
				current_depth--;
				PopNode();
			}
		}
	}

	public void PreprocessClassNode(ClassNode node) {
		if (!visited_nodes.contains(node.Name())) {
			super.PreprocessClassNode(Factory().CreateClass(node.Name()));
			visited_nodes.add(node.Name());
			TraverseOutbound(node.Outbound());
			TraverseInbound(node.Inbound());
		}
	}

	public void VisitInboundClassNode(ClassNode node) {
		if (CurrentNode() != null && current_depth < MaximumInboundDepth() && Strategy().InFilter(node)) {
			if (!SinglePath()) {
				Factory().CreateClass(node.Name()).AddDependency(CurrentNode());
			}
		
			if (!visited_nodes.contains(node.Name())) {
				if (SinglePath()) {
					Factory().CreateClass(node.Name()).AddDependency(CurrentNode());
				}
				current_depth++;
				PreprocessClassNode(node);
				current_depth--;
				PopNode();
			}
		}
	}

	public void VisitOutboundClassNode(ClassNode node) {
		if (CurrentNode() != null && current_depth < MaximumOutboundDepth() && Strategy().InFilter(node)) {
			
			if (!SinglePath()) {
				CurrentNode().AddDependency(Factory().CreateClass(node.Name()));
			}
		
			if (!visited_nodes.contains(node.Name())) {
				if (SinglePath()) {
					CurrentNode().AddDependency(Factory().CreateClass(node.Name()));
				}
				current_depth++;
				PreprocessClassNode(node);
				current_depth--;
				PopNode();
			}
		}
	}

	public void PreprocessFeatureNode(FeatureNode node) {
		if (!visited_nodes.contains(node.Name())) {
			super.PreprocessFeatureNode(Factory().CreateFeature(node.Name()));
			visited_nodes.add(node.Name());
			TraverseOutbound(node.Outbound());
			TraverseInbound(node.Inbound());
		}
	}

	public void VisitInboundFeatureNode(FeatureNode node) {
		if (CurrentNode() != null && current_depth < MaximumInboundDepth() && Strategy().InFilter(node)) {
			if (!SinglePath()) {
				Factory().CreateFeature(node.Name()).AddDependency(CurrentNode());
			}
		
			if (!visited_nodes.contains(node.Name())) {
				if (SinglePath()) {
					Factory().CreateFeature(node.Name()).AddDependency(CurrentNode());
				}
				current_depth++;
				PreprocessFeatureNode(node);
				current_depth--;
				PopNode();
			}
		}
	}

	public void VisitOutboundFeatureNode(FeatureNode node) {
		if (CurrentNode() != null && current_depth < MaximumOutboundDepth() && Strategy().InFilter(node)) {
			if (!SinglePath()) {
				CurrentNode().AddDependency(Factory().CreateFeature(node.Name()));
			}
		
			if (!visited_nodes.contains(node.Name())) {
				if (SinglePath()) {
					CurrentNode().AddDependency(Factory().CreateFeature(node.Name()));
				}
				current_depth++;
				PreprocessFeatureNode(node);
				current_depth--;
				PopNode();
			}
		}
	}
}
