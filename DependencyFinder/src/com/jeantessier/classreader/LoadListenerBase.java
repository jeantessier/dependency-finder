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

package com.jeantessier.classreader;

import java.util.*;

public class LoadListenerBase implements LoadListener {
    private LinkedList groups         = new LinkedList();
    private Collection visitedFiles   = new HashSet();
    
    protected GroupData getCurrentGroup() {
        GroupData result = null;

        if (!groups.isEmpty()) {
            result = (GroupData) groups.getLast();
        }
        
        return result;
    }

    protected Collection getVisitedFiles() {
        return visitedFiles;
    }
    
    public void beginSession(LoadEvent event) {
        // Do nothing
    }
    
    public void beginGroup(LoadEvent event) {
        groups.add(new GroupData(event.getGroupName(), event.getSize()));
    }

    public void beginFile(LoadEvent event) {
        getCurrentGroup().incrementCount();
    }

    public void beginClassfile(LoadEvent event) {
        // Do nothing
    }

    public void endClassfile(LoadEvent event) {
        visitedFiles.add(event.getFilename());
    }

    public void endFile(LoadEvent event) {
        // Do nothing
    }

    public void endGroup(LoadEvent event) {
        visitedFiles.add(event.getGroupName());
        groups.removeLast();
    }

    public void endSession(LoadEvent event) {
        // Do nothing
    }
}
