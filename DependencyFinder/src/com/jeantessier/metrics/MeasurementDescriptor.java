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

import java.lang.reflect.*;

public class MeasurementDescriptor {
	private static final Class constructor_signature[] = {MeasurementDescriptor.class, Metrics.class, String.class};

	private String     short_name;
	private String     long_name;
	private Class      clazz;
	private String     init_text;
	private Comparable lower_threshold;
	private Comparable upper_threshold;
	private boolean    visible         = true;	
	private boolean    cached          = true;	

	public String ShortName() {
		return short_name;
	}

	public void ShortName(String short_name) {
		this.short_name = short_name;
	}
	
	public String LongName() {
		return long_name;
	}

	public void LongName(String long_name) {
		this.long_name = long_name;
	}
	
	public Class Class() {
		return clazz;
	}

	public void Class(Class clazz) {
		if (clazz != null) {
			this.clazz = clazz;
		} else {
			throw new IllegalArgumentException("class cannot be null");
		}
	}

	public void Class(String class_name) throws ClassNotFoundException {
		this.clazz = Class.forName(class_name);
	}

	public String InitText() {
		return init_text;
	}

	public void InitText(String init_text) {
		this.init_text = init_text;
	}

	public Comparable LowerThreshold() {
		return lower_threshold;
	}

	public void LowerThreshold(Comparable lower_threshold) {
		this.lower_threshold = lower_threshold;
	}

	public Comparable UpperThreshold() {
		return upper_threshold;
	}

	public void UpperThreshold(Comparable upper_threshold) {
		this.upper_threshold = upper_threshold;
	}

	public boolean Visible() {
		return visible;
	}

	public void Visible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean Cached() {
		return cached;
	}

	public void Cached(boolean cached) {
		this.cached = cached;
	}

	public Measurement CreateMeasurement() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		return CreateMeasurement(null);
	}
	
	public Measurement CreateMeasurement(Metrics context) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Measurement result = null;

		Constructor constructor = Class().getConstructor(constructor_signature);
		Object params[] = new Object[3];
		params[0] = this;
		params[1] = context;
		params[2] = InitText();
		result = (Measurement) constructor.newInstance(params);

		return result;
	}

	public String Range() {
		StringBuffer result = new StringBuffer();

		result.append("[");
		result.append((LowerThreshold() != null) ? LowerThreshold().toString() : "*");
		result.append(", ");
		result.append((UpperThreshold() != null) ? UpperThreshold().toString() : "*");
		result.append("]");

		return result.toString();
	}
}
