/*
 *  Copyright (c) 2001-2004, Jean Tessier
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
 *  	* Neither the name of Jean Tessier nor the names of his contributors
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

	private LinkedList begin_session;
	private LinkedList begin_class;
	private LinkedList begin_method;
	private LinkedList end_method;
	private LinkedList end_class;
	private LinkedList end_session;

	protected void setUp() throws Exception {
		loader  = new AggregatingClassfileLoader();

		MetricsFactory factory = new MetricsFactory("test", new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).load("etc" + File.separator + "MetricsConfig.xml"));
		gatherer = new MetricsGatherer("test", factory);
		gatherer.addMetricsListener(this);

		begin_session = new LinkedList();
		begin_class   = new LinkedList();
		begin_method  = new LinkedList();
		end_method    = new LinkedList();
		end_class     = new LinkedList();
		end_session   = new LinkedList();
	}
	
	public void testEvents() throws IOException {
		loader.load(Collections.singleton(TEST_FILENAME));

		gatherer.visitClassfiles(loader.getAllClassfiles());

		assertEquals("Begin Session",  1, begin_session.size());
		assertEquals("Begin Class",    1, begin_class.size());
		assertEquals("Begin Method",   2, begin_method.size());
		assertEquals("End Method",     2, end_method.size());
		assertEquals("End Class",      1, end_class.size());
		assertEquals("End Session",    1, end_session.size());

		assertEquals(loader.getClassfile(TEST_CLASS), ((MetricsEvent) begin_class.getLast()).getClassfile());
		assertEquals(loader.getClassfile(TEST_CLASS), ((MetricsEvent) end_class.getLast()).getClassfile());
	}
	
	public void testMultipleEvents() throws IOException {
		Collection dirs = new ArrayList(2);
		dirs.add(TEST_DIRNAME);
		dirs.add(OTHER_DIRNAME);
		loader.load(dirs);

		gatherer.visitClassfiles(loader.getAllClassfiles());

		assertEquals("Begin Session",  1, begin_session.size());
		assertEquals("Begin Class",    9, begin_class.size());
		assertEquals("Begin Method",  16, begin_method.size());
		assertEquals("End Method",    16, end_method.size());
		assertEquals("End Class",      9, end_class.size());
		assertEquals("End Session",    1, end_session.size());
	}	
	
	public void testEventsWithNothing() throws IOException {
		loader.load(Collections.EMPTY_SET);

		gatherer.visitClassfiles(loader.getAllClassfiles());

		assertEquals("Begin Session",  1, begin_session.size());
		assertEquals("Begin Class",    0, begin_class.size());
		assertEquals("Begin Method",   0, begin_method.size());
		assertEquals("End Method",     0, end_method.size());
		assertEquals("End Class",      0, end_class.size());
		assertEquals("End Session",    1, end_session.size());
	}	
	
	public void beginSession(MetricsEvent event) {
		begin_session.add(event);
	}
	
	public void beginClass(MetricsEvent event) {
		begin_class.add(event);
	}
	
	public void beginMethod(MetricsEvent event) {
		begin_method.add(event);
	}
	
	public void endMethod(MetricsEvent event) {
		end_method.add(event);
	}
	
	public void endClass(MetricsEvent event) {
		end_class.add(event);
	}
	
	public void endSession(MetricsEvent event) {
		end_session.add(event);
	}
}
