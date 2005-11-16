/*
 *  Copyright (c) 2001-2005, Jean Tessier
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

import org.apache.log4j.*;

/**
 *  This is a basic implementation of Visitor.
 *  
 *  @see Visitor
 *  @author Jean Tessier
 */
public abstract class VisitorBase implements Visitor {
    private TraversalStrategy strategy;

    private LinkedList currentNodes = new LinkedList();
    
    public VisitorBase() {
        this(new SelectiveTraversalStrategy());
    }

    public VisitorBase(TraversalStrategy strategy) {
        this.strategy = strategy;
    }

    protected TraversalStrategy getStrategy() {
        return strategy;
    }

    public void traverseNodes(Collection nodes) {
        Logger.getLogger(getClass()).debug("nodes = " + nodes);
        
        Iterator i = getStrategy().order(nodes).iterator();
        while (i.hasNext()) {
            ((Node) i.next()).accept(this);
        }
    }

    protected void traverseInbound(Collection nodes) {
        Iterator i = getStrategy().order(nodes).iterator();
        while (i.hasNext()) {
            ((Node) i.next()).acceptInbound(this);
        }
    }

    protected void traverseOutbound(Collection nodes) {
        Iterator i = getStrategy().order(nodes).iterator();
        while (i.hasNext()) {
            ((Node) i.next()).acceptOutbound(this);
        }
    }

    protected Node getCurrentNode() {
        Node result = null;

        if (!currentNodes.isEmpty()) {
            result = (Node) currentNodes.getLast();
        }

        if (Logger.getLogger(getClass()).isDebugEnabled()) {
            Logger.getLogger(getClass()).debug(currentNodes + ": " + result);
        }
        
        return result;
    }

    protected void pushNode(Node currentNode) {
        if (Logger.getLogger(getClass()).isDebugEnabled()) {
            Logger.getLogger(getClass()).debug(currentNodes + " + " + currentNode);
        }
        
        currentNodes.addLast(currentNode);
    }

    protected Node popNode() {
        Node result = (Node) currentNodes.removeLast();

        if (Logger.getLogger(getClass()).isDebugEnabled()) {
            Logger.getLogger(getClass()).debug(currentNodes + " -> " + result);
        }
        
        return result;
    }

    public void visitPackageNode(PackageNode node) {
        boolean inScope = isInScope(node);
        
        if (inScope) {
            preprocessPackageNode(node);
            
            if (getStrategy().doPreOutboundTraversal()) {
                traverseOutbound(node.getOutboundDependencies());
            }
            
            if (getStrategy().doPreInboundTraversal()) {
                traverseInbound(node.getInboundDependencies());
            }
            
            preprocessAfterDependenciesPackageNode(node);
        }
            
        traverseNodes(node.getClasses());

        if (inScope) {
            postprocessBeforeDependenciesPackageNode(node);

            if (getStrategy().doPostOutboundTraversal()) {
                traverseOutbound(node.getOutboundDependencies());
            }
            
            if (getStrategy().doPostInboundTraversal()) {
                traverseInbound(node.getInboundDependencies());
            }
            
            postprocessPackageNode(node);
        }
    }

    protected boolean isInScope(PackageNode node) {
        return getStrategy().isInScope(node);
    }

    protected void preprocessPackageNode(PackageNode node) {
        pushNode(node);
    }
    
    protected void preprocessAfterDependenciesPackageNode(PackageNode node) {
        // Do nothing
    }
    
    protected void postprocessBeforeDependenciesPackageNode(PackageNode node) {
        // Do nothing
    }
    
    protected void postprocessPackageNode(PackageNode node) {
        if (node.equals(getCurrentNode())) {
            popNode();
        }
    }
    
    public void visitInboundPackageNode(PackageNode node) {
        // Do nothing
    }

    public void visitOutboundPackageNode(PackageNode node) {
        // Do nothing
    }

    public void visitClassNode(ClassNode node) {
        boolean inScope = isInScope(node);
        
        if (inScope) {
            preprocessClassNode(node);
            
            if (getStrategy().doPreOutboundTraversal()) {
                traverseOutbound(node.getOutboundDependencies());
            }
            
            if (getStrategy().doPreInboundTraversal()) {
                traverseInbound(node.getInboundDependencies());
            }

            preprocessAfterDependenciesClassNode(node);
        }
        
        traverseNodes(node.getFeatures());
            
        if (inScope) {
            postprocessBeforeDependenciesClassNode(node);

            if (getStrategy().doPostOutboundTraversal()) {
                traverseOutbound(node.getOutboundDependencies());
            }
            
            if (getStrategy().doPostInboundTraversal()) {
                traverseInbound(node.getInboundDependencies());
            }
            
            postprocessClassNode(node);
        }
    }

    protected boolean isInScope(ClassNode node) {
        return getStrategy().isInScope(node);
    }

    protected void preprocessClassNode(ClassNode node) {
        pushNode(node);
    }
    
    protected void preprocessAfterDependenciesClassNode(ClassNode node) {
        // Do nothing
    }

    protected void postprocessBeforeDependenciesClassNode(ClassNode node) {
        // Do nothing
    }

    protected void postprocessClassNode(ClassNode node) {
        if (node.equals(getCurrentNode())) {
            popNode();
        }
    }

    public void visitInboundClassNode(ClassNode node) {
        // Do nothing
    }

    public void visitOutboundClassNode(ClassNode node) {
        // Do nothing
    }

    public void visitFeatureNode(FeatureNode node) {
        if (isInScope(node)) {
            preprocessFeatureNode(node);
            
            if (getStrategy().doPreOutboundTraversal()) {
                traverseOutbound(node.getOutboundDependencies());
            }
            
            if (getStrategy().doPreInboundTraversal()) {
                traverseInbound(node.getInboundDependencies());
            }
            
            if (getStrategy().doPostOutboundTraversal()) {
                traverseOutbound(node.getOutboundDependencies());
            }
            
            if (getStrategy().doPostInboundTraversal()) {
                traverseInbound(node.getInboundDependencies());
            }
            
            postprocessFeatureNode(node);
        }
    }

    protected boolean isInScope(FeatureNode node) {
        return getStrategy().isInScope(node);
    }

    protected void preprocessFeatureNode(FeatureNode node) {
        pushNode(node);
    }
    
    protected void postprocessFeatureNode(FeatureNode node) {
        if (node.equals(getCurrentNode())) {
            popNode();
        }
    }

    public void visitInboundFeatureNode(FeatureNode node) {
        // Do nothing
    }

    public void visitOutboundFeatureNode(FeatureNode node) {
        // Do nothing
    }
}
