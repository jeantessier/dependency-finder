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

import java.io.*;
import java.util.*;

public class DepthFirstPrinter implements Visitor {
    private static final String DEFAULT_INDENT_TEXT = "    ";

    private int indentLevel = 0;
    private String indentText = DEFAULT_INDENT_TEXT;

    private final PrintWriter out;
    private final SelectionCriteria scopeCriteria;

    public DepthFirstPrinter(PrintWriter out, SelectionCriteria scopeCriteria) {
        this.out = out;
        this.scopeCriteria = scopeCriteria;
    }

    public void setIndentText(String indentText) {
        this.indentText = indentText;
    }

    protected void raiseIndent() {
        indentLevel++;
    }

    protected void lowerIndent() {
        indentLevel--;
    }

    protected DepthFirstPrinter indent() {
        for (int i = 0; i < indentLevel; i++) {
            print(indentText);
        }

        return this;
    }

    protected DepthFirstPrinter eol() {
        out.println();

        return this;
    }

    protected DepthFirstPrinter print(String s) {
        out.print(s);

        return this;
    }

    public void traverseNodes(Collection<? extends Node> nodes) {
        for (Node node : nodes) {
            node.accept(this);
        }
    }

    public void traverseInbound(Collection<? extends Node> nodes) {
        for (Node node : nodes) {
            node.acceptInbound(this);
        }
    }

    public void traverseOutbound(Collection<? extends Node> nodes) {
        for (Node node : nodes) {
            node.acceptOutbound(this);
        }
    }

    public void visitPackageNode(PackageNode node) {
        if (scopeCriteria.matches(node)) {
            visitNode(node);
            eol();
        }

        traverseNodes(node.getClasses());
    }

    public void visitInboundPackageNode(PackageNode node) {
        visitInboundNode(node);
    }

    public void visitOutboundPackageNode(PackageNode node) {
        visitOutboundNode(node);
    }

    public void visitClassNode(ClassNode node) {
        if (scopeCriteria.matches(node)) {
            visitNode(node);
            eol();
        }

        traverseNodes(node.getFeatures());
    }

    public void visitInboundClassNode(ClassNode node) {
        visitInboundNode(node);
    }

    public void visitOutboundClassNode(ClassNode node) {
        visitOutboundNode(node);
    }

    public void visitFeatureNode(FeatureNode node) {
        if (scopeCriteria.matches(node)) {
            visitNode(node);
            eol();
        }
    }

    public void visitInboundFeatureNode(FeatureNode node) {
        visitInboundNode(node);
    }

    public void visitOutboundFeatureNode(FeatureNode node) {
        visitOutboundNode(node);
    }

    protected void visitNode(Node node) {
        printNode("", node);

        raiseIndent();
        traverseInbound(node.getInboundDependencies());
        traverseOutbound(node.getOutboundDependencies());
        lowerIndent();
    }

    protected void visitInboundNode(Node node) {
        printNode("<-- ", node);

        raiseIndent();
        traverseInbound(node.getInboundDependencies());
        lowerIndent();
    }

    protected void visitOutboundNode(Node node) {
        printNode("--> ", node);

        raiseIndent();
        traverseOutbound(node.getOutboundDependencies());
        lowerIndent();
    }

    private void printNode(String label, Node node) {
        indent().print(label).print(node.getName()).eol();
    }
}
