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

import java.util.*;

import org.apache.oro.text.perl.*;

import junit.framework.*;

public class TestNbSubMetricsMeasurementSelectionCriteria extends TestCase {
    private Metrics metrics;
    private MeasurementDescriptor descriptor;

    private Metrics m1;
    private Metrics m2;
    private Metrics m3;
    private Metrics m4;
    private Metrics m5;
    private Metrics m6;
    
    protected void setUp() throws Exception {
        m1 = new Metrics("m1");
        m2 = new Metrics("m2");
        m3 = new Metrics("m3");
        m4 = new Metrics("m4");
        m5 = new Metrics("m5");
        m6 = new Metrics("m6");
        
        MeasurementDescriptor present = new MeasurementDescriptor();
        present.setShortName("P");
        present.setLongName("present");
        present.setClassFor(CounterMeasurement.class);

        MeasurementDescriptor counter = new MeasurementDescriptor();
        counter.setShortName("C");
        counter.setLongName("counter");
        counter.setClassFor(CounterMeasurement.class);

        m1.track(present.createMeasurement(m1));

        m2.track(present.createMeasurement(m2));
        m2.track(counter.createMeasurement(m2));
        m2.addToMeasurement("C", 0);

        m3.track(counter.createMeasurement(m3));
        m3.addToMeasurement("C", 1);

        m4.track(counter.createMeasurement(m4));
        m4.addToMeasurement("C", 2);

        m5.track(counter.createMeasurement(m5));
        m5.addToMeasurement("C", 3);

        m6.track(counter.createMeasurement(m6));
        m6.addToMeasurement("C", 4);

        metrics = new Metrics("metrics");
        
        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);
        metrics.addSubMetrics(m4);
        metrics.addSubMetrics(m5);
        metrics.addSubMetrics(m6);

        descriptor = new MeasurementDescriptor();
        descriptor.setShortName("Nb");
        descriptor.setLongName("Number");
        descriptor.setClassFor(NbSubMetricsMeasurement.class);
    }

    public void testDefault() throws Exception {
        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals("default", 6, measurement.getValue().intValue());
    }

    public void testPresence() throws Exception {
        descriptor.setInitText("P");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals("presence", 2, measurement.getValue().intValue());
    }

    public void testLesserThan() throws Exception {
        descriptor.setInitText("C < 3");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals("lesser than", 3, measurement.getValue().intValue());
    }

    public void testLesserThanOrEqual() throws Exception {
        descriptor.setInitText("C <= 3");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals("lesser than or equal", 4, measurement.getValue().intValue());
    }

    public void testGreaterThan() throws Exception {
        descriptor.setInitText("C > 1");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals("greater than", 3, measurement.getValue().intValue());
    }

    public void testGreaterThanOrEqual() throws Exception {
        descriptor.setInitText("C >= 1");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals("greater than or equal", 4, measurement.getValue().intValue());
    }

    public void testEqual() throws Exception {
        descriptor.setInitText("C == 1");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals("equal", 1, measurement.getValue().intValue());
    }

    public void testNotEqual() throws Exception {
        descriptor.setInitText("C != 1");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals("not equal", 4, measurement.getValue().intValue());
    }

    public void testAnd() throws Exception {
        descriptor.setInitText("1 <= C <= 3");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals("and", 3, measurement.getValue().intValue());
    }

    public void testOr() throws Exception {
        descriptor.setInitText("C == 1\nC == 2");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals("or", 2, measurement.getValue().intValue());
    }

    public void testSplit() {
        String operators = "/(<)|(<=)|(>)|(>=)|(==)|(!=)/";
        Perl5Util perl = new org.apache.oro.text.perl.Perl5Util();

        List   list = new ArrayList();
        String str;

        list.clear();
        str = "";
        perl.split(list, operators, str);
        assertEquals("split(\"" + str + "\") expected [] but was " + list, 0, list.size());

        list.clear();
        str = "P";
        perl.split(list, operators, str);
        assertEquals("split(\"" + str + "\") expected [\"P\"] but was " + list, 1, list.size());
        assertEquals("split(\"" + str + "\") expected [\"P\"] but was " + list, str, list.get(0));

        list.clear();
        str = "P > 0";
        perl.split(list, operators, str);
        assertEquals("split(\"" + str + "\") expected [\"P \", \">\", \" 0\"] but was " + list, 3, list.size());
        assertEquals("split(\"" + str + "\") expected [\"P \", \">\", \" 0\"] but was " + list, "P ", list.get(0));
        assertEquals("split(\"" + str + "\") expected [\"P \", \">\", \" 0\"] but was " + list, ">", list.get(1));
        assertEquals("split(\"" + str + "\") expected [\"P \", \">\", \" 0\"] but was " + list, " 0", list.get(2));

        list.clear();
        str = "1 < P < 3";
        perl.split(list, operators, str);
        assertEquals("split(\"" + str + "\") expected [\"1 \", \"<\", \" P \", \"<\", \" 3\"] but was " + list, 5, list.size());
        assertEquals("split(\"" + str + "\") expected [\"1 \", \"<\", \" P \", \"<\", \" 3\"] but was " + list, "1 ", list.get(0));
        assertEquals("split(\"" + str + "\") expected [\"1 \", \"<\", \" P \", \"<\", \" 3\"] but was " + list, "<", list.get(1));
        assertEquals("split(\"" + str + "\") expected [\"1 \", \"<\", \" P \", \"<\", \" 3\"] but was " + list, " P ", list.get(2));
        assertEquals("split(\"" + str + "\") expected [\"1 \", \"<\", \" P \", \"<\", \" 3\"] but was " + list, "<", list.get(3));
        assertEquals("split(\"" + str + "\") expected [\"1 \", \"<\", \" P \", \"<\", \" 3\"] but was " + list, " 3", list.get(4));

        list.clear();
        str = "1 < P DISPOSE_MEAN < 3";
        perl.split(list, operators, str);
        assertEquals("split(\"" + str + "\") expected [\"1 \", \"<\", \" P DISPOSE_MEAN \", \"<\", \" 3\"] but was " + list, 5, list.size());
        assertEquals("split(\"" + str + "\") expected [\"1 \", \"<\", \" P DISPOSE_MEAN \", \"<\", \" 3\"] but was " + list, "1 ", list.get(0));
        assertEquals("split(\"" + str + "\") expected [\"1 \", \"<\", \" P DISPOSE_MEAN \", \"<\", \" 3\"] but was " + list, "<", list.get(1));
        assertEquals("split(\"" + str + "\") expected [\"1 \", \"<\", \" P DISPOSE_MEAN \", \"<\", \" 3\"] but was " + list, " P DISPOSE_MEAN ", list.get(2));
        assertEquals("split(\"" + str + "\") expected [\"1 \", \"<\", \" P DISPOSE_MEAN \", \"<\", \" 3\"] but was " + list, "<", list.get(3));
        assertEquals("split(\"" + str + "\") expected [\"1 \", \"<\", \" P DISPOSE_MEAN \", \"<\", \" 3\"] but was " + list, " 3", list.get(4));
    }
}
