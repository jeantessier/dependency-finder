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

	private Metrics sm1;
	private Metrics sm2;
	private Metrics sm3;
	private Metrics sm4;
	private Metrics sm5;
	private Metrics sm6;
	
	protected void setUp() throws Exception {
		sm1 = new Metrics("sm1");
		sm2 = new Metrics("sm2");
		sm3 = new Metrics("sm3");
		sm4 = new Metrics("sm4");
		sm5 = new Metrics("sm5");
		sm6 = new Metrics("sm6");
		
		MeasurementDescriptor present = new MeasurementDescriptor();
		present.ShortName("P");
		present.LongName("present");
		present.Class(CounterMeasurement.class);

		MeasurementDescriptor counter = new MeasurementDescriptor();
		counter.ShortName("C");
		counter.LongName("counter");
		counter.Class(CounterMeasurement.class);

		sm1.Track(present.CreateMeasurement(sm1));

		sm2.Track(present.CreateMeasurement(sm2));
		sm2.Track(counter.CreateMeasurement(sm2));
		sm2.AddToMeasurement("C", 0);

		sm3.Track(counter.CreateMeasurement(sm3));
		sm3.AddToMeasurement("C", 1);

		sm4.Track(counter.CreateMeasurement(sm4));
		sm4.AddToMeasurement("C", 2);

		sm5.Track(counter.CreateMeasurement(sm5));
		sm5.AddToMeasurement("C", 3);

		sm6.Track(counter.CreateMeasurement(sm6));
		sm6.AddToMeasurement("C", 4);

		metrics = new Metrics("metrics");
		
		metrics.AddSubMetrics(sm1);
		metrics.AddSubMetrics(sm2);
		metrics.AddSubMetrics(sm3);
		metrics.AddSubMetrics(sm4);
		metrics.AddSubMetrics(sm5);
		metrics.AddSubMetrics(sm6);

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

		Vector result;
		List   list = new ArrayList();
		String str;

		str = "";
		result = perl.split(operators, str);
		assertEquals(str + " " + result, 1, result.size());
		assertEquals("split(\"\") expected [] but was " + result, "", result.get(0));

		str = "P";
		result = perl.split(operators, str);
		assertEquals(str + " " + result, 1, result.size());

		str = "P > 0";
		result = perl.split(operators, str);
		assertEquals(str + " " + result, 3, result.size());

		str = "1 < P < 3";
		result = perl.split(operators, str);
		assertEquals(str + " " + result, 5, result.size());

		str = "1 < P DISPOSE_MEAN < 3";
		result = perl.split(operators, str);
		assertEquals(str + " " + result, 5, result.size());
	}
}
