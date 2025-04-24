/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import org.apache.oro.text.perl.*;

import com.jeantessier.text.*;

import java.util.*;

public abstract class MeasurementBase implements Measurement {
    private static final Perl5Util perl = new Perl5Util(new MaximumCapacityPatternCache());

    protected static Perl5Util perl() {
        return perl;
    }
    
    private MeasurementDescriptor descriptor = null;
    private Metrics context = null;

    private boolean cached = false;
    private boolean empty = true;

    public MeasurementBase(MeasurementDescriptor descriptor, Metrics context, String initText) {
        this.descriptor = descriptor;
        this.context = context;
    }
    
    public MeasurementDescriptor getDescriptor() {
        return descriptor;
    }
    
    public Metrics getContext() {
        return context;
    }

    /**
     *  Tells this instance if it should reuse a previously
     *  computed value or if it should regenerate it.
     */
    protected boolean isCached() {
        return cached;
    }

    /**
     *  Sets the caching flag, telling this instance if
     *  its value has been computed.  This flag is
     *  conditional to caching being enabled in the
     *  corresponding descriptor.
     */
    protected void setCached(boolean cached) {
        this.cached = cached && getDescriptor().isCached();
    }

    public boolean isEmpty() {
        return empty;
    }

    protected void setEmpty(boolean empty) {
        this.empty = empty;
    }
    
    public String getShortName() {
        return getDescriptor().getShortName();
    }
    
    public String getLongName() {
        return getDescriptor().getLongName();
    }
    
    public Number getValue() {
        return compute();
    }

    public boolean isInRange() {
        var descriptor = Optional.ofNullable(getDescriptor());

        // Check the lower threshold, if present
        if (descriptor
                .map(MeasurementDescriptor::getLowerThreshold)
                .map(lowerThreshold -> lowerThreshold > compute())
                .orElse(false)) {
            return false;
        }

        // Check the upper threshold, if present
        if (descriptor
                .map(MeasurementDescriptor::getUpperThreshold)
                .map(upperThreshold -> upperThreshold < compute())
                .orElse(false)) {
            return false;
        }

        return true;
    }
    
    public void add(Object object) {
        // Do nothing
    }

    protected abstract double compute();

    public String toString() {
        return getValue().toString();
    }
}
