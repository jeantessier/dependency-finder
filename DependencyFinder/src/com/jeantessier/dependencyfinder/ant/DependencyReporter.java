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

	private Path    scopeIncludesList;
	private Path    scopeExcludesList;
	private Path    filterIncludesList;
	private Path    filterExcludesList;

	private boolean showInbounds   = false;
	private boolean showOutbounds  = false;
	private boolean showEmptyNodes = false;
	private boolean showAll        = false;

	private boolean minimize   = false;
	private boolean maximize   = false;
	private boolean copyOnly   = false;
	private boolean xml        = false;
	private String  encoding   = XMLPrinter.DEFAULT_ENCODING;
	private String  dtdPrefix  = XMLPrinter.DEFAULT_DTD_PREFIX;
	private String  indentText;
	
	public Path createScopeincludeslist() {
		if (scopeIncludesList == null) {
			scopeIncludesList = new Path(getProject());
		}

		return scopeIncludesList;
	}
	
	public Path getScopeincludeslist() {
		return scopeIncludesList;
	}
	
	public Path createScopeexcludeslist() {
		if (scopeExcludesList == null) {
			scopeExcludesList = new Path(getProject());
		}

		return scopeExcludesList;
	}
	
	public Path getScopeexcludeslist() {
		return scopeExcludesList;
	}
	
	public Path createFilterincludeslist() {
		if (filterIncludesList == null) {
			filterIncludesList = new Path(getProject());
		}

		return filterIncludesList;
	}
	
	public Path getFilterincludeslist() {
		return filterIncludesList;
	}
	
	public Path createFilterexcludeslist() {
		if (filterExcludesList == null) {
			filterExcludesList = new Path(getProject());
		}

		return filterExcludesList;
	}
	
	public Path getFilterexcludeslist() {
		return filterExcludesList;
	}
	
	public boolean getShowinbounds() {
		return showInbounds;
	}

	public void setShowinbounds(boolean showInbounds) {
		this.showInbounds = showInbounds;
	}
	
	public boolean getShowoutbounds() {
		return showOutbounds;
	}

	public void setShowoutbounds(boolean showOutbounds) {
		this.showOutbounds = showOutbounds;
	}
	
	public boolean getShowemptynodes() {
		return showEmptyNodes;
	}

	public void setShowemptynodes(boolean showEmptyNodes) {
		this.showEmptyNodes = showEmptyNodes;
	}
	
	public void setShowAll(boolean showAll) {
		setShowinbounds(showAll);
		setShowoutbounds(showAll);
		setShowemptynodes(showAll);
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
		return copyOnly;
	}

	public void setCopyOnly(boolean copyOnly) {
		this.copyOnly = copyOnly;
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

	protected void validateParameters() throws BuildException {
		super.validateParameters();

		if (hasScopeRegularExpressionSwitches() && hasScopeListSwitches()) {
			throw new BuildException("Cannot have scope attributes for regular expressions and lists at the same time!");
		}

		if (hasFilterRegularExpressionSwitches() && hasFilterListSwitches()) {
			throw new BuildException("Cannot have filter attributes for regular expressions and lists at the same time!");
		}
	}
	
	public void execute() throws BuildException {
		// first off, make sure that we've got what we need
		validateParameters();

		VerboseListener verboseListener = new VerboseListener(this);

		try {
			GraphCopier copier;
			if (getCopyOnly() || getMaximize()) {
				copier = new GraphCopier(getStrategy());
			} else {
				copier = new GraphSummarizer(getScopeCriteria(), getFilterCriteria());
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

	protected SelectionCriteria getScopeCriteria() throws BuildException {
		SelectionCriteria result = new ComprehensiveSelectionCriteria();

		try {
			if (hasScopeRegularExpressionSwitches()) {
				result = super.getScopeCriteria();
			} else if (hasScopeListSwitches()) {
				result = createCollectionSelectionCriteria(getScopeincludeslist(), getScopeexcludeslist());
			}
		} catch (IOException ex) {
			throw new BuildException(ex);
		}

		return result;
	}

	protected SelectionCriteria getFilterCriteria() throws BuildException {
		SelectionCriteria result = new ComprehensiveSelectionCriteria();
		
		try {
			if (hasFilterRegularExpressionSwitches()) {
				result = super.getFilterCriteria();
			} else if (hasFilterListSwitches()) {
				result = createCollectionSelectionCriteria(getFilterincludeslist(), getFilterexcludeslist());
			}
		} catch (IOException ex) {
			throw new BuildException(ex);
		}

		return result;
	}
	
	private boolean hasScopeRegularExpressionSwitches() {
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

	private boolean hasScopeListSwitches() {
		return
			getScopeincludeslist() != null ||
			getScopeexcludeslist() != null;
	}

	private boolean hasFilterRegularExpressionSwitches() {
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

	private boolean hasFilterListSwitches() {
		return
			getFilterincludeslist() != null ||
			getFilterexcludeslist() != null;
	}

	private CollectionSelectionCriteria createCollectionSelectionCriteria(Path includes, Path excludes) throws IOException {
		return new CollectionSelectionCriteria(loadCollection(includes), loadCollection(excludes));
	}

	private Collection loadCollection(Path path) {
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
