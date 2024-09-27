package com.jeantessier.dependency;

import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.*;

public class JSONPrinter extends Printer {
    public JSONPrinter(PrintWriter out) {
        super(out);
    }

    public JSONPrinter(TraversalStrategy strategy, PrintWriter out) {
        super(strategy, out);
    }

    public void traverseNodes(Collection<? extends Node> nodes) {
        append(printNodes(nodes));
    }

    public String printNodes(Collection<? extends Node> nodes) {
        return "[" +
                getStrategy().order(nodes).stream()
                        .map(this::printNode)
                        .filter(Objects::nonNull)
                        .collect(joining(","))
        + "]";
    }

    private String printNode(Node node) {
        if (node instanceof PackageNode packageNode && shouldShowPackageNode(packageNode)) {
            return printPackageNode(packageNode);
        } else if (node instanceof ClassNode classNode && shouldShowClassNode(classNode)) {
            return printClassNode(classNode);
        } else if (node instanceof FeatureNode featureNode && shouldShowFeatureNode(featureNode)) {
            return printFeatureNode(featureNode);
        } else {
            return null;
        }
    }

    private String printPackageNode(PackageNode packageNode) {
        return "{\"type\":\"package\",\"confirmed\":\"" + packageNode.isConfirmed() + "\",\"name\":\"" + packageNode.getName() + "\",\"outbound\":" + (isShowOutbounds() ? printDependencyNodes(packageNode.getOutboundDependencies()) : "[]") + ",\"inbound\":" + (isShowInbounds() ? printDependencyNodes(packageNode.getInboundDependencies()) : "[]") + ",\"classes\":" + printNodes(packageNode.getClasses()) + "}";
    }

    private String printClassNode(ClassNode classNode) {
        return "{\"type\":\"class\",\"confirmed\":\"" + classNode.isConfirmed() + "\",\"name\":\"" + classNode.getName() + "\",\"outbound\":" + (isShowOutbounds() ? printDependencyNodes(classNode.getOutboundDependencies()) : "[]") + ",\"inbound\":" + (isShowInbounds() ? printDependencyNodes(classNode.getInboundDependencies()) : "[]") + ",\"features\":" + printNodes(classNode.getFeatures()) + "}";
    }

    private String printFeatureNode(FeatureNode featureNode) {
        return "{\"type\":\"feature\",\"confirmed\":\"" + featureNode.isConfirmed() + "\",\"name\":\"" + featureNode.getName() + "\",\"outbound\":" + (isShowOutbounds() ? printDependencyNodes(featureNode.getOutboundDependencies()) : "[]") + ",\"inbound\":" + (isShowInbounds() ? printDependencyNodes(featureNode.getInboundDependencies()) : "[]") + "}";
    }

    public String printDependencyNodes(Collection<? extends Node> nodes) {
        return "[" +
                getStrategy().order(nodes).stream()
                        .map(this::printDependencyNode)
                        .collect(joining(","))
                + "]";
    }

    private String printDependencyNode(Node node) {
        if (node instanceof PackageNode packageNode) {
            return printDependency("package", packageNode);
        } else if (node instanceof ClassNode classNode) {
            return printDependency("class", classNode);
        } else if (node instanceof FeatureNode featureNode) {
            return printDependency("feature", featureNode);
        } else {
            throw new IllegalArgumentException("Unknown node type: " + node.getClass());
        }
    }

    private String printDependency(String type, Node node) {
        return "{\"type\":\"" + type + "\",\"confirmed\":\"" + node.isConfirmed() + "\",\"name\":\"" + node.getName() + "\"}";
    }
}
