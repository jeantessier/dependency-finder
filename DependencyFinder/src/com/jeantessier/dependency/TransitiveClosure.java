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
public class TransitiveClosure extends GraphCopier {
	public Set visited_nodes = new HashSet();

	public TransitiveClosure() {
		super();
	}

	public TransitiveClosure(TraversalStrategy strategy) {
		super(strategy);
	}

	public TransitiveClosure(NodeFactory factory) {
		super(factory);
	}

	public TransitiveClosure(TraversalStrategy strategy, NodeFactory factory) {
		super(strategy, factory);
	}

	public void PreprocessPackageNode(PackageNode node) {
		if (!visited_nodes.contains(node.Name())) {
			super.PreprocessPackageNode(node);
			visited_nodes.add(node.Name());
			TraverseOutbound(node.Outbound());
		}
	}

	public void VisitOutboundPackageNode(PackageNode node) {
		super.VisitOutboundPackageNode(node);
		
		if (Strategy().InFilter(node) && !visited_nodes.contains(node.Name())) {
			PreprocessPackageNode(node);
			PopNode();
		}
	}

	public void PreprocessClassNode(ClassNode node) {
		if (!visited_nodes.contains(node.Name())) {
			super.PreprocessClassNode(node);
			visited_nodes.add(node.Name());
			TraverseOutbound(node.Outbound());
		}
	}

	public void VisitOutboundClassNode(ClassNode node) {
		super.VisitOutboundClassNode(node);
		
		if (Strategy().InFilter(node) && !visited_nodes.contains(node.Name())) {
			PreprocessClassNode(node);
			PopNode();
		}
	}

	public void PreprocessFeatureNode(FeatureNode node) {
		if (!visited_nodes.contains(node.Name())) {
			super.PreprocessFeatureNode(node);
			visited_nodes.add(node.Name());
			TraverseOutbound(node.Outbound());
		}
	}

	public void VisitOutboundFeatureNode(FeatureNode node) {
		super.VisitOutboundFeatureNode(node);
		
		if (Strategy().InFilter(node) && !visited_nodes.contains(node.Name())) {
			PreprocessFeatureNode(node);
			PopNode();
		}
	}
}
