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

import org.apache.log4j.*;

/**
 *  This is a basic implementation of Visitor.
 *  
 *  @see Visitor
 *  @author Jean Tessier
 */
public abstract class VisitorBase implements Visitor {
	private TraversalStrategy strategy;

	private LinkedList current_nodes = new LinkedList();
	private SortedSet  scope         = new TreeSet();
	
	public VisitorBase() {
		this(new SelectiveTraversalStrategy());
	}

	public VisitorBase(TraversalStrategy strategy) {
		this.strategy = strategy;
	}

	protected TraversalStrategy Strategy() {
		return strategy;
	}
    
	public void TraverseNodes(Collection nodes) {
		Iterator i = Strategy().Order(nodes).iterator();
		while (i.hasNext()) {
			((Node) i.next()).Accept(this);
		}
	}

	protected void TraverseInbound(Collection nodes) {
		Iterator i = Strategy().Order(nodes).iterator();
		while (i.hasNext()) {
			((Node) i.next()).AcceptInbound(this);
		}
	}

	protected void TraverseOutbound(Collection nodes) {
		Iterator i = Strategy().Order(nodes).iterator();
		while (i.hasNext()) {
			((Node) i.next()).AcceptOutbound(this);
		}
	}

	protected Node CurrentNode() {
		Node result = null;

		if (!current_nodes.isEmpty()) {
			result = (Node) current_nodes.getLast();
		}

		Logger.getLogger(getClass()).debug(current_nodes + ": " + result);
		
		return result;
	}

	protected void PushNode(Node current_node) {
		Logger.getLogger(getClass()).debug(current_nodes + " + " + current_node);
		current_nodes.addLast(current_node);
	}

	protected Node PopNode() {
		Node result = (Node) current_nodes.removeLast();

		Logger.getLogger(getClass()).debug(current_nodes + " -> " + result);
		
		return result;
	}

	public void VisitPackageNode(PackageNode node) {
		boolean in_scope = Strategy().InScope(node);
		
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
		PushNode(node);
	}
	
	protected void PreprocessAfterDependenciesPackageNode(PackageNode node) {
		// Do nothing
	}
	
	protected void PostprocessBeforeDependenciesPackageNode(PackageNode node) {
		// Do nothing
	}
	
	protected void PostprocessPackageNode(PackageNode node) {
		if (node.equals(CurrentNode())) {
			PopNode();
		}
	}
	
	public void VisitInboundPackageNode(PackageNode node) {
		// Do nothing
	}

	public void VisitOutboundPackageNode(PackageNode node) {
		// Do nothing
	}

	public void VisitClassNode(ClassNode node) {
		boolean in_scope = Strategy().InScope(node);
		
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
		PushNode(node);
	}
	
	protected void PreprocessAfterDependenciesClassNode(ClassNode node) {
		// Do nothing
	}

	protected void PostprocessBeforeDependenciesClassNode(ClassNode node) {
		// Do nothing
	}

	protected void PostprocessClassNode(ClassNode node) {
		if (node.equals(CurrentNode())) {
			PopNode();
		}
	}

	public void VisitInboundClassNode(ClassNode node) {
		// Do nothing
	}

	public void VisitOutboundClassNode(ClassNode node) {
		// Do nothing
	}

	public void VisitFeatureNode(FeatureNode node) {
		if (Strategy().InScope(node)) {
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
		PushNode(node);
	}
	
	protected void PostprocessFeatureNode(FeatureNode node) {
		if (node.equals(CurrentNode())) {
			PopNode();
		}
	}

	public void VisitInboundFeatureNode(FeatureNode node) {
		// Do nothing
	}

	public void VisitOutboundFeatureNode(FeatureNode node) {
		// Do nothing
	}
}
