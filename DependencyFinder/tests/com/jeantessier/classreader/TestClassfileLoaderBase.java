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

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.apache.log4j.*;

public abstract class TestClassfileLoaderBase extends TestCase implements LoadListener {
    public static final String TEST_DIR = "tests" + File.separator + "JarJarDiff";

    private LinkedList<LoadEvent> beginSessionEvents;
    private LinkedList<LoadEvent> beginGroupEvents;
    private LinkedList<LoadEvent> beginFileEvents;
    private LinkedList<LoadEvent> beginClassfileEvents;
    private LinkedList<LoadEvent> endClassfileEvents;
    private LinkedList<LoadEvent> endFileEvents;
    private LinkedList<LoadEvent> endGroupEvents;
    private LinkedList<LoadEvent> endSessionEvents;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        Logger.getLogger(getClass()).info("Starting test: " + getName());

        beginSessionEvents   = new LinkedList<LoadEvent>();
        beginGroupEvents     = new LinkedList<LoadEvent>();
        beginFileEvents      = new LinkedList<LoadEvent>();
        beginClassfileEvents = new LinkedList<LoadEvent>();
        endClassfileEvents   = new LinkedList<LoadEvent>();
        endFileEvents        = new LinkedList<LoadEvent>();
        endGroupEvents       = new LinkedList<LoadEvent>();
        endSessionEvents     = new LinkedList<LoadEvent>();
    }

    protected void tearDown() throws Exception {
        Logger.getLogger(getClass()).info("End of " + getName());

        super.tearDown();
    }

    protected LinkedList<LoadEvent> getBeginSessionEvents() {
        return beginSessionEvents;
    }

    protected LinkedList<LoadEvent> getBeginGroupEvents() {
        return beginGroupEvents;
    }

    protected LinkedList<LoadEvent> getBeginFileEvents() {
        return beginFileEvents;
    }

    protected LinkedList<LoadEvent> getBeginClassfileEvents() {
        return beginClassfileEvents;
    }

    protected LinkedList<LoadEvent> getEndClassfileEvents() {
        return endClassfileEvents;
    }

    protected LinkedList<LoadEvent> getEndFileEvents() {
        return endFileEvents;
    }

    protected LinkedList<LoadEvent> getEndGroupEvents() {
        return endGroupEvents;
    }

    protected LinkedList<LoadEvent> getEndSessionEvents() {
        return endSessionEvents;
    }
    
    public void beginSession(LoadEvent event) {
        getBeginSessionEvents().add(event);
    }
    
    public void beginGroup(LoadEvent event) {
        getBeginGroupEvents().add(event);
    }
    
    public void beginFile(LoadEvent event) {
        getBeginFileEvents().add(event);
    }
    
    public void beginClassfile(LoadEvent event) {
        getBeginClassfileEvents().add(event);
    }
    
    public void endClassfile(LoadEvent event) {
        getEndClassfileEvents().add(event);
    }
    
    public void endFile(LoadEvent event) {
        getEndFileEvents().add(event);
    }
    
    public void endGroup(LoadEvent event) {
        getEndGroupEvents().add(event);
    }
    
    public void endSession(LoadEvent event) {
        getEndSessionEvents().add(event);
    }
}
