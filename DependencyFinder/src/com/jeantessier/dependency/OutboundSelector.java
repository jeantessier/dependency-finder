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

public class OutboundSelector implements Visitor {
	private Collection selected_nodes = new HashSet();
	private Collection copied_nodes   = new HashSet();

	private Node current_node = null;
	
	private NodeFactory factory;
	private Collection  coverage;
	
	public OutboundSelector(NodeFactory factory, Collection coverage) {
		this.factory  = factory;
		this.coverage = coverage;
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

	protected void TraverseInbound(Collection nodes) {
		Iterator i = nodes.iterator();
		while (i.hasNext()) {
			((Node) i.next()).AcceptInbound(this);
		}
	}

	protected void TraverseOutbound(Collection nodes) {
		Iterator i = nodes.iterator();
		while (i.hasNext()) {
			((Node) i.next()).AcceptOutbound(this);
		}
	}

	public void VisitPackageNode(PackageNode node) {
		current_node = factory.CreatePackage(node.Name());

		TraverseOutbound(node.Outbound());
	}
	
	public void VisitInboundPackageNode(PackageNode node) {
		// Do nothing
	}
	
	public void VisitOutboundPackageNode(PackageNode node) {
		if (!coverage.contains(node)) {
			selected_nodes.add(node);

			Node copy = factory.CreatePackage(node.Name());
			copied_nodes.add(copy);
			current_node.AddDependency(copy);
		}
	}

	public void VisitClassNode(ClassNode node) {
		current_node = factory.CreateClass(node.Name());

		TraverseOutbound(node.Outbound());
	}
	
	public void VisitInboundClassNode(ClassNode node) {
		// Do nothing
	}
	
	public void VisitOutboundClassNode(ClassNode node) {
		if (!coverage.contains(node)) {
			selected_nodes.add(node);

			Node copy = factory.CreateClass(node.Name());
			copied_nodes.add(copy);
			current_node.AddDependency(copy);
		}
	}

	public void VisitFeatureNode(FeatureNode node) {
		current_node = factory.CreateFeature(node.Name());

		TraverseOutbound(node.Outbound());
	}
	
	public void VisitInboundFeatureNode(FeatureNode node) {
		// Do nothing
	}
	
	public void VisitOutboundFeatureNode(FeatureNode node) {
		if (!coverage.contains(node)) {
			selected_nodes.add(node);

			Node copy = factory.CreateFeature(node.Name());
			copied_nodes.add(copy);
			current_node.AddDependency(copy);
		}
	}
}
