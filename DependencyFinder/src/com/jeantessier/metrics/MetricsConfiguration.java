/*
 *  Copyright (c) 2001-2005, Jean Tessier
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

import org.apache.oro.text.perl.*;

import com.jeantessier.text.*;

public class MetricsConfiguration {
	private static final Perl5Util perl = new Perl5Util(new MaximumCapacityPatternCache());
	
	private List projectMeasurements = new LinkedList();
	private List groupMeasurements   = new LinkedList();
	private List classMeasurements   = new LinkedList();
	private List methodMeasurements  = new LinkedList();
	private Map  groupDefinitions    = new HashMap();
	
	public List getProjectMeasurements() {
		return Collections.unmodifiableList(projectMeasurements);
	}

	public void addProjectMeasurement(MeasurementDescriptor descriptor) {
		projectMeasurements.add(descriptor);
	}
	
	public List getGroupMeasurements() {
		return Collections.unmodifiableList(groupMeasurements);
	}

	public void addGroupMeasurement(MeasurementDescriptor descriptor) {
		groupMeasurements.add(descriptor);
	}
	
	public List getClassMeasurements() {
		return Collections.unmodifiableList(classMeasurements);
	}

	public void addClassMeasurement(MeasurementDescriptor descriptor) {
		classMeasurements.add(descriptor);
	}
	
	public List getMethodMeasurements() {
		return Collections.unmodifiableList(methodMeasurements);
	}

	public void addMethodMeasurement(MeasurementDescriptor descriptor) {
		methodMeasurements.add(descriptor);
	}

	public void addGroupDefinition(String name, String pattern) {
		Collection bucket = (Collection) groupDefinitions.get(name);

		if (bucket == null) {
			bucket = new LinkedList();
			groupDefinitions.put(name, bucket);
		}

		bucket.add(pattern);
	}

	public Collection getGroups(String name) {
		Collection result = new HashSet();

		Iterator i = groupDefinitions.keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();

			if (groupDefinitions.get(key) != null) {
				Iterator j = ((Collection) groupDefinitions.get(key)).iterator();
				while (j.hasNext()) {
					if (perl.match((String) j.next(), name)) {
						result.add(key);
					}
				}
			}
		}
		
		return result;
	}
}
