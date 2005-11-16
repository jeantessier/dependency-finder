package com.jeantessier.dependency;

import java.util.*;

/**
 * TODO Class comment
 */
public class CycleDetector extends VisitorBase {
    private LinkedList currentPath = new LinkedList();
    private Collection cycles = new HashSet();

    public Collection getCycles() {
        return cycles;
    }

    protected void preprocessPackageNode(PackageNode node) {
        super.preprocessPackageNode(node);

        currentPath.addLast(node);
    }

    protected void postprocessPackageNode(PackageNode node) {
        super.postprocessPackageNode(node);

        currentPath.removeLast();
    }

    public void visitOutboundPackageNode(PackageNode node) {
        super.visitOutboundPackageNode(node);

        if (getStrategy().isInFilter(node)) {
            if (currentPath.getFirst().equals(node)) {
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
