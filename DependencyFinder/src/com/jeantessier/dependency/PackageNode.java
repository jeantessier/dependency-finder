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

public class PackageNode extends Node {
	private Collection classes = new HashSet();

	public PackageNode(String name) {
		super(name);
	}

	public void AddClass(ClassNode node) {
		classes.add(node);
	}

	public Collection Classes() {
		return Collections.unmodifiableCollection(classes);
	}
    
	public void AddDependency(Node node) {
		boolean ok = !Classes().contains(node);
		Iterator i = Classes().iterator();
		while (ok && i.hasNext()) {
			ClassNode class_node = (ClassNode) i.next();
			ok = !class_node.Features().contains(node);
		}
	
		if (ok) {
			super.AddDependency(node);
		}
	}

	public void Accept(Visitor visitor) {
		visitor.VisitPackageNode(this);
	}

	public void AcceptInbound(Visitor visitor) {
		visitor.VisitInboundPackageNode(this);
	}

	public void AcceptOutbound(Visitor visitor) {
		visitor.VisitOutboundPackageNode(this);
	}
}
