/*
 *  Copyright (c) 2001-2004, Jean Tessier
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
 *  	* Neither the name of Jean Tessier nor the names of his contributors
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

public abstract class ClosureLayerSelector extends ClosureSelector {
	private Node current_node;
	
	private Collection  coverage;
	
	public ClosureLayerSelector() {
		super();
	}
	
	public ClosureLayerSelector(NodeFactory factory, Collection coverage) {
		super(factory);
		
		Coverage(coverage);
	}

	public void Reset() {
		super.Reset();

		current_node = null;
	}

	public Collection Coverage() {
		return coverage;
	}

	public void Coverage(Collection coverage) {
		this.coverage = coverage;
	}
	
	public void VisitPackageNode(PackageNode node) {
		current_node = Factory().CreatePackage(node.Name());
	}
	
	public void VisitInboundPackageNode(PackageNode node) {
		if (!Coverage().contains(node)) {
			SelectedNodes().add(node);

			Node copy = Factory().CreatePackage(node.Name());
			CopiedNodes().add(copy);
			copy.AddDependency(current_node);
		}
	}
	
	public void VisitOutboundPackageNode(PackageNode node) {
		if (!Coverage().contains(node)) {
			SelectedNodes().add(node);

			Node copy = Factory().CreatePackage(node.Name());
			CopiedNodes().add(copy);
			current_node.AddDependency(copy);
		}
	}

	public void VisitClassNode(ClassNode node) {
		current_node = Factory().CreateClass(node.Name());
	}
	
	public void VisitInboundClassNode(ClassNode node) {
		if (!Coverage().contains(node)) {
			SelectedNodes().add(node);

			Node copy = Factory().CreateClass(node.Name());
			CopiedNodes().add(copy);
			copy.AddDependency(current_node);
		}
	}
	
	public void VisitOutboundClassNode(ClassNode node) {
		if (!Coverage().contains(node)) {
			SelectedNodes().add(node);

			Node copy = Factory().CreateClass(node.Name());
			CopiedNodes().add(copy);
			current_node.AddDependency(copy);
		}
	}

	public void VisitFeatureNode(FeatureNode node) {
		current_node = Factory().CreateFeature(node.Name());
	}
	
	public void VisitInboundFeatureNode(FeatureNode node) {
		if (!Coverage().contains(node)) {
			SelectedNodes().add(node);

			Node copy = Factory().CreateFeature(node.Name());
			CopiedNodes().add(copy);
			copy.AddDependency(current_node);
		}
	}
	
	public void VisitOutboundFeatureNode(FeatureNode node) {
		if (!Coverage().contains(node)) {
			SelectedNodes().add(node);

			Node copy = Factory().CreateFeature(node.Name());
			CopiedNodes().add(copy);
			current_node.AddDependency(copy);
		}
	}
}
