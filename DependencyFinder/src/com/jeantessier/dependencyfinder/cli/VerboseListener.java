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

package com.jeantessier.dependencyfinder.cli;

import java.io.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;
import com.jeantessier.dependencyfinder.*;
import com.jeantessier.metrics.*;

public class VerboseListener extends VerboseListenerBase implements DependencyListener, MetricsListener {
    private PrintWriter writer = new NullPrintWriter();

    public PrintWriter getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        setWriter(new PrintWriter(writer));
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }

    public void close() {
        getWriter().close();
    }

    public void print(String s) {
        getWriter().println(s);
    }

    public void beginSession(LoadEvent event) {
        super.beginSession(event);

        getWriter().print("Searching for classes ...");
        getWriter().println();
        getWriter().flush();
    }

    public void beginGroup(LoadEvent event) {
        super.beginGroup(event);

        getWriter().print("Searching ");
        getWriter().print(getCurrentGroup().getName());

        switch (getCurrentGroup().getSize()) {
            case -1:
                break;

            case 0:
            case 1:
                getWriter().print(" (");
                getWriter().print(getCurrentGroup().getSize());
                getWriter().print(" file)");
                break;

            default:
                getWriter().print(" (");
                getWriter().print(getCurrentGroup().getSize());
                getWriter().print(" files)");
                break;
        }

        getWriter().print(" ...");
        getWriter().println();
        getWriter().flush();
    }

    public void endClassfile(LoadEvent event) {
        super.endClassfile(event);

        getWriter().print("Loading ");
        getWriter().print(event.getClassfile());
        getWriter().print(" ...");
        getWriter().println();
        getWriter().flush();
    }

    public void endFile(LoadEvent event) {
        super.endFile(event);

        if (!getVisitedFiles().contains(event.getFilename())) {
            getWriter().print("Skipping ");
            getWriter().print(event.getFilename());
            getWriter().print(" ...");
            getWriter().println();
            getWriter().flush();
        }
    }

    public void beginSession(DependencyEvent event) {
        // Do nothing
    }

    public void beginClass(DependencyEvent event) {
        getWriter().print("Getting dependencies from ");
        getWriter().print(event.getClassName());
        getWriter().print(" ...");
        getWriter().println();
        getWriter().flush();
    }

    public void dependency(DependencyEvent event) {
        // Do nothing
    }

    public void endClass(DependencyEvent event) {
        // Do nothing
    }

    public void endSession(DependencyEvent event) {
        // Do nothing
    }

    public void beginSession(MetricsEvent event) {
        // Do nothing
    }

    public void beginClass(MetricsEvent event) {
        getWriter().print("Computing metrics for ");
        getWriter().print(event.getClassfile());
        getWriter().print(" ...");
        getWriter().println();
        getWriter().flush();
    }

    public void beginMethod(MetricsEvent event) {
        // Do nothing
    }

    public void endMethod(MetricsEvent event) {
        // Do nothing
    }

    public void endClass(MetricsEvent event) {
        // Do nothing
    }

    public void endSession(MetricsEvent event) {
        // Do nothing
    }
}
