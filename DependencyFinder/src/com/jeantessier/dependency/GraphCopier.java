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

public class GraphCopier extends VisitorBase {
	private NodeFactory factory;

	public GraphCopier() {
		this(new NodeFactory());
	}

	public GraphCopier(TraversalStrategy strategy) {
		this(strategy, new NodeFactory());
	}

	public GraphCopier(NodeFactory factory) {
		super();

		this.factory = factory;
	}
	
	public GraphCopier(TraversalStrategy strategy, NodeFactory factory) {
		super(strategy);

		this.factory = factory;
	}
	
	public NodeFactory Factory() {
		return factory;
	}

	protected void PreprocessPackageNode(PackageNode node) {
		super.PreprocessPackageNode(Factory().CreatePackage(node.Name()));
	}

	public void VisitInboundPackageNode(PackageNode node) {
		if (Strategy().InFilter(node)) {
			Factory().CreatePackage(node.Name()).AddDependency(CurrentNode());
		}
	}

	public void VisitOutboundPackageNode(PackageNode node) {
		if (Strategy().InFilter(node)) {
			CurrentNode().AddDependency(Factory().CreatePackage(node.Name()));
		}
	}

	protected void PreprocessClassNode(ClassNode node) {
		super.PreprocessClassNode(Factory().CreateClass(node.Name()));
	}

	public void VisitInboundClassNode(ClassNode node) {
		if (Strategy().InFilter(node)) {
			Factory().CreateClass(node.Name()).AddDependency(CurrentNode());
		}
	}

	public void VisitOutboundClassNode(ClassNode node) {
		if (Strategy().InFilter(node)) {
			CurrentNode().AddDependency(Factory().CreateClass(node.Name()));
		}
	}

	protected void PreprocessFeatureNode(FeatureNode node) {
		super.PreprocessFeatureNode(Factory().CreateFeature(node.Name()));
	}

	public void VisitInboundFeatureNode(FeatureNode node) {
		if (Strategy().InFilter(node)) {
			Factory().CreateFeature(node.Name()).AddDependency(CurrentNode());
		}
	}

	public void VisitOutboundFeatureNode(FeatureNode node) {
		if (Strategy().InFilter(node)) {
			CurrentNode().AddDependency(Factory().CreateFeature(node.Name()));
		}
	}
}
