/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

/**
 *  Creates a sub-graph of Nodes based on a scope and
 *  filtering rules.  To get all transitive dependencies,
 *  the visited graph should be maximized first with
 *  LinkMaximizer.  Otherwise, you will only get a subset
 *  of the explicit dependencies.
 */
public class TransitiveClosure {
    public static final long DO_NOT_FOLLOW = -1;
    public static final long UNBOUNDED_DEPTH = Long.MAX_VALUE;
    
    private long maximumInboundDepth = DO_NOT_FOLLOW;
    private long maximumOutboundDepth = UNBOUNDED_DEPTH;

    private final SelectionCriteria startCriteria;
    private final SelectionCriteria stopCriteria;

    private final NodeFactory factory = new NodeFactory();
    
    public TransitiveClosure(SelectionCriteria startCriteria, SelectionCriteria stopCriteria) {
        this.startCriteria = startCriteria;
        this.stopCriteria  = stopCriteria;
    }

    public NodeFactory getFactory() {
        return factory;
    }

    public void setMaximumInboundDepth(long maximumInboundDepth) {
        this.maximumInboundDepth  = maximumInboundDepth;
    }

    public void setMaximumOutboundDepth(long maximumOutboundDepth) {
        this.maximumOutboundDepth = maximumOutboundDepth;
    }

    public void traverseNodes(Collection<? extends Node> nodes) {
        if (maximumInboundDepth != DO_NOT_FOLLOW) {
            compute(nodes, maximumInboundDepth, new ClosureInboundSelector());
        }
        
        if (maximumOutboundDepth != DO_NOT_FOLLOW) {
            compute(nodes, maximumOutboundDepth, new ClosureOutboundSelector());
        }
    }

    private void compute(Collection<? extends Node> nodes, long depth, ClosureLayerSelector layerSelector) {
        TransitiveClosureEngine engine = new TransitiveClosureEngine(factory, nodes, startCriteria, stopCriteria, layerSelector);

        if (depth == UNBOUNDED_DEPTH) {
            engine.computeAllLayers();
        } else {
            engine.computeLayers(depth);
        }
    }
}
