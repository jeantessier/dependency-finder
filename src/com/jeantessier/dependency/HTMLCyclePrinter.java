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
import java.text.*;

import org.apache.oro.text.perl.*;

public class HTMLCyclePrinter extends TextCyclePrinter {
    private static final Perl5Util perl = new Perl5Util();

    protected static Perl5Util perl() {
        return perl;
    }

    private MessageFormat urlFormat;

    public HTMLCyclePrinter(PrintWriter out, MessageFormat urlFormat) {
        super(out);
        this.urlFormat = urlFormat;
    }

    protected void printFirstNode(Node node) {
        String fullName = node.getName();
        String url = formatUrl(fullName);

        out.print("<a class=\"scope\" href=\"");
        out.print(url);
        out.print("\" id=\"");
        out.print(fullName);
        out.print("\">");
        out.print(fullName);
        out.print("</a>");

        out.println();
    }

    protected void printNode(Node previousNode, Node currentNode) {
        String fullName = currentNode.getName();
        String url = formatUrl(fullName);

        out.print("--&gt; <a href=\"");
        out.print(url);
        out.print("\" id=\"");
        out.print(previousNode);
        out.print("_to_");
        out.print(fullName);
        out.print("\">");
        out.print(fullName);
        out.print("</a>");

        out.println();
    }

    private String formatUrl(String fullName) {
        String escapedName = fullName;
        escapedName = perl().substitute("s/\\(/\\\\(/g", escapedName);
        escapedName = perl().substitute("s/\\)/\\\\)/g", escapedName);
        escapedName = perl().substitute("s/\\$/\\\\\\$/g", escapedName);

        Object[] urlArgument = new Object[] {escapedName};
        return urlFormat.format(urlArgument);
    }
}
