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

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

/**
 *  <p>Base class that accumulates entries, filtering with regular
 *  expressions.  If no regular expressions are given, matches
 *  everything for the given measurement, which must implement
 *  the <code>CollectionMeasurement</code> interface.  Regular
 *  expressions matching using <code>Perl5Util</code> from
 *  Jakarta-ORO.  This measurement will use
 *  <code>Perl5Util.group(1)</code> if not null, otherwise the
 *  full string.</p>
 *
 *  <p>This is the syntax for initializing this type of
 *  measurement:</p>
 *  
 *  <pre>
 *  &lt;init&gt;
 *      measurement name [perl regular expression]
 *      ...
 *  &lt;/init&gt;
 *  </pre>
 */
public abstract class AccumulatorMeasurement extends MeasurementBase implements CollectionMeasurement {
    private Map<String, Collection<String>> terms  = new HashMap<String, Collection<String>>();
    private Collection<String> values = new TreeSet<String>();

    public AccumulatorMeasurement(MeasurementDescriptor descriptor, Metrics context, String initText) {
        super(descriptor, context, initText);

        if (initText != null) {
            try {
                BufferedReader in   = new BufferedReader(new StringReader(initText));
                String         line;
                
                while ((line = in.readLine()) != null) {
                    synchronized (perl()) {
                        if (perl().match("/^\\s*(\\S+)\\s*(.*)/", line)) {
                            String name = perl().group(1);
                            String re   = perl().group(2);

                            Collection<String> res = terms.get(name);
                            if (res == null) {
                                res = new ArrayList<String>();
                                terms.put(name, res);
                            }

                            if (re != null && re.length() > 0) {
                                res.add(re);
                            }
                        }
                    }
                }
                
                in.close();
            } catch (Exception ex) {
                Logger.getLogger(getClass()).debug("Cannot initialize with \"" + initText + "\"", ex);
                terms.clear();
            }
        }

        logTerms(initText);
    }

    private void logTerms(String initText) {
        Logger.getLogger(getClass()).debug("Initialize with\n" + initText);
        Logger.getLogger(getClass()).debug("Terms:");

        for (Map.Entry<String, Collection<String>> entry : terms.entrySet()) {
            Logger.getLogger(getClass()).debug("\t" + entry.getKey());

            for (String s : entry.getValue()) {
                Logger.getLogger(getClass()).debug("\t\t" + s);
            }
        }
    }

    public Number getValue() {
        return getValues().size();
    }

    public boolean isEmpty() {
        return getValues().isEmpty();
    }
    
    protected double compute() {
        return getValues().size();
    }

    public Collection<String> getValues() {
        if (!isCached()) {
            values.clear();
            
            populateValues();

            setCached(true);
        }
        
        return Collections.unmodifiableCollection(values);
    }

    protected abstract void populateValues();

    protected void filterMetrics(Metrics metrics) {
        for (Map.Entry<String, Collection<String>> entry : terms.entrySet()) {
            String name = entry.getKey();
            Collection<String> res = entry.getValue();

            Measurement measurement = metrics.getMeasurement(name);
            if (measurement instanceof CollectionMeasurement) {
                filterMeasurement((CollectionMeasurement) measurement, res);
            }
        }
    }
    
    private void filterMeasurement(CollectionMeasurement measurement, Collection<String> res) {
        if (res.isEmpty()) {
            values.addAll(measurement.getValues());
        } else {
            for (String member : measurement.getValues()) {
                filterElement(member, res);
            }
        }
    }
    
    private void filterElement(String element, Collection<String> res) {
        boolean found = false;
        Iterator<String> i = res.iterator();
        while (!found && i.hasNext()) {
            found = evaluateRE(i.next(), element);
        }
    }
    
    private boolean evaluateRE(String re, String element) {
        boolean result = false;

        synchronized (perl()) {
            if (perl().match(re, element)) {
                result = true;
                if (perl().group(1) != null) {
                    values.add(perl().group(1));
                } else {
                    values.add(element);
                }
            }
        }

        return result;
    }
}
