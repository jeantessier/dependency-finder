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

import java.io.*;

import org.apache.log4j.*;

/**
 *  <pre>
 *  &lt;init-text&gt;
 *      base measurement name [DISPOSE_x]
 *      divider measurement name [DISPOSE_x]
 *  &lt;/init-text&gt;
 *  </pre>
 *  
 *  <p>If either is missing, this measurement will be NaN.</p>
 */
public class RatioMeasurement extends MeasurementBase {
	private String base_name;
	private String divider_name;

	public RatioMeasurement(MeasurementDescriptor descriptor, Metrics context, String init_text) {
		super(descriptor, context, init_text);

		try {
			BufferedReader in = new BufferedReader(new StringReader(init_text));
			base_name    = in.readLine().trim();
			divider_name = in.readLine().trim();
			in.close();
		} catch (Exception ex) {
			Logger.getLogger(getClass()).debug("Cannot initialize with \"" + init_text + "\"", ex);
			base_name    = null;
			divider_name = null;
		}
	}
	
	public String BaseName() {
		return base_name;
	}

	public String DividerName() {
		return divider_name;
	}

	public void Accept(MeasurementVisitor visitor) {
		visitor.VisitRatioMeasurement(this);
	}

	protected double Compute() {
		double result = Double.NaN;

		if (Context() != null && BaseName() != null && DividerName() != null) {
			Measurement base    = Context().Measurement(BaseName());
			Measurement divider = Context().Measurement(DividerName());
			
			if (base != null && divider != null) {
				result = base.doubleValue() / divider.doubleValue();
			}
		}
		
		return result;
	}
}
