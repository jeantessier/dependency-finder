/*
 *  Copyright (c) 2001-2003, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of the Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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
	private String name              = "";
	private Path   old_path;
	private String old_label;
	private File   old_documentation = new File("old_documentation.txt");
	private Path   new_path;
	private String new_label;
	private File   new_documentation = new File("new_documentation.txt");
	private String dtd_prefix        = Report.DEFAULT_DTD_PREFIX;
	private String indent_text;
	private File   destfile;

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Path createOld() {
		if (old_path == null) {
			old_path = new Path(getProject());
		}

		return old_path;
	}
	
	public Path getOld() {
		return old_path;
	}

	public String getOldlabel() {
		return old_label;
	}
	
	public void setOldlabel(String old_label) {
		this.old_label = old_label;
	}

	public File getOlddocumentation() {
		return old_documentation;
	}
	
	public void setOlddocumentation(File old_documentation) {
		this.old_documentation = old_documentation;
	}
	
	public Path createNew() {
		if (new_path == null) {
			new_path = new Path(getProject());
		}

		return new_path;
	}
	
	public Path getNew() {
		return new_path;
	}

	public String getNewlabel() {
		return new_label;
	}
	
	public void setNewlabel(String new_label) {
		this.new_label = new_label;
	}

	public File getNewdocumentation() {
		return new_documentation;
	}
	
	public void setNewdocumentation(File new_documentation) {
		this.new_documentation = new_documentation;
	}

	public String getDtdprefix() {
		return dtd_prefix;
	}
	
	public void setDtdprefix(String dtd_prefix) {
		this.dtd_prefix = dtd_prefix;
	}

	public String getIndenttext() {
		return indent_text;
	}
	
	public void setIntenttext(String indent_text) {
		this.indent_text = indent_text;
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

		VerboseListener verbose_listener = new VerboseListener(this);

		try {
			// Collecting data, first classfiles from JARs,
			// then package/class trees using NodeFactory.
			
			log("Loading old codebase ...");
			Validator old_validator = new ListBasedValidator(getOlddocumentation());
			ClassfileLoader old_jar = new AggregatingClassfileLoader();
			old_jar.addLoadListener(verbose_listener);
			old_jar.Load(Arrays.asList(getOld().list()));
			
			log("Loading new codebase ...");
			Validator new_validator = new ListBasedValidator(getNewdocumentation());
			ClassfileLoader new_jar = new AggregatingClassfileLoader();
			new_jar.addLoadListener(verbose_listener);
			new_jar.Load(Arrays.asList(getNew().list()));
			
			// Starting to compare, first at package level,
			// then descending to class level for packages
			// that are in both the old and the new codebase.
			
			log("Comparing ...");
			
			String name      = getName();
			String old_label = (getOldlabel() != null) ? getOldlabel() : getOld().toString();
			String new_label = (getNewlabel() != null) ? getNewlabel() : getNew().toString();
			
			DifferencesFactory factory = new DifferencesFactory(old_validator, new_validator);
			Differences differences = factory.CreateJarDifferences(name, old_label, old_jar, new_label, new_jar);
			
			log("Saving difference report to " + getDestfile().getAbsolutePath());
			
			com.jeantessier.diff.Printer printer = new Report(getDtdprefix());
			if (getIndenttext() != null) {
				printer.IndentText(getIndenttext());
			}
			
			differences.Accept(printer);
			
			PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));
			out.print(printer);
			out.close();
		} catch (IOException ex) {
			throw new BuildException(ex);
		}
	}
}
