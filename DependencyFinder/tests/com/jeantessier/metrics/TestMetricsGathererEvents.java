/*
 *  Copyright (c) 2001-2002, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of the Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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

import junit.framework.*;

import java.io.*;
import java.util.*;

import org.xml.sax.*;

import com.jeantessier.classreader.*;

public class TestMetricsGathererEvents extends TestCase implements MetricsListener {
	public static final String TEST_CLASS    = "test";
	public static final String TEST_FILENAME = "classes" + File.separator + "test.class";
	public static final String TEST_DIRNAME  = "classes" + File.separator + "testpackage";
	public static final String OTHER_DIRNAME = "classes" + File.separator + "otherpackage";

	private ClassfileLoader loader;
	private MetricsGatherer gatherer;

	private LinkedList start_class;
	private LinkedList start_method;
	private LinkedList stop_method;
	private LinkedList stop_class;
	
	protected void setUp() throws Exception {
		loader  = new AggregatingClassfileLoader();

		MetricsFactory factory = new MetricsFactory("test", new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).Load("etc" + File.separator + "MetricsConfig.xml"));
		gatherer = new MetricsGatherer("test", factory);
		gatherer.addMetricsListener(this);

		start_class  = new LinkedList();
		start_method = new LinkedList();
		stop_method  = new LinkedList();
		stop_class   = new LinkedList();
	}
	
	public void testEvents() throws IOException {
		loader.Load(Collections.singleton(TEST_FILENAME));

		Iterator i = loader.Classfiles().iterator();
		while (i.hasNext()) {
			((Classfile) i.next()).Accept(gatherer);
		}

		assertEquals("Start Class",  1, start_class.size());
		assertEquals("Start Method", 2, start_method.size());
		assertEquals("Stop Method",  2, stop_method.size());
		assertEquals("Stop Class",   1, stop_class.size());

		assertEquals(loader.Classfile(TEST_CLASS), ((MetricsEvent) start_class.getLast()).Classfile());
		assertEquals(loader.Classfile(TEST_CLASS), ((MetricsEvent) stop_class.getLast()).Classfile());
	}
	
	public void testMultipleEvents() throws IOException {
		Collection dirs = new ArrayList(2);
		dirs.add(TEST_DIRNAME);
		dirs.add(OTHER_DIRNAME);
		loader.Load(dirs);

		Iterator i = loader.Classfiles().iterator();
		while (i.hasNext()) {
			((Classfile) i.next()).Accept(gatherer);
		}

		assertEquals("Start Class",   6, start_class.size());
		assertEquals("Start Method", 12, start_method.size());
		assertEquals("Stop Method",  12, stop_method.size());
		assertEquals("Stop Class",    6, stop_class.size());
	}	
	
	public void testEventsWithNothing() throws IOException {
		loader.Load(Collections.EMPTY_SET);

		Iterator i = loader.Classfiles().iterator();
		while (i.hasNext()) {
			((Classfile) i.next()).Accept(gatherer);
		}

		assertEquals("Start Class",  0, start_class.size());
		assertEquals("Start Method", 0, start_method.size());
		assertEquals("Stop Method",  0, stop_method.size());
		assertEquals("Stop Class",   0, stop_class.size());
	}	
	
	public void StartClass(MetricsEvent event) {
		start_class.add(event);
	}
	
	public void StartMethod(MetricsEvent event) {
		start_method.add(event);
	}
	
	public void StopMethod(MetricsEvent event) {
		stop_method.add(event);
	}
	
	public void StopClass(MetricsEvent event) {
		stop_class.add(event);
	}
}
