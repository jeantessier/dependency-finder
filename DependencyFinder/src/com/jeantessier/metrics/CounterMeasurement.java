/*
 *  Dependency Finder - Computes quality factors from compiled Java code
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeantessier.metrics;

public class CounterMeasurement extends MeasurementBase implements NumericalMeasurement {
	private Number value;

	public CounterMeasurement(String name) {
		this(name, new Double(0.0));
	}
	
	public CounterMeasurement(String name, double starting_value) {
		this(name, new Double(starting_value));
	}
	
	public CounterMeasurement(String name, Number starting_value) {
		super(name);
		
		value = starting_value;
	}

	public void Add(Object object) {
		if (object instanceof Number) {
			value = new Double(value.doubleValue() + ((Number) object).doubleValue());
		}
	}

	public void Accept(MeasurementVisitor visitor) {
		visitor.VisitCounterMeasurement(this);
	}

	public Number Value() {
		return value;
	}
}
