/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

import org.junit.jupiter.api.*;

import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestMetricsFactory {
    private MetricsConfiguration configuration;
    private MetricsFactory factory;

    @BeforeEach
    void setUp() throws Exception {
        configuration = new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).load(Paths.get("../etc/MetricsConfig.xml").toString());
        factory = new MetricsFactory("test", configuration);
    }
    
    @Test
    void testCreateProjectMetrics() {
        Metrics m1 = factory.createProjectMetrics("foo");
        assertNotNull(m1);
        assertEquals("foo", m1.getName(), "New metrics name");

        Metrics m2 = factory.createProjectMetrics("foo");
        assertSame(m1, m2);
        assertEquals(configuration.getProjectMeasurements().size(), m1.getMeasurementNames().size(), "project measurements");
    }
            
    @Test
    void testIncludeProjectMetrics() {
        Metrics m1 = factory.createProjectMetrics("foo");

        assertFalse(factory.getProjectMetrics().contains(m1), "ProjectMetrics() contains external metrics");

        factory.includeProjectMetrics(m1);

        assertTrue(factory.getProjectMetrics().contains(m1), "ProjectMetrics() does not contain internal metrics");
    }
    
    @Test
    void testCreateGroupMetrics() {
        Metrics m1 = factory.createGroupMetrics("foo");
        assertNotNull(m1);
        assertEquals("foo", m1.getName(), "New metrics name");

        Metrics m2 = factory.createGroupMetrics("foo");
        assertSame(m1, m2);
        assertEquals(configuration.getGroupMeasurements().size(), m1.getMeasurementNames().size(), "group measurements");
    }
            
    @Test
    void testIncludeGroupMetrics() {
        Metrics m1 = factory.createGroupMetrics("foo");

        assertFalse(factory.getGroupMetrics().contains(m1), "GroupMetrics() contains external metrics");

        factory.includeGroupMetrics(m1);

        assertTrue(factory.getGroupMetrics().contains(m1), "GroupMetrics() does not contain internal metrics");
    }

    @Test
    void testCreateClassMetrics() {
        Metrics m1 = factory.createClassMetrics("foo");
        assertNotNull(m1);
        assertEquals("foo", m1.getName(), "New metrics name");

        Metrics m2 = factory.createClassMetrics("foo");
        assertSame(m1, m2);
        assertEquals(configuration.getClassMeasurements().size(), m1.getMeasurementNames().size(), "class measurements");
    }
        
    @Test
    void testIncludeClassMetrics() {
        Metrics m1 = factory.createClassMetrics("foo");

        assertFalse(factory.getClassMetrics().contains(m1), "ClassMetrics() contains external metrics");

        factory.includeClassMetrics(m1);

        assertTrue(factory.getClassMetrics().contains(m1), "ClassMetrics() does not contain internal metrics");
    }

    @Test
    void testCreateMethodMetrics() {
        Metrics m1 = factory.createMethodMetrics("foo(): bar");
        assertNotNull(m1);
        assertEquals("foo(): bar", m1.getName(), "New metrics name");

        Metrics m2 = factory.createMethodMetrics("foo(): bar");
        assertSame(m1, m2);
        assertEquals(configuration.getMethodMeasurements().size(), m1.getMeasurementNames().size(), "method measurements");
    }
    
    @Test
    void testIncludeMethodMetrics() {
        Metrics m1 = factory.createMethodMetrics("foo(): bar");

        assertFalse(factory.getMethodMetrics().contains(m1), "MethodMetrics() contains external metrics");

        factory.includeMethodMetrics(m1);

        assertTrue(factory.getMethodMetrics().contains(m1), "MethodMetrics() does not contain internal metrics");
    }
    
    @Test
    void testCreateStaticInitializerMetrics() {
        Metrics m = factory.createMethodMetrics("foo.static {}: void");

        assertEquals("foo", m.getParent().getName(), "class name");
    }

    @Test
    void testCreateStructure() {
        Metrics methodMetrics  = factory.createMethodMetrics("a.A.a(): a.A");
        Metrics classMetrics   = factory.createClassMetrics("a.A");
        Metrics packageMetrics = factory.createGroupMetrics("a");
        Metrics projectMetrics = factory.createProjectMetrics();

        factory.includeMethodMetrics(methodMetrics);
        
        assertTrue(projectMetrics.getSubMetrics().contains(packageMetrics));
        assertTrue(packageMetrics.getSubMetrics().contains(classMetrics));
        assertTrue(classMetrics.getSubMetrics().contains(methodMetrics));
    }

    @Test
    void testGroupDefinitionsWithInternal() {
        configuration.addGroupDefinition("foo", "/foo/");
        configuration.addGroupDefinition("bar", "/bar/");
        configuration.addGroupDefinition("baz", "/baz/");

        Metrics metrics = factory.createClassMetrics("com.foobar.Foobar");
        factory.includeClassMetrics(metrics);

        assertEquals(3, factory.getGroupMetrics().size(), "Number of groups");
        assertEquals(3, factory.getAllGroupMetrics().size(), "Number of all groups");

        assertTrue(factory.getAllGroupNames().contains("foo"), "Group foo missing");
        assertTrue(factory.getAllGroupNames().contains("bar"), "Group bar missing");
        assertFalse(factory.getAllGroupNames().contains("baz"), "Group baz present");
        assertTrue(factory.getAllGroupNames().contains("com.foobar"), "Group com.foobar missing");

        assertTrue(factory.getGroupNames().contains("foo"), "Group foo missing");
        assertTrue(factory.getGroupNames().contains("bar"), "Group bar missing");
        assertFalse(factory.getGroupNames().contains("baz"), "Group baz present");
        assertTrue(factory.getGroupNames().contains("com.foobar"), "Group com.foobar missing");

        assertTrue(factory.createGroupMetrics("foo").getSubMetrics().contains(metrics), "Not in foo");
        assertTrue(factory.createGroupMetrics("bar").getSubMetrics().contains(metrics), "Not in bar");
        assertFalse(factory.createGroupMetrics("baz").getSubMetrics().contains(metrics), "In baz");
        assertTrue(factory.createGroupMetrics("com.foobar").getSubMetrics().contains(metrics), "Not in com.foobar");

        assertEquals(factory.createGroupMetrics("com.foobar"), metrics.getParent(), "Wrong parent");
    }

    @Test
    void testGroupDefinitionsWithExternal() {
        configuration.addGroupDefinition("foo", "/foo/");
        configuration.addGroupDefinition("bar", "/bar/");
        configuration.addGroupDefinition("baz", "/baz/");

        Metrics metrics = factory.createClassMetrics("com.foobar.Foobar");

        assertEquals(0, factory.getGroupMetrics().size(), "Number of groups");
        assertEquals(1, factory.getAllGroupMetrics().size(), "Number of all groups");

        assertFalse(factory.getAllGroupNames().contains("foo"), "Group foo present");
        assertFalse(factory.getAllGroupNames().contains("bar"), "Group bar present");
        assertFalse(factory.getAllGroupNames().contains("baz"), "Group baz present");
        assertTrue(factory.getAllGroupNames().contains("com.foobar"), "Group com.foobar missing");

        assertFalse(factory.createGroupMetrics("foo").getSubMetrics().contains(metrics), "In foo");
        assertFalse(factory.createGroupMetrics("bar").getSubMetrics().contains(metrics), "In bar");
        assertFalse(factory.createGroupMetrics("baz").getSubMetrics().contains(metrics), "In baz");
        assertFalse(factory.createGroupMetrics("com.foobar").getSubMetrics().contains(metrics), "In com.foobar");

        assertEquals(factory.createGroupMetrics("com.foobar"), metrics.getParent(), "Wrong parent");
    }

    @Test
    void testGroupDefinitionsWithBoth() {
        configuration.addGroupDefinition("foo", "/foo/");
        configuration.addGroupDefinition("baz", "/baz/");

        Metrics fooMetrics    = factory.createClassMetrics("com.foo.Foo");
        Metrics foobazMetrics = factory.createClassMetrics("com.baz.Foobaz");

        factory.includeClassMetrics(fooMetrics);

        assertEquals(2, factory.getGroupMetrics().size(), "Number of groups");

        assertTrue(factory.getGroupNames().contains("foo"), "Group foo missing");
        assertFalse(factory.getGroupNames().contains("baz"), "Group baz present");
        assertTrue(factory.getGroupNames().contains("com.foo"), "Group com.foo missing");
        assertFalse(factory.getGroupNames().contains("com.baz"), "Group com.baz missing");

        assertTrue(factory.createGroupMetrics("foo").getSubMetrics().contains(fooMetrics), "Not in foo");
        assertTrue(factory.createGroupMetrics("com.foo").getSubMetrics().contains(fooMetrics), "Not in com.foo");

        assertEquals(1, factory.createGroupMetrics("foo").getSubMetrics().size(), "foo.size()");
        assertEquals(1, factory.createGroupMetrics("com.foo").getSubMetrics().size(), "com.foo.size()");

        assertEquals(3, factory.getAllGroupMetrics().size(), "Number of all groups");

        assertTrue(factory.getAllGroupNames().contains("foo"), "Group foo missing");
        assertFalse(factory.getAllGroupNames().contains("baz"), "Group baz present");
        assertTrue(factory.getAllGroupNames().contains("com.foo"), "Group com.foo missing");
        assertTrue(factory.getAllGroupNames().contains("com.baz"), "Group com.baz missing");

        assertFalse(factory.createGroupMetrics("com.baz").getSubMetrics().contains(foobazMetrics), "In com.baz");
        
        assertEquals(0, factory.createGroupMetrics("com.baz").getSubMetrics().size(), "com.baz.size()");

        assertEquals(factory.createGroupMetrics("com.foo"), fooMetrics.getParent(), "Wrong parent");
        assertEquals(factory.createGroupMetrics("com.baz"), foobazMetrics.getParent(), "Wrong parent");
    }
}
