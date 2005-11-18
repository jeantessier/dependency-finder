package com.jeantessier.dependency;

import java.util.*;

/**
 * TODO Class comment
 */
public class CycleDetector extends VisitorBase {
    private LinkedList currentPath = new LinkedList();
    private Collection cycles = new HashSet();
    private int maximumCycleLength = Integer.MAX_VALUE;

    public Collection getCycles() {
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

        currentPath.addLast(node);
    }

    protected void preprocessAfterDependenciesPackageNode(PackageNode node) {
        super.preprocessAfterDependenciesPackageNode(node);

        currentPath.removeLast();
    }

    public void visitOutboundPackageNode(PackageNode node) {
        super.visitOutboundPackageNode(node);

        if (getStrategy().isInFilter(node)) {
            if (currentPath.getFirst().equals(node) && currentPath.size() <= getMaximumCycleLength()) {
                Cycle cycle = new Cycle(currentPath);
                cycles.add(cycle);
            } else if (!currentPath.contains(node)){
                currentPath.addLast(node);
                traverseOutbound(node.getOutboundDependencies());
                currentPath.removeLast();
            }
        }
    }

    protected void preprocessFeatureNode(FeatureNode node) {
        super.preprocessFeatureNode(node);

        currentPath.addLast(node);
    }

    protected void postprocessFeatureNode(FeatureNode node) {
        super.postprocessFeatureNode(node);

        currentPath.removeLast();
    }

    public void visitOutboundFeatureNode(FeatureNode node) {
        super.visitOutboundFeatureNode(node);

        if (getStrategy().isInFilter(node)) {
            if (currentPath.getFirst().equals(node) && currentPath.size() <= getMaximumCycleLength()) {
                Cycle cycle = new Cycle(currentPath);
                cycles.add(cycle);
            } else if (!currentPath.contains(node)){
                currentPath.addLast(node);
                traverseOutbound(node.getOutboundDependencies());
                currentPath.removeLast();
            }
        }
    }
}
