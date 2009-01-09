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

public class ClassNode extends Node {
    private PackageNode packageNode;
    private Collection<FeatureNode> features = new HashSet<FeatureNode>();

    private Collection<ClassNode> parents = new HashSet<ClassNode>();
    private Collection<ClassNode> children = new HashSet<ClassNode>();

    public ClassNode(PackageNode packageNode, String name, boolean concrete) {
        super(name, concrete);
        this.packageNode = packageNode;
    }

    public String getSimpleName() {
        return getName().substring(getName().lastIndexOf('.') + 1);
    }

    // Only to be used by NodeFactory and DeletingVisitor
    void setConfirmed(boolean confirmed) {
        if (!confirmed) {
            for (FeatureNode featureNode : getFeatures()) {
                featureNode.setConfirmed(false);
            }
        }
        
        super.setConfirmed(confirmed);
        getPackageNode().setConfirmed(confirmed);
    }

    public PackageNode getPackageNode() {
        return packageNode;
    }

    public void addFeature(FeatureNode node) {
        features.add(node);
    }

    public void removeFeature(FeatureNode node) {
        features.remove(node);
    }

    public Collection<FeatureNode> getFeatures() {
        return Collections.unmodifiableCollection(features);
    }

    public boolean canAddDependencyTo(Node node) {
        return super.canAddDependencyTo(node) && getPackageNode().canAddDependencyTo(node);
    }

    public void accept(Visitor visitor) {
        visitor.visitClassNode(this);
    }

    public void acceptInbound(Visitor visitor) {
        visitor.visitInboundClassNode(this);
    }

    public void acceptOutbound(Visitor visitor) {
        visitor.visitOutboundClassNode(this);
    }

    public void addParent(ClassNode parentClass) {
        parents.add(parentClass);
        parentClass.children.add(this);
    }

    public Collection<ClassNode> getParents() {
        return Collections.unmodifiableCollection(parents);
    }

    public Collection<ClassNode> getChildren() {
        return Collections.unmodifiableCollection(children);
    }

    public FeatureNode getFeature(String featureSimpleName) {
        FeatureNode result = null;

        String targetName = getName() + "." + featureSimpleName;
        for (FeatureNode feature : getFeatures()) {
            if (feature.getName().equals(targetName)) {
                result = feature;
            }
        }

        return result;
    }

    public Collection<FeatureNode> getInheritedFeatures(String featureSimpleName) {
        Collection<FeatureNode> results = new LinkedList<FeatureNode>();

        FeatureNode featureNode = getFeature(featureSimpleName);
        if (featureNode != null) {
            results.add(featureNode);
        }

        for (ClassNode parent : getParents()) {
            results.addAll(parent.getInheritedFeatures(featureSimpleName));
        }

        return results;
    }
}
