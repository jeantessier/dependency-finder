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

import java.util.*;

public class TestMetricsConfiguration extends TestCase {
	private MetricsConfiguration config;
	private MeasurementDescriptor d1;
	private MeasurementDescriptor d2;
	
	public TestMetricsConfiguration(String name) {
		super(name);
	}

	protected void setUp() {
		config = new MetricsConfiguration();

		d1 = new MeasurementDescriptor();
		d2 = new MeasurementDescriptor();
	}
	
	public void testCreate() {
		assertNotNull("ProjectMeasurements is null",  config.ProjectMeasurements());
		assertTrue("ProjectMeasurements is not emty", config.ProjectMeasurements().isEmpty());
		assertNotNull("GroupMeasurements is null",    config.GroupMeasurements());
		assertTrue("GroupMeasurements is not emty",   config.GroupMeasurements().isEmpty());
		assertNotNull("ClassMeasurements is null",    config.ClassMeasurements());
		assertTrue("ClassMeasurements is not emty",   config.ClassMeasurements().isEmpty());
		assertNotNull("MethodMeasurements is null",   config.MethodMeasurements());
		assertTrue("MethodMeasurements is not emty",  config.MethodMeasurements().isEmpty());
	}

	public void testAddProjectMeasurement() {
		assertEquals(0, config.ProjectMeasurements().size());

		config.AddProjectMeasurement(d1);
		assertEquals(1, config.ProjectMeasurements().size());
		assertTrue(config.ProjectMeasurements().contains(d1));

		config.AddProjectMeasurement(d2);
		assertEquals(2, config.ProjectMeasurements().size());
		assertTrue(config.ProjectMeasurements().contains(d2));

		assertEquals(d1, config.ProjectMeasurements().get(0));
		assertEquals(d2, config.ProjectMeasurements().get(1));
	}

	public void testAddGroupMeasurement() {
		assertEquals(0, config.GroupMeasurements().size());

		config.AddGroupMeasurement(d1);
		assertEquals(1, config.GroupMeasurements().size());
		assertTrue(config.GroupMeasurements().contains(d1));

		config.AddGroupMeasurement(d2);
		assertEquals(2, config.GroupMeasurements().size());
		assertTrue(config.GroupMeasurements().contains(d2));

		assertEquals(d1, config.GroupMeasurements().get(0));
		assertEquals(d2, config.GroupMeasurements().get(1));
	}

	public void testAddClassMeasurement() {
		assertEquals(0, config.ClassMeasurements().size());

		config.AddClassMeasurement(d1);
		assertEquals(1, config.ClassMeasurements().size());
		assertTrue(config.ClassMeasurements().contains(d1));

		config.AddClassMeasurement(d2);
		assertEquals(2, config.ClassMeasurements().size());
		assertTrue(config.ClassMeasurements().contains(d2));

		assertEquals(d1, config.ClassMeasurements().get(0));
		assertEquals(d2, config.ClassMeasurements().get(1));
	}

	public void testAddMethodMeasurement() {
		assertEquals(0, config.MethodMeasurements().size());

		config.AddMethodMeasurement(d1);
		assertEquals(1, config.MethodMeasurements().size());
		assertTrue(config.MethodMeasurements().contains(d1));

		config.AddMethodMeasurement(d2);
		assertEquals(2, config.MethodMeasurements().size());
		assertTrue(config.MethodMeasurements().contains(d2));

		assertEquals(d1, config.MethodMeasurements().get(0));
		assertEquals(d2, config.MethodMeasurements().get(1));
	}

	public void testGroupDefinitions() {
		Collection groups;

		groups = config.Groups("foobar");
		assertEquals(0, groups.size());
		
		config.AddGroupDefinition("foo", "/foo/");
		groups = config.Groups("foobar");
		assertEquals(1, groups.size());
		assertTrue(groups.contains("foo"));

		config.AddGroupDefinition("bar", "/bar/");
		groups = config.Groups("foobar");
		assertEquals(2, groups.size());
		assertTrue(groups.contains("foo"));
		assertTrue(groups.contains("bar"));

		config.AddGroupDefinition("baz", "/baz/");
		groups = config.Groups("foobar");
		assertEquals(2, groups.size());
		assertTrue(groups.contains("foo"));
		assertTrue(groups.contains("bar"));
	}
}
