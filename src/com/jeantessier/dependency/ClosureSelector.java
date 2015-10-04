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

public abstract class ClosureSelector implements Visitor {
    private Collection<Node> selectedNodes;
    private Collection<Node> copiedNodes;

    private NodeFactory factory;
    
    public ClosureSelector() {
        reset();
    }
    
    public ClosureSelector(NodeFactory factory) {
        this();
        setFactory(factory);
    }

    public void reset() {
        selectedNodes = new HashSet<Node>();
        copiedNodes = new HashSet<Node>();
    }

    public NodeFactory getFactory() {
        return factory;
    }

    public void setFactory(NodeFactory factory) {
        this.factory = factory;
    }
    
    public Collection<Node> getSelectedNodes() {
        return selectedNodes;
    }

    public Collection<Node> getCopiedNodes() {
        return copiedNodes;
    }
    
    public void traverseNodes(Collection<? extends Node> nodes) {
        for (Node node : nodes) {
            node.accept(this);
        }
    }

    public void traverseInbound(Collection<? extends Node> nodes) {
        for (Node node : nodes) {
            node.acceptInbound(this);
        }
    }

    public void traverseOutbound(Collection<? extends Node> nodes) {
        for (Node node : nodes) {
            node.acceptOutbound(this);
        }
    }
}
