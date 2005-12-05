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
import java.text.*;

public class HTMLPrinter extends TextPrinter {
    private MessageFormat urlFormat;

    public HTMLPrinter(PrintWriter out, MessageFormat format) {
        super(out);

        this.urlFormat = format;
    }

    public HTMLPrinter(TraversalStrategy strategy, PrintWriter out, MessageFormat format) {
        super(strategy, out);

        this.urlFormat = format;
    }

    protected void printDependencies(Map dependencies) {
        Object[] urlArgument = new Object[1];

        Iterator i = dependencies.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            urlArgument[0] = entry.getKey();
            if (((Integer) entry.getValue()).intValue() < 0) {
                indent().append("&lt;-- <a href=\"").append(urlFormat.format(urlArgument)).append("\">").printNodeName((Node) entry.getKey()).append("</a>").eol();
            } else if (((Integer) entry.getValue()).intValue() > 0) {
                indent().append("--&gt; <a href=\"").append(urlFormat.format(urlArgument)).append("\">").printNodeName((Node) entry.getKey()).append("</a>").eol();
            } else {
                indent().append("&lt;-&gt; <a href=\"").append(urlFormat.format(urlArgument)).append("\">").printNodeName((Node) entry.getKey()).append("</a>").eol();
            }
        }
    }
}