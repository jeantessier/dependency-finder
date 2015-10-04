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

package com.jeantessier.metrics;

import java.io.*;
import java.util.*;

import junit.framework.*;

import com.jeantessier.classreader.*;

public class TestMetricsGathererEvents extends TestCase implements MetricsListener {
    public static final String TEST_CLASS = "test";
    public static final String TEST_FILENAME = "classes" + File.separator + "test.class";
    public static final String TEST_DIRNAME = "classes" + File.separator + "testpackage";
    public static final String OTHER_DIRNAME = "classes" + File.separator + "otherpackage";

    private ClassfileLoader loader;
    private MetricsGatherer gatherer;

    private LinkedList<MetricsEvent> beginSessionEvents;
    private LinkedList<MetricsEvent> beginClassEvents;
    private LinkedList<MetricsEvent> beginMethodEvents;
    private LinkedList<MetricsEvent> endMethodEvents;
    private LinkedList<MetricsEvent> endClassEvents;
    private LinkedList<MetricsEvent> endSessionEvents;

    protected void setUp() throws Exception {
        super.setUp();
        
        loader = new AggregatingClassfileLoader();

        MetricsFactory factory = new MetricsFactory("test", new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).load("etc" + File.separator + "MetricsConfig.xml"));
        gatherer = new MetricsGatherer(factory);
        gatherer.addMetricsListener(this);

        beginSessionEvents = new LinkedList<MetricsEvent>();
        beginClassEvents = new LinkedList<MetricsEvent>();
        beginMethodEvents = new LinkedList<MetricsEvent>();
        endMethodEvents = new LinkedList<MetricsEvent>();
        endClassEvents = new LinkedList<MetricsEvent>();
        endSessionEvents = new LinkedList<MetricsEvent>();
    }
    
    public void testEvents() {
        loader.load(Collections.singleton(TEST_FILENAME));

        gatherer.visitClassfiles(loader.getAllClassfiles());

        assertEquals("Begin Session",  1, beginSessionEvents.size());
        assertEquals("Begin Class",    1, beginClassEvents.size());
        assertEquals("Begin Method",   2, beginMethodEvents.size());
        assertEquals("End Method",     2, endMethodEvents.size());
        assertEquals("End Class",      1, endClassEvents.size());
        assertEquals("End Session",    1, endSessionEvents.size());

        assertEquals(loader.getClassfile(TEST_CLASS), beginClassEvents.getLast().getClassfile());
        assertEquals(loader.getClassfile(TEST_CLASS), endClassEvents.getLast().getClassfile());
    }
    
    public void testMultipleEvents() {
        Collection<String> dirs = new ArrayList<String>();
        dirs.add(TEST_DIRNAME);
        dirs.add(OTHER_DIRNAME);
        loader.load(dirs);

        gatherer.visitClassfiles(loader.getAllClassfiles());

        assertEquals("Begin Session",  1, beginSessionEvents.size());
        assertEquals("Begin Class",    9, beginClassEvents.size());
        assertEquals("Begin Method",  16, beginMethodEvents.size());
        assertEquals("End Method",    16, endMethodEvents.size());
        assertEquals("End Class",      9, endClassEvents.size());
        assertEquals("End Session",    1, endSessionEvents.size());
    }
    
    public void testEventsWithNothing() {
        loader.load(Collections.<String>emptySet());

        gatherer.visitClassfiles(loader.getAllClassfiles());

        assertEquals("Begin Session",  1, beginSessionEvents.size());
        assertEquals("Begin Class",    0, beginClassEvents.size());
        assertEquals("Begin Method",   0, beginMethodEvents.size());
        assertEquals("End Method",     0, endMethodEvents.size());
        assertEquals("End Class",      0, endClassEvents.size());
        assertEquals("End Session",    1, endSessionEvents.size());
    }
    
    public void beginSession(MetricsEvent event) {
        beginSessionEvents.add(event);
    }
    
    public void beginClass(MetricsEvent event) {
        beginClassEvents.add(event);
    }
    
    public void beginMethod(MetricsEvent event) {
        beginMethodEvents.add(event);
    }
    
    public void endMethod(MetricsEvent event) {
        endMethodEvents.add(event);
    }
    
    public void endClass(MetricsEvent event) {
        endClassEvents.add(event);
    }
    
    public void endSession(MetricsEvent event) {
        endSessionEvents.add(event);
    }
}
