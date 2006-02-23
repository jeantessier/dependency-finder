/*
 *  Copyright (c) 2001-2006, Jean Tessier
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

package com.jeantessier.dependencyfinder.webwork;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

import com.opensymphony.webwork.interceptor.*;
import org.apache.oro.text.perl.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;
import com.jeantessier.dependencyfinder.*;

public class ExtractAction extends ActionBase implements ServletResponseAware {
    private boolean update;

    private PrintWriter out = new NullPrintWriter();

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean getUpdate() {
        return update;
    }

    public void setServletResponse(HttpServletResponse response) {
        try {
            out = new PrintWriter(response.getOutputStream(), true);
        } catch (IOException e) {
            // Ignore
        }
    }

    public String execute() throws Exception {
        return SUCCESS;
    }

    public String doDefault() {
        return INPUT;
    }

    public String doUpdate() {
        Date start = new Date();

        extractGraph();

        Date   stop     = new Date();
        double duration = (stop.getTime() - start.getTime()) / (double) 1000;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        application.put("updateStart",    formatter.format(start));
        application.put("updateDuration", duration);

        application.remove("loadStart");
        application.remove("loadDuration");

        return SUCCESS;
    }

    public String doExtract() {
        Date start = new Date();

        dispatcher = new ModifiedOnlyDispatcher(ClassfileLoaderEventSource.DEFAULT_DISPATCHER);
        factory = new NodeFactory();
        CodeDependencyCollector collector       = new CodeDependencyCollector(factory);
        DeletingVisitor         deletingVisitor = new DeletingVisitor(factory);

        monitor = new Monitor(collector, deletingVisitor);

        extractGraph();

        Date   stop     = new Date();
        double duration = (stop.getTime() - start.getTime()) / (double) 1000;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        application.put("dispatcher", dispatcher);
        application.put("factory",    factory);
        application.put("monitor",    monitor);

        application.put("extractStart",    formatter.format(start));
        application.put("extractDuration", duration);
        application.remove("updateStart");
        application.remove("updateDuration");

        application.remove("loadStart");
        application.remove("loadDuration");

        return SUCCESS;
    }

    private void extractGraph() {
        Perl5Util perl = new Perl5Util();
        Collection sources = new LinkedList();
        perl.split(sources, "/,\\s*/", source);

        VerboseListener listener = new VerboseListener(out);

        ClassfileLoader loader = new TransientClassfileLoader(dispatcher);
        loader.addLoadListener(listener);
        loader.addLoadListener(monitor);
        loader.load(sources);

        if ("maximize".equalsIgnoreCase(mode)) {
            listener.print("Maximizing ...");
            new LinkMaximizer().traverseNodes(factory.getPackages().values());
        } else if ("minimize".equalsIgnoreCase(mode)) {
            listener.print("Minimizing ...");
            new LinkMinimizer().traverseNodes(factory.getPackages().values());
        }
    }
}
