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

public class XMLPrinter extends Printer {
    public static final String DEFAULT_ENCODING   = "utf-8";
    public static final String DEFAULT_DTD_PREFIX = "http://depfind.sourceforge.net/dtd";

    private boolean atTopLevel = false;

    public XMLPrinter(PrintWriter out) {
        this(out, DEFAULT_ENCODING, DEFAULT_DTD_PREFIX);
    }

    public XMLPrinter(TraversalStrategy strategy, PrintWriter out) {
        this(strategy, out, DEFAULT_ENCODING, DEFAULT_DTD_PREFIX);
    }
    
    public XMLPrinter(PrintWriter out, String encoding, String dtdPrefix) {
        super(out);
        
        appendHeader(encoding, dtdPrefix);
    }
    
    public XMLPrinter(TraversalStrategy strategy, PrintWriter out, String encoding, String dtdPrefix) {
        super(strategy, out);

        appendHeader(encoding, dtdPrefix);
    }

    private void appendHeader(String encoding, String dtdPrefix) {
        append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\" ?>").eol();
        eol();
        append("<!DOCTYPE dependencies SYSTEM \"").append(dtdPrefix).append("/dependencies.dtd\">").eol();
        eol();
    }

    public void traverseNodes(Collection<? extends Node> nodes) {
        if (atTopLevel) {
            super.traverseNodes(nodes);
        } else {
            atTopLevel = true;
            indent().append("<dependencies>").eol();
            raiseIndent();
            super.traverseNodes(nodes);
            lowerIndent();
            indent().append("</dependencies>").eol();
            atTopLevel = false;
        }
    }

    protected void preprocessPackageNode(PackageNode node) {
        super.preprocessPackageNode(node);

        if (shouldShowPackageNode(node)) {
            indent().append("<package confirmed=\"").append(node.isConfirmed() ? "yes" : "no").append("\">").eol();
            raiseIndent();
            indent().printScopeNodeName(node).eol();
        }
    }

    protected void postprocessPackageNode(PackageNode node) {
        if (shouldShowPackageNode(node)) {
            lowerIndent();
            indent().append("</package>").eol();
        }
    }

    public void visitInboundPackageNode(PackageNode node) {
        printInboundNode(node, "package");
    }

    public void visitOutboundPackageNode(PackageNode node) {
        printOutboundNode(node, "package");
    }

    protected void preprocessClassNode(ClassNode node) {
        super.preprocessClassNode(node);

        if (shouldShowClassNode(node)) {
            indent().append("<class confirmed=\"").append(node.isConfirmed() ? "yes" : "no").append("\">").eol();
            raiseIndent();
            indent().printScopeNodeName(node).eol();
        }
    }

    protected void postprocessClassNode(ClassNode node) {
        if (shouldShowClassNode(node)) {
            lowerIndent();
            indent().append("</class>").eol();
        }
    }

    public void visitInboundClassNode(ClassNode node) {
        printInboundNode(node, "class");
    }

    public void visitOutboundClassNode(ClassNode node) {
        printOutboundNode(node, "class");
    }

    protected void preprocessFeatureNode(FeatureNode node) {
        super.preprocessFeatureNode(node);

        if (shouldShowFeatureNode(node)) {
            indent().append("<feature confirmed=\"").append(node.isConfirmed() ? "yes" : "no").append("\">").eol();
            raiseIndent();
            indent().printScopeNodeName(node).eol();
        }
    }

    protected void postprocessFeatureNode(FeatureNode node) {
        if (shouldShowFeatureNode(node)) {
            lowerIndent();
            indent().append("</feature>").eol();
        }
    }

    public void visitInboundFeatureNode(FeatureNode node) {
        printInboundNode(node, "feature");
    }

    public void visitOutboundFeatureNode(FeatureNode node) {
        printOutboundNode(node, "feature");
    }

    public void printInboundNode(Node node, String type) {
        if (isShowInbounds()) {
            indent().append("<inbound type=\"").append(type).append("\" confirmed=\"").append(node.isConfirmed() ? "yes" : "no").append("\">").printDependencyNodeName(node).append("</inbound>").eol();
        }
    }

    public void printOutboundNode(Node node, String type) {
        if (isShowOutbounds()) {
            indent().append("<outbound type=\"").append(type).append("\" confirmed=\"").append(node.isConfirmed() ? "yes" : "no").append("\">").printDependencyNodeName(node).append("</outbound>").eol();
        }
    }

    protected Printer printScopeNodeName(Node node, String name) {
        append("<name>");
        super.printScopeNodeName(node, name);
        append("</name>");

        return this;
    }
}
