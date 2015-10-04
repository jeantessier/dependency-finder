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

import org.apache.log4j.*;

/**
 *  <p>Accumulates a set of values.  Its numerical value is the
 *  cardinality (i.e., size) of the set.  <code>OOMetrics</code>
 *  uses it to keep track of dependencies.</p>
 *
 *  <p>This is the syntax for initializing this type of
 *  measurement:</p>
 *  
 *  <pre>
 *  &lt;init&gt;
 *      [SET | LIST]
 *  &lt;/init&gt;
 *  </pre>
 *
 *  <p>Defaults to SET (i.e., does not count duplicates).</p>
 */
public class NameListMeasurement extends MeasurementBase implements CollectionMeasurement {
    private Collection<String> values;

    public NameListMeasurement(MeasurementDescriptor descriptor, Metrics context, String initText) {
        super(descriptor, context, initText);

        if (initText != null) {
            if (initText.trim().equalsIgnoreCase("list")) {
                values = new LinkedList<String>();
            } else if (initText.trim().equalsIgnoreCase("set")) {
                values = new HashSet<String>();
            } else {
                Logger.getLogger(getClass()).debug("Cannot initialize with \"" + initText + "\", using default value of SET instead");
                values = new HashSet<String>();
            }
        } else {
            Logger.getLogger(getClass()).debug("Cannot initialize with null text, using default value of SET instead");
            values = new HashSet<String>();
        }
    }

    public void add(Object object) {
        if (object instanceof String) {
            values.add((String) object);
        }
    }

    public void accept(MeasurementVisitor visitor) {
        visitor.visitNameListMeasurement(this);
    }

    public Number getValue() {
        return values.size();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    protected double compute() {
        return values.size();
    }

    public Collection<String> getValues() {
        return Collections.unmodifiableCollection(values);
    }
}
