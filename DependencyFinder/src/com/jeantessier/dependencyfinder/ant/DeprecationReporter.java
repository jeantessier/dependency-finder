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

public class DeprecationReporter extends Task {

	private Path    includes;
	private Path    excludes;
	private boolean xml        = false;
	private String  encoding   = XMLPrinter.DEFAULT_ENCODING;
	private String  dtd_prefix = XMLPrinter.DEFAULT_DTD_PREFIX;
	private String  indent_text;

	private boolean validate   = false;
	private Path    src;
	private File    destfile;
	
	public Path createIncludes() {
		if (includes == null) {
			includes = new Path(getProject());
		}

		return includes;
	}
	
	public Path getIncludes() {
		return includes;
	}
	
	public Path createExcludes() {
		if (excludes == null) {
			excludes = new Path(getProject());
		}

		return excludes;
	}
	
	public Path getExcludes() {
		return excludes;
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

	protected void CheckParameters() throws BuildException {
		if (getIncludes() == null) {
			throw new BuildException("includes must be set!");
		}
		
		if (getIncludes().size() == 0) {
			throw new BuildException("includes is empty!");
		}
		
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
	
	public void execute() throws BuildException {
		// first off, make sure that we've got what we need
		CheckParameters();

		VerboseListener verbose_listener = new VerboseListener(this);

		try {
			Collection collection = new HashSet();

			if (getIncludes() != null) {
				String[] filenames = getIncludes().list();
				for (int i=0; i<filenames.length; i++) {
					log("Including elements from " + filenames[i]);
					
					BufferedReader reader = new BufferedReader(new FileReader(filenames[i]));
					String line;
					while ((line = reader.readLine()) != null) {
						collection.add(line);
					}
					reader.close();
				}
			}
			
			if (getExcludes() != null) {
				String[] filenames = getExcludes().list();
				for (int i=0; i<filenames.length; i++) {
					log("Excluding elements from " + filenames[i]);
					
					BufferedReader reader = new BufferedReader(new FileReader(filenames[i]));
					String line;
					while ((line = reader.readLine()) != null) {
						collection.remove(line);
					}
					reader.close();
				}
			}
			
			SelectionCriteria scope_criteria = new CollectionSelectionCriteria(collection);
			SelectionCriteria filter_criteria = new ComprehensiveSelectionCriteria();
			
			GraphCopier copier = new GraphCopier(new SelectiveTraversalStrategy(scope_criteria, filter_criteria));

			String[] filenames = getSrc().list();
			for (int i=0; i<filenames.length; i++) {
				log("Reading graph from " + filenames[i]);
				
				Collection packages = Collections.EMPTY_LIST;
				
				if (filenames[i].endsWith(".xml")) {
					NodeLoader loader = new NodeLoader(getValidate());
					loader.addDependencyListener(verbose_listener);
					packages = loader.Load(filenames[i]).Packages().values();
				}
				
				copier.TraverseNodes(packages);
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
				printer.IndentText(getIndenttext());
			}
				
			printer.TraverseNodes(copier.ScopeFactory().Packages().values());
				
			out.close();
		} catch (SAXException ex) {
			throw new BuildException(ex);
		} catch (IOException ex) {
			throw new BuildException(ex);
		}
	}
}
