/*
 *  Copyright (c) 2001-2003, Jean Tessier
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

public class TestMetricsFactory extends TestCase {
	MetricsConfiguration configuration;
	MetricsFactory       factory;
	
	protected void setUp() throws Exception {
		configuration = new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).Load("etc" + File.separator + "MetricsConfig.xml");
		factory       = new MetricsFactory("test", configuration);
	}
	
	public void testCreateProjectMetrics() {
		Metrics m1 = factory.CreateProjectMetrics("foo");
		assertNotNull(m1);
		assertEquals("New metrics name", "foo", m1.Name());

		Metrics m2 = factory.CreateProjectMetrics("foo");
		assertSame(m1, m2);
		assertEquals("project measurements", configuration.ProjectMeasurements().size(), m1.MeasurementNames().size());
	}
			
	public void testIncludeProjectMetrics() {
		Metrics m1 = factory.CreateProjectMetrics("foo");

		assertFalse("ProjectMetrics() contains external metrics", factory.ProjectMetrics().contains(m1));

		factory.IncludeProjectMetrics(m1);

		assertTrue("ProjectMetrics() does not contain internal metrics", factory.ProjectMetrics().contains(m1));
	}
	
	public void testCreateGroupMetrics() {
		Metrics m1 = factory.CreateGroupMetrics("foo");
		assertNotNull(m1);
		assertEquals("New metrics name", "foo", m1.Name());

		Metrics m2 = factory.CreateGroupMetrics("foo");
		assertSame(m1, m2);
		assertEquals("group measurements", configuration.GroupMeasurements().size(), m1.MeasurementNames().size());
	}
			
	public void testIncludeGroupMetrics() {
		Metrics m1 = factory.CreateGroupMetrics("foo");

		assertFalse("GroupMetrics() contains external metrics", factory.GroupMetrics().contains(m1));

		factory.IncludeGroupMetrics(m1);

		assertTrue("GroupMetrics() does not contain internal metrics", factory.GroupMetrics().contains(m1));
	}

	public void testCreateClassMetrics() {
		Metrics m1 = factory.CreateClassMetrics("foo");
		assertNotNull(m1);
		assertEquals("New metrics name", "foo", m1.Name());

		Metrics m2 = factory.CreateClassMetrics("foo");
		assertSame(m1, m2);
		assertEquals("class measurements", configuration.ClassMeasurements().size(), m1.MeasurementNames().size());
	}
		
	public void testIncludeClassMetrics() {
		Metrics m1 = factory.CreateClassMetrics("foo");

		assertFalse("ClassMetrics() contains external metrics", factory.ClassMetrics().contains(m1));

		factory.IncludeClassMetrics(m1);

		assertTrue("ClassMetrics() does not contain internal metrics", factory.ClassMetrics().contains(m1));
	}

	public void testCreateMethodMetrics() {
		Metrics m1 = factory.CreateMethodMetrics("foo");
		assertNotNull(m1);
		assertEquals("New metrics name", "foo", m1.Name());

		Metrics m2 = factory.CreateMethodMetrics("foo");
		assertSame(m1, m2);
		assertEquals("method measurements", configuration.MethodMeasurements().size(), m1.MeasurementNames().size());
	}
	
	public void testIncludeMethodMetrics() {
		Metrics m1 = factory.CreateMethodMetrics("foo");

		assertFalse("MethodMetrics() contains external metrics", factory.MethodMetrics().contains(m1));

		factory.IncludeMethodMetrics(m1);

		assertTrue("MethodMetrics() does not contain internal metrics", factory.MethodMetrics().contains(m1));
	}
	
	public void testCreateStaticInitializerMetrics() {
		Metrics m = factory.CreateMethodMetrics("foo.static {}");

		assertEquals("class name", "foo", m.Parent().Name());
	}

	public void testCreateStructure() {
		Metrics method_metrics  = factory.CreateMethodMetrics("a.A.a()");
		Metrics class_metrics   = factory.CreateClassMetrics("a.A");
		Metrics package_metrics = factory.CreateGroupMetrics("a");
		Metrics project_metrics = factory.CreateProjectMetrics();

		factory.IncludeMethodMetrics(method_metrics);
		
		assertTrue(project_metrics.SubMetrics().contains(package_metrics));
		assertTrue(package_metrics.SubMetrics().contains(class_metrics));
		assertTrue(class_metrics.SubMetrics().contains(method_metrics));
	}

	public void testGroupDefinitionsWithInternal() {
		configuration.AddGroupDefinition("foo", "/foo/");
		configuration.AddGroupDefinition("bar", "/bar/");
		configuration.AddGroupDefinition("baz", "/baz/");

		Metrics metrics = factory.CreateClassMetrics("com.foobar.Foobar");
		factory.IncludeClassMetrics(metrics);

		assertEquals("Number of groups",     3, factory.GroupMetrics().size());
		assertEquals("Number of all groups", 3, factory.AllGroupMetrics().size());

		assertTrue("Group foo missing",        factory.AllGroupNames().contains("foo"));
		assertTrue("Group bar missing",        factory.AllGroupNames().contains("bar"));
		assertFalse("Group baz present",       factory.AllGroupNames().contains("baz"));
		assertTrue("Group com.foobar missing", factory.AllGroupNames().contains("com.foobar"));

		assertTrue("Group foo missing",        factory.GroupNames().contains("foo"));
		assertTrue("Group bar missing",        factory.GroupNames().contains("bar"));
		assertFalse("Group baz present",       factory.GroupNames().contains("baz"));
		assertTrue("Group com.foobar missing", factory.GroupNames().contains("com.foobar"));

		assertTrue("Not in foo",        factory.CreateGroupMetrics("foo").SubMetrics().contains(metrics));
		assertTrue("Not in bar",        factory.CreateGroupMetrics("bar").SubMetrics().contains(metrics));
		assertFalse("In baz",           factory.CreateGroupMetrics("baz").SubMetrics().contains(metrics));
		assertTrue("Not in com.foobar", factory.CreateGroupMetrics("com.foobar").SubMetrics().contains(metrics));

		assertEquals("Wrong parent", factory.CreateGroupMetrics("com.foobar"), metrics.Parent());
	}

	public void testGroupDefinitionsWithExternal() {
		configuration.AddGroupDefinition("foo", "/foo/");
		configuration.AddGroupDefinition("bar", "/bar/");
		configuration.AddGroupDefinition("baz", "/baz/");

		Metrics metrics = factory.CreateClassMetrics("com.foobar.Foobar");

		assertEquals("Number of groups",     0, factory.GroupMetrics().size());
		assertEquals("Number of all groups", 1, factory.AllGroupMetrics().size());

		assertFalse("Group foo present",       factory.AllGroupNames().contains("foo"));
		assertFalse("Group bar present",       factory.AllGroupNames().contains("bar"));
		assertFalse("Group baz present",       factory.AllGroupNames().contains("baz"));
		assertTrue("Group com.foobar missing", factory.AllGroupNames().contains("com.foobar"));

		assertFalse("In foo",        factory.CreateGroupMetrics("foo").SubMetrics().contains(metrics));
		assertFalse("In bar",        factory.CreateGroupMetrics("bar").SubMetrics().contains(metrics));
		assertFalse("In baz",        factory.CreateGroupMetrics("baz").SubMetrics().contains(metrics));
		assertFalse("In com.foobar", factory.CreateGroupMetrics("com.foobar").SubMetrics().contains(metrics));

		assertEquals("Wrong parent", factory.CreateGroupMetrics("com.foobar"), metrics.Parent());
	}

	public void testGroupDefinitionsWithBoth() {
		configuration.AddGroupDefinition("foo", "/foo/");
		configuration.AddGroupDefinition("baz", "/baz/");

		Metrics foo_metrics    = factory.CreateClassMetrics("com.foo.Foo");
		Metrics foobaz_metrics = factory.CreateClassMetrics("com.baz.Foobaz");

		factory.IncludeClassMetrics(foo_metrics);

		assertEquals("Number of groups",     2, factory.GroupMetrics().size());

		assertTrue("Group foo missing",      factory.GroupNames().contains("foo"));
		assertFalse("Group baz present",     factory.GroupNames().contains("baz"));
		assertTrue("Group com.foo missing",  factory.GroupNames().contains("com.foo"));
		assertFalse("Group com.baz missing", factory.GroupNames().contains("com.baz"));

		assertTrue("Not in foo",     factory.CreateGroupMetrics("foo").SubMetrics().contains(foo_metrics));
		assertTrue("Not in com.foo", factory.CreateGroupMetrics("com.foo").SubMetrics().contains(foo_metrics));

		assertEquals("foo.size()",     1, factory.CreateGroupMetrics("foo").SubMetrics().size());
		assertEquals("com.foo.size()", 1, factory.CreateGroupMetrics("com.foo").SubMetrics().size());

		assertEquals("Number of all groups", 3, factory.AllGroupMetrics().size());

		assertTrue("Group foo missing",     factory.AllGroupNames().contains("foo"));
		assertFalse("Group baz present",    factory.AllGroupNames().contains("baz"));
		assertTrue("Group com.foo missing", factory.AllGroupNames().contains("com.foo"));
		assertTrue("Group com.baz missing", factory.AllGroupNames().contains("com.baz"));

		assertFalse("In com.baz", factory.CreateGroupMetrics("com.baz").SubMetrics().contains(foobaz_metrics));
		
		assertEquals("com.baz.size()", 0, factory.CreateGroupMetrics("com.baz").SubMetrics().size());

		assertEquals("Wrong parent", factory.CreateGroupMetrics("com.foo"), foo_metrics.Parent());
		assertEquals("Wrong parent", factory.CreateGroupMetrics("com.baz"), foobaz_metrics.Parent());
	}
}
