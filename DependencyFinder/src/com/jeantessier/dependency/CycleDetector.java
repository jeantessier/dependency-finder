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
