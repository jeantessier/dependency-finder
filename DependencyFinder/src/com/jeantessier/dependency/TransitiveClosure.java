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
