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

package com.jeantessier.dependencyfinder.ant;

import java.io.*;
import java.util.*;

import com.jeantessier.classreader.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

public class ListDeprecatedElements extends Task {
    private File    destfile;
    private Path    path;

    public File getDestfile() {
        return destfile;
    }
    
    public void setDestfile(File destfile) {
        this.destfile = destfile;
    }
    
    public Path createPath() {
        if (path == null) {
            path = new Path(getProject());
        }

        return path;
    }
    
    public Path getPath() {
        return path;
    }
    
    public void execute() throws BuildException {
        // first off, make sure that we've got what we need

        if (getPath() == null) {
            throw new BuildException("path must be set!");
        }

        if (getDestfile() == null) {
            throw new BuildException("destfile must be set!");
        }

        log("Saving elements to " + getDestfile().getAbsolutePath());
        
        try {
            PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));

            log("Reading classes from path " + getPath());

            VerboseListener    verboseListener = new VerboseListener(this);
            DeprecationPrinter printer         = new DeprecationPrinter(out);
            
            ClassfileLoader loader = new AggregatingClassfileLoader();
            loader.addLoadListener(verboseListener);
            loader.addLoadListener(new LoadListenerVisitorAdapter(printer));
            loader.load(Arrays.asList(getPath().list()));
            
            out.close();
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }
}
