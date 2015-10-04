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

package com.jeantessier.dependencyfinder.gui;

import javax.swing.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependencyfinder.*;

public class VerboseListener extends VerboseListenerBase {
    private StatusLine statusLine;
    private JProgressBar progressBar;

    public VerboseListener(StatusLine statusLine, JProgressBar progressBar) {
        this.statusLine = statusLine;
        this.progressBar = progressBar;
    }

    protected StatusLine getStatusLine() {
        return statusLine;
    }

    protected JProgressBar getProgressBar() {
        return progressBar;
    }

    public void beginSession(LoadEvent event) {
        super.beginSession(event);

        getStatusLine().showInfo("Searching for classes ...");
        getProgressBar().setValue(0);
        getProgressBar().setStringPainted(true);
    }

    public void beginGroup(LoadEvent event) {
        super.beginGroup(event);

        getStatusLine().showInfo("Loading from " + event.getGroupName() + " ...");
    }

    public void beginFile(LoadEvent event) {
        super.beginFile(event);

        if (event.getFilename().startsWith(event.getGroupName())) {
            getStatusLine().showInfo("Found " + event.getFilename() + " ...");
        } else {
            getStatusLine().showInfo("Found " + event.getGroupName() + " >> " + event.getFilename() + " ...");
        }
    }

    public void endFile(LoadEvent event) {
        super.endFile(event);

        getProgressBar().setValue(getProgressBar().getValue() + 1);
    }

    public void endSession(LoadEvent event) {
        super.endSession(event);

        getProgressBar().setValue(0);
        getProgressBar().setStringPainted(false);
    }
}
