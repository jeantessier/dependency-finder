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
import java.text.*;

public class HTMLPrinter extends TextPrinter {
    private static final String FROM = "&lt;--";
    private static final String TO = "--&gt;";
    private static final String BIDIRECTIONAL = "&lt;-&gt;";

    private MessageFormat urlFormat;

    public HTMLPrinter(PrintWriter out, MessageFormat format) {
        super(out);

        this.urlFormat = format;
    }

    public HTMLPrinter(TraversalStrategy strategy, PrintWriter out, MessageFormat format) {
        super(strategy, out);

        this.urlFormat = format;
    }

    protected Printer printScopeNodeName(Node node, String name) {
        String fullName = node.getName();

        Object[] urlArgument = new Object[1];
        urlArgument[0] = perlEscapeName(fullName);
        String url = urlFormat.format(urlArgument);

        StringBuffer link = new StringBuffer("<a");
        link.append(" class=\"scope");
        if (isShowInferred() && !node.isConfirmed()) {
            link.append(" inferred");
        }
        link.append("\"");
        link.append(" href=\"").append(url).append("\"");
        link.append(" id=\"").append(fullName).append("\"");
        link.append(">");
        link.append(name);
        link.append("</a>");

        openPotentialInferredSpan(node);
        printNodeName(node, link.toString());
        closePotentialInferredSpan(node);

        return this;
    }

    protected void printDependencies(Node node, Map<Node, Integer> dependencies) {
        Object[] urlArgument = new Object[1];

        String scopeNodeName = node.getName();

        for (Map.Entry<Node, Integer> entry : dependencies.entrySet()) {
            Node dependency = entry.getKey();

            String rawName = dependency.getName();
            urlArgument[0] = perlEscapeName(rawName);
            String url = urlFormat.format(urlArgument);

            String symbol;
            String idConjunction;
            if (entry.getValue() < 0) {
                symbol = FROM;
                idConjunction = "_from_";
            } else if (entry.getValue() > 0) {
                symbol = TO;
                idConjunction = "_to_";
            } else {
                symbol = BIDIRECTIONAL;
                idConjunction = "_bidirectional_";
            }

            StringBuffer link = new StringBuffer("<a");
            if (isShowInferred() && !dependency.isConfirmed()) {
                link.append(" class=\"inferred\"");
            }
            link.append(" href=\"").append(url).append("\"");
            link.append(" id=\"").append(scopeNodeName).append(idConjunction).append(rawName).append("\"");
            link.append(">");
            link.append(rawName);
            link.append("</a>");

            indent();
            openPotentialInferredSpan(dependency);
            append(symbol).append(" ").printDependencyNodeName(dependency, link.toString());
            closePotentialInferredSpan(dependency);
            eol();
        }
    }

    private void openPotentialInferredSpan(Node node) {
        if (isShowInferred() && !node.isConfirmed()) {
            append("<span class=\"inferred\">");
        }
    }

    private void closePotentialInferredSpan(Node node) {
        if (isShowInferred() && !node.isConfirmed()) {
            append("</span>");
        }
    }

    private String perlEscapeName(String name) {
        String result = name;

        result = perl().substitute("s/\\(/\\\\(/g", result);
        result = perl().substitute("s/\\)/\\\\)/g", result);
        result = perl().substitute("s/\\$/\\\\\\$/g", result);
        result = perl().substitute("s/\\[/\\\\[/g", result);
        result = perl().substitute("s/\\]/\\\\]/g", result);

        return result;
    }
}
