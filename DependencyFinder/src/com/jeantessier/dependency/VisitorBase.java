/*
 *  Dependency Finder - Computes quality factors from compiled Java code
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeantessier.dependency;

import java.util.*;

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
		
		return result;
	}

	protected void PushNode(Node current_node) {
		current_nodes.addLast(current_node);
	}

	protected Node PopNode() {
		Node result = (Node) current_nodes.removeLast();
		return result;
	}

	public SortedSet Scope() {
		return scope;
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
		Scope().add(node);
		PushNode(node);
	}
	
	protected void PreprocessAfterDependenciesPackageNode(PackageNode node) {
		// Do nothing
	}
	
	protected void PostprocessBeforeDependenciesPackageNode(PackageNode node) {
		// Do nothing
	}
	
	protected void PostprocessPackageNode(PackageNode node) {
		PopNode();
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
		Scope().add(node.Package());
		PushNode(node);
	}
	
	protected void PreprocessAfterDependenciesClassNode(ClassNode node) {
		// Do nothing
	}

	protected void PostprocessBeforeDependenciesClassNode(ClassNode node) {
		// Do nothing
	}

	protected void PostprocessClassNode(ClassNode node) {
		PopNode();
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
		Scope().add(node.Class().Package());
		PushNode(node);
	}
	
	protected void PostprocessFeatureNode(FeatureNode node) {
		PopNode();
	}

	public void VisitInboundFeatureNode(FeatureNode node) {
		// Do nothing
	}

	public void VisitOutboundFeatureNode(FeatureNode node) {
		// Do nothing
	}
}
