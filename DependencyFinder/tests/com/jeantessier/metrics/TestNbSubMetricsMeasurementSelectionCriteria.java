/*
 *  Copyright (c) 2001-2003, Jean Tessier
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
 *  	* Neither the name of Jean Tessier nor the names of his contributors
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

import java.util.*;

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
		present.ShortName("P");
		present.LongName("present");
		present.Class(CounterMeasurement.class);

		MeasurementDescriptor counter = new MeasurementDescriptor();
		counter.ShortName("C");
		counter.LongName("counter");
		counter.Class(CounterMeasurement.class);

		m1.Track(present.CreateMeasurement(m1));

		m2.Track(present.CreateMeasurement(m2));
		m2.Track(counter.CreateMeasurement(m2));
		m2.AddToMeasurement("C", 0);

		m3.Track(counter.CreateMeasurement(m3));
		m3.AddToMeasurement("C", 1);

		m4.Track(counter.CreateMeasurement(m4));
		m4.AddToMeasurement("C", 2);

		m5.Track(counter.CreateMeasurement(m5));
		m5.AddToMeasurement("C", 3);

		m6.Track(counter.CreateMeasurement(m6));
		m6.AddToMeasurement("C", 4);

		metrics = new Metrics("metrics");
		
		metrics.AddSubMetrics(m1);
		metrics.AddSubMetrics(m2);
		metrics.AddSubMetrics(m3);
		metrics.AddSubMetrics(m4);
		metrics.AddSubMetrics(m5);
		metrics.AddSubMetrics(m6);

		descriptor = new MeasurementDescriptor();
		descriptor.ShortName("Nb");
		descriptor.LongName("Number");
		descriptor.Class(NbSubMetricsMeasurement.class);
	}

	public void testDefault() throws Exception {
		NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals("default", 6, measurement.intValue());
	}

	public void testPresence() throws Exception {
		descriptor.InitText("P");

		NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals("presence", 2, measurement.intValue());
	}

	public void testLesserThan() throws Exception {
		descriptor.InitText("C < 3");

		NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals("lesser than", 3, measurement.intValue());
	}

	public void testLesserThanOrEqual() throws Exception {
		descriptor.InitText("C <= 3");

		NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals("lesser than or equal", 4, measurement.intValue());
	}

	public void testGreaterThan() throws Exception {
		descriptor.InitText("C > 1");

		NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals("greater than", 3, measurement.intValue());
	}

	public void testGreaterThanOrEqual() throws Exception {
		descriptor.InitText("C >= 1");

		NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals("greater than or equal", 4, measurement.intValue());
	}

	public void testEqual() throws Exception {
		descriptor.InitText("C == 1");

		NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals("equal", 1, measurement.intValue());
	}

	public void testNotEqual() throws Exception {
		descriptor.InitText("C != 1");

		NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals("not equal", 4, measurement.intValue());
	}

	public void testAnd() throws Exception {
		descriptor.InitText("1 <= C <= 3");

		NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals("and", 3, measurement.intValue());
	}

	public void testOr() throws Exception {
		descriptor.InitText("C == 1\nC == 2");

		NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals("or", 2, measurement.intValue());
	}

	public void testSplit() {
		String operators = "/(<)|(<=)|(>)|(>=)|(==)|(!=)/";
		org.apache.oro.text.perl.Perl5Util perl = new org.apache.oro.text.perl.Perl5Util();

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
