package com.jeantessier.dependency;

import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.*;

public class JSONCyclePrinter implements CyclePrinter {
    private final PrintWriter out;

    public JSONCyclePrinter(PrintWriter out) {
        this.out = out;
    }

    public void visitCycles(Collection<Cycle> cycles) {
        out.print(printCycles(cycles));
    }

    public void visitCycle(Cycle cycle) {
        out.print(printCycle(cycle));
    }

    private String printCycles(Collection<Cycle> cycles) {
        return "[" + cycles.stream().map(this::printCycle).collect(joining(",")) + "]";
    }

    private String printCycle(Cycle cycle) {
        return "[" + cycle.getPath().stream().map(this::printNode).collect(joining(",")) + "]";
    }

    private String printNode(Node node) {
        String type;
        if (node instanceof PackageNode) {
            type = "package";
        } else if (node instanceof ClassNode) {
            type = "class";
        } else if (node instanceof FeatureNode) {
            type = "feature";
        } else {
            throw new IllegalStateException("Unexpected node type " + node.getClass().getName());
        }

        // TODO: Replace with type pattern matching in switch expression in Java 21
        // String type = switch (node) {
        //     case PackageNode packageNode -> "package";
        //     case ClassNode classNode -> "class";
        //     case FeatureNode featureNode -> "feature";
        //     default -> throw new IllegalStateException("Unexpected node type " + node.getClass().getName());
        // };

        return "{\"type\":\"" + type + "\",\"name\":\"" + node.getName() + "\"}";
    }
}
