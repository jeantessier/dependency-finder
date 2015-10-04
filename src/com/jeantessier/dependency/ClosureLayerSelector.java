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

public abstract class ClosureLayerSelector extends ClosureSelector {
    private Node currentNode;
    
    private Collection<? extends Node> coverage;
    
    public ClosureLayerSelector() {
        super();
    }
    
    public ClosureLayerSelector(NodeFactory factory, Collection<? extends Node> coverage) {
        super(factory);
        
        setCoverage(coverage);
    }

    public void reset() {
        super.reset();

        currentNode = null;
    }

    public Collection<? extends Node> getCoverage() {
        return coverage;
    }

    public void setCoverage(Collection<? extends Node> coverage) {
        this.coverage = coverage;
    }
    
    public void visitPackageNode(PackageNode node) {
        currentNode = getFactory().createPackage(node.getName(), node.isConfirmed());
    }
    
    public void visitInboundPackageNode(PackageNode node) {
        if (!getCoverage().contains(node)) {
            getSelectedNodes().add(node);

            Node copy = getFactory().createPackage(node.getName(), node.isConfirmed());
            getCopiedNodes().add(copy);
            copy.addDependency(currentNode);
        }
    }
    
    public void visitOutboundPackageNode(PackageNode node) {
        if (!getCoverage().contains(node)) {
            getSelectedNodes().add(node);

            Node copy = getFactory().createPackage(node.getName(), node.isConfirmed());
            getCopiedNodes().add(copy);
            currentNode.addDependency(copy);
        }
    }

    public void visitClassNode(ClassNode node) {
        currentNode = getFactory().createClass(node.getName(), node.isConfirmed());
    }
    
    public void visitInboundClassNode(ClassNode node) {
        if (!getCoverage().contains(node)) {
            getSelectedNodes().add(node);

            Node copy = getFactory().createClass(node.getName(), node.isConfirmed());
            getCopiedNodes().add(copy);
            copy.addDependency(currentNode);
        }
    }
    
    public void visitOutboundClassNode(ClassNode node) {
        if (!getCoverage().contains(node)) {
            getSelectedNodes().add(node);

            Node copy = getFactory().createClass(node.getName(), node.isConfirmed());
            getCopiedNodes().add(copy);
            currentNode.addDependency(copy);
        }
    }

    public void visitFeatureNode(FeatureNode node) {
        currentNode = getFactory().createFeature(node.getName(), node.isConfirmed());
    }
    
    public void visitInboundFeatureNode(FeatureNode node) {
        if (!getCoverage().contains(node)) {
            getSelectedNodes().add(node);

            Node copy = getFactory().createFeature(node.getName(), node.isConfirmed());
            getCopiedNodes().add(copy);
            copy.addDependency(currentNode);
        }
    }
    
    public void visitOutboundFeatureNode(FeatureNode node) {
        if (!getCoverage().contains(node)) {
            getSelectedNodes().add(node);

            Node copy = getFactory().createFeature(node.getName(), node.isConfirmed());
            getCopiedNodes().add(copy);
            currentNode.addDependency(copy);
        }
    }
}
