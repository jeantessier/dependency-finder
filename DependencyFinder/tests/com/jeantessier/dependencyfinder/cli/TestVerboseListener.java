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
import java.util.*;

import javax.xml.parsers.*;

import junit.framework.*;

import org.xml.sax.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;
import com.jeantessier.metrics.*;

public class TestVerboseListener extends TestCase {
    public static final String TEST_CLASS = "test";
    public static final String TEST_FILENAME = "classes" + File.separator + "test.class";
    
    private StringWriter writer;
    private VerboseListener listener;

    protected void setUp() throws Exception {
        super.setUp();

        writer = new StringWriter();
        listener = new VerboseListener();

        listener.setWriter(writer);
    }
    
    public void testLoadListener() {
        AggregatingClassfileLoader loader = new AggregatingClassfileLoader();
        loader.addLoadListener(listener);
        loader.load(Collections.singleton(TEST_FILENAME));

        assertTrue("Wrote nothing", writer.toString().length() > 0);
    }
    
    public void testDependencyListener() {
        AggregatingClassfileLoader loader = new AggregatingClassfileLoader();
        loader.load(Collections.singleton(TEST_FILENAME));

        CodeDependencyCollector collector = new CodeDependencyCollector();
        collector.addDependencyListener(listener);
        loader.getClassfile(TEST_CLASS).accept(collector);

        assertTrue("Wrote nothing", writer.toString().length() > 0);
    }
    
    public void testMetricsListener() throws IOException, SAXException, ParserConfigurationException {
        AggregatingClassfileLoader loader = new AggregatingClassfileLoader();
        loader.load(Collections.singleton(TEST_FILENAME));

        MetricsFactory factory = new MetricsFactory("test", new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).load("etc" + File.separator + "MetricsConfig.xml"));
        com.jeantessier.metrics.MetricsGatherer gatherer = new com.jeantessier.metrics.MetricsGatherer(factory);
        gatherer.addMetricsListener(listener);
        loader.getClassfile(TEST_CLASS).accept(gatherer);

        assertTrue("Wrote nothing", writer.toString().length() > 0);
    }

    public void testPrintWriter() {
        String testText = "foobar";
        
        listener.print(testText);

        assertEquals(testText + System.getProperty("line.separator"), writer.toString());
    }
}
