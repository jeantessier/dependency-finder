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

import static org.junit.jupiter.api.Assertions.*;

public class TestStatisticalMeasurementWithMetrics {
    private final Metrics m1 = new Metrics("a.A.a");
    private final Metrics m2 = new Metrics("a.A.b");
    private final Metrics m3 = new Metrics("a.B.a");
    private final Metrics m4 = new Metrics("a.B.b");
    private final Metrics m5 = new Metrics("b.A.a");
    private final Metrics m6 = new Metrics("b.A.b");
    private final Metrics m7 = new Metrics("b.B.a");
    private final Metrics m8 = new Metrics("b.B.b");

    private final Metrics c1 = new Metrics("a.A");
    private final Metrics c2 = new Metrics("a.B");
    private final Metrics c3 = new Metrics("b.A");
    private final Metrics c4 = new Metrics("b.B");

    private final Metrics g1 = new Metrics("a");
    private final Metrics g2 = new Metrics("b");

    private final Metrics p = new Metrics("test");

    @BeforeEach
    void setUp() {
        m1.track("001", new CounterMeasurement(null, null, null));
        m1.track("011", new CounterMeasurement(null, null, null));
        m1.track("101", new CounterMeasurement(null, null, null));
        m1.track("111", new CounterMeasurement(null, null, null));
        m2.track("001", new CounterMeasurement(null, null, null));
        m2.track("011", new CounterMeasurement(null, null, null));
        m2.track("101", new CounterMeasurement(null, null, null));
        m2.track("111", new CounterMeasurement(null, null, null));
        m3.track("001", new CounterMeasurement(null, null, null));
        m3.track("011", new CounterMeasurement(null, null, null));
        m3.track("101", new CounterMeasurement(null, null, null));
        m3.track("111", new CounterMeasurement(null, null, null));
        m4.track("001", new CounterMeasurement(null, null, null));
        m4.track("011", new CounterMeasurement(null, null, null));
        m4.track("101", new CounterMeasurement(null, null, null));
        m4.track("111", new CounterMeasurement(null, null, null));
        m5.track("001", new CounterMeasurement(null, null, null));
        m5.track("011", new CounterMeasurement(null, null, null));
        m5.track("101", new CounterMeasurement(null, null, null));
        m5.track("111", new CounterMeasurement(null, null, null));
        m6.track("001", new CounterMeasurement(null, null, null));
        m6.track("011", new CounterMeasurement(null, null, null));
        m6.track("101", new CounterMeasurement(null, null, null));
        m6.track("111", new CounterMeasurement(null, null, null));
        m7.track("001", new CounterMeasurement(null, null, null));
        m7.track("011", new CounterMeasurement(null, null, null));
        m7.track("101", new CounterMeasurement(null, null, null));
        m7.track("111", new CounterMeasurement(null, null, null));
        m8.track("001", new CounterMeasurement(null, null, null));
        m8.track("011", new CounterMeasurement(null, null, null));
        m8.track("101", new CounterMeasurement(null, null, null));
        m8.track("111", new CounterMeasurement(null, null, null));
    
        m1.addToMeasurement("001", 1);
        m1.addToMeasurement("011", 1);
        m1.addToMeasurement("101", 1);
        m1.addToMeasurement("111", 1);
        m2.addToMeasurement("001", 1);
        m2.addToMeasurement("011", 1);
        m2.addToMeasurement("101", 1);
        m2.addToMeasurement("111", 1);
        m3.addToMeasurement("001", 1);
        m3.addToMeasurement("011", 1);
        m3.addToMeasurement("101", 1);
        m3.addToMeasurement("111", 1);
        m4.addToMeasurement("001", 1);
        m4.addToMeasurement("011", 1);
        m4.addToMeasurement("101", 1);
        m4.addToMeasurement("111", 1);
        m5.addToMeasurement("001", 1);
        m5.addToMeasurement("011", 1);
        m5.addToMeasurement("101", 1);
        m5.addToMeasurement("111", 1);
        m6.addToMeasurement("001", 1);
        m6.addToMeasurement("011", 1);
        m6.addToMeasurement("101", 1);
        m6.addToMeasurement("111", 1);
        m7.addToMeasurement("001", 1);
        m7.addToMeasurement("011", 1);
        m7.addToMeasurement("101", 1);
        m7.addToMeasurement("111", 1);
        m8.addToMeasurement("001", 1);
        m8.addToMeasurement("011", 1);
        m8.addToMeasurement("101", 1);
        m8.addToMeasurement("111", 1);
    
        c1.track("010", new CounterMeasurement(null, null, null));
        c1.track("011", new CounterMeasurement(null, null, null));
        c1.track("110", new CounterMeasurement(null, null, null));
        c1.track("111", new CounterMeasurement(null, null, null));
        c2.track("010", new CounterMeasurement(null, null, null));
        c2.track("011", new CounterMeasurement(null, null, null));
        c2.track("110", new CounterMeasurement(null, null, null));
        c2.track("111", new CounterMeasurement(null, null, null));
        c3.track("010", new CounterMeasurement(null, null, null));
        c3.track("011", new CounterMeasurement(null, null, null));
        c3.track("110", new CounterMeasurement(null, null, null));
        c3.track("111", new CounterMeasurement(null, null, null));
        c4.track("010", new CounterMeasurement(null, null, null));
        c4.track("011", new CounterMeasurement(null, null, null));
        c4.track("110", new CounterMeasurement(null, null, null));
        c4.track("111", new CounterMeasurement(null, null, null));
    
        c1.addToMeasurement("010", 10);
        c1.addToMeasurement("011", 10);
        c1.addToMeasurement("110", 10);
        c1.addToMeasurement("111", 10);
        c2.addToMeasurement("010", 10);
        c2.addToMeasurement("011", 10);
        c2.addToMeasurement("110", 10);
        c2.addToMeasurement("111", 10);
        c3.addToMeasurement("010", 10);
        c3.addToMeasurement("011", 10);
        c3.addToMeasurement("110", 10);
        c3.addToMeasurement("111", 10);
        c4.addToMeasurement("010", 10);
        c4.addToMeasurement("011", 10);
        c4.addToMeasurement("110", 10);
        c4.addToMeasurement("111", 10);
    
        c1.addSubMetrics(m1);
        c1.addSubMetrics(m2);
        c2.addSubMetrics(m3);
        c2.addSubMetrics(m4);
        c3.addSubMetrics(m5);
        c3.addSubMetrics(m6);
        c4.addSubMetrics(m7);
        c4.addSubMetrics(m8);

        g1.track("100", new CounterMeasurement(null, null, null));
        g1.track("101", new CounterMeasurement(null, null, null));
        g1.track("110", new CounterMeasurement(null, null, null));
        g1.track("111", new CounterMeasurement(null, null, null));
        g2.track("100", new CounterMeasurement(null, null, null));
        g2.track("101", new CounterMeasurement(null, null, null));
        g2.track("110", new CounterMeasurement(null, null, null));
        g2.track("111", new CounterMeasurement(null, null, null));
        
        g1.addToMeasurement("100", 100);
        g1.addToMeasurement("101", 100);
        g1.addToMeasurement("110", 100);
        g1.addToMeasurement("111", 100);
        g2.addToMeasurement("100", 100);
        g2.addToMeasurement("101", 100);
        g2.addToMeasurement("110", 100);
        g2.addToMeasurement("111", 100);
        
        g1.addSubMetrics(c1);
        g1.addSubMetrics(c2);
        g2.addSubMetrics(c3);
        g2.addSubMetrics(c4);

        p.addSubMetrics(g1);
        p.addSubMetrics(g2);
    }

    @Test
    void testProject() {
        StatisticalMeasurement m000 = new StatisticalMeasurement(null, p, "000");
        StatisticalMeasurement m001 = new StatisticalMeasurement(null, p, "001");
        StatisticalMeasurement m010 = new StatisticalMeasurement(null, p, "010");
        StatisticalMeasurement m011 = new StatisticalMeasurement(null, p, "011");
        StatisticalMeasurement m100 = new StatisticalMeasurement(null, p, "100");
        StatisticalMeasurement m101 = new StatisticalMeasurement(null, p, "101");
        StatisticalMeasurement m110 = new StatisticalMeasurement(null, p, "110");
        StatisticalMeasurement m111 = new StatisticalMeasurement(null, p, "111");

        assertEquals(0, m000.getNbDataPoints(), "000");
        assertEquals(8, m001.getNbDataPoints(), "001");
        assertEquals(4, m010.getNbDataPoints(), "010");
        assertEquals(4, m011.getNbDataPoints(), "011");
        assertEquals(2, m100.getNbDataPoints(), "100");
        assertEquals(2, m101.getNbDataPoints(), "101");
        assertEquals(2, m110.getNbDataPoints(), "110");
        assertEquals(2, m111.getNbDataPoints(), "111");
    }

    @Test
    void testGroup() {
        StatisticalMeasurement m000 = new StatisticalMeasurement(null, g1, "000");
        StatisticalMeasurement m001 = new StatisticalMeasurement(null, g1, "001");
        StatisticalMeasurement m010 = new StatisticalMeasurement(null, g1, "010");
        StatisticalMeasurement m011 = new StatisticalMeasurement(null, g1, "011");
        StatisticalMeasurement m100 = new StatisticalMeasurement(null, g1, "100");
        StatisticalMeasurement m101 = new StatisticalMeasurement(null, g1, "101");
        StatisticalMeasurement m110 = new StatisticalMeasurement(null, g1, "110");
        StatisticalMeasurement m111 = new StatisticalMeasurement(null, g1, "111");

        assertEquals(0, m000.getNbDataPoints(), "000");
        assertEquals(4, m001.getNbDataPoints(), "001");
        assertEquals(2, m010.getNbDataPoints(), "010");
        assertEquals(2, m011.getNbDataPoints(), "011");
        assertEquals(0, m100.getNbDataPoints(), "100");
        assertEquals(4, m101.getNbDataPoints(), "101");
        assertEquals(2, m110.getNbDataPoints(), "110");
        assertEquals(2, m111.getNbDataPoints(), "111");
    }

    @Test
    void testClass() {
        StatisticalMeasurement m000 = new StatisticalMeasurement(null, c1, "000");
        StatisticalMeasurement m001 = new StatisticalMeasurement(null, c1, "001");
        StatisticalMeasurement m010 = new StatisticalMeasurement(null, c1, "010");
        StatisticalMeasurement m011 = new StatisticalMeasurement(null, c1, "011");
        StatisticalMeasurement m100 = new StatisticalMeasurement(null, c1, "100");
        StatisticalMeasurement m101 = new StatisticalMeasurement(null, c1, "101");
        StatisticalMeasurement m110 = new StatisticalMeasurement(null, c1, "110");
        StatisticalMeasurement m111 = new StatisticalMeasurement(null, c1, "111");

        assertEquals(0, m000.getNbDataPoints(), "000");
        assertEquals(2, m001.getNbDataPoints(), "001");
        assertEquals(0, m010.getNbDataPoints(), "010");
        assertEquals(2, m011.getNbDataPoints(), "011");
        assertEquals(0, m100.getNbDataPoints(), "100");
        assertEquals(2, m101.getNbDataPoints(), "101");
        assertEquals(0, m110.getNbDataPoints(), "110");
        assertEquals(2, m111.getNbDataPoints(), "111");
    }

    @Test
    void testMethod() {
        StatisticalMeasurement m000 = new StatisticalMeasurement(null, m1, "000");
        StatisticalMeasurement m001 = new StatisticalMeasurement(null, m1, "001");
        StatisticalMeasurement m010 = new StatisticalMeasurement(null, m1, "010");
        StatisticalMeasurement m011 = new StatisticalMeasurement(null, m1, "011");
        StatisticalMeasurement m100 = new StatisticalMeasurement(null, m1, "100");
        StatisticalMeasurement m101 = new StatisticalMeasurement(null, m1, "101");
        StatisticalMeasurement m110 = new StatisticalMeasurement(null, m1, "110");
        StatisticalMeasurement m111 = new StatisticalMeasurement(null, m1, "111");

        assertEquals(0, m000.getNbDataPoints(), "000");
        assertEquals(0, m001.getNbDataPoints(), "001");
        assertEquals(0, m010.getNbDataPoints(), "010");
        assertEquals(0, m011.getNbDataPoints(), "011");
        assertEquals(0, m100.getNbDataPoints(), "100");
        assertEquals(0, m101.getNbDataPoints(), "101");
        assertEquals(0, m110.getNbDataPoints(), "110");
        assertEquals(0, m111.getNbDataPoints(), "111");
    }

    @Test
    void testIrregular() {
        Metrics m11 = new Metrics("m11");
        Metrics m12 = new Metrics("m12");
        Metrics m21 = new Metrics("m21");
        Metrics m22 = new Metrics("m22");

        Metrics c1 = new Metrics("c1");
        Metrics c2 = new Metrics("c2");

        c1.addSubMetrics(m11);
        c1.addSubMetrics(m12);
        c2.addSubMetrics(m21);
        c2.addSubMetrics(m22);
        
        Metrics g = new Metrics("g");
        
        g.addSubMetrics(c1);
        g.addSubMetrics(c2);

        m11.track("bar", new CounterMeasurement(null, null, null));
        m12.track("bar", new CounterMeasurement(null, null, null));
        m21.track("bar", new CounterMeasurement(null, null, null));
        m22.track("bar", new CounterMeasurement(null, null, null));

        c1.track("bar", new CounterMeasurement(null, null, null));

        StatisticalMeasurement sm = new StatisticalMeasurement(null, g, "bar");
        assertEquals(3, sm.getNbDataPoints());
    }
}
