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

import org.apache.oro.text.perl.*;

public class MetricsConfiguration {
	private static final Perl5Util perl = new Perl5Util();
	
	private List project_measurements = new LinkedList();
	private List group_measurements   = new LinkedList();
	private List class_measurements   = new LinkedList();
	private List method_measurements  = new LinkedList();
	private Map  group_definitions    = new HashMap();
	
	public List ProjectMeasurements() {
		return Collections.unmodifiableList(project_measurements);
	}

	public void AddProjectMeasurement(MeasurementDescriptor descriptor) {
		project_measurements.add(descriptor);
	}
	
	public List GroupMeasurements() {
		return Collections.unmodifiableList(group_measurements);
	}

	public void AddGroupMeasurement(MeasurementDescriptor descriptor) {
		group_measurements.add(descriptor);
	}
	
	public List ClassMeasurements() {
		return Collections.unmodifiableList(class_measurements);
	}

	public void AddClassMeasurement(MeasurementDescriptor descriptor) {
		class_measurements.add(descriptor);
	}
	
	public List MethodMeasurements() {
		return Collections.unmodifiableList(method_measurements);
	}

	public void AddMethodMeasurement(MeasurementDescriptor descriptor) {
		method_measurements.add(descriptor);
	}

	public void AddGroupDefinition(String name, String pattern) {
		Collection bucket = (Collection) group_definitions.get(name);

		if (bucket == null) {
			bucket = new LinkedList();
			group_definitions.put(name, bucket);
		}

		bucket.add(pattern);
	}

	public Collection Groups(String name) {
		Collection result = new HashSet();

		Iterator i = group_definitions.keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();

			if (group_definitions.get(key) != null) {
				Iterator j = ((Collection) group_definitions.get(key)).iterator();
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
