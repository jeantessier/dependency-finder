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

public class ClassNode extends Node {
	private PackageNode parent;
	private Collection  features = new HashSet();

	public ClassNode(PackageNode parent, String name) {
		super(name);
		this.parent = parent;
	}

	public PackageNode Package() {
		return parent;
	}

	public void AddFeature(FeatureNode node) {
		features.add(node);
	}

	public Collection Features() {
		return Collections.unmodifiableCollection(features);
	}
    
	public void AddDependency(Node node) {
		if (!Package().equals(node) && !Features().contains(node)) {
			super.AddDependency(node);
		}
	}

	public void Accept(Visitor visitor) {
		visitor.VisitClassNode(this);
	}

	public void AcceptInbound(Visitor visitor) {
		visitor.VisitInboundClassNode(this);
	}

	public void AcceptOutbound(Visitor visitor) {
		visitor.VisitOutboundClassNode(this);
	}
}
