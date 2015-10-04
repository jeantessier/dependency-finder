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

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

public abstract class GraphTask extends Task {
    private boolean validate = false;
    private Path    src;
    private File    destfile;

    public boolean getValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }
    
    public Path createSrc() {
        if (src == null) {
            src = new Path(getProject());
        }

        return src;
    }
    
    public Path getSrc() {
        return src;
    }

    public Path getSrcfile() {
        return src;
    }
    
    public void setSrcfile(Path srcfile) {
        if (src == null) {
            src = srcfile;
        } else {
            src.append(srcfile);
        }
    }

    public File getDestfile() {
        return destfile;
    }
    
    public void setDestfile(File destfile) {
        this.destfile = destfile;
    }

    protected void validateParameters() throws BuildException {
        if (getSrcfile() == null) {
            throw new BuildException("src or srcfile must be set!");
        }
        
        if (getSrc().size() == 0) {
            throw new BuildException("src and srcfile are both empty!");
        }

        if (getDestfile() == null) {
            throw new BuildException("destfile must be set!");
        }
    }
}
