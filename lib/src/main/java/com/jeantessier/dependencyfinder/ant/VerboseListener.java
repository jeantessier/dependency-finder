/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

package com.jeantessier.dependencyfinder.ant;

import org.apache.tools.ant.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;
import com.jeantessier.dependencyfinder.*;
import com.jeantessier.metrics.*;

public class VerboseListener extends VerboseListenerBase implements DependencyListener, MetricsListener {
    private final Task task;

    public VerboseListener(Task task) {
        this.task = task;
    }
    
    public void beginSession(LoadEvent event) {
        super.beginSession(event);
        
        task.log("Searching for classes ...", Project.MSG_VERBOSE);
    }
    
    public void beginGroup(LoadEvent event) {
        super.beginGroup(event);

        task.log(
                switch (getCurrentGroup().getSize()) {
                    case -1 -> "Searching " + getCurrentGroup().getName() + " ...";
                    case 1 -> "Searching " + getCurrentGroup().getName() + " (" + getCurrentGroup().getSize() + " file) ...";
                    default -> "Searching " + getCurrentGroup().getName() + " (" + getCurrentGroup().getSize() + " files) ...";
                },
                Project.MSG_VERBOSE);
    }

    public void endClassfile(LoadEvent event) {
        super.endClassfile(event);

        task.log("Loading " + event.getClassfile() + " ...", Project.MSG_VERBOSE);
    }
    
    public void endFile(LoadEvent event) {
        super.endFile(event);

        if (!getVisitedFiles().contains(event.getFilename())) {
            task.log("Skipping " + event.getFilename() + " ...", Project.MSG_VERBOSE);
        }
    }
    
    public void beginClass(DependencyEvent event) {
        DependencyListener.super.beginClass(event);

        task.log("Getting dependencies from " + event.getClassName() + " ...", Project.MSG_VERBOSE);
    }
    
    public void beginClass(MetricsEvent event) {
        MetricsListener.super.beginClass(event);

        task.log("Computing metrics for " + event.getClassfile() + " ...", Project.MSG_VERBOSE);
    }
}
