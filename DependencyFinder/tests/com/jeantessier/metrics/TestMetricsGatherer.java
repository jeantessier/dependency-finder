/*
 *  Dependency Finder - Computes quality factors from compiled Java code
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeantessier.metrics;

import junit.framework.*;

import java.io.*;

import com.jeantessier.classreader.*;

public class TestMetricsGatherer extends TestCase {
	public static final String TEST_CLASS    = "test";
	public static final String TEST_FILENAME = "classes" + File.separator + "test.class";
	
	public TestMetricsGatherer(String name) {
		super(name);
	}

	public void testCreateProjectMetrics() throws IOException {
		MetricsFactory  factory = new MetricsFactory("test");

		ClassfileLoader loader = new DirectoryClassfileLoader(new String[] {TEST_FILENAME});
		loader.Start();

		loader.Classfile(TEST_CLASS).Accept(new MetricsGatherer("test", factory));

		assertEquals("factory.ProjectNames().size()", 1, factory.ProjectNames().size());
		assertTrue(factory.ProjectNames().toString() + " does not contain project \"test\"", factory.ProjectNames().contains("test"));
		assertEquals("factory.GroupNames().size()",   1, factory.GroupNames().size());
		assertTrue(factory.GroupNames().toString() + " does not contain package \"\"", factory.GroupNames().contains(""));
		assertEquals("factory.ClassNames().size()",   1, factory.ClassNames().size());
		assertTrue(factory.ClassNames().toString() + " does not contain class \"test\"", factory.ClassNames().contains("test"));
		assertEquals("factory.MethodNames().size()",  2, factory.MethodNames().size());
		assertTrue(factory.MethodNames().toString() + " does not contain method \"test.test()\"", factory.MethodNames().contains("test.test()"));
		assertTrue(factory.MethodNames().toString() + " does not contain method \"test.main(java.lang.String[])\"", factory.MethodNames().contains("test.main(java.lang.String[])"));
	}
}
