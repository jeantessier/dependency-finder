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

import junit.framework.*;

public class TestMetrics extends TestCase {
	public void testCreate() {
		Metrics metrics = new Metrics("test");

		assertEquals("test", metrics.Name());
		assertNotNull(metrics.Measurement("test"));
		assertEquals(NullMeasurement.class, metrics.Measurement("test").getClass());
		assertTrue(metrics.SubMetrics().isEmpty());
	}

	public void testTrack() throws Exception {
		Metrics metrics = new Metrics("test");

		metrics.Track("test1", new CounterMeasurement(null, null, null));

		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("test2");
		descriptor.Class(CounterMeasurement.class);

		metrics.Track(descriptor.CreateMeasurement());

		assertNotNull(metrics.Measurement("test1"));
		assertNotNull(metrics.Measurement("test2"));

		assertEquals(NullMeasurement.class, metrics.Measurement("test").getClass());
		assertEquals(0, metrics.Measurement("test1").intValue());
		assertEquals(0, metrics.Measurement("test2").intValue());
	}

	public void testAddToMeasurement() {
		Metrics metrics = new Metrics("test");

		Measurement m0 = new CounterMeasurement(null, null, null);
		Measurement m1 = new CounterMeasurement(null, null, null);
		Measurement m2 = new CounterMeasurement(null, null, null);

		m1.Add(1);
		m2.Add(2.5);
		
		metrics.Track("test0", m0);
		metrics.Track("test1", m1);
		metrics.Track("test2", m2);

		assertEquals(NullMeasurement.class, metrics.Measurement("test").getClass());
		assertEquals(0.0, metrics.Measurement("test0").doubleValue(), 0.01);
		assertEquals(1.0, metrics.Measurement("test1").doubleValue(), 0.01);
		assertEquals(2.5, metrics.Measurement("test2").doubleValue(), 0.01);

		metrics.AddToMeasurement("test",  1.0);
		metrics.AddToMeasurement("test0", 1.0);
		metrics.AddToMeasurement("test1", 1.0);
		metrics.AddToMeasurement("test2", 1.0);
		
		assertEquals(NullMeasurement.class, metrics.Measurement("test").getClass());
		assertEquals(1.0, metrics.Measurement("test0").doubleValue(), 0.01);
		assertEquals(2.0, metrics.Measurement("test1").doubleValue(), 0.01);
		assertEquals(3.5, metrics.Measurement("test2").doubleValue(), 0.01);

		metrics.AddToMeasurement("test",  new Double(1.0));
		metrics.AddToMeasurement("test0", new Double(1.0));
		metrics.AddToMeasurement("test1", new Double(1.0));
		metrics.AddToMeasurement("test2", new Double(1.0));
		
		assertEquals(NullMeasurement.class, metrics.Measurement("test").getClass());
		assertEquals(2.0, metrics.Measurement("test0").doubleValue(), 0.01);
		assertEquals(3.0, metrics.Measurement("test1").doubleValue(), 0.01);
		assertEquals(4.5, metrics.Measurement("test2").doubleValue(), 0.01);

		metrics.AddToMeasurement("test",  1);
		metrics.AddToMeasurement("test0", 1);
		metrics.AddToMeasurement("test1", 1);
		metrics.AddToMeasurement("test2", 1);
		
		assertEquals(NullMeasurement.class, metrics.Measurement("test").getClass());
		assertEquals(3.0, metrics.Measurement("test0").doubleValue(), 0.01);
		assertEquals(4.0, metrics.Measurement("test1").doubleValue(), 0.01);
		assertEquals(5.5, metrics.Measurement("test2").doubleValue(), 0.01);
	}

	public void testAddSubMetrics() {
		Metrics metrics = new Metrics("test");

		metrics.AddSubMetrics(new Metrics("a"));
		metrics.AddSubMetrics(new Metrics("b"));
		
		assertEquals(2, metrics.SubMetrics().size());
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

		metrics.AddSubMetrics(a);
		metrics.AddSubMetrics(b);

		a.AddSubMetrics(aA);
		a.AddSubMetrics(aB);
		b.AddSubMetrics(bA);
		b.AddSubMetrics(bB);
		
		aA.AddSubMetrics(aAf);
		aA.AddSubMetrics(aAg);
		aB.AddSubMetrics(aBf);
		aB.AddSubMetrics(aBg);
		bA.AddSubMetrics(bAf);
		bA.AddSubMetrics(bAg);
		bB.AddSubMetrics(bBf);
		bB.AddSubMetrics(bBg);

		aAf.Track("0001", new CounterMeasurement(null, null, null));
		aAf.Track("0011", new CounterMeasurement(null, null, null));
		aAf.Track("0101", new CounterMeasurement(null, null, null));
		aAf.Track("0111", new CounterMeasurement(null, null, null));
		aAg.Track("0001", new CounterMeasurement(null, null, null));
		aAg.Track("0011", new CounterMeasurement(null, null, null));
		aAg.Track("0101", new CounterMeasurement(null, null, null));
		aAg.Track("0111", new CounterMeasurement(null, null, null));
		aBf.Track("0001", new CounterMeasurement(null, null, null));
		aBf.Track("0011", new CounterMeasurement(null, null, null));
		aBf.Track("0101", new CounterMeasurement(null, null, null));
		aBf.Track("0111", new CounterMeasurement(null, null, null));
		aBg.Track("0001", new CounterMeasurement(null, null, null));
		aBg.Track("0011", new CounterMeasurement(null, null, null));
		aBg.Track("0101", new CounterMeasurement(null, null, null));
		aBg.Track("0111", new CounterMeasurement(null, null, null));
		bAf.Track("1001", new CounterMeasurement(null, null, null));
		bAf.Track("1011", new CounterMeasurement(null, null, null));
		bAf.Track("1101", new CounterMeasurement(null, null, null));
		bAf.Track("1111", new CounterMeasurement(null, null, null));
		bAg.Track("1001", new CounterMeasurement(null, null, null));
		bAg.Track("1011", new CounterMeasurement(null, null, null));
		bAg.Track("1101", new CounterMeasurement(null, null, null));
		bAg.Track("1111", new CounterMeasurement(null, null, null));
		bBf.Track("1001", new CounterMeasurement(null, null, null));
		bBf.Track("1011", new CounterMeasurement(null, null, null));
		bBf.Track("1101", new CounterMeasurement(null, null, null));
		bBf.Track("1111", new CounterMeasurement(null, null, null));
		bBg.Track("1001", new CounterMeasurement(null, null, null));
		bBg.Track("1011", new CounterMeasurement(null, null, null));
		bBg.Track("1101", new CounterMeasurement(null, null, null));
		bBg.Track("1111", new CounterMeasurement(null, null, null));

		aA.Track("0011", new CounterMeasurement(null, null, null));
		aA.Track("0010", new CounterMeasurement(null, null, null));
		aA.Track("0111", new CounterMeasurement(null, null, null));
		aA.Track("0110", new CounterMeasurement(null, null, null));
		aB.Track("0011", new CounterMeasurement(null, null, null));
		aB.Track("0010", new CounterMeasurement(null, null, null));
		aB.Track("0111", new CounterMeasurement(null, null, null));
		aB.Track("0110", new CounterMeasurement(null, null, null));
		bA.Track("1011", new CounterMeasurement(null, null, null));
		bA.Track("1010", new CounterMeasurement(null, null, null));
		bA.Track("1111", new CounterMeasurement(null, null, null));
		bA.Track("1110", new CounterMeasurement(null, null, null));
		bB.Track("1011", new CounterMeasurement(null, null, null));
		bB.Track("1010", new CounterMeasurement(null, null, null));
		bB.Track("1111", new CounterMeasurement(null, null, null));
		bB.Track("1110", new CounterMeasurement(null, null, null));

		aA.AddToMeasurement("0011", 1);
		aA.AddToMeasurement("0010", 1);
		aA.AddToMeasurement("0111", 1);
		aA.AddToMeasurement("0110", 1);
		aB.AddToMeasurement("0011", 1);
		aB.AddToMeasurement("0010", 1);
		aB.AddToMeasurement("0111", 1);
		aB.AddToMeasurement("0110", 1);
		bA.AddToMeasurement("1011", 1);
		bA.AddToMeasurement("1010", 1);
		bA.AddToMeasurement("1111", 1);
		bA.AddToMeasurement("1110", 1);
		bB.AddToMeasurement("1011", 1);
		bB.AddToMeasurement("1010", 1);
		bB.AddToMeasurement("1111", 1);
		bB.AddToMeasurement("1110", 1);

		a.Track("0100", new CounterMeasurement(null, null, null));
		a.Track("0101", new CounterMeasurement(null, null, null));
		a.Track("0110", new CounterMeasurement(null, null, null));
		a.Track("0111", new CounterMeasurement(null, null, null));
		b.Track("1100", new CounterMeasurement(null, null, null));
		b.Track("1101", new CounterMeasurement(null, null, null));
		b.Track("1110", new CounterMeasurement(null, null, null));
		b.Track("1111", new CounterMeasurement(null, null, null));

		a.AddToMeasurement("0100", 2);
		a.AddToMeasurement("0101", 2);
		a.AddToMeasurement("0110", 2);
		a.AddToMeasurement("0111", 2);
		b.AddToMeasurement("1100", 2);
		b.AddToMeasurement("1101", 2);
		b.AddToMeasurement("1110", 2);
		b.AddToMeasurement("1111", 2);

		metrics.Track("1100", new CounterMeasurement(null, null, null));
		metrics.Track("1101", new CounterMeasurement(null, null, null));
		metrics.Track("1110", new CounterMeasurement(null, null, null));
		metrics.Track("1111", new CounterMeasurement(null, null, null));

		metrics.AddToMeasurement("1100", 3);
		metrics.AddToMeasurement("1101", 3);
		metrics.AddToMeasurement("1110", 3);
		metrics.AddToMeasurement("1111", 3);

		assertEquals(2, metrics.SubMetrics().size());
		assertEquals(NullMeasurement.class, metrics.Measurement("0000").getClass());
		assertEquals(NullMeasurement.class, metrics.Measurement("0001").getClass());
		assertEquals(NullMeasurement.class, metrics.Measurement("0010").getClass());
		assertEquals(NullMeasurement.class, metrics.Measurement("0011").getClass());
		assertEquals(NullMeasurement.class, metrics.Measurement("0100").getClass());
		assertEquals(NullMeasurement.class, metrics.Measurement("0101").getClass());
		assertEquals(NullMeasurement.class, metrics.Measurement("0110").getClass());
		assertEquals(NullMeasurement.class, metrics.Measurement("0111").getClass());
		assertEquals(NullMeasurement.class, metrics.Measurement("1000").getClass());
		assertEquals(NullMeasurement.class, metrics.Measurement("1001").getClass());
		assertEquals(NullMeasurement.class, metrics.Measurement("1010").getClass());
		assertEquals(NullMeasurement.class, metrics.Measurement("1011").getClass());
		assertEquals(3.0, metrics.Measurement("1100").doubleValue(), 0.01);
		assertEquals(3.0, metrics.Measurement("1101").doubleValue(), 0.01);
		assertEquals(3.0, metrics.Measurement("1110").doubleValue(), 0.01);
		assertEquals(3.0, metrics.Measurement("1111").doubleValue(), 0.01);
	}
}
