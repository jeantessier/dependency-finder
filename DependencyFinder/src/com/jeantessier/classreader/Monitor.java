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

package com.jeantessier.classreader;

import java.util.*;

import org.apache.log4j.*;

import com.google.common.annotations.*;
import com.google.common.collect.*;

/**
 * TODO Class comments
 */
public class Monitor extends LoadListenerVisitorAdapter {
    private RemoveVisitor removeVisitor;
    
    private Map<String, String> fileToClass = Maps.newHashMap();
    private boolean closedSession = true;

    @VisibleForTesting
    Collection<String> previousFiles = Sets.newTreeSet();

    @VisibleForTesting
    Collection<String> currentFiles = Sets.newTreeSet();

    public Monitor(Visitor addVisitor, RemoveVisitor removeVisitor) {
        super(addVisitor);

        this.removeVisitor = removeVisitor;
    }

    public boolean isClosedSession() {
        return closedSession;
    }
    
    public void setClosedSession(boolean closedSession) {
        if (!this.closedSession && closedSession) {
            closeSession();
        }
        
        this.closedSession = closedSession;
    }
    
    public void beginFile(LoadEvent event) {
        Logger.getLogger(getClass()).debug("beginFile(..., " + event.getFilename() + ", ...)");
        
        currentFiles.add(event.getFilename());
    }

    public void endClassfile(LoadEvent event) {
        Logger.getLogger(getClass()).debug("endClassfile(..., " + event.getFilename() + ", " + event.getClassfile() + ")");
        
        if (previousFiles.contains(event.getFilename())) {
            Logger.getLogger(getClass()).debug("Removing " + event.getClassfile() + " ...");
            removeVisitor.removeClass(event.getClassfile().getClassName());
        }
        
        super.endClassfile(event);

        fileToClass.put(event.getFilename(), event.getClassfile().getClassName());
    }
    
    public void endFile(LoadEvent event) {
        Logger.getLogger(getClass()).debug("endFile(..., " + event.getFilename() + ", ...)");
        
        previousFiles.remove(event.getFilename());
    }
    
    public void endSession(LoadEvent event) {
        Logger.getLogger(getClass()).debug("endSession(...)");

        if (isClosedSession()) {
            removeUnreadFiles();
            closeSession();
        }
    }

    private void removeUnreadFiles() {
        for (String previousFile : previousFiles) {
            String classname = fileToClass.get(previousFile);
            Logger.getLogger(getClass()).debug("Removing " + classname + " ...");
            removeVisitor.removeClass(classname);
        }
    }

    private void closeSession() {
        previousFiles = currentFiles;
        currentFiles = new TreeSet<String>();
    }
}
