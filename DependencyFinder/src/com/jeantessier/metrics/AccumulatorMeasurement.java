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
import java.util.*;

import org.apache.log4j.*;

/**
 *  Accumulates entries in submetrics, filtering with regular
 *  expressions.  If no regular expressions are given, matches
 *  everything.  Otherwise, each entry must match at least one
 *  of the regular expressions, using Perl5Util.  This measurement
 *  will use Perl5Util.group(1) if not null, or else the fill
 *  string.
 * 
 *  <pre>
 *  &lt;init-text&gt;
 *      measurement name
 *      (perl regular expression)*
 *  &lt;/init-text&gt;
 *  </pre>
 */
public class AccumulatorMeasurement extends MeasurementBase implements CollectionMeasurement {
	private String name  = null;
	private List   terms = new LinkedList();

	public AccumulatorMeasurement(MeasurementDescriptor descriptor, Metrics context, String init_text) {
		super(descriptor, context, init_text);

		try {
			BufferedReader in   = new BufferedReader(new StringReader(init_text));
			String         line = in.readLine();

			if (line != null) {
				name = line.trim();
			}
			
			while ((line = in.readLine()) != null) {
				terms.add(line.trim());
			}

			in.close();
		} catch (Exception ex) {
			Logger.getLogger(getClass()).debug("Cannot initialize with \"" + init_text + "\"", ex);
			terms.clear();
		}
	}

	public void Accept(MeasurementVisitor visitor) {
		visitor.VisitAccumulatorMeasurement(this);
	}

	protected double Compute() {
		return Values().size();
	}

	public Collection Values() {
		Collection results = new TreeSet();
		
		Iterator i = Context().SubMetrics().iterator();
		while (i.hasNext()) {
			FilterMetrics((Metrics) i.next(), results);
		}
		
		return results;
	}

	private void FilterMetrics(Metrics metrics, Collection results) {
		Measurement measurement = metrics.Measurement(name);
		if (measurement instanceof CollectionMeasurement) {
			Iterator i = ((CollectionMeasurement) measurement).Values().iterator();
			while (i.hasNext()) {
				FilterElement((String) i.next(), results);
			}
		}
	}

	private void FilterElement(String element, Collection results) {
		if (terms.isEmpty()) {
			results.add(element);
		} else {
			boolean found = false;
			Iterator i = terms.iterator();
			while (!found && i.hasNext()) {
				found = EvaluateRE((String) i.next(), element, results);
			}
		}
	}
	
	private synchronized boolean EvaluateRE(String re, String element, Collection results) {
		boolean result = false;
		
		if (Perl().match(re, element)) {
			result = true;
			if (Perl().group(1) != null) {
				results.add(Perl().group(1));
			} else {
				results.add(element);
			}
		}

		return result;
	}
}
