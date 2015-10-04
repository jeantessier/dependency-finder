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

import java.io.*;

public class TestMetricsFactory extends TestCase {
    private MetricsConfiguration configuration;
    private MetricsFactory factory;

    protected void setUp() throws Exception {
        configuration = new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).load("etc" + File.separator + "MetricsConfig.xml");
        factory = new MetricsFactory("test", configuration);
    }
    
    public void testCreateProjectMetrics() {
        Metrics m1 = factory.createProjectMetrics("foo");
        assertNotNull(m1);
        assertEquals("New metrics name", "foo", m1.getName());

        Metrics m2 = factory.createProjectMetrics("foo");
        assertSame(m1, m2);
        assertEquals("project measurements", configuration.getProjectMeasurements().size(), m1.getMeasurementNames().size());
    }
            
    public void testIncludeProjectMetrics() {
        Metrics m1 = factory.createProjectMetrics("foo");

        assertFalse("ProjectMetrics() contains external metrics", factory.getProjectMetrics().contains(m1));

        factory.includeProjectMetrics(m1);

        assertTrue("ProjectMetrics() does not contain internal metrics", factory.getProjectMetrics().contains(m1));
    }
    
    public void testCreateGroupMetrics() {
        Metrics m1 = factory.createGroupMetrics("foo");
        assertNotNull(m1);
        assertEquals("New metrics name", "foo", m1.getName());

        Metrics m2 = factory.createGroupMetrics("foo");
        assertSame(m1, m2);
        assertEquals("group measurements", configuration.getGroupMeasurements().size(), m1.getMeasurementNames().size());
    }
            
    public void testIncludeGroupMetrics() {
        Metrics m1 = factory.createGroupMetrics("foo");

        assertFalse("GroupMetrics() contains external metrics", factory.getGroupMetrics().contains(m1));

        factory.includeGroupMetrics(m1);

        assertTrue("GroupMetrics() does not contain internal metrics", factory.getGroupMetrics().contains(m1));
    }

    public void testCreateClassMetrics() {
        Metrics m1 = factory.createClassMetrics("foo");
        assertNotNull(m1);
        assertEquals("New metrics name", "foo", m1.getName());

        Metrics m2 = factory.createClassMetrics("foo");
        assertSame(m1, m2);
        assertEquals("class measurements", configuration.getClassMeasurements().size(), m1.getMeasurementNames().size());
    }
        
    public void testIncludeClassMetrics() {
        Metrics m1 = factory.createClassMetrics("foo");

        assertFalse("ClassMetrics() contains external metrics", factory.getClassMetrics().contains(m1));

        factory.includeClassMetrics(m1);

        assertTrue("ClassMetrics() does not contain internal metrics", factory.getClassMetrics().contains(m1));
    }

    public void testCreateMethodMetrics() {
        Metrics m1 = factory.createMethodMetrics("foo");
        assertNotNull(m1);
        assertEquals("New metrics name", "foo", m1.getName());

        Metrics m2 = factory.createMethodMetrics("foo");
        assertSame(m1, m2);
        assertEquals("method measurements", configuration.getMethodMeasurements().size(), m1.getMeasurementNames().size());
    }
    
    public void testIncludeMethodMetrics() {
        Metrics m1 = factory.createMethodMetrics("foo");

        assertFalse("MethodMetrics() contains external metrics", factory.getMethodMetrics().contains(m1));

        factory.includeMethodMetrics(m1);

        assertTrue("MethodMetrics() does not contain internal metrics", factory.getMethodMetrics().contains(m1));
    }
    
    public void testCreateStaticInitializerMetrics() {
        Metrics m = factory.createMethodMetrics("foo.static {}");

        assertEquals("class name", "foo", m.getParent().getName());
    }

    public void testCreateStructure() {
        Metrics methodMetrics  = factory.createMethodMetrics("a.A.a()");
        Metrics classMetrics   = factory.createClassMetrics("a.A");
        Metrics packageMetrics = factory.createGroupMetrics("a");
        Metrics projectMetrics = factory.createProjectMetrics();

        factory.includeMethodMetrics(methodMetrics);
        
        assertTrue(projectMetrics.getSubMetrics().contains(packageMetrics));
        assertTrue(packageMetrics.getSubMetrics().contains(classMetrics));
        assertTrue(classMetrics.getSubMetrics().contains(methodMetrics));
    }

    public void testGroupDefinitionsWithInternal() {
        configuration.addGroupDefinition("foo", "/foo/");
        configuration.addGroupDefinition("bar", "/bar/");
        configuration.addGroupDefinition("baz", "/baz/");

        Metrics metrics = factory.createClassMetrics("com.foobar.Foobar");
        factory.includeClassMetrics(metrics);

        assertEquals("Number of groups",     3, factory.getGroupMetrics().size());
        assertEquals("Number of all groups", 3, factory.getAllGroupMetrics().size());

        assertTrue("Group foo missing",        factory.getAllGroupNames().contains("foo"));
        assertTrue("Group bar missing",        factory.getAllGroupNames().contains("bar"));
        assertFalse("Group baz present",       factory.getAllGroupNames().contains("baz"));
        assertTrue("Group com.foobar missing", factory.getAllGroupNames().contains("com.foobar"));

        assertTrue("Group foo missing",        factory.getGroupNames().contains("foo"));
        assertTrue("Group bar missing",        factory.getGroupNames().contains("bar"));
        assertFalse("Group baz present",       factory.getGroupNames().contains("baz"));
        assertTrue("Group com.foobar missing", factory.getGroupNames().contains("com.foobar"));

        assertTrue("Not in foo",        factory.createGroupMetrics("foo").getSubMetrics().contains(metrics));
        assertTrue("Not in bar",        factory.createGroupMetrics("bar").getSubMetrics().contains(metrics));
        assertFalse("In baz",           factory.createGroupMetrics("baz").getSubMetrics().contains(metrics));
        assertTrue("Not in com.foobar", factory.createGroupMetrics("com.foobar").getSubMetrics().contains(metrics));

        assertEquals("Wrong parent", factory.createGroupMetrics("com.foobar"), metrics.getParent());
    }

    public void testGroupDefinitionsWithExternal() {
        configuration.addGroupDefinition("foo", "/foo/");
        configuration.addGroupDefinition("bar", "/bar/");
        configuration.addGroupDefinition("baz", "/baz/");

        Metrics metrics = factory.createClassMetrics("com.foobar.Foobar");

        assertEquals("Number of groups",     0, factory.getGroupMetrics().size());
        assertEquals("Number of all groups", 1, factory.getAllGroupMetrics().size());

        assertFalse("Group foo present",       factory.getAllGroupNames().contains("foo"));
        assertFalse("Group bar present",       factory.getAllGroupNames().contains("bar"));
        assertFalse("Group baz present",       factory.getAllGroupNames().contains("baz"));
        assertTrue("Group com.foobar missing", factory.getAllGroupNames().contains("com.foobar"));

        assertFalse("In foo",        factory.createGroupMetrics("foo").getSubMetrics().contains(metrics));
        assertFalse("In bar",        factory.createGroupMetrics("bar").getSubMetrics().contains(metrics));
        assertFalse("In baz",        factory.createGroupMetrics("baz").getSubMetrics().contains(metrics));
        assertFalse("In com.foobar", factory.createGroupMetrics("com.foobar").getSubMetrics().contains(metrics));

        assertEquals("Wrong parent", factory.createGroupMetrics("com.foobar"), metrics.getParent());
    }

    public void testGroupDefinitionsWithBoth() {
        configuration.addGroupDefinition("foo", "/foo/");
        configuration.addGroupDefinition("baz", "/baz/");

        Metrics fooMetrics    = factory.createClassMetrics("com.foo.Foo");
        Metrics foobazMetrics = factory.createClassMetrics("com.baz.Foobaz");

        factory.includeClassMetrics(fooMetrics);

        assertEquals("Number of groups",     2, factory.getGroupMetrics().size());

        assertTrue("Group foo missing",      factory.getGroupNames().contains("foo"));
        assertFalse("Group baz present",     factory.getGroupNames().contains("baz"));
        assertTrue("Group com.foo missing",  factory.getGroupNames().contains("com.foo"));
        assertFalse("Group com.baz missing", factory.getGroupNames().contains("com.baz"));

        assertTrue("Not in foo",     factory.createGroupMetrics("foo").getSubMetrics().contains(fooMetrics));
        assertTrue("Not in com.foo", factory.createGroupMetrics("com.foo").getSubMetrics().contains(fooMetrics));

        assertEquals("foo.size()",     1, factory.createGroupMetrics("foo").getSubMetrics().size());
        assertEquals("com.foo.size()", 1, factory.createGroupMetrics("com.foo").getSubMetrics().size());

        assertEquals("Number of all groups", 3, factory.getAllGroupMetrics().size());

        assertTrue("Group foo missing",     factory.getAllGroupNames().contains("foo"));
        assertFalse("Group baz present",    factory.getAllGroupNames().contains("baz"));
        assertTrue("Group com.foo missing", factory.getAllGroupNames().contains("com.foo"));
        assertTrue("Group com.baz missing", factory.getAllGroupNames().contains("com.baz"));

        assertFalse("In com.baz", factory.createGroupMetrics("com.baz").getSubMetrics().contains(foobazMetrics));
        
        assertEquals("com.baz.size()", 0, factory.createGroupMetrics("com.baz").getSubMetrics().size());

        assertEquals("Wrong parent", factory.createGroupMetrics("com.foo"), fooMetrics.getParent());
        assertEquals("Wrong parent", factory.createGroupMetrics("com.baz"), foobazMetrics.getParent());
    }
}
