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

import org.apache.log4j.*;

/**
 * TODO Class comment
 */
public class CycleDetector extends VisitorBase {
    private LinkedList<Node> currentPath = new LinkedList<Node>();
    private Collection<Cycle> cycles = new TreeSet<Cycle>(new CycleComparator());
    private int maximumCycleLength = Integer.MAX_VALUE;

    public CycleDetector() {
    }

    public CycleDetector(SelectionCriteria criteria) {
        super(new SelectiveTraversalStrategy(criteria, new ComprehensiveSelectionCriteria()));
    }

    public Collection<Cycle> getCycles() {
        return cycles;
    }

    public int getMaximumCycleLength() {
        return maximumCycleLength;
    }

    public void setMaximumCycleLength(int maximumCycleLength) {
        this.maximumCycleLength = maximumCycleLength;
    }

    protected void preprocessPackageNode(PackageNode node) {
        super.preprocessPackageNode(node);

        pushNodeOnCurrentPath(node);
    }

    protected void preprocessAfterDependenciesPackageNode(PackageNode node) {
        super.preprocessAfterDependenciesPackageNode(node);

        popNodeFromCurrentPath(node);
    }

    public void visitOutboundPackageNode(PackageNode node) {
        super.visitOutboundPackageNode(node);

        if (getStrategy().isInFilter(node)) {
            if (currentPath.getFirst().equals(node) && currentPath.size() <= getMaximumCycleLength()) {
                addCycle();
            } else if (!currentPath.contains(node)){
                pushNodeOnCurrentPath(node);
                traverseOutbound(node.getOutboundDependencies());
                traverseOutbound(node.getClasses());
                popNodeFromCurrentPath(node);
            }
        }
    }

    protected void preprocessClassNode(ClassNode node) {
        super.preprocessClassNode(node);

        pushNodeOnCurrentPath(node);
    }

    protected void preprocessAfterDependenciesClassNode(ClassNode node) {
        super.preprocessAfterDependenciesClassNode(node);

        popNodeFromCurrentPath(node);
    }

    public void visitOutboundClassNode(ClassNode node) {
        super.visitOutboundClassNode(node);

        if (getStrategy().isInFilter(node)) {
            if (currentPath.getFirst().equals(node) && currentPath.size() <= getMaximumCycleLength()) {
                addCycle();
            } else if (!currentPath.contains(node)){
                pushNodeOnCurrentPath(node);
                traverseOutbound(node.getOutboundDependencies());
                traverseOutbound(node.getFeatures());
                popNodeFromCurrentPath(node);
            }
        }
    }

    protected void preprocessFeatureNode(FeatureNode node) {
        super.preprocessFeatureNode(node);

        pushNodeOnCurrentPath(node);
    }

    protected void postprocessFeatureNode(FeatureNode node) {
        super.postprocessFeatureNode(node);

        popNodeFromCurrentPath(node);
    }

    public void visitOutboundFeatureNode(FeatureNode node) {
        super.visitOutboundFeatureNode(node);

        if (getStrategy().isInFilter(node)) {
            if (currentPath.getFirst().equals(node) && currentPath.size() <= getMaximumCycleLength()) {
                addCycle();
            } else if (!currentPath.contains(node)){
                pushNodeOnCurrentPath(node);
                traverseOutbound(node.getOutboundDependencies());
                popNodeFromCurrentPath(node);
            }
        }
    }

    private void addCycle() {
        Cycle cycle = new Cycle(currentPath);
        cycles.add(cycle);
        Logger.getLogger(getClass()).debug("Found cycle " + cycle);
    }

    private void pushNodeOnCurrentPath(Node node) {
        currentPath.addLast(node);
        Logger.getLogger(getClass()).debug("Pushed " + node + " on currentPath: " + currentPath);
    }

    private void popNodeFromCurrentPath(Node node) {
        Node popedNode = currentPath.removeLast();
        Logger.getLogger(getClass()).debug("Popped " + node + " (" + popedNode + ") from currentPath: " + currentPath);
    }
}
