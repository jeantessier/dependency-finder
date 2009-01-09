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

package com.jeantessier.dependencyfinder.ant;

import org.apache.tools.ant.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;
import com.jeantessier.dependencyfinder.*;
import com.jeantessier.metrics.*;

public class VerboseListener extends VerboseListenerBase implements DependencyListener, MetricsListener {
    private Task task;

    public VerboseListener(Task task) {
        this.task = task;
    }
    
    public void beginSession(LoadEvent event) {
        super.beginSession(event);
        
        task.log("Searching for classes ...", Project.MSG_VERBOSE);
    }
    
    public void beginGroup(LoadEvent event) {
        super.beginGroup(event);

        switch (getCurrentGroup().getSize()) {
            case -1:
                task.log("Searching " + getCurrentGroup().getName() + " ...", Project.MSG_VERBOSE);
                break;

            case 0:
            case 1:
                task.log("Searching " + getCurrentGroup().getName() + " (" + getCurrentGroup().getSize() + " file) ...", Project.MSG_VERBOSE);
                break;

            default:
                task.log("Searching " + getCurrentGroup().getName() + " (" + getCurrentGroup().getSize() + " files) ...", Project.MSG_VERBOSE);
                break;
        }
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
    
    public void beginSession(DependencyEvent event) {
        // Do nothing
    }

    public void beginClass(DependencyEvent event) {
        task.log("Getting dependencies from " + event.getClassName() + " ...", Project.MSG_VERBOSE);
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
        task.log("Computing metrics for " + event.getClassfile() + " ...", Project.MSG_VERBOSE);
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
