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

import java.lang.reflect.*;

public class MeasurementDescriptor {
    private static final Class constructorSignature[] = {MeasurementDescriptor.class, Metrics.class, String.class};

    private String     shortName;
    private String     longName;
    private Class      classFor;
    private String     initText;
    private Comparable lowerThreshold;
    private Comparable upperThreshold;
    private boolean    visible        = true;
    private boolean    cached         = true;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    
    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }
    
    public Class getClassFor() {
        return classFor;
    }

    public void setClassFor(Class classFor) {
        if (classFor != null) {
            this.classFor = classFor;
        } else {
            throw new IllegalArgumentException("class cannot be null");
        }
    }

    public void getClassForByName(String className) throws ClassNotFoundException {
        this.classFor = Class.forName(className);
    }

    public String getInitText() {
        return initText;
    }

    public void setInitText(String initText) {
        this.initText = initText;
    }

    public Comparable getLowerThreshold() {
        return lowerThreshold;
    }

    public void setLowerThreshold(Comparable lowerThreshold) {
        this.lowerThreshold = lowerThreshold;
    }

    public Comparable getUpperThreshold() {
        return upperThreshold;
    }

    public void setUpperThreshold(Comparable upperThreshold) {
        this.upperThreshold = upperThreshold;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public Measurement createMeasurement() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return createMeasurement(null);
    }
    
    public Measurement createMeasurement(Metrics context) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Measurement result = null;

        Constructor constructor = getClassFor().getConstructor(constructorSignature);
        Object params[] = new Object[3];
        params[0] = this;
        params[1] = context;
        params[2] = getInitText();
        result = (Measurement) constructor.newInstance(params);

        return result;
    }

    public String getRangeAsString() {
        StringBuffer result = new StringBuffer();

        result.append("[");
        result.append((getLowerThreshold() != null) ? getLowerThreshold().toString() : "*");
        result.append(", ");
        result.append((getUpperThreshold() != null) ? getUpperThreshold().toString() : "*");
        result.append("]");

        return result.toString();
    }
}
