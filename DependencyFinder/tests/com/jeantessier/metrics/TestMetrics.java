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

public class TestMetrics extends TestCase {
    public void testCreate() {
        Metrics metrics = new Metrics("test");

        assertEquals("test", metrics.getName());
        assertNotNull(metrics.getMeasurement("test"));
        assertEquals(NullMeasurement.class, metrics.getMeasurement("test").getClass());
        assertTrue(metrics.getSubMetrics().isEmpty());
    }

    public void testTrack() throws Exception {
        Metrics metrics = new Metrics("test");

        metrics.track("test1", new CounterMeasurement(null, null, null));

        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("test2");
        descriptor.setClassFor(CounterMeasurement.class);

        metrics.track(descriptor.createMeasurement());

        assertNotNull(metrics.getMeasurement("test1"));
        assertNotNull(metrics.getMeasurement("test2"));

        assertEquals(NullMeasurement.class, metrics.getMeasurement("test").getClass());
        assertEquals(0, metrics.getMeasurement("test1").getValue().intValue());
        assertEquals(0, metrics.getMeasurement("test2").getValue().intValue());
    }

    public void testAddToMeasurement() {
        Metrics metrics = new Metrics("test");

        Measurement m0 = new CounterMeasurement(null, null, null);
        Measurement m1 = new CounterMeasurement(null, null, null);
        Measurement m2 = new CounterMeasurement(null, null, null);

        m1.add(1);
        m2.add(2.5);
        
        metrics.track("test0", m0);
        metrics.track("test1", m1);
        metrics.track("test2", m2);

        assertEquals(NullMeasurement.class, metrics.getMeasurement("test").getClass());
        assertEquals(0.0, metrics.getMeasurement("test0").getValue().doubleValue(), 0.01);
        assertEquals(1.0, metrics.getMeasurement("test1").getValue().doubleValue(), 0.01);
        assertEquals(2.5, metrics.getMeasurement("test2").getValue().doubleValue(), 0.01);

        metrics.addToMeasurement("test",  1.0);
        metrics.addToMeasurement("test0", 1.0);
        metrics.addToMeasurement("test1", 1.0);
        metrics.addToMeasurement("test2", 1.0);
        
        assertEquals(NullMeasurement.class, metrics.getMeasurement("test").getClass());
        assertEquals(1.0, metrics.getMeasurement("test0").getValue().doubleValue(), 0.01);
        assertEquals(2.0, metrics.getMeasurement("test1").getValue().doubleValue(), 0.01);
        assertEquals(3.5, metrics.getMeasurement("test2").getValue().doubleValue(), 0.01);

        metrics.addToMeasurement("test",  new Double(1.0));
        metrics.addToMeasurement("test0", new Double(1.0));
        metrics.addToMeasurement("test1", new Double(1.0));
        metrics.addToMeasurement("test2", new Double(1.0));
        
        assertEquals(NullMeasurement.class, metrics.getMeasurement("test").getClass());
        assertEquals(2.0, metrics.getMeasurement("test0").getValue().doubleValue(), 0.01);
        assertEquals(3.0, metrics.getMeasurement("test1").getValue().doubleValue(), 0.01);
        assertEquals(4.5, metrics.getMeasurement("test2").getValue().doubleValue(), 0.01);

        metrics.addToMeasurement("test",  1);
        metrics.addToMeasurement("test0", 1);
        metrics.addToMeasurement("test1", 1);
        metrics.addToMeasurement("test2", 1);
        
        assertEquals(NullMeasurement.class, metrics.getMeasurement("test").getClass());
        assertEquals(3.0, metrics.getMeasurement("test0").getValue().doubleValue(), 0.01);
        assertEquals(4.0, metrics.getMeasurement("test1").getValue().doubleValue(), 0.01);
        assertEquals(5.5, metrics.getMeasurement("test2").getValue().doubleValue(), 0.01);
    }

    public void testAddSubMetrics() {
        Metrics metrics = new Metrics("test");

        metrics.addSubMetrics(new Metrics("a"));
        metrics.addSubMetrics(new Metrics("b"));
        
        assertEquals(2, metrics.getSubMetrics().size());
    }

    public void testMetricsInSubMetrics() {
        Metrics metrics = new Metrics("test");

        Metrics a = new Metrics("test.a");
        Metrics b = new Metrics("test.b");
        
        Metrics aA = new Metrics("test.a.A");
        Metrics aB = new Metrics("test.a.B");
        Metrics bA = new Metrics("test.b.A");
        Metrics bB = new Metrics("test.b.B");

        Metrics aAf = new Metrics("test.a.A.f");
        Metrics aAg = new Metrics("test.a.A.g");
        Metrics aBf = new Metrics("test.a.B.f");
        Metrics aBg = new Metrics("test.a.B.g");
        Metrics bAf = new Metrics("test.b.A.f");
        Metrics bAg = new Metrics("test.b.A.g");
        Metrics bBf = new Metrics("test.b.B.f");
        Metrics bBg = new Metrics("test.b.B.g");

        metrics.addSubMetrics(a);
        metrics.addSubMetrics(b);

        a.addSubMetrics(aA);
        a.addSubMetrics(aB);
        b.addSubMetrics(bA);
        b.addSubMetrics(bB);
        
        aA.addSubMetrics(aAf);
        aA.addSubMetrics(aAg);
        aB.addSubMetrics(aBf);
        aB.addSubMetrics(aBg);
        bA.addSubMetrics(bAf);
        bA.addSubMetrics(bAg);
        bB.addSubMetrics(bBf);
        bB.addSubMetrics(bBg);

        aAf.track("0001", new CounterMeasurement(null, null, null));
        aAf.track("0011", new CounterMeasurement(null, null, null));
        aAf.track("0101", new CounterMeasurement(null, null, null));
        aAf.track("0111", new CounterMeasurement(null, null, null));
        aAg.track("0001", new CounterMeasurement(null, null, null));
        aAg.track("0011", new CounterMeasurement(null, null, null));
        aAg.track("0101", new CounterMeasurement(null, null, null));
        aAg.track("0111", new CounterMeasurement(null, null, null));
        aBf.track("0001", new CounterMeasurement(null, null, null));
        aBf.track("0011", new CounterMeasurement(null, null, null));
        aBf.track("0101", new CounterMeasurement(null, null, null));
        aBf.track("0111", new CounterMeasurement(null, null, null));
        aBg.track("0001", new CounterMeasurement(null, null, null));
        aBg.track("0011", new CounterMeasurement(null, null, null));
        aBg.track("0101", new CounterMeasurement(null, null, null));
        aBg.track("0111", new CounterMeasurement(null, null, null));
        bAf.track("1001", new CounterMeasurement(null, null, null));
        bAf.track("1011", new CounterMeasurement(null, null, null));
        bAf.track("1101", new CounterMeasurement(null, null, null));
        bAf.track("1111", new CounterMeasurement(null, null, null));
        bAg.track("1001", new CounterMeasurement(null, null, null));
        bAg.track("1011", new CounterMeasurement(null, null, null));
        bAg.track("1101", new CounterMeasurement(null, null, null));
        bAg.track("1111", new CounterMeasurement(null, null, null));
        bBf.track("1001", new CounterMeasurement(null, null, null));
        bBf.track("1011", new CounterMeasurement(null, null, null));
        bBf.track("1101", new CounterMeasurement(null, null, null));
        bBf.track("1111", new CounterMeasurement(null, null, null));
        bBg.track("1001", new CounterMeasurement(null, null, null));
        bBg.track("1011", new CounterMeasurement(null, null, null));
        bBg.track("1101", new CounterMeasurement(null, null, null));
        bBg.track("1111", new CounterMeasurement(null, null, null));

        aA.track("0011", new CounterMeasurement(null, null, null));
        aA.track("0010", new CounterMeasurement(null, null, null));
        aA.track("0111", new CounterMeasurement(null, null, null));
        aA.track("0110", new CounterMeasurement(null, null, null));
        aB.track("0011", new CounterMeasurement(null, null, null));
        aB.track("0010", new CounterMeasurement(null, null, null));
        aB.track("0111", new CounterMeasurement(null, null, null));
        aB.track("0110", new CounterMeasurement(null, null, null));
        bA.track("1011", new CounterMeasurement(null, null, null));
        bA.track("1010", new CounterMeasurement(null, null, null));
        bA.track("1111", new CounterMeasurement(null, null, null));
        bA.track("1110", new CounterMeasurement(null, null, null));
        bB.track("1011", new CounterMeasurement(null, null, null));
        bB.track("1010", new CounterMeasurement(null, null, null));
        bB.track("1111", new CounterMeasurement(null, null, null));
        bB.track("1110", new CounterMeasurement(null, null, null));

        aA.addToMeasurement("0011", 1);
        aA.addToMeasurement("0010", 1);
        aA.addToMeasurement("0111", 1);
        aA.addToMeasurement("0110", 1);
        aB.addToMeasurement("0011", 1);
        aB.addToMeasurement("0010", 1);
        aB.addToMeasurement("0111", 1);
        aB.addToMeasurement("0110", 1);
        bA.addToMeasurement("1011", 1);
        bA.addToMeasurement("1010", 1);
        bA.addToMeasurement("1111", 1);
        bA.addToMeasurement("1110", 1);
        bB.addToMeasurement("1011", 1);
        bB.addToMeasurement("1010", 1);
        bB.addToMeasurement("1111", 1);
        bB.addToMeasurement("1110", 1);

        a.track("0100", new CounterMeasurement(null, null, null));
        a.track("0101", new CounterMeasurement(null, null, null));
        a.track("0110", new CounterMeasurement(null, null, null));
        a.track("0111", new CounterMeasurement(null, null, null));
        b.track("1100", new CounterMeasurement(null, null, null));
        b.track("1101", new CounterMeasurement(null, null, null));
        b.track("1110", new CounterMeasurement(null, null, null));
        b.track("1111", new CounterMeasurement(null, null, null));

        a.addToMeasurement("0100", 2);
        a.addToMeasurement("0101", 2);
        a.addToMeasurement("0110", 2);
        a.addToMeasurement("0111", 2);
        b.addToMeasurement("1100", 2);
        b.addToMeasurement("1101", 2);
        b.addToMeasurement("1110", 2);
        b.addToMeasurement("1111", 2);

        metrics.track("1100", new CounterMeasurement(null, null, null));
        metrics.track("1101", new CounterMeasurement(null, null, null));
        metrics.track("1110", new CounterMeasurement(null, null, null));
        metrics.track("1111", new CounterMeasurement(null, null, null));

        metrics.addToMeasurement("1100", 3);
        metrics.addToMeasurement("1101", 3);
        metrics.addToMeasurement("1110", 3);
        metrics.addToMeasurement("1111", 3);

        assertEquals(2, metrics.getSubMetrics().size());
        assertEquals(NullMeasurement.class, metrics.getMeasurement("0000").getClass());
        assertEquals(NullMeasurement.class, metrics.getMeasurement("0001").getClass());
        assertEquals(NullMeasurement.class, metrics.getMeasurement("0010").getClass());
        assertEquals(NullMeasurement.class, metrics.getMeasurement("0011").getClass());
        assertEquals(NullMeasurement.class, metrics.getMeasurement("0100").getClass());
        assertEquals(NullMeasurement.class, metrics.getMeasurement("0101").getClass());
        assertEquals(NullMeasurement.class, metrics.getMeasurement("0110").getClass());
        assertEquals(NullMeasurement.class, metrics.getMeasurement("0111").getClass());
        assertEquals(NullMeasurement.class, metrics.getMeasurement("1000").getClass());
        assertEquals(NullMeasurement.class, metrics.getMeasurement("1001").getClass());
        assertEquals(NullMeasurement.class, metrics.getMeasurement("1010").getClass());
        assertEquals(NullMeasurement.class, metrics.getMeasurement("1011").getClass());
        assertEquals(3.0, metrics.getMeasurement("1100").getValue().doubleValue(), 0.01);
        assertEquals(3.0, metrics.getMeasurement("1101").getValue().doubleValue(), 0.01);
        assertEquals(3.0, metrics.getMeasurement("1110").getValue().doubleValue(), 0.01);
        assertEquals(3.0, metrics.getMeasurement("1111").getValue().doubleValue(), 0.01);
    }

    public void testInRange() throws Exception {
        Metrics metrics = new Metrics("test");

        assertTrue(metrics.isInRange());

        MeasurementDescriptor descriptor1 = new MeasurementDescriptor();
        descriptor1.setShortName("foo");
        descriptor1.setLongName("foo");
        descriptor1.setClassFor(CounterMeasurement.class);
        descriptor1.setUpperThreshold(1);

        metrics.track(descriptor1.createMeasurement(metrics));

        MeasurementDescriptor descriptor2 = new MeasurementDescriptor();
        descriptor2.setShortName("bar");
        descriptor2.setLongName("bar");
        descriptor2.setClassFor(CounterMeasurement.class);

        metrics.track(descriptor2.createMeasurement(metrics));

        assertTrue(metrics.isInRange());

        metrics.addToMeasurement("foo", 2);

        assertFalse(metrics.isInRange());
    }

    public void testEmptyWithOneNonEmptyMeasurement() throws Exception {
        Metrics metrics = new Metrics("test");

        assertTrue("Before Track(foo)", metrics.isEmpty());

        MeasurementDescriptor descriptor1 = new MeasurementDescriptor();
        descriptor1.setShortName("foo");
        descriptor1.setLongName("foo");
        descriptor1.setClassFor(CounterMeasurement.class);

        metrics.track(descriptor1.createMeasurement(metrics));

        assertTrue("After Track(foo)", metrics.isEmpty());
        
        MeasurementDescriptor descriptor2 = new MeasurementDescriptor();
        descriptor2.setShortName("bar");
        descriptor2.setLongName("bar");
        descriptor2.setClassFor(CounterMeasurement.class);

        metrics.track(descriptor2.createMeasurement(metrics));

        assertTrue("After Track(bar)", metrics.isEmpty());
        
        metrics.addToMeasurement("foo", 2);

        assertFalse("After Add()", metrics.isEmpty());
    }

    public void testEmptyWithOtherNonEmptyMeasurement() throws Exception {
        Metrics metrics = new Metrics("test");

        assertTrue("Before Track(foo)", metrics.isEmpty());

        MeasurementDescriptor descriptor1 = new MeasurementDescriptor();
        descriptor1.setShortName("foo");
        descriptor1.setLongName("foo");
        descriptor1.setClassFor(CounterMeasurement.class);

        metrics.track(descriptor1.createMeasurement(metrics));

        assertTrue("After Track(foo)", metrics.isEmpty());
        
        MeasurementDescriptor descriptor2 = new MeasurementDescriptor();
        descriptor2.setShortName("bar");
        descriptor2.setLongName("bar");
        descriptor2.setClassFor(CounterMeasurement.class);

        metrics.track(descriptor2.createMeasurement(metrics));

        assertTrue("After Track(bar)", metrics.isEmpty());
        
        metrics.addToMeasurement("bar", 2);

        assertFalse("After Add()", metrics.isEmpty());
    }

    public void testEmptyWithOneNonVisibleNonEmptyMeasurement() throws Exception {
        Metrics metrics = new Metrics("test");

        assertTrue("Before Track(foo)", metrics.isEmpty());

        MeasurementDescriptor descriptor1 = new MeasurementDescriptor();
        descriptor1.setShortName("foo");
        descriptor1.setLongName("foo");
        descriptor1.setClassFor(CounterMeasurement.class);
        descriptor1.setVisible(false);

        metrics.track(descriptor1.createMeasurement(metrics));

        assertTrue("After Track(foo)", metrics.isEmpty());
        
        MeasurementDescriptor descriptor2 = new MeasurementDescriptor();
        descriptor2.setShortName("bar");
        descriptor2.setLongName("bar");
        descriptor2.setClassFor(CounterMeasurement.class);

        metrics.track(descriptor2.createMeasurement(metrics));

        assertTrue("After Track(bar)", metrics.isEmpty());
        
        metrics.addToMeasurement("foo", 2);

        assertTrue("After Add()", metrics.isEmpty());
    }

    public void testEmptyWithOneNonEmptySubMetrics() throws Exception {
        Metrics metrics = new Metrics("test");
        Metrics submetrics1 = new Metrics("submetrics1");
        Metrics submetrics2 = new Metrics("submetrics2");

        metrics.addSubMetrics(submetrics1);
        metrics.addSubMetrics(submetrics2);
        
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("foo");
        descriptor.setClassFor(CounterMeasurement.class);

        submetrics1.track(descriptor.createMeasurement(submetrics1));
        submetrics2.track(descriptor.createMeasurement(submetrics2));

        assertTrue("Before Add() to submetrics1", submetrics1.isEmpty());
        assertTrue("Before Add() to submetrics1", submetrics2.isEmpty());
        assertTrue("Before Add() to submetrics1", metrics.isEmpty());
        
        submetrics1.addToMeasurement("foo", 2);

        assertFalse("After Add() to submetrics1", submetrics1.isEmpty());
        assertTrue("After Add() to submetrics1", submetrics2.isEmpty());
        assertFalse("After Add() to submetrics1", metrics.isEmpty());
    }

    public void testEmptyWithOtherNonEmptySubMetrics() throws Exception {
        Metrics metrics = new Metrics("test");
        Metrics submetrics1 = new Metrics("submetrics1");
        Metrics submetrics2 = new Metrics("submetrics2");

        metrics.addSubMetrics(submetrics1);
        metrics.addSubMetrics(submetrics2);
        
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("foo");
        descriptor.setClassFor(CounterMeasurement.class);

        submetrics1.track(descriptor.createMeasurement(submetrics1));
        submetrics2.track(descriptor.createMeasurement(submetrics2));

        assertTrue("Before Add() to submetrics1", submetrics1.isEmpty());
        assertTrue("Before Add() to submetrics1", submetrics2.isEmpty());
        assertTrue("Before Add() to submetrics2", metrics.isEmpty());
        
        submetrics2.addToMeasurement("foo", 2);

        assertTrue("After Add() to submetrics1", submetrics1.isEmpty());
        assertFalse("After Add() to submetrics1", submetrics2.isEmpty());
        assertFalse("After Add() to submetrics2", metrics.isEmpty());
    }
}
