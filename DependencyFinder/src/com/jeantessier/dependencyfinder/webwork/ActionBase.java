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

package com.jeantessier.dependencyfinder.webwork;

import java.util.*;

import com.opensymphony.webwork.interceptor.*;
import com.opensymphony.xwork.*;
import org.apache.oro.text.perl.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;

public abstract class ActionBase extends ActionSupport implements ApplicationAware {
    protected Map application;

    protected String source;
    protected String mode;

    protected ClassfileLoaderDispatcher dispatcher;
    protected NodeFactory factory;
    protected Monitor monitor;

    public void setApplication(Map application) {
        this.application = application;
        setUpApplicationParameters();
        setUpApplicationState();
    }

    private void setUpApplicationState() {
        dispatcher = (ClassfileLoaderDispatcher) application.get("dispatcher");
        factory = (NodeFactory) application.get("factory");
        monitor = (Monitor) application.get("monitor");
    }

    private void setUpApplicationParameters() {
        source = (String) application.get("source");
        mode = (String) application.get("mode");
    }

    public Collection<String> getSources() {
        Perl5Util perl = new Perl5Util();
        Collection<String> sources = new LinkedList<String>();
        perl.split(sources, "/,\\s*/", source);
        return sources;
    }
}
