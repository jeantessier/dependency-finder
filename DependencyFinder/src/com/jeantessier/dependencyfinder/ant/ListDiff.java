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

import org.apache.tools.ant.*;

import com.jeantessier.diff.*;

public class ListDiff extends Task {
    private String name = "";
    private File oldFile;
    private String oldLabel;
    private File newFile;
    private String newLabel;
    private boolean compress = false;
    private String encoding = ListDiffPrinter.DEFAULT_ENCODING;
    private String dtdPrefix = ListDiffPrinter.DEFAULT_DTD_PREFIX;
    private String indentText;
    private File destfile;

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public File getOld() {
        return oldFile;
    }
    
    public void setOld(File oldFile) {
        this.oldFile = oldFile;
    }

    public String getOldlabel() {
        return oldLabel;
    }
    
    public void setOldlabel(String oldLabel) {
        this.oldLabel = oldLabel;
    }
    
    public File getNew() {
        return newFile;
    }
    
    public void setNew(File newFile) {
        this.newFile = newFile;
    }

    public String getNewlabel() {
        return newLabel;
    }
    
    public void setNewlabel(String newLabel) {
        this.newLabel = newLabel;
    }

    public boolean getCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }
    
    public String getEncoding() {
        return encoding;
    }
    
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    
    public String getDtdprefix() {
        return dtdPrefix;
    }
    
    public void setDtdprefix(String dtdPrefix) {
        this.dtdPrefix = dtdPrefix;
    }

    public String getIndenttext() {
        return indentText;
    }
    
    public void setIntenttext(String indentText) {
        this.indentText = indentText;
    }

    public File getDestfile() {
        return destfile;
    }
    
    public void setDestfile(File destfile) {
        this.destfile = destfile;
    }

    // Visible for tests only
    void validateParameters() throws BuildException {
        if (getOld() == null) {
            throw new BuildException("old must be set!");
        }

        if (!getOld().exists()) {
            throw new BuildException("old does not exist!");
        }

        if (!getOld().isFile()) {
            throw new BuildException("old is not a file!");
        }

        if (getOldlabel() == null) {
            setOldlabel(getOld().getPath());
        }

        if (getNew() == null) {
            throw new BuildException("new must be set!");
        }

        if (!getNew().exists()) {
            throw new BuildException("new does not exist!");
        }

        if (!getNew().isFile()) {
            throw new BuildException("new is not a file!");
        }

        if (getNewlabel() == null) {
            setNewlabel(getNew().getPath());
        }

        if (getDestfile() == null) {
            throw new BuildException("destfile must be set!");
        }
    }
    
    public void execute() throws BuildException {
        validateParameters();

        try {
            String line;
            
            log("Loading old list from " + getOld().getAbsolutePath());
            Collection<String> oldAPI = new TreeSet<String>();
            BufferedReader oldIn = new BufferedReader(new FileReader(getOld()));
            while((line = oldIn.readLine()) != null) {
                oldAPI.add(line);
            }
            oldIn.close();
            
            log("Loading new list from " + getNew().getAbsolutePath());
            Collection<String> newAPI = new TreeSet<String>();
            BufferedReader newIn = new BufferedReader(new FileReader(getNew()));
            while((line = newIn.readLine()) != null) {
                newAPI.add(line);
            }
            newIn.close();
            
            log("Comparing old and new lists ...");

            ListDiffPrinter printer = new ListDiffPrinter(getCompress(), getEncoding(), getDtdprefix());
            printer.setName(getName());
            printer.setOldVersion(getOldlabel());
            printer.setNewVersion(getNewlabel());
            if (getIndenttext() != null) {
                printer.setIndentText(getIndenttext());
            }

            for (String name : oldAPI) {
                if (!newAPI.contains(name)) {
                    printer.remove(name);
                }
            }

            for (String name : newAPI) {
                if (!oldAPI.contains(name)) {
                    printer.add(name);
                }
            }

            log("Saving difference report to " + getDestfile().getAbsolutePath());

            PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));
            out.print(printer);
            out.close();
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }
}
