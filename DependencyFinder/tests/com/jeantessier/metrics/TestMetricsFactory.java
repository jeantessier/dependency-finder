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
