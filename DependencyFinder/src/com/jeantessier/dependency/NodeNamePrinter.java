package com.jeantessier.dependency;

import java.io.*;
import java.util.*;

/**
 * TODO Class comment
 */
public class NodeNamePrinter implements Visitor {
    private PrintWriter out;

    public NodeNamePrinter(PrintWriter out) {
        this.out = out;
    }

    public void traverseNodes(Collection<? extends Node> nodes) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void visitPackageNode(PackageNode node) {
        out.print("package " + node);
    }

    public void visitInboundPackageNode(PackageNode node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void visitOutboundPackageNode(PackageNode node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void visitClassNode(ClassNode node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void visitInboundClassNode(ClassNode node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void visitOutboundClassNode(ClassNode node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void visitFeatureNode(FeatureNode node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void visitInboundFeatureNode(FeatureNode node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void visitOutboundFeatureNode(FeatureNode node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
