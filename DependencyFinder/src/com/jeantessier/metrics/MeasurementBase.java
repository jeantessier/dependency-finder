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

import org.apache.oro.text.perl.*;

public abstract class MeasurementBase implements Measurement {
	private static final Perl5Util perl = new Perl5Util();

	protected static Perl5Util Perl() {
		return perl;
	}
	
	private MeasurementDescriptor descriptor = null;
	private Metrics               context    = null;

	private boolean               cached     = false;

	public MeasurementBase(MeasurementDescriptor descriptor, Metrics context, String init_text) {
		this.descriptor = descriptor;
		this.context    = context;
	}
	
	public MeasurementDescriptor Descriptor() {
		return descriptor;
	}
	
	public Metrics Context() {
		return context;
	}

	/**
	 *  Tells this instance if it should reuse a previously
	 *  computed value or if it should regenerate it.
	 */
	protected boolean Cached() {
		return cached;
	}

	/**
	 *  Sets the caching flag, telling this instance if
	 *  its value has been computed.  This flag is
	 *  conditional to caching being enabled in the
	 *  corresponding descriptor.
	 */
	protected void Cached(boolean cached) {
		this.cached = cached && Descriptor().Cached();
	}
	
	public String ShortName() {
		return Descriptor().ShortName();
	}
	
	public String LongName() {
		return Descriptor().LongName();
	}
	
	public Number Value() {
		return new Double(Compute());
	}

	public int intValue() {
		return (int) Compute();
	}

	public long longValue() {
		return (long) Compute();
	}

	public float floatValue() {
		return (float) Compute();
	}

	public double doubleValue() {
		return Compute();
	}
	
	public void Add(Object object) {
		// Do nothing
	}
	
	public boolean InRange() {
		boolean result = true;

		if (Descriptor() != null) {
			Comparable lower_threshold = Descriptor().LowerThreshold();
			Comparable upper_threshold = Descriptor().UpperThreshold();

			if (result && lower_threshold != null) {
				if (lower_threshold instanceof String) {
					try {
						result = Double.parseDouble((String) lower_threshold) <= Compute();
					} catch (NumberFormatException ex) {
						// Ignore
					}
				} else if (lower_threshold instanceof Number) {
					result = ((Number) lower_threshold).doubleValue() <= Compute();
				} else {
					result = lower_threshold.compareTo(Value()) <= 0;
				}
			}
			
			if (result && upper_threshold != null) {
				if (upper_threshold instanceof String) {
					try {
						result = Double.parseDouble((String) upper_threshold) >= Compute();
					} catch (NumberFormatException ex) {
						// Ignore
					}
				} else if (upper_threshold instanceof Number) {
					result = ((Number) upper_threshold).doubleValue() >= Compute();
				} else {
					result = upper_threshold.compareTo(Value()) >= 0;
				}
			}
		}
		
		return result;
	}

	public void Add(int i) {
		// Do nothing
	}
	
	public void Add(long l) {
		// Do nothing
	}
	
	public void Add(float f) {
		// Do nothing
	}
	
	public void Add(double d) {
		// Do nothing
	}
	
	protected abstract double Compute();
}
