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

import org.xml.sax.*;

import com.jeantessier.dependency.*;

public class DependencyReporter extends GraphTask {

	private boolean minimize   = false;
	private boolean maximize   = false;
	private boolean copy_only  = false;
	private boolean serialize  = false;
	private boolean xml        = false;
	private String  dtd_prefix = XMLPrinter.DEFAULT_DTD_PREFIX;
	private String  indent_text;

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

	public boolean getCopyOnly() {
		return copy_only;
	}

	public void setCopyOnly(boolean copy_only) {
		this.copy_only = copy_only;
	}

	public boolean getSerialize() {
		return serialize;
	}
	
	public void setSerialize(boolean serialize) {
		this.serialize = serialize;
	}

	public boolean getXml() {
		return xml;
	}

	public void setXml(boolean xml) {
		this.xml = xml;
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
	
	public void execute() throws BuildException {
		// first off, make sure that we've got what we need
		CheckParameters();

		VerboseListener verbose_listener = new VerboseListener(this);

		try {
			GraphCopier copier;
			if (getCopyOnly() || getMaximize()) {
				copier = new GraphCopier(Strategy());
			} else {
				copier = new GraphSummarizer(ScopeCriteria(), FilterCriteria());
			}

			String[] filenames = getSrc().list();
			for (int i=0; i<filenames.length; i++) {
				log("Reading graph from " + filenames[i]);
				
				Collection packages;
				if (filenames[i].endsWith(".xml")) {
					NodeLoader loader = new NodeLoader(getValidate());
					loader.addDependencyListener(verbose_listener);
					packages = loader.Load(filenames[i]).Packages().values();
				} else if (filenames[i].endsWith(".ser")) {
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(filenames[i]));
					packages = (Collection) in.readObject();
				} else {
					packages = Collections.EMPTY_LIST;
				}
				
				if (getMaximize()) {
					log("Maximizing ...");
					new LinkMaximizer().TraverseNodes(packages);
				} else if (getMinimize()) {
					log("Minimizing ...");
					new LinkMinimizer().TraverseNodes(packages);
				}
				
				copier.TraverseNodes(packages);
			}

			log("Saving dependency graph to " + getDestfile().getAbsolutePath());
		
			if (getSerialize()) {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(getDestfile()));
				out.writeObject(new ArrayList(copier.ScopeFactory().Packages().values()));
				out.close();
			} else {
				PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));

				Printer printer;
				if (getXml()) {
					printer = new XMLPrinter(out, getDtdprefix());
				} else {
					printer = new TextPrinter(out);
				}
				
				if (getIndenttext() != null) {
					printer.IndentText(getIndenttext());
				}
				
				printer.TraverseNodes(copier.ScopeFactory().Packages().values());
				
				out.close();
			}
		} catch (SAXException ex) {
			throw new BuildException(ex);
		} catch (ClassNotFoundException ex) {
			throw new BuildException(ex);
		} catch (IOException ex) {
			throw new BuildException(ex);
		}
	}
}
