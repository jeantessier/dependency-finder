/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import static org.junit.jupiter.api.Assertions.*;

public class TestMetricsComparator {
    @Test
    void testSortOn() {
        MetricsComparator c = new MetricsComparator("foo", StatisticalMeasurement.DISPOSE_IGNORE);

        assertEquals("foo", c.getName(), "name");
        assertEquals(MetricsComparator.ASCENDING, c.getDirection(), "direction");

        c.sortOn("foo", StatisticalMeasurement.DISPOSE_IGNORE);
        
        assertEquals("foo", c.getName(), "name");
        assertEquals(MetricsComparator.DESCENDING, c.getDirection(), "direction");
        
        c.sortOn("foo", StatisticalMeasurement.DISPOSE_IGNORE);
        
        assertEquals("foo", c.getName(), "name");
        assertEquals(MetricsComparator.ASCENDING, c.getDirection(), "direction");

        c.sortOn("bar", StatisticalMeasurement.DISPOSE_IGNORE);

        assertEquals("bar", c.getName(), "name");
        assertEquals(MetricsComparator.ASCENDING, c.getDirection(), "direction");

        c.sortOn("bar", StatisticalMeasurement.DISPOSE_IGNORE);

        assertEquals("bar", c.getName(), "name");
        assertEquals(MetricsComparator.DESCENDING, c.getDirection(), "direction");

        c.sortOn("baz", StatisticalMeasurement.DISPOSE_IGNORE);

        assertEquals("baz", c.getName(), "name");
        assertEquals(MetricsComparator.ASCENDING, c.getDirection(), "direction");

        c.sortOn("foobar", StatisticalMeasurement.DISPOSE_IGNORE);

        assertEquals("foobar", c.getName(), "name");
        assertEquals(MetricsComparator.ASCENDING, c.getDirection(), "direction");

        c.sortOn("foobar", StatisticalMeasurement.DISPOSE_IGNORE);

        assertEquals("foobar", c.getName(), "name");
        assertEquals(MetricsComparator.DESCENDING, c.getDirection(), "direction");

        c.sortOn("foobar", StatisticalMeasurement.DISPOSE_MINIMUM);

        assertEquals("foobar", c.getName(), "name");
        assertEquals(MetricsComparator.ASCENDING, c.getDirection(), "direction");
    }
    
    @Test
    void testCompareTo() {
        Metrics m1 = new Metrics("m1");
        Metrics m2 = new Metrics("m2");

        m1.track("foo", new CounterMeasurement(null, null, null));
        m1.track("bar", new CounterMeasurement(null, null, null));
        m1.track("baz", new CounterMeasurement(null, null, null));
        m2.track("foo", new CounterMeasurement(null, null, null));
        m2.track("bar", new CounterMeasurement(null, null, null));
        m2.track("baz", new CounterMeasurement(null, null, null));
        
        m1.addToMeasurement("foo", 1);
        m1.addToMeasurement("bar", 2);
        m1.addToMeasurement("baz", 3);
        m2.addToMeasurement("foo", 3);
        m2.addToMeasurement("bar", 2);
        m2.addToMeasurement("baz", 1);
        
        MetricsComparator c1 = new MetricsComparator("foo");
        MetricsComparator c2 = new MetricsComparator("bar");
        MetricsComparator c3 = new MetricsComparator("baz");

        assertTrue(c1.compare(m1, m2) < 0);
        assertTrue(c2.compare(m1, m2) == 0);
        assertTrue(c3.compare(m1, m2) > 0);

        c1.reverse();
        c2.reverse();
        c3.reverse();

        assertTrue(c1.compare(m1, m2) > 0);
        assertTrue(c2.compare(m1, m2) == 0);
        assertTrue(c3.compare(m1, m2) < 0);
    }
    
    @Test
    void testCompareNaN() {
        Metrics m1 = new Metrics("m1");
        Metrics m2 = new Metrics("m2");

        m1.track("foo", new CounterMeasurement(null, null, null));
        m2.track("foo", new CounterMeasurement(null, null, null));
        m1.track("bar", new CounterMeasurement(null, null, null));
        m2.track("bar", new CounterMeasurement(null, null, null));
        m1.track("baz", new CounterMeasurement(null, null, null));
        m2.track("baz", new CounterMeasurement(null, null, null));
        
        m1.addToMeasurement("foo", Double.NaN);
        m2.addToMeasurement("foo", Double.NaN);
        m1.addToMeasurement("bar", Double.NaN);
        m2.addToMeasurement("bar", 1);
        m1.addToMeasurement("baz", 1);
        m2.addToMeasurement("baz", Double.NaN);
        
        MetricsComparator c1 = new MetricsComparator("foo");
        MetricsComparator c2 = new MetricsComparator("bar");
        MetricsComparator c3 = new MetricsComparator("baz");

        assertTrue(c1.compare(m1, m2) == 0);
        assertTrue(c2.compare(m1, m2) > 0);
        assertTrue(c3.compare(m1, m2) < 0);

        c1.reverse();
        c2.reverse();
        c3.reverse();

        assertTrue(c1.compare(m1, m2) == 0);
        assertTrue(c2.compare(m1, m2) > 0);
        assertTrue(c3.compare(m1, m2) < 0);
    }
}
