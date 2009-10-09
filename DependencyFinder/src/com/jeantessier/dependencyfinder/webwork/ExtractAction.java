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

package com.jeantessier.dependencyfinder.webwork;

import java.text.*;
import java.util.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;

public class ExtractAction extends ActionBase {
    private static final SimpleDateFormat START_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Date start;
    private Date stop;

    public Date getStart() {
        return start;
    }

    public Date getStop() {
        return stop;
    }

    public double getDurationInSecs() {
        return (stop.getTime() - start.getTime()) / (double) 1000;
    }
    
    public String execute() throws Exception {
        return INPUT;
    }

    public String doUpdate() {
        start = new Date();

        extractGraph();

        stop = new Date();

        application.put("updateStart", getStartText());
        application.put("updateDuration", getDurationInSecs());

        application.remove("loadStart");
        application.remove("loadDuration");

        return SUCCESS;
    }

    public String doExtract() {
        start = new Date();

        dispatcher = new ModifiedOnlyDispatcher(ClassfileLoaderEventSource.DEFAULT_DISPATCHER);
        factory = new NodeFactory();
        CodeDependencyCollector collector = new CodeDependencyCollector(factory);
        DeletingVisitor deletingVisitor = new DeletingVisitor(factory);

        monitor = new Monitor(collector, deletingVisitor);

        extractGraph();

        stop = new Date();

        application.put("dispatcher", dispatcher);
        application.put("factory", factory);
        application.put("monitor", monitor);

        application.put("extractStart", getStartText());
        application.put("extractDuration", getDurationInSecs());
        application.remove("updateStart");
        application.remove("updateDuration");

        application.remove("loadStart");
        application.remove("loadDuration");

        return SUCCESS;
    }

    private String getStartText() {
        return START_DATE_FORMATTER.format(start);
    }

    private void extractGraph() {
        ClassfileLoader loader = new TransientClassfileLoader(dispatcher);
        loader.addLoadListener(monitor);
        loader.load(getSources());

        if ("maximize".equalsIgnoreCase(mode)) {
            new LinkMaximizer().traverseNodes(factory.getPackages().values());
        } else if ("minimize".equalsIgnoreCase(mode)) {
            new LinkMinimizer().traverseNodes(factory.getPackages().values());
        }
    }
}
