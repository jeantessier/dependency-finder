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

import org.apache.log4j.*;

public class Metrics {
	public static final String PACKAGES = "packages";
	
	public static final String PUBLIC_CLASSES     = "public classes";
	public static final String FINAL_CLASSES      = "final classes";
	public static final String ABSTRACT_CLASSES   = "abstract classes";
	public static final String SYNTHETIC_CLASSES  = "synthetic classes";
	public static final String INTERFACES         = "interfaces";
	public static final String DEPRECATED_CLASSES = "deprecated classes";

	public static final String PUBLIC_METHODS       = "public methods";
	public static final String PROTECTED_METHODS    = "protected methods";
	public static final String PRIVATE_METHODS      = "private methods";
	public static final String PACKAGE_METHODS      = "package methods";
	public static final String FINAL_METHODS        = "final methods";
	public static final String ABSTRACT_METHODS     = "abstract methods";
	public static final String DEPRECATED_METHODS   = "deprecated methods";
	public static final String SYNTHETIC_METHODS    = "synthetic methods";
	public static final String STATIC_METHODS       = "static methods";
	public static final String SYNCHRONIZED_METHODS = "synchronized methods";
	public static final String NATIVE_METHODS       = "native methods";

	public static final String ATTRIBUTES            = "attributes";
	public static final String PUBLIC_ATTRIBUTES     = "public attributes";
	public static final String PROTECTED_ATTRIBUTES  = "protected attributes";
	public static final String PRIVATE_ATTRIBUTES    = "private attributes";
	public static final String PACKAGE_ATTRIBUTES    = "package attributes";
	public static final String FINAL_ATTRIBUTES      = "final attributes";
	public static final String DEPRECATED_ATTRIBUTES = "deprecated attributes";
	public static final String SYNTHETIC_ATTRIBUTES  = "synthetic attributes";
	public static final String STATIC_ATTRIBUTES     = "static attributes";
	public static final String TRANSIENT_ATTRIBUTES  = "transient attributes";
	public static final String VOLATILE_ATTRIBUTES   = "volatile attributes";

	public static final String INNER_CLASSES           = "inner classes";
	public static final String PUBLIC_INNER_CLASSES    = "public inner classes";
	public static final String PROTECTED_INNER_CLASSES = "protected inner classes";
	public static final String PRIVATE_INNER_CLASSES   = "private inner classes";
	public static final String PACKAGE_INNER_CLASSES   = "package inner classes";
	public static final String ABSTRACT_INNER_CLASSES  = "abstract inner classes";
	public static final String FINAL_INNER_CLASSES     = "final inner classes";
	public static final String STATIC_INNER_CLASSES    = "static inner classes";

	public static final String DEPTH_OF_INHERITANCE  = "depth of inheritance";
	public static final String SUBCLASSES            = "subclasses";

	public static final String NLOC            = "number of lines of code";
	public static final String PARAMETERS      = "parameters";
	public static final String LOCAL_VARIABLES = "local variables";
	
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
		Measurement measurement = Measurement(name);

		if (measurement instanceof NumericalMeasurement) {
			((NumericalMeasurement) measurement).Add(delta);
		} else {
			measurement.Add(new Integer(delta));
		}
	}
	
	public void AddToMeasurement(String name, long delta) {
		Measurement measurement = Measurement(name);

		if (measurement instanceof NumericalMeasurement) {
			((NumericalMeasurement) measurement).Add(delta);
		} else {
			measurement.Add(new Long(delta));
		}
	}
	
	public void AddToMeasurement(String name, float delta) {
		Measurement measurement = Measurement(name);

		if (measurement instanceof NumericalMeasurement) {
			((NumericalMeasurement) measurement).Add(delta);
		} else {
			measurement.Add(new Float(delta));
		}
	}
	
	public void AddToMeasurement(String name, double delta) {
		Measurement measurement = Measurement(name);

		if (measurement instanceof NumericalMeasurement) {
			((NumericalMeasurement) measurement).Add(delta);
		} else {
			measurement.Add(new Double(delta));
		}
	}
	
	public void AddToMeasurement(String name, Object delta) {
		Measurement(name).Add(delta);
	}
		
	public Measurement Measurement(String name) {
		Measurement result = (Measurement) measurements.get(name);
		
		if (result == null) {
			result = new NullMeasurement();
			Track(name, result);
		}

		return result;
	}

	public Collection MeasurementNames() {
		return Collections.unmodifiableCollection(measurements.keySet());
	}
	
	public Metrics AddSubMetrics(Metrics metrics) {
		return (Metrics) submetrics.put(metrics.Name(), metrics);
	}
	
	public Collection SubMetrics() {
		return Collections.unmodifiableCollection(submetrics.values());
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
