/*
 *  Copyright (c) 2001-2009, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
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
    private NodeFactory factory;
    private ClosureLayerSelector layerSelector;
    private ClosureStopSelector stopSelector;
    
    private Collection<Node> coverage = new HashSet<Node>();
    private LinkedList<Collection<? extends Node>> selections = new LinkedList<Collection<? extends Node>>();
    private LinkedList<Collection<? extends Node>> layers = new LinkedList<Collection<? extends Node>>();
    
    public TransitiveClosureEngine(Collection<? extends Node> packages, SelectionCriteria startCriteria, SelectionCriteria stopCriteria, ClosureLayerSelector layerSelector) {
        this(new NodeFactory(), packages, startCriteria, stopCriteria, layerSelector);
    }
    
    public TransitiveClosureEngine(NodeFactory factory, Collection<? extends Node> packages, SelectionCriteria startCriteria, SelectionCriteria stopCriteria, ClosureLayerSelector layerSelector) {
        this.factory = factory;
        
        this.layerSelector = layerSelector;
        this.layerSelector.setFactory(factory);
        this.layerSelector.setCoverage(coverage);

        this.stopSelector = new ClosureStopSelector(stopCriteria);
        
        init(packages, startCriteria);
    }

    private void init(Collection<? extends Node> packages, SelectionCriteria startCriteria) {
        ClosureStartSelector startSelector = new ClosureStartSelector(factory, startCriteria);
        startSelector.traverseNodes(packages);
        stopSelector.traverseNodes(startSelector.getCopiedNodes());
        gatherResults(startSelector);
    }

    public NodeFactory getFactory() {
        return factory;
    }

    public int getNbLayers() {
        return layers.size();
    }
    
    public Collection getLayer(int i) {
        return layers.get(i);
    }

    public void computeAllLayers() {
        while (!stopSelector.isDone()) {
            computeNextLayer();
        }
    }

    public void computeLayers(long nbLayers) {
        for (long i=0; !stopSelector.isDone() && i<nbLayers; i++) {
            computeNextLayer();
        }
    }

    public void computeNextLayer() {
        if (!stopSelector.isDone()) {
            layerSelector.reset();
            layerSelector.traverseNodes(selections.getLast());

            stopSelector.traverseNodes(layerSelector.getCopiedNodes());
            if (!layerSelector.getCopiedNodes().isEmpty()) {
                gatherResults(layerSelector);
            }
        }
    }

    private void gatherResults(ClosureSelector selector) {
        coverage.addAll(selector.getSelectedNodes());
        selections.add(selector.getSelectedNodes());
        layers.add(selector.getCopiedNodes());
    }
}
