package com.jeantessier.dependency;

import java.util.*;

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
                traverseOutbound(node.getClasses());
                currentPath.removeLast();
            }
        }
    }

    protected void preprocessClassNode(ClassNode node) {
        super.preprocessClassNode(node);

        currentPath.addLast(node);
    }

    protected void preprocessAfterDependenciesClassNode(ClassNode node) {
        super.preprocessAfterDependenciesClassNode(node);

        currentPath.removeLast();
    }

    public void visitOutboundClassNode(ClassNode node) {
        super.visitOutboundClassNode(node);

        if (getStrategy().isInFilter(node)) {
            if (currentPath.getFirst().equals(node) && currentPath.size() <= getMaximumCycleLength()) {
                Cycle cycle = new Cycle(currentPath);
                cycles.add(cycle);
            } else if (!currentPath.contains(node)){
                currentPath.addLast(node);
                traverseOutbound(node.getOutboundDependencies());
                traverseOutbound(node.getFeatures());
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
