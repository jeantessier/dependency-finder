/*
 *  Copyright (c) 2001-2003, Jean Tessier
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

public class NodeSelector implements Visitor {
	private Collection selected_nodes = new HashSet();
	private Collection copied_nodes   = new HashSet();

	private NodeFactory       factory;
	private SelectionCriteria criteria;
	
	public NodeSelector(NodeFactory factory, SelectionCriteria criteria) {
		this.factory  = factory;
		this.criteria = criteria;
	}

	public Collection SelectedNodes() {
		return selected_nodes;
	}

	public Collection CopiedNodes() {
		return copied_nodes;
	}
	
	public void TraverseNodes(Collection nodes) {
		Iterator i = nodes.iterator();
		while (i.hasNext()) {
			((Node) i.next()).Accept(this);
		}
	}

	public void VisitPackageNode(PackageNode node) {
		if (criteria.Match(node)) {
			selected_nodes.add(node);
			copied_nodes.add(factory.CreatePackage(node.Name()));
		}

		TraverseNodes(node.Classes());
	}
	
	public void VisitInboundPackageNode(PackageNode node) {
		// Do nothing
	}
	
	public void VisitOutboundPackageNode(PackageNode node) {
		// Do nothing
	}

	public void VisitClassNode(ClassNode node) {
		if (criteria.Match(node)) {
			selected_nodes.add(node);
			copied_nodes.add(factory.CreateClass(node.Name()));
		}

		TraverseNodes(node.Features());
	}
	
	public void VisitInboundClassNode(ClassNode node) {
		// Do nothing
	}
	
	public void VisitOutboundClassNode(ClassNode node) {
		// Do nothing
	}

	public void VisitFeatureNode(FeatureNode node) {
		if (criteria.Match(node)) {
			selected_nodes.add(node);
			copied_nodes.add(factory.CreateFeature(node.Name()));
		}
	}
	
	public void VisitInboundFeatureNode(FeatureNode node) {
		// Do nothing
	}
	
	public void VisitOutboundFeatureNode(FeatureNode node) {
		// Do nothing
	}
}
