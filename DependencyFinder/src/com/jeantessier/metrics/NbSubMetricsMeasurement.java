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
 *  Counts the number of submetrics according to selection
 *  criteria.  If there are no criteria, it matches all
 *  submetrics.  Each criterion is a boolean expression
 *  with measurement names, numbers, and boolean operators
 *  (<, <=, >, >=, ==, and !=).  If a submetric matches
 *  any one of the expressions in the criteria, it is
 *  included in the count.
 *  
 *  <pre>
 *  &lt;init-text&gt;
 *      (number | measurement name [DISPOSE_x]) [operator [(number | measurement name [DISPOSE_x])]]*
 *      ...
 *  &lt;/init-text&gt;
 *  </pre>
 */
public class NbSubMetricsMeasurement extends MeasurementBase {
	private static final String OPERATORS = "/(<=)|(<)|(>=)|(>)|(==)|(!=)/";

	private static final String LESSER_THAN           = "<";
	private static final String LESSER_THAN_OR_EQUAL  = "<=";
	private static final String GREATER_THAN          = ">";
	private static final String GREATER_THAN_OR_EQUAL = ">=";
	private static final String EQUALS                = "==";
	private static final String NOT_EQUALS            = "!=";

	private static final double DELTA = 0.1;

	private List terms = new LinkedList();
	private int  value = 0;

	public NbSubMetricsMeasurement(MeasurementDescriptor descriptor, Metrics context, String init_text) {
		super(descriptor, context, init_text);

		try {
			BufferedReader in   = new BufferedReader(new StringReader(init_text));
			String         line;

			while ((line = in.readLine()) != null) {
				terms.add(line.trim());
			}

			in.close();
		} catch (Exception ex) {
			Logger.getLogger(getClass()).debug("Cannot initialize with \"" + init_text + "\"", ex);
			terms.clear();
		}
	}

	public List Terms() {
		return terms;
	}
	
	public void Accept(MeasurementVisitor visitor) {
		visitor.VisitNbSubMetricsMeasurement(this);
	}

	protected double Compute() {
		if (!Cached()) {
			value = 0;

			if (Terms().isEmpty()) {
				value = Context().SubMetrics().size();
			} else {
				Iterator i = Context().SubMetrics().iterator();
				while (i.hasNext()) {
					Metrics metrics = (Metrics) i.next();
					
					if (SelectMetrics(metrics)) {
						value++;
					}
				}
			}

			Cached(true);
		}
		
		return value;
	}

	private boolean SelectMetrics(Metrics metrics) {
		boolean result = Terms().isEmpty();
		
		Iterator i = Terms().iterator();
		while (!result && i.hasNext()) {
			result = EvaluateTerm((String) i.next(), metrics);
		}

		return result;
	}

	private boolean EvaluateTerm(String term, Metrics metrics) {
		boolean result;

		Logger.getLogger(getClass()).debug("EvaluateTerm(\"" + term + "\", " + metrics + ")");
		
	    List elements = new ArrayList();
		Perl().split(elements, OPERATORS, term);

		result = (elements.size() > 0) && ((elements.size() % 2) == 1);
		
		if (elements.size() == 1) {
			result = metrics.HasMeasurement((String) elements.remove(0));
		} else {
			while (result && (elements.size() > 2) && ((elements.size() % 2) == 1)) {
				String left_string  = (String) elements.remove(0);
				String operator     = (String) elements.remove(0);
				String right_string = (String) elements.get(0);

				double left_operand = 0;
				try {
					left_operand = Double.parseDouble(left_string);
				} catch (NumberFormatException ex) {
					try {
						left_operand = ResolveOperand(left_string, metrics);
					} catch (NullPointerException ex2) {
						result = false;
					}
				}

				double right_operand = 0;
				try {
					right_operand = Double.parseDouble(right_string);
				} catch (NumberFormatException ex) {
					try {
						right_operand = ResolveOperand(right_string, metrics);
					} catch (NullPointerException ex2) {
						result = false;
					}
				}

				if (result) {
					if (operator.equals(LESSER_THAN)) {
						result = left_operand < right_operand;
					} else if (operator.equals(LESSER_THAN_OR_EQUAL)) {
						result = left_operand <= right_operand;
					} else if (operator.equals(GREATER_THAN)) {
						result = left_operand > right_operand;
					} else if (operator.equals(GREATER_THAN_OR_EQUAL)) {
						result = left_operand >= right_operand;
					} else if (operator.equals(EQUALS)) {
						result = Math.abs(left_operand - right_operand) <= DELTA;
					} else if (operator.equals(NOT_EQUALS)) {
						result = Math.abs(left_operand - right_operand) > DELTA;
					}
				}
			}
		}

		Logger.getLogger(getClass()).debug("EvaluateTerm(\"" + term + "\", " + metrics + "): " + result);

		return result;
	}

	private double ResolveOperand(String name, Metrics metrics) {
		double result = 0;
			
		name = name.trim();

		Logger.getLogger(getClass()).debug("ResolveOperand(\"" + name + "\", " + metrics + ")");

		if (name.length() != 0) {
			int dispose;

			synchronized (Perl()) {
				if (Perl().match("/(.*)\\s+(dispose_\\w+)$/i", name)) {
					name = Perl().group(1);
					
					String dispose_text = Perl().group(2);
					
					if (dispose_text.equalsIgnoreCase("DISPOSE_IGNORE")) {
						dispose = StatisticalMeasurement.DISPOSE_IGNORE;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_MINIMUM")) {
						dispose = StatisticalMeasurement.DISPOSE_MINIMUM;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_MEDIAN")) {
						dispose = StatisticalMeasurement.DISPOSE_MEDIAN;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_AVERAGE")) {
						dispose = StatisticalMeasurement.DISPOSE_AVERAGE;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_MAXIMUM")) {
						dispose = StatisticalMeasurement.DISPOSE_MAXIMUM;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_SUM")) {
						dispose = StatisticalMeasurement.DISPOSE_SUM;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_NB_DATA_POINTS")) {
						dispose = StatisticalMeasurement.DISPOSE_NB_DATA_POINTS;
					} else {
						dispose = StatisticalMeasurement.DISPOSE_IGNORE;
					}
				} else {
					dispose = StatisticalMeasurement.DISPOSE_IGNORE;
				}
			}
			
			Measurement measurement = metrics.Measurement(name);
			
			if (measurement instanceof StatisticalMeasurement) {
				StatisticalMeasurement stats = (StatisticalMeasurement) measurement;
				
				switch (dispose) {
					case StatisticalMeasurement.DISPOSE_MINIMUM:
						result = stats.Minimum();
						break;
					case StatisticalMeasurement.DISPOSE_MEDIAN:
						result = stats.Median();
						break;
					case StatisticalMeasurement.DISPOSE_AVERAGE:
						result = stats.Average();
						break;
					case StatisticalMeasurement.DISPOSE_MAXIMUM:
						result = stats.Maximum();
						break;
					case StatisticalMeasurement.DISPOSE_SUM:
						result = stats.Sum();
						break;
					case StatisticalMeasurement.DISPOSE_NB_DATA_POINTS:
						result = stats.NbDataPoints();
						break;
					case StatisticalMeasurement.DISPOSE_IGNORE:
					default:
						result = stats.doubleValue();
						break;
				}
			} else if (measurement instanceof NullMeasurement) {
				throw new NullPointerException();
			} else {
				result = measurement.doubleValue();
			}
		}

		Logger.getLogger(getClass()).debug("ResolveOperand(\"" + name + "\", " + metrics + "): " + result);
		
		return result;
	}
}
