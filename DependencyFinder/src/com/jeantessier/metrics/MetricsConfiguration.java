/*
 *  Copyright (c) 2001-2009, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
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
    
    private List<MeasurementDescriptor> projectMeasurements = new LinkedList<MeasurementDescriptor>();
    private List<MeasurementDescriptor> groupMeasurements = new LinkedList<MeasurementDescriptor>();
    private List<MeasurementDescriptor> classMeasurements = new LinkedList<MeasurementDescriptor>();
    private List<MeasurementDescriptor> methodMeasurements = new LinkedList<MeasurementDescriptor>();
    private Map<String, Collection<String>> groupDefinitions = new HashMap<String, Collection<String>>();
    
    public List<MeasurementDescriptor> getProjectMeasurements() {
        return Collections.unmodifiableList(projectMeasurements);
    }

    public void addProjectMeasurement(MeasurementDescriptor descriptor) {
        projectMeasurements.add(descriptor);
    }
    
    public List<MeasurementDescriptor> getGroupMeasurements() {
        return Collections.unmodifiableList(groupMeasurements);
    }

    public void addGroupMeasurement(MeasurementDescriptor descriptor) {
        groupMeasurements.add(descriptor);
    }
    
    public List<MeasurementDescriptor> getClassMeasurements() {
        return Collections.unmodifiableList(classMeasurements);
    }

    public void addClassMeasurement(MeasurementDescriptor descriptor) {
        classMeasurements.add(descriptor);
    }
    
    public List<MeasurementDescriptor> getMethodMeasurements() {
        return Collections.unmodifiableList(methodMeasurements);
    }

    public void addMethodMeasurement(MeasurementDescriptor descriptor) {
        methodMeasurements.add(descriptor);
    }

    public void addGroupDefinition(String name, String pattern) {
        Collection<String> bucket = groupDefinitions.get(name);

        if (bucket == null) {
            bucket = new LinkedList<String>();
            groupDefinitions.put(name, bucket);
        }

        bucket.add(pattern);
    }

    public Collection<String> getGroups(String name) {
        Collection<String> result = new HashSet<String>();

        for (String key : groupDefinitions.keySet()) {
            if (groupDefinitions.get(key) != null) {
                for (String pattern : groupDefinitions.get(key)) {
                    if (perl.match(pattern, name)) {
                        result.add(key);
                    }
                }
            }
        }
        
        return result;
    }
}
