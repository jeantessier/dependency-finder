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

public class LinkMinimizer extends VisitorBase {
	public LinkMinimizer() {
		super();

		Strategy().PreOutboundTraversal(false);
		Strategy().PreInboundTraversal(false);
		Strategy().PostOutboundTraversal(false);
		Strategy().PostInboundTraversal(false);
	}

	public LinkMinimizer(TraversalStrategy strategy) {
		super(strategy);

		Strategy().PreOutboundTraversal(false);
		Strategy().PreInboundTraversal(false);
		Strategy().PostOutboundTraversal(false);
		Strategy().PostInboundTraversal(false);
	}

	protected void PostprocessPackageNode(PackageNode node) {
		TraverseOutbound(node.Outbound());

		super.PostprocessPackageNode(node);
	}
	
	protected void PostprocessClassNode(ClassNode node) {
		Iterator i = Strategy().Order(node.Outbound()).iterator();
		while (i.hasNext()) {
			Node outbound = (Node) i.next();

			node.Package().RemoveDependency(outbound);

			outbound.AcceptOutbound(this);
			
			PushNode(node.Package());
			outbound.AcceptOutbound(this);
			PopNode();
		}

		super.PostprocessClassNode(node);
	}

	public void VisitOutboundClassNode(ClassNode node) {
		CurrentNode().RemoveDependency(node.Package());
	}
	
	protected void PostprocessFeatureNode(FeatureNode node) {
		Iterator i = Strategy().Order(node.Outbound()).iterator();
		while (i.hasNext()) {
			Node outbound = (Node) i.next();

			node.Class().RemoveDependency(outbound);
			node.Class().Package().RemoveDependency(outbound);

			outbound.AcceptOutbound(this);
	
			PushNode(node.Class());
			outbound.AcceptOutbound(this);
			PopNode();
	
			PushNode(node.Class().Package());
			outbound.AcceptOutbound(this);
			PopNode();
		}

		super.PostprocessFeatureNode(node);
	}

	public void VisitOutboundFeatureNode(FeatureNode node) {
		CurrentNode().RemoveDependency(node.Class());
		CurrentNode().RemoveDependency(node.Class().Package());
	}
}
