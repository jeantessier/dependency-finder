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

import org.apache.log4j.*;

public class TestStatisticalMeasurementEmpty extends TestCase {
    private StatisticalMeasurement measurement;
    private Metrics                metrics;

    protected void setUp() throws Exception {
        Logger.getLogger(getClass()).info("Starting test: " + getName());

        metrics = new Metrics("foo");
        measurement = new StatisticalMeasurement(null, metrics, "bar");
    }
    
    protected void tearDown() throws Exception {
        Logger.getLogger(getClass()).info("End of " + getName());
    }

    public void testDirect() {
        Metrics m = new Metrics("m");
        m.track("bar", new CounterMeasurement(null, null, null));
        metrics.addSubMetrics(m);

        assertTrue("Before AddToMeasurement()", measurement.isEmpty());
        
        m.addToMeasurement("bar", 1);

        assertFalse("After AddToMeasurement()", !measurement.isEmpty());
    }

    public void testIndirect() {
        Metrics m = new Metrics("m");
        metrics.addSubMetrics(m);

        Metrics sm = new Metrics("sm");
        sm.track("bar", new CounterMeasurement(null, null, null));
        m.addSubMetrics(sm);

        assertTrue("Before AddToMeasurement()", measurement.isEmpty());
        
        sm.addToMeasurement("bar", 1);

        assertFalse("After AddToMeasurement()", !measurement.isEmpty());
    }

    public void testViaStatisticalMeasurement() {
        Metrics m = new Metrics("m");
        m.track("bar", new StatisticalMeasurement(null, m, "bar"));
        metrics.addSubMetrics(m);

        Metrics sm = new Metrics("sm");
        sm.track("bar", new CounterMeasurement(null, null, null));
        m.addSubMetrics(sm);

        assertTrue("Before AddToMeasurement()", measurement.isEmpty());
        
        sm.addToMeasurement("bar", 1);

        assertFalse("After AddToMeasurement()", !measurement.isEmpty());
    }
}
