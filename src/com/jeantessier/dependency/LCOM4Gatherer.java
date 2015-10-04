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

import org.apache.oro.text.perl.*;

public class LCOM4Gatherer implements Visitor {
    private static final Perl5Util perl = new Perl5Util();

    private Map<ClassNode, Collection<Collection<FeatureNode>>> results = new HashMap<ClassNode, Collection<Collection<FeatureNode>>>();
    private Collection<FeatureNode> currentComponent;
    private ClassNode currentClass;
    private LinkedList<FeatureNode> unvisitedNodes;

    private HashSet<Collection<FeatureNode>> currentComponents;

    public Map<ClassNode, Collection<Collection<FeatureNode>>> getResults() {
        return results;
    }

    public void traverseNodes(Collection<? extends Node> nodes) {
        for (Node node : nodes) {
            if (node.isConfirmed()) {
                node.accept(this);
            }
        }
    }

    public void visitPackageNode(PackageNode node) {
        traverseNodes(node.getClasses());
    }

    public void visitInboundPackageNode(PackageNode node) {
        // Do nothing
    }

    public void visitOutboundPackageNode(PackageNode node) {
        // Do nothing
    }

    public void visitClassNode(ClassNode node) {
        currentClass = node;

        currentComponents = new HashSet<Collection<FeatureNode>>();
        results.put(currentClass, currentComponents);

        unvisitedNodes = filterOutConstructors(currentClass.getFeatures());
        while (!unvisitedNodes.isEmpty()) {
            unvisitedNodes.removeFirst().accept(this);
        }
    }

    public void visitInboundClassNode(ClassNode node) {
        // Do nothing
    }

    public void visitOutboundClassNode(ClassNode node) {
        // Do nothing
    }

    public void visitFeatureNode(FeatureNode node) {
        currentComponent = new HashSet<FeatureNode>();
        currentComponents.add(currentComponent);
        currentComponent.add(node);

        traverseInbound(node.getInboundDependencies());
        traverseOutbound(node.getOutboundDependencies());
    }

    public void traverseInbound(Collection<? extends Node> inboundDependencies) {
        for (Node inboundDependency : inboundDependencies) {
            inboundDependency.acceptInbound(this);
        }
    }

    public void traverseOutbound(Collection<? extends Node> outboundDependencies) {
        for (Node outboundDependency : outboundDependencies) {
            outboundDependency.acceptOutbound(this);
        }
    }

    public void visitInboundFeatureNode(FeatureNode node) {
        visitFeatureDependency(node);
    }

    public void visitOutboundFeatureNode(FeatureNode node) {
        visitFeatureDependency(node);
    }

    private void visitFeatureDependency(FeatureNode node) {
        if (currentClass.equals(node.getClassNode()) && unvisitedNodes.contains(node)) {
            unvisitedNodes.remove(node);
            currentComponent.add(node);
            traverseInbound(node.getInboundDependencies());
            traverseOutbound(node.getOutboundDependencies());
        }
    }

    private LinkedList<FeatureNode> filterOutConstructors(Collection<FeatureNode> featureNodes) {
        LinkedList<FeatureNode> result = new LinkedList<FeatureNode>();

        for (FeatureNode featureNode : featureNodes) {
            if (featureNode.isConfirmed() && !isConstructor(featureNode)) {
                result.add(featureNode);
            }
        }

        return result;
    }

    private boolean isConstructor(FeatureNode node) {
        return perl.match("/(\\w+)\\.\\1\\(/", node.getName());
    }
}
