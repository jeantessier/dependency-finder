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

public class TestMetricsFactory extends TestCase {
	MetricsFactory factory;
	
	public TestMetricsFactory(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		factory = new MetricsFactory("test");
	}
	
	public void testCreateProjectMetrics() {
		Metrics m1 = factory.CreateProjectMetrics("foo");
		Metrics m2 = factory.CreateProjectMetrics("foo");

		assertSame(m1, m2);
		assertEquals("groups",                       NbSubMetricsMeasurement.class, m1.Metric("groups").getClass());
		assertEquals("classes per package",          StatisticalMeasurement.class,  m1.Metric("classes per package").getClass());
		assertEquals("packages",                     AccumulatorMeasurement.class,  m1.Metric("packages").getClass());
		assertEquals("abstract classes per package", StatisticalMeasurement.class,  m1.Metric("abstract classes per package").getClass());
		assertEquals("interfaces per package",       StatisticalMeasurement.class,  m1.Metric("interfaces per package").getClass());
		assertEquals("LOC",                          StatisticalMeasurement.class,  m1.Metric(Metrics.NLOC).getClass());
	}
	
	public void testCreateGroupMetrics() {
		Metrics m1 = factory.CreateGroupMetrics("foo");
		Metrics m2 = factory.CreateGroupMetrics("foo");

		assertSame(m1, m2);
		assertTrue(m1.Metric("classes")              instanceof NbSubMetricsMeasurement);
		assertTrue(m1.Metric("abstract classes")     instanceof CounterMeasurement);
		assertTrue(m1.Metric("interfaces")           instanceof CounterMeasurement);
		assertTrue(m1.Metric("methods per class")    instanceof StatisticalMeasurement);
		assertTrue(m1.Metric(Metrics.NLOC)           instanceof StatisticalMeasurement);
		assertTrue(m1.Metric("attributes per class") instanceof StatisticalMeasurement);
	}
	
	public void testCreateClassMetrics() {
		Metrics m1 = factory.CreateClassMetrics("foo");
		Metrics m2 = factory.CreateClassMetrics("foo");

		assertSame(m1, m2);
		assertTrue(m1.Metric("methods")               instanceof NbSubMetricsMeasurement);
		assertTrue(m1.Metric("abstract methods")      instanceof CounterMeasurement);
		assertTrue(m1.Metric("parameters per method") instanceof StatisticalMeasurement);
		assertTrue(m1.Metric(Metrics.NLOC)            instanceof StatisticalMeasurement);
		assertTrue(m1.Metric("attributes")            instanceof CounterMeasurement);
	}
	
	public void testCreateMethodMetrics() {
		Metrics m1 = factory.CreateMethodMetrics("foo");
		Metrics m2 = factory.CreateMethodMetrics("foo");

		assertSame(m1, m2);
		assertTrue(m1.Metric(Metrics.NLOC) instanceof CounterMeasurement);
		assertTrue(m1.Metric("parameters") instanceof CounterMeasurement);
	}

	public void testCreateStructure() {
		Metrics method_metrics  = factory.CreateMethodMetrics("a.A.a()");
		Metrics class_metrics   = factory.CreateClassMetrics("a.A");
		Metrics package_metrics = factory.CreateGroupMetrics("a");
		Metrics project_metrics = factory.CreateProjectMetrics();

		assertTrue(project_metrics.SubMetrics().contains(package_metrics));
		assertTrue(package_metrics.SubMetrics().contains(class_metrics));
		assertTrue(class_metrics.SubMetrics().contains(method_metrics));
	}
}
