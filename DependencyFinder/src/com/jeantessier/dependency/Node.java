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
