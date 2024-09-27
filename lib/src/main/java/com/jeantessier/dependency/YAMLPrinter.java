package com.jeantessier.dependency;

import java.io.*;
import java.util.*;

public class YAMLPrinter extends Printer {
    private boolean atTopLevel = false;

    public YAMLPrinter(PrintWriter out) {
        super(out);
    }

    public YAMLPrinter(TraversalStrategy strategy, PrintWriter out) {
        super(strategy, out);
    }

    public void traverseNodes(Collection<? extends Node> nodes) {
        if (atTopLevel) {
            super.traverseNodes(nodes);
        } else {
            atTopLevel = true;
            indent().append("dependencies:").eol();
            raiseIndent();
            super.traverseNodes(nodes);
            lowerIndent();
            atTopLevel = false;
        }
    }

    public void traverseOutbound(Collection<? extends Node> nodes) {
        if (isShowOutbounds() && !nodes.isEmpty()) {
            indent().append("  outbound:").eol();
            raiseIndent();
            super.traverseOutbound(nodes);
            lowerIndent();
        } else {
            indent().append("  outbound: []").eol();
        }
    }

    public void traverseInbound(Collection<? extends Node> nodes) {
        if (isShowInbounds() && !nodes.isEmpty()) {
            indent().append("  inbound:").eol();
            raiseIndent();
            super.traverseOutbound(nodes);
            lowerIndent();
        } else {
            indent().append("  inbound: []").eol();
        }
    }

    protected void preprocessPackageNode(PackageNode node) {
        super.preprocessPackageNode(node);

        if (shouldShowPackageNode(node)) {
            indent().append("- type: package").eol();
            indent().append("  confirmed: ").append(node.isConfirmed()).eol();
            indent().append("  name: ").append(node.getName()).eol();
        }
    }

    protected void preprocessAfterDependenciesPackageNode(PackageNode node) {
        super.preprocessAfterDependenciesPackageNode(node);

        if (shouldShowPackageNode(node)) {
            if (hasVisibleClasses(node)) {
                indent().append("  classes:").eol();
            } else {
                indent().append("  classes: []").eol();
            }
            raiseIndent();
        }
    }

    protected void postprocessBeforeDependenciesPackageNode(PackageNode node) {
        super.postprocessBeforeDependenciesPackageNode(node);

        if (shouldShowPackageNode(node)) {
            lowerIndent();
        }
    }

    public void visitInboundPackageNode(PackageNode node) {
        printDependency(node, "package");
    }

    public void visitOutboundPackageNode(PackageNode node) {
        printDependency(node, "package");
    }

    protected void preprocessClassNode(ClassNode node) {
        super.preprocessClassNode(node);

        if (shouldShowClassNode(node)) {
            indent().append("- type: class").eol();
            indent().append("  confirmed: ").append(node.isConfirmed()).eol();
            indent().append("  name: ").append(node.getName()).eol();
        }
    }

    protected void preprocessAfterDependenciesClassNode(ClassNode node) {
        super.preprocessAfterDependenciesClassNode(node);

        if (shouldShowClassNode(node)) {
            if (hasVisibleFeatures(node)) {
                indent().append("  features:").eol();
            } else {
                indent().append("  features: []").eol();
            }
            raiseIndent();
        }
    }

    protected void postprocessBeforeDependenciesClassNode(ClassNode node) {
        super.postprocessBeforeDependenciesClassNode(node);

        if (shouldShowClassNode(node)) {
            lowerIndent();
        }
    }

    public void visitInboundClassNode(ClassNode node) {
        printDependency(node, "class");
    }

    public void visitOutboundClassNode(ClassNode node) {
        printDependency(node, "class");
    }

    protected void preprocessFeatureNode(FeatureNode node) {
        super.preprocessFeatureNode(node);

        if (shouldShowFeatureNode(node)) {
            indent().append("- type: feature").eol();
            indent().append("  confirmed: ").append(node.isConfirmed()).eol();
            indent().append("  name: ").append(node.getName()).eol();
        }
    }

    public void visitInboundFeatureNode(FeatureNode node) {
        printDependency(node, "feature");
    }

    public void visitOutboundFeatureNode(FeatureNode node) {
        printDependency(node, "feature");
    }

    private void printDependency(Node node, String type) {
        indent().append("- type: ").append(type).eol();
        indent().append("  confirmed: ").append(node.isConfirmed()).eol();
        indent().append("  name: ").append(node.getName()).eol();
    }
}
