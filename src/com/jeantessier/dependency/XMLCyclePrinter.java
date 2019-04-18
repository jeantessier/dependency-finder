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

public class XMLCyclePrinter implements CyclePrinter, Visitor {
    public static final String DEFAULT_ENCODING   = "utf-8";
    public static final String DEFAULT_DTD_PREFIX = "http://depfind.sourceforge.net/dtd";

    private PrintWriter out;

    private String indentText = "    ";

    public XMLCyclePrinter(PrintWriter out) {
        this(out, DEFAULT_ENCODING, DEFAULT_DTD_PREFIX);
    }

    public XMLCyclePrinter(PrintWriter out, String encoding, String dtdPrefix) {
        this.out = out;

        appendHeader(encoding, dtdPrefix);
    }

    private void appendHeader(String encoding, String dtdPrefix) {
        out.append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\" ?>").println();
        out.println();
        out.append("<!DOCTYPE dependencies SYSTEM \"").append(dtdPrefix).append("/cycles.dtd\">").println();
        out.println();
    }

    public void setIndentText(String indentText) {
        this.indentText = indentText;
    }

    public void visitCycles(Collection<Cycle> cycles) {
        out.append("<cycles>").println();

        for (Cycle cycle : cycles) {
            visitCycle(cycle);
        }
        
        out.append("</cycles>").println();
    }

    public void visitCycle(Cycle cycle) {
        out.append(indentText).append("<cycle>").println();
        traverseNodes(cycle.getPath());
        out.append(indentText).append("</cycle>").println();
    }

    public void traverseNodes(Collection<? extends Node> nodes) {
        for (Node node : nodes) {
            node.accept(this);
        }
    }

    public void traverseInbound(Collection<? extends Node> nodes) {
        throw new UnsupportedOperationException("not implemented yet.");
    }

    public void traverseOutbound(Collection<? extends Node> nodes) {
        throw new UnsupportedOperationException("not implemented yet.");
    }

    public void visitPackageNode(PackageNode node) {
        visitNode(node, "package");
    }

    public void visitInboundPackageNode(PackageNode node) {
        // Do nothing
    }

    public void visitOutboundPackageNode(PackageNode node) {
        // Do nothing
    }

    public void visitClassNode(ClassNode node) {
        visitNode(node, "class");
    }

    public void visitInboundClassNode(ClassNode node) {
        // Do nothing
    }

    public void visitOutboundClassNode(ClassNode node) {
        // Do nothing
    }

    public void visitFeatureNode(FeatureNode node) {
        visitNode(node, "feature");
    }

    public void visitInboundFeatureNode(FeatureNode node) {
        // Do nothing
    }

    public void visitOutboundFeatureNode(FeatureNode node) {
        // Do nothing
    }

    private void visitNode(Node node, String type) {
        out.append(indentText).append(indentText).append("<node type=\"").append(type).append("\">").append(node.getName()).append("</node>").println();
    }
}
