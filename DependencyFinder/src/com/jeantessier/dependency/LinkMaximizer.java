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

public class LinkMaximizer extends VisitorBase {
	public LinkMaximizer() {
		super();
	}

	public LinkMaximizer(TraversalStrategy strategy) {
		super(strategy);
	}

	protected void PostprocessClassNode(ClassNode node) {
		Iterator i = Strategy().Order(node.Outbound()).iterator();
		while (i.hasNext()) {
			node.Package().AddDependency((Node) i.next());
		}

		super.PostprocessClassNode(node);
	}

	public void VisitInboundClassNode(ClassNode node) {
		node.Package().AddDependency(CurrentNode());
	}

	public void VisitOutboundClassNode(ClassNode node) {
		CurrentNode().AddDependency(node.Package());
	}

	protected void PostprocessFeatureNode(FeatureNode node) {
		Iterator i = Strategy().Order(node.Outbound()).iterator();
		while (i.hasNext()) {
			Node outbound_node = (Node) i.next();
			node.Class().AddDependency(outbound_node);
			node.Class().Package().AddDependency(outbound_node);
		}

		super.PostprocessFeatureNode(node);
	}

	public void VisitInboundFeatureNode(FeatureNode node) {
		node.Class().AddDependency(CurrentNode());
		node.Class().Package().AddDependency(CurrentNode());
	}

	public void VisitOutboundFeatureNode(FeatureNode node) {
		CurrentNode().AddDependency(node.Class());
		CurrentNode().AddDependency(node.Class().Package());
	}
}
