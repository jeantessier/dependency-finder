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

public class TransitiveClosureEngine {
	private ClosureLayerSelector layer_selector;
	private ClosureStopSelector  stop_selector;
	
	private NodeFactory factory    = new NodeFactory();
	private Collection  coverage   = new HashSet();
	private LinkedList  selections = new LinkedList();
	private LinkedList  layers     = new LinkedList();
	
	public TransitiveClosureEngine(Collection packages, SelectionCriteria start_criteria, SelectionCriteria stop_criteria, ClosureLayerSelector layer_selector) {
		this.layer_selector = layer_selector;
		this.layer_selector.Factory(factory);
		this.layer_selector.Coverage(coverage);

		this.stop_selector = new ClosureStopSelector(stop_criteria);
		
		Init(packages, start_criteria);
	}

	private void Init(Collection packages, SelectionCriteria start_criteria) {
		ClosureStartSelector start_selector = new ClosureStartSelector(factory, start_criteria);
		start_selector.TraverseNodes(packages);
		stop_selector.TraverseNodes(start_selector.CopiedNodes());
		GatherResults(start_selector);
	}

	public NodeFactory Factory() {
		return factory;
	}

	public int NbLayers() {
		return layers.size();
	}
	
	public Collection Layer(int i) {
		return (Collection) layers.get(i);
	}

	public void ComputeNextLayer() {
		if (!stop_selector.Done()) {
			layer_selector.Reset();
			layer_selector.TraverseNodes((Collection) selections.getLast());

			stop_selector.TraverseNodes(layer_selector.CopiedNodes());
			if (!layer_selector.CopiedNodes().isEmpty()) {
				GatherResults(layer_selector);
			}
		}
	}

	public void ComputeAllLayers() {
		while (!stop_selector.Done()) {
			ComputeNextLayer();
		}
	}

	public void ComputeLayers(int nb_layers) {
		for (int i=0; !stop_selector.Done() && i<nb_layers; i++) {
			ComputeNextLayer();
		}
	}

	private void GatherResults(ClosureSelector selector) {
		coverage.addAll(selector.SelectedNodes());
		selections.add(selector.SelectedNodes());
		layers.add(selector.CopiedNodes());
	}
}
