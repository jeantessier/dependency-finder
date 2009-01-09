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

import junit.framework.*;

import java.util.*;

public class TestMetricsConfiguration extends TestCase {
    private MetricsConfiguration config;
    private MeasurementDescriptor d1;
    private MeasurementDescriptor d2;
    
    protected void setUp() {
        config = new MetricsConfiguration();

        d1 = new MeasurementDescriptor();
        d2 = new MeasurementDescriptor();
    }
    
    public void testCreate() {
        assertNotNull("ProjectMeasurements is null",  config.getProjectMeasurements());
        assertTrue("ProjectMeasurements is not emty", config.getProjectMeasurements().isEmpty());
        assertNotNull("GroupMeasurements is null",    config.getGroupMeasurements());
        assertTrue("GroupMeasurements is not emty",   config.getGroupMeasurements().isEmpty());
        assertNotNull("ClassMeasurements is null",    config.getClassMeasurements());
        assertTrue("ClassMeasurements is not emty",   config.getClassMeasurements().isEmpty());
        assertNotNull("MethodMeasurements is null",   config.getMethodMeasurements());
        assertTrue("MethodMeasurements is not emty",  config.getMethodMeasurements().isEmpty());
    }

    public void testAddProjectMeasurement() {
        assertEquals(0, config.getProjectMeasurements().size());

        config.addProjectMeasurement(d1);
        assertEquals(1, config.getProjectMeasurements().size());
        assertTrue(config.getProjectMeasurements().contains(d1));

        config.addProjectMeasurement(d2);
        assertEquals(2, config.getProjectMeasurements().size());
        assertTrue(config.getProjectMeasurements().contains(d2));

        assertEquals(d1, config.getProjectMeasurements().get(0));
        assertEquals(d2, config.getProjectMeasurements().get(1));
    }

    public void testAddGroupMeasurement() {
        assertEquals(0, config.getGroupMeasurements().size());

        config.addGroupMeasurement(d1);
        assertEquals(1, config.getGroupMeasurements().size());
        assertTrue(config.getGroupMeasurements().contains(d1));

        config.addGroupMeasurement(d2);
        assertEquals(2, config.getGroupMeasurements().size());
        assertTrue(config.getGroupMeasurements().contains(d2));

        assertEquals(d1, config.getGroupMeasurements().get(0));
        assertEquals(d2, config.getGroupMeasurements().get(1));
    }

    public void testAddClassMeasurement() {
        assertEquals(0, config.getClassMeasurements().size());

        config.addClassMeasurement(d1);
        assertEquals(1, config.getClassMeasurements().size());
        assertTrue(config.getClassMeasurements().contains(d1));

        config.addClassMeasurement(d2);
        assertEquals(2, config.getClassMeasurements().size());
        assertTrue(config.getClassMeasurements().contains(d2));

        assertEquals(d1, config.getClassMeasurements().get(0));
        assertEquals(d2, config.getClassMeasurements().get(1));
    }

    public void testAddMethodMeasurement() {
        assertEquals(0, config.getMethodMeasurements().size());

        config.addMethodMeasurement(d1);
        assertEquals(1, config.getMethodMeasurements().size());
        assertTrue(config.getMethodMeasurements().contains(d1));

        config.addMethodMeasurement(d2);
        assertEquals(2, config.getMethodMeasurements().size());
        assertTrue(config.getMethodMeasurements().contains(d2));

        assertEquals(d1, config.getMethodMeasurements().get(0));
        assertEquals(d2, config.getMethodMeasurements().get(1));
    }

    public void testGroupDefinitions() {
        Collection groups;

        groups = config.getGroups("foobar");
        assertEquals(0, groups.size());
        
        config.addGroupDefinition("foo", "/foo/");
        groups = config.getGroups("foobar");
        assertEquals(1, groups.size());
        assertTrue(groups.contains("foo"));

        config.addGroupDefinition("bar", "/bar/");
        groups = config.getGroups("foobar");
        assertEquals(2, groups.size());
        assertTrue(groups.contains("foo"));
        assertTrue(groups.contains("bar"));

        config.addGroupDefinition("baz", "/baz/");
        groups = config.getGroups("foobar");
        assertEquals(2, groups.size());
        assertTrue(groups.contains("foo"));
        assertTrue(groups.contains("bar"));
    }

    public void testGroupDefinitionsWithMultipleREs() {
        Collection groups;

        groups = config.getGroups("foobar");
        assertEquals(0, groups.size());
        
        config.addGroupDefinition("foo", "/foo/");
        config.addGroupDefinition("foo", "/bar/");

        groups = config.getGroups("foo only");
        assertEquals(1, groups.size());
        assertTrue(groups.contains("foo"));

        groups = config.getGroups("bar only");
        assertEquals(1, groups.size());
        assertTrue(groups.contains("foo"));
    }
}
