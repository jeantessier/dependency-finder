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

import java.io.*;
import java.util.*;

public abstract class Node implements Comparable, Serializable {
	private String     name     = "";
	private Collection inbound  = new HashSet();
	private Collection outbound = new HashSet();

	public Node(String name) {
		this.name = name;
	}

	public String Name() {
		return name;
	}

	public void AddDependency(Node node) {
		if (!equals(node)) {
			outbound.add(node);
			node.inbound.add(this);
		}
	}

	public void AddDependency(Collection nodes) {
		Iterator i = nodes.iterator();
		while (i.hasNext()) {
			AddDependency((Node) i.next());
		}
	}

	public void RemoveDependency(Node node) {
		outbound.remove(node);
		node.inbound.remove(this);
	}

	public void RemoveDependency(Collection nodes) {
		Iterator i = nodes.iterator();
		while (i.hasNext()) {
			RemoveDependency((Node) i.next());
		}
	}

	public Collection Inbound() {
		return Collections.unmodifiableCollection(inbound);
	}

	public Collection Outbound() {
		return Collections.unmodifiableCollection(outbound);
	}

	public abstract void Accept(Visitor visitor);
	public abstract void AcceptInbound(Visitor visitor);
	public abstract void AcceptOutbound(Visitor visitor);

	public int hashCode() {
		return Name().hashCode();
	}

	public boolean equals(Object object) {
		boolean result;

		if (this == object) {
			result = true;
		} else if (object == null || getClass() != object.getClass()) {
			result = false;
		} else {
			Node other = (Node) object;
			result = Name().equals(other.Name());
		}

		return result;
	}

	public int compareTo(Object object) {
		int result;

		if (this == object) {
			result = 0;
		} else if (object == null || !(object instanceof Node)) {
			throw new ClassCastException("compareTo: expected a " + getClass().getName() + " but got a " + object.getClass().getName());
		} else {
			Node other = (Node) object;
			result = Name().compareTo(other.Name());
		}

		return result;
	}

	public String toString() {
		return Name();
	}
}
