/*
 *  Copyright (c) 2001-2004, Jean Tessier
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

import org.apache.log4j.*;

public class Metrics {
	public static final String PACKAGES = "P";
	
	public static final String CLASSES            = "C";
	public static final String PUBLIC_CLASSES     = "PuC";
	public static final String FINAL_CLASSES      = "FC";
	public static final String ABSTRACT_CLASSES   = "AC";
	public static final String SYNTHETIC_CLASSES  = "SynthC";
	public static final String INTERFACES         = "I";
	public static final String DEPRECATED_CLASSES = "DC";
	public static final String STATIC_CLASSES     = "SC";

	public static final String PUBLIC_METHODS       = "PuM";
	public static final String PROTECTED_METHODS    = "ProM";
	public static final String PRIVATE_METHODS      = "ProM";
	public static final String PACKAGE_METHODS      = "PaM";
	public static final String FINAL_METHODS        = "FM";
	public static final String ABSTRACT_METHODS     = "AM";
	public static final String DEPRECATED_METHODS   = "DM";
	public static final String SYNTHETIC_METHODS    = "SynthM";
	public static final String STATIC_METHODS       = "SM";
	public static final String SYNCHRONIZED_METHODS = "SynchM";
	public static final String NATIVE_METHODS       = "NM";
	public static final String TRIVIAL_METHODS      = "TM";

	public static final String ATTRIBUTES            = "A";
	public static final String PUBLIC_ATTRIBUTES     = "PuA";
	public static final String PROTECTED_ATTRIBUTES  = "ProA";
	public static final String PRIVATE_ATTRIBUTES    = "PriA";
	public static final String PACKAGE_ATTRIBUTES    = "PaA";
	public static final String FINAL_ATTRIBUTES      = "FA";
	public static final String DEPRECATED_ATTRIBUTES = "DA";
	public static final String SYNTHETIC_ATTRIBUTES  = "SynthA";
	public static final String STATIC_ATTRIBUTES     = "SA";
	public static final String TRANSIENT_ATTRIBUTES  = "TA";
	public static final String VOLATILE_ATTRIBUTES   = "VA";

	public static final String INNER_CLASSES           = "IC";
	public static final String PUBLIC_INNER_CLASSES    = "PuIC";
	public static final String PROTECTED_INNER_CLASSES = "ProIC";
	public static final String PRIVATE_INNER_CLASSES   = "PriIC";
	public static final String PACKAGE_INNER_CLASSES   = "PaIC";
	public static final String ABSTRACT_INNER_CLASSES  = "AIC";
	public static final String FINAL_INNER_CLASSES     = "FIC";
	public static final String STATIC_INNER_CLASSES    = "SIC";

	public static final String DEPTH_OF_INHERITANCE  = "DOI";
	public static final String SUBCLASSES            = "SUB";
	public static final String CLASS_SLOC            = "class SLOC";

	public static final String SLOC            = "SLOC";
	public static final String PARAMETERS      = "PARAM";
	public static final String LOCAL_VARIABLES = "LVAR";

	public static final String INBOUND_INTRA_PACKAGE_DEPENDENCIES  = "IIP";
	public static final String INBOUND_EXTRA_PACKAGE_DEPENDENCIES  = "IEP";
	public static final String OUTBOUND_INTRA_PACKAGE_DEPENDENCIES = "OIP";
	public static final String OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES = "OEP";
	
	public static final String INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES     = "IICM";
	public static final String INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES   = "IIPM";
	public static final String INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES   = "IEPM";
	public static final String OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES   = "OICF";
	public static final String OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES = "OIPF";
	public static final String OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES   = "OIPC";
	public static final String OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES = "OEPF";
	public static final String OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES   = "OEPC";

	private static final Measurement NULL_MEASUREMENT = new NullMeasurement();
	
	private Metrics parent;
	private String  name;

	private Map measurements = new TreeMap();
	private Map submetrics   = new TreeMap();

	public Metrics(String name) {
		this(null, name);
	}
	
	/**
	 *  @param name The name of the element being measured
	 *              (e.g., class name, method name).
	 */
	public Metrics(Metrics parent, String name) {
		this.parent = parent;
		this.name   = name;

		if (parent == null) {
			Logger.getLogger(getClass()).debug("Created top-level metrics \"" + name + "\"");
		} else {
			Logger.getLogger(getClass()).debug("Created metrics \"" + name + "\" under \"" + parent.Name() + "\"");
		}
	}

	public Metrics Parent() {
		return parent;
	}

	/**
	 *  @return The name of the element being measured
	 *          (e.g., class name, method name).
	 */
	public String Name() {
		return name;
	}

	void Track(Measurement measurement) {
		Track(measurement.ShortName(), measurement);
	}
	
	void Track(String name, Measurement measurement) {
		measurements.put(name, measurement);
	}

	public void AddToMeasurement(String name) {
		AddToMeasurement(name, 1);
	}
	
	public void AddToMeasurement(String name, int delta) {
		Measurement(name).Add(delta);
	}
	
	public void AddToMeasurement(String name, long delta) {
		Measurement(name).Add(delta);
	}
	
	public void AddToMeasurement(String name, float delta) {
		Measurement(name).Add(delta);
	}
	
	public void AddToMeasurement(String name, double delta) {
		Measurement(name).Add(delta);
	}
	
	public void AddToMeasurement(String name, Object delta) {
		Measurement(name).Add(delta);
	}

	public Measurement Measurement(String name) {
		Measurement result = (Measurement) measurements.get(name);
		
		if (result == null) {
			result = NULL_MEASUREMENT;
			Logger.getLogger(getClass()).info("Null measurement \"" + name + "\" on \"" + Name() + "\"");
		}

		return result;
	}

	public boolean HasMeasurement(String name) {
		return measurements.get(name) != null;
	}
	
	public Collection MeasurementNames() {
		return Collections.unmodifiableCollection(new TreeSet(measurements.keySet()));
	}
	
	public Metrics AddSubMetrics(Metrics metrics) {
		return (Metrics) submetrics.put(metrics.Name(), metrics);
	}
	
	public Collection SubMetrics() {
		return Collections.unmodifiableCollection(submetrics.values());
	}

	public boolean Empty() {
		boolean result = true;

		Iterator i;

		i = measurements.values().iterator();
		while (result && i.hasNext()) {
			Measurement measurement = (Measurement) i.next();
			if (measurement.Descriptor().Visible()) {
				result = measurement.Empty();
			}
		}

		i = submetrics.values().iterator();
		while (result && i.hasNext()) {
			result = ((Metrics) i.next()).Empty();
		}
		
		return result;
	}

	public boolean InRange() {
		boolean result = true;

		Iterator i = measurements.values().iterator();
		while (result && i.hasNext()) {
			result = ((Measurement) i.next()).InRange();
		}
		
		return result;
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();

		result.append(getClass().getName()).append(" ").append(Name()).append(" with [");

		Iterator i = MeasurementNames().iterator();
		while(i.hasNext()) {
			String name = (String) i.next();
			Measurement measure = Measurement(name);

			result.append("\"").append(name).append("\"(").append(measure.getClass().getName()).append(")");
			if (i.hasNext()) {
				result.append(", ");
			}
		}

		result.append("]");

		return result.toString();
	}
}
