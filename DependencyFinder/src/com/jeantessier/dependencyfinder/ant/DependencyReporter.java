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

import org.apache.log4j.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

import org.xml.sax.*;

import com.jeantessier.dependency.*;

public class DependencyReporter extends GraphTask {

	private Path    scope_includes_list;
	private Path    scope_excludes_list;
	private Path    filter_includes_list;
	private Path    filter_excludes_list;

	private boolean show_inbounds    = false;
	private boolean show_outbounds   = false;
	private boolean show_empty_nodes = false;
	private boolean show_all         = false;

	private boolean minimize   = false;
	private boolean maximize   = false;
	private boolean copy_only  = false;
	private boolean xml        = false;
	private String  encoding   = XMLPrinter.DEFAULT_ENCODING;
	private String  dtd_prefix = XMLPrinter.DEFAULT_DTD_PREFIX;
	private String  indent_text;
	
	public Path createScopeincludeslist() {
		if (scope_includes_list == null) {
			scope_includes_list = new Path(getProject());
		}

		return scope_includes_list;
	}
	
	public Path getScopeincludeslist() {
		return scope_includes_list;
	}
	
	public Path createScopeexcludeslist() {
		if (scope_excludes_list == null) {
			scope_excludes_list = new Path(getProject());
		}

		return scope_excludes_list;
	}
	
	public Path getScopeexcludeslist() {
		return scope_excludes_list;
	}
	
	public Path createFilterincludeslist() {
		if (filter_includes_list == null) {
			filter_includes_list = new Path(getProject());
		}

		return filter_includes_list;
	}
	
	public Path getFilterincludeslist() {
		return filter_includes_list;
	}
	
	public Path createFilterexcludeslist() {
		if (filter_excludes_list == null) {
			filter_excludes_list = new Path(getProject());
		}

		return filter_excludes_list;
	}
	
	public Path getFilterexcludeslist() {
		return filter_excludes_list;
	}
	
	public boolean getShowinbounds() {
		return show_inbounds;
	}

	public void setShowinbounds(boolean show_inbounds) {
		this.show_inbounds = show_inbounds;
	}
	
	public boolean getShowoutbounds() {
		return show_outbounds;
	}

	public void setShowoutbounds(boolean show_outbounds) {
		this.show_outbounds = show_outbounds;
	}
	
	public boolean getShowemptynodes() {
		return show_empty_nodes;
	}

	public void setShowemptynodes(boolean show_empty_nodes) {
		this.show_empty_nodes = show_empty_nodes;
	}
	
	public void setShowAll(boolean show_all) {
		setShowinbounds(show_all);
		setShowoutbounds(show_all);
		setShowemptynodes(show_all);
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

	public boolean getCopyOnly() {
		return copy_only;
	}

	public void setCopyOnly(boolean copy_only) {
		this.copy_only = copy_only;
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

	protected void CheckParameters() throws BuildException {
		super.CheckParameters();

		if (HasScopeRegularExpressionSwitches() && HasScopeListSwitches()) {
			throw new BuildException("Cannot have scope attributes for regular expressions and lists at the same time!");
		}

		if (HasFilterRegularExpressionSwitches() && HasFilterListSwitches()) {
			throw new BuildException("Cannot have filter attributes for regular expressions and lists at the same time!");
		}
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
				
				Collection packages = Collections.EMPTY_LIST;
				
				if (filenames[i].endsWith(".xml")) {
					NodeLoader loader = new NodeLoader(getValidate());
					loader.addDependencyListener(verbose_listener);
					packages = loader.load(filenames[i]).getPackages().values();
				}
				
				if (getMaximize()) {
					log("Maximizing ...");
					new LinkMaximizer().traverseNodes(packages);
				} else if (getMinimize()) {
					log("Minimizing ...");
					new LinkMinimizer().traverseNodes(packages);
				}
				
				copier.traverseNodes(packages);
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

			printer.setShowInbounds(getShowinbounds());
			printer.setShowOutbounds(getShowoutbounds());
			printer.setShowEmptyNodes(getShowemptynodes());
				
			printer.traverseNodes(copier.getScopeFactory().getPackages().values());
				
			out.close();
		} catch (SAXException ex) {
			throw new BuildException(ex);
		} catch (IOException ex) {
			throw new BuildException(ex);
		}
	}

	protected SelectionCriteria ScopeCriteria() throws BuildException {
		SelectionCriteria result = new ComprehensiveSelectionCriteria();

		try {
			if (HasScopeRegularExpressionSwitches()) {
				result = super.ScopeCriteria();
			} else if (HasScopeListSwitches()) {
				result = CreateCollectionSelectionCriteria(getScopeincludeslist(), getScopeexcludeslist());
			}
		} catch (IOException ex) {
			throw new BuildException(ex);
		}

		return result;
	}

	protected SelectionCriteria FilterCriteria() throws BuildException {
		SelectionCriteria result = new ComprehensiveSelectionCriteria();
		
		try {
			if (HasFilterRegularExpressionSwitches()) {
				result = super.FilterCriteria();
			} else if (HasFilterListSwitches()) {
				result = CreateCollectionSelectionCriteria(getFilterincludeslist(), getFilterexcludeslist());
			}
		} catch (IOException ex) {
			throw new BuildException(ex);
		}

		return result;
	}
	
	private boolean HasScopeRegularExpressionSwitches() {
		return
			!getScopeincludes().equals("//") ||
			!getScopeexcludes().equals("") ||
			getPackagescope() ||
			!getPackagescopeincludes().equals("") ||
			!getPackagescopeexcludes().equals("") ||
			getClassscope() ||
			!getClassscopeincludes().equals("") ||
			!getClassscopeexcludes().equals("") ||
			getFeaturescope() ||
			!getFeaturescopeincludes().equals("") ||
			!getFeaturescopeexcludes().equals("");
	}

	private boolean HasScopeListSwitches() {
		return
			getScopeincludeslist() != null ||
			getScopeexcludeslist() != null;
	}

	private boolean HasFilterRegularExpressionSwitches() {
		return
			!getFilterincludes().equals("//") ||
			!getFilterexcludes().equals("") ||
			getPackagefilter() ||
			!getPackagefilterincludes().equals("") ||
			!getPackagefilterexcludes().equals("") ||
			getClassfilter() ||
			!getClassfilterincludes().equals("") ||
			!getClassfilterexcludes().equals("") ||
			getFeaturefilter() ||
			!getFeaturefilterincludes().equals("") ||
			!getFeaturefilterexcludes().equals("");
	}

	private boolean HasFilterListSwitches() {
		return
			getFilterincludeslist() != null ||
			getFilterexcludeslist() != null;
	}

	private CollectionSelectionCriteria CreateCollectionSelectionCriteria(Path includes, Path excludes) throws IOException {
		return new CollectionSelectionCriteria(LoadCollection(includes), LoadCollection(excludes));
	}

	private Collection LoadCollection(Path path) {
		Collection result = null;

		if (path != null) {
			result = new HashSet();
			
			String[] filenames = path.list();
			for (int i=0; i<filenames.length; i++) {
				BufferedReader reader = null;
				String line;
				
				try {
					reader = new BufferedReader(new FileReader(filenames[i]));
					while ((line = reader.readLine()) != null) {
						result.add(line);
					}
				} catch (IOException ex) {
					Logger.getLogger(getClass()).error("Couldn't read file " + filenames[i], ex);
				} finally {
					try {
						if (reader != null) {
							reader.close();
						}
					} catch (IOException ex) {
						Logger.getLogger(getClass()).error("Couldn't close file " + filenames[i], ex);
					}
				}
			}
		}
		
		return result;
	}
}
