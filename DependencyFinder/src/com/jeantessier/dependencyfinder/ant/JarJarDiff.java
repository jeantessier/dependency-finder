/*
 *  Copyright (c) 2001-2005, Jean Tessier
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
import org.apache.tools.ant.types.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;
import com.jeantessier.diff.*;

public class JarJarDiff extends Task {
    private String name             = "";
    private Path   oldPath;
    private String oldLabel;
    private File   oldDocumentation = new File("old_documentation.txt");
    private Path   newPath;
    private String newLabel;
    private File   newDocumentation = new File("new_documentation.txt");
    private String encoding         = Report.DEFAULT_ENCODING;
    private String dtdPrefix        = Report.DEFAULT_DTD_PREFIX;
    private String indentText;
    private File   destfile;

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Path createOld() {
        if (oldPath == null) {
            oldPath = new Path(getProject());
        }

        return oldPath;
    }
    
    public Path getOld() {
        return oldPath;
    }

    public String getOldlabel() {
        return oldLabel;
    }
    
    public void setOldlabel(String oldLabel) {
        this.oldLabel = oldLabel;
    }

    public File getOlddocumentation() {
        return oldDocumentation;
    }
    
    public void setOlddocumentation(File oldDocumentation) {
        this.oldDocumentation = oldDocumentation;
    }
    
    public Path createNew() {
        if (newPath == null) {
            newPath = new Path(getProject());
        }

        return newPath;
    }
    
    public Path getNew() {
        return newPath;
    }

    public String getNewlabel() {
        return newLabel;
    }
    
    public void setNewlabel(String newLabel) {
        this.newLabel = newLabel;
    }

    public File getNewdocumentation() {
        return newDocumentation;
    }
    
    public void setNewdocumentation(File newDocumentation) {
        this.newDocumentation = newDocumentation;
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
    
    public void execute() throws BuildException {
        // first off, make sure that we've got what we need

        if (getOld() == null) {
            throw new BuildException("old must be set!");
        }

        if (getNew() == null) {
            throw new BuildException("new must be set!");
        }

        if (getDestfile() == null) {
            throw new BuildException("destfile must be set!");
        }

        VerboseListener verboseListener = new VerboseListener(this);

        try {
            // Collecting data, first classfiles from JARs,
            // then package/class trees using NodeFactory.
            
            log("Loading old classes from path " + getOld());
            Validator oldValidator = new ListBasedValidator(getOlddocumentation());
            ClassfileLoader oldJar = new AggregatingClassfileLoader();
            oldJar.addLoadListener(verboseListener);
            oldJar.load(Arrays.asList(getOld().list()));
            
            log("Loading new classes from path " + getNew());
            Validator newValidator = new ListBasedValidator(getNewdocumentation());
            ClassfileLoader newJar = new AggregatingClassfileLoader();
            newJar.addLoadListener(verboseListener);
            newJar.load(Arrays.asList(getNew().list()));
            
            // Starting to compare, first at package level,
            // then descending to class level for packages
            // that are in both the old and the new codebase.
            
            log("Comparing old and new classes ...");
            
            String name     = getName();
            String oldLabel = (getOldlabel() != null) ? getOldlabel() : getOld().toString();
            String newLabel = (getNewlabel() != null) ? getNewlabel() : getNew().toString();
            
            DifferencesFactory factory = new DifferencesFactory(oldValidator, newValidator);
            Differences differences = factory.createJarDifferences(name, oldLabel, oldJar, newLabel, newJar);
            
            log("Saving difference report to " + getDestfile().getAbsolutePath());
            
            com.jeantessier.diff.Printer printer = new Report(getEncoding(), getDtdprefix());
            if (getIndenttext() != null) {
                printer.setIndentText(getIndenttext());
            }
            
            differences.accept(printer);
            
            PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));
            out.print(printer);
            out.close();
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }
}
