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

import org.xml.sax.*;

import com.jeantessier.dependency.*;

public class DependencyClosure extends GraphTask {

	private String  maximumInboundDepth  = "";
	private String  maximumOutboundDepth = "";
	private boolean xml                  = false;
	private String  encoding             = XMLPrinter.DEFAULT_ENCODING;
	private String  dtdPrefix            = XMLPrinter.DEFAULT_DTD_PREFIX;
	private String  indentText;

	public String getMaximuminbounddepth() {
		return maximumInboundDepth;
	}

	public void setMaximuminbounddepth(String maximumInboundDepth) {
		this.maximumInboundDepth = maximumInboundDepth;
	}
	
	public String getMaximumoutbounddepth() {
		return maximumOutboundDepth;
	}

	public void setMaximumoutbounddepth(String maximumOutboundDepth) {
		this.maximumOutboundDepth = maximumOutboundDepth;
	}

	public boolean getXml() {
		return xml;
	}

	public void setXml(boolean xml) {
		this.xml = xml;
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
	
	public void execute() throws BuildException {
		// first off, make sure that we've got what we need
		validateParameters();

		VerboseListener verboseListener = new VerboseListener(this);

		try {
			TransitiveClosure selector = new TransitiveClosure(getScopeCriteria(), getFilterCriteria());

			try {
				if (getMaximuminbounddepth() != null) {
					selector.setMaximumInboundDepth(Long.parseLong(getMaximuminbounddepth()));
				}
			} catch (NumberFormatException ex) {
				selector.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
			}
			
			try {
				if (getMaximumoutbounddepth() != null) {
					selector.setMaximumOutboundDepth(Long.parseLong(getMaximumoutbounddepth()));
				}
			} catch (NumberFormatException ex) {
				selector.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
			}
		
			String[] filenames = getSrc().list();
			for (int i=0; i<filenames.length; i++) {
				log("Reading graph from " + filenames[i]);
				
				Collection packages = Collections.EMPTY_LIST;
				
				if (filenames[i].endsWith(".xml")) {
					NodeLoader loader = new NodeLoader(getValidate());
					loader.addDependencyListener(verboseListener);
					packages = loader.load(filenames[i]).getPackages().values();
				}
				
				log("Maximizing ...");
				new LinkMaximizer().traverseNodes(packages);
				
				selector.traverseNodes(packages);
			}

			log("Saving dependency graph to " + getDestfile().getAbsolutePath());
		
			PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));

			Printer printer;
			if (getXml()) {
				printer = new XMLPrinter(out, getEncoding(), getDtdprefix());
			} else {
				printer = new TextPrinter(out);
			}
				
			if (getIndenttext() != null) {
				printer.setIndentText(getIndenttext());
			}
				
			printer.traverseNodes(selector.getFactory().getPackages().values());
				
			out.close();
		} catch (SAXException ex) {
			throw new BuildException(ex);
		} catch (IOException ex) {
			throw new BuildException(ex);
		}
	}
}
