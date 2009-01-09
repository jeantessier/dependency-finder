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

import com.jeantessier.classreader.*;

public class MetricsEvent extends EventObject {
    private Classfile   classfile;
    private Method_info method;
    private Metrics     metrics;
    private int         size;
    
    public MetricsEvent(Object source) {
        this(source, null, null, null, 0);
    }
    
    public MetricsEvent(Object source, Classfile classfile) {
        this(source, classfile, null, null, 0);
    }
    
    public MetricsEvent(Object source, Classfile classfile, Metrics metrics) {
        this(source, classfile, null, metrics, 0);
    }
        
    public MetricsEvent(Object source, Method_info method) {
        this(source, method.getClassfile(), method, null, 0);
    }
        
    public MetricsEvent(Object source, Method_info method, Metrics metrics) {
        this(source, method.getClassfile(), method, metrics, 0);
    }
    
    public MetricsEvent(Object source, int size) {
        this(source, null, null, null, size);
    }

    public MetricsEvent(Object source, Classfile classfile, Method_info method, Metrics metrics, int size) {
        super(source);

        this.classfile = classfile;
        this.method    = method;
        this.metrics   = metrics;
        this.size      = size;
    }

    public Classfile getClassfile() {
        return classfile;
    }

    public Method_info getMethod() {
        return method;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public int getSize() {
        return size;
    }
}
