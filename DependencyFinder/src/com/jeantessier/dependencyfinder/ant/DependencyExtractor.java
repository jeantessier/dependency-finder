/*
 *  Copyright (c) 2001-2004, Jean Tessier
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
 *  	* Neither the name of Jean Tessier nor the names of his contributors
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

public class DependencyExtractor extends Task {
	private boolean xml         = false;
	private boolean minimize    = false;
	private boolean maximize    = false;
	private String  encoding    = com.jeantessier.dependency.XMLPrinter.DEFAULT_ENCODING;
	private String  dtd_prefix  = com.jeantessier.dependency.XMLPrinter.DEFAULT_DTD_PREFIX;
	private String  indent_text;
	private File    destfile;
	private Path    path;

	public boolean getXml() {
		return xml;
	}

	public void setXml(boolean xml) {
		this.xml = xml;
	}

	public boolean getMinimize() {
		return minimize;
	}

	public void setMinimize(boolean minimize) {
		this.minimize = minimize;
	}

	public boolean getMaximize() {
		return maximize;
	}

	public void setMaximize(boolean maximize) {
		this.maximize = maximize;
	}

	public String getEncoding() {
		return encoding;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
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

		log("Reading classes from path " + getPath());

		VerboseListener verbose_listener = new VerboseListener(this);

		NodeFactory factory = new NodeFactory();
		CodeDependencyCollector collector = new CodeDependencyCollector(factory);
		
		ClassfileLoader loader = new TransientClassfileLoader();
		loader.addLoadListener(collector);
		loader.addLoadListener(verbose_listener);
		loader.load(Arrays.asList(getPath().list()));

		if (getMinimize()) {
			LinkMinimizer minimizer = new LinkMinimizer();
			minimizer.TraverseNodes(factory.Packages().values());
		} else if (getMaximize()) {
			LinkMaximizer maximizer = new LinkMaximizer();
			maximizer.TraverseNodes(factory.Packages().values());
		}

		log("Saving dependency graph to " + getDestfile().getAbsolutePath());
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));

			com.jeantessier.dependency.Printer printer;
			if (getXml()) {
				printer = new com.jeantessier.dependency.XMLPrinter(out, getEncoding(), getDtdprefix());
			} else {
				printer = new com.jeantessier.dependency.TextPrinter(out);
			}
				
			if (getIndenttext() != null) {
				printer.IndentText(getIndenttext());
			}
				
			printer.TraverseNodes(factory.Packages().values());
				
			out.close();
		} catch (IOException ex) {
			throw new BuildException(ex);
		}
	}
}
