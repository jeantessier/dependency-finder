/*
 *  Copyright (c) 2001-2005, Jean Tessier
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

    public void traverseNodes(Collection nodes) {
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
            indent().append("<package>").eol();
            raiseIndent();
            indent().append("<name>").append(node.getName()).append("</name>").eol();
        }
    }

    protected void postprocessPackageNode(PackageNode node) {
        if (shouldShowPackageNode(node)) {
            lowerIndent();
            indent().append("</package>").eol();
        }
    }

    public void visitInboundPackageNode(PackageNode node) {
        if (isShowInbounds()) {
            indent().append("<inbound type=\"package\">").append(node.getName()).append("</inbound>").eol();
        }
    }

    public void visitOutboundPackageNode(PackageNode node) {
        if (isShowOutbounds()) {
            indent().append("<outbound type=\"package\">").append(node.getName()).append("</outbound>").eol();
        }
    }

    protected void preprocessClassNode(ClassNode node) {
        super.preprocessClassNode(node);

        if (shouldShowClassNode(node)) {
            indent().append("<class>").eol();
            raiseIndent();
            indent().append("<name>").append(node.getName()).append("</name>").eol();
        }
    }

    protected void postprocessClassNode(ClassNode node) {
        if (shouldShowClassNode(node)) {
            lowerIndent();
            indent().append("</class>").eol();
        }
    }

    public void visitInboundClassNode(ClassNode node) {
        if (isShowInbounds()) {
            indent().append("<inbound type=\"class\">").append(node.getName()).append("</inbound>").eol();
        }
    }

    public void visitOutboundClassNode(ClassNode node) {
        if (isShowOutbounds()) {
            indent().append("<outbound type=\"class\">").append(node.getName()).append("</outbound>").eol();
        }
    }

    protected void preprocessFeatureNode(FeatureNode node) {
        super.preprocessFeatureNode(node);

        if (shouldShowFeatureNode(node)) {
            indent().append("<feature>").eol();
            raiseIndent();
            indent().append("<name>").append(node.getName()).append("</name>").eol();
        }
    }

    protected void postprocessFeatureNode(FeatureNode node) {
        if (shouldShowFeatureNode(node)) {
            lowerIndent();
            indent().append("</feature>").eol();
        }
    }

    public void visitInboundFeatureNode(FeatureNode node) {
        if (isShowInbounds()) {
            indent().append("<inbound type=\"feature\">").append(node.getName()).append("</inbound>").eol();
        }
    }

    public void visitOutboundFeatureNode(FeatureNode node) {
        if (isShowOutbounds()) {
            indent().append("<outbound type=\"feature\">").append(node.getName()).append("</outbound>").eol();
        }
    }
}
